package com.foryou.truck.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.R;

public class MutiChooseBtn extends RelativeLayout implements
		View.OnClickListener {
	private ImageView mImage, mImage1;
	private TextView mText, mText1;
	private RelativeLayout mlayout1, mlayout2;
	private boolean firstChoose= false,secondChoose=false;

	public MutiChooseBtn(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MutiChooseBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(String[] str) {
		mlayout1 = (RelativeLayout) findViewById(R.id.layout);
		mlayout1.setOnClickListener(this);
		mlayout2 = (RelativeLayout) findViewById(R.id.layout1);
		mlayout2.setOnClickListener(this);

		mImage = (ImageView) findViewById(R.id.img);
		mText = (TextView) findViewById(R.id.tx);
		mText.setText(str[0]);

		mImage1 = (ImageView) findViewById(R.id.img1);
		mText1 = (TextView) findViewById(R.id.tx1);
		mText1.setText(str[1]);
	}
	
	public void clearClickListener(){
		mlayout1.setOnClickListener(null);
		mlayout2.setOnClickListener(null);
	}
	
	public void setChooseStatus(boolean left,boolean right){
		if(left){
			//mImage.setVisibility(android.view.View.VISIBLE);
			mText.setTextColor(this.getResources().getColor(
					R.color.choose_color));
			firstChoose = true;
			
		}else{
			//mImage.setVisibility(android.view.View.GONE);
			mText.setTextColor(this.getResources().getColor(
					R.color.unchoose_color));
			firstChoose = false;
		}
		
		if(right){
			//mImage1.setVisibility(android.view.View.VISIBLE);
			mText1.setTextColor(this.getResources().getColor(
					R.color.choose_color));
			secondChoose = true;
		}else{
			//mImage1.setVisibility(android.view.View.GONE);
			mText1.setTextColor(this.getResources().getColor(
					R.color.unchoose_color));
			secondChoose = false;
		}
	}
	
	public int getChooseIndex(){
		if(firstChoose)
			return 0;
		if(secondChoose)
			return 1;
		return -1;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout:
			firstChoose = true;
			secondChoose = false;
			setChooseStatus(true,false);
			break;
		case R.id.layout1:
			firstChoose = false;
			secondChoose = true;
			setChooseStatus(false,true);
			break;
		}
	}
}
