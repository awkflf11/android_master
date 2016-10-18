package com.foryou.truck.tools;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.entity.OrderDetailEntity.LocationNew;
import com.foryou.truck.util.Constant;

import java.io.File;
import java.util.List;

public class SdCardUtil {


    /*@获取SD卡路径: mnt/sd/。如果sd卡不存在，保存在/data/data/com.my.app/files */
    public static String getSDCardPath(Context context) {
        String sdPath = "";
        if (isSDCardEnable()) {
            sdPath = Environment.getExternalStorageDirectory().getPath() + File.separator;
        } else {
            sdPath = context.getFilesDir() + File.separator;
        }
        return sdPath;
    }
        /* @ Des: 判断SDCard是否可用*/

    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    /*@保存头像的路径: mnt/sd/Android/data/。如果sd卡不存在，保存在/data/data/com.my.app/files */
    public static String getTouXiangPath(Context context) {
        String touXiangPath = "";
        if (isSDCardEnable()) {
            touXiangPath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator;
        } else {
            touXiangPath = context.getFilesDir() + File.separator;
        }
        return touXiangPath;
    }


}
