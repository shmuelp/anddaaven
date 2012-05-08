package com.saraandshmuel.anddaaven;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
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
		
        PreferenceManager man=getPreferenceManager();
        
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("test.autoIndex", false)) {
			man.findPreference("AutoIndexBrachot").setEnabled(true);
		}
		
		Preference pref=man.findPreference("FontSize");
		if (pref != null && pref instanceof ListPreference) {
			Log.v(TAG, "Found font size pref");
			ListPreference listPref = (ListPreference) pref;
			CharSequence[] entries = listPref.getEntries();
			CharSequence[] values = listPref.getEntryValues();
			CharSequence[] newEntries = new CharSequence[entries.length+1];
			CharSequence[] newValues = new CharSequence[values.length+1];
			System.arraycopy(entries, 0, newEntries, 1, entries.length);
			System.arraycopy(values, 0, newValues, 1, values.length);
			//TODO L10N
			String curValue = prefs.getString("FontSize", "20");
			Log.v(TAG, "Adding new pref for value " + curValue);
			newValues[0] = curValue;
			newEntries[0] = "Current ("+curValue+")";
			Log.v(TAG, "Setting new values");
			listPref.setEntries(newEntries);
			listPref.setEntryValues(newValues);
			Log.v(TAG, "Finished modifying values");
		}
		
		Log.v(TAG, "onCreate() ending");
    }
}
