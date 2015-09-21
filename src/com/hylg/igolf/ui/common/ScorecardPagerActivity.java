package com.hylg.igolf.ui.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.hylg.igolf.R;
import com.hylg.igolf.ui.view.ViewPagerFixed;
import com.hylg.igolf.utils.Utils;

public class ScorecardPagerActivity extends FragmentActivity implements OnClickListener {
	private static final String TAG = "ScorecardPagerActivity";

	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	public static final String MEMBER_SN = "member_sn";
	public static final String APP_SN = "app_sn";
	public static final String INVOKE_TYPE = "invoke_type";
	// 暂时未用到，保留
	public static final int TYPE_MEMEBER_VIEW = 1;
	public static final int TYPE_MY_HISTORY_VIEW = 2;

	private ViewPagerFixed mPager;
	private int pagerPosition;
	private String sn, appSn;
	private int type;
	private String[] urls;
	
	private ImagePagerAdapter mAdapter;

	public static void startScorecardPagerActivity(Context context, int position, String[] urls, int type, String sn, String appSn) {
		Intent intent = new Intent(context, ScorecardPagerActivity.class);
		intent.putExtra(EXTRA_IMAGE_URLS, urls);
		intent.putExtra(EXTRA_IMAGE_INDEX, position);
		intent.putExtra(INVOKE_TYPE, type);
		intent.putExtra(MEMBER_SN, sn);
		intent.putExtra(APP_SN, appSn);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_ac_scorecard_view_detail);

		mPager = (ViewPagerFixed) findViewById(R.id.score_card_pager);
		findViewById(R.id.customer_scorecard_detail_topbar_back).setOnClickListener(this);

		Utils.logh(TAG, "onCreate ----- ");
		setData(getIntent());

	}

	private void setData(Intent intent) {
		pagerPosition = intent.getIntExtra(EXTRA_IMAGE_INDEX, 0);
		urls = intent.getStringArrayExtra(EXTRA_IMAGE_URLS);
		type = intent.getIntExtra(INVOKE_TYPE, TYPE_MEMEBER_VIEW);
		sn = intent.getStringExtra(MEMBER_SN);
		appSn = intent.getStringExtra(APP_SN);
		Utils.logh(TAG, "pagerPosition: " + pagerPosition + " type: " + type + " sn: " + sn + " appSn: " + appSn +
				" \n urls: " + urls[0]);
		
		mAdapter = new ImagePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);

		mPager.setCurrentItem(pagerPosition);
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public ImagePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
        public int getItemPosition(Object object) {
        	return PagerAdapter.POSITION_NONE;
        }
		
		@Override
		public int getCount() {
			return urls.length;
		}

		@Override
		public Fragment getItem(int position) {
			String url = urls[position];
			return ScorcardDetailFragment.newInstance(url, appSn, sn);
		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			finishWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void finishWithAnim() {
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.customer_scorecard_detail_topbar_back:
				finishWithAnim();
				break;
		}
	}
	
//	public  Bitmap loadBitmapFromView(View v) {  
//        if (v == null) {  
//            return null;  
//        }  
//        Bitmap screenshot;  
//        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(),Config.ARGB_8888);  
//        Canvas c = new Canvas(screenshot);  
//        c.translate(-v.getScrollX(), -v.getScrollY());  
//        v.draw(c);  
//        return screenshot;  
//    } 

}