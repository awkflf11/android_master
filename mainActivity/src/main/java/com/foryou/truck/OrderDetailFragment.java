package com.foryou.truck;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
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
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.parser.AreasJsonParser;
import com.foryou.truck.parser.OrderDetailJsonParser;
import com.foryou.truck.parser.ReOrderJsonParser;
import com.foryou.truck.sendproduct.SendProductActivity;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.WithSpreadButton;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//没有报价的订单详情。 询价详情
public class OrderDetailFragment extends Fragment implements
        View.OnClickListener {
    private AgentAndQuoteDetailAct mContext;
    private String TAG = "OrderDetail2Activity";
    private TextView mSendLocal, mReceLocal, mSendTime,
            mProductName, mPayWay, mOrderNumber, mTruckModel, mTruckLenth;
    private RelativeLayout mTruckModelLayout;
    // private TextView mExpertPrice;
    // @BindView(id = R.id.remark)
    private TextView mRemark;
    private RelativeLayout mZhanyongLayout;
    // private TextView mZhangyongline;
    @BindView(id = R.id.zhengche_or_pinche)
    private TextView mZhengche;
    // @BindView(id = R.id.huidan)
    private TextView faPiaoTv;
    private TextView mHuidan;
    // @BindView(id = R.id.recreate_order, click = true)
//    private Button mReCreateOrder;
//    // @BindView(id = R.id.cancel_quote, click = true)
//    private Button mCancelQuote;

    WithSpreadButton mWithSpreadBtn;

    private GridView mGridView;
    private RelativeLayout mCarLenLayout;

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    private void InitView(View view) {
        mSendLocal = (TextView) view.findViewById(R.id.start_place);
        mReceLocal = (TextView) view.findViewById(R.id.end_place);
        mSendTime = (TextView) view.findViewById(R.id.send_time);

        mPayWay = (TextView) view.findViewById(R.id.pay_way);
        mProductName = (TextView) view.findViewById(R.id.product_name);
        mTruckModel = (TextView) view.findViewById(R.id.truck_type);
        mTruckModelLayout = (RelativeLayout) view.findViewById(R.id.car_model_layout);
        mTruckLenth = (TextView) view.findViewById(R.id.truck_length);
        mCarLenLayout = (RelativeLayout) view.findViewById(R.id.car_len_layout);
        // mExpertPrice = (TextView) view.findViewById(R.id.expert_price);
        mZhanyongLayout = (RelativeLayout) view
                .findViewById(R.id.zhanyong_layout);

        mWithSpreadBtn = (WithSpreadButton) view.findViewById(R.id.with_spread_button);
        mWithSpreadBtn.setSpreadFlag(false);
        mWithSpreadBtn.setSecondBtnText("再下一单");
        mWithSpreadBtn.setSpreadOnClickLister(new WithSpreadButton.SpreadOnclickListener() {
            @Override
            public void fistBtnClick() {
                mContext.alertDialog("确定取消询价?",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                // getReOrder();
                                TongjiModel.addEvent(mContext, "询价详情",
                                        TongjiModel.TYPE_BUTTON_CLIKC, "取消询价");
                                cancelQuote();
                            }

                        });
            }

            @Override
            public void secondBtnClick() {
                getReOrder();
                TongjiModel.addEvent(mContext, "询价详情",
                        TongjiModel.TYPE_BUTTON_CLIKC, "再来一单");

            }
        });
        faPiaoTv = (TextView) view.findViewById(R.id.fapiao_tv);
        mHuidan = (TextView) view.findViewById(R.id.huidan);

        mZhengche = (TextView) view.findViewById(R.id.zhengche_or_pinche);
        mRemark = (TextView) view.findViewById(R.id.remark);
        mGridView = (GridView) view.findViewById(R.id.gridview);

        getOrderDetail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        LayoutInflater lf = LayoutInflater.from(getActivity());
        View view = lf.inflate(R.layout.order_detail2, null);
        mOrderNumber = (TextView) view.findViewById(R.id.order_sn);
        InitView(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mContext = (AgentAndQuoteDetailAct) getActivity();
    }

    private void initData(OrderDetailJsonParser parser) {
        mOrderNumber.setText("单号：" + mContext.OrderSn);
        mOrderNumber.setVisibility(android.view.View.VISIBLE);

        mSendLocal.setText(parser.entity.data.sender.address);
        mReceLocal.setText(parser.entity.data.receiver.address);


        if (!TextUtils.isEmpty(parser.entity.data.common_tag)) {
            mGridView.setVisibility(android.view.View.VISIBLE);
            String[] tags = parser.entity.data.common_tag.replace("|", ",")
                    .split(",");
            UtilsLog.i(TAG, "tags.length:" + tags.length);
            if (tags.length > 0) {
                ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
                for (int k = 0; k < tags.length; k++) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("text", tags[k]);
                    lstImageItem.add(map);
                }
                UtilsLog.i(TAG, "lstImageItem.size:" + lstImageItem.size());
                SimpleAdapter myAdapter = new SimpleAdapter(getActivity(),
                        lstImageItem,// 数据来源
                        R.layout.order_detail_beizu_btn,// night_item的XML实现
                        // 动态数组与ImageItem对应的子项
                        new String[]{"text"}, new int[]{R.id.text});

                mGridView.setAdapter(myAdapter);
                mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        if (TextUtils.isEmpty(parser.entity.data.remark)
                && TextUtils.isEmpty(parser.entity.data.common_tag)) {
            mRemark.setText("无");
        } else {
            mRemark.setText(parser.entity.data.remark);
        }

        String producename = parser.entity.data.goods_name + ","
                + parser.entity.data.goods_weight + "吨";
        if (parser.entity.data.goods_cubage.equals("")) {
            mProductName.setText(producename);
        } else {
            if (parser.entity.data.goods_cubage.equals("0")) {// 判断货物体积是否为0
                mProductName.setText(parser.entity.data.goods_name + ","
                        + parser.entity.data.goods_weight + "吨");

            } else {
                mProductName.setText(parser.entity.data.goods_name + ","
                        + parser.entity.data.goods_weight + "吨" + ","
                        + parser.entity.data.goods_cubage + "方");
            }
            UtilsLog.i(TAG, "询价详情----体积=====" + parser.entity.data.goods_cubage);
        }

        mSendTime.setText(parser.entity.data.goods_loadtime + "  "
                + parser.entity.data.goods_loadtime_desc);

        if (!TextUtils.isEmpty(parser.entity.data.car_model)) {
            mTruckModel.setText(parser.entity.data.car_model);
        } else {
            mTruckModelLayout.setVisibility(View.GONE);
        }

        mPayWay.setText(parser.entity.data.pay_type);
        mHuidan.setText(parser.entity.data.need_back);
        faPiaoTv.setText(parser.entity.data.receipt);//发票
        mZhengche.setText(parser.entity.data.car_type);
        if (parser.entity.data.car_type.equals("拼车")) {
            mZhengche.setText("拼车");
            if (!TextUtils.isEmpty(parser.entity.data.occupy_length)
                    && !parser.entity.data.occupy_length.equals("0")) {
                mZhanyongLayout.setVisibility(View.VISIBLE);
                ((TextView) mZhanyongLayout.findViewById(R.id.zhanyong_length))
                        .setText(parser.entity.data.occupy_length + "米");
            } else {
                mZhanyongLayout.setVisibility(View.GONE);
            }
            mCarLenLayout.setVisibility(View.GONE);
        } else {
            mZhengche.setText("整车");
            mZhanyongLayout.setVisibility(android.view.View.GONE);

            if (!TextUtils.isEmpty(parser.entity.data.car_length) && !parser.entity.data.car_length.equals("无")) {
                mTruckLenth.setText(parser.entity.data.car_length + "米");
            } else {
                mCarLenLayout.setVisibility(View.GONE);
            }
        }

        if (!parser.entity.data.status.key.equals("0")) {
            mWithSpreadBtn.setFirstBtnText("取消询价");
        }
    }

    private OrderDetailJsonParser mOrderDetailParser = null;

    private void getOrderDetail() {
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("order_id", mContext.OrderId);
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                + "/order/detail", parmas);
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "response:" + response);
                        mContext.cancelProgressDialog();
                        OrderDetailJsonParser mParser = new OrderDetailJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
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
                mContext.cancelProgressDialog();
            }

        }, true) {
        };

        mContext.showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    private void getReOrder() {
        Map<String, String> parmas = new HashMap<String, String>();
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                + "/order/reOrder", parmas);
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "response:" + response);
                        mContext.cancelProgressDialog();
                        ReOrderJsonParser mParser = new ReOrderJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                Intent intent = new Intent(mContext,
                                        SendProductActivity.class);
                                FirstTabInitValue value = new FirstTabInitValue();
                                value.goods_loadtime = mParser.entity.data.goods_loadtime;
                                value.goods_loadtime_desc = mParser.entity.data.goods_loadtime_desc;
                                value.goods_unloadtime = mParser.entity.data.goods_unloadtime;
                                value.goods_unloadtime_desc = mParser.entity.data.goods_unloadtime_desc;
                                value.goods_load_region = mParser.entity.data.goods_load_region;
                                value.goods_load_address = mParser.entity.data.goods_load_address;
                                value.goods_unload_region = mParser.entity.data.goods_unload_region;
                                value.goods_unload_address = mParser.entity.data.goods_unload_address;
                                value.goods_name = mParser.entity.data.goods_name;
                                value.goods_cubage = mParser.entity.data.goods_cubage;// 货物的体积
                                value.goods_weight = mParser.entity.data.goods_weight;
//								value.car_model = mParser.entity.data.car_model;
//								value.car_length = mParser.entity.data.car_length;
                                value.car_model = mParser.entity.data.car_model.key;
                                value.car_length = mParser.entity.data.car_length.key;

                                if (mOrderDetailParser != null) {
                                    value.car_model_text = mOrderDetailParser.entity.data.car_model;
                                    value.car_length_text = mOrderDetailParser.entity.data.car_length;
                                    if (!TextUtils.isEmpty(value.car_length_text)
                                            && !value.car_length_text.contains("米")) {
                                        value.car_length_text += "米";
                                    }
                                }
                                value.pay_type = mParser.entity.data.pay_type;


                                value.car_type = mParser.entity.data.car_type;

                                value.occupy_length = mParser.entity.data.occupy_length;
                                value.remark = mParser.entity.data.remark;
                                value.expect_price = mParser.entity.data.expect_price;
                                value.receipt = mParser.entity.data.receipt;
                                value.need_back = mParser.entity.data.need_back;

                                value.mutiAndSingleTagText = "";
                                value.mutiAndSingleTagKey = "";
                                for (int i = 0; i < mParser.entity.data.common_tag.size(); i++) {
                                    if (i == mParser.entity.data.common_tag.size() - 1) {
                                        value.mutiAndSingleTagText = value.mutiAndSingleTagText + mParser.entity.data.common_tag.get(i).value;
                                        value.mutiAndSingleTagKey = value.mutiAndSingleTagKey + mParser.entity.data.common_tag.get(i).key;
                                    } else {
                                        value.mutiAndSingleTagText = value.mutiAndSingleTagText + mParser.entity.data.common_tag.get(i).value + ",";
                                        value.mutiAndSingleTagKey = value.mutiAndSingleTagKey + mParser.entity.data.common_tag.get(i).key + ",";
                                    }
                                }
                                value.businessId = mParser.entity.data.businessId;
                                value.businessName = mParser.entity.data.businessName;
                                // Log.i(TAG, "value:" + value);
                                intent.putExtra("firstTabValue", value);
                                startActivity(intent);
                                mContext.overridePendingTransition(R.anim.fade,
                                        R.anim.hold);
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
                mContext.cancelProgressDialog();
            }

        }, true) {

            @Override
            public Map<String, String> getPostBodyData() {
                // TODO Auto-generated method stub
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("order_id", "" + mContext.OrderId);
                return parmas;
            }

        };

        mContext.showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }

    private void cancelQuote() {
        String url = UrlConstant.BASE_URL
                + "/order/quoteCancel";
        mContext.showProgressDialog();
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "response:" + response);
                        mContext.cancelProgressDialog();
                        AreasJsonParser mParser = new AreasJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                ToastUtils.toast(mContext, "取消询价成功");
                                mContext.finish();
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
                mContext.cancelProgressDialog();
            }

        }, true) {

            @Override
            public Map<String, String> getPostBodyData() {
                // TODO Auto-generated method stub
                Map<String, String> map = new HashMap<String, String>();
                map.put("order_id", mContext.OrderId);
                return map;
            }

        };

        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }
}
