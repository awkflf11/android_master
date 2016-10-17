package com.foryou.truck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foryou.truck.activity.newGuideAct;
import com.foryou.truck.activity.newGuideBeginAct;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.UtilsLog;

public class WelcomeViewPage extends BaseActivity {
	private String TAG = "WelcomeViewPage";
	@BindView(id = R.id.vPager)
	private ViewPager mViewPager;
	@BindView(id = R.id.index1)
	private ImageView mIndex1;
	@BindView(id = R.id.index2)
	private ImageView mIndex2;
	@BindView(id = R.id.index3)
	private ImageView mIndex3;
	@BindView(id = R.id.index4)
	private ImageView mIndex4;
	@BindView(id = R.id.startapp, click = true)
	private ImageView mStartApp;//
	private int[] mImageArr = { R.drawable.bg1, R.drawable.bg2, R.drawable.bg3};
	private int mCurrentIndex;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.welcome_viewpage);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mViewPager.setAdapter(new MyViewPagerAdapter());
		mViewPager.setCurrentItem(0);
		mCurrentIndex = 0;
		int screenHeight = ScreenInfo.getScreenInfo(this).heightPixels;
		Log.i("aa", "screenWidth:" + screenHeight);
		float canshu = (float) screenHeight / (float) 1920;
		Log.i("aa", "canshu:" + canshu);
		// mStartApp.setL
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.bottomMargin = (int) (65 * 3 * canshu);
		Log.i("aa", "bottomMargin:" + params.bottomMargin);
		mStartApp.setLayoutParams(params);

		mIndex4.setVisibility(View.GONE);
		updateIndexImage(1);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.startapp:
			SharePerfenceUtil.SetFirstLogin(this, false);
			//Intent intent = new Intent(this, HomeMainScreenActivity.class);
			Intent intent = new Intent(this, newGuideBeginAct.class);
			startActivity(intent);
			finish();
			break;
		}
	}

	private class MyViewPagerAdapter extends PagerAdapter {

		public MyViewPagerAdapter() {
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView view = new ImageView(WelcomeViewPage.this);
			view.setBackgroundResource(mImageArr[position]);
			container.setId(position);
			((ViewPager) container).addView(view, 0);
			return view;
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
			updateIndexImage(arg0 + 1);
		}
	}

	private void updateIndexImage(int index) {
		UtilsLog.i(TAG, "index:" + index);
		switch (index) {
		case 1:
			mStartApp.setVisibility(android.view.View.GONE);
			mIndex1.setBackgroundResource(R.drawable.circle2);
			mIndex2.setBackgroundResource(R.drawable.circle1);
			mIndex3.setBackgroundResource(R.drawable.circle1);
			mIndex4.setBackgroundResource(R.drawable.circle1);
			break;
		case 2:
			mStartApp.setVisibility(android.view.View.GONE);
			mIndex1.setBackgroundResource(R.drawable.circle1);
			mIndex2.setBackgroundResource(R.drawable.circle2);
			mIndex3.setBackgroundResource(R.drawable.circle1);
			mIndex4.setBackgroundResource(R.drawable.circle1);
			break;
		case 3:
			mStartApp.setVisibility(View.VISIBLE);
			mIndex1.setBackgroundResource(R.drawable.circle1);
			mIndex2.setBackgroundResource(R.drawable.circle1);
			mIndex3.setBackgroundResource(R.drawable.circle2);
			mIndex4.setBackgroundResource(R.drawable.circle1);
			break;
		case 4:
			mStartApp.setVisibility(android.view.View.VISIBLE);
			mIndex1.setBackgroundResource(R.drawable.circle1);
			mIndex2.setBackgroundResource(R.drawable.circle1);
			mIndex3.setBackgroundResource(R.drawable.circle1);
			mIndex4.setBackgroundResource(R.drawable.circle2);
			break;
		}
	}

}
