package com.foryou.truck.adapter;

import java.util.List;
import com.foryou.truck.R;
import com.foryou.truck.model.Item;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
public class GridViewAdapter extends BaseAdapter {

	// private Integer[] myImageIds;
	private LayoutInflater inflater;
	private TextView tv;
	// private ImageView imageviews;
	private int clickTemp = -1;
	private Context context;
	private List<Item> datas;

	public GridViewAdapter(Context context, List<Item> datas) {
		this.context = context;
		// this.myImageIds=myImageIds;
		this.datas = datas;
		this.inflater = LayoutInflater.from(context);
	}

	public int getCount() {

		return datas.size();
	}

	public Object getItem(int position) {

		return position;
	}

	public long getItemId(int position) {

		return position;
	}

	public void setSeclection(int position) {
		clickTemp = position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.item_remarks, null);
		Item tempItem = datas.get(position);
		tv = (TextView) convertView.findViewById(R.id.imageText);
		if (tempItem.isSelect()) {
			tv.setBackgroundColor(Color.parseColor("#00a9e0"));
			tv.setTextColor(Color.parseColor("#ffffff"));

		} else {
			tv.setBackgroundColor(Color.parseColor("#f1f1f1"));
			tv.setTextColor(Color.parseColor("#101010"));
		}
		tv.setText(datas.get(position).tagText);
		/**
		 * imageviews=(ImageView)convertView.findViewById(R.id.Imageviews);
		 * imageview.setVisibility(View.VISIBLE);
		 * imageviews.setVisibility(View.GONE); if(clickTemp==position){
		 * imageview.setVisibility(View.GONE);
		 * imageviews.setVisibility(View.VISIBLE); }else{
		 * imageview.setVisibility(View.VISIBLE);
		 * imageviews.setVisibility(View.GONE); }
		 */

		return convertView;
	}

}
