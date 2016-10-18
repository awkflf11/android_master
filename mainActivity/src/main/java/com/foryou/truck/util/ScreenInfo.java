package com.foryou.truck.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 屏幕相关信息
 * @author androidloveme
 *
 */
public class ScreenInfo {
	/**
	 * 获取DisplayMetrics对象，已获取屏幕宽、高、密度等信息
	 * @param activity
	 * @return
	 */
	public static DisplayMetrics getScreenInfo(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
	    activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
	    return dm;
	}

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
	
}
