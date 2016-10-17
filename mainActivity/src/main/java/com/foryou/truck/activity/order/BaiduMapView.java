package com.foryou.truck.activity.order;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.foryou.truck.R;
import com.foryou.truck.entity.OrderDetailEntity;
import com.foryou.truck.net.HttpApi;
import com.foryou.truck.parser.BdrenderReverseParser;
import com.foryou.truck.tools.ImageTools;
import com.foryou.truck.tools.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by dubin on 16/7/22.
 */
public class BaiduMapView extends LinearLayout {

    public String TAG = "BaiduMapView";
    OrderDetailEntity.OrderDetail mOrderDetailData;
    Button mDetailPlace;
    BdrenderReverseParser mBdParser;
    ImageView mMapView;

    public BaiduMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDetailPlace = (Button) findViewById(R.id.detail_place);
        mMapView = (ImageView)findViewById(R.id.bmapView);
    }

    private ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {
        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
            Log.i("aa", "load map finish() .........");
            mMapView.setBackgroundDrawable(ImageTools.bitmapToDrawable(arg2));
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {
        }
    };

    Handler mGetAddressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mDetailPlace.setText(mBdParser.entity.result.formatted_address);
        }
    };


    private void getCurrentAddress() {
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    String mStringresult = HttpApi
                            .getString((Tools.getMapUrlWitchLat(
                                    Double.valueOf(mOrderDetailData.location
                                            .get(mOrderDetailData.location
                                                    .size() - 1).lat),
                                    Double.valueOf(mOrderDetailData.location
                                            .get(mOrderDetailData.location
                                                    .size() - 1).lng))));
                    // Log.i("aa", "mStringresult:" + mStringresult);
                    mBdParser = new BdrenderReverseParser();
                    mBdParser.parser(mStringresult.substring(
                            mStringresult.indexOf("{"),
                            mStringresult.length() - 1));
                    Log.i("aa", "mStringresult:"
                            + mBdParser.entity.result.formatted_address);
                    mGetAddressHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }


    public void initData(OrderDetailEntity.OrderDetail orderDetail) {
        if (orderDetail.location.size() > 0
                && !orderDetail.status.key.equals("0")) {
            String url = Tools.getMutiMarkMapImageUrl(
                    orderDetail.location, 0, 0);
            Log.i(TAG, "map url:" + url);
            ImageLoader.getInstance().loadImage(url, mImageLoadingListener);
            setVisibility(android.view.View.VISIBLE);
            getCurrentAddress();
        } else {
            setVisibility(android.view.View.GONE);
        }
    }
}
