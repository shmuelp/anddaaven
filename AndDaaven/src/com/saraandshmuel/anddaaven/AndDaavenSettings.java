package com.saraandshmuel.anddaaven;

import android.os.Bundle;
import android.preference.PreferenceActivity;
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
		Log.v(TAG, "onCreate() ending");
    }
}
