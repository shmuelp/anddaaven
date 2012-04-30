package com.saraandshmuel.anddaaven;
import android.util.*;
import android.content.*;

public class AndDaavenView
{
	private String TAG="AndDaavenView";
	
	public int getNightModeStyle(Context context, AndDaavenModel model) {
		if (model.getNightMode(context))
			return R.style.MyDark;
		else
			return R.style.MyLight;
	}

	public void setNightMode(Context context, AndDaavenModel model) {
		Log.v(TAG, "setNightMode() beginning");
		boolean nightMode = model.getNightMode(context);
		if (nightMode) {
			context.setTheme(R.style.MyDark);
		}
		Log.v(TAG, "setNightMode() ending");
	}
}
