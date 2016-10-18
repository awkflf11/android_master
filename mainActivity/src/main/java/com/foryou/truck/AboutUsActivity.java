package com.foryou.truck;

import android.content.Context;
import android.os.Bundle;
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
import com.foryou.truck.parser.AboutUsJsonParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;

//
public class AboutUsActivity extends BaseActivity {
    private static final String TAG = "AboutUsActivity";
    private Context mContext;

    @BindView(id = R.id.about_us)
    private TextView mContent;

    //
    @Override
    public void setRootView() {
        // TODO Auto-generated method stub
        super.setRootView();
        setContentView(R.layout.about_us);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        initView();
        getContent();
    }

    public void initView() {
        ShowBackView();
        setTitle("关于我们");
    }

    public void getContent() {
        if (!Tools.IsConnectToNetWork(mContext)) {
            ToastUtils.toast(mContext, "联网异常,请稍后再试");
            return;
        }
        getAboutUs();
    }

    private void getAboutUs() {
        String url = UrlConstant.BASE_URL + "/common/about";
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "response:" + response);
                        cancelProgressDialog();
                        AboutUsJsonParser mParser = new AboutUsJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                mContent.setText(mParser.entity.data.about);
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

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        cancelProgressDialog();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }

}
