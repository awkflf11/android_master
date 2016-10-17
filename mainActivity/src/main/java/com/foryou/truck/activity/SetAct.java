package com.foryou.truck.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import com.foryou.truck.AboutUsActivity;
import com.foryou.truck.BaseActivity;
import com.foryou.truck.EntryLoginActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.parser.UserDetailJsonParser;
import com.foryou.truck.tools.BaiduUpdate;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import java.util.HashMap;
import java.util.Map;

/**
 * @des:设置的页面
 */
public class SetAct extends BaseActivity {
	private static final String TAG = "SetAct";
	private Context mContext;
	@BindView(id = R.id.about_us_rl, click = true)
	private RelativeLayout abuotUsBtn;
	@BindView(id = R.id.update_rl, click = true)
	private RelativeLayout updateBtn;
	@BindView(id = R.id.verson_name_tv2)
	private TextView versonNameTv2;
	@BindView(id = R.id.exit_login_bt, click = true)
	private Button exit_login_bt;
	private String versionName = "";
	private BaiduUpdate baiduUpdate;


	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.act_set);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		initView();
		initBaidu();

	}
	private void initBaidu(){
		baiduUpdate = new BaiduUpdate(mContext,true);
		baiduUpdate.initNotification();
	}

	private void initView() {
		ShowBackView();
		setTitle("设置");
		try {
			versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		versonNameTv2.setText("当前版本" + versionName);

	}

	@Override
	public void onClickListener(int id) {
		switch (id) {
		case R.id.about_us_rl:
			Intent intent = new Intent();
			intent.setClass(this, AboutUsActivity.class);
			startActivity(intent);

			break;
		case R.id.update_rl:// 自动更新
			baiduUpdate.baiduUpdate(4);

			break;
		case R.id.exit_login_bt:
			TongjiModel.addEvent(mContext, "设置", TongjiModel.TYPE_BUTTON_CLIKC, "退出登录");
			 
			if (!Tools.IsConnectToNetWork(mContext)) {
				ToastUtils.toast(mContext, "联网异常,请稍后再试");
				return;
			}

			// exitLogin();
			unBindGt();
			break;

		default:
			break;
		}

	}

	private void unBindGt() {
		showProgressDialog();
		String url = UrlConstant.BASE_URL + "/user/bindGt";
		UtilsLog.i(TAG, "url:" + url);
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				UtilsLog.i(TAG, response);
				cancelProgressDialog();
				UserDetailJsonParser mParser = new UserDetailJsonParser();
				int result = mParser.parser(response);
				if (result == 1) {
				} else {
					Log.i(TAG, "/user/bindGt 解析错误");
					// ToastUtils.toast(mContext, mParser.entity.msg);
				}
				SharePerfenceUtil.ClearAll(mContext);
				finish();
				Intent intent = new Intent(mContext, EntryLoginActivity.class);
				startActivity(intent);
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
				SharePerfenceUtil.ClearAll(mContext);
				finish();
				Intent intent = new Intent(mContext, EntryLoginActivity.class);
				startActivity(intent);
				// ToastUtils.toast(mContext, "加载数据失败");
			}
		}, true) {
			@Override
			public Map<String, String> getPostBodyData() {
				// TODO Auto-generated method stub
				Map<String, String> parmas = new HashMap<String, String>();
				parmas.put("gt_id", "aaa");
				parmas.put("os_type", "" + 0);
				return parmas;
			}
		};
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		baiduUpdate.cancelDailog();

		cancelProgressDialog();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}
}