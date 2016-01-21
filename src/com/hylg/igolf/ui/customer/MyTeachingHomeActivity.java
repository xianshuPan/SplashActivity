package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.ui.MainActivity;
import com.hylg.igolf.ui.friend.FriendAttentionFrg;
import com.hylg.igolf.ui.friend.FriendLocalFrg;
import com.hylg.igolf.ui.friend.FriendNewFrg;
import com.hylg.igolf.ui.hall.InviteDetailActivity.onResultCallback;
import com.hylg.igolf.ui.view.PagerSlidingTabStrip;
import com.hylg.igolf.ui.view.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;

public class MyTeachingHomeActivity extends FragmentActivity implements onResultCallback {
	
	private static final String 					TAG = "CoachMyTeachingHomeActivity";

	private ImageButton                             mBack;

	private ViewPager 								viewPager;
	private List<Fragment> 							fragmentList;
	private PagerSlidingTabStrip 					mTabsIndicater;
	
	private Customer                                customer = null;

	/**
	 * 获取当前屏幕的密度
	 */
	private DisplayMetrics dm;

	public static void startCoachMyTeachingHomeActivity (Activity context) {

		Intent data = new Intent(context,MyTeachingHomeActivity.class);
		context.startActivity(data);
		context.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.hall_frg_my_teaching_home);

		customer = MainApp.getInstance().getCustomer();

		initUI();
	}

	public void initUI () {

		mBack = (ImageButton) findViewById(R.id.coach_my_teaching_home_back);
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				MainActivity.startMainActivity(MyTeachingHomeActivity.this);
				MyTeachingHomeActivity.this.finish();

			}
		});

		fragmentList = new ArrayList<Fragment>();

		mTabsIndicater = (PagerSlidingTabStrip) findViewById(R.id.my_teaching_home_tab);

		fragmentList.add(new MyStudyFrg());
		fragmentList.add(new MyTeachingFrg());

		viewPager = (ViewPager) findViewById(R.id.my_teaching_home_ViewPager);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int arg0) {

				//tabHost.setCurrentTab(arg0);
			}

		});

		viewPager.setAdapter(new FragmentViewPagerAdapter(getSupportFragmentManager()));
		viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

		mTabsIndicater.setViewPager(viewPager);

		setTabsValue();

	}

	public boolean onKeyDown (int key_code ,KeyEvent event) {

		MainActivity.startMainActivity(MyTeachingHomeActivity.this);
		MyTeachingHomeActivity.this.finish();
		return super.onKeyDown(key_code,event);
	}
	
	@Override
	public void onResume() {

		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void callback(boolean status, boolean alert) {

	}

	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {

		dm = getResources().getDisplayMetrics();

		// 设置Tab是自动填充满屏幕的
		mTabsIndicater.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		mTabsIndicater.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		mTabsIndicater.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		mTabsIndicater.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm));
		// 设置Tab标题文字的大小
		mTabsIndicater.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, dm));
		mTabsIndicater.setTextColor(getResources().getColor(R.color.color_gold));
		// 设置Tab Indicator的颜色
		//mTabsIndicater.setIndicatorColor(Color.parseColor("#000"));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		//mTabsIndicater.setSelectedTextColor(Color.parseColor("#45c01a"));
		// 取消点击Tab时的背景色
		mTabsIndicater.setTabBackground(0);

	}

	public class FragmentViewPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "我是学员", " 我是教练"};

		public FragmentViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return fragmentList.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragmentList.size();
		}

	}


}
