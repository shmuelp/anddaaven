package com.saraandshmuel.anddaaven;
import android.os.Bundle;
import android.util.Log;

public class AndDaavenTefillaIcs extends AndDaavenTefillaHoneycomb
{
	private final String TAG = "AndDaavenTefillaIcs";

	public AndDaavenTefillaIcs() {
		Log.v(TAG, "AndDaavenTefillaIcs()");
		System.setProperty("log.tag." + TAG, "VERBOSE");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setHomeButtonEnabled(true);
	}
}
