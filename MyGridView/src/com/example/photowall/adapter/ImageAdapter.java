package com.example.photowall.adapter;

import com.example.photowall.R;
import com.example.photowall.utils.DownloadImage;
import com.example.photowall.utils.DownloadImage.OnImageLoaderListener;
import com.example.photowall.utils.Images;





import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter implements OnScrollListener{
	private GridView mGridView;
	private boolean isFirst=true;
	private Context context;
	private String[] imagesStrings=Images.imageThumbUrls;
	private DownloadImage mDownloadImage;
	 /** 
     * 一屏中第一个 
     */  
	private int mFirstVisibleItem;  
    
    /** 
     * 一屏中所有item的个数 
     */  
    private int mVisibleItemCount;  
	public ImageAdapter(GridView mGridView, Context context) {
		this.mGridView = mGridView;
		this.context = context;
		mDownloadImage=new DownloadImage(context);
		initView();
	}

	private void initView() {
		mGridView.setOnScrollListener(this);
		
	}

	@Override
	public int getCount() {
		return imagesStrings.length;
	}

	@Override
	public Object getItem(int position) {
		return imagesStrings[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView mImageView;  
        final String mImageUrl = imagesStrings[position];  
        if(convertView == null){  
            mImageView = new ImageView(context);  
        }else{  
            mImageView = (ImageView) convertView;  
        }         
        mImageView.setLayoutParams(new GridView.LayoutParams(150, 150));  
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);  
        mImageView.setTag(mImageUrl);  
		Bitmap bitmap=mDownloadImage.getFromCache(mImageUrl.replaceAll("[^\\w]", ""));
		mImageView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.home_bg2));
		if(bitmap!=null){
			mImageView.setImageBitmap(bitmap);
		}else{
			mImageView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.home_bg2));
		}
		System.out.println("grt");
		return mImageView;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {  
            showImage(mFirstVisibleItem, mVisibleItemCount);  
        } else {  
        	mDownloadImage.cancleTask();  
        }  
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem=firstVisibleItem;
		mVisibleItemCount=visibleItemCount;
		if(isFirst && visibleItemCount>0){
			showImage(mFirstVisibleItem,mVisibleItemCount);
			isFirst=false;
		}
		
	}
	 private void showImage(int firstVisibleItem, int visibleItemCount){  
	     System.out.println("scroll");   
		 Bitmap bitmap = null;  
	        for(int i=firstVisibleItem; i<firstVisibleItem + visibleItemCount; i++){  
	            String mImageUrl = imagesStrings[i];  
	            final ImageView mImageView = (ImageView) mGridView.findViewWithTag(mImageUrl);  
	            bitmap = mDownloadImage.downloadImage(mImageUrl, new OnImageLoaderListener() {         
	                @Override  
	                public void onImageLoader(Bitmap bitmap, String url) {  
	                    if(mImageView != null && bitmap != null){  
	                        mImageView.setImageBitmap(bitmap);  
	                    }    
	                }  
	            }); 
	            if(bitmap != null){
					mImageView.setImageBitmap(bitmap);
				}else{
					mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.home_bg2));
				}
	        }  
	    }  
	
	

}





























