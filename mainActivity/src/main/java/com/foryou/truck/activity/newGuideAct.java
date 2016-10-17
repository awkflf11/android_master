package com.foryou.truck.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.HomeMainScreenActivity;
import com.foryou.truck.R;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.UtilsLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @des:新手引导
 */
public class newGuideAct extends BaseActivity {
	private String TAG = "newGuideAct";
	private Context mContext;
	@BindView(id = R.id.vPager)
	private ViewPager mViewPager;
	@BindView(id = R.id.startapp, click = true)
	private ImageView mStartApp;//

//	private ImageView mIndex4;
	private int[] mImageArr = { R.drawable.new_guide1, R.drawable.new_guide2
			, R.drawable.new_guide3, R.drawable.new_guide4, R.drawable.new_guide5, R.drawable.new_guide6};
	private int mCurrentIndex;
	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.act_new_guide);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private MyViewPagerAdapter mPageAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mContext=this;
		mPageAdapter = new MyViewPagerAdapter();
		mViewPager.setAdapter(mPageAdapter);
		mViewPager.setCurrentItem(0);
		mCurrentIndex = 0;
		int screenHeight = ScreenInfo.getScreenInfo(this).heightPixels;
		Log.i("aa", "screenWidth:" + screenHeight);
		float canshu = (float) screenHeight / (float) 1920;
		// mStartApp.setL
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.bottomMargin = (int) (65 * 3 * canshu);
		Log.i("aa", "bottomMargin:" + params.bottomMargin);
		mStartApp.setLayoutParams(params);
		//mIndex4.setVisibility(View.GONE);
		updateIndexImage(1);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	@Override
	public void onClickListener(int id) {
		Intent intent;
		switch (id) {
	   case R.id.tiao_guo_btn:
		   SharePerfenceUtil.SetFirstNewGuide(this, false);
		   TongjiModel.addEvent(mContext, "新手引导页", TongjiModel.TYPE_BUTTON_CLIKC,"第"+(mCurrentIndex+1)+"页面");
		   intent = new Intent(this, HomeMainScreenActivity.class);
		   startActivity(intent);
		   finish();
		   break;
	   case R.id.middle_btn:
	   case R.id.botoom_btn:
		   if(mCurrentIndex>=0&&mCurrentIndex<=5){
			   mViewPager.setCurrentItem(++mCurrentIndex);
		   }
		   if(mCurrentIndex==6){
			   SharePerfenceUtil.SetFirstNewGuide(this, false);
			   intent = new Intent(this, HomeMainScreenActivity.class);
			   startActivity(intent);
			   finish();
		   }
		   break;

		case R.id.startapp:
			//SharePerfenceUtil.SetFirstLogin(this, false);
			intent = new Intent(this, HomeMainScreenActivity.class);
			startActivity(intent);
			finish();
			break;
		}
	}

	public class MyViewPagerAdapter extends PagerAdapter {

		public MyViewPagerAdapter() {
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
				RelativeLayout mBg4Relative = (RelativeLayout) LayoutInflater.from(newGuideAct.this).inflate(R.layout.act_new_guide_layout, null);
				ImageView bgIv = (ImageView) mBg4Relative.findViewById(R.id.bg_image);
				Button tiaoGuoBtn = (Button) mBg4Relative.findViewById(R.id.tiao_guo_btn);
				Button middleBtn = (Button) mBg4Relative.findViewById(R.id.middle_btn);
				Button bootomBtn = (Button) mBg4Relative.findViewById(R.id.botoom_btn);
				bgIv.setBackgroundResource(mImageArr[position]);
			   tiaoGuoBtn.setOnClickListener(newGuideAct.this);
			    middleBtn.setOnClickListener(newGuideAct.this);
		     	bootomBtn.setOnClickListener(newGuideAct.this);
		   if(position==0){
				tiaoGuoBtn.setVisibility(View.VISIBLE);
				tiaoGuoBtn.setBackgroundResource(R.drawable.guide_pass);
				middleBtn.setVisibility(View.GONE);
				bootomBtn.setVisibility(View.VISIBLE);
				bootomBtn.setBackgroundResource(R.drawable.guide_xiayibu);
			}else if(position==1){
				tiaoGuoBtn.setVisibility(View.VISIBLE);
				tiaoGuoBtn.setBackgroundResource(R.drawable.guide_pass);
				middleBtn.setVisibility(View.VISIBLE);
				middleBtn.setBackgroundResource(R.drawable.guide_xiayibu);
			}else if(position==2){
				tiaoGuoBtn.setVisibility(View.VISIBLE);
				tiaoGuoBtn.setBackgroundResource(R.drawable.guide_pass);
				middleBtn.setVisibility(View.VISIBLE);
				middleBtn.setBackgroundResource(R.drawable.guide_xiayibu);
			}else if(position==3){
				tiaoGuoBtn.setVisibility(View.VISIBLE);
				tiaoGuoBtn.setBackgroundResource(R.drawable.guide_pass);
				bootomBtn.setVisibility(View.VISIBLE);
				bootomBtn.setBackgroundResource(R.drawable.guide_xiayibu);
			}else if(position==4){
				tiaoGuoBtn.setVisibility(View.VISIBLE);
				tiaoGuoBtn.setBackgroundResource(R.drawable.guide_pass);
				bootomBtn.setVisibility(View.VISIBLE);
				bootomBtn.setBackgroundResource(R.drawable.guide_xiayibu);
			}else if(position==5){
				tiaoGuoBtn.setVisibility(View.GONE);
				middleBtn.setVisibility(View.VISIBLE);
				middleBtn.setBackgroundResource(R.drawable.guide_kaishitiyan);
			}
//			ImageView view = new ImageView(newGuideAct.this);
//			view.setBackgroundResource(mImageArr[position]);
			container.setId(position);
			((ViewPager) container).addView(mBg4Relative, 0);
			return mBg4Relative;

		}
		@Override
		public int getCount() {
			return mImageArr.length;
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

		public void onPageScrollStateChanged(int arg0) {

		}
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		public void onPageSelected(int arg0) {
			mCurrentIndex = arg0;
			//updateIndexImage(arg0 + 1);
		}
	}



	private void updateIndexImage(int index) {
		UtilsLog.i(TAG, "index:" + index);
		switch (index) {
//		case 1:
//			mStartApp.setVisibility(View.GONE);
//			mIndex1.setBackgroundResource(R.drawable.circle2);
//			mIndex2.setBackgroundResource(R.drawable.circle1);
//			mIndex3.setBackgroundResource(R.drawable.circle1);
//			mIndex4.setBackgroundResource(R.drawable.circle1);
//			break;
//		case 2:
//			mStartApp.setVisibility(View.GONE);
//			mIndex1.setBackgroundResource(R.drawable.circle1);
//			mIndex2.setBackgroundResource(R.drawable.circle2);
//			mIndex3.setBackgroundResource(R.drawable.circle1);
//			mIndex4.setBackgroundResource(R.drawable.circle1);
//			break;
//		case 3:
//			mStartApp.setVisibility(View.VISIBLE);
//			mIndex1.setBackgroundResource(R.drawable.circle1);
//			mIndex2.setBackgroundResource(R.drawable.circle1);
//			mIndex3.setBackgroundResource(R.drawable.circle2);
//			mIndex4.setBackgroundResource(R.drawable.circle1);
//			break;
		case 4:
//			mStartApp.setVisibility(View.VISIBLE);
//			mIndex1.setBackgroundResource(R.drawable.circle1);
//			mIndex2.setBackgroundResource(R.drawable.circle1);
//			mIndex3.setBackgroundResource(R.drawable.circle1);
//			mIndex4.setBackgroundResource(R.drawable.circle2);
			break;
		}
	}

}
