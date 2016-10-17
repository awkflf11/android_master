package com.example.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.activity.Item;
import com.example.activity.R;

public class GridViewAdapter extends BaseAdapter {
	// private Integer[] myImageIds;
	private LayoutInflater inflater;
	private ImageView imageview;
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
		convertView = inflater.inflate(R.layout.image, null);
		Item tempItem = datas.get(position);
		imageview = (ImageView) convertView.findViewById(R.id.ImageView);
		if (tempItem.isSelect()) {
			imageview.setImageResource(R.drawable.right);
		} else {
			imageview.setImageResource(R.drawable.kong);
		}
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
