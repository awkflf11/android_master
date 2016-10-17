package com.foryou.truck.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewWithIconFont extends TextView {

	public TextViewWithIconFont(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setTypeface(Typeface tf, int style) {
		// TODO Auto-generated method stub
		super.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
				"iconfont/iconfont.ttf"));
	}
}
