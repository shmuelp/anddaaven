package com.saraandshmuel.anddaaven;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;


/**
 * Main activity of the AndSiddur application
 * @author shmuelp
 *
 */
public class AndDaavenSplash extends Activity implements OnClickListener, OnItemSelectedListener {

	static final String TAG = "AndDaavenSplash";
	private AndDaavenChangelogController changelogController;

	private AndDaavenBaseFactory factory;

	public AndDaavenSplash() {
		Log.v(TAG, "AndDaavenSplash()");
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate() beginning");
		// set preferences defaults from XML
		PreferenceManager.setDefaultValues(this, R.xml.settings, false);

		factory=new AndDaavenBaseFactory(this);
		factory.createMvc();
		model=factory.getModel();
		view=factory.getView();
		controller=factory.getController();
		changelogController = new AndDaavenChangelogController(this);

		Log.v(TAG, "onCreate() 01 view=" + view);
		view.setNightModeTheme();
		Log.v(TAG, "onCreate() 02");
		
    	// layout view from resource XML file
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		Log.v(TAG, "onCreate() 03");
        Typeface face = view.getDefaultHebrewTypeface();
		Log.v(TAG, "onCreate() 04");
		
        // get references to buttons
        nusachSpinner = (Spinner) findViewById(R.id.NusachSpinner);
    	shacharitButton = (Button) findViewById(R.id.ShacharitButton);
    	minchaButton = (Button) findViewById(R.id.MinchaButton);
    	minchaFastDayButton = (Button) findViewById(R.id.MinchaFastDayButton);
    	maarivButton = (Button) findViewById(R.id.MaarivButton);
    	berachotButton = (Button) findViewById(R.id.BerachotButton);
    	kiddushLevanaButton = (Button) findViewById(R.id.KiddushLevanaButton);
    	chanukkaButton = (Button) findViewById(R.id.ChanukkaButton);
    	estherButton = (Button) findViewById(R.id.EstherButton);
    	
		Log.v(TAG, "onCreate() 05");
		
		// set defaults
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
    	int nusachPosition=prefs.getInt("LastNusach", -1);
    	nusachSpinner.setSelection(nusachPosition);
		if (prefs.getBoolean("test.extraNusach", false)) {
			nusachSpinner.setVisibility(View.VISIBLE);
		}
    	
    	// register to receive clicks on the buttons
    	shacharitButton.setOnClickListener(this);
    	minchaButton.setOnClickListener(this);
    	minchaFastDayButton.setOnClickListener(this);
    	maarivButton.setOnClickListener(this);
    	berachotButton.setOnClickListener(this);
    	kiddushLevanaButton.setOnClickListener(this);
    	chanukkaButton.setOnClickListener(this);
    	estherButton.setOnClickListener(this);
    	nusachSpinner.setOnItemSelectedListener(this);
    	
    	// set the typeface so Hebrew labels can be displayed correctly
    	shacharitButton.setTypeface(face);
    	minchaButton.setTypeface(face);
    	minchaFastDayButton.setTypeface(face);
    	maarivButton.setTypeface(face);
    	berachotButton.setTypeface(face);
    	kiddushLevanaButton.setTypeface(face);
    	chanukkaButton.setTypeface(face);
    	estherButton.setTypeface(face);
    	
		Log.v(TAG, "onCreate() 06");

    	// get time
    	Time time = new Time();
    	time.setToNow();
    	int hour = time.hour;
    	
    	// heuristically guess the correct tefilla
    	Button toHighlight;
    	if ( hour >= 4 && hour < 12 ) {
    		toHighlight = shacharitButton;
    	} else if ( hour >= 12 && hour < 18 ) {
    		toHighlight = minchaButton;
    	} else {
    		toHighlight = maarivButton;
    	}

		Log.v(TAG, "onCreate() 07");
    	HebrewDate h = new HebrewDate(time);
    	if (model.isFastDay(h, time)) {
    		minchaFastDayButton.setVisibility(View.VISIBLE);
    		if (toHighlight==minchaButton) toHighlight=minchaFastDayButton;
    	}

		Log.v(TAG, "onCreate() 08");

    	toHighlight.setLines(4);
    	toHighlight.requestFocus();
    	
    	// Add the version number to the title bar
		setTitle(getTitle() + " " + AndDaavenBaseModel.getVersion(this));
		
		Log.v(TAG, "onCreate() ending");
    }

	@Override
	protected void onResume() {
		changelogController.showIfNewVersion();

		super.onResume();
	}

    /** 
     * Create menu to display when requested
     */
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptions() beginning");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        if (prefs.getBoolean("test.extraActions", false)) {
//        	menu.findItem(R.id.ChangelogButton).setVisible(true);
//        }
		Log.v(TAG, "onCreateOptions() returning true");
        return true;
	}
    
    /**
	 * Initial menu item handler - still being implemented
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return controller.onOptionsItemSelected(item);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onPrepareDialog() calling superclass");
		super.onPrepareDialog(id, dialog);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Log.v(TAG, "onCreateDialog(" + id + ") beginning");
		switch (id) {
			case R.id.About:
				AndDaavenAboutDialogController aboutController = new AndDaavenAboutDialogController(this);
				return aboutController.create();
			case R.id.ChangelogButton:
				return changelogController.create();
		}
		Log.v(TAG, "onCreateDialog() about to return");
		return super.onCreateDialog(id);
	}
	
    /** 
     * Handle button clicks
     */
    public void onClick(View v) {
		Log.v(TAG, "onClick() beginning");
    	long index=-1;
    	if ( v==shacharitButton ) { index = 0; }
    	else if ( v==minchaButton ) { index = 1; }
    	else if ( v==minchaFastDayButton ) { index = 2; }
    	else if ( v==maarivButton ) { index = 3; }
    	else if ( v==berachotButton ) { index = 4; }
    	else if ( v==kiddushLevanaButton ) { index = 5; }
    	else if ( v==chanukkaButton ) { index = 6; }
    	else if ( v==estherButton ) { index = 7; }
    	
    	// retrieve appropriate filename from resources
    	if ( index != -1 ) {
    		Intent intent = AndDaavenBaseFactory.getTefillaIntent(this);
    		intent.setData(Uri.fromParts("content", 
										 "com.saraandshmuel.anddaaven/" + 
										 Long.toString(index),
										 ""));
    		intent.putExtra("nusach", nusachSpinner.getSelectedItemPosition());
    		Log.i(TAG, "About to launch tefilla with Intent " + intent.toURI());
    		// call AndDaavenTefilla Activity
    		startActivity(intent);
    	} else {
    		Log.e(TAG, "Received click from unknown view; id=" + v.getId());
    	}
		Log.v(TAG, "onClick() ending");
    }

    private Spinner nusachSpinner;
    private Button shacharitButton;
    private Button minchaButton;
    private Button minchaFastDayButton;
    private Button maarivButton;
    private Button berachotButton;
    private Button kiddushLevanaButton;
    private Button chanukkaButton;
    private Button estherButton;
	private AndDaavenBaseModel model;
	AndDaavenBaseView view;
	private AndDaavenBaseController controller;

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		if (parent==nusachSpinner) {
			if (prefs.getInt("LastNusach", -1) != position) {
				prefs.edit().putInt("LastNusach", position).commit();
			}
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// Ignore: should never happen, and if it does, there is no harm to leave the pref
	}
}
