package com.example.photowall.ui;

import com.example.photowall.R;
import com.example.photowall.adapter.ImageAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

public class MainActivity extends Activity {
	private GridView mGridView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		initView();
	}

	private void initView() {
		mGridView=(GridView) findViewById(R.id.gridView);
		mGridView.setAdapter(new ImageAdapter(mGridView, getApplicationContext()));
	}
}
