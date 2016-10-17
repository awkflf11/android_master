package com.foryou.truck.activity.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

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
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.parser.OrderDetailJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dubin on 16/7/22.
 */
public class OrderDetailAct extends BaseActivity {

    @BindView(id = R.id.order_detail_layout)
    LinearLayout mOrderDetailLayout;

    private String TAG = "OrderDetailAct";
    private String order_id;
    private Context mContext;

    OrderDetailJsonParser mOrderDetailParser;

    private void CuiChuArrayDriver(){
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("order_id", order_id);
        NetWorkUtils.cuiChuArrayDriver(this, TAG, parmas);
    }

    private void RefreshLocation() {
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("order_id", "" + order_id);
        NetWorkUtils.reFreshLocation(this, TAG, parmas,
                new NetWorkUtils.NetJsonRespon() {
                    @Override
                    public void onRespon(BaseJsonParser parser) {
                        // TODO Auto-generated method stub
                        AlertDialog.Builder adb;
                        if (android.os.Build.VERSION.SDK_INT >= 11) {
                            adb = new AlertDialog.Builder(mContext,
                                    AlertDialog.THEME_HOLO_LIGHT);
                        } else {
                            adb = new AlertDialog.Builder(mContext);
                        }

                        SimpleJasonParser mParser = (SimpleJasonParser) parser;
                        adb.setTitle("更新当前位置");
                        adb.setMessage(mParser.entity.msg);
                        adb.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        getOrderDetail();
                                    }
                                });
                        adb.show();
                    }
                });
    }

    private void initData(OrderDetailJsonParser mParser){
        mOrderDetailLayout.removeAllViews();

        BaiduMapView view = (BaiduMapView)LayoutInflater.from(mContext).inflate(R.layout.map_layout,null);
        view.initData(mParser.entity.data);
        mOrderDetailLayout.addView(view);



    }

    private void getOrderDetail() {
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("order_id", order_id);
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                + "/order/detail", parmas);

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "response:" + response);
                        cancelProgressDialog();
                        OrderDetailJsonParser mParser = new OrderDetailJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1 && !TextUtils.isEmpty(response)) {
                            if (mParser.entity.status.equals("Y")) {
                                mOrderDetailParser = mParser;
                                initData(mParser);
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

    @Subscribe
    protected void onEvent(String msg){
        if(msg.equals(RefreshDriverLocView.REFRESH_LOC)){
            RefreshLocation();
        }else if(msg.equals(NotArrayDriverView.CUICHU_ARRAY_DRIVER)){
            CuiChuArrayDriver();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        getOrderDetail();
    }

    @Override
    protected void onPause(){
        super.onPause();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.order_detail_new);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("运单详情");
        ShowBackView();

        mContext = this;
    }

    @Override
    public void onClickListener(int id) {

    }


}
