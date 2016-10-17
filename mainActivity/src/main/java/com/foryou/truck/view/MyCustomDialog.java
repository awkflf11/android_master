package com.foryou.truck.view;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foryou.truck.R;

public class MyCustomDialog extends AlertDialog {
	private Context context;//
	private String mMessage = "";
	private SpannableString mMessage2 = null;
	private View.OnClickListener mListenerBtn1;
	private View.OnClickListener mListenerBtn2;
	private Boolean bool = false;//
	private TextView tv;//标题

	ProgressBar progressBar;
	TextView percentTv;


	public class ButtonItem {
		public int whichButton;
		public String text;
		public View.OnClickListener listener;
	}

	private List<ButtonItem> mButtonlist;
	private View mCustomView;

	@Override
	public void setMessage(CharSequence message) {
		mMessage = message.toString();
		mButtonlist = new ArrayList<ButtonItem>();
	}

	public void setMessage(SpannableString message) {
		mMessage2 = message;
		mButtonlist = new ArrayList<ButtonItem>();
	}

	public void setMessageLoacation(){
		tv.setGravity(Gravity.LEFT);
	}



	public MyCustomDialog(Context context) {
		super(context,THEME_HOLO_LIGHT);
		this.context = context;
		this.setTitle("");
		mCustomView = LayoutInflater.from(context).inflate(
				R.layout.custom_dialog_view, null);
		progressBar=(ProgressBar)mCustomView.findViewById(R.id.progressBar);
		percentTv=(TextView) mCustomView.findViewById(R.id.percent_tv);
		this.setView(mCustomView, 0, 0, 0, 0);
	}

	public MyCustomDialog(Context context, boolean bool) {
		super(context, THEME_HOLO_LIGHT);
		this.bool = bool;
		this.context = context;
		this.setTitle("");
		mCustomView = LayoutInflater.from(context).inflate(
				R.layout.custom_dialog_view, null);
		progressBar=(ProgressBar)mCustomView.findViewById(R.id.progressBar);
		percentTv=(TextView) mCustomView.findViewById(R.id.percent_tv);
		this.setView(mCustomView, 0, 0, 0, 0);
	}

	public ProgressBar getProgressBar(){
		return  progressBar;
	}

	public TextView getPercentTv(){
		return  percentTv;
	}

	public void  setProgressBarVisable(boolean isProgressBarVisable){
		if(isProgressBarVisable){
			progressBar.setVisibility(View.VISIBLE);
		}else{
			progressBar.setVisibility(View.GONE);
		}
	}

	private boolean isCancleDilog=true;//默认对话框是能取消的。

	public void  setCancleDialog(boolean isCancleDilog){
		this.isCancleDilog=isCancleDilog;
	}


	public void setButton(int whichButton, CharSequence text,
			View.OnClickListener listener) {
		ButtonItem mButtonItem = new ButtonItem();
		mButtonItem.whichButton = whichButton;
		mButtonItem.text = text.toString();
		mButtonItem.listener = listener;
		mButtonlist.add(mButtonItem);
	}

	private View.OnClickListener mlistener1 = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mListenerBtn1.onClick(v);
			if(!isCancleDilog){
				return;
			}
			MyCustomDialog.this.dismiss();
		}
	};

	private View.OnClickListener mlistener2 = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mListenerBtn2.onClick(v);
			MyCustomDialog.this.dismiss();
		}
	};
	public HasImageListener hasImageListener;

	public interface HasImageListener {
		void hasImage();
	}

	public void setOnClickListener(HasImageListener hasImageListener2) {
		hasImageListener = hasImageListener2;
	}

	@Override
	public void show() {
		tv = (TextView) mCustomView.findViewById(R.id.message);
		if (!mMessage.equals("")) {
			// TextView message = (TextView)mCustomView.findViewById(R.id.message);
			tv.setText(mMessage);
		} else if (mMessage2 != null) {
			// TextView message = (TextView)mCustomView.findViewById(R.id.message);
			// if (bool) {
			// // Drawable nav_up =
			// // context.getResources().getDrawable(R.drawable.attention);
			// // // Drawable nav_up =
			// context.getResources().getDrawable(context.getResources().getString(R.string.wenti));
			// // nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
			// // nav_up.getMinimumHeight());
			// // message.setCompoundDrawables(null, null, nav_up, null);
			// // // message.setCompoundDrawablePadding(-500);
			// // message.setGravity(Gravity.CENTER_HORIZONTAL);
			//
			// // hasImageListener.hasImage();
			// tv.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// hasImageListener.hasImage();
			// }
			//
			// });
			// }
			tv.setMovementMethod(LinkMovementMethod.getInstance());
			tv.setHighlightColor(Color.TRANSPARENT);
			tv.setText(mMessage2);
		}

		if (mButtonlist.size() > 0) {
			for (int i = 0; i < mButtonlist.size(); i++) {
				ButtonItem mButtonItem = mButtonlist.get(i);
				if (mButtonItem.whichButton == BUTTON_POSITIVE) {
					Button positionBtn = (Button) mCustomView.findViewById(R.id.position_btn);
					positionBtn.setVisibility(android.view.View.VISIBLE);
					positionBtn.setText(mButtonItem.text);
					mListenerBtn1 = mButtonItem.listener;
					positionBtn.setOnClickListener(mlistener1);

				} else if (mButtonItem.whichButton == BUTTON_NEGATIVE) {
					Button negaBtn = (Button) mCustomView.findViewById(R.id.cancel_btn);
					negaBtn.setVisibility(android.view.View.VISIBLE);
					negaBtn.setText(mButtonItem.text);
					mListenerBtn2 = mButtonItem.listener;
					negaBtn.setOnClickListener(mlistener2);
				}
			}
		}
		super.show();
	}
}
