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
import com.hylg.igolf.cs.request.ModifyPassword;
import com.hylg.igolf.ui.reqparam.ModifyCustomerReqParam;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class ModifyPwdActivity extends Activity implements OnClickListener{
		private static final String TAG = "ModifyPwdActivity";
		private ModifyCustomerReqParam reqParam;
		private EditText oldPwdEt,newPwdEt,confirmPwdEt;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.customer_ac_modify_pwd);
			getViews();
		}
	
		public static void startModifyPwdActivity(Context context) {
		Intent intent = new Intent(context, ModifyPwdActivity.class);
		context.startActivity(intent);
		}
		
		private void getViews(){
			findViewById(R.id.modify_pwd_topbar_back).setOnClickListener(this);
			findViewById(R.id.modify_pwd_do).setOnClickListener(this);
			oldPwdEt = (EditText) findViewById(R.id.modify_pwd_old_password);
			newPwdEt = (EditText) findViewById(R.id.modify_pwd_new_password);
			confirmPwdEt = (EditText) findViewById(R.id.modify_pwd_confirm_password);
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
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.modify_pwd_topbar_back:
				finishWithAnim();
				break;

			case R.id.modify_pwd_do:
				doModify();
				break;
			}
			
		}
		
		private void doModify() {
			if(!Utils.isConnected(this)){
				return;
			}
			reqParam = new ModifyCustomerReqParam();
			reqParam.sn = MainApp.getInstance().getCustomer().sn;
			String oldPwd = Utils.getEditTextString(oldPwdEt);
			if(null == oldPwd) {
				Toast.makeText(this, R.string.str_toast_old_pwd_input, Toast.LENGTH_SHORT).show();
				return;
			}
			int minLength = getResources().getInteger(R.integer.password_min_length);
			int maxLength = getResources().getInteger(R.integer.password_max_length);
			Utils.logh(TAG, "password minlength:"+minLength);
			if(oldPwd.length() < minLength) {
				Toast.makeText(this, String.format(getString(R.string.str_toast_pwd_length), 
						minLength,maxLength), Toast.LENGTH_SHORT).show();
				return;
			}
			reqParam.oldPwd = oldPwd;
			String newPwd = Utils.getEditTextString(newPwdEt);
			if(null == newPwd) {
				Toast.makeText(this, R.string.str_toast_new_pwd_input, Toast.LENGTH_SHORT).show();
				return;
			}
			if(newPwd.length() < minLength) {
				Toast.makeText(this, String.format(getString(R.string.str_toast_pwd_length), 
						minLength,maxLength), Toast.LENGTH_SHORT).show();
				return;
			}
			String confirmPwd = Utils.getEditTextString(confirmPwdEt);
			if(null == confirmPwd) {
				Toast.makeText(this, R.string.str_toast_pwd_confirm_input, Toast.LENGTH_SHORT).show();
				return;
			}
			if(!newPwd.equals(confirmPwd)) {
				Toast.makeText(this, R.string.str_toast_pwd_dismatch, Toast.LENGTH_SHORT).show();
				confirmPwdEt.requestFocus();
				return ;
			}
			reqParam.newPwd = newPwd;
			WaitDialog.showWaitDialog(this, R.string.str_loading_reset_pwd);
			new AsyncTask<Object, Object, Integer>() {
				ModifyPassword request = new ModifyPassword(ModifyPwdActivity.this, reqParam);
				@Override
				protected Integer doInBackground(Object... params) {
					return request.connectUrl();
				}
				@Override
				protected void onPostExecute(Integer result) {
					WaitDialog.dismissWaitDialog();
					if(BaseRequest.REQ_RET_OK == result) {
						Toast.makeText(ModifyPwdActivity.this,
								R.string.str_toast_modify_success, Toast.LENGTH_SHORT).show();
						finishWithAnim();
					} else {
						Toast.makeText(ModifyPwdActivity.this,
								request.getFailMsg(), Toast.LENGTH_SHORT).show();
					}				
				}
			}.execute(null, null, null);
		}
		
}
