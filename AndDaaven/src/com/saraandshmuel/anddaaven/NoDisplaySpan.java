/**
 * 
 */
package com.saraandshmuel.anddaaven;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.text.style.ReplacementSpan;

/**
 * Span class that hides the text that it spans
 * 
 * @author S Popper
 *
 */
public class NoDisplaySpan extends ReplacementSpan {

	public NoDisplaySpan() {}

	@Override
	public void draw(Canvas arg0, CharSequence arg1, int arg2, int arg3,
			float arg4, int arg5, int arg6, int arg7, Paint arg8) {}

	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end,
			FontMetricsInt fm) {
		return 0;
	}
}
