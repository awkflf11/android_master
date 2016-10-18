package com.foryou.truck.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.HomeMainScreenActivity;
import com.foryou.truck.R;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.UtilsLog;

/**
 * @des:新手引导
 */
public class newGuideBeginAct extends BaseActivity {
    private String TAG = "newGuideBeginAct";
    @BindView(id = R.id.middle_btn, click = true)
    private Button beginBtn;//

    @Override
    public void setRootView() {
        super.setRootView();
        setContentView(R.layout.act_new_guidebegin);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClickListener(int id) {
        Intent intent;
        switch (id) {
            case R.id.middle_btn:
                SharePerfenceUtil.SetFirstNewGuide(this, false);
                intent = new Intent(this, HomeMainScreenActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }


}
