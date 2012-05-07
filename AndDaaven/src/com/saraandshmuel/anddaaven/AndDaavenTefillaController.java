package com.saraandshmuel.anddaaven;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

public class AndDaavenTefillaController extends AndDaavenBaseController {

	protected AndDaavenTefillaView view;

	public AndDaavenTefillaController(Activity activity) {
		super(activity);
	}

	public void setView(AndDaavenTefillaView view)
	{
		this.view = view;
		super.setView(view);
	}

	public AndDaavenTefillaView getView()
	{
		return view;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected() beginning");
	    switch (item.getItemId()) {
			case R.id.NightModeButton:
				view.toggleNightMode();
				Intent intent = activity.getIntent();
				//				Only available in API 5 (Eclair)
				//				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.putExtra("ScrollPosition", view.getScrollPos());
				activity.finish(); 
				activity.startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
	
