package com.hylg.igolf.ui.common;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.DeleteAlbum;
import com.hylg.igolf.cs.request.UpdateAvatar;
import com.hylg.igolf.ui.common.AlbumSelectActivity.OnAlbumSelectListener;
import com.hylg.igolf.ui.view.ViewPagerFixed;
import com.hylg.igolf.utils.*;

public class AlbumPagerActivity extends FragmentActivity implements OnClickListener, OnAlbumSelectListener {
	private static final String TAG = "AlbumPagerActivity";

	
//	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "image_index";
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	public static final String MEMBER_SN = "member_sn";
	
	private static OnAlbumSetAvatarListener mListener;

	private View moreView;
	
	private ViewPagerFixed mPager;
	private TextView indicator;
	private ArrayList<String> albumList;
	
	private AlbumPagerAdapter mAdapter;

	public static void startAlbumPagerCustomer(Fragment fragment, int position, String[] urls, String sn) {
		try {
			mListener = (OnAlbumSetAvatarListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements OnAlbumSetAvatarListener");
		}
		Intent intent = new Intent(fragment.getActivity(), AlbumPagerActivity.class);
		intent.putExtra(EXTRA_IMAGE_URLS, urls);
		intent.putExtra(EXTRA_IMAGE_INDEX, position);
		intent.putExtra(MEMBER_SN, sn);
		fragment.startActivity(intent);
		fragment.getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	public static void startAlbumPagerMember(Activity activity, int position, String[] urls, String sn) {
		mListener = null;
		Intent intent = new Intent(activity, AlbumPagerActivity.class);
		intent.putExtra(EXTRA_IMAGE_URLS, urls);
		intent.putExtra(EXTRA_IMAGE_INDEX, position);
		intent.putExtra(MEMBER_SN, sn);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
//		activity.startActivityForResult(intent, Const.REQUST_CODE_DELETE_ALBUM);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);

		moreView = findViewById(R.id.customer_album_detail_operation);
		findViewById(R.id.customer_album_detail_topbar_back).setOnClickListener(this);
		mPager = (ViewPagerFixed) findViewById(R.id.pager);

		Utils.logh(TAG, "onCreate ----- ");
		setData(getIntent());

	}

	private void setData(Intent intent) {
		int pagerPosition = intent.getIntExtra(EXTRA_IMAGE_INDEX, 0);
		String[] urls = intent.getStringArrayExtra(EXTRA_IMAGE_URLS);
		String sn = intent.getStringExtra(MEMBER_SN);
		// 转存到列表
		albumList = new ArrayList<String>();
		for(String name : urls) {
			albumList.add(name);
		}
		Utils.logh(TAG, "pagerPosition: " + pagerPosition + " mListener: " + (null != mListener) + " sn: " + sn + " \n urls: " + urls.toString());
		
		if(null != mListener) {
			Utils.setVisible(moreView);
			moreView.setOnClickListener(this);
		} else {
			Utils.setGone(moreView);
		}
		
		mAdapter = new AlbumPagerAdapter(getSupportFragmentManager(), sn);
		mPager.setAdapter(mAdapter);
		indicator = (TextView) findViewById(R.id.indicator);

		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
				.getAdapter().getCount());
		indicator.setText(text);
		// 更新下标
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int page) {
				indicator.setText(getString(R.string.viewpager_indicator,
						page + 1, mAdapter.getCount()));
			}

		});
//		if (savedInstanceState != null) {
//			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
//		}

		mPager.setCurrentItem(pagerPosition);
	}
	
	private void deleteAlbum(String sn, String album) {
		int item = mPager.getCurrentItem();
		int cnt = mAdapter.getCount();
		Utils.logh(TAG, "1---deleteAlbum item: " + item + " cnt: " + cnt);
		albumList.remove(item);
		item = mPager.getCurrentItem();
		cnt = mAdapter.getCount();
		Utils.logh(TAG, "2===deleteAlbum item: " + item + " cnt: " + cnt);
		if(item + 1 > cnt) {
			item --;
		}
		indicator.setText(getString(R.string.viewpager_indicator,
				item + 1, mAdapter.getCount()));
		mAdapter.notifyDataSetChanged();
		mListener.onAlbumDelete(sn, album);
	}

	private String getCurrentAlbumName() {
		String name = albumList.get(mPager.getCurrentItem());
		Utils.logh(TAG, "getCurrentAlbumName: " + name);
		return name;
	}
	
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
//	}

	private class AlbumPagerAdapter extends FragmentStatePagerAdapter {
		private String memSn;

		public AlbumPagerAdapter(FragmentManager fm, String memSn) {
			super(fm);
			this.memSn = memSn;
		}

		public String getMemSn() {
			return memSn;
		}
		
		@Override
        public int getItemPosition(Object object) {
        	return PagerAdapter.POSITION_NONE;
        }
		
		@Override
		public int getCount() {
			return albumList.size();
		}

		@Override
		public Fragment getItem(int position) {
			return AlbumDetailFragment.newInstance(albumList.get(position), memSn);
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
			case R.id.customer_album_detail_topbar_back:
				finishWithAnim();
				break;
			case R.id.customer_album_detail_operation:
				AlbumSelectActivity.startAlbumSelect(AlbumPagerActivity.this);
				break;
		}
	}
	
	@Override
	public void onAvatarSelect() {
		if(!Utils.isConnected(this)){
			return;
		}
		String sn = mAdapter.getMemSn();
		String filename = getCurrentAlbumName();
		Drawable avatarDrawable = AsyncImageLoader.getInstance().getAlbum(this, sn,filename, Const.ALBUM_ORIGINAL);
		if(null == avatarDrawable) {
			Toast.makeText(this, R.string.str_toast_album_loading, Toast.LENGTH_SHORT).show();
			return ;
		}
//		if(null == avatarDrawable){
//			AsyncImageLoader.getInstance().loadAlbum(this, sn, filename, Const.ALBUM_ORIGINAL, new ImageCallback() {
//				@Override
//				public void imageLoaded(Drawable imageDrawable) {
//					uploadAvatar(imageDrawable);
//				}
//			});
//		}
		else{
			uploadAvatar(avatarDrawable);
		}
	}
	
	private void uploadAvatar(final Drawable avatarDrawable){
		WaitDialog.showWaitDialog(this, R.string.str_loading_avatar_album);
		new AsyncTask<Object, Object, Integer>() {
			Bitmap bitmap = ((BitmapDrawable) avatarDrawable).getBitmap();
			String sn = mAdapter.getMemSn();
			UpdateAvatar request = new UpdateAvatar(AlbumPagerActivity.this, bitmap, sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					mListener.onAvatarReset(sn, request.getImageName(), bitmap);
					Toast.makeText(AlbumPagerActivity.this,
							R.string.str_toast_update_avatar_success, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(AlbumPagerActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}


	@Override
	public void onDeleteSelect() {
		if(!Utils.isConnected(this)){
			return;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_delete_msg);
		new AsyncTask<Object, Object, Integer>() {
			String sn = mAdapter.getMemSn();
			String filename = getCurrentAlbumName();
			DeleteAlbum request = new DeleteAlbum(AlbumPagerActivity.this, sn, filename);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					deleteAlbum(sn, filename);
				} else {
					Toast.makeText(AlbumPagerActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	public interface OnAlbumSetAvatarListener {
		public abstract void onAvatarReset(String sn, String avatar, Bitmap bitmap);
		public abstract void onAlbumDelete(String sn, String album);
	}
}