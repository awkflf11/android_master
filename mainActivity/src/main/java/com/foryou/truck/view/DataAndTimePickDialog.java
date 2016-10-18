package com.foryou.truck.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.foryou.truck.R;
import com.foryou.truck.entity.CommonConfigEntity;

public class DataAndTimePickDialog extends AlertDialog {
	private WheelView mDataView, mTime1View, mTime2View;
	private Context mContext;
	private String[] mDataString;
	private String[] mWeekDay = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
			"星期六", };
	private String[] mWeekDate;
	private String[] mTime1String, mTime2String;

	private int mCurrentData = 0, mCurrent1Time, mCurrent2Time;
	private CommonConfigEntity mConfigParser;
	private onDataAndTimeSetListener mDataAndTimeListener;
	private boolean addNolimitFlag = false;
	private TextView mText;
	private String[] timeArray = { "00:00", "00:30", "01:00", "01:30", "02:00",
			"02:30", "03:00", "03:30", "04:00", "04:30", "05:00", "05:30",
			"06:00", "06:30", "07:00", "07:30", "08:00", "08:30", "09:00",
			"09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
			"13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00",
			"16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30",
			"20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00",
			"23:30" };
	private ArrayList<String> mTime1List = new ArrayList<String>();

	protected DataAndTimePickDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		DatePickerDialog dia;
	}

	public interface onDataAndTimeSetListener {
		void onDataSet(DialogInterface dialog, int dayindex, int time1index,
				int time2index);
	}

	private void InitDataStringArray() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		Long currenttime = cal.getTimeInMillis();

		int daylimit = Integer.valueOf(mConfigParser.data.limit_day
				.get(0).value.trim());
		mDataString = new String[daylimit];
		mWeekDate = new String[daylimit];
		for (int i = 0; i < daylimit; i++) {
			Date data = new Date(currenttime);
			mDataString[i] = sdf.format(new Date(currenttime));
			mWeekDate[i] = mWeekDay[data.getDay()];
			currenttime += 1000 * 60 * 60 * 24;
		}
	}

	public String getDataString(int index) {
		return mDataString[index];
	}

	public String getTime1String(int index) {
		return mTime1String[index];
	}

	public String getTime2String(int index) {
		return mTime2String[index];
	}

	public int getDataIndex() {
		return mCurrentData;
	}

	public int getTime1Index() {
		return mCurrent1Time;
	}

	public int getTime2Index() {
		return mCurrent2Time;
	}

	private void updateTitle(int newValue) {
		setTitle(mDataString[newValue] + "(" + mWeekDate[newValue] + ")");
	}

	private OnWheelChangedListener mDataChangedListener = new OnWheelChangedListener() {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			// TODO Auto-generated method stub
			updateTitle(newValue);
		}
	};

	private void updateTime2Data() {
		int value = mTime1View.getCurrentItem();
		if (addNolimitFlag) {
			if (value == 0) {
				mTime2View.setVisibility(android.view.View.GONE);
				mText.setVisibility(android.view.View.GONE);
				return;
			} else {
				mTime2View.setVisibility(android.view.View.VISIBLE);
				mText.setVisibility(android.view.View.VISIBLE);
				mTime2String = new String[mTime1String.length - value];
			}
		} else {
			mTime2String = new String[mTime1String.length - value];
		}

		for (int i = 0; i < mTime2String.length; i++) {
			if (i == mTime2String.length - 1) {
				mTime2String[i] = "23:30";
			} else {
				mTime2String[i] = mTime1String[value + 1 + i];
			}
		}

		mTime2View.setViewAdapter(new ArrayWheelAdapter<String>(mContext,
				mTime2String));

		mCurrent2Time = 0;
		mTime2View.setCurrentItem(0);
	}

	private OnWheelChangedListener mlistener = new OnWheelChangedListener() {

		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			// TODO Auto-generated method stub
			if (wheel == mTime1View) {
				updateTime2Data();
			} else if (wheel == mDataView) {
				updateTime1Data();
			}
		}
	};

	private void updateTime1Data() {
		Log.i("dataTime", "updateTime1Data .....");
		mTime1List.clear();
		int currentDateIndex = mDataView.getCurrentItem();
		Date date = new Date();
		date.setTime(System.currentTimeMillis());
		int currentSecondsCount = date.getHours() * 60 * 60 + date.getMinutes()
				* 60;
		if (currentSecondsCount >= 23 * 60 * 60) {
			currentSecondsCount = 23 * 60 * 60 - 10;
		}

		if (currentDateIndex == 0) {
			if (addNolimitFlag) {
				mTime1List.add("请选择");
			}

			for (int i = 0; i < 47; i++) {
				if (currentSecondsCount < i * 60 * 30) {
					mTime1List.add(timeArray[i]);
				}
			}
			mTime1String = new String[mTime1List.size()];
			for (int i = 0; i < mTime1List.size(); i++) {
				mTime1String[i] = mTime1List.get(i);
			}

			mTime1View.setViewAdapter(new ArrayWheelAdapter<String>(mContext,
					mTime1String));

			mCurrent1Time = 0;
			mTime1View.setCurrentItem(0);
		} else {
			mTime1String = new String[48];
			if (addNolimitFlag) {
				mTime1String = new String[48];
				mTime1List.add("请选择");
			} else {
				mTime1String = new String[47];
			}
			int tempInt = 9999;
			for (int i = 0; i < 47; i++) {
				mTime1List.add(timeArray[i]);
				if (i * 60 * 30 > 14 * 60 * 30) {
					if (tempInt > i) {
						tempInt = i;
					}
				}
			}
			for (int i = 0; i < mTime1List.size(); i++) {
				mTime1String[i] = mTime1List.get(i);
			}

			mTime1View.setViewAdapter(new ArrayWheelAdapter<String>(mContext,
					mTime1String));
			mCurrent1Time = tempInt;
			mTime1View.setCurrentItem(tempInt);
		}
		updateTime2Data();
	}

	private void InitView(View view) {
		mDataView = (WheelView) view.findViewById(R.id.data);
		mTime1View = (WheelView) view.findViewById(R.id.time1);
		mTime2View = (WheelView) view.findViewById(R.id.time2);

		mDataView.setVisibleItems(7);
		mTime1View.setVisibleItems(7);
		mTime2View.setVisibleItems(7);

		InitDataStringArray();
		mDataView.setViewAdapter(new ArrayWheelAdapter<String>(mContext,
				mDataString));
		mDataView.addChangingListener(mlistener);

		if (mCurrentData < 0 || mCurrentData >= mDataString.length) {
			mCurrentData = 0;
		}

		mDataView.setCurrentItem(mCurrentData);
		setTitle(mDataString[mCurrentData] + "(" + mWeekDate[mCurrentData]
				+ ")  ");

		updateTime1Data();

		mDataView.addChangingListener(mDataChangedListener);
		mTime1View.addChangingListener(mlistener);

		setButton(BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mCurrentData = mDataView.getCurrentItem();
				mCurrent1Time = mTime1View.getCurrentItem();
				mCurrent2Time = mTime2View.getCurrentItem();

				if (mDataAndTimeListener != null) {
					mDataAndTimeListener.onDataSet(dialog, mCurrentData,
							mCurrent1Time, mCurrent2Time);
				}
			}
		});
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public DataAndTimePickDialog(Context context, int theme,
								 onDataAndTimeSetListener callBack, CommonConfigEntity mConfig,
								 boolean addnolimit) {
		super(context, 3);
		mContext = context;
		mConfigParser = mConfig;
		mDataAndTimeListener = callBack;
		addNolimitFlag = addnolimit;

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.choose_data_time, null);
		mText = (TextView) view.findViewById(R.id.text);
		InitView(view);
		setView(view);
	}
}
