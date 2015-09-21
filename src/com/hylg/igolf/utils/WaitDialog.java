package com.hylg.igolf.utils;

import com.hylg.igolf.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class WaitDialog {
	private static Dialog waitDialog = null;
	
	public static void showWaitDialog(Context context, int msgId) {
		showWaitDialog(context, context.getString(msgId));
	}
	
	public static void showWaitDialog(Context context, String msg) {
		if(null == waitDialog) {
			View view = View.inflate(context, R.layout.loading, null);
			waitDialog = new Dialog(context, R.style.alert_dialog_style);
			waitDialog.setContentView(view);
			waitDialog.setCancelable(false);
		}
		TextView msgTv = (TextView) waitDialog.findViewById(R.id.loading_msg);
		msgTv.setText(msg);
		if(!waitDialog.isShowing()) {
			waitDialog.show();
		}
	}
	
	public static void dismissWaitDialog() {
		if(null != waitDialog && waitDialog.isShowing()) {
			waitDialog.dismiss();
			waitDialog = null;
		}
	}
}
