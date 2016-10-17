package com.foryou.truck;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import com.foryou.truck.parser.ContractJsonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;

public class XieyiActivity extends BaseActivity {
	private String TAG = "XieyiActivity";
	private Context mContext;
	@BindView(id = R.id.content)
	private TextView mContent;
	@BindView(id = R.id.aggree, click = true)
	private Button mAggree;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.xieyi);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		ShowBackView();
		setTitle("三方协议");
		getXieyi();
	}

	private void getXieyi() {
		String url = UrlConstant.BASE_URL + "/common/contract";
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
				mContext, Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						UtilsLog.i(TAG, "response:" + response);
						cancelProgressDialog();
						ContractJsonParser mParser = new ContractJsonParser();
						int result = mParser.parser(response);
						if (result == 1) {
							if (mParser.entity.status.equals("Y")) {
								mContent.setText(mParser.entity.data.contract);
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
		};
		showProgressDialog();
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.aggree:
			Log.i(TAG, "aggree ........");
			setResult(Constant.AGREEMENT_CODE);
			finish();
			break;
		}
	}

	// Handler mHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// cancelProgressDialog();
	// if (MLHttpConnect2.SUCCESS == msg.what) {
	// if (mSimpleParser.entity.status.equals("Y")) {
	// String result = (String) msg.obj;
	// Log.i("aa", "result:" + result);
	// mContent.setText(mSimpleParser.entity.data.contract);
	// } else {
	// ToastUtils.toast(mContext, mSimpleParser.entity.msg);
	// }
	// } else {
	// Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
	// }
	// super.handleMessage(msg);
	// }
	//
	// };

	@Override
	protected void onStop() {
		super.onStop();
		cancelProgressDialog();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}

}
