package com.foryou.truck.view;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.R;
import com.foryou.truck.util.ScreenInfo;

/**
 * Created by dubin on 16/7/21.
 */

//运单详情中具有展开功能的按钮样式
public class WithSpreadButton extends LinearLayout implements View.OnClickListener {

    LinearLayout mFirstButton;
    TextView mSecondButton;
    boolean mSpreadFlag = false;
    ImageView mFirstBtnArrow;
    TextView mFirstBtnText;
    SpreadOnclickListener mListener;

    public interface SpreadOnclickListener {
        public void fistBtnClick();

        public void secondBtnClick();
    }

    public void setSpreadOnClickLister(SpreadOnclickListener listener) {
        mListener = listener;
    }

    public WithSpreadButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFirstButton = (LinearLayout) findViewById(R.id.first_btn);
        mFirstButton.setOnClickListener(this);
        mSecondButton = (TextView) findViewById(R.id.second_btn);
        mSecondButton.setOnClickListener(this);
        mFirstBtnArrow = (ImageView) findViewById(R.id.first_btn_arrow);
        mFirstBtnText = (TextView) findViewById(R.id.first_btn_text);
    }

    public void setFirstBtnText(String str) {
        mFirstButton.setVisibility(View.VISIBLE);
        mFirstBtnText.setText(str);
    }

    public boolean getSpreadFlag() {
        return mSpreadFlag;
    }

    //如:  运费总额:400
    public void setFirstBtnText(String text1, String text2) {
        mFirstButton.setVisibility(View.VISIBLE);
        String str = text1 + text2;
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(
                new ForegroundColorSpan(getResources().getColor(
                        R.color.my_pink_color)), str.indexOf(text2),
                str.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mFirstBtnText.setText(spannableString);
    }

    public void setSecondBtnText(String str) {
        mSecondButton.setText(str);
    }

    public String getFirstBtnString() {
        return mFirstBtnText.getText().toString();
    }

    public String getSecondBtnString() {
        return mSecondButton.getText().toString();
    }

    public void setSpreadFlag(boolean flag) {
        mSpreadFlag = flag;
        if (mSpreadFlag) {

        } else {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
            lp.setMargins(ScreenInfo.dip2px(getContext(), 10), ScreenInfo.dip2px(getContext(), 10)
                    , ScreenInfo.dip2px(getContext(), 10), ScreenInfo.dip2px(getContext(), 10));
            LayoutParams lp1 = (LinearLayout.LayoutParams) mFirstButton.getLayoutParams();
            lp1.weight = 1;
            lp1.setMargins(0, 0, ScreenInfo.dip2px(getContext(), 40), 0);
            mFirstBtnArrow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_btn:
                if (mListener != null) {
                    mListener.fistBtnClick();
                }
                break;
            case R.id.second_btn:
                if (mListener != null) {
                    mListener.secondBtnClick();
                }
                break;
        }
    }
}
