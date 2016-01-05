package com.hylg.igolf.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.CommitFeedback;
import com.hylg.igolf.cs.request.RebindSubmitPhone;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class FeedBackActivity extends Activity implements OnClickListener{
	public static final String TAG = "AgreementActivityActivity";

	private EditText mFeedBackContent = null;
	
	public static void StartFeedBackActivity(FragmentActivity context) {
		Intent intent = new Intent(context, FeedBackActivity.class);
		context.startActivity(intent);
		context.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitToLogin.getInstance().addActivity(this);
		setContentView(R.layout.account_ac_feedback);
		getViews();
	}



	private void getViews() {
		findViewById(R.id.account_ac_feedback_back).setOnClickListener(this);
		findViewById(R.id.account_ac_feedback_commit_text).setOnClickListener(this);

		mFeedBackContent = (EditText) findViewById(R.id.account_ac_feedback_edit);

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.account_ac_feedback_back :

				this.finish();
				this.overridePendingTransition(0,R.anim.ac_slide_right_out);

				break;

			case R.id.account_ac_feedback_commit_text :

				if (mFeedBackContent.getText().toString().length() > 0) {

					commitFeedBack();
				}
				else {

					Toast.makeText(this,R.string.str_feedback_edit,Toast.LENGTH_SHORT).show();
				}


				break;


		}
	}

	private void commitFeedBack() {

		if(!Utils.isConnected(this)){
			return;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_waiting);
		new AsyncTask<Object, Object, Integer>() {
			final CommitFeedback request = new CommitFeedback(FeedBackActivity.this,MainApp.getInstance().getCustomer().sn,mFeedBackContent.getText().toString());
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				if(BaseRequest.REQ_RET_OK == result) {

					Toast.makeText(FeedBackActivity.this,R.string.str_feedback_ok,Toast.LENGTH_SHORT).show();
					FeedBackActivity.this.finish();
					FeedBackActivity.this.overridePendingTransition(0,R.anim.ac_slide_right_out);

				} else {

					request.getFailMsg();
				}
				WaitDialog.dismissWaitDialog();

			}
		}.execute(null, null, null);
	}
}
