package com.foryou.truck;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.util.AnnotateUtil;
import com.foryou.truck.util.MyActivityManager;
import com.foryou.truck.view.MyCustomProgressDlg;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.leakcanary.RefWatcher;

import org.greenrobot.eventbus.EventBus;

public abstract class BaseActivity extends Activity implements
		View.OnClickListener {
	private MyCustomProgressDlg progressDialog;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;

	// public Context mGlobleContext;

	public abstract void onClickListener(int id);

	public RelativeLayout mGlobleBackView;
	private TextView mGlobleTitle;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		EventBus.getDefault().unregister(this);
		MyActivityManager.create().finishActivity(this);
		progressDialog = null;
		RefWatcher refWatcher = MyApplication.getRefWatcher(this);
		refWatcher.watch(this);
	}

	public void setRootView() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		MyActivityManager.create().addActivity(this);
		setRootView();
		AnnotateUtil.initBindView(this);

	}

	public void setTitle(String title) {
		mGlobleTitle = (TextView) findViewById(R.id.title);
		mGlobleTitle.setText(title);
	}

	public void ShowBackView() {
		mGlobleBackView = (RelativeLayout) findViewById(R.id.back_view);
		mGlobleBackView.setVisibility(android.view.View.VISIBLE);
		mGlobleBackView.setOnClickListener(mBackClickListener);
	}

	private View.OnClickListener mBackClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onBackPressed();
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (mGlobleTitle != null) {
			TongjiModel.onResume(this, mGlobleTitle.getText().toString());
		}
	}

	@Override
	protected void onStop(){
		super.onStop();
		cancelProgressDialog();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mGlobleTitle != null) {
			TongjiModel.onPause(this, mGlobleTitle.getText().toString());
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		onClickListener(v.getId());
	}

	//######  对话框的开始################
	/**
	 * 普通的提示框，只有确定按钮，点击关闭对话框，停留当前页
	 * @param title：对话框标题
	 * @param des：对话框内容
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void alertDialog(String title, String des, final boolean needfinish) {
		AlertDialog.Builder adb;
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			adb = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		} else {
			adb = new AlertDialog.Builder(this);
		}
		// AlertDialog.Builder adb = new AlertDialog.Builder(this,
		// AlertDialog.THEME_HOLO_LIGHT);
		adb.setTitle(title);
		adb.setMessage(des);
		adb.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (needfinish) {
					finish();
				}
			}
		});
		adb.show();
		// alertDialog(des, null, null, needfinish);
	}
//有确定和取消的按钮
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void alertDialog(String des, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder adb;
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			adb = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		} else {
			adb = new AlertDialog.Builder(this);
		}
		adb.setTitle("");
		adb.setMessage(des);
		adb.setPositiveButton("确定", listener);
		adb.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		adb.show();
		// alertDialog(des, listener, new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// }
		// }, false);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void alertDialog(String title, String des,
			DialogInterface.OnClickListener listener,
			DialogInterface.OnClickListener listener2) {
		AlertDialog.Builder adb;
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			adb = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		} else {
			adb = new AlertDialog.Builder(this);
		}
		adb.setTitle(title);
		adb.setMessage(des);
		adb.setPositiveButton("确定", listener);
		adb.setNegativeButton("取消", listener2);
		adb.show();

		// alertDialog(des, listener, listener2, false);
	}
//只有确定的按钮
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void alertDialog(String des,
							DialogInterface.OnClickListener listener,
							boolean cancelable) {
		AlertDialog.Builder adb;
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			adb = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		} else {
			adb = new AlertDialog.Builder(this);
		}
		adb.setTitle("");
		adb.setMessage(des);
		adb.setCancelable(false);
		adb.setPositiveButton("确定", listener);
		adb.show();

		// alertDialog(des, listener, listener2, false);
	}
//################对话框的结束#########

	/**
	 * 取消加载框
	 */
	public void cancelProgressDialog() {
		if (!this.isFinishing()) {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.cancel();
				progressDialog = null;
			}
		}
	}

	public void setTextView(TextView tx, String str) {
		if (str != null && tx != null) {
			tx.setText(str);
		}
	}

	public void showProgressDialog() {
		if (!this.isFinishing()) {
			if(progressDialog == null){
				progressDialog = new MyCustomProgressDlg(this,
						R.style.mycustomprogressdlg);
				progressDialog.setContentView(R.layout.mycustomprogressdlg);
				progressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
			}
			showProgressDialog("正在加载数据，请稍候...");
		}
	}

	public void showProgressDialog(String message) {
		if (progressDialog != null
				&& !this.isFinishing()) {
			progressDialog.dismiss();
			progressDialog.setMessage(message);
			progressDialog.show();
		}
	}
}
