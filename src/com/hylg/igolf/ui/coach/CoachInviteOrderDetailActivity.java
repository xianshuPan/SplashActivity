package com.hylg.igolf.ui.coach;


import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachInviteOrderDetail;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.CoachAcceptInvite;
import com.hylg.igolf.cs.request.CoachEndTeaching;
import com.hylg.igolf.cs.request.CoachPayCharge;
import com.hylg.igolf.cs.request.CoachRefuseContent;
import com.hylg.igolf.cs.request.CoachStudentComment;
import com.hylg.igolf.cs.request.FriendAttentionAdd;
import com.hylg.igolf.cs.request.StudentRevoketInvite;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.view.CircleImageView;
import com.hylg.igolf.ui.view.DonutProgress;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;
import com.pingplusplus.android.PaymentActivity;
import com.pingplusplus.android.PingppLog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class CoachInviteOrderDetailActivity extends FragmentActivity implements 
															OnClickListener{
	
	private final String 					TAG = "CoachInviteOrderDetailActivity";
	
	private final static String 			BUNDLE_REQ_DATA = "data";
	
	private final int                       COUNTING_MESSAGE = 1001,
											REQUEST_CODE_PAYMENT =1003,
											END_TEACHING = 1002;
	
	private ImageButton                     mBack = null;
	
	private CircleImageView                 mAvatarImage = null;
	
	private TextView                        mComplainTxt,mNickNameTxt = null,mPhoneTxt,mQuestionTxt;

	private ImageView                       mCustomerHomeImg,mAttentionImg;
	
	/*
	 * 约球的时间和地点，球场
	 * 
	 * */
	private TextView                        mTeachingDateTxt,mTeachingTimeTxt,mTeachingHourTxt,mRegionTxt,mTeachingCourseTxt;
	private ImageView                       mStatusImage;
	
	
	/*
	 * 教练看到的，教学和收费两个tab 选项
	 * 
	 * */
	private LinearLayout                    mTabLinear,mTabCountingTimeLinear,mTabFeeDetailLinear;
	
	/*
	 * 教学操作按钮 开始、暂停、结束
	 * 
	 * */
	private RelativeLayout                  mTeachingOperateRelative;
	private DonutProgress                   mTeachingProgress;
	private LinearLayout                    mStartLinear,mPauseLinear,mStopLinear;
	
	/*
	 * 费用明细
	 * */
	private LinearLayout                    mFeeDetailLinear;
	private RatingBar                       mCommentRating;
	private EditText                        mCommentEdit;
	private TextView                        mStartTimeTxt,mPauseTimeTxt,mEndTimeTxt,mTaughtTimeTxt,mPriceUnitTxt,mTotalFeeTxt;
	
	/*
	 * 接受、结束、支付
	 * */

	private TextView                        mAcceptInviteTxt,mRefuseInviteTxt,mRevokeTxt,mPayTxt;
	private RelativeLayout                  mRevokeRelative,mAcceptLinear;

	/*
	*
	*
	* */
	private ListView 						mRefuseList = null;
	private TextView                        mRefuseContentTxt = null,mInviteCoachTxt = null,mInviteOtherCoachTxt = null,mWhoRefuseTxt = null;
	private ArrayList<String>               mSelectedReasonArray = null;
	private complainSelectionAdapter 		mRefuseAdapter;
	
	private Customer                        customer;
	
	private CoachInviteOrderDetail          mData;
	
	private long                            mAppId,mFirstStartTime = 0,mStartTime = 0,mPauseTime = 0,
											mPausedTimes = 0,mEndTime = 0;
	
	private SharedPreferences               mShare = null;
	
	
	private int                             mMaxMinutes = 0;
	private int                             mProgress = 0;
	private int 							mAttentionState = -1;
	private String                          attention_sn = "";
	
	
	private CountingTimer                   mCountingThread;
	
	private boolean                         mIsPaused = false;

	private FragmentActivity                mContext = null;
	
	
	public static void startCoachInviteOrderDetailActivity(Context context, CoachInviteOrderDetail data) {
		Intent intent = new Intent();
		intent.setClass(context, CoachInviteOrderDetailActivity.class);
		intent.putExtra(BUNDLE_REQ_DATA, data);
		context.startActivity(intent);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Tools.getInstance().setActivityNoTitle(this);
		DebugTools.getDebug().debug_v(TAG, "onCreate..");
		
		setContentView(R.layout.coach_invite_order_detail_ac);

		PingppLog.DEBUG = true;
		initUI();
		
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public void onRestart() {
		DebugTools.getDebug().debug_v(TAG, "onRestart..");
		super.onRestart();
	}
	
	@Override
	public void onStart() {
		DebugTools.getDebug().debug_v(TAG, "onStart..");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		DebugTools.getDebug().debug_v(TAG, "onResume..");
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		
		//.cancel(true);
		DebugTools.getDebug().debug_v(TAG, "onPause..");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		DebugTools.getDebug().debug_v(TAG, "onDestroy..");

		if (mCountingThread != null) {

			mCountingThread.interrupt();
		}
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		DebugTools.getDebug().debug_v(TAG, "onLowMemory..");
	}

	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

				getSupportFragmentManager().popBackStackImmediate();

				return true;

			} else {

				mContext.finish();
			}
		}

		return super.onKeyDown(keyCode,event);
	}
	
	
	/*初始化控件，及设置相关的事件监听器*/
	private void initUI() {
		mContext = this;
		
		mShare = getSharedPreferences("share", Context.MODE_PRIVATE);
		
		mBack = (ImageButton) findViewById(R.id.coach_invite_order_detail_back);
		mComplainTxt = (TextView) findViewById(R.id.coach_invite_order_detail_complain_text);
		mAvatarImage = (CircleImageView) findViewById(R.id.coach_invite_order_detail_avatar_image);
		mNickNameTxt = (TextView) findViewById(R.id.coach_invite_order_detail_name_text);
		mPhoneTxt = (TextView) findViewById(R.id.coach_invite_order_detail_phone_text);
		mQuestionTxt = (TextView) findViewById(R.id.coach_invite_order_detail_question_text);
		mStatusImage = (ImageView) findViewById(R.id.coach_invite_order_detail_status_image);
		mCustomerHomeImg = (ImageView) findViewById(R.id.coach_invite_order_detail_home_image);
		mAttentionImg = (ImageView) findViewById(R.id.coach_invite_order_detail_attention_image);
		mTeachingDateTxt = (TextView) findViewById(R.id.coach_invite_order_detail_date_text);
		mTeachingTimeTxt = (TextView) findViewById(R.id.coach_invite_order_detail_time_text);
		mTeachingHourTxt = (TextView) findViewById(R.id.coach_invite_order_detail_pre_teach_time_text);
		mRegionTxt = (TextView) findViewById(R.id.coach_invite_order_detail_region_text);
		mTeachingCourseTxt = (TextView) findViewById(R.id.coach_invite_order_detail_place_text);
		mTabLinear = (LinearLayout) findViewById(R.id.coach_invite_order_detail_time_and_fee_linear);
		mTabCountingTimeLinear = (LinearLayout) findViewById(R.id.coach_invite_order_detail_tab_count_time_linear);
		mTabFeeDetailLinear = (LinearLayout) findViewById(R.id.coach_invite_order_detail_tab_fee_detial_linear);
		mTeachingOperateRelative= (RelativeLayout) findViewById(R.id.coach_invite_order_detail_count_time_relative);
		mTeachingProgress = (DonutProgress) findViewById(R.id.coach_invite_order_detail_count_time_progress);
		mStartLinear = (LinearLayout) findViewById(R.id.coach_invite_order_detail_count_time_start_linear);
		mPauseLinear = (LinearLayout) findViewById(R.id.coach_invite_order_detail_count_time_pause_linear);
		mStopLinear = (LinearLayout) findViewById(R.id.coach_invite_order_detail_count_time_stop_linear);
		mFeeDetailLinear = (LinearLayout) findViewById(R.id.coach_invite_order_detail_fee_linear);
		mCommentRating = (RatingBar) findViewById(R.id.coach_invite_order_detail_rating);
		mCommentEdit = (EditText) findViewById(R.id.coach_invite_order_detail_comments_edit);

		mRefuseList = (ListView) findViewById(R.id.coach_invite_order_detail_refuse_content_list);
		mRefuseContentTxt = (TextView) findViewById(R.id.coach_invite_order_detail_refuse_content_text);
		mInviteCoachTxt = (TextView) findViewById(R.id.coach_invite_order_detail_invite_again_text);
		mWhoRefuseTxt  = (TextView) findViewById(R.id.coach_invite_order_detail_who_refuse_text);
		mInviteOtherCoachTxt = (TextView) findViewById(R.id.coach_invite_order_detail_invite_again_list_text);
		mInviteOtherCoachTxt.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

		mStartTimeTxt = (TextView) findViewById(R.id.coach_invite_order_start_text);
		mPauseTimeTxt = (TextView) findViewById(R.id.coach_invite_order_pause_text);
		mEndTimeTxt = (TextView) findViewById(R.id.coach_invite_order_end_text);
		mTaughtTimeTxt = (TextView) findViewById(R.id.coach_invite_order_teach_time_total_text);
		mPriceUnitTxt = (TextView) findViewById(R.id.coach_invite_order_price_unit_text);
		mTotalFeeTxt = (TextView) findViewById(R.id.coach_invite_order_detail_fee_text);
		mAcceptLinear = (RelativeLayout) findViewById(R.id.coach_invite_order_detail_accept_linear);
		mRevokeRelative = (RelativeLayout) findViewById(R.id.coach_invite_order_detail_revoke_relative);
		mAcceptInviteTxt = (TextView) findViewById(R.id.coach_invite_order_accept_text);
		mRefuseInviteTxt = (TextView) findViewById(R.id.coach_invite_order_refuse_text);
		mRevokeTxt = (TextView) findViewById(R.id.coach_invite_order_revoke_text);
		mPayTxt = (TextView) findViewById(R.id.coach_invite_order_detail_pay_text);
		findViewById(R.id.coach_invite_order_detail_avatar_relative).setOnClickListener(this);
		
		
		mBack.setOnClickListener(this);
		mAvatarImage.setOnClickListener(this);
		mPhoneTxt.setOnClickListener(this);
		mComplainTxt.setOnClickListener(this);
		mAcceptInviteTxt.setOnClickListener(this);
		mRefuseInviteTxt.setOnClickListener(this);
		mTabFeeDetailLinear.setOnClickListener(this);
		mRevokeTxt.setOnClickListener(this);
		mCustomerHomeImg.setOnClickListener(this);
		mAttentionImg.setOnClickListener(this);

		mInviteCoachTxt.setOnClickListener(this);
		mInviteOtherCoachTxt.setOnClickListener(this);
		mStartLinear.setOnClickListener(this);
		mPauseLinear.setOnClickListener(this);
		mStopLinear.setOnClickListener(this);
		mPayTxt.setOnClickListener(this);
		
		customer = MainApp.getInstance().getCustomer();
		
		mAppId = mShare.getLong(Const.APP_ID, 0);
		mFirstStartTime = mShare.getLong(Const.FIRST_START_TIME,0);
		mStartTime = mShare.getLong(Const.START_TIME,0);
		mPauseTime= mShare.getLong(Const.PAUSE_TIME,0);
		mPausedTimes = mShare.getLong(Const.PAUSED_TIMES,0);
		mEndTime = mShare.getLong(Const.END_TIME,0);

		mSelectedReasonArray = new ArrayList<String>();

		/*
		 * 获取从我的教学列表中，传递过来的字段
		 * */ 
		if (getIntent() != null && getIntent().getSerializableExtra(BUNDLE_REQ_DATA) != null) {
			
			mData = (CoachInviteOrderDetail) getIntent().getSerializableExtra(BUNDLE_REQ_DATA);
			
			refreshUI () ;

			mAttentionState = mData.attention;
			if (mAttentionState == 0) {

				mAttentionImg.setImageResource(R.drawable.add);
				//attention.setBackgroundColor(getResources().getColor(R.color.color_title_txt));

			} else if (mAttentionState == 1) {

				mAttentionImg.setImageResource(R.drawable.subtrac);

			}
		}
		
	}
	
	private void refreshUI () {
		
		mMaxMinutes = mData.times*3600;
		
		mTeachingProgress.setMax(mMaxMinutes);
		
		/*当前登陆者是教练*/
		if (customer.sn.equalsIgnoreCase(mData.teacher_sn)) {

			mNickNameTxt.setText(mData.student_name);
			mWhoRefuseTxt.setText("我说:");
			findViewById(R.id.coach_invite_order_detail_invite_again_relative).setVisibility(View.GONE);
			mPhoneTxt.setText(String.valueOf(mData.student_phone));
			loadAvatar(mData.student_sn, mData.student_avatar);
			
			/*
			 * 更具订单的状态，显示不同
			 * */
			switch (mData.status) {
			
				case Const.MY_TEACHING_WAITAPPLY:
					
					mAcceptLinear.setVisibility(View.VISIBLE);
					findViewById(R.id.coach_invite_order_detail_tips).setVisibility(View.VISIBLE);
					
				break;
					
				case Const.MY_TEACHING_ACCEPTED:

					if (mData.comment_star > 0) {

						findViewById(R.id.coach_invite_order_detail_rating_relative).setVisibility(View.VISIBLE);
						mCommentRating.setIsIndicator(true);
						mCommentEdit.setEnabled(false);
					}

					break;
					
//				case Const.MY_TEACHING_START:
//
//					mAcceptLinear.setVisibility(View.GONE);
//					mTeachingOperateRelative.setVisibility(View.VISIBLE);
//					mTabLinear.setVisibility(View.VISIBLE);
//					mTabCountingTimeLinear.setVisibility(View.VISIBLE);
//					mTabCountingTimeLinear.setSelected(true);
//
//					refreshOperateStatus();
//
//				break;
				
//				case Const.MY_TEACHING_REFUSE:
//
//					mAcceptLinear.setVisibility(View.GONE);
//					mTeachingOperateRelative.setVisibility(View.GONE);
//					mTabLinear.setVisibility(View.GONE);
//
//				break;
				
//				case Const.MY_TEACHING_END:
//
//					mFeeDetailLinear.setVisibility(View.VISIBLE);
//					findViewById(R.id.coach_invite_order_detail_rating_relative).setVisibility(View.GONE);
//					mCommentRating.setIsIndicator(true);
//					//mCommentEdit.setBackgroundColor(getResources().getColor(R.color.color_white));
//					mCommentEdit.setEnabled(false);
//					mTabLinear.setVisibility(View.VISIBLE);
//					mTabFeeDetailLinear.setVisibility(View.VISIBLE);
//
//				break;

				case Const.MY_TEACHING_FINISHED:

//					mFeeDetailLinear.setVisibility(View.VISIBLE);
//					mTabLinear.setVisibility(View.VISIBLE);
//					mTabFeeDetailLinear.setVisibility(View.VISIBLE);
//					mPayTxt.setVisibility(View.GONE);
//					mCommentEdit.setEnabled(false);
//					mRevokeRelative.setVisibility(View.GONE);

					findViewById(R.id.coach_invite_order_detail_rating_relative).setVisibility(View.VISIBLE);
					mCommentRating.setIsIndicator(true);
					mCommentEdit.setEnabled(false);
					mCommentEdit.setHint("");
					break;
			}
		
		/*当前登录者是学员*/
		} else {
					
			/*
			 * 更具订单的状态，显示
			 * */
			switch (mData.status) {
			
				case Const.MY_TEACHING_WAITAPPLY:
					
					mRevokeRelative.setVisibility(View.VISIBLE);
					mRevokeTxt.setVisibility(View.VISIBLE);
					
					break;

				case Const.MY_TEACHING_ACCEPTED :

					findViewById(R.id.coach_invite_order_detail_rating_relative).setVisibility(View.VISIBLE);
					mRevokeRelative.setVisibility(View.VISIBLE);
					mPayTxt.setVisibility(View.VISIBLE);
					mComplainTxt.setVisibility(View.VISIBLE);

					break;
				
//				case Const.MY_TEACHING_END:
//
//					mFeeDetailLinear.setVisibility(View.VISIBLE);
//					mTabLinear.setVisibility(View.VISIBLE);
//					mTabFeeDetailLinear.setVisibility(View.VISIBLE);
//					mPayTxt.setVisibility(View.VISIBLE);
//					mRevokeRelative.setVisibility(View.VISIBLE);
//
//					break;

				case Const.MY_TEACHING_FINISHED:

					findViewById(R.id.coach_invite_order_detail_rating_relative).setVisibility(View.VISIBLE);
					mCommentRating.setIsIndicator(true);
					mCommentEdit.setEnabled(false);
					mCommentEdit.setHint("");
					mComplainTxt.setVisibility(View.VISIBLE);
//					mFeeDetailLinear.setVisibility(View.VISIBLE);
//					mTabLinear.setVisibility(View.VISIBLE);
//					mTabFeeDetailLinear.setVisibility(View.VISIBLE);
//					mPayTxt.setVisibility(View.GONE);
//					mCommentEdit.setEnabled(false);
//					mRevokeRelative.setVisibility(View.GONE);

					break;
			}

			mNickNameTxt.setText(mData.teacher_name);
			mWhoRefuseTxt.setText("教练说:");
			mPhoneTxt.setText(String.valueOf(mData.teacher_phone));
			
			loadAvatar(mData.teacher_sn, mData.teacher_avatar);
		}

		switch (mData.status) {

			case Const.MY_TEACHING_WAITAPPLY:

				mStatusImage.setImageResource(R.drawable.teaching_wait);

				break;
			case Const.MY_TEACHING_REVOKE:

				mStatusImage.setImageResource(R.drawable.teaching_revoke);

				break;
			case Const.MY_TEACHING_REFUSE:

				mStatusImage.setImageResource(R.drawable.teaching_refuse);
				findViewById(R.id.coach_invite_order_detail_refuse_content_linear).setVisibility(View.VISIBLE);
				if (mData.refuse_content != null && mData.refuse_content.length() > 0) {

					mRefuseContentTxt.setText(mData.refuse_content);

				} else {

					mRefuseContentTxt.setVisibility(View.GONE);
				}
				getCoachRefuseContent();
				break;
			case Const.MY_TEACHING_ACCEPTED:

				mStatusImage.setImageResource(R.drawable.teaching_end);

				break;
//			case Const.MY_TEACHING_START:
//
//				mStatusImage.setImageResource(R.drawable.teaching_teached);
//
//				break;
//			case Const.MY_TEACHING_END:
//
//				mStatusImage.setImageResource(R.drawable.teaching_end);
//
//				break;
			case Const.MY_TEACHING_FINISHED:

				mStatusImage.setImageResource(R.drawable.teaching_payed);

				break;
			case Const.MY_TEACHING_CANCEL:

				mStatusImage.setImageResource(R.drawable.teaching_cancel);

				break;
		}

		mQuestionTxt.setText(mData.msg);
		mTeachingDateTxt.setText(mData.coachDate);
		mTeachingTimeTxt.setText(mData.coachTime);
		mTeachingCourseTxt.setText(mData.course_abbr);
		mRegionTxt.setText(MainApp.getInstance().getGlobalData().getRegionName(mData.course_state));
		mTeachingHourTxt.setText(String.valueOf(mData.times)+getResources().getString(R.string.str_coachers_apply_order_teach_time));
		mStartTimeTxt.setText(Utils.longTimeToString(mData.start_time));
		mEndTimeTxt.setText(Utils.longTimeToString(mData.end_time));
		mPauseTimeTxt.setText(Utils.longTimePeriodToString(mData.pause_time));
		mTaughtTimeTxt.setText(Utils.longTimePeriodToString(mData.period_time));
		mTotalFeeTxt.setText(String.valueOf(mData.fee));
		mPriceUnitTxt.setText(mData.teacher_price+"元／小时");

		if (mData.comment_star > 0) {

			mCommentRating.setRating((float) mData.comment_star);
			mCommentEdit.setText(mData.comment_content);
			mCommentRating.setIsIndicator(true);
			mCommentEdit.setEnabled(false);
		}
	}
	
	/*
	 * 更新开始，暂停，结束等按钮的状态
	 * */
	private void refreshOperateStatus () {
		
		/*证明已经点击了开始*/
		if (mAppId == mData.id ) {
			
			/*点击了暂停按钮*/
			if (mPauseTime > mStartTime) {
				
				mIsPaused = true;
				mStartLinear.setEnabled(true);
				mPauseLinear.setEnabled(false);
				mStopLinear.setEnabled(true);
				
				mTeachingProgress.setInnerBottomText("暂停中");
				
				mProgress = (int)((mPauseTime - mFirstStartTime - mPausedTimes)/1000);
				mTeachingProgress.setProgress(mProgress);
				
			/*没有点击暂停按钮*/
			} else {
				
				mStartLinear.setEnabled(false);
				mPauseLinear.setEnabled(true);
				mStopLinear.setEnabled(true);

				if (mCountingThread == null) {

					mCountingThread = new CountingTimer(mMaxMinutes);
					//mCountingThread.execute(mMaxMinutes);
					mCountingThread.start();
				}

				
				mProgress = (int)((System.currentTimeMillis() - mFirstStartTime - mPausedTimes)/1000);
				mTeachingProgress.setProgress(mProgress);
				
				mTeachingProgress.setInnerBottomText("教学中");
				//mCountingThread.start();
			}
			
		/*还没有点击过开始*/
		} else {
			
			mTeachingProgress.setInnerBottomText("请开始");
			mStartLinear.setEnabled(true);
			mPauseLinear.setEnabled(false);
			mStopLinear.setEnabled(false);
		}
		
	}
	
	/*
	 * 加载头像
	 * 
	 * */
	private void loadAvatar(String sn,String filename) {
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(this, sn, filename,
				(int) getResources().getDimension(R.dimen.avatar_detail_size));
		if(null != avatar) {
			mAvatarImage.setImageDrawable(avatar);
		} else {
			mAvatarImage.setImageResource(R.drawable.avatar_loading);
			AsyncImageLoader.getInstance().loadAvatar(this, sn, filename,
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if (null != imageDrawable && null != mAvatarImage) {
								mAvatarImage.setImageDrawable(imageDrawable);
							}
						}
					});
		}
	}

	private void getCoachRefuseContent() {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);

		new AsyncTask<Object, Object, Integer>() {

			CoachRefuseContent request = new CoachRefuseContent(mContext, mData.id);

			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				WaitDialog.dismissWaitDialog();

				if ( result == BaseRequest.REQ_RET_OK){

					initListView(request.getSelectionList());
				}

			}
		}.execute(null, null, null);
	}

	private class complainSelectionAdapter extends IgBaseAdapter {
		private ArrayList<HashMap<String, String>> list;

		public complainSelectionAdapter(ArrayList<HashMap<String, String>> list) {
			this.list = list;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(mContext, R.layout.coach_complain_or_refuse_item, null);
				holder = new ViewHolder();

				holder.selectImage = (ImageView) convertView.findViewById(R.id.coach_complain_or_refuse_item_select_image);
				holder.selectImage.setVisibility(View.GONE);
				holder.reason = (TextView) convertView.findViewById(R.id.coach_complain_or_refuse_item_reason_text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}


			final HashMap<String, String> data = list.get(position);

			holder.reason.setText(data.get("reason"));

			if (mSelectedReasonArray.contains(data.get("id"))) {

				holder.selectImage.setImageResource(R.drawable.selected);

			} else {

				holder.selectImage.setImageResource(R.drawable.select);
			}

			holder.selectImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (mSelectedReasonArray.contains(data.get("id"))) {

						mSelectedReasonArray.remove(data.get("id"));

					} else {

						mSelectedReasonArray.add(data.get("id"));
					}

					mRefuseAdapter.notifyDataSetChanged();
				}
			});

			return convertView;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

	}

	class ViewHolder {

		private ImageView selectImage;
		private TextView reason;
	}

	private void initListView(ArrayList<HashMap<String, String>> inviteList) {
		mRefuseAdapter = new complainSelectionAdapter(inviteList);
		mRefuseList.setAdapter(mRefuseAdapter);

		setListViewHeightBasedOnChildren(mRefuseList);
		Utils.logh(TAG, "initListView myTeachingAdapter " + mRefuseAdapter);
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		if(listView == null) return;
		ListAdapter listAdapter = listView.getAdapter();
//		if (listAdapter == null) {
//			// pre-condition
//			return;
//		}
//		int totalHeight = 0;
//		for (int i = 0; i < listAdapter.getCount(); i++) {
//			View listItem = listAdapter.getView(i, null, listView);
//			listItem.measure(0, 0);
//			totalHeight += listItem.getMeasuredHeight();
//		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		//params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		int scale = (int) this.getResources().getDisplayMetrics().scaledDensity;
		params.height = listAdapter.getCount()*50*scale;
		listView.setLayoutParams(params);
	}

	/*
	* the counting thread must using Thread , can not using asyntask
	* */
	private class CountingTimer extends Thread{
		
		int countTimes = 0;
		
		public CountingTimer (int times){
			
			countTimes = times;
		}
		
		@Override
		public void run( ) {
			
			while (countTimes > 0) {
				
				try {
					Thread.sleep(1000);
					
					if(!mIsPaused) {
						
						mHandler.sendEmptyMessage(COUNTING_MESSAGE);
					}
					
					countTimes--;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
	}
	

	/*
	* update the teaching progress
	* */
	public Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message arg0) {
			// TODO Auto-generated method stub
			
			switch (arg0.what ) {
			
				case COUNTING_MESSAGE:

					mProgress = mProgress +1;
					
					DebugTools.getDebug().debug_v("mProgress", "------>>>"+mProgress);
					mTeachingProgress.setProgress(mProgress);
				
				break;
				
				case END_TEACHING:

					WaitDialog.dismissWaitDialog();
					
					if (arg0.arg1 == BaseRequest.REQ_RET_OK) {
						
						saveShare(Const.APP_ID, 0);
						saveShare(Const.FIRST_START_TIME, 0);
						saveShare(Const.START_TIME, 0);
						saveShare(Const.PAUSE_TIME, 0);
						saveShare(Const.PAUSED_TIMES, 0);
						
						CoachInviteOrderDetailActivity.this.finish();
					}
				break;

				case Const.PAY_TYPE_SELECT :

					Bundle data = arg0.getData();

					if (data != null && data.getString("Type") != null) {

						getChargeInfo(data.getString("Type"));
					}

					break;
			}
			
			
			return false;
		}
	});

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		
			case R.id.coach_invite_order_detail_back:

				this.finish();
				break;

			case R.id.coach_invite_order_detail_complain_text:

				startComplainOrRefuseFrg (0) ;
				break;

			case R.id.coach_invite_order_detail_home_image :

			case R.id.coach_invite_order_detail_avatar_relative:

			case R.id.coach_invite_order_detail_avatar_image :


				if (customer.sn.equalsIgnoreCase(mData.teacher_sn)) {

					MemDetailActivityNew.startMemDetailActivity(mContext, mData.student_sn);

				} else {

					MemDetailActivityNew.startMemDetailActivity(mContext, mData.teacher_sn);
				}
				break;

			case R.id.coach_invite_order_detail_phone_text:

				String phoneStr = mPhoneTxt.getText().toString();
				if (phoneStr != null && phoneStr.length() > 0) {

					Intent data = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneStr));
					startActivity(data);

				} else {

					Toast.makeText(mContext,R.string.str_toast_invalid_phone,Toast.LENGTH_SHORT).show();
				}

				break;

			case R.id.coach_invite_order_detail_attention_image :

				attention();

				break;
			
			case R.id.coach_invite_order_accept_text:

				CoachAcceptOrStartInvite (0);

				break;

			case R.id.coach_invite_order_refuse_text:

				startComplainOrRefuseFrg(1);

				break;

			case R.id.coach_invite_order_revoke_text:

				StudentRevokeInvite ();

				break;

			case R.id.coach_invite_order_detail_tab_fee_detial_linear:

//				if (mData.status < Const.MY_TEACHING_END) {
//
//					Toast.makeText(this, R.string.str_coach_invite_not_end, Toast.LENGTH_SHORT).show();
//					break;
//				}

				mTeachingOperateRelative.setVisibility(View.GONE);
				mFeeDetailLinear.setVisibility(View.VISIBLE);

				mTabCountingTimeLinear.setSelected(false);
				mTabFeeDetailLinear.setSelected(true);


				break;

			case R.id.coach_invite_order_detail_tab_count_time_linear:

				mTeachingOperateRelative.setVisibility(View.VISIBLE);
				mFeeDetailLinear.setVisibility(View.GONE);

				mTabCountingTimeLinear.setSelected(true);
				mTabFeeDetailLinear.setSelected(false);


				break;

			case R.id.coach_invite_order_detail_count_time_start_linear:

				/*
				 * 开始教学
				 * */

				mStartTime = System.currentTimeMillis();
				saveShare(Const.START_TIME,mStartTime);

				/*没有点击过开始按钮*/
				if (mAppId <= 0) {

					showStart();

				} else {

					/*计算暂停的时间*/
					if (mAppId == mData.id) {

						mPausedTimes = mPausedTimes+mStartTime-mPauseTime;
						saveShare(Const.PAUSED_TIMES,mPausedTimes);
						mIsPaused = false;
						refreshOperateStatus();

					} else {

						Toast.makeText(this, "您有未结束的教学", Toast.LENGTH_SHORT).show();
					}

				}

				break;

			case R.id.coach_invite_order_detail_count_time_pause_linear:

				mPauseTime = System.currentTimeMillis();
				saveShare(Const.PAUSE_TIME,mPauseTime);

				mIsPaused = true;
				mCountingThread.interrupt();
				refreshOperateStatus();

				break;

			case R.id.coach_invite_order_detail_count_time_stop_linear:

				showEnd();

				break;

			case R.id.coach_invite_order_detail_pay_text:

				if (mCommentRating.getRating() > 0) {

					FragmentManager frgMan = getSupportFragmentManager();
					FragmentTransaction ftsd = frgMan.beginTransaction();
					ftsd.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

					Fragment frg = new PayFrg(mHandler,mData.fee);

					ftsd.replace(R.id.coach_invite_order_detail_container, frg);
					ftsd.addToBackStack("payType");
					ftsd.commit();
				} else {

					Toast.makeText(CoachInviteOrderDetailActivity.this, "亲,给个5星吧", Toast.LENGTH_SHORT).show();
				}

				break;

			case R.id.coach_invite_order_detail_invite_again_text :

				CoachInviteAgainActivity.startCoachInviteAgainActivity(mContext,mData);

				break;

			case R.id.coach_invite_order_detail_invite_again_list_text :

				CoachListActivity.startCoachList(mContext, -1);

				break;

		}
	}
	
	private void saveShare(String key ,long values) {
		
		mShare.edit().putLong(key, values).commit();
	
	}
	
	/*
	 * 打开投诉或者拒绝界面
	 * 
	 * */
	private void startComplainOrRefuseFrg (int type) {
		
		FragmentManager frgMan = getSupportFragmentManager();
		FragmentTransaction ftsd = frgMan.beginTransaction();
		ftsd.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		
		Fragment frg = new CoachComplainOrRefuseFrg(type,mData.teacher_id,mData.student_id,mData.id);
		
		ftsd.replace(R.id.coach_invite_order_detail_container, frg);
		ftsd.addToBackStack("CoachComplainOrRefuseFrg");
		ftsd.commit();
	}
	
	/*
	 * 教练接受邀请 or 开始教学
	 * */
	private void CoachAcceptOrStartInvite (final int type) {
		
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.is_loading);
		new AsyncTask<Object, Object, Integer>() {
			
			CoachAcceptInvite request = new CoachAcceptInvite(CoachInviteOrderDetailActivity.this, mData.id,type);
			
			@Override
			protected Integer doInBackground(Object... params) {
				
				return request.connectUrl();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				WaitDialog.dismissWaitDialog();
				if(BaseRequest.REQ_RET_OK == result) {
					
					/*
					 * 接受邀请
					 * */
					if(request.type == 0) {
						
						Toast.makeText(CoachInviteOrderDetailActivity.this, R.string.str_start_invite_teaching_success, Toast.LENGTH_SHORT).show();
						mData.status = Const.MY_TEACHING_ACCEPTED;
						refreshUI () ;
						mContext.finish();
						
					/*
					 * 开始教学
					 * */
					} else if (request.type == 1)  {
						
						mFirstStartTime = System.currentTimeMillis();
						mAppId = mData.id;
						saveShare(Const.FIRST_START_TIME,mFirstStartTime);
						saveShare(Const.APP_ID,mAppId);
						//mData.status = Const.MY_TEACHING_START;
						
						refreshOperateStatus();
						refreshUI ();
					}
					
					
				} else {

					Toast.makeText(CoachInviteOrderDetailActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
	}
	
	
	/*
	 * 学员撤约
	 * */
	private void StudentRevokeInvite () {
		
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_invite_revoking_invite);
		new AsyncTask<Object, Object, Integer>() {
			
			StudentRevoketInvite request = new StudentRevoketInvite(CoachInviteOrderDetailActivity.this, mData.id);
			
			@Override
			protected Integer doInBackground(Object... params) {
				
				return request.connectUrl();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				WaitDialog.dismissWaitDialog();
				if(BaseRequest.REQ_RET_OK == result) {
					
					Toast.makeText(CoachInviteOrderDetailActivity.this, R.string.str_invite_detail_oper_btn_app_revoke_done, Toast.LENGTH_SHORT).show();
					mData.status = Const.MY_TEACHING_REVOKE;
					refreshUI () ;
					mContext.finish();
					
				} else {

					Toast.makeText(CoachInviteOrderDetailActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
	}
	
	/*
	 * 教练第一次开始教学的时候，弹出选择框
	 * */
	private void showStart() {
		new AlertDialog.Builder(this)
			.setTitle(R.string.str_coach_start_teaching_title)
			.setMessage(R.string.str_coach_start_teaching_message)
			.setPositiveButton(R.string.str_photo_commit, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					CoachAcceptOrStartInvite(1);
				}
			})
			.setNegativeButton(R.string.str_photo_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//checkIndustry();
				}
			})
			.setCancelable(true)
			.show();
	}
	
	/*
	 * 教练结束的教学的时候，弹出对话框
	 * */
	private void showEnd() {
		new AlertDialog.Builder(this)
			.setTitle(R.string.str_coach_end_teaching_title)
			.setMessage(R.string.str_coach_end_teaching_message)
			.setPositiveButton(R.string.str_photo_commit, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					//CoachEndTeachingThread();
					
					if(!Utils.isConnected(CoachInviteOrderDetailActivity.this)) {
						return ;
					}
					
					WaitDialog.showWaitDialog(CoachInviteOrderDetailActivity.this, R.string.is_loading);
					
					/*如果是处于暂停状态*/
					if (mPauseTime > mStartTime) {
						
						mPausedTimes = mPausedTimes+System.currentTimeMillis() - mPauseTime;
					}
					
					new CoachEndTeachingThread1().start();
				}
			})
			.setNegativeButton(R.string.str_photo_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			})
			.setCancelable(true)
			.show();
	}
	
	private void CoachEndTeachingThread() {
		
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_invite_revoking_invite);
		new AsyncTask<Object, Object, Integer>() {
			
			CoachEndTeaching request = new CoachEndTeaching(CoachInviteOrderDetailActivity.this, mData.id, mProgress, mPausedTimes);
			
			@Override
			protected Integer doInBackground(Object... params) {
				
				return request.connectUrl();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				WaitDialog.dismissWaitDialog();
				if(BaseRequest.REQ_RET_OK == result) {
					
					
				} else {

					Toast.makeText(CoachInviteOrderDetailActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
		
	}
	
	/*
	 * 学员评价教练
	 * */
	private void studentCommentCoach() {
		
		if(!Utils.isConnected(this)) {
			return ;
		}

		final String commentStr = mCommentEdit.getText().toString();
		if (commentStr.length() <= 0) {


			return ;
		}

		new AsyncTask<Object, Object, Integer>() {
			
			CoachStudentComment request = new CoachStudentComment(CoachInviteOrderDetailActivity.this,
					mData.id,
					mData.student_id,
					mData.teacher_id,
					mCommentRating.getRating(),
					commentStr);
			
			@Override
			protected Integer doInBackground(Object... params) {
				
				return request.connectUrl();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				WaitDialog.dismissWaitDialog();
				if(BaseRequest.REQ_RET_OK == result) {
					
					
				} else {

					Toast.makeText(CoachInviteOrderDetailActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
		
	}
	
	/*
	 * 获取ping++ 的charge 信息
	 * */
	private void getChargeInfo(final String pay_chanel) {
		
		if(!Utils.isConnected(this)) {
			return ;
		}

		WaitDialog.showWaitDialog(mContext,R.string.str_loading_msg);
		new AsyncTask<Object, Object, Integer>() {
			
			CoachPayCharge request = new CoachPayCharge(CoachInviteOrderDetailActivity.this, mData.id,pay_chanel);
			
			@Override
			protected Integer doInBackground(Object... params) {
				
				return request.connectUrl();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				WaitDialog.dismissWaitDialog();
				if(BaseRequest.REQ_RET_OK == result) {
					
					Intent intent = new Intent();
					String packageName = getPackageName();
					ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
					intent.setComponent(componentName);
					
					intent.putExtra(PaymentActivity.EXTRA_CHARGE, request.charge);
					startActivityForResult(intent, REQUEST_CODE_PAYMENT);
					
				} else {

					Toast.makeText(CoachInviteOrderDetailActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
		
	}
	
	private class CoachEndTeachingThread1 extends Thread {
	
	
		@Override
		public void run( ) {
			
			CoachEndTeaching request = new CoachEndTeaching(CoachInviteOrderDetailActivity.this, mData.id, mProgress, mPausedTimes/1000);
			
			int result = request.connectUrl();
			
			Message msg = mHandler.obtainMessage();
			
			msg.what = END_TEACHING;
			msg.arg1 = result;
			mHandler.sendMessage(msg);
		}
	}

	/*
	 * 添加关注
	 * */
	private void attention() {

		/*添加或取消关注*/
		WaitDialog.showWaitDialog(this, R.string.str_loading_waiting);

		if (customer.sn.equalsIgnoreCase(mData.teacher_sn)) {

			attention_sn = mData.student_sn;
		} else {

			attention_sn = mData.teacher_sn;
		}
		new AsyncTask<Object, Object, Integer>() {


			FriendAttentionAdd request = new FriendAttentionAdd(mContext,customer.sn,attention_sn,mAttentionState);
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				if(BaseRequest.REQ_RET_OK == result) {

					mAttentionState = mAttentionState == 1 ? 0 : 1;
					mData.attention = mAttentionState;

					if (mAttentionState == 0) {

						mAttentionImg.setImageResource(R.drawable.add);
						//attention.setBackgroundColor(getResources().getColor(R.color.color_title_txt));

					} else {

						mAttentionImg.setImageResource(R.drawable.subtrac);

					}

				} else {


				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	     //支付页面返回处理
	    if (requestCode == REQUEST_CODE_PAYMENT) {
	        if (resultCode == Activity.RESULT_OK) {
	            String result = data.getExtras().getString("pay_result");
	            /* 处理返回值
	             * "success" - payment succeed
	             * "fail"    - payment failed
	             * "cancel"  - user canceld
	             * "invalid" - payment plugin not installed
	             *
	             * 如果是银联渠道返回 invalid，调用 UPPayAssistEx.installUPPayPlugin(this); 安装银联安全支付控件。
	             */
	            String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
	            String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
	            
	            DebugTools.getDebug().debug_v("ping++ 支付的结果 result", result);
	            DebugTools.getDebug().debug_v("ping++ 支付的结果 errorMsg", errorMsg);
	            //DebugTools.getDebug().debug_v("ping++ 支付的结果 extraMsg", extraMsg);

				if (result != null && result.equals("success")) {

					Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();

					studentCommentCoach();
					this.finish();

				} else if (result != null && result.equals("fail")) {

					Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
				}
	        }
	    }
	}
}
