package com.foryou.truck.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.foryou.truck.tools.SdCardUtil;

/**
 * @UncaughtException处理类
 * @"当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告. 请在/sdcard/acrash/： 目录下查找错误的文件 ：
 * @des:
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";

	/**
	 * @是否开启日志输出,在Debug状态下开启, 在Release状态下关闭以提示程序性能
	 * */
	public static final boolean DEBUG = true;

	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	// CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();

	// 程序的Context对象
	private Context mContext;

	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/** 错误报告文件的扩展名 */
	private static final String CRASH_REPORTER_EXTENSION = ".log";

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				// 休息的时间
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			// 退出程序
			// android.os.Process.killProcess(android.os.Process.myPid());
			// System.exit(1);
			// ExitApplication.getInstance().exit(mContext);
			// #######
			// MApplication application = (MApplication)
			// mContext.getApplicationContext();
			// application.exit();
			// MyActivityManager.create().AppExit(mContext);

		}
	}

	/**
	 * @自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				// Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.",Toast.LENGTH_SHORT).show();
				Toast.makeText(mContext, "系统繁忙，请稍后再试", Toast.LENGTH_SHORT).show();

				Looper.loop();
			}
		}.start();
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		String crashFileName = saveCrashInfo2File(ex);

		// 发送错误报告到服务器
		sendCrashReportsToServer(mContext);
		return true;
	}

	/**
	 * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
	 */
	public void sendPreviousReportsToServer() {
		sendCrashReportsToServer(mContext);
	}

	/**
	 * 把错误报告发送给服务器,包含新产生的和以前没发送的.
	 * 
	 * @param ctx
	 */
	private void sendCrashReportsToServer(Context ctx) {
		String[] crFiles = getCrashReportFiles(ctx);
		if (crFiles != null && crFiles.length >= 0) {
			TreeSet<String> sortedFiles = new TreeSet<String>();
			sortedFiles.addAll(Arrays.asList(crFiles));

			for (String fileName : sortedFiles) {
				// File cr = new File(ctx.getFilesDir(), fileName);
				//String path = "/sdcard/acrash/";
				String path = SdCardUtil.getSDCardPath(mContext) +"acrash/";
				File filesDir = new File(path);
				File cr = new File(filesDir, fileName);
				postReport(cr);
				// cr.delete();// 删除已发送的报告
			}
		}
	}

	private void postReport(File file) {
		// TODO 使用HTTP Post 发送错误报告到服务器
		// 这里不再详述,开发者可以根据OPhoneSDN上的其他网络操作
		// 教程来提交错误报告
	}

	/**
	 * 获取错误报告文件名
	 * 
	 * @param ctx
	 * @return
	 */
	private String[] getCrashReportFiles(Context ctx) {
		// File filesDir = ctx.getFilesDir();//之前的代码
		//String path = "/sdcard/acrash/";//
		String path = SdCardUtil.getSDCardPath(mContext) +"acrash/";
		File filesDir = new File(path);

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(CRASH_REPORTER_EXTENSION);
			}
		};
		return filesDir.list(filter);
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				// Log.d(TAG, field.getName() + " : " + field.get(null)); 之前的
				// 修改的，在Debug状态下开启, 在Release状态下关闭以提示程序性能
				if (DEBUG) {
					Log.d(TAG, field.getName() + " : " + field.get(null));
				}
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp + CRASH_REPORTER_EXTENSION;
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				//String path = "/sdcard/acrash/";//
				String path = SdCardUtil.getSDCardPath(mContext) +"acrash/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}
}
