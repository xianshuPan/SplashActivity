package com.hylg.igolf.ui.golfers;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.ui.MainActivity;
import com.hylg.igolf.ui.customer.InviteFriendActivity;
import com.hylg.igolf.ui.hall.InviteDetailOpenOtherActivity;
import com.hylg.igolf.ui.hall.OpenInviteListFrgNew;
import com.hylg.igolf.ui.hall.StartInviteOpenActivity;
import com.hylg.igolf.ui.view.MyViewPager;
import com.hylg.igolf.ui.view.ZoomOutPageTransformer;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GolfersAndInviteHomeFrg extends Fragment implements View.OnClickListener,
		MainActivity.onKeyDownClick{
	private static final String TAG = "GolfersAndInviteHomeFrg";

	//private final static int CONTAINER = R.id.golfers_and_invite_frame;
	private static GolfersAndInviteHomeFrg hallFrg = null;
	//private FragmentTabHost mTabHost;
	private final static String TABHOST_HALL_OPEN = "hall_open";
	private final static String TABHOST_HALL_STS = "hall_sts";

	
	private ImageView mInviteGolfersImage = null,mSearchImage = null;

	private TextView  mInvitegGolfersTitleTxt,mInviteHallTitleTxt = null;

	private MyViewPager viewPager;
	private List<Fragment> fragmentList;

	private FrameLayout mSearchContainer;

	private View searchBar, flowBar;
	private EditText searchEt;

	private FragmentActivity mContext=null;

	private long app_id = -1;

	private int local_fans_amount= 0;

	/**
     * 获取当前屏幕的密度 
     */  
    private DisplayMetrics dm; 
    
    
	public static GolfersAndInviteHomeFrg getInstance() {
		if(null == hallFrg) {
			hallFrg = new GolfersAndInviteHomeFrg();
		}
		
		return hallFrg;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		DebugTools.getDebug().debug_v(TAG, " --- onCreateView");

		super.onCreate(savedInstanceState);

		mContext = this.getActivity();

		View view = inflater.inflate(R.layout.golfers_and_invite_frg_home, container, false);

		mInviteGolfersImage = (ImageView) view.findViewById(R.id.golfers_and_invite_invite_image);
		mSearchImage = (ImageView) view.findViewById(R.id.golfers_and_invite_search_image);
		mInvitegGolfersTitleTxt = (TextView) view.findViewById(R.id.golfers_and_invite_golfers_text);
		mInviteHallTitleTxt = (TextView) view.findViewById(R.id.golfers_and_invite_hall_text);

		mSearchContainer = (FrameLayout) view.findViewById(R.id.golfers_and_invite_search_container);

		flowBar = View.inflate(mContext, R.layout.golfers_ac_search_bar, null);
		mSearchContainer.addView(flowBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		searchBar = flowBar.findViewById(R.id.golfers_list_search_bar);
		searchBar.findViewById(R.id.golfers_list_search_bar_cancel).setOnClickListener(this);
		searchEt = (EditText) searchBar.findViewById(R.id.golfers_list_search_box_input);
		searchEt.setOnEditorActionListener(mOnEditorActionListener);
		flowBar.findViewById(R.id.golfers_list_search_box_cancel).setOnClickListener(this);
		Utils.setGone(flowBar);

		mInvitegGolfersTitleTxt.setSelected(true);

		mSearchContainer.setOnClickListener(this);
		mInviteGolfersImage.setOnClickListener(this);
		mSearchImage.setOnClickListener(this);
		mInviteHallTitleTxt.setOnClickListener(this);
		mInvitegGolfersTitleTxt.setOnClickListener(this);

		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(GolfersHomeFrgNew.getInstance());
		fragmentList.add(OpenInviteListFrgNew.getInstance());

		//navigateToGolfers();


		viewPager = (MyViewPager) view.findViewById(R.id.golfers_and_invite_itemViewPager);
		viewPager.setVisibility(View.GONE);
		viewPager.setScrollble(false);
		viewPager.setAdapter(new FragmentViewPagerAdapter(getChildFragmentManager()));
		viewPager.setEnabled(false);

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
		viewPager.setPageTransformer(true, new ZoomOutPageTransformer());


		boolean fa = SharedPref.getBoolean(MainApp.getInstance().getCustomer().sn+"_Registerd",getActivity());
		if (!fa) {

			showInvitePopWin();
		}

		return view;
	}

	@Override
	public void onViewCreated(View view,Bundle savedInstanceState) {
		DebugTools.getDebug().debug_v(TAG, " --- onViewCreated");

		navigateToInviteHall();
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onPause () {

		DebugTools.getDebug().debug_v(TAG, " --- onPause..");

		super.onPause();
	}

	@Override
	public void onResume() {
		DebugTools.getDebug().debug_v(TAG, " --- onResume..");

		super.onResume();
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, "resultCode: " + resultCode);

		if(Activity.RESULT_OK == resultCode && null != intent) {
			switch(requestCode) {
				case Const.REQUST_CODE_INVITE_DETAIL_OPEN: {
					if(!intent.getExtras().getBoolean(InviteDetailOpenOtherActivity.BUNDLE_KEY_STATUS_CHANGED)) {
						Log.w(TAG, "REQUST_CODE_INVITE_DETAIL_OPEN false should be true !!!");
					} else {
						// 状态变化了，目前重刷列表
						OpenInviteListFrgNew.getInstance().handler.sendEmptyMessageDelayed(OpenInviteListFrgNew.MSG_INIT_LIST, OpenInviteListFrgNew.INIT_DELAY);
					}
					return ;
				}

				case Const.REQUST_CODE_INVITE_OPEN: {

					// 状态变化了，目前重刷列表
					//navigateToInviteHall();
					//OpenInviteListFrgNew.getInstance().handler.sendEmptyMessageDelayed(OpenInviteListFrgNew.MSG_INIT_LIST, OpenInviteListFrgNew.INIT_DELAY);

					app_id = intent.getLongExtra("app_id",-1);
					local_fans_amount = intent.getIntExtra("local_fans_amount",0);
					Message msg = handler.obtainMessage();
					msg.what = 0;
					handler.sendMessageDelayed(msg, 500);
					return ;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.golfers_and_invite_golfers_text:

				navigateToGolfers();

				break;

			case R.id.golfers_and_invite_hall_text:

				navigateToInviteHall();

				break;

			case R.id.golfers_and_invite_invite_image:

				StartInviteOpenActivity.startOpenInviteForResult(this);
				break;

			case R.id.golfers_and_invite_search_image:
				displaySearchBar();
				break;

			case R.id.golfers_list_search_bar_cancel:
				doSearch();
				break;

			case R.id.golfers_list_search_box_cancel:
				if(null != Utils.getEditTextString(searchEt)) {
					searchEt.setText("");
				}
				break;

//			case R.id.golfers_and_invite_search_container:
//
//				dismissSearchBar();
//				break;
		}
	}


	private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

			if(event != null &&
					event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
					event.getAction()  == KeyEvent.ACTION_DOWN ) {
				Utils.logh(TAG, "======" + event.getKeyCode() + "  " + event.getAction());
				doSearch();
				return true;
			}
			return false;
		}

	};

	private void doSearch() {
		if(null == Utils.getEditTextString(searchEt)) {
			Toast.makeText(mContext, R.string.str_toast_keyword_null, Toast.LENGTH_SHORT).show();
		} else if(!Utils.isConnected(mContext)) {
			;
		} else {
			dismissSearchBar();
			searchByKeyword(Utils.getEditTextString(searchEt));
		}
	}

	private void searchByKeyword(String keyword) {
		searchEt.setText("");
		GolfersSearchResultActivity.startGolfersSearchResult(mContext, keyword);
		mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	private void displaySearchBar() {

		mSearchContainer.setVisibility(View.VISIBLE);
//		if(flowBar.getVisibility() == View.VISIBLE) {
//			return;
//		}
		Utils.setVisible(flowBar);
		Animation anim = new TranslateAnimation(0, 0, -searchBar.getHeight(), 0);
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				Utils.setVisible(searchBar);
			}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				searchEt.requestFocus();
			}
		});
		searchBar.startAnimation(anim);
	}

	private void dismissSearchBar() {

		mSearchContainer.setVisibility(View.GONE);
//		if(flowBar.getVisibility() != View.VISIBLE) {
//			return;
//		}
		Animation anim = new TranslateAnimation(0, 0, 0, -searchBar.getHeight());
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				Utils.setInvisible(searchBar);
				Utils.setGone(flowBar);
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(searchEt.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});
		searchBar.startAnimation(anim);
	}


	private void navigateToGolfers() {

		mInvitegGolfersTitleTxt.setSelected(true);
		mInviteHallTitleTxt.setSelected(false);
		mSearchImage.setVisibility(View.VISIBLE);
		mInviteGolfersImage.setVisibility(View.GONE);

		String tag = "Golfers";

		FragmentManager fm = getChildFragmentManager();
		Fragment frg = fm.findFragmentByTag(tag);
		Utils.logh(TAG , "GolfersHomeFrg Selected ...... exist " + (null!=frg));
		if(null == frg) {
			frg = new GolfersHomeFrgNew();
		}
		replaceFragment(fm, frg, tag);

//		viewPager.setCurrentItem(0);
	}

	private void navigateToInviteHall() {

		mInvitegGolfersTitleTxt.setSelected(false);
		mInviteHallTitleTxt.setSelected(true);
		mSearchImage.setVisibility(View.GONE);
		mInviteGolfersImage.setVisibility(View.VISIBLE);

		String tag = "InviteHall";

		FragmentManager fm = getChildFragmentManager();
		Fragment frg = fm.findFragmentByTag(tag);
		Utils.logh(TAG , "GolfersHomeFrg Selected ...... exist " + (null!=frg));
		if(null == frg) {
			frg = new OpenInviteListFrgNew();
		}
		replaceFragment(fm, frg, tag);

		//viewPager.setCurrentItem(1);
	}

	/**
	 * Replace fragment of activity, to display different content
	 * while click navigation button.
	 * @param fragment The fragment to display
	 * @param tag The fragment tag
	 */
	private void replaceFragment(FragmentManager fm, Fragment fragment, String tag) {
		FragmentTransaction ft = fm.beginTransaction();
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

		ft.replace(R.id.golfers_and_invite_frame, fragment, tag);

		if (!tag.equalsIgnoreCase("Golfers")) {

			ft.addToBackStack(tag);
		}

		ft.commit();
	}

	@Override
	public boolean onKeyDown() {

		if (mSearchContainer.getVisibility() == View.VISIBLE) {

			dismissSearchBar();
			return true;
		}

		return false;
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

	public Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {

			//initListDataAsync(reqData);

			switch (msg.what){

				case 0:

					showDesPopWin();
					navigateToInviteHall();
					break;
			}

			return false;
		}
	});


	private PopupWindow mDesPopWin;
	private void showDesPopWin() {
		if(null != mDesPopWin && mDesPopWin.isShowing()) {
			return ;
		}
		RelativeLayout sv = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.invite_friend_popwin, null);
		ImageView inviteFriendImage = (ImageView)sv.findViewById(R.id.invite_friend_pop_commit_bg);

		TextView local_fans_text = (TextView)sv.findViewById(R.id.invite_list_open_item_fans);
		mDesPopWin = new PopupWindow(sv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
		mDesPopWin.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		mDesPopWin.setAnimationStyle(android.R.style.Animation_Dialog);

		inviteFriendImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				InviteFriendActivity.startInviteFriendActivity(getActivity(), InviteFriendActivity.OPEN_INVITE, app_id);
				dismissExchgPopwin();
			}
		});
		sv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissExchgPopwin();
			}
		});
		mDesPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				if (null != mDesPopWin) {
					Utils.logh(TAG, "onDismiss ");
					mDesPopWin = null;
				}
			}
		});
		local_fans_text.setText(String.valueOf(local_fans_amount));
		mDesPopWin.showAtLocation(sv, Gravity.CENTER, 0, 0);
	}

	private void showInvitePopWin() {
		if(null != mDesPopWin && mDesPopWin.isShowing()) {
			return ;
		}
		RelativeLayout sv = (RelativeLayout) LayoutInflater.from(getActivity())
				.inflate(R.layout.invite_open_hint_popwin, null);
		mDesPopWin = new PopupWindow(sv, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
		mDesPopWin.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		mDesPopWin.setAnimationStyle(android.R.style.Animation_Dialog);
		sv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				SharedPref.setBoolean(MainApp.getInstance().getCustomer().sn + "_Registerd", true, getActivity());
				dismissExchgPopwin();
			}
		});
		mDesPopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				if (null != mDesPopWin) {
					Utils.logh(TAG, "onDismiss ");
					mDesPopWin = null;
				}
			}
		});
		//mDesPopWin.showAtLocation(mInviteGolfersImage, Gravity.BOTTOM, 0, 0);

		mDesPopWin.showAsDropDown(mInviteGolfersImage, 0, 0);

	}

	private void dismissExchgPopwin() {
		if(null != mDesPopWin && mDesPopWin.isShowing()) {
			mDesPopWin.dismiss();
//			mDesPopWin = null;
		}
	}

}
