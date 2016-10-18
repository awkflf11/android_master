package com.foryou.truck.activity;

import android.content.Context;
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
import com.foryou.truck.BaseActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;

import java.util.HashMap;
import java.util.Map;

/**
 * @des:留言反馈的页面 和 ---问题反馈页面
 */
public class FeedBackAct extends BaseActivity {
    private static final String TAG = "FeedBackAct";
    private Context mContext;
    @BindView(id = R.id.feedback_et)
    private EditText feedbackEt;
    @BindView(id = R.id.commit_feedback_bt, click = true)
    private Button commitFeedbackBt;
	@BindView(id = R.id.callphone_tv, click = true)
	private TextView callPhoneTv;

    private String advice;
    private String order_id;
    private boolean quesBack;// 标记- 是问题反馈还是 留言反馈。true:问题反馈，false:留言反馈

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.act_feedback);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		quesBack = getIntent().getBooleanExtra("ques_back", false);
		order_id = getIntent().getStringExtra("order_id");
		initView();

	}

	public void initView() {
		ShowBackView();
		if (quesBack) {
			setTitle("问题反馈");
			feedbackEt.setHint("请简单完整的描述您遇到的问题");
		} else {
			setTitle("留言反馈");
			feedbackEt.setHint("意见或建议都可以写在这里哟~~~");
		}
	}

	public void onCommitClicked() {
		if (!Tools.IsConnectToNetWork(mContext)) {
			ToastUtils.toast(mContext, "联网异常,请稍后再试");
			return;
		}
		if (feedbackEt.getText().toString().trim().equals("")) {
			ToastUtils.toast(mContext, "您没有填写任何内容");
			return;
		}
		if (!SharePerfenceUtil.IsLogin(mContext)) {
			ToastUtils.toast(this, "请登录后再提交反馈");
			return;
		}
		advice = feedbackEt.getText().toString();
		pushSuggest();
	}


	private void pushSuggest() {
		String url;
		if (quesBack) {
			url = UrlConstant.BASE_URL
					+ "/order/problem";
		} else {
			url = UrlConstant.BASE_URL
					+ "/common/advise";
		}
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
				mContext, Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						UtilsLog.i(TAG, "response:" + response);
						cancelProgressDialog();
						SimpleJasonParser mParser = new SimpleJasonParser();
						// ReOrderJsonParser mParser = new ReOrderJsonParser();
						int result = mParser.parser(response);
						if (result == 1) {
							if (mParser.entity.status.equals("Y")) {
								if (quesBack) {//问题反馈
									 ToastUtils
									 .toast(mContext, "您的反馈我们已经收到，谢谢！");
									finish();
								} else {//留言反馈
									alertDialog("", "您的留言已提交，谢谢您对我们的关心和关注！",true);
								}

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
				if (quesBack) {// 问题反馈
					parmas.put("order_id", order_id);
				}
				parmas.put("content", feedbackEt.getText().toString());
				return parmas;
			}

		};

		showProgressDialog();
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	@Override
	public void onClickListener(int id) {
		switch (id) {
		case R.id.commit_feedback_bt:
			onCommitClicked();
			break;
		case R.id.callphone_tv:
			Constant.GotoDialPage(mContext, Constant.PHONE_NUMBER);
			break;
		default:
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