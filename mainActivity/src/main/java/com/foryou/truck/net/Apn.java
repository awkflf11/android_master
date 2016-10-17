package com.foryou.truck.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.foryou.truck.MyApplication;
import com.foryou.truck.util.UtilsLog;

import java.util.HashMap;
import java.util.Map;


public class Apn {

	public static final String TAG="Apn";
	public static final int TYPE_UNKNOWN = 0x000;
	public static final int TYPE_NET = 0x001;
	public static final int TYPE_3G_NET = 0x002;
	public static final int TYPE_WAP = 0x004;
	public static final int TYPE_3G_WAP = 0x003;
	public static final int TYPE_WIFI = 0x005;

	public static final String APN_UNKNOWN = "N/A";
	public static final String APN_NET = "Net";
	public static final String APN_3G_NET = "3GNet";
	public static final String APN_WAP = "Wap";
	public static final String APN_3G_WAP = "3GWap";
	public static final String APN_WIFI = "Wifi";

	/** 连网类型 */
	public static int M_APN_TYPE = TYPE_WIFI;
	/** 代理地址 */
	public static String M_APN_PROXY = null;
	/** 代理端口 */
	public static int M_APN_PORT = 80;
	/** 代理方式 */
	public static byte M_PROXY_TYPE = 0;
	/** 是否代理 */
	public static boolean M_USE_PROXY = false;

	public static final byte PROXY_TYPE_CM = 0;
	public static final byte PROXY_TYPE_CT = 1;

	/** 电信代理地址 */
	private static final String PROXY_CTWAP = "10.0.0.200";

	// APN 名称
	public static String APN_CMWAP = "cmwap";
	public static String APN_CMNET = "cmnet";
	public static String APN_3GWAP = "3gwap";
	public static String APN_3GNET = "3gnet";
	public static String APN_UNIWAP = "uniwap";
	public static String APN_UNINET = "uninet";
	public static String APN_CTWAP = "ctwap";
	public static String APN_CTNET = "ctnet";

	/**
	 * 渠道号
	 */
	public static String APP_COMPANY = "";// 渠道名称
	/**
	 * 手机串号
	 */
	public static String imei="";
	/**
	 *手机号码 
	 */
	public static String phoneNumber="";
	/**
	 * 版本号
	 */
	public static String version="";
	/**
	 * 手机系统版本
	 */
	public static final String osVersion = android.os.Build.VERSION.RELEASE;
	/**
	 * 手机型号
	 */
	public static final String model = android.os.Build.MODEL;
	/**
	 * 联网方式
	 */
	public static String conn_mode = "";
	/**
	 * 有无sim卡
	 */
	public static String iscard = "";
	/**
	 * 是否定位成功
	 */
	public static int ispos = 0;
	/**
	 * 压缩方式
	 */
	public static final String HTTP_PRESSED_TYPE= "";
	public static Map<String,String> getHeads() {
		Map<String,String> map=new HashMap<String, String>();
		map.put("Accept-Encoding", HTTP_PRESSED_TYPE);//压缩方式
		map.put("version", version);//版本号
		map.put("connmode", conn_mode);//联网方式
		map.put("imei", imei);//手机串号
		map.put("model", model);//手机型号
		map.put("osVersion", osVersion);//手机系统版本
		map.put("phoneNumber", phoneNumber);//手机号码
		map.put("posmode", "gps,wifi");//定位方式
		map.put("ispos", ""+ispos);//是否定位成功
		map.put("iscard", iscard);//有无sim卡
		map.put("company",APP_COMPANY);//渠道号
		map.put("type","1"); //1:android 2:ios
		return map;
	}
	
	public static void init() {
		Context context = MyApplication.getSelf();
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			if(tm.getSimState() == TelephonyManager.SIM_STATE_READY){
				iscard="1";
			}else{
				iscard="0";
			}
			imei = tm.getDeviceId();
			phoneNumber = tm.getLine1Number();
			version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			UtilsLog.i(TAG,"version==========="+version);
			int type = -1;
			M_APN_TYPE = TYPE_UNKNOWN;
			String extraInfo = null;
			if (networkInfo != null) {
				type = networkInfo.getType();
				extraInfo = networkInfo.getExtraInfo();
				if (extraInfo == null)
					M_APN_TYPE = TYPE_UNKNOWN;
				else
					extraInfo = extraInfo.trim().toLowerCase();
			}

			if (type == ConnectivityManager.TYPE_WIFI) {
				M_APN_TYPE = TYPE_WIFI;
				M_USE_PROXY = false;
			} else {
				if (extraInfo == null) {
					M_APN_TYPE = TYPE_UNKNOWN;
				} else if (extraInfo.contains(APN_CMWAP) || extraInfo.contains(APN_UNIWAP)
						|| extraInfo.contains(APN_CTWAP)) {
					M_APN_TYPE = TYPE_WAP;
				} else if (extraInfo.contains(APN_3GWAP)) {
					M_APN_TYPE = TYPE_3G_WAP;
				} else if (extraInfo.contains(APN_CMNET) || extraInfo.contains(APN_UNINET)
						|| extraInfo.contains(APN_CTNET)) {
					M_APN_TYPE = TYPE_NET;
				} else if (extraInfo.contains(APN_3GNET)) {
					M_APN_TYPE = TYPE_3G_NET;
				} else {
					M_APN_TYPE = TYPE_UNKNOWN;
				}

				M_USE_PROXY = false;
				if (isProxyMode(M_APN_TYPE)) {
					M_APN_PROXY = android.net.Proxy.getDefaultHost();
					M_APN_PORT = android.net.Proxy.getDefaultPort();

					if (M_APN_PROXY != null)
						M_APN_PROXY = M_APN_PROXY.trim();

					if (M_APN_PROXY != null && !"".equals(M_APN_PROXY)) {
						M_USE_PROXY = true;
						M_APN_TYPE = TYPE_WAP;

						if (PROXY_CTWAP.equals(M_APN_PROXY)) {
							M_PROXY_TYPE = PROXY_TYPE_CT;
						} else {
							M_PROXY_TYPE = PROXY_TYPE_CM;
						}
					} else {
						M_USE_PROXY = false;
						M_APN_TYPE = TYPE_NET;
					}
				}
			}
			conn_mode=getApnName(M_APN_TYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean isProxyMode(int apnType) {
		return apnType == TYPE_WAP || apnType == TYPE_UNKNOWN;
	}

	public static String getApnName(int apnType) {
		switch (apnType) {
		case TYPE_WAP:
			return APN_WAP;
		case TYPE_3G_WAP:
			return APN_3G_WAP;
		case TYPE_NET:
			return APN_NET;
		case TYPE_3G_NET:
			return APN_3G_NET;
		case TYPE_WIFI:
			return APN_WIFI;
		case TYPE_UNKNOWN:
			return APN_UNKNOWN;
		default:
			return APN_UNKNOWN;
		}
	}
}
