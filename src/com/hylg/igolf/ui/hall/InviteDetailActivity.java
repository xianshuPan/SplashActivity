package com.hylg.igolf.ui.hall;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.RatingBar.OnRatingBarChangeListener;
import cn.gl.lib.view.*;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.*;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.request.*;
import com.hylg.igolf.ui.hall.adapter.*;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.utils.*;

import org.w3c.dom.Text;

public abstract class InviteDetailActivity extends Activity implements View.OnClickListener {
	/**
	 * HallMyInvitesFrg 发起的result被HallHomeFrg拦截，无法获取onActivityResult，自定义回调接口实现
	 * @return 是否通过自定义接口回调实现
	 * @see onResultCallback
	 */
	public abstract boolean getResultByCallback();
	
	private static final String TAG = "InviteDetailActivity";
	private int finishResult;
	private Intent finishIntent;
	private View svLayout;
	private LinearLayout operBarLl; // 底部操作按钮
	// 双方头像，昵称，性别
	private InfoViewHolder leftInfoVh, rightInfoVh,right1InfoVh,right2InfoVh;//right0InfoVh
	// 称呼(一对一)及约球语
	private TextView inviteMsgTv;
	// 双方成绩信息
	private PerInfoViewHolder leftPerInfoVh, rightPerInfoVh,right1PerInfoVh,right2PerInfoVh;

	protected RelativeLayout invitee_one_relative,invitee_two_relative,invitee_three_relative;

	protected LinearLayout mInviteAllInfoLinear,mInviteShowLinear;
	protected ImageView mInviteShowImage;
	protected TextView mInviteaShowTxt;

	// 开球时间，场地；方案(一对一确定后) 1/2
	//private LinearLayout appInfoLl;
	private RelativeLayout appInfoLl;
	private TextView teeTimeTv, courseTv;
	// 方案(一对一) 2/2
	private LinearLayout plansLl;
	private NestListView plansLv;
	// 付款方式
	private NestGridView payTypeGv;
	private TextView     payTypeTxt;
	// 球注
	//private NestGridView stakeGv;
	//private TextView     stakeTxt;
	// 记分
	private LinearLayout scoreRegion;
	private ImageView scoreCardIv;
	private ImageView scoreCardInvalidIv;
	private Button scoreUploadBtn;
	private EditText scoresEt;
	// 评价
	private LinearLayout rateRegion;
	protected Button rateDoBtn;
	
	protected RatingBar rateBarRb;
	private TextView rateName;
	private ImageView rateHeadImage;

	protected RatingBar rateBarRb1;
	private TextView rateName1;
	private ImageView rateHeadImage1;

	protected RatingBar rateBarRb2;
	private TextView rateName2;
	private ImageView rateHeadImage2;
	
	// 申请(开放式)
	private LinearLayout requestRegion;
	private NestGridView requestGv;
	private TextView requestTitleTv;
	
	
	protected Customer customer;
	
	
	/*高德定位操作*/
	protected LocationManagerProxy 				mLocationManagerProxy;
	protected myAMapLocationListener      		mAMapLocationListener;
	
	protected String 							mLocationLongStr  = "",
												mLocationLatStr   = "";
	
	protected AMapLocation 						curLocation = null;
	
	
	
	public MyInviteDetail detail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hall_ac_invite_detail_new);
		finishResult = RESULT_CANCELED;
		finishIntent = null;
		getViews();
		
		customer = MainApp.getInstance().getCustomer();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		getLocation();
		
	}
	
	@Override
	protected void onPause() {
		
		 if (mLocationManagerProxy != null) {
			 mLocationManagerProxy.removeUpdates(mAMapLocationListener);
			 mLocationManagerProxy.destroy();
		  }
		 
		 mAMapLocationListener = null;
		 mLocationManagerProxy = null;
		
		super.onPause();
	}
	
	public void getLocation() {
		
		if (mLocationManagerProxy == null) {
			
			mLocationManagerProxy = LocationManagerProxy.getInstance(this);
		}
		
		if (mAMapLocationListener == null) {
			
			mAMapLocationListener = new myAMapLocationListener();
		}
		 
		 
	     //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
	     //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
	     //在定位结束后，在合适的生命周期调用destroy()方法     
	     //其中如果间隔时间为-1，则定位只定一次
	     mLocationManagerProxy.requestLocationData(
	                LocationProviderProxy.AMapNetwork, 60*1000, 15,mAMapLocationListener);
	 
	     mLocationManagerProxy.setGpsEnable(false);
		
	}

	private void getViews() {
		svLayout = findViewById(R.id.invite_detail_sv_layout);
		findViewById(R.id.invite_detail_topbar_back).setOnClickListener(this);
		operBarLl = (LinearLayout) findViewById(R.id.invite_detail_oper_bar);
		Utils.setInvisible(svLayout, operBarLl);
		getLeftInfoViews();
		getRightInfoViews();
		inviteMsgTv = (TextView) findViewById(R.id.invite_detail_message);
		getLeftPerInfoViews();
		getRightPerInfoViews();

		invitee_one_relative = (RelativeLayout) findViewById(R.id.invite_detail_invitee_realtive);
		invitee_two_relative = (RelativeLayout) findViewById(R.id.invite_detail_invitee_realtive1);
		invitee_three_relative = (RelativeLayout) findViewById(R.id.invite_detail_invitee_realtive2);

		mInviteAllInfoLinear = (LinearLayout) findViewById(R.id.invite_detail_invintee_info_all_linear);
		mInviteShowLinear = (LinearLayout) findViewById(R.id.invite_detail_invintee_info_all_show_linear);
		mInviteaShowTxt = (TextView) findViewById(R.id.invite_detail_invintee_info_all_show_txt);
		mInviteShowImage= (ImageView) findViewById(R.id.invite_detail_invintee_info_all_show_image);

		mInviteShowLinear.setOnClickListener(this);

		invitee_one_relative.setOnClickListener(this);
		invitee_two_relative.setOnClickListener(this);
		invitee_three_relative.setOnClickListener(this);
		//appInfoLl = (LinearLayout) findViewById(R.id.invite_detail_app_info);
		appInfoLl = (RelativeLayout) findViewById(R.id.invite_detail_app_info);
		teeTimeTv = (TextView) appInfoLl.findViewById(R.id.invite_detail_app_info_teetime);
		courseTv = (TextView) appInfoLl.findViewById(R.id.invite_detail_app_info_course);
		plansLl = (LinearLayout) findViewById(R.id.invite_detail_plans_ll);
		plansLv = (NestListView) findViewById(R.id.invite_detail_app_plan_list);
		payTypeGv = (NestGridView) findViewById(R.id.invite_detail_pay_type_gridview);
		payTypeTxt = (TextView) findViewById(R.id.invite_detail_pay_type_text);
		//stakeGv = (NestGridView) findViewById(R.id.invite_detail_stake_gridview);
		//stakeTxt = (TextView) findViewById(R.id.invite_detail_stake_type_text);
		scoreRegion = (LinearLayout) findViewById(R.id.invite_detail_score_region);
		scoreCardIv = (ImageView) scoreRegion.findViewById(R.id.invite_detail_score_card);
		scoreCardIv.setOnClickListener(this);
		scoreCardInvalidIv = (ImageView) scoreRegion.findViewById(R.id.invite_detail_score_card_invalid);
		scoreUploadBtn = (Button) scoreRegion.findViewById(R.id.invite_detail_score_btn_do);
		scoreUploadBtn.setOnClickListener(this);
		scoresEt = (EditText) scoreRegion.findViewById(R.id.invite_detail_scores);
		rateRegion = (LinearLayout) findViewById(R.id.invite_detail_rate_region);
		rateDoBtn = (Button) rateRegion.findViewById(R.id.invite_detail_rate_btn_do);
		rateDoBtn.setOnClickListener(this);
		
		rateBarRb = (RatingBar) rateRegion.findViewById(R.id.invite_detail_rate_set_bar);
		rateName = (TextView) rateRegion.findViewById(R.id.invite_detail_rate_name);
		rateHeadImage = (ImageView) rateRegion.findViewById(R.id.user_headImage);
		
		rateBarRb1 = (RatingBar) rateRegion.findViewById(R.id.invite_detail_rate_set_bar1);
		rateName1 = (TextView) rateRegion.findViewById(R.id.invite_detail_rate_name1);
		rateHeadImage1 = (ImageView) rateRegion.findViewById(R.id.user_headImage1);
		
		rateBarRb2 = (RatingBar) rateRegion.findViewById(R.id.invite_detail_rate_set_bar2);
		rateName2 = (TextView) rateRegion.findViewById(R.id.invite_detail_rate_name2);
		rateHeadImage2 = (ImageView) rateRegion.findViewById(R.id.user_headImage2);
		
		requestRegion = (LinearLayout) findViewById(R.id.invite_detail_request_region);
		requestGv = (NestGridView) requestRegion.findViewById(R.id.invite_detail_request_gridview);
		requestTitleTv = (TextView) requestRegion.findViewById(R.id.invite_detail_request_title);
	}
	
	private void getLeftInfoViews() {
		leftInfoVh = new InfoViewHolder();
		leftInfoVh.avatar = (ImageView) findViewById(R.id.invite_detail_avatar_left);
		leftInfoVh.nickname = (TextView) findViewById(R.id.invite_detail_nickname_left);
		leftInfoVh.sex = (ImageView) findViewById(R.id.invite_detail_sex_left);
	}

	private void getRightInfoViews() {
		rightInfoVh = new InfoViewHolder();
		rightInfoVh.avatar = (ImageView) findViewById(R.id.invite_detail_avatar_right);
		rightInfoVh.nickname = (TextView) findViewById(R.id.invite_detail_nickname_right);
		rightInfoVh.sex = (ImageView) findViewById(R.id.invite_detail_sex_right);
		
//		right0InfoVh = new InfoViewHolder();
//		right0InfoVh.avatar = (RoundedImageView) findViewById(R.id.invite_detail_avatar_right0);
//		right0InfoVh.nickname = (TextView) findViewById(R.id.invite_detail_nickname_right0);
//		right0InfoVh.sex = (ImageView) findViewById(R.id.invite_detail_sex_right0);
		
		right1InfoVh = new InfoViewHolder();
		right1InfoVh.avatar = (ImageView) findViewById(R.id.invite_detail_avatar_right1);
		right1InfoVh.nickname = (TextView) findViewById(R.id.invite_detail_nickname_right1);
		right1InfoVh.sex = (ImageView) findViewById(R.id.invite_detail_sex_right1);
		
		right2InfoVh = new InfoViewHolder();
		right2InfoVh.avatar = (ImageView) findViewById(R.id.invite_detail_avatar_right2);
		right2InfoVh.nickname = (TextView) findViewById(R.id.invite_detail_nickname_right2);
		right2InfoVh.sex = (ImageView) findViewById(R.id.invite_detail_sex_right2);
	}
	
	private void getLeftPerInfoViews() {
		leftPerInfoVh = new PerInfoViewHolder();
		leftPerInfoVh.nickname = (TextView) findViewById(R.id.invite_detail_pi_nickname_left);
		leftPerInfoVh.hi = (TextView) findViewById(R.id.invite_detail_pi_hi_left);
		leftPerInfoVh.matches = (TextView) findViewById(R.id.invite_detail_pi_matches_left);
		leftPerInfoVh.score = (TextView) findViewById(R.id.invite_detail_pi_score_left);
		leftPerInfoVh.rate = (LinearLayout) findViewById(R.id.invite_detail_pi_rate_left);
	}

	private void getRightPerInfoViews() {
		rightPerInfoVh = new PerInfoViewHolder();
		rightPerInfoVh.nickname = (TextView) findViewById(R.id.invite_detail_pi_nickname_right);
		rightPerInfoVh.hi = (TextView) findViewById(R.id.invite_detail_pi_hi_right);
		rightPerInfoVh.matches = (TextView) findViewById(R.id.invite_detail_pi_matches_right);
		rightPerInfoVh.score = (TextView) findViewById(R.id.invite_detail_pi_score_right);
		rightPerInfoVh.rate = (LinearLayout) findViewById(R.id.invite_detail_pi_rate_right);
		
		right1PerInfoVh = new PerInfoViewHolder();
		right1PerInfoVh.nickname = (TextView) findViewById(R.id.invite_detail_pi_nickname_right1);
		right1PerInfoVh.hi = (TextView) findViewById(R.id.invite_detail_pi_hi_right1);
		right1PerInfoVh.matches = (TextView) findViewById(R.id.invite_detail_pi_matches_right1);
		right1PerInfoVh.score = (TextView) findViewById(R.id.invite_detail_pi_score_right1);
		right1PerInfoVh.rate = (LinearLayout) findViewById(R.id.invite_detail_pi_rate_right1);
		
		right2PerInfoVh = new PerInfoViewHolder();
		right2PerInfoVh.nickname = (TextView) findViewById(R.id.invite_detail_pi_nickname_right2);
		right2PerInfoVh.hi = (TextView) findViewById(R.id.invite_detail_pi_hi_right2);
		right2PerInfoVh.matches = (TextView) findViewById(R.id.invite_detail_pi_matches_right2);
		right2PerInfoVh.score = (TextView) findViewById(R.id.invite_detail_pi_score_right2);
		right2PerInfoVh.rate = (LinearLayout) findViewById(R.id.invite_detail_pi_rate_right2);
	}
	
	protected void loadAvatar(final String sn, String name, final ImageView iv) {
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(this, sn, name,
				(int) getResources().getDimension(R.dimen.avatar_detail_size));
		if(null != avatar) {
			iv.setImageDrawable(avatar);
		} else {
			iv.setImageResource(R.drawable.avatar_no_golfer);
			AsyncImageLoader.getInstance().loadAvatar(this, sn, name,
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if(null != imageDrawable && null != iv) {
								iv.setImageDrawable(imageDrawable);
							}
						}
				});
		}
		// 头像存在，且不是自己的头像
		if(null != name && !name.isEmpty() && !sn.equals(MainApp.getInstance().getCustomer().sn)) {
			iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MemDetailActivityNew.startMemDetailActivity(InviteDetailActivity.this, sn);
					overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				}
			});
		} else {
			iv.setOnClickListener(null);
		}
	}
	
	public void appendCommonData(InviteRoleInfo left, InviteRoleInfo right,InviteRoleInfo right1,InviteRoleInfo right2,
			String inviteMsg, int payType, int stake) {
		
		Utils.setVisible(svLayout, operBarLl);
		// base information
		setLeftInfoViews(left);
		setLeftPerInfoViews(left);
		
//		if (right1 != null ) {
//			
//			setRight0InfoViews(right);
//			setRightPerInfoViews(right);
//			
//		} else {
//			
//			findViewById(R.id.invite_detail_invitee_realtive2).setVisibility(View.GONE);
//			findViewById(R.id.invite_detail_invitee_realtive).setVisibility(View.VISIBLE);
//			findViewById(R.id.invite_detail_vs).setVisibility(View.VISIBLE);
//			setRightInfoViews(right);
//			setRightPerInfoViews(right);
//		}
		
			
		setRightInfoViews(right);
		setRightPerInfoViews(right);
			
		setRight1InfoViews(right1);
		setRight1PerInfoViews(right1);
			
		
		setRight2InfoViews(right2);
		setRight2PerInfoViews(right2);

		
		// invite message
		setInviteMessage(inviteMsg);
		// pay type
		displayPayType(payType);
		// stake
		displayStake(stake);
	}
	
	protected void setLeftInfoViews(InviteRoleInfo role) {
		setInfoViews(role, leftInfoVh);
	}
	
	protected void setRightInfoViews(InviteRoleInfo role) {
		setInfoViews(role, rightInfoVh);
	}
	
//	protected void setRight0InfoViews(InviteRoleInfo role) {
//		setInfoViews(role, right0InfoVh);
//	}
	
	protected void setRight1InfoViews(InviteRoleInfo role) {
		setInfoViews(role, right1InfoVh);
	}
	
	protected void setRight2InfoViews(InviteRoleInfo role) {
		setInfoViews(role, right2InfoVh);
	}
	
	private void setInfoViews(InviteRoleInfo role, InfoViewHolder holder) {
		if(null == role)  {
			
			Utils.setGone(holder.sex);
			holder.nickname.setText(R.string.str_no_value);
			holder.avatar.setImageResource(R.drawable.avatar_no_golfer);
			return ;
		}
		
		loadAvatar(role.sn, role.avatar, holder.avatar);
		holder.nickname.setText(role.nickname);

		if(Const.SEX_MALE == role.sex) {
			holder.sex.setImageResource(R.drawable.ic_male);
		} else {
			holder.sex.setImageResource(R.drawable.ic_female);
		}
	}
	
	protected void setLeftPerInfoViews(InviteRoleInfo role) {
		setPerInfoViews(role, leftPerInfoVh);
	}
	
	protected void setRightPerInfoViews(InviteRoleInfo role) {
		setPerInfoViews(role, rightPerInfoVh);
	}
	
	protected void setRight1PerInfoViews(InviteRoleInfo role) {
		setPerInfoViews(role, right1PerInfoVh);
	}
	
	protected void setRight2PerInfoViews(InviteRoleInfo role) {
		setPerInfoViews(role, right2PerInfoVh);
	}
	
	private void setPerInfoViews(InviteRoleInfo role, PerInfoViewHolder holder) {
		if(null == role) {
			
			holder.hi.setText(R.string.str_no_value);
			holder.matches.setText(R.string.str_no_value);
			holder.nickname.setText(R.string.str_no_value);
			holder.score.setText(R.string.str_no_value);
			Utils.setLevel(this, holder.rate, (int) getResources().getDimension(R.dimen.invite_detail_rate_star_size), 0);
			return ;
		}
		Utils.logh(TAG, "setPerInfoViews role.nickname: " + role.nickname + " role.score: " + role.score + " role.rate: " + role.rate);
		holder.nickname.setText(role.nickname);
		holder.hi.setText(Utils.getDoubleString(this, role.handicapIndex));
		holder.matches.setText(Utils.getIntString(this, role.matches));
		holder.score.setText(Utils.getIntString(this, role.score));
		
//		if(role.rate > 0) {
			Utils.setLevel(this, holder.rate, (int) getResources().getDimension(R.dimen.invite_detail_rate_star_size), role.rate);
//		}
	}
	
	protected void refreshPerInfoViews(int leftRate, int leftScore, int rightRate, int rightScore) {
		if(leftRate > 0) {
			Utils.setLevel(this, leftPerInfoVh.rate, (int) getResources().getDimension(R.dimen.invite_detail_rate_star_size), leftRate);
		}
		if(leftScore > 0) {
			leftPerInfoVh.score.setText(Utils.getIntString(this, leftScore));
		}
		if(rightRate > 0) {
			Utils.setLevel(this, rightPerInfoVh.rate, (int) getResources().getDimension(R.dimen.invite_detail_rate_star_size), rightRate);
		}
		if(rightScore > 0) {
			rightPerInfoVh.score.setText(Utils.getIntString(this, rightScore));
		}
	}
	
	/**
	 * 接受某人后，更新页面显示信息
	 * @param ii
	 */
	protected void refreshAcceptRole(InviteRoleInfo ii) {
		Utils.logh(TAG, "refreshAcceptRole: " + ii.log());
		// 成绩信息
		rightPerInfoVh.nickname.setText(ii.nickname);
		Utils.logh(TAG, "---- nickname");
		rightPerInfoVh.hi.setText(Utils.getDoubleString(this, ii.handicapIndex));
		Utils.logh(TAG, "---- handicapIndex");
		rightPerInfoVh.matches.setText(Utils.getIntString(this, ii.matches));
		Utils.logh(TAG, "---- matches");
		rightPerInfoVh.score.setText(Utils.getIntString(this, ii.score));
//		if(role.rate > 0) {
			Utils.setLevel(this, rightPerInfoVh.rate, (int) getResources().getDimension(R.dimen.invite_detail_rate_star_size), ii.rate);
//		}
			Utils.logh(TAG, "---- ");
		// 头像部分信息
		rightInfoVh.nickname.setText(ii.nickname);
		Utils.setVisible(rightInfoVh.sex);
		if(Const.SEX_MALE == ii.sex) {
			rightInfoVh.sex.setImageResource(R.drawable.ic_male);
		} else {
			rightInfoVh.sex.setImageResource(R.drawable.ic_female);
		}
		Utils.logh(TAG, "---- before refresh avatar");
		loadAvatar(ii.sn, ii.avatar, rightInfoVh.avatar);
	}
	
	protected void setInviteMessage(String msg) {
		inviteMsgTv.setText(msg);
	}
	
	protected void displayAppInfo(String teeTime, String courseStr) {
		Utils.setVisibleGone(appInfoLl, plansLl);
		teeTimeTv.setText(teeTime);
		courseTv.setText(courseStr);
	}
	
	protected ListView displayPlansShowListView(ArrayList<PlanShowInfo> plans) {

		/*pxs 2015.12.30 update*/
		//Utils.setVisibleGone(plansLl, appInfoLl);
		Utils.setVisibleGone(plansLl);
		plansLv.setAdapter(new PlanShowAdapter(this, plans));
		return plansLv;
	}
	
	protected PlanSelectAdapter displayPlansSelectListView(ArrayList<PlanShowInfo> plans) {

		/*pxs 2015.12.30 update*/
		//Utils.setVisibleGone(plansLl, appInfoLl);
		Utils.setVisibleGone(plansLl);
		PlanSelectAdapter adapter = new PlanSelectAdapter(this, plans);
		plansLv.setAdapter(adapter);
		return adapter;
	}
	
	protected void displayPayType(int type) {
		//PayTypeShowAdapter adapter = new PayTypeShowAdapter(this, type);
		
		payTypeTxt.setText(MainApp.getInstance().getGlobalData().getPayTypeName(type));
		//payTypeGv.setAdapter(adapter);
	}
	
	protected void displayStake(int type) {
		//StakeShowAdapter adapter = new StakeShowAdapter(this, type);
		//stakeGv.setAdapter(adapter);
		
		//stakeTxt.setText(MainApp.getInstance().getGlobalData().getStakeName(type));
	}
	
	/**
	 * 设置杆数(已上传)
	 * @param score 杆数
	 */
	protected void setScoreNumber(int score) {
		setScoreNumber(score, true);
	}
	
	/**
	 * 设置杆数
	 * @param score 杆数
	 * @param scored 已上传，还是默认填充
	 */
	protected void setScoreNumber(int score, boolean scored) {
		String str;
		if(score > 0) {
			str = String.valueOf(score);
		} else {
			str = getString(R.string.str_no_value);
		}
		displayScoreRegion();
		scoresEt.setText(str);
		// 点击登记才记录，无需判断
		Utils.setDisable(scoresEt);
//		if(scored) {
//			Utils.setDisable(scoresEt, scoreCardIv);
//		} else {
//			Utils.setEnable(scoresEt, scoreCardIv);
//		}
	}
	
	protected void setSoreDbData(int dbScore) {
		displayScoreRegion();
		scoresEt.setText(String.valueOf(dbScore));
		Utils.setEnable(scoresEt);
	}
	
	protected void displayScoreRegion() {
		Utils.setVisibleGone(scoreRegion, scoreCardInvalidIv);

	}
	
	protected void disableScoreItems() {
		Utils.setDisable(scoreUploadBtn, scoreCardIv, scoresEt);
	}
	
	protected void displayScorecardInvalid() {
		Utils.setVisible(scoreCardInvalidIv);
	}
	
	protected ImageView getScoreCard() {
		return scoreCardIv;
	}
	
	protected void disableRateBarItems() {
		rateBarRb.setIsIndicator(true);
		rateBarRb1.setIsIndicator(true);
		rateBarRb2.setIsIndicator(true);
	}
	
//	protected View displayScoreRegion(int score, String cardName, int dbScore) {
//		Utils.setVisible(scoreRegion);
//		if(Integer.MAX_VALUE == score) { // 系统自动完成
//			hasScored(0);
//		} else if(score > 0) { // 已成功记分
//			hasScored(score);
//			// 只有在记分成功的情况下，才允许上传记分卡
//			if(cardName.isEmpty()) {
//				scoreCardIv.setImageResource(R.drawable.upload_scorecard);
//			} else {
//				
//			}
//		} else { // 未成功记分，需要首先查看记录，有的话自动填充
//			if(dbScore > 0) {
//				scoresEt.setText(String.valueOf(dbScore));
//				Utils.setEnable(scoresEt);
//			}
//		}
//		return scoreRegion;
//	}
	
//	protected void hasScored(int score) {
//		scoresEt.setText(String.valueOf(score));
//		Utils.setDisable(scoresEt);
//	}
	
	protected String getScore() {
		String score = Utils.getEditTextString(scoresEt);
		if(null == score) {
			Toast.makeText(this, R.string.str_invite_detail_score_null, Toast.LENGTH_SHORT).show();
			scoresEt.requestFocus();
		}
		return score;
	}
	
	protected void dismissScoreRegion() {
		Utils.setGone(scoreRegion);
	}
	
	protected float getCurrentRating() {
		return rateBarRb.getRating();
	}
	
	protected int[] getCurrentRatingAll() {
		
		int[] rateRBs = {0,0,0};
		
		rateRBs[0] = (int)rateBarRb.getRating();
		
		rateRBs[1] = (int)rateBarRb1.getRating();
		
		rateRBs[2] = (int)rateBarRb2.getRating();
		
		
		return rateRBs;
	}
	/**
	 * 显示评分区域
	 * 			0: 未评分
	 * 			>0: 已经评分
	 * @return 
	 * @return
	 */
	//protected RatingBar displayRateRegion(int rate) {
//	protected RatingBar displayRateRegion(int rate) {
//		if(rate > 0) {
//			rateDoBtn.setText(R.string.str_invite_detail_rate_btn_done);
//			Utils.setDisable(rateDoBtn);
//			rateBarRb.setRating((float)rate);
//			rateBarRb.setIsIndicator(true);
//		} else if(rate == 0) {
//			rateDoBtn.setText(R.string.str_invite_detail_rate_btn_do);
//			Utils.setEnable(rateDoBtn);
//			rateBarRb.setIsIndicator(false);
//			rateBarRb.setRating((float) 0.0);
//			rateBarRb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
//				@Override
//				public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//					Utils.logh(TAG, "onRatingChanged " + rating);
////					if(rating == 0) { // 不可评零分
////						rateBarRb.setRating(1);
////					}
//				}
//			});
//		} else {
//			// 不应该存在此种情况
//			Log.w(TAG, "displayRateRegion invalide parameter rate < 0 ");
//			dismissRateRegion();
//			return null;
//		}
//		Utils.setVisible(rateRegion);
//		return rateBarRb;
//	}
	
	protected RatingBar displayRateRegion(MyInviteDetail detail) {
		
		this.detail = detail;
		
		if (Const.INVITE_TYPE_OPEN == detail.type) {
			
			/*当前登陆者为约球发起人*/
			if (detail.inviter.sn.equalsIgnoreCase(customer.sn)) {
				
				/*已经评价*/
				if(detail.ratesIdHash != null && detail.ratesIdHash.size() > 0) {
					
					if (detail.ratesIdHash.size() == 1) {
						
						
						rateBarRb.setRating((float)detail.ratesIdHash.get(detail.invitee.id));
						rateBarRb.setIsIndicator(true);
						rateName.setText(detail.invitee.nickname);
						loadAvatar(detail.invitee.sn, detail.invitee.avatar, rateHeadImage);
						
					} else if (detail.ratesIdHash.size() == 2) {
						
						rateBarRb.setRating((float)detail.ratesIdHash.get(detail.invitee.id));
						rateBarRb1.setRating((float)detail.ratesIdHash.get(detail.inviteeone.id));

						rateBarRb.setIsIndicator(true);
						rateBarRb1.setIsIndicator(true);
						findViewById(R.id.invite_detail_rate_relative1).setVisibility(View.VISIBLE);
						
						rateName.setText(detail.invitee.nickname);
						loadAvatar(detail.invitee.sn, detail.invitee.avatar, rateHeadImage);
						
						rateName1.setText(detail.inviteeone.nickname);
						loadAvatar(detail.inviteeone.sn, detail.inviteeone.avatar, rateHeadImage1);
						
					} else {
						
						rateBarRb.setRating((float)detail.ratesIdHash.get(detail.invitee.id));
						rateBarRb1.setRating((float)detail.ratesIdHash.get(detail.inviteeone.id));
						rateBarRb2.setRating((float)detail.ratesIdHash.get(detail.inviteetwo.id));

						rateBarRb.setIsIndicator(true);
						rateBarRb1.setIsIndicator(true);
						rateBarRb2.setIsIndicator(true);
						findViewById(R.id.invite_detail_rate_relative1).setVisibility(View.VISIBLE);
						findViewById(R.id.invite_detail_rate_relative2).setVisibility(View.VISIBLE);
						
						rateName.setText(detail.invitee.nickname);
						loadAvatar(detail.invitee.sn, detail.invitee.avatar, rateHeadImage);
						
						rateName1.setText(detail.inviteeone.nickname);
						loadAvatar(detail.inviteeone.sn, detail.inviteeone.avatar, rateHeadImage1);
						
						rateName2.setText(detail.inviteetwo.nickname);
						loadAvatar(detail.inviteetwo.sn, detail.inviteetwo.avatar, rateHeadImage2);
					}
					
					Utils.setDisable(rateDoBtn);
					
				/*没有评价，只能根据 invite 信心判断*/
				} else {
					
					//rateName.setText(detail.invitee.nickname);
					//loadAvatar(detail.invitee.sn, detail.invitee.avatar, rateHeadImage);
					if (detail.invitee!= null) {
						
						rateName.setText(detail.invitee.nickname);
						loadAvatar(detail.invitee.sn, detail.invitee.avatar, rateHeadImage);
						//findViewById(R.id.invite_detail_rate_relative).setVisibility(View.VISIBLE);
						
					}
					
					if (detail.inviteeone!= null) {
						
						rateName1.setText(detail.inviteeone.nickname);
						loadAvatar(detail.inviteeone.sn, detail.inviteeone.avatar, rateHeadImage1);
						findViewById(R.id.invite_detail_rate_relative1).setVisibility(View.VISIBLE);
						
					}
					
					if (detail.inviteetwo!= null) {
						
						rateName2.setText(detail.inviteetwo.nickname);
						loadAvatar(detail.inviteetwo.sn, detail.inviteetwo.avatar, rateHeadImage2);
						findViewById(R.id.invite_detail_rate_relative2).setVisibility(View.VISIBLE);
					}
					
				}
				
			/*当前登陆者不是约球发起人*/	
			} else {
				
				findViewById(R.id.invite_detail_rate_relative1).setVisibility(View.GONE);
				findViewById(R.id.invite_detail_rate_relative2).setVisibility(View.GONE);
				
				/*已经评价*/
				if (detail.ratesIdHash != null && detail.ratesIdHash.size() > 0) {
					
					rateBarRb.setRating((float)detail.ratesIdHash.get(detail.inviter.id));
					rateBarRb.setIsIndicator(true);
					rateName.setText(detail.inviter.nickname);
					
					Utils.setDisable(rateDoBtn);
					
				/*未评价*/
				} else {
					
					//rateBarRb.setRating((float)detail.ratesIdHash.get(customer.sn));
					rateName.setText(detail.inviter.nickname);
					
				}
				
				loadAvatar(detail.inviter.sn, detail.inviter.avatar, rateHeadImage);
				
			}
			
			
		/*一对一约球*/
		} else if (Const.INVITE_TYPE_STS == detail.type) {
			
			findViewById(R.id.invite_detail_rate_relative1).setVisibility(View.GONE);
			findViewById(R.id.invite_detail_rate_relative2).setVisibility(View.GONE);
			
			/**/
			if (detail.inviter.sn.equalsIgnoreCase(customer.sn)) {
				
				rateName.setText(detail.invitee.nickname);
				loadAvatar(detail.invitee.sn, detail.invitee.avatar, rateHeadImage);
				
			} else {
				
				rateName.setText(detail.inviter.nickname);
				loadAvatar(detail.inviter.sn, detail.inviter.avatar, rateHeadImage);
			}
			
			if(detail.rateStar > 0) {
				rateDoBtn.setText(R.string.str_invite_detail_rate_btn_done);
				Utils.setDisable(rateDoBtn);
				rateBarRb.setRating((float)detail.rateStar);
				rateBarRb.setIsIndicator(true);
				
			} else if(detail.rateStar == 0) {
				
				rateDoBtn.setText(R.string.str_invite_detail_rate_btn_do);
				Utils.setEnable(rateDoBtn);
				rateBarRb.setIsIndicator(false);
				//rateBarRb.setRating((float) 0.0);
				rateBarRb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
					@Override
					public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
						Utils.logh(TAG, "onRatingChanged " + rating);
//						if(rating == 0) { // 不可评零分
//							rateBarRb.setRating(1);
//						}
					}
				});
			} else {
				// 不应该存在此种情况
				Log.w(TAG, "displayRateRegion invalide parameter rate < 0 ");
				dismissRateRegion();
				return null;
			}
		}
		
		Utils.setVisible(rateRegion);
		return rateBarRb;
	}
	
	protected void dismissRateRegion() {
		Utils.setGone(rateRegion);
	}
	
	protected View displayRequestRegionOther(int num) {

//		requestTitleTv.setText(msg);
		//2015,12.24 next two line
		//Utils.setVisibleGone(requestRegion, requestGv);
		//refreshRequestTitle(num);
		return requestRegion;
	}
	
	protected void refreshRequestTitle(int num) {
		if(num == 0) {
			requestTitleTv.setText(R.string.str_invite_detail_request_no_one);
		} else {
			requestTitleTv.setText(String.format(getString(R.string.str_invite_detail_request_num_txt), num));
		}
	}
	
	
	/**
	 *
	 * @param selectable 是否可选
	 * @param inviteeSn 被选申请者会员编号
	 * @return
	 */
	protected ApplicantsAdapter displayRequestRegionMine(MyInviteDetail detail, boolean selectable, String inviteeSn) {
		ApplicantsAdapter adapter = null;
		
		this.detail = detail;
//		int size = 0;
//		if(detail == null || null == detail.applicants || detail.applicants.isEmpty()) {
//			Utils.setVisibleGone(requestRegion, requestGv);
//		} else {
//			Utils.setVisible(requestRegion, requestGv);
//			size = detail.applicants.size();
//			adapter = new ApplicantsAdapter(this, detail, selectable, inviteeSn);
//			requestGv.setAdapter(adapter);
//		}
//		refreshRequestTitle(size);
		if (detail.select_menber_sn.size() > 0) {

			invitee_one_relative.setClickable(true);
			invitee_one_relative.setSelected(true);
		}

		if (detail.select_menber_sn.size() > 1) {

			invitee_one_relative.setClickable(true);
			invitee_two_relative.setClickable(true);

			invitee_one_relative.setSelected(true);
			invitee_two_relative.setSelected(true);
		}

		return adapter;
	}

	protected void unClickableleRequestRegionMine() {

		invitee_one_relative.setClickable(false);
		invitee_two_relative.setClickable(false);
		invitee_three_relative.setClickable(false);

	}
	
	protected void dismissRequestRegion() {

		/*
		* pxs update 2015.12.29
		* */
		Utils.setGone(mInviteAllInfoLinear);
		Utils.setVisible(mInviteShowLinear);


		//Utils.setGone(requestRegion);

	}

	/**
	 * 添加、替换底部操作按钮。
	 */
	protected void addOperBarLayout(ViewGroup vg) {
		operBarLl.removeAllViews();
		operBarLl.addView(vg);
		// 默认子控件全部显示
		for(int i=0, size=vg.getChildCount(); i<size; i++) {
			Utils.setVisible(vg.getChildAt(i));
		}
	}
	
	protected void dismissOperBar() {
		Utils.setGone(operBarLl);
	}
	
	protected void setFinishResult(int result) {
		finishResult = result;
	}
	
	protected void setFinishIntent(Intent intent) {
		if(null != finishIntent) {
			finishIntent = null;
		}
		finishIntent = intent;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.invite_detail_topbar_back:
				finishWithAnim();
				break;

			case R.id.invite_detail_invitee_realtive:

				boolean result = checkInviteeSelect(detail.invitee.sn);


				if(result){

					invitee_one_relative.setSelected(true);
				}
				else {

					invitee_one_relative.setSelected(false);
				}

				break;

			case R.id.invite_detail_invitee_realtive1:

				boolean result1 = checkInviteeSelect(detail.inviteeone.sn);


				if(result1){

					invitee_two_relative.setSelected(true);
				}
				else {

					invitee_two_relative.setSelected(false);
				}

				break;

			case R.id.invite_detail_invintee_info_all_show_linear:

				if(mInviteAllInfoLinear.getVisibility() == View.GONE) {

					mInviteAllInfoLinear.setVisibility(View.VISIBLE);
					mInviteaShowTxt.setText(R.string.str_invite_detail_show_invitee_hide);
					mInviteShowImage.setImageResource(R.drawable.arrow_up_green);

//					Animation anim = new TranslateAnimation(0, 0, 0, mInviteAllInfoLinear.getHeight());
//					anim.setDuration(200);
//					mInviteAllInfoLinear.startAnimation(anim);
				}
				else {

					mInviteAllInfoLinear.setVisibility(View.GONE);
					mInviteaShowTxt.setText(R.string.str_invite_detail_show_invitee);
					mInviteShowImage.setImageResource(R.drawable.icon_arrow_down_green);

//					Animation anim = new TranslateAnimation(0, 0, mInviteAllInfoLinear.getHeight(), 0);
//					anim.setDuration(200);
//					mInviteAllInfoLinear.startAnimation(anim);
				}

				break;
		}
	}

	private boolean checkInviteeSelect (String sn) {

		boolean result = false;
		if (detail.select_menber_sn !=null) {

			boolean contains = detail.select_menber_sn.contains(sn);

			if (contains) {

				if (detail.select_menber_sn.size() <= 1) {

					Toast.makeText(this, "最少选择一个", Toast.LENGTH_SHORT).show();
					result = true;

				} else {

					//selectedApplicantsList.remove(applicatants.get(sn));

					for(int i=0, size=detail.select_menber_sn.size(); i<size; i++) {


						String snSelect = detail.select_menber_sn.get(i);

						if (sn.equalsIgnoreCase(snSelect)) {

							detail.select_menber_sn.remove(i);
							result = false;
							break ;
						}
					}

				}
			}
			else {

				detail.select_menber_sn.add(sn);
				result = true;
			}
		}

		return result;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			finishWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public static final String BUNDLE_KEY_STATUS_CHANGED = "detail_status_changed";
	/**
	 * 返回列表更新，状态发送变化：1、时间延时导致状态变化；2、申请操作导致状态变化(信息转移到我的约球)
	 * @param changed
	 */
	protected void finishWithResult(boolean changed) {
		Intent intent = new Intent();
		intent.putExtra(BUNDLE_KEY_STATUS_CHANGED, changed);
//		finishWithAnim(RESULT_OK, intent);
		setFinishResult(RESULT_OK);
		setFinishIntent(intent);
		finishWithAnim();
	}
	
	protected void setFinishResult(boolean changed) {
		if(changed) {
			setFinishResult(RESULT_OK);
			Intent intent = new Intent();
			intent.putExtra(BUNDLE_KEY_STATUS_CHANGED, changed);
			setFinishIntent(intent);
		} else {
			setFinishResult(RESULT_CANCELED);
			setFinishIntent(null);
		}

	}
	
//	protected void finishWithAnim(int result, Intent intent) {
//		setFinishResult(result);
//		setFinishIntent(intent);
//		finishWithAnim();
//	}
	
	private void finishWithAnim() {
		if(!getResultByCallback()) {
			setResult(finishResult, finishIntent);
		} else {
			boolean changed = false;
			if(null != finishIntent) {
				changed = finishIntent.getBooleanExtra(BUNDLE_KEY_STATUS_CHANGED, false);
			}
			resultCallback.callback(changed, alertChanged);
		}
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}
	
	// 我的约球，新通知状态
	private boolean alertChanged = false;
	protected void setAlertChanged() {
		alertChanged = true;
	}
	
	private onResultCallback resultCallback;
	public interface onResultCallback {
		/**
		 * 
		 * @param status 状态是否有变化
		 * @param alert 标记是否有变化
		 */
		void callback(boolean status, boolean alert);
	}
	
	public void setOnResultCallback(onResultCallback callback) {
		resultCallback = callback;
	}
	
	/**
	 * 获取我的约球详情信息
	 * @param appSn 约球单号
	 * @param sn 用户会员编号
	 * @param callback
	 * @since 我的约球详情，在四种子类中均使用，提出公共方法
	 */
	protected void getMyInviteDetail(final String appSn, final String sn, final getMyInviteDetailCallback callback) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		new AsyncTask<Object, Object, Integer>() {
			GetMyInviteDetail request = new GetMyInviteDetail(InviteDetailActivity.this, appSn, sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				callback.callBack(result, request.getFailMsg(), request.getMyInviteDetail());
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	protected interface getMyInviteDetailCallback {
		void callBack(int retId, String msg, MyInviteDetail detail);
	}
	
	/**
	 * 申请开放式约球。
	 * @param appSn 约球单号
	 * @param sn 用户会员编号
	 * @param callback
	 * @since 操作后停留在详情页面，理论上大厅中和我的约球中，支持与cancelOpenInvite交替操作。
	 */
	protected void applyOpenInvite(final String appSn, final String sn, final applyOpenInviteCallback callback) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			ApplyOpenInvite request = new ApplyOpenInvite(InviteDetailActivity.this, appSn, sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				callback.callBack(result, request.getApplyNum(), request.getFailMsg());
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	protected interface applyOpenInviteCallback {
		 void callBack(int retId, int applyNum, String msg);
	}
	
	/**
	 * 取消对开放式约球申请。
	 * @param appSn 约球单号
	 * @param sn 用户会员编号
	 * @param callback
	 * @since 操作后停留在详情页面，理论上大厅中和我的约球中，支持与applyOpenInvite交替操作。
	 */
	protected void cancelOpenInvite(final String appSn, final String sn, final cancelOpenInviteCallback callback) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			CancelOpenInvite request = new CancelOpenInvite(InviteDetailActivity.this, appSn, sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				callback.callBack(result, request.getApplyNum(), request.getFailMsg());
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	protected interface cancelOpenInviteCallback {
		void callBack(int retId, int applyNum, String msg);
	}
	
	
	/**
	 * 撤销约球
	 * 	只有在自己发起的约球中，才能在他人未接受或未接受他人前，进行撤销。
	 * @param appSn 约球单号
	 * @param sn 用户会员编号
	 * @param callback
	 */
	protected void revokeInviteApp(final String appSn, final String sn, final revokeInviteAppCallback callback) {
		new AlertDialog.Builder(this)
			.setTitle(R.string.str_invite_detail_dialog_revoke_app_title)
			.setMessage(R.string.str_invite_detail_dialog_revoke_app_msg)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doRevokeInviteApp(appSn, sn, callback);
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { }
			})
			.show();
	}
	
	private void doRevokeInviteApp(final String appSn, final String sn, final revokeInviteAppCallback callback) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			RevokeInviteApp request = new RevokeInviteApp(InviteDetailActivity.this, appSn, sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				callback.callBack(result, request.getFailMsg());
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

	protected interface revokeInviteAppCallback {
		void callBack(int retId, String msg);
	}

	private class InfoViewHolder {
		protected ImageView avatar;
		protected TextView nickname;
		protected ImageView sex;
	}
	
	private class PerInfoViewHolder {
		protected TextView nickname;
		protected TextView hi;
		protected TextView matches;
		protected TextView score;
		protected LinearLayout rate;
	}
		
	protected void toastShort(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	protected void toastShort(int msgId) {
		Toast.makeText(this, getString(msgId), Toast.LENGTH_SHORT).show();
	}
	
	
	private class myAMapLocationListener implements AMapLocationListener {

		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			// TODO Auto-generated method stub
			if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
		           //获取位置信息
				
				curLocation = amapLocation;
				
				mLocationLongStr = String.valueOf(amapLocation.getLongitude());
				mLocationLatStr = String.valueOf(amapLocation.getLatitude());
				
				DebugTools.getDebug().debug_v(TAG, "定位省："+mLocationLongStr);
		      }
		}
		
	}
}
