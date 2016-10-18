package com.foryou.truck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foryou.truck.activity.newGuideBeginAct;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.SharePerfenceUtil;
import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
	SharedPreferences sp;
	@BindView(id = R.id.main_bg)
	private RelativeLayout mMainBg;
	private int count = 7;
	@BindView(id = R.id.img1)
	private ImageView mImg1;
	@BindView(id = R.id.img2)
	private ImageView mImg2;
	@BindView(id = R.id.img3)
	private ImageView mImg3;
	@BindView(id = R.id.img4)
	private ImageView mImg4;
	@BindView(id = R.id.img5)
	private ImageView mImg5;
	@BindView(id = R.id.img6)
	private ImageView mImg6;
	@BindView(id = R.id.img7)
	private ImageView mImg7;
	private ArrayList<ImageView> mlist;
	private int[] timeArr = { 20, 50, 80, 110, 140, 170, 200 };
	// test ....
	@Override
	public void setRootView() {
		// super.setRootView();
		setContentView(R.layout.guide_view);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		PushManager.getInstance().initialize(this.getApplicationContext());

		mlist = new ArrayList<ImageView>();
		mlist.add(mImg1);
		mlist.add(mImg2);
		mlist.add(mImg3);
		mlist.add(mImg4);
		mlist.add(mImg5);
		mlist.add(mImg6);
		mlist.add(mImg7);

		animationTo();
		// RequestQueue mVolleyQueue = Volley.newRequestQueue(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			mlist.get(7 - count).setVisibility(android.view.View.VISIBLE);
			count--;

			// mTextView.setText(content.substring(0, content.length() -
			// count));
			if (count == 0) {
				Thread t = new Thread() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						redirectTo();
						finish();
						// mMainBg.setAnimation(animation);
						// MainActivity.this.runOnUiThread(new Runnable() {
						//
						// @Override
						// public void run() {
						// // TODO Auto-generated method stub
						// mMainBg.startAnimation(animation);
						// }
						//
						// });

					}

				};
				t.start();

			} else {
				mHandler.sendEmptyMessageDelayed(0, timeArr[count - 1]);
			}

		}
	};

	Animation animation;

	private void animationTo() {
		// count = content.length();

		animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		// 监听动画过程
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// checkVersion();

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				redirectTo();
				finish();
			}
		});
		// mMainBg.setAnimation(animation);

		mHandler.sendEmptyMessageDelayed(0, timeArr[count - 1]);
	}

	protected void redirectTo() {
		// TODO Auto-generated method stub
		Intent intent;
		if (SharePerfenceUtil.getFirstLogin(this)) {
			intent = new Intent(this, WelcomeViewPage.class);
			startActivity(intent);
		} else {
//			String classname = SharePerfenceUtil.getLoadClass(this);
//			String id = SharePerfenceUtil.getFYID(this);
//			Log.i("aa", "classname:" + classname + ",id:" + id);
//			if (!classname.equals("") && !id.equals("")) {
//				if (classname.equals("AgentAndQuoteDetailAct")) {
//					intent = new Intent(this, AgentAndQuoteDetailAct.class);
//					intent.putExtra("order_id", id);
//					startActivity(intent);
//				} else if (classname.equals("OrderDetailActivity")) {
//					intent = new Intent(this, OrderDetailActivity.class);
//					intent.putExtra("order_id", id);
//					startActivity(intent);
//				}
//				SharePerfenceUtil.clearGTMESSAGE(this);
//				this.finish();
//			} else {
//				intent = new Intent(this, HomeMainScreenActivity.class);
//				startActivity(intent);
//				// skipActivity(this, TabActivity.class);
//			}
			if(SharePerfenceUtil.getFirstNewGuide(this)){
				intent = new Intent(this, newGuideBeginAct.class);
			}else{
				intent = new Intent(this, HomeMainScreenActivity.class);
			}
			startActivity(intent);
			overridePendingTransition(R.anim.fade, R.anim.hold);
			// skipActivity(this, WelcomeViewPage.class);
		}
	}

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub

	}
}
