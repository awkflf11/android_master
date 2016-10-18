package com.foryou.truck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.CommonConfigEntity;
import com.foryou.truck.entity.UserContractEntity.ContractInfo;
import com.foryou.truck.entity.UserDetailEntity;
import com.foryou.truck.parser.AvailSuranceJasonParser;
import com.foryou.truck.parser.GetUserContractJsonParser;
import com.foryou.truck.parser.QuoteConfirmParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.MyActivityManager;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.SpinnerPopUpListView;
import com.foryou.truck.view.TextViewWithIconFont;
import com.foryou.truck.view.WithDelImgEditText;
import com.foryou.truck.view.WithSpreadButton;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @des: 确定下单
 */
public class BeginSendProductAct extends BaseActivity {
    private Context mContext;
    private String TAG = "BeginSendProductAct";
    @BindView(id = R.id.send_info_layout)
    private RelativeLayout mSenderInfo;
    @BindView(id = R.id.rece_info_layout)
    private RelativeLayout mRecerInfo;
    @BindView(id = R.id.pay_type)
    private TextView mPayType;
    @BindView(id = R.id.baoxian_title)
    private TextView mBaoxianTitle;
    @BindView(id = R.id.not_buy_baoxian, click = true)
    private RelativeLayout mNotBuyBaoxian;
    @BindView(id = R.id.buy_baoxian_layout, click = true)
    private RelativeLayout mBuyBaoxian;
    @BindView(id = R.id.discount_layout, click = true)
    private RelativeLayout mDiscountLayout;//代金券
    @BindView(id = R.id.discount_line)
    private View mDiscountLine;
    @BindView(id = R.id.daijinquan_hint)
    private TextView mDaijinHint;
    @BindView(id = R.id.xieyi, click = true)
    private TextView mXieyi;
    @BindView(id = R.id.button_layout)
    WithSpreadButton mConfirmBtn;

    private ImageView mSendPhoneImg, mRecePhoneImg;
    private TextViewWithIconFont mSendImg, mReceImg;
    private AutoCompleteTextView mSendName, mReceName;
    private WithDelImgEditText mSendPhone, mRecePhone;
    private WithDelImgEditText mSendPhone2, mRecePhone2;
    private GetUserContractJsonParser mUserContractParser;
    private CommonConfigEntity mConfigEntity;
    private int REQUEST_SENDER_REQUEST = 100;
    private int REQUEST_RECE_REQUEST = 101;
    private int REQUEST_BAOXIAN_REQUEST = 102;
    //private int REQUEST_COUPON_REQUEST = 103;
    private int REQUEST_XIEYI_REQUEST = 104;
    private String avaiInsurance = "0";
    private String faPiaoStr = "0";//0不显示，1显示
    @BindView(id = R.id.fapiao_taitou_layout)
    private RelativeLayout mFapiaoLayout;//发票抬头
    @BindView(id = R.id.fapiao_edit)
    private WithDelImgEditText mFapiaoEdit;
    @BindView(id = R.id.baoxian_ll)
    private LinearLayout baoxianLL;//购买保险方式的部分
    @BindView(id = R.id.goods_value_layout)
    RelativeLayout mGoodsValueLayout;
    @BindView(id = R.id.goods_value_edit)
    WithDelImgEditText mGoodsValueEdit;

    @Override
    public void onStart() {
        super.onStart();
    }

    private class BaoxianInfo {
        String insurance_name = "";
        String insurance_num = ""; // 货物件数
        String insurance_package = "";
        String insurance_type = "";
        String insurance_type_value = "";
        String insurance_value = ""; // 货物价值
        String insuranceGoods = ""; //保险计算费率 如0.6
        String insurance_money = "";//保险费用
        String insurance_cal_money = ""; //计算得出的保险费用
        String gift_insurance = ""; //赠送保险金额
    }

    @BindView(id = R.id.xieyi_check_img, click = true)
    ImageView mAgreeCheckImg;
    @BindView(id = R.id.tongyi_text, click = true)
    private TextView mTongyiText;

    BaoxianInfo mBaoxianInfo = new BaoxianInfo();

    private void showBaoxianLayout(boolean flag) {
        if (flag) {
            mNotBuyBaoxian.setVisibility(View.VISIBLE);
            mBuyBaoxian.setVisibility(View.VISIBLE);
            mBaoxianTitle.setVisibility(View.VISIBLE);
        } else {
            mNotBuyBaoxian.setVisibility(View.GONE);
            mBuyBaoxian.setVisibility(View.GONE);
            mBaoxianTitle.setVisibility(View.GONE);
        }
    }

    private void showDiscountLayout(boolean flag) {
        if (flag) {
            mDiscountLayout.setVisibility(View.VISIBLE);
            mDiscountLine.setVisibility(View.VISIBLE);
        } else {
            mDiscountLayout.setVisibility(View.GONE);
            mDiscountLine.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener mPhoneListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(ContactsContract.Contacts.CONTENT_URI);
                if (v == mSendPhoneImg) {
                    TongjiModel.addEvent(mContext, "确定下单",
                            TongjiModel.TYPE_BUTTON_CLIKC, "装货_联系人");
                    startActivityForResult(intent, REQUEST_SENDER_REQUEST);
                } else {
                    startActivityForResult(intent, REQUEST_RECE_REQUEST);
                    TongjiModel.addEvent(mContext, "确定下单",
                            TongjiModel.TYPE_BUTTON_CLIKC, "卸货_联系人");
                }
            } catch (Exception e) {

            }

        }
    };

    private String getContactPhone(Cursor cursor) {
        // TODO Auto-generated method stub
        int phoneColumn = cursor
                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = 0;
        String result = "";
        try {
            phoneNum = cursor.getInt(phoneColumn);
            if (phoneNum > 0) {
                // 获得联系人的ID号
                int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                String contactId = cursor.getString(idColumn);
                // 获得联系人电话的cursor
                Cursor phone = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                                + contactId, null, null);

                if (phone.moveToFirst()) {
                    for (; !phone.isAfterLast(); phone.moveToNext()) {
                        int index = phone
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int typeindex = phone
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                        int phone_type = phone.getInt(typeindex);
                        String phoneNumber = phone.getString(index);
                        result = phoneNumber;
                    }
                    if (!phone.isClosed()) {
                        phone.close();
                    }
                }
            }

        } catch (Exception e) {
            return "";
        }
        return result;
    }

    private void setBuyBaoxianStatus(boolean flag) {
        TextView mBuyBaoxianTextView = (TextView) mBuyBaoxian.findViewById(R.id.buy_baoxian);
        ImageView mBuyBaoxianImg = (ImageView) mBuyBaoxian.findViewById(R.id.buy_baoxian_img);
        TextView mBuyBaoxianHint = (TextView) mBuyBaoxian.findViewById(R.id.buy_baoxian_hint);
        if (flag) {
            mBuyBaoxianTextView.setTextColor(getResources().getColor(R.color.write_text_color));
            mBuyBaoxianImg.setBackgroundResource(R.drawable.check_off_btn);
            mBuyBaoxianHint.setVisibility(View.GONE);
        } else {
            mBuyBaoxianTextView.setTextColor(getResources().getColor(R.color.hint_color));
            mBuyBaoxianImg.setBackgroundResource(R.drawable.check_off_btn2);
            mBuyBaoxianHint.setVisibility(View.VISIBLE);
            setBaoxianChecked(false);
        }
        mBuyBaoxian.setEnabled(flag);
    }

    private TextWatcher mProductValueEditListenre = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mConfirmBtn.getSpreadFlag()) {
                double baoxianValue;
                baoxianValue = Double.valueOf(mBaoxianInfo.insuranceGoods) * Double.valueOf(s.toString()) * 0.01;
                mBaoxianInfo.insurance_cal_money = "" + baoxianValue;
                mConfirmBtn.setFirstBtnText("运费总额:  ", "¥" +
                        Float.valueOf(zong_e) + baoxianValue);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void getAvailInsurace() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("order_id", order_id);
        showProgressDialog();
        NetWorkUtils.BasePostNetWorkRequest(mContext, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                cancelProgressDialog();
                AvailSuranceJasonParser mParser = new AvailSuranceJasonParser();
                UtilsLog.i(TAG, "order/avaiInsurance/respone====" + s);
                int result = mParser.parser(s);
                if (result == 1) {
                    if (mParser.entity.status.equals("Y")) {
                        if (!TextUtils.isEmpty(mParser.entity.receipt)) {
                            faPiaoStr = mParser.entity.receipt;
                        }
                        if (!TextUtils.isEmpty(mParser.entity.avaiInsurance)) {
                            avaiInsurance = mParser.entity.avaiInsurance;
                        }

                        if (avaiInsurance.equals("1")) {
                        } else {
                            setBuyBaoxianStatus(false);
                        }
                        if (faPiaoStr.equals("0")) {
                            mFapiaoLayout.setVisibility(View.GONE);
                        } else {
                            mFapiaoLayout.setVisibility(View.VISIBLE);
                        }

                        if (!TextUtils.isEmpty(mParser.entity.insuranceType)
                                && mParser.entity.insuranceType.equals("2")) { //强制购买
                            baoxianLL.setVisibility(View.GONE);

                            if (!TextUtils.isEmpty(mParser.entity.insuranceTypeSelect)
                                    && mParser.entity.insuranceTypeSelect.equals("1")) {
                                mGoodsValueLayout.setVisibility(View.VISIBLE);
                                mGoodsValueEdit.addTextChangedListener(mProductValueEditListenre);
                            }
                        } else {
                            baoxianLL.setVisibility(View.VISIBLE);
                        }

                        if (!TextUtils.isEmpty(mParser.entity.insuranceTypeSelect)
                                && mParser.entity.insuranceTypeSelect.equals("1")) {
                            mBaoxianInfo.insuranceGoods = mParser.entity.insuranceGoods;
                        } else {
                            mBaoxianInfo.insurance_money = insurance_money;
                        }
                    } else {
                        ToastUtils.toast(mContext, mParser.entity.msg);
                    }
                } else {
                    Log.i(TAG, "解析错误");
                }
            }
        }, TAG, "order/avaiInsurance", map);
    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SENDER_REQUEST
                || requestCode == REQUEST_RECE_REQUEST) {
            if (data == null) {
                return;
            }
            Uri result = data.getData();
            Cursor cursor;
            String name = "";
            String phone = "";
            try {
                cursor = managedQuery(result, null, null, null, null);
                cursor.moveToFirst();
                phone = this.getContactPhone(cursor);
                name = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            } catch (Exception e) {
                ToastUtils.toast(mContext, "请在设置里面设置允许读取联系人信息");
                return;
            }
            // 显示
            setNameAndPhone(requestCode, name, removeInValideCharact(phone));

        } else if (requestCode == REQUEST_BAOXIAN_REQUEST) {
            if (data == null) {
                return;
            }
            mBaoxianInfo.insurance_name = data.getStringExtra("insurance_name");
            mBaoxianInfo.insurance_num = data.getStringExtra("insurance_num");
            mBaoxianInfo.insurance_package = data
                    .getStringExtra("insurance_package");
            mBaoxianInfo.insurance_type = data.getStringExtra("insurance_type");
            mBaoxianInfo.insurance_type_value = data
                    .getStringExtra("insurance_type_value");
            if (!mBaoxianInfo.insurance_name.equals("")) {
                ((TextView) mBuyBaoxian.findViewById(R.id.baoxian_value))
                        .setText("保险人：" + mBaoxianInfo.insurance_name);
            }
            mBaoxianInfo.insurance_value = data.getStringExtra("insurance_value");
            mBaoxianInfo.insurance_cal_money = data.getStringExtra("insurance_cal_money");

            if (!TextUtils.isEmpty(mBaoxianInfo.insuranceGoods)) {
                mConfirmBtn.setFirstBtnText("运费总额:  ", "¥" +
                        Float.valueOf(zong_e) + Float.valueOf(mBaoxianInfo.insurance_cal_money));
            }
        } else if (requestCode == REQUEST_XIEYI_REQUEST) {
            if (resultCode == Constant.AGREEMENT_CODE) {
                if (mAgreeCheckImg.getTag().equals("0")) {
                    mAgreeCheckImg.setImageResource(R.drawable.check_on_btn);
                    mAgreeCheckImg.setTag("1");
                }
            }
        }
    }

    private void setNameAndPhone(int requestCode, String name, String phone) {
        if (requestCode == REQUEST_SENDER_REQUEST) {
            mSendName.setText(name);
            mSendPhone.setText(phone);
            mSendName.dismissDropDown();
        } else if (requestCode == REQUEST_RECE_REQUEST) {
            mReceName.setText(name);
            mRecePhone.setText(phone);
            mReceName.dismissDropDown();
        }
    }

    private String removeInValideCharact(String content) {
        String result = "";
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            if (ch != ' ' && ch != '-') {
                result += ch;
            }
        }
        if (result.length() > 11) {
            result = result.substring(result.length() - 11, result.length());
        }
        return result;
    }

    public void initAutoCompleteTextViewData(final boolean sender) {
        String[] hisArrays;
        AutoCompleteTextView mName;
        if (sender) {
            hisArrays = new String[mUserContractParser.entity.data.sender
                    .size()];
            mName = mSendName;
        } else {

            hisArrays = new String[mUserContractParser.entity.data.receiver
                    .size()];
            mName = mReceName;
        }
        UtilsLog.i(TAG, "hisArrays.length:" + hisArrays.length);
        for (int i = 0; i < hisArrays.length; i++) {
            if (sender) {
                hisArrays[i] = mUserContractParser.entity.data.sender.get(i).name
                        + "  "
                        + mUserContractParser.entity.data.sender.get(i).mobile;
            } else {
                hisArrays[i] = mUserContractParser.entity.data.receiver.get(i).name
                        + "  "
                        + mUserContractParser.entity.data.receiver.get(i).mobile;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.dialog_listitem, R.id.text, hisArrays);
        mName.setAdapter(adapter);
        mName.setThreshold(1);
        mName.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                List<ContractInfo> list;
                if (sender) {
                    list = mUserContractParser.entity.data.sender;
                } else {
                    list = mUserContractParser.entity.data.receiver;
                }
                int index;
                String selectContent = (String) parent
                        .getItemAtPosition(position);
                Log.i("GetSenderInfoActivity", "selectMobile:" + selectContent);
                for (index = 0; index < list.size(); index++) {
                    String listContent = list.get(index).name + "  "
                            + list.get(index).mobile;
                    Log.i("GetSenderInfoActivity", "listMobile:" + listContent);
                    if (selectContent.equals(listContent)) {
                        Log.i("GetSenderInfoActivity", "list.get(index).name:"
                                + list.get(index).name);
                        break;
                    }
                }
                if (index != list.size()) {
                    if (sender) {
                        mSendPhone.setText(list.get(index).mobile);
                        mSendName.setText(list.get(index).name);
                        mSendPhone2.setText(list.get(index).mobile2);
                    } else {
                        mRecePhone.setText(list.get(index).mobile);
                        mReceName.setText(list.get(index).name);
                        mRecePhone2.setText(list.get(index).mobile2);
                    }

                }
            }

        });
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        }
    };

    private void initView() {
        TextView mSendPlace = (TextView) mSenderInfo.findViewById(R.id.send_place);
        mSendPlace.setText(load_region + load_address);
        mSendPhoneImg = (ImageView) mSenderInfo.findViewById(R.id.phone_img);
        mSendPhoneImg.setOnClickListener(mPhoneListener);
        mSendName = (AutoCompleteTextView) mSenderInfo.findViewById(R.id.sender_name);
        mSendImg = (TextViewWithIconFont) mSenderInfo.findViewById(R.id.send_img);
        mSendImg.setText(getString(R.string.dingwei_icon));
        mSendPhone2 = (WithDelImgEditText) mSenderInfo.findViewById(R.id.sender_phone2);
        mSendPhone = (WithDelImgEditText) mSenderInfo.findViewById(R.id.sender_phone);
        TextView mRecePlace = (TextView) mRecerInfo.findViewById(R.id.send_place);
        TextView mReceNameText = (TextView) mRecerInfo.findViewById(R.id.send_name_text);
        mReceNameText.setText("卸货人姓名");
        TextView mRecePhoneText = (TextView) mRecerInfo.findViewById(R.id.send_phone_text);
        mRecePhoneText.setText("卸货人手机");
        mRecePlace.setText(unload_region + unload_address);
        mRecePhoneImg = (ImageView) mRecerInfo.findViewById(R.id.phone_img);
        mRecePhoneImg.setOnClickListener(mPhoneListener);
        mReceImg = (TextViewWithIconFont) mRecerInfo.findViewById(R.id.send_img);
        mReceImg.setText(getString(R.string.qizi_icon));
        mReceImg.setTextColor(getResources().getColor(R.color.my_pink_color));
        mReceName = (AutoCompleteTextView) mRecerInfo.findViewById(R.id.sender_name);
        mReceName.setHint("卸货人姓名");
        mRecePhone = (WithDelImgEditText) mRecerInfo.findViewById(R.id.sender_phone);
        mRecePhone.setHint("卸货人手机");
        mRecePhone2 = (WithDelImgEditText) mRecerInfo.findViewById(R.id.sender_phone2);

        for (int i = 0; i < mConfigEntity.data.system_switch.size(); i++) {
            String key = mConfigEntity.data.system_switch.get(i).key;
            String value = mConfigEntity.data.system_switch.get(i).value;
            Log.i(TAG, "key:" + key + ",value:" + value);
            if (key.equals("insurance_switch")) {
                if (value.equals("1")) {
                    showBaoxianLayout(true);
                } else {
                    showBaoxianLayout(false);
                }
            }
            if (key.equals("coupon_switch")) {
                if (value.equals("1")) {
                    showDiscountLayout(true);
                } else {
                    showDiscountLayout(false);
                }
            }
        }

        if (pay_type.equals("2")) {
            mPayType.setText("运费到付");
            showDiscountLayout(false);
        } else if (pay_type.equals("3")) {
            mPayType.setText("合同结算");

        } else if (pay_type.equals("5")) {
            mPayType.setText("在线支付");
        }

        initButton(pay_type);
    }

    private void initButton(String pay_type) {
        if (pay_type.equals("2")
                || pay_type.equals("3")) {
            mConfirmBtn.setSpreadFlag(true);
            mConfirmBtn.setFirstBtnText("运费总额:  ", "¥" + zong_e);
            mConfirmBtn.setSecondBtnText("确定下单");
        } else {
            mConfirmBtn.setFirstBtnText("确定下单");
            mConfirmBtn.setSpreadFlag(false);
        }

        mConfirmBtn.setSpreadOnClickLister(new WithSpreadButton.SpreadOnclickListener() {
            @Override
            public void fistBtnClick() {
                if (mConfirmBtn.getSpreadFlag()) {
                    View view = LinearLayout.inflate(mContext, R.layout.yunfei_detail_view, null);
                    TextView yunfei = (TextView) view.findViewById(R.id.yunfei);
                    yunfei.setText("¥" + zong_e);

                    TextView baoxianText = (TextView) view.findViewById(R.id.baoxian_text);
                    TextView baoxian = (TextView) view.findViewById(R.id.baoxian);
                    if (TextUtils.isEmpty(mBaoxianInfo.insuranceGoods)) {
                        if (!TextUtils.isEmpty(insurance_money)) {
                            baoxian.setText("¥" + insurance_money);
                        } else {
                            baoxianText.setVisibility(View.GONE);
                            baoxian.setVisibility(View.GONE);
                        }
                    } else {
                        if (!TextUtils.isEmpty(mBaoxianInfo.insurance_cal_money)) {
                            baoxian.setText("¥" + mBaoxianInfo.insurance_cal_money);
                        } else {
                            baoxianText.setVisibility(View.GONE);
                            baoxian.setVisibility(View.GONE);
                        }
                    }

                    TextView suifeiText = (TextView) view.findViewById(R.id.suifei_text);
                    TextView suifei = (TextView) view.findViewById(R.id.suifei);
                    if (!TextUtils.isEmpty(invoice_money)) {
                        suifei.setText("¥" + invoice_money);
                    } else {
                        suifeiText.setVisibility(View.GONE);
                        suifei.setVisibility(View.GONE);
                    }

                    TextView giveInsurance = (TextView) view.findViewById(R.id.give_baoxian_text);
                    TextView giveInssuranceValue = (TextView) view.findViewById(R.id.give_baoxian);
                    if (!TextUtils.isEmpty(gift_insurance)) {
                        giveInssuranceValue.setText("-¥" + gift_insurance);
                    } else {
                        giveInsurance.setVisibility(View.GONE);
                        giveInssuranceValue.setVisibility(View.GONE);
                    }

                    SpinnerPopUpListView mSpinnerPopUpView = new SpinnerPopUpListView(mContext, view
                            , mConfirmBtn, SpinnerPopUpListView.DropAboveStyle);
                    mSpinnerPopUpView.Show();
                } else {

                }
            }

            @Override
            public void secondBtnClick() {

            }
        });
    }


    private String order_id, agent_id, baojia, name, pay_type, load_region,
            load_address, unload_region, unload_address, zong_e, invoice_money, insurance_money, gift_insurance, order_money;

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.begin_send_product2);
        setTitle("确定下单");
        ShowBackView();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        alertDialog("确认离开吗？如您已填写信息,您已填写的信息将会丢失！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    //zong_e,invoice_money
    //,insurance_money,gift_insurance,order_money

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        Intent intent = getIntent();
        order_id = intent.getStringExtra("order_id");
        agent_id = intent.getStringExtra("agent_id");
        baojia = intent.getStringExtra("baojia");
        name = intent.getStringExtra("name");
        pay_type = intent.getStringExtra("pay_type");
        load_region = intent.getStringExtra("load_region");
        load_address = intent.getStringExtra("load_address");
        unload_region = intent.getStringExtra("unload_region");
        zong_e = intent.getStringExtra("zong_e");
        invoice_money = intent.getStringExtra("invoice_money");
        insurance_money = intent.getStringExtra("insurance_money");
        gift_insurance = intent.getStringExtra("gift_insurance");
        order_money = intent.getStringExtra("order_money");

        mConfigEntity = SharePerfenceUtil.getConfigData(mContext);
        if (mConfigEntity != null) {
            initView();
        }

        NetWorkUtils.getUserContract(mContext, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                UtilsLog.i(TAG, "response:" + response);
                GetUserContractJsonParser mParser = new GetUserContractJsonParser();
                int result = mParser.parser(response);
                if (result == 1) {
                    if (mParser.entity.status.equals("Y")) {
                        SharePerfenceUtil.SaveUserContactData(mContext, response);
                        mUserContractParser = mParser;
                        if (mUserContractParser.entity.data != null
                                && mUserContractParser.entity.data.sender
                                .size() > 0) {
                            initAutoCompleteTextViewData(true);
                        }
                        if (mUserContractParser.entity.data != null
                                && mUserContractParser.entity.data.receiver.size() > 0) {
                            initAutoCompleteTextViewData(false);
                        }
                    } else {
                        ToastUtils.toast(mContext, mParser.entity.msg);
                    }
                } else {
                    Log.i(TAG, "解析错误");
                }
            }
        }, TAG);

        getAvailInsurace();
    }

    private void setBaoxianChecked(boolean flag) {
        if (flag) {
            mNotBuyBaoxian.findViewById(R.id.not_buy_baoxian_img)
                    .setBackgroundResource(R.drawable.check_off_btn);
            mBuyBaoxian.findViewById(R.id.buy_baoxian_img)
                    .setBackgroundResource(R.drawable.check_on_btn);
            mNotBuyBaoxian.setTag("0");
            mBuyBaoxian.setTag("1");
            Intent intent = new Intent(mContext, GetBaoxianActivity.class);
            intent.putExtra("insurance_name", mBaoxianInfo.insurance_name);
            intent.putExtra("insurance_num", mBaoxianInfo.insurance_num);
            intent.putExtra("insurance_package", mBaoxianInfo.insurance_package);
            intent.putExtra("insurance_type", mBaoxianInfo.insurance_type);
            intent.putExtra("insurance_type_value", mBaoxianInfo.insurance_type_value);
            intent.putExtra("insurance_value", mBaoxianInfo.insurance_value);
            intent.putExtra("pay_type", pay_type);
            intent.putExtra("insuranceGoods", mBaoxianInfo.insuranceGoods);
            intent.putExtra("insurance_money", mBaoxianInfo.insurance_money);
            intent.putExtra("gift_insurance", gift_insurance);
            startActivityForResult(intent, REQUEST_BAOXIAN_REQUEST);
        } else {
            mNotBuyBaoxian.findViewById(R.id.not_buy_baoxian_img).setBackgroundResource(R.drawable.check_on_btn);
            mBuyBaoxian.findViewById(R.id.buy_baoxian_img).setBackgroundResource(R.drawable.check_off_btn);
            mNotBuyBaoxian.setTag("1");
            mBuyBaoxian.setTag("0");
        }
    }

    @Override
    public void onClickListener(int id) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (id) {
            case R.id.not_buy_baoxian:
                setBaoxianChecked(false);
                break;
            case R.id.buy_baoxian_layout:
                setBaoxianChecked(true);
                break;

            case R.id.xieyi:
                //intent = new Intent(this, XieyiActivity.class);
                intent = new Intent(this, XieyiContentActivity.class);
                intent.putExtra("order_id", order_id);
                intent.putExtra("xiyi_flag", 1);
                this.startActivityForResult(intent, 104);
                break;
            case R.id.tongyi_text:
            case R.id.xieyi_check_img:
                if (mAgreeCheckImg.getTag().equals("0")) {
                    mAgreeCheckImg.setImageResource(R.drawable.check_on_btn);
                    mAgreeCheckImg.setTag("1");
                } else {
                    mAgreeCheckImg.setImageResource(R.drawable.check_off_btn);
                    mAgreeCheckImg.setTag("0");
                }
                break;
            case R.id.to_order:
                if (mSendName.getText().toString().equals("")) {
                    ToastUtils.toast(this, "请填写装货人姓名");
                    return;
                }
                if (mSendPhone.getText().toString().equals("")) {
                    ToastUtils.toast(this, "请填写装货人手机");
                    return;
                }
                if (mReceName.getText().toString().equals("")) {
                    ToastUtils.toast(this, "请填写卸货人姓名");
                    return;
                }
                if (mRecePhone.getText().toString().equals("")) {
                    ToastUtils.toast(this, "请填写卸货人手机");
                    return;
                }

                if (faPiaoStr.equals("1")) {
                    if (mFapiaoEdit.getText().toString().trim().equals("")) {
                        ToastUtils.toast(this, "请填写发票抬头");
                        return;
                    }
                }
                if (!mBuyBaoxian.getTag().equals("1")
                        && !mNotBuyBaoxian.getTag().equals("1")
                        && baoxianLL.getVisibility() == View.VISIBLE) {
                    ToastUtils.toast(this, "请选择购买保险方式");
                    return;
                }

                if (mBuyBaoxian.getTag().equals("1")) {
                    if (mBaoxianInfo.insurance_name.equals("")) {
                        ToastUtils.toast(this, "您选择了在线购买保险，需要把保险信息填写完整");
                        return;
                    }
                }

                if (mAgreeCheckImg.getTag().equals("0")) {
                    ToastUtils.toast(this, "您还未同意三方协议");
                    return;
                }
                UserDetailEntity userEntity = SharePerfenceUtil.getUserDetail(mContext);
                if (userEntity.data.type.equals("1") && mBuyBaoxian.getTag().equals("1")) {
                    alertDialog("您选择了购买保险，保费费用将会在结算中体现", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            QuoteConfirm();
                        }
                    });
                } else {
                    QuoteConfirm();
                }
                break;

        }
    }

    private void QuoteConfirm() {
        String url = UrlConstant.BASE_URL
                + "/order/quoteConfirm";
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "response:" + response);
                        cancelProgressDialog();
                        final QuoteConfirmParser mParser = new QuoteConfirmParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                ToastUtils.toast(mContext, "下单成功");
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("order_id", order_id);
                                map.put("order_price", baojia);
                                map.put("agent_id", agent_id);
                                map.put("agent_name", name);
                                map.put("uid", SharePerfenceUtil.getUid(mContext));
                                MobclickAgent.onEvent(mContext, "quoteConfirm", map);
                                Intent intent;
                                if (!TextUtils.isEmpty(mParser.entity.data.needPay)
                                        && mParser.entity.data.needPay.equals("1")) {
                                    intent = new Intent(mContext, PayOnLineActivity.class);
                                    intent.putExtra("order_id", order_id);
                                    intent.putExtra("fromSendProduct", true);
                                    if (mBuyBaoxian.getTag().equals("1")) {
                                        intent.putExtra("insurance_flag", "1");
                                    } else {
                                        intent.putExtra("insurance_flag", "0");
                                    }
                                    startActivity(intent);
                                } else {
                                    intent = new Intent(mContext, OrderDetailActivity.class);
                                    intent.putExtra("order_id", order_id);
                                    startActivity(intent);
                                }
                                finish();
                                if (MyActivityManager.create().findActivity(AgentAndQuoteDetailAct.class) != null) {
                                    MyActivityManager.create().findActivity(AgentAndQuoteDetailAct.class).finish();
                                }
                                if (MyActivityManager.create().findActivity(CommentActivity.class) != null) {
                                    MyActivityManager.create().findActivity(CommentActivity.class).finish();
                                }
                            } else {
                                if (mParser.entity.code == 4014) {
                                    //由于在线购买保险时间最晚为装货前一小时,保险购买失败
                                    alertDialog("在线购买保险时间晚于装货前一小时。请选择自行购买保险。", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }, false);
                                } else {
                                    ToastUtils.toast(mContext, mParser.entity.msg);
                                }
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
                ToastUtils.toast(mContext, "网络异常，请稍后再试");
            }
        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                // TODO Auto-generated method stub
                Map<String, String> parmas = super.getPostBodyData();
                parmas.put("order_id", order_id);
                parmas.put("agent_id", agent_id);
                parmas.put("sender_name", mSendName.getText().toString());
                parmas.put("sender_mobile", mSendPhone.getText().toString());
                parmas.put("sender_address", "");
                parmas.put("receiver_name", mReceName.getText().toString());
                parmas.put("receiver_mobile", mRecePhone.getText().toString());
                parmas.put("receiver_address", "");
                parmas.put("sender_mobile2", mSendPhone2.getText().toString());
                parmas.put("receiver_mobile2", mRecePhone2.getText().toString());

                if (baoxianLL.getVisibility() == View.VISIBLE) {
                    if (mBuyBaoxian.getTag().equals("1")) {
                        parmas.put("insurance_flag", "1");
                    } else {
                        parmas.put("insurance_flag", "0");
                    }
                } else {
                    parmas.put("insurance_flag", "1");
                }

                parmas.put("insurance_name", mBaoxianInfo.insurance_name);
                parmas.put("insurance_num", mBaoxianInfo.insurance_num);
                parmas.put("insurance_type", mBaoxianInfo.insurance_type);
                parmas.put("insurance_package", mBaoxianInfo.insurance_package);
                parmas.put("insurance_values", mBaoxianInfo.insurance_value);
                if (!TextUtils.isEmpty(mFapiaoEdit.getText().toString())) {
                    parmas.put("invoice_name", mFapiaoEdit.getText().toString());
                }
                return parmas;
            }
        };
        showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }
}
