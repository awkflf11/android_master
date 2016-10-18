package com.foryou.truck;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.Button;
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
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.parser.RegisterJsonParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.tools.StringUtils;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.WithDelImgEditText;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @des:注册页面(也是申请加入的页面)
 */
public class RegisterActivity extends BaseActivity {
    Context mContext;
    private String TAG = "RegisterActivity";
  	@BindView(id = R.id.title)
	private TextView mTitle;
    @BindView(id = R.id.account_edit)
    private WithDelImgEditText mAccountEditText;
    @BindView(id = R.id.username_edit)
    private WithDelImgEditText usernameEt;//联系人和选择的城市
//    @BindView(id = R.id.cityname_edit,click = true)
//    private WithDelImgEditText selectCityEt;
//    @BindView(id = R.id.select_city_rl,click = true)
//    private RelativeLayout selectCityRl;
    @BindView(id = R.id.yanzhengma_edit)
    private WithDelImgEditText mYanzhengmaEditText;
    @BindView(id = R.id.mPassword_edit)
    private WithDelImgEditText mPasswordEditText;
    @BindView(id = R.id.password_iv, click = true)
    private ImageView passwordIv;//密码状态
    @BindView(id = R.id.invite_code_et)
    private WithDelImgEditText inviteCodeET;// 邀请码选填。
    @BindView(id = R.id.get_yanzhengma, click = true)
    private Button mGetYanzhengmaBtn;
    @BindView(id = R.id.button1, click = true)
    private Button mRegisterBtn;//注册按钮
    @BindView(id = R.id.voice_verify, click = true)
    private TextView mVoiceVerify;
    @BindView(id = R.id.acccount_type_layout, click = true)
    private RelativeLayout mAccountTypeLayout;
    @BindView(id = R.id.account_type_text)
    private TextView mAccoutTypeText;
    // private String[] mAccountTypeItem;
    private String mChooseKey = "";// 选择的账户类型
    int COUNT_SECONDS = 60;
    boolean voiceYanzheng = false;
    long mGetVoiceVerifyTime = (long) 0;
    SimpleJasonParser mYanzhengmaParser;
    RegisterJsonParser mRegisterParser;
   //
    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.foryou_register);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initView();
    }

    private void initView(){
        mTitle.setText("申请加入");
        ShowBackView();
        mRegisterBtn.setText("申请加入");
//        SpannableString msp = new SpannableString(content);
//        msp.setSpan(new UnderlineSpan(), 0, content.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        mVoiceVerify.setText(msp);
        String content = "短信验证码收不到? 试试语音验证码";
        StringUtils.setTextViewColor(mContext,content,mVoiceVerify,content.indexOf("语音")
                     ,content.length(), R.color.my_pink_color);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mGetYanzhengmaBtn.setText(COUNT_SECONDS + "s");
                    COUNT_SECONDS--;
                    if (COUNT_SECONDS < 0) {
                        mGetYanzhengmaBtn.setText("获取验证码");
                        mGetYanzhengmaBtn.setTextColor(Color.parseColor("#ff6900"));
                        COUNT_SECONDS = 60;
                        mGetYanzhengmaBtn.setEnabled(true);
                        mGetYanzhengmaBtn.setBackgroundResource(R.drawable.border_btn_getcode);
                        //mGetYanzhengmaBtn.setBackgroundResource(R.drawable.login_btn);
                        //mGetYanzhengmaBtn.setBackgroundColor(Color.parseColor("#21cc71"));
                    } else {
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                    break;
                case 1:
                    mVoiceVerify.setTextColor(Color.parseColor("#666666"));
                    break;
            }
        }
    };

    private void getYanzhengma() {
        Map<String, String> parmas = new HashMap<String, String>();
        parmas.put("tel", mAccountEditText.getText().toString());
        parmas.put("type", "1");
        if (voiceYanzheng) {
            parmas.put("extend", "voice");
        } else {
            parmas.put("extend", "text");
        }
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                 + "/common/verify_code", parmas);
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        cancelProgressDialog();
                        mYanzhengmaParser = new SimpleJasonParser();
                        // Log.i(TAG, "result:" + Tools.UnicodeDecode((String)msg.obj));
                        int result = mYanzhengmaParser.parser(response);
                        if (result == 1) {
                            if (mYanzhengmaParser.entity.status.equals("Y")) {
                                ToastUtils.toast(mContext, "验证码获取成功");
                                if (!voiceYanzheng) {
                                    mGetYanzhengmaBtn.setEnabled(false);
                                    mGetYanzhengmaBtn.setBackgroundResource(R.drawable.border_btn_getcode_hui);
                                    mGetYanzhengmaBtn.setTextColor(Color.parseColor("#999999"));
                                    mGetYanzhengmaBtn.setText(COUNT_SECONDS + "s");
                                    COUNT_SECONDS--;
                                    mHandler.sendEmptyMessageDelayed(0, 1000);
                                }
                            } else {
                                //此处要增加 新的强提示判断：
                                ToastUtils.toast(mContext, mYanzhengmaParser.entity.msg);
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

    private void Register() {
        showProgressDialog();
        String url = UrlConstant.BASE_URL + "/user/register";
        UtilsLog.i(TAG, "url:" + url);
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, response);
                        cancelProgressDialog();
                        mRegisterParser = new RegisterJsonParser();
                        int result = mRegisterParser.parser(response);
                        if (result == 1) {
                            if (mRegisterParser.entity.status.equals("Y")) {
                                ToastUtils.toast(mContext, "您的申请提交成功，客服会主动联系您");
//                                SharePerfenceUtil.setKey(mContext, mRegisterParser.entity.data.key);
//                                SharePerfenceUtil.setUid(mContext, mRegisterParser.entity.data.uid);
                                MobclickAgent.onEvent(mContext, "register");
                                Intent intent = new Intent(mContext, HomeMainScreenActivity.class);
                                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                if(!TextUtils.isEmpty(mRegisterParser.entity.flag)&& mRegisterParser.entity.flag.equals("0")){
                                    ToastUtils.toast(mContext, mRegisterParser.entity.msg);
                                }else if( !TextUtils.isEmpty(mRegisterParser.entity.flag)&& mRegisterParser.entity.flag.equals("1")){
                                    alertDialog("" + mRegisterParser.entity.msg + "",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constant.PHONE_NUMBER));
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    RegisterActivity.this.startActivity(intent);}
                                            });
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
            }
        }, true) {
            @Override
            public Map<String, String> getPostBodyData() {
                Map<String, String> parmas = new HashMap<String, String>();
                //parmas.put("key", SharePerfenceUtil.getKey(mContext));
                //parmas.put("uid", SharePerfenceUtil.getUid(mContext));
                parmas.put("mobile", mAccountEditText.getText().toString());
                parmas.put("verify_code", mYanzhengmaEditText.getText().toString());
                //新增
               parmas.put("password", mPasswordEditText.getText().toString());
                parmas.put("username", usernameEt.getText().toString());
                //parmas.put("province", selectCityEt.getText().toString());
                // parmas.put("account_type", mChooseKey);账户类型
                // 邀请码（选填的）, 新版去掉
               // parmas.put("invitation_code", inviteCodeET.getText().toString().trim());
                return parmas;
            }
        };
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    private boolean isPasswordState=true;

    @Override
    public void onClickListener(int id) {
        String number = mAccountEditText.getText().toString();
        String usernameStr=usernameEt.getText().toString().trim();
        switch (id) {
            case R.id.password_iv:
                isPasswordState=!isPasswordState;
                StringUtils.setPasswordState(isPasswordState,mPasswordEditText,passwordIv);
                break;

            case R.id.get_yanzhengma:
                if(TextUtils.isEmpty(usernameStr)){
                    ToastUtils.toast(mContext,"联系人不能为空");
                    return;
                }
                if(usernameStr.length()<2 || usernameStr.length()>20){
                    ToastUtils.toast(mContext,"联系人请输入2~20个汉字字符");
                    return;
                }
                if(!StringUtils.isChinese(usernameStr)){
                    ToastUtils.toast(this, "联系人请输入汉字字符");
                    return;
                }

                if (number.equals("")) {
                    ToastUtils.toast(this, "手机号不能为空");
                    return;
                }
                if (!Tools.isNumeric(number) || !number.startsWith("1")||number.length() < 11) {
                    ToastUtils.toast(this, "请输入正确的手机号码");
                    return;
                }

                voiceYanzheng = false;
                getYanzhengma();
                break;
            case R.id.button1:// 注册:
                String passwordStr=mPasswordEditText.getText().toString().trim();
                if (!Tools.IsConnectToNetWork(mContext)) {
                    ToastUtils.toast(mContext, "联网异常,请稍后再试");
                    return;
                }
                if(TextUtils.isEmpty(usernameStr)){
                    ToastUtils.toast(mContext,"联系人不能为空");
                    return;
                }
                if(usernameStr.length()<2 || usernameStr.length()>20){
                    ToastUtils.toast(mContext,"联系人请输入2~20个汉字字符");
                    return;
                }

                if(!StringUtils.isChinese(usernameStr)){
                    ToastUtils.toast(this, "联系人请输入汉字字符");
                    return;
                }

                if (number.equals("")) {
                    ToastUtils.toast(this, "手机号不能为空");
                    return;
                }
                if (!Tools.isNumeric(number) || !number.startsWith("1")||number.length() < 11) {
                    ToastUtils.toast(this, "请输入正确的手机号码");
                    return;
                }

                if ( TextUtils.isEmpty(passwordStr) ) {
                    ToastUtils.toast(this, "密码不能为空");
                    return;
                }
                if (passwordStr.length() < 8 || passwordStr.length()>20) {
                    ToastUtils.toast(this, "密码长度在8到20个字符");
                    return;
                }
//                if(TextUtils.isEmpty(selectCityEt.getText().toString().trim())){
//                    ToastUtils.toast(mContext,"请选择所在城市");
//                }
                if (mYanzhengmaEditText.getText().toString().equals("")) {
                    ToastUtils.toast(this, "请输入验证码");
                    return;
                }
                // if (mChooseKey.equals("")) {//账户类型
                // ToastUtils.toast(this, "请选择客户类型");
                // return;
                // }
                Register();
                break;

            case R.id.voice_verify:
                TongjiModel.addEvent(mContext, "新用户注册",
                        TongjiModel.TYPE_BUTTON_CLIKC, "语音验证码");

                if(TextUtils.isEmpty(usernameStr)){
                    ToastUtils.toast(mContext,"联系人不能为空");
                    return;
                }
                if(usernameStr.length()<2 || usernameStr.length()>20){
                    ToastUtils.toast(mContext,"联系人请输入2~20个汉字字符");
                    return;
                }
                if(!StringUtils.isChinese(usernameStr)){
                    ToastUtils.toast(this, "联系人请输入汉字字符");
                    return;
                }

                if (number.equals("")) {
                    ToastUtils.toast(this, "手机号不能为空");
                    return;
                }
                if (!Tools.isNumeric(number) || !number.startsWith("1")||number.length() < 11) {
                    ToastUtils.toast(this, "请输入正确的手机号码");
                    return;
                }

                if (System.currentTimeMillis() - mGetVoiceVerifyTime > 60 * 1000) {
                    voiceYanzheng = true;
                    mVoiceVerify.setTextColor(Color.GRAY);
                    mHandler.sendEmptyMessageDelayed(1, 1000 * 60);
                    mGetVoiceVerifyTime = System.currentTimeMillis();
                    getYanzhengma();
                } else {
                    ToastUtils.toast(this, "60秒内只能获取一次验证码,请稍后再试");
                }
                break;
            // case R.id.acccount_type_layout:// 选择客户类型
            // if (mConfigParser == null) {
            // ToastUtils.toast(this, "无法获取账户类型配置,请稍后再试");
            // return;
            // }
            // new AlertDialog.Builder(mContext).setItems(mAccountTypeItem, new
            // AlertListener()).show();
            // break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        cancelProgressDialog();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }

    // class AlertListener implements OnClickListener {
    // @Override
    // public void onClick(DialogInterface view, int position) {
    // mChooseKey = mConfigParser.entity.data.account_type.get(position).key;
    // mAccoutTypeText.setText(mAccountTypeItem[position]);
    // mAccoutTypeText.setTextColor(Color.BLACK);
    // }
    // }

}
