package com.hylg.igolf.ui.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.w3c.dom.Text;

public class PhoneSubmitActivity extends FragmentActivity implements View.OnClickListener {
	public static final String TAG = "PhoneSubmitActivity";
	private static final String BUNDLE_SUBMIT_TYPE = "submit_type";
	private int operType;
	private EditText inputPhoneEt,verifyEdit;

	private TextView mGetVerifyTxt;

	private LinearLayout mAgreementLinea = null,mLoginLinear;

	public static final int 			WAIT_TIME 					= 60;
	private int 						recLen 					= WAIT_TIME;

	private String verify_code_str = "";
	
	public static void submitPhoneForRegister(Context context) {
		Intent intent = new Intent(context, PhoneSubmitActivity.class);
		Bundle b = new Bundle();
		b.putInt(BUNDLE_SUBMIT_TYPE, AccConst.OPER_TYPE_REGISTER);
		intent.putExtras(b);
		context.startActivity(intent);
	}
	
	public static void submitPhoneForReset(Context context) {
		Intent intent = new Intent(context, PhoneSubmitActivity.class);
		Bundle b = new Bundle();
		b.putInt(BUNDLE_SUBMIT_TYPE, AccConst.OPER_TYPE_RESET);
		intent.putExtras(b);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitToLogin.getInstance().addActivity(this);
		setContentView(R.layout.account_ac_phone_submit_new);
		getData();
		getViews();
	}

	private void getData() {
		Bundle b = getIntent().getExtras();
		operType = b.getInt(BUNDLE_SUBMIT_TYPE);
		Utils.logh(TAG, "getData::: " + "operType: " + operType);
	}

	private void getViews() {
		//TextView title = (TextView) findViewById(R.id.account_psubmit_title);
		//TextView regHint = (TextView) findViewById(R.id.account_psubmit_reg_hint);
		//LinearLayout resetRegion = (LinearLayout) findViewById(R.id.account_psubmit_reset);

		mGetVerifyTxt = (TextView) findViewById(R.id.verify_code_get);
		mLoginLinear = (LinearLayout) findViewById(R.id.verify_login_linear);
		mAgreementLinea = (LinearLayout) findViewById(R.id.account_psubmit_agreement_linear);
		mAgreementLinea.setOnClickListener(this);
		if(AccConst.OPER_TYPE_REGISTER == operType) {

			//title.setText(R.string.str_reg_phone_submit_title);
			//Utils.setGone(resetRegion);
			Utils.setVisible(mLoginLinear);

		} else if(AccConst.OPER_TYPE_RESET == operType) {
			//title.setText(R.string.str_reset_phone_submit_title);
			//Utils.setVisibleGone(resetRegion);
			Utils.setGone(mAgreementLinea);
			//resetRegion.setOnClickListener(this);
		}
		//findViewById(R.id.account_psubmit_topbar_back).setOnClickListener(this);
		findViewById(R.id.account_psubmit_next).setOnClickListener(this);
		findViewById(R.id.verify_code_get).setOnClickListener(this);
		mLoginLinear.setOnClickListener(this);
		inputPhoneEt = (EditText) findViewById(R.id.account_psubmit_input_et);
		inputPhoneEt.addTextChangedListener(new TextWatcherBkgVariable(inputPhoneEt));

		verifyEdit = (EditText) findViewById(R.id.verify_code_input);
//		TextView callView = (TextView)findViewById(R.id.account_psubmit_call_gm);
//		final String phoneNumber = getResources().getString(R.string.str_reset_phone_submit_num);
//		String info = String.format(
//				getResources().getString(R.string.str_reset_phone_submit_hint),
//				phoneNumber);
//		SpannableStringBuilder style = new SpannableStringBuilder(info);
//		String colorStr = String.valueOf(phoneNumber);
//		int index = info.indexOf(colorStr);
//		style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_help_phone)), index, index + colorStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//		callView.setText(style);
//		callView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + phoneNumber));
//				startActivity(intent);
//			}
//		});
	}
	
	private void onSubmitNextClick(final String phone) {

		if(!Utils.isConnected(this)){
			return;
		}
		if(null == phone) {
			Toast.makeText(this, R.string.str_toast_input_phone, Toast.LENGTH_SHORT).show();
			return ;
		}
		// check phone number format
		if(!Utils.isMobileNum(phone)){
			Utils.logh(TAG, phone);
			Toast.makeText(this, R.string.str_toast_invalid_phone, Toast.LENGTH_SHORT).show();
			return ;
		}
		// ..............
		// show confirm dialog
		final Dialog dialog = new Dialog(this, R.style.alert_dialog_style);
		View view = View.inflate(this, R.layout.dialog_phone_submit, null);
		TextView msg = (TextView) view.findViewById(R.id.dialog_ps_msg_view);
		msg.setText(String.format(getResources().getString(R.string.str_verify_content), phone));
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
				sbumitPhone(phone);
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}

	private void initRenewButton(){
		mGetVerifyTxt.setEnabled(false);
		mGetVerifyTxt.setText(getString(R.string.str_btn_renew_time));
		handler.postDelayed(runnable, 1000);
	}

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			recLen--;
			String str = "" + recLen;
			if(str.length()==1){
				str = "0"+str;
			}
			if(recLen == 0){
				mGetVerifyTxt.setText(getString(R.string.str_btn_renew));
				mGetVerifyTxt.setEnabled(true);
				handler.removeCallbacks(runnable);
				recLen = WAIT_TIME;
			}else{
				mGetVerifyTxt.setText(str+"秒");
				handler.postDelayed(this, 1000);
			}
		}
	};
	
	private void sbumitPhone(final String phone) {

		initRenewButton();
		if(AccConst.OPER_TYPE_REGISTER == operType) {
			registerSubmitPhone(phone);
		} else if(AccConst.OPER_TYPE_RESET == operType) {
			resetSubmitPhone(phone);
		}
	}

	private void registerSubmitPhone(final String phone) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_get_verify);
		new AsyncTask<Object, Object, Integer>() {
			RegisterSubmitPhone request = new RegisterSubmitPhone(PhoneSubmitActivity.this, phone);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
//					PhoneVerifyActivity.verifyPhoneForRegister(PhoneSubmitActivity.this, phone, request.getVerifyCode());
//					overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
					verify_code_str = request.getVerifyCode();

				} else {
					Toast.makeText(PhoneSubmitActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
					if(BaseRequest.REQ_RET_F_REG_ACCOUNT_EXIST == result) {
						backToLogin(phone);
					}
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

	private void resetSubmitPhone(final String phone) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_get_verify);
		new AsyncTask<Object, Object, Integer>() {
			ResetSubmitPhone request = new ResetSubmitPhone(PhoneSubmitActivity.this, phone);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
//					PhoneVerifyActivity.verifyPhoneForReset(PhoneSubmitActivity.this, phone, request.getVerifyCode());
//					overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);

					verify_code_str = request.getVerifyCode();

				} else {
					Toast.makeText(PhoneSubmitActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
					if(BaseRequest.REQ_RET_F_RESET_NOT_REGISTERD == result) {
//						backToLogin(phone);
					}
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
			case R.id.account_psubmit_topbar_back:
				finishWithAnim();
				break;
			case R.id.account_psubmit_next:

				onVerifyNextClick(inputPhoneEt.getText().toString(),verifyEdit.getText().toString());

				break;

			case R.id.verify_code_get :

				onSubmitNextClick(Utils.getEditTextString(inputPhoneEt));

				break;
			case R.id.account_psubmit_reset:
				// set call
				break;

			case R.id.verify_login_linear:

				LoginActivity.backWithPhone(this, "");
				break;

			case R.id.account_psubmit_agreement_linear:

				AgreementActivityActivity.StartAgreementActivityActivity(this);

				break;
		}
	}


	private void onVerifyNextClick(String phone,String verify) {
		if(null == verify || verify.length() < 6 || !verify_code_str.equalsIgnoreCase(verify)) {
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
			registerVerifyPhone(phone, verify);

//			if(verify.equalsIgnoreCase(verify)) {
//
//				InfoSetupActivity.startInfoSetup(this, phone);
//				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
//
//			} else {
//
//				Toast.makeText(this, R.string.str_toast_error_verify, Toast.LENGTH_SHORT).show();
//				return ;
//			}


		} else if(AccConst.OPER_TYPE_RESET == operType) {
			resetVerifyPhone(phone, verify);
		} else if(AccConst.OPER_TYPE_REBIND == operType) {
			rebindVerifyPhone(phone, verify);
		}
	}

	private void registerVerifyPhone(final String phone, final String verify) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_verify_phone);
		new AsyncTask<Object, Object, Integer>() {

			RegisterVerifyPhone request = new RegisterVerifyPhone(PhoneSubmitActivity.this, phone, verify);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					InfoSetupActivity.startInfoSetup(PhoneSubmitActivity.this, phone);
					overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				} else {
					Toast.makeText(PhoneSubmitActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

	private void resetVerifyPhone(final String phone, final String verify) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_verify_phone);
		new AsyncTask<Object, Object, Integer>() {
			ResetVerifyPhone request = new ResetVerifyPhone(PhoneSubmitActivity.this, phone, verify);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					ResetPwdActivity.startResetPwd(PhoneSubmitActivity.this, phone, request.getSn());
					overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				} else {
					Toast.makeText(PhoneSubmitActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

	private void rebindVerifyPhone(final String phone, final String verify) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_verify_phone);
		new AsyncTask<Object, Object, Integer>() {
			RebindVerifyPhone request = new RebindVerifyPhone(PhoneSubmitActivity.this, MainApp.getInstance().getCustomer().sn,phone, verify);
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
					Toast.makeText(PhoneSubmitActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
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
