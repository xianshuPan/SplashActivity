package com.hylg.igolf.ui.coach;

import java.util.ArrayList;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachComemntsItem;
import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.GetCoachCommentsListLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.loader.GetCoachCommentsListLoader.GetCoachCommentsCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.FriendAttentionAdd;
import com.hylg.igolf.ui.friend.PhotoViewActivity;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.view.CircleImageView;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class CoachInfoDetailActivity extends FragmentActivity implements OnClickListener{
	
	private final String 					TAG = "CoachInfoDetailActivity";
	
	private final static String 			BUNDLE_REQ_DATA = "coach_data";
	
	private ImageButton                     mBack = null;

	private ScrollView                      mScroll = null;
	private CircleImageView                 mAvatarImage = null;
	
	private TextView                        mNickNameTxt = null,mMetalCountTxt;
	private ImageView                       mSexImage = null;
	
	private RatingBar                       mRating = null;

	
	private RelativeLayout                  mMoreCommentsRelative ;
	
	private TextView                        mCommentsCountTxt ;
	
	private ListView                        mCommentsList;
	
	private ProgressBar                     mProgress;
	
	private TextView                        mCourseName,mCourseAddress,mTeachYearTxt,mTeachCountTxt,mSpecialTxt;

	private ImageView                       mAwardImage;
	
	private TextView                        mInviteCoachTxt;
	
	private CoachItem                       mCoachInfoDetail;

	private GetCoachCommentsListLoader      reqLoader;
	
	private CoachCommentsAdapter            mAdapter = null;


	private Customer                        customer = null;

	private FragmentActivity                mContext = null;

	private ArrayList<String> 				maxPaths = null;
	
	public static void startCoachInfoDetail(Context context, CoachItem data) {
		Intent intent = new Intent();
		intent.setClass(context, CoachInfoDetailActivity.class);
		intent.putExtra(BUNDLE_REQ_DATA, data);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Tools.getInstance().setActivityNoTitle(this);
		DebugTools.getDebug().debug_v(TAG, "onCreate..");
		
		setContentView(R.layout.coach_info_detail_ac);
		
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
		mScroll = (ScrollView) findViewById(R.id.coach_info_detail_scroll);
		mSexImage = (ImageView) findViewById(R.id.coach_info_detail_sex_image);
		mBack = (ImageButton) findViewById(R.id.coach_info_detail_back);
		mAvatarImage = (CircleImageView) findViewById(R.id.coach_info_detail_avatar_image);
		mNickNameTxt = (TextView) findViewById(R.id.coach_info_detail_name_text);
		mRating = (RatingBar) findViewById(R.id.coach_info_detail_rating);
		mMetalCountTxt = (TextView) findViewById(R.id.coach_info_detail_metal_count_text);
		mMoreCommentsRelative  = (RelativeLayout) findViewById(R.id.coach_info_detial_comments_ll);
		mCommentsCountTxt = (TextView) findViewById(R.id.coach_info_detail_comments_count_text);
		mProgress = (ProgressBar) findViewById(R.id.coach_info_detail_comments_progress);
		mCommentsList = (ListView) findViewById(R.id.coach_info_detail_comments_list);
		mCourseName = (TextView) findViewById(R.id.coach_info_detail_place_name_text);
		mCourseAddress = (TextView) findViewById(R.id.coach_info_detail_place_address_text);
		mTeachYearTxt = (TextView) findViewById(R.id.coach_info_detail_teacing_year_txt);
		mTeachCountTxt = (TextView) findViewById(R.id.coach_info_detail_teacing_count_txt);
		mSpecialTxt = (TextView) findViewById(R.id.coach_info_detail_special_edit);
		mAwardImage = (ImageView) findViewById(R.id.coach_info_detail_award_selected_image);
		mInviteCoachTxt = (TextView) findViewById(R.id.coach_info_detail_invite_coach_text);


		mBack.setOnClickListener(this);

		mMoreCommentsRelative.setOnClickListener(this);
		mInviteCoachTxt.setOnClickListener(this);

		mAwardImage.setOnClickListener(this);

		customer = MainApp.getInstance().getCustomer();

		maxPaths = new ArrayList<String>();
		
		if (getIntent() != null && getIntent().getSerializableExtra(BUNDLE_REQ_DATA) != null) {
			
			mCoachInfoDetail = (CoachItem) getIntent().getSerializableExtra(BUNDLE_REQ_DATA);
			
			loadAvatar(mCoachInfoDetail.sn, mCoachInfoDetail.avatar);
			
			mNickNameTxt.setText(mCoachInfoDetail.nickname);
			mRating.setRating(mCoachInfoDetail.rate);
			mCourseName.setText(mCoachInfoDetail.course_name);
			mCourseAddress.setText(mCoachInfoDetail.course_address);
			mTeachYearTxt.setText(String.valueOf(mCoachInfoDetail.teachYear)+"年");
			mTeachCountTxt.setText(String.valueOf(mCoachInfoDetail.teachTimes));
			mSpecialTxt.setText(mCoachInfoDetail.special);

			if (mCoachInfoDetail.award != null && mCoachInfoDetail.award.length() > 0 && !mCoachInfoDetail.award.equalsIgnoreCase("null")) {

				//findViewById(R.id.coach_detail_info_award_relative).setVisibility(View.VISIBLE);
				DownLoadImageTool.getInstance(this).displayImage(BaseRequest.CoachPic_Original_PATH + mCoachInfoDetail.award, mAwardImage, null);
				maxPaths.add(0,BaseRequest.CoachPic_Original_PATH + mCoachInfoDetail.award);
			}

			if (mCoachInfoDetail.graduate != null && mCoachInfoDetail.graduate.length() > 0 && !mCoachInfoDetail.graduate.equalsIgnoreCase("null")) {

				//findViewById(R.id.coach_detail_info_graduate_selected_relative).setVisibility(View.VISIBLE);
				//DownLoadImageTool.getInstance(this).displayImage(BaseRequest.CoachPic_Original_PATH + mCoachInfoDetail.graduate, mGraduateImage, null);
				maxPaths.add(maxPaths.size(),BaseRequest.CoachPic_Original_PATH + mCoachInfoDetail.graduate);
			}

			if (mCoachInfoDetail.certificate != null && mCoachInfoDetail.certificate.length() > 0 && !mCoachInfoDetail.certificate.equalsIgnoreCase("null")) {

				//findViewById(R.id.coach_apply_info_certificate_selected_relative).setVisibility(View.VISIBLE);
				maxPaths.add(maxPaths.size(), BaseRequest.CoachPic_Original_PATH + mCoachInfoDetail.certificate);
				//DownLoadImageTool.getInstance(this).displayImage(BaseRequest.CoachPic_Original_PATH + mCoachInfoDetail.certificate, mCertificateImage, null);
			}

			if (maxPaths.size() > 0) {

				mMetalCountTxt.setText("(已上传"+maxPaths.size()+"项)");
			}
			else {

				findViewById(R.id.coach_apply_metal_info_relative).setVisibility(View.GONE);
			}

			if (mCoachInfoDetail.sex == Const.SEX_MALE) {

				mSexImage.setImageResource(R.drawable.man);
			}
			else {

				mSexImage.setImageResource(R.drawable.woman);
			}

			if (customer.sn.equalsIgnoreCase(mCoachInfoDetail.sn)) {

				findViewById(R.id.coach_info_detail_invite_relative).setVisibility(View.GONE);
				//findViewById(R.id.coach_info_detail_comments_title_right).setVisibility(View.GONE);
				RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mScroll.getLayoutParams();
				lp.setMargins(0, 0, 0, 0);
				mScroll.setLayoutParams(lp);
			}

			findViewById(R.id.coach_info_detail_base_info_relative).setOnClickListener(this);

			initListDataAsync();

		}
		
	}
	
	
	/**
	 *
	 * true: do init the first time, or fail retry.
	 * false: init by change the filter condition.
	 */
	private void initListDataAsync() {
		if(!Utils.isConnected(this)) {
			return ;
		}
		mProgress.setVisibility(View.VISIBLE);
		mMoreCommentsRelative.setEnabled(false);
		clearLoader();
		reqLoader = new GetCoachCommentsListLoader(this, mCoachInfoDetail.id, 1, 2, new GetCoachCommentsCallback() {

			@Override
			public void callBack(int retId, String msg, ArrayList<CoachComemntsItem> commentsList) {
					
					if(BaseRequest.REQ_RET_F_NO_DATA == retId || commentsList.size() == 0) {
						if(msg.trim().length() == 0) {
							msg = getString(R.string.str_golfers_req_no_data_hint);
						}
		
						//Toast.makeText(CoachInfoDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
						mCommentsCountTxt.setText("暂无评价");
						
					} else if (BaseRequest.REQ_RET_OK == retId) {
						
						initListView(commentsList);
						mMoreCommentsRelative.setEnabled(true);
						findViewById(R.id.coach_info_detail_comments_relative).setVisibility(View.VISIBLE);
						
					} else {

						Toast.makeText(CoachInfoDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
					reqLoader = null;
					mProgress.setVisibility(View.GONE);
				}
			
		});
		reqLoader.requestData();
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetCoachCommentsListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	private void initListView(ArrayList<CoachComemntsItem> List) {
		if(null == mAdapter) {
			
			mAdapter = new CoachCommentsAdapter(this,List);
			mCommentsList.setAdapter(mAdapter);
			
		} else {
			
			mAdapter.refreshListInfo(List);
		}
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
							if (null != imageDrawable && null != mAvatarImage) {
								mAvatarImage.setImageDrawable(imageDrawable);
							}
						}
					});
		}
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {

			case R.id.coach_info_detail_back :

				mContext.finish();

				break;
		
			case R.id.coach_info_detail_invite_coach_text:
			
				CoachInviteActivity.startCoachInviteActivity(this, mCoachInfoDetail);
				break;
			
			case R.id.coach_info_detial_comments_ll:
			
				CoachCommentsListActivity.startCoachCommentsListActivity(this, mCoachInfoDetail.id);
				break;

			case R.id.coach_info_detail_award_selected_image:

				Intent data = new Intent(mContext, PhotoViewActivity.class);
				data.putStringArrayListExtra("Photos", maxPaths);
				data.putExtra("Index", 0);
				mContext.startActivity(data);
				break;

			case R.id.coach_info_detail_base_info_relative :

				if (customer.sn.equalsIgnoreCase(mCoachInfoDetail.sn)) {

					CoachApplyInfoActivity.startCoachApplyInfoActivity(this);
				}
				else {

					MemDetailActivityNew.startMemDetailActivity(this,mCoachInfoDetail.sn);
				}

				break;


		}
	}
}
