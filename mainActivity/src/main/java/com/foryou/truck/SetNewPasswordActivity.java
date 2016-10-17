package com.foryou.truck;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.foryou.truck.parser.LoginJsonParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.WithDelImgEditText;

import java.util.HashMap;
import java.util.Map;

public class SetNewPasswordActivity extends BaseActivity {
	private static final String TAG = "SetNewPasswordActivity";
	Context mContext;
	@BindView(id = R.id.title)
	private TextView mTitle;
	@BindView(id = R.id.right,click = true)
	private Button rightBtn;
	@BindView(id = R.id.back_view, click = true)
	private RelativeLayout mBackView;
	@BindView(id = R.id.set_password)
	private WithDelImgEditText mPassword1;
	@BindView(id = R.id.set_password2)
	private WithDelImgEditText mPassword2;
	@BindView(id = R.id.ok, click = true)
	private Button mConfirm;
	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.set_password);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mTitle.setText("设置新密码");
		mBackView.setVisibility(android.view.View.VISIBLE);
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setTextColor(Color.parseColor("#ffffff"));
		rightBtn.setText("确定");
	}

	private void setNewPassword() {
		String url = UrlConstant.BASE_URL + "/user/reset";
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				UtilsLog.i(TAG, "response:" + response);
				cancelProgressDialog();
				LoginJsonParser mParser = new LoginJsonParser();
				int result = mParser.parser(response);
				if (result == 1) {
					if (mParser.entity.status.equals("Y")) {
						ToastUtils.toast(mContext, "设置成功");
						SharePerfenceUtil.setKey(mContext, mParser.entity.data.key);
						SharePerfenceUtil.setUid(mContext, mParser.entity.data.uid);
						Intent intent = new Intent(mContext, HomeMainScreenActivity.class);
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
				Map<String, String> parmas = new HashMap<String, String>();
				//parmas.put("key", SharePerfenceUtil.getKey(mContext));
				parmas.put("uid", SharePerfenceUtil.getUid(mContext));
				parmas.put("password", mPassword1.getText().toString());
				parmas.put("re_password", mPassword1.getText().toString());
				return parmas;
			}

		};
		showProgressDialog();
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	// Handler mSetPassHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// cancelProgressDialog();
	// if (MLHttpConnect2.SUCCESS == msg.what) {
	// if (mSetPassJasonParser.entity.status.equals("Y")) {
	// String result = (String) msg.obj;
	// // Log.i("aa", "result:" + result);
	// ToastUtils.toast(mContext, "设置成功");
	// finish();
	// Intent intent = new Intent(mContext, HomeMainScreenActivity.class);
	// startActivity(intent);
	// } else {
	// ToastUtils.toast(mContext, mSetPassJasonParser.entity.msg);
	// }
	// } else {
	// Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
	// }
	// super.handleMessage(msg);
	// }
	//
	// };
	// private void setNewPassword() {
	// Map<String, String> parmas = new HashMap<String, String>();
	//
	// }

	@Override
	public void onClickListener(int id) {
		switch (id) {
		case R.id.right:
			if (!Tools.IsConnectToNetWork(mContext)) {
				ToastUtils.toast(mContext, "联网异常,请稍后再试");
				return;
			}
//			if (!mPassword1.getText().toString().equals(mPassword2.getText().toString())) {
//				ToastUtils.toast(this, "两次输入密码不一致,请重新输入");
//				return;
//			}
			if (mPassword1.getText().toString().equals("")) {
				ToastUtils.toast(this, "密码不能为空");
				return;
			}
			if (mPassword1.getText().toString().length() < 6) {
				ToastUtils.toast(this, "密码不能少于6位");
				return;
			}
			setNewPassword();
			break;
		case R.id.back_view:
			finish();
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
