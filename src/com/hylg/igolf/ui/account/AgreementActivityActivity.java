package com.hylg.igolf.ui.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.RegisterSubmitPhone;
import com.hylg.igolf.cs.request.ResetSubmitPhone;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import cn.gl.lib.impl.TextWatcherBkgVariable;

public class AgreementActivityActivity extends Activity {
	public static final String TAG = "AgreementActivityActivity";
	
	public static void StartAgreementActivityActivity(FragmentActivity context) {
		Intent intent = new Intent(context, AgreementActivityActivity.class);
		context.startActivity(intent);
		context.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitToLogin.getInstance().addActivity(this);
		setContentView(R.layout.account_ac_agreement);
		getViews();
	}



	private void getViews() {
		TextView read = (TextView) findViewById(R.id.account_ac_agreement_read_text);

		read.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
				finish();
			}
		});
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
