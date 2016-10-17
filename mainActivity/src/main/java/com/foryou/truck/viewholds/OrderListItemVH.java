package com.foryou.truck.viewholds;

import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.AgentAndQuoteDetailAct;
import com.foryou.truck.OrderDetailActivity;
import com.foryou.truck.R;
import com.foryou.truck.entity.OrderListEntity;

import in.srain.cube.views.list.ViewHolderBase;

/**
 * Created by dubin on 16/7/16.
 */
public class OrderListItemVH extends ViewHolderBase<OrderListEntity.OrderContent> {

    private int mCurrentIndex;
    private TextView mStartPlace, mEndPlace, mSendTime;
    private TextView mOrderStatus, mOrderNumber, mProductName;
    private TextView mBaojiaText;
    private RelativeLayout mBaojiaLayout;
    TextView mBaojia;
    TextView mWuBaojia;
    ImageView mFanhuiArrow;
    OrderListEntity.OrderContent mDataItem;

    public OrderListItemVH(Integer index) {//
        mCurrentIndex = index;
    }

    private View.OnClickListener mItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            String agent_id = mDataItem.agent_id;
            String mOrderId = mDataItem.id;
            String mOrderSn = mDataItem.order_sn;
            if (mCurrentIndex == 0) {
                String mBaojiaNum = mDataItem.quote_num;
                String mOrderStatusKey = mDataItem.status.key;
                if (mOrderStatusKey.equals("10")) { // 未成单待支付
                    intent = new Intent(v.getContext(),
                            OrderDetailActivity.class);
                } else {
                    intent = new Intent(v.getContext(),
                            AgentAndQuoteDetailAct.class);
                }
                if (mOrderStatusKey.equals("7")) {
                    intent.putExtra("quote_detail", true);
                } else {
                    intent.putExtra("quote_detail", false);
                }
                intent.putExtra("order_id", mOrderId);
                intent.putExtra("order_sn", mOrderSn);
                v.getContext().startActivity(intent);

            } else if (mCurrentIndex == 1) {
                intent = new Intent(v.getContext(),
                        OrderDetailActivity.class);
                intent.putExtra("order_id",
                        mDataItem.id);
                v.getContext().startActivity(intent);
            } else if (mCurrentIndex == 2) {
                if (agent_id.equals("0")) {// 跳到询价详情
                    intent = new Intent(v.getContext(),
                            AgentAndQuoteDetailAct.class);
                    intent.putExtra("quote_detail", true);
                } else {
                    intent = new Intent(v.getContext(),
                            OrderDetailActivity.class);
                }
                intent.putExtra("order_id",
                        mOrderId);
                intent.putExtra("order_sn",
                        mOrderSn);
                v.getContext().startActivity(intent);
            }
        }
    };

    @Override
    public View createView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.query_list_item, null);
        mStartPlace = (TextView) v.findViewById(R.id.start_place);
        mEndPlace = (TextView) v.findViewById(R.id.end_place);
        mSendTime = (TextView) v.findViewById(R.id.send_time);
        mOrderStatus = (TextView) v.findViewById(R.id.order_status);
        mOrderNumber = (TextView) v.findViewById(R.id.order_number);
        mProductName = (TextView) v.findViewById(R.id.product_name);
        mBaojiaText = (TextView) v.findViewById(R.id.baojia_text);
        mBaojiaLayout = (RelativeLayout) v.findViewById(R.id.baojia_layout);
        mBaojia = (TextView) v.findViewById(R.id.baojia_text);
        mWuBaojia = (TextView) v.findViewById(R.id.wubaojia_text);
        mFanhuiArrow = (ImageView) v.findViewById(R.id.fanhui2);
        v.setOnClickListener(mItemListener);
        return v;
    }

    @Override
    public void showData(int position, OrderListEntity.OrderContent itemData) {
        mDataItem = itemData;

        mOrderStatus.setText(itemData.status.text);
        mOrderNumber.setText(itemData.order_sn);
        mStartPlace.setText(itemData.start_place_short);
        mEndPlace.setText(itemData.end_place_short);
        mSendTime.setText(itemData.goods_loadtime + " "
                + itemData.goods_loadtime_desc);
        mProductName.setText(itemData.goods_name);

        if (mCurrentIndex != 0
                || itemData.status.key.equals("10")
                || itemData.status.key.equals("7")) {
            mBaojiaLayout.setVisibility(View.GONE);
        } else {
            mBaojiaLayout.setVisibility(View.VISIBLE);
            int quoteN = 0;
            try {
                quoteN = Integer.valueOf(itemData.quote_num);
            } catch (Exception e) {
                quoteN = 0;
            }

            if ((!"2".equals(itemData.biddingType) && quoteN > 0) ||
                    ("2".equals(itemData.biddingType) && "0".equals(itemData.expireTime)
                            && quoteN > 0)) {
                mWuBaojia.setVisibility(android.view.View.GONE);
                mBaojia.setVisibility(android.view.View.VISIBLE);
                mFanhuiArrow.setVisibility(android.view.View.VISIBLE);
                String content;
                SpannableString msp;
                if ("2".equals(itemData.biddingType)) {
                    content = "集合竞价结束, 最低报价¥"
                            + itemData.quote_min;
                    msp = new SpannableString(content);
                } else {
                    content = "已有" + quoteN + "份经纪人报价, 最低报价¥"
                            + itemData.quote_min;

                    msp = new SpannableString(content);
                    int index0 = content.indexOf("份经纪人");
                    msp.setSpan(new ForegroundColorSpan(0xff00a9e0), 2,
                            index0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    msp.setSpan(new RelativeSizeSpan(1.4f), 2, index0,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                int index1 = content.indexOf("最低报价");
                msp.setSpan(new ForegroundColorSpan(0xffff6900), index1 + 4,
                        content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                msp.setSpan(new RelativeSizeSpan(1.4f), index1 + 4, content.length(),
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                mBaojia.setText(msp);
            } else {
                mWuBaojia.setVisibility(View.GONE);
                mBaojia.setVisibility(View.VISIBLE);
                if ("2".equals(itemData.biddingType)) {
                    if ("0".equals(itemData.expireTime)) {
                        mBaojia.setText("集合竞价结束,暂无经纪人报价");
                    } else {
                        mBaojia.setText("集合竞价中");
                    }
                } else {
                    mBaojia.setText("暂无经纪人报价");
                }
                mFanhuiArrow.setVisibility(android.view.View.GONE);
            }
        }

    }
}