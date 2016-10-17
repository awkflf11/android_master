package com.foryou.truck.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import com.foryou.truck.BaseActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.adapter.MyContentAdapter;
import com.foryou.truck.datamodel.BankNameListDM;
import com.foryou.truck.entity.BankNameListEntity;
import com.foryou.truck.util.BindView;
import com.foryou.truck.viewholds.BankNameListVH;
import com.woozzu.android.util.StringMatcher;
import com.woozzu.android.widget.IndexableListView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @des:银行名字的列表
 */
public class BankNameListAct extends BaseActivity {
	private static final String TAG = "BankNameListAct";
	private Context mContext;
	@BindView(id = R.id.listview)
	private IndexableListView mListView;
	private int PAGE_SIZE = 30;// Constant.PAGE_SIZE

	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.act_bankname_list);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		initView();
		EventBus.getDefault().register(this);
		initListView();
	}

	@Subscribe
	public void onEvent(String msg){
		cancelProgressDialog();
//		mPtrFrameLayout.refreshComplete();
//		// load more
//		loadMoreListViewContainer.loadMoreFinish(mDataModel.getListPageInfo().isEmpty(), mDataModel.getListPageInfo().hasMore());
//		mPageListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();

		initDataInfoList();
		initData();

	}

	private void initDataInfoList() {
		//mDriverInfoList = dh.GetUserList();
		//mDriverInfoList=mDataModel.getListPageInfo().getDataList();
		mDriverInfoList=new ArrayList<BankNameListEntity.BankNameItem>();


	}
	private void initData(){
		listViewDataAdapter.getDataList().clear();
		Collections.sort(mDriverInfoList, new CollatorComparator());
		addGroupFlag(mDriverInfoList);
		listViewDataAdapter.getDataList().addAll(mDriverInfoList);
		listViewDataAdapter.notifyDataSetChanged();
	}


	//private PagedListViewDataAdapter mPageListAdapter;
	private BankNameListDM mDataModel;
	MyContentAdapter<BankNameListEntity.BankNameItem> listViewDataAdapter;

	private void initListView(){
		//listViewDataAdapter = new MyContentAdapter<BankNameListEntity.BankNameItem>();
		listViewDataAdapter = new MyContentAdapter<BankNameListEntity.BankNameItem>();
		listViewDataAdapter.setViewHolderClass(this,BankNameListVH.class,TAG);
		mListView.setAdapter(listViewDataAdapter);
		mListView.setFastScrollEnabled(true);
		mListView.setOnItemClickListener(mItemClickListener);
		mDataModel = new BankNameListDM(PAGE_SIZE, mContext, TAG);
	}

	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
//  		   Intent intent = new Intent(mContext, ZiliaoRenzhengAct.class);
//			BankNameListEntity.BankNameItem mInfo = listViewDataAdapter.getDataList().get(position);
//			String driver_id = mInfo.id;
//			intent.putExtra("id", driver_id);
//			setResult(RESULT_OK,intent);
//			finish();


		}

	};



	private String mSections = "＃ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public class CollatorComparator implements Comparator<Object> {
		// Collator collator = Collator.getInstance();
		public int compare(Object element1, Object element2) {
			BankNameListEntity.BankNameItem info1 = (BankNameListEntity.BankNameItem) element1;
			BankNameListEntity.BankNameItem info2 = (BankNameListEntity.BankNameItem) element2;
			String str1 = info1.name;
			String str2 = info2.name;
			if (TextUtils.isEmpty(str1)) {
				str1 = " ";
			}
			if (TextUtils.isEmpty(str2)) {
				str2 = " ";
			}
			char ch1 = str1.charAt(0);
			char ch2 = str2.charAt(0);

			if (StringMatcher.isChineseCharacter(ch1)) {
				ch1 = StringMatcher.toPinYin(ch1).charAt(0);
			}
			if (StringMatcher.isChineseCharacter(ch2)) {
				ch2 = StringMatcher.toPinYin(ch2).charAt(0);
			}
			char temp1 = String.valueOf(ch1).toUpperCase().charAt(0);
			char temp2 = String.valueOf(ch2).toUpperCase().charAt(0);
			return temp1 - temp2;
		}
	}

	private boolean isSectionCharacter(char ch) {
		for (int i = 0; i < mSections.length(); i++) {
			char temp = StringMatcher.getUpcaseCharacter(String.valueOf(ch));
			if (temp == mSections.charAt(i)) {
				return true;
			}
		}
		return false;
	}

	List<BankNameListEntity.BankNameItem> mDriverInfoList;

	private void addGroupFlag(List<BankNameListEntity.BankNameItem> list) {
		ArrayList<BankNameListEntity.BankNameItem> list2 = new ArrayList<BankNameListEntity.BankNameItem>();
		char lastCh = 0;
		for (int i = 0; i < list.size(); i++) {
			String name = (String) list.get(i).name;
			char ch = 0;
			if(!TextUtils.isEmpty(name)){
				ch = StringMatcher.getUpcaseCharacter(name);
			}
			if (!isSectionCharacter(ch)) {
				ch = '#';
			}
			if (ch != lastCh) {
				BankNameListEntity.BankNameItem driverInfo = new BankNameListEntity.BankNameItem();
				driverInfo.id = "-1";
				if (isSectionCharacter(ch)) {
					driverInfo.name = String.valueOf(ch);
					lastCh = ch;
				} else {
					driverInfo.name = "#";
					lastCh = '#';
				}
				list2.add(driverInfo);
			}
			list2.add(list.get(i));
		}
		list.clear();
		list.addAll(list2);
	}




	private void initView() {
		ShowBackView();
		setTitle("银行名称");
	}

	@Override
	public void onClickListener(int id) {
		switch (id) {
		case R.id.about_us_rl:
			break;
		default:
			break;
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		cancelProgressDialog();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}
}