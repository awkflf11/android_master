package com.foryou.truck.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("deprecation")
public class MyGallery extends Gallery {
    private int time=5000;
	private static final int timerAnimation = 1;
	private final Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case timerAnimation:
				int position = getSelectedItemPosition();
				Log.i("msg", "position:" + position);
				if (position >= (getCount() - 1)) {
					onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
				} else {
					onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
				}
				break;

			default:
				break;
			}
		};
	};

	public void onStart(){
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
		if(task!=null){
			task.cancel();
			task = null;
		}

		timer = new Timer();
		task = new TimerTask() {
			public void run() {
				mHandler.sendEmptyMessage(timerAnimation);
			}
		};
		timer.schedule(task,time,time);
	}

	public void onStop(){
		if(task!=null) {
			task.cancel();
			task = null;
		}
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
	}

	private Timer timer;
	private TimerTask task;

	public MyGallery(Context paramContext) {
		super(paramContext);
	}

	public MyGallery(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public MyGallery(Context paramContext, AttributeSet paramAttributeSet,
					 int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	private boolean isScrollingLeft(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2) {
		float f2 = paramMotionEvent2.getX();
		float f1 = paramMotionEvent1.getX();
		if (f2 > f1)
			return true;
		return false;
	}

	public boolean onFling(MotionEvent paramMotionEvent1,
						   MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		int keyCode;
		if (isScrollingLeft(paramMotionEvent1, paramMotionEvent2)) {
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(keyCode, null);
		return true;
	}
}