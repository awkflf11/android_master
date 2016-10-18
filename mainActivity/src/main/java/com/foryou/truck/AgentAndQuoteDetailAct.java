package com.foryou.truck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.foryou.truck.util.BindView;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.MutiTabChoose.TabClickListener;
import com.foryou.truck.view.MutiTabChoose2;

public class AgentAndQuoteDetailAct extends BaseFragmentActivity {
	private static String TAG = "AgentAndQuoteDetailAct";
	@BindView(id = R.id.fragment)  
	private Fragment mFragment;
	@BindView(id = R.id.menu_layout)
	private MutiTabChoose2 mMutiTabChoose;
	public String OrderId, OrderSn;
	@BindView(id = R.id.title)
	private TextView mTitle;

	private Fragment mAgentListFragment = new AgentListActivity();
	private Fragment mOrderDetailFragment = new OrderDetailFragment();
	private int mCurrentFragmentIndex = -1;
	public boolean quote_list; // 查看询价详情，运单详情跳转过来
	public boolean quote_detail; //
	public boolean fromGT = false;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.agent_and_quote_detail);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		OrderId = getIntent().getStringExtra("order_id");
		OrderSn = OrderId; //getIntent().getStringExtra("order_sn");
		quote_list = getIntent().getBooleanExtra("quote_list", false);
		quote_detail = getIntent().getBooleanExtra("quote_detail", false);
		fromGT = getIntent().getBooleanExtra("fromGT",false);

		mMutiTabChoose.init(new String[] { "报价列表", "询价详情" });
		mMutiTabChoose.setOnTabClickListener(new TabClickListener() {
			@Override
			public boolean tabClicked(int index) {
				// TODO Auto-generated method stub
				if (index == 1) {
					changeFragment(1);
				} else {
					changeFragment(0);
				}
				return false;
			}
		});
		changeFragment(0);
		if (quote_list) {//
			mTitle.setText("报价列表");
			mTitle.setVisibility(android.view.View.VISIBLE);
			mMutiTabChoose.setVisibility(android.view.View.GONE);
			ShowBackView();
		} else if (quote_detail) {
			mTitle.setText("询价详情");
			mTitle.setVisibility(android.view.View.VISIBLE);
			mMutiTabChoose.setVisibility(android.view.View.GONE);
			UtilsLog.i(TAG, "询价详情1");
			changeFragment(1);
			ShowBackView();
		} else {
			mTitle.setVisibility(android.view.View.GONE);
			mMutiTabChoose.setVisibility(android.view.View.VISIBLE);
			ShowBackView();
		}

	}

	private void changeFragment(int index) {
		FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
		if (index == 0) {
			if (mCurrentFragmentIndex == 0) {
				return;
			}
			if (!mAgentListFragment.isAdded()) {
				fr.add(R.id.fragment, mAgentListFragment).commitAllowingStateLoss();
			} else {
				fr.hide(mOrderDetailFragment);
				fr.show(mAgentListFragment).commitAllowingStateLoss();
			}
			mCurrentFragmentIndex = 0;
		} else {
			if (mCurrentFragmentIndex == 1) {
				return;
			}

			if (!mOrderDetailFragment.isAdded()) {
				fr.add(R.id.fragment, mOrderDetailFragment).commitAllowingStateLoss();
			} else {
				fr.hide(mAgentListFragment);
				fr.show(mOrderDetailFragment).commitAllowingStateLoss();
			}
			mCurrentFragmentIndex = 1;
		}
	}

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(fromGT){
			Intent intent = new Intent(this,HomeMainScreenActivity.class);
			startActivity(intent);
		}
	}
}
