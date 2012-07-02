package com.saraandshmuel.anddaaven;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Adds pinch-to-zoom behavior to AndDaavenTefilla
 * @author shmuelp
 *
 */
@TargetApi(8)
public class AndDaavenTefillaFroyo extends AndDaavenTefilla implements OnScaleGestureListener, OnTouchListener
{
 
    public float thresh=(float)0.0001;
    private boolean doZoom=true;

	private final String TAG = "AndDaavenTefillaFroyo";

	public AndDaavenTefillaFroyo() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sgd = new ScaleGestureDetector(this, this);
		originalSpan = 0;
	}
	
	public boolean onScale(ScaleGestureDetector detector) {
		if (!doZoom) return true;
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
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		doZoom = prefs.getBoolean("PinchZoom", true);
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
			try {
				sgd.onTouchEvent(ev);
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.w(TAG, "Received ArrayIndexOutOfBoundsException while delegating touch event to ScaleGestureDetector: " + e.toString());
			}
		}
		return super.onTouch(view, ev);
	}

	private ScaleGestureDetector sgd;
	private float originalSpan;
}
