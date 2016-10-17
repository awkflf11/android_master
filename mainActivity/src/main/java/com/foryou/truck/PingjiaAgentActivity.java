package com.foryou.truck;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foryou.truck.entity.CommentEntity.ComtentData;
import com.foryou.truck.parser.BaseJsonParser;
import com.foryou.truck.parser.CommentJsonParser;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.Constant;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UtilsLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @des:评论经纪人
 */
public class PingjiaAgentActivity extends BaseActivity {
	private String TAG = "PingjiaAgentActivity";
	private Context mContext;
	private String order_id;
	@BindView(id = R.id.danhao)
	private TextView mDanhao;
//	@BindView(id = R.id.place)
//	private TextView mPlace;
	@BindView(id = R.id.start_place)
	private TextView startPlaceTv;
	@BindView(id = R.id.end_place)
	private TextView endPlaceTv;



	@BindView(id = R.id.time)
	private TextView mTime;
	@BindView(id = R.id.product_name)
	private TextView mProductName;
	@BindView(id = R.id.agent_name)
	private TextView mAgentComment;
	@BindView(id = R.id.comment_time)
	private TextView mAgentCommentTime;
	@BindView(id = R.id.content)
	private TextView mAgentCommentContent;
	@BindView(id = R.id.a2c_star_img)
	private ImageView mA2cStarImg;
	@BindView(id = R.id.agent_star1, click = true)
	private ImageView mAgentStar1;
	@BindView(id = R.id.agent_star2, click = true)
	private ImageView mAgentStar2;
	@BindView(id = R.id.agent_star3, click = true)
	private ImageView mAgentStar3;
	@BindView(id = R.id.agent_star4, click = true)
	private ImageView mAgentStar4;
	@BindView(id = R.id.agent_star5, click = true)
	private ImageView mAgentStar5;
	@BindView(id = R.id.driver_star1, click = true)
	private ImageView mDriverStar1;
	@BindView(id = R.id.driver_star2, click = true)
	private ImageView mDriverStar2;
	@BindView(id = R.id.driver_star3, click = true)
	private ImageView mDriverStar3;
	@BindView(id = R.id.driver_star4, click = true)
	private ImageView mDriverStar4;
	@BindView(id = R.id.driver_star5, click = true)
	private ImageView mDriverStar5;

	@BindView(id = R.id.pingjia_agent_edit)
	private EditText mAgentEdit;
	@BindView(id = R.id.pingjia_driver_edit)
	private EditText mDriverEdit;
	@BindView(id = R.id.save_btn, click = true)
	private Button mSaveBtn;
	@BindView(id = R.id.agent_comment_layout)
	private RelativeLayout mAgentCommentLayout;
	@BindView(id = R.id.comment_driver_layout)
	private RelativeLayout mDriverCommentLayout;
	private int[] startImgArr = { R.drawable.star1, R.drawable.star2,
			R.drawable.star3, R.drawable.star4, R.drawable.star5 };
	private ImageView[] mAgentStarViewArr, mDriverStarViewArr;
	private int mAgentStarLevel = 0, mDriverStarLevel = 0;
	private boolean driver_array;

	@Override
	public void setRootView() {
		// TODO Auto-generated method stub
		super.setRootView();
		setContentView(R.layout.pingjia_agent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ShowBackView();
		setTitle("评价经纪人");
		mAgentStarViewArr = new ImageView[5];
		mAgentStarViewArr[0] = mAgentStar1;
		mAgentStarViewArr[1] = mAgentStar2;
		mAgentStarViewArr[2] = mAgentStar3;
		mAgentStarViewArr[3] = mAgentStar4;
		mAgentStarViewArr[4] = mAgentStar5;

		mDriverStarViewArr = new ImageView[5];
		mDriverStarViewArr[0] = mDriverStar1;
		mDriverStarViewArr[1] = mDriverStar2;
		mDriverStarViewArr[2] = mDriverStar3;
		mDriverStarViewArr[3] = mDriverStar4;
		mDriverStarViewArr[4] = mDriverStar5;

		mContext = this;
		order_id = getIntent().getStringExtra("order_id");
		driver_array = getIntent().getBooleanExtra("driver_array", false);
		if (driver_array) {
			mDriverCommentLayout.setVisibility(android.view.View.VISIBLE);
		} else {
			mDriverCommentLayout.setVisibility(android.view.View.GONE);
		}
		//ToastUtils.toast(mContext,"您的订单已完成，请评价经纪人和司机~");
		getComment();
	}

	private void initData(CommentJsonParser mCommentParser) {
		ComtentData data = mCommentParser.entity.data;
		if (data.a2c_id.equals("")) {
			mAgentCommentLayout.setVisibility(android.view.View.GONE);
		} else {
			mAgentCommentLayout.setVisibility(android.view.View.VISIBLE);
			mAgentComment.setText("经纪人" + data.agent_name + "对您的评价");
			mAgentCommentTime.setText(data.a2c_time);
			mAgentCommentContent.setText(data.a2c_comment);
			if (Constant.isNumeric(data.a2c_level)) {
				if ((Integer.valueOf(data.a2c_level) > 0)
						&& (Integer.valueOf(data.a2c_level) <= 5)) {
					mA2cStarImg.setBackgroundResource(startImgArr[Integer
							.valueOf(data.a2c_level) - 1]);
				}
			}
		}
		mDanhao.setText("单号: " + data.order_sn);
		//mPlace.setText(data.start_place + " 至 " + data.end_place);
		startPlaceTv.setText(data.start_place_short );
		endPlaceTv.setText(data.end_place_short);
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		// 例如：cc_time=1291778220
		long lcc_time = Long.valueOf(data.confirm_time);
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));
		mTime.setText(re_StrTime);
		mProductName.setText(data.goods_name);

		if (Constant.isNumeric(data.c2a_level)) {
			setAgentStar(mAgentStarViewArr, Integer.valueOf(data.c2a_level));
		}
		mAgentEdit.setText(data.c2a_comment);

		if (driver_array) {
			if (Constant.isNumeric(data.c2d_level)) {
				setAgentStar(mDriverStarViewArr,
						Integer.valueOf(data.c2d_level));
			}
			mDriverEdit.setText(data.c2d_comment);
		}
	}

	private void CommentAdd() {

		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("order_id", "" + order_id);
		parmas.put("c2a_id", mCommentParser.entity.data.c2a_id);
		parmas.put("c2a_level", "" + mAgentStarLevel);
		parmas.put("c2a_comment", mAgentEdit.getText().toString());
		parmas.put("c2d_id", "" + mCommentParser.entity.data.c2d_id);
		parmas.put("c2d_level", "" + mDriverStarLevel);
		parmas.put("c2d_comment", mDriverEdit.getText().toString());
		UtilsLog.i(TAG,"NetWork==="+mDriverEdit.getText().toString());

		NetWorkUtils.CommentAdd(this, TAG, parmas,
				new NetWorkUtils.NetJsonRespon() {

					@Override
					public void onRespon(BaseJsonParser parser) {
						// TODO Auto-generated method stub
						ToastUtils.toast(mContext, "添加评论成功");
						finish();
					}
				});
	}

	CommentJsonParser mCommentParser;

	private void getComment() {
		Map<String, String> parmas = new HashMap<String, String>();
		parmas.put("order_id", "" + order_id);
		NetWorkUtils.getComment(this, TAG, parmas,
				new NetWorkUtils.NetJsonRespon() {

					@Override
					public void onRespon(BaseJsonParser parser) {
						// TODO Auto-generated method stub
						mCommentParser = (CommentJsonParser) parser;
						initData(mCommentParser);
					}
				});
	}

	@Override
	public void onStop() {
		super.onStop();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}

	private void setAgentStar(ImageView[] arr, int num) {

		if (arr == mAgentStarViewArr) {
			mAgentStarLevel = num;
		} else {
			mDriverStarLevel = num;
		}

		for (int i = 0; i < 5; i++) {
			if (i < num) {
				arr[i].setBackgroundResource(R.drawable.star_haiglight);
			} else {
				arr[i].setBackgroundResource(R.drawable.star);
			}
		}
	}

	@Override
	public void onClickListener(int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.agent_star1:
			setAgentStar(mAgentStarViewArr, 1);
			break;
		case R.id.agent_star2:
			setAgentStar(mAgentStarViewArr, 2);
			break;
		case R.id.agent_star3:
			setAgentStar(mAgentStarViewArr, 3);
			break;
		case R.id.agent_star4:
			setAgentStar(mAgentStarViewArr, 4);
			break;
		case R.id.agent_star5:
			setAgentStar(mAgentStarViewArr, 5);
			break;
		case R.id.driver_star1:
			setAgentStar(mDriverStarViewArr, 1);
			break;
		case R.id.driver_star2:
			setAgentStar(mDriverStarViewArr, 2);
			break;
		case R.id.driver_star3:
			setAgentStar(mDriverStarViewArr, 3);
			break;
		case R.id.driver_star4:
			setAgentStar(mDriverStarViewArr, 4);
			break;
		case R.id.driver_star5:
			setAgentStar(mDriverStarViewArr, 5);
			break;
		case R.id.save_btn:
			if (mAgentStarLevel <= 0 && mDriverStarLevel <=0) {
				ToastUtils.toast(mContext, "请评价经纪人星级");
				return;
			}

			CommentAdd();
			break;
		}
	}
}
