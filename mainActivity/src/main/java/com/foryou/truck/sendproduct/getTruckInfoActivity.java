package com.foryou.truck.sendproduct;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.R;
import com.foryou.truck.adapter.MyListViewAdapter;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.CarLoadEntity;
import com.foryou.truck.entity.CommonConfigEntity;
import com.foryou.truck.entity.CommonConfigEntity.BaseKeyValue;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.parser.CarLoadParser;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.MutiChooseBtn2;
import com.foryou.truck.view.MutiTabChoose.TabClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @des:用车信息
 */
public class getTruckInfoActivity extends BaseActivity {
	private String TAG = "getTruckInfoActivity";
	private Context mContext;
	@BindView(id = R.id.zhangyong_layout, click = true)
	private LinearLayout mZhangyongLayout;//占用体积和占用车长
	@BindView(id = R.id.tiji_value_et)
	private EditText zhangTiJiEdit;
	@BindView(id = R.id.zhangyong_len_edit)
	private EditText mZhangyongEdit;
	@BindView(id = R.id.muti_btn_choose)
	MutiChooseBtn2 mutiCarTypeBtn;
	@BindView(id = R.id.muti_btn_ll)
	private LinearLayout mutiBtnLL;// 标记的tag 1：整车，2：拼车
	@BindView(id = R.id.truck_type, click = true)
	private TextView mTruckType;
	@BindView(id = R.id.truck_length, click = true)
	private TextView mTruckLen;
	//
	@BindView(id = R.id.confirm_query, click = true)
	private Button mConfirmBtn;
	private GridView mTruckTypeGridView, mTruckLenGridView;
	private AlertDialog mTruckTypeDialog, mTruckLenDialog;
	private CommonConfigEntity mConfigEntity;
	//
	@BindView(id = R.id.car_length_tv)
	private TextView carLengthTv;// 车长
	@BindView(id = R.id.pagerHeight)
	private RelativeLayout pagerHeight;
	@BindView(id = R.id.car_length_gridView)
	private GridView carLengthGridView;//

	List<Map<String, Object>> carLengthList;
	//
	@BindView(id = R.id.car_type_gridView)
	private GridView carTypeGridView;
	@BindView(id = R.id.pagerHeight2)
	private RelativeLayout pagerHeight2;
	@BindView(id = R.id.car_type_tv)
	private TextView carTypeTv;// 车型
	List<Map<String, Object>> carTypeList;
	//
	@BindView(id = R.id.notice_truck_tv)
	private TextView noticeTv;// 提示信息
	@BindView(id = R.id.notice_truck_weight_tv)
	private TextView noticeTv2;// 提示信息
	public static boolean carLengthFlag = false;
	public static boolean carTypeFlag = false;
	private String carLengthValue = "";
	private String carLengthId = "";
	private String carTypeValue = "";
	private String carTypeId = "";
	private int topLengtnPadding = 110;//   2*50+10
	private int bottomLengthPadding = 110;// 5*50+10
	private int topTypePadding = 60;//60     1*50+10
	private int bottomTypePadding = 60;//160    3*50+10
	private String indexCarLengthStr;
	private String indexCarTypeStr;

	private String car_type, car_model_text, car_length_text;

	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.get_truck_info);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("用车信息");
		ShowBackView();
		mContext = this;
		carLengthFlag = false;
		carTypeFlag = false;
		mConfigEntity = SharePerfenceUtil.getConfigData(mContext);
		Intent intent = getIntent();
		car_type = intent.getStringExtra("car_type");
		initCarTypeBtn();
		car_model_text = intent.getStringExtra("car_model_text");
		if (!TextUtils.isEmpty(car_model_text)) {
			mTruckType.setText(intent.getStringExtra("car_model_text"));
			mTruckType.setTag(intent.getStringExtra("car_model"));
		}
		car_length_text = intent.getStringExtra("car_length_text");
		if (!TextUtils.isEmpty(car_length_text)) {
			mTruckLen.setText(intent.getStringExtra("car_length_text"));
			mTruckLen.setTag(intent.getStringExtra("car_length"));
		}

		if(car_type.equals("1")){//整车
//			mZhangyongEdit.setText("");
//			zhangTiJiEdit.setText("");
			zhangTiJiEdit.setText(intent.getStringExtra("occupy_tiji"));
			zhangTiJiEdit.setSelection(zhangTiJiEdit.getText().length());
		}else{//拼车
			mZhangyongEdit.setText(intent.getStringExtra("occupy_length"));
			mZhangyongEdit.setSelection(mZhangyongEdit.getText().length());
			zhangTiJiEdit.setText(intent.getStringExtra("occupy_tiji"));
			zhangTiJiEdit.setSelection(zhangTiJiEdit.getText().length());

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		String goodsWeight = getIntent().getStringExtra("goods_weight");
		if (!TextUtils.isEmpty(goodsWeight)) {
			if(car_type.equals("1")){
				noticeTv2.setVisibility(View.VISIBLE);
				noticeTv2.setText("你填写的货物重量是" + goodsWeight + "吨");
			}
		}
		initCarLengthAndType();
	}

	private void initCarLengthAndType() {
		if(!carLengthFlag){
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					ScreenInfo.dip2px(this, topLengtnPadding));
			pagerHeight.setLayoutParams(param);
		}
		if(!carTypeFlag) {
			LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ScreenInfo.dip2px(this, topTypePadding));
			pagerHeight2.setLayoutParams(param2);
		}
		if (getCarLengthAdapter() == null) {
			return;
		}
		carLengthGridView.setAdapter(getCarLengthAdapter());
		carLengthGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		carLengthGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
				//carLengthFlag = false;
				if (mCarLenselectIndex != -1) {
					carLengthList.get(mCarLenselectIndex).put("textBackground", "*f1f1f1");
					carLengthList.get(mCarLenselectIndex).put("textColor", "#101010");
				}
				carLengthList.get(index).put("textBackground", "*00a9e0");
				carLengthList.get(index).put("textColor", "#ffffff");
				mCarLenselectIndex = index;
				carLengthGridView.setTag("1");
				if (index == 5) {//7
					String str = (String) (carLengthList.get(5).get("itemText"));//7
					if (str.equals("更多") && !carLengthFlag) {
						TongjiModel.addEvent(mContext, "用车信息", TongjiModel.TYPE_BUTTON_CLIKC, "车长更多");
						expendLayout(1);
						carLengthList.get(index).put("itemText", indexCarLengthStr);
						carLengthList.get(index).put("showUpIv", false);
						//
						carLengthList.get(mCarLenselectIndex).put("textBackground", "*f1f1f1");
						carLengthList.get(mCarLenselectIndex).put("textColor", "#101010");
						carLengthGridView.setTag("0");
						noticeTv.setVisibility(View.GONE);
					}
					if(carLengthFlag){
//						carLengthList.get(5).put("textBackground", "*f1f1f1");
//						carLengthList.get(5).put("itemText",fineIndexStr);
						carLengthList.get(6).put("textBackground", "*f1f1f1");
						carLengthList.get(6).put("itemText",sixIndexStr);
						carLengthList.get(7).put("textBackground", "*f1f1f1");
						carLengthList.get(7).put("itemText",sevenIndexStr);
					}
				}
				carLengthAdapter.notifyDataSetChanged();
				carLengthValue = (String) carLengthList.get(index).get("itemText");
				UtilsLog.i(TAG,"carLengthValue==="+carLengthValue);
				carLengthId = (String) carLengthList.get(index).get("carLengthKey");
				isXianShiNotice();

			}
		});

		if (getCarTypeAdapter() == null) {
			return;
		}
		carTypeGridView.setAdapter(getCarTypeAdapter());
		carTypeGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		carTypeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
				//carTypeFlag = false;
				if (mCarModelSelectIndex != -1) {
					carTypeList.get(mCarModelSelectIndex).put("textBackground", "*f1f1f1");
					carTypeList.get(mCarModelSelectIndex).put("textColor", "#101010");
				}
				carTypeList.get(index).put("textBackground", "*00a9e0");
				carTypeList.get(index).put("textColor", "#ffffff");
				mCarModelSelectIndex = index;
				carTypeGridView.setTag("1");
				if (index == 3) {
					String str = (String) (carTypeList.get(3).get("itemText"));
					UtilsLog.i(TAG, "carTypeFlag===" + carTypeFlag + "");
					if (str.equals("更多") && !carTypeFlag) {
						expendLayout(2);
						TongjiModel.addEvent(mContext, "用车信息", TongjiModel.TYPE_BUTTON_CLIKC, "车型更多");
						carTypeList.get(index).put("itemText", indexCarTypeStr);
						carTypeList.get(index).put("showUpIv", false);
						//
						carTypeList.get(mCarModelSelectIndex).put("textBackground", "*f1f1f1");
						carTypeList.get(mCarModelSelectIndex).put("textColor", "#101010");
						carTypeGridView.setTag("0");
						noticeTv.setVisibility(View.GONE);
					}
				}
				carTypeAdapter.notifyDataSetChanged();
				carTypeValue = (String) carTypeList.get(index).get("itemText");
				carTypeId = (String) carTypeList.get(index).get("carTypeKey");

				isXianShiNotice();

			}
		});
	}
	CarLoadEntity carLoadEntity;
	public void isXianShiNotice() {
		// 9.6米平板车，标准载重30吨 。 您填写的货物重量是30吨
	 final 	String length = carLengthValue;
	final 	String type = carTypeValue;
	final 	String carLength = carLengthId;
	final 	String carModel = carTypeId;
		if (car_type.equals("2")) {
			return;
		}
		carLoadEntity = SharePerfenceUtil.getCarLoadData(mContext);
		if (carLoadEntity == null) {
			NetWorkUtils.SaveCarLoad(mContext, new NetWorkUtils.NetJsonRespon() {
				@Override
				public void onRespon(BaseJsonParser parser) {
					CarLoadParser mParser = (CarLoadParser) parser;
					carLoadEntity = mParser.entity;
					if(carLoadEntity == null){
						ToastUtils.toast(mContext, "正在加载配置文件，请稍候再试");
						return;
					}else{
						String biaozhunWeight = NetWorkUtils.getCarLoad(carModel, carLength, carLoadEntity);
						if (carLengthGridView.getTag().equals("1") && carTypeGridView.getTag().equals("1")) {
							if (!TextUtils.isEmpty(biaozhunWeight)) {
								noticeTv.setVisibility(View.VISIBLE);
								noticeTv.setText(length + "米" + type + ", 标准载重" + biaozhunWeight + "吨");
							} else {
								noticeTv.setVisibility(View.GONE);
							}
						}
					}
				}
			});
		}else{
			String biaozhunWeight = NetWorkUtils.getCarLoad(carModel, carLength, carLoadEntity);
			if (carLengthGridView.getTag().equals("1") && carTypeGridView.getTag().equals("1")) {
				if (!TextUtils.isEmpty(biaozhunWeight)) {
					noticeTv.setVisibility(View.VISIBLE);
					noticeTv.setText(length + "米" + type + ", 标准载重" + biaozhunWeight + "吨");
				} else {
					noticeTv.setVisibility(View.GONE);
				}
			}
		}

	}

	MyListViewAdapter carLengthAdapter;
	int mCarLenselectIndex = -1;
	MyListViewAdapter carTypeAdapter;
	int mCarModelSelectIndex = -1;
	private String  fineIndexStr="";
	private String  sixIndexStr="";
	private String  sevenIndexStr="";

	private ListAdapter getCarLengthAdapter() {
		carLengthList = new ArrayList<Map<String, Object>>();
		if (mConfigEntity == null) {
			return null;
		}
		UtilsLog.i(TAG,"mCarLenselectIndex===="+mCarLenselectIndex);
		if (getIntent().getStringExtra("mCarLenselectIndex") != null) {
			try {
				mCarLenselectIndex = Integer.parseInt(getIntent().getStringExtra("mCarLenselectIndex"));
				if (mCarLenselectIndex >= 5) {
					//carLengthFlag = false;
					expendLayout(1);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		List<Map<String, Object>> commonCarLengthList =NetWorkUtils.getCommonCarLength(mConfigEntity);

		if(commonCarLengthList.size()%4==0){
			bottomLengthPadding=  commonCarLengthList.size()/4*50+10;

		}else{
			bottomLengthPadding=(commonCarLengthList.size()/4+1)*50+10;
		}

		for (int i = 0; i < commonCarLengthList.size(); i++) {// mConfigEntity.data.car_length.size()
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("itemText", commonCarLengthList.get(i).get("value"));//
			map.put("carLengthKey", commonCarLengthList.get(i).get("key"));
			map.put("textBackground", "*f1f1f1");
			map.put("textColor", "#101010");
			if (i == 5&&!carLengthFlag) {//7
				map.put("itemText", "更多");
				map.put("showUpIv", true);
				indexCarLengthStr = (String) commonCarLengthList.get(5).get("value");//7
			} else {
				map.put("itemText", commonCarLengthList.get(i).get("value"));
			}
			if((i==7||i==6)&&!carLengthFlag){
				map.put("itemText",false);
				map.put("textBackground",false);
				sixIndexStr=(String) commonCarLengthList.get(6).get("value");
				sevenIndexStr=(String) commonCarLengthList.get(7).get("value");
			}
			if (mCarLenselectIndex != -1) {
				if (i == mCarLenselectIndex) {// 选中的
					map.put("textBackground", "*00a9e0");
					map.put("textColor", "#ffffff");
					// carLengthValue=mConfigEntity.data.car_length.get(i).value;
					carLengthValue = (String) commonCarLengthList.get(i).get("value");
					// carLengthId=mConfigEntity.data.car_length.get(i).key;
					carLengthId = (String) commonCarLengthList.get(i).get("key");
					carLengthGridView.setTag("1");
					isXianShiNotice();
				}
				if (mCarLenselectIndex >= 5) {//7
					map.put("itemText", commonCarLengthList.get(i).get("value"));
					map.put("showUpIv", false);
				}
			}
			carLengthList.add(map);
		}
		carLengthAdapter = new MyListViewAdapter(this, carLengthList, R.layout.item, new String[] { "itemText", "textBackground",
				"textColor", "showUpIv" }, new int[] { R.id.imageText, R.id.image_rl, R.id.imageText, R.id.showup_iv }, false);
		return carLengthAdapter;
	}

	private ListAdapter getCarTypeAdapter() {
		carTypeList = new ArrayList<Map<String, Object>>();
		if (mConfigEntity == null) {
			return null;
		}

		if (getIntent().getStringExtra("mCarModelSelectIndex") != null) {
			try {
				mCarModelSelectIndex = Integer.parseInt(getIntent().getStringExtra("mCarModelSelectIndex"));
				if (mCarModelSelectIndex >= 3) {
					//carTypeFlag = false;
					expendLayout(2);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		List<Map<String, Object>> commonCarTypeList = NetWorkUtils.getCommonCarType(mConfigEntity);

		if(commonCarTypeList.size()%4==0){
			bottomTypePadding=  commonCarTypeList.size()/4*50+10;
		}else{
			bottomTypePadding=(commonCarTypeList.size()/4+1)*50+10;
		}

		for (int i = 0; i < commonCarTypeList.size(); i++) {// mConfigEntity.data.car_model.size()
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("showUpIv", false);
			map.put("itemText", commonCarTypeList.get(i).get("value"));
			map.put("carTypeKey", commonCarTypeList.get(i).get("key"));
			map.put("textBackground", "*f1f1f1");
			map.put("textColor", "#101010");
			if (i == 3) {
				map.put("itemText", "更多");
				map.put("showUpIv", true);
				indexCarTypeStr = (String)commonCarTypeList.get(3).get("value");
			} else {
				map.put("itemText", commonCarTypeList.get(i).get("value"));
			}
			if (mCarModelSelectIndex != -1) {
				if (i == mCarModelSelectIndex) {// 选中的
					map.put("textBackground", "*00a9e0");
					map.put("textColor", "#ffffff");
					carTypeValue = (String)commonCarTypeList.get(i).get("value");
					carTypeId = (String)commonCarTypeList.get(i).get("key");
					
					carTypeGridView.setTag("1");
					isXianShiNotice();
				}
				if (mCarModelSelectIndex >= 3) {
					map.put("itemText", commonCarTypeList.get(i).get("value"));
					map.put("showUpIv", false);
				}
			}

			carTypeList.add(map);
		}
		carTypeAdapter = new MyListViewAdapter(this, carTypeList, R.layout.item, new String[] { "itemText", "textBackground", "textColor",
				"showUpIv" }, new int[] { R.id.imageText, R.id.image_rl, R.id.imageText, R.id.showup_iv }, false);
		return carTypeAdapter;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void expendLayout(final int flagInt) {
		final LinearLayout.LayoutParams params;
		int t = 0;
		int t2 = 0;
		if (flagInt == 1) {
			params = (android.widget.LinearLayout.LayoutParams) pagerHeight.getLayoutParams();
			t = ScreenInfo.dip2px(this, topLengtnPadding);
			t2 = ScreenInfo.dip2px(this, bottomLengthPadding);
		} else {
			params = (android.widget.LinearLayout.LayoutParams) pagerHeight2.getLayoutParams();
			t = ScreenInfo.dip2px(this, topTypePadding);
			t2 = ScreenInfo.dip2px(this, bottomTypePadding);
		}
		ValueAnimator va;
		if (flagInt == 1) {
			if (carLengthFlag) {
				carLengthFlag = false;
				va = ValueAnimator.ofInt(t2, t);
				// if (mIndicator != null) {
				// hologram_rl.removeView(mIndicator);
				// }
			} else {
				carLengthFlag = true;
				va = ValueAnimator.ofInt(t, t2);
				// initIndicator();
				// hologram_rl.addView(mIndicator);
			}
		} else {
			if (carTypeFlag) {
				//carTypeFlag = false;
				va = ValueAnimator.ofInt(t2, t);
			} else {
				carTypeFlag = true;
				va = ValueAnimator.ofInt(t, t2);
			}
		}
		// mLayout.setEnabled(false);
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator va) {
				params.height = (Integer) va.getAnimatedValue();
				int hh = params.height;
				if (flagInt == 1) {
					pagerHeight.setLayoutParams(params);
				} else {
					pagerHeight2.setLayoutParams(params);
				}
			}
		});
		va.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator arg0) {
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				// mLayout.setEnabled(true);
			}

			@Override
			public void onAnimationCancel(Animator arg0) {

			}
		});
		va.setDuration(300);
		va.start();
	}

	private void initCarTypeBtn() {
		mutiCarTypeBtn.init(new String[] { "整车", "拼车" });
		if (!TextUtils.isEmpty(car_type)) {
			if (car_type.equals("1")) {
				mutiCarTypeBtn.setChooseStatus(true, false);
				chooseCarType("1");
				car_type = "1";
				carTypeTv.setText("需求车型");
				pagerHeight.setVisibility(View.VISIBLE);
				carLengthTv.setVisibility(View.VISIBLE);
				noticeTv.setVisibility(View.VISIBLE);
			} else if (car_type.equals("2")) {
				mutiCarTypeBtn.setChooseStatus(false, true);
				chooseCarType("2");
				car_type = "2";
				carTypeTv.setText("需求车型(可选)");
				noticeTv.setVisibility(View.GONE);
				carLengthTv.setVisibility(View.GONE);
				pagerHeight.setVisibility(View.GONE);
			}
		} else {
			car_type = "1";
		}

		mutiCarTypeBtn.setOnTabClickListener(new TabClickListener() {
			@Override
			public boolean tabClicked(int index) {
				if (index == 0) {// 整车
					mutiCarTypeBtn.setChooseStatus(true, false);
					chooseCarType("1");
					car_type = "1";
					pagerHeight.setVisibility(View.VISIBLE);
					carLengthTv.setVisibility(View.VISIBLE);
					carTypeTv.setText("需求车型");
					noticeTv.setVisibility(View.VISIBLE);
					noticeTv2.setVisibility(View.VISIBLE);
					TongjiModel.addEvent(mContext, "用车信息", TongjiModel.TYPE_BUTTON_CLIKC, "整车" + "车长_" + carLengthValue + ",车型_"
							+ carTypeValue);
					Tools.hideInput(getTruckInfoActivity.this);
				} else {//拼车
					pagerHeight.setVisibility(View.GONE);
					carLengthTv.setVisibility(View.GONE);
					mutiCarTypeBtn.setChooseStatus(false, true);
					chooseCarType("2");
					car_type = "2";
					carTypeTv.setText("需求车型(可选)");
					noticeTv.setVisibility(View.GONE);
					noticeTv2.setVisibility(View.GONE);
					TongjiModel.addEvent(mContext, "用车信息", TongjiModel.TYPE_BUTTON_CLIKC, "拼车" + "占用车长_"
							+ mZhangyongEdit.getText().toString() + ",车型_" + carTypeValue);
				}
				return false;
			}
		});

	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private List<BaseKeyValue> mArrayData;

		public ImageAdapter(Context context, List<BaseKeyValue> data) {
			mContext = context;
			mArrayData = data;
		}

		public int getCount() {
			return mArrayData.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView img;
			if (convertView == null) {
				TextView mText = new TextView(mContext);
				if (mArrayData == mConfigEntity.data.car_length) {
					mText.setText(mArrayData.get(position).value + "米");
				} else {
					mText.setText(mArrayData.get(position).value);
				}
				mText.setTextSize(16);
				mText.setGravity(Gravity.CENTER_HORIZONTAL);
				convertView = mText;
			} else {
				img = (TextView) convertView;
			}
			return convertView;
		}
	}

	private void chooseCarType(String type) {
		if (type.equals("1")) {
			mutiBtnLL.setTag("1");
			mZhangyongLayout.setVisibility(android.view.View.GONE);
		} else if (type.equals("2")) {
			mutiBtnLL.setTag("2");
			mZhangyongLayout.setVisibility(android.view.View.VISIBLE);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub
		AlertDialog.Builder adb;
		switch (id) {
		case R.id.truck_type:
			if (mTruckTypeDialog == null) {
				adb = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
				adb.setTitle("请选择需要的车型");
				LayoutInflater mInflater = LayoutInflater.from(mContext);
				View mTruckView = mInflater.inflate(R.layout.gallery_view, null);
				mTruckTypeGridView = (GridView) mTruckView.findViewById(R.id.mygridview);
				mTruckTypeGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
				mTruckTypeGridView.setAdapter(new ImageAdapter(mContext, mConfigEntity.data.car_model));
				mTruckTypeGridView.setOnItemClickListener(mGridItemListener);
				adb.setView(mTruckView);
				mTruckTypeDialog = adb.create();
			}
			mTruckTypeDialog.show();
			break;
		case R.id.truck_length:
			if (mTruckLenDialog == null) {
				adb = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
				adb.setTitle("请选择需要的车长");
				LayoutInflater mInflater = LayoutInflater.from(mContext);
				View mTruckLenView = mInflater.inflate(R.layout.gallery_view, null);
				mTruckLenGridView = (GridView) mTruckLenView.findViewById(R.id.mygridview);
				mTruckLenGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
				mTruckLenGridView.setAdapter(new ImageAdapter(mContext, mConfigEntity.data.car_length));
				mTruckLenGridView.setOnItemClickListener(mGridItemListener);
				adb.setView(mTruckLenView);
				mTruckLenDialog = adb.create();
			}
			mTruckLenDialog.show();
			break;
		case R.id.confirm_query:
			if (mutiBtnLL.getTag().equals("1")) {// 整车
				if (!carLengthGridView.getTag().equals("1")) {
					ToastUtils.toast(mContext, "请选择车长");
					return;
				}

				if (!carTypeGridView.getTag().equals("1")) {
					ToastUtils.toast(mContext, "请选择车型");
					return;
				}

			} else {// 拼车
				if (mZhangyongEdit.getText().toString().trim().equals("")&&zhangTiJiEdit.getText().toString().trim().equals("")) {
					ToastUtils.toast(mContext, "请填写占用体积或占用车长");
					return;
				}
				if (zhangTiJiEdit.getText().toString().trim().equals("0")) {
					ToastUtils.toast(mContext, "请填写大于0的占用体积值");
					return;
				}
				if (mZhangyongEdit.getText().toString().trim().equals("0")) {
					ToastUtils.toast(mContext, "请填写大于0的占用车长值");
					return;
				}
				if (!carTypeGridView.getTag().equals("1")) {
					//ToastUtils.toast(mContext, "请选择车型");
					//return;
					carTypeValue="";
					carTypeId="";
					mCarModelSelectIndex=-1;
				}
			}
			Intent intent = new Intent();
			if (mutiBtnLL.getTag().equals("1")) { // mZhengcheLayout.getTag().equals("1")
				intent.putExtra("car_type", "1");
			} else {
				intent.putExtra("car_type", "2");
				intent.putExtra("occupy_tiji", zhangTiJiEdit.getText().toString().trim());
				intent.putExtra("occupy_length", mZhangyongEdit.getText().toString().trim());
			}
			// intent.putExtra("car_model", (String) mTruckType.getTag());
			// intent.putExtra("car_model_text",
			// mTruckType.getText().toString());
			// intent.putExtra("car_length", (String) mTruckLen.getTag());
			// intent.putExtra("car_length_text",
			// mTruckLen.getText().toString());
			intent.putExtra("car_model", carTypeId);
			intent.putExtra("car_model_text", carTypeValue);
			intent.putExtra("car_length", carLengthId);
			intent.putExtra("car_length_text", carLengthValue);
			intent.putExtra("mCarLenselectIndex", mCarLenselectIndex + "");
			UtilsLog.i(TAG, "mCarLenselectIndex==-- " + mCarLenselectIndex);
			intent.putExtra("mCarModelSelectIndex", mCarModelSelectIndex + "");
			this.setResult(100, intent);
			finish();
			break;
		}
	}

	private OnItemClickListener mGridItemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			GridView mGridView = (GridView) parent;
			if (mGridView == mTruckLenGridView) {
				mTruckLen.setText(mConfigEntity.data.car_length.get(position).value + "米");
				mTruckLen.setTag(mConfigEntity.data.car_length.get(position).key);
				mTruckLenDialog.dismiss();
			} else if (mGridView == mTruckTypeGridView) {
				mTruckType.setText(mConfigEntity.data.car_model.get(position).value);
				mTruckType.setTag(mConfigEntity.data.car_model.get(position).key);
				mTruckTypeDialog.dismiss();
			}
		}
	};

	@Override
	public void onBackPressed() {
		// if (!region.equals(provinceText.getText().toString())
		// || !address.equals(mDetailPlace.getText().toString())) {
		// alertDialog("确认离开吗？", "您已填写的数据将会丢失",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// finish();
		// }
		// }, new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// } else {
		finish();
		// }
		// super.onBackPressed();
	}

}
