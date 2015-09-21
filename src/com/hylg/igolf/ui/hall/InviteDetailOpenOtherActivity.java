package com.hylg.igolf.ui.hall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.OpenInvitationInfo;
import com.hylg.igolf.cs.data.OpenInviteDetail;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.GetOpenInviteDetail;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

/**
 * 他人发起的大厅约球，可查看时(申请结束，之前)的详情页面
 *
 */
public class InviteDetailOpenOtherActivity extends InviteDetailOpenActivity implements OnClickListener {
	private static final String TAG = "InviteDetailOpenOtherActivity";
	private static final String BUNDLE_KEY_OPEN_INVITATION_INFO = "open_invitation_info";
	private Button reqApplyBtn, reqCancelBtn;
	
	public static void startInviteDetailOpenOtherForResult(Context context, OpenInvitationInfo invitation) {
		Intent intent = new Intent(context, InviteDetailOpenOtherActivity.class);
		intent.putExtra(BUNDLE_KEY_OPEN_INVITATION_INFO, invitation);
		((Activity) context).startActivityForResult(intent, Const.REQUST_CODE_INVITE_DETAIL_OPEN);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getAndSetViews();
		getDetailAsync();
	}

	private void getAndSetViews() {
		RelativeLayout operBar = (RelativeLayout) View.inflate(this, R.layout.invite_detail_oper_bar_apply_cancel, null);
		addOperBarLayout(operBar);
		reqApplyBtn = (Button) operBar.findViewById(R.id.invite_detail_oper_btn_req_apply);
		reqApplyBtn.setOnClickListener(this);
		reqCancelBtn = (Button) operBar.findViewById(R.id.invite_detail_oper_btn_req_cancel);
		reqCancelBtn.setOnClickListener(this);
	}
	
	private void getDetailAsync() {
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		new AsyncTask<Object, Object, Integer>() {
			GetOpenInviteDetail request = new GetOpenInviteDetail(InviteDetailOpenOtherActivity.this, customer.sn, invitation.sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				switch(result) {
					case BaseRequest.REQ_RET_OK:
						appendMoreData(request.getOpenInviteDetail());
						break;
					case BaseRequest.REQ_RET_F_OPEN_ACCEPT_OTHER:
						toastShort(request.getFailMsg());
						finishWithResult(true);
						break;
					default:
						toastShort(request.getFailMsg());
//						disableRequestButtons();
						dismissOperBar();
						break;
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private void appendMoreData(OpenInviteDetail detail) {
//		Utils.logh(TAG, "detail----------- \n" + detail.log());
		appendCommonData(detail.inviter, detail.invitee,detail.inviteeone,detail.inviteetwo,
				detail.message, detail.paymentType, detail.stake);
		// open: app info, not plans
		displayAppInfo(invitation.teeTime, invitation.courseName);
		// score
		dismissScoreRegion();
		// rate
		dismissRateRegion();
		// request
		Utils.logh(TAG, "displayStatus: " + invitation.displayStatus + " payType: " + invitation.payType);
		switch(invitation.displayStatus) {
			case Const.INVITE_OPEN_GREEN_WAIT:
				displayRequestRegionOther(0);
				break;
			case Const.INVITE_OPEN_YELLOW_APPLYING:
				displayRequestRegionOther(invitation.applicantsNum);
				break;
		}
		if(detail.isApplied) {
			Utils.setVisibleGone(reqCancelBtn, reqApplyBtn);
		} else {
			Utils.setVisibleGone(reqApplyBtn, reqCancelBtn);
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.invite_detail_oper_btn_req_apply:
				clickReqApply();
				break;
			case R.id.invite_detail_oper_btn_req_cancel:
				clickReqCancel();
				break;
			default:
				super.onClick(v);
				break;
		}
	}

	private void clickReqCancel() {
		cancelOpenInvite(invitation.sn, customer.sn, new cancelOpenInviteCallback() {
			@Override
			public void callBack(int retId, int applyNum, String msg) {
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						Utils.setVisibleGone(reqApplyBtn, reqCancelBtn);
						toastShort(msg);
						setFinishResult(true);
						refreshRequestTitle(applyNum);
						MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
						break;
					case BaseRequest.REQ_RET_F_APP_ACCEPT: {
						// 对方已经接受自己的请求。
						toastShort(msg);
						finishWithResult(true);						
						break;
					}
					case BaseRequest.REQ_RET_F_APP_PREPARE: {
						// 对方已经接受自己的请求，且进入打球准备阶段。
						toastShort(msg);
						finishWithResult(true);	
						break;
					}
					case BaseRequest.REQ_RET_F_OPEN_ACCEPT_OTHER: {
						// 对方接受了其他人
						toastShort(msg);
						finishWithResult(true);
						break;
					}
					case BaseRequest.REQ_RET_F_APP_OVERDUE: {
						// 对方或系统撤销了约球
						toastShort(msg);
						finishWithResult(true);
						break;
					}
					default:
						toastShort(msg);
						break;
				}
			}
		});
	}

	private void clickReqApply() {
		applyOpenInvite(invitation.sn, customer.sn, new applyOpenInviteCallback() {
			@Override
			public void callBack(int retId, int applyNum, String msg) {
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						Utils.setVisibleGone(reqCancelBtn, reqApplyBtn);
						toastShort(msg);
						setFinishResult(true);
						refreshRequestTitle(applyNum);
						MainApp.getInstance().getGlobalData().setHasStartNewInvite(true);
						break;
					case BaseRequest.REQ_RET_F_OPEN_ACCEPT_OTHER: {
						// 对方接受了其他人
						toastShort(msg);
						finishWithResult(true);
						break;
					}
					case BaseRequest.REQ_RET_F_APP_OVERDUE: {
						// 对方或系统撤销了约球
						toastShort(msg);
						finishWithResult(true);
						break;
					}
					default:
						toastShort(msg);
						break;
				}
			}
		});
	}
	
}
