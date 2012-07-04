package com.saraandshmuel.anddaaven;
import android.util.*;
import android.content.*;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.app.Activity;

public class AndDaavenBaseView
{
	private static final String TAG="AndDaavenView";
	public static final float FONT_MIN=6;
	public static final float FONT_MAX=100;
	
	protected Activity activity;
	protected SharedPreferences prefs;

	public AndDaavenBaseView(Activity activity) {
		System.setProperty("log.tag." + TAG, "VERBOSE");
		this.activity = activity;
		Log.v(TAG, "Getting prefs, activity=" + activity);
		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
	}
	
	public void adjustFontSize(float total)
	{
		Log.v(TAG, "adjustFontSize(" + total + ")");
		String sizeStr = prefs.getString("FontSize", "20");
		float size=Float.parseFloat(sizeStr);
		size *= total;
		if (size<FONT_MIN) size=FONT_MIN;
		if (size>FONT_MAX) size=FONT_MAX;
		prefs.edit().putString("FontSize", Float.toString(size)).commit();
//		View v = activity.findViewById(R.id.DaavenText);
//		if (v != null ) {
//			TextView text = (TextView) v;
//			text.setTextSize((int) size);
//		}
	}
	
	public int getNightModeStyle(AndDaavenBaseModel model) {
		if (getNightMode())
			return R.style.MyDark;
		else
			return R.style.MyLight;
	}

	public boolean getNightMode() {
		boolean result=prefs.getBoolean("DarkMode", false);
		return result;
	}

	public static boolean getNightMode(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean result=prefs.getBoolean("DarkMode", false);
		return result;
	}

	public void toggleNightMode() {
		// TODO Auto-generated method stub
		boolean current=prefs.getBoolean("DarkMode", false);
		prefs.edit().putBoolean("DarkMode", !current).commit();
	}

	public void setNightModeTheme() {
		setNightModeTheme(activity);
	}
	
	public static void setNightModeTheme(Context context) {
		Log.v(TAG, "setNightMode() beginning");
		boolean nightMode = getNightMode(context);
		if (nightMode) {
			context.setTheme(R.style.MyDark);
		}
		Log.v(TAG, "setNightMode() ending");
	}

	/**
	 * Gets the font size of the tefilla text
	 * 
	 * @return
	 */
	public float getFontSize() {
		Log.v(TAG, "getFontSize() beginning");
		String sizePref = prefs.getString("FontSize", "20");
		float size = Float.parseFloat(sizePref);
		Log.v(TAG, "getFontSize() about to return " + size);
		return size;
	}

	/**
	 * @return
	 */
	public Typeface getDefaultHebrewTypeface() {
		// setup Typeface object with Hebrew font
	    Typeface face;
	    face = Typeface.createFromAsset(activity.getAssets(), "FreeSerifBoldSubset.ttf");
		return face;
	}

	/**
	 */
	public Typeface getSelectedHebrewTypeface() {
		Log.v(TAG, "getSelectedHebrewTypeface beginning");
		Typeface result=null;
		
		try {
			String typefaceName;
			typefaceName = prefs.getString("TextFont", "FreeSerifBoldSubset.ttf");
			Log.v(TAG, "got name=" + typefaceName);
	
			// Backwards compatibility
			if (typefaceName == "FreeSans.ttf") {
				SharedPreferences.Editor edit = prefs.edit();
				edit.putString("TextFont", "FreeSansSubset.ttf").commit();
			}
			if (typefaceName == "FreeMono.ttf") {
				SharedPreferences.Editor edit = prefs.edit();
				edit.putString("TextFont", "FreeMonoSubset.ttf").commit();
			}
	
			result = Typeface.createFromAsset(activity.getAssets(), typefaceName);
			// face = Typeface.createFromAsset(getAssets(), "SILEOTSR.ttf");
		} catch (Exception e) {
			Log.e(TAG, "Couldn't create font, will try default");
			// Apparently, the expected font does not exist. Most likely, the
			// user selected something
			// which has changed names. Clear the pref and use the default
			String typefaceName = "FreeSerifBoldSubset.ttf";
			SharedPreferences.Editor edit = prefs.edit();
			edit.putString("TextFont", "FreeSerifBoldSubset.ttf").commit();
			edit.commit();
			result = Typeface
					.createFromAsset(activity.getAssets(), typefaceName);
		}
		
		return result;
	}
}

