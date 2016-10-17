package com.foryou.truck.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.foryou.truck.util.UtilsLog;

/**
 * 网络请求工具类
 * 
 * @author
 * 
 */
public class HttpApi {

	// private static NetManager manager;

	//
	// static {
	// manager = new NetManager();
	// }

	/**
	 * 通过网络获取返回的内容（为字符串）
	 * 
	 * @param methodName
	 * @param pairs
	 * @return
	 * @throws Exception
	 */
	public static String getString(Map<String, String> pairs) throws Exception {
		MLNetManager manager = new MLNetManager();
		return manager.getContentByString(manager.HandlerRequest(pairs));
	}
	/**
	 * 通过网络获取返回的内容（为字符串）
	 * 
	 * @param methodName
	 * @param pairs
	 * @return
	 * @throws Exception
	 */
	public static String getString(String url) throws Exception {
		MLNetManager manager = new MLNetManager();
		return manager.getContentByString(manager.HandlerRequest(url));
	}
	
	/**
	 * 通过网络获取返回的内容（为字符串）
	 * 
	 * @param methodName
	 * @param pairs
	 * @return
	 * @throws Exception
	 */
	public static String getString(String url,Map<String, String> pairs) throws Exception {
		MLNetManager manager = new MLNetManager();
		return manager.getContentByString(manager.HandlerRequest(url,pairs,1));
	}
	
	/**
	 * 通过网络获取返回的流
	 * 
	 * @param methodName
	 * @param pairs
	 * @return
	 * @throws Exception
	 */
	public static InputStream getInputStream(Map<String, String> pairs) {
		MLNetManager manager = new MLNetManager();
		return manager.HandlerRequest(pairs);
	}

	/**
	 * 通过网络获取返回的实体bean
	 * 
	 * @param methodName
	 * @param pairs
	 * @return
	 * @throws Exception
	 */
	public static <T> T getBeanByPullXml(Map<String, String> pairs,
			Class<T> clazz) throws Exception {
		MLNetManager manager = null;
		try {
			manager = new MLNetManager();
			InputStream xml = manager.HandlerRequest(pairs);
			if (xml == null)
				return null;
			return null;
		} finally {
			manager.close();
		}
	}

	/**
	 * 通过网络获取返回的实体bean集合
	 * 
	 * @param methodName
	 * @param pairs
	 * @return
	 * @throws Exception
	 */
	public static <T> ArrayList<T> getListByPullXml(Map<String, String> pairs,
			String root, Class<T> clazz) throws Exception {
		MLNetManager manager = null;
		try {
			manager = new MLNetManager();
			InputStream xml = manager.HandlerRequest(pairs);
			if (xml == null)
				return null;
			return null;
		} finally {
			manager.close();
		}
	}

	
	
	/**
	 * 上传图片
	 * 
	 * @param filePath
	 *            图片地址
	 * @param username
	 * @return
	 */
	public static String uploadFile(String filePath) {

		String result = null;
		BufferedReader rd = null;
		URL u = null;
		URLConnection conn = null;
		OutputStream os = null;
		try {
			String url = NetConstants.HTTP_URL + "UploadPhoto";
			UtilsLog.e("url", url);
			u = new URL(url);
			conn = u.openConnection();
			conn.setReadTimeout(15 * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			if (filePath != null) {
				os = conn.getOutputStream();
			}
			FileInputStream fis = new FileInputStream(filePath);
			byte[] buffer = new byte[4096];
			int length = -1;

			while ((length = fis.read(buffer)) != -1) {
				os.write(buffer, 0, length);
			}

			rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			StringBuffer strBuffer = new StringBuffer();
			String rn = System.getProperty("line.separator");
			while ((result = rd.readLine()) != null) {
				strBuffer.append(result + rn);
			}
			result = strBuffer.toString();
			strBuffer.delete(0, strBuffer.length());
			if (fis != null)
				fis.close();
			if (rd != null)
				rd.close();
			if (os != null)
				os.close();
			conn = null;
			u = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result != null) {
			result = result.replaceAll("<.*?>", "").trim();
			result = result.replaceAll("[%]amp", "&");
		}
		return result;
	}

	/**
	 * 上传图片
	 * 
	 * @param filePath
	 *            图片地址
	 * @param username
	 * @return
	 */
	public static String uploadFile(Map<String, String> map, String filePath) {
		String result = null;
		BufferedReader rd = null;
		URL u = null;
		URLConnection conn = null;
		OutputStream os = null;
		try {
			String url = NetConstants.HTTP_URL + "NhPhoto";
			UtilsLog.e("url", url);
			u = new URL(url);
			conn = u.openConnection();
			conn.setReadTimeout(15 * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			for (Entry<String, String> entry : map.entrySet()) {
				String value = entry.getValue();
				if (!TextUtils.isEmpty(value)) {
					value = URLEncoder.encode(value, MLNetManager.ENCODING);
				}
				conn.addRequestProperty(entry.getKey(), value);
			}

			if (filePath != null) {
				os = conn.getOutputStream();
			}
			FileInputStream fis = new FileInputStream(filePath);
			byte[] buffer = new byte[4096];
			int length = -1;

			while ((length = fis.read(buffer)) != -1) {
				os.write(buffer, 0, length);
			}

			rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			StringBuffer strBuffer = new StringBuffer();
			String rn = System.getProperty("line.separator");
			while ((result = rd.readLine()) != null) {
				strBuffer.append(result + rn);
			}
			result = strBuffer.toString();
			strBuffer.delete(0, strBuffer.length());
			if (fis != null)
				fis.close();
			if (rd != null)
				rd.close();
			if (os != null)
				os.close();
			conn = null;
			u = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result != null) {
			result = result.replaceAll("<.*?>", "").trim();
			result = result.replaceAll("[%]amp", "&");
		}
		return result;

	}

	/**
	 * 上传大图片，先压缩后再上传
	 * 
	 * @param filePath
	 *            图片地址
	 * @param username
	 * @param isZip
	 *            是否压缩
	 * @return
	 */
	public static String uploadFile(Map<String, String> map, String filePath,
			boolean isZip) {
		String result = null;
		if (!TextUtils.isEmpty(filePath)) {
			BufferedReader rd = null;
			URL u = null;
			URLConnection conn = null;
			OutputStream os = null;
			FileInputStream fis = null;
			try {
				String url = NetConstants.HTTP_URL + "NhPhoto";
				UtilsLog.e("url", url);
				u = new URL(url);
				conn = u.openConnection();
				conn.setReadTimeout(15 * 1000);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);

				for (Entry<String, String> entry : map.entrySet()) {
					String value = entry.getValue();
					if (!TextUtils.isEmpty(value)) {
						value = URLEncoder.encode(value, MLNetManager.ENCODING);
					}
					conn.addRequestProperty(entry.getKey(), value);
				}
				os = conn.getOutputStream();

				if (isZip) {
					BitmapFactory.Options opts = new BitmapFactory.Options();
					File file = new File(filePath);
					if (file.length() < 524288) { // 1-512k
						opts.inSampleSize = 1;
					} else if (file.length() < 1048576) { // 500-1024k
						opts.inSampleSize = 2;
					} else if (file.length() < 2097152) { // 1M-2M
						opts.inSampleSize = 3;
					} else {
						opts.inSampleSize = 4;
					}
					Bitmap bitmap = BitmapFactory.decodeFile(filePath, opts);
					if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)) {
						os.flush();
					}
				} else {
					fis = new FileInputStream(filePath);
					byte[] buffer = new byte[4096];
					int length = -1;
					while ((length = fis.read(buffer)) != -1) {
						os.write(buffer, 0, length);
					}
				}

				rd = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "UTF-8"));
				StringBuffer strBuffer = new StringBuffer();
				String rn = System.getProperty("line.separator");
				while ((result = rd.readLine()) != null) {
					strBuffer.append(result + rn);
				}
				result = strBuffer.toString();
				strBuffer.delete(0, strBuffer.length());
				if (fis != null)
					fis.close();
				if (rd != null)
					rd.close();
				if (os != null)
					os.close();
				conn = null;
				u = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (result != null) {
				result = result.replaceAll("<.*?>", "").trim();
				result = result.replaceAll("[%]amp", "&");
			}
		}
		return result;
	}

}