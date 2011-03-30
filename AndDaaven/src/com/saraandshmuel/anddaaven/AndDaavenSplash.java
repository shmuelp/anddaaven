package com.saraandshmuel.anddaaven;

import com.saraandshmuel.anddaaven.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


/**
 * Main activity of the AndSiddur application
 * @author shmuelp
 *
 */
public class AndDaavenSplash extends Activity implements OnClickListener {

	private static final String TAG = "AndDaavenSplash";

	public AndDaavenSplash() {
		Log.v(TAG, "AndDaavenSplash()");
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate() beginning");
    	// layout view from resource XML file
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // setup Typeface object with Hebrew font
        Typeface face;
        face = Typeface.createFromAsset(getAssets(), "FreeSerifBoldSubset.ttf");

        // get references to buttons
    	shacharitButton = (Button) findViewById(R.id.ShacharitButton);
    	minchaButton = (Button) findViewById(R.id.MinchaButton);
    	maarivButton = (Button) findViewById(R.id.MaarivButton);
    	berachotButton = (Button) findViewById(R.id.BerachotButton);
//    	estherButton = (Button) findViewById(R.id.EstherButton);
    	
    	// register to receive clicks on the buttons
    	shacharitButton.setOnClickListener(this);
    	minchaButton.setOnClickListener(this);
    	maarivButton.setOnClickListener(this);
    	berachotButton.setOnClickListener(this);
//    	estherButton.setOnClickListener(this);
    	
    	// set the typeface so Hebrew labels can be displayed correctly
    	shacharitButton.setTypeface(face);
    	minchaButton.setTypeface(face);
    	maarivButton.setTypeface(face);
    	berachotButton.setTypeface(face);
//    	estherButton.setTypeface(face);
    	
    	// get time
    	Time time = new Time();
    	time.set(System.currentTimeMillis());
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
    	toHighlight.setLines(4);
    	toHighlight.requestFocus();
    	
    	// Add the version number to the title bar
    	try {
			PackageInfo pi = getPackageManager().getPackageInfo("com.saraandshmuel.anddaaven",0);
	        setTitle(getTitle() + " " + pi.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
		}
		Log.v(TAG, "onCreate() ending");
    }

    /** 
     * Create menu to display when requested
     */
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptions() beginning");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
		Log.v(TAG, "onCreateOptions() returning true");
        return true;
	}
    
    /**
     * Initial menu item handler - still being implemented
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected() beginning");
        switch (item.getItemId()) {
        	case R.id.About:
        		Dialog aboutDialog = new Dialog(this);
        		aboutDialog.setContentView(R.layout.acknowlegements);
        		aboutDialog.setTitle(getString(R.string.TextViewAcknowledgementTitle));
        		aboutDialog.show();
        		Log.v(TAG, "onOptionsItemSelected() returning early 1");
        		return true;
        	case R.id.Settings:
        		Intent intent = new Intent(this, com.saraandshmuel.anddaaven.AndDaavenSettings.class);
        		startActivity(intent);
        		Log.v(TAG, "onOptionsItemSelected() returning early 2");
        		return true;
        }
        Log.w(getClass().getName(), "Got an unknown MenuItem event");
		Log.v(TAG, "onOptionsItemSelected() ending");
        return false;        
    }

    /** 
     * Handle button clicks
     */
    public void onClick(View v) {
		Log.v(TAG, "onClick() beginning");
    	long index=-1;
    	if ( v==shacharitButton ) { index = 0; }
    	else if ( v==minchaButton ) { index = 1; }
    	else if ( v==maarivButton ) { index = 2; }
    	else if ( v==berachotButton ) { index = 3; }
//    	else if ( v==estherButton ) { index = 4; }
    	
    	// retrieve appropriate filename from resources
    	if ( index != -1 ) {
    		Intent intent = new Intent(this, com.saraandshmuel.anddaaven.AndDaavenTefilla.class);
    		intent.setData(Uri.fromParts("content", 
										 "com.saraandshmuel.anddaaven/" + 
										 Long.toString(index),
										 ""));
    		// call AndDaavenTefilla Activity
    		startActivity(intent);
    	}
		Log.v(TAG, "onClick() ending");
    }

    private Button shacharitButton;
    private Button minchaButton;
    private Button maarivButton;
    private Button berachotButton;
//    private Button estherButton;
}