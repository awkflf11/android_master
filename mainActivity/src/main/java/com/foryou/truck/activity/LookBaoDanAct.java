package com.foryou.truck.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.DisplayImageActivity;
import com.foryou.truck.DubangXieyiActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.util.BindView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @des:查看保单的页面
 */
public class LookBaoDanAct extends BaseActivity {
    private Context mContext;
    private String TAG = "LookBaoDanAct";
    //
    @BindView(id = R.id.baodan_url, click = true)
    private ImageView baoDanIv;//
    @BindView(id = R.id.baodan_tv2, click = true)
    private TextView baoDanTv2;
    Drawable drawable;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    private String imageUrl = "";

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.act_look_baodan);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this;
        initView();
        imageUrl = getIntent().getStringExtra("image");
        //imageUrl="http://d.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=603e37439313b07ebde8580c39e7bd15/a8014c086e061d9591b7875a7bf40ad163d9cadb.jpg";
        imageLoader.displayImage(imageUrl, baoDanIv,
                options, animateFirstListener);
    }

    private void initView() {
        setTitle("查看保单");
        ShowBackView();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.avatar_default)
                // 在ImageView加载过程中显示图片
                .showImageForEmptyUri(R.drawable.avatar_default)
                .showImageOnFail(R.drawable.avatar_default).cacheInMemory(true) // 加载图片时会在内存中加载缓存
                .cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
                .displayer(new RoundedBitmapDisplayer(0)).build();

        String hint = baoDanTv2.getText().toString();
        SpannableString spanText = new SpannableString(hint);
        spanText.setSpan(
                new ForegroundColorSpan(getResources().getColor(
                        R.color.my_blue_color)), hint.indexOf("《"),
                spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        baoDanTv2.setMovementMethod(LinkMovementMethod.getInstance());
        baoDanTv2.setHighlightColor(Color.TRANSPARENT);
        baoDanTv2.setText(spanText);
    }


    @Override
    public void onClickListener(int id) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (id) {
            case R.id.baodan_url://
                intent = new Intent(mContext, DisplayImageActivity.class);
                intent.putExtra("image", imageUrl);
                startActivity(intent);
                break;
            case R.id.baodan_tv2:// 跳到报险流程
                intent = new Intent();
                intent.setClass(this, DubangXieyiActivity.class);
                intent.putExtra("baodan", true);
                startActivity(intent);
                break;
        }
    }


    /**
     * 图片加载监听事件
     **/
    private static class AnimateFirstDisplayListener extends
            SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

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
        // TODO Auto-generated method stub
        super.onStop();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }

}
