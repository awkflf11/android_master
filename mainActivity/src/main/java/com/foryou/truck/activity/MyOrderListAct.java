package com.foryou.truck.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.R;
import com.foryou.truck.datamodel.OrderListDataModel;
import com.foryou.truck.entity.OrderListEntity;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.PtrListView;
import com.foryou.truck.view.viewPager.ViewPagerWithTab;
import com.foryou.truck.viewholds.OrderListItemVH;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.list.PagedListViewDataAdapter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.loadmore.LoadMoreListViewContainer;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by dubin on 16/7/16.
 */
public class MyOrderListAct extends BaseActivity {

    ViewPagerWithTab mMyViewPager;
    String TAG = "MyOrderListAct";
    private List<OrderListDataModel> mOrderListDataModelList = new ArrayList<OrderListDataModel>();
    private List<PtrFrameLayout> mPtrFrameLayoutList = new ArrayList<PtrFrameLayout>();
    private List<LoadMoreListViewContainer> mLoadMoreContainerList = new ArrayList<LoadMoreListViewContainer>();
    private List<PagedListViewDataAdapter> mPageListViewAdapterList = new ArrayList<PagedListViewDataAdapter>();

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.order_list_with_tab);
    }

    private PtrFrameLayout getPtrFrameLayout(int index) {
        final OrderListDataModel mDataModel = new OrderListDataModel(10, this, TAG, index);

        final PagedListViewDataAdapter mPageListAdapter = new PagedListViewDataAdapter<OrderListEntity.OrderContent>();
        // pull to refresh
        final PtrFrameLayout mPtrFrameLayout = (PtrFrameLayout) LayoutInflater.from(this)
                .inflate(R.layout.ptr_loadmore, null);

        final PtrListView mListView = (PtrListView) mPtrFrameLayout.findViewById(R.id.listview);
        final LoadMoreListViewContainer loadMoreListViewContainer = (LoadMoreListViewContainer) mPtrFrameLayout.findViewById(R.id.load_more_list_view_container);

        mOrderListDataModelList.add(mDataModel);
        mPtrFrameLayoutList.add(mPtrFrameLayout);
        mLoadMoreContainerList.add(loadMoreListViewContainer);
        mPageListViewAdapterList.add(mPageListAdapter);

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                // here check list view, not content.
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mDataModel.queryFirstPage();
            }
        });

        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        mPageListAdapter.setViewHolderClass(this, OrderListItemVH.class, index);
        mPageListAdapter.setListPageInfo(mDataModel.getListPageInfo());

        // load more container
        loadMoreListViewContainer.useDefaultHeader();

        // binding view and data
        mListView.setAdapter(mPageListAdapter);
        loadMoreListViewContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                mDataModel.queryNextPage();
            }
        });
        showProgressDialog();
        mDataModel.queryFirstPage();
        return mPtrFrameLayout;
    }

    private void initViewPagerWithTab() {
        setTitle("我的运单");
        mMyViewPager = (ViewPagerWithTab) findViewById(R.id.viewpager_withtab);
        // 判断，读本地配置信息。
        mMyViewPager
                .setTabText(new String[]{"询价中", "已下单", "已失效"}, 0);

        List<View> mViewList = new ArrayList<View>();
        for (int i = 0; i < 3; i++) {
            mViewList.add(getPtrFrameLayout(i));
        }
        mMyViewPager.setAdapter(mViewList);
    }

    @Subscribe
    public void onEvent(String msg){
        UtilsLog.i(TAG, "msg:" + msg);
        cancelProgressDialog();
        try {
            // ptr
            mPtrFrameLayoutList.get(Integer.valueOf(msg)).refreshComplete();
            // load more
            mLoadMoreContainerList.get(Integer.valueOf(msg)).loadMoreFinish(mOrderListDataModelList
                    .get(Integer.valueOf(msg)).getListPageInfo().isEmpty(), mOrderListDataModelList
                    .get(Integer.valueOf(msg)).getListPageInfo().hasMore());
            mPageListViewAdapterList.get(Integer.valueOf(msg)).notifyDataSetChanged();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        initViewPagerWithTab();
    }

    @Override
    public void onClickListener(int id) {

    }
}
