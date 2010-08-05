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
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
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
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AndDaavenTefilla extends Activity implements OnSharedPreferenceChangeListener {
	
	private static final String TAG = "AndDaavenTefilla";

	public AndDaavenTefilla() {
//	    System.setProperty("log.tag."+TAG, "VERBOSE");
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// layout view from resource XML file
    	super.onCreate(savedInstanceState);
		setFullScreen();

		setContentView(R.layout.daaven);
        findLayoutObjects();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        setHebrewFont();
        setFontSize();
		setAlignment();
                
        // find and setup text to display
        showTefilla(getIntent());
    }

	/**
	 * Sets the alignment - called from onCreate and on OldAlignment pref change
	 */
	private void setAlignment() {
		// Make sure that Hebrew text is right-aligned on Froyo
		// Some third-party ROMs need different logic for Froyo
        boolean oldAlignment = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("OldAlignment", false);
        final int version = Integer.parseInt(VERSION.SDK); 
        if ( version >= 8 && !oldAlignment )
        {
			daavenText.setGravity(Gravity.RIGHT);
        } else {
			daavenText.setGravity(Gravity.LEFT);
        }
	}

	/**
	 * Sets the content view - called from onCreate and if FullScreen pref is changed
	 */
	private void setFullScreen() {
		boolean fullScreen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("FullScreen", false);
		if ( fullScreen ) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	/**
	 * Sets the Hebrew font on the tefilla text
	 */
	private void setHebrewFont() {
		String typefaceName;
		typefaceName = PreferenceManager.getDefaultSharedPreferences(this).getString("TextFont", "SILEOTSR.ttf");
        final Typeface face = Typeface.createFromAsset(getAssets(), typefaceName );
//        face = Typeface.createFromAsset(getAssets(), "SILEOTSR.ttf");

//        daavenText.setTypeface(face);
        runOnUiThread(new Runnable() {
        	public void run() { daavenText.setTypeface(face); }
        });
	}

	/**
	 * Sets the font size of the tefilla text
	 */
	private void setFontSize() {
		String sizePref = PreferenceManager.getDefaultSharedPreferences(this).getString("FontSize", "17"); 
		float size = Float.parseFloat(sizePref);
		daavenText.setTextSize(size);
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
		boolean pageDown = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("PageDown", true);
		boolean sectionJump = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("SectionJump", true);
		String volumeButtonCode = PreferenceManager.getDefaultSharedPreferences(this).getString("VolumeButton", "IGNORE");
		
    	int keyAction = event.getAction();
    	int keyCode = event.getKeyCode();
    	Log.v(TAG,"dispatchKeyEvent(), action="+keyAction+",code="+keyCode);
    	
    	// Flag to determine if AndDaaven handles the event or if it delegates
    	boolean handleEvent= false;

    	if ( pageDown && ( keyCode == KeyEvent.KEYCODE_DPAD_UP ||
   			 			   keyCode == KeyEvent.KEYCODE_DPAD_DOWN ) )
    	{
    		handleEvent = true;
    	}
    	
    	if ( sectionJump && ( keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
	 			   			  keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) )
    	{
    		handleEvent = true;
    	}
    	
    	if ( !volumeButtonCode.equals("IGNORE") && ( keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
    												 keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ) )
    	{
    		handleEvent = true;
    		if ( volumeButtonCode.equals("PAGE") ) {
    			if ( keyCode == KeyEvent.KEYCODE_VOLUME_UP ) {
    				keyCode = KeyEvent.KEYCODE_DPAD_UP;
    			} else {
    				keyCode = KeyEvent.KEYCODE_DPAD_DOWN;
    			}
    		} else if ( volumeButtonCode.equals("SECTION") ) {
    			if ( keyCode == KeyEvent.KEYCODE_VOLUME_UP ) {
    				keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
    			} else {
    				keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
    			}
    		}
    	}

//    	Log.v(TAG, "Got keyAction=" + keyAction + ", keyCode=" + keyCode + 
//    			", ACTION_UP=" + KeyEvent.ACTION_UP + 
//    			", ACTION_DOWN=" + KeyEvent.ACTION_DOWN 
//    			);
    	
    	if ( !handleEvent ) {
    		return super.dispatchKeyEvent(event);
    	}

    	int count = 1;
    	if ( keyAction == KeyEvent.ACTION_MULTIPLE && 
    	     keyCode != KeyEvent.KEYCODE_UNKNOWN ) {
    		keyAction = KeyEvent.ACTION_DOWN;
    		Log.v(TAG, "Got ACTION_MULTIPLE, repeat=" + event.getRepeatCount());
    		count = event.getRepeatCount()/2 + 1;
    	}

    	// Calculate screen height for page scrolling if not already cached
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
    			case KeyEvent.KEYCODE_DPAD_LEFT:
    				count=-count;
    				// intentional pass-through
    			case KeyEvent.KEYCODE_DPAD_RIGHT:
    				jumpSection(count);
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
     * Jumps back of forward a number of sections
     * @param count the number of sections to jump forward.  Will jump 
     * backward with a negative count
     */
	private void jumpSection(int count) {
		Layout layout = daavenText.getLayout();
    	if ( layout == null ) {
    		Log.w(TAG, "jumpSection(): cannot jump if layout is null!");
    		return;
    	}
    	
    	// Check that jumpOffsets has been initialized
    	if ( jumpOffsets.isEmpty() ) {
    		Log.w(TAG, "jumpSection(): cannot jump if jumpOffsets is empty!");
    		return;
    	}
	
    	// Get current offset
		int offset = locateCurrentOffset(layout);
		
		// Find current section in jumpOffsets
		int section=0;
		for (section = 0; section+1 < jumpOffsets.size(); ++section) {
			if ( offset < jumpOffsets.get(section+1) ) {
				break;
			}
		}
		Log.d(TAG, "Found offset=" + offset + ", section=" + section);
		
		// Calculate new section
		// When moving backwards, first scroll to the beginning of the 
		// current section
		if ( count < 0 && layout.getLineForOffset(offset) > 
				layout.getLineForOffset(jumpOffsets.get(section)) ) {
			++count;
		}
		section += count;
		if ( section < 0 ) {
			// Tried to scroll past initial section
			// Reset position to beginning of initial section
			section = 0;
		} else if ( section >= jumpOffsets.size() ) {
			// Tried to go past last section
			// Scroll to end and return
			daavenScroll.scrollTo(0, daavenText.getBottom());
			return;
		}
		
		// Scroll to new section
		int newOffset = jumpOffsets.get(section);
		int newLine = layout.getLineForOffset(newOffset);
		int newY = layout.getLineTop(newLine);
		Log.d(TAG, "Scrolling to offset=" + newOffset + ", section=" + 
				   section + ", line=" + newLine + ", y=" + newY );
		daavenScroll.scrollTo(0, newY);
	}

	/**
	 * Saves the current scrolled position
	 */
	private void savePosition() {
		Layout layout = daavenText.getLayout();
    	if ( layout != null ) {
    		int newOffset = locateCurrentOffset(layout);
    		if ( layout.getLineForOffset(newOffset) != 
    			 layout.getLineForOffset(currentOffset) ) {
    			currentOffset = newOffset;
//    			setTitle("Saved current offset=" + currentOffset + ",line=" + line);
    		}
    	}
    	else {
    		Log.w(TAG, "savePosition(): cannot save if layout is null!");
    	}
	}

	/**
	 * 	Locates the current offset by examining the scroll state of the text
	 * @param layout - the layout object of the current tefilla
	 * @return The current offset of the tefilla (in characters of text)
	 */
	private int locateCurrentOffset(Layout layout) {
		int result;
		// Find highest pixel of interest (ignore e.g. faded pixels)
		int topPixel = daavenScroll.getScrollY() + daavenScroll.getPaddingTop() + 
					   daavenScroll.getVerticalFadingEdgeLength();
		int line = layout.getLineForVertical(topPixel);
//		result = (layout.getLineStart(line) + layout.getLineEnd(line))/2;
		result = layout.getLineStart(line);
		return result;
	}

	/**
	 * Restores the current scrolled position 
	 */
	private void restorePosition() {
		runOnUiThread(new Runnable() {
			public void run() {
				Layout layout = daavenText.getLayout();
		    	if ( layout != null ) {
		    		int line = layout.getLineForOffset(currentOffset);
		    		daavenScroll.scrollTo(0, daavenText.getLineHeight() * line + 
		    								 daavenText.getPaddingTop() + 
		    								 daavenText.getVerticalFadingEdgeLength() );
//		    		setTitle("Restored current offset=" + currentOffset + ", line=" + line);
		    	} else {
		    		Log.e(TAG, "Unable to restore scrolled position because layout was null");
		    	}
			}
		
		});
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
		boolean showSectionNames = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("SectionName", true);
		currentOffset=0;
		daavenText.setText(filename);
		try {
			daavenText.setText("Preparing " + filename);
			InputStream is = getAssets().open(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			ssb.clear();
			jumpOffsets.clear();
			sectionNames.clear();
			int offset=0;
			while ( br.ready() ) {
				String s = br.readLine();
				if ( s.length() == 0 ) {
					ssb.append("\n");
					++offset;
				} else if ( s.charAt(0)=='\013' ) {
					jumpOffsets.add( offset );
					String name = s.substring( 1 );
					sectionNames.add( name );
					if ( showSectionNames && name.length() != 0 ) {
						ssb.append(name);
						ssb.append("\n");
						offset += name.length() + 1;
					}
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
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if ( key.equals("TextFont") ) {
			setHebrewFont();
		} else if ( key.equals("FontSize") ) {
			setFontSize();
			daavenText.requestLayout();
		} else if ( key.equals("FullScreen") ) {
			setFullScreen();
			daavenText.requestLayout();
		} else if ( key.equals("OldAlignment") ) {
			setAlignment();
			daavenText.requestLayout();
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
	private ArrayList<Integer> jumpOffsets = new ArrayList<Integer>();
	private ArrayList<String> sectionNames = new ArrayList<String>();
}
