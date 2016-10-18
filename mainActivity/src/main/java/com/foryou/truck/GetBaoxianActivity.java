package com.foryou.truck;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.CommonConfigEntity;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @des: 填写保险信息
 */
public class GetBaoxianActivity extends BaseActivity {
    private String TAG = "GetBaoxianActivity";
    private Context mContext;
    //public static List<BaseKeyValue> insurance_type;
    @BindView(id = R.id.product_value)
    private EditText mProductValue;// 货物总价值
    @BindView(id = R.id.baoxian_value)
    private TextView mBaoxianValue;
    @BindView(id = R.id.baoxianxieyi, click = true)
    private TextView mBaoxianXieyi;
    private String give_insurance;
    @BindView(id = R.id.save_btn, click = true)
    private Button mSaveBtn;
    @BindView(id = R.id.baoxian_name)
    private EditText mBaoxianPerson;
    @BindView(id = R.id.product_number)
    private EditText mBaoxianCount;
    @BindView(id = R.id.baozhuang)
    private EditText mBaoxianPackage;
    @BindView(id = R.id.not_use_baoxian, click = true)
    private TextView mNotUseBaoxian;
    @BindView(id = R.id.free_baoxian_text)
    private TextView mFreeHintText;
    @BindView(id = R.id.give_baoxian_tv)
    private TextView giveBaoxianTv;//新增的,赠送保险费用
    //
    private AlertDialog marketDialog;
    private List<Map<String, Object>> adapterList = new ArrayList<Map<String, Object>>();
    @BindView(id = R.id.goodstype__tv)
    private TextView goodsTypeTv;
    //
    @BindView(id = R.id.baoxian_rl)
    private RelativeLayout baoXianRL;// 保险费用默认隐藏，
    @BindView(id = R.id.select_goods_rl, click = true)
    private RelativeLayout seletGoodsTypeRL;
    private CommonConfigEntity mConfigEntity;
    private String pay_type;
    private String insurance_type_key = "", insurance_type_value = "";
    private String insuranceGoods = ""; //保险费用计算费率
    private String insurance_money; //保险金额
    private String insurance_cal_money = "";//计算得出的保险金额;
    private String gift_insurance = "";

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.get_baoxian);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        ShowBackView();
        setTitle("填写保险信息");
        Intent intent = this.getIntent();

        pay_type = intent.getStringExtra("pay_type");
        mProductValue.setOnEditorActionListener(mEditActionListener);
        mBaoxianPerson.setText(intent.getStringExtra("insurance_name"));
        mBaoxianCount.setText(intent.getStringExtra("insurance_num"));
        mBaoxianPackage.setText(intent.getStringExtra("insurance_package"));
        mProductValue.setText(intent.getStringExtra("insurance_value"));
        mProductValue.setSelection(mProductValue.getText().length());
        goodsTypeTv.setText(intent.getStringExtra("insurance_type_value"));
        insurance_type_value = intent.getStringExtra("insurance_type_value");
        insurance_type_key = intent.getStringExtra("insurance_type");
        gift_insurance = intent.getStringExtra("gift_insurance");
        if(!TextUtils.isEmpty(gift_insurance)){
            mFreeHintText.setVisibility(View.VISIBLE);
            mFreeHintText.setText("赠送保险费用"+gift_insurance+"元");
        }else{
            mFreeHintText.setVisibility(View.GONE);
        }


        insuranceGoods = intent.getStringExtra("insuranceGoods");
        insurance_money = intent.getStringExtra("insurance_money");
        insurance_cal_money = intent.getStringExtra("insurance_cal_money");
        if (!TextUtils.isEmpty(insurance_money)) {
            baoXianRL.setVisibility(View.VISIBLE);
            mBaoxianValue.setText(insurance_money);
        } else if (!TextUtils.isEmpty(insuranceGoods)) {
            if(!TextUtils.isEmpty(insurance_cal_money)){
                mBaoxianValue.setText(insurance_cal_money);
            }
            mProductValue.addTextChangedListener(textWatcherListener);
        }

        mConfigEntity = SharePerfenceUtil.getConfigData(this);


    }

    private TextWatcher textWatcherListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setBaoxianValue();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void setBaoxianValue() {
        if (TextUtils.isEmpty(mProductValue.getText().toString().trim()))
            return;
        baoXianRL.setVisibility(View.VISIBLE);
        double productvalue = Double
                .valueOf(mProductValue.getText().toString());

        double baoxianValue;
        baoxianValue = (productvalue) * Double.valueOf(insuranceGoods) * 0.01;
        mBaoxianValue.setText("" + String.format("%.2f", baoxianValue));
        // mBaoxianValue.setText("" + baoxianValue);
    }

    private OnEditorActionListener mEditActionListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setBaoxianValue();
            }
            return false;
        }

    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void selectGoodsType() {
        if (marketDialog == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                marketDialog = new AlertDialog.Builder(this,
                        AlertDialog.THEME_HOLO_LIGHT).create();
            } else {
                marketDialog = new AlertDialog.Builder(this).create();
            }
            adapterList.clear();
            if (mConfigEntity == null) {
                ToastUtils.toast(mContext, "正在加载数据，请稍候");
                return;
            }
            for (int i = 0; i < mConfigEntity.data.insurance_type.size(); i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("value", mConfigEntity.data.insurance_type.get(i).value);
                map.put("key", mConfigEntity.data.insurance_type.get(i).key);
                adapterList.add(map);
            }

            ListView list = new ListView(this);
            list.setDividerHeight(1);
            MyListViewAdapter mAdapter = new MyListViewAdapter(this,
                    adapterList, R.layout.dialog_listitem,
                    new String[]{"value"}, new int[]{R.id.text}, false);
            list.setOnItemClickListener(mItemListener);
            list.setAdapter(mAdapter);
            marketDialog.setView(list);
        }

        marketDialog.show();
        int width = ScreenInfo.getScreenInfo(this).widthPixels;
        int height = ScreenInfo.getScreenInfo(this).heightPixels;
        marketDialog.getWindow().setLayout(width - 40, height * 2 / 3);
        marketDialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
    }

    private OnItemClickListener mItemListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            insurance_type_key = (String) adapterList.get(position).get("key");
            insurance_type_value = (String) adapterList.get(position).get(
                    "value");
            goodsTypeTv.setText(insurance_type_value);
            marketDialog.dismiss();
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClickListener(int id) {
        Intent intent;
        switch (id) {
            case R.id.select_goods_rl:
                selectGoodsType();
                break;
            case R.id.save_btn:
                if (mProductValue.getText().toString().equals("")) {
                    ToastUtils.toast(this, "请填写货物总价值");
                    TongjiModel.addEvent(mContext, "保险信息",
                            TongjiModel.TYPE_BUTTON_CLIKC, "请填写货物总价值");
                    return;
                }
                if (goodsTypeTv.getText().toString().equals("")) {
                    ToastUtils.toast(this, "请选择货物类型");
                    TongjiModel.addEvent(mContext, "保险信息",
                            TongjiModel.TYPE_BUTTON_CLIKC, "请选择货物类型");
                    return;
                }
                if (mBaoxianPerson.getText().toString().equals("")) {
                    ToastUtils.toast(this, "请填被保险人");
                    TongjiModel.addEvent(mContext, "保险信息",
                            TongjiModel.TYPE_BUTTON_CLIKC, "请填被保险人");
                    return;
                }
                if (mBaoxianCount.getText().toString().equals("")) {
                    ToastUtils.toast(this, "请填写货物件数");
                    TongjiModel.addEvent(mContext, "保险信息",
                            TongjiModel.TYPE_BUTTON_CLIKC, "请填写货物件数");
                    return;
                }
                if (mBaoxianPackage.getText().toString().equals("")) {
                    ToastUtils.toast(this, "请填写货物包装");
                    TongjiModel.addEvent(mContext, "保险信息",
                            TongjiModel.TYPE_BUTTON_CLIKC, "请填写货物包装");
                    return;
                }

                intent = new Intent();

                intent.putExtra("insurance_type", insurance_type_key);
                intent.putExtra("insurance_type_value", insurance_type_value);

                intent.putExtra("insurance_name", mBaoxianPerson.getText()
                        .toString());
                intent.putExtra("insurance_num", mBaoxianCount.getText().toString());
                intent.putExtra("insurance_package", mBaoxianPackage.getText()
                        .toString());
                intent.putExtra("insurance_value", mProductValue.getText()
                        .toString());
                intent.putExtra("insurance_cal_money",mBaoxianValue.getText().toString());
                this.setResult(Constant.GET_BAOXIAN_CODE, intent);
                finish();
                break;
            case R.id.not_use_baoxian:
                this.setResult(Constant.NO_BAOXIAN_CODE);
                finish();
                break;
            case R.id.baoxianxieyi:
                intent = new Intent(this, DubangXieyiActivity.class);
                startActivity(intent);
                break;
        }
    }

}
