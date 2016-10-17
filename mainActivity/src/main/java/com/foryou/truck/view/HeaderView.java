package com.foryou.truck.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class HeaderView extends LinearLayout {
	private View view = null;

	public HeaderView(Context context) {
		super(context);
	}

	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public View getView() {
		return this.view;
	}

	@Override
	public void addView(View child) {
		LayoutParams layoutParams = new LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		super.addView(child, layoutParams);

		view = child;
	}

}

