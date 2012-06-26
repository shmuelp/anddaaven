package com.saraandshmuel.anddaaven;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;

import com.saraandshmuel.anddaaven.R.array;
import com.saraandshmuel.anddaaven.R.string;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

public class AndDaavenTefillaController extends AndDaavenBaseController {

	protected AndDaavenTefillaView view;
	private AndDaavenTefillaModel model;

	private static final String TAG = "AndDaavenTefillaController";

	public AndDaavenTefillaController(Activity activity) {
		super(activity);
		System.setProperty("log.tag." + TAG, "VERBOSE");
	}

	public void setView(AndDaavenTefillaView view)
	{
		this.view = view;
		super.setView(view);
	}

//	public AndDaavenTefillaView getView()
//	{
//		return view;
//	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected() beginning");
	    switch (item.getItemId()) {
			case R.id.NightModeButton:
				view.toggleNightMode();
				Intent intent = activity.getIntent();
				//				Only available in API 5 (Eclair)
				//				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.putExtra("ScrollPosition", view.getScrollPos());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Returns the resource filename for a given tefilla 
	 * @param andDaavenTefilla TODO
	 * @param tefillaId
	 * @param nusach
	 * @return
	 */
	protected String getFileName(int tefillaId, String nusach) {
		StringBuilder result = new StringBuilder();
		
		Resources res=activity.getResources();
		String baseStr=res.getStringArray(array.FileName)[tefillaId];
		String extStr=res.getString(string.FileNameSuffix);
		AssetManager assets=activity.getAssets();
		
		model.setTefillaName(tefillaId);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		
		if (prefs.getBoolean("test.html", false)) {
			try {
				result.append(baseStr).append(".html");
				InputStream is=assets.open(result.toString());
				is.close();
				Log.v(TAG, "Found HTML tefilla: " + result.toString());
				return result.toString();
			} catch (IOException e) {
				Log.v(TAG, "Did not find HTML tefilla: " + result.toString());
				result.setLength(0);
			}
		}

		try {
			result.append(baseStr)
	          .append('-')
	          .append(nusach)
	          .append('.')
	          .append(extStr);
		
			Log.d(TAG, "asset filename=" + result.toString());
			
			InputStream is=assets.open(result.toString());
			is.close();
		} catch (IOException e) {
			Log.w(TAG, "Unable to open asset " + result.toString()
					+ ", trying with no nusach");
			result.setLength(0);
			result.append(baseStr)
				  .append('.')
				  .append(extStr);
		}
		
		return result.toString();
	}

	public void showTefilla(Intent intent) {
		Log.v(TAG, "showTefilla() beginning");
		model.setTefilla(intent);
		String tefillaPath = intent.getData().getSchemeSpecificPart();
		int nusach=intent.getIntExtra("nusach", 0);
		if (nusach<0) nusach=0;
		String nusachStr=activity.getResources().getStringArray(array.FileNameNusach)[nusach];
		int tefillaId = 0;
		if (tefillaPath.startsWith("com.saraandshmuel.anddaaven/")) {
			tefillaId = Integer.parseInt(tefillaPath.substring(28));
		}
		String filename = getFileName(tefillaId, nusachStr);
		Log.v(TAG, "About to prepare Tefilla");
		model.prepareTefilla(filename, nusachStr);
	
		Log.v(TAG, "About to check for maariv");
		// Put Hebrew date on the title bar
		// Advance to next day for ma'ariv
		if (tefillaId == 3 && model.inAfternoon() ) {
			model.advanceDay();
		}
	
		Log.v(TAG, "About to set title");
		activity.setTitle(model.getDateString());
	
	//		daavenText.postAfterDraw(new Runnable() {
	//			public void run() {
	//				Layout l=daavenText.getLayout();
	//				if (l==null) {
	//					Log.v(TAG, "Delayed from showTefilla() - layout is null");
	//				} else {
	//					Log.v(TAG, "Delayed from showTefilla() - layout.getLineCount()=" + l.getLineCount() + 
	//							", layout.getHeight()=" + l.getHeight() + 
	//							", layout class name=" + l.getClass().getName());
	//				}
	//			}
	//		});
		
		
		Log.v(TAG, "showTefilla() about to return");
	}

	protected void setModel(AndDaavenTefillaModel model) {
		this.model = model;
	}

//	protected AndDaavenTefillaModel getModel() {
//		return model;
//	}
}
	
