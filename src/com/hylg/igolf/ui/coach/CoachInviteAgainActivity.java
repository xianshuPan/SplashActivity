package com.hylg.igolf.ui.coach;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.hylg.igolf.cs.data.CourseInfo;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.CoachInviteCommit;
import com.hylg.igolf.cs.request.CoachRefuseContent;
import com.hylg.igolf.cs.request.FriendAttentionAdd;
import com.hylg.igolf.cs.request.GetCourseAllInfoList;
import com.hylg.igolf.ui.common.CourseAllSelectActivity;
import com.hylg.igolf.ui.common.CourseAllSelectActivity.onCourseAllSelectListener;
import com.hylg.igolf.ui.common.HourExpSelectActivity;
import com.hylg.igolf.ui.common.HourExpSelectActivity.onHourExpSelectListener;
import com.hylg.igolf.ui.common.RegionSelectActivity;
import com.hylg.igolf.ui.common.TeeDateSelectActivity;
import com.hylg.igolf.ui.common.TeeDateSelectActivity.onTeeDateSelectListener;
import com.hylg.igolf.ui.customer.MyTeachingHomeActivity;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.reqparam.CoachInviteReqParam;
import com.hylg.igolf.ui.view.CircleImageView;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.ui.widget.IgTimePickerDialog;
import com.hylg.igolf.ui.widget.IgTimePickerDialog.OnIgTimeSetListener;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CoachInviteAgainActivity extends FragmentActivity implements
															OnClickListener,
															onTeeDateSelectListener,
															onHourExpSelectListener,
															RegionSelectActivity.onRegionSelectListener,
		onCourseAllSelectListener {
	
	private final String 					TAG = "CoachInviteAgainActivity";
	
	private final static String 			BUNDLE_REQ_DATA = "coach_data";
	
	private ImageButton                     mBack = null;
	private CircleImageView                 mAvatarImage = null;
	private ImageView                       mSexImage = null;
	
	private TextView                        mNickNameTxt = null,mTeachingTimesTxt,mSexpYearTxt;
	
	private RatingBar                       mRating = null;
	
	private TextView                        mExperinceTxt,mDateSelectTxt,mTimeSelectTxt,mTeachingHoursTxt,mRegionTxt,mCourseSelectTxt;

	private RelativeLayout 					mPinDanRelative;
	
	private EditText                        mQuestionEdit;
	
	
	private CoachInviteReqParam             mReqPara ;
	
	private Customer                        customer;

	private GlobalData 						goGlobalData;

	/*
	*
	*
	* */
	private CoachInviteOrderDetail          mData = null;
	private TextView                        mInviteCoachTxt = null;

	private FragmentActivity                mContext;


	private long 							mSelectTime = 0;
	
	
	public static void startCoachInviteAgainActivity(Context context, CoachInviteOrderDetail data) {
		Intent intent = new Intent();
		intent.setClass(context, CoachInviteAgainActivity.class);
		intent.putExtra(BUNDLE_REQ_DATA, data);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Tools.getInstance().setActivityNoTitle(this);
		DebugTools.getDebug().debug_v(TAG, "onCreate..");
		
		setContentView(R.layout.coach_invite_ac_new);
		
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
	
	
	/*初始化控件，及设置相关的事件监听器*/
	private void initUI() {

		mContext = this;

		goGlobalData = MainApp.getInstance().getGlobalData();
		
		mBack = (ImageButton) findViewById(R.id.coach_invite_back);
		mAvatarImage = (CircleImageView) findViewById(R.id.coach_invite_avatar_image);
		mNickNameTxt = (TextView) findViewById(R.id.coach_invite_name_text);
		mSexImage = (ImageView) findViewById(R.id.coach_invite_sex_image);
		mSexpYearTxt  =  (TextView) findViewById(R.id.coach_invite_yearsexp_text);
		mRating = (RatingBar) findViewById(R.id.coach_invite_rating);
		mExperinceTxt = (TextView) findViewById(R.id.coach_invite_teaching_time_text);
		mDateSelectTxt = (TextView) findViewById(R.id.coach_invite_date_text);
		mTimeSelectTxt = (TextView) findViewById(R.id.coach_invite_time_text);
		mTeachingHoursTxt = (TextView) findViewById(R.id.coach_invite_teaching_hour_text);
		mRegionTxt = (TextView) findViewById(R.id.coach_invite_region_text);
		mTeachingTimesTxt = (TextView) findViewById(R.id.coach_invite_teaching_time_text);
		mCourseSelectTxt = (TextView) findViewById(R.id.coach_invite_place_text);
		mQuestionEdit  = (EditText) findViewById(R.id.coach_invite_question_edit);
		mInviteCoachTxt = (TextView) findViewById(R.id.coach_invite_commit_text);
		mPinDanRelative = (RelativeLayout)findViewById(R.id.coach_invite_pin_relative);

		mBack.setOnClickListener(this);
		mDateSelectTxt.setOnClickListener(this);
		mTimeSelectTxt.setOnClickListener(this);
		mTeachingHoursTxt.setOnClickListener(this);
		mCourseSelectTxt.setOnClickListener(this);
		mRegionTxt.setOnClickListener(this);
		mPinDanRelative.setOnClickListener(this);
		mInviteCoachTxt.setOnClickListener(this);
		mAvatarImage.setOnClickListener(this);

		if (getIntent() != null && getIntent().getSerializableExtra(BUNDLE_REQ_DATA) != null) {
			
			mData = (CoachInviteOrderDetail) getIntent().getSerializableExtra(BUNDLE_REQ_DATA) ;

			mNickNameTxt.setText(mData.teacher_name);
			mRating.setRating(mData.teacher_star);
			mExperinceTxt.setText(String.valueOf(mData.teacher_experience));
			DownLoadImageTool.getInstance(this).displayImage(Utils.getAvatarURLString(mData.teacher_sn), mAvatarImage, null);
			//loadAvatar(this,mData.teacher_sn,mAvatarImage);
			mTeachingTimesTxt.setText(String.valueOf(mData.teacher_experience));
			mSexpYearTxt.setText(mData.teacher_ball_age+getResources().getString(R.string.str_year));
			if (mData.teacher_sex == Const.SEX_MALE) {

				mSexImage.setImageResource(R.drawable.man);
			}
			else {

				mSexImage.setImageResource(R.drawable.woman);
			}
			customer = MainApp.getInstance().getCustomer();

			if (mData.msg != null && mData.msg.length() > 0) {

				mQuestionEdit.setText(mData.msg);
			}


			mReqPara = new CoachInviteReqParam();
			mReqPara.studentid = customer.id;
			mReqPara.coachid = mData.teacher_coach_id;
			mReqPara.courseid = mData.course_id;
			//mReqPara.state = mData.course_state;
			//mReqPara.times = mData.times;
			//mReqPara.coachDate = mData.coachDate;
			//mReqPara.coachTime = mData.coachTime;


			mReqPara.times = 1;
			mTeachingHoursTxt.setText(mReqPara.times + "小时");
		}

		
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
			case R.id.coach_invite_back:

				this.finish();
				break;

		
			case R.id.coach_invite_date_text :

				TeeDateSelectActivity.startTeeDateSelect(this, false, mReqPara.coachDate);
				break;

			case R.id.coach_invite_time_text :

				setTimeForResult();
				break;

			case R.id.coach_invite_teaching_hour_text :

				HourExpSelectActivity.startHourExpSelect(this, mReqPara.times);
				break;

			case R.id.coach_invite_region_text :

				RegionSelectActivity.startRegionSelect(this, RegionSelectActivity.REGION_TYPE_SELECT_COURSE, mReqPara.state);
				break;

			case R.id.coach_invite_place_text :

				getCourseList();
				break;

			case R.id.coach_invite_pin_relative:

				if (mPinDanRelative.isSelected()) {

					mPinDanRelative.setSelected(false);
					mReqPara.pin_dan = 0;
				}
				else {

					mPinDanRelative.setSelected(true);
					mReqPara.pin_dan = 1;
				}

				break;

			case R.id.coach_invite_avatar_image:

				MemDetailActivityNew.startMemDetailActivity(this, mData.teacher_sn);
				break;

			case R.id.coach_invite_commit_text :

				commitCoachInvite();

				break;
		}
	}
	
	/*选择时间*/
	private void setTimeForResult() {
		IgTimePickerDialog td = new IgTimePickerDialog(this, System.currentTimeMillis(), true);
		td.setOnIgTimeSetListener(new OnIgTimeSetListener() {
			@Override
			public void OnIgTimeSet(AlertDialog dialog, long date) {
				mReqPara.coachTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(date));

				mSelectTime = date;
				mTimeSelectTxt.setText(mReqPara.coachTime);
			}

		});
		td.show();
	}

	/*
	 * 获取球场信息，并弹出选择框
	 * */
	private void getCourseList() {
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		new AsyncTask<Object, Object, Integer>() {
			GetCourseAllInfoList request = new GetCourseAllInfoList(CoachInviteAgainActivity.this, mReqPara.state);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					CourseAllSelectActivity.startCourseSelect(CoachInviteAgainActivity.this, request.getCourseList());
				} else {
//					if(BaseRequest.REQ_RET_F_NO_DATA == result) { }
					Toast.makeText(CoachInviteAgainActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private void commitCoachInvite() {
		
		if (mReqPara.coachDate <= 0) {
			
			Toast.makeText(this, R.string.str_toast_date_select, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (mReqPara.coachTime == null || mReqPara.coachTime.length() <= 0) {
					
			Toast.makeText(this, R.string.str_toast_time_select, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (mReqPara.times  <= 0) {
			
			Toast.makeText(this, R.string.str_toast_teaching_time_select, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (mReqPara.courseid <= 0) {
			
			Toast.makeText(this, R.string.str_toast_course_select, Toast.LENGTH_SHORT).show();
			return;
		}

		String strEdit = mQuestionEdit.getText().toString();
		if (strEdit.length() <=0 ) {

			Toast.makeText(this, R.string.str_toast_input_invite_msg, Toast.LENGTH_SHORT).show();
			return;
		}
		mReqPara.msg = strEdit;

		Calendar now_time = Calendar.getInstance();

		Calendar select_time = Calendar.getInstance();

		select_time.setTimeInMillis(mSelectTime);

		if (now_time.after(select_time) && mReqPara.coachDate == 1) {

			Toast.makeText(CoachInviteAgainActivity.this,"时间无效",Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_invite_starting_open);
		new AsyncTask<Object, Object, Integer>() {
			
			CoachInviteCommit request = new CoachInviteCommit(CoachInviteAgainActivity.this, mReqPara);
			
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				WaitDialog.dismissWaitDialog();
				if(BaseRequest.REQ_RET_OK == result) {
					
					Toast.makeText(CoachInviteAgainActivity.this, R.string.str_start_invite_coach_success, Toast.LENGTH_SHORT).show();
					//CoachMyTeachingActivity.startCoachMyTeachingActivity(mContext);
					MyTeachingHomeActivity.startCoachMyTeachingHomeActivity(mContext);
					CoachInviteAgainActivity.this.finish();
					
				} else {

					Toast.makeText(CoachInviteAgainActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
	}

	@Override
	public void onTeeDateSelect(int newTeeDate) {
		// TODO Auto-generated method stub
		
		mReqPara.coachDate = newTeeDate;
		mDateSelectTxt.setText(MainApp.getInstance().getGlobalData().getTeeDateName(newTeeDate));
	}

	@Override
	public void onRegionSelect(String newRegion) {

		mReqPara.state = newRegion;
		mRegionTxt.setText(goGlobalData.getRegionName(newRegion));
		// 修改地区后，清楚球场信息
		if(Long.MAX_VALUE != mReqPara.courseid) {

			mReqPara.courseid = -1;
			mCourseSelectTxt.setText(R.string.str_comm_unset);
		}
	}

	@Override
	public void onCourseAllSelect(CourseInfo course) {

		mReqPara.courseid = course.id;
		mCourseSelectTxt.setText(course.abbr);
	}

	@Override
	public void onHourExpSelect(int newHourExp) {
		// TODO Auto-generated method stub
		mReqPara.times = newHourExp;
		
		mTeachingHoursTxt.setText(newHourExp + "小时");
	}
}
