package com.hylg.igolf.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.hylg.igolf.R;
import com.hylg.igolf.ui.view.ProgressWheel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class DownLoadImageTool {
	
	private final static String 				TAG = "DownLoadImageTool";
	
	private static DownLoadImageTool 			mDownLoadImageTool = null;
	
	private ImageLoader 						mImageLoader = null;
	
	private DisplayImageOptions 				mDisplayImageOptions = null;
	
	private AnimateFirstDisplayListener 		animateFirstListener;
	
	
	private ProgressWheel                       mProgress;

	/*���Context �Ĺ��캯��*/
	public DownLoadImageTool(Context context)
	{
		//��ʼ������ŭurl����ͼƬ�ĵĶ���
		mDisplayImageOptions = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.avatar_loading)
		.showStubImage(R.drawable.avatar_loading)
		.showImageOnFail(R.drawable.avatar_loading)
		.cacheInMemory()
		.cacheOnDisc()
		//.decodingOptions(new Options().)
		//.displayer(new FadeInBitmapDisplayer(2000))
		.build();
		/*ͼƬ������*/
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
		animateFirstListener = new AnimateFirstDisplayListener();
		
	}
	
	/*��ȡ��̬����*/
	public static DownLoadImageTool getInstance(Context context)
	{
		if (mDownLoadImageTool == null) {
			mDownLoadImageTool = new DownLoadImageTool(context);
		}
		return mDownLoadImageTool;
	}
	
	/*����ͼƬ*/
	public void displayImage(String uri, ImageView imageView,ProgressWheel progress)
	{
		//mImageLoader.displayImage(uri, imageView, mDisplayImageOptions, animateFirstListener);
		
		mProgress = progress;
		
		mImageLoader.displayImage(uri, imageView, mDisplayImageOptions, animateFirstListener, new ImageLoadingProgressListener() {
			
			@Override
			public void onProgressUpdate(String arg0, View arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
				if (mProgress == null) {
					
					return;
				}
				
				mProgress.setVisibility(View.VISIBLE);
				
				if (!mProgress.isSpinning()) {
					
					mProgress.spin();
				}
				
				mProgress.setText(String.valueOf(arg2*100/arg3+"%"));
				
				if (arg2 >= arg3) {
					
					mProgress.setVisibility(View.GONE);
					mProgress.stopSpinning();
				}
			}
		});
	
	}
	
	/*ͼƬ��̬���ؼ�����*/
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener{

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
