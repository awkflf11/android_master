package com.foryou.truck.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.foryou.truck.R;
import com.foryou.truck.util.Constant;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

/**
 * @des:分享工具类：微信好友，微信朋友圈，微博分享
 */
public class SharePlatform {

    /**
     * @des:
     * @param:i:SendMessageToWX.Req.WXSceneTimeline.分享到微信朋友圈;
     * @SendMessageToWX.Req.WXSceneSession; 分享到微信朋友圈
     */
    public static void shareWebPage(Context context, IWXAPI api, int i) {
        Log.i("ForthTabFragment", "shareWebPage");
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = Constant.SHARE_URL;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "福佑相伴,省钱放心";
        msg.description = "福佑相伴,省钱放心";
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.share_wx);
        msg.thumbData = Constant.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = i;
        api.sendReq(req);
    }

    public static void shareWebPage(Context context, IWXAPI api, String url, String title, String content, int i) {
        Log.i("ForthTabFragment", "shareWebPage");
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = content;
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.share_wx);
        msg.thumbData = Constant.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = i;
        api.sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    // public static boolean isNumeric(String str) {
    // for (int i = str.length(); --i >= 0;) {
    // if (!Character.isDigit(str.charAt(i))) {
    // return false;
    // }
    // }
    // return true;
    // }

}
