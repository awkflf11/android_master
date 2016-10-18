package com.foryou.truck;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.foryou.truck.parser.LoginJsonParser;
import com.foryou.truck.tools.StringUtils;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ShakeListener;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.AuthCodeView;
import com.foryou.truck.view.TextViewWithIconFont;
import com.foryou.truck.view.WithDelImgEditText;

import java.util.HashMap;
import java.util.Map;

public class EntryLoginActivity extends BaseActivity {
	private Context mContext;
	private String TAG = "EntryLoginActivity";
	@BindView(id = R.id.register, click = true)
	private TextView mRegister;
	@BindView(id = R.id.fetch_password, click = true)
	private TextView mGetPassword;
	@BindView(id = R.id.button1, click = true)
	private Button mLogin;
//	@BindView(id = R.id.back_view, click = true)
//	private RelativeLayout mBackView;
    @BindView(id = R.id.call_phone_tv, click = true)
    private TextView callPhoneTv;
	@BindView(id = R.id.back_btn, click = true)
	private ImageView backBtn;
	@BindView(id = R.id.account_edit)
	private WithDelImgEditText mAccountEdit;
	@BindView(id = R.id.mPassword_edit)
	private WithDelImgEditText mPassowordEdit;
	@BindView(id = R.id.password_iv,click = true)
	private ImageView passwordIv;
	@BindView(id = R.id.yanzhengma_layout,click = true)
	private RelativeLayout getImageCodeRL;// 图形验证码
	@BindView(id = R.id.yanzhengma_iv,click = true)
	private ImageView getImageCodeIv;
	@BindView(id = R.id.AuthCodeView)
	private AuthCodeView mauthCodeView;
	@BindView(id = R.id.yanzhengma_edit)
	private WithDelImgEditText picVerifyCodeEt;


	ProgressBar mProgressBar;
	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.foryou_login);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mLogin.setText("登录");

		if(UtilsLog.isTest){
			ListenerShark();
		}
		initPicVerifyCode();

	}

	private void LoginAccount() {
		String url = UrlConstant.BASE_URL + "/user/login";
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				UtilsLog.i(TAG, "response:" + response);
				cancelProgressDialog();
				LoginJsonParser mParser = new LoginJsonParser();
				int result = mParser.parser(response);
				if (result == 1) {
					if (mParser.entity.status.equals("Y")) {
						SharePerfenceUtil.setKey(mContext, mParser.entity.data.key);
						SharePerfenceUtil.setUid(mContext, mParser.entity.data.uid);
						SharePerfenceUtil.setLoginErrorCount(mContext,0);
						Intent intent = new Intent(mContext, HomeMainScreenActivity.class);
						// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
					} else {
						SharePerfenceUtil.setLoginErrorCount(mContext,SharePerfenceUtil.getLoginErrorCount(mContext)+1);
						initPicVerifyCode();

						TongjiModel.addEvent(mContext, "登录", TongjiModel.TYPE_ERROR, mParser.entity.msg);
						if(!TextUtils.isEmpty(mParser.entity.flag) && mParser.entity.flag.equals("0")){
							ToastUtils.toast(mContext, mParser.entity.msg);
						}else{
							alertDialog("" + mParser.entity.msg + "",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constant.PHONE_NUMBER));
											intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											EntryLoginActivity.this.startActivity(intent);}
									});
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
				//parmas.put("key", SharePerfenceUtil.getKey(mContext));
				//parmas.put("uid", SharePerfenceUtil.getUid(mContext));
				parmas.put("tel", mAccountEdit.getText().toString());
				parmas.put("password", mPassowordEdit.getText().toString());
				//parmas.put("verify_code", picVerifyCodeEt.getText().toString().trim());
				return parmas;
			}
		};
		showProgressDialog();
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		super.onBackPressed();
		Intent intent = new Intent(this, HomeMainScreenActivity.class);
		startActivity(intent);
	}

	private boolean isPassword=true;

	@Override
	public void onClickListener(int id) {
		Intent intent;
		switch (id) {
		case R.id.yanzhengma_iv:
			mauthCodeView.flushView();

			break;
		case R.id.password_iv:
				isPassword=!isPassword;
				StringUtils.setPasswordState(isPassword,mPassowordEdit,passwordIv);
			break;
		case R.id.call_phone_tv:
			Constant.GotoDialPage(mContext, Constant.PHONE_NUMBER);
		  break;
		case R.id.button1:
			String number = mAccountEdit.getText().toString();
			if (!Tools.IsConnectToNetWork(mContext)) {
				ToastUtils.toast(mContext, "联网异常,请稍后再试");
				return;
			}

			if (number.equals("")) {
				ToastUtils.toast(this, "手机号不能为空");
				return;
			}
			if (!Tools.isNumeric(number) || !number.startsWith("1")|| number.length() < 11) {
				ToastUtils.toast(this, "请输入正确的手机号码");
				return;
			}
			if (mPassowordEdit.getText().toString().equals("")) {
				ToastUtils.toast(this, "密码不能为空");
				return;
			}
			if (mPassowordEdit.getText().toString().length() < 6) {
				ToastUtils.toast(this, "密码长度不能低于6位");
				return;
			}

			if (mPassowordEdit.getText().toString().length() < 6) {
				ToastUtils.toast(this, "密码长度不能低于6位");
				return;
			}

			if(isPicVerifyCode){//显示图像验证码的时候
				String codeString = picVerifyCodeEt.getText().toString().trim();
				if(TextUtils.isEmpty(codeString)){
					ToastUtils.toast(this, "请输入验证码");
					return;
				}
			    if (!codeString.equals(mauthCodeView.getAuthCode())) {
				   ToastUtils.toast(this, "验证码不正确，请重新输入");
				   return;
			    }
			}

			LoginAccount();
			break;
		case R.id.register:
			intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.fetch_password:
			intent = new Intent(this, GetPassowrdActivity.class);
			startActivity(intent);
			break;
		case R.id.back_btn:
			onBackPressed();
			break;
		}
	}


	private  boolean isPicVerifyCode=false;

	private void initPicVerifyCode(){
       int count=SharePerfenceUtil.getLoginErrorCount(mContext);
		if(count>=3){
			isPicVerifyCode=true;
			getImageCodeRL.setVisibility(View.VISIBLE);
		}else{
			isPicVerifyCode=false;
			getImageCodeRL.setVisibility(View.GONE);
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		cancelProgressDialog();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}

	@Override
	public void onDestroy(){
		if(shakeListener!=null){
			shakeListener.stop();
		}
		super.onDestroy();
	}


	AlertDialog mListEnvirDialog;
	ShakeListener shakeListener;
	private void ListenerShark(){
		shakeListener = new ShakeListener(this);//创建一个对象
		shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener(){//调用setOnShakeListener方法进行监听

			public void onShake() {
				listDialog();
			}
		});
	}
	private void listDialog() {
		if(mListEnvirDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this,
					AlertDialog.THEME_HOLO_LIGHT);
			final String[] items = new String[4];
			items[0] = "开发环境";
			items[1] = "测试环境";
			items[2] = "正式环境";
			items[3] = "预演环境";

			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ToastUtils.toast(mContext, "您已选择" + items[which]);
					if (which == 0) {
						UrlConstant.BASE_URL = UrlConstant.DEV_URL;
					} else if (which == 1) {
						UrlConstant.BASE_URL = UrlConstant.TEST_URL;
					} else if(which == 2){
						UrlConstant.BASE_URL = UrlConstant.ONLINE_URL;
					}else{
						UrlConstant.BASE_URL = UrlConstant.YUYAN_URL;
					}
					SharePerfenceUtil.saveUrlPath(mContext,UrlConstant.BASE_URL);
				}
			});
			mListEnvirDialog = builder.create();
		}
		mListEnvirDialog.setTitle("当前环境:"+UrlConstant.BASE_URL);
		if(!mListEnvirDialog.isShowing()) {
			mListEnvirDialog.show();
		}
	}
}
