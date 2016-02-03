package com.hylg.igolf.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.LoginUser;
import com.hylg.igolf.cs.request.RegisterCustomer;
import com.hylg.igolf.cs.request.UpdateAvatar;
import com.hylg.igolf.ui.MainActivity;
import com.hylg.igolf.ui.common.AgeSelectActivity;
import com.hylg.igolf.ui.common.AgeSelectActivity.onAgeSelectListener;
import com.hylg.igolf.ui.common.ImageSelectActivity;
import com.hylg.igolf.ui.common.ImageSelectActivity.onImageSelectListener;
import com.hylg.igolf.ui.common.IndustrySelectActivity;
import com.hylg.igolf.ui.common.IndustrySelectActivity.onIndustrySelectListener;
import com.hylg.igolf.ui.common.RegionSelectActivity;
import com.hylg.igolf.ui.common.RegionSelectActivity.onRegionSelectListener;
import com.hylg.igolf.ui.common.SexSelectActivity;
import com.hylg.igolf.ui.common.SexSelectActivity.onSexSelectListener;
import com.hylg.igolf.ui.common.YearsExpSelectActivity;
import com.hylg.igolf.ui.common.YearsExpSelectActivity.onYearsExpSelectListener;
import com.hylg.igolf.ui.reqparam.GetGolfersReqParam;
import com.hylg.igolf.ui.reqparam.RegisterCustomerReqParam;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.gl.lib.impl.TextWatcherBkgVariable;

public class InfoSetupActivity extends Activity
								implements View.OnClickListener,
								onSexSelectListener, onRegionSelectListener,
								onIndustrySelectListener, onAgeSelectListener,
								onYearsExpSelectListener, onImageSelectListener {
	public static final String TAG = "InfoSetupActivity";
	private final static String BUNDLE_INFO_PHONE = "setup_phone";
	private String phone;
	private ImageView avatar;
	private Uri mUri;
	private Bitmap avatarBmp = null;
	private EditText nicknameEt, pwdEt, pwdCfmEt;
	private TextView sexTxt, yearsExpTxt, regionTxt, industryTxt, ageTxt;
	private RegisterCustomerReqParam reqParam;
	
	public static void startInfoSetup(Context context, String phone) {
		Intent intent = new Intent(context, InfoSetupActivity.class);
		Bundle b = new Bundle();
		b.putString(BUNDLE_INFO_PHONE, phone);
		intent.putExtras(b);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitToLogin.getInstance().addActivity(this);
		setContentView(R.layout.account_ac_info_setup_new);
		getData();
		getViews();  
	}

	private void getData() {
		Bundle b = getIntent().getExtras();
		phone = b.getString(BUNDLE_INFO_PHONE);
		Utils.logh(TAG, "getData::: " + "phone: " + phone);
		reqParam = new RegisterCustomerReqParam(phone);
	}

	private void getViews() {
		findViewById(R.id.account_info_setup_topbar_back).setOnClickListener(this);
		findViewById(R.id.account_info_setup_avtar).setOnClickListener(this);
		findViewById(R.id.account_info_setup_do).setOnClickListener(this);
		
		findViewById(R.id.account_info_setup_sex_ll).setOnClickListener(this);
		findViewById(R.id.account_info_setup_yearsexp_ll).setOnClickListener(this);
		findViewById(R.id.account_info_setup_region_ll).setOnClickListener(this);
		findViewById(R.id.account_info_setup_industry_ll).setOnClickListener(this);
		findViewById(R.id.account_info_setup_age_ll).setOnClickListener(this);
		
		avatar = (ImageView) findViewById(R.id.account_info_setup_avtar);
		
		nicknameEt = (EditText) findViewById(R.id.account_info_setup_nickname);
		nicknameEt.addTextChangedListener(new TextWatcherBkgVariable(nicknameEt, true));
		pwdEt = (EditText) findViewById(R.id.account_info_setup_pwd);
		pwdCfmEt = (EditText) findViewById(R.id.account_info_setup_pwd_confirm);
		pwdCfmEt.setOnEditorActionListener(mOnEditorActionListener);
		
		sexTxt = (TextView) findViewById(R.id.account_info_setup_sex_selection);
		yearsExpTxt = (TextView) findViewById(R.id.account_info_setup_yearsexp_selection);
		regionTxt = (TextView) findViewById(R.id.account_info_setup_region_selection);
		industryTxt = (TextView) findViewById(R.id.account_info_setup_industry_selection);
		ageTxt = (TextView) findViewById(R.id.account_info_setup_age_selection);
	}
		
	private void hideImm() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		if(imm.isActive() && getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	private void doRegister() {
		if(!Utils.isConnected(this)){
			return;
		}
		if(null == avatarBmp) {
			Toast.makeText(this, R.string.str_toast_avatar_null, Toast.LENGTH_SHORT).show();
			return;
		}
		String nickname = Utils.getEditTextString(nicknameEt);
		if(null == nickname) {
			Toast.makeText(this, R.string.str_toast_nickname_input, Toast.LENGTH_SHORT).show();
			return;
		}
		int min_nickname = getResources().getInteger(R.integer.nickname_min_length);
		int max_nickname = getResources().getInteger(R.integer.nickname_max_length);
		if(nickname.length() < min_nickname) {
			Toast.makeText(this, String.format(getString(R.string.str_toast_sts_nickname_len),
					min_nickname,max_nickname), Toast.LENGTH_SHORT).show();
			return;
		}
		reqParam.nickname = nickname;
		String pwd = Utils.getEditTextString(pwdEt);
		if(null == pwd) {
			Toast.makeText(this, R.string.str_toast_pwd_input, Toast.LENGTH_SHORT).show();
			return;
		}	
		int minLength = getResources().getInteger(R.integer.password_min_length);
		if(pwd.length() < minLength) {
			Toast.makeText(this, String.format(getString(R.string.str_toast_pwd_length), 
					minLength, getResources().getInteger(R.integer.password_max_length)), Toast.LENGTH_SHORT).show();
			return;
		}
		String pwdCfm = Utils.getEditTextString(pwdCfmEt);
		if(null == pwdCfm) {
			Toast.makeText(this, R.string.str_toast_pwd_confirm_input, Toast.LENGTH_SHORT).show();
			return;
		}
		if(!pwd.equals(pwdCfm)) {
			Toast.makeText(this, R.string.str_toast_pwd_dismatch, Toast.LENGTH_SHORT).show();
			pwdCfmEt.requestFocus();
			return ;
		}
		reqParam.password = pwd;
		if(Integer.MAX_VALUE == reqParam.sex) {
			Toast.makeText(this, R.string.str_toast_sex_set, Toast.LENGTH_SHORT).show();
			return;
		}
		if(Integer.MAX_VALUE == reqParam.yearsExp) {
			Toast.makeText(this, R.string.str_toast_yearsexp_set, Toast.LENGTH_SHORT).show();
			return;
		}
		if(reqParam.city.isEmpty()) {
			Toast.makeText(this, R.string.str_toast_region_set, Toast.LENGTH_SHORT).show();
			return;
		}
		if(reqParam.industry.isEmpty()) {
			Toast.makeText(this, R.string.str_toast_industry_set, Toast.LENGTH_SHORT).show();
			return;
		}
		if(Integer.MAX_VALUE == reqParam.age) {
			Toast.makeText(this, R.string.str_toast_age_set, Toast.LENGTH_SHORT).show();
			return;
		}
		
		doRegisterInfo();
	}
	
	private void doRegisterInfo() {
		WaitDialog.showWaitDialog(this, R.string.str_loading_register);
		new AsyncTask<Object, Object, Integer>() {
			RegisterCustomer request = new RegisterCustomer(InfoSetupActivity.this, reqParam);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					doRegisterAvatar(phone, avatarBmp, request.getSn());
				} else {
					Toast.makeText(InfoSetupActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
					WaitDialog.dismissWaitDialog();
				}				
			}
		}.execute(null, null, null);
	}

	private void doRegisterAvatar(final String phone, final Bitmap bitmap, final String sn) {
		new AsyncTask<Object, Object, Integer>() {
			UpdateAvatar request = new UpdateAvatar(InfoSetupActivity.this, bitmap, sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				WaitDialog.dismissWaitDialog();
				if(BaseRequest.REQ_RET_OK == result) {
					Toast.makeText(InfoSetupActivity.this,
							R.string.str_toast_register_success, Toast.LENGTH_SHORT).show();
					backToLogin(phone, reqParam.password);
				} else {
					Toast.makeText(InfoSetupActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private void backToLogin(final String phone, final String password) {
		// 注册成功，直接登录
		WaitDialog.showWaitDialog(this, R.string.str_loading_login);
		new AsyncTask<Object, Object, Integer>() {
			LoginUser request = new LoginUser(InfoSetupActivity.this, phone, password);
			@Override
			protected Integer doInBackground(Object... params) {
				int result = request.connectUrl();
				if(BaseRequest.REQ_RET_OK == result) {
					SharedPref.setPrefPhone(InfoSetupActivity.this, phone);
					SharedPref.setPrefPwd(InfoSetupActivity.this, password);
					SharedPref.setString(SharedPref.SPK_SN, MainApp.getInstance().getCustomer().sn, InfoSetupActivity.this);
					SharedPref.setString(SharedPref.SPK_AVATAR, MainApp.getInstance().getCustomer().avatar, InfoSetupActivity.this);
				}
				return result;
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {

					GetGolfersReqParam data = new GetGolfersReqParam();
					data.pageSize  = MainApp.getInstance().getGlobalData().pageSize;
					data.pageNum  = MainApp.getInstance().getGlobalData().startPage;

					if (reqParam.city != null && reqParam.city.length() >= 7) {

						data.region = reqParam.city.substring(0,7);
					}
					//data.region = reqParam.city;
					data.sn = MainApp.getInstance().getCustomer().sn;
					RecommendGolfersListActivity.startRecommendGolfersListActivity(InfoSetupActivity.this,data);
					//startMainActivity();

				} else {
					String msg = request.getFailMsg();
					if(msg.isEmpty()) {
						msg = getString(R.string.str_acc_login_def_msg);
					}
					Toast.makeText(InfoSetupActivity.this, msg, Toast.LENGTH_SHORT).show();
					// 登录失败，提示并退回登录页面
					LoginActivity.backWithPhone(InfoSetupActivity.this, phone);
					ExitToLogin.getInstance().exitToLogin();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);

	}
	
	private void startMainActivity() {
		MainActivity.startMainActivityFromSetup(InfoSetupActivity.this);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);					
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
						avatar.setImageBitmap(photo);
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
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.account_info_setup_topbar_back:
				finishWithAnim();
				break;
			case R.id.account_info_setup_avtar:
				hideImm();
				ImageSelectActivity.startImageSelect(InfoSetupActivity.this);
				break;
			case R.id.account_info_setup_do:
				doRegister();
//				doRegisterAvatar(phone, avatarBmp, "1001");
				break;
			case R.id.account_info_setup_sex_ll:
				hideImm();
				SexSelectActivity.startSexSelect(InfoSetupActivity.this, false, reqParam.sex);
				break;
			case R.id.account_info_setup_yearsexp_ll:
				hideImm();
				YearsExpSelectActivity.startYearsExpSelect(this, reqParam.yearsExp);
				break;
			case R.id.account_info_setup_region_ll:
				hideImm();
				RegionSelectActivity.startRegionSelect(InfoSetupActivity.this, RegionSelectActivity.REGION_TYPE_SET_INFO, reqParam.city);
				break;
			case R.id.account_info_setup_industry_ll:
				hideImm();
				IndustrySelectActivity.startIndustrySelect(InfoSetupActivity.this, false, reqParam.industry);
				break;
			case R.id.account_info_setup_age_ll:
				hideImm();
				AgeSelectActivity.startAgeSelect(InfoSetupActivity.this, reqParam.age);
				break;
		}
	}
	
	// 在最后一条输入框中，点击回车，隐藏输入框
	private OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if( event != null &&
					event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
					event.getAction()  == KeyEvent.ACTION_DOWN ) {
				hideImm();
				return true;
			}
			return false;
		}
		
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			finishWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void finishWithAnim() {
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}

	@Override
	public void onSexSelect(int newSex) {
		reqParam.sex = newSex;
		sexTxt.setText(MainApp.getInstance().getGlobalData().getSexName(newSex));
	}

	@Override
	public void onRegionSelect(String newRegion) {
		reqParam.city = newRegion;
		regionTxt.setText(MainApp.getInstance().getGlobalData().getRegionName(newRegion));
	}

	@Override
	public void onIndustrySelect(String newIndustry) {
		reqParam.industry = newIndustry;
		industryTxt.setText(MainApp.getInstance().getGlobalData().getIndustryName(newIndustry));
	}

	@Override
	public void onAgeSelect(int newAge) {
		reqParam.age = newAge;
		ageTxt.setText(String.format(getString(R.string.str_account_age_wrap), newAge));
	}

	@Override
	public void onYearsExpSelect(int newYearsExp) {
		reqParam.yearsExp = newYearsExp;
		yearsExpTxt.setText(String.format(getString(R.string.str_account_yearsexp_wrap), newYearsExp));
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
}
