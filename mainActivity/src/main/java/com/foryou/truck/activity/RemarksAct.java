package com.foryou.truck.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.AdapterView.OnItemClickListener;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.foryou.truck.BaseActivity;
import com.foryou.truck.MyApplication;
import com.foryou.truck.R;
import com.foryou.truck.adapter.GridViewAdapter;
import com.foryou.truck.count.TongjiModel;
import com.foryou.truck.entity.CommonTagEntity;
import com.foryou.truck.model.Item;
import com.foryou.truck.net.MLHttpConstant;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.parser.CarLoadParser;
import com.foryou.truck.parser.CommonTagParser;
import com.foryou.truck.parser.SimpleJasonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.NormalNetworkRequest;

import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.util.NetWorkUtils.NetJsonRespon;
import com.foryou.truck.view.GridViewForScrollView;
import com.tencent.mm.sdk.platformtools.Util;

/**
 * @des:备注的页面
 */
public class RemarksAct extends BaseActivity {
	private static final String TAG = "RemarksAct";
	private Context mContext;
	//
	@BindView(id = R.id.tag_beizhu_gv)
	private GridViewForScrollView grid;
	@BindView(id = R.id.beizhu_content)
	private EditText beiZhuContent;
	@BindView(id = R.id.save_beizhu_bt, click = true)
	private Button saveBeizhuBn;
	@BindView(id = R.id.single_rg, click = true)
	private RadioGroup singleRg;
	@BindView(id = R.id.rb1, click = true)
	private RadioButton rb1;
	@BindView(id = R.id.rb2, click = true)
	private RadioButton rb2;
	@BindView(id = R.id.rb3, click = true)
	private RadioButton rb3;
	@BindView(id = R.id.rb4, click = true)
	private RadioButton rb4;
	private GridViewAdapter adapter;
	private ImageView imageview;
	private ImageView imageviews;
	private Boolean bl = true;
	private List<Item> datas;
	public Button bt;
	List<Map<String, Object>> mutiTagList = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> singleTagList = new ArrayList<Map<String, Object>>();
	public int singleIndex = -1;
	public String singleStr = "";
	public String singleKeyStr = "";
	public List<String> doubleList = new ArrayList<String>();
	public List<String> dounleKeyList = new ArrayList<String>();
	String remark = "";
	String mutiAndSingleText = "";
	List<Integer> list = new ArrayList<Integer>();
	 CommonTagEntity commonTagEntity;
	public RelativeLayout mGlobleBackView;

	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.act_remarks);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		initView();
		remark = getIntent().getStringExtra("remark");
		mutiAndSingleText = getIntent().getStringExtra("mutiAndSingleText");
        if(!TextUtils.isEmpty(remark)){
        	beiZhuContent.setText(remark);
        }
		loadNetData();
		if (!TextUtils.isEmpty(mutiAndSingleText)) {
			initSingleData();
			initMutiData();
		}
		initSingleClick();
	}
	private void loadNetData() {	
		 commonTagEntity = SharePerfenceUtil.getCommonTagData(mContext);
		 if(commonTagEntity==null){
			 NetWorkUtils.SaveCommonTag(mContext, new NetJsonRespon(){
					@Override
					public void onRespon(BaseJsonParser parser) {
						// TODO Auto-generated method stub
						CommonTagParser mParser = (CommonTagParser) parser;
						commonTagEntity = mParser.entity;
						if(commonTagEntity == null){
							ToastUtils.toast(mContext, "正在加载配置文件，请稍候再试");
							return;
						}else{
							loadNetData2();
						}
					}
				});
		 }else{
			 loadNetData2();
			 UtilsLog.i(TAG, "commonTagEntity==" + commonTagEntity.toString());
		 }

	}

	protected void loadNetData2() {
		if(commonTagEntity==null||commonTagEntity.data==null||commonTagEntity.data.tag==null){
			ToastUtils.toast(mContext, "正在加载配置文件，请稍候再试");
			return;
		}
		for (int i = 0; i < commonTagEntity.data.tag.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("tag_key", commonTagEntity.data.tag.get(i).key);
			map.put("tag_vaule", commonTagEntity.data.tag.get(i).value);
			mutiTagList.add(map);
		}
		initMutiDefaultData();
		//单选
		for (int i = 0; i < commonTagEntity.data.load_type.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("load_type_key", commonTagEntity.data.load_type.get(i).key);
			map.put("load_type_vaule", commonTagEntity.data.load_type.get(i).value);
			singleTagList.add(map);
		}
		UtilsLog.i(TAG, "commonTagEntity.data.load_type.size===" + commonTagEntity.data.load_type.size());
		if( commonTagEntity.data.load_type.size()>=4){
			rb1.setText(commonTagEntity.data.load_type.get(0).value);
			rb2.setText(commonTagEntity.data.load_type.get(1).value);
			rb3.setText(commonTagEntity.data.load_type.get(2).value);
			rb4.setText(commonTagEntity.data.load_type.get(3).value);
		}
	}

	public void initView() {
		ShowBackView();
		setTitle("备注");
	}

	private void initSingleData() {
		if (singleTagList == null) {
			return;
		}
		for (int i = 0; i < singleTagList.size(); i++) {
			if (mutiAndSingleText.contains((String) singleTagList.get(i).get("load_type_vaule"))) {
				singleStr = (String) singleTagList.get(i).get("load_type_vaule");
				singleIndex = i;
			}
		}
		injudgeSingleView(singleIndex);
	}

	private void initMutiData() {
		if (mutiTagList == null) {
			return;
		}

		for (int i = 0; i < mutiTagList.size(); i++) {
			if (mutiAndSingleText.contains((String) mutiTagList.get(i).get("tag_vaule"))) {
				list.add(i);
			}
		}
		for (int i = 0; i < list.size(); i++) {
			Item tempItem = datas.get(list.get(i));
			tempItem.setSelect(!tempItem.isSelect());
			if (tempItem.isSelect()) {
				if (!doubleList.contains(tempItem.tagText)) {
					doubleList.add(tempItem.tagText);
					dounleKeyList.add(tempItem.tagKey);					 
				}
			} else {
				if (doubleList.contains(tempItem.tagText)) {
					doubleList.remove(tempItem.tagText);
					dounleKeyList.remove(tempItem.tagKey);
				}
			}
			adapter.notifyDataSetChanged();
		}

	}

	private void initMutiDefaultData() {
		datas = new ArrayList<Item>();
		if (mutiTagList == null) {
			return;
		}
		for (int i = 0; i < mutiTagList.size(); i++) {
			Item tempItem = new Item();
			tempItem.tagText = (String) mutiTagList.get(i).get("tag_vaule");
			tempItem.tagKey = (String) mutiTagList.get(i).get("tag_key");
			tempItem.setId(i);
			tempItem.setSelect(false);

			datas.add(tempItem);
		}
		grid = (GridViewForScrollView) findViewById(R.id.tag_beizhu_gv);
		adapter = new GridViewAdapter(this, datas);
		grid.setAdapter(adapter);
		grid.setSelector(new ColorDrawable(Color.TRANSPARENT));

		grid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Map<Integer, Object> map = new HashMap<Integer, Object>();
				Item tempItem = datas.get(position);
				tempItem.setSelect(!tempItem.isSelect());
				// Integer iv = (Integer) parent.getItemAtPosition(position);
				// adapter.setSeclection(iv);
				if (tempItem.isSelect()) {
					if (!doubleList.contains(tempItem.tagText)) {
						doubleList.add(tempItem.tagText);
						dounleKeyList.add(tempItem.tagKey);
					}
				} else {
					if (doubleList.contains(tempItem.tagText)) {
						doubleList.remove(tempItem.tagText);
						dounleKeyList.remove(tempItem.tagKey);
					}
				}
				adapter.notifyDataSetChanged();
			}
		});

	}

	public String listToString(List list, char separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if(i==list.size()-1){
				sb.append(list.get(i));
			}else{
				sb.append(list.get(i)).append(separator);
			}

		}
		return sb.toString().substring(0, sb.toString().length());
	}

	public void saveBeizhuClicked() {
//		if (doubleList == null || doubleList.size() == 0) {
//			ToastUtils.toast(mContext, "请选择上面两行的的多选标签");
//			return;
//		}
//		if (TextUtils.isEmpty(listToString(doubleList, ','))) {
//			ToastUtils.toast(mContext, "请选择上面两行的的多选标签");
//			return;
//		}
//		if (TextUtils.isEmpty(singleStr)) {
//			ToastUtils.toast(mContext, "请选择最下面的单选标签");
//			return;
//		}
//		if (beiZhuContent.getText().toString().trim().equals("")) {
//			ToastUtils.toast(mContext, "您没有填写任何内容");
//			return;
//		}
		saveBeizhuClicked2();
	}

	private void saveBeizhuClicked2() {
		// TODO Auto-generated method stub
		//String totalTag = listToString(doubleList, ',') + "" + singleStr ;
		Intent intent = new Intent();
		intent.putExtra("singleText", singleStr );
		intent.putExtra("singleKey", singleKeyStr);
		if(doubleList!=null){
			intent.putExtra("mutiText", listToString(doubleList, ',') );
		}else{
			intent.putExtra("mutiText","");
		}
		if(dounleKeyList!=null){
			intent.putExtra("mutiKey", listToString(dounleKeyList, ','));
		}else{
			intent.putExtra("mutiKey", "");
		}
		intent.putExtra("remark", beiZhuContent.getText().toString().trim());
		
        //UtilsLog.i(TAG, "mutiKey==="+listToString(dounleKeyList, ','));
        UtilsLog.i(TAG, "singleKey=="+singleKeyStr);
		this.setResult(100, intent);
		finish();
	}

	@Override
	public void onClickListener(int id) {
		switch (id) {
		case R.id.save_beizhu_bt:
			TongjiModel.addEvent(mContext, "备注信息", TongjiModel.TYPE_BUTTON_CLIKC, "常用标签");
			saveBeizhuClicked();
			break;
		default:
			break;
		}
	}

	private void initSingleClick() {
		singleRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb1:
					injudgeSingleView(0);
					break;
				case R.id.rb2:
					injudgeSingleView(1);
					break;
				case R.id.rb3:
					injudgeSingleView(2);
					break;
				case R.id.rb4:
					injudgeSingleView(3);
					break;
				default:
					break;
				}
			}
		});

	}

	private void injudgeSingleView(int i) {
		if (singleTagList == null||singleTagList.size()<4) {
			return;
		}
		if (i == 0) {
			singleStr = (String) singleTagList.get(0).get("load_type_vaule");
			singleKeyStr = (String) singleTagList.get(0).get("load_type_key");
			rb1.setText(singleStr);
			rb1.setBackgroundColor(Color.parseColor("#00a9e0"));
			rb1.setTextColor(Color.parseColor("#ffffff"));
			rb2.setTextColor(Color.parseColor("#ff000000"));
			rb3.setTextColor(Color.parseColor("#ff000000"));
			rb4.setTextColor(Color.parseColor("#ff000000"));
			rb2.setBackgroundColor(Color.parseColor("#f1f1f1"));
			rb3.setBackgroundColor(Color.parseColor("#f1f1f1"));
			rb4.setBackgroundColor(Color.parseColor("#f1f1f1"));
		} else if (i == 1) {
			singleStr = (String) singleTagList.get(1).get("load_type_vaule");
			singleKeyStr = (String) singleTagList.get(1).get("load_type_key");
			rb2.setText(singleStr);
			rb1.setBackgroundColor(Color.parseColor("#f1f1f1"));
			rb2.setBackgroundColor(Color.parseColor("#00a9e0"));
			rb3.setBackgroundColor(Color.parseColor("#f1f1f1"));
			rb4.setBackgroundColor(Color.parseColor("#f1f1f1"));
			rb1.setTextColor(Color.parseColor("#ff000000"));
			rb2.setTextColor(Color.parseColor("#ffffff"));
			rb3.setTextColor(Color.parseColor("#ff000000"));
			rb4.setTextColor(Color.parseColor("#ff000000"));
		} else if (i == 2) {
			singleStr = (String) singleTagList.get(2).get("load_type_vaule");
			singleKeyStr = (String) singleTagList.get(2).get("load_type_key");
			rb3.setText(singleStr);
			rb1.setBackgroundColor(Color.parseColor("#f1f1f1"));
			rb2.setBackgroundColor(Color.parseColor("#f1f1f1"));
			rb3.setBackgroundColor(Color.parseColor("#00a9e0"));
			rb4.setBackgroundColor(Color.parseColor("#f1f1f1"));
			rb1.setTextColor(Color.parseColor("#ff000000"));
			rb2.setTextColor(Color.parseColor("#ff000000"));
			rb3.setTextColor(Color.parseColor("#ffffff"));
			rb4.setTextColor(Color.parseColor("#ff000000"));
		} else if (i == 3) {
			singleStr = (String) singleTagList.get(3).get("load_type_vaule");
			singleKeyStr = (String) singleTagList.get(3).get("load_type_key");
			rb4.setText(singleStr);
			rb1.setBackgroundColor(Color.parseColor("#f1f1f1"));
			rb2.setBackgroundColor(Color.parseColor("#f1f1f1"));
			rb3.setBackgroundColor(Color.parseColor("#f1f1f1"));
			rb4.setBackgroundColor(Color.parseColor("#00a9e0"));
			rb1.setTextColor(Color.parseColor("#ff000000"));
			rb2.setTextColor(Color.parseColor("#ff000000"));
			rb3.setTextColor(Color.parseColor("#ff000000"));
			rb4.setTextColor(Color.parseColor("#ffffff"));
		}
	}

}