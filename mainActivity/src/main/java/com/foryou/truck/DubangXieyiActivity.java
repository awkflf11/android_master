package com.foryou.truck;

import android.content.Context;
import android.util.Log;
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
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;

import java.util.HashMap;
import java.util.Map;

public class DubangXieyiActivity extends AboutUsActivity {
	private String TAG = "DubangXieyiActivity";
	private Context mContext;
	@BindView(id = R.id.about_us)
	private TextView mContent;
	private  Boolean baodan;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}

	private void getXieYi() {
		Map<String, String> parmams = new HashMap<String, String>();
		String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL + "/common/contract", parmams);

		showProgressDialog();
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.GET, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				UtilsLog.i(TAG, "response:" + response);
				cancelProgressDialog();
				ContractJsonParser mParser = new ContractJsonParser();
				int result = mParser.parser(response);
				if (result == 1) {
					if (mParser.entity.status.equals("Y")) {
						if(baodan){
							mContent.setText(mParser.entity.data.fuyouchuxian);
						}else{
							mContent.setText(mParser.entity.data.dadi);
						}
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
			public Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = super.getParams();
				return map;
			}
		};

		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		// super.initView();
		mContext = this;
		baodan=getIntent().getBooleanExtra("baodan",false);
		if(baodan){
			setTitle("福佑出险流程");
		}else{
			setTitle("大地保险协议");
		}
		ShowBackView();
	}

	@Override
	public void getContent() {
		// TODO Auto-generated method stub
		// super.getContent();
		getXieYi();
	}
}
