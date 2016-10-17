package com.foryou.truck;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @des:查看保险单
 */
public class DisplayImageActivity extends BaseActivity {
	private String TAG = "DisplayImageActivity";
	PhotoView img;
	String imgPath;
	RelativeLayout imgLayout;
	public ImageLoader imageLoader = ImageLoader.getInstance();
	private Bitmap mBitmap;
	private String filename;
	private int imgId;

	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.display_image);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		img = (PhotoView) findViewById(R.id.img);
		// imgLayout = (RelativeLayout)findViewById(R.id.img_layout);
		// img.setOnClickListener(imgListener);
		img.setOnViewTapListener(mViewTapListener);
		// img.setOnLongClickListener(mOnLongClickListener);
		imgPath = getIntent().getStringExtra("image");

		imgId = getIntent().getIntExtra("imageid", 0);
		if (imgId != 0) {
			img.setImageResource(imgId);
		}

		if (imgPath != null) {
			imageLoader.displayImage(imgPath, img, options);
			/*
			 * imageLoader.loadImage(imgPath, new ImageLoadingListener() {
			 * 
			 * @Override public void onLoadingCancelled(String arg0, View arg1)
			 * { // TODO Auto-generated method stub
			 * 
			 * }
			 * 
			 * @Override public void onLoadingComplete(String arg0, View arg1,
			 * Bitmap arg2) { // TODO Auto-generated method stub mBitmap = arg2;
			 * img.setImageBitmap(mBitmap); }
			 * 
			 * @Override public void onLoadingFailed(String arg0, View arg1,
			 * FailReason arg2) { // TODO Auto-generated method stub
			 * 
			 * }
			 * 
			 * @Override public void onLoadingStarted(String arg0, View arg1) {
			 * // TODO Auto-generated method stub
			 * 
			 * }
			 * 
			 * });
			 */
			// imageLoader.displayImage(imgPath, img);
		}
	}
	public AlertDialog alertCustomeDialog(Context context, String title,
			String des, String leftBtnStr,
			DialogInterface.OnClickListener listenerLeft, String rightStr,
			DialogInterface.OnClickListener listenerRigth) {
		AlertDialog.Builder adb = new AlertDialog.Builder(context);
		if (title != null) {
			adb.setTitle(title);
		}
		if (des != null) {
			adb.setMessage(des);
		}
		if (leftBtnStr != null) {
			adb.setPositiveButton(leftBtnStr, listenerLeft);
		}
		if (rightStr != null) {
			adb.setNegativeButton(rightStr, listenerRigth);
		}

		return adb.show();
	}

	// OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
	//
	// @Override
	// public boolean onLongClick(View arg0) {
	// // TODO Auto-generated method stub
	// filename = "" + System.currentTimeMillis();
	// alertCustomeDialog(DisplayImageActivity.this,
	// getString(R.string.save_picture), getString(R.string.path)
	// + ":" + Constant.PitcturPath + "\n"
	// + getString(R.string.file_name) + ":" + filename
	// + ".png", getString(R.string.ok), listenerLeft,
	// getString(R.string.cancel), listenerRigth);
	// return false;
	// }
	// };

//	DialogInterface.OnClickListener listenerLeft = new DialogInterface.OnClickListener() {
//
//		@Override
//		public void onClick(DialogInterface arg0, int arg1) {
//			// TODO Auto-generated method stub
//			ImageTools.savePhotoToSDCard(mBitmap, Constant.PitcturPath, ""
//					+ filename);
//			Toast.makeText(DisplayImageActivity.this,
//					getString(R.string.save_success), Toast.LENGTH_SHORT)
//					.show();
//		}
//
//	};
//	DialogInterface.OnClickListener listenerRigth = new DialogInterface.OnClickListener() {
//
//		@Override
//		public void onClick(DialogInterface arg0, int arg1) {
//			// TODO Auto-generated method stub
//
//		}
//
//	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//MobclickAgent.onPause(this);
	}

	OnViewTapListener mViewTapListener = new OnViewTapListener() {
		@Override
		public void onViewTap(View arg0, float arg1, float arg2) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onViewTap--------->");
			finish();
		}
	};

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub

	}

}
