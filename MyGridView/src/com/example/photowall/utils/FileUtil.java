package com.example.photowall.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class FileUtil {
	private static String mRootPath=Environment.getExternalStorageDirectory().getPath();
	private static String mCachePath=null;
	private static String FOLDER_NAME="/PhotoWall";
	
	
	public FileUtil(Context context) {
		mCachePath=context.getCacheDir().getPath();
	}
	private String getStoragePath(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
				?mRootPath+FOLDER_NAME:mCachePath+FOLDER_NAME;
	}
	public void saveImage(String fileName,Bitmap bitmap) throws Exception{
		if(bitmap==null){
			return;
		}
		
		String path=getStoragePath();
		System.out.println(path+File.separator+fileName);
		File folder=new File(path);
		if(!folder.exists()){
			folder.mkdir();
		}
		File file=new File(path+File.separator+fileName);
		file.createNewFile();
		FileOutputStream out=new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, out);
		out.flush();
		out.close();
	}
	public boolean findFile(String fileName){
		return new File(getStoragePath()+File.separator+fileName).exists();
	}
	public long getSize(String fileName){
		if(!findFile(fileName)){
			return 0;
		}else{
			return new File(getStoragePath()+File.separator+fileName).length();
		}
	}
	public Bitmap getBitmap(String fileName){
		return BitmapFactory.decodeFile(getStoragePath()+File.separator+fileName);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
