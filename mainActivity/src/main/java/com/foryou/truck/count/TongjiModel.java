package com.foryou.truck.count;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.foryou.truck.MyApplication;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TongjiModel {

	private static String TAG = "TongjiModel";
	// 1：按钮点击
	// 2：下拉刷新
	// 3：上拉刷新
	// 4：打开页面
	// 5：表单点击
	// 6：启动应用
	// 7：关闭应用
	public static final int TYPE_BUTTON_CLIKC = 1;
	public static final int TYPE_PULL_REFLESH = 2;
	public static final int TYPE_LOADER_MORE = 3;
	public static final int TYPE_VIEW_RESUME = 4;
	public static final int TYPE_TABLE_CLICK = 5;
	public static final int TYPE_LOADER_APP = 6;
	public static final int TYPE_CLOSE_APP = 7;
	public static final int TYPE_SEARCH_PARAMS = 8;
	public static final int TYPE_ERROR = 9;

	public static String source_page = "";

	private static List<PageInfo> mPageList = new ArrayList<PageInfo>();
	private static List<String> mClassList = new ArrayList<String>();
	private static boolean OPEN_FLAG = true;

	public static class PageInfo {
		public String start_time;
		public String end_time;
		public String source_page;
		public EventHandler event_handler;
	}

	public static class EventHandler {
		public String module;
		public String page;
		public String user_id;
		public List<Event> data;
	}

	public static class Event {
		public int type;
		public String content;
		public String dateline;
		public String inputData;
	}

	public static void onPause(Context mContext, String pageName) {
		if(!OPEN_FLAG){
			return;
		}

		UtilsLog.i(TAG, "onPause:" + mContext.getClass().toString());
		mClassList.remove(mContext.getClass().toString());

		String end_time = "" + System.currentTimeMillis();
		UtilsLog.i(TAG, "mPageList.size():" + mPageList.size());
		for (int i = 0; i < mPageList.size(); i++) {
			if (pageName.equals(mPageList.get(i).event_handler.page)) {
				mPageList.get(i).end_time = end_time;
				pushEventDataToServer(mContext, mPageList.get(i));
				mPageList.remove(i);
				break;
			}
		}
	}

	public static void addEvent(Context mContext, String pageName, int type,
			String description) {

		if(!OPEN_FLAG){
			return;
		}

		 String time = "" + System.currentTimeMillis();
		 Event mEvent = new Event();
		 mEvent.type = type;
		 mEvent.dateline = time;
		 mEvent.content = description;
		 for (int i = 0; i < mPageList.size(); i++) {
		 if (pageName.equals(mPageList.get(i).event_handler.page)) {
		 mPageList.get(i).event_handler.data.add(mEvent);
		 break;
		 }
		 }
	}

	public static void onResume(Context mContext, String pageName) {
		if(!OPEN_FLAG){
			return;
		}
		UtilsLog.i(TAG, "onResume:" + mContext.getClass().toString());
		for (int i = 0; i < mClassList.size(); i++) {
			if (mContext.getClass().toString().equals(mClassList.get(i))) {
				return;
			}
		}
		mClassList.add(mContext.getClass().toString());

		PageInfo mPageInfo = new PageInfo();
		mPageInfo.start_time = "" + System.currentTimeMillis();
		mPageInfo.event_handler = new EventHandler();
		mPageInfo.event_handler.data = new ArrayList<Event>();
		mPageInfo.event_handler.page = pageName;
		mPageInfo.source_page = source_page;
		source_page = pageName;
		mPageList.add(mPageInfo);
	}

	public static void onFragmentResume(Context mContext, String pageName) {

		if(!OPEN_FLAG){
			return;
		}
		PageInfo mPageInfo = new PageInfo();
		mPageInfo.start_time = "" + System.currentTimeMillis();
		mPageInfo.event_handler = new EventHandler();
		mPageInfo.event_handler.data = new ArrayList<Event>();
		mPageInfo.event_handler.page = pageName;
		mPageInfo.source_page = source_page;
		source_page = pageName;
		mPageList.add(mPageInfo);
	}

	public static void onFragmentPause(Context mContext, String pageName) {
		if(!OPEN_FLAG){
			return;
		}
		String end_time = "" + System.currentTimeMillis();
		UtilsLog.i(TAG, "mPageList.size():" + mPageList.size());
		for (int i = 0; i < mPageList.size(); i++) {
			if (pageName.equals(mPageList.get(i).event_handler.page)) {
				mPageList.get(i).end_time = end_time;
				pushEventDataToServer(mContext, mPageList.get(i));
				mPageList.remove(i);
				break;
			}
		}
	}

	private static void pushEventDataToServer(final Context mContext,
			final PageInfo pageInfo) {

		String url = UrlConstant.TONGJI_URL + "/point/operate";
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
				mContext, Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						UtilsLog.i(TAG, "maidian______url:" + UrlConstant.TONGJI_URL + "/point/operate");
						UtilsLog.i(TAG, "maidian______response:" + response);
						SimpleJasonParser mParser = new SimpleJasonParser();
						if(response==null){
							return;
						}
						int result = mParser.parser(response);
						if(mParser==null||mParser.entity==null){
							return;
						}
						if (result == 1) {
							if (mParser.entity.status.equals("Y")) {
								UtilsLog.i(TAG, "send success");
							} else {
								UtilsLog.i(TAG, "server return error");
							}
						} else {
							Log.i(TAG, "解析错误");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (error instanceof NetworkError) {
							Log.i(TAG, "NetworkError");
						} else if (error instanceof ServerError) {
							Log.i(TAG, "ServerError");
						} else if (error instanceof AuthFailureError) {
							Log.i(TAG, "AuthFailureError");
						} else if (error instanceof ParseError) {
							Log.i(TAG, "ParseError");
						} else if (error instanceof NoConnectionError) {
							Log.i(TAG, "NoConnectionError");
						} else if (error instanceof TimeoutError) {
							Log.i(TAG, "TimeoutError");
						}
					}

				}, true) {

			@Override
			public Map<String, String> getParams() throws AuthFailureError {
				// TODO Auto-generated method stub
				// UserInfoEntity mUserDetail = SharePerUtils
				// .getUserInfo(mContext);
				Map<String, String> parmas = super.getPostBodyData();
				parmas.put("module", "");
				parmas.put("page", pageInfo.event_handler.page);
				// parmas.put("user_id", mUserDetail.data.id);
				parmas.put("start_time", pageInfo.start_time);
				parmas.put("end_time", pageInfo.end_time);

				String json = "";
				if (pageInfo.event_handler.data.size() > 0) {
					Gson gson = new Gson();
					json = gson.toJson(pageInfo.event_handler.data);
				}
				parmas.put("data", json);
				UtilsLog.i(TAG, "data:" + json);
				return parmas;
			}
		};
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}
}
