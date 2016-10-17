package com.foryou.truck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;

import java.util.HashMap;
import java.util.Map;

/**
 * @des:确认打款给司机
 */
public class ComfirmPayDriverActivity extends BaseActivity {
	private String TAG = "ComfirmPayDriverActivity";
	@BindView(id = R.id.confirm_btn, click = true)
	private Button mConfirmBtn;
	@BindView(id = R.id.contract_us, click = true)
	private Button mContractUs;
	private Context mContext;
	private String order_id;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.confirm_paydriver);
	}

	@Override
	public void onStop() {
		super.onStop();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setTitle("确定打款给司机");
		ShowBackView();

		order_id = getIntent().getStringExtra("order_id");
	}

	private void ConfirmPayDriver() {
		String url = UrlConstant.BASE_URL
				+ "/order/payDriver";

		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
				mContext, Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						UtilsLog.i(TAG, "response:" + response);
						cancelProgressDialog();
						SimpleJasonParser mParser = new SimpleJasonParser();
						int result = mParser.parser(response);
						if (result == 1) {
							if (mParser.entity.status.equals("Y")) {
								UtilsLog.i(TAG,"go to pingjia Activity");
								Intent intent = new Intent(mContext,
										PingjiaAgentActivity.class);
								intent.putExtra("order_id", order_id);
								intent.putExtra("driver_array", true);
								startActivity(intent);
								finish();
							}else{
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
				return parmams;

			}

		};

		showProgressDialog();
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.confirm_btn:
			ConfirmPayDriver();
			break;
		case R.id.contract_us:
			Constant.GotoDialPage(mContext, Constant.PHONE_NUMBER);
			break;
		}
	}

}
