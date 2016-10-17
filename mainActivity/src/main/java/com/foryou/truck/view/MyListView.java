package com.foryou.truck.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.foryou.truck.util.UtilsLog;

public class MyListView extends ListView {
	private OnScrollListener mScrollListener;
	private ImageView mTopImage;
	String TAG = "MyListView";

	private int mMaxvisibleItemCount = 0;

	private final OnScrollListener ON_LISTVIEW_SCROLL_LISTENER = new OnScrollListener() {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// UtilsLog.i(TAG, "firstVisibleItem:" + firstVisibleItem
			// + ",visibleItemCount" + visibleItemCount
			// + ",totalItemCount" + totalItemCount);
			if (mMaxvisibleItemCount < visibleItemCount) {
				mMaxvisibleItemCount = visibleItemCount;
			}

			if (firstVisibleItem > mMaxvisibleItemCount) {
				if (mTopImage != null) {
					mTopImage.setVisibility(android.view.View.VISIBLE);
				}
			} else {
				if (mTopImage != null) {
					mTopImage.setVisibility(android.view.View.GONE);
				}
			}
			if (mScrollListener != null) {
				mScrollListener.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mScrollListener != null) {
				mScrollListener.onScrollStateChanged(view, scrollState);
			}
		}
	};

	private View.OnClickListener mTopImageListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MyListView.this.smoothScrollToPosition(0);
		}
	};

	public void setTopImage(ImageView image) {
		mTopImage = image;
		mTopImage.setOnClickListener(mTopImageListener);
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		super.setOnScrollListener(ON_LISTVIEW_SCROLL_LISTENER);
		mScrollListener = l;
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mGestureDetector = new GestureDetector(context, new YScrollDetector());
	}

	public MyListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// Log.i("aa", "MyListView--dispatchTouchEvent");
		return super.dispatchTouchEvent(ev);
	}

	class YScrollDetector extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			/**
			 * 如果我们滚动更接近水平方向,返回false,让子视图来处理它
			 */

			boolean flag = (Math.abs(distanceY) > Math.abs(distanceX));
			return flag;
		}
	}

	public boolean vpFlag = false;
	private GestureDetector mGestureDetector;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// Log.i("aa", "MyListView--onInterceptTouchEvent");
		boolean flag1 = super.onInterceptTouchEvent(ev);
		boolean flag2 = mGestureDetector.onTouchEvent(ev);
		UtilsLog.i(TAG, "super.onInterceptTouchEvent(ev):" + flag1
				+ ",mGestureDetector.onTouchEvent(ev):" + flag2);
		return flag1 && flag2;
		// return flags && ;
	}

	// @Override
	// public boolean onInterceptTouchEvent(MotionEvent ev) {
	// boolean result = super.onInterceptTouchEvent(ev);
	// if (getFirstVisiblePosition() == 0) {
	// if (touchView != null) {
	// Rect rect = new Rect(touchView.getLeft(), touchView.getTop(),
	// touchView.getRight(), touchView.getBottom());
	// if (rect.contains((int) ev.getX(), (int) ev.getY())) {
	// result = false;
	// }
	// }
	// }
	// return result;
	// }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// Log.i("aa", "MyListView--onTouchEvent");
		return super.onTouchEvent(event);
	}
	
//	@Override
//
//	/**   只重写该方法，达到使ListView适应ScrollView的效果   */ 
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//	     int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//	MeasureSpec.AT_MOST);
//	super.onMeasure(widthMeasureSpec, expandSpec);
//
//	}

}
