package com.foryou.truck.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DialogBuilder {
	public static AlertDialog.Builder NewAlertDialog(Context context) {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			return new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
		} else {
			return new AlertDialog.Builder(context);
		}
	}
}
