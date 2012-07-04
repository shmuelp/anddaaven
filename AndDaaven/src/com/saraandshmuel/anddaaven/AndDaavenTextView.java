package com.saraandshmuel.anddaaven;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;
import android.content.Context;
import android.graphics.Canvas;

public class AndDaavenTextView extends TextView
{
	protected Queue<Runnable> afterDrawRunnables_;

	public AndDaavenTextView(Context context)
	{
		super(context);
		afterDrawRunnables_=new LinkedList<Runnable>();
	}
	
	public AndDaavenTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		afterDrawRunnables_=new LinkedList<Runnable>();
	}

	public AndDaavenTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		afterDrawRunnables_=new LinkedList<Runnable>();
	}

	public void postAfterDraw(Runnable r) {
		afterDrawRunnables_.add(r);
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		while (!afterDrawRunnables_.isEmpty()) {
			afterDrawRunnables_.remove().run();
		}
	}
}
