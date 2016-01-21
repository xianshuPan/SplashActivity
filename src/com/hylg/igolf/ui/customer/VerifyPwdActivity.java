package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.VerifyUserPassword;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class VerifyPwdActivity extends Activity implements OnClickListener{
	
//		private static final String TAG = "VerifyPwdActivity";
		private EditText pwdEt;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			ExitToLogin.getInstance().addActivity(this);
			setContentView(R.layout.customer_ac_verify_pwd);
			getViews();
		}
	
		public static void startVerifyPwdActivity(Context context) {
		Intent intent = new Intent(context, VerifyPwdActivity.class);
		context.startActivity(intent);
		}
		
		private void getViews(){
			findViewById(R.id.verify_pwd_next).setOnClickListener(this);
			pwdEt = (EditText) findViewById(R.id.verify_pwd_input);
		}
		
		private void finishWithAnim() {
			finish();
			overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
		}
		
		protected void toastShort(String msg) {
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(KeyEvent.KEYCODE_BACK == keyCode) {
				finishWithAnim();
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
		
		public void onVerifyPwdBackBtnClick(View view) {
			finishWithAnim();
		}
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.verify_pwd_next:
				nextStep();
				break;
			}
			
		}
		
		private void nextStep() {
			final String password = Utils.getEditTextString(pwdEt);
			if(null == password) {
				Toast.makeText(this, R.string.str_toast_pwd_input, Toast.LENGTH_SHORT).show();
				return;
			}
			verifyPwd(MainApp.getInstance().getCustomer().sn,password);
		}
		
		private void verifyPwd(final String sn,final String pwd) {
			WaitDialog.showWaitDialog(this, R.string.str_loading_verify_pwd);
			new AsyncTask<Object, Object, Integer>() {
				VerifyUserPassword request = new VerifyUserPassword(VerifyPwdActivity.this, sn, pwd);
				@Override
				protected Integer doInBackground(Object... params) {
					return request.connectUrl();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					if(BaseRequest.REQ_RET_OK == result) {
						RebindPhoneActivity.startRebindPhoneActivity(VerifyPwdActivity.this);
						overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
					} else {
						Toast.makeText(VerifyPwdActivity.this,
								request.getFailMsg(), Toast.LENGTH_SHORT).show();
					}
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);
		}
}
