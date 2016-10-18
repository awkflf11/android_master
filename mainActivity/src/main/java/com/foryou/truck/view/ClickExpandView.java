package com.foryou.truck.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.foryou.truck.R;

/**
 * @des:点击可展开和伸缩的view
 */
public class ClickExpandView extends RelativeLayout {
    private String TAG = "ClickExpandView";
    private TextView contentTv;
    private ImageView clickIv;
    int[] images={R.drawable.icon_down,R.drawable.icon_up};

    boolean isExpand=false;//默认是不展开的

    public ClickExpandView(Context context) {
        super(context);
    }
    public ClickExpandView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentTv = (TextView) findViewById(R.id.content_tv);
        clickIv = (ImageView) findViewById(R.id.switch_icon);
    }

    public void setIsExpand(boolean isExpand ){
        this.isExpand=isExpand;
        if(isExpand){
            contentTv.setText("收起");
            clickIv.setBackgroundResource(images[1]);
        }else{
            contentTv.setText("详情");
            clickIv.setBackgroundResource(images[0]);
        }

    }

    public Boolean getIsExand(){
        return isExpand;
    }



    public void initData( ) {

    }
}
