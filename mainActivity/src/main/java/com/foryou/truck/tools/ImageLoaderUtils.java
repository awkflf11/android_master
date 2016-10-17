package com.foryou.truck.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.style.IconMarginSpan;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;

/**
 * @des:
 */
public class ImageLoaderUtils {
	private String TAG = "ImageLoaderUtils";
	private Context mContext;
	private static ImageLoaderUtils imageLoaderUtils;
	private static ImageLoader imageLoader;
	private DisplayImageOptions options;

	private ImageLoaderUtils() {
	}

	public static ImageLoaderUtils getImageLoaderUtils() {
        if(imageLoaderUtils==null){
			imageLoaderUtils=new ImageLoaderUtils();
		}
		return imageLoaderUtils;
	}

	public static ImageLoader getImageLoader() {
		imageLoader=ImageLoader.getInstance();
		return imageLoader;
	}

	public  DisplayImageOptions setOptions (int defaultPic,int roundDegree ) {
		options = new DisplayImageOptions.Builder()
				.showStubImage(defaultPic)// 在ImageView加载过程中显示图片
				.showImageForEmptyUri(defaultPic)
				.showImageOnFail(defaultPic)
				.cacheInMemory(true) // 加载图片时会在内存中加载缓存
				.cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
				.displayer(new RoundedBitmapDisplayer(roundDegree))
				.build();
		return options;
	}







}
