package com.foryou.truck;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.foryou.truck.activity.AliPayActivity;
import com.foryou.truck.parser.OrderPay2JsonParser;
import com.foryou.truck.parser.OrderPayJsonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.MyActivityManager;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.ClickExpandView;

import java.util.HashMap;
import java.util.Map;

/**
 * @des: 立即支付的页面
 */
public class PayOnLineActivity extends BaseActivity {
    private Context mContext;
    private String TAG = "PayOnLineActivity";
    // boolean resultDisp = false;
    @BindView(id = R.id.give_baoxian)
    private TextView mGiveBaoxian;
    @BindView(id = R.id.baoxian_value)
    private TextView mBaoxianValue;
    @BindView(id = R.id.pay_btn, click = true)
    private Button mPayBtn;
    @BindView(id = R.id.danhao)
    private TextView mDanhao;
    @BindView(id = R.id.orignal_price)
    private TextView mOrignalPrice;
//    @BindView(id = R.id.hongbao_layout)
//    private RelativeLayout mGiftPricelayout;// 代金券
//    @BindView(id = R.id.discount_price)
//    private TextView mDiscountPrice;// 代金券
    @BindView(id = R.id.total_price)
    private TextView mTotalPrice;
    @BindView(id = R.id.jiner_layout)
    private RelativeLayout mOrderPriceLayout;
    @BindView(id = R.id.baoxian_layout)
    private RelativeLayout mBaoxianLayout;
    @BindView(id = R.id.taxation_layout)
    private RelativeLayout taxationLayout;
    @BindView(id = R.id.taxation)
    private TextView taxationTv;//税费
    @BindView(id = R.id.give_baoxian_layout)
    private LinearLayout mGiveBaoxianlayout;
    //
    @BindView(id = R.id.pay_zhifubao_rl, click = true)
    private RelativeLayout payZhiFuBaoRL;// 支付宝和易宝的选项
    @BindView(id = R.id.pay_yibao_rl, click = true)
    private RelativeLayout payYiBaoRL;
    @BindView(id = R.id.pay_zhifubao_iv)
    private ImageView payZhiFuBaoIV;
    @BindView(id = R.id.pay_yibao_iv)
    private ImageView payYiBaoIV;
    @BindView(id = R.id.hint_pay_tv)
    private TextView hintPayTv;//提示信息
    @BindView(id = R.id.back_view,click = true)
    public RelativeLayout mGlobleBackView;//提示信息
    private String payTag = "1";// 1:支付宝 2：易宝
    String order_id, order_price, coupon_value, order_sn;
    boolean fromSendProduct;
    //新增
    @BindView(id = R.id.top_ll, click = true)
    private LinearLayout topLL;
    @BindView(id = R.id.layout_online, click = true)
    private LinearLayout layoutonlineLL;
    @BindView(id = R.id.start_place)
    private TextView startPlaceTv;
    @BindView(id = R.id.end_place)
    private TextView endPlaceTv;
    @BindView(id = R.id.expand_view1)
    private ClickExpandView expandView1;
    @BindView(id = R.id.expand_view2)
    private ClickExpandView expandView2;


    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.pay_online);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // ShowBackView();
        setTitle("立即支付");
        mContext = this;
        initExpand();
        mGlobleBackView.setVisibility(android.view.View.VISIBLE);
        payZhiFuBaoIV.setBackgroundResource(R.drawable.check_on_btn);
        payYiBaoIV.setBackgroundResource(R.drawable.check_off_btn);
        fromSendProduct = getIntent().getBooleanExtra("fromSendProduct", false);
        order_id = getIntent().getStringExtra("order_id");
//        if (!Constant.isNumeric(coupon_value)) {
//            coupon_value = "0";
//        }
        if(fromSendProduct){
            if(getIntent().getStringExtra("insurance_flag").equals("1")){
                hintPayTv.setVisibility(View.VISIBLE);
            }else{
                hintPayTv.setVisibility(View.GONE);
            }
        }else{
            if(!TextUtils.isEmpty(getIntent().getStringExtra("insuranceFlag"))&&!TextUtils.isEmpty(getIntent().getStringExtra("insuranceStatus"))){
                if(getIntent().getStringExtra("insuranceFlag").equals("1")&&getIntent().getStringExtra("insuranceStatus").equals("1")){
                    hintPayTv.setVisibility(View.VISIBLE);
                }else{
                    hintPayTv.setVisibility(View.GONE);
                }
            }
        }
        // mTimeNotice = (TextView) findViewById(R.id.time_notice);
        getOrderPay();
    }

    public void initExpand(){
        expandView1.setIsExpand(false);
        layoutonlineLL.setVisibility(View.GONE);
    }

     public  void  isEpandLogic(boolean boolan){
         if(boolan){
             expandView1.setVisibility(View.GONE);
             layoutonlineLL.setVisibility(View.VISIBLE);
             expandView2.setIsExpand(true);
             expandView1.setIsExpand(true);
         }else{
             expandView1.setVisibility(View.VISIBLE);
             expandView1.setIsExpand(false);
             layoutonlineLL.setVisibility(View.GONE);
         }
     }

    @Override
    public void onClickListener(int id) {
        switch (id) {
            case R.id.top_ll:
                isEpandLogic(!expandView1.getIsExand());
                break;
            case R.id.layout_online:
                isEpandLogic(false);
                break;
            case R.id.pay_zhifubao_rl:
                payZhiFuBaoIV.setBackgroundResource(R.drawable.check_on_btn);
                payYiBaoIV.setBackgroundResource(R.drawable.check_off_btn);
                payTag = "1";
                break;
            case R.id.pay_yibao_rl:
                payZhiFuBaoIV.setBackgroundResource(R.drawable.check_off_btn);
                payYiBaoIV.setBackgroundResource(R.drawable.check_on_btn);
                payTag = "2";
                break;
            case R.id.pay_btn://调支付宝和 易宝的2个页面
                getOrderPayMethod();
                break;
            case R.id.back_view:
                goBackClick();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        goBackClick();
    }

    public void  goBackClick(){
        if (fromSendProduct) {
//            Intent intent = new Intent(this, MyOrderListActivity.class);
//            startActivity(intent);
             // finish();
            alertDialog("确定放弃支付?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Activity agentAndQuoteAct = MyActivityManager.create().findActivity(AgentAndQuoteDetailAct.class);
                    if(agentAndQuoteAct!=null){
                        agentAndQuoteAct.finish();
                    }
                    Intent intent = new Intent(mContext, OrderDetailActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("order_id", order_id);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            alertDialog("确定放弃支付?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
    }

   public void initPayData( OrderPayJsonParser mParser){
       mDanhao.setText(mParser.entity.data.order_sn);
       startPlaceTv.setText(mParser.entity.data.start_place);
       endPlaceTv.setText(mParser.entity.data.end_place);

       if (!TextUtils.isEmpty(mParser.entity.data.order_price)) {
           if (!mParser.entity.data.order_price.equals("0")) {
               mOrderPriceLayout.setVisibility(android.view.View.VISIBLE);
               mOrignalPrice.setText("¥" + mParser.entity.data.order_price);
           }
       }
//       if (!TextUtils.isEmpty(mParser.entity.data.gift_price)) {
//           if (!mParser.entity.data.gift_price.equals("0")) {
//               mGiftPricelayout.setVisibility(android.view.View.VISIBLE);
//               mDiscountPrice.setText("-" + mParser.entity.data.gift_price + "元");
//           }
//       }

       if (!TextUtils.isEmpty(mParser.entity.data.real_price)) {
           mTotalPrice.setText("" + mParser.entity.data.real_price + "元");
       }
       if (!TextUtils.isEmpty(mParser.entity.data.insurance_price)) {
           if (!mParser.entity.data.insurance_price.equals("0")) {
               mBaoxianLayout.setVisibility(android.view.View.VISIBLE);
               mBaoxianValue.setText( mParser.entity.data.insurance_price + "元");
           }
       }
       if (!TextUtils.isEmpty(mParser.entity.data.insurance_given_price)) {
           if (!mParser.entity.data.insurance_given_price.equals("0")) {
               mGiveBaoxianlayout.setVisibility(android.view.View.VISIBLE);
               mGiveBaoxian.setText("-" + mParser.entity.data.insurance_given_price + "元");
           }
       }
       if (!TextUtils.isEmpty(mParser.entity.data.invoice_money)) {
           if (!mParser.entity.data.invoice_money.equals("0")) {
               taxationLayout.setVisibility(android.view.View.VISIBLE);
               taxationTv.setText( mParser.entity.data.invoice_money + "元");
           }
       }

   }

    private void getOrderPay() {
        Map<String, String> parmas = new HashMap<String, String>();
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL+"/order/payInfo", parmas);
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "order/payInfo/response:" + response);
                        cancelProgressDialog();
                        OrderPayJsonParser mParser = new OrderPayJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                initPayData(mParser);

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
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("order_id", "" + order_id);
                //parmas.put("client", "1");
                //parmas.put("price_only", "0");
                return parmas;
            }
        };
        showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    private void getOrderPayMethod() {
        Map<String, String> parmas = new HashMap<String, String>();
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL + "/order/pay", parmas);
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        cancelProgressDialog();
                        OrderPay2JsonParser mParser = new OrderPay2JsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                if (mParser != null) {
                                    Intent intent;
                                    if (payTag.equals("1")) {//跳到支付宝
                                        intent = new Intent(mContext, AliPayActivity.class);
                                        intent.putExtra("payurl", mParser.entity.data.payurl);
                                        PayOnLineActivity.this.startActivityForResult(intent, 101);
                                    } else {
                                        intent = new Intent(mContext, PayOnLineWebActivity.class);
                                        intent.putExtra("payurl", mParser.entity.data.payurl);
                                        PayOnLineActivity.this.startActivityForResult(intent, 100);
                                    }
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
                ToastUtils.toast(mContext,"当前网络异常，请稍后再试");
                cancelProgressDialog();
            }
        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("order_id", "" + order_id);
                if (payTag.equals("2")) {//
                    parmas.put("payment", "" + 1);
                } else {//支付宝
                    parmas.put("payment", "" + 2);
                }
                parmas.put("client", "1");
                return parmas;
            }
        };
        showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                String code = data.getStringExtra("code");
                if (code.equals("9000")) {
                    Activity agentAndQuoteAct = MyActivityManager.create().findActivity(AgentAndQuoteDetailAct.class);
                    if(agentAndQuoteAct!=null){
                        agentAndQuoteAct.finish();
                    }
                    Intent intent = new Intent(mContext, OrderDetailActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("order_id", order_id);
                    startActivity(intent);
                    finish();
                } else {
                    getOrderPay();
                }
            } else {
                getOrderPay();
            }
        } else if (requestCode == 100) {
            getOrderPay();
            AlertDialog.Builder adb;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                adb = new AlertDialog.Builder(mContext,
                        AlertDialog.THEME_HOLO_LIGHT);
            } else {
                adb = new AlertDialog.Builder(mContext);
            }
            adb.setTitle("立即支付");
            adb.setMessage("是否支付成功");
            adb.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    Activity agentAndQuoteAct = MyActivityManager.create().findActivity(AgentAndQuoteDetailAct.class);
                    if(agentAndQuoteAct!=null){
                        agentAndQuoteAct.finish();
                    }
                    Intent intent = new Intent(mContext, OrderDetailActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("order_id", order_id);
                    startActivity(intent);
                    finish();
                }
            });
            adb.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            adb.show();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }

}
