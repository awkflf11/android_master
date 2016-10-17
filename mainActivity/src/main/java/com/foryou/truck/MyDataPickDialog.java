package com.foryou.truck;

import android.app.DatePickerDialog;
import android.content.Context;

public class MyDataPickDialog extends DatePickerDialog {
	public MyDataPickDialog(Context context, int theme,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
		setButton("确定", this);
		setButton2("",this);
	}

	public MyDataPickDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}
}
