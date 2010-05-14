package com.saraandshmuel.anddaaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.saraandshmuel.anddaaven.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AndDaavenTefilla extends Activity {
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
    public boolean onTrackballEvent(MotionEvent event) {
    	int x = (int) event.getX();
    	int y = (int) event.getY();
    	
    	while ( x >= 1 ) {
    		scrollRight();
    		--x;
    	}
    	while ( x <= -1) {
    		scrollLeft();
    		++x;
    	}
    	
    	while ( y >= 1 ) {
    		scrollUp();
    	}
    	while ( y <= -1 ) {
    		scrollDown();
    	}
    	
    	return true;
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
    		Log.e("AndSiddur", "Unable to restore scrolled position because layout was null");
    	}
	}
    
    public void scrollRight() {
    	Toast.makeText(this, "Scroll Right", Toast.LENGTH_SHORT).show();
    }

    public void scrollLeft() {
    	Toast.makeText(this, "Scroll Left", Toast.LENGTH_SHORT).show();
    }

    public void scrollUp() {
    	Toast.makeText(this, "Scroll Up", Toast.LENGTH_SHORT).show();
    }

    public void scrollDown() {
    	Toast.makeText(this, "Scroll Down", Toast.LENGTH_SHORT).show();
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
    	    		Log.w("AndSiddur", "Warning: asked to restore position of 0");
    	    	}
    	    	restorePosition();
    		}
    	} );
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
    private TextView daavenText;
    private ScrollView daavenScroll; 
    private String currentFilename;
}
