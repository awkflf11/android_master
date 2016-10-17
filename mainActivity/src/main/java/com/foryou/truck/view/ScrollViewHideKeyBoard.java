package com.foryou.truck.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

public class ScrollViewHideKeyBoard extends ScrollView {
	private Context mContext;
	private String TAG = "ScrollViewHideKeyBoard";

	public ScrollViewHideKeyBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		mContext = context;
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
			boolean clampedY) {
		// TODO Auto-generated method stub
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindowToken(), 0);
	}
}
