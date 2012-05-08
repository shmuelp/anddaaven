package com.saraandshmuel.anddaaven;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AndDaavenTefillaFactory
{
	protected Activity activity;
	protected AndDaavenTefillaModel model;
	protected AndDaavenTefillaController controller;
	protected AndDaavenTefillaView view;
	
	public AndDaavenTefillaFactory(Activity activity) {
			this.activity=activity;
		}

		public AndDaavenTefillaView getView()
		{
			return view;
		}

		public AndDaavenTefillaController getController()
		{
			return controller;
		}

		public AndDaavenTefillaModel getModel()
		{
			return model;
		}

		public void createMvc() {
			model = new AndDaavenTefillaModel();
			view = new AndDaavenTefillaView(activity);
			controller = new AndDaavenTefillaController(activity);
			controller.setView(view);
		}
	}
