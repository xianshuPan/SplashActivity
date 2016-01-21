package com.hylg.igolf.ui.coach;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Paint;
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

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachInviteOrderDetail;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.CoachAcceptInvite;
import com.hylg.igolf.cs.request.CoachPayCharge;
import com.hylg.igolf.cs.request.CoachRefuseContent;
import com.hylg.igolf.cs.request.CoachStudentComment;
import com.hylg.igolf.cs.request.GetCoachInviteOrderDetail;
import com.hylg.igolf.cs.request.StudentPinDanInvite;
import com.hylg.igolf.cs.request.StudentRevoketInvite;
import com.hylg.igolf.ui.view.CircleImageView;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;
import com.pingplusplus.android.PaymentActivity;
import com.pingplusplus.android.PingppLog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CoachInviteOrderDetailActivityNew extends FragmentActivity implements
															OnClickListener{
	
	private final String 					TAG = "CoachInviteOrderDetailActivityNew";

	public final static int 				PIN_DAN = 0,MY_TEACHING = 1,MY_STUDY = 2;
	
	private final static String 			BUNDLE_REQ_DATA = "data";
	private final static String 			BUNDLE_REQ_DATA_ID = "ID";
	private final static String 			BUNDLE_REQ_DATA_FROM = "FROM";
	
	private final int 						REQUEST_CODE_PAYMENT =1003;
	
	private ImageButton                     mBack = null;

	private ImageView                       mTypeImage,mCoachType;

	private TextView                        mQuestionTxt, mComplainTxt;
	/*
	 * 约球的时间和地点，球场
	 *
	 * */
	private TextView                        mTeachingDateTxt,mTeachingHourTxt,mTeachingCourseAddressTxt,mTeachingCourseTxt,mOriginalPriceTxt,mDiscountPriceTxt;

	private RelativeLayout                  mCoachPhoneRelative = null;
	private LinearLayout                    mCoachLinear = null;
	private CircleImageView                 mCoachAvatarImage = null;
	private ImageView                       mCoachSexImage = null;
	private TextView                      	mCoachNameTxt = null,mCoachPhoneTxt,mCoachBallAgeTxt,mCoachTeachingCountTxt;
	private RatingBar                       mCoachRating;

	private RelativeLayout                  mStudent1PhoneRelative = null,mStudent1CommentRelative;
	private LinearLayout                    mStudent1Linear = null;
	private CircleImageView                 mStudent1AvatarImage = null;
	private ImageView                       mStudent1SexImage = null;
	private TextView                      	mStudent1NameTxt = null,mStudent1PhoneTxt,mStudent1BallAgeTxt,mStudent1TeachingCountTxt,mStudent1PaymentAmountTxt,
											mStudent1PaymentStatusTxt,mStudent1CommentContentTxt;
	private RatingBar                       mStudent1CommentRating;


	private RelativeLayout                  mStudent2PhoneRelative = null,mStudent2CommentRelative;
	private LinearLayout                    mStudent2Linear = null;
	private CircleImageView                 mStudent2AvatarImage = null;
	private ImageView                       mStudent2SexImage = null;
	private TextView                      	mStudent2NameTxt = null,mStudent2PhoneTxt,mStudent2BallAgeTxt,mStudent2TeachingCountTxt,mStudent2PaymentAmountTxt,
											mStudent2PaymentStatusTxt,mStudent2CommentContentTxt;
	private RatingBar                       mStudent2CommentRating;

	
	/*
	 * 费用明细
	 * */
	private RatingBar                       mCommentRating;
	private EditText                        mCommentEdit;
	
	/*
	 * 接受、结束、支付
	 * */

	private TextView                        mAcceptInviteTxt,mRefuseInviteTxt,mRevokeTxt,mRevokePinDanTxt,mPinDanTxt,mPayTxt;
	private RelativeLayout                  mAcceptLinear;
	private LinearLayout                    mRevokeRelative;

	/*
	*
	* refuse
	* */
	private ListView 						mRefuseList = null;
	private TextView                        mRefuseContentTxt = null,mInviteCoachTxt = null,mInviteOtherCoachTxt = null,mWhoRefuseTxt = null;
	private ArrayList<String>               mSelectedReasonArray = null;
	private complainSelectionAdapter 		mRefuseAdapter;
	
	private Customer                        customer;
	
	private CoachInviteOrderDetail          mData;

	private long                            id;
	private int                             from;

	private FragmentActivity                mContext = null;
	DecimalFormat df = new DecimalFormat("#.00");
	
	
	public static void startCoachInviteOrderDetailActivityNew(Activity context, CoachInviteOrderDetail data) {
		Intent intent = new Intent();
		intent.setClass(context, CoachInviteOrderDetailActivityNew.class);
		intent.putExtra(BUNDLE_REQ_DATA, data);
		context.startActivity(intent);
		context.overridePendingTransition(R.anim.ac_slide_right_in,R.anim.ac_slide_left_out);

	}

	public static void startCoachInviteOrderDetailActivityNew(Activity context, long id,int from) {
		Intent intent = new Intent();
		intent.setClass(context, CoachInviteOrderDetailActivityNew.class);
		intent.putExtra(BUNDLE_REQ_DATA_ID, id);
		intent.putExtra(BUNDLE_REQ_DATA_FROM, from);
		context.startActivity(intent);
		context.overridePendingTransition(R.anim.ac_slide_right_in,R.anim.ac_slide_left_out);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Tools.getInstance().setActivityNoTitle(this);
		DebugTools.getDebug().debug_v(TAG, "onCreate..");
		
		setContentView(R.layout.coach_invite_order_detail_ac_new);
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

		mBack = (ImageButton) findViewById(R.id.coach_invite_order_detail_back);
		mComplainTxt = (TextView) findViewById(R.id.coach_invite_order_detail_complain_text);
		mQuestionTxt = (TextView) findViewById(R.id.coach_invite_order_detail_content_text);
		mTeachingDateTxt = (TextView) findViewById(R.id.coach_invite_order_detail_date_text);
		mTeachingHourTxt = (TextView) findViewById(R.id.coach_invite_order_detail_teach_time_text);
		mTeachingCourseAddressTxt = (TextView) findViewById(R.id.coach_invite_order_detail_place_address_text);
		mTeachingCourseTxt = (TextView) findViewById(R.id.coach_invite_order_detail_place_text);
		mOriginalPriceTxt = (TextView) findViewById(R.id.coach_invite_order_detail_original_price_text);
		mDiscountPriceTxt = (TextView) findViewById(R.id.coach_invite_order_detail_discount_price_text);
		mTypeImage  = (ImageView) findViewById(R.id.coach_invite_order_detail_type_image);
		mCoachType = (ImageView) findViewById(R.id.coach_invite_order_detail_coach_type_image);

		mCoachPhoneRelative  = (RelativeLayout) findViewById(R.id.coach_invite_order_detail_coach_phone_relative);
		mCoachLinear  = (LinearLayout) findViewById(R.id.coach_invite_order_detail_coach_linear);
		mCoachAvatarImage  = (CircleImageView) findViewById(R.id.coach_invite_order_detail_coach_avatar);
		mCoachSexImage  = (ImageView) findViewById(R.id.coach_invite_order_detail_coach_sex_image);
		mCoachNameTxt = (TextView) findViewById(R.id.coach_invite_order_detail_coach_name_text);
		mCoachPhoneTxt= (TextView) findViewById(R.id.coach_invite_order_detail_coach_phone_text);
		mCoachBallAgeTxt= (TextView) findViewById(R.id.coach_invite_order_detail_coach_ball_age_text);
		mCoachTeachingCountTxt= (TextView) findViewById(R.id.coach_invite_order_detail_coach_teaching_count_text);
		mCoachRating= (RatingBar) findViewById(R.id.coach_invite_order_detail_coach_rating);

		mStudent1PhoneRelative  = (RelativeLayout) findViewById(R.id.coach_invite_order_detail_student1_phone_relative);
		mStudent1Linear  = (LinearLayout) findViewById(R.id.coach_invite_order_detail_student1_linear);
		mStudent1AvatarImage  = (CircleImageView) findViewById(R.id.coach_invite_order_detail_student1_avatar);
		mStudent1SexImage  = (ImageView) findViewById(R.id.coach_invite_order_detail_student1_sex_image);
		mStudent1NameTxt = (TextView) findViewById(R.id.coach_invite_order_detail_student1_name_text);
		mStudent1PhoneTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student1_phone_text);
		mStudent1PaymentAmountTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student1_pay_amount_text);
		mStudent1PaymentStatusTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student1_pay_status_text);
		mStudent1CommentContentTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student1_comment_content_text);
		mStudent1CommentRelative = (RelativeLayout) findViewById(R.id.coach_invite_order_detail_student1_comment_relative);
		mStudent1BallAgeTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student1_ball_age_text);
		mStudent1TeachingCountTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student1_teaching_count_text);
		mStudent1CommentRating= (RatingBar) findViewById(R.id.coach_invite_order_detail_student1_comment_rating);

		mStudent2PhoneRelative  = (RelativeLayout) findViewById(R.id.coach_invite_order_detail_student2_phone_relative);
		mStudent2Linear  = (LinearLayout) findViewById(R.id.coach_invite_order_detail_student2_linear);
		mStudent2AvatarImage  = (CircleImageView) findViewById(R.id.coach_invite_order_detail_student2_avatar);
		mStudent2SexImage  = (ImageView) findViewById(R.id.coach_invite_order_detail_student2_sex_image);
		mStudent2NameTxt = (TextView) findViewById(R.id.coach_invite_order_detail_student2_name_text);
		mStudent2PhoneTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student2_phone_text);
		mStudent2PaymentAmountTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student2_pay_amount_text);
		mStudent2PaymentStatusTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student2_pay_status_text);
		mStudent2CommentContentTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student2_comment_content_text);
		mStudent2CommentRelative = (RelativeLayout) findViewById(R.id.coach_invite_order_detail_student2_comment_relative);
		mStudent2BallAgeTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student2_ball_age_text);
		mStudent2TeachingCountTxt= (TextView) findViewById(R.id.coach_invite_order_detail_student2_teaching_count_text);
		mStudent2CommentRating= (RatingBar) findViewById(R.id.coach_invite_order_detail_student2_comment_rating);
		mCommentRating = (RatingBar) findViewById(R.id.coach_invite_order_detail_rating);
		mCommentEdit = (EditText) findViewById(R.id.coach_invite_order_detail_comments_edit);

		mRefuseList = (ListView) findViewById(R.id.coach_invite_order_detail_refuse_content_list);
		mRefuseContentTxt = (TextView) findViewById(R.id.coach_invite_order_detail_refuse_content_text);
		mInviteCoachTxt = (TextView) findViewById(R.id.coach_invite_order_detail_invite_again_text);
		mWhoRefuseTxt  = (TextView) findViewById(R.id.coach_invite_order_detail_who_refuse_text);
		mInviteOtherCoachTxt = (TextView) findViewById(R.id.coach_invite_order_detail_invite_again_list_text);
		mInviteOtherCoachTxt.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

		mAcceptLinear = (RelativeLayout) findViewById(R.id.coach_invite_order_detail_accept_linear);
		mRevokeRelative = (LinearLayout) findViewById(R.id.coach_invite_order_detail_revoke_relative);
		mAcceptInviteTxt = (TextView) findViewById(R.id.coach_invite_order_accept_text);
		mRefuseInviteTxt = (TextView) findViewById(R.id.coach_invite_order_refuse_text);
		mRevokeTxt = (TextView) findViewById(R.id.coach_invite_order_revoke_text);
		mPinDanTxt = (TextView) findViewById(R.id.coach_invite_order_pin_dan_text);
		mRevokePinDanTxt = (TextView) findViewById(R.id.coach_invite_order_revoke_pin_dan_text);
		mPayTxt = (TextView) findViewById(R.id.coach_invite_order_detail_pay_text);
		findViewById(R.id.coach_invite_order_detail_avatar_relative).setOnClickListener(this);
		
		
		mBack.setOnClickListener(this);
		mComplainTxt.setOnClickListener(this);
		mAcceptInviteTxt.setOnClickListener(this);
		mRefuseInviteTxt.setOnClickListener(this);
		mRevokeTxt.setOnClickListener(this);
		mPinDanTxt.setOnClickListener(this);
		mRevokePinDanTxt.setOnClickListener(this);
		mInviteCoachTxt.setOnClickListener(this);
		mInviteOtherCoachTxt.setOnClickListener(this);
		mPayTxt.setOnClickListener(this);
		mCoachPhoneRelative.setOnClickListener(this);
		mStudent1PhoneRelative.setOnClickListener(this);
		mStudent2PhoneRelative.setOnClickListener(this);
		
		customer = MainApp.getInstance().getCustomer();

		mSelectedReasonArray = new ArrayList<String>();

		/*
		 * 获取从我的教学列表中，传递过来的字段
		 * */ 
		if (getIntent() != null && getIntent().getSerializableExtra(BUNDLE_REQ_DATA) != null) {
			
			mData = (CoachInviteOrderDetail) getIntent().getSerializableExtra(BUNDLE_REQ_DATA);
			
			//refreshUI () ;
		}

		if (getIntent() != null && getIntent().getLongExtra(BUNDLE_REQ_DATA_ID, -1) >= 0) {

			id = getIntent().getLongExtra(BUNDLE_REQ_DATA_ID,-1);

		}

		if (getIntent() != null && getIntent().getIntExtra(BUNDLE_REQ_DATA_FROM, -1) >= 0) {

			from = getIntent().getIntExtra(BUNDLE_REQ_DATA_FROM, -1);

			initData();
		}
		
	}


	private void initData() {

		if(!Utils.isConnected(this)) {
			return ;
		}

		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		final GetCoachInviteOrderDetail request = new GetCoachInviteOrderDetail(mContext,id,customer.sn);
		new AsyncTask<Object,Object,Integer>(){

			@Override
			protected Integer doInBackground (Object... params) {

				return request.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer retId) {
				super.onPostExecute(retId);

				if(BaseRequest.REQ_RET_F_NO_DATA == retId ) {


				}
				else if (BaseRequest.REQ_RET_OK == retId) {

					mData = request.data;
					mData.id = id;
					refreshUI();

				}

				WaitDialog.dismissWaitDialog();
				//WaitDialog.dismissWaitDialog();
			}

		}.execute(null,null,null);
	}
	
	private void refreshUI () {


		mQuestionTxt.setText(mData.msg);
		mTeachingDateTxt.setText(mData.coachDate);
		mTeachingCourseTxt.setText(mData.course_abbr);
		mTeachingCourseAddressTxt.setText(mData.course_address);
		mTeachingHourTxt.setText(String.valueOf(mData.times));
		mDiscountPriceTxt.setText(getResources().getString(R.string.str_rmb)+df.format(mData.discountPrice));

		if (mData.type == 0) {

			mTypeImage.setVisibility(View.GONE);
		}
		else {

			mTypeImage.setVisibility(View.VISIBLE);

		}


		if (mData.teacher_sn != null && mData.teacher_sn.length() > 0) {

			//DownLoadImageTool.getInstance(mContext).displayImage(Utils.getAvatarURLString(mData.teacher_sn), mCoachAvatarImage, null);
			Utils.loadAvatar(this,mData.teacher_sn,mCoachAvatarImage);
			mCoachNameTxt.setText(mData.teacher_name);
			mCoachPhoneTxt.setText(mData.teacher_phone);
			mCoachBallAgeTxt.setText(String.valueOf(mData.teacher_ball_age)+getResources().getString(R.string.str_year));
			mCoachTeachingCountTxt.setText(String.valueOf(mData.teacher_experience));
			mCoachRating.setRating(mData.teacher_star);

			if(mData.teacher_sex == Const.SEX_MALE ){

				mCoachSexImage.setImageResource(R.drawable.man);

			}
			else {

				mCoachSexImage.setImageResource(R.drawable.woman);
			}

			if(mData.teacher_type == Const.COACH_TYPE_PROFESSIONAL ){

				mCoachType.setVisibility(View.VISIBLE);

			}
			else {

				mCoachType.setVisibility(View.GONE);
			}

		}


		if (mData.student1_sn != null && mData.student1_sn.length() > 0) {

			//DownLoadImageTool.getInstance(mContext).displayImage(Utils.getAvatarURLString(mData.student1_sn), mStudent1AvatarImage, null);
			Utils.loadAvatar(this,mData.student1_sn,mStudent1AvatarImage);
			mStudent1NameTxt.setText(mData.student1_name);
			mStudent1PhoneTxt.setText(mData.student1_phone);
			mStudent1BallAgeTxt.setText(String.valueOf(mData.student1_ball_age)+getResources().getString(R.string.str_year));
			mStudent1TeachingCountTxt.setText(String.valueOf(mData.student1_experiment));
			if(mData.student1_sex == Const.SEX_MALE ){

				mStudent1SexImage.setImageResource(R.drawable.man);

			}
			else {

				mStudent1SexImage.setImageResource(R.drawable.woman);
			}

			mStudent1PaymentAmountTxt.setText(getResources().getString(R.string.str_rmb)+df.format(mData.student1_payment_amount)+getResources().getString(R.string.str_rmb_unit));

//			if (mData.student1_payment_status == Const.MY_TEACHING_FINISHED) {
//
//				mStudent1PaymentStatusTxt.setText(R.string.str_pay_finish);
//			}
//			else {
//
//				mStudent1PaymentStatusTxt.setText(R.string.str_not_pay);
//			}

			setStatus(mData.student1_payment_status,mStudent1PaymentStatusTxt);

			if (mData.student1_comment_status == 1) {

				mStudent1CommentRating.setRating(mData.student1_comment_rating);
				mStudent1CommentContentTxt.setText(mData.student1_comment_content);
			}
			else {

				mStudent1CommentRelative.setVisibility(View.GONE);
			}

		}

		if (mData.student2_sn != null && mData.student2_sn.length() > 0) {

			//DownLoadImageTool.getInstance(mContext).displayImage(Utils.getAvatarURLString(mData.student2_sn), mStudent2AvatarImage, null);
			Utils.loadAvatar(this,mData.student2_sn,mStudent2AvatarImage);
			mStudent2NameTxt.setText(mData.student2_name);
			mStudent2PhoneTxt.setText(mData.student2_phone);
			mStudent2BallAgeTxt.setText(String.valueOf(mData.student2_ball_age)+getResources().getString(R.string.str_year));
			mStudent2TeachingCountTxt.setText(String.valueOf(mData.student2_experiment));
			if(mData.student2_sex == Const.SEX_MALE ){

				mStudent2SexImage.setImageResource(R.drawable.man);

			}
			else {

				mStudent2SexImage.setImageResource(R.drawable.woman);
			}

			mStudent2PaymentAmountTxt.setText(getResources().getString(R.string.str_rmb)+df.format(mData.student2_payment_amount)+getResources().getString(R.string.str_rmb_unit));
			setStatus(mData.student2_payment_status, mStudent2PaymentStatusTxt);

			if (mData.student2_comment_status == 1) {

				mStudent2CommentRating.setRating(mData.student2_comment_rating);
				mStudent2CommentContentTxt.setText(mData.student2_comment_content);
			}
			else {

				mStudent2CommentRelative.setVisibility(View.GONE);
			}

		}



		
		/*当前登陆者是教练*/
		if (customer.sn.equalsIgnoreCase(mData.teacher_sn)) {

			mWhoRefuseTxt.setText("我说:");
			findViewById(R.id.coach_invite_order_detail_invite_again_relative).setVisibility(View.GONE);

			/*
			 * 更具订单的状态，显示不同
			 * */
			switch (mData.status) {
			
				case Const.MY_TEACHING_WAITAPPLY:

					findViewById(R.id.coach_invite_order_detail_bottom_relative).setVisibility(View.VISIBLE);
					mAcceptLinear.setVisibility(View.VISIBLE);

					findViewById(R.id.coach_invite_order_detail_tips).setVisibility(View.VISIBLE);
					
				break;

			}
		
		/*当前登录者是学员*/
		} else {

			mWhoRefuseTxt.setText("教练说:");

		}

		if (from == PIN_DAN) {

			mCoachLinear.setVisibility(View.VISIBLE);
			mStudent1Linear.setVisibility(View.VISIBLE);

			mRevokeRelative.setVisibility(View.VISIBLE);
			findViewById(R.id.coach_invite_order_detail_bottom_relative).setVisibility(View.VISIBLE);
			mPinDanTxt.setVisibility(View.VISIBLE);


		/*my teaching*/
		} else if (from == MY_TEACHING) {

			mDiscountPriceTxt.setText(getResources().getString(R.string.str_rmb)+df.format(mData.discountPrice*mData.ratio));
			if (mData.student1_sn != null && mData.student1_sn.length() > 0) {

				mStudent1Linear.setVisibility(View.VISIBLE);
				mStudent1PaymentAmountTxt.setText(getResources().getString(R.string.str_rmb) + df.format(mData.student1_payment_amount * mData.ratio) + getResources().getString(R.string.str_rmb_unit));
			}


			if (mData.student2_sn != null && mData.student2_sn.length() > 0) {

				mStudent2PaymentAmountTxt.setText(getResources().getString(R.string.str_rmb) + df.format(mData.student2_payment_amount * mData.ratio) + getResources().getString(R.string.str_rmb_unit));
				mStudent2Linear.setVisibility(View.VISIBLE);
			}


			/*
			 * 更具订单的状态，显示
			 * */
			switch (mData.status) {

				case Const.MY_TEACHING_WAITAPPLY:

					findViewById(R.id.coach_invite_order_detail_bottom_relative).setVisibility(View.VISIBLE);
					mAcceptLinear.setVisibility(View.VISIBLE);
					findViewById(R.id.coach_invite_order_detail_tips).setVisibility(View.VISIBLE);

					break;

			}

		/* my study */
		} else {

			if (mData.alone_status == 1) {

				mOriginalPriceTxt.setVisibility(View.VISIBLE);
				DecimalFormat df = new DecimalFormat("#.00");
				mOriginalPriceTxt.setText(Utils.addStrikeLine(getResources().getString(R.string.str_rmb) + df.format(mData.originalPrice)));
			}

			mCoachLinear.setVisibility(View.VISIBLE);
			switch (mData.status) {
				case Const.MY_TEACHING_WAITAPPLY:

					findViewById(R.id.coach_invite_order_detail_bottom_relative).setVisibility(View.VISIBLE);
					mRevokeRelative.setVisibility(View.VISIBLE);
					mRevokeTxt.setVisibility(View.VISIBLE);

					break;

				case Const.MY_TEACHING_ACCEPTED:

					findViewById(R.id.coach_invite_order_detail_rating_relative).setVisibility(View.VISIBLE);
					findViewById(R.id.coach_invite_order_detail_bottom_relative).setVisibility(View.VISIBLE);
					mRevokeRelative.setVisibility(View.VISIBLE);
					mPayTxt.setVisibility(View.VISIBLE);

					if (mData.type == 1) {

						mRevokePinDanTxt.setVisibility(View.VISIBLE);
					}

					mComplainTxt.setVisibility(View.VISIBLE);

					break;


				case Const.MY_TEACHING_FINISHED:

					findViewById(R.id.coach_invite_order_detail_rating_relative).setVisibility(View.VISIBLE);
					mCommentRating.setIsIndicator(true);
					mCommentEdit.setEnabled(false);
					mCommentEdit.setHint("");
					mComplainTxt.setVisibility(View.VISIBLE);

					break;
			}

		}

		switch (mData.status) {

			case Const.MY_TEACHING_WAITAPPLY:

				//mStatusImage.setImageResource(R.drawable.teaching_wait);

				break;
			case Const.MY_TEACHING_REVOKE:

				//mStatusImage.setImageResource(R.drawable.teaching_revoke);

				break;
			case Const.MY_TEACHING_REFUSE:

				//mStatusImage.setImageResource(R.drawable.teaching_refuse);
				findViewById(R.id.coach_invite_order_detail_refuse_content_linear).setVisibility(View.VISIBLE);
				if (mData.refuse_content != null && mData.refuse_content.length() > 0) {

					mRefuseContentTxt.setText(mData.refuse_content);

				} else {

					mRefuseContentTxt.setVisibility(View.GONE);
				}
				getCoachRefuseContent();
				break;
			case Const.MY_TEACHING_ACCEPTED:

				//mStatusImage.setImageResource(R.drawable.teaching_end);

				break;

			case Const.MY_TEACHING_FINISHED:

				//mStatusImage.setImageResource(R.drawable.teaching_payed);

				break;
			case Const.MY_TEACHING_CANCEL:

				//mStatusImage.setImageResource(R.drawable.teaching_cancel);

				break;
		}

	}

	private void setStatus (int status,TextView statusText) {

		switch (status) {
			case Const.MY_TEACHING_WAITAPPLY:

				statusText.setText("待接受");
				statusText.setTextColor(getResources().getColor(R.color.green_5fb64e));
				break;

			case Const.MY_TEACHING_REVOKE:

				statusText.setText("已撤销");
				statusText.setTextColor(getResources().getColor(R.color.color_red));

				break;

			case Const.MY_TEACHING_REFUSE:

				statusText.setText("已拒绝");
				statusText.setTextColor(getResources().getColor(R.color.color_red));


				break;

			case Const.MY_TEACHING_ACCEPTED:
				statusText.setText("待支付");
				statusText.setTextColor(getResources().getColor(R.color.color_yellow));


				break;

			case Const.MY_TEACHING_FINISHED:

				statusText.setText("已支付");
				statusText.setTextColor(getResources().getColor(R.color.color_blue));

				break;



			case Const.MY_TEACHING_CANCEL:

				statusText.setText("已过期");
				statusText.setTextColor(getResources().getColor(R.color.color_red));

				break;
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
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		//params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		int scale = (int) this.getResources().getDisplayMetrics().scaledDensity;
		params.height = listAdapter.getCount()*50*scale;
		listView.setLayoutParams(params);
	}

	/*
	* update the teaching progress
	* */
	public Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message arg0) {
			// TODO Auto-generated method stub
			
			switch (arg0.what ) {

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

				startComplainOrRefuseFrg(0) ;
				break;

			
			case R.id.coach_invite_order_accept_text:

				CoachAcceptOrStartInvite(0);

				break;

			case R.id.coach_invite_order_refuse_text:

				startComplainOrRefuseFrg(1);

				break;

			case R.id.coach_invite_order_revoke_text:

				StudentRevokeInvite();

				break;

			case R.id.coach_invite_order_pin_dan_text:

				StudentPinDanInvite(0);

				break;

			case R.id.coach_invite_order_revoke_pin_dan_text:

				StudentPinDanInvite(1);

				break;

			case R.id.coach_invite_order_detail_coach_phone_relative:

				Utils.callPhone(mContext, mCoachPhoneTxt.getText().toString());

				break;

			case R.id.coach_invite_order_detail_student1_phone_relative:

				Utils.callPhone(mContext,mStudent1PhoneTxt.getText().toString());

				break;

			case R.id.coach_invite_order_detail_student2_phone_relative:

				Utils.callPhone(mContext,mStudent2PhoneTxt.getText().toString());

				break;


			case R.id.coach_invite_order_detail_pay_text:

				if (mCommentRating.getRating() > 0) {

					FragmentManager frgMan = getSupportFragmentManager();
					FragmentTransaction ftsd = frgMan.beginTransaction();
					ftsd.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

					Fragment frg = new PayFrg(mHandler,mData.student1_payment_amount);

					ftsd.replace(R.id.coach_invite_order_detail_container, frg);
					ftsd.addToBackStack("payType");
					ftsd.commit();

				} else {

					Toast.makeText(CoachInviteOrderDetailActivityNew.this, "亲,给个5星吧", Toast.LENGTH_SHORT).show();
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
	
	/*
	 * 打开投诉或者拒绝界面
	 * 
	 * */
	private void startComplainOrRefuseFrg (int type) {
		
		FragmentManager frgMan = getSupportFragmentManager();
		FragmentTransaction ftsd = frgMan.beginTransaction();
		ftsd.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		
		Fragment frg = new CoachComplainOrRefuseFrg(type,mData.teacher_coach_id,customer.id,mData.id);
		
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
			
			CoachAcceptInvite request = new CoachAcceptInvite(CoachInviteOrderDetailActivityNew.this, mData.id,type);
			
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
						
						Toast.makeText(CoachInviteOrderDetailActivityNew.this, R.string.str_start_invite_teaching_success, Toast.LENGTH_SHORT).show();
						mData.status = Const.MY_TEACHING_ACCEPTED;
						refreshUI () ;
						mContext.finish();
						
					/*
					 * 开始教学
					 * */
					} else if (request.type == 1)  {

						refreshUI ();
					}
					
					
				} else {

					Toast.makeText(CoachInviteOrderDetailActivityNew.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
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
			
			StudentRevoketInvite request = new StudentRevoketInvite(CoachInviteOrderDetailActivityNew.this, mData.id);
			
			@Override
			protected Integer doInBackground(Object... params) {
				
				return request.connectUrl();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				WaitDialog.dismissWaitDialog();
				if(BaseRequest.REQ_RET_OK == result) {
					
					Toast.makeText(CoachInviteOrderDetailActivityNew.this, R.string.str_invite_detail_oper_btn_app_revoke_done, Toast.LENGTH_SHORT).show();
					mData.status = Const.MY_TEACHING_REVOKE;
					refreshUI () ;
					mContext.finish();
					
				} else {

					Toast.makeText(CoachInviteOrderDetailActivityNew.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
	}

	/*
	 * 学员撤约
	 * */
	private void StudentPinDanInvite (final int type) {

		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_invite_pin_dan_invite);
		new AsyncTask<Object, Object, Integer>() {

			StudentPinDanInvite request = new StudentPinDanInvite(CoachInviteOrderDetailActivityNew.this, mData.id,customer.id,customer.sn,type);

			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				WaitDialog.dismissWaitDialog();
				if(BaseRequest.REQ_RET_OK == result) {

					mContext.finish();

				}

				Toast.makeText(CoachInviteOrderDetailActivityNew.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();

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
			
			CoachStudentComment request = new CoachStudentComment(CoachInviteOrderDetailActivityNew.this,
					mData.id,
					customer.id,
					mData.teacher_coach_id,
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

					Toast.makeText(CoachInviteOrderDetailActivityNew.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
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
			
			CoachPayCharge request = new CoachPayCharge(CoachInviteOrderDetailActivityNew.this, mData.id,pay_chanel);
			
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

					Toast.makeText(CoachInviteOrderDetailActivityNew.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
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
