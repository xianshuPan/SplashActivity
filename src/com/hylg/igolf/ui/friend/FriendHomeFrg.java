package com.hylg.igolf.ui.friend;

import java.util.ArrayList;
import java.util.List;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.loader.GetTipsCountLoader;
import com.hylg.igolf.imagepicker.ImageGridActivity;
import com.hylg.igolf.ui.view.PagerSlidingTabStrip;
import com.hylg.igolf.ui.view.ZoomOutPageTransformer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FriendHomeFrg extends Fragment {
	
	private static final String 				TAG = "FriendHomeFrg";
	
	/*
	 * 头部的relative
	 * */
	private RelativeLayout 						mHeadRelative = null;
  
    /*
     * 显示最新、热门、本地、关注四个朋友圈
     * */
    private ViewPager 							viewPager;  
    private List<Fragment> 						fragmentList;  
    private PagerSlidingTabStrip 				mTabsIndicater;
    
    private ImageView 							mCameraImage = null,
    		          							mCustomerImage = null;
    
    /*
     * 显示用户有几条新消息
     * */
    private TextView                            mTipsCountTxt = null;
    
    /** 
     * 获取当前屏幕的密度 
     */  
    private DisplayMetrics 						dm; 
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		DebugTools.getDebug().debug_v(TAG, " --- onCreate");
		super.onCreate(savedInstanceState);
	}
    
    @Override
   	public void onViewCreated(View view,Bundle savedInstanceState) {
   		DebugTools.getDebug().debug_v(TAG, " --- onViewCreated");
   		
   		int height = mHeadRelative.getHeight();
		
		DebugTools.getDebug().debug_v(TAG, "头部的高度-----》》》"+height);
   		super.onViewCreated(view, savedInstanceState);
   	}
  
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){  
        super.onCreate(savedInstanceState);  
        
        View view = inflater.inflate(R.layout.friend_frg_home1, null);
        //setContentView(R.layout.friend_frg_home1);  
  
        initTabHost(view);  
 
        initViewPager(view);  
        
        return view;
    }  
    
    @Override
	public void onResume() {
		DebugTools.getDebug().debug_v(TAG, "onResume..");
		
		DebugTools.getDebug().debug_v(TAG, "tipsAmount-----》》》"+MainApp.getInstance().getGlobalData().tipsAmount);
		
		if (MainApp.getInstance().getGlobalData().tipsAmount > 0) {
			
			mTipsCountTxt.setText(MainApp.getInstance().getGlobalData().tipsAmount+" 条新评论");
			
			mTipsCountTxt.setVisibility(View.VISIBLE);
			
		} else {
			
			mTipsCountTxt.setVisibility(View.GONE);
		}
		
		super.onResume();
	}
    
    @Override 
    public void onPause () {
    	
    	DebugTools.getDebug().debug_v(TAG, "onPause..");
    	getTipsCount();
    	
    	super.onPause();
    }
    
    private GetTipsCountLoader getTipsCountLoader = null;
	private void getTipsCount() {
		
		if (getTipsCountLoader == null) {
			
			getTipsCountLoader = new GetTipsCountLoader(getActivity(), MainApp.getInstance().getCustomer().sn);
		}
		
		if (getTipsCountLoader.isRunning()) {
			
			return;
		}
		
		getTipsCountLoader.requestData();
	}
  
    private void initTabHost(View view) {  
        fragmentList = new ArrayList<Fragment>();  
        
        mHeadRelative = (RelativeLayout) view.findViewById(R.id.friend_frg_home_title_relative); 
        
        mTabsIndicater = (PagerSlidingTabStrip) view.findViewById(R.id.friend_frg_tab);
        
        mCameraImage = (ImageView) view.findViewById(R.id.friend_frg_camera_image);  
        mCameraImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(getActivity(), ImageGridActivity.class);
				startActivity(intent);
			}
		});
        
//        intentmCustomerImage = (ImageView) view.findViewById(R.id.friend_frg_camera_customer_image);
//        mCustomerImage.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(getActivity(), CustomerHomeActivity.class);
//				startActivity(intent);
//			}
//		});
        
        mTipsCountTxt = (TextView) view.findViewById(R.id.friend_frg_home_tips_count_text);
        mTipsCountTxt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				FriendNewTipsCountActivity.startFriendTipsCountActivity(getActivity());
			}
		});
        
        //tabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);  
        //tabHost.setup((Context)getActivity(), getChildFragmentManager(),  android.R.id.tabcontent);  
      
  
        //LayoutInflater inflate = LayoutInflater.from(this);  
//        View tab0 = mInflater.inflate(R.layout.friend_tab_indicator, null);  
//        View tab1 = mInflater.inflate(R.layout.friend_tab_indicator, null);  
//        View tab2 = mInflater.inflate(R.layout.friend_tab_indicator, null);  
//        View tab3 = mInflater.inflate(R.layout.friend_tab_indicator, null);   
//  
//        tab0.setEnabled(false);  
//        tab1.setEnabled(true);  
//        tab2.setEnabled(true);  
//        tab3.setEnabled(true); 
        
        
        FriendHotFrg friendHot = new FriendHotFrg();
        //friendHot.setArguments(data);
        
        FriendNewFrg friendNewFrg = new FriendNewFrg();
        friendNewFrg.setHeadRelative(mHeadRelative);
        friendNewFrg.setTabHost(viewPager);
        fragmentList.add(friendNewFrg);  
        fragmentList.add(friendHot);  
        fragmentList.add(new FriendLocalFrg());  
        fragmentList.add(new FriendAttentionFrg());  
        
//        TextView title0 = (TextView)tab0.findViewById(R.id.friend_tab_indicator_txt);
//        title0.setText("最新");
//        tabHost.addTab(tabHost.newTabSpec("tab0").setIndicator(tab0), FriendNewFrg.class, null); 
//        tabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
//  
//        TextView title1 = (TextView)tab1.findViewById(R.id.friend_tab_indicator_txt);
//        title1.setText("热门");
//        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(tab1), FriendHotFrg.class, null); 
//        
//        TextView title2 = (TextView)tab2.findViewById(R.id.friend_tab_indicator_txt);
//        title2.setText("本地");
//        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(tab2), FriendLocalFrg.class, null);  
//        
//        TextView title3 = (TextView)tab3.findViewById(R.id.friend_tab_indicator_txt);
//        title3.setText("关注");
//        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator(tab3), FriendAttentionFrg.class, null);
  
        //currentTabId = "tab0";  
        //currentView = tab0;  
  
//        tabHost.setOnTabChangedListener(new OnTabChangeListener() {  
//            @Override  
//            public void onTabChanged(String tabId) {  
//            	
//                View preView = tabHost.getCurrentTabView();  
//            	//ImageView indicator = (ImageView)preView.findViewById(R.id.friend_tab_indicator_img);
//            	
//                if (!currentTabId.equalsIgnoreCase(tabId)) {  
//                    currentView.setEnabled(true);  
//  
//                   // indicator.setVisibility(View.VISIBLE);
//                    currentView = preView;  
//                    preView.setEnabled(false);  
//  
//                    currentTabId = tabId;  
//                } else {
//                	
//                	// indicator.setVisibility(View.GONE);
//                }
//                viewPager.setCurrentItem(tabHost.getCurrentTab());  
//            }  
//        });  
//        
//        tabHost.setCurrentTab(0);
    }  
  
    private void initViewPager(View view) {  
    	
    	 
        viewPager = (ViewPager) view.findViewById(R.id.itemViewPager);  
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {  
  
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
  
        viewPager.setAdapter(new FragmentViewPagerAdapter(getChildFragmentManager()));  
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        
        mTabsIndicater.setViewPager(viewPager);
        
        ((FriendNewFrg)fragmentList.get(0)).setTabHost(viewPager);
        
        setTabsValue();
    }  
    
    public class DepthPageTransformer implements PageTransformer { 
    	
        private  float MIN_SCALE = 0.75f;  
      
        @Override  
        public void transformPage(View view, float position) {  
            int pageWidth = view.getWidth();  
            if (position < -1) { // [-Infinity,-1)  
                                    // This page is way off-screen to the left.  
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]  
                                        // Use the default slide transition when  
                                        // moving to the left page  
                view.setAlpha(1);  
                view.setTranslationX(0);  
                view.setScaleX(1);  
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]  
                                        // Fade the page out.  
                view.setAlpha(1 - position);  
                // Counteract the default slide transition  
                view.setTranslationX(pageWidth * -position);  
                // Scale the page down (between MIN_SCALE and 1)  
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)  
                        * (1 - Math.abs(position));  
                view.setScaleX(scaleFactor);  
                view.setScaleY(scaleFactor);  
                
            } else { // (1,+Infinity]  
                        // This page is way off-screen to the right.  
                view.setAlpha(0);  
      
            }  
        }  
      
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
    	
    	private final String[] TITLES = { "最新", "热门","本地","关注"};
      
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
