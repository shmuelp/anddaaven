package com.saraandshmuel.anddaaven;
import android.annotation.TargetApi;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.os.Bundle;

@TargetApi(11)
public class AndDaavenTefillaHoneycomb extends AndDaavenTefillaFroyo
{
	private final String TAG = "AndDaavenTefillaHoneycomb";

	public AndDaavenTefillaHoneycomb() {
		Log.v(TAG, "AndDaavenTefillaHoneycomb()");
		System.setProperty("log.tag." + TAG, "VERBOSE");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(model.getTefillaName());
		getActionBar().setSubtitle(model.getDateString());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG , "onOptionsItemSelected() beginning");
		switch (item.getItemId()) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				Intent intent = new Intent(this, AndDaavenSplash.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			// No default case; try to resolve in base class
		}
		return super.onOptionsItemSelected(item);
	}
}
