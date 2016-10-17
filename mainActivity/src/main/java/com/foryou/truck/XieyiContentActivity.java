package com.foryou.truck;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foryou.truck.entity.AgreeMentEntity;
import com.foryou.truck.parser.AgreeMentJsonParser;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NetWorkUtils;

/**
 * @des:确认下单页面 和运单详情的页面 跳到的协议
 */
public class XieyiContentActivity extends BaseActivity {
	private String TAG = "XieyiContentActivity";
	private Context mContext;
	@BindView(id = R.id.xiyi_head_ll)
	private LinearLayout headLL;//协议的头部分
	@BindView(id = R.id.customerName)
	private TextView mCustomerName;
	@BindView(id = R.id.driverName)
	private TextView mDriverName;
	@BindView(id = R.id.driverIdCard)
	private TextView mDriverIdCard;
	@BindView(id = R.id.driverPlate)
	private TextView mDriverPlate;
	@BindView(id = R.id.driverMobile)
	private TextView mDriverMobile;
	@BindView(id = R.id.data1)
	private TextView mData1;
	@BindView(id = R.id.data2)
	private TextView mData2;
	@BindView(id = R.id.data3)
	private TextView mData3;
	@BindView(id = R.id.jiafang)
	private TextView mJiaFang;
	@BindView(id = R.id.yifang)
	private TextView mYiFang;
	private String order_id = "";//
	private int xiyiFlag;
	//
	@BindView(id = R.id.xiyi_title)
	private TextView xiYiTitle;
	@BindView(id = R.id.customerName_key)
	private TextView customerNameKey;
	@BindView(id = R.id.yiFangName_key)
	private TextView yiFangNameKey;
	@BindView(id = R.id.yiFangName)
	private TextView yiFangName;
	@BindView(id = R.id.bingFangName)
	private TextView bingFangName;//丙方的名字
	@BindView(id = R.id.yiFangContent_ll)
	private LinearLayout yiFangContentLL;
	@BindView(id = R.id.xieyiContent_tv)
	private TextView xieyiContentTv;
	@BindView(id = R.id.bottom_content_LL)
	private LinearLayout bottomContentLL;

	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.xieyi_content);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		order_id = getIntent().getStringExtra("order_id");
		ShowBackView();
		setTitle("三方协议");
		xiyiFlag=getIntent().getIntExtra("xiyi_flag",0);

	}

	private void InitData(AgreeMentJsonParser mAgreeMentParser) {
		AgreeMentEntity.AgreeMent agreeMent;
		if(mAgreeMentParser.entity.data!=null){
			agreeMent=mAgreeMentParser.entity.data;
		}else{
			return;
		}
        if(!TextUtils.isEmpty(agreeMent.tplType) && agreeMent.tplType.equals("2")){//2,盛丰物流, 1表示普通协议
			xiYiTitle.setText("货物运输三方协议书");
			customerNameKey.setText("甲方（货主）:");
			mCustomerName.setText("盛丰物流集团有限公司");
			yiFangNameKey.setText("乙方（物流经纪人）:");
			yiFangName.setText(agreeMent.agentName);
			yiFangContentLL.setVisibility(View.GONE);
			bingFangName.setText("丙方（承运人）：南京福佑在线电子商务有限公司");
			xieyiContentTv.setText(mContext.getResources().getString(R.string.xieyi_content2));
			xieyiContentTv.setVisibility(View.VISIBLE);
			bottomContentLL.setVisibility(View.GONE);

		}else{//1,普通货主模版
			xiYiTitle.setText("福佑卡车整车（拼车）运输三方协议");
			customerNameKey.setText("甲方（委托人）:");
			mCustomerName.setText(mAgreeMentParser.entity.data.customerName);
			yiFangNameKey.setText("乙方（司机）:");
			yiFangContentLL.setVisibility(View.VISIBLE);
			bingFangName.setText("丙方： 南京福佑在线电子商务有限公司");
			xieyiContentTv.setText(mContext.getResources().getString(R.string.xieyi_content));
			xieyiContentTv.setVisibility(View.VISIBLE);
			bottomContentLL.setVisibility(View.VISIBLE);
			//
			mDriverName.setText(mAgreeMentParser.entity.data.driverName);
			mDriverIdCard.setText(mAgreeMentParser.entity.data.driverIdCard);
			mDriverPlate.setText(mAgreeMentParser.entity.data.driverPlate);
			mDriverMobile.setText(mAgreeMentParser.entity.data.driverMobile);
			mData1.setText(mAgreeMentParser.entity.data.confirmTime);
			mData2.setText(mAgreeMentParser.entity.data.arrangeTime);
			mData3.setText(mAgreeMentParser.entity.data.confirmTime);
			mJiaFang.setText(mAgreeMentParser.entity.data.customerName);
			mYiFang.setText(mAgreeMentParser.entity.data.driverName);
		}

		if(xiyiFlag==1){//下单页页面
			headLL.setVisibility(View.GONE);
			bottomContentLL.setVisibility(View.GONE);

		}else{
			headLL.setVisibility(View.VISIBLE);
		}


	}

//	public AgreeMentJsonParser mAgreeMentParser;

	private void getAgreeMent() {
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("order_id", order_id);
		NetWorkUtils.getAggreeMent(this, TAG, parmas, new NetWorkUtils.NetJsonRespon() {
			@Override
			public void onRespon(BaseJsonParser parser) {
				// TODO Auto-generated method stub
				AgreeMentJsonParser mAgreeMentParser = (AgreeMentJsonParser)parser;
				InitData(mAgreeMentParser);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getAgreeMent();
	}

	@Override
	public void onClickListener(int id) {

	}

	@Override
	public void onStop() {
		super.onStop();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}
}
