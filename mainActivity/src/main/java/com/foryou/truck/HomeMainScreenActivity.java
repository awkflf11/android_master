package com.foryou.truck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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
import com.foryou.truck.activity.MyOrderListAct;
import com.foryou.truck.activity.PersonCenterAct;
import com.foryou.truck.activity.newGuideAct;
import com.foryou.truck.adapter.ImgAdapter;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.CommonIndexEntity.Banner;
import com.foryou.truck.entity.UserDetailEntity;
import com.foryou.truck.parser.CommonIndexParser;
import com.foryou.truck.parser.IndexAdsJasonParser;
import com.foryou.truck.sendproduct.DisplayAdsActivity;
import com.foryou.truck.sendproduct.SendProductAct;
import com.foryou.truck.tools.BaiduUpdate;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.MyActivityManager;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.MyGallery;
import com.foryou.truck.view.WaveView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeMainScreenActivity extends BaseActivity {
    private Context mContext;
    private String TAG = "HomeMainScreenActivity";
    @BindView(id = R.id.default_img)
    private ImageView defaultBannerIV;// 默认的轮播图
    @BindView(id = R.id.banner_layout)
    private RelativeLayout mBannerLayout;
    @BindView(id = R.id.tishi_text)
    private TextView mTishiText;
    @BindView(id = R.id.gridview)
    private GridView mGridView;
    private int mCurrentIndex = 0;
    private int numOfAgent;
    private final int COUNTOFAGENT = 50;
    private final int COUNTOFSECONDS = 20;
    @BindView(id=R.id.my_order,click=true)
    private LinearLayout mMyOrder;
    @BindView(id=R.id.my_info,click=true)
    private LinearLayout mMyInfo;
    @BindView(id=R.id.call_center,click=true)
    private LinearLayout mCallCenter;
    @BindView(id=R.id.title_layout)
    private RelativeLayout mTitleLayout;
    @BindView(id=R.id.title)
    private TextView titleTv;
    @BindView(id=R.id.waveview,click=true)
    private WaveView mWaveView;
    @BindView(id=R.id.xinshouzhinan,click=true)
    private TextView mXinshouzhinan;
    BaiduUpdate baiduUpdate;

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.home_main_screen);
    }
    private void setNumberOfAgentHint(int number){
        String content = "当前城市有" + number + "位经纪人等待为您服务";
        SpannableString msp = new SpannableString(content);
        msp.setSpan(new RelativeSizeSpan(1.4f), 5,
                content.indexOf("位经纪人"),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.my_blue_color)), 5,
                content.indexOf("位经纪人"),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTishiText.setText(msp);
        number++;
        mHandler.sendEmptyMessageDelayed(number, COUNTOFSECONDS);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what <= numOfAgent) {
                setNumberOfAgentHint(msg.what);
            }
        }
    };

    private OnPageChangeListener pageListener = new OnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageSelected(int arg0) {
            UtilsLog.i(TAG, "arg0:" + arg0);
            mCurrentIndex = arg0;
            // setIndicateImage(arg0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        int screenHeight = ScreenInfo.getScreenInfo(this).heightPixels;
        int viewPageHeight = screenHeight * 338 / 1136;
        UtilsLog.i(TAG, "viewPageHeight:" + viewPageHeight);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, viewPageHeight);
        lp.addRule(RelativeLayout.BELOW, R.id.title_layout);
        mBannerLayout.setLayoutParams(lp);

        initBannerView();
        initBaiDu();
        ll_focus_indicator_container = (LinearLayout)findViewById(R.id.ll_focus_indicator_container);
        //
        getHomeConfigData();
        NetWorkUtils.getConfigData(mContext, TAG, null);
        setNumberOfAgentHint(0);
        getAdsData();

    }

    private void initBaiDu(){
        baiduUpdate=new BaiduUpdate(mContext,false);
        baiduUpdate.initNotification();
        baiduUpdate.baiduUpdate(4);
    }

    private void getAdsData(){
        Map<String, String> map = new HashMap<String,String>();
        NetWorkUtils.BaseGetNetWorkRequest(this,new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Log.i(TAG,"response:"+s);
                IndexAdsJasonParser mParser = new IndexAdsJasonParser();
                int result = mParser.parser(s);
                if(result == 1){
                    if(mParser.entity.status.equals("Y")) {
                        if (mParser.entity.data != null && mParser.entity.data.isshow.equals("1")) {
                            Intent intent = new Intent(mContext, DisplayAdsActivity.class);
                            intent.putExtra("adlink", mParser.entity.data.adlink);
                            intent.putExtra("adphoto", mParser.entity.data.adphoto);
                            startActivity(intent);
                            overridePendingTransition(R.anim.dialog_enter, R.anim.dialog_exit);
                        }
                    }else{
                        ToastUtils.toast(mContext,mParser.entity.msg);
                    }
                }
            }
        },TAG,"/common/indexad",map);
    }

    @Override
    protected void onResume() {
        super.onResume();

        gallery.onStart();
        if (SharePerfenceUtil.IsLogin(mContext)) {
            NetWorkUtils.getUserDetail(mContext, TAG, null);
            NetWorkUtils.BindGtAction(getApplicationContext());
        }
        mWaveView.start();
    }

    private CommonIndexParser mCommonIndexParser;
    private List<String> networkImages;

    private void initBannerData() {

        imgList.clear();
//        imgList.add("http://img2.ph.126.net/23B1VC0VZHYWiFByk9iknA==/6599289680633310485.jpg");
//        imgList.add("http://scs.ganjistatic1.com/gjfs03/M01/70/BD/wKhzGVEk,hXeun0yAAA0Ubo8aWA529_600-0_6-0.jpg");
//        imgList.add("http://fdfs.xmcdn.com/group7/M0B/4B/CA/wKgDWlWvCDzj1nXuAADoSBScAu0998_web_large.jpg");
         for (int i = 0; i < banner.size(); i++) {
            imgList.add(banner.get(i).imgUrl);
        }
        mImageAdapter.notifyDataSetChanged();
        InitFocusIndicatorContainer();
    }


    private List<Banner> banner;

    private void getHomeConfigData() {
        Map<String, String> parmams = new HashMap<String, String>();
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                + "/common/index", parmams);
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        cancelProgressDialog();
                        CommonIndexParser mParser = new CommonIndexParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                mCommonIndexParser = mParser;
                                if (TextUtils.isDigitsOnly(mCommonIndexParser.entity.data.agentCnt)) {
                                    numOfAgent = Integer.valueOf(mCommonIndexParser.entity.data.agentCnt);
                                    if (numOfAgent - COUNTOFAGENT >= 0) {
                                        mHandler.sendEmptyMessageDelayed(
                                                numOfAgent - COUNTOFAGENT,
                                                COUNTOFSECONDS);
                                    } else {
                                        mHandler.sendEmptyMessageDelayed(0, COUNTOFSECONDS);
                                    }
                                }
                                if (mParser.entity.data.banner.size() > 0) {
                                    defaultBannerIV.setVisibility(View.GONE);
                                    banner = mParser.entity.data.banner;
                                    initBannerData();
                                }
                            } else {
                                ToastUtils.toast(mContext, mCommonIndexParser.entity.msg);
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
                //initBannerData();//测试的
                cancelProgressDialog();
            }
        }, true) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = super.getParams();
                return map;
            }
        };
        showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gallery.onStop();

    }

    @Override
    public void onBackPressed() {
        alertDialog("确定退出应用", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyActivityManager.create().AppExit(mContext);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        baiduUpdate.cancelDailog();

        cancelProgressDialog();
        MyApplication.getInstance().cancelAllRequests(TAG);
        mWaveView.stop();
    }

    @Override
    public void onClickListener(int id) {
        Intent intent;
        switch (id){
            case R.id.my_order:
                if (!SharePerfenceUtil.IsLogin(mContext)) {
                    intent = new Intent(mContext, EntryLoginActivity.class);
                    startActivity(intent);
                    return;
                }
                UserDetailEntity entity = SharePerfenceUtil.getUserDetail(mContext);
                if (entity == null) {
                    ToastUtils.toast(mContext, "正在获取配置文件，请稍候再试");
                    if (SharePerfenceUtil.IsLogin(mContext)) {
                        NetWorkUtils.getUserDetail(mContext, TAG, null);
                    }
                    return;
                }
                intent = new Intent(mContext, MyOrderListAct.class);//
                startActivity(intent);
                break;
            case R.id.my_info:
                if (!SharePerfenceUtil.IsLogin(mContext)) {
                    intent = new Intent(mContext, EntryLoginActivity.class);
                    startActivity(intent);
                    return;
                }
                // myInfoActivity PersonCenterAct
                intent = new Intent(mContext, PersonCenterAct.class);
                startActivity(intent);
                break;
            case R.id.call_center:
                TongjiModel.addEvent(mContext, "首页",
                        TongjiModel.TYPE_BUTTON_CLIKC, "电话下单");
                Constant.GotoDialPage(mContext, Constant.PHONE_NUMBER);
                break;
            case R.id.waveview:
                if (!SharePerfenceUtil.IsLogin(mContext)) {
                    intent = new Intent(mContext, EntryLoginActivity.class);
                    startActivity(intent);
                    return;
                }
                intent = new Intent(mContext, SendProductAct.class);
                startActivity(intent);
                break;
            case R.id.xinshouzhinan:
                intent = new Intent(mContext, newGuideAct.class);
                startActivity(intent);
                break;
        }
    }

   // ########## banneer的设置
    private ImgAdapter mImageAdapter;

    private void initBannerView() {
        imgList = new ArrayList<String>();
        mImageAdapter = new ImgAdapter(mContext, imgList);
        gallery = (MyGallery)findViewById(R.id.gallery);
        gallery.setAdapter(mImageAdapter);
        gallery.setFocusable(true);
        gallery.setOnItemClickListener(new MyClickListener());
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int selIndex, long arg3) {
                if (imgList != null && imgList.size() > 0) {
                    selIndex = selIndex % imgList.size();
                    if(portImg.size()> preSelImgIndex){
                        // 修改上一次选中项的背景
                        portImg.get(preSelImgIndex).setImageResource(R.drawable.circle1);
                        // 修改当前选中项的背景
                        portImg.get(selIndex).setImageResource(R.drawable.circle2);
                        preSelImgIndex = selIndex;
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private MyGallery gallery = null;
    private ArrayList<String> imgList;
    private ArrayList<ImageView> portImg;
    private int preSelImgIndex = 0;  //存储上一个选择项的Index
    private LinearLayout ll_focus_indicator_container = null;

    private class MyClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
            if (banner == null || banner.size() == 0 || imgList == null || imgList.size() == 0) {
                return;
            }
            if (index % imgList.size() >= imgList.size()) {
                return;
            }
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("url", banner.get(index % imgList.size()).href);
//           intent.putExtra("url", imgList.get(index % imgList.size()));
            startActivity(intent);

        }
    }

    private void InitFocusIndicatorContainer() {
        portImg = new ArrayList<ImageView>();

        this.ll_focus_indicator_container.removeAllViews();
        if (imgList == null || imgList.size() == 0) {
            return;
        }
        for (int i = 0; i < imgList.size(); i++) {
            ImageView localImageView = new ImageView(mContext);
            localImageView.setId(i);
            localImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(25, 25);
            localImageView.setLayoutParams(localLayoutParams);
            //localImageView.setPadding(5, 5, 5, 5);
            localImageView.setImageResource(R.drawable.circle1);
            portImg.add(localImageView);
            this.ll_focus_indicator_container.addView(localImageView);
        }
    }



}
