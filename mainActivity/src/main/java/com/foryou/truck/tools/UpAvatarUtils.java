package com.foryou.truck.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;

/**
 * @des: 上传头像的相关工具
 */
public class UpAvatarUtils {
    private static String TAG = "UpAvatarUtils";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void ShowPickDialog(final Context mContext) {
        AlertDialog.Builder build;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            build = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT);
        } else {
            build = new AlertDialog.Builder(mContext);
        }
        build.setMessage("设置头像")
                .setPositiveButton("相册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * 要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                         */
           //			Intent intent = new Intent(Intent.ACTION_PICK, null);
          //		    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        ((Activity) mContext).startActivityForResult(intent, 1);
                    }
                })
                .setNegativeButton("拍照", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 下面这句指定调用相机拍照后的照片存储的路径
                        //new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "avatar.png"))
                        File file = new File(SdCardUtil.getTouXiangPath(mContext) + "avatar.png");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        ((Activity) mContext).startActivityForResult(intent, 2);
                    }
                }).create().show();
    }


    /**
     * @裁剪图片方法
     */
    public static void startPhotoZoom(Context mContext, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", true);
        ((Activity) mContext).startActivityForResult(intent, 3);
    }


}
