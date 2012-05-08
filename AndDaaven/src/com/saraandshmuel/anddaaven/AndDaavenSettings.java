package com.saraandshmuel.anddaaven;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class AndDaavenSettings extends PreferenceActivity {
	public static final String PREFS_NAME = "AndDaavenPrefs";
	private static final String TAG = "AndDaavenSettings";

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate() beginning");
    	// layout view from resource XML file
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
		
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("test.autoIndex", false)) {
			PreferenceManager man=getPreferenceManager();
			man.findPreference("AutoIndexBrachot").setEnabled(true);
		}
		
		Log.v(TAG, "onCreate() ending");
    }
}
