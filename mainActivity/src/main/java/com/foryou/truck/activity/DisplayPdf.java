package com.foryou.truck.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dubin on 16/6/15.
 */
public class DisplayPdf{

    private String TAG = "DisplayPdf";

    public static Intent getPdfIntent(File file){
        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = Uri.fromFile(file);

        intent.setDataAndType(uri, "application/pdf");

        return intent;
    }

    public static File downLoadPdfFile(String order_sn,String urlString){
        String[] name = urlString.split("/");
        String path = Environment.getExternalStorageDirectory()
                + "/downloads/";
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }
        path += order_sn+".pdf";
        File file = new File(path);
        if(file.exists()){
            return file;
        }

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)
                    url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            //实现连接
            connection.connect();

            System.out.println("connection.getResponseCode()="+connection.getResponseCode());
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                //以下为下载操作
                byte[] arr = new byte[1];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(baos);
                int n = is.read(arr);
                while (n > 0) {
                    bos.write(arr);
                    n = is.read(arr);
                }
                bos.close();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.close();
                //关闭网络连接
                connection.disconnect();
            }
        } catch (IOException e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
        return file;
    }
}
