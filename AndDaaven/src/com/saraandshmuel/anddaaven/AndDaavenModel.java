package com.saraandshmuel.anddaaven;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.*;
import java.lang.reflect.*;

public class AndDaavenModel implements TefillaModelInterface
{
	private static final String TAG = "AndDaavenModel";

	public AndDaavenModel() {
		tefillaModel = new TefillaModel();
		System.setProperty("log.tag." + TAG, "VERBOSE");
	}

	public static String getVersion(Context context)
	{
		String result = "Unknown";
    	try {
			PackageInfo pi = context.getPackageManager().getPackageInfo("com.saraandshmuel.anddaaven",0);
	        result = pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
		}
		// TODO: Implement this method
		return result;
	}
	
	public boolean getNightMode(Context context) {
		boolean result=PreferenceManager.getDefaultSharedPreferences(context)
			.getBoolean("DarkMode", false);
		return result;
	}

	public void toggleNightMode(Context context) {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean current=prefs.getBoolean("DarkMode", false);
		prefs.edit().putBoolean("DarkMode", !current).commit();
//		view.reload();
	}

	public void advanceDay() { 
		Log.v(TAG, "Delegating advanceDay() to TefillaModel");
		tefillaModel.advanceDay();
	}

	public String getDateString() {
		Log.v(TAG, "Delegating getDateString() to TefillaModel");
		return tefillaModel.getDateString();
	}

	public String getOmerString() {
		Log.v(TAG, "Delegating getOmerString() to TefillaModel");
		return tefillaModel.getOmerString();
	}

	public boolean inAfternoon() {
		Log.v(TAG, "Delegating inAfternoon() to TefillaModel");
		return tefillaModel.inAfternoon();
	}

	public static int getAndroidSdkVersion() {
		int result = 0;
		
		try {
			result=Integer.parseInt(Build.VERSION.SDK);
		} catch (Exception e) {
			Log.e(TAG, "Unable to parse integer SDK version \'" + Build.VERSION.SDK + "\'");
		}
		
		return result;
	}

	public static String getAndroidRelease() {
		String result = Build.VERSION.RELEASE;

		return result;
	}

	public static String getAndroidModel() {
		StringBuilder result = new StringBuilder();
		String man="";
		try
		{
			Field f=Build.class.getDeclaredField("MANUFACTURER");
			Build b = new Build();
			man = (String) f.get(b);
		}
		catch (Exception e) {
			Log.w(TAG, "Exception getting manufacturer name");
			Log.w(TAG, e);
		}

		result.append(man).append(' ')
//		The following is only available in API level 4 (Donut) and above 
//			  .append(Build.MANUFACTURER).append(" ")
			  .append(Build.MODEL).append(" (")
			  .append(Build.BRAND).append(")");
		
		return result.toString();
	}

	private TefillaModel tefillaModel;
}
