package com.saraandshmuel.anddaaven;

import android.app.Activity;

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
			model = new AndDaavenTefillaModel(activity);
			view = new AndDaavenTefillaView(activity);
			controller = new AndDaavenTefillaController(activity);
			controller.setView(view);
			controller.setModel(model);
			model.setView(view);
		}
	}
