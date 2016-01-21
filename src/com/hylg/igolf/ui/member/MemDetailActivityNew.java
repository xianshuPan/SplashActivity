package com.hylg.igolf.ui.member;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.cs.data.Member;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.loader.GetMemberloader;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.FriendAttentionAdd;
import com.hylg.igolf.cs.request.GetMember;
import com.hylg.igolf.cs.request.GetSelfInfo;
import com.hylg.igolf.ui.coach.CoachInviteActivity;
import com.hylg.igolf.ui.common.ImageSelectActivity.onImageSelectListener;
import com.hylg.igolf.ui.customer.MyAttentionsActivity;
import com.hylg.igolf.ui.customer.MyFollowerActivity;
import com.hylg.igolf.ui.customer.MyPraiseActivity;
import com.hylg.igolf.ui.friend.FriendAttentionFrg;
import com.hylg.igolf.ui.friend.FriendHotFrg;
import com.hylg.igolf.ui.friend.FriendLocalFrg;
import com.hylg.igolf.ui.friend.FriendNewFrg;
import com.hylg.igolf.ui.hall.StartInviteStsActivity;
import com.hylg.igolf.ui.reqparam.CoachApplyInfoReqParam;
import com.hylg.igolf.ui.view.PagerSlidingTabStrip;
import com.hylg.igolf.ui.view.ShareMenuCutomerInfo;
import com.hylg.igolf.ui.view.ZoomOutPageTransformer;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemDetailActivityNew extends FragmentActivity implements View.OnClickListener,onImageSelectListener{
	
	private static final String 				TAG = "MemDetailActivityNew";
	
	private static final String 				BUNDLE_KEY_MEM_SN = "memSn";

	private static final String 				BUNDLE_KEY_DISPLAY_INDEX = "index";
	
	private ImageView 							customerAvatar,shareImage,attentionImage,sex;

	private TextView 							headNickName,nickName,location,attention,inviteGolfer,
												yearsExp,handicapi,best,matches,heat,rank,act;

	private LinearLayout 						mPraiseLinear,mAttentionLinear,mFollowerLinear,inviteCoach;
	private TextView                            mPraiseCountTxt,mAttentionCountTxt,mFollowerCountTxt;
	

	private GetMemberloader 					reqLoader = null;

	private Uri 								mUri;


	private int 								mAttentionState = -1,displayIndex = 0;
	
	private String 								sn      = "",
												memSn	= "",
												scroeMsg = "",
												fightMsg = "";

	
	private LayoutInflater 						mLayoutInflater;
	
	private Activity 							mContext ;

	private PagerSlidingTabStrip 				mTabsIndicater,viewToolBar;
	private ViewPager 							viewPager;
	private List<Fragment> 						fragmentList;

	private CoachItem                           coach_item ;
	
	public static void startMemDetailActivity(Activity context,String memSn,int index) {
		Intent intent = new Intent(context, MemDetailActivityNew.class);
		intent.putExtra(BUNDLE_KEY_MEM_SN, memSn);
		intent.putExtra(BUNDLE_KEY_DISPLAY_INDEX,index);
		context.startActivity(intent);
		context.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	public static void startMemDetailActivity(Activity context,String memSn) {
		Intent intent = new Intent(context, MemDetailActivityNew.class);
		intent.putExtra(BUNDLE_KEY_MEM_SN, memSn);
		context.startActivity(intent);
		context.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		sn = MainApp.getInstance().getCustomer().sn;
		
		getViews();
		
		getMemberInfo();

		getSelfInfo();

	}

	
	private void getViews() {
		
		mLayoutInflater = getLayoutInflater();
		setContentView(R.layout.mem_info_ac_detail_new);

		attentionImage = (ImageView) findViewById(R.id.mem_info_attention_image);
		attention = (TextView) findViewById(R.id.mem_info_attention_text);
		inviteGolfer = (TextView) findViewById(R.id.mem_info_invite_golfer_text);
		inviteCoach = (LinearLayout) findViewById(R.id.mem_info_invite_coach_linear);
		attention.setOnClickListener(this);
		inviteGolfer.setOnClickListener(this);
		inviteCoach.setOnClickListener(this);
		attention.setText("");
		
		findViewById(R.id.mem_info_head_back_image).setOnClickListener(this);

		shareImage = (ImageView) findViewById(R.id.mem_info_share_image);
		shareImage.setOnClickListener(this);
		nickName = (TextView) findViewById(R.id.customer_info_name_text);
		sex = (ImageView) findViewById(R.id.customer_info_sex_image);
		headNickName = (TextView) findViewById(R.id.mem_info_head_nick_text);
		handicapi = (TextView) findViewById(R.id.customer_info_handicapi_txt);
		best = (TextView) findViewById(R.id.customer_info_best_txt);
		matches = (TextView) findViewById(R.id.customer_info_matches_txt);
		location = (TextView) findViewById(R.id.customer_info_location_txt);
		
		/**/
		yearsExp = (TextView) findViewById(R.id.customer_info_yearsexp_txt);
		rank = (TextView) findViewById(R.id.customer_info_cityrank_txt);
		heat = (TextView) findViewById(R.id.customer_info_heat_txt);
		act = (TextView) findViewById(R.id.customer_info_activity_txt);
		customerAvatar = (ImageView) findViewById(R.id.customer_info_avatar_image);

		mPraiseLinear = (LinearLayout) findViewById(R.id.customer_info_my_praise_linear);
		mFollowerLinear = (LinearLayout) findViewById(R.id.customer_info_my_follower_linear);
		mAttentionLinear = (LinearLayout) findViewById(R.id.customer_info_my_attention_linear);
		mPraiseCountTxt = (TextView) findViewById(R.id.customer_info_my_praise_txt);
		mFollowerCountTxt = (TextView) findViewById(R.id.customer_info_my_follower_txt);
		mAttentionCountTxt = (TextView) findViewById(R.id.customer_info_my_attention_txt);

		((TextView)findViewById(R.id.customer_info_my_attention_title_txt)).setText(R.string.str_mem_praise_attention);
		((TextView)findViewById(R.id.customer_info_my_follower_title_txt)).setText(R.string.str_mem_praise_follower);

		
		/*是否显示msg 的*/
//		if(MainApp.getInstance().getGlobalData().msgNumSys == 0){
//			Utils.setGone(msgAlertIv);
//		}else{
//			Utils.setVisible(msgAlertIv);
//		}
		mPraiseLinear.setOnClickListener(this);
		mFollowerLinear.setOnClickListener(this);
		mAttentionLinear.setOnClickListener(this);
		memSn = getIntent().getExtras().getString(BUNDLE_KEY_MEM_SN);
		displayIndex = getIntent().getExtras().getInt(BUNDLE_KEY_DISPLAY_INDEX);
		initTabHost();

		mTabsIndicater = (PagerSlidingTabStrip)findViewById(R.id.slidingTabs);
		//viewToolBar  = (PagerSlidingTabStrip)findViewById(R.id.viewToolbar);

		viewPager = (ViewPager) findViewById(R.id.pager);
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

		int statusBar = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
			statusBar = SystemBarUtils.getStatusBarHeight(this);

			// statusBar = 100;
		}
		int height = getResources().getDisplayMetrics().heightPixels;
		int headHeight = (mTabsIndicater.getHeight()+statusBar+findViewById(R.id.mem_info_ac_detail_new_head).getHeight());
		viewPager.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				height - headHeight));

		mTabsIndicater.setViewPager(viewPager);

		if (displayIndex == 1) {

			viewPager.setCurrentItem(displayIndex);
		}

		setTabsValue();

	}

	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {

		DisplayMetrics dm = getResources().getDisplayMetrics();

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
		mTabsIndicater.setIndicatorColor(getResources().getColor(R.color.color_gold));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		//mTabsIndicater.setSelectedTextColor(Color.parseColor("#45c01a"));
		// 取消点击Tab时的背景色
		mTabsIndicater.setTabBackground(0);
	}

	private void getMemberInfo(){
		new AsyncTask<Object, Object, Integer>() {
			GetMember request = new GetMember(mContext, MainApp.getInstance().getCustomer().sn, memSn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					
					attention.setVisibility(View.VISIBLE);

					coach_item = request.coach_item;
					initMemberInfo(request.getMember(),coach_item);
					
				} else {
					
					attention.setVisibility(View.GONE);
					Toast.makeText(mContext,request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
	}

	private void getSelfInfo() {
		if(!Utils.isConnected(mContext)){
			return;
		}

		final GetSelfInfo request = new GetSelfInfo(mContext,memSn);
		new AsyncTask<Object, Object, Integer>() {
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				if(BaseRequest.REQ_RET_OK == result) {

					mPraiseCountTxt.setText(String.valueOf(request.praiseAmount));
					mFollowerCountTxt.setText(String.valueOf(request.myFansAmount));
					mAttentionCountTxt.setText(String.valueOf(request.myAttentionAmount));

				}
				else {

					Toast.makeText(mContext,request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}

			}
		}.execute(null, null, null);

	}

	private void initTabHost() {
		fragmentList = new ArrayList<android.support.v4.app.Fragment>();

		MemTipsFrg memTipsFrg = new MemTipsFrg();
		Bundle data = new Bundle();
		data.putString("mem_sn", memSn);
		memTipsFrg.setArguments(data);
		fragmentList.add(memTipsFrg);

		MemScoreHistoryFrg frag = new MemScoreHistoryFrg();
		Bundle data1 = new Bundle();
		data1.putString("memSn", memSn);
		data1.putString("scoreMsg", scroeMsg);
		frag.setArguments(data1);
		fragmentList.add(frag);

		MemFightRecordFrg fightFrg = new MemFightRecordFrg();
		Bundle dataa = new Bundle();
		dataa.putString("mem_sn", memSn);
		fightFrg.setArguments(dataa);
		fragmentList.add(fightFrg);

	}
	
	private void initMemberInfo(Member member,CoachItem coach_item){
		
		//loadAvatar(member.sn,member.avatar);
		/*加载头像*/

		if (member == null || member.sn == null ) {

			return ;
		}
		loadAvatar(member.sn, member.avatar, customerAvatar);
		
		nickName.setText(member.nickname);
		headNickName.setText(member.nickname);
		//Utils.setLevel(MemDetailActivity.this, ratell, (int) getResources().getDimension(R.dimen.personal_detail_rate_star_size), member.rate);
		yearsExp.setText(member.yearsExpStr+getResources().getString(R.string.str_year));
		rank.setText(Utils.getCityRankString(this, member.rank));
		heat.setText(String.valueOf(member.heat));
		act.setText(String.valueOf(member.activity));
		best.setText(Utils.getIntString(this, member.best));
		handicapi.setText(Utils.getDoubleString(this, member.handicapIndex));
		matches.setText(String.valueOf(member.matches));
		location.setText(MainApp.getInstance().getGlobalData().getRegionName(member.city));
		
		mAttentionState = member.attention;
		if (member.attention == 0) {
			
			attention.setText(R.string.str_friend_attention);
			attention.setTextColor(getResources().getColor(R.color.gray));
			attentionImage.setImageResource(R.drawable.add);
			//attention.setBackgroundResource(R.drawable.attent_color);
			//attention.setBackgroundColor(getResources().getColor(R.color.color_title_txt));
			
		}
		else if (member.attention == 1) {
			
			attention.setText(R.string.str_friend_attented);
			attention.setTextColor(getResources().getColor(R.color.color_red));
			attentionImage.setImageResource(R.drawable.subtrac);
			//attention.setBackgroundResource(R.drawable.attented_color);
			//attention.setBackgroundColor(getResources().getColor(R.color.color_hint_txt));
		}

		if (member.sex == 0) {

			sex.setImageResource(R.drawable.man);
		}
		else {

			sex.setImageResource(R.drawable.woman);
		}

		if(coach_item != null && coach_item.audit == 1) {

			inviteCoach.setVisibility(View.VISIBLE);

		}
		
		//scoreView.setText(member.scoreMsg);
		scroeMsg = member.scoreMsg;
		fightMsg = member.fightMsg;
		//initAlbum(member);
	}
	
	
	@Override
	public void onResume() {
		DebugTools.getDebug().debug_v(TAG, "onResume..");
		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();

		int sdf = yearsExp.getHeight();

		DisplayMetrics asdf = getResources().getDisplayMetrics();
		DebugTools.getDebug().debug_v(TAG, "onPause..");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		clearLoader();
		Debug.stopMethodTracing();
		DebugTools.getDebug().debug_v(TAG, "onDestroy..");
	}
	

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		DebugTools.getDebug().debug_v(TAG, "onLowMemory..");
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetMemberloader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
			Utils.logh(TAG, "clearLoader reqLoader: " + reqLoader);
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	private void loadAvatar(String sn,String filename,final ImageView iv){
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(mContext, sn, filename,
				(int) getResources().getDimension(R.dimen.avatar_detail_size));
//		if(null != avatar) {
//			iv.setImageDrawable(avatar);
//		} else {
			iv.setImageResource(R.drawable.avatar_loading);
			AsyncImageLoader.getInstance().loadAvatar(mContext, sn, filename,
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if (null != imageDrawable && null != iv) {
								iv.setImageDrawable(imageDrawable);
							}
						}
					});
		//}
	}

	@Override
	public void onClick(View v) {

		switch(v.getId()) {

			case R.id.mem_info_share_image :

				ShareMenuCutomerInfo share = new ShareMenuCutomerInfo(this,viewPager,memSn);
				share.showPopupWindow();

				break;

			case R.id.mem_info_head_back_image:

				mContext.finish();
				mContext.overridePendingTransition(0,R.anim.ac_slide_right_out);
				
				break;
				
			case R.id.mem_info_attention_text:
				
				attention();
				
				break;
			case R.id.mem_info_invite_coach_linear :

				CoachInviteActivity.startCoachInviteActivity(mContext,coach_item);

				break;

			case R.id.mem_info_invite_golfer_text:

				StartInviteStsActivity.startOpenInvite(mContext, memSn);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;

			case R.id.customer_info_my_follower_linear:

				MyFollowerActivity.startMyFollowerActivity(this, memSn);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;

			case R.id.customer_info_my_attention_linear:

				MyAttentionsActivity.startMyAttentionsActivity(this, memSn);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;

			case R.id.customer_info_my_praise_linear:

				int sdf = yearsExp.getHeight();

				MyPraiseActivity.startMyPraiseActivity(this, memSn);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
		}
	}
	
	/*
	 * 添加关注
	 * */
	private void attention() {
		
		/*添加或取消关注*/
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_waiting);
		new AsyncTask<Object, Object, Integer>() {
		
			FriendAttentionAdd request = new FriendAttentionAdd(mContext,sn,memSn,mAttentionState);
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				if(BaseRequest.REQ_RET_OK == result) {
					
					mAttentionState = mAttentionState == 1 ? 0 : 1;
					
					if (mAttentionState == 0) {
						
						attention.setText(R.string.str_friend_attention);
						attention.setTextColor(getResources().getColor(R.color.gray));
						attentionImage.setImageResource(R.drawable.add);
						//attention.setBackgroundResource(R.drawable.attent_color);
						//attention.setBackgroundColor(getResources().getColor(R.color.color_title_txt));
						
					}
					else if (mAttentionState == 1) {
						
						attention.setText(R.string.str_friend_attented);
						attention.setTextColor(getResources().getColor(R.color.color_tab_green));
						attentionImage.setImageResource(R.drawable.subtrac);
						//attention.setBackgroundResource(R.drawable.attented_color);
						//attention.setBackgroundColor(getResources().getColor(R.color.color_hint_txt));
					}
					
				} else {
					

				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, " ::: requestCode: " + requestCode + " resultCode: " + resultCode + " intent: " + intent);
		if(resultCode != Activity.RESULT_OK) {
			super.onActivityResult(requestCode, resultCode, intent);
			return ;
		}
		if(Const.REQUST_CODE_SIGNATURE_MY == requestCode) {}
		if(Const.REQUST_CODE_MODIFY_MY_INFO == requestCode && null != intent){}
		if(Const.REQUST_CODE_PHOTE_TAKE_PHOTO == requestCode) {
//				startPhotoCrop(mUri);
				try {
					//addAlbum(new File(new URI(mUri.toString())));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return ;
		} else if(Const.REQUST_CODE_PHOTE_GALLERY == requestCode) {
			if(null != intent) {
//				startPhotoCrop(intent.getData());
				Uri uri = intent.getData();
				String img_path = FileUtils.getMediaImagePath(mContext, uri);
				//addAlbum(new File(img_path));
				return ;
			}
		} else if(Const.REQUST_CODE_PHOTE_CROP == requestCode) {
			if(null != intent) {
				Bundle b = intent.getExtras();
				if(null != b) {
					Bitmap photo = b.getParcelable("data");
					if(null != photo) {
						//doAddAlbum(photo,customer.sn);
						return ;
					} else {
						//Log.w(TAG, "photo null ! ");
					}
				} else {
					//Log.w(TAG, "intent.getExtras() null ! ");
				}
			}
		}
	}
	
	@Override
	public void onCameraTypeSelect() {
		mUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
				new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.getDefault())
					.format(new Date(System.currentTimeMillis())) + ".jpg"));
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
		startActivityForResult(intent, Const.REQUST_CODE_PHOTE_TAKE_PHOTO);
	}

	@Override
	public void onGalleryTypeSelect() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(Media.EXTERNAL_CONTENT_URI, "image/*");
		startActivityForResult(intent, Const.REQUST_CODE_PHOTE_GALLERY);
	}

	public class FragmentViewPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "TA的球友圈", getResources().getString(R.string.str_member_score_history_title),getResources().getString(R.string.str_member_fight_record_title)};

		public FragmentViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public android.support.v4.app.Fragment getItem(int arg0) {
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
