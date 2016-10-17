package com.foryou.truck.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.foryou.truck.EntryLoginActivity;
import com.foryou.truck.HomeMainScreenActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
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

/**
 * @des:从个人中心进入的 修改密码页面：
 */
public class ModifyPasswordAct extends BaseActivity {
	private static final String TAG = "ModifyPasswordAct";
	Context mContext;
	@BindView(id = R.id.title)
	private TextView mTitle;
	@BindView(id = R.id.current_password_et)
	private WithDelImgEditText currentPasswordEt;
	@BindView(id = R.id.new_password_et)
	private WithDelImgEditText newPasswordEt;
	@BindView(id = R.id.confirm_password_et)
	private WithDelImgEditText confirmPasswordEt;
	@BindView(id = R.id.visable_password_rl, click = true)
	private RelativeLayout visablePasswordRL;
	@BindView(id = R.id.visable_password_iv)
	private ImageView isVisablePasswordIv;
	@BindView(id = R.id.button1, click = true)
	private Button mConfirm;
	private boolean isPasswordState=true;

	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.act_modify_password);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mTitle.setText("设置新密码");
		ShowBackView();
		mConfirm.setText("确定");

	}

	public void setPasswordState(boolean passwordState) {
		if(passwordState){//显示密码
			isVisablePasswordIv.setBackgroundResource(R.drawable.icon_gou1);
			currentPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			newPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			confirmPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

		}else{// 显示明文
			isVisablePasswordIv.setBackgroundResource(R.drawable.icon_gou2);
			currentPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			newPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			confirmPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}
	}


	@Override
	public void onClickListener(int id) {
		switch (id) {
	    case R.id.visable_password_rl:
			isPasswordState=!isPasswordState;
			setPasswordState(isPasswordState);
			break;

		case R.id.button1:
			if (!Tools.IsConnectToNetWork(mContext)) {
				ToastUtils.toast(mContext, "联网异常,请稍后再试");
				return;
			}
			String currentPasswordStr=currentPasswordEt.getText().toString().trim();
			String newPasswordStr=newPasswordEt.getText().toString().trim();
			String confirmPasswordStr=confirmPasswordEt.getText().toString().trim();

			if(TextUtils.isEmpty(currentPasswordStr)){
				ToastUtils.toast(this, "请输入当前密码");
				return;
			}

			if(TextUtils.isEmpty(newPasswordStr)){
				ToastUtils.toast(this, "请设置新登录密码");
				return;
			}
			if(newPasswordStr.length()<6){
				ToastUtils.toast(this, "登录密码长度不足6位");
				return;
			}
			if(newPasswordStr.contains(" ")){
				ToastUtils.toast(this, "登录密码不能包含空格");
				return;
			}

			if (newPasswordStr.equals(currentPasswordStr)) {
				ToastUtils.toast(this, "新登录密码不能与当前登录密码相同");
				return;
			}

			if (!newPasswordStr.equals(confirmPasswordStr)) {
				ToastUtils.toast(this, "两次新登录密码输入不一致");
				return;
			}
			setNewPassword();
			break;
		}
	}

	private void setNewPassword() {
		String url = UrlConstant.BASE_URL + "/user/updatePassword";
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				UtilsLog.i(TAG, "user/updatePassword/response:" + response);
				cancelProgressDialog();
				LoginJsonParser mParser = new LoginJsonParser();
				int result = mParser.parser(response);
				if (result == 1) {
					if (mParser.entity.status.equals("Y")) {
						ToastUtils.toast(mContext, "设置成功");
			    	 //   SharePerfenceUtil.setKey(mContext, mParser.entity.data.key);
					//    SharePerfenceUtil.setUid(mContext, mParser.entity.data.uid);
						Intent intent = new Intent(mContext, EntryLoginActivity.class);
						startActivity(intent);
						finish();
					} else {
						if(!TextUtils.isEmpty(mParser.entity.flag ) && mParser.entity.flag.equals("1") ){
							alertDialog("当前登录密码出错已达上限，请重新登录", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(mContext, EntryLoginActivity.class);
									startActivity(intent);
									finish();
								}
							},false);
						}else{
							ToastUtils.toast(mContext, mParser.entity.msg);
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
			public Map<String, String> getPostBodyData() {
				Map<String, String> parmas = new HashMap<String, String>();
				parmas.put("uid", SharePerfenceUtil.getUid(mContext));
				parmas.put("password", currentPasswordEt.getText().toString());
				parmas.put("newpassword", newPasswordEt.getText().toString());
				parmas.put("re_newpassword", confirmPasswordEt.getText().toString());
				return parmas;
			}
		};
		showProgressDialog();
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}



	@Override
	protected void onStop() {
		super.onStop();
		cancelProgressDialog();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}

}
