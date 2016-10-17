package com.foryou.truck;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.foryou.truck.entity.UserDetailEntity;
import com.foryou.truck.parser.UpAvaterParser;
import com.foryou.truck.parser.UserDetailJsonParser;
import com.foryou.truck.tools.ImageLoaderUtils;
import com.foryou.truck.tools.SdCardUtil;
import com.foryou.truck.tools.StringUtils;
import com.foryou.truck.tools.Tools;
import com.foryou.truck.tools.UpAvatarUtils;
import com.foryou.truck.util.BindView;
import com.foryou.truck.util.ImageUtils;
import com.foryou.truck.util.NetWorkUtils;
import com.foryou.truck.util.NormalNetworkRequest;
import com.foryou.truck.util.SharePerfenceUtil;
import com.foryou.truck.util.ToastUtils;
import com.foryou.truck.util.UrlConstant;
import com.foryou.truck.util.UtilsLog;
import com.foryou.truck.view.WithDelImgEditText;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModifyMyInfoActivity extends BaseActivity {
	private String TAG = "ModifyMyInfoActivity";
	private Context mContext;
//	@BindView(id = R.id.female)
//	private TextView mFemale;
	@BindView(id = R.id.button1, click = true)
	private Button mSaveBtn;
	@BindView(id = R.id.name)
	private WithDelImgEditText mName;
	@BindView(id = R.id.mod_phone_tv)
	private TextView phoneTv;
//	@BindView(id = R.id.mod_sex_rl, click = true)
//	private RelativeLayout sexRL;
	@BindView(id = R.id.mod_avater_iv)
	private ImageView avatarIv;// 头像
	@BindView(id = R.id.mod_avater_rl, click = true)
	private RelativeLayout upAvatarRL;//
	private String tp = null;// 头像的base64字符串
	Drawable drawable;
	//
	private ImageLoaderUtils imageLoaderUtils=ImageLoaderUtils.getImageLoaderUtils();
	private DisplayImageOptions options;
	private ImageLoader imageLoader = imageLoaderUtils.getImageLoader();
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private String[] mPayTypeItem = { "男", "女" };
	UserDetailEntity userInfoEntity;

	@Override
	public void setRootView() {
		super.setRootView();
		setContentView(R.layout.modify_myinfo);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		initView();
		initData();
	}

	private void initView() {
		setTitle("我的资料");
		ShowBackView();
		mSaveBtn.setText("保存");
		options=imageLoaderUtils.setOptions(R.drawable.avatar_default,100);
	}

	private void initData() {
		UserDetailEntity mUserDetail = SharePerfenceUtil.getUserDetail(mContext);
		if (mUserDetail == null) {
			setDefault();
			return;
		}
		if (mUserDetail.data == null) {
			setDefault();
			return;
		}
		if (!TextUtils.isEmpty(mUserDetail.data.portrait)) {// 头像路径
			imageLoader.displayImage(mUserDetail.data.portrait, avatarIv,
					options, animateFirstListener);
		} else {
			avatarIv.setBackgroundResource(R.drawable.avatar_default);
		}
//		if (!TextUtils.isEmpty(mUserDetail.data.gender)) {
//			String gender = mUserDetail.data.gender;
//			if (gender.equals("0")) {
//				mFemale.setText("未设置");
//				mFemale.setTextColor(Color.parseColor("#c5c5c5"));
//			} else if (gender.equals("1")) {
//				mFemale.setText("男");
//				mFemale.setTextColor(Color.parseColor("#333333"));
//			} else {
//				mFemale.setText("女");
//				mFemale.setTextColor(Color.parseColor("#333333"));
//			}
//		} else {
//			mFemale.setText("未设置");
//			mFemale.setTextColor(Color.parseColor("#c5c5c5"));
//		}

		if (!TextUtils.isEmpty(mUserDetail.data.mobile)) {
			phoneTv.setText( StringUtils.getStyleText(mUserDetail.data.mobile) );
		} else {
			phoneTv.setText("未设置");
		}
		phoneTv.setTextColor(Color.parseColor("#c5c5c5"));

		if (!TextUtils.isEmpty(mUserDetail.data.name)) {
			mName.setText(mUserDetail.data.name);
			mName.setSelection(mName.getText().toString().length());
			mName.setTextColor(Color.parseColor("#333333"));
		} else {
			// mName.setText("未设置");
			mName.setSelection(mName.getText().length());
			mName.setTextColor(Color.parseColor("#c5c5c5"));
		}
	}

	private void setDefault() {
		// TODO Auto-generated method stub
		avatarIv.setBackgroundResource(R.drawable.avatar_default);
		// mName.setText("未设置");
		//mFemale.setText("未设置");
		phoneTv.setText("未设置");
		mName.setTextColor(Color.parseColor("#c5c5c5"));
		//mFemale.setTextColor(Color.parseColor("#c5c5c5"));
		phoneTv.setTextColor(Color.parseColor("#c5c5c5"));
		mName.setSelection(mName.getText().length());

	}

	class AlertListener implements OnClickListener {
		@Override
		public void onClick(DialogInterface view, int position) {
			//mFemale.setText(mPayTypeItem[position]);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:// 如果是直接从相册获取
			if (data != null) {
				ImageUtils.startPhotoZoom(mContext, data.getData());
			}
			break;
		case 2:// 如果是调用相机拍照时
			File temp = new File(SdCardUtil.getTouXiangPath(mContext)+ "avatar.png");
			ImageUtils.startPhotoZoom(mContext,Uri.fromFile(temp));
			break;
		case 3:// 取得裁剪后的图片
			/**
			 * 如果不验证的话，在剪裁之后如果发现不满意，要重新裁剪，丢弃当前功能时，会报NullException，
			 */
			if (data != null) {
				setPicToView(data);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * @保存裁剪之后的图片数据
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			if(photo==null){
				return;
			}
			//photo = ImageUtils.toRoundBitmap(photo);
			drawable = new BitmapDrawable(photo);
			tp = ImageUtils.bitmapToBase64(photo);
			if (!Tools.IsConnectToNetWork(mContext)) {
				ToastUtils.toast(mContext, "联网异常,请稍后再试");
				return;
			}
			// avatarIv.setBackgroundDrawable(drawable);  
			upAvatar();
			/**
			 * @将裁剪之后的图片以Base64Coder的字符方式上 传到服务器
			 */
			/*
			 * ByteArrayOutputStream stream = new ByteArrayOutputStream();
			 * photo.compress(Bitmap.CompressFormat.JPEG, 60, stream); byte[] b
			 * = stream.toByteArray(); // 将图片流以字符串形式存储下来 tp = new
			 * String(Base64Coder.encodeLines(b));
			 * 这个地方大家可以写下给服务器上传图片的实现，直接把tp直接上传就可以了， 服务器处理的方法是服务器那边的事了，吼吼
			 * 
			 * 如果下载到的服务器的数据还是以Base64Coder的形式的话，可以用以下方式转换 为我们可以用的图片类型就OK啦...吼吼
			 * Bitmap dBitmap = BitmapFactory.decodeFile(tp); Drawable drawable
			 * = new BitmapDrawable(dBitmap);
			 */
			// ib.setBackgroundDrawable(drawable);
			// iv.setBackgroundDrawable(drawable);
		}
	}

	private void ModifyUserInfo() {
		Map<String, String> parmas = new HashMap<String, String>();
		String url = UrlConstant.BASE_URL + "/user/update";
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
				mContext, Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						UtilsLog.i(TAG, "response:" + response);
						cancelProgressDialog();
						UserDetailJsonParser mParser = new UserDetailJsonParser();
						int result = mParser.parser(response);
						if (result == 1) {
							if (mParser.entity.status.equals("Y")) {
								ToastUtils.toast(mContext, "修改成功");
								// SharePerfenceUtil.setName(mContext,mName.getText().toString());
								UserDetailEntity userInfoEntity = SharePerfenceUtil.getUserDetail(mContext);
								userInfoEntity.data.name = mName.getText().toString().trim();
//								if (mFemale.getText().toString().equals("男")) {
//									userInfoEntity.data.gender = "1";
//								} else if (mFemale.getText().toString().equals("女")) {
//									userInfoEntity.data.gender = "2";
//								} else {
//									userInfoEntity.data.gender = "0";
//								}
								SharePerfenceUtil.SaveUserDetail(mContext, userInfoEntity);
								// SharePerfenceUtil.SaveUserDetail(mContext,mParser.entity);
								// setResult(RESULT_OK);
								finish();
							} else {
								ToastUtils.toast(mContext, mParser.entity.msg);
							}
						} else {
							Log.i(TAG, "解析错误");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (error instanceof NetworkError) {
							Log.i(TAG, "NetworkError");
						} else if (error instanceof ServerError) {
							Log.i(TAG, "ServerError");
						} else if (error instanceof AuthFailureError) {
							Log.i(TAG, "AuthFailureError");
						} else if (error instanceof ParseError) {
							Log.i(TAG, "ParseError");
						} else if (error instanceof NoConnectionError) {
							Log.i(TAG, "NoConnectionError");
						} else if (error instanceof TimeoutError) {
							Log.i(TAG, "TimeoutError");
						}
						cancelProgressDialog();
					}
				}, true) {

			@Override
			public Map<String, String> getPostBodyData() {
				Map<String, String> parmas = new HashMap<String, String>();
				//parmas.put("key", SharePerfenceUtil.getKey(mContext));
				parmas.put("uid", SharePerfenceUtil.getUid(mContext));
				parmas.put("name", mName.getText().toString());
//				if (mFemale.getText().toString().equals("男")) {
//					parmas.put("gender", "1");
//				} else if (mFemale.getText().toString().equals("女")) {
//					parmas.put("gender", "2");
//				} else {
//					parmas.put("gender", "0");
//				}
				return parmas;
			}
		};
		showProgressDialog();
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	@Override
	public void onClickListener(int id) {
		switch (id) {
		case R.id.mod_avater_rl:
			UpAvatarUtils.ShowPickDialog(mContext);
			break;
//		case R.id.mod_sex_rl: // female
//			new AlertDialog.Builder(this).setItems(mPayTypeItem, new AlertListener()).show();
//			break;
		case R.id.button1:
			if (!Tools.IsConnectToNetWork(mContext)) {
				ToastUtils.toast(mContext, "联网异常,请稍后再试");
				return;
			}
			if (!SharePerfenceUtil.IsLogin(mContext)) {
				ToastUtils.toast(this, "请登录后再修改个人信息");
				return;
			}

			if (mName.getText().toString().trim().equals("")
					|| mName.getText().toString().trim().equals("未设置")) {
				ToastUtils.toast(this, "姓名不能为空");
				return;
			}
			if(mName.getText().toString().trim().length()<2
					|| mName.getText().toString().trim().length()>8){
				ToastUtils.toast(this, "姓名的长度为2~8个汉字");
				return;
			}

			if(!StringUtils.isChinese(mName.getText().toString().trim())){
				ToastUtils.toast(this, "姓名请输入汉字字符");
				return;
			}
//			if (mFemale.getText().toString().equals("")
//					|| mFemale.getText().toString().equals("未设置")) {
//				ToastUtils.toast(this, "请选择性别");
//				return;
//			}
			ModifyUserInfo();
			break;
		}
	}

	private void upAvatar() {
		// String url = UrlConstant.BASE_URL + "/user/portraitUpdate";
		String url = UrlConstant.BASE_URL + "/user/portraitUpdate";
		final NormalNetworkRequest stringRequest = new NormalNetworkRequest(
				mContext, Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						UtilsLog.i(TAG, "response:" + response);
						cancelProgressDialog();
						UpAvaterParser mParser = new UpAvaterParser();
						int result = mParser.parser(response);
						if (result == 1) {
							if (mParser.entity.status.equals("Y")) {
								ToastUtils.toast(mContext, "上传头像成功");
								imageLoader.displayImage(
										mParser.entity.data.portrait, avatarIv, options, animateFirstListener);
								if(userInfoEntity!=null) {
									userInfoEntity.data.portrait = mParser.entity.data.portrait;
								}
								SharePerfenceUtil.SaveUserDetail(mContext, userInfoEntity);
							} else {
								ToastUtils.toast(mContext, mParser.entity.msg);
							}
						} else {
							Log.i(TAG, "解析错误");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (error instanceof NetworkError) {
							Log.i(TAG, "NetworkError");
						} else if (error instanceof AuthFailureError) {
							Log.i(TAG, "AuthFailureError");
						} else if (error instanceof ParseError) {
							Log.i(TAG, "ParseError");
						} else if (error instanceof NoConnectionError) {
							Log.i(TAG, "NoConnectionError");
						} else if (error instanceof TimeoutError) {
							Log.i(TAG, "TimeoutError");
						}
						cancelProgressDialog();
					}
				}, true) {
			@Override
			public Map<String, String> getPostBodyData() {
				Map<String, String> parmas = new HashMap<String, String>();
				parmas.put("imagePortrait", tp);// 头像的base64字符串
				parmas.put("uid", SharePerfenceUtil.getUid(mContext));
				// UtilsLog.i(TAG, "头像的base64字符串==================" + tp);
				return parmas;
			}
		};
		showProgressDialog();
		MyApplication.getInstance().addRequest(stringRequest, TAG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		userInfoEntity = SharePerfenceUtil.getUserDetail(mContext);
		if(userInfoEntity == null){
			NetWorkUtils.getUserDetail(mContext,TAG,null);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		cancelProgressDialog();
		MyApplication.getInstance().cancelAllRequests(TAG);
	}

	/** 图片加载监听事件 **/
	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());
		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500); // 设置image隐藏动画500ms
					displayedImages.add(imageUri);
				}

			}
		}
	}

}
