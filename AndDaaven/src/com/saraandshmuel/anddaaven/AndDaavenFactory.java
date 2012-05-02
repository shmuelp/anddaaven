package com.saraandshmuel.anddaaven;

import android.content.Context;
import android.content.Intent;


public class AndDaavenFactory {
	
	public static Intent getTefillaIntent(Context context) {
		Intent result;
		
		// Ice Cream Sandwich
		if (AndDaavenModel.getAndroidSdkVersion() >= 14 ) { 
//			Toast.makeText(context, "returning ICS intent", Toast.LENGTH_SHORT).show();
			result = new Intent(context, com.saraandshmuel.anddaaven.AndDaavenTefillaIcs.class);
		// Honeycomb
		} else if (AndDaavenModel.getAndroidSdkVersion() >= 11 ) { 
//			Toast.makeText(context, "returning Honeycombd intent", Toast.LENGTH_SHORT).show();
			result = new Intent(context, com.saraandshmuel.anddaaven.AndDaavenTefillaHoneycomb.class);
		// Froyo
		} else if (AndDaavenModel.getAndroidSdkVersion() >= 8 ) { 
//			Toast.makeText(context, "returning Froyo intent", Toast.LENGTH_SHORT).show();
			result = new Intent(context, com.saraandshmuel.anddaaven.AndDaavenTefillaFroyo.class);
		// Cupcake
		} else {
//			Toast.makeText(context, "Returning normal intent", Toast.LENGTH_SHORT).show();
			result = new Intent(context, com.saraandshmuel.anddaaven.AndDaavenTefilla.class);
		}
		
		return result;
	}
}
