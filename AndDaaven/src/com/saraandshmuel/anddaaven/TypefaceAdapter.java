package com.saraandshmuel.anddaaven;

import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Wraps another ListAdapter, and explicitly sets the Typeface.
 * 
 * All calls are delegated to the wrapped ListAdapter class.  Any returned Views 
 * are checked to see if they are a TextView.  If they are, then the 
 * setTypeface() method is called to set the text to the specified typeface.
 * 
 * @author shmuelp
 *
 */
public class TypefaceAdapter implements ListAdapter {
	private ListAdapter wrappedAdapter_ = null;
	private Typeface typeface_ = null;
	private float typeSize_ = 17;
	
	public TypefaceAdapter( ListAdapter wrapped, Typeface typeface, float typeSize ) {
		wrappedAdapter_ = wrapped;
		typeface_ = typeface;
		typeSize_ = typeSize;
	}
	
	public int getCount() {
		return wrappedAdapter_.getCount();
	}
	
	public Object getItem(int position) {
		Object result = wrappedAdapter_.getItem(position);
		if ( result instanceof TextView ) {
			TextView tv = (TextView) result;
			tv.setTypeface(typeface_);
			tv.setTextSize(typeSize_);
		}
		return result;
	}
	
	public long getItemId(int position) {
		return wrappedAdapter_.getItemId(position);
	}
	
	public int getItemViewType(int position) {
		return wrappedAdapter_.getItemViewType(position);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View result = wrappedAdapter_.getView(position, convertView, parent);
		if ( result instanceof TextView ) {
			TextView tv = (TextView) result;
			tv.setTypeface(typeface_);
			tv.setTextSize(typeSize_);
		}
		return result;
	}

	public int getViewTypeCount() {
		return wrappedAdapter_.getViewTypeCount();
	}

	public boolean hasStableIds() {
		return wrappedAdapter_.hasStableIds();
	}

	public boolean isEmpty() {
		return wrappedAdapter_.isEmpty();
	}

	public void registerDataSetObserver(DataSetObserver observer) {
		wrappedAdapter_.registerDataSetObserver(observer);
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		wrappedAdapter_.unregisterDataSetObserver(observer);
	}

	public boolean areAllItemsEnabled() {
		return wrappedAdapter_.areAllItemsEnabled();
	}

	public boolean isEnabled(int position) {
		return wrappedAdapter_.isEnabled(position);
	}
}
