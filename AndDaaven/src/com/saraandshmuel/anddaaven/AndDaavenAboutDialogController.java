package com.saraandshmuel.anddaaven;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.TextView;


/**
 * Creates and controls the about dialog
 * @author shmuelp
 *
 */
public class AndDaavenAboutDialogController implements OnLongClickListener
{

	private Activity context;
	private TextView versionText;
	
	public boolean onLongClick(View p1)
	{
		Intent intent = new Intent(context, com.saraandshmuel.anddaaven.AndDaavenTestSettings.class);
		context.startActivity(intent);
		Log.v(TAG, "onOptionsItemSelected() returning early 2");
		return true;
	}
	
	
	private static final String TAG = "AndDaavenAboutDialogController";

    public AndDaavenAboutDialogController(Activity context){
		this.context=context;
	}
	
	public Dialog create()
	{
		Dialog aboutDialog = new Dialog(context);
		aboutDialog.setContentView(R.layout.acknowlegements);
		String version=new String();
		// Add the version number to the title bar
		try
		{
			PackageInfo pi = context.getPackageManager().getPackageInfo("com.saraandshmuel.anddaaven", 0);
			version = pi.versionName;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
			Log.e(TAG, "Error looking up version information", e);
		}
		View v=aboutDialog.findViewById(R.id.VersionAcknowledgement);
		if (v != null && v instanceof TextView)
		{
			versionText=((TextView)v);
			versionText.setText(version);
			versionText.setOnLongClickListener(this);
			
		} else {
			Log.e(TAG, "Can't set version, v=" + v);
		}
		aboutDialog
			.setTitle(context.getString(R.string.TextViewAcknowledgementTitle));
		aboutDialog.setCancelable(true);
		aboutDialog.setOwnerActivity(context);
		Log.v(TAG, "onOptionsItemSelected() returning early 1");
		return aboutDialog;
	}
}
