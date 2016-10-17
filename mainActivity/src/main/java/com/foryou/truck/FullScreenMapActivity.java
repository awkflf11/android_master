package com.foryou.truck;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.foryou.truck.util.ScreenInfo;
import com.foryou.truck.util.UtilsLog;

public class FullScreenMapActivity extends BaseActivity {
	private String TAG ="FullScreenMapActivity";
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private boolean geoCodeEnd = false;
	// private double mLatitude = 39.904965, mLongitude = 116.327764;
	private double mStartLat, mStartLong, mEndLat, mEndLong;
	private String mStartCity, mEndCity, mStartAddress, mEndAddress;
	private double mLatitude, mLongitude;
	private String[] lngArray, latArray,locations;
	private Context mContext;

	OnGetGeoCoderResultListener mSearchListener = new OnGetGeoCoderResultListener() {

		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			// TODO Auto-generated method stub
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(mContext, "抱歉，未能找到结果", Toast.LENGTH_LONG)
						.show();
				return;
			}

			LatLng llA = result.getLocation();
			Log.i("aa", "onGetGeoCodeResult:" + llA.latitude + ","
					+ llA.longitude);
			// mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
			// .getLocation()));

			if (!geoCodeEnd) {
				Log.i("aa", "add overlay start place");
				mStartLat = result.getLocation().latitude;
				mStartLong = result.getLocation().longitude;
				mBaiduMap.addOverlay(new MarkerOptions().position(
						result.getLocation()).icon(
						BitmapDescriptorFactory
								.fromResource(R.drawable.shidian)));
				mSearch.geocode(new GeoCodeOption().city(mEndCity).address(
						mEndAddress));
				geoCodeEnd = true;
			} else {
				Log.i("aa", "add overlay end place");
				mBaiduMap.addOverlay(new MarkerOptions().position(
						result.getLocation()).icon(
						BitmapDescriptorFactory
								.fromResource(R.drawable.zongdian)));
				// mEndLat = result.getLocation().latitude;
				// mEndLong = result.getLocation().longitude;
				// double mLat = (mStartLat+mEndLat)/2;
				// double mLong = (mStartLat+mEndLong)/2;
				// mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(new
				// LatLng(mLat,mLong)));
			}
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			// TODO Auto-generated method stub
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				Toast.makeText(mContext, "抱歉，未能找到结果", Toast.LENGTH_LONG)
						.show();
				return;
			}

			LatLng llA = result.getLocation();
			Log.i("aa", "onGetReverseGeoCodeResult:" + llA.latitude + ","
					+ llA.longitude);

			mBaiduMap.addOverlay(new MarkerOptions().position(
					result.getLocation()).icon(
					BitmapDescriptorFactory.fromResource(R.drawable.dingwei)));

			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
					.getLocation()));
			String strInfo = String.format("纬度：%f 经度：%f",
					result.getLocation().latitude,
					result.getLocation().longitude);
			Log.i("aa", "strInfo:" + strInfo);

			mSearch.geocode(new GeoCodeOption().city(mStartCity).address(
					mStartAddress));
		}
	};

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		mSearch.destroy();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_map);
		mContext = this;

		ShowBackView();
		setTitle("司机位置");
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(mSearchListener);

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(8f);
		mBaiduMap.setMapStatus(msu);

		Intent intent = getIntent();
		lngArray = intent.getExtras().getStringArray("lngArray");
		latArray = intent.getExtras().getStringArray("latArray");
		locations=intent.getExtras().getStringArray("locations");

		LatLng ptCenter = new LatLng(
				Double.valueOf(latArray[latArray.length - 1]),
				Double.valueOf(lngArray[lngArray.length - 1]));

		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(ptCenter));
		// mSearch.reverseGeoCode(new
		// ReverseGeoCodeOption().location(ptCenter));
		addOverLay();
		addPopClick();
	}

	private void addOverLay() {
		for (int i = 0; i < lngArray.length; i++) {
			LatLng mlatlng = new LatLng(Double.valueOf(latArray[i]),
					Double.valueOf(lngArray[i]));
			BitmapDescriptor mBitmap;
			if (i == 0) {
				mBitmap = BitmapDescriptorFactory
						.fromResource(R.drawable.shidian);
			} else if (i == lngArray.length - 1) {
				mBitmap = BitmapDescriptorFactory
						.fromResource(R.drawable.dingwei);
			} else {
				mBitmap = BitmapDescriptorFactory
						.fromResource(R.drawable.lit_dingwei);
			}
			mBaiduMap.addOverlay(new MarkerOptions().position(mlatlng).icon(
					mBitmap).zIndex(i));
		}
	}

	public class MyOnMarkerClikListener implements BaiduMap.OnMarkerClickListener{
//		private int index;
//		public MyOnMarkerClikListener(int i){
//			index = i;
//		}
		@Override
		public boolean onMarkerClick(Marker marker) {
			return false;
		}
	};

	InfoWindow mInfoWindow;
	private void addPopClick() {
		mBaiduMap.setOnMarkerClickListener(new MyOnMarkerClikListener() {
			public boolean onMarkerClick(final Marker marker) {
				LayoutInflater	inflater = LayoutInflater.from(mContext);
				Button button= (Button) inflater.inflate(R.layout.act_fullmap_button, null);
//				Button button = new Button(mContext);
//				button.setBackgroundResource(R.drawable.popup);
//				button.setMaxWidth(ScreenInfo.dip2px(mContext, 280));
//				button.setTextColor(getResources().getColor(R.color.text_color2));
//				button.setTextSize(14);
				InfoWindow.OnInfoWindowClickListener listener = null;
				int i=marker.getZIndex();
				//int i=marker.getPeriod();
				button.setText(locations[i]);
				button.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// marker.setIcon(bd);
						mBaiduMap.hideInfoWindow();
					}
				});
				LatLng ll = marker.getPosition();
				mInfoWindow = new InfoWindow(button, ll, -47);
				mBaiduMap.showInfoWindow(mInfoWindow);
				//perfomZoom(ll.latitude, ll.longitude);
				return true;
			}
		});
	}
	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub

	}
}
