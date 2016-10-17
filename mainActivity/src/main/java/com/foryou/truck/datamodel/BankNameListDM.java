package com.foryou.truck.datamodel;

import android.content.Context;
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
import com.foryou.truck.MyApplication;
import com.foryou.truck.entity.BankNameListEntity;
import com.foryou.truck.entity.OrderListEntity;
import com.foryou.truck.parser.BankNameListJsonParser;
import com.foryou.truck.parser.OrderListJsonParser;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import in.srain.cube.views.list.ListPageInfo;
import in.srain.cube.views.list.PagedListDataModel;

/**
 * Created by dubin on 16/7/16.
 */
public class BankNameListDM extends PagedListDataModel<BankNameListEntity.BankNameItem> {
    public String TAG;
    public Context mContext;

    private int numberPerPage;
    private String searchval = "";

    public BankNameListDM(int numPerPage, Context context, String tag) {
        mListPageInfo = new ListPageInfo<BankNameListEntity.BankNameItem>(numPerPage);
        TAG = tag;
        mContext = context;
        this.numberPerPage = numPerPage;
    }

    @Override
    protected void doQueryData() {

        String url = UrlConstant.BASE_URL + "/bankname/list";
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "bankname/list:" + response);

                        BankNameListJsonParser mParser = new BankNameListJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if(mParser.entity.status.equals("Y")) {
                                setRequestResult(mParser.entity.data.list, mParser.entity.data.list.size() == numberPerPage);
                            }else{
                                setRequestFail();
                            }

                            UtilsLog.i(TAG,"post ....");
                        } else {

                            Log.i(TAG, "解析错误");
                            setRequestFail();
                        }

                        EventBus.getDefault().post("0");
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
                setRequestFail();
                EventBus.getDefault().post("0");
            }

        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                Map<String, String> map = super.getPostBodyData();
                map.put("page",""+(mListPageInfo.getStart()/mListPageInfo.getNumPerPage()+1));
                map.put("pagesize",""+numberPerPage);
                return map;
            }
        };

        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

}
