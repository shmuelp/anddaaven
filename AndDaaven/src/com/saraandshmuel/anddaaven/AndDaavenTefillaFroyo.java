package com.saraandshmuel.anddaaven;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Adds pinch-to-zoom behavior to AndDaavenTefilla
 * @author shmuelp
 *
 */
public class AndDaavenTefillaFroyo extends AndDaavenTefilla implements OnScaleGestureListener, OnTouchListener
{
 
    public float thresh=(float)0.1;

	public AndDaavenTefillaFroyo() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sgd = new ScaleGestureDetector(this, this);
		originalSpan = 0;
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("test.zoom", false)) {
			daavenText.setOnTouchListener(this);
		}
	}
	
	public boolean onScale(ScaleGestureDetector detector) {
		float total = detector.getCurrentSpan() / originalSpan;
		if ( total > originalSpan + thresh ||
			 total < originalSpan - thresh ) {
			view.adjustFontSize(total);
			originalSpan = detector.getCurrentSpan();
		}
		
		return true;
	}

	public boolean onScaleBegin(ScaleGestureDetector detector) {
		originalSpan = detector.getCurrentSpan();
		return true;
	}
	
	public void onScaleEnd(ScaleGestureDetector detector) {
		// TODO: Consider scaling the text here
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		sgd.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouch(View view, MotionEvent ev) {
		if (view==daavenText || view==daavenScroll) {
			sgd.onTouchEvent(ev);
		}
		return super.onTouch(view, ev);
	}

	private ScaleGestureDetector sgd;
	private float originalSpan;
}
