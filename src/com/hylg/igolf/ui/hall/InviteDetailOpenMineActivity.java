package com.hylg.igolf.ui.hall;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.ApplicantsInfo;
import com.hylg.igolf.cs.data.InviteRoleInfo;
import com.hylg.igolf.cs.data.MyInviteDetail;
import com.hylg.igolf.cs.data.OpenInvitationInfo;
import com.hylg.igolf.cs.request.AcceptOpenInvite;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.EndOpenInvite;
import com.hylg.igolf.ui.hall.adapter.ApplicantsAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

/**
 * 我发起的开放式约球，可查看时(申请结束，之前)的详情页面
 * 
 */
public class InviteDetailOpenMineActivity extends InviteDetailOpenActivity {
//	private static final String TAG = "InviteDetailOpenMineActivity";
	private ApplicantsAdapter applicantsAdapter;
	// 撤销，接受按钮
	private LinearLayout appRevokeAcceptBar;
	private Button appRevokeBtn, appAcceptBtn;
	private View appRaSpaceView;
	
	private boolean isEndInvite = false;
	
	public static void startInviteDetailOpenMineForResult(Context context, OpenInvitationInfo invitation) {
		Intent intent = new Intent(context, InviteDetailOpenMineActivity.class);
		intent.putExtra(BUNDLE_KEY_OPEN_INVITATION_INFO, invitation);
		((Activity) context).startActivityForResult(intent, Const.REQUST_CODE_INVITE_DETAIL_OPEN);
	}

	public static void startInviteDetailOpenMineForResult(Fragment context, OpenInvitationInfo invitation) {
		Intent intent = new Intent(context.getActivity(), InviteDetailOpenMineActivity.class);
		intent.putExtra(BUNDLE_KEY_OPEN_INVITATION_INFO, invitation);
		context.startActivityForResult(intent, Const.REQUST_CODE_INVITE_DETAIL_OPEN);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getAndSetViews();
		getDetailAsync();
	}

	private void getDetailAsync() {
		getMyInviteDetail(invitation.sn, customer.sn, new getMyInviteDetailCallback() {
			@Override
			public void callBack(int retId, String msg, MyInviteDetail detail) {
				switch (retId) {
					case BaseRequest.REQ_RET_OK:
						appendMoreData(detail);
						break;
					default:
						toastShort(msg);
						dismissOperBar();
						break;
				}
			}
		});
	}

	private void getAndSetViews() {
//		// 撤销，接受
		appRevokeAcceptBar = (LinearLayout) View.inflate(this, R.layout.invite_detail_oper_bar_revoke_accept, null);
		appRevokeBtn = (Button) appRevokeAcceptBar.findViewById(R.id.invite_detail_oper_btn_revoke);
		appAcceptBtn = (Button) appRevokeAcceptBar.findViewById(R.id.invite_detail_oper_btn_accept);
		appRaSpaceView = appRevokeAcceptBar.findViewById(R.id.invite_detail_oper_ra_btn_space);
		appRevokeBtn.setOnClickListener(this);
		appAcceptBtn.setOnClickListener(this);
	}
	
	private void appendMoreData(MyInviteDetail detail) {
		addOperBarLayout(appRevokeAcceptBar);
		appendCommonData(detail.inviter, detail.invitee, detail.inviteeone, detail.inviteetwo,
				detail.message, detail.paymentType, detail.stake);
		// open: app info, not plans
		displayAppInfo(detail.teeTime, detail.courseName);

		unClickableleRequestRegionMine();
		//2015,12.25 next two line
//		if(detail.applicants != null && detail.applicants.size() > 0) {
//
//			//applicantsAdapter = displayRequestRegionMine(detail.applicants, true, null);
//			//pxs 2015.07.24 注释修改
//			applicantsAdapter = displayRequestRegionMine(detail, true, null);
//
//		} else {
//
//			displayRequestRegionMine(null, false, null);
//			Utils.setVisibleGone(appRevokeBtn, appAcceptBtn, appRaSpaceView);
//		}

		if(detail.select_menber_sn != null && detail.select_menber_sn.size() > 0) {

			applicantsAdapter = displayRequestRegionMine(detail, true, null);

		} else {

			//displayRequestRegionMine(null, false, null);
			Utils.setVisibleGone(appRevokeBtn, appAcceptBtn, appRaSpaceView);
		}


		// 隐藏评分，记分
		dismissScoreRegion();
		dismissRateRegion();	
	}
	
	@Override
	protected void onDestroy () {
		
		if (applicantsAdapter != null && applicantsAdapter.getSelectedApplicatant() != null && 
			applicantsAdapter.getSelectedApplicatant().size() > 0 && !isEndInvite) {
			
			doAcceptInviteApp(invitation.sn, applicantsAdapter.getSelectedApplicatant());
		}
		
		
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.invite_detail_oper_btn_revoke:
				clickAppRevoke();
				break;


			case R.id.invite_detail_oper_btn_accept:

				//doAcceptInviteApp(invitation.sn, applicantsAdapter.getSelectedApplicatant());
				
				selectEndInviteApp();
				break;
			default:
				super.onClick(v);
				break;
		}
	}

	private void clickAppRevoke() {
		revokeInviteApp(invitation.sn, customer.sn, new revokeInviteAppCallback() {
			@Override
			public void callBack(int retId, String msg) {
				switch(retId) {
					// 大厅中撤销成功，或过期，直接返回更新列表(转为申请结束，点击提示到我的约球中查看)
					case BaseRequest.REQ_RET_OK:
					case BaseRequest.REQ_RET_F_APP_OVERDUE:
						toastShort(msg);
						finishWithResult(true);	
						break;
					default: // 其他错误状态
						toastShort(msg);
						break;
				}
			}
		});
	}


	//private void doAcceptInviteApp(final String appSn, final ApplicantsInfo ai) {
	private void doAcceptInviteApp(final String appSn, final ArrayList<InviteRoleInfo> applys) {
		//WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			AcceptOpenInvite request = new AcceptOpenInvite(InviteDetailOpenMineActivity.this, appSn, applys);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				String msg = request.getFailMsg();
				switch(result) {
					case BaseRequest.REQ_RET_OK:
						// 大厅中，接受后，直接提示并退出
//						toastShort(msg);
						toastShort(R.string.str_hall_open_list_click_mine);
						finishWithResult(true);	
						// 接受成功，显示不可操作按钮“已接受”；更新申请人列表状态，不可选。
//						setFinishResult(true);
//						toastShort(msg);
//						applicantsAdapter.setSelectable(false);
//						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_accepted);
						break;
					case BaseRequest.REQ_RET_F_APP_REVOKE: // 对方已取消申请
						toastShort(msg);
						setFinishResult(true);
						break;
					case BaseRequest.REQ_RET_F_APP_OVERDUE: // 约球单已过期
						toastShort(msg);
						finishWithResult(true);
//						applicantsAdapter.setSelectable(false);
//						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
						break;
					default:
						toastShort(msg);
						break;
				}
				//WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private void selectEndInviteApp () {
		
		AlertDialog.Builder dialog = new Builder(this);
		dialog.setTitle(R.string.str_invite_end_invite_app);
		dialog.setMessage(R.string.str_invite_end_invite_app_content);
		dialog.setPositiveButton(R.string.str_photo_commit, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
				isEndInvite = true;
				//doEndInviteApp(invitation.sn,applicantsAdapter.getSelectedApplicatant());
				doEndInviteApp(invitation.sn,detail.select_menber_sn);
			}
		});
		dialog.setNegativeButton(R.string.str_photo_cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				//CartActivity.this.finish();
			}
		});
		dialog.show();
	}
	
	private void doEndInviteApp(final String appSn,final ArrayList<String> applys) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		//WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			EndOpenInvite request = new EndOpenInvite(InviteDetailOpenMineActivity.this, appSn, applys);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				String msg = request.getFailMsg();
				switch(result) {
					case BaseRequest.REQ_RET_OK:
						//refreshAcceptRole(request.getAcceptRoleInfo());// 更新对手数据
						
						
						/* operation ，update
						 * auther,pxs
						 * 2015.07.14
						 * 
						 * 除了 toastShort()函数，其余全部注释
						 * */
						//displayAppCancel();
						//setFinishResult(true);
						
						//InviteDetailOpenMineActivity.this.finish();
						
						toastShort(msg);
						
						finishWithResult(true);	
//						applicantsAdapter.setSelectable(false);
//						dismissRequestRegion();
//						if(DISP_SCORE_) {
//							displayScoreRegion();
//							displayRateRegion(detail);
//						}
						
						break;
					case BaseRequest.REQ_RET_F_APP_REVOKE: // 对方已取消申请
						toastShort(msg);
						setFinishResult(true);
						break;
					case BaseRequest.REQ_RET_F_APP_OVERDUE: // 约球单已过期
						toastShort(msg);
						//displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
						setFinishResult(true);
						applicantsAdapter.clearApplicantSn();
						break;
					default:
						toastShort(msg);
						break;
				}
				//WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
//	private void displayDisableBtn(int msgId) {
//		Utils.setVisibleGone(appRevokeBtn, appAcceptBtn, appRaSpaceView);
//		Utils.setDisable(appRevokeBtn);
//		appRevokeBtn.setText(msgId);
//	}
}
