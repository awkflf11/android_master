package com.foryou.truck.sendproduct;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.foryou.truck.BaseActivity;
import com.foryou.truck.EntryLoginActivity;
import com.foryou.truck.FirstTabInitValue;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.activity.MyOrderListAct;
import com.foryou.truck.activity.RemarksAct;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.AreasEntity;
import com.foryou.truck.entity.BusinessListEntity;
import com.foryou.truck.entity.CarLoadEntity;
import com.foryou.truck.entity.CommonConfigEntity;
import com.foryou.truck.parser.AreasJsonParser;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.parser.CarLoadParser;
import com.foryou.truck.parser.CommonConfigJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.MyActivityManager;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.MutiChooseBtn2;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dubin on 16/7/25.
 */
public class SendProductAct extends BaseActivity implements SendProductContract.View {
    public String TAG = "SendProductAct";
    @BindView(id = R.id.business_name_layout, click = true)
    private RelativeLayout mBusinessLayout; //业务列表
    @BindView(id = R.id.buiness_text_value)
    private TextView mBusinessTextValue;

    @BindView(id = R.id.paymethod_layout)
    LinearLayout mPayMethodLayout;  //支付方式
    @BindView(id = R.id.fapiao_layout) //发票
            RelativeLayout mFapiaoLayout;
    @BindView(id = R.id.between_fphuidan_line)
    View mFpHuidanLine; //发票和回单之间的线条
    @BindView(id = R.id.huidan_layout)
    RelativeLayout mHuidanLayout;
    @BindView(id = R.id.remark_layout)   //备注
            RelativeLayout mRemarkLayout;

    @BindView(id = R.id.product_info_layout, click = true)
    private RelativeLayout mProductInfoLayout;
    @BindView(id = R.id.use_truck_info, click = true)
    private RelativeLayout mUserTurckInfo;

    FirstTabInitValue mInitValue;

    @BindView(id = R.id.send_time, click = true)
    private TextView mSendTime;
    @BindView(id = R.id.load_address, click = true)
    private TextView mLoadAddress;
    @BindView(id = R.id.rece_address, click = true)
    private TextView mReceAddress;
    @BindView(id = R.id.truck_info)
    private TextView mTruckInfo;//用车信息
    @BindView(id = R.id.product_info)
    private TextView mProductInfoText;//货物信息
    @BindView(id = R.id.more_info)
    private TextView mRemarkText;
    @BindView(id = R.id.muti_btn_choose)
    private MutiChooseBtn2 mNeedFapiaoBtn;//发票
    @BindView(id = R.id.noneed_fapiao_tv)
    private TextView noNeedFapiaoTv;//发票
    @BindView(id = R.id.muti_btn_choose2)
    MutiChooseBtn2 mutiHuiDanBtn;//回单

    @BindView(id = R.id.pay_online_layout)
    RelativeLayout mPayOnLineLayout;
    @BindView(id = R.id.pay_offline_layout)
    RelativeLayout mPayOffLineLayout;

    @BindView(id = R.id.confirm_query, click = true)
    private Button mConfirmBtn;// 开始询价

    private String mCarModelSelectIndex, mCarLenselectIndex;

    private static final int GET_TIME_CODE = 100;
    private static final int GET_PLACE_CODE = 101;
    private static final int GET_PRODUCTINFO_CODE = 102;
    private static final int GET_TRUCKINFO_CODE = 103;
    private static final int GET_REMARK_CODE = 104;
    private static final int GET_BUSINESS_TYPE_CODE = 105;

    private int mBusinessTypeIndex; //0:福佑公司业务 1.普通公司一种业务 2.普通公司,两种业务

    BusinessListEntity.BusinessItem mCurrentBusinessItem;
    public static AreasEntity areaEntity = null;

    CommonConfigEntity mConfigEntity;
    SendProductPresent mPresent;
    Context mContext;
    CarLoadEntity carLoadEntity;
    boolean fromReorder = false;

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.send_product);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("货源询价");
        ShowBackView();
        mContext = this;

        mConfigEntity = SharePerfenceUtil.getConfigData(this);
        if (mConfigEntity == null) {
            NetWorkUtils.getConfigData(this, TAG, new NetWorkUtils.NetJsonRespon() {
                @Override
                public void onRespon(BaseJsonParser parser) {
                    CommonConfigJsonParser mParser = (CommonConfigJsonParser) parser;
                    mConfigEntity = mParser.entity;
                }
            });
        }

        if (areaEntity == null) {
            NetWorkUtils.getAreaData(this, TAG, new NetWorkUtils.NetJsonRespon() {
                @Override
                public void onRespon(BaseJsonParser parser) {
                    areaEntity = ((AreasJsonParser) parser).entity;
                }
            });
        }

        mInitValue = (FirstTabInitValue) getIntent().getSerializableExtra("firstTabValue");
        if (mInitValue != null) {
            showInvisibleView(true);
            fromReorder = true;
        } else {
            fromReorder = false;
            mInitValue = new FirstTabInitValue();
        }

        mPresent = new SendProductPresent(this, TAG);
        mPresent.init(mInitValue);
    }

    private void JudgeNoticMessage(String carModel, String carLength, CarLoadEntity carLoadEntity, String goodsWeight, String carLengthText, String carModelText) {
        String biaozhunWeight = NetWorkUtils.getCarLoad(carModel, carLength, carLoadEntity);
        // 您填写的货物重量30吨超过9.6米平板车标准载重25吨，是否继续询价？
        if (!TextUtils.isEmpty(biaozhunWeight) && Float.valueOf(goodsWeight) > Float.valueOf(biaozhunWeight)) {
            alertDialog("您填写的货物重量" + goodsWeight + "吨超过" + carLengthText + "" + carModelText + "标准载重" + biaozhunWeight
                    + "吨，是否继续询价？", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    pushDataToServer();
                }
            });
        } else {
            pushDataToServer();
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mConfirmBtn.setEnabled(true);
                    mConfirmBtn.setBackgroundResource(R.drawable.login_btn);
                    break;
            }
        }
    };

    private void pushDataToServer() {
        mConfirmBtn.setEnabled(false);
        mConfirmBtn.setBackgroundColor(Color.parseColor("#c5c5c5"));
        mHandler.sendEmptyMessageDelayed(1, 5000);
        String url = UrlConstant.BASE_URL + "/order/quoteCreate";
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, response);
                        cancelProgressDialog();
                        SimpleJasonParser mParser = new SimpleJasonParser();

                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                ToastUtils.toast(mContext, mParser.entity.msg);
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("startPlace", mInitValue.goods_load_region + "," + mInitValue.goods_load_address);
                                map.put("endPlace", mInitValue.goods_unload_region + "," + mInitValue.goods_unload_address);
                                map.put("uid", SharePerfenceUtil.getUid(mContext));
                                MobclickAgent.onEvent(mContext, "quoteCreate", map);
                                Intent intent = new Intent(mContext, MyOrderListAct.class);
                                SharePerfenceUtil.saveOrderListTabIndex(mContext, 0);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtils.toast(mContext, mParser.entity.msg);
                                mHandler.removeMessages(1);
                                mHandler.sendEmptyMessage(1);
                            }
                        } else {
                            Log.i(TAG, "common/config 解析错误");
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
                mHandler.removeMessages(1);
                mHandler.sendEmptyMessage(1);
                //ToastUtils.toast(mContext, "网络连接失败，请稍候再试");
            }
        }, true) {

            @Override
            public Map<String, String> getPostBodyData() {
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("goods_load_region", mInitValue.goods_load_region);
                parmas.put("goods_unload_region", mInitValue.goods_unload_region);
                parmas.put("goods_load_address", mInitValue.goods_load_address);
                parmas.put("goods_unload_address", mInitValue.goods_unload_address);
                UtilsLog.i(TAG, "装货地址============" + mInitValue.goods_load_region + "======" + mInitValue.goods_load_address);
                UtilsLog.i(TAG, "卸货地址============" + mInitValue.goods_unload_region + "=======" + mInitValue.goods_unload_address);
                parmas.put("goods_loadtime", mInitValue.goods_loadtime);
                parmas.put("goods_loadtime_desc", mInitValue.goods_loadtime_desc);
                parmas.put("goods_name", mInitValue.goods_name);
                parmas.put("goods_cubage", mInitValue.goods_cubage);// 货物体积
                UtilsLog.i(TAG, "货物的体积============" + mInitValue.goods_cubage);
                parmas.put("goods_weight", mInitValue.goods_weight);
                parmas.put("car_model", mInitValue.car_model);
                parmas.put("car_length", mInitValue.car_length);
                // 2=>运费到付;3=>合同客户结算 ; 5=>在线支付 ;
                parmas.put("pay_type", "" + mInitValue.pay_type);
                parmas.put("receipt", "" + mInitValue.receipt);
                UtilsLog.i(TAG, " --pay_type========" + mInitValue.pay_type);
                UtilsLog.i(TAG, " --receipt========" + mInitValue.receipt);
                parmas.put("need_back", "" + mInitValue.need_back);
                //        parmas.put("expect_price", mExpertPrice.getText().toString());
                parmas.put("occupy_length", mInitValue.occupy_length);
                parmas.put("remark", mInitValue.remark);
                parmas.put("car_type", mInitValue.car_type);
                parmas.put("client", "2");
                //常用的标签：不必填。 “1,2,5,7”，不同标签以“,”分割
                parmas.put("common_tag", mInitValue.mutiAndSingleTagKey);
                UtilsLog.i(TAG, "request--common_tag========" + mInitValue.mutiAndSingleTagKey);
                return parmas;
            }
        };
        UtilsLog.i(TAG, "pushDataToServer ....");
        showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }


    @Override
    public void onClickListener(int id) {
        Intent intent;
        switch (id) {
            case R.id.business_name_layout:
                if (mBusinessTypeIndex != 2) {
                    return;
                }
                intent = new Intent(this, BusinessNameListAct.class);
                startActivityForResult(intent, GET_BUSINESS_TYPE_CODE);
                break;

            case R.id.send_time:
                if (mConfigEntity == null) {
                    ToastUtils.toast(mContext, "正在获取配置文件，请稍候再试");
                    return;
                }
                intent = new Intent(mContext, chooseTimeActivity.class);
                if (mConfigEntity.data.limit_day != null && mConfigEntity.data.limit_day.size() > 0) {
                    intent.putExtra("limit_day", mConfigEntity.data.limit_day.get(0).value.trim());
                } else {
                    intent.putExtra("limit_day", "30");
                }
                intent.putExtra("rece_time", false);
                startActivityForResult(intent, GET_TIME_CODE);
                break;
            case R.id.send_layout:
            case R.id.load_address:
                intent = new Intent(mContext, getPlaceActivity.class);
                intent.putExtra("send_address", true);
                if (mInitValue.goods_load_region == null) {
                    intent.putExtra("goods_load_region", "");
                } else {
                    intent.putExtra("goods_load_region", mInitValue.goods_load_region);
                }
                if (mInitValue.goods_load_address == null) {
                    intent.putExtra("goods_load_address", "");
                } else {
                    intent.putExtra("goods_load_address", mInitValue.goods_load_address);
                }
                if (MyActivityManager.create().findActivity(getPlaceActivity.class) == null) {
                    startActivityForResult(intent, GET_PLACE_CODE);
                }
                break;
            case R.id.rece_layout:
            case R.id.rece_address:
                intent = new Intent(mContext, getPlaceActivity.class);
                intent.putExtra("send_address", false);
                if (mInitValue.goods_unload_region == null) {
                    intent.putExtra("goods_unload_region", "");
                } else {
                    intent.putExtra("goods_unload_region", mInitValue.goods_unload_region);
                }
                if (mInitValue.goods_unload_address == null) {
                    intent.putExtra("goods_unload_address", "");
                } else {
                    intent.putExtra("goods_unload_address", mInitValue.goods_unload_address);
                }
                if (MyActivityManager.create().findActivity(getPlaceActivity.class) == null) {
                    startActivityForResult(intent, GET_PLACE_CODE);
                }
                break;
            case R.id.product_info_layout:
                intent = new Intent(mContext, getProductInfoActivity.class);
                intent.putExtra("goods_name", mInitValue.goods_name);
                intent.putExtra("goods_weight", mInitValue.goods_weight);
                intent.putExtra("goods_cubage", mInitValue.goods_cubage);
                //
                intent.putExtra("car_type", mInitValue.car_type);
                intent.putExtra("car_model", mInitValue.car_model);
                intent.putExtra("car_model_text", mInitValue.car_model_text);
                intent.putExtra("car_length", mInitValue.car_length);
                intent.putExtra("car_length_text", mInitValue.car_length_text);
                intent.putExtra("occupy_length", mInitValue.occupy_length);
                startActivityForResult(intent, GET_PRODUCTINFO_CODE);
                break;
            case R.id.use_truck_info:
                if (mConfigEntity == null) {
                    ToastUtils.toast(getContext(), "正在获取配置文件，请稍候再试");
                    return;
                }
                intent = new Intent(getContext(), getTruckInfoActivity.class);
                intent.putExtra("goods_weight", mInitValue.goods_weight);// 货物重量
                intent.putExtra("car_type", mInitValue.car_type);
                intent.putExtra("car_model", mInitValue.car_model);
                intent.putExtra("car_model_text", mInitValue.car_model_text);
                intent.putExtra("car_length", mInitValue.car_length);
                intent.putExtra("car_length_text", mInitValue.car_length_text);
                intent.putExtra("occupy_length", mInitValue.occupy_length);
                intent.putExtra("occupy_tiji", mInitValue.goods_cubage);//占用体积
                intent.putExtra("mCarLenselectIndex", mCarLenselectIndex);
                intent.putExtra("mCarModelSelectIndex", mCarModelSelectIndex);
                startActivityForResult(intent, GET_TRUCKINFO_CODE);
                break;
            case R.id.remark_layout:
                // intent = new Intent(this, getRemarkActivity.class);
                intent = new Intent(this, RemarksAct.class);
                intent.putExtra("remark", mInitValue.remark);
                intent.putExtra("mutiAndSingleText", mInitValue.mutiAndSingleTagText);

                startActivityForResult(intent, GET_REMARK_CODE);
                break;
            case R.id.confirm_query:// 开始询价
                if (!SharePerfenceUtil.IsLogin(mContext)) {
                    intent = new Intent(mContext, EntryLoginActivity.class);
                    startActivity(intent);
                } else {
                    if (TextUtils.isEmpty(mInitValue.goods_loadtime)) {
                        ToastUtils.toast(mContext, "请选择装货时间");
                        TongjiModel.addEvent(mContext, "货物询价", TongjiModel.TYPE_ERROR, "请选择装货时间");
                        return;
                    }
                    if (mLoadAddress.getText().toString().equals("")) {
                        ToastUtils.toast(mContext, "请选择装货地址");
                        TongjiModel.addEvent(mContext, "货物询价", TongjiModel.TYPE_ERROR, "请选择装货地址");
                        return;
                    }

                    if (mReceAddress.getText().toString().equals("")) {
                        ToastUtils.toast(mContext, "请选择卸货地址");
                        TongjiModel.addEvent(mContext, "货物询价", TongjiModel.TYPE_ERROR, "请选择卸货地址");
                        return;
                    }

                    if (mLoadAddress.getText().toString().equals(mReceAddress.getText().toString())) {
                        ToastUtils.toast(mContext, "装货地址和卸货地址不能相同");
                        return;
                    }
                    if (TextUtils.isEmpty(mInitValue.car_type)) {
                        //ToastUtils.toast(mContext, "请选择用车类型");
                        ToastUtils.toast(mContext, "请在用车信息中选择车长");
                        return;
                    }
                    if (mInitValue.car_type.equals("2")) {
                        if (TextUtils.isEmpty(mInitValue.occupy_length) && TextUtils.isEmpty(mInitValue.goods_cubage)) {
                            ToastUtils.toast(mContext, "请在用车信息中填写预计占用体积或占用车长");
                            TongjiModel.addEvent(mContext, "货物询价", TongjiModel.TYPE_ERROR, "请在用车信息中选择车长");
                            return;
                        }
                    }
                    // if (mProductVolume.getText().toString().equals("")) {
                    // ToastUtils.toast(mContext, "请填写货物体积");
                    // return;
                    // }
                    if (mInitValue.car_type.equals("1")) {
                        if (TextUtils.isEmpty(mInitValue.car_length)) {
                            ToastUtils.toast(mContext, "请在用车信息中选择车长");
                            TongjiModel.addEvent(mContext, "货物询价", TongjiModel.TYPE_ERROR, "请在用车信息中选择车长");
                            return;
                        }
                        if (TextUtils.isEmpty(mInitValue.car_model)) {
                            ToastUtils.toast(mContext, "请在用车信息中选择车型");
                            TongjiModel.addEvent(mContext, "货物询价", TongjiModel.TYPE_ERROR, "请在用车信息中选择车型");
                            return;
                        }
                    }
                    if (TextUtils.isEmpty(mInitValue.goods_name)) {
                        ToastUtils.toast(mContext, "请在货物信息中填写货物名称");
                        TongjiModel.addEvent(mContext, "货物询价", TongjiModel.TYPE_ERROR, "请在货物信息中填写货物名称");
                        return;
                    }
                    if (TextUtils.isEmpty(mInitValue.goods_weight)) {
                        ToastUtils.toast(mContext, "请在货物信息中填写货物重量");
                        TongjiModel.addEvent(mContext, "货物询价", TongjiModel.TYPE_ERROR, "请在货物信息中填写货物重量");
                        return;
                    }

                    if (mCurrentBusinessItem.paidType.size() > 1) {
                        if (!mPayOffLineLayout.getTag().equals("1") && !mPayOnLineLayout.getTag().equals("1")) {
                            ToastUtils.toast(mContext, "请选择结算方式");
                            return;
                        }
                    }

                    // 强提示：此处判断
                    final String goodsWeight = mInitValue.goods_weight;
                    final String carModelText = mInitValue.car_model_text;
                    final String carLengthText = mInitValue.car_length_text.contains("米") ? mInitValue.car_length_text : mInitValue.car_length_text + "米";
                    final String carModel = mInitValue.car_model;
                    final String carLength = mInitValue.car_length;
//				final String carModel = "1";
//				final String carLength = "1";
                    //goodsWeight = 3 + "";
                    if (mInitValue.car_type.equals("1")) {// 整车
                        carLoadEntity = SharePerfenceUtil.getCarLoadData(mContext);
                        if (carLoadEntity == null) {
                            NetWorkUtils.SaveCarLoad(mContext, new NetWorkUtils.NetJsonRespon() {
                                @Override
                                public void onRespon(BaseJsonParser parser) {
                                    CarLoadParser mParser = (CarLoadParser) parser;
                                    carLoadEntity = mParser.entity;
                                    if (carLoadEntity != null) {
                                        JudgeNoticMessage(carModel, carLength, carLoadEntity, goodsWeight, carLengthText, carModelText);
                                    } else {
                                        pushDataToServer();
                                    }
                                }

                            });

                        } else {//不为null的时候
                            JudgeNoticMessage(carModel, carLength, carLoadEntity, goodsWeight, carLengthText, carModelText);
                        }
                    } else {// 拼车
                        pushDataToServer();
                    }
                }
                break;
        }
    }


    private void choosePayMethod(int index) {
        if (index == 0) {
            mPayOnLineLayout.findViewById(R.id.arrow_icon6).setBackgroundResource(R.drawable.check_on_btn);
            mPayOnLineLayout.setTag("1");
            mInitValue.pay_type = mCurrentBusinessItem.paidType.get(0).id;
            mPayOffLineLayout.findViewById(R.id.arrow_icon7).setBackgroundResource(R.drawable.check_off_btn);
            mPayOffLineLayout.setTag("0");
        } else if (index == 1) {
            mPayOnLineLayout.findViewById(R.id.arrow_icon6).setBackgroundResource(R.drawable.check_off_btn);
            mPayOnLineLayout.setTag("0");
            mPayOffLineLayout.findViewById(R.id.arrow_icon7).setBackgroundResource(R.drawable.check_on_btn);
            mPayOffLineLayout.setTag("1");
            mInitValue.pay_type = mCurrentBusinessItem.paidType.get(1).id;
        }
    }

    private View.OnClickListener mPayMethodListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCurrentBusinessItem.paidType.size() < 2) {
                return;
            }
            if (v == mPayOnLineLayout) {
                choosePayMethod(0);
            } else if (v == mPayOffLineLayout) {
                choosePayMethod(1);
            }
        }
    };

    private void setBusinessStrategy(BusinessListEntity.BusinessItem businessItem) {
        mBusinessTextValue.setText(businessItem.name);
        if (businessItem.paidType.size() == 1) {
            mPayOffLineLayout.setVisibility(View.GONE);
            mPayOnLineLayout.findViewById(R.id.arrow_icon6).setVisibility(View.GONE);
            mPayOnLineLayout.setTag("1");
            mInitValue.pay_type = businessItem.paidType.get(0).id;
            ((TextView) mPayOnLineLayout.findViewById(R.id.pay_online_text)).setText("付款方式");
            ((TextView) mPayOnLineLayout.findViewById(R.id.pay_online_text2)).setVisibility(View.VISIBLE);
            ((TextView) mPayOnLineLayout.findViewById(R.id.pay_online_text2)).setText(businessItem.paidType.get(0).name);
        } else if (businessItem.paidType.size() == 2) {
            ((TextView) mPayOnLineLayout.findViewById(R.id.pay_online_text)).setText(businessItem.paidType.get(0).name);
            mPayOnLineLayout.setOnClickListener(mPayMethodListener);
            ((TextView) mPayOffLineLayout.findViewById(R.id.pay_offline_text)).setText(businessItem.paidType.get(1).name);
            mPayOffLineLayout.setOnClickListener(mPayMethodListener);
        }
        setInvoiceType(businessItem);
    }

    public void setTruckInfo() {
        String content2 = "";
        if (!TextUtils.isEmpty(mInitValue.car_type) && mInitValue.car_type.equals("2")) {
            content2 = "拼车";
            if (!mInitValue.car_model_text.equals("")) {
                content2 += "," + mInitValue.car_model_text;
            }
            if (!mInitValue.car_length_text.equals("")) {
                // content += "," + mInitValue.car_length_text;
            }
            //  content += "," +mInitValue.goods_cubage + "方" +mInitValue.occupy_length + "米";
            content2 += "" + (TextUtils.isEmpty(mInitValue.goods_cubage) ? "" : "," + mInitValue.goods_cubage + "方") +
                    (TextUtils.isEmpty(mInitValue.occupy_length) ? "" : "," + mInitValue.occupy_length + "米");
        } else if (!TextUtils.isEmpty(mInitValue.car_type)) {// 整车
            content2 = "整车," + mInitValue.car_model_text + "," + mInitValue.car_length_text + "米";
        } else {
        }
        mTruckInfo.setText(content2);

    }

    public void setGoodsInfo() {
        String content = mInitValue.goods_name + "," + mInitValue.goods_weight + "吨";
        if (!TextUtils.isEmpty(mInitValue.goods_cubage) && !mInitValue.goods_cubage.equals("0")) {//
            content += "," + mInitValue.goods_cubage + "方";
        } else {
            content += "";
        }
        if (TextUtils.isEmpty(mInitValue.goods_name) || TextUtils.isEmpty(mInitValue.goods_weight)) {
            mProductInfoText.setText("");
        } else {
            mProductInfoText.setText(content);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GET_BUSINESS_TYPE_CODE:
                if (resultCode == BusinessNameListAct.RESULT_CODE) {
                    BusinessListEntity.BusinessItem businessItem = (BusinessListEntity.BusinessItem)
                            data.getSerializableExtra("businessItem");
                    if (businessItem != null) {
                        mCurrentBusinessItem = businessItem;
                        setBusinessStrategy(businessItem);
                    }
                }
                break;
            case GET_TIME_CODE:
                if (resultCode == 100) {
                    mSendTime.setText(data.getStringExtra("goods_time") + " " + data.getStringExtra("goods_time_desc"));
                    mInitValue.goods_loadtime = data.getStringExtra("goods_time");
                    mInitValue.goods_loadtime_index = data.getStringExtra("goods_time_index");
                    mInitValue.goods_loadtime_desc = data.getStringExtra("goods_time_desc");
                    mInitValue.goods_loadtime_desc_index = data.getStringExtra("goods_time_desc_index");

                }
                break;
            case GET_PLACE_CODE:
                if (resultCode == 100) {
                    mInitValue.goods_load_region = data.getStringExtra("goods_load_region");
                    mInitValue.goods_load_address = data.getStringExtra("goods_load_address");
                    mLoadAddress.setText(mInitValue.goods_load_region + " " + mInitValue.goods_load_address);
                } else if (resultCode == 101) {
                    mInitValue.goods_unload_region = data.getStringExtra("goods_unload_region");
                    mInitValue.goods_unload_address = data.getStringExtra("goods_unload_address");

                    mReceAddress.setText(mInitValue.goods_unload_region + " " + mInitValue.goods_unload_address);
                }
                break;
            case GET_PRODUCTINFO_CODE://货物信息
                if (resultCode == 100) {
                    mInitValue.goods_name = data.getStringExtra("goods_name");
                    mInitValue.goods_cubage = data.getStringExtra("goods_cubage");
                    mInitValue.goods_weight = data.getStringExtra("goods_weight");
                    setGoodsInfo();
                    setTruckInfo();
                }
                break;

            case GET_TRUCKINFO_CODE://用车信息
                if (resultCode == 100) {
                    mCarLenselectIndex = data.getStringExtra("mCarLenselectIndex");
                    mCarModelSelectIndex = data.getStringExtra("mCarModelSelectIndex");
                    mInitValue.car_model = data.getStringExtra("car_model");
                    mInitValue.car_model_text = data.getStringExtra("car_model_text");
                    mInitValue.car_length = data.getStringExtra("car_length");
                    mInitValue.car_length_text = data.getStringExtra("car_length_text");
                    mInitValue.car_type = data.getStringExtra("car_type");
                    if (data.getStringExtra("car_type").equals("2")) {
                        mInitValue.goods_cubage = data.getStringExtra("occupy_tiji");//占用体积和货物体积是一致的
                    }
                    mInitValue.occupy_length = data.getStringExtra("occupy_length");
                    setGoodsInfo();
                    setTruckInfo();
                }

                break;
            case GET_REMARK_CODE:
                if (resultCode == 100) {
                    // if(!TextUtils.isEmpty(data.getStringExtra("remark"))){
                    mInitValue.remark = data.getStringExtra("remark");
                    //mInitValue.mutiAndSingleTagText=data.getStringExtra("mutiText")+data.getStringExtra("singleText");
                    if (TextUtils.isEmpty(data.getStringExtra("singleText"))) {
                        mInitValue.mutiAndSingleTagText = data.getStringExtra("mutiText");
                    } else if (TextUtils.isEmpty(data.getStringExtra("mutiText"))) {
                        mInitValue.mutiAndSingleTagText = data.getStringExtra("singleText");
                    } else {
                        mInitValue.mutiAndSingleTagText = data.getStringExtra("mutiText") + "," + data.getStringExtra("singleText");
                    }
                    //mInitValue.mutiAndSingleTagKey=data.getStringExtra("mutiKey")+","+data.getStringExtra("singleKey");
                    if (TextUtils.isEmpty(data.getStringExtra("singleKey"))) {
                        mInitValue.mutiAndSingleTagKey = data.getStringExtra("mutiKey");

                    } else if (TextUtils.isEmpty(data.getStringExtra("mutiKey"))) {
                        mInitValue.mutiAndSingleTagKey = data.getStringExtra("singleKey");
                    } else {
                        mInitValue.mutiAndSingleTagKey = data.getStringExtra("mutiKey") + "," + data.getStringExtra("singleKey");
                    }
                    //mRemarkText.setText(mInitValue.remark);
                    if (TextUtils.isEmpty(mInitValue.remark)) {
                        mRemarkText.setText(mInitValue.mutiAndSingleTagText);
                    } else {
                        if (TextUtils.isEmpty(mInitValue.mutiAndSingleTagText)) {
                            mRemarkText.setText(mInitValue.remark);
                        } else {
                            mRemarkText.setText(mInitValue.mutiAndSingleTagText + "," + mInitValue.remark);
                        }
                    }
                    UtilsLog.i(TAG, "mutiKey===" + data.getStringExtra("mutiKey"));
                    UtilsLog.i(TAG, "singleKey===" + data.getStringExtra("singleKey"));
                }
                // }
                break;
        }
    }

    private void showInvisibleView(boolean flag) {
        if (flag) {
            mPayMethodLayout.setVisibility(View.VISIBLE);
            mFapiaoLayout.setVisibility(View.VISIBLE);
            mFpHuidanLine.setVisibility(View.VISIBLE);
            mHuidanLayout.setVisibility(View.VISIBLE);
            mRemarkLayout.setVisibility(View.VISIBLE);
        } else {
            mPayMethodLayout.setVisibility(View.GONE);
            mFapiaoLayout.setVisibility(View.GONE);
            mFpHuidanLine.setVisibility(View.GONE);
            mHuidanLayout.setVisibility(View.GONE);
            mRemarkLayout.setVisibility(View.GONE);
        }
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

    private void initViewAndData(BusinessListEntity.BusinessData businessData) {
        mInitValue.goods_loadtime = "";
        mInitValue.goods_loadtime_desc = "";
        mLoadAddress.setText(mInitValue.goods_load_region + " " + mInitValue.goods_load_address);
        mReceAddress.setText(mInitValue.goods_unload_region + " " + mInitValue.goods_unload_address);

        String content = mInitValue.goods_name + "," + mInitValue.goods_weight + "吨";
        setNullDefaultQuoteValue();//当传过来的数据为空时的处理
        if (!mInitValue.goods_cubage.equals("") && !mInitValue.goods_cubage.equals("0")) {//
            content += "," + mInitValue.goods_cubage + "方";
        }
        mProductInfoText.setText(content);
        String content2 = "";
        if (mInitValue.car_type.equals("2")) {//拼车
            content2 = "拼车";
            if (!mInitValue.car_model_text.equals("")) {
                content2 += "," + mInitValue.car_model_text;
            }
            content2 += "" + (TextUtils.isEmpty(mInitValue.goods_cubage) ? "" : "," + mInitValue.goods_cubage + "方") +
                    (TextUtils.isEmpty(mInitValue.occupy_length) ? "" : "," + mInitValue.occupy_length + "米");

            if (mConfigEntity != null) {
                List<Map<String, Object>> carTypeList = NetWorkUtils.getCommonCarType(mConfigEntity);
                for (int i = 0; i < carTypeList.size(); i++) {
                    if (mInitValue.car_model_text.contains((String) carTypeList.get(i).get("value"))) {
                        mCarModelSelectIndex = i + "";
                        break;
                    }
                }
            }
        } else {
            content2 = "整车," + mInitValue.car_model_text + "," + mInitValue.car_length_text;
            if (mConfigEntity != null) {
                List<Map<String, Object>> carLengthList = NetWorkUtils.getCommonCarLength(mConfigEntity);
                List<Map<String, Object>> carTypeList = NetWorkUtils.getCommonCarType(mConfigEntity);

                UtilsLog.i(TAG, "mInitValue.car_length_text" + mInitValue.car_length_text);
                mInitValue.car_length_text = mInitValue.car_length_text.contains("米") ? mInitValue.car_length_text.substring(0, mInitValue.car_length_text.indexOf("米")) : mInitValue.car_length_text;
                for (int i = 0; i < carLengthList.size(); i++) {
                    if (mInitValue.car_length_text.equals(carLengthList.get(i).get("value"))) {
                        mCarLenselectIndex = i + "";
                        UtilsLog.i(TAG, "mInitValue.mCarLenselectIndex ==" + mCarLenselectIndex);
                        break;
                    }
                }
                for (int i = 0; i < carTypeList.size(); i++) {
                    if (mInitValue.car_model_text.contains((String) carTypeList.get(i).get("value"))) {
                        mCarModelSelectIndex = i + "";
                        break;
                    }
                }
            }
        }
        mTruckInfo.setText(content2);
        if (TextUtils.isEmpty(mInitValue.remark)) {
            mRemarkText.setText(mInitValue.mutiAndSingleTagText);
        } else {
            if (TextUtils.isEmpty(mInitValue.mutiAndSingleTagText)) {
                mRemarkText.setText(mInitValue.remark);
            } else {
                mRemarkText.setText(mInitValue.mutiAndSingleTagText + "," + mInitValue.remark);
            }
        }
        UtilsLog.i(TAG, "mutiText ===" + mInitValue.mutiAndSingleTagText);
        UtilsLog.i(TAG, "mutiKey2===" + getIntent().getStringExtra("mutiKey"));
        UtilsLog.i(TAG, "singleKey2===" + getIntent().getStringExtra("singleKey"));

        if (businessData.list.size() > 1) {
            mBusinessTextValue.setText(mInitValue.businessName);
            BusinessListEntity.BusinessItem businessItem = new BusinessListEntity.BusinessItem();
            businessItem.id = mInitValue.businessId;
            businessItem.name = mInitValue.businessName;

            businessItem.paidType = new ArrayList<BusinessListEntity.PayType>();
            BusinessListEntity.PayType payType = new BusinessListEntity.PayType();
            payType.id = mInitValue.pay_type1_id;
            payType.name = mInitValue.pay_type1_name;
            businessItem.paidType.add(payType);
            if (!TextUtils.isEmpty(mInitValue.pay_type2_id)) {
                BusinessListEntity.PayType payType2 = new BusinessListEntity.PayType();
                payType2.id = mInitValue.pay_type2_id;
                payType2.name = mInitValue.pay_type2_name;
                businessItem.paidType.add(payType2);
            }
            mCurrentBusinessItem = businessItem;
            setBusinessStrategy(businessItem);

            if (mInitValue.pay_type.equals(mInitValue.pay_type1_id)) {
                choosePayMethod(0);
            } else if (mInitValue.pay_type.equals(mInitValue.pay_type2_id)) {
                choosePayMethod(1);
            }
        }

        UtilsLog.i(TAG, " mInitValue.receipt===" + mInitValue.receipt);

        if (mInitValue.receipt.equals("1")) {// 需要发票
            mInitValue.receipt = "1";
            mNeedFapiaoBtn.setChooseStatus(false, true);
        } else {
            mInitValue.receipt = "0";
            mNeedFapiaoBtn.setChooseStatus(true, false);
        }

        if (mInitValue.need_back.equals("1")) {// 需要回单
            mInitValue.need_back = "1";
            mutiHuiDanBtn.setChooseStatus(false, true);
        } else {
            mInitValue.need_back = "0";
            mutiHuiDanBtn.setChooseStatus(true, false);
        }
    }

    private void setInvoiceType(BusinessListEntity.BusinessItem businessItem) {
        if (!TextUtils.isEmpty(businessItem.invoiceType)
                && businessItem.invoiceType.equals("1")) {
            mNeedFapiaoBtn.setVisibility(View.VISIBLE);
            noNeedFapiaoTv.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(businessItem.invoiceType)
                && businessItem.invoiceType.equals("2")) {
            mNeedFapiaoBtn.setVisibility(View.GONE);
            noNeedFapiaoTv.setVisibility(View.VISIBLE);
            noNeedFapiaoTv.setText("需要");
        } else if (!TextUtils.isEmpty(businessItem.invoiceType)
                && businessItem.invoiceType.equals("3")) {
            mNeedFapiaoBtn.setVisibility(View.GONE);
            noNeedFapiaoTv.setVisibility(View.VISIBLE);
            noNeedFapiaoTv.setText("不需要");
        }
    }


    @Override
    public void showCompanyStrategy(BusinessListEntity.BusinessData businessData) {
        if (businessData == null) {
            UtilsLog.i(TAG, "businessData is null,return");
        }

        if (businessData.is_fuyou.equals("1")) {
            mBusinessTypeIndex = 0;
            mBusinessLayout.setVisibility(View.GONE);
        } else {
            mBusinessLayout.setVisibility(View.VISIBLE);
            if (businessData.list.size() == 1) {
                mBusinessTypeIndex = 1;
                BusinessListEntity.BusinessItem businessItem = businessData.list.get(0);
                mBusinessTextValue.setText(businessItem.name);
                mInitValue.pay_type = businessItem.id;

                mFapiaoLayout.setVisibility(View.VISIBLE);
                setInvoiceType(businessItem);
            } else {
                mBusinessTypeIndex = 2;
                mBusinessTextValue.setText("");
                if (mInitValue == null) {
                    showInvisibleView(false);
                }
            }
        }

        if (fromReorder) {
            initViewAndData(businessData);
        }
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i(TAG, "onDestroy ...");
        areaEntity = null;
    }
}
