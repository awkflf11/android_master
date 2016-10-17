package com.foryou.truck.adapter;

import android.widget.SectionIndexer;
import com.foryou.truck.entity.BankNameListEntity;
import com.woozzu.android.util.StringMatcher;

import in.srain.cube.views.list.ListViewDataAdapter;
import in.srain.cube.views.list.ViewHolderCreator;

public class MyContentAdapter<ItemDataType> extends ListViewDataAdapter<ItemDataType> implements
		SectionIndexer {
	public static String mSections = "＃ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public MyContentAdapter() {
		// TODO Auto-generated constructor stub
		super();
	}

	public MyContentAdapter(ViewHolderCreator<ItemDataType> viewHolderCreator) {
		super(viewHolderCreator);
	}
	@Override
	public int getPositionForSection(int section) {
		// If there is no item for current section, previous section will be
		// selected
		for (int i = section; i >= 0; i--) {
			for (int j = 0; j < getCount(); j++) {
				BankNameListEntity.BankNameItem beanInfo = (BankNameListEntity.BankNameItem) getItem(j);
				String str = beanInfo.name;
				if (i == 0) {
					// For numeric section
					for (int k = 0; k <= 9; k++) {
						if (StringMatcher.match(String.valueOf(str.charAt(0)),
								String.valueOf(k)))
							return j;
					}
				} else {
					if (StringMatcher.match(String.valueOf(str.charAt(0)),
							String.valueOf(mSections.charAt(i))))
						return j;
				}
			}
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		View v;
//		Map<String, Object> map = dataList.get(position);
//		if ((Boolean) map.get("is_group_name")) {
//			TextView textView = new TextView(mContext);
//			textView.setText(String.valueOf(map.get("name")));
//			textView.setTextSize(14);
//			textView.setTextColor(Color.BLACK);
//			textView.setPadding(30, 0, 0, 0);
//			v = textView;
//		} else {
//			v = inflater.inflate(layoutId, parent, false);
//			bindView(position, v);
//			initViewClick(v, position);
//		}
//		return v;
//	}

	@Override
	public Object[] getSections() {
		String[] sections = new String[mSections.length()];
		for (int i = 0; i < mSections.length(); i++)
			sections[i] = String.valueOf(mSections.charAt(i));
		return sections;
	}

}
