package com.foryou.truck.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.foryou.truck.tools.ImageTools;
import com.foryou.truck.util.UtilsLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @des:首页Banner图的adapter
 */

public class ImgAdapter extends BaseAdapter {
    private Context _context;
    private List<String> imgList;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    public ImgAdapter(Context context, List<String> imgList) {
        _context = context;
        this.imgList = imgList;
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 2;
        options = new DisplayImageOptions.Builder()
//                .showStubImage(R.drawable.default_banner) // 在ImageView加载过程中显示图片
//                .showImageForEmptyUri(R.drawable.default_banner)
//                .showImageOnFail(R.drawable.default_banner)
                .bitmapConfig(Bitmap.Config.RGB_565)
                // .decodingOptions(option)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true) // 加载图片时会在内存中加载缓存
                .cacheOnDisc(true) // 加载图片时会在磁盘中加载缓存
                .displayer(new RoundedBitmapDisplayer(0))
                .build();
    }

    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            ImageView imageView = new ImageView(_context);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ScaleType.FIT_XY);
            imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            convertView = imageView;
            viewHolder.imageView = (ImageView) convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // viewHolder.imageView.setImageResource(imgList.get(position % imgList.size()));
        if (imgList == null || imgList.size() == 0) {
            imageLoader.displayImage("", viewHolder.imageView, options, animateFirstListener);
        } else {
            //imageLoader.displayImage(imgList.get(position % imgList.size()), viewHolder.imageView, options, animateFirstListener);
            // mbeans.get(position).getPreviewList().get(0).getPath()
            imageLoader.displayImage(
                    imgList.get(position % imgList.size()),
                    viewHolder.imageView, options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            //ImageView imageView = (ImageView) view;
                           // imageView.setImageBitmap(loadedImage);
                            try{
                                if(loadedImage!=null){
                                    ImageView imageView = (ImageView) view;
                                    imageView.setScaleType(ScaleType.FIT_XY);
                                    imageView.setBackgroundDrawable(ImageTools.bitmapToDrawable(loadedImage));
                                }
                            }catch (Exception e){
                                UtilsLog.i("ImgAdapter","首页轮播图加载图片时，出现异常");
                            }

                        }
                    });
        }


        return convertView;

    }

    private static class ViewHolder {
        ImageView imageView;
    }

    /**
     * 图片加载监听事件
     **/
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500); // 设置image隐藏动画500ms
                    displayedImages.add(imageUri);

                }
            }
        }
    }
}
