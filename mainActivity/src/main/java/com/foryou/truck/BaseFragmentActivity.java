package com.foryou.truck;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.util.AnnotateUtil;
import com.foryou.truck.util.MyActivityManager;
import com.foryou.truck.view.MyCustomProgressDlg;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public abstract class BaseFragmentActivity extends FragmentActivity implements
		View.OnClickListener {
	private MyCustomProgressDlg progressDialog;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	public DisplayImageOptions options;

	// public Context mGlobleContext;

	public abstract void onClickListener(int id);

	private RelativeLayout mGlobleBackView;
	private TextView mGlobleTitle;

	public void setRootView() {

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyActivityManager.create().finishActivity(this);
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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		onClickListener(v.getId());
	}

	/**
	 * 普通的提示框，只有确定按钮，点击关闭对话框，停留当前页
	 * 
	 * @param title
	 *            对话框标题
	 * @param des
	 *            对话框内容
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
				// TODO Auto-generated method stub
			}

		});
		adb.show();

		// alertDialog(des, listener, new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// }
		//
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
			if (progressDialog == null) {
				progressDialog = new MyCustomProgressDlg(this,
						R.style.mycustomprogressdlg);
				progressDialog.setContentView(R.layout.mycustomprogressdlg);
				progressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
			}
			showProgressDialog("正在加载数据，请稍候...");
		}
	}

	public void showProgressDialog(String message) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog.setMessage(message);
			progressDialog.show();
		}
	}
}
