package com.foryou.truck.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.foryou.truck.entity.AreasEntity;
import com.foryou.truck.entity.BusinessListEntity;
import com.foryou.truck.entity.CarLoadEntity;
import com.foryou.truck.entity.CommonConfigEntity;
import com.foryou.truck.entity.CommonTagEntity;
import com.foryou.truck.entity.UserContractEntity;
import com.foryou.truck.entity.UserDetailEntity;
import com.google.gson.Gson;

public class SharePerfenceUtil {
	public final static String EXIT_NOT_CLEAR = "exit_not_clear";
	public final static String EXIT_CLIEAR = "exit_clear";

	public final static String FIRST_LOGIN = "first_login";
	public final static String KEY = "key";
	public final static String UID = "uid";
	public final static String TOKEN = "token";
	public final static String NAME = "name";
	public final static String MOBILE = "mobile";
	public final static String USER_TYPE = "user_type";
	public final static String USER_GENDER = "user_gender";
	public final static String ACCOUNT_TYPE_KEY = "account_type_key";
	public final static String ACCOUNT_TYPE_TEXT = "account_type_text";

	public final static String GT_CID = "cid";
	public final static String GT_LOADCLASS = "load_class";
	public final static String GT_FYID = "GT_ORDER_ID";
	public final static String SAVE_CALSS_NAME = "class_name";

	public static void SetFirstLogin(Context context, boolean flag) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		sp.edit().putBoolean(EXIT_CLIEAR, flag).commit();
	}

	public static boolean getFirstLogin(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		return sp.getBoolean(EXIT_CLIEAR, true);
	}
//
	public static void SetFirstNewGuide(Context context, boolean flag) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		sp.edit().putBoolean("FirstNewGuide", flag).commit();
   }
	public static boolean getFirstNewGuide(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		return sp.getBoolean("FirstNewGuide", true);
	}

	public static String getKey(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		return sp.getString(KEY, "");
	}

	public static void setKey(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		sp.edit().putString(KEY, key).commit();
	}

	public static String getUid(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		return sp.getString(UID, "");
	}

	public static void setUid(Context context, String uid) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		sp.edit().putString(UID, uid).commit();
	}


	public static boolean IsLogin(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		return !sp.getString(KEY, "").equals("")
				&& !sp.getString(UID, "").equals("");
	}

	public static void setLoginErrorCount(Context context,int errorCount) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		sp.edit().putInt("login_error_count", errorCount).commit();
	}

	public static int getLoginErrorCount(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		return sp.getInt("login_error_count",-1);
	}


	public static void ClearAll(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		sp.edit().clear().commit();
	}

	// 保存User信息
	public static void SaveUserDetail(Context context, UserDetailEntity entity) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		Gson gson = new Gson();
		String str = gson.toJson(entity);
		// Log.i("aa","gson:"+str);
		sp.edit().putString("user_detail", str).commit();
	}

	public static UserDetailEntity getUserDetail(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		String detail = sp.getString("user_detail", "");
		Gson gson = new Gson();
		UserDetailEntity entity = gson.fromJson(detail, UserDetailEntity.class);
		return entity;
	}

	public static boolean IsContractPerson(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		String detail = sp.getString("user_detail", "");
		Gson gson = new Gson();
		UserDetailEntity entity = gson.fromJson(detail, UserDetailEntity.class);
		return entity.data.type.equals("1");
	}

	public static void SaveUserContactData(Context context, String content) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		sp.edit().putString("user_contact", content).commit();
	}

	public static UserContractEntity getUserConTactData(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		String config = sp.getString("user_contact", "");
		Gson gson = new Gson();
		UserContractEntity entity = gson.fromJson(config,
				UserContractEntity.class);
		return entity;
	}

	public static void SaveGtCid(Context context, String cid) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		sp.edit().putString(GT_CID, cid).commit();
	}

	public static void SaveGtStartClassAndCid(Context context,
			String classname, String id) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		Editor edit = sp.edit();
		edit.putString(GT_LOADCLASS, classname);
		edit.putString(GT_FYID, id).commit();
	}

	public static String getLoadClass(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		return sp.getString(GT_LOADCLASS, "");
	}

	public static String getFYID(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		return sp.getString(GT_FYID, "");
	}

	public static void clearGTMESSAGE(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		Editor edit = sp.edit();
		edit.putString(GT_LOADCLASS, "");
		edit.putString(GT_FYID, "").commit();
	}

	public static String GetGtCid(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		return sp.getString(GT_CID, "");
	}

	public static void SaveBindGTSuccessResult(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		String content = sp.getString(GT_CID, "") + "_" + sp.getString(UID, "");
		sp.edit().putString("gt_result", content).commit();
	}

	public static boolean IsGTBindSuccess(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR, 0);
		String content = sp.getString(GT_CID, "gt") + "_"
				+ sp.getString(UID, "key");
		String result = sp.getString("gt_result", "");

		UtilsLog.i("SharePerfenceUtil", "result:" + result + ",content:"
				+ content);
		return result.equals(content);
	}

	// #####################################################################################
	// 退出账号后不清除的数据
	public static void saveUrlPath(Context context, String url){
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR,0);
		sp.edit().putString("url",url).commit();
	}

	public static String getUrlPath(Context context){
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR,0);
		String url = sp.getString("url",UrlConstant.TEST_URL);
		return url;
	}

	// ++++++++++++  保存apk的安装路径 ++++++++++
	public static void saveApkUrlPath(Context context, String url){
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR,0);
		sp.edit().putString("apkurl",url).commit();
	}

	public static String getApkUrlPath(Context context){
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR,0);
		String url = sp.getString("apkurl","");
		return url;
	}


	public static AreasEntity getAreaData(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		String detail = sp.getString("area_data", "");
		Gson gson = new Gson();
		AreasEntity entity = gson.fromJson(detail, AreasEntity.class);
		return entity;
	}

	public static void SaveAreaData(Context context,String area) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		sp.edit().putString("area_data", area).commit();
	}

	public static void SaveConfigData(Context context, CommonConfigEntity entity) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		Gson gson = new Gson();
		String str = gson.toJson(entity);
		// Log.i("aa","gson:"+str);
		sp.edit().putString("config", str).commit();
	}

	public static CommonConfigEntity getConfigData(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		String config = sp.getString("config", "");
		Gson gson = new Gson();
		CommonConfigEntity entity = gson.fromJson(config,
				CommonConfigEntity.class);
		return entity;
	}

	// 保存车的标准载重;
	public static void SaveCarLoadData(Context context, CarLoadEntity entity) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		Gson gson = new Gson();
		String str = gson.toJson(entity);
		sp.edit().putString("carload_data", str).commit();
	}

	public static CarLoadEntity getCarLoadData(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		String detail = sp.getString("carload_data", "");
		Gson gson = new Gson();
		CarLoadEntity entity = gson.fromJson(detail, CarLoadEntity.class);
		return entity;
	}

	public static void saveOrderListTabIndex(Context context,int index) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		sp.edit().putInt("orderlist_index", index).commit();
	}

	public static int getOrderListTabIndex(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		return sp.getInt("orderlist_index",-1);
	}

	public static void saveBusinessList(Context context,String content){
		SharedPreferences sp = context.getSharedPreferences(EXIT_CLIEAR,0);
		sp.edit().putString("businesslist",content).commit();
	}

	public static BusinessListEntity.BusinessData getBusinessList(Context context){
		String content = context.getSharedPreferences(EXIT_CLIEAR,0).getString("businesslist","");
		if(TextUtils.isEmpty(content)){
			return null;
		}
		Gson gson = new Gson();
		BusinessListEntity entity = gson.fromJson(content, BusinessListEntity.class);
		return entity.data;
	}


	// 保存常用的标签：
	public static void SaveCommonTagData(Context context, CommonTagEntity entity) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		Gson gson = new Gson();
		String str = gson.toJson(entity);
		sp.edit().putString("common_tag_data", str).commit();
	}

	public static CommonTagEntity getCommonTagData(Context context) {
		SharedPreferences sp = context.getSharedPreferences(EXIT_NOT_CLEAR, 0);
		String detail = sp.getString("common_tag_data", "");
		Gson gson = new Gson();
		CommonTagEntity entity = gson.fromJson(detail, CommonTagEntity.class);
		return entity;
	}

}
