package com.saraandshmuel.anddaaven;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class AndDaavenFactory {
	
	public static Intent getTefillaIntent(Context context) {
		Intent result = new Intent();
		String pkg = context.getPackageName(); // com.saraandshmuel.anddaaven
		
		// Ice Cream Sandwich
		if (AndDaavenModel.getAndroidSdkVersion() >= 14 ) { 
			result.setClassName(pkg, "com.saraandshmuel.anddaaven.AndDaavenTefillaIcs");
		// Honeycomb
		} else if (AndDaavenModel.getAndroidSdkVersion() >= 11 ) {
			result.setClassName(pkg, "com.saraandshmuel.anddaaven.AndDaavenTefillaHoneycomb.class");
		// Froyo
		} else if (AndDaavenModel.getAndroidSdkVersion() >= 8 ) { 
			result.setClassName(pkg, "com.saraandshmuel.anddaaven.AndDaavenTefillaFroyo");
		// Cupcake
		} else {
			result.setClassName(pkg, "com.saraandshmuel.anddaaven.AndDaavenTefilla");
		}
		
		return result;
	}
}
