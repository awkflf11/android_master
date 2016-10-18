package com.foryou.truck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
import com.foryou.truck.parser.CouponParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;

import java.util.HashMap;
import java.util.Map;

//老版本的  填写代金券
public class getDiscountActivity extends BaseActivity {
	private String TAG = "getDiscountActivity";
	private Context mContext;
	private String agent_id, order_id;

	@BindView(id = R.id.not_use_baoxian, click = true)
	private TextView mNotUseBaoxian;
	@BindView(id = R.id.save_btn, click = true)
	private Button mSaveBtn;
	@BindView(id = R.id.discount_number)
	private EditText mCoupon;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.get_discount);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ShowBackView();
		setTitle("填写代金券");
		mContext = this;
		agent_id = getIntent().getStringExtra("agent_id");
		order_id = getIntent().getStringExtra("order_id");
		mCoupon.setText(getIntent().getStringExtra("coupon"));

	}

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.save_btn:
			if (mCoupon.getText().toString().equals("")) {
				ToastUtils.toast(this, "请填写代金券");
				return;
			}
			checkCouponValid();
			break;
		case R.id.not_use_baoxian:
			this.setResult(Constant.NO_DISCOUNT_CODE);
			finish();
			break;
		}
	}

	private void checkCouponValid() {
		String url = UrlConstant.BASE_URL + "/order/checkCoupon";
		showProgressDialog();
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.POST, url, new Response.Listener<String>() {
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
						intent.putExtra("coupon", mCoupon.getText().toString());
						intent.putExtra("value", mParser.entity.data.value);
						getDiscountActivity.this.setResult(Constant.GET_COUPON_CODE, intent);
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
				// TODO Auto-generated method stub
				Map<String, String> parmas = new HashMap<String, String>();
				parmas.put("coupon", mCoupon.getText().toString());
				parmas.put("order_id", order_id);
				parmas.put("agent_id", agent_id);
				return parmas;
			}

		};

		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}
}
