package com.hylg.igolf.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.ResetUserPassword;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class ResetPwdActivity extends Activity implements View.OnClickListener {
	public static final String TAG = "ResetPwdActivity";
	private final static String BUNDLE_SN = "sn";
	private final static String BUNDLE_RESET_PHONE = "reset_phone";
	private String sn;
	private String phone;
	private EditText pwdFirstEt;
	private EditText pwdConfirmEt;
	
	public static void startResetPwd(Context context, String phone, String sn) {
		Intent intent = new Intent(context, ResetPwdActivity.class);
		Bundle b = new Bundle();
		b.putString(BUNDLE_SN, sn);
		b.putString(BUNDLE_RESET_PHONE, phone);
		intent.putExtras(b);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitToLogin.getInstance().addActivity(this);
		setContentView(R.layout.account_ac_reset_pwd);
		getData();
		getViews();
	}

	private void getData() {
		Bundle b = getIntent().getExtras();
		sn = b.getString(BUNDLE_SN);
		phone = b.getString(BUNDLE_RESET_PHONE);
		Utils.logh(TAG, "getData::: " + "sn: " + sn + " phone: " + phone);
	}

	private void getViews() {
		pwdFirstEt = (EditText) findViewById(R.id.account_reset_pwd);
		pwdConfirmEt = (EditText) findViewById(R.id.account_reset_pwd_confirm);
		findViewById(R.id.account_reset_topbar_back).setOnClickListener(this);
		findViewById(R.id.account_reset_do).setOnClickListener(this);
	}

	private void onResetBtnClick(final String first, String confirm) {
		if(null == first) {
			Toast.makeText(this, R.string.str_toast_pwd_input, Toast.LENGTH_SHORT).show();
			pwdFirstEt.requestFocus();
			return ;
		}
		int minLength = getResources().getInteger(R.integer.password_min_length);
		if(first.length() < minLength) {
			Toast.makeText(this, String.format(getString(R.string.str_toast_pwd_length), 
					minLength, getResources().getInteger(R.integer.password_max_length)), Toast.LENGTH_SHORT).show();
			return;
		}
		if(null == confirm) {
			Toast.makeText(this, R.string.str_toast_pwd_confirm_input, Toast.LENGTH_SHORT).show();
			pwdConfirmEt.requestFocus();
			return ;
		}
		if(!first.equals(confirm)) {
			Toast.makeText(this, R.string.str_toast_pwd_dismatch, Toast.LENGTH_SHORT).show();
			pwdConfirmEt.requestFocus();
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_reset_pwd);
		new AsyncTask<Object, Object, Integer>() {
			ResetUserPassword request = new ResetUserPassword(ResetPwdActivity.this, sn, first);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					Toast.makeText(ResetPwdActivity.this,
							R.string.str_toast_modify_success, Toast.LENGTH_SHORT).show();
					backToLogin(phone);
				} else {
					Toast.makeText(ResetPwdActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private void backToLogin(String phone) {
		LoginActivity.backWithPhone(this, phone);
		ExitToLogin.getInstance().exitToLogin();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.account_reset_topbar_back:
				finishWithAnim();
				break;
			case R.id.account_reset_do:
				onResetBtnClick(Utils.getEditTextString(pwdFirstEt),
						Utils.getEditTextString(pwdConfirmEt));
				break;
		}
	}
	
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
}
