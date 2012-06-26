package com.saraandshmuel.anddaaven;
import android.os.Bundle;
import android.util.Log;
import android.app.ActionBar;

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
		ActionBar actionBar=getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
}
