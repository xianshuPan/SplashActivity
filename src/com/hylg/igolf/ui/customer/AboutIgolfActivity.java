package com.hylg.igolf.ui.customer;

import com.hylg.igolf.R;
import com.hylg.igolf.ui.UserGuideActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class AboutIgolfActivity extends Activity {
	private TextView versionTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_ac_about_igolf);
		
		versionTv = (TextView) findViewById(R.id.about_version);
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
