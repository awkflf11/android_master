package com.foryou.truck.view.viewPager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.R;
import com.foryou.truck.util.ScreenInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dubin on 16/7/16.
 */
public class CustomTab {
    private String[] mTabTextArray;
    private LinearLayout mTabView;
    private Context mContext;
    private List<TextView> mTextArray;
    private List<TextView> mLineArray;
    private List<TextView> mNumberArray;
    private ViewOnClickListener mListener;
    private TextView mNumber;

    class MyOnClickListener implements View.OnClickListener{
        int position;
        public MyOnClickListener(int pos){
            position = pos;
        }
        @Override
        public void onClick(View v) {
            setTabSelected(position);
            mListener.onClick(position);
        }
    };

    public static interface ViewOnClickListener{
        public void onClick(int index);
    }

    public void setOnTabClickListener(ViewOnClickListener listener){
        mListener = listener;
    }

    public LinearLayout getTabView(){
        return mTabView;
    }

    public void setTabSelected(int index){
        for(int i = 0;i<mTabTextArray.length;i++){
            if(i == index){
                mTextArray.get(i).setTextColor(mContext.getResources()
                        .getColor(R.color.my_blue_color));
                mLineArray.get(i).setBackgroundColor(mContext.getResources()
                        .getColor(R.color.my_blue_color));
            }else{
                mTextArray.get(i).setTextColor(mContext.getResources().getColor(R.color.my_black_color));
                mLineArray.get(i).setBackgroundColor(Color.WHITE);
            }
        }
    }

    private void initTab(){
        mTabView = new LinearLayout(mContext);
        mTabView.setOrientation(LinearLayout.HORIZONTAL);
        mTabView.setBackgroundColor(Color.WHITE);
        mTabView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ScreenInfo.dip2px(mContext,35)));

        mTextArray = new ArrayList<TextView>();
        mLineArray = new ArrayList<TextView>();
        mNumberArray = new ArrayList<TextView>();
    }

    public CustomTab(Context context,String[] tabText,int detaultIndex){
        mTabTextArray = tabText;
        mContext = context;
        initTab();

        for(int i = 0;i<tabText.length;i++) {
            RelativeLayout tabView = (RelativeLayout) LayoutInflater
                    .from(mContext).inflate(R.layout.tab_style, null);
            tabView.setOnClickListener(new MyOnClickListener(i));
            tabView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            TextView textview = (TextView) tabView.findViewById(R.id.tab_text);
            TextView tabLine = (TextView) tabView.findViewById(R.id.tab_line);
            TextView number = (TextView)tabView.findViewById(R.id.number);
            mTextArray.add(textview);
            mLineArray.add(tabLine);
            mNumberArray.add(number);
            textview.setText(tabText[i]);
            mTabView.addView(tabView);
        }
        setTabSelected(detaultIndex);
    }


    public void setNumber(int index,int count){
        if(count > 0){
            mNumberArray.get(index).setVisibility(View.VISIBLE);
            if(count>99) {
                mNumberArray.get(index).setText("99+");
            }else{
                mNumberArray.get(index).setText(count+"");
            }
        }else{
            mNumberArray.get(index).setVisibility(View.GONE);
        }
    }
}
