package com.saraandshmuel.coresiddur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.acra.ErrorReporter;

import com.saraandshmuel.anddaaven.AndDaavenTefilla;

import android.content.Context;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.widget.Toast;

public class TefillaModel {
	public TefillaModel() {
	}
	
    /** 
     * Read text in from file (if not already being displayed) and display it 
     * in the daavenText TextView
     * @param filename The filename to read in
     * @param ssb 
     * @param context 
     */
	public void prepareTefilla(String filename, SpannableStringBuilder ssb, AndDaavenTefilla activity) {
		this.setFilename(filename);
		ErrorReporter er = ErrorReporter.getInstance();

		er.addCustomData("prepareTefilla()", filename);
		
		boolean showNikud = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("ShowNikud", true);
		boolean showSectionNames = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("SectionName", true);
		activity.setCurrentOffset(0);
		try {
			InputStream is = activity.getAssets().open(filename);
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

//			// In UI thread:
//			// Get layout
//			// For each jump offset:
//			// 		layout.getLineForOffset
//			//		layout.getLineForVertical
//			daavenText.post( new Runnable() {
//				public void run() {
//				};
//			});
		} catch (IOException e) {
//			Toast.makeText(this, "Caught an exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			er.addCustomData("IOException.getMessage", e.getMessage());
			er.handleException(e);
		}
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	private void setSectionNames(String[] sectionNames) {
		this.sectionNames = sectionNames;
	}

	public String[] getSectionNames() {
		return sectionNames;
	}

	private void setJumpOffsets(ArrayList<Integer> jumpOffsets) {
		this.jumpOffsets = jumpOffsets;
	}

	public ArrayList<Integer> getJumpOffsets() {
		return jumpOffsets;
	}

	private void setSectionOffsets(ArrayList<Integer> sectionOffsets) {
		this.sectionOffsets = sectionOffsets;
	}

	public ArrayList<Integer> getSectionOffsets() {
		return sectionOffsets;
	}

	private String filename="";
	private String[] sectionNames = new String[0];
	private ArrayList<Integer> jumpOffsets = new ArrayList<Integer>();
	private ArrayList<Integer> sectionOffsets = new ArrayList<Integer>();
}
