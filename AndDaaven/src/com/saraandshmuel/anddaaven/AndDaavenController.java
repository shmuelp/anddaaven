package com.saraandshmuel.anddaaven;
import android.content.*;
import android.widget.*;

public class AndDaavenController
{
	public AndDaavenController() {
	}
	
	public void feedback(Context context)
	{
		// TODO: Implement this method
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822"); //"text/plain");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"android@saraandshmuel.com"});
		intent.putExtra(Intent.EXTRA_SUBJECT, "AndDaaven Siddur");
		StringBuilder sb = new StringBuilder();
		sb.append("App version: ").append(AndDaavenModel.getVersion(context));
		sb.append("\nAndroid version: ").append(AndDaavenModel.getAndroidRelease());
		sb.append("\nDevice name: ").append(AndDaavenModel.getAndroidModel());
		sb.append("\n\nFeedback: \n");
		String body = sb.toString();
		intent.putExtra(Intent.EXTRA_TEXT, body);
		context.startActivity(intent);
	}
}
