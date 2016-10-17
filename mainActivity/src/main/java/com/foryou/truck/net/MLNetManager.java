package com.foryou.truck.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;

import com.foryou.truck.util.UtilsLog;

/**
 * 网络管理类（需要可扩展）
 * 
 * @author
 * 
 */
public class MLNetManager implements NetConstants {
	private HttpClient client;

	public MLNetManager() {
		client = HttpClientFactory.getHttpClient();
	}

	/**
	 * 网络请求入口
	 * 
	 * @param method
	 *            jsp地址
	 * @param map
	 *            参数键值
	 * @return
	 */
	public InputStream HandlerRequest(Map<String, String> map) {
		return HandlerRequest(METHOD, map);
	}

	/**
	 * 网络请求入口
	 * 
	 * @param method
	 *            jsp地址
	 * @param map
	 *            参数键值
	 * @return
	 */
	public InputStream HandlerRequest(String method, Map<String, String> map) {
		if (TextUtils.isEmpty(method)) {
			return null;
		}
		if (!method.startsWith("http")) {
			method = HTTP_URL + method;
		}
		if (HTTP_METHOD) {
			method = method + buildUrl(map);
			return createGetRequest(method);
		} else {
			return createPostRequest(method, map);
		}
	}

	/**
	 * 网络请求入口
	 * 
	 * @param url
	 *            地址
	 * @return
	 */
	public InputStream HandlerRequest(String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		if (HTTP_METHOD) {
			return createGetRequest(url);
		} else {
			return createPostRequest(url, null);
		}
	}


	/**
	 * get方式拼接url地址
	 */
	public static String buildUrl(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		String value;
		try {
			if (map != null && map.size() > 0) {
				sb.append("?");
				for (Entry<String, String> entry : map.entrySet()) {
					value = "";
					value = entry.getValue();
					if (TextUtils.isEmpty(value)) {
						continue;
					} else {
						value = URLEncoder.encode(value, ENCODING);
					}
					sb.append(entry.getKey()).append("=").append(value)
							.append("&");
				}
				sb.deleteCharAt(sb.length() - 1);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// if (!StringUtils.isNullOrEmpty(sb.toString())
		// || sb.toString().length() > 0) {
		// String str = sb.toString().substring(1, sb.toString().length());
		// str = StringUtils.getMD5Str(str + MD);
		// sb.append("&wirelesscode=" + str);
		// }
		return sb.toString();
	}
	
	/**
	 * 网络请求入口
	 * 
	 * @param url
	 *            地址
	 * @return
	 */
	public InputStream HandlerRequest(String url,Map<String, String> map,int type) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		return createPostRequest(url, map);
	}
	

	/**
	 * post方式封装键值
	 */
	public UrlEncodedFormEntity buildFormEntity(Map<String, String> map) {
		try {
			String value;
			if (map.size() > 0) {
				// String meString = map.get(SoufunConstants.MWSSAGE_NAME);
				// String wirelesscode = StringUtils.getMD5Str(meString + MD);
				// map.put("wirelesscode", wirelesscode);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				for (Entry<String, String> entry : map.entrySet()) {
					value = "";
					value = entry.getValue();
					if (TextUtils.isEmpty(value)) {
						continue;
					}
					UtilsLog.i("aa","entry.getKey:"+entry.getKey()+",value:"+value);
					list.add(new BasicNameValuePair(entry.getKey(), value));

				}
				return new UrlEncodedFormEntity(list, HTTP.UTF_8);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得Get方式请求
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public InputStream createGetRequest(String url) {
		if (TextUtils.isEmpty(url))
			return null;
		HttpGet httpGet = new HttpGet(url);
		try {
			for (Entry<String, String> entry : Apn.getHeads().entrySet()) {
				String value = entry.getValue();
				if (!TextUtils.isEmpty(value)) {
					value = URLEncoder.encode(value, ENCODING);
				}
				httpGet.addHeader(entry.getKey(), value);
				UtilsLog.i("head", entry.getKey() + "---"+value);
			}
			HttpResponse response = client.execute(httpGet);
			switch (response.getStatusLine().getStatusCode()) {
			case HttpStatus.SC_OK:
				return getUngzippedContent(response.getEntity());
			default:
				httpGet.abort();
				return null;
			}
		} catch (Exception e) {
			httpGet.abort();
			e.printStackTrace();
			throw new MLNetException(e.getMessage(), e);
		}
	}

	/**
	 * 获得Post方式请求
	 * 
	 * @param url
	 * @param map
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public InputStream createPostRequest(String url, Map<String, String> map) {
		if (TextUtils.isEmpty(url))
			return null;

		HttpPost httpPost = new HttpPost(url);
		try {

			for (Entry<String, String> entry : Apn.getHeads().entrySet()) {
				String value = entry.getValue();
				if (!TextUtils.isEmpty(value)) {
					value = URLEncoder.encode(value, ENCODING);
				}
				httpPost.addHeader(entry.getKey(), value);
				// UtilsLog.e("header", entry.getKey() + " = " +
				// entry.getValue());
			}

			UrlEncodedFormEntity buildFormEntity = buildFormEntity(map);
			if (buildFormEntity != null)
				httpPost.setEntity(buildFormEntity);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			HttpResponse response = client.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			UtilsLog.i("aa","statusCode:"+statusCode);
			switch (statusCode) {
			case HttpStatus.SC_OK:
				return getUngzippedContent(response.getEntity());
			default:
				httpPost.abort();
				return null;
			}
		} catch (Exception e) {
			httpPost.abort();
			e.printStackTrace();
			throw new MLNetException(e.getMessage(), e);
		}
	}

	/**
	 * 判断是否是gzip
	 * 
	 * @param entity
	 * @return
	 * @throws IOException
	 */
	public InputStream getUngzippedContent(HttpEntity entity)
			throws IOException {
		InputStream responseStream = entity.getContent();
		if (responseStream == null) {
			return responseStream;
		}
		Header header = entity.getContentEncoding();
		if (header == null) {
			return responseStream;
		}
		String contentEncoding = header.getValue();
		if (contentEncoding == null) {
			return responseStream;
		}
		if (contentEncoding.contains(Apn.HTTP_PRESSED_TYPE)) {
			responseStream = new GZIPInputStream(responseStream);
		}
		return responseStream;
	}

	@Override
	public String getContentByString(InputStream is) throws IOException {
		if (is == null)
			return null;
		// BufferedReader br = new BufferedReader(reader);
		// StringBuilder sb = new StringBuilder();
		// String line = "";
		// while ((line = br.readLine()) != null) {
		// sb.append(line);
		// }

		// byte[] b = new byte[1024];
		// int len = -1;
		// StringBuilder sb = new StringBuilder();
		// while ((len = is.read(b)) != -1) {
		// sb.append(new String(b, 0, len));
		// }
		// String clob = new String(sb.toString().getBytes("iso8859-1"),
		// ENCODING);

		ByteArrayOutputStream outSteam;
		String sb2 = null;
		outSteam = new ByteArrayOutputStream();
		byte[] bufferArray = new byte[4096];
		int len = 0;
		while ((len = is.read(bufferArray)) != -1) {
			outSteam.write(bufferArray, 0, len);
		}
		sb2 = outSteam.toString("UTF-8");

		return sb2.toString();
	}

	@Override
	public void close() {
		try {
			client.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
