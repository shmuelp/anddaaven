package com.saraandshmuel.anddaaven;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.acra.ErrorReporter;
import org.xml.sax.XMLReader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.text.Spanned;
import android.text.Html;
import android.text.style.UnderlineSpan;

public class AndDaavenTefillaModel extends AndDaavenBaseModel implements TagHandler
{

	protected String tefilla;
	protected Context context;
	protected AndDaavenTefillaView view;

	private String currentFilename = "";
	protected int currentOffset=0;
	protected int deleteFrom=-1;
	private Spanned spanText = new SpannableString("");
	private String[] sectionNames = new String[0];
	protected ArrayList<Integer> jumpOffsets = new ArrayList<Integer>();
	protected ArrayList<Integer> sectionOffsets = new ArrayList<Integer>();
	
	private static final String TAG = "AndDaavenTefillaModel";
	
	public AndDaavenTefillaModel(Context context) {
		// TODO Auto-generated constructor stub
		System.setProperty("log.tag." + TAG, "VERBOSE");
		this.context=context;
	}

	public void setCurrentOffset(int currentOffset)
	{
		this.currentOffset = currentOffset;
	}

	public int getCurrentOffset()
	{
		return currentOffset;
	}

//	public void setSectionNames(String[] sectionNames)
//	{
//		this.sectionNames = sectionNames;
//	}
//
	public String[] getSectionNames()
	{
		return sectionNames;
	}

//	public void setSpanText(Spanned spanText)
//	{
//		this.spanText = spanText;
//	}
//
	public Spanned getSpanText()
	{
		return spanText;
	}


	public void setTefilla(Intent intent)
	{
		// TODO: Implement this method
		if (intent.hasExtra("tefilla")) {
			tefilla=intent.getStringExtra("tefilla");
		}
	}



	/**
	 * Read text in from file (if not already being displayed) and display it in
	 * the daavenText TextView
	 * 
	 * @param andDaavenTefilla TODO
	 * @param filename
	 *            The filename to read in
	 */
	public void prepareTefilla(final String filename) {
		if (filename.endsWith(".html")) {
			prepareHtmlTefilla(this, view, context, filename, jumpOffsets, sectionOffsets);
		} else {
			prepareUtf8Tefilla( this, view, context,
								filename, jumpOffsets, sectionOffsets);
		}
	}

	void prepareUtf8Tefilla( AndDaavenTefillaModel model,
								AndDaavenTefillaView view,
								Context context,
								final String filename, 
								List<Integer> jumpOffsets, 
								List<Integer> sectionOffsets) {
		Log.v(TAG, "prepareTefilla(" + filename + ") beginning");
		ErrorReporter er = ErrorReporter.getInstance();
		
		 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); 
		 boolean showNikud = prefs.getBoolean("ShowNikud", true);
		 boolean showMeteg = prefs.getBoolean("ShowMeteg", false);
		 boolean showSectionNames = prefs.getBoolean("SectionName", true);
		 
		 String currentFilename=model.getCurrentFilename();
	
		if (filename == currentFilename) {
			Log.v(TAG, "prepareTefilla() about to return early");
			return;
		}
	
		er.addCustomData("prepareTefilla()", filename);
	
		model.setCurrentOffset(0);
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = context.getAssets().open(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			jumpOffsets.clear();
			ArrayList<String> sectionNamesList = new ArrayList<String>();
			sectionOffsets.clear();
			int offset = 0;
			while (br.ready()) {
				String s = br.readLine();
				if (s.length() == 0) {
					sb.append('\n');
					offset += 1;
				} else if (s.charAt(0) == '\013') {
					jumpOffsets.add(offset);
					String name = s.substring(1);
					if (name.length() > 0) {
						sectionNamesList.add(name);
						sectionOffsets.add(offset);
						if (showSectionNames) {
							sb.append(name);
							sb.append('\n');
							offset += name.length() + 1;
						}
					}
				} else {
					if (!showNikud) {
						// Remove nikud based on Unicode character ranges
						// Does not replace combined characters (\ufb20-\ufb4f)
						// See
						// http://en.wikipedia.org/wiki/Unicode_and_HTML_for_the_Hebrew_alphabet
						s = s. replaceAll("[\u05b0-\u05c7]", "");
					}
					if (!showMeteg) {
						// Remove meteg based on Unicode character ranges
						// Does not replace combined characters (\ufb20-\ufb4f)
						// See
						// http://en.wikipedia.org/wiki/Unicode_and_HTML_for_the_Hebrew_alphabet
						s = s.replaceAll("\u05bd", "");
					}
					sb.append(s);
					sb.append('\n');
					offset += s.length() + 1;
				}
			}
			
			Spannable spanText = new SpannableString(sb);
			if (showSectionNames) {
				for (int i=0; i<Math.max(sectionNamesList.size(), sectionOffsets.size()); ++i) {
					int begin=sectionOffsets.get(i);
					int end=begin+sectionNamesList.get(i).length();
					spanText.setSpan(new UnderlineSpan(), begin, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
				}
			}
			// Spanned spanText = Html.fromHtml(sb.toString(), null, this);
			model.setSpanText(spanText);
	
			model.setSectionNames(sectionNamesList.toArray(new String[0]));
	
			currentFilename=filename;
			model.setCurrentFilename(currentFilename);
			
			Log.v(TAG, "spanText.length()=" + spanText.length() +
			         ", sb.length()=" + sb.length() +
					 ", daavenText.getText().length()=" +
					 view.getDaavenTextLength() + 
					 ", showNikud=" + showNikud +
					 ", showMeteg=" + showMeteg +
					 ", showSectionNames=" + showSectionNames +
					 ", currentFilename=" + currentFilename
					 );
	
			er.addCustomData("spanText.length()", "" + spanText.length());
			er.addCustomData("sb.length()", "" + sb.length());
			er.addCustomData("daavenText.getText().length()", "" 
					+ view.getDaavenTextLength());
			er.addCustomData("showNikud", "" + showNikud);
			er.addCustomData("showMeteg", "" + showMeteg);
			er.addCustomData("showSectionNames", "" + showSectionNames);
			er.addCustomData("currentFilename", currentFilename);
	
			// // In UI thread:
			// // Get layout
			// // For each jump offset:
			// // layout.getLineForOffset
			// // layout.getLineForVertical
			// daavenText.post( new Runnable() {
			// public void run() {
			// };
			// });
		} catch (IOException e) {
			Toast.makeText(context, "Caught an exception: " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
			er.addCustomData("IOException.getMessage", e.getMessage());
			er.handleException(e);
		}
		Log.v(TAG, "prepareTefilla() ending");
	}

	void prepareHtmlTefilla( AndDaavenTefillaModel model,
			AndDaavenTefillaView view,
			Context context,
			final String filename, 
			List<Integer> jumpOffsets, 
			List<Integer> sectionOffsets) {
		Log.v(TAG, "prepareTefilla(" + filename + ") beginning");
		ErrorReporter er = ErrorReporter.getInstance();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context); 
		boolean showNikud = prefs.getBoolean("ShowNikud", true);
		boolean showMeteg = prefs.getBoolean("ShowMeteg", false);
		boolean showSectionNames = prefs.getBoolean("SectionName", true);

		String currentFilename=model.getCurrentFilename();

		if (filename == currentFilename) {
			Log.v(TAG, "prepareTefilla() about to return early");
			return;
		}

		er.addCustomData("prepareTefilla()", filename);

		model.setCurrentOffset(0);
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = context.getAssets().open(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			jumpOffsets.clear();
			ArrayList<String> sectionNamesList = new ArrayList<String>();
			sectionOffsets.clear();
			int offset = 0;
			while (br.ready()) {
				String s = br.readLine();
				if (s.length() == 0) {
					sb.append("<br/>");
					offset += 1;
				} else if (s.charAt(0) == '\013') {
					jumpOffsets.add(offset);
					String name = s.substring(1);
					if (name.length() > 0) {
						sectionNamesList.add(name);
						sectionOffsets.add(offset);
						if (showSectionNames) {
							sb.append(name);
							sb.append("<br/>");
							offset += name.length() + 1;
						}
					}
				} else {
					if (!showNikud) {
						// Remove nikud based on Unicode character ranges
						// Does not replace combined characters (\ufb20-\ufb4f)
						// See
						// http://en.wikipedia.org/wiki/Unicode_and_HTML_for_the_Hebrew_alphabet
						s = s. replaceAll("[\u05b0-\u05c7]", "");
					}
					if (!showMeteg) {
						// Remove meteg based on Unicode character ranges
						// Does not replace combined characters (\ufb20-\ufb4f)
						// See
						// http://en.wikipedia.org/wiki/Unicode_and_HTML_for_the_Hebrew_alphabet
						s = s.replaceAll("\u05bd", "");
					}
					sb.append(s);
					sb.append("<br/>");
					offset += s.length() + 1;
				}
			}

			//Spannable spanText = new SpannableString(sb);
			Spanned spanText = Html.fromHtml(sb.toString(), null, this);
			model.setSpanText(spanText);

			model.setSectionNames(sectionNamesList.toArray(new String[0]));

			currentFilename=filename;
			model.setCurrentFilename(currentFilename);

			Log.v(TAG, "spanText.length()=" + spanText.length() +
					", sb.length()=" + sb.length() +
					", daavenText.getText().length()=" +
					view.getDaavenTextLength() + 
					", showNikud=" + showNikud +
					", showMeteg=" + showMeteg +
					", showSectionNames=" + showSectionNames +
					", currentFilename=" + currentFilename
			);

			er.addCustomData("spanText.length()", "" + spanText.length());
			er.addCustomData("sb.length()", "" + sb.length());
			er.addCustomData("daavenText.getText().length()", "" 
					+ view.getDaavenTextLength());
			er.addCustomData("showNikud", "" + showNikud);
			er.addCustomData("showMeteg", "" + showMeteg);
			er.addCustomData("showSectionNames", "" + showSectionNames);
			er.addCustomData("currentFilename", currentFilename);

			// // In UI thread:
			// // Get layout
			// // For each jump offset:
			// // layout.getLineForOffset
			// // layout.getLineForVertical
			// daavenText.post( new Runnable() {
			// public void run() {
			// };
			// });
		} catch (IOException e) {
			Toast.makeText(context, "Caught an exception: " + e.getMessage(),
					Toast.LENGTH_SHORT).show();
			er.addCustomData("IOException.getMessage", e.getMessage());
			er.handleException(e);
		}
		Log.v(TAG, "prepareTefilla() ending");
	}

	protected ArrayList<Integer> getJumpOffsets() {
		return jumpOffsets;
	}

	protected ArrayList<Integer> getSectionOffsets() {
		return sectionOffsets;
	}

	public void setView(AndDaavenTefillaView view) {
		this.view = view;
	}

	protected void setCurrentFilename(String currentFilename) {
		this.currentFilename = currentFilename;
	}

	protected String getCurrentFilename() {
		return currentFilename;
	}

	protected void setSectionNames(String[] sectionNames) {
		this.sectionNames = sectionNames;
	}

	protected void setSpanText(Spanned spanText) {
		this.spanText = spanText;

		Log.v(TAG, "About to set text in view");
		view.setDaavenText(spanText);
	}

	public void handleTag(boolean opening, String tag, Editable output,
						XMLReader xmlReader) {
		Log.v(TAG, "Got tag=" + tag + ", opening=" + opening);
		if (tag=="section") {
			if (opening) {
				deleteFrom=output.length();
			} else {
				//output.delete(deleteFrom, output.length());
				output.setSpan(new NoDisplaySpan(), deleteFrom, output.length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
				deleteFrom=-1;
			}
		}
	}

}
