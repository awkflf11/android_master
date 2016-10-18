package com.foryou.truck.sendproduct;

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
import com.foryou.truck.FirstTabInitValue;
import com.foryou.truck.MyApplication;
import com.foryou.truck.parser.BusinessListJsonParser;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;

/**
 * Created by dubin on 16/7/19.
 */
public class SendProductPresent implements SendProductContract.Present {

    SendProductContract.View mSendProductView;
    FirstTabInitValue mInitValue;

    String TAG;

    public SendProductPresent(SendProductContract.View view, String tag) {
        mSendProductView = view;
        TAG = tag;
    }

    private void getBusinessList() {
        String url = UrlConstant.BASE_URL + "/order/businessList";
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mSendProductView.getContext(), Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "response:" + response);
                        mSendProductView.cancelProgressDialog();
                        BusinessListJsonParser mParser = new BusinessListJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                SharePerfenceUtil.saveBusinessList(mSendProductView.getContext(),response);
                                mSendProductView.showCompanyStrategy(mParser.entity.data);
                            } else {
                                ToastUtils.toast(mSendProductView.getContext(), mParser.entity.msg);
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
                mSendProductView.cancelProgressDialog();
            }

        }, true) {
        };
        mSendProductView.showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    private void setNullDefaultQuoteValue() {
        if (TextUtils.isEmpty(mInitValue.goods_cubage) || mInitValue.goods_cubage.equals("0")) {
            mInitValue.goods_cubage = "";
        }
        if (TextUtils.isEmpty(mInitValue.occupy_length) || mInitValue.occupy_length.equals("0")) {
            mInitValue.occupy_length = "";
        }

        if (TextUtils.isEmpty(mInitValue.car_type)) {
            mInitValue.car_type = "1";
        }
        if (TextUtils.isEmpty(mInitValue.car_model_text)) {
            mInitValue.car_model_text = "";
        }
        if (TextUtils.isEmpty(mInitValue.car_length_text)) {
            mInitValue.car_length_text = "";
        }
        if (TextUtils.isEmpty(mInitValue.remark)) {
            mInitValue.remark = "";
        }

        if (TextUtils.isEmpty(mInitValue.pay_type)) {
            mInitValue.pay_type = "5";
        }
        if (TextUtils.isEmpty(mInitValue.need_back)) {
            mInitValue.need_back = "0";
        }
        if (TextUtils.isEmpty(mInitValue.receipt)) {
            mInitValue.receipt = "0";
        }
    }

    private void initInitailData(){
        setNullDefaultQuoteValue();
        mInitValue.goods_loadtime = "";
        mInitValue.goods_loadtime_desc = "";
    }

    @Override
    public void init(FirstTabInitValue initValue) {
        if(initValue != null){
            mInitValue = initValue;
            initInitailData();
        }
        getBusinessList();
    }
}
