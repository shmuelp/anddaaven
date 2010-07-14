package com.saraandshmuel.anddaaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.saraandshmuel.anddaaven.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AndDaavenTefilla extends Activity {
	
	private static final String TAG = "AndDaavenTefilla";

	public AndDaavenTefilla() {
//	    System.setProperty("log.tag."+TAG, "VERBOSE");
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// layout view from resource XML file
    	super.onCreate(savedInstanceState);
		boolean fullScreen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("FullScreen", false);
		if ( fullScreen ) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
    	setContentView(R.layout.daaven);
        findLayoutObjects();

        // Make sure that Hebrew text is right-aligned on Froyo 
        if ( VERSION.SDK_INT >= 8 )
        {
        	daavenText.setGravity(Gravity.RIGHT);
        }
        
        setHebrewFont();
        
        // find and setup text to display
        showTefilla(getIntent());
    }

	/**
	 * Sets the Hebrew font on the tefilla text
	 */
	private void setHebrewFont() {
		Typeface face;
		String typefaceName;
		typefaceName = PreferenceManager.getDefaultSharedPreferences(this).getString("TextFont", "SILEOTSR.ttf");
        face = Typeface.createFromAsset(getAssets(), typefaceName );
//        face = Typeface.createFromAsset(getAssets(), "SILEOTSR.ttf");
        daavenText.setTypeface(face);
	}

	/**
	 * Saves local pointers to interesting objects so they may be manipulated
	 */
	private void findLayoutObjects() {
		daavenText = (TextView) findViewById(R.id.DaavenText);
        daavenScroll = (ScrollView) findViewById(R.id.DaavenScroll);
	}
    
    // update text if needed (called when switching back to this Activity)
    @Override
    protected void onNewIntent(Intent intent) {
    	showTefilla(intent);
    	super.onNewIntent(intent);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
		boolean result = false;
		boolean pageDown = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("PageDown", false);
    	int keyAction = event.getAction();
    	int keyCode = event.getKeyCode();
    	Log.v(TAG,"dispatchKeyEvent(), action="+keyAction+",code="+keyCode);
    	int count = 1;
    	if ( keyCode != KeyEvent.KEYCODE_DPAD_UP &&
   			 keyCode != KeyEvent.KEYCODE_DPAD_DOWN )
    	{
    		return super.dispatchKeyEvent(event);
    	}
    	
    	if ( !pageDown )
    	{
    		return super.dispatchKeyEvent(event);
    	}
    	
//    	Log.v(TAG, "Got keyAction=" + keyAction + ", keyCode=" + keyCode + 
//    			", ACTION_UP=" + KeyEvent.ACTION_UP + 
//    			", ACTION_DOWN=" + KeyEvent.ACTION_DOWN 
//    			);

    	if ( keyAction == KeyEvent.ACTION_MULTIPLE && 
    	     keyCode != KeyEvent.KEYCODE_UNKNOWN ) {
    		keyAction = KeyEvent.ACTION_DOWN;
    		Log.v(TAG, "Got ACTION_MULTIPLE, repeat=" + event.getRepeatCount());
    		count = event.getRepeatCount()/2 + 1;
    	}

    	if ( scrollHeight == 0 ) {
            Log.v(TAG, "height=" + daavenScroll.getHeight() + 
            		   ", padBottom=" + daavenScroll.getPaddingBottom() + 
            		   ", padTop=" + daavenScroll.getPaddingTop() +
            		   ", fadingEdge=" + daavenScroll.getVerticalFadingEdgeLength() );

            scrollHeight = daavenScroll.getHeight() - 
			   daavenScroll.getPaddingBottom() - 
			   daavenScroll.getPaddingTop() - 
			   daavenScroll.getVerticalFadingEdgeLength() * 2;

            Log.v(TAG, "scrollHeight=" + scrollHeight );
    	}
    	
    	
    	if ( keyAction == KeyEvent.ACTION_DOWN) {
    		switch ( keyCode ) {
    			case KeyEvent.KEYCODE_DPAD_UP:
    				pageUp(count);
    	    		result = true;
    	    		break;
    			case KeyEvent.KEYCODE_DPAD_DOWN:
    				pageDown(count);
    	    		result = true;
    				break;
				default:
					result = super.dispatchKeyEvent(event);
    		}
    	}
    	else if ( keyAction == KeyEvent.ACTION_DOWN ) {
    		// Do nothing - consume event and handle on key up
    		result = true;
    	}
    	else 
    	{
    		result = super.dispatchKeyEvent(event);
    	}

    	return result;
	}
    
	/**
	 * Saves the current scrolled position
	 */
	private void savePosition() {
		Layout layout = daavenText.getLayout();
    	if ( layout != null ) {
    		int line = layout.getLineForVertical(daavenScroll.getScrollY());
    		currentOffset = (layout.getLineStart(line) + layout.getLineEnd(line))/2;
//    		setTitle("Saved current offset=" + currentOffset + ",line=" + line);
    	}
	}

	/**
	 * Restores the current scrolled position 
	 */
	private void restorePosition() {
		Layout layout = daavenText.getLayout();
    	if ( layout != null ) {
    		int line = layout.getLineForOffset(currentOffset);
    		daavenScroll.scrollTo(0, daavenText.getLineHeight() * line );
//    		setTitle("Restored current offset=" + currentOffset + ", line=" + line);
    	} else {
    		Log.e(TAG, "Unable to restore scrolled position because layout was null");
    	}
	}
    
    public void pageDown(int count) {
    	Log.v(TAG, "pageDown("+count+")" );
    	for ( int i=0; i < count; ++i ) {
    		daavenScroll.scrollBy(0, scrollHeight);
    		Log.v(TAG, "scrollBy(0," + scrollHeight + ")" );
    	}
    }

    public void pageUp(int count) {
    	Log.v(TAG, "pageUp("+count+")" );
    	for ( int i=0; i < count; ++i ) {
    		daavenScroll.scrollBy(0, -scrollHeight);
    		Log.v(TAG, "scrollBy(0," + (-scrollHeight) + ")" );
    	}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	savePosition();
    	outState.putInt("TefillaPosition", currentOffset);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	final Bundle myState = savedInstanceState;
    	daavenText.post( new Runnable() {
    		public void run() {
    	    	currentOffset = myState.getInt("TefillaPosition");
    	    	if ( currentOffset == 0 ) {
    	    		Log.w(TAG, "Warning: asked to restore position of 0");
    	    	}
    	    	restorePosition();
    		}
    	} );
    }

    /**
     * Initial menu item creator- still being implemented
     * 
     * TODO: Share this code with AndDaavenSplash
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		boolean settingInTefilla = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("SettingInTefilla", false);
    	
		if (settingInTefilla)
		{
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.mainmenu, menu);
	        return true;
		}
		
        return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * Initial menu item handler - still being implemented
     * 
     * TODO: Share this code with AndDaavenSplash
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.About:
        		Dialog aboutDialog = new Dialog(this);
        		aboutDialog.setContentView(R.layout.acknowlegements);
        		aboutDialog.setTitle(getString(R.string.TextViewAcknowledgementTitle));
        		aboutDialog.show();
        		return true;
        	case R.id.Settings:
        		Intent intent = new Intent(this, com.saraandshmuel.anddaaven.AndDaavenSettings.class);
        		startActivity(intent);
        		return true;
        }
        Log.w(getClass().getName(), "Got an unknown MenuItem event");
        return false;        
    }

    // build filename in assets to use to display tefilla, call helper to read it
    public void showTefilla(Intent intent) {
    	String tefillaPath = intent.getData().getSchemeSpecificPart();
    	int tefillaId=0;
    	if ( tefillaPath.startsWith("com.saraandshmuel.anddaaven/")) {
    		 tefillaId = Integer.parseInt(tefillaPath.substring(28));
	 	}
    	String filename = getResources().getStringArray(R.array.FileName)[tefillaId];
    	prepareTefilla(filename);

        // Put Hebrew date on the title bar
        Time t = new Time();
        t.setToNow();
        
        // Advance to next day for ma'ariv
        if ( tefillaId == 2 && ( t.hour > 12 )) {
        	++t.monthDay;
        	t.normalize(false);
        }

        HebrewDate h = new HebrewDate(t);
		setTitle(getTitle() + " " + h);
    }

    /** 
     * Read text in from file (if not already being displayed) and display it 
     * in the daavenText TextView
     * @param filename The filename to read in
     */
	private void prepareTefilla(final String filename) {
		if ( filename == this.currentFilename ) {
			return;
		}
		
		boolean showNikud = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("ShowNikud", true);
		currentOffset=0;
		daavenText.setText(filename);
		try {
			daavenText.setText("Preparing " + filename);
			InputStream is = getAssets().open(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			ssb.clear();
			ArrayList<Integer> jumpOffsets = new ArrayList<Integer>(); 
			int offset=0;
			while ( br.ready() ) {
				String s = br.readLine();
				if ( s.length()==1 &&  s.charAt(0)=='\013') {
					jumpOffsets.add(offset);
					++offset;
				} else {
					if ( ! showNikud ) {
						// Remove nikud based on Unicode character ranges
						// Does not replace combined characters (\ufb20-\ufb4f)
						// See http://en.wikipedia.org/wiki/Unicode_and_HTML_for_the_Hebrew_alphabet
						s = s.replaceAll("[\u05b0-\u05c7]", "");
					}
					ssb.append(s);
					ssb.append("\n");
					offset += s.length() + 1;
				}
			}
			
			daavenText.setText(ssb);
			currentFilename = filename;
			
//			// In UI thread:
//			// Get layout
//			// For each jump offset:
//			// 		layout.getLineForOffset
//			//		layout.getLineForVertical
//			daavenText.post( new Runnable() {
//				public void run() {
//				};
//			});
		} catch (IOException e) {
			Toast.makeText(this, "Caught an exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
	}
	
	// use a SpannableStringBuilder to allow addition of formatting in
	// future
	SpannableStringBuilder ssb = new SpannableStringBuilder();
	private int currentOffset=0;
    private TextView daavenText=null;
    private ScrollView daavenScroll=null; 
    private String currentFilename="";
    private int scrollHeight=0;
}
