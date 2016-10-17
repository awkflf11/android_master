package com.foryou.truck.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.foryou.truck.util.ScreenInfo;

/**
 * Created by dubin on 16/8/1.
 */
public class CircleReduceTimeView extends View {

    private int mSeconds;
    private int mMin = 0, mSec = 0;


    Paint paint = new Paint();

    public InvalidListener mListener;

    public static interface InvalidListener {
        public void OnInValid();
        public void OnSeconds(long seconds);
    }

    public void setOnInValidListener(InvalidListener listener) {
        mListener = listener;
    }

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mSeconds--;
                    convertSeconds();
                    invalidate();
                    if (mSeconds > 0) {
                        mHander.removeMessages(0);
                        mHander.sendEmptyMessageDelayed(0, 1000);
                    }else{
                        if(mListener!=null){
                            mListener.OnInValid();
                        }
                    }
                    break;
            }

        }
    };

    public CircleReduceTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private void convertSeconds() {
        mMin = mSeconds / 60;
        mSec = mSeconds % 60;
    }

    public void setSeconds(String seconds) {
        try {
            mSeconds = Integer.valueOf(seconds);
        } catch (Exception e) {
            mSeconds = 0;
        }

        convertSeconds();
        mHander.removeMessages(0);
        mHander.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHander != null) {
            mHander.removeMessages(0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setAntiAlias(true);                       //设置画笔为无锯齿
        //canvas.drawColor(Color.WHITE);                  //白色背景
        paint.setStrokeWidth((float) ScreenInfo.dip2px(getContext(), 5));//线宽
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(android.graphics.Paint.Style.STROKE);

        RectF oval = new RectF();
        paint.setColor(0xffcceef9);
        oval.left = getMeasuredWidth() / 2 - ScreenInfo.dip2px(getContext(), 75) / 2;                              //左边
        oval.top = ScreenInfo.dip2px(getContext(), 5);//上边
        oval.right = getMeasuredWidth() / 2 + ScreenInfo.dip2px(getContext(), 75) / 2;                             //右边
        oval.bottom = ScreenInfo.dip2px(getContext(), 80);                                //下边
        canvas.drawArc(oval, 0, 360, false, paint);    //绘制圆弧

        paint.setColor(0xff00a9e0);                     //设置画笔颜色
        RectF oval1 = new RectF();                     //RectF对象
        oval1.left = getMeasuredWidth() / 2 - ScreenInfo.dip2px(getContext(), 75) / 2;                              //左边
        oval1.top = ScreenInfo.dip2px(getContext(), 5);                                   //上边
        oval1.right = getMeasuredWidth() / 2 + ScreenInfo.dip2px(getContext(), 75) / 2;                             //右边
        oval1.bottom = ScreenInfo.dip2px(getContext(), 80);//下边
        float sweepAngle = Float.valueOf(360) * Float.valueOf(mSeconds) / Float.valueOf(30 * 60);
        canvas.drawArc(oval1, -90, sweepAngle, false, paint);    //绘制圆弧

        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(ScreenInfo.dip2px(getContext(), 18));
        textPaint.setColor(0xff00a9e0);
        String miniuteText = mMin + "分";
        String secondText = mSec + "秒";
        canvas.drawText(miniuteText, getMeasuredWidth() / 2
                - textPaint.measureText(miniuteText) / 2, oval1.top + ScreenInfo.dip2px(getContext(), 35), textPaint);
        textPaint.setTextSize(ScreenInfo.dip2px(getContext(), 14));
        canvas.drawText(secondText, getMeasuredWidth() / 2
                - textPaint.measureText(secondText) / 2, oval1.top + ScreenInfo.dip2px(getContext(), 52), textPaint);
    }
}
