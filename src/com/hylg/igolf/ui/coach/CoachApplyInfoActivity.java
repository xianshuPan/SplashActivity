package com.hylg.igolf.ui.coach;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CourseInfo;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.CommitCoachInfo;
import com.hylg.igolf.cs.request.GetCoachApplyInfo;
import com.hylg.igolf.cs.request.GetCourseAllInfoList;

import com.hylg.igolf.cs.request.UpdateAvatar;
import com.hylg.igolf.cs.request.UploadImageRequest;
import com.hylg.igolf.ui.common.AgeSelectActivity;
import com.hylg.igolf.ui.common.AgeSelectActivity.onAgeSelectListener;
import com.hylg.igolf.ui.common.CourseAllSelectActivity;
import com.hylg.igolf.ui.common.ImageSelectActivity;
import com.hylg.igolf.ui.common.IndustrySelectActivity;
import com.hylg.igolf.ui.common.IndustrySelectActivity.onIndustrySelectListener;
import com.hylg.igolf.ui.common.ImageSelectActivity.onImageSelectListener;
import com.hylg.igolf.ui.common.RegionSelectActivity;
import com.hylg.igolf.ui.common.SexSelectActivity;
import com.hylg.igolf.ui.common.SexSelectActivity.onSexSelectListener;
import com.hylg.igolf.ui.common.YearsExpSelectActivity;
import com.hylg.igolf.ui.common.YearsExpSelectActivity.onYearsExpSelectListener;
import com.hylg.igolf.ui.reqparam.CoachApplyInfoReqParam;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import org.w3c.dom.Text;

public class CoachApplyInfoActivity extends Activity implements 
												OnClickListener,onSexSelectListener, 
												onAgeSelectListener,onYearsExpSelectListener, 
												onImageSelectListener ,RegionSelectActivity.onRegionSelectListener,
												CourseAllSelectActivity.onCourseAllSelectListener,
		onIndustrySelectListener{
	
	private static final String 				TAG = "CoachInfoActivity";

	private LinearLayout                        mStarLinear = null;
	private RatingBar                           mRatingStar = null;
	private TextView                            mTeachingCountTxt = null,
												mPlaceName = null,
												mPlaceAddress = null;
	
	private TextView                            mStatusTxt = null;
	private ImageView                           mTitleTipsImage = null;
	
	private EditText                            nickNameTxt;
	private TextView 							ageTxt,sexTxt,ballAgeTxt,customerRegionTxt,indsutryTxt,regionTxt,placeTxt,teachAgeTxt;
	
	/*
	 * 选择业余教练还是职业教练
	 * 
	 * */
	private LinearLayout                        mHobbyCoachLinear,mProfessionalCoachLinear;
	private RelativeLayout  					mRootLinear;
	
	/*
	 * 上传省份证相关
	 * */
	private ImageView                           mIDFrondSelectImage,mIDBackSelectImage,
												mIDFrondSelectOkImage,mIDBackSelectOkImage,
												mIDFrondSelectDeleteImage,mIDBackSelectDeleteImage;
	private ProgressBar                         mIDFrondProgress,mIDBackProgress;
	private TextView                            mIDFrondTipsTxt,mIDBackTipsTxt;
	
	/*
	 * 职业教练选填相关项
	 * */
	private ImageView                           mGraduateSelectImage,mCertificateSelectImage,mAwardSelectImage,
												mGraduateSelectOkImage,mCertificateSelectOkImage,mAwardSelectOkImage,
												mGraduateSelectDeleteImage,mCertificateSelectDeleteImage,mAwardSelectDeleteImage;
	private ProgressBar                         mGraduateProgress,mCertificateProgress,mAwardProgress;
	private TextView                            mGraduateTipsTxt,mCertificateTipsTxt,mAwardTipsTxt,mCommiTxt;
	private EditText							mSpecialEdit;
	
	private GlobalData 							goGlobalData;
	private ImageView 							avatarView;
	private Uri 								mUri;
	private Bitmap 								avatarBmp = null;
	private Customer 							customer;
	
	private boolean 							changedFlag = false;
	
	/*
	 * 存储教练信息
	 * */
	private CoachApplyInfoReqParam              reqParam;
	
	
	/*
	 * 表示当前选择的图片是上传头像、还是上传身份证、还是证书
	 * */
	
	private int                                 mCurrentPhotoSelectTypeInt = 0;
	
	private Activity                            mContext;

	private boolean                             isTeachingAgeSelect,isBallAgeSelect,isCustomerRegionSelect,isCourseRegionSelect;

	private  Bitmap avatarBitmap;

	public static void startCoachApplyInfoActivity (Context context) {

		Intent intent1 = new Intent(context, CoachApplyInfoActivity.class);
		context.startActivity(intent1);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DebugTools.getDebug().debug_v(TAG,"----->>>onCreate");
		
		ExitToLogin.getInstance().addActivity(this);
		setContentView(R.layout.coach_apply_ac_info_new);
		getViews();
		
		initMyInfoData();
	}
	
	/*
	 * 获取控件，并设置相关的时间监听器，初始化相关变量，
	 * 
	 * */
	private void getViews(){

		mStarLinear = (LinearLayout) findViewById(R.id.coach_apply_star_info_title);
		mRatingStar = (RatingBar) findViewById(R.id.coach_apply_rating);
		mTeachingCountTxt = (TextView) findViewById(R.id.coach_apply_teacing_count_txt);
		mPlaceAddress = (TextView) findViewById(R.id.coach_apply_info_place_address_text);
		mPlaceName = (TextView) findViewById(R.id.coach_apply_info_place_name_text);

		mRootLinear = (RelativeLayout) findViewById(R.id.coach_applay_root_linear);
		mStatusTxt = (TextView) findViewById(R.id.coach_applay_info_status_text);
		mTitleTipsImage = (ImageView) findViewById(R.id.coach_applay_info_title_tips_image);
		nickNameTxt = (EditText) findViewById(R.id.coach_apply_info_name_selection);
		ageTxt = (TextView) findViewById(R.id.coach_apply_info_age_selection);
		sexTxt = (TextView) findViewById(R.id.coach_apply_info_sex_selection);
		ballAgeTxt = (TextView) findViewById(R.id.coach_apply_info_ball_age_selection);
		customerRegionTxt = (TextView) findViewById(R.id.coach_apply_info_custome_region_selection);
		indsutryTxt = (TextView) findViewById(R.id.coach_apply_info_industry_selection);
		mSpecialEdit = (EditText) findViewById(R.id.coach_apply_info_special_edit);
		placeTxt = (TextView) findViewById(R.id.coach_apply_info_place_selection);
		regionTxt = (TextView) findViewById(R.id.coach_apply_info_region_selection);
		teachAgeTxt = (TextView) findViewById(R.id.coach_apply_info_teach_age_selection);

		avatarView = (ImageView) findViewById(R.id.coach_apply_info_avtar);
		mIDFrondProgress = (ProgressBar) findViewById(R.id.coach_apply_info_id_front_progress);
		mIDBackProgress = (ProgressBar) findViewById(R.id.coach_apply_info_id_back_progress);
		mAwardProgress = (ProgressBar) findViewById(R.id.coach_apply_info_award_progress);
		mGraduateProgress = (ProgressBar) findViewById(R.id.coach_apply_info_graduate_progress);
		mCertificateProgress = (ProgressBar) findViewById(R.id.coach_apply_info_certificate_progress);
		
		mIDFrondSelectImage = (ImageView) findViewById(R.id.coach_apply_info_id_front_selected_image);
		mIDBackSelectImage = (ImageView) findViewById(R.id.coach_apply_info_id_back_selected_image);
		mCertificateSelectImage = (ImageView) findViewById(R.id.coach_apply_info_certificate_selected_image);
		mGraduateSelectImage = (ImageView) findViewById(R.id.coach_apply_info_graduate_selected_image);
		mAwardSelectImage = (ImageView) findViewById(R.id.coach_apply_info_award_selected_image);
		
		mIDFrondSelectOkImage = (ImageView) findViewById(R.id.coach_apply_info_id_front_selected_ok_image);
		mIDBackSelectOkImage = (ImageView) findViewById(R.id.coach_apply_info_id_back_selected_ok_image);
		mCertificateSelectOkImage = (ImageView) findViewById(R.id.coach_apply_info_certificate_selected_ok_image);
		mGraduateSelectOkImage = (ImageView) findViewById(R.id.coach_apply_info_graduate_selected_ok_image);
		mAwardSelectOkImage = (ImageView) findViewById(R.id.coach_apply_info_award_selected_ok_image);

		mIDFrondSelectDeleteImage = (ImageView) findViewById(R.id.coach_apply_info_id_front_selected_delete_image);
		mIDBackSelectDeleteImage = (ImageView) findViewById(R.id.coach_apply_info_id_back_selected_delete_image);
		mCertificateSelectDeleteImage = (ImageView) findViewById(R.id.coach_apply_info_certificate_selected_delete_image);
		mGraduateSelectDeleteImage = (ImageView) findViewById(R.id.coach_apply_info_graduate_selected_delete_image);
		mAwardSelectDeleteImage = (ImageView) findViewById(R.id.coach_apply_info_award_selected_delete_image);
		
		mHobbyCoachLinear = (LinearLayout) findViewById(R.id.coach_apply_info_hobby_linear);
		mHobbyCoachLinear.setSelected(true);
		mProfessionalCoachLinear = (LinearLayout) findViewById(R.id.coach_apply_info_professional_linear);

		mCommiTxt = (TextView) findViewById(R.id.coach_apply_info_commit_text);
		
		findViewById(R.id.coach_applay_info_back).setOnClickListener(this);
		findViewById(R.id.coach_applay_info_title_tips_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_avtar_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_age_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_sex_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_ball_age_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_custome_region_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_industry_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_name_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_hobby_linear).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_professional_linear).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_id_front_selected_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_id_back_selected_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_special_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_place_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_region_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_teach_age_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_graduate_selected_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_certificate_selected_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_award_selected_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_commit_text).setOnClickListener(this);

		mIDFrondSelectDeleteImage.setOnClickListener(this);
		mIDBackSelectDeleteImage.setOnClickListener(this);
		mCertificateSelectDeleteImage.setOnClickListener(this);
		mGraduateSelectDeleteImage.setOnClickListener(this);
		mAwardSelectDeleteImage.setOnClickListener(this);
		//mCommiTxt.setOnClickListener(this);
		
		mSpecialEdit.setFocusable(true);

		nickNameTxt.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub

				if (arg1) {

					nickNameTxt.setBackgroundResource(R.drawable.frame_bkg);

				} else {

					nickNameTxt.setBackgroundColor(getResources().getColor(R.color.color_white));
				}
			}
		});

		
		mContext = this;
		mCommiTxt.setEnabled(false);
		
		customer = MainApp.getInstance().getCustomer();
		
		/*
		 * 初始化变量，默认是业余教练
		 * 
		 * */
		reqParam = new CoachApplyInfoReqParam();
		
		/*
		 * 获取我的教练信息
		 * 
		 * */
		getCoachApplyInfo();
		
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.coach_applay_info_back:
				finishWithAnim();
				break;
				
			case R.id.coach_applay_info_title_tips_image :
				
				showDesPopWin() ;
				
				break;
				
			case R.id.coach_apply_info_sex_ll:
				SexSelectActivity.startSexSelect(this, false, reqParam.sex);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
				
			case R.id.coach_apply_info_teach_age_ll:

				isTeachingAgeSelect = true;
				isBallAgeSelect = false;
				YearsExpSelectActivity.startYearsExpSelect(this, reqParam.teach_age);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;


			case R.id.coach_apply_info_ball_age_ll:

				isTeachingAgeSelect = false;
				isBallAgeSelect = true;
				YearsExpSelectActivity.startYearsExpSelect(this, reqParam.ball_age);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;

			case R.id.coach_apply_info_industry_ll:

				IndustrySelectActivity.startIndustrySelect(this,true,reqParam.industry);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;

			case R.id.coach_apply_info_age_ll:
				AgeSelectActivity.startAgeSelect(this, reqParam.age);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;

			case R.id.coach_apply_info_region_ll:

				isCourseRegionSelect = true;
				isCustomerRegionSelect = false;
				RegionSelectActivity.startRegionSelect(mContext, RegionSelectActivity.REGION_TYPE_SELECT_COURSE, reqParam.state);
				break;

			case R.id.coach_apply_info_custome_region_ll:

				isCourseRegionSelect = false;
				isCustomerRegionSelect = true;
				RegionSelectActivity.startRegionSelect(mContext, RegionSelectActivity.REGION_TYPE_SELECT_COURSE, reqParam.customer_region);
				break;
				
			case R.id.coach_apply_info_place_ll:
				
				setupCourse();
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
				
			case R.id.coach_apply_info_hobby_linear:
				
				mHobbyCoachLinear.setSelected(true);
				mProfessionalCoachLinear.setSelected(false);
				//mProfessionalSelectInputRelative.setVisibility(View.GONE);
				reqParam.type = Const.HOBBY_COACH;
//				findViewById(R.id.coach_apply_info_professional_tips_text).setVisibility(View.GONE);
				findViewById(R.id.coach_apply_info_graduate_selected_relative).setVisibility(View.GONE);
				findViewById(R.id.coach_apply_info_certificate_selected_relative).setVisibility(View.GONE);

				break;
				
			case R.id.coach_apply_info_professional_linear:
				
				mHobbyCoachLinear.setSelected(false);
				mProfessionalCoachLinear.setSelected(true);
				//mProfessionalSelectInputRelative.setVisibility(View.VISIBLE);
				reqParam.type = Const.PROFESSIONAL_COACH;
//				findViewById(R.id.coach_apply_info_professional_tips_text).setVisibility(View.VISIBLE);
//				findViewById(R.id.coach_apply_info_graduate_selected_relative).s

				findViewById(R.id.coach_apply_info_graduate_selected_relative).setVisibility(View.VISIBLE);
				findViewById(R.id.coach_apply_info_certificate_selected_relative).setVisibility(View.VISIBLE);
				
				break;
				
			case R.id.coach_apply_info_avtar_ll:
				ImageSelectActivity.startImageSelect(this);
				mCurrentPhotoSelectTypeInt = Const.AVATAR;
				break;
				
			case R.id.coach_apply_info_id_front_selected_image:
				
				if (mIDFrondProgress.getVisibility() == View.GONE) {
					
					ImageSelectActivity.startImageSelect(this);
					mCurrentPhotoSelectTypeInt = Const.ID_FRONT;
				}
				
				break;
				
			case R.id.coach_apply_info_id_back_selected_image:
				
				if (mIDBackProgress.getVisibility() == View.GONE) {
					
					ImageSelectActivity.startImageSelect(this);
					mCurrentPhotoSelectTypeInt = Const.ID_BACK;
				}
				
				break;
				
			case R.id.coach_apply_info_graduate_selected_image:
				
				if (mGraduateProgress.getVisibility() == View.GONE) {
					
					ImageSelectActivity.startImageSelect(this);
					mCurrentPhotoSelectTypeInt = Const.GRADUATE;
				}
				
				break;
				
			case R.id.coach_apply_info_certificate_selected_image:
				
				if (mCertificateProgress.getVisibility() == View.GONE) {
					
					ImageSelectActivity.startImageSelect(this);
					mCurrentPhotoSelectTypeInt = Const.CERTIFICATE;
				}
				
				break;
				
			case R.id.coach_apply_info_award_selected_image:
				
				if (mAwardProgress.getVisibility() == View.GONE) {
					
					ImageSelectActivity.startImageSelect(this);
					mCurrentPhotoSelectTypeInt = Const.AWARD;
				}
				
				break;

			case R.id.coach_apply_info_id_front_selected_delete_image:

				reqParam.id_fron_name = "";
				mIDFrondSelectImage.setImageResource(R.drawable.id_front);
				mIDFrondSelectDeleteImage.setVisibility(View.GONE);
				mIDFrondSelectOkImage.setVisibility(View.GONE);

				break;

			case R.id.coach_apply_info_id_back_selected_delete_image:

				reqParam.id_back_name = "";
				mIDBackSelectImage.setImageResource(R.drawable.id_back);
				mIDBackSelectDeleteImage.setVisibility(View.GONE);
				mIDBackSelectOkImage.setVisibility(View.GONE);

				break;

			case R.id.coach_apply_info_certificate_selected_delete_image:

				reqParam.certificate_name = "";
				mCertificateSelectImage.setImageResource(R.drawable.coach_add_icon);
				mCertificateSelectDeleteImage.setVisibility(View.GONE);
				mCertificateSelectOkImage.setVisibility(View.GONE);

				break;

			case R.id.coach_apply_info_award_selected_delete_image:

				reqParam.award_name = "";
				mAwardSelectImage.setImageResource(R.drawable.coach_add_icon);
				mAwardSelectDeleteImage.setVisibility(View.GONE);
				mAwardSelectOkImage.setVisibility(View.GONE);

				break;

			case R.id.coach_apply_info_graduate_selected_delete_image:

				reqParam.graduate_name = "";
				mGraduateSelectImage.setImageResource(R.drawable.coach_add_icon);
				mGraduateSelectDeleteImage.setVisibility(View.GONE);
				mGraduateSelectOkImage.setVisibility(View.GONE);

				break;


				
			case R.id.coach_apply_info_commit_text:

				commitCoachApplyInfo();
				
				break;
				
				
		}
	}
	
	/*
	 * 弹出审核失败的，原因对话框
	 * */
	private PopupWindow mDesPopWin;
	private void showDesPopWin() {
		if(null != mDesPopWin && mDesPopWin.isShowing()) {
			return ;
		}
		LinearLayout sv = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.rank_hint_popwin, null);
		
		TextView tipsTxt = (TextView)sv.findViewById(R.id.tips_text);
		
		tipsTxt.setText(reqParam.auditString);
		mDesPopWin = new PopupWindow(sv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		mDesPopWin.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_gray));
		mDesPopWin.setAnimationStyle(android.R.style.Animation_Dialog);
		sv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissExchgPopwin();
			}
		});
		mDesPopWin.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if(null != mDesPopWin) {
					Utils.logh(TAG, "onDismiss ");
					mDesPopWin = null;
				}
			}
		});
		mDesPopWin.showAtLocation(sv, Gravity.CENTER, 0, 0);
	}
	
	private void dismissExchgPopwin() {
		if(null != mDesPopWin && mDesPopWin.isShowing()) {
			mDesPopWin.dismiss();
		}
	}
	
	/*
	 * 初始化显示
	 * */
	private void initMyInfoData() {
		
		loadAvatar(customer.sn,customer.avatar);
		goGlobalData = MainApp.getInstance().getGlobalData();
		
	}
	
	private void loadAvatar(String sn,String filename){
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(this, sn, filename,
				(int) getResources().getDimension(R.dimen.avatar_detail_size));
		if(null != avatar) {
			avatarView.setImageDrawable(avatar);
		} else {
			avatarView.setImageResource(R.drawable.avatar_loading);
			AsyncImageLoader.getInstance().loadAvatar(this, sn, filename,
				new ImageCallback() {
					@Override
					public void imageLoaded(Drawable imageDrawable) {
						if(null != imageDrawable && null != avatarView) {
							avatarView.setImageDrawable(imageDrawable);
						}
					}
			});
		}
	}
	
	
	private void finishWithAnim() {
		if(changedFlag) {
			Intent intent = new Intent();
			intent.putExtra(Const.BUNDLE_KEY_MY_INFO_CHANGED, true);
			setResult(RESULT_OK, intent);
		}
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}
	
	protected void toastShort(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	private void setupCourse() {
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		new AsyncTask<Object, Object, Integer>() {
			GetCourseAllInfoList request = new GetCourseAllInfoList(mContext, reqParam.state);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					CourseAllSelectActivity.startCourseSelect(mContext, request.getCourseList());
				} else {
//					if(BaseRequest.REQ_RET_F_NO_DATA == result) { }
					Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
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

	@Override
	public void onYearsExpSelect(int newYearsExp) {
//		doModifyCustomerInfo(RequestParam.PARAM_REQ_YEARSEXP_STR,String.valueOf(newYearsExp),teachAgeTxt,
//				String.format(getString(R.string.str_account_yearsexp_wrap), newYearsExp));
		
		//customer.yearsExpStr = String.valueOf(newYearsExp);


		if (isTeachingAgeSelect) {

			teachAgeTxt.setText(String.format(getString(R.string.str_account_yearsexp_wrap), newYearsExp));
			reqParam.teach_age = newYearsExp;
		}

		if (isBallAgeSelect) {

			ballAgeTxt.setText(String.format(getString(R.string.str_account_yearsexp_wrap), newYearsExp));
			reqParam.ball_age = newYearsExp;
		}


	}

	
	@Override
	public void onAgeSelect(int newAge) {
//		doModifyCustomerInfo(RequestParam.PARAM_REQ_AGE,String.valueOf(newAge),
//				ageTxt,String.format(getString(R.string.str_account_age_wrap), newAge));
		//customer.age = String.valueOf(newAge);

		reqParam.age = newAge;
		ageTxt.setText(String.format(getString(R.string.str_account_age_wrap), newAge));
	}


	@Override
	public void onSexSelect(int newSex) {
//		doModifyCustomerInfo(RequestParam.PARAM_REQ_SEX,String.valueOf(newSex),
//				sexTxt,goGlobalData.getSexName(newSex));

		sexTxt.setText(goGlobalData.getSexName(newSex));
		reqParam.sex = newSex;
	}

	@Override
	public void onRegionSelect(String newRegion) {

		if (isCourseRegionSelect) {

			reqParam.state = newRegion;
			regionTxt.setText(goGlobalData.getRegionName(reqParam.state));
			// 修改地区后，清楚球场信息
			if(Long.MAX_VALUE != reqParam.courseid) {

				reqParam.courseid = -1;
				placeTxt.setText(R.string.str_comm_unset);
			}
		}

		if (isCustomerRegionSelect) {

			reqParam.customer_region = newRegion;
			customerRegionTxt.setText(goGlobalData.getRegionName(reqParam.customer_region));
		}

	}


	@Override
	public void onCourseAllSelect(CourseInfo course) {

		reqParam.courseid = course.id;
		placeTxt.setText(course.abbr);
		mPlaceName.setText(course.name);
		mPlaceAddress.setText(course.address);
	}

	@Override
	public void onIndustrySelect(String newIndustry) {

		reqParam.industry = newIndustry;
		indsutryTxt.setText(goGlobalData.getIndustryName(newIndustry));
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, " ::: requestCode: " + requestCode + " resultCode: " + resultCode + " intent: " + intent);
		if(Const.REQUST_CODE_PHOTE_TAKE_PHOTO == requestCode) {
			if(RESULT_OK == resultCode) {

				if (mCurrentPhotoSelectTypeInt == Const.AVATAR) {

					//startPhotoCrop(mUri);

					doModifyAvatar(mUri,customer.sn);

				} else {

					doUploadFile(mUri);
				}

				return ;
			}
		} else if(Const.REQUST_CODE_PHOTE_GALLERY == requestCode) {
			if(RESULT_OK == resultCode && null != intent) {
				if (mCurrentPhotoSelectTypeInt == Const.AVATAR) {

					//startPhotoCrop(intent.getData());
					doModifyAvatar(intent.getData(),customer.sn);
				} else {

					doUploadFile(intent.getData());
				}

				return ;
			}
		} else if(Const.REQUST_CODE_PHOTE_CROP == requestCode) {
			if(RESULT_OK == resultCode && null != intent) {
				Bundle b = intent.getExtras();
				if(null != b) {
					Bitmap photo = b.getParcelable("data");
					if(null != photo) {
						
						avatarBmp = photo;
						
						switch (mCurrentPhotoSelectTypeInt) {
						
						case  Const.AVATAR:
							
							doModifyAvatar(avatarBmp, customer.sn);
							
							break;
						
						}
						
						return ;
						
					} else {
						Log.w(TAG, "photo null ! ");
					}
				} else {
					Log.w(TAG, "intent.getExtras() null ! ");
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	/*
	 * 修改教练信息
	 * */
//	private void doModifyCustomerInfo(final String key,final String value,final TextView textView,final String textValue){
//		WaitDialog.showWaitDialog(this,
//				R.string.str_loading_modify_msg);
//		if(!Utils.isConnected(this)){
//			return;
//		}
//		new AsyncTask<Object, Object, Integer>() {
//			ModifyCustomer request = new ModifyCustomer(mContext, customer.sn, key, value);
//			@Override
//			protected Integer doInBackground(Object... params) {
//				return request.connectUrl();
//			}
//			@Override
//			protected void onPostExecute(Integer result) {
//				super.onPostExecute(result);
//				if(BaseRequest.REQ_RET_OK == result) {
//					textView.setText(textValue);
//					changedFlag = true;
//				} else {
//					Toast.makeText(mContext,request.getFailMsg(), Toast.LENGTH_SHORT).show();
//				}
//				WaitDialog.dismissWaitDialog();
//			}
//		}.execute(null, null, null);
//	}

	void doUploadFile (Uri uri) {


		switch (mCurrentPhotoSelectTypeInt) {

			case  Const.ID_FRONT:

				doUploadPhoto(uri, mCurrentPhotoSelectTypeInt,"recieveIdCard", "idCards",
						mIDFrondSelectOkImage,mIDFrondSelectDeleteImage,mIDFrondProgress,mIDFrondSelectImage);
				break;

			case  Const.ID_BACK:

				doUploadPhoto(uri, mCurrentPhotoSelectTypeInt,"recieveIdCard", "idCards",
						mIDBackSelectOkImage,mIDBackSelectDeleteImage,mIDBackProgress,mIDBackSelectImage);

				break;

			case  Const.GRADUATE:

				doUploadPhoto(uri, mCurrentPhotoSelectTypeInt,"recieveOtherCards", "otherCards",
						mGraduateSelectOkImage,mGraduateSelectDeleteImage,mGraduateProgress,mGraduateSelectImage);

				break;

			case  Const.CERTIFICATE:

				doUploadPhoto(uri, mCurrentPhotoSelectTypeInt,"recieveOtherCards", "otherCards",
						mCertificateSelectOkImage,mCertificateSelectDeleteImage,mCertificateProgress,mCertificateSelectImage);

				break;

			case  Const.AWARD	:

				doUploadPhoto(uri, mCurrentPhotoSelectTypeInt,"recieveOtherCards", "otherCards",
						mAwardSelectOkImage,mAwardSelectDeleteImage,mAwardProgress,mAwardSelectImage);

				break;
		}

	}
	
	/*
	 * 启动裁减功能
	 * */
	private void startPhotoCrop(Uri uri) {
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = width*5/8;

		Utils.logh(TAG, "startPhotoCrop width: " + width + " height: " + height);
		
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", width);
		intent.putExtra("aspectY", height);
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		intent.putExtra("return-data", true);

		startActivityForResult(intent, Const.REQUST_CODE_PHOTE_CROP);
	}
	
	/*
	 * 执行修改头像
	 * */
	private void doModifyAvatar(final Bitmap bitmap, final String sn) {
		if(!Utils.isConnected(this)){
			return;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_modify_msg);
		
		new AsyncTask<Object, Object, Integer>() {
			UpdateAvatar request = new UpdateAvatar(mContext, bitmap, sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					AsyncImageLoader.getInstance().clearOverDueCache(sn, 
							FileUtils.getAvatarPathBySn(mContext, sn, customer.avatar));
//					loadNewAvatar(sn);
					String avatar = request.getImageName();
					customer.avatar = avatar;
					SharedPref.setString(SharedPref.SPK_AVATAR, avatar, mContext);
//					loadAvatar(customer.sn, avatar);//修改成功,重新加载头像,同步客户端内存图片
					avatarView.setImageBitmap(bitmap);
					changedFlag = true;
					
					reqParam.avatar = avatar;
					
				} else {
					
					Toast.makeText(mContext,request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

	/*
	 * 执行修改头像
	 * */
	private void doModifyAvatar(final Uri uri, final String sn) {

		try {
			avatarBitmap = Media.getBitmap(mContext.getContentResolver(), uri);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);

			float zoom = (float)Math.sqrt(128 * 1024 / (float)out.toByteArray().length);

			Matrix matrix = new Matrix();
			matrix.setScale(zoom, zoom);

			Bitmap result = Bitmap.createBitmap(avatarBitmap, 0, 0, avatarBitmap.getWidth(), avatarBitmap.getHeight(), matrix, true);

			result.compress(Bitmap.CompressFormat.JPEG, 85, out);

			while(out.toByteArray().length > 128 * 1024){
				System.out.println(out.toByteArray().length);
				matrix.setScale(0.9f, 0.9f);
				result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
				out.reset();
				result.compress(Bitmap.CompressFormat.JPEG, 85, out);
			}

			avatarBitmap = result;



			if(!Utils.isConnected(this)){
				return;
			}
			WaitDialog.showWaitDialog(this,R.string.str_loading_modify_msg);

			new AsyncTask<Object, Object, Integer>() {
				UpdateAvatar request = new UpdateAvatar(mContext, avatarBitmap, sn);
				@Override
				protected Integer doInBackground(Object... params) {
					return request.connectUrl();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					if(BaseRequest.REQ_RET_OK == result) {
						AsyncImageLoader.getInstance().clearOverDueCache(sn,
								FileUtils.getAvatarPathBySn(mContext, sn, customer.avatar));
//					loadNewAvatar(sn);
						String avatar = request.getImageName();
						customer.avatar = avatar;
						SharedPref.setString(SharedPref.SPK_AVATAR, avatar, mContext);
//					loadAvatar(customer.sn, avatar);//修改成功,重新加载头像,同步客户端内存图片
						avatarView.setImageBitmap(avatarBitmap);
						changedFlag = true;

						reqParam.avatar = avatar;

					} else {

						Toast.makeText(mContext,request.getFailMsg(), Toast.LENGTH_SHORT).show();
					}
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
	
	/*
	 * 开启上传图片功能，
	 * @ para bitmap 经过裁减的要上传的图片
	 * @ para methodname 表示 要上传的身份证，还是其他图片（高尔夫毕业证，奖状等） 
	 * @ para okImage 上传图片之后，显示成功
	 * @ para progress 上传图片的进度显示
	 * @ para selectedImage 上传图片成功之后，显示图片
	 * */
	private void doUploadPhoto(final Uri uri,final int type,final String methodName,final String bodyName,
								final ImageView okImage,final ImageView deleteImage,final ProgressBar progress,final ImageView selectedImage) {
		if(!Utils.isConnected(this)){
			return;
		}
		//WaitDialog.showWaitDialog(this,R.string.str_loading_modify_msg);
		
		final String fileName =  "android_"+customer.sn+"_"+ System.currentTimeMillis()+".jpg";
		
		new AsyncTask<Object, Object, Integer>() {
			UploadImageRequest request = new UploadImageRequest(mContext,uri,type,methodName,bodyName,fileName,okImage,deleteImage,progress,selectedImage) ;
			@Override
			protected Integer doInBackground(Object... params) {
				
				return request.connectUrl();
			}
			
			@Override
			protected void onPreExecute() {
				
				request.progress.setVisibility(View.VISIBLE);
				request.okImageView.setVisibility(View.GONE);
				request.deleteImageView.setVisibility(View.GONE);
				
				super.onPreExecute();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				request.progress.setVisibility(View.GONE);
				
				if(BaseRequest.REQ_RET_OK == result) {

					request.okImageView.setVisibility(View.VISIBLE);
					request.deleteImageView.setVisibility(View.VISIBLE);
					request.selectedImage.setImageBitmap(request.bitmap);
					
					switch (request.type) {
					
					case  Const.AVATAR:
						
						doModifyAvatar(avatarBmp, customer.sn);
						
						break;
						
					case  Const.ID_FRONT:
						
						reqParam.id_fron_name = fileName;
						break;
						
					case  Const.ID_BACK:
						
						reqParam.id_back_name = fileName;
						
						break;
						
					case  Const.GRADUATE:
						
						reqParam.graduate_name = fileName;
						
						break;
						
					case  Const.CERTIFICATE:
						
						reqParam.certificate_name = fileName;
						
						break;
						
					case  Const.AWARD:
						
						reqParam.award_name = fileName;
						
						break;
					
					}

				} else {
					
					Toast.makeText(mContext,request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	/*
	 * 提交教练信息
	 * */
	private void commitCoachApplyInfo() {
		if(!Utils.isConnected(this)){
			return;
		}
	
		reqParam.special = mSpecialEdit.getText().toString();
		
		if (!verifyUserInput()) {
			
			return ;
		}
		
		WaitDialog.showWaitDialog(this,R.string.str_loading_modify_msg);
		new AsyncTask<Object, Object, Integer>() {
			
			CommitCoachInfo request = new CommitCoachInfo(mContext,customer.id,reqParam);
			@Override
			protected Integer doInBackground(Object... params) {
				
				return request.connectUrl();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				if(BaseRequest.REQ_RET_OK == result) {

					mContext.finish();

					MainApp.getInstance().getCustomer().nickname = reqParam.name;
					MainApp.getInstance().getCustomer().age = reqParam.age;
					MainApp.getInstance().getCustomer().sex = reqParam.sex;

				} else {
					
					Toast.makeText(mContext,request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	/*
	 * 获取我的教练信息
	 * */
	private void getCoachApplyInfo() {
		if(!Utils.isConnected(this)){
			return;
		}
		
		WaitDialog.showWaitDialog(this,R.string.str_loading_modify_msg);
		
		new AsyncTask<Object, Object, Integer>() {
			
			GetCoachApplyInfo request = new GetCoachApplyInfo(mContext, customer.id);
			@Override
			protected Integer doInBackground(Object... params) {
				
				return request.connectUrl();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				if(BaseRequest.REQ_RET_OK == result) {

					/*
					 * 已经注册成为教练
					 * */
					if (request.item != null && request.item.id > 0) {
						
						reqParam = request.item;

						loadAvatar(reqParam.sn, reqParam.avatar);

						sexTxt.setText(goGlobalData.getSexName(reqParam.sex));
						ballAgeTxt.setText(String.format(getString(R.string.str_account_yearsexp_wrap), reqParam.ball_age));
						customerRegionTxt.setText(goGlobalData.getRegionName(reqParam.customer_region));
						indsutryTxt.setText(goGlobalData.getIndustryName(reqParam.industry));
						ageTxt.setText(reqParam.age_str);
						nickNameTxt.setText(reqParam.name);
						placeTxt.setText(reqParam.course_abbr);
						mPlaceName.setText(reqParam.course_name);
						mPlaceAddress.setText(reqParam.course_address);
						regionTxt.setText(goGlobalData.getRegionName(reqParam.state));
						teachAgeTxt.setText(String.format(getString(R.string.str_account_yearsexp_wrap), reqParam.teach_age));
						mSpecialEdit.setText(reqParam.special);

						mStarLinear.setVisibility(View.VISIBLE);
						mRatingStar.setRating(reqParam.star);
						mTeachingCountTxt.setText(String.valueOf(reqParam.teacing_count));
						
						if(reqParam.type == Const.PROFESSIONAL_COACH) {
							
							mProfessionalCoachLinear.setSelected(true);
							mHobbyCoachLinear.setSelected(false);
							//mProfessionalSelectInputRelative.setVisibility(View.VISIBLE);
							findViewById(R.id.coach_apply_info_graduate_selected_relative).setVisibility(View.VISIBLE);
							findViewById(R.id.coach_apply_info_certificate_selected_relative).setVisibility(View.VISIBLE);
							//findViewById(R.id.coach_apply_info_professional_tips_text).setVisibility(View.VISIBLE);
							
						} else {
							
							mProfessionalCoachLinear.setSelected(false);
							mHobbyCoachLinear.setSelected(true);
							//mProfessionalSelectInputRelative.setVisibility(View.GONE);
							findViewById(R.id.coach_apply_info_graduate_selected_relative).setVisibility(View.GONE);
							findViewById(R.id.coach_apply_info_certificate_selected_relative).setVisibility(View.GONE);
							//findViewById(R.id.coach_apply_info_professional_tips_text).setVisibility(View.GONE);
						}
						
						DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.id_fron_name, mIDFrondSelectImage, null);
						DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.id_back_name, mIDBackSelectImage, null);

						if (reqParam.award_name != null && reqParam.award_name.length() > 0 && !reqParam.award_name.equalsIgnoreCase("null")) {

							DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.award_name, mAwardSelectImage, null);

						} else {

							//findViewById(R.id.coach_apply_info_award_relative).setVisibility(View.GONE);
						}

						if (reqParam.certificate_name != null && reqParam.certificate_name.length() > 0 && !reqParam.certificate_name.equalsIgnoreCase("null")) {

							DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.certificate_name, mCertificateSelectImage, null);

						} else {

							//findViewById(R.id.coach_apply_info_certificate_selected_relative).setVisibility(View.GONE);
						}

						if (reqParam.graduate_name != null && reqParam.graduate_name.length() > 0 && !reqParam.graduate_name.equalsIgnoreCase("null")) {

							DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.graduate_name, mGraduateSelectImage, null);

						} else {

							//findViewById(R.id.coach_apply_info_graduate_selected_relative).setVisibility(View.GONE);
						}

//						DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.certificate_name, mCertificateSelectImage, null);
//						DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.graduate_name, mGraduateSelectImage, null);
						
						
						mStatusTxt.setVisibility(View.VISIBLE);
						if(reqParam.audit == 0) {
							
							mStatusTxt.setText(R.string.str_coach_apply_status_waiting);
							mCommiTxt.setEnabled(false);
							mRootLinear.setEnabled(false);
							findViewById(R.id.coach_apply_info_avtar_ll).setEnabled(false);
							findViewById(R.id.coach_apply_info_age_ll).setEnabled(false);
							findViewById(R.id.coach_apply_info_sex_ll).setEnabled(false);
							findViewById(R.id.coach_apply_info_name_ll).setEnabled(false);
							findViewById(R.id.coach_apply_info_hobby_linear).setEnabled(false);
							findViewById(R.id.coach_apply_info_professional_linear).setEnabled(false);
							findViewById(R.id.coach_apply_info_id_front_selected_image).setEnabled(false);
							findViewById(R.id.coach_apply_info_id_back_selected_image).setEnabled(false);
							findViewById(R.id.coach_apply_info_special_ll).setEnabled(false);
							findViewById(R.id.coach_apply_info_place_ll).setEnabled(false);
							findViewById(R.id.coach_apply_info_region_ll).setEnabled(false);
							findViewById(R.id.coach_apply_info_teach_age_ll).setEnabled(false);
							findViewById(R.id.coach_apply_info_graduate_selected_image).setEnabled(false);
							findViewById(R.id.coach_apply_info_certificate_selected_image).setEnabled(false);
							findViewById(R.id.coach_apply_info_award_selected_image).setEnabled(false);
							nickNameTxt.setEnabled(false);
							mSpecialEdit.setEnabled(false);
							mCommiTxt.setBackgroundColor(getResources().getColor(R.color.gray));
							mCommiTxt.setText(R.string.str_coach_apply_status_waiting);
							mStatusTxt.setTextColor(getResources().getColor(R.color.color_yellow));
							
						} else if(reqParam.audit == 1) {
													
							mStatusTxt.setText(R.string.str_coach_apply_status_success);
							mCommiTxt.setEnabled(true);
							mStatusTxt.setTextColor(getResources().getColor(R.color.color_white));
							
						} else if(reqParam.audit == 2) {
							
							mStatusTxt.setTextColor(getResources().getColor(R.color.color_red));
							mStatusTxt.setText(R.string.str_coach_apply_status_fail);
							mTitleTipsImage.setVisibility(View.VISIBLE);
							mCommiTxt.setEnabled(true);
						}
						
					/*
					 * 还没有出测成为教练信息
					 * */
					} else {
						
						loadAvatar(customer.sn, customer.avatar);
						mCommiTxt.setEnabled(true);

						regionTxt.setText(goGlobalData.getRegionName(customer.state));
						sexTxt.setText(goGlobalData.getSexName(customer.sex));
						ageTxt.setText(String.format(getString(R.string.str_account_age_wrap), customer.age));
						nickNameTxt.setText(customer.nickname);
						
						reqParam.type = Const.HOBBY_COACH;
						reqParam.avatar = customer.avatar;
						reqParam.age = Integer.valueOf(customer.age);
						reqParam.sex = customer.sex;
						reqParam.state = customer.state;
						reqParam.name = customer.nickname;
						
					}

				} else {
					
					Toast.makeText(mContext,request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	/*
	 * 验证用户的输入，必填项是否填完
	 * */
	private boolean verifyUserInput() {
		
		if (reqParam.avatar == null || reqParam.avatar.length() <= 0) {
			
			Toast.makeText(mContext, R.string.str_coach_invite_no_avatar, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (nickNameTxt == null || nickNameTxt.getText().toString().length() <= 0) {
			
			Toast.makeText(mContext, R.string.str_coach_invite_no_name, Toast.LENGTH_SHORT).show();
			return false;
		}
		reqParam.name = nickNameTxt.getText().toString();
		
		if (reqParam.sex < 0) {
			
			Toast.makeText(mContext, R.string.str_coach_invite_no_sex, Toast.LENGTH_SHORT).show();
			return false;
		}

		if (reqParam.age <= 0) {
			
			Toast.makeText(mContext, R.string.str_coach_invite_no_age, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (reqParam.id_fron_name == null || reqParam.id_fron_name.length() <= 0) {
			
			Toast.makeText(mContext, R.string.str_coach_invite_no_id_frond, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (reqParam.id_back_name == null || reqParam.id_back_name.length() <= 0) {
			
			Toast.makeText(mContext, R.string.str_coach_invite_no_id_back, Toast.LENGTH_SHORT).show();
			return false;
		}
		
//		if (reqParam.award_name == null || reqParam.award_name.length() <= 0) {
//
//			Toast.makeText(mContext, R.string.str_coach_invite_no_award, Toast.LENGTH_SHORT).show();
//			return false;
//		}
		
		if (reqParam.courseid <= 0) {
			
			Toast.makeText(mContext, R.string.str_coach_invite_no_course, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (reqParam.teach_age < 0) {
			
			Toast.makeText(mContext, R.string.str_coach_invite_no_teach_age, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (reqParam.special == null || reqParam.special.length() <= 0) {
			
			Toast.makeText(mContext, R.string.str_coach_invite_no_special, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		
		/*
		 * 职业教练，至少上传毕业证或者是资格证
		 * */
		if(reqParam.type == Const.PROFESSIONAL_COACH) {
			
			if ((reqParam.graduate_name == null || reqParam.graduate_name.length() <= 0) &&
					(reqParam.certificate_name == null || reqParam.certificate_name.length() <= 0) &&
					(reqParam.award_name == null || reqParam.award_name.length() <= 0)) {
					
				Toast.makeText(mContext, R.string.str_coach_invite_no_graduate_or_certificate, Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		return true;
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			finishWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


}
