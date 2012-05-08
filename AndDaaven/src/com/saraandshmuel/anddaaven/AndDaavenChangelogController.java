package com.saraandshmuel.anddaaven;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;


/**
 * Creates and controls the about dialog
 * @author shmuelp
 *
 */
public class AndDaavenChangelogController
{
	
	private Activity context;
	private static final String TAG = "AndDaavenChangelogController";

    public AndDaavenChangelogController(Activity context){
		this.context=context;
		System.setProperty("log.tag." + TAG, "VERBOSE");
	}
	
	public void showIfNewVersion() {
		try
		{
			String pkg=context.getPackageName();
			PackageInfo pi = context.getPackageManager().getPackageInfo(pkg, 0);
			int curVersion = pi.versionCode;
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			int runVersion = prefs.getInt("LastRunVersion", 0);
			
			Log.v(TAG, "Last run check, current=" + curVersion + ", lastRunVersion=" + runVersion);
			
			if (runVersion < curVersion ) {
				prefs.edit().putInt("LastRunVersion", curVersion).commit();
				context.runOnUiThread(new Runnable() {
					public void run() {
						context.showDialog(R.id.ChangelogButton);
					}
				});
			}
		}
		catch (NameNotFoundException e)
		{
			Log.e(TAG, "Error looking up version information", e);
		}
		
	}
	
	public Dialog create()
	{
		Dialog changelogDialog = new Dialog(context);
		changelogDialog.setContentView(R.layout.changelog);

		changelogDialog
			.setTitle(context.getString(R.string.ChangelogTitle));
		
		WebView webView = (WebView) changelogDialog.findViewById(R.id.ChangelogHtmlView);
		webView.loadUrl("file:///android_asset/changelog.html");
		
		changelogDialog.setCancelable(true);
		changelogDialog.setOwnerActivity(context);
		Log.v(TAG, "onOptionsItemSelected() returning early 1");
		return changelogDialog;
	}
}
