package com.hylg.igolf.ui.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.gl.lib.impl.TextWatcherBkgVariable;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.RebindVerifyPhone;
import com.hylg.igolf.cs.request.RegisterSubmitPhone;
import com.hylg.igolf.cs.request.RegisterVerifyPhone;
import com.hylg.igolf.cs.request.ResetSubmitPhone;
import com.hylg.igolf.cs.request.ResetVerifyPhone;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class PhoneVerifyActivity extends Activity implements View.OnClickListener {
	public static final String TAG = "PhoneVerifyActivity";
	private final static String BUNDLE_VERIFY_TYPE = "verify_type";
	private final static String BUNDLE_VERIFY_PHONE = "verify_phone";
	private final static String BUNDLE_VERIFY_CODE = "verify_code";
	private int operType;
	private String phone;
	private String verify;
	private EditText inputVerifyEt;
	public static final int WAIT_TIME = 60;
	private int recLen = WAIT_TIME;
	private Button renewBtn;
	
	public static void verifyPhoneForRegister(Context context, String phone, String verify) {
		Intent intent = new Intent(context, PhoneVerifyActivity.class);
		Bundle b = new Bundle();
		b.putInt(BUNDLE_VERIFY_TYPE, AccConst.OPER_TYPE_REGISTER);
		b.putString(BUNDLE_VERIFY_PHONE, phone);
		b.putString(BUNDLE_VERIFY_CODE, verify);
		intent.putExtras(b);
		context.startActivity(intent);
	}
	
	public static void verifyPhoneForReset(Context context, String phone, String verify) {
		Intent intent = new Intent(context, PhoneVerifyActivity.class);
		Bundle b = new Bundle();
		b.putInt(BUNDLE_VERIFY_TYPE, AccConst.OPER_TYPE_RESET);
		b.putString(BUNDLE_VERIFY_PHONE, phone);
		b.putString(BUNDLE_VERIFY_CODE, verify);
		intent.putExtras(b);
		context.startActivity(intent);
	}
	
	public static void verifyPhoneForRebind(Context context, String phone, String verify) {
		Intent intent = new Intent(context, PhoneVerifyActivity.class);
		Bundle b = new Bundle();
		b.putInt(BUNDLE_VERIFY_TYPE, AccConst.OPER_TYPE_REBIND);
		b.putString(BUNDLE_VERIFY_PHONE, phone);
		b.putString(BUNDLE_VERIFY_CODE, verify);
		intent.putExtras(b);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitToLogin.getInstance().addActivity(this);
		setContentView(R.layout.account_ac_phone_verify);
		getData();
		getViews();
		initRenewButton();
	}
	
	private void initRenewButton(){
		renewBtn.setEnabled(false);
		renewBtn.setText(getString(R.string.str_btn_renew_time));
		handler.postDelayed(runnable, 1000);
	}
	
	Handler handler = new Handler();  
    Runnable runnable = new Runnable() {  
        @Override  
        public void run() {  
            recLen--;  
            String str = new String(""+recLen);
            if(str.length()==1){
            	str = "0"+str;
            }
            if(recLen == 0){
            	renewBtn.setText(getString(R.string.str_btn_renew)); 
            	renewBtn.setEnabled(true);
            	handler.removeCallbacks(runnable);
            	recLen = WAIT_TIME;
            }else{
            	renewBtn.setText(str+"秒");  
            	handler.postDelayed(this, 1000);  
            }
        }  
    };

	private void getData() {
		Bundle b = getIntent().getExtras();
		operType = b.getInt(BUNDLE_VERIFY_TYPE);
		phone = b.getString(BUNDLE_VERIFY_PHONE);
		verify = b.getString(BUNDLE_VERIFY_CODE);
		Utils.logh(TAG, "getData::: " + "operType: " + operType + " phone: " + phone + " verify: " + verify);
	}

	private void getViews() {
		TextView title = (TextView) findViewById(R.id.account_pverify_title);
		if(AccConst.OPER_TYPE_REGISTER == operType) {
			title.setText(R.string.str_reg_phone_verify_title);
		} else if(AccConst.OPER_TYPE_RESET == operType) {
			title.setText(R.string.str_reset_phone_verify_title);
		}else if(AccConst.OPER_TYPE_REBIND == operType){
			title.setText(R.string.str_rebind_phone_verify_title);
		}
		TextView phoneHint = (TextView) findViewById(R.id.account_pverify_hint_phone);
		phoneHint.setText(phone);
		findViewById(R.id.account_pverify_topbar_back).setOnClickListener(this);
		renewBtn = (Button) findViewById(R.id.account_pverify_renew);
		renewBtn.setOnClickListener(this);
		findViewById(R.id.account_pverify_next).setOnClickListener(this);
		inputVerifyEt = (EditText) findViewById(R.id.account_pverify_input_et);
		inputVerifyEt.addTextChangedListener(new TextWatcherBkgVariable(inputVerifyEt));
//		if(Utils.LOG_H) {
//			inputVerifyEt.setText(verify);
//		}
	}

	private void onVerifyNextClick(String verify) {
		if(null == verify || verify.length() < 6 ) {
			Toast.makeText(this, R.string.str_toast_input_verify, Toast.LENGTH_SHORT).show();
			return ;
		}
		// check verify with local data
//		if(!verify.equals(this.verify)) {
//			Toast.makeText(this, R.string.str_toast_error_verify, Toast.LENGTH_SHORT).show();
//			inputVerifyEt.setText("");
//			inputVerifyEt.requestFocus();
//			return ;
//		}
		// do request
		if(AccConst.OPER_TYPE_REGISTER == operType) {
			
			/*改了短信接口，没有写验证的功能，就不能在服务器端验证了*/
			//registerVerifyPhone(phone, verify);
			
			if(verify.equalsIgnoreCase(verify)) {
				
				InfoSetupActivity.startInfoSetup(PhoneVerifyActivity.this, phone);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				
			} else {
				
				Toast.makeText(this, R.string.str_toast_error_verify, Toast.LENGTH_SHORT).show();
				return ;
			}
			
			
		} else if(AccConst.OPER_TYPE_RESET == operType) {
			resetVerifyPhone(phone, verify);
		} else if(AccConst.OPER_TYPE_REBIND == operType) {
			rebindVerifyPhone(phone, verify);
		}
	}

	private void registerVerifyPhone(final String phone, final String verify) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_verify_phone);
		new AsyncTask<Object, Object, Integer>() {
			RegisterVerifyPhone request = new RegisterVerifyPhone(PhoneVerifyActivity.this, phone, verify);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					InfoSetupActivity.startInfoSetup(PhoneVerifyActivity.this, phone);
					overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				} else {
					Toast.makeText(PhoneVerifyActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private void resetVerifyPhone(final String phone, final String verify) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_verify_phone);
		new AsyncTask<Object, Object, Integer>() {
			ResetVerifyPhone request = new ResetVerifyPhone(PhoneVerifyActivity.this, phone, verify);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					ResetPwdActivity.startResetPwd(PhoneVerifyActivity.this, phone, request.getSn());
					overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				} else {
					Toast.makeText(PhoneVerifyActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private void rebindVerifyPhone(final String phone, final String verify) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_verify_phone);
		new AsyncTask<Object, Object, Integer>() {
			RebindVerifyPhone request = new RebindVerifyPhone(PhoneVerifyActivity.this, MainApp.getInstance().getCustomer().sn,phone, verify);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					backToLogin(phone);
				} else {
					Toast.makeText(PhoneVerifyActivity.this,
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
	
	private void resbumitPhone(final String phone) {
		if(AccConst.OPER_TYPE_REGISTER == operType) {
			registerSubmitPhone(phone);
		} else if(AccConst.OPER_TYPE_RESET == operType) {
			resetSubmitPhone(phone);
		}
	}
	
	private void registerSubmitPhone(final String phone) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_get_verify);
		new AsyncTask<Object, Object, Integer>() {
			RegisterSubmitPhone request = new RegisterSubmitPhone(PhoneVerifyActivity.this, phone);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					verify = request.getVerifyCode();
//					if(Utils.LOG_H) {
//						inputVerifyEt.setText(verify);
//					}
					initRenewButton();
				} else {
					Toast.makeText(PhoneVerifyActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

	private void resetSubmitPhone(final String phone) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_get_verify);
		new AsyncTask<Object, Object, Integer>() {
			ResetSubmitPhone request = new ResetSubmitPhone(PhoneVerifyActivity.this, phone);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					verify = request.getVerifyCode();
//					if(Utils.LOG_H) {
//						inputVerifyEt.setText(verify);
//					}
					initRenewButton();
				} else {
					Toast.makeText(PhoneVerifyActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.account_pverify_topbar_back:
				finishWithAnim();
				break;
			case R.id.account_pverify_next:
				//onVerifyNextClick(Utils.getEditTextString(inputVerifyEt));
				
				String code = inputVerifyEt.getText().toString();
				onVerifyNextClick(code);
				break;
			case R.id.account_pverify_renew:
				// renew verify code
				resbumitPhone(phone);
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
