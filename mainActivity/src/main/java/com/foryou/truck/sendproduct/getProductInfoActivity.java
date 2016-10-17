package com.foryou.truck.sendproduct;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.entity.CarLoadEntity;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.parser.CarLoadParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.NetWorkUtils.NetJsonRespon;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.R;

/**
 * @des: 货物信息页面
 */
public class getProductInfoActivity extends BaseActivity {
	private String TAG = "getProductInfoActivity";
	private Context mContext;
	@BindView(id = R.id.product_name)
	private EditText mProductName;
	@BindView(id = R.id.product_weight)
	private EditText mProductWeight;
	@BindView(id = R.id.product_volume)
	private EditText mProductVolume;//
	@BindView(id = R.id.confirm_query, click = true)
	private Button mConfirmBtn;
	@BindView(id = R.id.notice_truck_tv)
	private TextView noticeTv;// 提示的信息：你选择的是9.6米平板车，标准载重是30吨
	  CarLoadEntity carLoadEntity;
	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.get_product_info);
	}

	private String goodsName, goodCubage, goodsWeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("货物信息");
		mContext = this;
		ShowBackView();
		Intent intent = this.getIntent();
		initNotice(intent);
		goodsName = intent.getStringExtra("goods_name");
		mProductName.setText(goodsName);
		mProductName.setSelection(mProductName.getText().length());
		if (goodsName == null) {
			goodsName = "";
		}

		goodCubage = intent.getStringExtra("goods_cubage");
		if (!TextUtils.isEmpty(goodCubage)) {//
			if (!intent.getStringExtra("goods_cubage").equals("0")) {
				mProductVolume.setText(intent.getStringExtra("goods_cubage"));
			}
		}
		if (goodCubage == null) {
			goodCubage = "";
		}

		goodsWeight = intent.getStringExtra("goods_weight");
		mProductWeight.setText(goodsWeight);
		if (goodsWeight == null) {
			goodsWeight = "";
		}
	}

	private void initNotice(Intent intent) {
		final String carModelText = intent.getStringExtra("car_model_text");
		final String carLengthText = intent.getStringExtra("car_length_text");
		final String carModel = intent.getStringExtra("car_model");
		final String carLength = intent.getStringExtra("car_length");
		if (TextUtils.isEmpty(carModelText)||TextUtils.isEmpty(carLengthText) || TextUtils.isEmpty(carModel)||TextUtils.isEmpty(carLength)) {
			return;
		}
		final String carType = intent.getStringExtra("car_type");
		// 你选择的是9.6米平板车，标准载重是30吨
		 carLoadEntity = SharePerfenceUtil.getCarLoadData(mContext);
		if (carLoadEntity == null) {
			NetWorkUtils.SaveCarLoad(mContext, new NetJsonRespon(){
				@Override
				public void onRespon(BaseJsonParser parser) {
					CarLoadParser mParser = (CarLoadParser) parser;
					carLoadEntity = mParser.entity;
					if(carLoadEntity!=null){
						check(carModel, carLength, carLoadEntity, carModelText , carType,  carLengthText);
					}
				}
			});
		} else{	
			check(carModel, carLength, carLoadEntity, carModelText , carType,  carLengthText);
		}
	}
  public void check(String carModel,String carLength,CarLoadEntity carLoadEntity,String carModelText 
		  ,String carType, String carLengthText ){
    	 String biaozhunWeight=NetWorkUtils.getCarLoad(carModel, carLength, carLoadEntity);
		if (!TextUtils.isEmpty(carModelText)) {
			if (carType.equals("1")) {
				if (!TextUtils.isEmpty(carLengthText)&&!TextUtils.isEmpty( biaozhunWeight)) {
					noticeTv.setVisibility(View.VISIBLE);
					carLengthText=carLengthText.contains("米")?carLengthText:carLengthText+"米";
					noticeTv.setText("你选择的是" + carLengthText + "" + carModelText + ", 标准载重是"
							+ biaozhunWeight+"吨");
				}else{
					noticeTv.setVisibility(View.GONE);
				}
			} else {
				noticeTv.setVisibility(View.GONE);
			}
		} else {
			noticeTv.setVisibility(View.GONE);
		}
    }
	
	@Override
	public void onClickListener(int id) {
		switch (id) {
		case R.id.confirm_query:
			if (mProductName.getText().toString().trim().equals("")) {
				ToastUtils.toast(this, "请填写货物名称");
				return;
			}

			if (mProductWeight.getText().toString().trim().equals("")) {
				ToastUtils.toast(this, "请填写货物重量");
				return;
			}
			if (mProductWeight.getText().toString().trim().equals("0")) {
				ToastUtils.toast(this, "请填写不小于0.0001的货物重量");
				return;
			}

			// if (mProductVolume.getText().toString().trim().equals("")) {
			// ToastUtils.toast(this, "请填写货物体积");
			// return;
			// }
			if (mProductVolume.getText().toString().trim().equals("0")) {
				ToastUtils.toast(this, "请填写不小于0.0001的货物体积");
				return;
			}

			// if (mPincheTx.getTag().equals(1)) {
			// if (mWriteUseTruckLen.getText().toString().equals("")) {
			// ToastUtils.toast(mContext, "请填写预计占用车长");
			// return;
			// }
			// }

			Intent intent = new Intent();
			intent.putExtra("goods_name", mProductName.getText().toString());
			intent.putExtra("goods_cubage", mProductVolume.getText().toString());
			intent.putExtra("goods_weight", mProductWeight.getText().toString());

			UtilsLog.i(TAG, "货物的体积=========="
					+ mProductVolume.getText().toString());
			this.setResult(100, intent);
			finish();
			break;
		}
	}

}
