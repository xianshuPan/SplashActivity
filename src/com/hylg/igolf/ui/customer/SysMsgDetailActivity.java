package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.SysMsgDetail;
import com.hylg.igolf.cs.data.SysMsgInfo;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.GetSystemMsgDetail;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class SysMsgDetailActivity extends Activity {
//	private static final String TAG = "SysMsgDetailActivity";
	private static final String BUNDLE_KEY_SYSTEM_MSG_INFO = "system_msg_info";
	private static final String BUNDLE_KEY_SYSTEM_MSG_POS = "system_msg_pos";
	private TextView titleView;
	private TextView timeView;
	private TextView contentView;
	private SysMsgInfo sysMsgInfo;
	private int position;
	private static GetSysMsgDetailCallback callback = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_ac_sys_msg_detail);
		getViews();
		getDetailAsync();
	}
	
	public static void startSysMsgDetailActivity(Context context, SysMsgInfo sysMsgInfo, int pos) {
		if(sysMsgInfo.status.equals(SysMsgActivity.MSG_STATUS_UNREAD)) {
		// 只需在未读状态下，设置回调更新
			try {
				callback = (GetSysMsgDetailCallback) context;
			} catch (ClassCastException e) {
				throw new ClassCastException(context.toString() + 
						" must implements GetSysMsgDetailCallback");
			}
		}
		Intent intent = new Intent(context, SysMsgDetailActivity.class);
		intent.putExtra(BUNDLE_KEY_SYSTEM_MSG_INFO, sysMsgInfo);
		intent.putExtra(BUNDLE_KEY_SYSTEM_MSG_POS, pos);
		
		context.startActivity(intent);
	}
	
	private void getViews(){
		sysMsgInfo = (SysMsgInfo) getIntent().getSerializableExtra(BUNDLE_KEY_SYSTEM_MSG_INFO);
		position = getIntent().getExtras().getInt(BUNDLE_KEY_SYSTEM_MSG_POS);

		titleView = (TextView) findViewById(R.id.system_msg_detail_title);
		timeView = (TextView) findViewById(R.id.system_msg_detail_time);
		contentView = (TextView) findViewById(R.id.system_msg_detail_content);
		titleView.setText(sysMsgInfo.title);
		timeView.setText(sysMsgInfo.sendTimestamp);
	}
	
	private void getData(SysMsgDetail detail){
		if(null != detail){
			Utils.logh("content",detail.content);
			contentView.setText(detail.content);
			sysMsgInfo.status = SysMsgActivity.MSG_STATUS_READ;
		}
	}
	
	private void getDetailAsync() {
		
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		new AsyncTask<Object, Object, Integer>() {
			GetSystemMsgDetail request = new GetSystemMsgDetail(SysMsgDetailActivity.this, String.valueOf(sysMsgInfo.id));
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				switch(result) {
					case BaseRequest.REQ_RET_OK:
						getData(request.getSysMsgDetail());
						if(null != callback) {
							callback.callBack(position);
						}
						break;
//					case BaseRequest.REQ_RET_EXCEP:
//						toastShort(request.getFailMsg());
//						break;
					default:
						toastShort(request.getFailMsg());
						break;
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
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
	
	public void onSysMsgDetailBackBtnClick(View view) {
		finishWithAnim();
	}

	public interface GetSysMsgDetailCallback{
		public abstract void callBack(int position);
	}
}
