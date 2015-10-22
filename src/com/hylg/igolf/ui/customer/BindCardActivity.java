package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.hylg.igolf.cs.data.MyBalanceRecordInfo;
import com.hylg.igolf.cs.loader.GetMyBalanceRecordListLoader;
import com.hylg.igolf.cs.loader.GetMyBalanceRecordListLoader.GetBalanceRecordListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.CommitCardAndPayPsw;
import com.hylg.igolf.cs.request.GetMyBalanceAmount;
import com.hylg.igolf.cs.request.RegisterSubmitPhone;
import com.hylg.igolf.cs.request.ResetSubmitPhone;
import com.hylg.igolf.cs.request.ResetVerifyPhone;
import com.hylg.igolf.cs.request.UpdateCard;
import com.hylg.igolf.cs.request.UpdatePayPsw;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.EhecdListview.OnLoadMoreListener;
import com.hylg.igolf.ui.view.EhecdListview.OnRefreshListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

public class BindCardActivity extends FragmentActivity implements View.OnClickListener {
	
	private final String 				TAG 						= "MyBalanceRecordActivity";
	
	private ImageButton  				mBack 						= null;

	private TextView                    mTitleTxt,mCommitTxt,mAccountTxt,mTimerTxt;

	private EditText                    mCardNoEdit,mPswEdit,mPswConfirmEdit,mVerifyCodeEdit;

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
		mPswEdit = (EditText) findViewById(R.id.bind_card_psw_edit);
		mPswConfirmEdit = (EditText) findViewById(R.id.bind_card_psw_confirm_edit);
		mVerifyCodeEdit = (EditText) findViewById(R.id.bind_card_verify_edit);

		mAccountRelative = (RelativeLayout) findViewById(R.id.bind_card_account_relative);
		mPswRelative = (RelativeLayout) findViewById(R.id.bind_card_psw_all_relative);
		
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
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
	}
	
	@Override
	protected void onResume () {

		super.onResume();
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
			String str = new String(""+recLen);
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

			final String card_no = mCardNoEdit.getText().toString();

			if (card_no == null || card_no.length() <16) {

				Toast.makeText(mContext, "请输入有效的卡号", Toast.LENGTH_SHORT).show();
				return;

			}

			final String psw = mPswEdit.getText().toString();
			if (psw == null || psw.length() < 4 ) {

				Toast.makeText(mContext, "请输入有效的提现密码", Toast.LENGTH_SHORT).show();
				return;
			}

			String psw_confirm = mPswConfirmEdit.getText().toString();
			if (psw_confirm == null || !psw_confirm.equalsIgnoreCase(psw)) {

				Toast.makeText(mContext, R.string.str_toast_pwd_dismatch, Toast.LENGTH_SHORT).show();
				return;
			}

			final String verify_code = mVerifyCodeEdit.getText().toString();
			if (verify_code == null || verify_code.length() < 6) {

				Toast.makeText(mContext, R.string.str_toast_input_verify, Toast.LENGTH_SHORT).show();
				return;
			}


			WaitDialog.showWaitDialog(this, R.string.str_committing);
			new AsyncTask<Object, Object, Integer>() {
				CommitCardAndPayPsw request = new CommitCardAndPayPsw(mContext, customer.id,card_no,verify_code,psw,"建设银行");
				@Override
				protected Integer doInBackground(Object... params) {
					return request.connectUrl();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					if(BaseRequest.REQ_RET_OK == result) {

						MainApp.getInstance().getGlobalData().setCardNo(card_no);
						ToCashActivity.startToCashActivity(mContext);

					} else {
						Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
					}
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);

		} else if (Const.UPDATE_CARD == mOperateTypeInt) {

			final String card_no = mCardNoEdit.getText().toString();
			if (card_no == null || card_no.length() < 16) {

				Toast.makeText(mContext, "请输入有效的卡号", Toast.LENGTH_SHORT).show();
				return;

			}

			final String verify_code = mVerifyCodeEdit.getText().toString();
			if (verify_code == null || verify_code.length() < 6) {

				Toast.makeText(mContext, R.string.str_toast_input_verify, Toast.LENGTH_SHORT).show();
				return;
			}

			WaitDialog.showWaitDialog(this, R.string.str_committing);
			new AsyncTask<Object, Object, Integer>() {
				UpdateCard request = new UpdateCard(mContext, customer.id,card_no,verify_code,"建设银行");
				@Override
				protected Integer doInBackground(Object... params) {
					return request.connectUrl();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					if(BaseRequest.REQ_RET_OK == result) {

						MainApp.getInstance().getGlobalData().setCardNo(card_no);
						ToCashActivity.startToCashActivity(mContext);

					} else {

						Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
					}
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);

		} else if (Const.RESET_PAY_PASSWORD == mOperateTypeInt) {

			final String psw = mPswEdit.getText().toString();
			if (psw == null || psw.length() < 4) {

				Toast.makeText(mContext, "请输入有效的提现密码", Toast.LENGTH_SHORT).show();
				return;
			}

			String psw_confirm = mPswConfirmEdit.getText().toString();
			if (psw_confirm == null || !psw_confirm.equalsIgnoreCase(psw)) {

				Toast.makeText(mContext, R.string.str_toast_pwd_dismatch, Toast.LENGTH_SHORT).show();
				return;
			}

			final String verify_code = mVerifyCodeEdit.getText().toString();
			if (verify_code == null || verify_code.length() < 6) {

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
