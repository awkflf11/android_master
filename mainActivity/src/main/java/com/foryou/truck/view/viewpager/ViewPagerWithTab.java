package com.foryou.truck.view.viewPager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.foryou.truck.util.ScreenInfo;

import java.util.List;

/**
 * Created by dubin on 16/7/16.
 */
public class ViewPagerWithTab extends LinearLayout {
    CustomTab mCustomTab;
    ViewPager mViewPager;


    public ViewPagerWithTab(Context context) {
        super(context);
        mViewPager = new ViewPager(getContext());
    }

    public ViewPagerWithTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewPager = new ViewPager(getContext());
        mViewPager.setPadding(0, ScreenInfo.dip2px(getContext(),10),0,0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCustomTab.setTabSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setCurrentItem(int index){
        mViewPager.setCurrentItem(index);
    }

    public void setTabText(String[] tabText,int selectIndex){
        mCustomTab = new CustomTab(getContext(),tabText,selectIndex);
        mCustomTab.setOnTabClickListener(new CustomTab.ViewOnClickListener(){
            @Override
            public void onClick(int index) {
                mViewPager.setCurrentItem(index);
            }
        });
        addView(mCustomTab.getTabView());

        addView(mViewPager);
    }

    public void setAdapter(List<View> mViewList){
        PtrPagerAdapter mViewPagerAdapter = new PtrPagerAdapter(mViewList);
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    public void setTabNumber(int index,String count){
        int number = 0;
        try{
            number = Integer.valueOf(count);
        }catch (Exception e){

        }
        mCustomTab.setNumber(index,number);
    }
}
