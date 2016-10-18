package com.foryou.truck.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.foryou.truck.R;
import com.foryou.truck.util.UtilsLog;

import java.util.Date;

public class PullDownView extends LinearLayout {

    public enum EPullStatus { // 此时listview处于什么状态
        PULL_NORMAL, // 普通状态
        PULL_DOWN, // 需要继续下拉才能刷新
        PULL_RELEASE, // 松开触摸屏刷新
        PULL_REFRESHING, // 下拉之后正在刷新
        MORE_REFRESHING // 点击更多正在刷新
    }

    private MyListView mListView;
    private HeaderView mHeaderView; // 显示当前listview状态的控件，位于listview上边
    private ImageView mDivider; // header 和 listview之间的分割线

    private EPullStatus mPullStatus = EPullStatus.PULL_NORMAL; // 当前listview状态
    private int mFirstVisibleItem = -1; // listview中最上边的item的下标值

    private OnTouchDownUpListener mOnTouchDownUpListener;// 用于监听touchdown和touchup
    private OnRefreshListener mOnRefreshListener = null; // 当listview进行刷新时的回调
    private OnHeaderViewStateChangeListener mOnHeaderViewStateChangeListener = null; // 当headerview的状态改变时的回调
    private OnObtainMoreListener mOnObtainMoreListener = null; // 当点击“获取更多”时的回调
    private OnItemClickListener mPullDownViewOnItemClickListener = null; // 当点击items时的回调
    private OnItemLongClickListener mPullDownViewOnItemLongClickListener = null; // 当长点击items时的回调
    private OnPullDownListener mPullDownListener = null; // 监听开始下拉的listener
    private OnPullDownBackListener mOnPullDownBackListener = null; // 当headerview回弹到初始位置时触发
    private OnTouchViewListener mOnTouchViewListener = null;// 当前view被触摸的响应

    private int mTouchSlop = 0;
    private boolean mMoved = false; // 记录用户是否只是拉动控件，而不是点击某一item
    private boolean mPullDownEnabled = true; // 该控件是否可以下拉刷新

    private View mFooterView = null;
    // private int mFooterViewHeight = -1; // footerView的高度
    private Scroller mScroller;

    private String[] mStrHeaderStatusInfo;
    private String mLastUpdateTitle;
    private int mArrowIcon = R.drawable.refurbish_1; // headerView中的箭头图标
    private int mArrowIcon1 = R.drawable.refurbish_2;

    private RelativeLayout mNoContentView;

    //@SuppressWarnings("unused")
    private final String TAG = "PullDownView";

    public PullDownView(Context context) {
        super(context);
        mScroller = new Scroller(context);
    }

    public PullDownView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = new Scroller(context);
    }

    /**
     * 初始化
     */
    public void init() {
        initControls();

        // 初始化headerview中要显示的内容
        Context context = getContext();
        mStrHeaderStatusInfo = new String[]{
                context.getString(R.string.pull_to_refresh_pull_label),
                context.getString(R.string.pull_to_refresh_release_label),
                context.getString(R.string.pull_to_refresh_refreshing_label)};
        mLastUpdateTitle = context.getString(R.string.last_update);

        // 获取touchSlop
        final ViewConfiguration configuration = ViewConfiguration
                .get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();

        mOnHeaderViewStateChangeListener = ON_HEADER_VIEW_STATE_CHANGE_LISTENER;
    }

    /**
     * 设置headerview中要显示的信息
     *
     * @param strs :长度为3的字符串数组，分别为下拉状态提示语；回弹状态提示语；刷新状态提示语
     */
    public void setHeaderStatusInfo(String[] strs) {
        mStrHeaderStatusInfo = strs;
    }

    /**
     * 设置更新时间的标题栏
     *
     * @param title
     */
    public void setLastUpdateTitle(String title) {
        mLastUpdateTitle = title;
    }

    /**
     * 初始化
     *
     * @param layoutID headerview的布局，layoutID等于-1表示使用默认的布局
     */
    public void init(int layoutID) {
        initControls();

        if (layoutID == -1) {
            return;
        }

        mHeaderView.removeAllViews();

        // 添加headerView中的layout
        View view = ((LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(layoutID, null);
        mHeaderView.addView(view);

        mOnHeaderViewStateChangeListener = null;

        final ViewConfiguration configuration = ViewConfiguration
                .get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @SuppressLint("WrongViewCast")
    private void initControls() {
        // 获取到控件的引用
        mListView = (MyListView) findViewById(R.id.pull_down_listview);
        mHeaderView = (HeaderView) findViewById(R.id.pull_down_headerview);
        mDivider = (ImageView) findViewById(R.id.iv_divider);

        // 为listview设置监听函数
        // mListView.setOnScrollListener(ON_LISTVIEW_SCROLL_LISTENER);
        mListView.setOnTouchListener(ON_LISTVIEW_TOUCH_LISTENER);
        mListView.setOnItemClickListener(ON_ITEM_CLICK_LISTENER);
        mListView.setOnItemLongClickListener(ON_ITEM_LONG_CLICK_LISTENER);

        ImageView mTopImage = (ImageView) findViewById(R.id.top_image);
        mListView.setTopImage(mTopImage);
        // mListView.setAdapter(new PraiseAdapter(getContext(), new
        // ArrayList<Rows>()));

        mNoContentView = (RelativeLayout) findViewById(R.id.no_content);
    }

    public void showNoNetWorkView() {
        mNoContentView.setVisibility(android.view.View.VISIBLE);
        TextView text = (TextView) mNoContentView.findViewById(R.id.text);
        ImageView img = (ImageView) mNoContentView.findViewById(R.id.img);

        text.setText("网络连接失败，请稍候再试哦~");
        img.setBackgroundResource(R.drawable.wifi);
    }

    public void showNoDataView(boolean flag) {
        if (flag) {
            mNoContentView.setVisibility(android.view.View.VISIBLE);
            TextView text = (TextView) mNoContentView.findViewById(R.id.text);
            ImageView img = (ImageView) mNoContentView.findViewById(R.id.img);
            Button btn = (Button) mNoContentView.findViewById(R.id.btn);
            btn.setVisibility(android.view.View.GONE);

            text.setText("暂无内容哦~");
            img.setBackgroundResource(R.drawable.no_content);
        } else {
            mNoContentView.setVisibility(android.view.View.GONE);
        }
    }

    public void HideNoContentView() {
        mNoContentView.setVisibility(View.GONE);
    }

    public void showNoDataView(int imgId, String contentText, String btnText, View.OnClickListener listener) {
        mNoContentView.setVisibility(android.view.View.VISIBLE);
        TextView text = (TextView) mNoContentView.findViewById(R.id.text);
        if (!TextUtils.isEmpty(contentText)) {
            text.setText(contentText);
        }
        ImageView img = (ImageView) mNoContentView.findViewById(R.id.img);
        TextView btn = (TextView) mNoContentView.findViewById(R.id.btn);
        if (imgId > 0) {
            img.setBackgroundResource(imgId);
        }
        if (!TextUtils.isEmpty(btnText)) {
            btn.setVisibility(View.VISIBLE);
            btn.setText(btnText);
            if (listener != null) {
                btn.setOnClickListener(listener);
            }
        }
    }

    public ListView getListView() {
        return mListView;
    }

    public HeaderView getHeaderView() {
        return mHeaderView;
    }

    /**
     * 设置该控件是否可以下拉刷新
     *
     * @param pullDownEnabled
     */
    public void setPullDownEnabled(boolean pullDownEnabled) {
        this.mPullDownEnabled = pullDownEnabled;
    }

    /**
     * 监听开始下拉的listener
     *
     * @param pullDownListener
     */
    public void setOnPullDownListener(OnPullDownListener pullDownListener) {
        mPullDownListener = pullDownListener;
    }

    /**
     * 当headerview回弹到初始位置时触发
     *
     * @param onPullDownBackListener
     */
    public void setOnPullDownBackListener(
            OnPullDownBackListener onPullDownBackListener) {
        mOnPullDownBackListener = onPullDownBackListener;
    }

    /**
     * 设置下拉头的背景色
     *
     * @param bkg :resource id
     */
    public void setHeaderViewBkg(int bkg) {
        mHeaderView.setBackgroundResource(bkg);
    }

    public void setHeaderViewBkg(Drawable d) {
        mHeaderView.setBackgroundDrawable(d);
    }

    /**
     * header和listview之间的分割线是否需要显示，默认是不显示
     *
     * @param enabled
     */
    public void setDividerEnabled(boolean enabled) {
        if (enabled) {
            mDivider.setVisibility(View.VISIBLE);
        } else {
            mDivider.setVisibility(View.GONE);
        }
    }

    /**
     * 设置状态文字的颜色
     *
     * @param color
     */
    public void setHeaderViewStatusTextColor(int color) {
        TextView textView = (TextView) mHeaderView
                .findViewById(R.id.status_info);
        textView.setTextColor(color);
    }

    /**
     * 设置更新时间的颜色
     *
     * @param color
     */
    public void setHeaderViewUpdateTimeTextColor(int color) {
        TextView textView = (TextView) mHeaderView
                .findViewById(R.id.last_update);
        textView.setTextColor(color);
    }

    /**
     * 添加图片，表示状态栏中的箭头
     *
     * @param res
     */
    public void setArrowImage(int res) {
        mArrowIcon = res;
    }

    /**
     * 更新时间控件的可见性的设置
     *
     * @param visibility
     */
    public void setUpdateTimeVisibility(int visibility) {
        mHeaderView.findViewById(R.id.last_update)
                .setVisibility(visibility);
    }

    public void setFooterViewBkg(Drawable d) {
        if (null != mFooterView) {
            mFooterView.setBackgroundDrawable(d);
        }
    }

    /**
     * 刷新后调用该函数
     */
    public void notifyRefreshComplete() {
        setPullStatus(EPullStatus.PULL_NORMAL);
        setLastUpdateTime();
        onRefreshComplete();

        // mNoContentView.setVisibility(android.view.View.GONE);
    }

    /**
     * 是否要显示按钮“获取更多”
     *
     * @param visibility
     */
    public void showFooterView(boolean visibility) {
        if (null != mFooterView) {
            if (visibility) {
                // mFooterView.setVisibility(View.VISIBLE);
                mFooterView.findViewById(R.id.refresh_progress).setVisibility(
                        View.VISIBLE);
                mFooterView.findViewById(R.id.textView_obtain_more)
                        .setVisibility(View.VISIBLE);

            } else {
                // mFooterView.setVisibility(View.GONE);
                mFooterView.findViewById(R.id.refresh_progress).setVisibility(
                        View.GONE);
                mFooterView.findViewById(R.id.textView_obtain_more)
                        .setVisibility(View.GONE);
            }
        }
    }

    /**
     * 刷新后的处理
     */
    private void onRefreshComplete() {
        resetHeader();

        // resetFooter();

    }

    @SuppressWarnings("unused")
    private void resetFooter() {
        if (null != mFooterView) {
            ProgressBar pb = (ProgressBar) mFooterView
                    .findViewById(R.id.refresh_progress);
            pb.setVisibility(View.GONE);
        }
    }

    public void setOnTouchDownUpListener(OnTouchDownUpListener listener) {
        mOnTouchDownUpListener = listener;
    }

    /**
     * 设置刷新时的监听函数
     *
     * @param onRefreshListener
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    /**
     * 设置状态发生变化时的监听函数
     *
     * @param onHeaderViewStateChangeListener
     */
    public void setOnHeaderViewStateChangeListener(
            OnHeaderViewStateChangeListener onHeaderViewStateChangeListener) {
        mOnHeaderViewStateChangeListener = onHeaderViewStateChangeListener;
    }

    /**
     * 设置获取更多信息时的监听函数
     *
     * @param onObtainMoreListener
     */
    public void setOnObtainMoreListener(
            OnObtainMoreListener onObtainMoreListener) {
        this.mOnObtainMoreListener = onObtainMoreListener;
    }

    /**
     * 设置listview中的item被点击时的监听函数
     *
     * @param pullDownViewOnItemClickListener
     */
    public void setPullDownViewOnItemClickListener(
            OnItemClickListener pullDownViewOnItemClickListener) {
        this.mPullDownViewOnItemClickListener = pullDownViewOnItemClickListener;
    }

    /**
     * 设置listview中的item被长时间点击时的监听函数
     *
     * @param pullDownViewOnItemLongClickListener
     */
    public void setPullDownViewOnItemLongClickListener(
            OnItemLongClickListener pullDownViewOnItemLongClickListener) {
        this.mPullDownViewOnItemLongClickListener = pullDownViewOnItemLongClickListener;
    }

    public void setOnTouchViewListener(OnTouchViewListener mOnTouchViewListener) {
        this.mOnTouchViewListener = mOnTouchViewListener;
    }

    /**
     * 设置获取更多时的监听函数
     *
     * @param layoutID
     */
    public void setFooterView(int layoutID) {
        if (layoutID == 0) {
            layoutID = R.layout.footer_item;
        }

        View view = ((LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(layoutID, null);

        mFooterView = view;

        mListView.addFooterView(view);
    }

    /**
     * 设置获取更多时的监听函数
     *
     * @param view
     */
    public void setFooterView(View view) {
        mFooterView = view;

        mListView.addFooterView(view);
    }

    /**
     * 如果有footview 就remove，没有就不处理
     *
     * @param
     */
    public void mRemoveFootView() {
        if (mFooterView != null) {
            mListView.removeFooterView(mFooterView);
            mFooterView = null;
        }
    }

    /**
     * 重新设置状态
     */
    private void resetHeader() {
        int scrollY = getScrollY();
        mScroller.startScroll(0, scrollY, 0, -(scrollY), 800);
        invalidate();
    }

    /**
     * 准备刷新
     */
    private void preparePullRefresh() {

        int height = mHeaderView.getHeight();
        if (height == 0) { // 外部控件可能在onCreate里边调用了该函数，所以高度为0
            height = convertDIP2PX(getContext(), 60);
        }
        int scrollY = getScrollY();
        mScroller.startScroll(0, scrollY, 0, -(scrollY + height), 800);
        invalidate();
    }

    /**
     * dip转换为px
     *
     * @param context
     * @param dip
     * @return
     */
    public static int convertDIP2PX(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 设置成正在刷新状态
     */
    public void setRefreshState() {
        setPullStatus(EPullStatus.PULL_REFRESHING);
        preparePullRefresh();
    }

    /**
     * 设置最后一次更新的时间
     *
     * @param //最后一次更新的时间
     */
    public void setLastUpdateTime() {
        // 转换时间格式
        // String newFormatTimeString = getFormatChineseTime(context);
        ((TextView) mHeaderView.findViewById(R.id.last_update))
                .setText(mLastUpdateTitle + new Date().toLocaleString());
    }

    /**
     * 设置当前状态，当状态发生改变时，调用mOnHeaderViewStateChangeListener的回调
     *
     * @param pullStatus 当前状态
     */
    private void setPullStatus(EPullStatus pullStatus) {
        if (mPullStatus == pullStatus) {
            return;
        }

        /**
         * 当控件开始下拉时，调用该接口
         */
        if (mPullStatus == EPullStatus.PULL_NORMAL
                && pullStatus == EPullStatus.PULL_DOWN) {
            if (mPullDownListener != null)
                mPullDownListener.startPullDown();
        } else if (mPullStatus == EPullStatus.PULL_DOWN
                && pullStatus == EPullStatus.PULL_NORMAL) {
            if (mOnPullDownBackListener != null) {
                mOnPullDownBackListener.onPullDownBack();
            }
        }

        mPullStatus = pullStatus;
        if (mOnHeaderViewStateChangeListener != null) {
            mOnHeaderViewStateChangeListener.onStateChange(
                    mHeaderView.getChildAt(0), mFooterView, mPullStatus);
        }
    }

    /**
     * 设置状态，获取更多
     */
    public void setObtainingMoreStatus() {
        setPullStatus(EPullStatus.MORE_REFRESHING);
    }

    // @Override
    // public boolean onInterceptTouchEvent(MotionEvent ev) {
    // // TODO Auto-generated method stub
    // Log.i("aa", "linear--onInterceptTouchEvent");
    // super.onInterceptTouchEvent(ev);
    // return true;
    // }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else {
            super.computeScroll();
        }
    }

    /**
     * listview发生滚动时的接口调用
     */
    private final OnScrollListener ON_LISTVIEW_SCROLL_LISTENER = new OnScrollListener() {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            UtilsLog.i(TAG, "firstVisibleItem:" + firstVisibleItem
                    + ",visibleItemCount" + visibleItemCount
                    + ",totalItemCount" + totalItemCount);
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    };

    /**
     * 当触摸listview时的接口调用
     */
    private final OnTouchListener ON_LISTVIEW_TOUCH_LISTENER = new OnTouchListener() {
        private float mPreMotionY = -1;
        private float mFirstMotionY = -1; // 记录用户首次触摸屏幕时的位置

        private int mPointPosition = -1; // 被点击的item

        private final int FALSE = 0;
        private final int TRUE = 1;
        private final int OTHER = 2;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (false == mPullDownEnabled
                    || EPullStatus.MORE_REFRESHING == mPullStatus
                    || mListView.getCount() == 0
                    || EPullStatus.PULL_REFRESHING == mPullStatus) {
                return false;
            }

            if (mOnTouchViewListener != null) {
                mOnTouchViewListener.onTouch(v, event);
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    performActionDown(event);
                    break;
                case MotionEvent.ACTION_MOVE: {
                    switch (performActionMove(event)) {
                        case FALSE:
                            return false;
                        case TRUE:
                            return true;
                        case OTHER:
                            break;
                    }

                }
                break;
                case MotionEvent.ACTION_UP: // 当手势离开屏幕时
                    performActionUp();
                    break;
            }

            return false;
        }

        private void performActionDown(MotionEvent event) {
            if (mListView.getCount() <= 0) {
                return;
            }

            mPreMotionY = event.getRawY();
            mFirstMotionY = mPreMotionY;
            mMoved = false;
            mPointPosition = mListView.pointToPosition((int) event.getX(),
                    (int) event.getY());
            if (mPointPosition != ListView.INVALID_POSITION
                    && mOnTouchDownUpListener != null) {
                mOnTouchDownUpListener.onTouchDown(mPointPosition);
            }
        }

        private void performActionUp() {
            if (mOnTouchDownUpListener != null
                    && mPointPosition != ListView.INVALID_POSITION) {
                mOnTouchDownUpListener.OnTouchUp(mPointPosition);
            }
            mPreMotionY = -1;
            mPointPosition = -1;

            if (mPullStatus == EPullStatus.PULL_RELEASE) {

                setPullStatus(EPullStatus.PULL_REFRESHING);
                preparePullRefresh();

                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }

            if (mPullStatus == EPullStatus.PULL_DOWN) {

                setPullStatus(EPullStatus.PULL_NORMAL);

                resetHeader();
            }
        }

        /**
         * 是否需要滚动
         *
         * @param deltaY
         * @return
         */
        boolean needScroll(int deltaY) {
            return (deltaY > mTouchSlop);
        }

        private int performActionMove(MotionEvent event) {
            if (mListView == null) {
                return FALSE;
            }
            mFirstVisibleItem = mListView.getFirstVisiblePosition();

            int position = mListView.pointToPosition((int) event.getX(),
                    (int) event.getY());
            if (needScroll((int) Math.abs(event.getRawY() - mFirstMotionY))) {
                if (mOnTouchDownUpListener != null
                        && position != ListView.INVALID_POSITION) {
                    mOnTouchDownUpListener.OnTouchUp(position);
                }
            }

            int yPos = getScrollY();

            // 部分手机不会接收到MotionEvent.ACTION_DOWN消息
            if (mPreMotionY == -1) {
                mPreMotionY = event.getRawY();
                mFirstMotionY = mPreMotionY;
                mPointPosition = position;
                return TRUE;
            }

            if (EPullStatus.PULL_NORMAL == mPullStatus) {
                if (mPreMotionY >= event.getRawY()
                        || mFirstVisibleItem != 0
                        || (mListView.getChildAt(0) != null && mListView
                        .getChildAt(0).getTop() != 0)) {
                    return FALSE;
                }
            }

            // 如果listview处于下拉状态时
            if (mPullStatus == EPullStatus.PULL_DOWN
                    || mPullStatus == EPullStatus.PULL_RELEASE
                    || mPullStatus == EPullStatus.PULL_NORMAL) {
                if (Math.abs(event.getRawY() - mFirstMotionY) > mTouchSlop) {
                    mMoved = true;
                    if (mPointPosition != ListView.INVALID_POSITION) {
                        View view = mListView.getChildAt(mPointPosition
                                - mFirstVisibleItem);
                        mListView.setPressed(false);
                        if (view != null) {
                            view.setPressed(false);
                        }

                        mListView.requestDisallowInterceptTouchEvent(true);
                    }
                }

                int listViewHeight = mListView.getHeight();
                double division = ((double) (listViewHeight))
                        / (listViewHeight - 2 * (-yPos)) * 1.2;

                int y = (int) ((event.getRawY() - mPreMotionY) / division);
                if (-yPos + y < 0) {
                    y = yPos;
                }
                scrollBy(0, -y);
                mPreMotionY = event.getRawY();

                // 设置新的状态
                if (-getScrollY() >= mHeaderView.getHeight()) {
                    setPullStatus(EPullStatus.PULL_RELEASE);
                } else {
                    setPullStatus(EPullStatus.PULL_DOWN);
                }

                return TRUE;
            }

            return OTHER;
        }

    };

    private final AdapterView.OnItemClickListener ON_ITEM_CLICK_LISTENER = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            if (mMoved == true) {
                mMoved = false;
                return;
            }

            final int FOOTER_VIEW_COUNT_INSIDE = 1;
            final int obtainMorePosition = mListView.getCount()
                    - mListView.getFooterViewsCount()
                    + FOOTER_VIEW_COUNT_INSIDE - 1;

            if (arg2 == obtainMorePosition
                    && mListView.getFooterViewsCount() > 0
                    && mListView.getCount() > 0) {
                if (mOnObtainMoreListener != null
                        && mPullStatus == EPullStatus.PULL_NORMAL) {
                    setPullStatus(EPullStatus.MORE_REFRESHING);
                    mOnObtainMoreListener.onObtainMore(arg1);
                }
            } else {
                if (mPullStatus == EPullStatus.PULL_DOWN
                        || mPullStatus == EPullStatus.PULL_RELEASE) {
                    return;
                }
                if (mPullDownViewOnItemClickListener != null) {
                    mPullDownViewOnItemClickListener.onItemClick(arg0, arg1,
                            arg2, arg3);
                }
            }
        }
    };

    private final AdapterView.OnItemLongClickListener ON_ITEM_LONG_CLICK_LISTENER = new AdapterView.OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            if (mMoved == true) {
                mMoved = false;
                return false;
            }

            final int FOOTER_VIEW_COUNT_INSIDE = 1;
            final int obtainMorePosition = mListView.getCount()
                    - mListView.getFooterViewsCount()
                    + FOOTER_VIEW_COUNT_INSIDE - 1;

            if (position == obtainMorePosition
                    && mListView.getFooterViewsCount() > 0
                    && mListView.getCount() > 0) {
                if (mOnObtainMoreListener != null
                        && mPullStatus == EPullStatus.PULL_NORMAL) {
                    setPullStatus(EPullStatus.MORE_REFRESHING);
                    mOnObtainMoreListener.onObtainMore(view);
                }
            } else {
                if (mPullStatus == EPullStatus.PULL_DOWN
                        || mPullStatus == EPullStatus.PULL_RELEASE) {
                    return false;
                }
                if (mPullDownViewOnItemLongClickListener != null) {
                    mPullDownViewOnItemLongClickListener.onItemLongClick(
                            parent, view, position, id);
                    return true;
                }
            }
            return false;
        }
    };

    private final OnHeaderViewStateChangeListener ON_HEADER_VIEW_STATE_CHANGE_LISTENER = new OnHeaderViewStateChange();

    private class OnHeaderViewStateChange implements
            OnHeaderViewStateChangeListener {
        // 动画效果
        private RotateAnimation mFlipAnimation; // 变为向下的箭头
        private RotateAnimation mReverseFlipAnimation; // 变为逆向的箭头

        public OnHeaderViewStateChange() {
            // 初始化动画
            mFlipAnimation = new RotateAnimation(0, -180,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            mFlipAnimation.setInterpolator(new LinearInterpolator());
            mFlipAnimation.setDuration(250);
            mFlipAnimation.setFillAfter(true);

            mReverseFlipAnimation = new RotateAnimation(-180, 0,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                    RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
            mReverseFlipAnimation.setDuration(250);
            mReverseFlipAnimation.setFillAfter(true);
        }

        @Override
        public void onStateChange(View headView, View footerView,
                                  EPullStatus status) {
            TextView textView = (TextView) headView
                    .findViewById(R.id.status_info);
            ProgressBar progressBar = (ProgressBar) headView
                    .findViewById(R.id.refresh_progress);
            ImageView imageView = (ImageView) headView
                    .findViewById(R.id.status_img);

            if (status == PullDownView.EPullStatus.PULL_DOWN) {
                textView.setText(mStrHeaderStatusInfo[0]);
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);

                imageView.setImageResource(mArrowIcon);
                // imageView.clearAnimation();
                // imageView.startAnimation(mReverseFlipAnimation);
            } else if (status == PullDownView.EPullStatus.PULL_RELEASE) {
                textView.setText(mStrHeaderStatusInfo[1]);

                imageView.setImageResource(mArrowIcon1);
                // imageView.clearAnimation();
                // imageView.startAnimation(mFlipAnimation);

            } else if (status == PullDownView.EPullStatus.PULL_REFRESHING) {
                textView.setText(mStrHeaderStatusInfo[2]);

                imageView.setImageResource(R.drawable.animation2);
                AnimationDrawable animationDrawable = (AnimationDrawable) imageView
                        .getDrawable();
                animationDrawable.start();
                // imageView.setVisibility(View.GONE);
                // imageView.setImageDrawable(null);
                // progressBar.setVisibility(View.VISIBLE);

                // 关闭更多按钮的事件响应
                if (null != footerView) {
                    footerView.setClickable(false);
                }
            } else if (status == PullDownView.EPullStatus.MORE_REFRESHING) {
                footerView.findViewById(R.id.refresh_progress)
                        .setVisibility(View.VISIBLE);
            }
        }

    }

    public interface OnRefreshListener {

        void onRefresh();
    }

    public interface OnHeaderViewStateChangeListener {

        void onStateChange(View headView, View footerView,
                           EPullStatus status);
    }

    public interface OnObtainMoreListener {

        void onObtainMore(View footerView);
    }

    public interface OnItemClickListener {

        void onItemClick(AdapterView<?> parent, View view, int position,
                         long id);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClick(AdapterView<?> parent, View view,
                                int position, long id);
    }

    public interface OnTouchViewListener {
        boolean onTouch(View v, MotionEvent event);
    }

    /**
     * 用于监听touch事件的开始和结束 Class description
     */
    public interface OnTouchDownUpListener {
        void onTouchDown(int position);

        void OnTouchUp(int position);
    }

    /**
     * 监听开始下拉的listener
     */
    public interface OnPullDownListener {
        void startPullDown();
    }

    /**
     * 当HeaderView回弹到初始位置时触发
     */
    public interface OnPullDownBackListener {
        void onPullDownBack();
    }

}
