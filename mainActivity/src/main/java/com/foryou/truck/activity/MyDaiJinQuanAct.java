package com.foryou.truck.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.foryou.truck.BaseActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.entity.MyCouponListEntity.MyCoupon;
import com.foryou.truck.parser.MyCouponListParser;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.PullDownView;
import com.foryou.truck.view.PullDownView.OnItemClickListener;
import com.foryou.truck.view.PullDownView.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @des:我的代金券
 */
public class MyDaiJinQuanAct extends BaseActivity {
	private static final String TAG = "MyDaiJinQuanAct";
	private Context mContext;
	private LayoutInflater inflater;
	private LinearLayout bottomView;
	private TextView noticeTv;
	//
	private PullDownView pullDownView;
	private ListView listView;
	private MyListViewAdapter mAdapter;
	private List<Map<String, Object>> adapterlist = new ArrayList<Map<String, Object>>();
	private TextView mTitle;
	private boolean isRefresh = false;
	private int PAGE_SIZE = Constant.PAGE_SIZE;
	private int PAGE_INDEX = 1;
	MyCouponListParser mParser;

	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.act_my_daijinquan);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		UtilsLog.i(TAG, "MyDaiJinQuanAct onCreate");
		initView();
		InitListView();
	}

	private void initView() {
		ShowBackView();
		setTitle("我的代金券");
		inflater = LayoutInflater.from(mContext);
		bottomView = (LinearLayout) inflater.inflate(R.layout.bottom_daijinquan, null);
		noticeTv = (TextView)bottomView.findViewById(R.id.hint_text);

	}

	private void InitListView() {
		pullDownView = (PullDownView) findViewById(R.id.feeds);
		pullDownView.init();
		pullDownView.setFooterView(R.layout.footer_item);
		pullDownView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getComentList(false);

			}
		});
		pullDownView.showFooterView(false);
		listView = pullDownView.getListView();	
		mAdapter = new MyListViewAdapter(this, adapterlist, R.layout.item_mydaijinquan, 				
	   new String[] { "money" , "condition" , "bianhao" , "place_limit","effectivedtime","item_ll" ,"placeLL"},
	   new int[] { R.id.money_myquan_tv,R.id.used_condition_tv,R.id.number_tv_,R.id.place_limit_tv,
				R.id.used_timed_tv, R.id.item_daijinquan_ll,R.id.place_limit_ll}, false);
		//
		listView.addFooterView(bottomView);
		listView.setAdapter(mAdapter);
		pullDownView.setPullDownViewOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

			}
		});
		getComentList(false);
	}

	private void initData() {
		if(mParser==null||mParser.entity==null||mParser.entity.data==null){
			return;
		}
		if(mParser.entity.data.size()>0){
			noticeTv.setVisibility(View.VISIBLE);
		}

		for (int i = 0; i < mParser.entity.data.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			MyCoupon mCoupon = mParser.entity.data.get(i);
			 map.put("money",  mCoupon.value+"" );
			 if(TextUtils.isEmpty(mCoupon.min_price_limit)){
				 map.put("condition",  "  ");
			 }else{
				 map.put("condition",  "满" +  mCoupon.min_price_limit+"元可用");
			 }
			 map.put("bianhao",  "编号:"+mCoupon.code);
			 map.put("effectivedtime",  mCoupon.start_day + "至"+mCoupon.end_day);
			 if(TextUtils.isEmpty(mCoupon.region_limit)){
				 map.put("placeLL", false);
				 map.put("place_limit", false);
				 map.put("item_ll", R.drawable.daijinquan_1);
			 }else{
				 map.put("placeLL", true);
				 map.put("place_limit", "始发地"+ mCoupon.region_limit+"可用");
				 map.put("item_ll", R.drawable.daijinquan_2);
			 }
			adapterlist.add(map);
		}
		//测试的部分
//		for (int i = 0; i < 10; i++) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			 map.put("money",   "100900" );
//			 map.put("bianhao",  " 编号：3434");
//			map.put("effectivedtime",  false  );
//			 map.put("effectivedtime",  "2323-45454"  );
//			map.put("condition",  "满1222" + "可用");
//			 //map.put("condition",  "  ");
//			if(5==5){
//				map.put("lineview", true);
//				map.put("item_ll", R.drawable.daijinquan_2);
//				map.put("place_limit", "装货地限制：fytrytrytrytryty676576576576576575775757");
//			}else{
//				map.put("lineview", false);
//				map.put("item_ll", R.drawable.daijinquan_1);
//				map.put("place_limit", false);
//			}
//			adapterlist.add(map);
//		}

		mAdapter.notifyDataSetChanged();
		if (mParser.entity.data.size() == 0 && !isRefresh) {
			pullDownView.showNoDataView(true);
		} else{
			pullDownView.showNoDataView(false);
		}
	}

	private void getComentList(boolean getMore) {
			if (getMore) {
				PAGE_INDEX += 1;
				isRefresh = true;
				listView.setOnScrollListener(null);
			} else {
				PAGE_INDEX = 1;
			}
			getMyDaiJinQuan();
		 
	}

	OnScrollListener scorllListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			Log.i("aa", "firstVisibleItem" + firstVisibleItem + "visibleItemCount" + visibleItemCount + "totalItemCount" + totalItemCount);
			if (((firstVisibleItem + visibleItemCount) == totalItemCount) && !isRefresh) {
				getComentList(true);
			} else {

			}
		}

	};

	private void getMyDaiJinQuan() {
	
		String url = UrlConstant.BASE_URL + "/user/myCoupon";
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				UtilsLog.i(TAG, "response:" + response);
				cancelProgressDialog();
				pullDownView.notifyRefreshComplete();
				mParser = new MyCouponListParser();
				int result = mParser.parser(response);
				if (result == 1) {
					if (mParser.entity.status.equals("Y")) {
						if (!isRefresh) {
							// 正常刷新加载
							adapterlist.clear();
							initData();
						} else {
							// 加载更多
							initData();
							isRefresh = false;

						}
						if (mParser.entity.data.size() == PAGE_SIZE) {
							listView.setOnScrollListener(scorllListener);
							pullDownView.showFooterView(true);
						} else {
							listView.setOnScrollListener(null);
							pullDownView.showFooterView(false);
						}

					} else {
						ToastUtils.toast(mContext, mParser.entity.msg);
						listView.setOnScrollListener(null);
						pullDownView.showFooterView(false);
						pullDownView.showNoDataView(true);
						noticeTv.setVisibility(View.GONE);
					}
				} else {
					Log.i(TAG, "解析错误");
					//ToastUtils.toast(mContext, "网络连接失败，请稍后重试");
					listView.setOnScrollListener(null);
					pullDownView.showFooterView(false);
					noticeTv.setVisibility(View.GONE);
					pullDownView.showNoDataView(true);
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (error instanceof NetworkError) {
					Log.i(TAG, "NetworkError");
				} else if (error instanceof AuthFailureError) {
					Log.i(TAG, "AuthFailureError");
				} else if (error instanceof ParseError) {
					Log.i(TAG, "ParseError");
				} else if (error instanceof NoConnectionError) {
					Log.i(TAG, "NoConnectionError");
				} else if (error instanceof TimeoutError) {
					Log.i(TAG, "TimeoutError");
				}
				listView.setOnScrollListener(null);
				pullDownView.showFooterView(false);
				cancelProgressDialog();
				pullDownView.notifyRefreshComplete();
				noticeTv.setVisibility(View.GONE);
				pullDownView.showNoDataView(true);
			}

		}, true) {

			@Override
			public Map<String, String> getPostBodyData() {
				// return super.getPostBodyData();
				Map<String, String> parmas = new HashMap<String, String>();
				parmas.put("page", "" + PAGE_INDEX);
				parmas.put("pagesize", "" + PAGE_SIZE);
				return parmas;

			}
		};
		showProgressDialog();
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	@Override
	public void onClickListener(int id) {
		switch (id) {
		default:
			break;
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		cancelProgressDialog();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}

}
