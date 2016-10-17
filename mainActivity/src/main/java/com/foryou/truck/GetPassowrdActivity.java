package com.foryou.truck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.foryou.truck.parser.LoginJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.WithDelImgEditText;

import java.util.HashMap;
import java.util.Map;

/**
 * @des:找回密码的页面
 */
public class GetPassowrdActivity extends BaseActivity {
	private String TAG = "GetPassowrdActivity";
	private Context mContext;
	@BindView(id = R.id.title)
	private TextView mTitle;
	@BindView(id = R.id.next_step, click = true)
	private Button mNextStep;
	@BindView(id = R.id.get_yanzhengma, click = true)
	private Button mGetYanzhengma;
	@BindView(id = R.id.voice_verify, click = true)
	private TextView mVoiceVerify;
	@BindView(id = R.id.account_edit)
	private WithDelImgEditText mAccountEdit;
	@BindView(id = R.id.yanzhengma)
	private WithDelImgEditText mYanzhengmaEdit;
	private SimpleJasonParser mYanzhengmaParser;
	private LoginJsonParser mVerifyYanzhengmaParser;
	private int COUNT_SECONDS = 60;
	private long mGetVoiceVerifyTime = 0;
	private boolean voiceYanzheng = false;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.get_password);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		// DistanceUtil
		mTitle.setText("找回密码");
		ShowBackView();

//		String content = "语音验证码";
//		SpannableString msp = new SpannableString(content);
//		msp.setSpan(new UnderlineSpan(), 0, content.length(),
//				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//		mVoiceVerify.setText(msp);

	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mGetYanzhengma.setText(COUNT_SECONDS + "s");
				COUNT_SECONDS--;
				if (COUNT_SECONDS < 0) {
					mGetYanzhengma.setText("获取验证码");
					mGetYanzhengma.setTextColor(Color.parseColor("#ff6900"));
					COUNT_SECONDS = 60;
					mGetYanzhengma.setEnabled(true);
					mGetYanzhengma.setBackgroundResource(R.drawable.border_btn_getcode);
					//mGetYanzhengma.setBackgroundResource(R.drawable.login_btn);
				} else {
					mHandler.sendEmptyMessageDelayed(0, 1000);
				}
				break;
			case 1:
				mVoiceVerify.setTextColor(Color.RED);
				break;
			}

		}
	};

	private void getYanzhengma() {
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("tel", mAccountEdit.getText().toString());
		parmas.put("type", "2");
		if (voiceYanzheng) {
			parmas.put("extend", "voice");
		} else {
			parmas.put("extend", "text");
		}
		String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
				 + "/common/verify_code", parmas);
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
				mContext, Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						UtilsLog.i(TAG, "response:" + response);
						cancelProgressDialog();
						mYanzhengmaParser = new SimpleJasonParser();
						// Log.i(TAG, "result:" + Tools.UnicodeDecode((String)
						// msg.obj));
						int result = mYanzhengmaParser.parser(response);
						if (result == 1) {
							if (mYanzhengmaParser.entity.status.equals("Y")) {
								// Intent intent = new Intent(mContext,
								// BeginQueryPrice.class);
								// startActivity(intent);
								ToastUtils.toast(mContext, "验证码获取成功");
								if (!voiceYanzheng) {
									mGetYanzhengma.setEnabled(false);
									//mGetYanzhengma.setBackgroundResource(R.drawable.anniuhui);
									mGetYanzhengma.setBackgroundResource(R.drawable.border_btn_getcode_hui);
									mGetYanzhengma.setTextColor(Color.parseColor("#999999"));
									mGetYanzhengma.setText(COUNT_SECONDS + "s");
									COUNT_SECONDS--;
									mHandler.sendEmptyMessageDelayed(0, 1000);
								}
							} else {
								ToastUtils.toast(mContext,
										mYanzhengmaParser.entity.msg);
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

	// ################
	private void VerifyYanzhengma() {
		String url = UrlConstant.BASE_URL
				+ "/user/forget";

		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
				mContext, Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						UtilsLog.i(TAG, "response:" + response);
						cancelProgressDialog();
						mVerifyYanzhengmaParser = new LoginJsonParser();
						int result = mVerifyYanzhengmaParser.parser(response);
						if (result == 1) {
							if (mVerifyYanzhengmaParser.entity.status
									.equals("Y")) {
								SharePerfenceUtil.setKey(mContext, mVerifyYanzhengmaParser.entity.data.key);
								SharePerfenceUtil.setUid(mContext, mVerifyYanzhengmaParser.entity.data.uid);
								ToastUtils.toast(mContext, "验证通过");
								Intent intent = new Intent(mContext, SetNewPasswordActivity.class);
								startActivity(intent);
							} else {
								//ToastUtils.toast(mContext, mVerifyYanzhengmaParser.entity.msg);
                                //Constant.PHONE_NUMBER
								if(mVerifyYanzhengmaParser.entity.flag.equals("1")){
									 alertDialog("" + mVerifyYanzhengmaParser.entity.msg
									 + "",
									 new DialogInterface.OnClickListener() {
									 @Override
									 public void onClick(DialogInterface
									 dialog, int which) {
										 Intent intent = new Intent(Intent.ACTION_DIAL, Uri
												 .parse("tel:" + Constant.PHONE_NUMBER));
										 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										 GetPassowrdActivity.this.startActivity(intent);}
									 });
								} else{
									ToastUtils.toast(mContext, mVerifyYanzhengmaParser.entity.msg);
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
				// TODO Auto-generated method stub
				Map<String, String> parmas = new HashMap<String, String>();
				parmas.put("key", SharePerfenceUtil.getKey(mContext));
				parmas.put("uid", SharePerfenceUtil.getUid(mContext));
				parmas.put("tel", mAccountEdit.getText().toString());
				parmas.put("verify_code", mYanzhengmaEdit.getText().toString());
				return parmas;
			}

		};

		showProgressDialog();
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	// ################# 得到验证码：
	// Handler mGetYanzhengmaHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// cancelProgressDialog();
	// if (MLHttpConnect2.SUCCESS == msg.what) {
	// Log.i(TAG, "result:" + Tools.UnicodeDecode((String) msg.obj));
	// if (mYanzhengmaParser.entity.status.equals("Y")) {
	// String result = (String) msg.obj;
	// // Intent intent = new Intent(mContext,
	// // BeginQueryPrice.class);
	// // startActivity(intent);
	// Log.i(TAG, "result:" + result);
	// ToastUtils.toast(mContext, "验证码获取成功");
	// if (!voiceYanzheng) {
	// mGetYanzhengma.setEnabled(false);
	// mGetYanzhengma.setText(COUNT_SECONDS + "秒后重试");
	// COUNT_SECONDS--;
	// mHandler.sendEmptyMessageDelayed(0, 1000);
	// }
	// } else {
	// ToastUtils.toast(mContext, mYanzhengmaParser.entity.msg);
	// }
	// } else {
	// Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
	// }
	// super.handleMessage(msg);
	// }
	//
	// };
	//
	// private void getYanzhengma() {
	// showProgressDialog();
	// mYanzhengmaParser = new SimpleJasonParser();
	// Map<String, String> parmas = new HashMap<String, String>();
	// parmas.put("tel", mAccountEdit.getText().toString());
	// parmas.put("type", "2");
	// if (voiceYanzheng) {
	// parmas.put("extend", "voice");
	// } else {
	// parmas.put("extend", "text");
	// }
	// MLHttpConnect.GetYanzhengma(this, parmas, mYanzhengmaParser,
	// mGetYanzhengmaHandler);
	// }

	// ##############验证码：
	// Handler mVerifyYanzhengmaHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// cancelProgressDialog();
	// if (MLHttpConnect2.SUCCESS == msg.what) {
	// if (mVerifyYanzhengmaParser.entity.status.equals("Y")) {
	//
	// String result = (String) msg.obj;
	// Log.i(TAG, "result:" + result);
	//
	// SharePerfenceUtil.setKey(mContext,
	// mVerifyYanzhengmaParser.entity.data.key);
	// SharePerfenceUtil.setUid(mContext,
	// mVerifyYanzhengmaParser.entity.data.uid);
	// ToastUtils.toast(mContext, "验证通过");
	// Intent intent = new Intent(mContext, SetNewPasswordActivity.class);
	// startActivity(intent);
	//
	// } else {
	// ToastUtils.toast(mContext, mVerifyYanzhengmaParser.entity.msg);
	// }
	// } else {
	// Toast.makeText(mContext, "网络连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
	// }
	// super.handleMessage(msg);
	// }
	//
	// };
	//
	// private void VerifyYanzhengma() {
	// showProgressDialog();
	// mVerifyYanzhengmaParser = new RegisterJsonParser();
	// Map<String, String> parmas = new HashMap<String, String>();
	// parmas.put("tel", mAccountEdit.getText().toString());
	// parmas.put("verify_code", mYanzhengmaEdit.getText().toString());
	// MLHttpConnect.VerifyYanzhengma(this, parmas, mVerifyYanzhengmaParser,
	// mVerifyYanzhengmaHandler);
	// }
	//

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub
		String number = mAccountEdit.getText().toString();
		switch (id) {
		case R.id.next_step:
			if (number.equals("")) {
				ToastUtils.toast(this, "手机号不能为空!");
				return;
			}
			if (!Tools.isNumeric(number) || !number.startsWith("1")||number.length() < 11) {
				ToastUtils.toast(this, "请输入正确的手机号码");
				return;
			}
			if (mYanzhengmaEdit.getText().toString().equals("")) {
				ToastUtils.toast(this, "验证码不能为空");
				return;
			}
			VerifyYanzhengma();
			break;
		case R.id.get_yanzhengma:
			if (number.equals("")) {
				ToastUtils.toast(this, "手机号不能为空!");
				return;
			}
			if (!Tools.isNumeric(number) || !number.startsWith("1")||number.length() < 11) {
				ToastUtils.toast(this, "请输入正确的手机号码");
				return;
			}

			voiceYanzheng = false;
			getYanzhengma();
			break;
		case R.id.back_view:
			finish();
			break;
		case R.id.voice_verify:
			if (number.equals("")) {
				ToastUtils.toast(this, "手机号不能为空");
				return;
			}
			if (!Tools.isNumeric(number) || !number.startsWith("1")||number.length() < 11) {
				ToastUtils.toast(this, "请输入正确的手机号码");
				return;
			}

			if (System.currentTimeMillis() - mGetVoiceVerifyTime > 60 * 1000) {
				mVoiceVerify.setTextColor(Color.GRAY);
				mHandler.sendEmptyMessageDelayed(1, 1000 * 60);
				voiceYanzheng = true;
				mGetVoiceVerifyTime = System.currentTimeMillis();
				getYanzhengma();
			} else {
				ToastUtils.toast(this, "60秒内只能获取一次验证码,请稍后再试");
			}
			break;
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		cancelProgressDialog();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}

}
