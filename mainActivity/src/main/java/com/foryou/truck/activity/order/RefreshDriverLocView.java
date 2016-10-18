package com.foryou.truck.activity.order;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.R;
import com.foryou.truck.entity.OrderDetailEntity;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by dubin on 16/7/23.
 */
public class RefreshDriverLocView extends RelativeLayout {

    private TextView mDriverName;
    private TextView mRefreshLoc;
    private OrderDetailEntity.OrderDetail mOrderDetailData;
    public static String REFRESH_LOC = "refresh_location";

    public RefreshDriverLocView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(REFRESH_LOC);
        }
    };


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDriverName = (TextView) findViewById(R.id.driver_name);
        mRefreshLoc = (TextView) findViewById(R.id.refresh_locate);
        mRefreshLoc.setOnClickListener(mListener);
    }

    public void initData(OrderDetailEntity.OrderDetail orderDetail) {
        mOrderDetailData = orderDetail;
        if (orderDetail.driver != null
                && TextUtils.isEmpty(orderDetail.driver.name)) {
            mDriverName.setText(orderDetail.driver.name);
        }else{
            setVisibility(View.GONE);
        }
    }
}
