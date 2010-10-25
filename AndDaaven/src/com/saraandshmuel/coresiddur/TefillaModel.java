package com.saraandshmuel.coresiddur;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.acra.ErrorReporter;

import com.saraandshmuel.anddaaven.AndDaavenTefilla;

import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.Toast;

public class TefillaModel {

	private static final String TAG = "TefillaModel";

	public TefillaModel(AndDaavenTefilla tefillaView) {
		this.tefillaView = tefillaView;
	}
	
    /** 
     * Read text in from file (if not already being displayed) and display it 
     * in the daavenText TextView
     * @param filename The filename to read in
     * @param context 
     */
	public void prepareTefilla(String filename) {
		this.setFilename(filename);
		ErrorReporter er = ErrorReporter.getInstance();

		er.addCustomData("prepareTefilla()", filename);
		
		boolean showNikud = PreferenceManager.getDefaultSharedPreferences(tefillaView).getBoolean("ShowNikud", true);
		boolean showSectionNames = PreferenceManager.getDefaultSharedPreferences(tefillaView).getBoolean("SectionName", true);
		tefillaView.setCurrentOffset(0);
		if (filename.endsWith(".utf8")) {
			readPlainText(filename, er, showNikud, showSectionNames);
		} else if (filename.endsWith(".dat")) {
			readDatFile(filename, er, showNikud, showSectionNames);
		} else {
			er.addCustomData("error", "Invalid filename");
			er.handleException(null);
		}
	}

	/**
	 * @param filename
	 * @param er
	 * @param showNikud
	 * @param showSectionNames
	 */
	private void readPlainText(String filename, ErrorReporter er,
			boolean showNikud, boolean showSectionNames) {
		try {
			InputStream is = tefillaView.getAssets().open(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			ssb.clear();
			jumpOffsets.clear();
			ArrayList<String> sectionNamesList = new ArrayList<String>();
			sectionOffsets.clear();
			int offset=0;
			while ( br.ready() ) {
				String s = br.readLine();
				if ( s.length() == 0 ) {
					ssb.append("\n");
					++offset;
				} else if ( s.charAt(0)=='\013' ) {
					jumpOffsets.add( offset );
					String name = s.substring( 1 );
					if (name.length() > 0 ) {
						sectionNamesList.add( name );
						sectionOffsets.add(offset);
						if ( showSectionNames ) {
							ssb.append(name);
							ssb.append("\n");
							offset += name.length() + 1;
						}
					}
				} else {
					if ( ! showNikud ) {
						// Remove nikud based on Unicode character ranges
						// Does not replace combined characters (\ufb20-\ufb4f)
						// See http://en.wikipedia.org/wiki/Unicode_and_HTML_for_the_Hebrew_alphabet
						s = s.replaceAll("[\u05b0-\u05c7]", "");
					}
					ssb.append(s);
					ssb.append("\n");
					offset += s.length() + 1;
				}
			}

			sectionNames = sectionNamesList.toArray(new String[0]);
			
			er.addCustomData("ssb.length()", ""+ssb.length());
			er.addCustomData("showNikud", ""+showNikud);
			er.addCustomData("showSectionNames", ""+showSectionNames);

		} catch (IOException e) {
			er.addCustomData("IOException.getMessage", e.getMessage());
			er.handleException(e);
		}
	}

	/**
	 * Reads Dynamic files
	 * 
	 * File format:
	 *  <magic number=SP101010>
	 *  <4-byte # of Tefilla Records>
	 * 
	 * x Tefilla Records
	 *  <4-byte Tefilla name length><tefilla name>
	 *  <4-byte # of Tefilla text components>
	 *  <4-byte format flag><4-byte text record #>
	 *  <4-byte format flag><4-byte text record #>
	 *  ...
	 *  <4-byte format flag><4-byte text record #>
	 * 
	 * n Text Records:
	 *   <4-byte # of text records
	 * 	 <4-byte length><text data>
	 * 	 <4-byte length><text data>
	 * 	 ...
	 * 	 <4-byte length><text data>
	 * 
	 * @param filename
	 * @param er
	 * @param showNikud
	 * @param showSectionNames
	 */
	private void readDatFile(String filename, ErrorReporter er,
			boolean showNikud, boolean showSectionNames) {
		try {
			Toast.makeText(tefillaView, "readDatFile", Toast.LENGTH_SHORT).show();
			InputStream is = tefillaView.getAssets().open(filename);
			DataInputStream dis = new DataInputStream(is);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			ssb.clear();
			jumpOffsets.clear();
			ArrayList<String> sectionNamesList = new ArrayList<String>();
			sectionOffsets.clear();
			
			// Check magic number
			byte[] magic = new byte[8];
			byte[] test = {'S', 'P', '1', '0', '1', '0', '1', '0'};
			dis.readFully(magic);
			if (!Arrays.equals(magic, test)) {
				Log.e(TAG, "Invalid magic number found in dat file " + filename);
				er.addCustomData("readDatFileError", "Invalid magic number found in dat file " + filename);
			}

			// Read # of tefillot
			String temp = br.readLine();
			temp = br.readLine();
			int numTefillot = Integer.parseInt(temp);
			ssb.append("# Tefillot: " + numTefillot + "\n");
			
			for (int i=0; i<numTefillot; ++i) {
				ssb.append("Reading tefilla ");
				temp = br.readLine();
				int size = Integer.parseInt(temp);
				String name="";
				while (name.length() < size) {
					name = name + br.readLine() + "\n";
				}
				ssb.append(" " + name);
				temp = br.readLine();
				int numComponents = Integer.parseInt(temp);
				ssb.append("# components: " + numComponents + "\n");
				for (int j=0; j<numComponents; ++j) {
					temp = br.readLine();
					int format = Integer.parseInt(temp);
					temp = br.readLine();
					int component = Integer.parseInt(temp);
					ssb.append("Format: " + format + ", component=" + component);
				}
			}
			sectionNames = sectionNamesList.toArray(new String[0]);

			ssb.append("readDatFile successful!");
//			Toast.makeText(tefillaView, "ReadDatFile successful", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(tefillaView, "IOException: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
			er.addCustomData("IOException.getMessage", e.getMessage());
//			er.handleException(e);
		}
	}

	public SpannableStringBuilder getSSB() {
		return ssb;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public String[] getSectionNames() {
		return sectionNames;
	}

	public ArrayList<Integer> getJumpOffsets() {
		return jumpOffsets;
	}

	public ArrayList<Integer> getSectionOffsets() {
		return sectionOffsets;
	}
	
	AndDaavenTefilla tefillaView = null;
	
	// use a SpannableStringBuilder to allow addition of formatting in
	// future
	SpannableStringBuilder ssb = new SpannableStringBuilder();
	
	private String filename="";
	private String[] sectionNames = new String[0];
	private ArrayList<Integer> jumpOffsets = new ArrayList<Integer>();
	private ArrayList<Integer> sectionOffsets = new ArrayList<Integer>();
}
