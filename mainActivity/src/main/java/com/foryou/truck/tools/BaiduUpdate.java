package com.foryou.truck.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.CPUpdateDownloadCallback;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.foryou.truck.R;
import com.foryou.truck.util.MyActivityManager;

import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.MyCustomDialog;

import java.io.File;

/**
 * @des:百度自动更新工具类
 */
public class BaiduUpdate {
	private String TAG = "BaiduUpdate";
	private Context mContext;
	private int notification_id = 19172439;
	private NotificationManager nm;
	private Notification notification;
	private boolean isTishi = false;

	public BaiduUpdate(Context context, boolean bool) {
		mContext = context;
		isTishi = bool;
	}

	public void initNotification() {
		nm = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
		notification = new Notification(R.drawable.ic_launch, "", System.currentTimeMillis());
		//使用notification.xml文件作VIEW
		notification.contentView = new RemoteViews(mContext.getPackageName(), R.layout.notification);
		//设置进度条，最大值 为100,当前值为0，最后一个参数为true时显示条纹（就是在Android Market下载软件，点击下载但还没获取到目标大小时的状态）
		notification.contentView.setProgressBar(R.id.pb, 100, 0, false);
//	    Intent notificationIntent = new Intent(mContext, className);
//		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
//		notification.contentIntent = contentIntent;
	}

	public void cancelDailog() {
		if (mDialog2 != null) {
			if (mDialog2.isShowing()) {
				mDialog2.dismiss();
			}
			mDialog2 = null;
		}
	}

	public void baiduUpdate(int type) {
		if (!Tools.IsConnectToNetWork(mContext)) {
			ToastUtils.toast(mContext, "联网异常,请稍后再试");
			return;
		}
		switch (type) {
			case 1:// UI更新
				BDAutoUpdateSDK.uiUpdateAction(mContext, new MyUICheckUpdateCallback());
				break;
			case 2://静默更新
				BDAutoUpdateSDK.silenceUpdateAction(mContext);
				break;
			case 3://百度助手更新
				BDAutoUpdateSDK.asUpdateAction(mContext, new MyUICheckUpdateCallback());
				break;
			case 4:// 无UI更新
				BDAutoUpdateSDK.cpUpdateCheck(mContext, new MyCPCheckUpdateCallback());
				break;
			default:
				break;
		}
	}


	private void gotoDownLoad(final AppUpdateInfo info) {
		if ( Tools.isConnectToWifi(mContext)) {
			UtilsLog.i(TAG, "download ....");
			BDAutoUpdateSDK.cpUpdateDownload(mContext, info, new UpdateDownloadCallback());
		} else {
			mDialog2 = new MyCustomDialog(mContext);
			progressBar=mDialog2.getProgressBar();
			percentTv = mDialog2.getPercentTv();

			mDialog2.setCancelable(false);
			mDialog2.setCanceledOnTouchOutside(false);
			mDialog2.setCancleDialog(false);
			mDialog2.setProgressBarVisable(true);

			mDialog2.setMessage("版本：" + info.getAppVersionName() + "\n \n" + Html.fromHtml(info.getAppChangeLog()));
			mDialog2.setButton(MyCustomDialog.BUTTON_POSITIVE, "更新",
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							BDAutoUpdateSDK.cpUpdateDownload(mContext, info, new UpdateDownloadCallback());
						}
					});
			mDialog2.show();
			mDialog2.setMessageLoacation();
		}
	}

	private MyCustomDialog mDialog2;
	ProgressBar progressBar;
	TextView percentTv;

	private class MyCPCheckUpdateCallback implements CPCheckUpdateCallback {
		@Override
		public void onCheckUpdateCallback(final AppUpdateInfo info, AppUpdateInfoForInstall infoForInstall) {
//			if(infoForInstall != null && !TextUtils.isEmpty(infoForInstall.getInstallPath())) {
//				//txt_log.setText(txt_log.getText() + "\n install info: " + infoForInstall.getAppSName() + ",
//				// \nverion=" + infoForInstall.getAppVersionName() + ", \nchange log=" + infoForInstall.getAppChangeLog());
//				//txt_log.setText(txt_log.getText() + "\n we can install the apk file in: " + infoForInstall.getInstallPath());
//				BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), infoForInstall.getInstallPath());
//			}
			UtilsLog.i(TAG, "info..............." + info);
			if (info != null) {
				try {
					int currentVersion = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
					if (info.getAppVersionCode() <= currentVersion) {

						if (isTishi) {
							ToastUtils.toast(mContext, "已经是最新版本，无需更新");
						}
						return;
					}
					UtilsLog.i(TAG, "info:" + info.getAppVersionCode());
					String fileurl = SharePerfenceUtil.getApkUrlPath(mContext);
					UtilsLog.i(TAG, "fileurl:" + fileurl);
					if (!TextUtils.isEmpty(fileurl)) {
						File file = new File(fileurl);
						if (file.exists()) {
							BDAutoUpdateSDK.cpUpdateInstall(mContext.getApplicationContext(), SharePerfenceUtil.getApkUrlPath(mContext));
							MyActivityManager.create().finishAllActivity();

						} else {
							gotoDownLoad(info);
						}
					} else {
						gotoDownLoad(info);
					}
				} catch (Exception e) {
				}
			} else {
				if (isTishi) {
					ToastUtils.toast(mContext, "已经是最新版本，无需更新");
					//txt_log.setText(txt_log.getText() + "\n no update.");
				}
			}
		}
	}

	private class UpdateDownloadCallback implements CPUpdateDownloadCallback {
		@Override
		public void onDownloadComplete(String apkPath) {
			if (nm != null) {
				nm.cancel(notification_id);
			}
			if (mDialog2 != null) {
				cancelDailog();
			}

			if (mContext != null) {
				SharePerfenceUtil.saveApkUrlPath(mContext, apkPath);
				//txt_log.setText(txt_log.getText() + "\n onDownloadComplete: " + apkPath);
				BDAutoUpdateSDK.cpUpdateInstall(mContext.getApplicationContext(), apkPath);
			}
			MyActivityManager.create().finishAllActivity();

		}

		@Override
		public void onStart() {
			//txt_log.setText(txt_log.getText() + "\n Download onStart");
		}

		@Override
		public void onPercent(int percent, long rcvLen, long fileSize) {
			//txt_log.setText(txt_log.getText() + "\n Download onPercent: " + percent + "%");
			if ( Tools.isConnectToWifi(mContext)) {
				notification.contentView.setProgressBar(R.id.pb, 100, percent, false);
				showNotification();//更新notification,就是更新进度条
			} else {
				Message msg = new Message();
				msg.what = 0x111;
				msg.obj = percent;
				mHandler.sendMessage(msg);
			}
		}

		@Override
		public void onFail(Throwable error, String content) {
			//txt_log.setText(txt_log.getText() + "\n Download onFail: " + content);
			if (nm != null) {
				nm.cancel(notification_id);
			}
		}

		@Override
		public void onStop() {
			// txt_log.setText(txt_log.getText() + "\n Download onStop");
			if (nm != null) {
				nm.cancel(notification_id);
			}
		}
	}

	private class MyUICheckUpdateCallback implements UICheckUpdateCallback {
		@Override
		public void onCheckComplete() {
		}
	}

	public void showNotification() {
		nm.notify(notification_id, notification);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0x111) {
				if (!TextUtils.isEmpty(msg.obj + "") && progressBar != null) {
					progressBar.setProgress((int) msg.obj);
					percentTv.setText((int) msg.obj + "/100");
				}

			}
		}
	};

}
