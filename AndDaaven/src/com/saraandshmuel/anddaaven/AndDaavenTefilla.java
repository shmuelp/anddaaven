package com.saraandshmuel.anddaaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.acra.ErrorReporter;

import com.saraandshmuel.anddaaven.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AndDaavenTefilla extends Activity implements OnSharedPreferenceChangeListener, TextWatcher, android.content.DialogInterface.OnClickListener
{
	
	private static final String TAG = "AndDaavenTefilla";

	public AndDaavenTefilla() {
//	    System.setProperty("log.tag."+TAG, "VERBOSE");
		Log.v(TAG, "AndDaavenTefilla()");
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "onCreate() beginning");
    	// layout view from resource XML file
    	super.onCreate(savedInstanceState);
		setFullScreen();

		setContentView(R.layout.daaven);
        findLayoutObjects();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        setHebrewFont();
        setFontSize();
		setAlignment();
        tapToScroll=PreferenceManager.getDefaultSharedPreferences(this).getBoolean("TapToScroll", tapToScroll);
                
        // find and setup text to display
        showTefilla(getIntent());

//        SDP TODO: if is brachot and autoindex brachot
        
        
        Log.v(TAG, "onCreate() ending");
    }

	/**
	 * Sets the alignment - called from onCreate and on OldAlignment pref change
	 */
	private void setAlignment() {
    	Log.v(TAG, "setAlignment() beginning");
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
    	Log.v(TAG, "setAlignment() ending");
	}

	/**
	 * Sets the content view - called from onCreate and if FullScreen pref is changed
	 */
	private void setFullScreen() {
    	Log.v(TAG, "setFullScreen() beginning");
		boolean fullScreen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("FullScreen", false);
		if ( fullScreen ) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
    	Log.v(TAG, "setAlignment() ending");
	}

	/**
	 * Sets the Hebrew font on the tefilla text
	 */
	private void setHebrewFont() {
    	Log.v(TAG, "setHebrewFont() beginning");
		try {
			if ( hebrewTypeface == null ) {
				String typefaceName;
				typefaceName = PreferenceManager.getDefaultSharedPreferences(this).getString("TextFont", "FreeSerifBoldSubset.ttf");
				
				// Backwards compatibility
				if (typefaceName=="FreeSans.ttf") {
					SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
					edit.putString("TextFont", "FreeSansSubset.ttf");
				}
				if (typefaceName=="FreeMono.ttf") {
					SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
					edit.putString("TextFont", "FreeMonoSubset.ttf");
				}
				
				
			    hebrewTypeface = Typeface.createFromAsset(getAssets(), typefaceName );
//        face = Typeface.createFromAsset(getAssets(), "SILEOTSR.ttf");
			}
		} catch (Exception e) {
			// Apparently, the expected font does not exist.  Most likely, the user selected something 
			// which has changed names.  Clear the pref and use the default
			String typefaceName = "FreeSerifBoldSubset.ttf";
			SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
			edit.putString("TextFont", "FreeSerifBoldSubset.ttf");
			edit.commit();
		    hebrewTypeface = Typeface.createFromAsset(getAssets(), typefaceName );
			e.printStackTrace();
		}

//        daavenText.setTypeface(hebrewTypeface);
        runOnUiThread(new Runnable() {
        	public void run() { daavenText.setTypeface(hebrewTypeface); }
        });
    	Log.v(TAG, "setHebrewFont() ending");
	}
	
	/**
	 * Sets the font size of the tefilla text
	 */
	private void setFontSize() {
    	Log.v(TAG, "setFontSize() beginning");
		float size = getFontSize();
		daavenText.setTextSize(size);
    	Log.v(TAG, "setFontSize() ending");
	}

	/**
	 * Gets the font size of the tefilla text
	 * @return
	 */
	private float getFontSize() {
    	Log.v(TAG, "getFontSize() beginning");
		String sizePref = PreferenceManager.getDefaultSharedPreferences(this).getString("FontSize", "19"); 
		float size = Float.parseFloat(sizePref);
    	Log.v(TAG, "getFontSize() about to return "+size);
		return size;
	}

	/**
	 * Saves local pointers to interesting objects so they may be manipulated
	 */
	private void findLayoutObjects() {
    	Log.v(TAG, "findLayoutObjects() beginning");
		daavenText = (TextView) findViewById(R.id.DaavenText);
		daavenText.addTextChangedListener(this);
		daavenScroll = (ScrollView) findViewById(R.id.DaavenScroll);
    	Log.v(TAG, "findLayoutObjects() ending");
	}
    
    // update text if needed (called when switching back to this Activity)
    @Override
    protected void onNewIntent(Intent intent) {
    	Log.v(TAG, "onNewIntent() beginning");
    	showTefilla(intent);
    	super.onNewIntent(intent);
    	Log.v(TAG, "findLayoutObjects() ending");
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
    	Log.v(TAG, "dispatchKeyEvent() beginning");
		boolean result = false;
		boolean pageDown = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("PageDown", true);
		boolean sectionJump = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("SectionJump", true);
		String volumeButtonCode = PreferenceManager.getDefaultSharedPreferences(this).getString("VolumeButton", "IGNORE");
		
    	int keyAction = event.getAction();
    	int keyCode = event.getKeyCode();
    	Log.d(TAG,"dispatchKeyEvent(), action="+keyAction+",code="+keyCode);
    	
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

//    	Log.d(TAG, "Got keyAction=" + keyAction + ", keyCode=" + keyCode + 
//    			", ACTION_UP=" + KeyEvent.ACTION_UP + 
//    			", ACTION_DOWN=" + KeyEvent.ACTION_DOWN 
//    			);
    	
    	if ( !handleEvent ) {
        	Log.v(TAG, "findLayoutObjects() about to return early");
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
            calculateScrollHeight();
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

    	Log.v(TAG, "findLayoutObjects() about to return");
    	return result;
	}
    
    /**
     * Jumps back of forward a number of sections
     * @param count the number of sections to jump forward.  Will jump 
     * backward with a negative count
     */
	private void jumpSection(int count) {
    	Log.v(TAG, "jumpSection(" + count + ") beginning");
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
	    	Log.v(TAG, "jumpSection() about to return early");
			return;
		}
		
		// Scroll to new section
		int newOffset = jumpOffsets.get(section);
		int newLine = layout.getLineForOffset(newOffset);
		int newY = layout.getLineTop(newLine);
		Log.d(TAG, "Scrolling to offset=" + newOffset + ", section=" + 
				   section + ", line=" + newLine + ", y=" + newY );
		daavenScroll.scrollTo(0, newY);

		Log.v(TAG, "jumpSection() ending");
	}

	/**
	 * Saves the current scrolled position
	 */
	private void savePosition() {
		Log.v(TAG, "savePosition() beginning");
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
		Log.v(TAG, "savePosition() ending");
	}

	/**
	 * 	Locates the current offset by examining the scroll state of the text
	 * @param layout - the layout object of the current tefilla
	 * @return The current offset of the tefilla (in characters of text)
	 */
	private int locateCurrentOffset(Layout layout) {
		Log.v(TAG, "locateCurrentOffset() beginning");
		int result;
		// Find highest pixel of interest (ignore e.g. faded pixels)
		int topPixel = daavenScroll.getScrollY() + daavenScroll.getPaddingTop() + 
					   daavenScroll.getVerticalFadingEdgeLength();
		int line = layout.getLineForVertical(topPixel);
//		result = (layout.getLineStart(line) + layout.getLineEnd(line))/2;
		result = layout.getLineStart(line);
		Log.v(TAG, "locateCurrentOffset() returning " + result);
		return result;
	}

	/**
	 * Restores the current scrolled position 
	 */
	private void restorePosition() {
		Log.v(TAG, "restorePosition() beginning");
		runOnUiThread(new Runnable() {
			public void run() {
				Layout layout = daavenText.getLayout();
		    	if ( layout != null ) {
		    		int line = layout.getLineForOffset(currentOffset);
		    		int y = layout.getLineTop(line);
		    		daavenScroll.scrollTo(0, y);
		    		Log.d(TAG, "Restored current offset=" + currentOffset + ", line=" + line);
		    	} else {
		    		Log.e(TAG, "Unable to restore scrolled position because layout was null");
		    	}
			}
		
		});
		Log.v(TAG, "restorePosition() ending");
	}
    
    public void pageDown(int count) {
    	Log.v(TAG, "pageDown("+count+") beginning" );
    	for ( int i=0; i < count; ++i ) {
    		daavenScroll.scrollBy(0, scrollHeight);
    		Log.d(TAG, "scrollBy(0," + scrollHeight + ")" );
    	}
		Log.v(TAG, "pageDown() ending");
    }

    public void pageUp(int count) {
    	Log.v(TAG, "pageUp("+count+") beginning" );
    	for ( int i=0; i < count; ++i ) {
    		daavenScroll.scrollBy(0, -scrollHeight);
    		Log.v(TAG, "scrollBy(0," + (-scrollHeight) + ")" );
    	}
    	Log.v(TAG, "pageUp() ending" );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	Log.v(TAG, "onSaveInstanceState() beginning" );
    	super.onSaveInstanceState(outState);
    	savePosition();
    	outState.putInt("TefillaPosition", currentOffset);
    	Log.v(TAG, "onSaveInstanceState() ending" );
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	Log.v(TAG, "onRestoreInstanceState() beginning" );
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
    	Log.v(TAG, "onRestoreInstanceState() ending" );
    }

    /**
     * Initial menu item creator- still being implemented
     * 
     * TODO: Share this code with AndDaavenSplash
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	Log.v(TAG, "onCreateOptionsMenu() beginning" );
		boolean settingInTefilla = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("SettingInTefilla", false);
    	
		if (settingInTefilla)
		{
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.mainmenu, menu);
	    	MenuItem index = menu.findItem(R.id.Index);
	    	index.setVisible(true);
	    	Log.v(TAG, "onCreateOptionsMenu() returning early" );
	        return true;
		}
		
    	Log.v(TAG, "onCreateOptionsMenu() about to return" );
        return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * Initial menu item handler - still being implemented
     * 
     * TODO: Share this code with AndDaavenSplash
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.v(TAG, "onOptionsItemSelected() beginning" );
        switch (item.getItemId()) {
        	case R.id.About:
        		Dialog aboutDialog = new Dialog(this);
        		aboutDialog.setContentView(R.layout.acknowlegements);
        		aboutDialog.setTitle(getString(R.string.TextViewAcknowledgementTitle));
        		aboutDialog.show();
            	Log.v(TAG, "onOptionsItemSelected() returning early 1" );
        		return true;
        	case R.id.Settings:
				showIndex();
            	Log.v(TAG, "onOptionsItemSelected() returning early 2" );
        		return true;
        	case R.id.Index:
				showDialog(R.id.Index);
		    	Log.v(TAG, "onOptionsItemSelected() returning early 3" );
        		return true;
        }
        Log.w(getClass().getName(), "Got an unknown MenuItem event");
    	Log.v(TAG, "onOptionsItemSelected() about to return" );
        return false;        
    }

	/**
	 * 
	 */
	private void showIndex() {
		Intent intent = new Intent(this, com.saraandshmuel.anddaaven.AndDaavenSettings.class);
		startActivity(intent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
    	Log.v(TAG, "onCreateDialog(" + id + ") beginning" );
		switch (id) {
		case R.id.Index:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			if ( hebrewTypeface==null ) setHebrewFont();
			ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.index_list_item, sectionNames);
			TypefaceAdapter ta = new TypefaceAdapter(aa, hebrewTypeface, getFontSize());
//			builder.setItems(sectionNames, this);
			builder.setAdapter(ta, this);
			AlertDialog indexDialog = builder.create();
			
//			/// TODO: Finish implementing this 
//			ScrollView sv = new ScrollView(this);
//			ListView lv = new ListView(this);
////			ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.id.IndexDummyText, sectionNames);
//			for (int i = 0; i < sectionNames.length; i++) {
//				TextView tv = new TextView(this);
//				tv.setText(sectionNames[i]);
//				if ( hebrewTypeface==null ) setHebrewFont();
//				tv.setTypeface(hebrewTypeface);
//				tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//				lv.addFooterView(tv);
////				lv.addView(tv, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			}
////			lv.setAdapter(aa);
//			sv.addView(lv, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			
//			LinearLayout ll = new LinearLayout(this);
//			ll.addView(sv, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			
////			FrameLayout fl = (FrameLayout) findViewById(android.R.id.custom);
////			fl.addView(sv, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			
//			Dialog indexDialog = new Dialog(this);
//			indexDialog.setTitle("Select a section");
//			indexDialog.setContentView(ll);

	    	Log.v(TAG, "onCreateDialog() returning early" );
			return indexDialog;
		}
    	Log.v(TAG, "onCreateDialog() about to return" );
		return super.onCreateDialog(id);
	}

	public void onClick(DialogInterface dialog, int which) {
    	Log.v(TAG, "onClick(dialog,"+which+") beginning" );
		Log.d(TAG, "Jump to index section " + which);
		Layout layout = daavenText.getLayout();
    	if ( layout != null ) {
    		int newOffset = sectionOffsets.get(which);
    		int newLine = layout.getLineForOffset(newOffset);
    		int newY = layout.getLineTop(newLine);
    		daavenScroll.scrollTo(0, newY);
    		Log.d(TAG, "Scrolling to offset=" + newOffset + ", section=" + 
    				   which + ", line=" + newLine + ", y=" + newY );
    	} else {
    		Toast.makeText(this, "Cannot jump; layout was null", Toast.LENGTH_SHORT).show();
    	}
    	Log.v(TAG, "onClick() about to return" );
	}
	
    // build filename in assets to use to display tefilla, call helper to read it
    public void showTefilla(Intent intent) {
    	Log.v(TAG, "showTefilla() beginning" );
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
    	Log.v(TAG, "showTefilla() about to return" );
    }

    /** 
     * Read text in from file (if not already being displayed) and display it 
     * in the daavenText TextView
     * @param filename The filename to read in
     */
	private void prepareTefilla(final String filename) {
    	Log.v(TAG, "prepareTefilla(" + filename + ") beginning" );
		ErrorReporter er = ErrorReporter.getInstance();

		if ( filename == this.currentFilename ) {
	    	Log.v(TAG, "prepareTefilla() about to return early" );
			return;
		}
		
		er.addCustomData("prepareTefilla()", filename);
		
		boolean showNikud = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("ShowNikud", true);
		boolean showSectionNames = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("SectionName", true);
		currentOffset=0;
		try {
			InputStream is = getAssets().open(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			ssb.clear();
			jumpOffsets.clear();
			ArrayList<String> sectionNamesList = new ArrayList<String>();
			sectionOffsets.clear();
			int offset=0;
			while ( br.ready() ) {
				String s = br.readLine();
				if ( s.length() == 0 ) {
					ssb.append("\n");
					++offset;
				} else if ( s.charAt(0)=='\013' ) {
					jumpOffsets.add( offset );
					String name = s.substring( 1 );
					if (name.length() > 0 ) {
						sectionNamesList.add( name );
						sectionOffsets.add(offset);
						if ( showSectionNames ) {
							ssb.append(name);
							ssb.append("\n");
							offset += name.length() + 1;
						}
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
			
			sectionNames = sectionNamesList.toArray(new String[0]);

			currentFilename = filename;

			er.addCustomData("ssb.length()", ""+ssb.length());
			er.addCustomData("daavenText.getText().length()", ""+daavenText.getText().length());
			er.addCustomData("showNikud", ""+showNikud);
			er.addCustomData("showSectionNames", ""+showSectionNames);
			er.addCustomData("currentFilename", currentFilename);

			daavenText.setText(ssb);
			
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
			er.addCustomData("IOException.getMessage", e.getMessage());
			er.handleException(e);
		}
    	Log.v(TAG, "prepareTefilla() ending" );
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
    	Log.v(TAG, "onSharedPreferenceChanged(sharedPreferences, " + key + ") beginning" );
		if ( key.equals("TextFont") ) {
			hebrewTypeface=null;
			setHebrewFont();
			scrollHeight = 0; // recompute scroll height when needed
		} else if ( key.equals("FontSize") ) {
			setFontSize();
			daavenText.requestLayout();
			scrollHeight = 0;
		} else if ( key.equals("FullScreen") ) {
			setFullScreen();
			daavenText.requestLayout();
			scrollHeight = 0;
		} else if ( key.equals("OldAlignment") ) {
			setAlignment();
			daavenText.requestLayout();
			scrollHeight = 0;
		} else if ( key.equals("TapToScroll") ) {
			tapToScroll=sharedPreferences.getBoolean(key, tapToScroll);
		} 
    	Log.v(TAG, "onSharedPreferenceChanged() ending" );
	}
	
	public void afterTextChanged(android.text.Editable s) {
    	Log.v(TAG, "afterTextChanged() beginning" );
		ErrorReporter er = ErrorReporter.getInstance();
		er.addCustomData("after:s.length", ""+s.length());
		if (s.length() != ssb.length()) {
			er.handleException(null);
		}
    	Log.v(TAG, "afterTextChanged() ending" );
	}
	
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
    	Log.v(TAG, "beforeTextChanged()" );
	}
	
	public void onTextChanged(CharSequence s, int start, int before, int count) {
    	Log.v(TAG, "onTextChanged()" );
	}
	
	protected void onPostCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "onPostCreate() beginning" );
		ErrorReporter er = ErrorReporter.getInstance();
		er.addCustomData("postCreate:daavenText.length()", ""+daavenText.length());
		if (daavenText.length() != ssb.length()) {
			ErrorReporter.getInstance().handleException(null);
		}
		super.onPostCreate(savedInstanceState);
    	Log.v(TAG, "onPostCreate() ending" );
	};
	
	protected void onPostResume() {
    	Log.v(TAG, "onPostResume() beginning" );
		ErrorReporter er = ErrorReporter.getInstance();
		er.addCustomData("postResume:daavenText.length()", ""+daavenText.length());
		if (daavenText.length() != ssb.length()) {
			ErrorReporter.getInstance().handleException(null);
		}
		super.onPostResume();
    	Log.v(TAG, "onPostResume() ending" );
	};

//	private String getMotionEventString(MotionEvent event) {
//		StringBuilder result = new StringBuilder("action=");
//		int action = event.getAction();
//		if ( action==MotionEvent.ACTION_CANCEL ) result.append("ACTION_CANCEL");
//		else if ( action==MotionEvent.ACTION_DOWN ) result.append("ACTION_DOWN");
//		else if ( action==MotionEvent.ACTION_MASK) result.append("ACTION_MASK");
//		else if ( action==MotionEvent.ACTION_UP) result.append("ACTION_UP");
//		else result.append("UNKNOWN");
//		
//		result.append(", x=").append(event.getX());
//		result.append(", y=").append(event.getY());
//		
//		result.append(", rawX=").append(event.getRawX());
//		result.append(", rawY=").append(event.getRawY());
//		
//		result.append(", downTime=").append(event.getDownTime());
//		result.append(", eventTime=").append(event.getEventTime());
//		
//		long delta=event.getEventTime()-event.getDownTime();
//		result.append(", delta=").append(delta);
//		
//		boolean tap=false;
//		if (action==MotionEvent.ACTION_UP && delta > 0 && delta <= 500 ) tap=true;
//		
//		result.append(", tap=").append(tap);
//		
//		return result.toString();
//	}

	private boolean handleTap(float x, float y) {
    	Log.v(TAG, "handleTap(" + x + ", " + y + ") beginning" );
		int right=daavenScroll.getRight();
		int left=daavenScroll.getLeft();
		int width=right-left;
		int top=daavenScroll.getTop();
		int bottom=daavenScroll.getBottom();
		int height=bottom-top;

    	// Calculate screen height for page scrolling if not already cached
    	if ( scrollHeight == 0 ) {
            calculateScrollHeight();
    	}
    	
		if ( x>=left && x<right && y>=top && y<bottom) {
			if (x<(width/10)) {
				jumpSection(-1);
		    	Log.v(TAG, "handleTap() returning early 1" );
				return true;
			}
			if (x>(width*9)/10) {
				jumpSection(1);
		    	Log.v(TAG, "handleTap() returning early 2" );
				return true;
			}
			if (y<((height*45)/100)) {
				pageUp(1);
		    	Log.v(TAG, "handleTap() returning early 3" );
				return true;
			}
			if (y>((height*55)/100)) {
				pageDown(1);
		    	Log.v(TAG, "handleTap() returning early 4" );
				return true;
			}
		} else {
			Log.v(TAG, "Skipping tap, x="+x+", y="+y+
					", left="+left+", right="+right+
					", top="+top+", bottom="+bottom);
		}
		
    	Log.v(TAG, "handleTap() returning" );
		return false;
	}

	private void calculateScrollHeight() {
    	Log.v(TAG, "calculateScrollHeight() beginning" );
		Log.d(TAG, "height=" + daavenScroll.getHeight() + 
				   ", padBottom=" + daavenScroll.getPaddingBottom() + 
				   ", padTop=" + daavenScroll.getPaddingTop() +
				   ", fadingEdge=" + daavenScroll.getVerticalFadingEdgeLength() );

		scrollHeight = daavenScroll.getHeight() - 
		   daavenScroll.getPaddingBottom() - 
		   daavenScroll.getPaddingTop() - 
		   daavenScroll.getVerticalFadingEdgeLength() * 2;

		Log.v(TAG, "scrollHeight=" + scrollHeight );
    	Log.v(TAG, "calculateScrollHeight() ending" );
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
    	Log.v(TAG, "dispatchTouchEvent() beginning" );
//		String toast = "dispatchTouchEvent: event={" + getMotionEventString(ev) + "}";
//		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
		
		if (!tapToScroll) return super.dispatchTouchEvent(ev);

		if (ev.getAction()==MotionEvent.ACTION_DOWN ) {
			currentDownTime=ev.getEventTime();
			currentDownX=ev.getX();
			currentDownY=ev.getY();
		}

		if (ev.getAction()==MotionEvent.ACTION_UP && ev.getDownTime()==currentDownTime) {
			float dx=ev.getX() - currentDownX;
			float dy=ev.getY() - currentDownY;
			float distance2= dx*dx + dy*dy;
			if ( distance2 < tapThreshold2 ) {
				return handleTap(ev.getX(), ev.getY());
			}
		}
	
    	Log.v(TAG, "dispatchTouchEvent() about to return" );
		return super.dispatchTouchEvent(ev);
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
	private String[] sectionNames = new String[0];
	private ArrayList<Integer> sectionOffsets = new ArrayList<Integer>();
	private Typeface hebrewTypeface=null;
	private boolean tapToScroll=false;

	private long currentDownTime=0;
	private float currentDownX=0;
	private float currentDownY=0;
	private float tapThreshold2=25*25;
}
