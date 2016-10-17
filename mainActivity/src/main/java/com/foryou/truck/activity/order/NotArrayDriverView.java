package com.foryou.truck.activity.order;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foryou.truck.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by dubin on 16/7/23.
 */
public class NotArrayDriverView extends LinearLayout{

    private TextView mCuiChuBtn;
    public static final String CUICHU_ARRAY_DRIVER = "urge_array_driver";

    public NotArrayDriverView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private View.OnClickListener mListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(CUICHU_ARRAY_DRIVER);
        }
    };

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCuiChuBtn = (TextView)findViewById(R.id.cuichu_array);
        mCuiChuBtn.setOnClickListener(mListener);
    }
}
