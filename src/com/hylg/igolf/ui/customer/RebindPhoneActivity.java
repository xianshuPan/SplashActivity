package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.RebindSubmitPhone;
import com.hylg.igolf.ui.account.PhoneVerifyActivity;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class RebindPhoneActivity extends Activity implements OnClickListener{
		private static final String TAG = "RebindPhoneActivity";
		private EditText newPhoneEt;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			ExitToLogin.getInstance().addActivity(this);
			setContentView(R.layout.customer_ac_rebind_phone);
			getViews();
		}
	
		public static void startRebindPhoneActivity(Context context) {
		Intent intent = new Intent(context, RebindPhoneActivity.class);
		context.startActivity(intent);
		}
		
		private void getViews(){
			findViewById(R.id.rebind_phone_topbar_back).setOnClickListener(this);
			findViewById(R.id.rebind_phone_next).setOnClickListener(this);
			newPhoneEt = (EditText) findViewById(R.id.rebind_phone_new_phone);
			TextView bindPhoneTextView = (TextView) findViewById(R.id.rebind_phone_current_phone);
			String bindPhone = String.format(getResources().getString(R.string.str_comm_current_bind_phone),MainApp.getInstance().getCustomer().phone);
			bindPhoneTextView.setText(bindPhone);
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
			case R.id.rebind_phone_topbar_back:
				finishWithAnim();
				break;

			case R.id.rebind_phone_next:
				nextStep();
				break;
			}
			
		}
		
		private void nextStep() {
			if(!Utils.isConnected(this)){
				return;
			}
			final String newPhone = Utils.getEditTextString(newPhoneEt);
			if(null == newPhone) {
				Toast.makeText(this, R.string.str_toast_input_phone, Toast.LENGTH_SHORT).show();
				return;
			}	
			if(!Utils.isMobileNum(newPhone)){
				Utils.logh(TAG, newPhone);
				Toast.makeText(this, R.string.str_toast_invalid_phone, Toast.LENGTH_SHORT).show();
				return ;
			}
			// show confirm dialog
			final Dialog dialog = new Dialog(this, R.style.alert_dialog_style);
			View view = View.inflate(this, R.layout.dialog_phone_submit, null);
			TextView msg = (TextView) view.findViewById(R.id.dialog_ps_msg_view);
			msg.setText(String.format(getResources().getString(R.string.str_verify_content), newPhone));
			view.findViewById(R.id.dialog_ps_btn_cancel).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.cancel();
				}
			});
			view.findViewById(R.id.dialog_ps_btn_ok).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.cancel();
					rebindSubmitPhone(newPhone);
				}
			});
			dialog.setContentView(view);
			dialog.show();
		}
		
		private void rebindSubmitPhone(final String phone) {
			WaitDialog.showWaitDialog(this, R.string.str_loading_get_verify);
			new AsyncTask<Object, Object, Integer>() {
				RebindSubmitPhone request = new RebindSubmitPhone(RebindPhoneActivity.this, MainApp.getInstance().getCustomer().sn, phone);
				@Override
				protected Integer doInBackground(Object... params) {
					return request.connectUrl();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					if(BaseRequest.REQ_RET_OK == result) {
						PhoneVerifyActivity.verifyPhoneForRebind(RebindPhoneActivity.this,
								phone, request.getVerifyCode());
						overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
					} else {
						Toast.makeText(RebindPhoneActivity.this,
								request.getFailMsg(), Toast.LENGTH_SHORT).show();
					}
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);
		}
		
}
