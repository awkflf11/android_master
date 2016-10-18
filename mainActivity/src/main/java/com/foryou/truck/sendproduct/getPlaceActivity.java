package com.foryou.truck.sendproduct;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.foryou.truck.BaseActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.AddressListEntity;
import com.foryou.truck.entity.AreasEntity;
import com.foryou.truck.parser.AddressListParser;
import com.foryou.truck.parser.AreasJsonParser;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.AreasPickDialog;
import com.foryou.truck.view.AreasPickDialog.AreasDataPickLisener;
import com.foryou.truck.view.SpinnerPopUpListView;
import com.foryou.truck.view.WithDelImgEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @des: 装货地址和卸货地址：
 */
public class getPlaceActivity extends BaseActivity {
	private Context mContext;
	private String TAG = "getPlaceActivity";
	@BindView(id = R.id.text1)
	private TextView provinceText;// 选择省市区
	// click = true
	@BindView(id = R.id.select_province_ll, click = true)
	private LinearLayout select_ll;// 选择地区
	@BindView(id = R.id.text2)
	private WithDelImgEditText mDetailPlace;// 详细地址
	@BindView(id = R.id.confirm_query_place, click = true)
	private Button mConfirmBtn;
	@BindView(id = R.id.regular_address, click = true)
	private TextView mRegularAddr;
	@BindView(id = R.id.pick_up)
	private TextView mPickUp;
	@BindView(id = R.id.text2)
	private AutoCompleteTextView mDetailAddress;
	@BindView(id = R.id.regular_address_layout)
	private RelativeLayout mRegularAddLayout;
	@BindView(id = R.id.img1)
	private ImageView mImageView;
	private boolean mSenderFlag;
	List<Map<String, Object>> adapterlist;
	private SpinnerPopUpListView mPopList = null;
	private int mStartProvinceIndex = 0, mStartCityIndex = 0,
			mStartDistrictIndex = 0;
	private AreasPickDialog mAreasStartDialog;

	private AreasEntity areaEntity;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.get_place);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (!region.equals(provinceText.getText().toString())
				|| !address.equals(mDetailPlace.getText().toString())) {
			alertDialog("", "确认离开吗？您已填写的数据将会丢失",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					});
		} else {
			finish();
		}
		// super.onBackPressed();
	}

	String region = "", address = "";


	public class ProgressAreaAsyncTask extends AsyncTask<Integer, Integer, String>{
		@Override
		protected String doInBackground(Integer... params) {
			AreasEntity entity = SharePerfenceUtil.getAreaData(mContext);
			if(entity == null){
				NetWorkUtils.getAreaData(mContext, TAG, new NetWorkUtils.NetJsonRespon() {
					@Override
					public void onRespon(BaseJsonParser parser) {
						AreasJsonParser mParser = (AreasJsonParser)parser;
						areaEntity = mParser.entity;
						SendProductActivity.areaEntity = areaEntity;
						//	new initProvinceTask(mParser.entity).execute();
					}
				});
			}else{
				areaEntity = entity;
			}
			return null;
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate .....");
		mContext = this;
		if (getIntent().getBooleanExtra("send_address", true)) {
			setTitle("装货地址");
			mSenderFlag = true;
			region = getIntent().getStringExtra("goods_load_region");
			address = getIntent().getStringExtra("goods_load_address");
			mImageView.setBackgroundResource(R.drawable.send_place);
			mDetailPlace.setHint("请输入装货详细地址");

		} else {
			setTitle("卸货地址");
			mSenderFlag = false;
			region = getIntent().getStringExtra("goods_unload_region");
			address = getIntent().getStringExtra("goods_unload_address");
			mImageView.setBackgroundResource(R.drawable.rece_place);
			mDetailPlace.setHint("请输入卸货详细地址");
		}
		provinceText.setText(region);
		if (!TextUtils.isEmpty(address)) {
			mDetailPlace.setText(address);
		}
		ShowBackView();
		if (SendProductActivity.areaEntity == null) {
			UtilsLog.i(TAG,"entity == null");
			new ProgressAreaAsyncTask().execute();
		} else {
			areaEntity = SendProductActivity.areaEntity;
		}
		getUserAddressList();
	}

	@Override
	protected void onDestroy() {
		// mPoiSearch.destroy();
		super.onDestroy();
	}

	private View getAddressListView() {
		LayoutInflater lf = LayoutInflater.from(mContext);
		RelativeLayout linearlayout = (RelativeLayout) lf.inflate(
				R.layout.address_list_background, null);
		ListView listView = new ListView(this);
		listView.setDividerHeight(0);
		MyListViewAdapter mAdapter = new MyListViewAdapter(mContext,
				adapterlist, R.layout.address_list_items, new String[] {
						"region", "address" }, new int[] { R.id.address1,
						R.id.address2 }, false);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(mItemlistener);
		linearlayout.addView(listView);
		return linearlayout;
	}

	private ListView.OnItemClickListener mItemlistener = new ListView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			UtilsLog.i(TAG, "position:" + position);
			mPopList.getPopWindow().dismiss();
			Map<String, Object> map = adapterlist.get(position);
			String region = (String) map.get("region");
			provinceText.setText(region);
			mDetailPlace.setText((String) map.get("address"));
			mDetailPlace.setDrawable();
		}

	};

	private void InitAddressListData(AddressListEntity entity) {
		adapterlist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < entity.data.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("region", entity.data.get(i).region);
			map.put("address", entity.data.get(i).address);
			adapterlist.add(map);
		}
	}

	private void getUserAddressList() {
		Map<String, String> parmams = new HashMap<String, String>();
		String url = NormalNetworkRequest.getUrl(mContext, UrlConstant.BASE_URL
				 + "/user/addressList", parmams);
		showProgressDialog();
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
				mContext, Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						// UtilsLog.i(TAG, "response:" + response);
						cancelProgressDialog();
						AddressListParser mParser = new AddressListParser();
						int result = mParser.parser(response);
						if (result == 1) {
							if (mParser.entity.status.equals("Y")) {
								InitAddressListData(mParser.entity);
							}
						} else {
							Log.i(TAG, "解析错误");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (error instanceof NetworkError) {
							Log.i(TAG, "NetworkError");
						} else if (error instanceof ServerError) {
							Log.i(TAG, "ServerError");
						} else if (error instanceof AuthFailureError) {
							Log.i(TAG, "AuthFailureError");
						} else if (error instanceof ParseError) {
							Log.i(TAG, "ParseError");
						} else if (error instanceof NoConnectionError) {
							Log.i(TAG, "NoConnectionError");
						} else if (error instanceof TimeoutError) {
							Log.i(TAG, "TimeoutError");
						}
						cancelProgressDialog();
					}

				}, true) {
			@Override
			public Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = super.getParams();
				return map;
			}
		};

		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	private AreasDataPickLisener mAreaDataListener = new AreasDataPickLisener() {

		@Override
		public void onAreasDataSelect(int proviceindex, int cityid,
				int districtid) {
			// TODO Auto-generated method stub
			mStartProvinceIndex = proviceindex;
			mStartCityIndex = cityid;
			mStartDistrictIndex = districtid;
			String province = areaEntity.data.get(proviceindex).name;
			String city = areaEntity.data.get(proviceindex).city
					.get(mStartCityIndex).name;
			String district;
			if(mStartDistrictIndex == areaEntity.data.get(proviceindex).city  //其他
					.get(mStartCityIndex).district.size()){
				district = "";
				provinceText.setText(province + "-" + city);
			}else {
				district = areaEntity.data.get(proviceindex).city
						.get(mStartCityIndex).district
						.get(mStartDistrictIndex).name;
				provinceText.setText(province + "-" + city + "-" + district);
			}
			UtilsLog.i(TAG, "city:" + city + ",dristict:" + district);
			// mDetailPlace.setText("");
		}

	};

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.select_province_ll: // select_province_ll text1
			if (areaEntity == null) {
				ToastUtils.toast(mContext, "正在获取城市列表,请稍后再试");
				return;
			}
			if (mAreasStartDialog == null) {
				mAreasStartDialog = new AreasPickDialog(mContext, areaEntity,true);
				if (mSenderFlag) {
					mAreasStartDialog.setTitle("请选择装货地址");
				} else {
					mAreasStartDialog.setTitle("请选择卸货地址");
				}
				mAreasStartDialog
						.SetDataSelectOnClickListener(mAreaDataListener);
			}
			mAreasStartDialog.setCurrentArea(mStartProvinceIndex,
					mStartCityIndex, mStartDistrictIndex);
			mAreasStartDialog.show();
			break;
		case R.id.regular_address:
			if (adapterlist == null) {
				ToastUtils.toast(mContext, "正在获取常用地址信息，请稍候再试");
				return;
			}

			if (adapterlist.size() == 0) {
				ToastUtils.toast(mContext, "没有可用地址信息");
				return;
			}

			if (mPickUp.getVisibility() == android.view.View.VISIBLE) {// GONE
				mPickUp.setVisibility(android.view.View.VISIBLE);
				if (mPopList == null) {
					mPopList = new SpinnerPopUpListView(mContext,
							getAddressListView(), mRegularAddLayout, 1);
					mPopList.getPopWindow().setOnDismissListener(
							mDismissListener);
				}
				mPopList.Show();
			}
			break;
		case R.id.confirm_query_place:// 确认按钮
			if (TextUtils.isEmpty(provinceText.getText().toString())) {
				ToastUtils.toast(mContext, "请选择省市区");
				return;
			}
			if (TextUtils.isEmpty(mDetailPlace.getText().toString().trim())) {
				ToastUtils.toast(mContext, "请输入详细地址");
				return;
			}

			Intent intent = new Intent();
			if (mSenderFlag) {
				TongjiModel.addEvent(mContext, "装货地址",
						TongjiModel.TYPE_BUTTON_CLIKC, "常用地址");
				intent.putExtra("goods_load_region", provinceText.getText()
						.toString());
				intent.putExtra("goods_load_address", mDetailPlace.getText()
						.toString());
				setResult(100, intent);
			} else {
				TongjiModel.addEvent(mContext, "卸货地址",
						TongjiModel.TYPE_BUTTON_CLIKC, "常用地址");
				intent.putExtra("goods_unload_region", provinceText.getText()
						.toString());
				intent.putExtra("goods_unload_address", mDetailPlace.getText()
						.toString());
				setResult(101, intent);
			}
			finish();
			break;
		}
	}

	OnDismissListener mDismissListener = new OnDismissListener() {
		@Override
		public void onDismiss() {
			// TODO Auto-generated method stub
			mPickUp.setVisibility(android.view.View.VISIBLE);// GONE
		}

	};

//	public class initProvinceTask extends AsyncTask<Integer, Integer, Integer> {
//		AreasEntity entity;
//
//		public initProvinceTask(AreasEntity areaentity) {
//			entity = areaentity;
//		}
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//
//		}
//
//		@Override
//		protected Integer doInBackground(Integer... params) {
//			// TODO Auto-generated method stub
//			initProvinceDatas(entity);
//			return 0;
//		}
//
//		@Override
//		protected void onPostExecute(Integer result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//		}
//	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		MyApplication.getInstance().cancelAllRequests(TAG);
		cancelProgressDialog();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
