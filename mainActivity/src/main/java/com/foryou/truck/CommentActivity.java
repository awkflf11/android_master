package com.foryou.truck;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.foryou.truck.parser.AgentCommentListJsonParser;
import com.foryou.truck.tools.ImageTools;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.TextViewWithIconFont;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @des:经纪人详情的页面:经纪人评论页面, 现在改成， 经纪人报价详情。
 */
public class CommentActivity extends BaseActivity {
    private String TAG = "CommentActivity";
    private Context mContext;
    @BindView(id = R.id.confirm_btn, click = true)
    private TextView mConfirmBtn;
    @BindView(id = R.id.confirm_ll)
    private RelativeLayout mConfirmRL;
    @BindView(id = R.id.yunfei)
    private TextView yunFeiTv;//
    @BindView(id = R.id.total_price)
    private TextView totalPriceTv;//总额
    @BindView(id = R.id.baoxian_text)
    private TextView baoXianKeyTv;
    @BindView(id = R.id.baoxian)
    private TextView baoXianTv;
    @BindView(id = R.id.give_baoxian_text)
    private TextView giveBaoxianKeyTv;
    @BindView(id = R.id.give_baoxian)
    private TextView giveBaoxianTv;
    @BindView(id = R.id.suifei_text)
    private TextView shuiFeiKeyTv;
    @BindView(id = R.id.suifei)
    private TextView shuiFeiTv;
    //
    private String agent_id, name;
    boolean mConfirmFlag;
    private ImageView mSeekImg;
    private TextView mXinyiText;
    String xinyi, mobile, photo, chengdan_m, chengdan_t, order_id, baojia, coupon_amount, load_region, load_address, unload_region, unload_address, pay_type;
    String zong_e,invoice_money,insurance_money,gift_insurance,order_money;
    private TextView mName;
    private ImageView mAvatar;
    TextViewWithIconFont mPhone;
    TextView mYueChengdan;
    TextView mZongchengdan;

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.comment_layout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShowBackView();
        setTitle("经纪人报价详情");
        mContext = this;
        initGetString();
        initHeaderView();
        initData();
    }

    private void initData(){
        yunFeiTv.setText("￥"+order_money);
        totalPriceTv.setText("￥"+zong_e);

        if(!TextUtils.isEmpty(insurance_money) && !insurance_money.equals("0")){
            baoXianKeyTv.setVisibility(View.VISIBLE);
            baoXianTv.setVisibility(View.VISIBLE);
            baoXianTv.setText("￥"+insurance_money);
        }else{
            baoXianKeyTv.setVisibility(View.GONE);
            baoXianTv.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(gift_insurance) && !gift_insurance.equals("0")){
            giveBaoxianKeyTv.setVisibility(View.VISIBLE);
            giveBaoxianTv.setVisibility(View.VISIBLE);
            giveBaoxianTv.setText("￥"+gift_insurance);
        }else{
            giveBaoxianKeyTv.setVisibility(View.GONE);
            giveBaoxianTv.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(invoice_money) && !invoice_money.equals("0")){
            shuiFeiKeyTv.setVisibility(View.VISIBLE);
            shuiFeiTv.setVisibility(View.VISIBLE);
            shuiFeiTv.setText("￥"+invoice_money);
        }else{
            shuiFeiKeyTv.setVisibility(View.GONE);
            shuiFeiTv.setVisibility(View.GONE);
        }

    }

    private void initHeaderView() {
        mName = (TextView)findViewById(R.id.name);
        mName.setText(name);
        mAvatar = (ImageView)findViewById(R.id.avatar);
        mPhone = (TextViewWithIconFont)findViewById(R.id.phone);
        mPhone.setOnClickListener(this);
        mYueChengdan = (TextView)findViewById(R.id.yuechengdan);
        mZongchengdan = (TextView)findViewById(R.id.zongchengdan);
        mXinyiText = (TextView)findViewById(R.id.progress);
        mSeekImg = (ImageView)findViewById(R.id.seek_img);

        if(xinyi.contains("%")){
            xinyi = xinyi.replace("%","");
        }
        if (TextUtils.isDigitsOnly(xinyi)) {
            mSeekImg.setImageBitmap(ImageTools.getSeekImage(ScreenInfo.dip2px(this, 50),
                    ScreenInfo.dip2px(this, 6), Integer.valueOf(xinyi)));
        }
        mXinyiText.setText(xinyi + "%");

        imageLoader.loadImage(photo, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                mAvatar.setImageBitmap(ImageTools.toRoundBitmap(bitmap));
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
            }
        });

        SpannableString sp_m = new SpannableString("月成单  " + chengdan_m);
        sp_m.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.my_blue_color)), 5, sp_m.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mYueChengdan.setText(sp_m);

        SpannableString sp_t = new SpannableString("总成单  " + chengdan_t);
        sp_t.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.my_blue_color)), 5, sp_t.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mZongchengdan.setText(sp_t);

        if (mConfirmFlag) {
            mConfirmRL.setVisibility(View.VISIBLE);
        }
    }


    private void initGetString(){
        agent_id = getIntent().getStringExtra("agent_id");
        name = getIntent().getStringExtra("name");
        xinyi = getIntent().getStringExtra("xinyi");
        mobile = getIntent().getStringExtra("mobile");
        photo = getIntent().getStringExtra("photo");
        chengdan_m = getIntent().getStringExtra("chengdan_m");
        chengdan_t = getIntent().getStringExtra("chengdan_t");
        order_id = getIntent().getStringExtra("order_id");
        baojia = getIntent().getStringExtra("baojia");
        load_region = getIntent().getStringExtra("load_region");
        load_address = getIntent().getStringExtra("load_address");
        unload_region = getIntent().getStringExtra("unload_region");
        unload_address = getIntent().getStringExtra("unload_address");
        pay_type = getIntent().getStringExtra("pay_type");
        coupon_amount = getIntent().getStringExtra("coupon_amount");
        mConfirmFlag = getIntent().getBooleanExtra("confirm_btn", false);
        //
        zong_e = getIntent().getStringExtra("zong_e");
        invoice_money = getIntent().getStringExtra("invoice_money");
        insurance_money = getIntent().getStringExtra("insurance_money");
        gift_insurance = getIntent().getStringExtra("gift_insurance");
        order_money = getIntent().getStringExtra("order_money");

    }


    @Override
    public void onClickListener(int id) {
        switch (id) {
            case R.id.phone:
                Constant.GotoDialPage(this, mobile);
                break;
            case R.id.confirm_btn:
                Intent intent = new Intent(this, BeginSendProductAct.class);
                intent.putExtra("order_id", order_id);
                //可以代金券的数量
                intent.putExtra("coupon_amount", coupon_amount);
                intent.putExtra("baojia", baojia);
                intent.putExtra("agent_id", agent_id);
                intent.putExtra("name", name);
                intent.putExtra("pay_type", pay_type);
                intent.putExtra("load_region", load_region);
                intent.putExtra("load_address", load_address);
                intent.putExtra("unload_region", unload_region);
                intent.putExtra("unload_address", unload_address);
                intent.putExtra("zong_e",zong_e);
                intent.putExtra("invoice_money",invoice_money);
                intent.putExtra("insurance_money",insurance_money);
                intent.putExtra("gift_insurance",gift_insurance);
                intent.putExtra("order_money",order_money);
                startActivity(intent);
                break;
        }
    }

    public void getCommentList() {
        String url = UrlConstant.BASE_URL + "/comment/agent";
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        cancelProgressDialog();
                        AgentCommentListJsonParser mParser = new AgentCommentListJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
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
            }
        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("agent_id", agent_id);
                return parmas;
            }
        };
        showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }


    @Override
    protected void onStop() {
        super.onStop();
        cancelProgressDialog();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }


}
