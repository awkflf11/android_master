package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.adapter.GridViewAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class RemarksAct extends Activity {

	private GridView grid;
	private GridViewAdapter adapter;
	private ImageView imageview;
	private ImageView imageviews;
	private Boolean bl = true;
	private List<Item> datas;
	public Integer[] myImageIds = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
	public Button bt;
	Item tempItem;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		datas = new ArrayList<Item>();

		for (int i = 0; i < 12; i++) {
			Item tempItem = new Item();
			tempItem.setId(i);
			tempItem.setSelect(false);
			datas.add(tempItem);
		}
		grid = (GridView) findViewById(R.id.grid);
		bt = (Button) findViewById(R.id.btn);
		adapter = new GridViewAdapter(RemarksAct.this, datas);

		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				tempItem = datas.get(position);
				tempItem.setSelect(!tempItem.isSelect());
				// Integer iv = (Integer) parent.getItemAtPosition(position);
				// adapter.setSeclection(iv);
				adapter.notifyDataSetChanged();
			}
		});

		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < datas.size(); i++) {
					Item tempItem = datas.get(i);
					if (tempItem.isSelect()) {
						Log.i("MyActivity", "isSelect==" + i + "");
					}
				}
			}
		});

		initFor();

	}

	private void initFor() {
		// TODO Auto-generated method stub
		String[] strs1 = { "1", "2", "3", "4", "5", "6" };
		String[] strs2 = { "2", "6" };
		String[] strs3 = new String[strs1.length];

		for (int i = 0; i < strs1.length; i++) {

			int j = 0;
			for (j = 0; j < strs2.length; j++) {

				if (strs1[i] == strs2[j]) {
					

					break;
				}
			}

			if (j == strs2.length - 1) {
				strs3[i] = strs1[i];
			}

		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strs3.length; i++) {
			sb.append(strs3[i]);
		}
		String s = sb.toString();
		Log.i("tag", s);

	}

}