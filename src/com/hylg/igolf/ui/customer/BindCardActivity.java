package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.CommitCardAndPayPsw;
import com.hylg.igolf.cs.request.ResetSubmitPhone;
import com.hylg.igolf.cs.request.ResetVerifyPhone;
import com.hylg.igolf.cs.request.UpdateCard;
import com.hylg.igolf.cs.request.UpdatePayPsw;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.WaitDialog;
import com.hylg.igolf.utils.BankInfo;

import java.util.ArrayList;

public class BindCardActivity extends FragmentActivity implements View.OnClickListener {
	
	private final String 				TAG 						= "MyBalanceRecordActivity";
	
	private ImageButton  				mBack 						= null;

	private TextView                    mTitleTxt,mCommitTxt,mAccountTxt,mTimerTxt;

	private EditText                    mCardNoEdit,mCardNameEdit,mPswEdit,mPswConfirmEdit,mVerifyCodeEdit;

	private RelativeLayout              mAccountRelative,mPswRelative;

	private FragmentActivity 			mContext 					= null;
	
	private Customer 					customer                 	= null;

	/*
	*  indicate the operating
	*  0,bind card and commit pay password
	*  1,update the card
	*  2,reset pay password
	* */
	private final static String          OPERRATE_TYPE               = "type";

	private int                          mOperateTypeInt             = 0;
	public static final int 			 WAIT_TIME 					 = 60;
	private int 						 recLen 					 = WAIT_TIME;

	private String 						bank_name;
	
	
	public static void startBindCardActivity(Activity context,int type) {

		Intent intent = new Intent(context, BindCardActivity.class);

		intent.putExtra(OPERRATE_TYPE, type);
		context.startActivity(intent);
	}
	
	public static void startBindCardActivity(Fragment context,int type) {

		Intent intent = new Intent(context.getActivity(), BindCardActivity.class);
		intent.putExtra(OPERRATE_TYPE,type);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.customer_info_my_balance_bind_card);
		
		mBack =  (ImageButton)  findViewById(R.id.bind_card_topbar_back);
		mTitleTxt = (TextView) findViewById(R.id.bind_card_title);
		mAccountTxt = (TextView) findViewById(R.id.bind_card_info_user_name_text);
		mTimerTxt = (TextView) findViewById(R.id.bind_card_verify_get);
		mCommitTxt = (TextView) findViewById(R.id.bind_card_commit);

		mCardNoEdit = (EditText) findViewById(R.id.bind_card_no_edit);
		mCardNameEdit = (EditText) findViewById(R.id.bind_card_name_edit);
		mPswEdit = (EditText) findViewById(R.id.bind_card_psw_edit);
		mPswConfirmEdit = (EditText) findViewById(R.id.bind_card_psw_confirm_edit);
		mVerifyCodeEdit = (EditText) findViewById(R.id.bind_card_verify_edit);

		mAccountRelative = (RelativeLayout) findViewById(R.id.bind_card_account_relative);
		mPswRelative = (RelativeLayout) findViewById(R.id.bind_card_psw_all_relative);
		
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (Const.BING_CARD_AND_PASSWORD == mOperateTypeInt) {

					MyBalanceRecordActivity.startMyBalanceRecordActivity(mContext);

				} else if (Const.UPDATE_CARD == mOperateTypeInt) {

					ToCashActivity.startToCashActivity(mContext);

				} else if (Const.RESET_PAY_PASSWORD == mOperateTypeInt) {

					ToCashActivity.startToCashActivity(mContext);
				}

				mContext.finish();
			}
		});

		mTimerTxt.setOnClickListener(this);
		mCommitTxt.setOnClickListener(this);

		customer = MainApp.getInstance().getCustomer();


		Intent data = getIntent();
		if (data != null && data.getIntExtra(OPERRATE_TYPE,-1) >= 0) {

			mOperateTypeInt = data.getIntExtra(OPERRATE_TYPE,-1);

			mAccountTxt.setText(customer.nickname);
			if (Const.BING_CARD_AND_PASSWORD == mOperateTypeInt) {


			} else if (Const.UPDATE_CARD == mOperateTypeInt) {

				mPswRelative.setVisibility(View.GONE);
				mCommitTxt.setText(R.string.str_cusinfo_modify_pwd_do);

			} else if (Const.RESET_PAY_PASSWORD == mOperateTypeInt) {

				mAccountRelative.setVisibility(View.GONE);
				mCommitTxt.setText(R.string.str_cusinfo_modify_pwd_do);
				mTitleTxt.setText(R.string.str_to_cash_set_pay_psw);
			}

		}

		mCardNoEdit.addTextChangedListener(new TextWatcher() {


			int beforeTextLength = 0;
			int onTextLength = 0;
			boolean isChanged = false;

			int location = 0;// 记录光标的位置
			private char[] tempChar;
			private StringBuffer buffer = new StringBuffer();
			int konggeNumberB = 0;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int i1, int i2) {

				beforeTextLength = s.length();
				if (buffer.length() > 0) {
					buffer.delete(0, buffer.length());
				}
				konggeNumberB = 0;
				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == ' ') {
						konggeNumberB++;
					}
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int i, int i1, int i2) {

				onTextLength = s.length();
				buffer.append(s.toString());
				if (onTextLength == beforeTextLength || onTextLength <= 3
						|| isChanged) {
					isChanged = false;
					return;
				}
				isChanged = true;
			}

			@Override
			public void afterTextChanged(Editable editable) {

				if (isChanged) {
					location = mCardNoEdit.getSelectionEnd();
					int index = 0;
					while (index < buffer.length()) {
						if (buffer.charAt(index) == ' ') {
							buffer.deleteCharAt(index);
						} else {
							index++;
						}
					}

					index = 0;
					int konggeNumberC = 0;
					while (index < buffer.length()) {
						if ((index == 4 || index == 9 || index == 14 || index == 19)) {
							buffer.insert(index, ' ');
							konggeNumberC++;
						}
						index++;
					}

					if (konggeNumberC > konggeNumberB) {
						location += (konggeNumberC - konggeNumberB);
					}

					tempChar = new char[buffer.length()];
					buffer.getChars(0, buffer.length(), tempChar, 0);
					String str = buffer.toString();
					if (location > str.length()) {
						location = str.length();
					} else if (location < 0) {
						location = 0;
					}

					mCardNoEdit.setText(str);
					Editable etable = mCardNoEdit.getText();
					Selection.setSelection(etable, location);
					isChanged = false;
				}

				String result = editable.toString();
				String asdf = result.replace(" ","");
				if ( asdf.length() >15) {

					String result1 = asdf.substring(0, 6);

					char[] cardNumber = result1.toCharArray();

					bank_name = BankInfo.getNameOfBank(cardNumber, 0);

					if (bank_name != null && bank_name.length() > 0) {

						mCardNameEdit.setText(bank_name);
						mCardNameEdit.setEnabled(false);

					} else {

						mCardNameEdit.setEnabled(true);
					}

				} else {

					mCardNameEdit.setText("");
				}

			}
		});

		mCardNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub

				if (arg1) {

					//mCardNameEdit.setBackgroundResource(R.drawable.frame_bkg);
					mCardNameEdit.setHint("请正确填写银行的名称");

				} else {

					//mCardNameEdit.setBackgroundColor(getResources().getColor(R.color.color_white));
					mCardNameEdit.setHint("");
				}
			}
		});
	}
	
	@Override
	protected void onResume () {

		super.onResume();
	}

	@Override
	public boolean onKeyDown(int key_code,KeyEvent envent){

		if (key_code == KeyEvent.KEYCODE_BACK) {

			if (Const.BING_CARD_AND_PASSWORD == mOperateTypeInt) {

				MyBalanceRecordActivity.startMyBalanceRecordActivity(mContext);

			} else if (Const.UPDATE_CARD == mOperateTypeInt) {

				ToCashActivity.startToCashActivity(mContext);

			} else if (Const.RESET_PAY_PASSWORD == mOperateTypeInt) {

				ToCashActivity.startToCashActivity(mContext);
			}
			mContext.finish();
		}

		return super.onKeyDown(key_code,envent);
	}


	/*
	*
	* get verify code
	* */
	private void getVerifyCode() {

		WaitDialog.showWaitDialog(this, R.string.str_loading_get_verify);
		new AsyncTask<Object, Object, Integer>() {
			ResetSubmitPhone request = new ResetSubmitPhone(mContext, customer.phone);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					//verify = request.getVerifyCode();
					initRenewButton();

				} else {
					Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

	private void initRenewButton(){
		mTimerTxt.setEnabled(false);
		mTimerTxt.setText(getString(R.string.str_btn_renew_time));
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
				mTimerTxt.setText(getString(R.string.str_btn_renew));
				mTimerTxt.setEnabled(true);
				handler.removeCallbacks(runnable);
				recLen = WAIT_TIME;
			} else {
				mTimerTxt.setText(str+"秒");
				handler.postDelayed(this, 1000);
			}
		}
	};

	private void commit() {

		if (Const.BING_CARD_AND_PASSWORD == mOperateTypeInt) {

			String card_no_str = mCardNoEdit.getText().toString();

			if (card_no_str.length() <16) {

				Toast.makeText(mContext, "请输入有效的卡号", Toast.LENGTH_SHORT).show();
				return;

			}
			final String card_no = card_no_str.replace(" ","");

			final String card_name = mCardNameEdit.getText().toString();
			if (card_name.length() <= 0) {

				Toast.makeText(mContext, R.string.str_toast_input_bank_name, Toast.LENGTH_SHORT).show();
				return;
			}

			final String psw = mPswEdit.getText().toString();
			if (psw.length() < 4 ) {

				Toast.makeText(mContext, "请输入有效的提现密码", Toast.LENGTH_SHORT).show();
				return;
			}

			String psw_confirm = mPswConfirmEdit.getText().toString();
			if (!psw_confirm.equalsIgnoreCase(psw)) {

				Toast.makeText(mContext, R.string.str_toast_pwd_dismatch, Toast.LENGTH_SHORT).show();
				return;
			}

			final String verify_code = mVerifyCodeEdit.getText().toString();
			if (verify_code.length() < 6) {

				Toast.makeText(mContext, R.string.str_toast_input_verify, Toast.LENGTH_SHORT).show();
				return;
			}

			WaitDialog.showWaitDialog(this, R.string.str_committing);
			new AsyncTask<Object, Object, Integer>() {
				CommitCardAndPayPsw request = new CommitCardAndPayPsw(mContext, customer.id,card_no,verify_code,psw,card_name);
				@Override
				protected Integer doInBackground(Object... params) {
					return request.connectUrl();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					if(BaseRequest.REQ_RET_OK == result) {

						MainApp.getInstance().getGlobalData().setCardNo(card_no);
						MainApp.getInstance().getGlobalData().setBankName(card_name);

						ToCashActivity.startToCashActivity(mContext);
						mContext.finish();

					} else {
						Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
					}
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);

		} else if (Const.UPDATE_CARD == mOperateTypeInt) {

			String card_no_str = mCardNoEdit.getText().toString();
			if (card_no_str.length() < 16) {

				Toast.makeText(mContext, "请输入有效的卡号", Toast.LENGTH_SHORT).show();
				return;

			}
			final String card_no = card_no_str.replace(" ","");

			final String card_name = mCardNameEdit.getText().toString();
			if (card_name.length() <= 0) {

				Toast.makeText(mContext, R.string.str_toast_input_bank_name, Toast.LENGTH_SHORT).show();
				return;
			}

			final String verify_code = mVerifyCodeEdit.getText().toString();
			if (verify_code.length() < 6) {

				Toast.makeText(mContext, R.string.str_toast_input_verify, Toast.LENGTH_SHORT).show();
				return;
			}

			WaitDialog.showWaitDialog(this, R.string.str_committing);
			new AsyncTask<Object, Object, Integer>() {
				UpdateCard request = new UpdateCard(mContext, customer.id,card_no,verify_code,card_name);
				@Override
				protected Integer doInBackground(Object... params) {
					return request.connectUrl();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					if(BaseRequest.REQ_RET_OK == result) {

						MainApp.getInstance().getGlobalData().setCardNo(card_no);
						MainApp.getInstance().getGlobalData().setBankName(card_name);

						ToCashActivity.startToCashActivity(mContext);
						mContext.finish();

					} else {

						Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
					}
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);

		} else if (Const.RESET_PAY_PASSWORD == mOperateTypeInt) {

			final String psw = mPswEdit.getText().toString();
			if (psw.length() < 4) {

				Toast.makeText(mContext, "请输入有效的提现密码", Toast.LENGTH_SHORT).show();
				return;
			}

			String psw_confirm = mPswConfirmEdit.getText().toString();
			if (!psw_confirm.equalsIgnoreCase(psw)) {

				Toast.makeText(mContext, R.string.str_toast_pwd_dismatch, Toast.LENGTH_SHORT).show();
				return;
			}

			final String verify_code = mVerifyCodeEdit.getText().toString();
			if (verify_code.length() < 6) {

				Toast.makeText(mContext, R.string.str_toast_input_verify, Toast.LENGTH_SHORT).show();
				return;
			}

			WaitDialog.showWaitDialog(this, R.string.str_committing);
			new AsyncTask<Object, Object, Integer>() {
				UpdatePayPsw request = new UpdatePayPsw(mContext, customer.id,verify_code,psw,"建设银行");
				@Override
				protected Integer doInBackground(Object... params) {
					return request.connectUrl();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					if(BaseRequest.REQ_RET_OK == result) {

						ToCashActivity.startToCashActivity(mContext);
						mContext.finish();

					} else {

						Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
					}
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);
		}

	}


	@Override
	public void onClick (View view) {

		if (view.getId() == mTimerTxt.getId()) {

			getVerifyCode();

		} else if (view.getId() == mCommitTxt.getId()) {

			commit();
		}

	}
}
