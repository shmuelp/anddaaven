package com.saraandshmuel.anddaaven;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AndDaavenSettings extends PreferenceActivity {
	public static final String PREFS_NAME = "AndDaavenPrefs";

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    	// layout view from resource XML file
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
