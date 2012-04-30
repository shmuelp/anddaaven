package com.saraandshmuel.anddaaven;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;

/**
 * Adds pinch-to-zoom behavior to AndDaavenTefilla
 * @author shmuelp
 *
 */
public class AndDaavenTefillaFroyo extends AndDaavenTefilla implements OnScaleGestureListener, OnTouchListener
{

	public AndDaavenTefillaFroyo() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sgd = new ScaleGestureDetector(this, this);
		originalSpan = 0;
		daavenText.setOnTouchListener(this);
	}
	
	public boolean onScale(ScaleGestureDetector detector) {
		float total = detector.getCurrentSpan() / originalSpan;
		Toast.makeText(this, "onScale(" + detector.getScaleFactor() + "), total=" + total, Toast.LENGTH_SHORT).show();
		return true;
	}

	public boolean onScaleBegin(ScaleGestureDetector detector) {
		Toast.makeText(this, "onScaleBegin(" + detector.getScaleFactor() + ")", Toast.LENGTH_SHORT).show();
		originalSpan = detector.getCurrentSpan();
		return true;
	}
	
	public void onScaleEnd(ScaleGestureDetector detector) {
		Toast.makeText(this, "onScaleEnd(" + detector.getScaleFactor() + ")", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Toast.makeText(this, "Froyo onTouchEvent", Toast.LENGTH_SHORT).show();
		sgd.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

@Override
	public boolean onTouch(View view, MotionEvent ev) {
	//Toast.makeText(this, "Froyo onTouch, view=" + view.getId(), Toast.LENGTH_SHORT).show();
		if (view==daavenText || view==daavenScroll) {
			//Toast.makeText(this, "Froyo onTouch from daavenText or daavenScroll", Toast.LENGTH_SHORT).show();
			sgd.onTouchEvent(ev);
		}
		return super.onTouch(view, ev);
	}

	private ScaleGestureDetector sgd;
	private float originalSpan;
}
