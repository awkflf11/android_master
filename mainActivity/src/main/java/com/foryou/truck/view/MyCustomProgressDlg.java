package com.foryou.truck.view;

import android.app.Dialog;
import android.content.Context;

public class MyCustomProgressDlg extends Dialog{

	private MyCustomProgressDlg ProgressDialog = null;

	public MyCustomProgressDlg(Context context) {
		super(context);
	}

	public MyCustomProgressDlg(Context context, int theme) {
		super(context, theme);
	}

//	public MyCustomProgressDlg createDialog(Context context) {
//
//		if (null == context) {
//			return null;
//		}
//
//		ProgressDialog = new MyCustomProgressDlg(context,
//				R.style.mycustomprogressdlg);
//		ProgressDialog.setContentView(R.layout.mycustomprogressdlg);
//		ProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
//
//		return ProgressDialog;
//	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		if (ProgressDialog == null) {
			return;
		}

		/*
		ImageView imageView = (ImageView) ProgressDialog
				.findViewById(R.id.loadingImageView);
		
			AnimationDrawable animationDrawable = (AnimationDrawable) imageView
					.getBackground();
			animationDrawable.start();
		*/
	}

	/**
	 * 
	 * [Summary] setTitile 标题
	 * 
	 * @param strTitle
	 * @return
	 * 
	 */
	public MyCustomProgressDlg setTitile(String strTitle) {
		return ProgressDialog;
	}

	/**
	 * 
	 * [Summary] setMessage 提示内容
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public MyCustomProgressDlg setMessage(String strMessage) {

		//产品人员要求不显示加载提示,只显示白色圆环
		/*
		TextView tvMsg = (TextView) ProgressDialog
				.findViewById(R.id.id_tv_loadingmsg);

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
		*/

		return ProgressDialog;
	}
}
