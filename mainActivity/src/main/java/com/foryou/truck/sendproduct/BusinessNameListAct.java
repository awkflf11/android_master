package com.foryou.truck.sendproduct;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.R;
import com.foryou.truck.entity.BusinessListEntity;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.UtilsLog;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by dubin on 16/7/19.
 */
public class BusinessNameListAct extends BaseActivity {
    @BindView(id = R.id.content)
    LinearLayout mListView;

    ScrollView mScrollView;
    PtrClassicFrameLayout mPtrFrame;
    public static final int RESULT_CODE = 100;
    private String TAG = "BusinessNameListAct";

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.business_name_list);
    }

    private class MyOnClickListener implements View.OnClickListener{
        private BusinessListEntity.BusinessItem mDataItem;
        public MyOnClickListener(BusinessListEntity.BusinessItem data){
            mDataItem = data;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("businessItem",mDataItem);
            setResult(RESULT_CODE,intent);
            finish();
        }
    };

    private void initList() {
        BusinessListEntity.BusinessData data = SharePerfenceUtil.getBusinessList(this);
        if(data == null){
            UtilsLog.i(TAG,"businessList is null");
            return;
        }
        for (int i = 0; i < data.list.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.business_item_textview, null);
            TextView textview = (TextView) view.findViewById(R.id.textview);
            textview.setText(data.list.get(i).name);
            textview.setOnClickListener(new MyOnClickListener(data.list.get(i)));
            mListView.addView(view);
        }
    }

    private void initPtrFrame() {
        mScrollView = (ScrollView) findViewById(R.id.scrool_view);
        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.fragment_rotate_header_with_view_group_frame);
        mPtrFrame.disableWhenHorizontalMove(true);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mScrollView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPtrFrame.refreshComplete();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("业务名称");
        ShowBackView();

        initPtrFrame();
        initList();
    }

    @Override
    public void onClickListener(int id) {

    }
}
