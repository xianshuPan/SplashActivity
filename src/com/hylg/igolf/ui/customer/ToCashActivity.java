package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.loader.GetMyBalanceRecordListLoader;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.CommitCardAndPayPsw;
import com.hylg.igolf.cs.request.CommitToCashInfo;
import com.hylg.igolf.cs.request.RegisterSubmitPhone;
import com.hylg.igolf.cs.request.ResetSubmitPhone;
import com.hylg.igolf.cs.request.UpdateCard;
import com.hylg.igolf.cs.request.UpdatePayPsw;
import com.hylg.igolf.ui.view.CircleImageView;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.WaitDialog;

import org.w3c.dom.Text;

public class ToCashActivity extends FragmentActivity implements OnClickListener {
	
	private final String 				TAG 						= "MyBalanceRecordActivity";
	
	private ImageButton  				mBack 						= null;

	private TextView                    mBankNameTxt,mCardNoTxt,mCommitTxt,mTipsTxt,mTimerTxt,mForgotPswTxt;

	private EditText                    mAmountEdit,mVerifyCodeEdit;

	private RelativeLayout              mBankRelative;

	private CircleImageView             mBankImage;

	private FragmentActivity 			mContext 					= null;
	
	private Customer 					customer                 	= null;

	public static final int 			WAIT_TIME 					= 60;
	private int 						recLen 					= WAIT_TIME;

	private String                      mAmountStr,mVerifyCodeStr ,mPayPswStr;
	
	
	public static void startToCashActivity(Activity context) {

		Intent intent = new Intent(context, ToCashActivity.class);

		context.startActivity(intent);
	}
	
	public static void startToCashActivity(Fragment context) {

		Intent intent = new Intent(context.getActivity(), ToCashActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.customer_info_my_balance_to_cash);
		
		mBack =  (ImageButton)  findViewById(R.id.to_cash_topbar_back);
		mBankNameTxt = (TextView) findViewById(R.id.to_cash_bank_nameText);
		mTipsTxt = (TextView) findViewById(R.id.to_cash_tipsText);
		mCardNoTxt = (TextView) findViewById(R.id.to_cash_card_noText);
		mTimerTxt = (TextView) findViewById(R.id.to_cash_verify_get);
		mCommitTxt = (TextView) findViewById(R.id.to_cash_commit);

		mVerifyCodeEdit = (EditText) findViewById(R.id.to_cash_verify_edit);
		mAmountEdit = (EditText) findViewById(R.id.to_cash_amount_edit);

		mBankRelative = (RelativeLayout) findViewById(R.id.to_cash_cardRelative);
		
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContext.finish();
			}
		});

		mTimerTxt.setOnClickListener(this);
		mCommitTxt.setOnClickListener(this);
		mBankRelative.setOnClickListener(this);
		customer = MainApp.getInstance().getCustomer();

		String card_no = MainApp.getInstance().getGlobalData().getCardNo();

		if (card_no != null && card_no.length() > 15) {

			int index = card_no.length();
			mCardNoTxt.setText("尾号 "+card_no.substring(index-4,index));
		}

		mBankNameTxt.setText("建设银行");
		mAmountEdit.setHint("本次最多提现"+MainApp.getInstance().getGlobalData().getBalance()+"元");

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
			}else{
				mTimerTxt.setText(str+"秒");
				handler.postDelayed(this, 1000);
			}
		}
	};

	private void commit() {

		mAmountStr = mAmountEdit.getText().toString();
		if (mAmountStr == null || mAmountStr.length() <= 0 || Double.valueOf(mAmountStr) <= 0 ||
				Double.valueOf(mAmountStr) > MainApp.getInstance().getGlobalData().getBalance()	) {

			Toast.makeText(mContext, "请输入有效金额", Toast.LENGTH_SHORT).show();
			return;
		}

		mVerifyCodeStr = mVerifyCodeEdit.getText().toString();
		if (mVerifyCodeStr == null || mVerifyCodeStr.length() < 6) {

			Toast.makeText(mContext, R.string.str_toast_input_verify, Toast.LENGTH_SHORT).show();
			return;
		}


		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		View view = mContext.getLayoutInflater().inflate(R.layout.input_pay_psw, null);
		final EditText payPsw = (EditText) view.findViewById(R.id.input_pay_pswEdit);
		mForgotPswTxt = (TextView) view.findViewById(R.id.input_pay_psw_forgot_text);
		mForgotPswTxt.setOnClickListener(this);
		dialog.setView(view);
		dialog.setTitle(getResources().getString(R.string.str_to_cash_pay_psw));
		dialog.setPositiveButton(R.string.str_photo_commit, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				String payPswStr = payPsw.getText().toString();

				if (payPswStr == null || payPswStr.length() < 4) {

					Toast.makeText(mContext, R.string.str_input_to_cash_pay_psw, Toast.LENGTH_SHORT).show();
					return;
				}

				mPayPswStr = payPsw.getText().toString();

				commitToCash ();
			}
		});
		dialog.setNegativeButton(R.string.str_photo_cancel,new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});

		dialog.show();

	}


	private void commitToCash () {

		WaitDialog.showWaitDialog(this, R.string.str_committing);
		new AsyncTask<Object, Object, Integer>() {
			CommitToCashInfo request = new CommitToCashInfo(mContext, customer.id,mVerifyCodeStr,mPayPswStr,mAmountStr);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {

					MyBalanceRecordActivity.startMyBalanceRecordActivity(mContext);

				} else {
					Toast.makeText(mContext, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);

	}


	@Override
	public void onClick (View view) {

		if (view.getId() == mTimerTxt.getId()) {

			getVerifyCode();

		} else if (view.getId() == mCommitTxt.getId()) {

			commit();

		} else if (view.getId() == mBankRelative.getId()) {

			BindCardActivity.startBindCardActivity(mContext,Const.UPDATE_CARD);

		} else if (view.getId() == mForgotPswTxt.getId()) {

			BindCardActivity.startBindCardActivity(mContext,Const.RESET_PAY_PASSWORD);
		}

	}
}
