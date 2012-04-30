package com.saraandshmuel.anddaaven;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.*;
import android.view.*;
import android.app.*;
import android.preference.*;


/**
 * Creates and controls the about dialog
 * @author shmuelp
 *
 */
public class AndDaavenChangelogController implements DialogInterface.OnKeyListener
{

	public boolean onKey(DialogInterface dialog, int p2, KeyEvent keyEvent)
	{
		//dialog.dismiss();
		context.removeDialog(R.id.ChangelogButton);
		return true;
	}

	
	private Activity context;
	private static final String TAG = "AndDaavenChangelogController";

    public AndDaavenChangelogController(Activity context){
		this.context=context;
		System.setProperty("log.tag." + TAG, "VERBOSE");
	}
	
	public void showIfNewVersion() {
		try
		{
			String pkg=context.getApplication().getApplicationInfo().packageName;
			PackageInfo pi = context.getPackageManager().getPackageInfo(pkg, 0);
			int curVersion = pi.versionCode;
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			int runVersion = prefs.getInt("LastRunVersion", 0);
			
			Log.v(TAG, "Last run check, current=" + curVersion + ", lastRunVersion=" + runVersion);
			
			if (runVersion < curVersion ) {
				context.showDialog(R.id.ChangelogButton);
				prefs.edit().putInt("LastRunVersion", curVersion).commit();
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
		changelogDialog.setOnKeyListener(this);
		changelogDialog.show();
		Log.v(TAG, "onOptionsItemSelected() returning early 1");
		return changelogDialog;
	}
}
