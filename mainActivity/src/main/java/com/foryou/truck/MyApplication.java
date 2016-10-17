package com.foryou.truck;


import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.foryou.truck.net.Apn;
import com.foryou.truck.util.CrashHandler;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MyApplication extends Application {
	public static String MYCITY = "";
	private String TAG = "MyApplication";
	private static MyApplication instance;
	// Volley request queue
	private RequestQueue mRequestQueue;

	public static void initImageLoader(Context context) {
		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);

		MemoryCacheAware<String, Bitmap> memoryCache;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			memoryCache = new LruMemoryCache(memoryCacheSize);
		} else {
			memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
		}
		// 初始化sd卡缓存和内存缓存
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(false).cacheOnDisc(true).build();

		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).defaultDisplayImageOptions(defaultOptions)
				.memoryCacheExtraOptions(480, 800)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)// Not
																// necessary
																// in
																// common
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

	}

	public static Context getSelf() {
		return instance;
	}

	private RefWatcher refWatcher;
	public static RefWatcher getRefWatcher(Context context) {
		MyApplication application = (MyApplication) context.getApplicationContext();
		return application.refWatcher;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "MyApplication onCreate .........");
		instance = (MyApplication) getApplicationContext();
		if (UtilsLog.isTest) {
			initCrash();
			UrlConstant.BASE_URL = SharePerfenceUtil.getUrlPath(this);
		}
		initImageLoader(instance);
		SDKInitializer.initialize(getApplicationContext());
		Apn.init();

		refWatcher = LeakCanary.install(this);
	}

	private void initCrash() {
		// 处理全局异常：
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		// 发送以前没发送的报告(可选)
		crashHandler.sendPreviousReportsToServer();
	}

	/**
	 * Returns the application instance
	 *
	 * @return the application instance
	 */
	public static MyApplication getInstance() {
		return instance;
	}

	/**
	 * Returns a Volley request queue for creating network requests
	 *
	 * @return {@link com.android.volley.RequestQueue}
	 */
	public RequestQueue getVolleyRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(this);
		}
		return mRequestQueue;
	}

	/**
	 * Adds a request to the Volley request queue
	 *
	 * @param request
	 *            is the request to add to the Volley queue
	 */
	private static void addRequest(Request<?> request) {
		getInstance().getVolleyRequestQueue().add(request);
	}

	/**
	 * Adds a request to the Volley request queue with a given tag
	 *
	 * @param request
	 *            is the request to be added
	 * @param tag
	 *            tag identifying the request
	 */
	public void addRequest(Request<?> request, String tag) {
		request.setTag(tag);
		request.setShouldCache(true);
		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		addRequest(request);
	}

	/**
	 * Cancels all the request in the Volley queue for a given tag
	 *
	 * @param tag
	 *            associated with the Volley requests to be cancelled
	 */
	public void cancelAllRequests(String tag) {
		if (getInstance().getVolleyRequestQueue() != null) {
			getInstance().getVolleyRequestQueue().cancelAll(tag);
		}
	}

}
