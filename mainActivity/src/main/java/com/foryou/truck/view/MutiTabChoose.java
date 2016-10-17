package com.foryou.truck.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foryou.truck.R;

public class MutiTabChoose extends LinearLayout implements View.OnClickListener {

	private LinearLayout mFirstTab, mSecondTab,mThirdTab;
	private TabClickListener mlistener;
	private TextView firstTabLine, secondTabLine, thirdTabLine;
	private TextView firstTabText, secondTabText, thirdTabText;
	private final int COLOR_CHOOSE = 0xff00a9e0;
	private final int COLOR_UNCHOOSE = 0xff333333;

	public interface TabClickListener {
		boolean tabClicked(int index);

	}

	public MutiTabChoose(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setTabText(String[] textArr) {
		if (textArr.length == 2) {
			firstTabText.setText(textArr[0]);
			secondTabText.setText(textArr[1]);
		} else if (textArr.length == 3) {
			firstTabText.setText(textArr[0]);
			secondTabText.setText(textArr[1]);
			mThirdTab.setVisibility(android.view.View.VISIBLE);
			thirdTabText.setText(textArr[2]);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public MutiTabChoose(Context context, AttributeSet attrs) {
		super(context,attrs, 0);
	}

	public void setOnTabClickListener(TabClickListener listener) {
		mlistener = listener;
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mFirstTab = (LinearLayout) findViewById(R.id.first_tab);
		mFirstTab.setOnClickListener(this);
		mSecondTab = (LinearLayout) findViewById(R.id.second_tab);
		mSecondTab.setOnClickListener(this);
		mThirdTab = (LinearLayout) findViewById(R.id.third_tab);
		mThirdTab.setOnClickListener(this);

		firstTabLine = (TextView) findViewById(R.id.first_tab_line);
		firstTabText = (TextView) findViewById(R.id.first_tab_text);

		secondTabLine = (TextView) findViewById(R.id.second_tab_line);
		secondTabText = (TextView) findViewById(R.id.second_tab_text);

		thirdTabLine = (TextView) findViewById(R.id.third_tab_line);
		thirdTabText = (TextView) findViewById(R.id.third_tab_text);

		chooseTab(0);
	}

	public void chooseTab(int index) {
		switch (index) {
		case 0:
			firstTabText.setTextColor(COLOR_CHOOSE);
			firstTabLine.setBackgroundColor(COLOR_CHOOSE);
			secondTabText.setTextColor(COLOR_UNCHOOSE);
			secondTabLine.setBackgroundColor(0xf1f1f1);
			thirdTabText.setTextColor(COLOR_UNCHOOSE);
			thirdTabLine.setBackgroundColor(0xf1f1f1);
			break;
		case 1:
			firstTabText.setTextColor(COLOR_UNCHOOSE);
			firstTabLine.setBackgroundColor(0xf1f1f1);
			secondTabText.setTextColor(COLOR_CHOOSE);
			secondTabLine.setBackgroundColor(COLOR_CHOOSE);
			thirdTabText.setTextColor(COLOR_UNCHOOSE);
			thirdTabLine.setBackgroundColor(0xf1f1f1);
			break;
		case 2:
			firstTabText.setTextColor(COLOR_UNCHOOSE);
			firstTabLine.setBackgroundColor(0xf1f1f1);
			secondTabText.setTextColor(COLOR_UNCHOOSE);
			secondTabLine.setBackgroundColor(0xf1f1f1);
			thirdTabText.setTextColor(COLOR_CHOOSE);
			thirdTabLine.setBackgroundColor(COLOR_CHOOSE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.second_tab) {
			if (mlistener != null) {
				if (mlistener.tabClicked(1)) {
					chooseTab(1);
				}
			} else {
				chooseTab(1);
			}

		} else if (v.getId() == R.id.third_tab) {
			if (mlistener != null) {
				if (mlistener.tabClicked(2)) {
					chooseTab(2);
				}
			} else {
				chooseTab(2);
			}   
		} else {
			if (mlistener != null) {
				if (mlistener.tabClicked(0)) { 
					chooseTab(0);
				}
			} else {
				chooseTab(0);
			}
		}
	}
}
