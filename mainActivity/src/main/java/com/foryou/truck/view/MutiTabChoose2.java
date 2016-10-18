package com.foryou.truck.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.R;
import com.foryou.truck.view.MutiTabChoose.TabClickListener;

public class MutiTabChoose2 extends RelativeLayout implements
		View.OnClickListener {
	private TextView mText, mText1;
	private boolean firstChoose = false, secondChoose = false;
	private TabClickListener mlistener;

	public MutiTabChoose2(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MutiTabChoose2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnTabClickListener(TabClickListener listener) {
		mlistener = listener;
	}

	public void init(String[] str) {

		mText = (TextView) findViewById(R.id.tab1_text);
		mText.setText(str[0]);
		mText.setOnClickListener(this);

		mText1 = (TextView) findViewById(R.id.tab2_text);
		mText1.setText(str[1]);
		mText1.setOnClickListener(this);
	}

	public void setChooseStatus(boolean left, boolean right) {
		if (left) {
			// mImage.setVisibility(android.view.View.VISIBLE);
			mText.setTextColor(this.getResources().getColor(
					R.color.my_blue_color));
			mText.setBackgroundResource(R.drawable.muti_tab_btn_choose);
			firstChoose = true;

		} else {
			// mImage.setVisibility(android.view.View.GONE);
			mText.setTextColor(Color.WHITE);
			mText.setBackgroundResource(R.drawable.muti_tab_btn_unchoose);
			firstChoose = false;
		}

		if (right) {
			// mImage1.setVisibility(android.view.View.VISIBLE);
			mText1.setTextColor(this.getResources().getColor(
					R.color.my_blue_color));
			mText1.setBackgroundResource(R.drawable.muti_tab_btn_choose);
			secondChoose = true;
		} else {
			// mImage1.setVisibility(android.view.View.GONE);
			mText1.setTextColor(Color.WHITE);
			secondChoose = false;
			mText1.setBackgroundResource(R.drawable.muti_tab_btn_unchoose);
		}
	}

	public int getChooseIndex() {
		if (firstChoose)
			return 0;
		if (secondChoose)
			return 1;
		return -1;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tab1_text:
			if (firstChoose) {
				return;
			}
			firstChoose = true;
			secondChoose = false;
			setChooseStatus(true, false);
			if (mlistener != null)
				mlistener.tabClicked(0);
			break;
		case R.id.tab2_text:
			if (secondChoose) {
				return;
			}
			firstChoose = false;
			secondChoose = true;
			setChooseStatus(false, true);
			if (mlistener != null)
				mlistener.tabClicked(1);
			break;
		}
	}
}
