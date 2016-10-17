package com.foryou.truck.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.foryou.truck.BaseActivity;
import com.foryou.truck.EntryLoginActivity;
import com.foryou.truck.ModifyMyInfoActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.UserDetailEntity;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.parser.UpAvaterParser;
import com.foryou.truck.parser.UserDetailJsonParser;
import com.foryou.truck.tools.ImageLoaderUtils;
import com.foryou.truck.tools.SdCardUtil;
import com.foryou.truck.tools.SharePlatform;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.tools.UpAvatarUtils;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.ImageUtils;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.TextViewWithIconFont;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PersonCenterAct extends BaseActivity {
    private Context mContext;
    private String TAG = "PersonCenterAct";
    // #######分享:
    private IWXAPI api;
    // req.scene = SendMessageToWX.Req.WXSceneTimeline;//分享到微信朋友圈
    // req.scene = SendMessageToWX.Req.WXSceneSession;// 分享到微信朋友圈
    private static int WXHaoYou = SendMessageToWX.Req.WXSceneSession;
    private static int WXfirend = SendMessageToWX.Req.WXSceneTimeline;
    @BindView(id = R.id.mod_personinfo_rl, click = true)
    private RelativeLayout modifyInfoBtn;
    @BindView(id = R.id.share_rl, click = true)
    private RelativeLayout shareBtn;
    @BindView(id = R.id.mod_password_rl, click = true)
    private LinearLayout modPasswordRL;//跳到修改密码
    @BindView(id = R.id.fankui_rl, click = true)
    private RelativeLayout fanKuiBtn;
    @BindView(id = R.id.sett_rl, click = true)
    private RelativeLayout setBtn;//
    //@BindView(id = R.id.get_daijinquan_rl, click = true)
    //private RelativeLayout shareQuanRL;// 邀请好友获得代金券
    @BindView(id = R.id.my_daijinquan_rl, click = true)
    private RelativeLayout myQuanRl;// 我的代金券
    @BindView(id = R.id.call_phone_tv, click = true)
    private TextView callPhoneBtn;
//    @BindView(id = R.id.title)
//    private TextView mTitle;
    @BindView(id = R.id.arraw_back_btn,click = true)
    private RelativeLayout backBtn;
    @BindView(id = R.id.avatar_iv, click = true)
    private ImageView avatarIv;// 上传头像
    @BindView(id = R.id.login_ll, click = true)
    private LinearLayout loginLL;
    @BindView(id = R.id.name_rl)
    private RelativeLayout nameRL;
    @BindView(id = R.id.phone_ll)
    private LinearLayout phoneLL;
    @BindView(id = R.id.usename_tv)
    private TextView mName;//
    @BindView(id = R.id.sex_iv)
    private TextViewWithIconFont sexIv;
    @BindView(id = R.id.phone_tv)
    private TextView mPhonenumber;
    private String tp = null;// 头像的base64字符串
    Drawable drawable;
    private ImageLoaderUtils imageLoaderUtils=ImageLoaderUtils.getImageLoaderUtils();
    private DisplayImageOptions options;
    private ImageLoader imageLoader = imageLoaderUtils.getImageLoader();
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    @BindView(id = R.id.status_tv)
    private TextView statusTv;//认证的状态

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.act_person_center);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initView();
        // 微信分享注册
        api = WXAPIFactory.createWXAPI(mContext, Constant.APP_ID, true);
        api.registerApp(Constant.APP_ID);
        UtilsLog.i(TAG, "getUid" + SharePerfenceUtil.getUid(mContext));

        if (SharePerfenceUtil.IsLogin(mContext)) {
            getUserDetail();
        } else {
            nameRL.setVisibility(View.INVISIBLE);
            phoneLL.setVisibility(View.INVISIBLE);
            loginLL.setVisibility(View.VISIBLE);
        }
    }

    // @Override
    // public void onResume() {
    // super.onResume();
    // if (SharePerfenceUtil.IsLogin(mContext)) {
    // getUserDetail();
    // } else {
    // nameRL.setVisibility(View.INVISIBLE);
    // phoneLL.setVisibility(View.INVISIBLE);
    // loginLL.setVisibility(View.VISIBLE);
    // }
    // }

    private void initView() {
        //mTitle.setText("会员中心");
       // ShowBackView();
        options=imageLoaderUtils.setOptions(R.drawable.avatar_default,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 4:
                // if (resultCode == RESULT_OK) {
                UserDetailEntity userInfoEntity = SharePerfenceUtil.getUserDetail(mContext);
                if (userInfoEntity == null||userInfoEntity.data == null) {
                    return;
                }
                imageLoader.displayImage(userInfoEntity.data.portrait, avatarIv, options, animateFirstListener);
                if (SharePerfenceUtil.IsLogin(mContext)) {
                    getUserDetail();
                }
                // }
                break;
            case 1:// 如果是直接从相册获取
                if (data != null) {
                    ImageUtils.startPhotoZoom(mContext, data.getData());
                }
                break;
            case 2:// 如果是调用相机拍照时
                //getExternalFilesDir(Environment.DIRECTORY_PICTURES)+ File.separator
                File temp = new File(SdCardUtil.getTouXiangPath(mContext) + "avatar.png");
                ImageUtils.startPhotoZoom(mContext, Uri.fromFile(temp));
                break;
            case 3:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClickListener(int id) {
        Intent intent;
        switch (id) {
            case R.id.arraw_back_btn:
                this.finish();
                break;
            case R.id.avatar_iv:// 上传头像
                if (!SharePerfenceUtil.IsLogin(mContext)) {
                    ToastUtils.toast(this, "请登录后再操作");
                    return;
                }
                UpAvatarUtils.ShowPickDialog(mContext);
                break;
            case R.id.login_ll:// 跳到登录页面
                intent = new Intent();
                intent.setClass(this, EntryLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.mod_personinfo_rl:
                intent = new Intent();
                intent.setClass(this, ModifyMyInfoActivity.class);
                startActivityForResult(intent, 4);
                break;
            case R.id.mod_password_rl:// 跳到修改密码
                intent = new Intent();
                intent.setClass(this, ModifyPasswordAct.class);
                startActivity(intent);
                break;

            case R.id.share_rl:// 分享微信朋友圈
                SharePlatform.shareWebPage(PersonCenterAct.this, api, WXfirend);
                TongjiModel.addEvent(mContext, "个人中心",
                        TongjiModel.TYPE_BUTTON_CLIKC, "分享到朋友圈");
                break;
            case R.id.my_daijinquan_rl:// 我的代金券
                intent = new Intent();
                intent.setClass(this, MyDaiJinQuanAct.class);// ,
                startActivity(intent);
                break;
            case R.id.fankui_rl:
                intent = new Intent();
                intent.setClass(this, FeedBackAct.class);
                //intent.setClass(this, ZiliaoRenzhengAct.class);//测试
                startActivity(intent);
                break;
            case R.id.sett_rl:
                intent = new Intent();
                intent.setClass(this, SetAct.class);
                startActivity(intent);
                break;
            case R.id.call_phone_tv:
                Constant.GotoDialPage(PersonCenterAct.this, Constant.PHONE_NUMBER);
                break;
//            case R.id.get_daijinquan_rl:
//                if(mUserDetailParser!=null && !TextUtils.isEmpty(mUserDetailParser.entity.data.code)) {
//                    SharePlatform.shareWebPage(PersonCenterAct.this, api, UrlConstant.INVATE_URL + "?code="
//                                    + mUserDetailParser.entity.data.code, "我用福佑卡车发货，省钱更省心~送你100元代金券，快来体验！"
//                            , "点击立即省钱。整车运输来福佑，运费便宜找车快！", WXHaoYou);
//                    TongjiModel.addEvent(mContext, "个人中心",
//                            TongjiModel.TYPE_BUTTON_CLIKC, "分享好友获取代金券");
//                }else{
//                    ToastUtils.toast(mContext,"正在加载数据，请稍后再试");
//                }
//                break;
        }
    }

    private void upAvatar() {
        String url = UrlConstant.BASE_URL + "/user/portraitUpdate";
        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        UtilsLog.i(TAG, "response:" + response);
                        cancelProgressDialog();
                        UpAvaterParser mParser = new UpAvaterParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                ToastUtils.toast(mContext, "上传头像成功");
                                imageLoader.displayImage(mParser.entity.data.portrait, avatarIv,
                                        options, animateFirstListener);
                                UserDetailEntity userInfoEntity = SharePerfenceUtil.getUserDetail(mContext);
                                userInfoEntity.data.portrait = mParser.entity.data.portrait;
                                SharePerfenceUtil.SaveUserDetail(mContext, userInfoEntity);
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
                // return super.getPostBodyData();
                Map<String, String> parmas = new HashMap<String, String>();
                parmas.put("imagePortrait", tp);// 头像的base64字符串
                parmas.put("uid", SharePerfenceUtil.getUid(mContext));
                // UtilsLog.i(TAG, "头像的base64字符串==================" + tp);
                return parmas;
            }
        };
        showProgressDialog();
        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }


    private void initData(UserDetailJsonParser mUserDetailParser){
        if (!TextUtils.isEmpty(mUserDetailParser.entity.data.portrait)) {// 加载头像
            imageLoader.displayImage(mUserDetailParser.entity.data.portrait,
                    avatarIv, options, animateFirstListener);
        } else {
        }
        if (mUserDetailParser.entity.data.name.equals("")) {
            mName.setText("无");
        } else {
            mName.setText(mUserDetailParser.entity.data.name);
        }
//        if (mUserDetailParser.entity.data.mobile.equals("")) {
//            mPhonenumber.setText("无");
//        } else {
//           mPhonenumber.setText(mUserDetailParser.entity.data.mobile);
//         }
        if (mUserDetailParser.entity.data.gender.equals("0")) {
            // mGenderLayout.setVisibility(android.view.View.GONE);
        } else if (mUserDetailParser.entity.data.gender.equals("1")) {
            // mGenderLayout.setVisibility(android.view.View.VISIBLE);
            sexIv.setText(getResources().getString(R.string.man_icon));
        } else {
            // mGenderLayout.setVisibility(android.view.View.VISIBLE);
            sexIv.setText(getResources().getString(R.string.women_icon));
        }
        // 增加是否认证的状态 处理：
        if(!TextUtils.isEmpty(mUserDetailParser.entity.data.check_status)
              && mUserDetailParser.entity.data.check_status.equals("1")  ){
            statusTv.setText("已认证");
            statusTv.setBackgroundResource(R.drawable.corner_blue_bg);
        }else{
            statusTv.setText("未认证");
            statusTv.setBackgroundResource(R.drawable.corner_hui_bg);
        }

        SharePerfenceUtil.SaveUserDetail(mContext, mUserDetailParser.entity);

    }

    private UserDetailJsonParser mUserDetailParser;

    private void getUserDetail() {
        showProgressDialog();
        NetWorkUtils.getUserDetail(mContext, TAG,
                new NetWorkUtils.NetJsonRespon() {
                    @Override
                    public void onRespon(BaseJsonParser parser) {
                        cancelProgressDialog();
                        mUserDetailParser = (UserDetailJsonParser) parser;
                        initData(mUserDetailParser);
                    }
                });
    }

    /**
     * @保存裁剪之后的图片数据
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            if (photo == null) {
                return;
            }
            //photo = ImageUtils.toRoundBitmap(photo);
            drawable = new BitmapDrawable(photo);
            tp = ImageUtils.bitmapToBase64(photo);
            if (!Tools.IsConnectToNetWork(mContext)) {
                ToastUtils.toast(mContext, "联网异常,请稍后再试");
                return;
            }
            // avatarIv.setBackgroundDrawable(drawable);
            upAvatar();
            /**
             * @将裁剪之后的图片以Base64Coder的字符方式上 传到服务器
             */
            /*
             * ByteArrayOutputStream stream = new ByteArrayOutputStream();
			 * photo.compress(Bitmap.CompressFormat.JPEG, 60, stream); byte[] b
			 * = stream.toByteArray(); // 将图片流以字符串形式存储下来 tp = new
			 * String(Base64Coder.encodeLines(b));
			 * 这个地方大家可以写下给服务器上传图片的实现，直接把tp直接上传就可以了， 服务器处理的方法是服务器那边的事了
			 * 如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换 为我们可以用的图片类型就OK啦...吼吼
			 * Bitmap dBitmap = BitmapFactory.decodeFile(tp); Drawable drawable
			 * = new BitmapDrawable(dBitmap);
			 */
            // ib.setBackgroundDrawable(drawable);
            // iv.setBackgroundDrawable(drawable);
        }
    }

    /**
     * @图片加载监听事件
     **/
    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500); // 设置image隐藏动画500ms
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }

}
