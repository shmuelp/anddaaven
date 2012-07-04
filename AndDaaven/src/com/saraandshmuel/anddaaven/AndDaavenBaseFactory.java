package com.saraandshmuel.anddaaven;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION_CODES;


public class AndDaavenBaseFactory
{

	protected Activity activity;
	protected AndDaavenBaseModel model;
	protected AndDaavenBaseController controller;
	protected AndDaavenBaseView view;

	public AndDaavenBaseFactory(Activity activity) {
		this.activity=activity;
	}

	public AndDaavenBaseView getView()
	{
		return view;
	}

	public AndDaavenBaseController getController()
	{
		return controller;
	}

	public AndDaavenBaseModel getModel()
	{
		return model;
	}
	
	public void createMvc() {
		model = new AndDaavenBaseModel();
		view = new AndDaavenBaseView(activity);
		controller = new AndDaavenBaseController(activity);
		controller.setView(view);
		controller.setModel(model);
	}

	public static Intent getTefillaIntent(Context context) {
		Intent result = new Intent();
		String pkg = context.getPackageName(); // com.saraandshmuel.anddaaven
		
		// Ice Cream Sandwich
		if (AndDaavenBaseModel.getAndroidSdkVersion() >= VERSION_CODES.ICE_CREAM_SANDWICH ) { 
			result.setClassName(pkg, "com.saraandshmuel.anddaaven.AndDaavenTefillaIcs");
		// Honeycomb
		} else if (AndDaavenBaseModel.getAndroidSdkVersion() >= VERSION_CODES.HONEYCOMB ) {
			result.setClassName(pkg, "com.saraandshmuel.anddaaven.AndDaavenTefillaHoneycomb");
		// Froyo
		} else if (AndDaavenBaseModel.getAndroidSdkVersion() >= VERSION_CODES.FROYO ) { 
			result.setClassName(pkg, "com.saraandshmuel.anddaaven.AndDaavenTefillaFroyo");
		// Cupcake
		} else {
			result.setClassName(pkg, "com.saraandshmuel.anddaaven.AndDaavenTefilla");
		}
		
		return result;
	}
}
