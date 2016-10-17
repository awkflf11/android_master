package com.foryou.truck.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.ImageLoader;
// //
public class ImagePagerAdapter extends RecyclingPagerAdapter {

	private Context context;
	private List<String> imageIdList;

	private int size;
	private boolean isInfiniteLoop;
	private ImageLoader imageLoader;

	public ImagePagerAdapter(Context context, List<String> imageIdList,
			ImageLoader imageloader) {
		this.context = context;
		this.imageIdList = imageIdList;
		this.size = imageIdList.size();
		isInfiniteLoop = false;
		imageLoader = imageloader;
	}

	@Override
	public int getCount() {
		// Infinite loop
		return isInfiniteLoop ? Integer.MAX_VALUE : imageIdList.size();
	}

	/**
	 * get really position
	 * 
	 * @param position
	 * @return
	 */
	private int getPosition(int position) {
		return isInfiniteLoop ? position % size : position;
	}

	@Override
	public View getView(int position, View view, ViewGroup container) {
		ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			holder.imageView = new ImageView(context);
			holder.imageView.setScaleType(ScaleType.CENTER_CROP);
			view = holder.imageView;
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		// holder.imageView.setImageResource(imageIdList.get(getPosition(position)));
		imageLoader.displayImage(imageIdList.get(getPosition(position)),
				holder.imageView);
		return view;
	}

	private static class ViewHolder {

		ImageView imageView;
	}

	/**
	 * @return the isInfiniteLoop
	 */
	public boolean isInfiniteLoop() {
		return isInfiniteLoop;
	}

	/**
	 * @param isInfiniteLoop
	 *            the isInfiniteLoop to set
	 */
	public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
		return this;
	}
}
