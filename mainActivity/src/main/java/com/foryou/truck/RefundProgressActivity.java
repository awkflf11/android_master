package com.foryou.truck;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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
import com.foryou.truck.entity.RefundProgressEntity;
import com.foryou.truck.parser.RefundProgressParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;

import java.util.HashMap;
import java.util.Map;

public class RefundProgressActivity extends BaseActivity {

	private String TAG = "RefundProgressActivity";
	private Context mContext;
	private String order_id = "";
	@BindView(id = R.id.data1)
	private TextView mData1;
	@BindView(id = R.id.data2)
	private TextView mData2;
	@BindView(id = R.id.status1)
	private TextView mStatus1;
	@BindView(id = R.id.status2)
	private TextView mStatus2;
	@BindView(id = R.id.status3)
	private TextView mStatus3;
	@BindView(id = R.id.refund_progress)
	private ImageView mRefundProgress;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.refund_progress);
	}

	private void initData(RefundProgressEntity entity) {
		mData1.setText(entity.data.get(0).date);
		mStatus1.setText(entity.data.get(0).status);
		if (entity.data.get(0).flag.equals("1") && entity.data.get(1).flag.equals("0") && entity.data.get(2).flag.equals("0")) {
			mRefundProgress.setImageResource(R.drawable.refund1);
		} else if (entity.data.get(0).flag.equals("1") && entity.data.get(1).flag.equals("1") && entity.data.get(2).flag.equals("0")) {
			mRefundProgress.setBackgroundResource(R.drawable.refund2);
			mData2.setText(entity.data.get(1).date);
		} else if (entity.data.get(0).flag.equals("1") && entity.data.get(1).flag.equals("0") && entity.data.get(2).flag.equals("1")) {
			mRefundProgress.setBackgroundResource(R.drawable.refund3);
			mData2.setText(entity.data.get(2).date);
		}
	}

	private void getRefundProgress() {
		Map<String, String> parmams = new HashMap<String, String>();
		parmams.put("order_id", order_id);
		String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL + "/order/refundProgress", parmams);
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.GET, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				UtilsLog.i(TAG, "response:" + response);
				cancelProgressDialog();
				RefundProgressParser mParser = new RefundProgressParser();
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
				// TODO Auto-generated method stub
				Map<String, String> parmams = new HashMap<String, String>();
				parmams.put("order_id", order_id);
				return super.getPostBodyData();
			}

		};

		showProgressDialog();
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mContext = this;
		setTitle("退款进度");
		ShowBackView();

		order_id = getIntent().getStringExtra("order_id");
		UtilsLog.i(TAG, "order_id:" + order_id);
		getRefundProgress();
	}

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStop() {
		super.onStop();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}
}
