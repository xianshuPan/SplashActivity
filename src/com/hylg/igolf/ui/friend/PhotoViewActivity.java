package com.hylg.igolf.ui.friend;

import java.util.ArrayList;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.ui.view.ProgressWheel;
import com.hylg.igolf.ui.view.ViewPagerFixed;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.xc.lib.view.PhotoView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PhotoViewActivity extends FragmentActivity implements 
															OnClickListener {
	
	public final String 					TAG 						= "PhotoViewActivity";
	
											/*滑动查看图片的view pager*/
	private ViewPagerFixed					mViewPager                  = null;
	private photoViewPagerAdaper           	mAdapter                    = null;
	
											/*上一张和下一张按钮*/
	private ImageView                      	mPreImage                   = null,
											mNextImage                  = null;
	
											/*显示滑动到第几张*/
	private TextView                       mCurrentText                 = null;
	
											/*上传图片按钮*/
	private Button							mUploadBtn                   = null;
	
											/*存储当前目录先的图片*/
	private ArrayList<View>                 mPhotoViewList              = null;
	
	private ArrayList<String>               mPhotoPathList              = null;
	
	private int                             mIndex                      = -1,
											mSelectPhotosInt            = 0;
	
	private RelativeLayout                  mHeadRelative               = null;
	
	private ImageButton                     mBack                       = null;
	private ImageView                       mDelete                     = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.photo_view);
		initUI();
	}
	
	@Override
	public void onRestart() {
		//DebugTools.getDebug().debug_v(TAG, "onRestart..");
		super.onRestart();
	}
	
	@Override
	public void onStart() {
		//DebugTools.getDebug().debug_v(TAG, "onStart..");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		//DebugTools.getDebug().debug_v(TAG, "onResume..");
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		//DebugTools.getDebug().debug_v(TAG, "onPause..");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//DebugTools.getDebug().debug_v(TAG, "onDestroy..");
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		//DebugTools.getDebug().debug_v(TAG, "onLowMemory..");
	}
	
//	@Override 
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		super.onKeyDown(keyCode, event);
//		if((keyCode == KeyEvent.KEYCODE_BACK) && 
//		   (event.getAction() == KeyEvent.ACTION_DOWN)) {  
//			
//			startActivity(new Intent(this, CameraActivity.class));
//			this.finish();
//		} 
//		return false;
//	}
	
	
	/*初始化控件，设置事件监听器*/
	protected void initUI() {
		
		mHeadRelative      = (RelativeLayout) findViewById(R.id.photo_view_head_realtive);
		
		mBack              = (ImageButton) findViewById(R.id.photo_view_head_back);
		
		mDelete             = (ImageView) findViewById(R.id.photo_view_delete_image);
		
		mViewPager          = (ViewPagerFixed) findViewById(R.id.photo_view_pager);
		
		mPreImage           = (ImageView) findViewById(R.id.photo_view_pre_image);
		mNextImage          = (ImageView) findViewById(R.id.photo_view_next_image);
		
		mCurrentText        = (TextView) findViewById(R.id.photo_view_current_text);
		
		mUploadBtn          = (Button) findViewById(R.id.photo_view_upload_btn);
		mPhotoViewList      = new ArrayList<View>();
		mPhotoPathList      = new ArrayList<String>();
		
		/*获取传递过来的数据*/
		Intent data = getIntent();
		if (data != null && data.getStringArrayListExtra("Photos") != null) {
			
			mHeadRelative.setVisibility(View.GONE);
			mPhotoPathList = data.getStringArrayListExtra("Photos");
			mIndex = data.getIntExtra("Index", -1);
			
			mCurrentText.setText((mIndex+1)+"/"+mPhotoPathList.size());
			
			DebugTools.getDebug().debug_v(TAG, "mIndex----->>>>"+mIndex);
			
			getPhotoViewByPath();
		} else if (data.getIntExtra("SelectPhotos", 0) > 0) {
			
			mHeadRelative.setVisibility(View.VISIBLE);
			mPhotoPathList = Config.drr;
			mIndex = data.getIntExtra("Index", -1);
			mSelectPhotosInt = data.getIntExtra("SelectPhotos", 0);
			mCurrentText.setText((mIndex+1)+"/"+mPhotoPathList.size());
			
			getPhotoViewByPath();
		} else {
			
			this.finish();
		}
		
		mAdapter            = new photoViewPagerAdaper(mPhotoViewList);
		
		mBack.setOnClickListener(this);
		mDelete.setOnClickListener(this);
		mDelete.setVisibility(View.GONE);
		mPreImage.setOnClickListener(this);
		mNextImage.setOnClickListener(this);
		mUploadBtn.setOnClickListener(this);
		mViewPager.setAdapter(mAdapter);
		
		if (mIndex >= 0) {
			
			mViewPager.setCurrentItem(mIndex);
		}
		
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				
				mIndex = arg0;
				mCurrentText.setText((arg0+1)+"/"+mPhotoPathList.size());
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		if (arg0.getId() == mUploadBtn.getId()) {
			
			if (mPhotoPathList != null && mPhotoPathList.size() > 0) {
				
				/*应该开启service 来上传*/
				//for (int i = 0; i < mPhotoPathList.size(); i++) {
					
					//new UploadPhotoThread(mPhotoPathList.get(i)).start();
				
//				Intent data = new Intent(this, HdService.class);
//				data.putExtra("Type", 0);
//				data.putStringArrayListExtra("Paths", mPhotoPathList);
//				startService(data);
					
				//}
			}
			
		} else if (arg0.getId() == mPreImage.getId()) {
			
			mIndex = mIndex-1;
			
			if (mIndex < 0) {
				
				this.finish();
				//mIndex = mPhotoPathList.size() -1;
			}
			
			mViewPager.setCurrentItem(mIndex);
			
		} else if (arg0.getId() == mNextImage.getId()) {
			
			mIndex = mIndex+1;
			
			if (mIndex > mPhotoPathList.size()) {
				
				
				mIndex = 0;
			}
			
			mViewPager.setCurrentItem(mIndex);
			
		} else if (arg0.getId() == mBack.getId()) {
			
			this.finish();
			
		} else if (arg0.getId() == mDelete.getId()) {
			
			
			if (Config.drr != null && (mIndex) < Config.drr.size() && (mIndex) >= 0) {
				
				
				Config.drr.remove(mIndex);
				
				//mIndex = mIndex -1;
				
			}
			
			if (mPhotoViewList != null && (mIndex) < mPhotoViewList.size() && (mIndex) >= 0) {
				
				mPhotoViewList.remove(mIndex);
				
				
				
				getPhotoViewByPath();
				
				mAdapter = new photoViewPagerAdaper(mPhotoViewList);
				
				mViewPager.setAdapter(mAdapter);
				
				//mAdapter.notifyDataSetChanged();
				
				//mIndex =  mIndex-1;
				
				mCurrentText.setText((mIndex)+"/"+mPhotoPathList.size());
				//mViewPager.removeViewAt(mIndex-1);
				//mViewPager.invalidate();
				
			}
			
			if (mPhotoViewList.size() <= 0) {
				
				this.finish();
			}
			
		}
	}
	
	
	/*view pager 数据适配器*/
	class photoViewPagerAdaper extends PagerAdapter {
		
		//private ArrayList<View> mPhotoViewList = null;
		
		photoViewPagerAdaper (ArrayList<View> list) {
			
			//mPhotoViewList = list;
		}
		

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mPhotoViewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		
		
		@Override
		public Object instantiateItem (ViewGroup container, int position) {
			
			
			container.addView(mPhotoViewList.get(position));
			
			return mPhotoViewList.get(position);
		}
		
		@Override 
		public void destroyItem (ViewGroup container, int position, Object object) {
			
			
			if (position < mPhotoViewList.size()) {
				
				container.removeView(mPhotoViewList.get(position));
			}
			
		}
		
		@Override
		public int getItemPosition(Object object) {
		return POSITION_NONE;
		}
		
	}
	
	/*根据路径，找到下面的图片*/
	private void getPhotoViewByPath() {
		
		mPhotoViewList.clear();
		
		LayoutParams imageLayoutParames = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		imageLayoutParames.setMargins(5, 0, 5, 0);
		imageLayoutParames.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		
		for (int i = 0; i<  mPhotoPathList.size(); i++) {
					
			//RelativeLayout imageLiner = new RelativeLayout(this);
			
			
			com.hylg.igolf.ui.view.photoview.PhotoView image = null;
			ProgressWheel progress = null;
			
			View view = this.getLayoutInflater().inflate(R.layout.image_loading, null);
			
			image = (com.hylg.igolf.ui.view.photoview.PhotoView)view.findViewById(R.id.image_loading_photo);
			progress = (ProgressWheel) view.findViewById(R.id.image_loading_progress);

			mPhotoViewList.add(view);
			
			image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					PhotoViewActivity.this.finish();
				}
			});
			
			String path = mPhotoPathList.get(i);
			if (path != null && path.contains(BaseRequest.SERVER_IP)) {
				
				DownLoadImageTool.getInstance(this).displayImage(path, image,progress);
				
			} else {
				
				DownLoadImageTool.getInstance(this).displayImage("file:///"+path, image,progress);
			}
			
					
		}
	
	}
	

}
