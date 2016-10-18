package com.foryou.truck.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.foryou.truck.MyApplication;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.util.UtilsLog;

public class MLHttpConnect2 {
	private static int versionCode = -1;
	public static final int SUCCESS = 200;
	public static final int FAILED = SUCCESS + 1;

	public static final String USE_CASH = "USE_CASH";

	/**
	 * 调试接口用，直接获取String
	 * 
	 * @param url
	 * @param parmas
	 * @return
	 */
	public static String getData(final String url,
			final Map<String, String> pramas) {

		String urlString = url + MLNetManager.buildUrl(pramas);
		return getHttpData(urlString);
	}

	/**
	 * 
	 * 默认使用缓存及applicationContext
	 * 
	 * @param context
	 *            上下文菜单
	 * @param url
	 *            url
	 * @param parmas
	 *            参数列表
	 * @param jsonParser
	 *            解析器
	 * @param handler
	 */
	public static void getData(final String url,
			final Map<String, String> parmas, final BaseJsonParser jsonParser,
			final Handler handler) {
		getData(MyApplication.getSelf(), url, parmas, jsonParser, handler);
	}

	/***
	 * post方式
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static void getHttpPostData(final Context context, final String url,
			final Map<String, String> parmas, final BaseJsonParser jsonParser,
			final Handler handler) {
		// String result = null;
		// try {
		// result = HttpApi.getString(url,map);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String urlString = url;
				UtilsLog.i("url", "url:" + urlString);

				String mStringresult = "";
				try {
					mStringresult = HttpApi.getString(url, parmas);
				} catch (Exception e) {
					e.printStackTrace();
				}
				UtilsLog.i("url", "mStringresult:" + mStringresult);

				if (!TextUtils.isEmpty(mStringresult)) {
					int result = jsonParser.parser(mStringresult);
					if (result == 1) {
						Message msg = new Message();
						msg.what = SUCCESS;
						msg.obj = mStringresult;
						handler.sendMessage(msg);
						writeCache(urlString, mStringresult, context);
						return;
					} else {
						Toast.makeText(context, "解析错误1", Toast.LENGTH_SHORT)
								.show();
						handler.sendEmptyMessage(FAILED);
					}
				} else {
					handler.sendEmptyMessage(FAILED);
				}

			}

		}).start();

	}

	public static Message getHttpPostData2(final Context context,
			final String url, final Map<String, String> parmas,
			final BaseJsonParser jsonParser) {
		// String result = null;
		// try {
		// result = HttpApi.getString(url,map);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		Message msg = new Message();
		msg.what = FAILED;
		msg.obj = "";

		// TODO Auto-generated method stub

		String urlString = url;
		UtilsLog.i("url", "url:" + urlString);

		String mStringresult = "";
		try {
			mStringresult = HttpApi.getString(url, parmas);
		} catch (Exception e) {
			e.printStackTrace();
		}
		UtilsLog.i("url", "mStringresult:" + mStringresult);

		if (!TextUtils.isEmpty(mStringresult)) {
			int result = jsonParser.parser(mStringresult);
			if (result == 1) {

				msg.what = SUCCESS;
				msg.obj = mStringresult;
				writeCache(urlString, mStringresult, context);
				return msg;
			} else {
				Toast.makeText(context, "解析错误1", Toast.LENGTH_SHORT).show();
				// handler.sendEmptyMessage(FAILED);
				return msg;
			}
		} else {
			// handler.sendEmptyMessage(FAILED);
			return msg;
		}
	}

	/****
	 * 
	 * @param context
	 *            上下文菜单
	 * @param url
	 *            url
	 * @param parmas
	 *            参数列表
	 * @param jsonParser
	 *            解析器
	 * @param handler
	 *            UI回调函数
	 * 
	 * @param useCash
	 *            true的时候 直接读取缓存数据 数据为空就去联网请求数据 false的时候 直接联网请求数据 数据为空就去读取缓存数据
	 * 
	 */
	public static void getData(final Context context, final String url,
			final Map<String, String> parmas, final BaseJsonParser jsonParser,
			final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// Looper.prepare();
				try {
					if (versionCode < 0) {
						versionCode = context.getPackageManager()
								.getPackageInfo(context.getPackageName(),
										PackageManager.GET_CONFIGURATIONS).versionCode;
					}
				} catch (NameNotFoundException e1) {
					e1.printStackTrace();
				}
				parmas.put("version", versionCode + "");

				boolean useCash = true;
				if (parmas.containsKey(USE_CASH)) {
					useCash = parmas.get(USE_CASH).equals("false") ? false
							: true;
					parmas.remove(USE_CASH);
				}

				String urlString = url + MLNetManager.buildUrl(parmas);
				UtilsLog.i("url", "url:" + urlString);

				String mStringresult = "";
				mStringresult = getHttpData(urlString);
				if (!TextUtils.isEmpty(mStringresult)) {
					int result = jsonParser.parser(mStringresult);
					if (result == 1) {
						Message msg = new Message();
						msg.what = SUCCESS;
						msg.obj = mStringresult;
						handler.sendMessage(msg);
						writeCache(urlString, mStringresult, context);
						return;
					} else {
						Toast.makeText(context, "解析错误1", Toast.LENGTH_SHORT)
								.show();
						handler.sendEmptyMessage(FAILED);
					}
				} else {
					if (useCash) {
						mStringresult = readCacheDirectly(urlString, context);
						if (mStringresult != null
								&& !TextUtils.isEmpty(mStringresult)) {

							int result = jsonParser.parser(mStringresult);
							if (result == 1) {
								Message msg = new Message();
								msg.what = SUCCESS;
								msg.obj = mStringresult;
								handler.sendMessage(msg);
								return;
							} else {
								Toast.makeText(context, "解析错误2",
										Toast.LENGTH_SHORT).show();
								handler.sendEmptyMessage(FAILED);
							}
						} else {
							handler.sendEmptyMessage(FAILED);
						}
					}
				}
				// Looper.loop();

			}

		}).start();
	}

	public static Message getData2(final Context context, final String url,
			final Map<String, String> parmas, final BaseJsonParser jsonParser) {
		Message msg = new Message();
		msg.what = FAILED;
		msg.obj = "";
		// TODO Auto-generated method stub
		// Looper.prepare();
		try {
			if (versionCode < 0) {
				versionCode = context.getPackageManager().getPackageInfo(
						context.getPackageName(),
						PackageManager.GET_CONFIGURATIONS).versionCode;
			}
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		parmas.put("version", versionCode + "");

		boolean useCash = true;
		if (parmas.containsKey(USE_CASH)) {
			useCash = parmas.get(USE_CASH).equals("false") ? false : true;
			parmas.remove(USE_CASH);
		}

		String urlString = url + MLNetManager.buildUrl(parmas);
		UtilsLog.i("url", "url:" + urlString);

		String mStringresult = "";
		mStringresult = getHttpData(urlString);
		if (!TextUtils.isEmpty(mStringresult)) {
			int result = jsonParser.parser(mStringresult);
			if (result == 1) {
				msg.what = SUCCESS;
				msg.obj = mStringresult;
				writeCache(urlString, mStringresult, context);
				return msg;
			} else {
				// Toast.makeText(context, "解析错误1", Toast.LENGTH_SHORT).show();
				UtilsLog.i("url", "解析错误1");
				return msg;
			}
		} else {
			if (useCash) {
				mStringresult = readCacheDirectly(urlString, context);
				if (mStringresult != null && !TextUtils.isEmpty(mStringresult)) {

					int result = jsonParser.parser(mStringresult);
					if (result == 1) {
						msg.what = SUCCESS;
						msg.obj = mStringresult;
						return msg;
					} else {
						// Toast.makeText(context, "解析错误2", Toast.LENGTH_SHORT)
						// .show();
						UtilsLog.i("url", "解析错误2");
						return msg;
					}
				} else {
					return msg;
				}
			}
			return msg;
		}
	}

	public static String getHttpData(String url) {
		String result = null;
		try {
			result = HttpApi.getString(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 写入本地缓存
	 * 
	 * @param url
	 *            数据链接地址
	 * @param content
	 *            存入的内容
	 * @param context
	 *            Context环境
	 */
	public static void writeCache(String url, String content, Context context) {
		FileOutputStream fosContent = null;
		SDCardCashEntity entity;
		ObjectOutputStream oos = null;
		try {
			// 存放缓存内容
			fosContent = context.openFileOutput(getMD5(url),
					Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fosContent);

			entity = new SDCardCashEntity();
			entity.setData(content);
			entity.setTime(System.currentTimeMillis());
			entity.setUrl(url);
			oos.writeObject(entity);
			oos.flush();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fosContent != null) {
					fosContent.close();
					fosContent = null;
				}
				if (oos != null) {
					oos.close();
					oos = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static final long maxCashTime = 5 * 24 * 60 * 60 * 1000;// 最大缓存时间5天

	/**
	 * 直接从本地读取
	 * 
	 * @param url
	 * @param context
	 * @return 缓存数据
	 */
	public static String readCacheDirectly(String url, Context context) {
		String k = getMD5(url);
		String contentfileName = context.getFilesDir() + java.io.File.separator
				+ k;
		String sb2 = null;
		InputStream is = null;
		ObjectInputStream ois = null;
		SDCardCashEntity entity;
		try {
			File contentFile = new File(contentfileName);
			if (!contentFile.exists()) {
				return null;
			}
			is = new FileInputStream(contentFile);
			ois = new ObjectInputStream(is);
			entity = (SDCardCashEntity) ois.readObject();
			if (entity == null || TextUtils.isEmpty(entity.data)) {
				return null;
			}
			if (System.currentTimeMillis() - entity.getTime() < maxCashTime) {
				return entity.data;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
				if (ois != null) {
					ois.close();
					ois = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb2;
	}

	/**
	 * 取得字符串MD5加密后内容
	 * 
	 * @param s
	 * @return
	 */
	public final static String getMD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
