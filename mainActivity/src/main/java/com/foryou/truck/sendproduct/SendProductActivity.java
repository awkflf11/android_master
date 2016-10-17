package com.foryou.truck.sendproduct;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.foryou.truck.entity.CarLoadEntity;
import com.foryou.truck.entity.CommonConfigEntity;
import com.foryou.truck.entity.UserDetailEntity;
import com.foryou.truck.parser.AreasJsonParser;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.parser.CarLoadParser;
import com.foryou.truck.parser.CommonConfigJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.parser.UserDetailJsonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.MyActivityManager;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.NetWorkUtils.NetJsonRespon;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.MutiChooseBtn2;
import com.foryou.truck.view.MutiTabChoose.TabClickListener;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @des:货源询价的页面
 */
public class SendProductActivity extends BaseActivity {
    private String TAG = "SendProductActivity";
    private Context mContext;
    @BindView(id = R.id.title)
    private TextView mTitle;
    @BindView(id = R.id.send_time, click = true)
    private TextView mSendTime;
    @BindView(id = R.id.load_address, click = true)
    private TextView mLoadAddress;
    @BindView(id = R.id.rece_address, click = true)
    private TextView mReceAddress;
    @BindView(id = R.id.product_info_layout, click = true)
    private RelativeLayout mProductInfoLayout;
    @BindView(id = R.id.product_info)
    private TextView mProductInfoText;//货物信息
    @BindView(id = R.id.use_truck_info, click = true)
    private RelativeLayout mUserTurckInfo;
    @BindView(id = R.id.truck_info)
    private TextView mTruckInfo;//用车信息
    @BindView(id = R.id.pay_online_layout, click = true)
    RelativeLayout mPayOnLine;//
    @BindView(id = R.id.pay_offline_layout, click = true)
    RelativeLayout mPayOffLine;//运费到付
    @BindView(id = R.id.muti_btn_choose)
    private MutiChooseBtn2 mNeedFapiaoBtn;//发票
    @BindView(id = R.id.noneed_fapiao_tv)
    private TextView noNeedFapiaoTv;//发票
    @BindView(id = R.id.muti_btn_choose2)
    MutiChooseBtn2 mutiHuiDanBtn;//回单
    //
    @BindView(id = R.id.tishi_tv)
    private TextView tiShiTv;
    @BindView(id = R.id.remark_layout, click = true)
    private RelativeLayout mRemarkLayout;
    @BindView(id = R.id.more_info)
    private TextView mRemarkText;
    @BindView(id = R.id.confirm_query, click = true)
    private Button mConfirmBtn;// 开始询价
    @BindView(id = R.id.pay_online_text)
    private TextView mPayOnlineText;
    @BindView(id = R.id.pay_online_text2)
    private TextView mPayOnlineText2;

    @BindView(id = R.id.send_layout, click = true)
    RelativeLayout mSenderLayout;
    @BindView(id = R.id.rece_layout, click = true)
    RelativeLayout mReceLayout;

    @BindView(id = R.id.business_name_layout, click = true)
    RelativeLayout mBusinessNameLayout;
    private CommonConfigEntity mConfigEntity = null;
    private static final int GET_TIME_CODE = 100;
    private static final int GET_PLACE_CODE = 101;
    private static final int GET_PRODUCTINFO_CODE = 102;
    private static final int GET_TRUCKINFO_CODE = 103;
    private static final int GET_REMARK_CODE = 104;

    private UserDetailEntity mUserDetailEntity;
    // private String[] mPayTypeKeyItem, mPayTypeValueItem;
    FirstTabInitValue mInitValue;
    private CarLoadEntity carLoadEntity;
    // car_type 1:整车 2:拼车
    private String mCurrentSelectIndex = "";
    private String mCurrentSelectIndex2 = "";
    //public static ArrayList<ProvinceModel> provinceList = null;
    public static AreasEntity areaEntity = null;

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.send_product);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i(TAG, "onDestroy ...");
        areaEntity = null;
    }

    //根据用户属性默认选中相对应的支付方式
    private void initPayStatus() {
        if (mInitValue.pay_type.equals("3")) {//合同结算
            mInitValue.receipt = "1";
            mNeedFapiaoBtn.setChooseStatus(false, true);
        } else {//在线支付
            mInitValue.receipt = "0";
            mNeedFapiaoBtn.setChooseStatus(true, false);
        }
    }

    private void getUserDetail() {
        NetWorkUtils.getUserDetail(mContext, TAG, new NetWorkUtils.NetJsonRespon() {
            @Override
            public void onRespon(BaseJsonParser parser) {
                // TODO Auto-generated method stub
                UserDetailJsonParser mParser = (UserDetailJsonParser) parser;
                mUserDetailEntity = mParser.entity;
                initPayOnOffOption();
                initTiShi();
                if (mConfigEntity == null) {
                    getConfig();
                } else {
                    initQuoteValue();
                }
            }
        });
    }

    private void initPayOnOffOption() {
        if (mUserDetailEntity.data.type.equals("1")) {
            mPayOnlineText.setText("合同结算");
            mPayOnlineText2.setText("按照合同约定结算");
            if (!fromReOrder) {
                mInitValue.pay_type = "3";// 合同结算 默认需要回单
                mNeedFapiaoBtn.setChooseStatus(false, true);
                mInitValue.receipt = "1";
            }
        } else {
            if (!fromReOrder) {
                mInitValue.pay_type = "5";// 在线支付
                mNeedFapiaoBtn.setChooseStatus(true, false);
                mInitValue.receipt = "0";
            }
        }
        choosePayMethod(true, false, true);

        if (!TextUtils.isEmpty(mUserDetailEntity.data.isOnlyContract)
                && mUserDetailEntity.data.isOnlyContract.equals("1")) {//1，只显示合同结算,且 发票必须需要，
            mPayOffLine.setVisibility(View.GONE);
            mPayOnLine.setClickable(false);
            mPayOnlineText.setText("合同结算");
            mPayOnlineText2.setText("按照合同约定结算");
            if (!fromReOrder) {
                mInitValue.pay_type = "3";// 合同结算
                mInitValue.receipt = "1";
                mNeedFapiaoBtn.setVisibility(View.GONE);
                noNeedFapiaoTv.setVisibility(View.VISIBLE);
                noNeedFapiaoTv.setText("需要");
            }
        }

    }

    private void getConfig() {
        NetWorkUtils.getConfigData(mContext, TAG, new NetWorkUtils.NetJsonRespon() {
            @Override
            public void onRespon(BaseJsonParser parser) {
                // TODO Auto-generated method stub
                CommonConfigJsonParser mParser = (CommonConfigJsonParser) parser;
                mConfigEntity = mParser.entity;
                initQuoteValue();
            }
        });
    }

    //合同客户默认需要发票,并且在线支付改为合同结算
    //getConfig 获取车长车型信息,和选择时间天数
    private boolean fromReOrder = false;

    private void initView() {
        initFaPiaoMutiBtn();
        initHuiDanMutiBtn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate ......");
        mContext = this;
        mInitValue = (FirstTabInitValue) getIntent().getSerializableExtra("firstTabValue");
        mConfigEntity = SharePerfenceUtil.getConfigData(mContext);
        setTitle("货源询价");
        ShowBackView();

        initView();
        if (mInitValue == null) {
            mInitValue = new FirstTabInitValue();
            fromReOrder = false;
            mInitValue.need_back = "0"; //默认不需要回单
        } else {
            fromReOrder = true;
        }

        getUserDetail();
        if (areaEntity == null) {
            NetWorkUtils.getAreaData(mContext, TAG, new NetWorkUtils.NetJsonRespon() {
                @Override
                public void onRespon(BaseJsonParser parser) {
                    areaEntity = ((AreasJsonParser) parser).entity;
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initTiShi() {
        if (mPayOnlineText.getText().toString().equals("在线支付")) {
            tiShiTv.setVisibility(View.VISIBLE);
            String tishi = tiShiTv.getText().toString();
            SpannableString spanText = new SpannableString(tishi);
            spanText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.my_orange_color)), tishi.indexOf("￥"),
                    spanText.length() - 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            tiShiTv.setMovementMethod(LinkMovementMethod.getInstance());
            tiShiTv.setHighlightColor(Color.TRANSPARENT);
            tiShiTv.setText(spanText);
        } else {
            tiShiTv.setVisibility(View.INVISIBLE);
        }
    }


    private void initFaPiaoMutiBtn() {
        mNeedFapiaoBtn.init(new String[]{"不要", "需要"});
        mNeedFapiaoBtn.setOnTabClickListener(new TabClickListener() {
            @Override
            public boolean tabClicked(int index) {
                if (index == 0) {// 不要
                    mInitValue.receipt = "0";
                    mNeedFapiaoBtn.setChooseStatus(true, false);
                } else {
                    mInitValue.receipt = "1";
                    mNeedFapiaoBtn.setChooseStatus(false, true);
                }
                return false;
            }
        });

    }

    private void initHuiDanMutiBtn() {
        mutiHuiDanBtn.init(new String[]{"不要", "需要"});
        mutiHuiDanBtn.setOnTabClickListener(new TabClickListener() {
            @Override
            public boolean tabClicked(int index) {
                if (index == 0) {// 不要
                    mInitValue.need_back = "0";
                    mutiHuiDanBtn.setChooseStatus(true, false);
                } else {
                    mInitValue.need_back = "1";
                    mutiHuiDanBtn.setChooseStatus(false, true);
                }
                return false;
            }
        });

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
                    mCurrentSelectIndex = data.getStringExtra("mCurrentSelectIndex");
                    mCurrentSelectIndex2 = data.getStringExtra("mCurrentSelectIndex2");
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

    private void initQuoteValue() {
        if (!fromReOrder) {
            return;
        }
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
            if (!mInitValue.car_length_text.equals("")) {
                // content2 += "," + mInitValue.car_length_text;
            }
            // content2 += "," + mInitValue.occupy_length + "米";
            content2 += "" + (TextUtils.isEmpty(mInitValue.goods_cubage) ? "" : "," + mInitValue.goods_cubage + "方") +
                    (TextUtils.isEmpty(mInitValue.occupy_length) ? "" : "," + mInitValue.occupy_length + "米");

            if (mConfigEntity != null) {
                List<Map<String, Object>> carTypeList = NetWorkUtils.getCommonCarType(mConfigEntity);
                for (int i = 0; i < carTypeList.size(); i++) {
                    if (mInitValue.car_model_text.contains((String) carTypeList.get(i).get("value"))) {
                        mCurrentSelectIndex2 = i + "";
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
                        mCurrentSelectIndex = i + "";
                        UtilsLog.i(TAG, "mInitValue.mCurrentSelectIndex ==" + mCurrentSelectIndex);
                        UtilsLog.i(TAG, "(String) carLengthList.get(i).get(\"value\") ==" + carLengthList.get(i).get("value"));
                        break;
                    }
                }
                for (int i = 0; i < carTypeList.size(); i++) {
                    if (mInitValue.car_model_text.contains((String) carTypeList.get(i).get("value"))) {
                        mCurrentSelectIndex2 = i + "";
                        break;
                    }
                }
            }
        }
        mTruckInfo.setText(content2);
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
        UtilsLog.i(TAG, "mutiText ===" + mInitValue.mutiAndSingleTagText);
        UtilsLog.i(TAG, "mutiKey2===" + getIntent().getStringExtra("mutiKey"));
        UtilsLog.i(TAG, "singleKey2===" + getIntent().getStringExtra("singleKey"));

        if (mInitValue.pay_type.equals("3") || mInitValue.pay_type.equals("5")) {//在线支付和合同用户
            choosePayMethod(true, false, true);
        } else if (mInitValue.pay_type.equals("2")) {
            choosePayMethod(false, false, true);
        }

        UtilsLog.i(TAG, " mInitValue.receipt===" + mInitValue.receipt);
        if (!TextUtils.isEmpty(mUserDetailEntity.data.isOnlyContract)
                && mUserDetailEntity.data.isOnlyContract.equals("1")) {//1，只显示合同结算 且 发票必是需要，
            mInitValue.receipt = "1";
            mNeedFapiaoBtn.setVisibility(View.GONE);
            noNeedFapiaoTv.setVisibility(View.VISIBLE);
            noNeedFapiaoTv.setText("需要");
        } else {
            noNeedFapiaoTv.setText("不要");
        }

        if (mInitValue.receipt.equals("1")) {// 需要发票
            mInitValue.receipt = "1";
            mNeedFapiaoBtn.setChooseStatus(false, true);
        } else {
            mInitValue.receipt = "0";
            mNeedFapiaoBtn.setChooseStatus(true, false);
        }

        if (mInitValue.need_back.equals("1")) {// 需要回程
            mInitValue.need_back = "1";
            mutiHuiDanBtn.setChooseStatus(false, true);
        } else {
            mInitValue.need_back = "0";
            mutiHuiDanBtn.setChooseStatus(true, false);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        MyApplication.getInstance().cancelAllRequests(TAG);
        cancelProgressDialog();
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
                                ToastUtils.toast(mContext, "新增询价成功");
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
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed ...");
        judgeBack();
    }

    private void judgeBack() {
        if (!TextUtils.isEmpty(mSendTime.getText().toString()) ||
                !TextUtils.isEmpty(mLoadAddress.getText().toString()) || !TextUtils.isEmpty(mReceAddress.getText().toString())
                || !TextUtils.isEmpty(mTruckInfo.getText().toString()) || !TextUtils.isEmpty(mProductInfoText.getText().toString())) {
            alertDialog("确认离开吗？您已填写的数据将会丢失", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClickListener(int id) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (id) {
            case R.id.send_time:
                // DataAndTimePickDialog mSendTimeDialog = new
                // DataAndTimePickDialog();
                if (mConfigEntity == null || mUserDetailEntity == null) {
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
                if (mConfigEntity == null || mUserDetailEntity == null) {
                    ToastUtils.toast(mContext, "正在获取配置文件，请稍候再试");
                    return;
                }
                intent = new Intent(mContext, getTruckInfoActivity.class);
                intent.putExtra("goods_weight", mInitValue.goods_weight);// 货物重量
                intent.putExtra("car_type", mInitValue.car_type);
                intent.putExtra("car_model", mInitValue.car_model);
                intent.putExtra("car_model_text", mInitValue.car_model_text);
                intent.putExtra("car_length", mInitValue.car_length);
                intent.putExtra("car_length_text", mInitValue.car_length_text);
                intent.putExtra("occupy_length", mInitValue.occupy_length);
                intent.putExtra("occupy_tiji", mInitValue.goods_cubage);//占用体积
                intent.putExtra("mCurrentSelectIndex", mCurrentSelectIndex);
                UtilsLog.i(TAG, "mCurrentSelectIndex==" + mCurrentSelectIndex);
                intent.putExtra("mCurrentSelectIndex2", mCurrentSelectIndex2);
                startActivityForResult(intent, GET_TRUCKINFO_CODE);
                break;
            case R.id.pay_online_layout:
                choosePayMethod(true, true, false);
                break;
            case R.id.pay_offline_layout:
                choosePayMethod(false, true, false);
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

                    if (mPayOnLine.getTag().equals("0") && mPayOffLine.getTag().equals("0")) {
                        ToastUtils.toast(mContext, "请选择结算方式");
                        return;
                    }
                    // 5=>在线支付 ;  2=>运费到付;3=>合同客户结算
                    if (mPayOnLine.getTag().equals("1")) {
                        UtilsLog.i(TAG, "mUserDetailEntity.data.type======" + mUserDetailEntity.data.type);
                        if (mUserDetailEntity.data.type.equals("1")) {// 0:普通客户 ， 1:合同客户
                            mInitValue.pay_type = "3";// 合同结算
                        } else {
                            mInitValue.pay_type = "5";// 在线支付
                        }
                    } else {
                        mInitValue.pay_type = "2";//运费到付
                    }

                    if (!TextUtils.isEmpty(mUserDetailEntity.data.isOnlyContract)
                            && mUserDetailEntity.data.isOnlyContract.equals("1")) {// 1,必须 仅显示合同结算，
                        mInitValue.pay_type = "3";// 合同结算
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
                            NetWorkUtils.SaveCarLoad(mContext, new NetJsonRespon() {
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

            case R.id.business_name_layout:
                intent = new Intent(mContext,BusinessNameListAct.class);
                startActivity(intent);
                break;
        }
    }


    private void choosePayMethod(boolean flag, boolean isToast, boolean isInitValue) {
        if (flag) {//合同结算和 在线支付
            mNeedFapiaoBtn.setVisibility(View.VISIBLE);
            noNeedFapiaoTv.setVisibility(View.GONE);
            if (!isInitValue) {
                if (mInitValue == null || mInitValue.pay_type == null) {
                    ToastUtils.toast(mContext, "正在获取配置文件，请稍候再试");
                    return;
                }
                if (mInitValue.pay_type.equals("3")) {//合同结算
                    mInitValue.receipt = "1";
                    mNeedFapiaoBtn.setChooseStatus(false, true);
                } else {//5，在线支付
                    mInitValue.receipt = "0";
                    mNeedFapiaoBtn.setChooseStatus(true, false);
                }
            }

            if (mPayOnLine.getTag().equals("1")) {
                return;
            }
            mPayOnLine.setTag("1");
            mPayOnLine.findViewById(R.id.arrow_icon6).setBackgroundResource(R.drawable.check_on_btn);
            mPayOffLine.setTag("0");
            mPayOffLine.findViewById(R.id.arrow_icon7).setBackgroundResource(R.drawable.check_off_btn);

        } else {//运费到付
            mInitValue.receipt = "0";
            if (mPayOffLine.getTag().equals("1")) {
                return;
            }
            mNeedFapiaoBtn.setVisibility(View.GONE);
            noNeedFapiaoTv.setVisibility(View.VISIBLE);
            noNeedFapiaoTv.setText("不要");
            if (isToast) {
                ToastUtils.toast(mContext, "运费到付不可开发票");
            }
            mPayOffLine.setTag("1");
            mPayOffLine.findViewById(R.id.arrow_icon7).setBackgroundResource(R.drawable.check_on_btn);

            mPayOnLine.setTag("0");
            mPayOnLine.findViewById(R.id.arrow_icon6).setBackgroundResource(R.drawable.check_off_btn);
        }
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

}
