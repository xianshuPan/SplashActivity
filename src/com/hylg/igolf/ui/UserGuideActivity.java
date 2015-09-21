package com.hylg.igolf.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.hylg.igolf.R;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class UserGuideActivity extends Activity
								implements ViewPager.OnPageChangeListener,
									View.OnTouchListener {
	private LinearLayout mImageIndex;
	private ViewPager mViewPager;
	private int mIndex = 0, mMax;
	private boolean mMove = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_guide);
		getViews();
	}
	
	private void getViews() {
        mImageIndex = (LinearLayout) findViewById(R.id.user_guide_index_container);
        mImageIndex.removeAllViews();
		mViewPager = (ViewPager) findViewById(R.id.user_guide_view_pager);
		ArrayList<View> list = new ArrayList<View>();
		TypedArray ta = getResources().obtainTypedArray(R.array.user_guide_array);
		mMax = ta.length();
    	ImageView img = null;
        for(int i=0; i<mMax; i++) {
        	// add for view pager
        	img = new ImageView(this);
        	img.setImageDrawable(ta.getDrawable(i));
        	img.setId(ta.getResourceId(i, 0));
        	img.setScaleType(ScaleType.FIT_XY);
        	list.add(img);
        	// add for index container
        	ImageView index = new ImageView(this);
        	index.setLayoutParams(new LayoutParams(30, 30));
        	index.setPadding(8, 8, 8, 8);
        	index.setImageResource(R.drawable.shelf_circle_selector);
        	index.setSelected(i ==0 ? true : false);
        	mImageIndex.addView(index);
        }
        if(mMax > 0) {
	        // finish activity while click on the last page
	        img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finishWithAnim();
				}
	        });
        }
        ta.recycle();
        mViewPager.setAdapter(new ViewPagerAdapter(list));
        mViewPager.setOnTouchListener(this);
        mViewPager.setOnPageChangeListener(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public void onPageScrollStateChanged(int status) {
		if(mMove && status == ViewPager.SCROLL_STATE_IDLE && mIndex+1 >= mMax) {
			finishWithAnim();
		} else {
			mMove = false;
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int index) {
		mIndex = index;
		int cnt = mImageIndex.getChildCount();
		for(int i=0; i<cnt; i++) {
			mImageIndex.getChildAt(i).setSelected(i == index ? true : false);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if(!mMove) mMove = true;
				break;
		}
		return false;
	}
	
	private void finishWithAnim() {
		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}
	
	class ViewPagerAdapter extends PagerAdapter {
		private ArrayList<View> mList;

		public ViewPagerAdapter(ArrayList<View> views) {
			mList = views;
		}

		@Override
		public int getCount() {
			return mList.size();
		}
		
		@Override
		public Object instantiateItem(View container, int position) {
			View view = mList.get(position);
			((ViewPager)container).addView(view, 0);
			return view;
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}
		
		@Override
		public Parcelable saveState() {
			return super.saveState();
		}
		
		@Override
		public void startUpdate(View container) {
		}
		
		@Override
		public void finishUpdate(View container) {
		}
		
		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			super.restoreState(state, loader);
		}
		
		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager)container).removeView(mList.get(position));
		}
	}
}
