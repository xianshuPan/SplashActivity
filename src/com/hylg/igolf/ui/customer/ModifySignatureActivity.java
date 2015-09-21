package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.ModifySignature;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class ModifySignatureActivity extends Activity implements OnClickListener{
//	private static final String TAG = "ModifySignatureActivity";
	private final static String BUNDLE_SIGNATURE = "signature";
	private EditText signatureEt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_ac_modify_signature);
		getViews();
	}

	public static void startModifySignatureActivity(Activity context,String signature) {
//		try {
//			listener = (OnSignatureChangedListener) context;
//		} catch (ClassCastException e) {
//			throw new ClassCastException(context.toString() + 
//					" must implements OnSignatureChangedListener");
//		}
		Intent intent = new Intent(context, ModifySignatureActivity.class);
		intent.putExtra(BUNDLE_SIGNATURE, signature);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
	}
	
	public static void startModifySignatureActivity(Fragment fragment, String signature) {
//		try {
//			listener = (OnSignatureChangedListener) context;
//		} catch (ClassCastException e) {
//			throw new ClassCastException(context.toString() + 
//					" must implements OnSignatureChangedListener");
//		}
		Intent intent = new Intent(fragment.getActivity(), ModifySignatureActivity.class);
		intent.putExtra(BUNDLE_SIGNATURE, signature);
		fragment.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
	}
	
	private void getViews(){
		findViewById(R.id.modify_signature_topbar_back).setOnClickListener(this);
		findViewById(R.id.modify_signature_save).setOnClickListener(this);
		signatureEt = (EditText) findViewById(R.id.modify_signature_input);
		signatureEt.setText(getIntent().getStringExtra(BUNDLE_SIGNATURE));
		 CharSequence text = signatureEt.getText();
		 if (text instanceof Spannable) {
		    Spannable spanText = (Spannable)text;
		     Selection.setSelection(spanText, text.length());
		 }
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
		case R.id.modify_signature_topbar_back:
			finishWithAnim();
			break;

		case R.id.modify_signature_save:
			saveSignature();
			break;
		}
		
	}
	
	private void saveSignature() {
		if(!Utils.isConnected(this)){
			return;
		}
		final String signature = Utils.getEditTextString(signatureEt)==null?"":Utils.getEditTextString(signatureEt);
		String oldSignature = MainApp.getInstance().getCustomer().signature;
		if(null != oldSignature && null != signature && oldSignature.equals(signature)) {
			Toast.makeText(this, R.string.str_toast_signature_invalid, Toast.LENGTH_SHORT).show();
			return;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_save_signature);
		new AsyncTask<Object, Object, Integer>() {
			ModifySignature request = new ModifySignature(ModifySignatureActivity.this, MainApp.getInstance().getCustomer().sn, signature);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					setResult(RESULT_OK);
					
					finishWithAnim();
				} else {
					Toast.makeText(ModifySignatureActivity.this,
							request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
//	public interface OnSignatureChangedListener {
//		public abstract void onSignatureChanged(String newSignature);
//	}
	
}
