package com.saraandshmuel.anddaaven;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.DynamicLayout;
import android.text.TextPaint;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;

public class HebrewMarkDynamicLayout extends DynamicLayout {
	
	private static String TAG="HebrewMarkDynamicLayout";

	public HebrewMarkDynamicLayout(CharSequence base, TextPaint paint,
			int width, Alignment align, float spacingmult, float spacingadd,
			boolean includepad) {
		super(base, paint, width, align, spacingmult, spacingadd, includepad);
		// TODO Auto-generated constructor stub
	}


	 public HebrewMarkDynamicLayout(CharSequence base, CharSequence display, 
			 TextPaint paint, int width, Layout.Alignment align, 
			 float spacingmult, float spacingadd, boolean includepad){
		 super(base, display, paint, width, align, spacingmult, spacingadd, 
				 includepad);
	 }
	 public HebrewMarkDynamicLayout(CharSequence base, CharSequence display, 
			 TextPaint paint, int width, Layout.Alignment align, 
			 float spacingmult, float spacingadd, boolean includepad, 
			 TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
		 super(base, display,paint, width, align, spacingmult, spacingadd, 
				 includepad, ellipsize, ellipsizedWidth);
	 }

	@Override
	public void draw(Canvas c) {
		Log.d(TAG, "draw(Canvas c)");
		// TODO Auto-generated method stub
		super.draw(c);
	}
	
	@Override
	public void draw(Canvas c, Path highlight, Paint highlightPaint,
			int cursorOffsetVertical) {
		Log.d(TAG, "draw(Canvas c, Path highlight, Paint highlightPaint, int cursorOffsetVertical)");
		// TODO Auto-generated method stub
		super.draw(c, highlight, highlightPaint, cursorOffsetVertical);
	}
}
