package com.foryou.truck.sendproduct;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.R;
import com.foryou.truck.WebViewActivity;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.UtilsLog;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by dubin on 16/2/29.
 */
public class DisplayAdsActivity extends BaseActivity {
    @BindView(id = R.id.ads_img)
    private ImageView mAdsImage;
    @BindView(id = R.id.close_img,click=true)
    private ImageView mCloseImage;

    private int mScreenWidth, mScreenHeight;
    private int mImageWidth, mImageHeight;
    private String TAG = "DisplayAdsActivity";
    private String adlink;

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.display_ads);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScreenWidth = ScreenInfo.getScreenInfo(this).widthPixels;
        mScreenHeight = ScreenInfo.getScreenInfo(this).heightPixels;


        adlink = getIntent().getStringExtra("adlink");
        String adphoto = getIntent().getStringExtra("adphoto");
        //adphoto = "http://juemei-img.xiuna.com/list/2015-4-17/4/001.jpg";

        mAdsImage.setOnClickListener(this);

        imageLoader.displayImage(adphoto, mAdsImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                mImageWidth = bitmap.getWidth();
                mImageHeight = bitmap.getHeight();
                UtilsLog.i(TAG, "width:" + mImageWidth + ",mImageHeight:" + mImageHeight);

                int img_h, img_w;
                img_w = mScreenWidth * 250 / 320;
                img_h = img_w * mImageHeight / mImageWidth;
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(img_w, img_h);
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                mAdsImage.setLayoutParams(lp);
                mAdsImage.setImageBitmap(bitmap);

                RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp2.topMargin = (mScreenHeight - img_h)/2-65;
                lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                lp2.rightMargin = (mScreenWidth-img_w)/2-25;
                mCloseImage.setLayoutParams(lp2);
                mCloseImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onClickListener(int id) {
        switch(id){
            case R.id.close_img:
                finish();
                this.overridePendingTransition(0, 0);
                break;
            case R.id.ads_img:
                if(!TextUtils.isEmpty(adlink)) {
                    Intent intent = new Intent(this, WebViewActivity.class);
                    intent.putExtra("url", adlink);
                    startActivity(intent);
                }
                break;
        }
    }
}
