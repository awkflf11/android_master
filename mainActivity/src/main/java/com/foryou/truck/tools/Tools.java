package com.foryou.truck.tools;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.foryou.truck.BaseActivity;
import com.foryou.truck.entity.OrderDetailEntity.LocationNew;
import com.foryou.truck.util.Constant;

import java.util.List;

public class Tools {

    /**
     * @des: 判断是否网络
     */
    public static boolean IsConnectToNetWork(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        return !(activeNetworkInfo == null || !activeNetworkInfo.isAvailable());
        // int networkType = activeNetworkInfo.getType();
        // if(networkType == ConnectivityManager.TYPE_WIFI){
        // Toast.makeText(getApplicationContext(), "当前成功连接WIFI"
        // +activeNetworkInfo.isConnected(), Toast.LENGTH_SHORT).show();
        // }else if(networkType==ConnectivityManager.TYPE_MOBILE){
        // if(activeNetworkInfo.isRoaming()){
        // Toast.makeText(getApplicationContext(), "当前成功连接漫游3G网络"
        // +activeNetworkInfo.isConnected(), Toast.LENGTH_SHORT).show();
        // }else{
        // Toast.makeText(getApplicationContext(), "当前成功连接非漫游3G网络"
        // +activeNetworkInfo.isConnected(), Toast.LENGTH_SHORT).show();
        // }
        // }
    }

    public static boolean isConnectToWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
            int networkType = activeNetworkInfo.getType();
            if (networkType == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String UnicodeDecode(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\\' && chars[i + 1] == 'u') {
                char cc = 0;
                for (int j = 0; j < 4; j++) {
                    char ch = Character.toLowerCase(chars[i + 2 + j]);
                    if ('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f') {
                        cc |= (Character.digit(ch, 16) << (3 - j) * 4);
                    } else {
                        cc = 0;
                        break;
                    }
                }
                if (cc > 0) {
                    i += 5;
                    sb.append(cc);
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String getStaticMapImageUrl(int latitude, int longitude, int width, int height) {
        float lat = ((float) latitude) / 1000000;
        float longit = ((float) longitude) / 1000000;
        String uri = "http://api.map.baidu.com/staticimage?center=" + longit + "," + lat + "&width=320&height=100&zoom=15&markers=" + longit + "," + lat
                + "&scale=2";
        return uri;
    }

    public static String getMutiMarkMapImageUrl(List<LocationNew> marklist, int width, int heidht) {
        String baseUri = "http://api.map.baidu.com/staticimage?center=" + marklist.get(marklist.size() - 1).lng + "," + marklist.get(marklist.size() - 1).lat
                + "&width=640&height=200&zoom=5&markers=";
        for (int i = 0; i < marklist.size(); i++) {
            if (i != 0) {
                baseUri += "|" + marklist.get(i).lng + "," + marklist.get(i).lat;
            } else {
                baseUri += marklist.get(i).lng + "," + marklist.get(i).lat;
            }
        }
        baseUri += "&bbox=109.2,36.3;117.2,40.5";
        return baseUri;
    }

    public static String getStaticMapImageUrl(double latitude, double longitude, int width, int height) {
        float lat = (float) latitude;
        float longit = (float) longitude;
        String uri = "http://api.map.baidu.com/staticimage?center=" + longit + "," + lat + "&width=640&height=200&zoom=15&scale=2&bbox=109.2,36.3;117.2,40.5";
        return uri;
    }

    public static String getMapUrlWitchLat(double latitude, double longitude) {
        return "http://api.map.baidu.com/geocoder/v2/?" + "ak=" + Constant.AK + "&callback=" + "renderReverse&location=" + latitude + "," + longitude
                + "&output=json&pois=1" + "&mcode=" + Constant.MCODE;
    }

    public static int getVersionCode(Context context) {
        int versionCode = 1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionCode;
    }

    /*隐藏软键盘*/
    public static void hideInput(BaseActivity context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        }
    }

    //金钱数字显示增加逗号
    public static void testforNumber(){
        for(int i = 1;i<20;i++){
            String test="";
            int index = 1;
            for(int j = 0;j<i;j++){
                test+=index%10;
                index++;
            }
            Log.i("tools", "test:" + test + ",result:" + Tools.formatNumber(test));
        }
    }

    public static String formatNumber(String number) {
        if (number == null || number.length() == 0) {
            return "";
        }

        Log.i("tools","number:"+number);
        //如果有小数点
        String nodeString = "";
        int indexofNode = number.indexOf(".");
        if(indexofNode != -1){
            nodeString = number.substring(indexofNode,number.length());
            number = number.substring(0,indexofNode);
        }

        if (number.length() > 3) {
            int insertcount = 0;
            if (number.length() % 3 == 0) {
                insertcount = number.length() / 3 - 1;
            } else {
                insertcount = number.length() / 3;
            }
            String result = "";
            for (int j = number.length() - 1; j >= 0; j--) {
                if (j == insertcount * 3 && insertcount!=0) {
                    result += number.charAt(number.length() - j-1) + ",";
                    insertcount--;
                } else {
                    result += number.charAt(number.length()-j-1);
                }
            }
            return result+nodeString;
        } else {
            return number+nodeString;
        }
    }
}
