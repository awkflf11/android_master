package com.foryou.truck.util;


public class UtilsLog {

	public static boolean isTest = true;

	public static void d(String key, String value) {
		if (isTest) {
			android.util.Log.d(key, value);
		}
	}

	public static void i(String key, String value) {
		if (isTest) {
			android.util.Log.i(key, value);
		}
	}

	public static void e(String key, String value) {
		if (isTest) {
			android.util.Log.e(key, value);
		}
	}
}
