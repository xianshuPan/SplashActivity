package com.hylg.igolf.ui.coach;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CourseInfo;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.CommitCoachInfo;
import com.hylg.igolf.cs.request.GetCoachApplyInfo;
import com.hylg.igolf.cs.request.GetCourseInfoList;
import com.hylg.igolf.cs.request.ModifyCustomer;
import com.hylg.igolf.cs.request.RequestParam;
import com.hylg.igolf.cs.request.UpdateAvatar;
import com.hylg.igolf.cs.request.UploadImageRequest;
import com.hylg.igolf.ui.common.AgeSelectActivity;
import com.hylg.igolf.ui.common.AgeSelectActivity.onAgeSelectListener;
import com.hylg.igolf.ui.common.ImageSelectActivity;
import com.hylg.igolf.ui.common.ImageSelectActivity.onImageSelectListener;
import com.hylg.igolf.ui.common.SexSelectActivity;
import com.hylg.igolf.ui.common.SexSelectActivity.onSexSelectListener;
import com.hylg.igolf.ui.common.YearsExpSelectActivity;
import com.hylg.igolf.ui.common.YearsExpSelectActivity.onYearsExpSelectListener;
import com.hylg.igolf.ui.hall.CourseSelectActivity;
import com.hylg.igolf.ui.hall.CourseSelectActivity.onCourseSelectListener;
import com.hylg.igolf.ui.reqparam.CoachApplyInfoReqParam;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class CoachApplyInfoActivity extends Activity implements 
												OnClickListener,onSexSelectListener, 
												onAgeSelectListener,onYearsExpSelectListener, 
												onImageSelectListener ,onCourseSelectListener{
	
	private static final String 				TAG = "CoachInfoActivity";
	
	private ImageButton                         mBack = null;
	
	private TextView                            mStatusTxt = null;
	private ImageView                           mTitleTipsImage = null;
	
	private EditText                            nickNameTxt;
	private TextView 							ageTxt,sexTxt,idTxt,specialTxt,placeTxt,teachAgeTxt,graduateTxt,certificateTxt,awardTxt,mCommitTxt;
	
	/*
	 * 选择业余教练还是职业教练
	 * 
	 * */
	private LinearLayout                        mHobbyCoachLinear,mProfessionalCoachLinear;
	
	/*
	 * 上传省份证相关
	 * */
	private ImageView                           mIDFrondSelectImage,mIDBackSelectImage,mIDFrondSelectOkImage,mIDBackSelectOkImage;
	private ProgressBar                         mIDFrondProgress,mIDBackProgress;
	private TextView                            mIDFrondTipsTxt,mIDBackTipsTxt;
	
	/*
	 * 职业教练选填相关项
	 * */
	private ImageView                           mGraduateSelectImage,mCertificateSelectImage,mAwardSelectImage,
												mGraduateSelectOkImage,mCertificateSelectOkImage,mAwardSelectOkImage;
	private ProgressBar                         mGraduateProgress,mCertificateProgress,mAwardProgress;
	private TextView                            mGraduateTipsTxt,mCertificateTipsTxt,mAwardTipsTxt;
	private EditText							mSpecialEdit;
	
	/*
	 * 职业教练选填项
	 * */
	private RelativeLayout                      mProfessionalSelectInputRelative;
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ExitToLogin.getInstance().addActivity(this);
		setContentView(R.layout.coach_apply_ac_info);
		getViews();
		
		initMyInfoData();
	}
	
	/*
	 * 获取控件，并设置相关的时间监听器，初始化相关变量，
	 * 
	 * */
	private void getViews(){
		
		mBack = (ImageButton) findViewById(R.id.coach_applay_info_back);
		mStatusTxt = (TextView) findViewById(R.id.coach_applay_info_status_text);
		mTitleTipsImage = (ImageView) findViewById(R.id.coach_applay_info_title_tips_image);
		nickNameTxt = (EditText) findViewById(R.id.coach_apply_info_name_selection);
		ageTxt = (TextView) findViewById(R.id.coach_apply_info_age_selection);
		sexTxt = (TextView) findViewById(R.id.coach_apply_info_sex_selection);
		idTxt = (TextView) findViewById(R.id.coach_apply_info_id_selection);
		specialTxt = (TextView) findViewById(R.id.coach_apply_info_special_selection);
		mSpecialEdit = (EditText) findViewById(R.id.coach_apply_info_special_edit);
		placeTxt = (TextView) findViewById(R.id.coach_apply_info_place_selection);
		teachAgeTxt = (TextView) findViewById(R.id.coach_apply_info_teach_age_selection);
		graduateTxt = (TextView) findViewById(R.id.coach_apply_info_graduate_selection);
		certificateTxt = (TextView) findViewById(R.id.coach_apply_info_certificate_selection);
		
		awardTxt = (TextView) findViewById(R.id.coach_apply_info_award_selection);
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
		
		
		mHobbyCoachLinear = (LinearLayout) findViewById(R.id.coach_apply_info_hobby_linear);
		mHobbyCoachLinear.setSelected(true);
		mProfessionalCoachLinear = (LinearLayout) findViewById(R.id.coach_apply_info_professional_linear);
		mProfessionalSelectInputRelative = (RelativeLayout) findViewById(R.id.coach_apply_info_professional_select_input_relative);
		
		findViewById(R.id.coach_applay_info_back).setOnClickListener(this);
		findViewById(R.id.coach_applay_info_title_tips_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_avtar_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_age_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_sex_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_name_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_hobby_linear).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_professional_linear).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_id_front_selected_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_id_back_selected_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_special_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_place_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_teach_age_ll).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_graduate_selected_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_certificate_selected_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_award_selected_image).setOnClickListener(this);
		findViewById(R.id.coach_apply_info_commit_text).setOnClickListener(this);
		
		mSpecialEdit.setFocusable(true);
		mSpecialEdit.requestFocus();
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
				YearsExpSelectActivity.startYearsExpSelect(this, reqParam.teach_age);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;

			case R.id.coach_apply_info_age_ll:
				AgeSelectActivity.startAgeSelect(this, reqParam.age);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
				
			case R.id.coach_apply_info_place_ll:
				
				setupCourse();
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
				
			case R.id.coach_apply_info_hobby_linear:
				
				mHobbyCoachLinear.setSelected(true);
				mProfessionalCoachLinear.setSelected(false);
				mProfessionalSelectInputRelative.setVisibility(View.GONE);
				reqParam.type = Const.HOBBY_COACH;
				findViewById(R.id.coach_apply_info_professional_tips_text).setVisibility(View.GONE);

				break;
				
			case R.id.coach_apply_info_professional_linear:
				
				mHobbyCoachLinear.setSelected(false);
				mProfessionalCoachLinear.setSelected(true);
				mProfessionalSelectInputRelative.setVisibility(View.VISIBLE);
				reqParam.type = Const.PROFESSIONAL_COACH;
				findViewById(R.id.coach_apply_info_professional_tips_text).setVisibility(View.VISIBLE);
				
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
		mDesPopWin.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
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
			GetCourseInfoList request = new GetCourseInfoList(mContext, customer.state);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					CourseSelectActivity.startCourseSelect(mContext, request.getCourseList());
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
		doModifyCustomerInfo(RequestParam.PARAM_REQ_YEARSEXP_STR,String.valueOf(newYearsExp),teachAgeTxt,
				String.format(getString(R.string.str_account_yearsexp_wrap), newYearsExp));
		
		//customer.yearsExpStr = String.valueOf(newYearsExp);
		reqParam.teach_age = newYearsExp;
	}

	
	@Override
	public void onAgeSelect(int newAge) {
		doModifyCustomerInfo(RequestParam.PARAM_REQ_AGE,String.valueOf(newAge), 
				ageTxt,String.format(getString(R.string.str_account_age_wrap), newAge));
		//customer.age = String.valueOf(newAge);
		reqParam.age = newAge;
	}


	@Override
	public void onSexSelect(int newSex) {
		doModifyCustomerInfo(RequestParam.PARAM_REQ_SEX,String.valueOf(newSex),
				sexTxt,goGlobalData.getSexName(newSex));

		reqParam.sex = newSex;
	}
	
	@Override
	public void onCourseSelect(CourseInfo course) {
		// TODO Auto-generated method stub
		
		reqParam.courseid = course.id;
		placeTxt.setText(course.abbr);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, " ::: requestCode: " + requestCode + " resultCode: " + resultCode + " intent: " + intent);
		if(Const.REQUST_CODE_PHOTE_TAKE_PHOTO == requestCode) {
			if(RESULT_OK == resultCode) {
				startPhotoCrop(mUri);
				return ;
			}
		} else if(Const.REQUST_CODE_PHOTE_GALLERY == requestCode) {
			if(RESULT_OK == resultCode && null != intent) {
				startPhotoCrop(intent.getData());
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
							
						case  Const.ID_FRONT:
							
							doUploadPhoto(avatarBmp, mCurrentPhotoSelectTypeInt,"recieveIdCard", "idCards",
									mIDFrondSelectOkImage,mIDFrondProgress,mIDFrondSelectImage);
							break;
							
						case  Const.ID_BACK:
							
							doUploadPhoto(avatarBmp, mCurrentPhotoSelectTypeInt,"recieveIdCard", "idCards",
									mIDBackSelectOkImage,mIDBackProgress,mIDBackSelectImage);
							
							break;
							
						case  Const.GRADUATE:
							
							doUploadPhoto(avatarBmp, mCurrentPhotoSelectTypeInt,"recieveOtherCards", "otherCards",
									mGraduateSelectOkImage,mGraduateProgress,mGraduateSelectImage);
							
							break;
							
						case  Const.CERTIFICATE:
							
							doUploadPhoto(avatarBmp, mCurrentPhotoSelectTypeInt,"recieveOtherCards", "otherCards",
									mCertificateSelectOkImage,mCertificateProgress,mCertificateSelectImage);
							
							break;
							
						case  Const.AWARD	:

							doUploadPhoto(avatarBmp, mCurrentPhotoSelectTypeInt,"recieveOtherCards", "otherCards",
									mAwardSelectOkImage,mAwardProgress,mAwardSelectImage);
							
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
	private void doModifyCustomerInfo(final String key,final String value,final TextView textView,final String textValue){
		WaitDialog.showWaitDialog(this,
				R.string.str_loading_modify_msg);
		if(!Utils.isConnected(this)){
			return;
		}
		new AsyncTask<Object, Object, Integer>() {
			ModifyCustomer request = new ModifyCustomer(mContext, customer.sn, key, value);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					textView.setText(textValue);
					changedFlag = true;
				} else {
					Toast.makeText(mContext,request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	/*
	 * 启动裁减功能
	 * */
	private void startPhotoCrop(Uri uri) {
		int width = getResources().getInteger(R.integer.upload_avatar_size);
		int height = getResources().getInteger(R.integer.upload_avatar_size);

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
		WaitDialog.showWaitDialog(this,R.string.str_loading_modify_msg);
		
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
	 * 开启上传图片功能，
	 * @ para bitmap 经过裁减的要上传的图片
	 * @ para methodname 表示 要上传的身份证，还是其他图片（高尔夫毕业证，奖状等） 
	 * @ para okImage 上传图片之后，显示成功
	 * @ para progress 上传图片的进度显示
	 * @ para selectedImage 上传图片成功之后，显示图片
	 * */
	private void doUploadPhoto(final Bitmap bitmap,final int type,final String methodName,final String bodyName,
								final ImageView okImage,final ProgressBar progress,final ImageView selectedImage) {
		if(!Utils.isConnected(this)){
			return;
		}
		//WaitDialog.showWaitDialog(this,R.string.str_loading_modify_msg);
		
		final String fileName = System.currentTimeMillis()+".jpg";
		
		new AsyncTask<Object, Object, Integer>() {
			UploadImageRequest request = new UploadImageRequest(mContext,bitmap,type,methodName,bodyName,fileName,okImage,progress,selectedImage) ;
			@Override
			protected Integer doInBackground(Object... params) {
				
				return request.connectUrl();
			}
			
			@Override
			protected void onPreExecute() {
				
				request.progress.setVisibility(View.VISIBLE);
				request.okImageView.setVisibility(View.GONE);
				
				super.onPreExecute();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				request.progress.setVisibility(View.GONE);
				
				if(BaseRequest.REQ_RET_OK == result) {

					request.okImageView.setVisibility(View.VISIBLE);
					request.selectedImage.setImageBitmap(bitmap);
					
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
						
					case  Const.AWARD	:
						
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
						
						loadAvatar(customer.sn,reqParam.avatar);
				
						sexTxt.setText(goGlobalData.getSexName(reqParam.sex));
						//ageTxt.setText(String.format(getString(R.string.str_account_age_wrap), reqParam.age));
						nickNameTxt.setText(reqParam.name);
						placeTxt.setText(reqParam.course_name);
						teachAgeTxt.setText(String.valueOf(reqParam.teach_age));
						specialTxt.setText(reqParam.special);
						
						if(reqParam.type == Const.PROFESSIONAL_COACH) {
							
							mProfessionalCoachLinear.setSelected(true);
							mHobbyCoachLinear.setSelected(false);
							mProfessionalSelectInputRelative.setVisibility(View.VISIBLE);
							findViewById(R.id.coach_apply_info_professional_tips_text).setVisibility(View.VISIBLE);
							
						} else {
							
							mProfessionalCoachLinear.setSelected(false);
							mHobbyCoachLinear.setSelected(true);
							mProfessionalSelectInputRelative.setVisibility(View.GONE);
							findViewById(R.id.coach_apply_info_professional_tips_text).setVisibility(View.GONE);
						}
						
						DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.id_fron_name, mIDFrondSelectImage, null);
						DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.id_back_name, mIDBackSelectImage, null);
						DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.award_name, mAwardSelectImage, null);
						DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.certificate_name, mCertificateSelectImage, null);
						DownLoadImageTool.getInstance(mContext).displayImage(BaseRequest.CoachPic_Original_PATH+reqParam.graduate_name, mGraduateSelectImage, null);
						
						
						mStatusTxt.setVisibility(View.VISIBLE);
						if(reqParam.audit == 0) {
							
							mStatusTxt.setText(R.string.str_coach_apply_status_waiting);
							mStatusTxt.setTextColor(getResources().getColor(R.color.color_yellow));
							
						} else if(reqParam.audit == 1) {
													
							mStatusTxt.setText(R.string.str_coach_apply_status_success);
							mStatusTxt.setTextColor(getResources().getColor(R.color.color_white));
							
						} else if(reqParam.audit == 2) {
							
							mStatusTxt.setTextColor(getResources().getColor(R.color.color_red));
							mStatusTxt.setText(R.string.str_coach_apply_status_fail);
							mTitleTipsImage.setVisibility(View.VISIBLE);
						}
						
					/*
					 * 还没有出测成为教练信息
					 * */
					} else {
						
						loadAvatar(customer.sn,customer.avatar);
						
						sexTxt.setText(goGlobalData.getSexName(customer.sex));
						//ageTxt.setText(String.format(getString(R.string.str_account_age_wrap), customer.age));
						nickNameTxt.setText(customer.nickname);
						
						reqParam.type = Const.HOBBY_COACH;
						reqParam.avatar = customer.avatar;
						reqParam.age = Integer.valueOf(customer.age);
						reqParam.sex = customer.sex;
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
		
		if (reqParam.name == null || reqParam.name.length() <= 0) {
			
			Toast.makeText(mContext, R.string.str_coach_invite_no_name, Toast.LENGTH_SHORT).show();
			return false;
		}
		
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
		
		if (reqParam.award_name == null || reqParam.award_name.length() <= 0) {
			
			Toast.makeText(mContext, R.string.str_coach_invite_no_award, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (reqParam.courseid < 0) {
			
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
				(reqParam.certificate_name == null || reqParam.certificate_name.length() <= 0)) {
					
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
