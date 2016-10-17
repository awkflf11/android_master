package com.example.photowall.utils;

import java.net.HttpURLConnection;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

public class DownloadImage {
	private LruCache<String, Bitmap> mLruCache=null;
	private FileUtil mFileUtil;
	private ExecutorService mThreadPool=null;
	
	public DownloadImage(Context context) {
		Long totle=Runtime.getRuntime().maxMemory();
		int maxSize=(int) (totle/8);
		mLruCache=new LruCache<String, Bitmap>(maxSize){

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes()*value.getHeight();
			}
			
		};
		mFileUtil=new FileUtil(context);
	}
	public void addToCache(String key,Bitmap bitmap){
		if(mLruCache.get(key)==null&&bitmap!=null){
			mLruCache.put(key, bitmap);
		}
	}
	public Bitmap downloadImage(final String url,final OnImageLoaderListener listener){
		final String fileName=url.replaceAll("[^\\w]", "");
		Bitmap bitmap=getFromCache(fileName);
		if(bitmap!=null){
			return bitmap;
		}else{
			final Handler handle=new Handler(){
				@Override
				public void handleMessage(Message msg) {
					listener.onImageLoader((Bitmap)msg.obj, url);
					super.handleMessage(msg);
				}
				
			};		
			getThreadPool().execute(new Thread(){

				@Override
				public void run() {
					Bitmap bitmap=getFromURL(url);
					Message msg=new Message();
					msg.obj=bitmap;
					handle.sendMessage(msg);
					try {
						mFileUtil.saveImage(fileName, bitmap);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
					addToCache(fileName,bitmap);
				}
				
			});
			
			
			
		}
		return null;	
	}
	
	protected Bitmap getFromURL(String url) {
		try {
			System.out.println("url");
			URL urlConn;
			urlConn = new URL(url);
			HttpURLConnection conn=(HttpURLConnection) urlConn.openConnection();
			conn.setConnectTimeout(10*1000);
			conn.setReadTimeout(10*1000);
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			return BitmapFactory.decodeStream(conn.getInputStream());
		} catch (Exception e) {
			System.out.println("null");
			return null;
		}
	
	}
	public Bitmap getFromCache(String key){
		if(mLruCache.get(key)!=null){
			return mLruCache.get(key);
		}else if(mFileUtil.findFile(key)&&mFileUtil.getSize(key)!=0){
			Bitmap bitmap=mFileUtil.getBitmap(key);
			addToCache(key,bitmap);
			return bitmap;
		}
		return null;
	}
	

	
	private ExecutorService getThreadPool(){
		if(mThreadPool==null){
			synchronized (DownloadImage.this) {
				if(mThreadPool == null){
					mThreadPool = Executors.newFixedThreadPool(2);
				}
			}
		}
		return mThreadPool;
	}
	
	public interface OnImageLoaderListener{  
        void onImageLoader(Bitmap bitmap, String url);  
    }  
	
	public void cancleTask(){
		if(mThreadPool!=null){
			mThreadPool.shutdown();
			mThreadPool=null;
		}
	}
	
}
