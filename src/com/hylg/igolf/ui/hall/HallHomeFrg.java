package com.hylg.igolf.ui.hall;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.ui.view.PagerSlidingTabStrip;
import com.hylg.igolf.ui.view.ZoomOutPageTransformer;
import com.hylg.igolf.utils.Utils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class HallHomeFrg extends Fragment {
	private static final String TAG = "GolfersAndInviteHomeFrg";
	private static HallHomeFrg hallFrg = null;
	//private FragmentTabHost mTabHost;
	private final static String TABHOST_HALL_OPEN = "hall_open";
	private final static String TABHOST_HALL_STS = "hall_sts";
	private static ImageView stsAlertImg;
	
	private ImageView mCustomerImage = null;
	
	
	private ViewPager viewPager;  
    private List<Fragment> fragmentList;  
    private PagerSlidingTabStrip mTabsIndicater;
	
    
    /** 
     * 获取当前屏幕的密度 
     */  
    private DisplayMetrics dm; 
    
    
	public static HallHomeFrg getInstance() {
		if(null == hallFrg) {
			hallFrg = new HallHomeFrg();
		}
		
		return hallFrg;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Bundle args = getArguments();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentList = new ArrayList<Fragment>();  
		View view = inflater.inflate(R.layout.hall_frg_home, container, false);
		//mTabHost = (FragmentTabHost) view.findViewById(R.id.hall_tabhost);
		
		//mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.hall_real_tabcontent);
		
		//mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
		// 大厅
		View open = View.inflate(getActivity(), R.layout.friend_tab_indicator, null);
		((TextView) open.findViewById(R.id.friend_tab_indicator_txt)).setText(R.string.str_hall_tab_open);
		//mTabHost.addTab(mTabHost.newTabSpec(TABHOST_HALL_OPEN).setIndicator(open),HallOpenPresetFrg.class, null);
		Utils.setGone(open.findViewById(R.id.tab_indicator_img));
		
		// 我的约球
		View sts = View.inflate(getActivity(), R.layout.friend_tab_indicator, null);
		((TextView) sts.findViewById(R.id.friend_tab_indicator_txt)).setText(R.string.str_hall_tab_sts);
		stsAlertImg = (ImageView) sts.findViewById(R.id.tab_indicator_img);
		if(MainApp.getInstance().getGlobalData().msgNumInvite > 0) {
			Utils.setVisible(stsAlertImg);
		} else {
			Utils.setGone(stsAlertImg);
		}
		
		fragmentList.add(new HallOpenPresetFrg());
		fragmentList.add(new HallMyInvitesFrg());
		//fragmentList.add(new HallMyTeachingFrg());
		
		mTabsIndicater = (PagerSlidingTabStrip) view.findViewById(R.id.hall_frg_tab);
		
		viewPager = (ViewPager) view.findViewById(R.id.hall_itemViewPager);  
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
	        
	      setTabsValue();
	      
//	      mCustomerImage = (ImageView) view.findViewById(R.id.friend_frg_camera_customer_image);
//		  mCustomerImage.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//						// TODO Auto-generated method stub
//						Intent intent = new Intent(getActivity(), CustomerHomeActivity.class);
//						startActivity(intent);
//					}
//		  });
		
		
		//mTabHost.addTab(mTabHost.newTabSpec(TABHOST_HALL_STS).setIndicator(sts),HallMyInvitesFrg.class, null);
//		addTabForHost(TABHOST_HALL_OPEN, R.string.str_hall_tab_open, HallOpenPresetFrg.class);
//		addTabForHost(TABHOST_HALL_STS, R.string.str_hall_tab_sts, HallMyInvitesFrg.class);
		return view;
	}
	
//	private void addTabForHost(String tag, int txtId, Class<?> fragment) {
//		View view = View.inflate(getActivity(), R.layout.tab_indicator, null);
//		((TextView) view.findViewById(R.id.tab_indicator_txt)).setText(txtId);
//		mTabHost.addTab(mTabHost.newTabSpec(tag).setIndicator(view),
//						fragment, null);
//	}
	
	public static void updateTabAlert(int count) {
		Utils.logh(TAG, "updateTabAlert count: " + count);
		if(count > 0) {
			Utils.setVisible(stsAlertImg);
		} else {
			Utils.setGone(stsAlertImg);
		}
	}
	
    @Override
    public void onDestroyView() {
    	Utils.logh(TAG, " --- onDestroyView");
        super.onDestroyView();
        //mTabHost = null;
    }
    
    @Override
    public void onDetach()
    {
        super.onDetach();
        try
        {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
     
        } catch (NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
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
    	mTabsIndicater.setUnderlineHeight((int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 1, dm));  
        // 设置Tab Indicator的高度  
    	mTabsIndicater.setIndicatorHeight((int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 3, dm));  
        // 设置Tab标题文字的大小  
    	mTabsIndicater.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, dm));  
    	mTabsIndicater.setTextColor(getResources().getColor(R.color.color_friend_item_praiser_name));
    	
        // 设置Tab Indicator的颜色  
    	//mTabsIndicater.setIndicatorColor(Color.parseColor("#000"));  
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)  
    	//mTabsIndicater.setSelectedTextColor(Color.parseColor("#45c01a"));  
        // 取消点击Tab时的背景色  
    	mTabsIndicater.setTabBackground(0);  
    }  
    
    public class FragmentViewPagerAdapter extends FragmentPagerAdapter {  
    	
    	private final String[] TITLES = { "约球大厅", "我的约球"};
      
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
