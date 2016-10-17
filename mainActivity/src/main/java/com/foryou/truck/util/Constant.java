package com.foryou.truck.util;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.foryou.truck.entity.UserDetailEntity;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Constant {
	public static String KEY_LOGIN = "key_login";

	public static final String APP_ID = "wxdb192353a1928828";
	public static final String MCODE = "74:0E:D0:BF:F6:31:81:36:48:6D:E7:2E:5C:D3:ED:96:BB:9A:C0:0E;com.foryou.truck";
	public static final String AK = "n1q7WoDXoEZK43d4ywvQP3Hm";
	public static final int ZHENG_CHE = 1;
	public static final int PIN_CHE = 2;
	public static final String GT_CID_CHANGED_BROADCASE = "com.android.foryou.truck.GT_CID";
	// public static final String SHARE_URL =
	// "http://eqxiu.com/s/rdym0d7w?eqrcode=1&from=finglemessage&isappinstalled=0";
	public static final String SHARE_URL = "http://mp.weixin.qq.com/s?__biz=MzAxNzI0NDEwNw==&mid=209490713&idx=1&sn=c56a6b11ae02fa627864c069cd41ca3b#rd";
	public static int GET_SENDER_CODE = 200;
	public static int GET_RECEIVER_CODE = 201;
	public static int GET_BAOXIAN_CODE = 202;
	public static int GET_COUPON_CODE = 203;
	public static int AGREEMENT_CODE = 204;
	public static int NO_BAOXIAN_CODE = 205;
	public static int NO_DISCOUNT_CODE = 206;
	public final static String PHONE_NUMBER = "400-8008-577";
	public final static int PAGE_SIZE = 5;

	private static final String TAG = "Constant";

	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		// ByteArrayOutputStream output = new ByteArrayOutputStream();
		// bmp.compress(CompressFormat.PNG, 100, output);
		// if (needRecycle) {
		// bmp.recycle();
		// }
		//
		// byte[] result = output.toByteArray();
		// try {
		// output.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		int i;
		int j;
		if (bmp.getHeight() > bmp.getWidth()) {
			i = bmp.getWidth();
			j = bmp.getWidth();
		} else {
			i = bmp.getHeight();
			j = bmp.getHeight();
		}

		Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
		Canvas localCanvas = new Canvas(localBitmap);

		while (true) {
			localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0, i,
					j), null);
			if (needRecycle)
				bmp.recycle();
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
					localByteArrayOutputStream);
			localBitmap.recycle();
			byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
			try {
				localByteArrayOutputStream.close();
				return arrayOfByte;
			} catch (Exception e) {
				// F.out(e);
			}
			i = bmp.getHeight();
			j = bmp.getHeight();
		}

		// return result;
	}

	public static byte[] getHtmlByteArray(final String url) {
		URL htmlUrl = null;
		InputStream inStream = null;
		try {
			htmlUrl = new URL(url);
			URLConnection connection = htmlUrl.openConnection();
			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				inStream = httpConnection.getInputStream();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = inputStreamToByte(inStream);

		return data;
	}

	public static byte[] inputStreamToByte(InputStream is) {
		try {
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			int ch;
			while ((ch = is.read()) != -1) {
				bytestream.write(ch);
			}
			byte imgdata[] = bytestream.toByteArray();
			bytestream.close();
			return imgdata;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] readFromFile(String fileName, int offset, int len) {
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);
		if (!file.exists()) {
			Log.i(TAG, "readFromFile: file not found");
			return null;
		}

		if (len == -1) {
			len = (int) file.length();
		}

		Log.d(TAG, "readFromFile : offset = " + offset + " len = " + len
				+ " offset + len = " + (offset + len));

		if (offset < 0) {
			Log.e(TAG, "readFromFile invalid offset:" + offset);
			return null;
		}
		if (len <= 0) {
			Log.e(TAG, "readFromFile invalid len:" + len);
			return null;
		}
		if (offset + len > (int) file.length()) {
			Log.e(TAG, "readFromFile invalid file len:" + file.length());
			return null;
		}

		byte[] b = null;
		try {
			RandomAccessFile in = new RandomAccessFile(fileName, "r");
			b = new byte[len]; // ���������ļ���С������
			in.seek(offset);
			in.readFully(b);
			in.close();

		} catch (Exception e) {
			Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
			e.printStackTrace();
		}
		return b;
	}

	private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

	public static Bitmap extractThumbNail(final String path, final int height,
			final int width, final boolean crop) {
		Assert.assertTrue(path != null && !path.equals("") && height > 0
				&& width > 0);

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			Log.d(TAG, "extractThumbNail: round=" + width + "x" + height
					+ ", crop=" + crop);
			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = "
					+ beY);
			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY)
					: (beY < beX ? beX : beY));
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (crop) {
				if (beY > beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			} else {
				if (beY < beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			}

			options.inJustDecodeBounds = false;

			Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight
					+ ", orig=" + options.outWidth + "x" + options.outHeight
					+ ", sample=" + options.inSampleSize);
			Bitmap bm = BitmapFactory.decodeFile(path, options);
			if (bm == null) {
				Log.e(TAG, "bitmap decode failed");
				return null;
			}

			Log.i(TAG,
					"bitmap decoded size=" + bm.getWidth() + "x"
							+ bm.getHeight());
			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth,
					newHeight, true);
			if (scale != null) {
				bm.recycle();
				bm = scale;
			}

			if (crop) {
				final Bitmap cropped = Bitmap.createBitmap(bm,
						(bm.getWidth() - width) >> 1,
						(bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}

				bm.recycle();
				bm = cropped;
				Log.i(TAG,
						"bitmap croped size=" + bm.getWidth() + "x"
								+ bm.getHeight());
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			Log.e(TAG, "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}

	// public static void GotoDialPage(Context content, String num) {
	// Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// content.startActivity(intent);
	// }

	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean needPayOnLine(Context mContext, String pay_type) {
		// if(!pay_type.equals("1")){
		// return false;
		// }

		UserDetailEntity mUserDetail = SharePerfenceUtil
				.getUserDetail(mContext);
		return mUserDetail.data.type.equals("0");
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void GotoDialPage(final Context context, final String telnum) {
		try {
			AlertDialog.Builder build;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				build = new AlertDialog.Builder(context,
						AlertDialog.THEME_HOLO_LIGHT);
			} else {
				build = new AlertDialog.Builder(context);
			}
			build.setMessage("确定拨打" + telnum + "?")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Intent.ACTION_DIAL, Uri
									.parse("tel:" + telnum));
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(intent);
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).create().show();
		}catch (Exception e){
			UtilsLog.i(TAG,"打电话的电话号码格式不对");
		}

	}

	public static boolean isContractUser(Context mContext){
		UserDetailEntity entity = SharePerfenceUtil.getUserDetail(mContext);
		if(entity == null){
			return false;
		}

		if(entity.data.type.equals("1")){
			return true;
		}
		return false;
	}
}
