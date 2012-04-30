package com.saraandshmuel.anddaaven;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.text.BoringLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class HebrewMarkTextView extends TextView {
	
	private static String TAG = "HebrewMarkTextView";

	public HebrewMarkTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HebrewMarkTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public HebrewMarkTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
//	@Override
	protected void makeNewLayout(int w, int hintWidth,
								BoringLayout.Metrics boring,
								BoringLayout.Metrics hintBoring,
								int ellipsisWidth, boolean bringIntoView) {
		Log.d(TAG, "makeNewLayout()");
		
		Class signature[] = new Class[] {
			int.class,
			int.class,
			BoringLayout.Metrics.class, 
			BoringLayout.Metrics.class, 
			boolean.class, 
			int.class
		};
		
		Method m=null;
		try {
			m = TextView.class.getMethod("makeNewLayout", signature);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		if ( m != null ) {
			Object args[] = new Object[] { w, hintWidth,
					boring,
					hintBoring,
					ellipsisWidth, bringIntoView};
			try {
				m.invoke(this, args);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
