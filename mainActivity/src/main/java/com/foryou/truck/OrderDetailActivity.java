package com.foryou.truck;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.foryou.truck.activity.DisplayPdf;
import com.foryou.truck.activity.FeedBackAct;
import com.foryou.truck.activity.LookBaoDanAct;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.UserDetailEntity;
import com.foryou.truck.net.HttpApi;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.parser.BdrenderReverseParser;
import com.foryou.truck.parser.OrderDetailJsonParser;
import com.foryou.truck.parser.ReOrderJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.sendproduct.SendProductActivity;
import com.foryou.truck.tools.ImageTools;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.SpinnerPopUpListView;
import com.foryou.truck.view.WithSpreadButton;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//运单详情
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class OrderDetailActivity extends BaseActivity {
    // private double mLatitude, mLongitude;
    private Context mContext;
    private String TAG = "OrderDetailActivity";
    private Button mDetailPlace;
    RelativeLayout mMapLayout;

    @BindView(id = R.id.button_layout)
    WithSpreadButton mWithSpreadButton;

    LinearLayout mDriverInfoLayout, mNotArrayDriverLayout;
    RelativeLayout mAgentPhoneLayout, mDriverPhoneLayout;
    @BindView(id = R.id.bmapView)
    private ImageView mMapView;
    @BindView(id = R.id.right_img, click = true)
    private ImageView mRightBtn;
    @BindView(id = R.id.title_layout)
    RelativeLayout mTopView;
    @BindView(id = R.id.zhengche_or_pinche)
    private TextView mZhengche;
    @BindView(id = R.id.kaifapiao)
    private TextView mFapiao;
    @BindView(id = R.id.huidan)
    private TextView mHuidan;
    @BindView(id = R.id.remark)
    private TextView mRemark;
    @BindView(id = R.id.driver_name)
    private TextView mDriverName;
    @BindView(id = R.id.driver_phone)
    private TextView mDriverPhone;
    @BindView(id = R.id.jijiren_name)
    private TextView mJinjiName;
    @BindView(id = R.id.jijiren_phone)
    private TextView mJinjiPhone;
    @BindView(id = R.id.send_name)
    private TextView mSendName;
    @BindView(id = R.id.send_phone, click = true)
    private TextView mSendPhone;
    @BindView(id = R.id.send_address)
    private TextView mSendAddr;
    @BindView(id = R.id.rece_name)
    private TextView mReceName;
    @BindView(id = R.id.rece_phone, click = true)
    private TextView mRecePhone;
    @BindView(id = R.id.rece_address)
    private TextView mReceAddr;
    @BindView(id = R.id.product_name)
    private TextView mProductName;
    @BindView(id = R.id.send_time)
    private TextView mSendTime;
    @BindView(id = R.id.price)
    private TextView mPrice;

    @BindView(id = R.id.yunfeizonger_layout)
    RelativeLayout mYunfeiZongerLayout;

    @BindView(id = R.id.yunfeiandbaoxian)
    private TextView mPriceHint;
    @BindView(id = R.id.pay_way)
    private TextView mPayWay;
    // @BindView(id = R.id.send_local)
    // private TextView mSendLocal;
    @BindView(id = R.id.truck_type)
    private TextView mTruckModel;
    @BindView(id = R.id.truck_length)
    private TextView mTruckLenth;
    @BindView(id = R.id.car_model_layout)
    private RelativeLayout mCarModelLayout;
    @BindView(id = R.id.car_len_layout)
    private RelativeLayout mCarLenLayout;
    @BindView(id = R.id.send_phone2, click = true)
    private TextView mSendPhone2;
    @BindView(id = R.id.send_phone2_layout)
    private TextView mSendPHone2Layout;
    @BindView(id = R.id.rece_phone2, click = true)
    private TextView mRecePhone2;
    @BindView(id = R.id.rece_phone2_layout)
    private RelativeLayout mRecePhone2Layout;

    @BindView(id = R.id.driver_plate)
    private TextView mDriverPlate;
    @BindView(id = R.id.insurance_layout)
    private LinearLayout mInsuranceLayout;//保险信息模块
    @BindView(id = R.id.insurance_values)
    private TextView mInsuranceValue;
    @BindView(id = R.id.insurance_name)
    private TextView mInsuranceName;
    @BindView(id = R.id.insurance_number)
    private TextView mInsuranceNumber;
    @BindView(id = R.id.insurance_package)
    private TextView mInsurancePackage;
    @BindView(id = R.id.insurance_type)
    private TextView mInsuranceType;
    @BindView(id = R.id.insurance_price)
    private TextView mInsurancePrice;
    @BindView(id = R.id.pay_status_layout)
    private RelativeLayout mPayStatusLayout;
    // @BindView(id = R.id.pay_msg)
    // private TextView mPayMsg;
    @BindView(id = R.id.urge_pay_layout)
    private LinearLayout mUrgePayLayout;
    @BindView(id = R.id.agent_name_layout)
    private LinearLayout mAgentNameLayout;
    @BindView(id = R.id.order_sn_agent)
    private TextView mOrderSnAgent;
    @BindView(id = R.id.order_sn_agent_layout)
    private RelativeLayout mOrderSnAgentLayout;
    @BindView(id = R.id.call_center, click = true)
    private TextView mCallCenter;
    @BindView(id = R.id.tax_layout)
    private RelativeLayout mTaxLayout;
    @BindView(id = R.id.tax_value)
    private TextView mTaxValue;
    @BindView(id = R.id.fapiao_taitou_layout)
    private RelativeLayout mFapiaoTaitouLayout;
    @BindView(id = R.id.fapiao_taitou_value)
    private TextView mFapiaoValue;
    @BindView(id = R.id.change_agent_layout)
    private RelativeLayout mChangeAgentLayout;
    @BindView(id = R.id.change_agent_value)
    private TextView mChangeAgentValue;
    private TextView mMapline;
    private TextView mRefreshLocate;
    private TextView mCuichuArray;
    private String order_id;
    private LinearLayout mMainMapLayout;
    private RelativeLayout mZhanyongLayout;
    // private TextView mZhangyongline;
    @BindView(id = R.id.order_sn)
    private TextView mOrderSn;
    @BindView(id = R.id.top_pay_msg)
    private TextView mTopPayMsg;
    private GridView mGridView;
    @BindView(id = R.id.coupon_layout)
    private RelativeLayout mCouponLayout;
    private boolean fromGT = false;
    private String insuranceFlag = "";//是否显示保险
    private String insuranceStatus = "";//保险是否失效
    private String isWuYouBao = "0";// 0表示有无忧保，1有无忧保

    Handler mGetAddressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mDetailPlace.setText(mBdParser.entity.result.formatted_address);
        }
    };

    private BdrenderReverseParser mBdParser;

    private void getCurrentAddress() {
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String mStringresult = HttpApi
                            .getString((Tools.getMapUrlWitchLat(
                                    Double.valueOf(mOrderDetailParser.entity.data.location
                                            .get(mOrderDetailParser.entity.data.location
                                                    .size() - 1).lat),
                                    Double.valueOf(mOrderDetailParser.entity.data.location
                                            .get(mOrderDetailParser.entity.data.location
                                                    .size() - 1).lng))));
                    // Log.i("aa", "mStringresult:" + mStringresult);
                    mBdParser = new BdrenderReverseParser();
                    mBdParser.parser(mStringresult.substring(
                            mStringresult.indexOf("{"),
                            mStringresult.length() - 1));
                    Log.i("aa", "mStringresult:"
                            + mBdParser.entity.result.formatted_address);
                    mGetAddressHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {
        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
            Log.i("aa", "load map finish() .........");
            mMapView.setBackgroundDrawable(ImageTools.bitmapToDrawable(arg2));
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {
        }
    };

    private void InitData(OrderDetailJsonParser parser) {
        if (!TextUtils.isEmpty(parser.entity.data.pay_type)&& parser.entity.data.pay_type.contains("在线支付")
                && "0".equals(parser.entity.data.is_payed)
                && !TextUtils.isEmpty(parser.entity.data.msg.pay)) {
            mTopPayMsg.setText(parser.entity.data.msg.pay);
            mUrgePayLayout.setVisibility(android.view.View.VISIBLE);
            mNotArrayDriverLayout.setVisibility(android.view.View.GONE);
            mDriverInfoLayout.setVisibility(android.view.View.GONE);
            mAgentNameLayout.setVisibility(android.view.View.GONE);
            mAgentPhoneLayout.setVisibility(android.view.View.GONE);
            mOrderSnAgentLayout.setVisibility(android.view.View.GONE);
            mOrderSn.setVisibility(android.view.View.VISIBLE);
        } else {
            mUrgePayLayout.setVisibility(android.view.View.GONE);
            mOrderSnAgentLayout.setVisibility(android.view.View.VISIBLE);
            mOrderSn.setVisibility(android.view.View.GONE);

            if ( parser.entity.data.driver==null|| TextUtils.isEmpty(parser.entity.data.driver.name)) {
                mDriverInfoLayout.setVisibility(android.view.View.GONE);
                if (parser.entity.data.status.key.equals("0")) {
                    mNotArrayDriverLayout.setVisibility(android.view.View.GONE);
                } else {
                    mNotArrayDriverLayout
                            .setVisibility(android.view.View.VISIBLE);
                }
            } else {
                mDriverInfoLayout.setVisibility(android.view.View.VISIBLE);
                mNotArrayDriverLayout.setVisibility(android.view.View.GONE);
                mDriverName.setText(parser.entity.data.driver.name);
                if (parser.entity.data.driver.locate.equals("0")) {
                    mDriverName.setText(mDriverName.getText().toString() + "  (未开启定位)");
                    mRefreshLocate.setVisibility(android.view.View.GONE);
                } else {
                    if (parser.entity.data.status.key.equals("0")) {
                        mRefreshLocate.setVisibility(android.view.View.GONE);
                    } else {
                        mRefreshLocate.setVisibility(android.view.View.VISIBLE);
                    }
                }
                mDriverPhone.setText(parser.entity.data.driver.mobile);
                mDriverPlate.setText(parser.entity.data.driver.plate);
            }
            mJinjiName.setText(parser.entity.data.agent.name);
            mJinjiPhone.setText(parser.entity.data.agent.mobile);
        }

        mOrderSn.setText("单号：" + parser.entity.data.order_sn);
        mOrderSnAgent.setText("单号：" + parser.entity.data.order_sn);
        mSendName.setText(parser.entity.data.sender.name);
        mSendPhone.setText(parser.entity.data.sender.tel);
        if (parser.entity.data.sender.tel2 != null) {
            if (!parser.entity.data.sender.tel2.equals("")) {
                mSendPHone2Layout.setVisibility(View.VISIBLE);
                mSendPhone2.setText(parser.entity.data.sender.tel2);
            } else {
                mSendPHone2Layout.setVisibility(View.GONE);
            }
        } else {
            mSendPHone2Layout.setVisibility(View.GONE);
        }

        mSendAddr.setText(parser.entity.data.sender.address);

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
                SimpleAdapter myAdapter = new SimpleAdapter(this, lstImageItem,// 数据来源
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

        mReceName.setText(parser.entity.data.receiver.name);
        mRecePhone.setText(parser.entity.data.receiver.tel);
        if (parser.entity.data.receiver.tel2 != null) {
            if (!parser.entity.data.receiver.tel2.equals("")) {
                mRecePhone2.setText(parser.entity.data.receiver.tel2);
                mRecePhone2Layout.setVisibility(View.VISIBLE);
            } else {
                mRecePhone2Layout.setVisibility(View.GONE);
            }
        } else {
            mRecePhone2Layout.setVisibility(View.GONE);
        }

        mReceAddr.setText(parser.entity.data.receiver.address);


        String producename = parser.entity.data.goods_name + ","
                + parser.entity.data.goods_weight + "吨";
        if (parser.entity.data.goods_cubage.equals("")) {
            mProductName.setText(producename);
        } else {// 货物的体积
            if (parser.entity.data.goods_cubage.equals("0")) {
                mProductName.setText(parser.entity.data.goods_name + ","
                        + parser.entity.data.goods_weight + "吨");
            } else {
                mProductName.setText(parser.entity.data.goods_name + ","
                        + parser.entity.data.goods_weight + "吨" + ","
                        + parser.entity.data.goods_cubage + "方");
            }

        }
        mSendTime.setText(parser.entity.data.goods_loadtime + "  "
                + parser.entity.data.goods_loadtime_desc);

        mPrice.setText("¥" + Tools.formatNumber(parser.entity.data.order_price));
        mPayWay.setText(parser.entity.data.pay_type);
        if (!TextUtils.isEmpty(parser.entity.data.car_model)) {
            mTruckModel.setText(parser.entity.data.car_model);
        } else {
            mCarModelLayout.setVisibility(View.GONE);
        }

        mFapiao.setText(parser.entity.data.receipt);
        mHuidan.setText(parser.entity.data.need_back);

        UserDetailEntity mUserDetail = SharePerfenceUtil.getUserDetail(mContext);
        if (mUserDetail.data.type.equals("0") && (parser.entity.data.pay_type.contains("在线支付")
                || parser.entity.data.insurance.flag.equals("1"))) {
            TextView payStatus = (TextView) mPayStatusLayout.findViewById(R.id.pay_status);
            if (parser.entity.data.is_payed.equals("1")) {
                payStatus.setText("已支付");
            } else {
                payStatus.setText("未支付");
            }
        } else {
            mPayStatusLayout.setVisibility(View.GONE);
        }

        UtilsLog.i(TAG, "car_type:" + parser.entity.data.car_type);
        mZhengche.setText(parser.entity.data.car_type);

        if (!parser.entity.data.car_type.equals("整车")) {//拼车
            if (!TextUtils.isEmpty(parser.entity.data.occupy_length)
                    && !parser.entity.data.occupy_length.equals("0")) {
                mZhanyongLayout.setVisibility(android.view.View.VISIBLE);
                ((TextView) mZhanyongLayout.findViewById(R.id.zhanyong_length))
                        .setText(parser.entity.data.occupy_length + "米");
            } else {
                mZhanyongLayout.setVisibility(View.GONE);
            }
            mCarLenLayout.setVisibility(View.GONE);
        } else {
            mZhanyongLayout.setVisibility(android.view.View.GONE);
            if (!TextUtils.isEmpty(parser.entity.data.car_length) && !parser.entity.data.car_length.equals("无")) {
                mTruckLenth.setText(parser.entity.data.car_length + "米");
            } else {
                mCarLenLayout.setVisibility(View.GONE);
            }
        }

        if (parser.entity.data.location.size() > 0
                && !parser.entity.data.status.key.equals("0")) {
            String url = Tools.getMutiMarkMapImageUrl(
                    parser.entity.data.location, 0, 0);
            Log.i(TAG, "map url:" + url);
            imageLoader.loadImage(url, mImageLoadingListener);
            mMainMapLayout.setVisibility(android.view.View.VISIBLE);
            mMapline.setVisibility(android.view.View.VISIBLE);
            getCurrentAddress();
        } else {
            mMainMapLayout.setVisibility(android.view.View.GONE);
            mMapline.setVisibility(android.view.View.GONE);
        }

        if (parser.entity.data.coupon != null) {
            if (parser.entity.data.coupon.flag.equals("0")) {
                mCouponLayout.setVisibility(android.view.View.GONE);
            } else {
                mCouponLayout.setVisibility(android.view.View.VISIBLE);
                ((TextView) mCouponLayout.findViewById(R.id.coupon_value))
                        .setText("抵扣¥" + parser.entity.data.coupon.value);

            }
        }

        insuranceFlag = parser.entity.data.insurance.flag;
        insuranceStatus = parser.entity.data.insurance.status;
        if (parser.entity.data.insurance.flag.equals("1")) {//  保险信息显示和隐藏
            mInsuranceLayout.setVisibility(android.view.View.VISIBLE);
            mInsuranceValue.setText("¥" + Tools.formatNumber(parser.entity.data.insurance.value));
            mInsuranceName.setText(parser.entity.data.insurance.name);
            mInsuranceNumber.setText(parser.entity.data.insurance.num);
            mInsurancePackage.setText(parser.entity.data.insurance.package_type);
            if (!TextUtils.isEmpty(parser.entity.data.insurance.insurance_type)) {
                mInsuranceType.setText(parser.entity.data.insurance.insurance_type);
            }
            if (!TextUtils.isEmpty(parser.entity.data.insurance.insurance_price)) {
                mInsurancePrice.setText("¥" + Tools.formatNumber(parser.entity.data.insurance.insurance_price));
            }

            if (parser.entity.data.insurance.status.equals("0")) {// hint_color
                mInsuranceValue.setTextColor(getResources().getColor(R.color.text_color3));
                mInsuranceName.setTextColor(getResources().getColor(R.color.text_color3));
                mInsuranceNumber.setTextColor(getResources().getColor(R.color.text_color3));
                mInsurancePackage.setTextColor(getResources().getColor(R.color.text_color3));
                mInsuranceType.setTextColor(getResources().getColor(R.color.text_color3));
                mInsurancePrice.setTextColor(getResources().getColor(R.color.text_color3));
                ((TextView) mInsuranceLayout.findViewById(R.id.insurance_package_name))
                        .setTextColor(getResources().getColor(R.color.text_color3));
                ((TextView) mInsuranceLayout.findViewById(R.id.insurance_number_name))
                        .setTextColor(getResources().getColor(R.color.text_color3));
                ((TextView) mInsuranceLayout.findViewById(R.id.insurance_person_name))
                        .setTextColor(getResources().getColor(R.color.text_color3));
                ((TextView) mInsuranceLayout.findViewById(R.id.insurance_price_name))
                        .setTextColor(getResources().getColor(R.color.text_color3));
                ((TextView) mInsuranceLayout.findViewById(R.id.insurance_type_name))
                        .setTextColor(getResources().getColor(R.color.text_color3));
                ((TextView) mInsuranceLayout.findViewById(R.id.insurance_value_name))
                        .setTextColor(getResources().getColor(R.color.text_color3));
                mInsuranceLayout.findViewById(R.id.baoxian_hint_text).setVisibility(View.VISIBLE);
                mInsuranceLayout.findViewById(R.id.shixiao_img).setVisibility(View.VISIBLE);
            }
        } else {
            mInsuranceLayout.setVisibility(android.view.View.GONE);
        }

        String priceStr = "";
        if (parser.entity.data.insurance.status.equals("1")) {
            priceStr = "运费+保险";
        }

        //税费
        if (!TextUtils.isEmpty(parser.entity.data.money_tax)
                && !parser.entity.data.money_tax.equals("0")) {
            mTaxLayout.setVisibility(View.VISIBLE);
            mTaxValue.setText("¥" + Tools.formatNumber(parser.entity.data.money_tax));
            if (priceStr.length() == 0) {
                priceStr = "运费+税费";
            } else {
                priceStr += "+税费";
            }
        }

        isWuYouBao = parser.entity.data.is_wuyou;
        if (!TextUtils.isEmpty(isWuYouBao) && isWuYouBao.equals("1")) {//有无忧保
            mInsuranceLayout.setVisibility(android.view.View.GONE);
            if (!TextUtils.isEmpty(parser.entity.data.money_tax)
                    && !parser.entity.data.money_tax.equals("0")) {
                mTaxLayout.setVisibility(View.VISIBLE);
                mTaxValue.setText("¥" + Tools.formatNumber(parser.entity.data.money_tax));
                priceStr = "运费+税费";
            } else {
                priceStr = "";
            }
        }

        if (!TextUtils.isEmpty(priceStr)) {
            mPriceHint.setVisibility(View.VISIBLE);
            mPriceHint.setText("  ( " + priceStr + " )");
        }

        //发票抬头
        if (!TextUtils.isEmpty(parser.entity.data.invoice_name)) {
            mFapiaoTaitouLayout.setVisibility(View.VISIBLE);
            mFapiaoValue.setText(parser.entity.data.invoice_name);
        }

        //更换经纪人，后来改为客户补贴
        if (!TextUtils.isEmpty(parser.entity.data.money_chanage_agent)
                && !parser.entity.data.money_chanage_agent.equals("0")) {
            mChangeAgentLayout.setVisibility(View.VISIBLE);
            mChangeAgentValue.setText("¥" + parser.entity.data.money_chanage_agent);
        }

        initButtonView(parser);
    }

    private void initButtonView(OrderDetailJsonParser parser) {
        if (parser.entity.data.button.pay.equals("1")) {
            HidePriceLayout();
            mWithSpreadButton.setSpreadFlag(true);
            mWithSpreadButton.setSecondBtnText("立即支付");
            mWithSpreadButton.setFirstBtnText("运费总额:  ", "¥" + parser.entity.data.order_price);
            //mSecondBtn.setText("问题反馈");
        } else if (parser.entity.data.button.refund.equals("1")) {
            mWithSpreadButton.setSecondBtnText("申请退款");
            //mSecondBtn.setText("问题反馈");
        } else if (parser.entity.data.button.refund.equals("2")) {
            mWithSpreadButton.setSecondBtnText("查看退款进度");
            //mSecondBtn.setText("问题反馈");
        } else if (parser.entity.data.button.pay_driver.equals("1")) {
            mWithSpreadButton.setSecondBtnText("确定打款给司机");
            //mSecondBtn.setText("问题反馈");
        } else {
            mWithSpreadButton.setSecondBtnText("再下一单");
            if (parser.entity.data.status.key.equals("15")) {
                mWithSpreadButton.setFirstBtnText("评价经纪人");
            } else {
                //mSecondBtn.setText("问题反馈");
            }
        }
    }

    private void initView() {
        mMainMapLayout = (LinearLayout) findViewById(R.id.map_main_layout);

        mDriverInfoLayout = (LinearLayout) findViewById(R.id.driver_detail_layout);
        mNotArrayDriverLayout = (LinearLayout) findViewById(R.id.not_array_driver_layout);
    }

    private void HidePriceLayout() {
        mYunfeiZongerLayout.setVisibility(View.GONE);
        mTaxLayout.setVisibility(View.GONE);
        mInsuranceLayout.findViewById(R.id.insurace_price_layout).setVisibility(View.GONE);

    }

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.order_detail);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        // imageLoader.displayImage(Tools.getStaticMapImageUrl(mLatitude,
        // mLongitude, 0, 0), mMapView);
        ShowBackView();
        setTitle("运单详情");
        mRightBtn.setVisibility(android.view.View.VISIBLE);
        mRightBtn.setImageResource(R.drawable.more_icon);
        order_id = getIntent().getStringExtra("order_id");
        mRefreshLocate = (TextView) findViewById(R.id.refresh_locate);
        mRefreshLocate.setOnClickListener(this);
        mMapline = (TextView) findViewById(R.id.map_line);
        mMapView = (ImageView) findViewById(R.id.bmapView);
        mDetailPlace = (Button) findViewById(R.id.detail_place);

        mMapLayout = (RelativeLayout) findViewById(R.id.map_layout);
        mMapLayout.setOnClickListener(this);

        mAgentPhoneLayout = (RelativeLayout) findViewById(R.id.agent_phone_layout);
        mAgentPhoneLayout.setOnClickListener(this);
        mDriverPhoneLayout = (RelativeLayout) findViewById(R.id.driver_phone_layout);
        mDriverPhoneLayout.setOnClickListener(this);

        // mConfirmBtn = (Button) findViewById(R.id.confirm_btn);
        // mConfirmBtn.setOnClickListener(this);

        mCuichuArray = (TextView) findViewById(R.id.cuichu_array);
        mCuichuArray.setOnClickListener(this);

        mZhanyongLayout = (RelativeLayout) findViewById(R.id.zhanyong_layout);
        // mZhangyongline = (TextView) findViewById(R.id.zhanyong_line);

        mGridView = (GridView) findViewById(R.id.gridview);

        fromGT = getIntent().getBooleanExtra("fromGT", false);

        mWithSpreadButton.setSpreadOnClickLister(new WithSpreadButton.SpreadOnclickListener() {
            @Override
            public void fistBtnClick() {
                if (mWithSpreadButton.getSpreadFlag()) {
                    UtilsLog.i(TAG, "fistBtn click ....");
                    View view = LinearLayout.inflate(mContext, R.layout.yunfei_detail_view, null);
                    TextView yunfei = (TextView) view.findViewById(R.id.yunfei);
                    yunfei.setText("¥" + mOrderDetailParser.entity.data.order_price);

                    TextView baoxianText = (TextView) view.findViewById(R.id.baoxian_text);
                    TextView baoxian = (TextView) view.findViewById(R.id.baoxian);
                    if (mOrderDetailParser.entity.data.insurance != null
                            && mOrderDetailParser.entity.data.insurance.flag.equals("1")
                            && !TextUtils.isEmpty(mOrderDetailParser.entity.data.insurance.insurance_price)
                            && !mOrderDetailParser.entity.data.insurance.insurance_price.equals("0")
                            ) {
                        baoxian.setText("¥" + mOrderDetailParser.entity.data.insurance.insurance_price);
                    } else {
                        baoxianText.setVisibility(View.GONE);
                        baoxian.setVisibility(View.GONE);
                    }

                    TextView giveInsurance = (TextView) view.findViewById(R.id.give_baoxian_text);
                    TextView giveInssuranceValue = (TextView) view.findViewById(R.id.give_baoxian);
                    if (mOrderDetailParser.entity.data.insurance != null
                            && !TextUtils.isEmpty(mOrderDetailParser.entity.data.insurance.give_money)) {
                        giveInssuranceValue.setText("-¥" + mOrderDetailParser.entity.data.insurance.give_money);
                    } else {
                        giveInsurance.setVisibility(View.GONE);
                        giveInssuranceValue.setVisibility(View.GONE);
                    }

                    TextView suifeiText = (TextView) view.findViewById(R.id.suifei_text);
                    TextView suifei = (TextView) view.findViewById(R.id.suifei);
                    if (!TextUtils.isEmpty(mOrderDetailParser.entity.data.money_tax)
                            && mOrderDetailParser.entity.data.money_tax.equals("0")) {
                        suifei.setText("¥" + mOrderDetailParser.entity.data.money_tax);
                    } else {
                        suifeiText.setVisibility(View.GONE);
                        suifei.setVisibility(View.GONE);
                    }

                    SpinnerPopUpListView mSpinnerPopUpView = new SpinnerPopUpListView(mContext, view
                            , mWithSpreadButton, SpinnerPopUpListView.DropAboveStyle);
                    mSpinnerPopUpView.Show();

                } else {
                    CheckBtnAction(mWithSpreadButton.getFirstBtnString());
                }
            }

            @Override
            public void secondBtnClick() {
                CheckBtnAction(mWithSpreadButton.getSecondBtnString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fromGT) {
            Intent intent = new Intent(this, HomeMainScreenActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        getOrderDetail();
    }

    private SpinnerPopUpListView mPopList;

    private OrderDetailJsonParser mOrderDetailParser = null;

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
                                InitData(mParser);
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
                        cancelProgressDialog();
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
                                value.goods_cubage = mParser.entity.data.goods_cubage;
                                value.goods_weight = mParser.entity.data.goods_weight;

                                //value.car_model = mParser.entity.data.car_model;
                                //value.car_length = mParser.entity.data.car_length;
                                value.car_model = mParser.entity.data.car_model.key;
                                value.car_length = mParser.entity.data.car_length.key;

                                if (mOrderDetailParser != null) {
                                    value.car_model_text = mOrderDetailParser.entity.data.car_model;
                                    value.car_length_text = mOrderDetailParser.entity.data.car_length;
                                    if (!value.car_length_text.contains("米")) {
                                        value.car_length_text += "米";
                                    }
                                }
                                value.pay_type = mParser.entity.data.pay_type;

                                value.receipt = mParser.entity.data.receipt;
                                value.need_back = mParser.entity.data.need_back;

                                value.car_type = mParser.entity.data.car_type;

                                value.occupy_length = mParser.entity.data.occupy_length;
                                value.remark = mParser.entity.data.remark;
                                value.expect_price = mParser.entity.data.expect_price;

                                //
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
                                value.pay_type1_id = mParser.entity.data.paidType.get(0).id;
                                value.pay_type1_name = mParser.entity.data.paidType.get(0).name;

                                if (mParser.entity.data.paidType.size() > 1) {
                                    value.pay_type2_id = mParser.entity.data.paidType.get(1).id;
                                    value.pay_type2_name = mParser.entity.data.paidType.get(1).name;
                                } else {
                                    value.pay_type2_id = "";
                                    value.pay_type2_name = "";
                                }

                                intent.putExtra("firstTabValue", value);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade,
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
                cancelProgressDialog();
            }

        }, true) {

            @Override
            public Map<String, String> getPostBodyData() {
                // TODO Auto-generated method stub
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("order_id", "" + order_id);
                return parmas;
            }

        };

        showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    @Override
    public void onClickListener(int id) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (id) {
            case R.id.right_img:
                if (mOrderDetailParser != null &&
                        mOrderDetailParser.entity != null) {
                    final ArrayList<String> datalist = new ArrayList<String>();
                    datalist.add("立即支付");
                    datalist.add("查看三方协议");
                    datalist.add("评价经纪人");
                    datalist.add("查看保险单");
                    datalist.add("查看报价列表");
                    datalist.add("再下一单");
                    datalist.add("申请退款");
                    datalist.add("查看退款进度");
                    datalist.add("确定打款给司机");
                    //  datalist.add("问题反馈");

                    if (!mOrderDetailParser.entity.data.button.pay.equals("1")) {
                        datalist.remove("立即支付");
                    }
                    if (!mOrderDetailParser.entity.data.button.refund.equals("1")) {
                        datalist.remove("申请退款");
                    }
                    if (!mOrderDetailParser.entity.data.button.refund.equals("2")) {
                        datalist.remove("查看退款进度");
                    }

                    if (mOrderDetailParser.entity.data.status.key.equals("15")) {
                        //      datalist.remove("问题反馈");
                    } else {
                        datalist.remove("评价经纪人");
                    }


                    if (!mOrderDetailParser.entity.data.button.pay_driver
                            .equals("1")) {
                        datalist.remove("确定打款给司机");
                    }

                    if (!TextUtils.isEmpty(mOrderDetailParser.entity.data.is_wuyou)
                            && mOrderDetailParser.entity.data.is_wuyou.equals("1")) {

                    } else {
                        if (mOrderDetailParser.entity.data.insurance.flag.equals("0")
                                || mOrderDetailParser.entity.data.status.key
                                .equals("0")
                                || mOrderDetailParser.entity.data.insurance.status.equals("0")) {
                            datalist.remove("查看保险单");
                        }
                    }

                    mPopList = new SpinnerPopUpListView(mContext, datalist,
                            mTopView);
                    mPopList.setSpClickListener(new SpinnerPopUpListView.SpOnClickListener() {
                        @Override
                        public void onItemsClick(int pos) {
                            // TODO Auto-generated method stub
                            CheckBtnAction(datalist.get(pos));
                        }
                    });
                }
                if (mPopList != null) {
                    mPopList.Show();
                }
                break;
            case R.id.map_layout:
                intent = new Intent(this, FullScreenMapActivity.class);
                String[] lngArray = new String[mOrderDetailParser.entity.data.location
                        .size()];
                String[] latArray = new String[mOrderDetailParser.entity.data.location
                        .size()];
                String[] locations = new String[mOrderDetailParser.entity.data.location.size()];
                for (int i = 0; i < mOrderDetailParser.entity.data.location.size(); i++) {
                    lngArray[i] = mOrderDetailParser.entity.data.location.get(i).lng;
                    latArray[i] = mOrderDetailParser.entity.data.location.get(i).lat;
                    locations[i] = mOrderDetailParser.entity.data.location.get(i).location;

                }
                intent.putExtra("lngArray", lngArray);
                intent.putExtra("latArray", latArray);
                intent.putExtra("locations", locations);

                startActivity(intent);
                break;

            case R.id.agent_phone_layout:
                Constant.GotoDialPage(mContext, mJinjiPhone.getText().toString());
                break;
            case R.id.driver_phone_layout:
                Constant.GotoDialPage(mContext, mDriverPhone.getText().toString());
                break;
            case R.id.cuichu_array:
                TongjiModel.addEvent(mContext, "运单详情",
                        TongjiModel.TYPE_BUTTON_CLIKC, "催促安排司机");
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("order_id", order_id);
                NetWorkUtils.cuiChuArrayDriver(this, TAG, parmas);
                break;
            case R.id.refresh_locate:// 更新司机
                TongjiModel.addEvent(mContext, "运单详情",
                        TongjiModel.TYPE_BUTTON_CLIKC, "更新司机位置");
                RefreshLocation();
                break;

            case R.id.send_phone:
                Constant.GotoDialPage(mContext, mSendPhone.getText().toString());
                break;
            case R.id.send_phone2:
                if (mSendPhone2.getText().toString().length() > 5) {
                    Constant.GotoDialPage(mContext, mSendPhone2.getText()
                            .toString());
                }
                break;
            case R.id.rece_phone:
                Constant.GotoDialPage(mContext, mRecePhone.getText().toString());
                break;
            case R.id.rece_phone2:
                if (mRecePhone2.getText().toString().length() > 5) {
                    Constant.GotoDialPage(mContext, mRecePhone2.getText()
                            .toString());
                }
                break;
            case R.id.call_center:
                Constant.GotoDialPage(mContext, Constant.PHONE_NUMBER);
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                cancelProgressDialog();
                File file = (File) msg.obj;
                Intent intent = DisplayPdf.getPdfIntent(file);
                startActivity(intent);
            }

        }
    };


    private void CheckBtnAction(String str) {
        Intent intent;
        if (str.equals("立即支付")) {
            goToPayOnLineActivity();
        } else if (str.equals("查看三方协议")) {
            UserDetailEntity mUserDetail = SharePerfenceUtil
                    .getUserDetail(mContext);
            if (mUserDetail.data.name.equals("")) {
                alertDialog("用户姓名为空,请填写用户姓名后查看",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated
                                // method
                                // stub
                                Intent i = new Intent(mContext,
                                        ModifyMyInfoActivity.class);
                                startActivity(i);
                            }

                        });
            } else {
                intent = new Intent(mContext, XieyiContentActivity.class);
                intent.putExtra("order_id", order_id);
                startActivity(intent);
            }
        } else if (str.equals("评价经纪人")) {
            // 评价经纪人
            intent = new Intent(mContext, PingjiaAgentActivity.class);
            intent.putExtra("order_id", order_id);
            if (mNotArrayDriverLayout.getVisibility() == android.view.View.VISIBLE) {
                intent.putExtra("driver_array", false);
            } else {
                intent.putExtra("driver_array", true);
            }
            startActivity(intent);
        } else if (str.equals("查看保险单")) {
            if (!TextUtils.isEmpty(mOrderDetailParser.entity.data.wuyou_pdf)) {
                showProgressDialog();
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        File file = DisplayPdf.downLoadPdfFile(mOrderDetailParser.entity.data.order_sn
                                , mOrderDetailParser.entity.data.wuyou_pdf);
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = file;
                        mHandler.sendMessage(msg);
                    }
                };
                t.start();
//                intent = new Intent(mContext, PdfViewAct.class);
//                intent.putExtra("wuyou_pdf", mOrderDetailParser.entity.data.wuyou_pdf);
//                startActivity(intent);
            } else if (!TextUtils.isEmpty(mOrderDetailParser.entity.data.insurance.image)) {
                intent = new Intent(mContext, LookBaoDanAct.class);
                intent.putExtra("image",
                        mOrderDetailParser.entity.data.insurance.image);
                startActivity(intent);
            } else {
                ToastUtils.toast(mContext, "系统暂未上传保险单，请稍后再试");
            }
        } else if (str.equals("查看报价列表")) {
            intent = new Intent(mContext, AgentAndQuoteDetailAct.class);
            intent.putExtra("order_id", order_id);
            intent.putExtra("quote_list", true);
            startActivity(intent);
        } else if (str.equals("再下一单")) {
            getReOrder();
        } else if (str.equals("申请退款")) {
            intent = new Intent(mContext, ApplyRefundActivity.class);
            intent.putExtra("order_id", order_id);
            startActivity(intent);
        } else if (str.equals("查看退款进度")) {
            intent = new Intent(mContext, RefundProgressActivity.class);
            intent.putExtra("order_id", order_id);
            startActivity(intent);
        } else if (str.equals("确定打款给司机")) {
            intent = new Intent(mContext, ComfirmPayDriverActivity.class);
            intent.putExtra("order_id", order_id);
            startActivity(intent);
        } else if (str.equals("问题反馈")) {
            intent = new Intent(mContext, FeedBackAct.class);
            intent.putExtra("ques_back", true);
            intent.putExtra("order_id", order_id);
            startActivity(intent);
        }

    }

    private void goToPayOnLineActivity() {
        Intent intent = new Intent(mContext, PayOnLineActivity.class);
        intent.putExtra("order_id", order_id);
        intent.putExtra("insuranceFlag", insuranceFlag);
        intent.putExtra("insuranceStatus", insuranceStatus);
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelAllRequests(TAG);
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
}
