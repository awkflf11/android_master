package com.foryou.truck.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.foryou.truck.R;
import com.foryou.truck.util.ScreenInfo;

public class WithDelImgEditText extends EditText {
	private Context mContext;
	// private Drawable imgInable;
	private Drawable imgAble;
	private String TAG = "WithDelImgEditText";
	private boolean isVisable;

	public WithDelImgEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public WithDelImgEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public WithDelImgEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	//
	private FocustListener mFocusListener;

	public interface FocustListener {
		void onFocusChange(boolean flag);
	}

	public void setOnfocusChangeListener(FocustListener listener) {
		mFocusListener = listener;
	}

	//
	private void init() {
		// imgInable =
		// mContext.getResources().getDrawable(R.drawable.delete_gray);
		imgAble = mContext.getResources().getDrawable(R.drawable.close_btn);

		addTextChangedListener(new TextWatcher(){
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				setDrawable();
			}
		});
		setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					setDrawable();
				} else {
					setCompoundDrawablesWithIntrinsicBounds(null, null, null,
							null);
					isVisable = false;
				}
				//
				if (mFocusListener != null) {
					mFocusListener.onFocusChange(hasFocus);
				}
				if (mFocusListener != null) {
					mFocusListener.onFocusChange(hasFocus);
				}
			}

		});
		setDrawable();
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		super.setText(text, type);
		setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		isVisable = false;
		try {
			if (text != null) {
				setSelection(text.length());
			}
		} catch (Exception e) {

		}

	}

	// 设置删除图片
	public void setDrawable() {
		// Log.i(TAG, "setDrawable:" + this.isFocusable());
		if (length() < 1) {
			setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			isVisable = false;
		} else {
			setCompoundDrawablesWithIntrinsicBounds(null, null, imgAble, null);
			isVisable = true;
		}
	}

	// 处理删除事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isVisable) {
			return super.onTouchEvent(event);
		}
		if (imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
			int eventX = (int) event.getRawX();
			int eventY = (int) event.getRawY();
			Log.e(TAG, "eventX = " + eventX + "; eventY = " + eventY);
			Rect rect = new Rect();
			getGlobalVisibleRect(rect);
			rect.left = rect.right - ScreenInfo.dip2px(mContext, 30);
			if (rect.contains(eventX, eventY)) {
				setText("");
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
