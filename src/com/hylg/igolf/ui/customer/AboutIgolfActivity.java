package com.hylg.igolf.ui.customer;

import com.hylg.igolf.R;
import com.hylg.igolf.ui.UserGuideActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutIgolfActivity extends Activity {
	private TextView versionTv;

	private LinearLayout mCustomerServiceLinear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_ac_about_igolf);
		
		versionTv = (TextView) findViewById(R.id.about_version);
		mCustomerServiceLinear = (LinearLayout ) findViewById(R.id.about_customer_service_phone_text);
		mCustomerServiceLinear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent data = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getResources().getString(R.string.str_customer_service_phone)));
				startActivity(data);
			}
		});

		try {
			versionTv.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
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
	
	public void onBarBackBtnClick(View view) {
		finishWithAnim();
	}
	
	public void onIntroBtnClick(View view) {
		Intent intent = new Intent();
		intent.setClass(this, UserGuideActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
}
