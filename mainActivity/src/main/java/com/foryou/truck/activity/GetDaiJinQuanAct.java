package com.foryou.truck.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.foryou.truck.BaseActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.MyCouponListEntity.MyCoupon;
import com.foryou.truck.parser.CouponParser;
import com.foryou.truck.parser.MyCouponListParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.ListViewForScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @des: 代金券页面
 */
public class GetDaiJinQuanAct extends BaseActivity {
	public static String TAG = "GetDaiJinQuanAct";
	private Context mContext;
	// @BindView(id = R.id.not_use_baoxian, click = true)
	// private TextView mNotUseBaoxian;
	@BindView(id = R.id.duihuan_btn, click = true)
	private Button dunHuanBtn;
	@BindView(id = R.id.discount_number)
	private EditText mCoupon;//输入代金券
	private String agent_id, order_id,quote_price;
	@BindView(id = R.id.not_user_daijinquan, click = true)
	private RelativeLayout mNotUseDaijinquan;
	 @BindView(id = R.id.hint_text)
	private TextView noticeTv;
	@BindView(id = R.id.select_quan_tv)
    private TextView selectQuanTv;
	//
	private ListViewForScrollView listView;
	private List<Map<String, Object>> adapterlist = new ArrayList<Map<String, Object>>();
	private MyAdapter mAdapter = new MyAdapter();
	MyCouponListParser mParser;

	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.act_get_daijinquan);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ShowBackView();
		setTitle("代金券");
		listView = (ListViewForScrollView) findViewById(R.id.daijinquan_lv);
		agent_id = getIntent().getStringExtra("agent_id");
		order_id = getIntent().getStringExtra("order_id");
		quote_price = getIntent().getStringExtra("quote_price");

		//mCoupon.setCursorVisible(false);
		//mCoupon.setText(getIntent().getStringExtra("coupon"));
		//测试
//		agent_id = "";
//		order_id = "";
//		mCoupon.setText("");
		getDaiJinQuan();
	}

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.duihuan_btn:
			if (mCoupon.getText().toString().equals("")) {
				ToastUtils.toast(this, "请填写代金券");
				return;
			}
			checkCouponValid();
			break;

		case R.id.not_user_daijinquan:
			this.setResult(Constant.NO_DISCOUNT_CODE);
			finish();
			break;
		}
	}

	private void initData() {
		 if(mParser==null||mParser.entity==null||mParser.entity.data==null){
		 return;
		 }
		 if(mParser.entity.data.size()>0){
			 noticeTv.setVisibility(View.VISIBLE);
			 selectQuanTv.setVisibility(View.VISIBLE);
		 }
		 
		for (int i = 0; i < mParser.entity.data.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				MyCoupon mCoupon = mParser.entity.data.get(i);
				 map.put("money",  mCoupon.value );
				 map.put("bianhao",  "编号:"+mCoupon.code);
				 map.put("effectivedtime",  ""  +mCoupon.start_day +  "至"+mCoupon.end_day);
				 //map.put("condition",  false);
				adapterlist.add(map);
			}
		listView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		// if (mParser.entity.data.size() == 0 && !isRefresh) {
		// pullDownView.showNoDataView();
		// }
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
 				Intent intent = new Intent();
 				String coupon=(String)adapterlist.get(position).get("bianhao");
 				if(coupon.contains("编号")){
 					coupon=coupon.split("编号:")[1];
 				}
 				intent.putExtra("coupon", coupon);
 				intent.putExtra("value", (String)adapterlist.get(position).get("money"));
 				GetDaiJinQuanAct.this.setResult(Constant.GET_COUPON_CODE, intent);
// 				UtilsLog.i(TAG, "coupon"+coupon);
// 				UtilsLog.i(TAG, "value"+(String)adapterlist.get(position).get("money"));
 			    finish();
			}
		});
	}

	private void getDaiJinQuan() {
		showProgressDialog();
		String url = UrlConstant.BASE_URL + "/order/availableCoupon";
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				UtilsLog.i(TAG, response);
				cancelProgressDialog();
				 mParser = new MyCouponListParser();
				int result = mParser.parser(response);
				if (result == 1) {
					if (mParser.entity.status.equals("Y")) {
							initData();
					} else {
						ToastUtils.toast(mContext, mParser.entity.msg);
						noticeTv.setVisibility(View.GONE);
						selectQuanTv.setVisibility(View.GONE);
						// pullDownView.showNoDataView();
					}
				} else {
					Log.i(TAG, "/user/bindGt 解析错误");
					noticeTv.setVisibility(View.GONE);
					selectQuanTv.setVisibility(View.GONE);
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
				cancelProgressDialog();
				noticeTv.setVisibility(View.GONE);
				selectQuanTv.setVisibility(View.GONE);
			}

		}, true) {
			@Override
			public Map<String, String> getPostBodyData() {
				Map<String, String> parmas = new HashMap<String, String>();
				//测试
//				parmas.put("order_id", "493237");
 			   // parmas.put("quote_price", "67898");
				parmas.put("order_id",order_id);
				parmas.put("quote_price", quote_price);
				//UtilsLog.i(TAG, "quote_price=="+quote_price);
				return parmas;
			}
		};
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	
	private void checkCouponValid() {
		String url = UrlConstant.BASE_URL
				+ "/order/checkCoupon";
		showProgressDialog();
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
				mContext, Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						UtilsLog.i(TAG, "response:" + response);
						cancelProgressDialog();
						CouponParser mParser = new CouponParser();
						int result = mParser.parser(response);
						if (result == 1) {
							if (mParser.entity.status.equals("Y")) {
								Intent intent = new Intent();
								intent.putExtra("coupon", mCoupon.getText()
										.toString());
								intent.putExtra("value",
										mParser.entity.data.value);
								GetDaiJinQuanAct.this.setResult(Constant.GET_COUPON_CODE, intent);
								finish();
							} else {
								ToastUtils.toast(mContext, mParser.entity.msg);
								TongjiModel.addEvent(mContext, "代金券",
										TongjiModel.TYPE_ERROR,
										mParser.entity.msg);

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
						cancelProgressDialog();
					}

				}, true) {

			@Override
			public Map<String, String> getPostBodyData() {
				// TODO Auto-generated method stub
				Map<String, String> parmas = new HashMap<String, String>();
				parmas.put("coupon", mCoupon.getText().toString());
				parmas.put("order_id", order_id);
				parmas.put("agent_id", agent_id);
				UtilsLog.i(TAG,"coupon=="+mCoupon.getText().toString());
				UtilsLog.i(TAG,"order_id=="+order_id);
				UtilsLog.i(TAG,"agent_id=="+agent_id);
				return parmas;
			}

		};

		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return adapterlist.size();
		}

		@Override
		public Object getItem(int position) {
			return adapterlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_mydaijinquan, null);
				viewHolder.moneyTv = (TextView) convertView
						.findViewById(R.id.money_myquan_tv);
				viewHolder.conditionTv = (TextView) convertView
						.findViewById(R.id.used_condition_tv);
				viewHolder.numberTv = (TextView) convertView
						.findViewById(R.id.number_tv_);
				viewHolder.usedTimeTv = (TextView) convertView
						.findViewById(R.id.used_timed_tv);
				viewHolder.placeLimitTv = (TextView) convertView
						.findViewById(R.id.place_limit_tv);
				viewHolder.itemLL = (LinearLayout) convertView
						.findViewById(R.id.item_daijinquan_ll);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			//viewHolder.conditionTv.setVisibility(View.GONE);
			viewHolder.conditionTv.setText(" ");
			viewHolder.placeLimitTv.setVisibility(View.GONE);
			viewHolder.itemLL.setBackgroundResource(R.drawable.daijinquan_1);
			// map.put("money", 500+ i+"元");
			// map.put("condition", "运单满100可用");
			// map.put("bianhao", "编号：123456");
			// map.put("effectivedtime", "有效期：2015-12-12至2016-12-12");
			viewHolder.moneyTv.setText(""+ adapterlist.get(position).get(
					"money"));
			viewHolder.numberTv.setText((String) adapterlist.get(position).get(
					"bianhao"));
			viewHolder.usedTimeTv.setText((String) adapterlist.get(position)
					.get("effectivedtime"));
			return convertView;
		}
	}

	class ViewHolder {
		TextView moneyTv;
		TextView conditionTv;
		TextView numberTv;
		TextView usedTimeTv;
		TextView placeLimitTv;
		LinearLayout itemLL;


	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}
}
