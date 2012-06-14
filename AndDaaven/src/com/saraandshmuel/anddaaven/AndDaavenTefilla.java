package com.saraandshmuel.anddaaven;

import java.util.ArrayList;

import org.acra.ErrorReporter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Spannable;
import android.text.SpannableString;

public class AndDaavenTefilla extends Activity implements
		OnSharedPreferenceChangeListener, TextWatcher,
		android.content.DialogInterface.OnClickListener,
		View.OnTouchListener,
GestureDetector.OnGestureListener
{
	private static final String TAG = "AndDaavenTefilla";

	protected AndDaavenTefillaFactory factory;

	public AndDaavenTefilla() {
		Log.v(TAG, "AndDaavenTefilla()");
		System.setProperty("log.tag." + TAG, "VERBOSE");
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate() beginning");

		//AndDaavenBaseFactory.initMvc(this, model, view, controller);
		factory=new AndDaavenTefillaFactory(this);
		factory.createMvc();
		model=factory.getModel();
		view=factory.getView();
		controller=factory.getController();
		
		gestureDetector = new GestureDetector(this, this);

		// layout view from resource XML file
		view.setNightModeTheme();
		super.onCreate(savedInstanceState);
		setWindowFlags();

		setContentView(R.layout.daaven);
		findLayoutObjects();
		view.findLayoutObjects();

		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(this);

		setHebrewFont();
		setFontSize();
		setAlignment();
		tapToScroll = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("TapToScroll", tapToScroll);
		
		daavenScroll.setOnTouchListener(this);

		// find and setup text to display
		controller.showTefilla(getIntent());

	// SDP TODO: if is brachot and autoindex brachot then show index here

		Log.v(TAG, "onCreate() ending");
	}

	@Override 
	protected void onStart() {
		Log.v(TAG, "onStart() beginning");

		super.onStart();
		
		Log.v(TAG, "onStart() ending");
	}
	
	@Override 
	protected void onResume() {
		Log.v(TAG, "onResume() beginning");

		super.onResume();

		if (model.getCurrentOffset()==0 && getIntent().hasExtra("ScrollPosition")) {
			final int pos=getIntent().getIntExtra("ScrollPosition", 0);
			Log.v(TAG, "will restore pos=" + pos);
			daavenScroll.post(new Runnable() {
					public void run() {
						if (daavenScroll.getScrollY() == 0) {
							daavenScroll.scrollTo(0, pos);
							Log.v(TAG, "restored pos=" + pos);
						} else {
							Log.v(TAG, "skipping restore from intent because scrollview is already scrolled");
						}
					}
				});
		}

		Log.v(TAG, "onResume() ending");
	}
	
	/**
	 * Sets the alignment - called from onCreate and on OldAlignment pref change
	 */
	private void setAlignment() {
		Log.v(TAG, "setAlignment() beginning");
		// Make sure that Hebrew text is right-aligned on Froyo
		// Some third-party ROMs need different logic for Froyo
		boolean oldAlignment = PreferenceManager.getDefaultSharedPreferences(
				this).getBoolean("OldAlignment", false);
		final int version = Integer.parseInt(VERSION.SDK);
		int g = Gravity.LEFT;
		if (version >= 8) {
			g = Gravity.RIGHT;
		}
		if (oldAlignment) {
			if (g == Gravity.RIGHT)
				g = Gravity.LEFT;
			else if (g == Gravity.LEFT)
				g = Gravity.RIGHT;
		}
		daavenText.setGravity(g);
		Log.v(TAG, "setAlignment() ending");
	}

	/**
	 * Sets the content view - called from onCreate and if FullScreen pref is
	 * changed
	 */
	private void setWindowFlags() {
		Log.v(TAG, "setWindowFlags() beginning");
		boolean fullScreen = PreferenceManager
				.getDefaultSharedPreferences(this).getBoolean("FullScreen",
						false);
		boolean screenOn = PreferenceManager
			.getDefaultSharedPreferences(this).getBoolean("ScreenOn",
														  false);
		if (fullScreen) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		if (screenOn) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		Log.v(TAG, "setWindowFlags() ending");
	}

	/**
	 * Sets the Hebrew font on the tefilla text
	 */
	private void setHebrewFont() {
		Log.v(TAG, "setHebrewFont() beginning");
		if (hebrewTypeface==null)
			hebrewTypeface=view.getSelectedHebrewTypeface();

		// daavenText.setTypeface(hebrewTypeface);
		runOnUiThread(new Runnable() {
			public void run() {
				daavenText.setTypeface(hebrewTypeface);
			}
		});
		Log.v(TAG, "setHebrewFont() ending");
	}

	/**
	 * Sets the font size of the tefilla text
	 */
	private void setFontSize() {
		Log.v(TAG, "setFontSize() beginning");
		float size = view.getFontSize();
		daavenText.setTextSize(size);
		Log.v(TAG, "setFontSize() ending");
	}

	/**
	 * Saves local pointers to interesting objects so they may be manipulated
	 */
	private void findLayoutObjects() {
		Log.v(TAG, "findLayoutObjects() beginning");
		daavenText = (AndDaavenTextView) findViewById(R.id.DaavenText);
		daavenText.addTextChangedListener(this);
		daavenScroll = (ScrollView) findViewById(R.id.DaavenScroll);
		Log.v(TAG, "findLayoutObjects() ending");
	}

	// update text if needed (called when switching back to this Activity)
	@Override
	protected void onNewIntent(Intent intent) {
		Log.v(TAG, "onNewIntent() beginning");
		controller.showTefilla(intent);
		super.onNewIntent(intent);
		Log.v(TAG, "onNewIntent() ending");
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.v(TAG, "dispatchKeyEvent() beginning");
		boolean result = false;
		boolean pageDown = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("PageDown", true);
		boolean sectionJump = PreferenceManager.getDefaultSharedPreferences(
				this).getBoolean("SectionJump", true);
		String volumeButtonCode = PreferenceManager
				.getDefaultSharedPreferences(this).getString("VolumeButton",
						"IGNORE");

		int keyAction = event.getAction();
		int keyCode = event.getKeyCode();
		Log.d(TAG, "dispatchKeyEvent(), action=" + keyAction + ",code="
				+ keyCode);

		// Flag to determine if AndDaaven handles the event or if it delegates
		boolean handleEvent = false;

		if (pageDown
				&& (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
			handleEvent = true;
		}

		if (sectionJump
				&& (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
			handleEvent = true;
		}

		if (!volumeButtonCode.equals("IGNORE")
				&& (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
			handleEvent = true;
			if (volumeButtonCode.equals("PAGE")) {
				if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
					keyCode = KeyEvent.KEYCODE_DPAD_UP;
				} else {
					keyCode = KeyEvent.KEYCODE_DPAD_DOWN;
				}
			} else if (volumeButtonCode.equals("SECTION")) {
				if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
					keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
				} else {
					keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
				}
			}
		}

		if (!handleEvent) {
			Log.v(TAG, "findLayoutObjects() about to return early");
			return super.dispatchKeyEvent(event);
		}

		int count = 1;
		if (keyAction == KeyEvent.ACTION_MULTIPLE
				&& keyCode != KeyEvent.KEYCODE_UNKNOWN) {
			keyAction = KeyEvent.ACTION_DOWN;
			Log.v(TAG, "Got ACTION_MULTIPLE, repeat=" + event.getRepeatCount());
			count = event.getRepeatCount() / 2 + 1;
		}

		// Calculate screen height for page scrolling if not already cached
		if (scrollHeight == 0) {
			calculateScrollHeight();
		}

		if (keyAction == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_UP:
				pageUp(count);
				result = true;
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				pageDown(count);
				result = true;
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				count = -count;
				// intentional pass-through
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				jumpSection(count);
				result = true;
				break;
			default:
				result = super.dispatchKeyEvent(event);
			}
		} else if (keyAction == KeyEvent.ACTION_DOWN) {
			// Do nothing - consume event and handle on key up
			result = true;
		} else {
			result = super.dispatchKeyEvent(event);
		}

		Log.v(TAG, "findLayoutObjects() about to return");
		return result;
	}

	/**
	 * Jumps back of forward a number of sections
	 * 
	 * @param count
	 *            the number of sections to jump forward. Will jump backward
	 *            with a negative count
	 */
	private void jumpSection(int count) {
		Log.v(TAG, "jumpSection(" + count + ") beginning");
		ArrayList<Integer> jumpOffsets = model.getJumpOffsets();
		Layout layout = daavenText.getLayout();
		if (layout == null) {
			Log.w(TAG, "jumpSection(): cannot jump if layout is null!");
			return;
		}

		// Check that jumpOffsets has been initialized
		if (jumpOffsets.isEmpty()) {
			Log.w(TAG, "jumpSection(): cannot jump if jumpOffsets is empty!");
			return;
		}

		// Get current offset
		int offset = locateCurrentOffset(layout);

		// Find current section in jumpOffsets
		int section = 0;
		for (section = 0; section + 1 < jumpOffsets.size(); ++section) {
			if (offset < jumpOffsets.get(section + 1)) {
				break;
			}
		}
		Log.d(TAG, "Found offset=" + offset + ", section=" + section);

		// Calculate new section
		// When moving backwards, first scroll to the beginning of the
		// current section
		if (count < 0
				&& layout.getLineForOffset(offset) > layout
						.getLineForOffset(jumpOffsets.get(section))) {
			++count;
		}
		section += count;
		if (section < 0) {
			// Tried to scroll past initial section
			// Reset position to beginning of initial section
			section = 0;
		} else if (section >= jumpOffsets.size()) {
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
		Log.d(TAG, "Scrolling to offset=" + newOffset + ", section=" + section
				+ ", line=" + newLine + ", y=" + newY);
		daavenScroll.scrollTo(0, newY);

		Log.v(TAG, "jumpSection() ending");
	}

	/**
	 * Saves the current scrolled position
	 */
	private void savePosition() {
		Log.v(TAG, "savePosition() beginning");
		Layout layout = daavenText.getLayout();
		if (layout != null) {
			int newOffset = locateCurrentOffset(layout);
			if (layout.getLineForOffset(newOffset) != layout
					.getLineForOffset(model.getCurrentOffset())) {
				model.setCurrentOffset(newOffset);
				Log.v(TAG, "Saved current offset=" + model.getCurrentOffset());
			}
		} else {
			Log.w(TAG, "savePosition(): cannot save if layout is null!");
		}
		Log.v(TAG, "savePosition() ending");
	}

	/**
	 * Locates the current offset by examining the scroll state of the text
	 * 
	 * @param layout
	 *            - the layout object of the current tefilla
	 * @return The current offset of the tefilla (in characters of text)
	 */
	private int locateCurrentOffset(Layout layout) {
		Log.v(TAG, "locateCurrentOffset() beginning");
		int result;
		// Find highest pixel of interest (ignore e.g. faded pixels)
		int topPixel = daavenScroll.getScrollY() + daavenScroll.getPaddingTop()
				+ daavenScroll.getVerticalFadingEdgeLength();
		int line = layout.getLineForVertical(topPixel);
		// result = (layout.getLineStart(line) + layout.getLineEnd(line))/2;
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
				
				
				for (int offset:model.getJumpOffsets()) {
					int line=layout.getLineForOffset(offset);
					int start=layout.getLineStart(line);
					Log.v(TAG, " jump offset " + offset + " has line start " + start + " on line " + line);
				}
				for (int offset:model.getSectionOffsets()) {
					int line=layout.getLineForOffset(offset);
					int start=layout.getLineStart(line);
					Log.v(TAG, " section offset " + offset + " has line start " + start + " on line " + line);
				}
				
				
				
				
				if (layout != null) {
					int line = layout.getLineForOffset(model.getCurrentOffset());
					int y = layout.getLineTop(line);
					daavenScroll.scrollTo(0, y);
					Log.d(TAG, "Restored current offset=" + model.getCurrentOffset()
							+ ", line=" + line);
				} else {
					Log
							.e(TAG,
									"Unable to restore scrolled position because layout was null");
				}
			}

		});
		Log.v(TAG, "restorePosition() ending");
	}

	public void pageDown(int count) {
		Log.v(TAG, "pageDown(" + count + ") beginning");
		for (int i = 0; i < count; ++i) {
			daavenScroll.scrollBy(0, scrollHeight);
			Log.d(TAG, "scrollBy(0," + scrollHeight + ")");
		}
		Log.v(TAG, "pageDown() ending");
	}

	public void pageUp(int count) {
		Log.v(TAG, "pageUp(" + count + ") beginning");
		for (int i = 0; i < count; ++i) {
			daavenScroll.scrollBy(0, -scrollHeight);
			Log.v(TAG, "scrollBy(0," + (-scrollHeight) + ")");
		}
		Log.v(TAG, "pageUp() ending");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.v(TAG, "onSaveInstanceState() beginning");
		super.onSaveInstanceState(outState);
		savePosition();
		outState.putInt("TefillaPosition", model.getCurrentOffset());
		Log.v(TAG, "onSaveInstanceState() ending");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.v(TAG, "onRestoreInstanceState() beginning");
		super.onRestoreInstanceState(savedInstanceState);
		final Bundle myState = savedInstanceState;
		daavenText.post(new Runnable() {
			public void run() {
				model.setCurrentOffset(myState.getInt("TefillaPosition"));
				if (model.getCurrentOffset() == 0) {
					Log.w(TAG, "Warning: asked to restore position of 0");
				}
				restorePosition();
			}
		});
		Log.v(TAG, "onRestoreInstanceState() ending");
	}

	/**
	 * Initial menu item creator- still being implemented
	 * 
	 * TODO: Share this code with AndDaavenSplash
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptionsMenu() beginning");
		boolean settingInTefilla = PreferenceManager
				.getDefaultSharedPreferences(this).getBoolean(
						"SettingInTefilla", true);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		
		MenuItem index = menu.findItem(R.id.Index);
		index.setVisible(true);
		
		if (!settingInTefilla) {
			index = menu.findItem(R.id.Settings);
			index.setVisible(false);
			index = menu.findItem(R.id.About);
			index.setVisible(false);
		}
		
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        if (prefs.getBoolean("test.extraActions", false)) {
//        	menu.findItem(R.id.ChangelogButton).setVisible(true);
//        }

        Log.v(TAG, "onCreateOptionsMenu() about to return");
		return true;
	}

	/**
	 * Initial menu item handler - still being implemented
	 * 
	 * TODO: Share this code with AndDaavenSplash
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
		case R.id.Index:
			ContextThemeWrapper ctx = new ContextThemeWrapper(this,
				view.getNightModeStyle(model));

			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			if (hebrewTypeface == null)
				setHebrewFont();
			ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
					R.layout.index_list_item, model.getSectionNames());
			TypefaceAdapter ta = new TypefaceAdapter(aa, hebrewTypeface,
					view.getFontSize());
			// builder.setItems(sectionNames, this);
			builder.setAdapter(ta, this);
			AlertDialog indexDialog = builder.create();

			Log.v(TAG, "onCreateDialog() returning early");
			return indexDialog;
			case R.id.About:
				AndDaavenAboutDialogController aboutController = new AndDaavenAboutDialogController(this);
				return aboutController.create();
			case R.id.ChangelogButton:
				AndDaavenChangelogController changelogController = new AndDaavenChangelogController(this);
				return changelogController.create();
		}
		Log.v(TAG, "onCreateDialog() about to return");
		return super.onCreateDialog(id);
	}

	public void onClick(DialogInterface dialog, int which) {
		Log.v(TAG, "onClick(dialog," + which + ") beginning");
		Log.d(TAG, "Jump to index section " + which);
		Layout layout = daavenText.getLayout();
		if (layout != null) {
			int newOffset = model.getSectionOffsets().get(which);
			int newLine = layout.getLineForOffset(newOffset);
			int newY = layout.getLineTop(newLine);
			daavenScroll.scrollTo(0, newY);
			Log.d(TAG, "Scrolling to offset=" + newOffset + ", section="
					+ which + ", line=" + newLine + ", y=" + newY);
		} else {
			Toast.makeText(this, "Cannot jump; layout was null",
					Toast.LENGTH_SHORT).show();
		}
		Log.v(TAG, "onClick() about to return");
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.v(TAG, "onSharedPreferenceChanged(sharedPreferences, " + key
				+ ") beginning");
		if (key.equals("TextFont")) {
			hebrewTypeface = null;
			setHebrewFont();
			scrollHeight = 0; // recompute scroll height when needed
		} else if (key.equals("FontSize")) {
			savePosition();
			setFontSize();
			daavenText.requestLayout();
			scrollHeight = 0;
			daavenText.postAfterDraw(new Runnable() {
				public void run() {
					restorePosition();
				}
			});
		} else if (key.equals("FullScreen") || 
		           key.equals("ScreenOn") ) {
			setWindowFlags();
			daavenText.requestLayout();
			scrollHeight = 0;
		} else if (key.equals("OldAlignment")) {
			setAlignment();
			daavenText.requestLayout();
			scrollHeight = 0;
		} else if (key.equals("TapToScroll")) {
			tapToScroll = sharedPreferences.getBoolean(key, tapToScroll);
		}
		Log.v(TAG, "onSharedPreferenceChanged() ending");
	}

	public void afterTextChanged(android.text.Editable s) {
		Log.v(TAG, "afterTextChanged() beginning, s.length()=" + s.length());
		Layout l=daavenText.getLayout();
		if (l==null) {
			Log.v(TAG, "afterTextChanged(), layout is null");
		} else {
			Log.v(TAG, "layout.getLineCount()=" + l.getLineCount() + 
					", layout.getHeight()=" + l.getHeight() + 
					", layout class name=" + l.getClass().getName());
		}
		ErrorReporter er = ErrorReporter.getInstance();
		er.addCustomData("after:s.length", "" + s.length());
		if (s.length() != model.getSpanText().length()) {
			er.handleException(null);
		}
		Log.v(TAG, "afterTextChanged() ending");
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
//		Log.v(TAG, "beforeTextChanged(), count=" + count + ", s.length()=" + s.length());
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
//		Log.v(TAG, "onTextChanged(), count=" + count + ", s.length()=" + s.length());
	}

	// private String getMotionEventString(MotionEvent event) {
	// StringBuilder result = new StringBuilder("action=");
	// int action = event.getAction();
	// if ( action==MotionEvent.ACTION_CANCEL ) result.append("ACTION_CANCEL");
	// else if ( action==MotionEvent.ACTION_DOWN ) result.append("ACTION_DOWN");
	// else if ( action==MotionEvent.ACTION_MASK) result.append("ACTION_MASK");
	// else if ( action==MotionEvent.ACTION_UP) result.append("ACTION_UP");
	// else result.append("UNKNOWN");
	//		
	// result.append(", x=").append(event.getX());
	// result.append(", y=").append(event.getY());
	//		
	// result.append(", rawX=").append(event.getRawX());
	// result.append(", rawY=").append(event.getRawY());
	//		
	// result.append(", downTime=").append(event.getDownTime());
	// result.append(", eventTime=").append(event.getEventTime());
	//		
	// long delta=event.getEventTime()-event.getDownTime();
	// result.append(", delta=").append(delta);
	//		
	// boolean tap=false;
	// if (action==MotionEvent.ACTION_UP && delta > 0 && delta <= 500 )
	// tap=true;
	//		
	// result.append(", tap=").append(tap);
	//		
	// return result.toString();
	// }

	private boolean handleTap(float x, float y) {
		Log.v(TAG, "handleTap(" + x + ", " + y + ") beginning");
		int right = daavenScroll.getRight();
		int left = daavenScroll.getLeft();
		int width = right - left;
		int top = daavenScroll.getTop();
		int bottom = daavenScroll.getBottom();
		int height = bottom - top;

		// Calculate screen height for page scrolling if not already cached
		if (scrollHeight == 0) {
			calculateScrollHeight();
		}
		
		if (x >= left && x < right && y >= top && y < bottom) {
			if (x < (width / 10)) {
				jumpSection(-1);
				Log.v(TAG, "handleTap() returning early 1");
				return true;
			}
			if (x > (width * 9) / 10) {
				jumpSection(1);
				Log.v(TAG, "handleTap() returning early 2");
				return true;
			}
			if (y < ((height * 45) / 100)) {
				pageUp(1);
				Log.v(TAG, "handleTap() returning early 3");
				return true;
			}
			if (y > ((height * 55) / 100)) {
				pageDown(1);
				Log.v(TAG, "handleTap() returning early 4");
				return true;
			}
		} else {
			Log.v(TAG, "Skipping tap, x=" + x + ", y=" + y + ", left=" + left
					+ ", right=" + right + ", top=" + top + ", bottom="
					+ bottom);
		}

		Log.v(TAG, "handleTap() returning");
		return false;
	}

	private void calculateScrollHeight() {
		Log.v(TAG, "calculateScrollHeight() beginning");
		Log.d(TAG, "height=" + daavenScroll.getHeight() + ", padBottom="
				+ daavenScroll.getPaddingBottom() + ", padTop="
				+ daavenScroll.getPaddingTop() + ", fadingEdge="
				+ daavenScroll.getVerticalFadingEdgeLength());

		scrollHeight = daavenScroll.getHeight()
				- daavenScroll.getPaddingBottom()
				- daavenScroll.getPaddingTop()
				- daavenScroll.getVerticalFadingEdgeLength() * 2;

		Log.v(TAG, "scrollHeight=" + scrollHeight);
		Log.v(TAG, "calculateScrollHeight() ending");
	}

	public boolean onTouch(View view, MotionEvent ev) {
		Log.v(TAG, "onTouch() beginning");
		// String toast = "dispatchTouchEvent: event={" +
		// getMotionEventString(ev) + "}";
		// Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();

		if (!tapToScroll)
			return false; //super.onTouchEvent(ev);
			
		gestureDetector.onTouchEvent(ev);

//		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//			currentDownTime = ev.getEventTime();
//			currentDownX = ev.getX();
//			currentDownY = ev.getY();
//		}
//
//		if (ev.getAction() == MotionEvent.ACTION_UP
//				&& ev.getDownTime() == currentDownTime) {
//			float dx = ev.getX() - currentDownX;
//			float dy = ev.getY() - currentDownY;
//			float distance2 = dx * dx + dy * dy;
//			if (distance2 < tapThreshold2) {
//					return handleTap(ev.getX(), ev.getY());
//			}
//		}
//
		Log.v(TAG, "onTouch() about to return");
		return false; //super.dispatchTouchEvent(ev);
	}

	public boolean onSearchRequested() {
		showDialog(R.id.Index);
		return true;
	};
	
	public boolean onDown(MotionEvent p1)
	{
		// TODO: Implement this method
		return false;
	}

	public void onShowPress(MotionEvent p1)
	{
		// TODO: Implement this method
	}

	public boolean onSingleTapUp(MotionEvent event)
	{
		// TODO: Implement this method
		handleTap(event.getX(), event.getY());
		return false;
	}

	public boolean onScroll(MotionEvent p1, MotionEvent p2, float p3, float p4)
	{
		// TODO: Implement this method
		return false;
	}

	public void onLongPress(MotionEvent p1)
	{
		// TODO: Implement this method
	}

	public boolean onFling(MotionEvent p1, MotionEvent p2, float p3, float p4)
	{
		// TODO: Implement this method
		return false;
	}
	
	
	protected AndDaavenTextView daavenText = null;
	protected ScrollView daavenScroll = null;
	private int scrollHeight = 0;
	private Typeface hebrewTypeface = null;
	private boolean tapToScroll = false;

	protected AndDaavenTefillaController controller;
	protected AndDaavenTefillaModel model;
	protected AndDaavenTefillaView view;
//	private TefillaModel tefillaModel;

	private GestureDetector gestureDetector;
}
