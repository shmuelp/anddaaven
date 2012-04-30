package com.saraandshmuel.anddaaven;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.preference.*;
import android.widget.*;

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
		Toast.makeText(this, "Looking for AutoIndex testpref, #prefs=" + prefs.getAll().size(), Toast.LENGTH_SHORT).show();
		if (prefs.getBoolean("test.autoIndex", false)) {
			PreferenceManager man=getPreferenceManager();
			man.findPreference("AutoIndexBrachot").setEnabled(true);
			Toast.makeText(this, "Enabling AutoIndex", Toast.LENGTH_SHORT).show();
		}
		
		Log.v(TAG, "onCreate() ending");
    }
}
