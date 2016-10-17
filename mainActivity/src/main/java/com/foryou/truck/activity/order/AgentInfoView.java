package com.foryou.truck.activity.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foryou.truck.R;
import com.foryou.truck.entity.OrderDetailEntity;

/**
 * Created by dubin on 16/7/23.
 */
public class AgentInfoView extends LinearLayout {

    private TextView mOrderSn;
    private TextView mKey;
    private String[] KeyText = {"", ""};


    public AgentInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View agentTitle = LayoutInflater.from(getContext()).inflate(R.layout.order_detail_cell_title_info, null);
        TextView title = (TextView)agentTitle.findViewById(R.id.key);
        title.setText("经纪人信息");

        addView(agentTitle);

    }

    public void initData(OrderDetailEntity.OrderDetail orderDetail) {

    }
}
