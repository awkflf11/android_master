package com.foryou.truck.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.foryou.truck.MyApplication;
import com.foryou.truck.net.Apn;
import com.foryou.truck.parser.CryptParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NormalNetworkRequest extends StringRequest {
	private final String mUrl;
	private String TAG = "NormalNetworkRequest";
	public static final String ENCODING = "UTF-8";
	private final boolean userCache;
	private final Listener<String> mListener;
	
	private String key,uid;

	public Map<String, String> getPostBodyData() {
		Map<String, String> parmas = new HashMap<String, String>();
		return parmas;
	}

	public NormalNetworkRequest(Context context, int method, String url,
			Listener<String> listener, ErrorListener errorListener,
			boolean userCache) {
		super(method, url, listener, errorListener);
		this.mUrl = url;
		this.userCache = userCache;
		this.mListener = listener;

		key = SharePerfenceUtil.getKey(context);
		uid = SharePerfenceUtil.getUid(context);
		UtilsLog.i(TAG, "url:" + mUrl);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		// TODO Auto-generated method stub
		Map<String, String> headerMap = Apn.getHeads();
		UtilsLog.i(TAG, "headerMap===========" + headerMap.toString());
		Map<String, String> map = new HashMap<String, String>();
		String value;
		Log.i(TAG,"headerMap:"+headerMap.toString());
		for (Entry<String, String> entry : headerMap.entrySet()) {
			value = entry.getValue();
			if (TextUtils.isEmpty(value)) {
				// UtilsLog.i(TAG, "header entry.getKey is null:" + entry.getKey());
				continue;
			} else {
				// UtilsLog.i(TAG, "header Key:" + entry.getKey() + ",Value:"
				// + entry.getValue());
				map.put(entry.getKey(), value);
			}
		}
		return map;
	}

	@Override
	protected void deliverResponse(String response) {
		UtilsLog.i(TAG,"orignal response:"+response);
		CryptParser mCryPtParser = new CryptParser();
		int result = mCryPtParser.parser(response);
		if (result != 1) {
			return;
		}
		if(!TextUtils.isEmpty(mCryPtParser.entity.fydata)){
			try {
				response = AESCrypt.getInstance().decrypt(mCryPtParser.entity.fydata);
			}catch(Exception e){
				UtilsLog.i(TAG,"aescrypt error .......");
				return;
			}
		}
		UtilsLog.i(TAG,"final response:"+response);
		mListener.onResponse(response);
	}


	@Override
	public void deliverError(VolleyError error) {

		if (error instanceof NetworkError) {
			Log.i(TAG, "NetworkError ...");
		} else if (error instanceof ServerError) {
			Log.i(TAG, "ServerError ...");
		} else if (error instanceof AuthFailureError) {
			Log.i(TAG, "AuthFailureError ...");
		} else if (error instanceof ParseError) {
			Log.i(TAG, "ParseError ...");
		} else if (error instanceof NoConnectionError) {
			Log.i(TAG, "NoConnectionError ...");
		} else if (error instanceof TimeoutError) {
			Log.i(TAG, "TimeoutError ...");
		}

		if (userCache) {
			com.android.volley.Cache.Entry entry = MyApplication.getInstance()
					.getVolleyRequestQueue().getCache().get(mUrl);
			if (entry != null) {
				String response = new String(entry.data);
				if (response != null && !TextUtils.isEmpty(response)) {
					deliverResponse(response);
				} else {
					if (error instanceof NetworkError
							|| error instanceof NoConnectionError) {
					//	ToastUtils.toast(mContext, "联网异常,请稍后再试");
					}
					super.deliverError(error);
				}
			} else {
				if (error instanceof NetworkError
						|| error instanceof NoConnectionError) {
				//	ToastUtils.toast(mContext, "联网异常,请稍后再试");
				}
				super.deliverError(error);
			}
		} else {
			if (error instanceof NetworkError
					|| error instanceof NoConnectionError) {
		//		ToastUtils.toast(mContext, "联网异常,请稍后再试");
			}
			super.deliverError(error);
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
		return sb.toString();
	}

	public static String getUrl(Context context, String url,
			Map<String, String> parmas) {
		parmas.put("key", SharePerfenceUtil.getKey(context));
		parmas.put("uid", SharePerfenceUtil.getUid(context));
		String urlString = url + buildUrl(parmas);
		return urlString;
	}

	@Override
	public Map<String, String> getParams() throws AuthFailureError {
		Map<String, String> parmas;
		parmas = getPostBodyData();
		parmas.put("key", key);
		parmas.put("uid", uid);

		Map<String, String> map = new HashMap<String, String>();

		String value;
		for (Entry<String, String> entry : parmas.entrySet()) {
			value = entry.getValue();
			if (TextUtils.isEmpty(value)) {
				UtilsLog.i(TAG, "entry.getKey is null:" + entry.getKey());
				continue;
			} else {
				UtilsLog.i(TAG,
						"Key:" + entry.getKey() + ",Value:" + entry.getValue());
				map.put(entry.getKey(), value);
			}
		}
		return map;
	}
}
