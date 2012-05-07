package com.saraandshmuel.anddaaven;
import com.saraandshmuel.anddaaven.R.id;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

public class AndDaavenBaseController
{
	static final String TAG = "AndDaavenBaseController";
	protected AndDaavenBaseView view;
	protected Activity activity;

	public AndDaavenBaseController(Activity activity) {
		System.setProperty("log.tag." + TAG, "VERBOSE");
		this.activity = activity;
	}
	
	public void feedback(Context context)
	{
		// TODO: Implement this method
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822"); //"text/plain");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"android@saraandshmuel.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "AndDaaven Siddur");
		StringBuilder sb = new StringBuilder();
		sb.append("App version: ").append(AndDaavenBaseModel.getVersion(context));
		sb.append("\nAndroid version: ").append(AndDaavenBaseModel.getAndroidRelease());
		sb.append("\nDevice name: ").append(AndDaavenBaseModel.getAndroidModel());
		sb.append("\n\nFeedback: \n");
		String body = sb.toString();
		intent.putExtra(Intent.EXTRA_TEXT, body);
		context.startActivity(intent);
	}

	/**
	 * Initial menu item handler - still being implemented
	 * @param andDaavenSplash TODO
	 * @param item TODO
	 */
	public boolean onOptionsItemSelected(AndDaavenSplash andDaavenSplash, MenuItem item) {
		return onOptionsItemSelected(item);
	}

	/**
	 * Initial menu item handler - still being implemented
	 * @param item TODO
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected() beginning");
	    switch (item.getItemId()) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				Intent intent = new Intent(activity, AndDaavenSplash.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(intent);
				return true;
	    	case id.About:
	    		activity.showDialog(id.About);
	    		return true;
	    	case id.ChangelogButton:
	    		activity.showDialog(id.ChangelogButton);
	    		return true;
			case R.id.Index:
				activity.showDialog(R.id.Index);
				return true;
	    	case id.Settings:
	    		intent = new Intent(activity, com.saraandshmuel.anddaaven.AndDaavenSettings.class);
	    		activity.startActivity(intent);
	    		return true;
			case id.NightModeButton:
				view.toggleNightMode();
				intent = activity.getIntent();
	//				Only available in API 5 (Eclair)
	//				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				activity.finish(); 
				activity.startActivity(intent);
				break;
			case id.FeedbackButton:
				feedback(activity);
				break;
	    }
	    Log.w(TAG, "Got an unknown MenuItem event");
		Log.v(TAG, "onOptionsItemSelected() ending");
	    return false;        
	}

	public void setView(AndDaavenBaseView view) {
		this.view = view;
	}

	public AndDaavenBaseView getView() {
		return view;
	}
}
