package com.saraandshmuel.anddaaven;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

public class HebrewMarkStaticLayout extends StaticLayout {

	private static String TAG="HebrewMarkStaticLayout";

	public HebrewMarkStaticLayout(CharSequence source, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
		super(source, paint, width, align, spacingmult, spacingadd, includepad);
	}
	public HebrewMarkStaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
		super(source, bufstart, bufend, paint, outerwidth, align, spacingmult, spacingadd, includepad);
	}
	public HebrewMarkStaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
		super(source, bufstart, bufend, paint, outerwidth, align, spacingmult, spacingadd, includepad, ellipsize, ellipsizedWidth);
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
