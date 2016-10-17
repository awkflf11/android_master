package com.foryou.truck.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;

import com.foryou.truck.R;
import com.foryou.truck.entity.AreasEntity;
import com.foryou.truck.model.CityModel;
import com.foryou.truck.model.DistrictModel;
import com.foryou.truck.model.ProvinceModel;
import com.foryou.truck.util.UtilsLog;

public class AreasPickDialog extends AlertDialog {
	private WheelView mViewProvince, mViewCity, mViewDistrict;
	private Context mContext;
	private Boolean isVisableDistrict=true;//表示是否显示区，true：显示，
	//private List<ProvinceModel> mProvinceList;
	private AreasEntity areaEntity;
	protected String[] mProvinceDatas;
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();
	private AreasDataPickLisener mDataSelectLister;

	private String mCurrentProviceName, mCurrentCityName, mCurrentDistrictName,
			mCurrentZipCode;

	private void initProvinceDatas() {
		mProvinceDatas = new String[areaEntity.data.size()];
		for (int i = 0; i < areaEntity.data.size(); i++) {
			mProvinceDatas[i] = areaEntity.data.get(i).name;
			List<AreasEntity.City> cityList = areaEntity.data.get(i).city;
			String[] cityNames = new String[cityList.size()];
			for (int j = 0; j < cityList.size(); j++) {
				cityNames[j] = cityList.get(j).name;
				List<AreasEntity.District> districtList = cityList.get(j)
						.district;
				//String[] distrinctNameArray = new String[districtList.size()+1];
				String[] distrinctNameArray = new String[districtList.size()];
				DistrictModel[] distrinctArray = new DistrictModel[districtList
						.size()+1];
				if (districtList.size() == 0) {
					UtilsLog.i("getPlace", "cityList.get(j).getName():"
							+ cityList.get(j).name);
				}
				for (int k = 0; k < districtList.size(); k++) {
					DistrictModel districtModel;
					if(k==districtList.size()){
						districtModel = new DistrictModel("其他","-1");
					}else {
						districtModel = new DistrictModel(
								districtList.get(k).name, districtList.get(k)
								.id);
						mZipcodeDatasMap.put(districtList.get(k).name,
								districtList.get(k).id);
					}
					distrinctArray[k] = districtModel;
					distrinctNameArray[k] = districtModel.getName();
				}
				mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
			}
			mCitisDatasMap.put(areaEntity.data.get(i).name, cityNames);
		}
	}

	private void updateAreas() {
		try {
			int pCurrent = mViewCity.getCurrentItem();
			mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
			String[] areas = mDistrictDatasMap.get(mCurrentCityName);

			mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(mContext,
					areas));
			mViewDistrict.setCurrentItem(0);
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}catch(Exception e){

		}
	}

	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity
				.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	private OnWheelChangedListener mWheelChangedListener = new OnWheelChangedListener() {
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			// TODO Auto-generated method stub
			if (wheel == mViewProvince) {
				updateCities();
			} else if (wheel == mViewCity) {
				updateAreas();
			} else if (wheel == mViewDistrict) {
				mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
				mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
			}
		}
	};

	public interface AreasDataPickLisener {
		void onAreasDataSelect(int proviceindex, int cityid, int districtid);
	}

	private void InitWheelView(View view, Boolean isVisableDistrict) {
		mViewProvince = (WheelView) view.findViewById(R.id.id_province);
		mViewCity = (WheelView) view.findViewById(R.id.id_city);
		mViewDistrict = (WheelView) view.findViewById(R.id.id_district);
        if(isVisableDistrict){
			mViewDistrict.setVisibility(View.VISIBLE);
		}else{
			mViewDistrict.setVisibility(View.GONE);
		}
		initProvinceDatas();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(mContext,
				mProvinceDatas));
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);

		updateCities();
		updateAreas();

		mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);

		mViewProvince.addChangingListener(mWheelChangedListener);
		mViewCity.addChangingListener(mWheelChangedListener);
		mViewDistrict.addChangingListener(mWheelChangedListener);

		setButton(BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (mDataSelectLister != null) {
					// 避免当区数据为空时程序崩溃
					mDataSelectLister.onAreasDataSelect(
							mViewProvince.getCurrentItem(),
							mViewCity.getCurrentItem(),
							mViewDistrict.getCurrentItem());
				}
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public AreasPickDialog(Context context, AreasEntity entity,Boolean isVisableDistrict) {
		super(context, THEME_HOLO_LIGHT);
		// TODO Auto-generated constructor stub
		isVisableDistrict=isVisableDistrict;
		mContext = context;
		areaEntity = entity;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.choose_place, null);
		InitWheelView(view, isVisableDistrict);
		setView(view);
	}

	public void SetDataSelectOnClickListener(AreasDataPickLisener listener) {
		mDataSelectLister = listener;
	}

	public void setCurrentArea(int provinceIndex, int cityIndex,
			int districtIndex) {
		mViewProvince.setCurrentItem(provinceIndex);
		updateCities();
		mViewCity.setCurrentItem(cityIndex);
		updateAreas();
		mViewDistrict.setCurrentItem(districtIndex);
	}
}
