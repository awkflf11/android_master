package com.foryou.truck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.OrderPayRefundEntity;
import com.foryou.truck.parser.OrderPayRefundParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.ClickExpandView;

import java.util.HashMap;
import java.util.Map;
/**
 * @des: 申请退款
 */
public class ApplyRefundActivity extends BaseActivity {
	private String TAG = "ApplyRefundActivity";
	private Context mContext;
	@BindView(id = R.id.danhao)
	private TextView mDanhao;
	@BindView(id = R.id.status)
	private TextView mStatus;
	@BindView(id = R.id.pay_method_tv)
	private TextView payMethodTv;//支付的方式
	//
	@BindView(id = R.id.pay_btn, click = true)
	private Button mPayBtn;
	@BindView(id = R.id.jiner_layout2)
	private RelativeLayout mOrderPriceLayout2;
//	@BindView(id = R.id.hongbao_layout2)
//	private RelativeLayout mGiftPricelayout2;// 代金券 删除了
//	@BindView(id = R.id.quan_price)
//	private TextView mDiscountPrice2;//

	@BindView(id = R.id.total_price_ll2)
	private LinearLayout totalPriceLL2;
	@BindView(id=R.id.baoxian_layout)
	private RelativeLayout mBaoxianLayout;
	@BindView(id=R.id.baoxian_price)
	private TextView mBaoxianPrice;
	@BindView(id=R.id.give_baoxian_layout)
	private RelativeLayout mGiveBaoxianLayout;
	@BindView(id=R.id.give_baoxian_price)
	private TextView mGiveBaoxianPrice;
	@BindView(id=R.id.taxation_layout)
	private RelativeLayout taxationLayout;//税费
	@BindView(id=R.id.taxation)
	private TextView taxationTv;
	//
	@BindView(id = R.id.order_price)
	private TextView orderPrice;//运单金额
	@BindView(id = R.id.refund_total_price)
	private TextView refundTotalPrice;//退款的总额
	private String order_id = "";
	//新增
	@BindView(id = R.id.top_ll, click = true)
	private LinearLayout topLL;
	@BindView(id = R.id.bottom_layout_rl, click = true)
	private RelativeLayout bottomLayoutRL;
	@BindView(id = R.id.start_place)
	private TextView startPlaceTv;
	@BindView(id = R.id.end_place)
	private TextView endPlaceTv;
	@BindView(id = R.id.expand_view1)
	private ClickExpandView expandView1;
	@BindView(id = R.id.expand_view2)
	private ClickExpandView expandView2;

	@Override
	public void setRootView() {
		// super.setRootView();
		setContentView(R.layout.pay_online_refund);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("申请退款");
		ShowBackView();
		InitView();
		initExpand();
		mStatus.setText("已取消");
		//mTotalText.setText("退款合计");
		mPayBtn.setText("申请退款");
		order_id = getIntent().getStringExtra("order_id");
		UtilsLog.i(TAG, "order_id:" + order_id);
		getOrderPayRefund();
	}
	private void InitView() {
		mStatus.setVisibility(android.view.View.VISIBLE);
	}

	private void initData(OrderPayRefundEntity entity) {		
		payMethodTv.setText(entity.data.payment_type);
		mDanhao.setText(entity.data.order_sn);
		startPlaceTv.setText(entity.data.start_place);
		endPlaceTv.setText(entity.data.end_place);
		//
		if(!TextUtils.isEmpty(entity.data.order_price)&&!entity.data.order_price.equals("0")){
			mOrderPriceLayout2.setVisibility(View.VISIBLE);
			orderPrice.setText("¥"+entity.data.order_price);
		}

		if(!TextUtils.isEmpty(entity.data.insurance_price)&&!entity.data.insurance_price.equals("0")){
			mBaoxianPrice.setText("¥" + entity.data.insurance_price);
			mBaoxianLayout.setVisibility(View.VISIBLE);
		}

		if(!TextUtils.isEmpty(entity.data.insurance_given_price)&&!entity.data.insurance_given_price.equals("0")){
			mGiveBaoxianPrice.setText("-¥"+entity.data.insurance_given_price);
			mGiveBaoxianLayout.setVisibility(View.VISIBLE);
		}
//		if(!TextUtils.isEmpty(entity.data.gift_price)&&!entity.data.gift_price.equals("0")){
//			mGiftPricelayout2.setVisibility(View.VISIBLE);
//			mDiscountPrice2.setText("-" +"¥"+ entity.data.gift_price);
//		}
		if(!TextUtils.isEmpty(entity.data.invoice_money)&& !entity.data.invoice_money.equals("0")){
			taxationLayout.setVisibility(View.VISIBLE);
			taxationTv.setText("¥"+ entity.data.invoice_money);
		}

		//退款总额
		if(!TextUtils.isEmpty(entity.data.refund_price)&&!entity.data.refund_price.equals("0")){
			totalPriceLL2.setVisibility(View.VISIBLE);
			refundTotalPrice.setText(""+"¥"+entity.data.refund_price);
		}
	}

	private void getOrderPayRefund() {
		String url = UrlConstant.BASE_URL + "/order/refundInfo";
		showProgressDialog();
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				UtilsLog.i(TAG, "response:" + response);
				cancelProgressDialog();
				OrderPayRefundParser mParser = new OrderPayRefundParser();
				int result = mParser.parser(response);
				if (result == 1) {
					if (mParser.entity.status.equals("Y")) {
						initData(mParser.entity);
					} else {
						ToastUtils.toast(mContext, mParser.entity.msg);
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
				Map<String, String> parmams = new HashMap<String, String>();
				parmams.put("order_id", order_id);
				//parmams.put("payment", "1");
				//parmams.put("client", "1");
				//parmams.put("price_only", "1");
				return parmams;
			}
		};
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}


	private void orderReund() {
		String url = UrlConstant.BASE_URL + "/order/refundConfirm";
		showProgressDialog();
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				UtilsLog.i(TAG, "response:" + response);
				cancelProgressDialog();
				SimpleJasonParser mParser = new SimpleJasonParser();
				int result = mParser.parser(response);
				if (result == 1) {
					if (mParser.entity.status.equals("Y")) {
						// ToastUtils.toast(mContext,mParser.entity.msg);
						Intent intent = new Intent(mContext, RefundProgressActivity.class);
						intent.putExtra("order_id", order_id);
						startActivity(intent);
						finish();
					} else {
						ToastUtils.toast(mContext, mParser.entity.msg);
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
				Map<String, String> parmams = new HashMap<String, String>();
				parmams.put("order_id", order_id);
				return parmams;
			}
		};
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	public void initExpand(){
		expandView1.setIsExpand(false);
		bottomLayoutRL.setVisibility(View.GONE);
	}
	public  void  isEpandLogic(boolean boolan){
		if(boolan){
			expandView1.setVisibility(View.GONE);
			bottomLayoutRL.setVisibility(View.VISIBLE);
			expandView2.setIsExpand(true);
			expandView1.setIsExpand(true);
		}else{
			expandView1.setVisibility(View.VISIBLE);
			expandView1.setIsExpand(false);
			bottomLayoutRL.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClickListener(int id) {
		switch (id) {
		case R.id.top_ll:
				isEpandLogic(!expandView1.getIsExand());
				break;
		case R.id.bottom_layout_rl:
				isEpandLogic(false);
				break;
		case R.id.pay_btn:
			TongjiModel.addEvent(mContext, "申请退款", TongjiModel.TYPE_BUTTON_CLIKC, "确认退款");
			orderReund();
			break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		cancelProgressDialog();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}

}
