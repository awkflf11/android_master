package com.foryou.truck.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.foryou.truck.R;
import com.foryou.truck.tools.ImageTools;
import com.foryou.truck.util.ScreenInfo;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dubin on 16/7/16.
 */
public class PtrListView extends ListView {

    private ListView.OnScrollListener mScrollListener;
    private int mMaxvisibleItemCount = 0;
    private boolean showTopImage = false;
    private Bitmap mTopBitmap, mNoContentBitmap;
    private int left = 0, top = 0;
    private boolean mTimeArriver = false;
    private String noContentText = "暂无内容哦~";
    private String TAG = "PtrListView";


    public void setNoContentText(String text) {
        noContentText = text;
    }

    private final ListView.OnScrollListener ON_LISTVIEW_SCROLL_LISTENER = new ListView.OnScrollListener() {

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            // UtilsLog.i(TAG, "firstVisibleItem:" + firstVisibleItem
            // + ",visibleItemCount" + visibleItemCount
            // + ",totalItemCount" + totalItemCount);
            if (mMaxvisibleItemCount < visibleItemCount) {
                mMaxvisibleItemCount = visibleItemCount;
            }

            if (firstVisibleItem > mMaxvisibleItemCount) {
//                if (mTopImage != null) {
//                    mTopImage.setVisibility(android.view.View.VISIBLE);
//                }
                showTopImage = true;
            } else {
//                if (mTopImage != null) {
//                    mTopImage.setVisibility(android.view.View.GONE);
//                }
                showTopImage = false;
            }
            if (mScrollListener != null) {
                mScrollListener.onScroll(view, firstVisibleItem,
                        visibleItemCount, totalItemCount);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mScrollListener != null) {
                mScrollListener.onScrollStateChanged(view, scrollState);
            }
        }
    };


    private boolean isInImageRect(MotionEvent ev) {
        if (left != 0 && top != 0) {
            if (ev.getX() > (left - ScreenInfo.dip2px(getContext(), 10))
                    && ev.getY() > (top - ScreenInfo.dip2px(getContext(), 10))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (isInImageRect(ev)) {
                smoothScrollToPosition(0);
                return true;
            }
        }
        return super.onTouchEvent(ev);
    }


    private View headerView = null;
    @Override
    public void addHeaderView(View v) {
        super.addHeaderView(v);

        headerView = v;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        Paint paint = new Paint();

        int leastcount = 1;
        int headViewHeight=0;
        if(getHeaderViewsCount() == 1){
            leastcount = 2;
            headViewHeight = headerView.getHeight();
        }

        if ((getCount() == leastcount) && mTimeArriver) {
            int templ = (getRight() - mNoContentBitmap.getWidth()) / 2;
            int tempt = headViewHeight + (getBottom() - mNoContentBitmap.getHeight()-headViewHeight) / 2
                    - ScreenInfo.dip2px(getContext(), 80);
            canvas.drawBitmap(mNoContentBitmap, templ, tempt, paint);
            paint.setTextSize(ScreenInfo.dip2px(getContext(), 15));
            paint.setColor(0xff8e8e8e);

            int fontleft = (getRight() - ScreenInfo.dip2px(getContext(), 15) * noContentText.length()) / 2;
            //    + ScreenInfo.dip2px(getContext(), 5);
            if (TextUtils.isEmpty(noContentText)) {
                canvas.drawText("暂无内容哦~", fontleft,
                        tempt + mNoContentBitmap.getHeight() + ScreenInfo.dip2px(getContext(), 25), paint);
            } else {
                canvas.drawText(noContentText, fontleft,
                        tempt + mNoContentBitmap.getHeight() + ScreenInfo.dip2px(getContext(), 25), paint);
            }

        } else if (showTopImage) {
            left = getRight() - ScreenInfo.dip2px(getContext(), 10) - mTopBitmap.getWidth();
            top = getBottom() - ScreenInfo.dip2px(getContext(), 10) - mTopBitmap.getWidth();
            canvas.drawBitmap(mTopBitmap, left, top, paint);
        }
    }

    public PtrListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }


    private Timer mTimer = new Timer();

    public PtrListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTopBitmap = ImageTools.drawableToBitmap(getResources()
                .getDrawable(R.drawable.fanhuidingbu));
        mNoContentBitmap = ImageTools.drawableToBitmap(getResources()
                .getDrawable(R.drawable.no_content));

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTimeArriver = true;
            }
        }, 500);
    }

    @Override
    public void setOnScrollListener(ListView.OnScrollListener l) {
        super.setOnScrollListener(ON_LISTVIEW_SCROLL_LISTENER);
        mScrollListener = l;
    }
}

