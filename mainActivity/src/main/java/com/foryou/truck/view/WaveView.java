package com.foryou.truck.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.foryou.truck.util.ScreenInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dubin on 16/2/26.
 */
public class WaveView extends ImageView {

    private Paint paint;
    // 是否运行
    private boolean isStarting = false;
    private List<String> alphaList = new ArrayList<String>();
    private List<String> startWidthList = new ArrayList<String>();
    private Drawable mDrawable;
    private String TAG = "WaveView";
    private int mBgHeight, mBgWidth;
    private String startWidht = "180";
    private String startalpha = "255";
    private int addOneCirleApha = 140;//新增圆的刻度
    private int widthInc = 1;
    private int alphaDec = 8;

    private int mImageWidth = 0, mImageHeight = 0;
    private int mScreenWidth, mScreenHeight;

    private int DEFAULTDELAY = 50;
    private int postDelay = DEFAULTDELAY;

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init();
    }

    public WaveView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init();
    }

    private void init() {
        paint = new Paint();
        // 设置博文的颜色
        paint.setColor(0xff00a9e0);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        alphaList.add(startalpha);// 圆心的不透明度

        mImageWidth = this.getDrawable().getIntrinsicWidth();
        mImageHeight = this.getDrawable().getIntrinsicHeight();
        if (mImageWidth > 0) {
            startWidht = mImageWidth / 2 + "";
        }
        startWidthList.add(startWidht);

        mScreenWidth = ScreenInfo.getScreenInfo((Activity) getContext()).widthPixels;
        mScreenHeight = ScreenInfo.getScreenInfo((Activity) getContext()).heightPixels;
    }

    private View.OnClickListener mlistener;
    private boolean downflag = false;

    private boolean isInImageAreas(float x, float y) {
        int right = getRight(), bottom = getBottom();
        int marginX = (right - mImageWidth) / 2;
        int marginY = (bottom - mImageHeight) / 2;
        if (x >= marginX && x <= right - marginX) {
            if (y >= marginY && y <= bottom - marginY) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isStarting) {
            return super.onTouchEvent(event);
        }
        android.util.Log.i(TAG, "action:" + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInImageAreas(event.getX(), event.getY())) {
                    postDelay = 0;
                    downflag = true;
                } else {
                    downflag = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                android.util.Log.i(TAG, "actionup.....");
                postDelay = DEFAULTDELAY;
                if (isInImageAreas(event.getX(), event.getY())) {
                    if (downflag) {
                        mlistener.onClick(this);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(mListener);
        mlistener = l;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundColor(Color.TRANSPARENT);// 颜色：完全透明

        for (int i = 0; i < alphaList.size(); i++) {
            int alpha = Integer.parseInt(alphaList.get(i));
            // 圆半径
            int startWidth = Integer.parseInt(startWidthList.get(i));
            paint.setAlpha(alpha);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, startWidth,
                    paint);
            if (isStarting && alpha > 0) {
                int decAlpha = alpha - alphaDec;
                if (decAlpha <= 0) {
                    decAlpha = 0;
                }

                alphaList.set(i, decAlpha + "");
                startWidthList.set(i, (startWidth + widthInc) + "");
            }
        }

        if (isStarting && Integer
                .parseInt(alphaList.get(alphaList.size() - 1)) <= addOneCirleApha) {
            alphaList.add(startalpha);
            startWidthList.add(startWidht);
        }

        if (isStarting && Integer.valueOf(alphaList.get(0)) == 0) {
            startWidthList.remove(0);
            alphaList.remove(0);
        }
        // 刷新界面
        postInvalidateDelayed(postDelay);
        //invalidate();
    }

    // 执行动画
    public void start() {
        isStarting = true;
    }

    // 停止动画
    public void stop() {
        isStarting = false;
    }

    // 判断是都在不在执行
    public boolean isStarting() {
        return isStarting;
    }

}

