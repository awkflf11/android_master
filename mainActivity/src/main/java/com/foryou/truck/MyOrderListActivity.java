package com.foryou.truck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.OrderListEntity.OrderContent;
import com.foryou.truck.parser.OrderListJsonParser;
import com.foryou.truck.sendproduct.SendProductActivity;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.MutiTabChoose;
import com.foryou.truck.view.MutiTabChoose.TabClickListener;
import com.foryou.truck.view.PullDownView;
import com.foryou.truck.view.PullDownView.OnRefreshListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @des:我的运单列表
 */
public class MyOrderListActivity extends BaseActivity {
    private String TAG = "MyOrderListActivity";
    private Context mContext;
    @BindView(id = R.id.tab_layout)
    private MutiTabChoose mMutiTab;
    // private ListView listView;
    @BindView(id = R.id.vPager)
    private ViewPager mViewPager;
    private MyViewPagerAdapter mViewPageAdapter;
    private MutiTabChoose mMutiTabChoose;
    int tabIndex;

    @Override
    public void setRootView() {
        // TODO Auto-generated method stub
        super.setRootView();
        setContentView(R.layout.my_order_list);
    }

    private class MyViewPagerAdapter extends PagerAdapter {
        public List<PullDownView> mPullDownViewList;
        private List<MyListViewAdapter> mListAdapter;
        private int[] PageIndexArray;
        private Boolean[] haveLoader;
        public MyViewPagerAdapter(List<PullDownView> list,
                                  List<MyListViewAdapter> adapterlist) {
            mPullDownViewList = list;
            mListAdapter = adapterlist;
            PageIndexArray = new int[3];
            PageIndexArray[0] = 1;
            PageIndexArray[1] = 1;
            PageIndexArray[2] = 1;
            haveLoader = new Boolean[3];
            for (int i = 0; i < 3; i++) {
                haveLoader[i] = false;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mPullDownViewList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mPullDownViewList.get(position), 0);
            return mPullDownViewList.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        public PullDownView getPullDownListView() {
            return mPullDownViewList.get(mViewPager.getCurrentItem());
        }

        public ListView getListView() {
            return mPullDownViewList.get(mViewPager.getCurrentItem())
                    .getListView();
        }

        public MyListViewAdapter getAdpater() {
            return mListAdapter.get(mViewPager.getCurrentItem());
        }

        public List<Map<String, Object>> getDataList() {
            return getAdpater().dataList;
        }

        public void setIsReflesh(boolean flag) {
            getAdpater().IsReflesh = flag;
        }

        public boolean IsReflesh() {
            return getAdpater().IsReflesh;
        }

        public void reSetPageIndex() {
            PageIndexArray[mViewPager.getCurrentItem()] = 1;
            haveLoader[mViewPager.getCurrentItem()] = true;
        }

        public boolean haveLoader() {
            return haveLoader[mViewPager.getCurrentItem()];
        }

        public void SetPageIndexAddOne() {
            PageIndexArray[mViewPager.getCurrentItem()] = PageIndexArray[mViewPager
                    .getCurrentItem()] + 1;
            setIsReflesh(true);
        }

        public int getPageIndex() {
            return PageIndexArray[mViewPager.getCurrentItem()];
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    List<MyListViewAdapter> mAdapterList = new ArrayList<MyListViewAdapter>();

    private PullDownView getPullDownListView(int index) {
        LayoutInflater lf = LayoutInflater.from(mContext);
        PullDownView pullDownView = (PullDownView) lf.inflate(
                R.layout.pull_down_view, null);
        pullDownView.init();
        pullDownView.setFooterView(R.layout.footer_item);
        pullDownView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mViewPageAdapter.mPullDownViewList.get(mViewPager
                        .getCurrentItem()) != null) {
                    getOrderList(false);
                }
            }
        });
        pullDownView.showFooterView(false);
        ListView listView = pullDownView.getListView();
        List<Map<String, Object>> adapterlist = new ArrayList<Map<String, Object>>();
        MyListViewAdapter mAdapter = new MyListViewAdapter(mContext,
                adapterlist, R.layout.query_list_item, new String[]{
                "order_number", "order_status", "send_time",
                "product_name", "start_place", "end_place",
                "baojia_layout"}, new int[]{R.id.order_number,
                R.id.order_status, R.id.send_time, R.id.product_name,
                R.id.start_place, R.id.end_place, R.id.baojia_layout},
                false);

        listView.setAdapter(mAdapter);
        mAdapterList.add(mAdapter);

        initListViewClickEvent(mAdapterList);
        return pullDownView;
    }

    public void initListViewClickEvent(List<MyListViewAdapter> mListAdapter){
        for (int i = 0; i < mListAdapter.size(); i++) {
            MyListViewAdapter adapter = mListAdapter.get(i);
            adapter.setOnClickViewLisener(R.id.content_item, new MyListViewAdapter.CallBacks() {
                @Override
                public void onViewClicked(int position, int viewId) {
                    if (position >= mViewPageAdapter.getDataList().size()) {
                        ToastUtils.toast(mContext, "正在加载数据，请稍候再试");
                        return;
                    }
                    Intent intent;
                    String agent_id = (String) mViewPageAdapter
                            .getDataList().get(position).get("agent_id");

                    String mOrderId = (String) mViewPageAdapter
                            .getDataList().get(position)
                            .get("order_id");
                    String mOrderSn = (String) mViewPageAdapter
                            .getDataList().get(position)
                            .get("order_number");
                    if (mViewPager.getCurrentItem() == 0) {

                        String mBaojiaNum = (String) mViewPageAdapter
                                .getDataList().get(position)
                                .get("quote_num");

                        String mOrderStatusKey = (String) mViewPageAdapter
                                .getDataList().get(position)
                                .get("order_status_key");
                        if (mOrderStatusKey.equals("10")) { // 未成单待支付
                            intent = new Intent(mContext,
                                    OrderDetailActivity.class);
                        } else {
                            intent = new Intent(mContext,
                                    AgentAndQuoteDetailAct.class);
                        }
                        if(mOrderStatusKey.equals("7")){
                            intent.putExtra("quote_detail", true);
                        }else{
                            intent.putExtra("quote_detail", false);
                        }
//							if (!TextUtils.isEmpty(mBaojiaNum)
//									&& TextUtils.isDigitsOnly(mBaojiaNum) // 跳到询价详情
//									&& Integer.valueOf(mBaojiaNum) > 0) {
//                        intent.putExtra("quote_detail", false);
//							} else {
//								intent.putExtra("quote_detail", true);
//							}
                        intent.putExtra("order_id", mOrderId);
                        intent.putExtra("order_sn", mOrderSn);
                        startActivity(intent);

                    } else if (mViewPager.getCurrentItem() == 1) {
                        intent = new Intent(mContext,
                                OrderDetailActivity.class);
                        intent.putExtra("order_id",
                                (String) mViewPageAdapter.getDataList()
                                        .get(position).get("order_id"));
                        startActivity(intent);
                    } else if (mViewPager.getCurrentItem() == 2) {
                        if (agent_id.equals("0")) {// 跳到询价详情
                            intent = new Intent(mContext,
                                    AgentAndQuoteDetailAct.class);
                            intent.putExtra("quote_detail", true);
                        } else {
                            intent = new Intent(mContext,
                                    OrderDetailActivity.class);
                        }
                        intent.putExtra("order_id",
                                mOrderId);
                        intent.putExtra("order_sn",
                                mOrderSn);
                        startActivity(intent);
                    }
                }
            });

        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext, HomeMainScreenActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setTitle("我的运单");
        mContext = this;
        ShowBackView();

        mMutiTab.setTabText(new String[]{"询价中", "已下单", "已失效"});
        mMutiTab.setOnTabClickListener(new TabClickListener() {

            @Override
            public boolean tabClicked(int index) {
                // TODO Auto-generated method stub
                mViewPager.setCurrentItem(index);
                if (index == 0) {
                    TongjiModel.addEvent(mContext, "我的运单",
                            TongjiModel.TYPE_BUTTON_CLIKC, "询价中");
                } else if (index == 1) {
                    TongjiModel.addEvent(mContext, "我的运单",
                            TongjiModel.TYPE_BUTTON_CLIKC, "已下单");
                } else {
                    TongjiModel.addEvent(mContext, "我的运单",
                            TongjiModel.TYPE_BUTTON_CLIKC, "已失效");
                }

                return true;
            }

        });

        List<PullDownView> pullDownViewList = new ArrayList<PullDownView>();
        for (int i = 0; i < 3; i++) {
            pullDownViewList.add(getPullDownListView(i));
        }
        mViewPageAdapter = new MyViewPagerAdapter(pullDownViewList,
                mAdapterList);
        mViewPager.setAdapter(mViewPageAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(mPagerListener);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        int tabIndex = SharePerfenceUtil.getOrderListTabIndex(mContext);
        if (tabIndex != -1 && tabIndex >= 0 && tabIndex <= 2) {
            mViewPager.setCurrentItem(tabIndex);
            SharePerfenceUtil.saveOrderListTabIndex(mContext, -1);
        }
        getOrderList(false);
    }

    private OnPageChangeListener mPagerListener = new OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            mMutiTab.chooseTab(arg0);
            //if (!mViewPageAdapter.haveLoader()) {
            getOrderList(false);
            //}
        }

    };

    private void InitData() {
        // adapterlist.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < mOrderListParser.entity.data.list.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            OrderContent mContent = mOrderListParser.entity.data.list.get(i);
            map.put("order_number", "" + mContent.order_sn);
            map.put("order_id", mContent.id);
            map.put("order_status", mContent.status.text);
            map.put("order_status_key", mContent.status.key);
            map.put("send_time", mContent.goods_loadtime + " "
                    + mContent.goods_loadtime_desc);
            map.put("product_name", mContent.goods_name);
            map.put("start_place", mContent.start_place_short);
            map.put("end_place", mContent.end_place_short);
            map.put("quote_num", "" + mContent.quote_num);
            map.put("quote_min", "" + mContent.quote_min);//最低报价￥0.1
            map.put("agent_id", mContent.agent_id);

            if (mViewPager.getCurrentItem() != 0 // 未成单，待支付,等待审核
                    || mContent.status.key.equals("10")
                    || mContent.status.key.equals("7")) {
                map.put("baojia_layout", false);
            } else {
                    map.put("baojia_layout", true);
                    if(!TextUtils.isDigitsOnly(mContent.quote_num)){
                        map.put("quote_num", "0");
                    }
            }
            mViewPageAdapter.getDataList().add(map);
        }
        mViewPageAdapter.getAdpater().notifyDataSetChanged();

        if (mOrderListParser.entity.data.list.size() == 0
                && !mViewPageAdapter.getAdpater().IsReflesh) {
            mViewPageAdapter.getPullDownListView().showNoDataView(0, "暂无运单"
                    , "立即发货", mSendProListener);
        } else {
            mViewPageAdapter.getPullDownListView().HideNoContentView();
        }
    }

    private View.OnClickListener mSendProListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(mContext, SendProductActivity.class);
            startActivity(intent);
        }

    };

    OnScrollListener scorllListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            // TODO Auto-generated method stub
            Log.i(TAG, "firstVisibleItem" + firstVisibleItem
                    + "visibleItemCount" + visibleItemCount + "totalItemCount"
                    + totalItemCount);
            if (((firstVisibleItem + visibleItemCount) == totalItemCount)
                    && !mViewPageAdapter.IsReflesh()) {
                getOrderList(true);
            } else {

            }
        }

    };

    private OrderListJsonParser mOrderListParser;
    private int PAGE_SIZE = 5;

    private void getOrderList(boolean getMore) {
        showProgressDialog();
        if (getMore) {
            mViewPageAdapter.SetPageIndexAddOne();
        } else {
            mViewPageAdapter.reSetPageIndex();
        }
        Map<String, String> parmams = new HashMap<String, String>();
        parmams.put("page", "" + mViewPageAdapter.getPageIndex());
        parmams.put("flag", "" + (mViewPager.getCurrentItem() + 1));
        String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
                 + "/order/list", parmams);

        final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
                mContext, Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        UtilsLog.i(TAG, "response:" + response);
                        cancelProgressDialog();
                        mViewPageAdapter.mPullDownViewList.get(
                                mViewPager.getCurrentItem())
                                .notifyRefreshComplete();
                        OrderListJsonParser mParser = new OrderListJsonParser();
                        int result = mParser.parser(response);
                        if (result == 1) {
                            if (mParser.entity.status.equals("Y")) {
                                mOrderListParser = mParser;
                                if (!mViewPageAdapter.getAdpater().IsReflesh) {// 正常刷新加载
                                    mViewPageAdapter.getDataList().clear();
                                    InitData();
                                } else {// 加载更多
                                    InitData();
                                    mViewPageAdapter.setIsReflesh(false);
                                }

                                if (mOrderListParser.entity.data.list.size() == PAGE_SIZE) {
                                    mViewPageAdapter
                                            .getListView()
                                            .setOnScrollListener(scorllListener);
                                    mViewPageAdapter.getPullDownListView()
                                            .showFooterView(true);
                                } else {
                                    mViewPageAdapter.getListView()
                                            .setOnScrollListener(null);
                                    mViewPageAdapter.getPullDownListView()
                                            .showFooterView(false);
                                }
                            } else {
                                mViewPageAdapter.getListView()
                                        .setOnScrollListener(null);
                                mViewPageAdapter.getPullDownListView()
                                        .showFooterView(false);
                                if(mOrderListParser!=null&&mOrderListParser.entity!=null&&mOrderListParser.entity.msg!=null){
                                    ToastUtils.toast(mContext, mOrderListParser.entity.msg);
                                }
                            }

                        } else {
                            Log.i(TAG, "/order/list 解析错误");
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
                mViewPageAdapter.mPullDownViewList.get(
                        mViewPager.getCurrentItem())
                        .notifyRefreshComplete();
                cancelProgressDialog();

                //mViewPageAdapter.getPullDownListView().showNoNetWorkView();
            }

        }, true) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = super.getParams();
                return map;
            }
        };

        MyApplication.getInstance().addRequest(stringRequest, TAG);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        MyApplication.getInstance().cancelAllRequests(TAG);
    }

    @Override
    public void onClickListener(int id) {
        // TODO Auto-generated method stub

    }
}
