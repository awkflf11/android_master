package com.foryou.truck.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.entity.AreasEntity;
import com.foryou.truck.entity.BankNameListEntity;
import com.foryou.truck.entity.UserDetailEntity;
import com.foryou.truck.model.ProvinceModel;
import com.foryou.truck.parser.AreasJsonParser;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.tools.SdCardUtil;
import com.foryou.truck.tools.StringUtils;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.BankInfo;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.ImageUtils;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.AreasPickDialog;
import com.foryou.truck.view.WithDelImgEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @des：资料认证页面
 */
public class ZiliaoRenzhengAct extends BaseActivity {
    private Context mContext;
    private String TAG = "ZiliaoRenzhengAct";
    public LayoutInflater lf;
    private TextView hintTopTv;
    private String[] keyArray = {"身份证照片", "身份证合影照", "名片", "营业执照、税务登记证、组织资格代码证"};
    private String[] hintArray = {"正面", "手持身份证正面合影", "", "三合一"};
    private ArrayList<View> mValueList;
    public TextView mTitle;

    @Override
    public void setRootView() {
        super.setRootView();
        lf = LayoutInflater.from(this);
        mContext = this;
        View view = lf.inflate(R.layout.act_ziliao_renzheng, null);
        setHintTop(view);
        LinearLayout mMainContent = (LinearLayout) view.findViewById(R.id.main_content);
        addTopInfo(mMainContent);
        addBottomInfo(mMainContent);
        addCallPhoneInfo(mMainContent);
        addConfirmBtnInfo(mMainContent);
        setContentView(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle();
        initBankName();
        // getCityData();
    }

    private void initTitle() {
        setTitle("资料认证");
        ShowBackView();
    }

    List<PictureInfo> pictureInfos;
    public class PictureInfo {
        public TextView keyTv;
        public ImageView vlaueIv;
        public LinearLayout pictureLL2;
        public ImageView vlaueIv2;
        public TextView zhangjianTv2;
    }

    private void addBottomInfo(LinearLayout mMainContent) {
        pictureInfos = new ArrayList<PictureInfo>();
        for (int i = 0; i < keyArray.length; i++) {
            View view = lf.inflate(R.layout.layout_ziliao_cell, null);
            PictureInfo info=new PictureInfo();
            info.keyTv = (TextView) view.findViewById(R.id.text_key_tv);
            info.keyTv.setText(keyArray[i]);
            info.vlaueIv = (ImageView) view.findViewById(R.id.imageView_vlue_iv);
            info.vlaueIv.setBackgroundResource(R.drawable.icon_upavater);
            info.vlaueIv.setOnClickListener(new MyOnclickListener(i+1));
            info.pictureLL2 = (LinearLayout) view.findViewById(R.id.linearlayout_vlue_ll);
            info.vlaueIv2 = (ImageView) view.findViewById(R.id.imageView_vlue_iv2);
            info.zhangjianTv2 = (TextView) view.findViewById(R.id.zhengjian_type_tv);
            if(hintArray[i].equals("")){
                info.zhangjianTv2.setVisibility(View.GONE);
            }else{
                info.zhangjianTv2.setVisibility(View.VISIBLE);
                info.zhangjianTv2.setText(hintArray[i]);
            }
            pictureInfos.add(info);
            mMainContent.addView(view);
        }

    }

    private View.OnClickListener mConfimClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == confirmBtn){
                ToastUtils.toast(mContext,"提交认证");
            }else if(v == bankNameRL){//选择银行名称和 和填写名称
                Intent intent=new Intent(mContext,BankNameListAct.class);
                startActivityForResult(intent,6);
            }else if(v == areaRL){
                //ToastUtils.toast(mContext,"选择地区");
                selectArea();
            }else if(v == callPhoneTv){
                Constant.GotoDialPage(mContext,"400-8008-577");
            }else if(v == bankNameRL){

            }else if(v == avaterValueIv){
                Intent intent=new Intent(mContext,PhotoAct.class);
                intent.putExtra("picType",0);
                startActivityForResult(intent,0);
                //overridePendingTransition(R.anim.fade, R.anim.hold);
                overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
            }
        }
    };

    int picType=0;

    class  MyOnclickListener implements View.OnClickListener{
        int index;
        public MyOnclickListener(int num) {
            index = num;
        }
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(mContext,PhotoAct.class);
            if(index==1){
                intent.putExtra("picType",1);
                startActivityForResult(intent,1);
            }else if(index==2){
                intent.putExtra("picType",2);
                startActivityForResult(intent,2);
            }else if(index==3){
                intent.putExtra("picType",3);
                startActivityForResult(intent,3);

            }else if(index==4){
                intent.putExtra("picType",4);
                startActivityForResult(intent,4);
            }
            //overridePendingTransition(R.anim.fade, R.anim.hold);
           // overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
            overridePendingTransition(R.anim.push_bottom_in, R.anim.push_bottom_out);
        }
    }

    Button confirmBtn;
    private void addConfirmBtnInfo(LinearLayout mMainContent) {
        View view = lf.inflate(R.layout.layout_button, null);
        confirmBtn = (Button) view.findViewById(R.id.button1);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) confirmBtn.getLayoutParams();
        layoutParams.setMargins(ScreenInfo.dip2px(mContext, 10), ScreenInfo.dip2px(mContext, 20),
                ScreenInfo.dip2px(mContext, 10), ScreenInfo.dip2px(mContext, 20));
        confirmBtn.setLayoutParams(layoutParams);
        confirmBtn.setText("提交认证");
        confirmBtn.setOnClickListener(mConfimClick );
        mMainContent.addView(view);
    }
    TextView  callPhoneTv;
    public TextView avaterKeyTv;
    public ImageView avaterValueIv;//默认图
    public ImageView avaterValueIv2;//上传后的图
    public TextView  avaterValueTv;
    public WithDelImgEditText bankHaoTv;
    public RelativeLayout bankNameRL;
    public WithDelImgEditText  bankNameTv;// 可点击，可输入状态
    public TextView  bankPlaceTv;//开户行
    public RelativeLayout areaRL;
    public TextView  selectCityEt;
    private String banknNameStr;


    private void addTopInfo(LinearLayout mMainContent) {
        View view = lf.inflate(R.layout.layout_ziliaorenzheng, null);
        avaterKeyTv = (TextView) view.findViewById(R.id.text_key_tv);
        avaterValueIv = (ImageView) view.findViewById(R.id.imageView_vlue_iv);
        avaterValueIv2 = (ImageView) view.findViewById(R.id.imageView_vlue_iv2);
        avaterValueTv = (TextView) view.findViewById(R.id.zhengjian_type_tv);
        avaterValueTv.setVisibility(View.GONE);
        avaterKeyTv.setText("个人头像");
        avaterValueIv.setOnClickListener(mConfimClick);
        bankHaoTv = (WithDelImgEditText) view.findViewById(R.id.bank_hao_value);
        bankNameRL = (RelativeLayout) view.findViewById(R.id.bank_name_rl);
        bankNameRL.setOnClickListener(mConfimClick);
        bankNameTv = (WithDelImgEditText) view.findViewById(R.id.bank_name_value);
        bankPlaceTv = (TextView) view.findViewById(R.id.bank_place_value);
        areaRL = (RelativeLayout) view.findViewById(R.id.area_name_rl);
        areaRL.setOnClickListener(mConfimClick);
        selectCityEt = (TextView) view.findViewById(R.id.area_name_vlaue);
        mMainContent.addView(view);
    }



    public void setHintTop(View view) {
        hintTopTv = (TextView) view.findViewById(R.id.hint_top_tv);
        hintTopTv.setText("温馨提示：如想编辑手机号码，请重新添加手机");
    }

    private void addCallPhoneInfo(LinearLayout mMainContent) {
        View view = lf.inflate(R.layout.layout_callphone, null);
        callPhoneTv=(TextView) view.findViewById(R.id.call_phone_tv);
        callPhoneTv.setOnClickListener(mConfimClick);
        String hint=callPhoneTv.getText().toString().trim();
        StringUtils.setTextViewColor(mContext,hint,callPhoneTv,hint.indexOf("400"),hint.length(),
                R.color.my_blue_color);
        mMainContent.addView(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap photo=null;
        Drawable drawable=null;
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                      photo = BitmapFactory.decodeFile(SdCardUtil.getTouXiangPath(mContext) + "truck_pic0.png", null);
                      drawable = new BitmapDrawable(photo);
                    avaterValueIv2.setBackgroundDrawable(drawable);
                    avaterValueIv2.setVisibility(View.VISIBLE);
                    avaterValueIv.setVisibility(View.GONE);
                }
                break;
            case 1:
                if(resultCode == RESULT_OK && pictureInfos!=null){
                    PictureInfo info=pictureInfos.get(0);
                    info.vlaueIv.setVisibility(View.GONE);
                    info.vlaueIv2.setVisibility(View.VISIBLE);
                      photo = BitmapFactory.decodeFile(SdCardUtil.getTouXiangPath(mContext) + "truck_pic1.png", null);
                      drawable = new BitmapDrawable(photo);
                    info.vlaueIv2.setBackgroundDrawable(drawable);
                }
                break;
            case 2:
                if(resultCode == RESULT_OK && pictureInfos!=null){
                    PictureInfo info=pictureInfos.get(1);
                    info.vlaueIv.setVisibility(View.GONE);
                    info.vlaueIv2.setVisibility(View.VISIBLE);
                      photo = BitmapFactory.decodeFile(SdCardUtil.getTouXiangPath(mContext) + "truck_pic2.png", null);
                      drawable = new BitmapDrawable(photo);
                    info.vlaueIv2.setBackgroundDrawable(drawable);
                }
                break;
            case 3:
                if(resultCode == RESULT_OK && pictureInfos!=null){
                    PictureInfo info=pictureInfos.get(2);
                    info.vlaueIv.setVisibility(View.GONE);
                    info.vlaueIv2.setVisibility(View.VISIBLE);
                      photo = BitmapFactory.decodeFile(SdCardUtil.getTouXiangPath(mContext) + "truck_pic3.png", null);
                      drawable = new BitmapDrawable(photo);
                    info.vlaueIv2.setBackgroundDrawable(drawable);
                }
                break;
            case 4:
                if(resultCode == RESULT_OK && pictureInfos!=null){
                    PictureInfo info=pictureInfos.get(3);
                    info.vlaueIv.setVisibility(View.GONE);
                    info.vlaueIv2.setVisibility(View.VISIBLE);
                      photo = BitmapFactory.decodeFile(SdCardUtil.getTouXiangPath(mContext) + "truck_pic4.png", null);
                      drawable = new BitmapDrawable(photo);
                    info.vlaueIv2.setBackgroundDrawable(drawable);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClickListener(int id) {

    }

    public void initBankName(){
        bankHaoTv.setOnfocusChangeListener(new WithDelImgEditText.FocustListener() {
            @Override
            public void onFocusChange(boolean flag) {
                if (flag) {
                    banknNameStr = bankNameTv.getText().toString();
                } else {

                    if (TextUtils.isEmpty(bankHaoTv.getText().toString().trim())) {
                        //ToastUtils.toast(mContext,"请输入银行卡卡号");
                        return;
                    }
                    // if (bankHaoStr.length() < 6) {
                    // ToastUtils.toast(mContext, "请输入至少6位的银行卡号");
                    // // return;
                    // }
                    if (bankHaoTv.getText().toString().trim().length() >= 6) {
                        banknNameStr = BankInfo.getNameOfBank(bankHaoTv.getText().toString().trim().substring(0, 6));
                    }
                    if (banknNameStr != null && banknNameStr.contains("·")) {
                        String[] strs = banknNameStr.split("·");
                        banknNameStr = strs[0];
                    }
                    if(banknNameStr.equals("") || banknNameStr.contains("没有找到银行卡信息")){
                        bankNameTv.setEnabled(true);
                        bankNameRL.setClickable(false);
                    }else{
                        bankNameTv.setEnabled(false);
                        bankNameRL.setClickable(true);
                    }

                    bankNameTv.setText(banknNameStr);
                }
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }


    //获取地区信息模块：++++++++

    AreasEntity entity;
    private int mStartProvinceIndex = 0, mStartCityIndex = 0, mStartDistrictIndex = 0;
    private ArrayList<ProvinceModel> provinceList = null;
    private String areaId;
    private String provinceId;
    private String cityId;
    private AreasPickDialog.AreasDataPickLisener mAreaDataListener = new AreasPickDialog.AreasDataPickLisener() {
        @Override
        public void onAreasDataSelect(int proviceindex, int cityid,
                                      int districtid) {
            mStartProvinceIndex = proviceindex;
            mStartCityIndex = cityid;
            mStartDistrictIndex = districtid;
            String province = entity.data.get(proviceindex).name;
            String city = entity.data.get(proviceindex).city.get(mStartCityIndex).name;
            provinceId=entity.data.get(proviceindex).id;
            cityId=entity.data.get(proviceindex).city.get(cityid).id;
            String district;
            if(mStartDistrictIndex == entity.data.get(proviceindex).city  //其他
                    .get(mStartCityIndex).district.size()){
                district = "";
                selectCityEt.setText(province + "-" + city);
            }else {
                district = entity.data.get(proviceindex).city
                        .get(mStartCityIndex).district
                        .get(mStartDistrictIndex).name;
                // selectCityEt.setText(province + "-" + city + "-" + district);
                selectCityEt.setText(province + "-" + city );
            }
            UtilsLog.i(TAG, "city:" + city + ",dristict:" + district);
            // mDetailPlace.setText("");
        }
    };

    public void selectArea(){
        if (entity == null) {
            ToastUtils.toast(mContext, "正在加载配置文件，请稍候再试");
            NetWorkUtils.getAreaData(mContext, TAG, new NetWorkUtils.NetJsonRespon(){
                @Override
                public void onRespon(BaseJsonParser parser) {
                    AreasJsonParser mParser = (AreasJsonParser) parser;
                    entity = mParser.entity;
                    if(entity == null){
                        ToastUtils.toast(mContext, "正在加载配置文件，请稍候再试");
                        return;
                    }else{
                        initAreasDilog();
                    }
                }
            });
        }else{
            initAreasDilog();
        }
    }

    public void initAreasDilog(){
        //initProvinceDatas(entity);
        AreasPickDialog mAreasStartDialog;
        // mAreasStartDialog = new AreasPickDialog(mContext, provinceList);
        mAreasStartDialog = new AreasPickDialog(mContext, entity,false);
        mAreasStartDialog.setTitle("请选择目的地");
        mAreasStartDialog.SetDataSelectOnClickListener(mAreaDataListener);
        mAreasStartDialog.setCurrentArea(mStartProvinceIndex, mStartCityIndex, mStartDistrictIndex);
        mAreasStartDialog.show();
    }

    class  mThread  extends Thread{
        @Override
        public void run() {
            super.run();
            entity = SharePerfenceUtil.getAreaData(mContext);
        }
    }

    private void getCityData() {
        new mThread().start();
    }

//    protected void addDriverInfoView(LinearLayout mMainContent) {
//        mValueList = new ArrayList<View>();
//        for (int i = 0; i < keyArray.length; i++) {
//            View cell = lf.inflate(R.layout.add_driver_cell_one, null);
//            TextView textView = (TextView) cell.findViewById(R.id.key);
//            chaPaiTv = (TextViewWithIconFont) cell.findViewById(R.id.che_paihao_if);
//            textView.setText(keyArray[i]);
//            WithDelImgEditText mValue = (WithDelImgEditText) cell.findViewById(R.id.value);
//
//            if (i == 0) {
//                mValue.addTextChangedListener(mWatcher);
////				mValue.setOnFocusChangeListener(focusChangeListener);
//            }
//            if (i == 3) {
//                mValue.setPadding(ScreenInfo.dip2px(this, 50), 0, ScreenInfo.dip2px(this, 10), 0);
//                chaPaiTv.setOnClickListener(new MyOnclickListener(i));
//                mValueList.add(chaPaiTv);
//            }
//            mMainContent.addView(cell);
//        }
//    }




}
