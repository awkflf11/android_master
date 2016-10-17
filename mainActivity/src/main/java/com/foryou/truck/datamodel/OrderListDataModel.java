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
import com.foryou.truck.entity.OrderListEntity;
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
public class OrderListDataModel extends PagedListDataModel<OrderListEntity.OrderContent> {
    public String TAG;
    private int numberPerPage;
    public WeakReference<Context> mContext;
    private String searchval = "";
    private int PageIndex;

    public OrderListDataModel(int numPerPage, Context context, String tag, int pageIndex) {
        mListPageInfo = new ListPageInfo<OrderListEntity.OrderContent>(numPerPage);
        TAG = tag;
        mContext = new WeakReference<Context>(context);
        this.numberPerPage = numPerPage;
        PageIndex = pageIndex;
    }

    private void QuoteOrderList() {
        //String url;// = UrlConstant.BASE_URL + "/order/list";
        Map<String, String> parmams = new HashMap<String, String>();
        parmams.put("page", "" + (mListPageInfo.getStart() / mListPageInfo.getNumPerPage() + 1));
        parmams.put("flag", "" + (PageIndex + 1));
        String url = NormalNetworkRequest.getUrl(mContext.get(), UrlConstant.BASE_URL
                + "/order/list", parmams);

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext.get(), Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "/order/list:" + response);
                        OrderListJsonParser mParser = new OrderListJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                setRequestResult(mParser.entity.data.list,
                                        mParser.entity.data.list.size() == numberPerPage);
                            } else {
                                setRequestFail();
                                ToastUtils.toast(mContext.get(),mParser.entity.msg);
                            }
                        } else {
                            Log.i(TAG, "解析错误");
                            setRequestFail();
                        }
                        EventBus.getDefault().post(""+PageIndex);
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
                EventBus.getDefault().post(""+PageIndex);
                setRequestFail();
            }

        }, true) {

            @Override
            public Map<String, String> getPostBodyData() {
                // TODO Auto-generated method stub
                Map<String, String> parmas = new HashMap<String, String>();

                return parmas;
            }
        };
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    @Override
    protected void doQueryData() {
        if (mContext == null) {
            return;
        }
        QuoteOrderList();
    }
}
