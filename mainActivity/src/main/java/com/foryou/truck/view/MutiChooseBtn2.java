package com.foryou.truck.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.R;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.view.MutiTabChoose.TabClickListener;

/**
 * @des:需要不需要按钮
 */
public class MutiChooseBtn2 extends RelativeLayout implements View.OnClickListener {
	public Context mContext;
	private TextView mText, mText1;
	private boolean firstChoose = false, secondChoose = false;
	private TabClickListener mlistener;

	public MutiChooseBtn2(Context context) {
		super(context);
		mContext = context;
	}

	public MutiChooseBtn2(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public void setOnTabClickListener(TabClickListener listener) {
		mlistener = listener;
	}

	public void init(String[] str) {
		mText = (TextView) findViewById(R.id.btn1_text);
		mText.setText(str[0]);
		mText.setOnClickListener(this);
		//mText.setTextColor(Color.parseColor("#ffffff"));
		mText1 = (TextView) findViewById(R.id.btn2_text);
		mText1.setText(str[1]);
		mText1.setOnClickListener(this);
		//mText1.setTextColor(Color.parseColor("#21cc71"));

	}

	public void setChooseStatus(boolean left, boolean right) {

		if (left) {
			// mImage.setVisibility(android.view.View.VISIBLE);
			// mText.setTextColor(this.getResources().getColor(R.color.my_blue_color));
			mText.setBackgroundResource(R.drawable.btn_6_highlight);
			firstChoose = true;
			mText.setTextColor(Color.parseColor("#ffffff"));

		} else {
			// mImage.setVisibility(android.view.View.GONE);
			mText.setBackgroundResource(R.drawable.btn_6);
			firstChoose = false;
			mText.setTextColor(Color.parseColor("#00a9e0"));
		}

		if (right) {
			// mImage1.setVisibility(android.view.View.VISIBLE);
			mText1.setTextColor(Color.parseColor("#ffffff"));
			mText1.setBackgroundResource(R.drawable.btn_7_highlight);
			secondChoose = true;

		} else {
			// mImage1.setVisibility(android.view.View.GONE);
			mText1.setTextColor(Color.parseColor("#00a9e0"));
			secondChoose = false;
			mText1.setBackgroundResource(R.drawable.btn_7);
		}
		mText.setPadding(ScreenInfo.dip2px(mContext, 15), ScreenInfo.dip2px(mContext, 4), ScreenInfo.dip2px(mContext, 15),
				ScreenInfo.dip2px(mContext, 4));
		mText1.setPadding(ScreenInfo.dip2px(mContext, 15), ScreenInfo.dip2px(mContext, 4), ScreenInfo.dip2px(mContext, 15),
				ScreenInfo.dip2px(mContext, 4));
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
		case R.id.btn1_text:
			if (firstChoose) {
				return;
			}
			firstChoose = true;
			secondChoose = false;
			setChooseStatus(true, false);
			if (mlistener != null)
				mlistener.tabClicked(0);
			break;
		case R.id.btn2_text:
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
