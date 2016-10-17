package com.foryou.truck.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.foryou.truck.BaseActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.parser.OrderDetailJsonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dubin on 16/7/19.
 */
public class DisPlayMapOverLayouAct extends BaseActivity {
    @BindView(id = R.id.bmapView)
    MapView mMapView;
    BaiduMap mBaiduMap;
    String TAG = "DisPlayMapOverLayouAct";
    Context mContext;


    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.display_map_overlay);
    }

    public class ProgressBarAsyncTask extends AsyncTask<Integer, Integer, String> {
        String response;
        OrderDetailJsonParser mParser;
        OverlayOptions ooPolyline;

        public ProgressBarAsyncTask(String response) {
            super();
            this.response = response;
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数
         * 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            mParser = new OrderDetailJsonParser();
            int result = mParser.parser(response);
            if (result == 1 && !TextUtils.isEmpty(response)) {
                if (mParser.entity.status.equals("Y")) {
                    List<LatLng> mList = new ArrayList<LatLng>();
                    int count = mParser.entity.data.location.size();
                    UtilsLog.i(TAG,"count:"+count);
                    for (int i = 0; i < count; i++) {
                        LatLng latLng = new LatLng(Double.valueOf(mParser.entity.data.location.get(i).lat),
                                Double.valueOf(mParser.entity.data.location.get(i).lng));
                        mList.add(latLng);
                    }
                    ooPolyline = new PolylineOptions().width(10)
                            .color(0xff00a9e0).points(mList);
                } else {
                    ToastUtils.toast(mContext, mParser.entity.msg);
                }
            } else {
                Log.i(TAG, "解析错误");
            }
            return "";
        }


        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            cancelProgressDialog();
            if(ooPolyline!=null) {
                mBaiduMap.addOverlay(ooPolyline);
            }
        }
    }

    private void getOrderDetail(String order_id) {
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
                        //UtilsLog.i(TAG, "response:" + response);
                        new ProgressBarAsyncTask(response).execute();
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
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        String order_id = getIntent().getStringExtra("order_id");
        getOrderDetail(order_id);
    }

    @Override
    public void onClickListener(int id) {

    }
}
