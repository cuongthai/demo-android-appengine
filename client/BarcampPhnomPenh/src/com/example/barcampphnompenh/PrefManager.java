package com.example.barcampphnompenh;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PrefManager {


	protected static final String username = "com.example.barcampphnompenh.username";

	public static void write(final Context ctx, final String key,
			final String value) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);

		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();

	}

	public static String read(final Context ctx, final String key,
			String defaultVal) {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return sp.getString(key, defaultVal);

	}


}
