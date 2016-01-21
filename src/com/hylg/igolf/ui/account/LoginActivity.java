package com.hylg.igolf.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.LoginUser;
import com.hylg.igolf.ui.MainActivity;
import com.hylg.igolf.ui.SplashActivity;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class LoginActivity extends Activity {
	private static final String TAG = "LoginActivity";
	private static final String BUNDLE_PHONE = "login_phone";
	private ImageView avatarIv,deletePhoneImage;
	private EditText phoneInput;
	private EditText pwdInput;
	private String prefPhone;
	private String prefSn;
	private String prefAvatar;

	/**
	 * Restart by phone number.
	 * @param context
	 * @param phone
	 */
	public static void backWithPhone(Context context, String phone) {
		Intent intent = new Intent(context, LoginActivity.class);
		intent.putExtra(BUNDLE_PHONE, phone);
		context.startActivity(intent);
	}
	public static void backWithPhone(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_ac_login_new);
		getData();
		getViews();
		setData(prefPhone);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 正常退出应用，设置为0
		SharedPref.setInt(SharedPref.PREFS_KEY_PID, 0, this);
	}
	private void getData() {
		prefPhone = SharedPref.getPrefPhone(this);
		prefSn = SharedPref.getString(SharedPref.SPK_SN, this);
		prefAvatar = SharedPref.getString(SharedPref.SPK_AVATAR, this);
		Utils.logh(TAG, "prefPhone: " + prefPhone + " prefSn: " + prefSn + " prefAvatar: " + prefAvatar);
	}
	
	private void getViews() {
		avatarIv = (ImageView) findViewById(R.id.account_login_avatar);
		phoneInput = (EditText) findViewById(R.id.account_login_phone_input);
		pwdInput = (EditText) findViewById(R.id.account_login_pwd_input);
		deletePhoneImage  = (ImageView) findViewById(R.id.account_login_delete);

		phoneInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

				if (charSequence.length() > 0) {

					deletePhoneImage.setVisibility(View.VISIBLE);
				}
				else {

					deletePhoneImage.setVisibility(View.GONE);
				}

			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

		deletePhoneImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				phoneInput.setText("");
			}
		});
	}

	private void setData(String phone) {
		// set phone
		if(null != phone && Utils.isMobileNum(phone)) {
			phoneInput.setText(phone);
		}
		// 直接登录，临时---需在SplashActivity中处理。
//		if(null != SharedPref.getPrefPwd(this)) {
//			pwdInput.setText(SharedPref.getPrefPwd(this));
//			onLoginClick(null);
//		}
		// clear password note
		SharedPref.clearPrefPwd(this);
		pwdInput.setText("");
		pwdInput.requestFocus();
		// get avatar if phone saved
		if(!setAvatar()) {
			avatarIv.setImageResource(R.drawable.avatar_null);
		}
	}
	
	private boolean setAvatar() {
		String phone = Utils.getEditTextString(phoneInput);
		if(SharedPref.isInvalidPrefString(prefPhone) ||
				null == phone || !phone.equals(prefPhone)) {
			phoneInput.requestFocus();
			Utils.logh(TAG, "phone is invalid  !");
			return false;
		}
		if(SharedPref.isInvalidPrefString(prefSn)) {
			Utils.logh(TAG, "sn is invalid  !");
			return false;
		}
		// 重新取值，更新头像后，会有变化
		prefAvatar = SharedPref.getString(SharedPref.SPK_AVATAR, this);
		if(SharedPref.isInvalidPrefString(prefAvatar)) {
			Utils.logh(TAG, "avatar name is invalid  !");
			return false;
		}
//		String path = FileUtils.getAvatarPathBySn(prefSn, prefAvatar);
//		if(null == path) {
//			Utils.logh(TAG, "avatar path null !");
//			avatarIv.setImageResource(R.drawable.ic_launcher);
//		} else {
//			avatarIv.setImageBitmap(BitmapFactory.decodeFile(path));
//		}
		loadAvatar(prefSn, prefAvatar, avatarIv);
		return true;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String phone = intent.getStringExtra(BUNDLE_PHONE);
		/*
		 * 已经显示了登录页面，注册或者注销跳转
		 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		 * (注册返回时，应该添加头像更新)
		 * XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		 */
		Utils.logh(TAG, "onNewIntent ........ " + phone);
		getData();
		setData(phone);
//		if(null != phone) phoneInput.setText(phone);
//		pwdInput.setText("");
//		pwdInput.requestFocus();
	}

	public void onLoginClick(View view) {
		if(!Utils.isConnected(this)){
			return;
		}
		final String phone = Utils.getEditTextString(phoneInput);
		if(null == phone) {
			Toast.makeText(this, R.string.str_toast_input_phone, Toast.LENGTH_SHORT).show();
			return ;
		}
		if(!Utils.isMobileNum(phone)) {
			Utils.logh(TAG, phone);
			Toast.makeText(this, R.string.str_toast_invalid_phone, Toast.LENGTH_SHORT).show();
			return ;
		}
		final String password = Utils.getEditTextString(pwdInput);
		if(null == password) {
			Toast.makeText(this, R.string.str_toast_pwd_input, Toast.LENGTH_SHORT).show();
			return ;
		}
		int minLength = getResources().getInteger(R.integer.password_min_length);
		if(password.length() < minLength) {
			Toast.makeText(this, String.format(getString(R.string.str_toast_pwd_length), 
					minLength, getResources().getInteger(R.integer.password_max_length)), Toast.LENGTH_SHORT).show();
			return;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_login);
		new AsyncTask<Object, Object, Integer>() {
			LoginUser request = new LoginUser(LoginActivity.this, phone, password);
			@Override
			protected Integer doInBackground(Object... params) {
				
				int result = request.connectUrl();
				
				if(BaseRequest.REQ_RET_OK == result) {
					SharedPref.setPrefPhone(LoginActivity.this, phone);
					SharedPref.setPrefPwd(LoginActivity.this, password);
					// test...........
					SharedPref.setString(SharedPref.SPK_SN, MainApp.getInstance().getCustomer().sn, LoginActivity.this);
					SharedPref.setString(SharedPref.SPK_AVATAR, MainApp.getInstance().getCustomer().avatar, LoginActivity.this);
//					SharedPref.setString(SharedPref.SPK_SN, phone, LoginActivity.this);
				}
				return result;
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					startMainActivity();
				} else {
					if(BaseRequest.REQ_RET_F_ACCOUNT_NOT_EXIST == result) {
						phoneInput.requestFocus();
					} else if(BaseRequest.REQ_RET_F_PWD_ERROR == result) {
						pwdInput.setText("");
						pwdInput.requestFocus();
					}
					String msg = request.getFailMsg();
					if(msg.isEmpty()) {
						msg = getString(R.string.str_acc_login_def_msg);
					}
					Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);

	}
	
	private void startMainActivity() {
		MainActivity.startMainActivity(LoginActivity.this);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);					
	}
	
	public void onRegisterClick(View view) {
		PhoneSubmitActivity.submitPhoneForRegister(this);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	public void onResetClick(View view) {
		PhoneSubmitActivity.submitPhoneForReset(this);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	private void loadAvatar(String sn, String name, final ImageView iv) {
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(this, sn, name,
				(int) getResources().getDimension(R.dimen.avatar_detail_size));
		if(null != avatar) {
			iv.setImageDrawable(avatar);
		} else {
			iv.setImageResource(R.drawable.avatar_null);
			AsyncImageLoader.getInstance().loadAvatar(this, sn, name,
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if(null != imageDrawable ) {
								iv.setImageDrawable(imageDrawable);
							}
						}
				});
		}

	}
}
