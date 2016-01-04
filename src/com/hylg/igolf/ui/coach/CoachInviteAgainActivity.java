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
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.reqparam.CoachInviteReqParam;
import com.hylg.igolf.ui.view.CircleImageView;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.ui.widget.IgTimePickerDialog;
import com.hylg.igolf.ui.widget.IgTimePickerDialog.OnIgTimeSetListener;
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
	
	private TextView                        mNickNameTxt = null,mPhoneTxt,mTeachingTimesTxt;

	private ImageView                       mCustomerHomeImg,mAttentionImg;
	
	private RatingBar                       mRating = null;
	
	private TextView                        mExperinceTxt,mDateSelectTxt,mTimeSelectTxt,mTeachingHoursTxt,mRegionTxt,mCourseSelectTxt;
	
	private EditText                        mQuestionEdit;
	
	
	private CoachInviteReqParam             mReqPara ;
	
	private Customer                        customer;

	private GlobalData 						goGlobalData;

	/*
	*
	*
	* */
	private CoachInviteOrderDetail          mData = null;
	private TextView                        mWhoRefuseTxt = null;
	private ListView 						mRefuseList = null;
	private TextView                        mRefuseContentTxt = null,mInviteCoachTxt = null,mInviteOtherCoachTxt = null;
	private ArrayList<String> 				mSelectedReasonArray = null;
	private complainSelectionAdapter 		mRefuseAdapter;

	private FragmentActivity                mContext;

	private int 							mAttentionState = -1;
	private String                          attention_sn = "";


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
		
		setContentView(R.layout.coach_invite_again_ac);
		
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
		
		mBack = (ImageButton) findViewById(R.id.coach_invite_again_back);
		
		mAvatarImage = (CircleImageView) findViewById(R.id.coach_invite_again_avatar_image);
		
		mNickNameTxt = (TextView) findViewById(R.id.coach_invite_again_name_text);
		mPhoneTxt = (TextView) findViewById(R.id.coach_invite_again_phone_text) ;
		mCustomerHomeImg = (ImageView) findViewById(R.id.coach_invite_again_home_image);
		mAttentionImg = (ImageView) findViewById(R.id.coach_invite_again_attention_image);
		mRating = (RatingBar) findViewById(R.id.coach_invite_again_rating);
		mExperinceTxt = (TextView) findViewById(R.id.coach_invite_again_teaching_time_text);
		
		mDateSelectTxt = (TextView) findViewById(R.id.coach_invite_again_date_text);
		mTimeSelectTxt = (TextView) findViewById(R.id.coach_invite_again_time_text);
		mTeachingHoursTxt = (TextView) findViewById(R.id.coach_invite_again_pre_teach_time_text);
		mRegionTxt = (TextView) findViewById(R.id.coach_invite_again_region_text);
		mCourseSelectTxt = (TextView) findViewById(R.id.coach_invite_again_place_text);
		mQuestionEdit  = (EditText) findViewById(R.id.coach_invite_again_question_edit);

		mWhoRefuseTxt  = (TextView) findViewById(R.id.coach_invite_again_who_refuse_text);
		mRefuseList = (ListView) findViewById(R.id.coach_invite_again_refuse_content_list);
		mRefuseContentTxt = (TextView) findViewById(R.id.coach_invite_again_refuse_content_text);
		mInviteCoachTxt = (TextView) findViewById(R.id.coach_invite_again_commit_text);
		mInviteOtherCoachTxt = (TextView) findViewById(R.id.coach_invite_again_other_text);
		mInviteOtherCoachTxt.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

		mBack.setOnClickListener(this);
		mCustomerHomeImg.setOnClickListener(this);
		mAttentionImg.setOnClickListener(this);
		mPhoneTxt.setOnClickListener(this);
		mDateSelectTxt.setOnClickListener(this);
		mTimeSelectTxt.setOnClickListener(this);
		mTeachingHoursTxt.setOnClickListener(this);
		mCourseSelectTxt.setOnClickListener(this);
		mRegionTxt.setOnClickListener(this);

		mInviteCoachTxt.setOnClickListener(this);
		mInviteOtherCoachTxt.setOnClickListener(this);

		mSelectedReasonArray = new ArrayList<String>();
		if (getIntent() != null && getIntent().getSerializableExtra(BUNDLE_REQ_DATA) != null) {
			
			mData = (CoachInviteOrderDetail) getIntent().getSerializableExtra(BUNDLE_REQ_DATA) ;

			mRating.setRating(mData.teacher_star);
			mExperinceTxt.setText(String.valueOf(mData.teacher_experience));

			customer = MainApp.getInstance().getCustomer();
			mAttentionState = mData.attention;
			if (mData.msg != null && mData.msg.length() > 0) {

				mQuestionEdit.setText(mData.msg);
			}

			mRefuseContentTxt.setText(mData.refuse_content);
			/*当前登陆者是教练*/
			if (customer.sn.equalsIgnoreCase(mData.teacher_sn)) {

				mNickNameTxt.setText(mData.student_name);
				mPhoneTxt.setText(String.valueOf(mData.student_phone));
				loadAvatar(mData.student_sn, mData.student_avatar);

				mWhoRefuseTxt.setText("我说:");
				findViewById(R.id.coach_invite_again_commit_relative).setVisibility(View.GONE);
				findViewById(R.id.coach_invite_again_refuse_content_last).setVisibility(View.GONE);
				mDateSelectTxt.setEnabled(false);
				mTimeSelectTxt.setEnabled(false);
				mTeachingHoursTxt.setEnabled(false);
				mCourseSelectTxt.setEnabled(false);
				mRegionTxt.setEnabled(false);
				mQuestionEdit.setEnabled(false);


		/*当前登录者是学员*/
			} else {

				mWhoRefuseTxt.setText("教练说:");
				mNickNameTxt.setText(mData.teacher_name);
				mPhoneTxt.setText(String.valueOf(mData.teacher_phone));
				loadAvatar(mData.teacher_sn, mData.teacher_avatar);
				mQuestionEdit.setSelection(mQuestionEdit.getText().length());
			}

			//mTeachingTimesTxt.setText(String.valueOf(mCoachItem.teachTimes));
			mReqPara = new CoachInviteReqParam();
			
			mReqPara.studentid = customer.id;
			mReqPara.coachid = mData.teacher_id;
			mReqPara.courseid = mData.course_id;
			//mReqPara.state = mData.course_state;
			//mReqPara.times = mData.times;
			//mReqPara.coachDate = mData.coachDate;
			//mReqPara.coachTime = mData.coachTime;

//			int dateInt = 0;
//			Calendar result = Utils.getCalendar(mData.coachDate+" "+mData.coachTime);
//			Calendar now = Calendar.getInstance();
//			dateInt = (result.get(Calendar.DAY_OF_MONTH)-now.get(Calendar.DAY_OF_MONTH))+1;
//			mReqPara.coachDate = dateInt;
//			mReqPara.coachTime = result.get(Calendar.HOUR_OF_DAY)+":"+result.get(Calendar.MINUTE);

//			mRegionTxt.setText(goGlobalData.getRegionName(mData.course_state));
//			mCourseSelectTxt.setText(mData.course_abbr);
//			mDateSelectTxt.setText(MainApp.getInstance().getGlobalData().getTeeDateName(mReqPara.coachDate));
//			mTimeSelectTxt.setText(mReqPara.coachTime);

			mReqPara.times = 1;
			mTeachingHoursTxt.setText(mReqPara.times + "小时");
		}

		//getCoachRefuseContent();
		
	}
	
	/*
	 * 加载头像
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
						if(null != imageDrawable && null != mAvatarImage) {
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
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		//params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		int scale = (int) this.getResources().getDisplayMetrics().scaledDensity;
		params.height = listAdapter.getCount()*50*scale;
		listView.setLayoutParams(params);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
			case R.id.coach_invite_again_back:

				this.finish();
				break;

			case R.id.coach_invite_again_home_image :

				if (customer.sn.equalsIgnoreCase(mData.teacher_sn)) {

					MemDetailActivityNew.startMemDetailActivity(mContext, mData.student_sn);

				} else {

					MemDetailActivityNew.startMemDetailActivity(mContext, mData.teacher_sn);
				}

				break;

			case R.id.coach_invite_again_attention_image :

				attention();

				break;

			case R.id.coach_invite_again_phone_text :

				String phone = mPhoneTxt.getText().toString();
				if (phone.length() > 0) {

					Intent data = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
					startActivity(data);

				} else {

					Toast.makeText(mContext,R.string.str_toast_invalid_phone,Toast.LENGTH_SHORT).show();
				}

				break;
		
			case R.id.coach_invite_again_date_text :

				TeeDateSelectActivity.startTeeDateSelect(this, false, mReqPara.coachDate);
				break;

			case R.id.coach_invite_again_time_text :

				setTimeForResult();
				break;

			case R.id.coach_invite_again_pre_teach_time_text :

				HourExpSelectActivity.startHourExpSelect(this, mReqPara.times);
				break;

			case R.id.coach_invite_again_region_text :

				RegionSelectActivity.startRegionSelect(this, RegionSelectActivity.REGION_TYPE_SELECT_COURSE, mReqPara.state);
				break;

			case R.id.coach_invite_again_place_text :

				getCourseList();
				break;

			case R.id.coach_invite_again_commit_text :

				commitCoachInvite();

				break;

			case R.id.coach_invite_again_other_text :

				CoachListActivity.startCoachList(mContext, -1);

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
					CoachMyTeachingActivity.startCoachMyTeachingActivity(mContext);
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
		
		mTeachingHoursTxt.setText(newHourExp+"小时");
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
}
