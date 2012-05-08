package com.saraandshmuel.anddaaven;

import android.app.Activity;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

public class AndDaavenTefillaView extends AndDaavenBaseView
{

	private String TAG="AndDaavenTefillaView";
	private TextView daavenText;
	private ScrollView daavenScroll;

	public AndDaavenTefillaView(Activity activity) {
		super(activity);
	}

	public int getScrollPos() {
		return daavenScroll.getScrollY();
	}
	
	public void findLayoutObjects() {
		Log.v(TAG, "findLayoutObjects() beginning");
		daavenText = (TextView) activity.findViewById(R.id.DaavenText);
		daavenScroll = (ScrollView) activity.findViewById(R.id.DaavenScroll);
		Log.v(TAG, "findLayoutObjects() ending");
	}
}
