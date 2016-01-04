package com.hylg.igolf.ui.hall;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.ApplicantsInfo;
import com.hylg.igolf.cs.data.InviteRoleInfo;
import com.hylg.igolf.cs.data.MyInviteDetail;
import com.hylg.igolf.cs.data.MyInviteInfo;
import com.hylg.igolf.cs.request.AcceptOpenInvite;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.EndOpenInvite;
import com.hylg.igolf.cs.request.EndOpenInvite01;
import com.hylg.igolf.ui.hall.adapter.ApplicantsAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

/**
 * 我发起的开放式约球。
 * 后续状态：
 * 		显示状态		说明
		等申请		我发起了约球，还没有人来申请
		有申请		有人来申请约球
		已接受		我接受了他人的申请
		已撤销		我撤销了约球
		已撤约		任一一方撤约
		待签到		约球单为接受，并当前时间小于开球时间
		进行中		当前时间大于等于开球时间，并未记分或未评价对方
		已完成		有记分并有已评价对方

 * 
 */
public class InviteDetailMyOpenActivity extends InviteDetailMineActivity {
//	private static final String TAG = "InviteDetailMyOpenActivity";
	private ApplicantsAdapter applicantsAdapter;
	
	
	private MyInviteDetail detail;
	
	private boolean isEndInvite = false;
	
//	public static void startInviteDetailMyOpenForResult(Fragment fragment, MyInviteInfo invitation) {
//		Intent intent = new Intent(fragment.getActivity(), InviteDetailMyOpenActivity.class);
//		intent.putExtra(BUNDLE_KEY_DETAIL_INFO, invitation);
//		fragment.startActivityForResult(intent, Const.REQUST_CODE_INVITE_DETAIL_MY_OPEN);
//		fragment.getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
//	}
	
	public static void startInviteDetailMyOpenForCallback(Fragment fragment, MyInviteInfo invitation) {
		
		callback = (onResultCallback) fragment;
		Intent intent = new Intent(fragment.getActivity(), InviteDetailMyOpenActivity.class);
		intent.putExtra(BUNDLE_KEY_DETAIL_INFO, invitation);
		fragment.startActivity(intent);
		fragment.getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	public static void startInviteDetailMyOpenForCallback(FragmentActivity fragment, MyInviteInfo invitation) {

		callback = (onResultCallback) fragment;
		Intent intent = new Intent(fragment, InviteDetailMyOpenActivity.class);
		intent.putExtra(BUNDLE_KEY_DETAIL_INFO, invitation);
		fragment.startActivity(intent);
		fragment.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getAndSetViews();
		getDetailAsync();
	}
	
	
	@Override
	protected void onDestroy () {

		/*
		* pxs 2015.12.30 update 不用手动添加候选人给 invitee 、inviteone、 invitetwo
		* */
//		if (applicantsAdapter != null && applicantsAdapter.getSelectedApplicatant() != null &&
//			applicantsAdapter.getSelectedApplicatant().size() > 0 && !isEndInvite) {
//
//			doAcceptInviteApp(invitation.sn, applicantsAdapter.getSelectedApplicatant());
//		}
		
		super.onDestroy();
	}

	private void getDetailAsync() {
		getMyInviteDetail(invitation.sn, customer.sn, new getMyInviteDetailCallback() {
			@Override
			public void callBack(int retId, String msg, MyInviteDetail detail) {
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						appendMoreData(detail);
						break;
					default:
						toastShort(msg);
						dismissOperBar();
						removeLocation();
						break;
				}
			}
		});
	}

	private void getAndSetViews() {
		// 撤约，签到
		getAppCancelMarkBar();
		appCancelBtn.setOnClickListener(this);
		appMarkBtn.setOnClickListener(this);
		// 撤销，接受
		getAppRevokeAcceptBar();
		appRevokeBtn.setOnClickListener(this);
		appAcceptBtn.setOnClickListener(this);
	}
	
	private void appendMoreData(MyInviteDetail detail1) {
		
		this.detail = detail1;
		
		appendMyCommonData(detail.inviter, detail.invitee,detail.inviteeone,detail.inviteetwo,
				detail.message, detail.paymentType, detail.stake);
		// open: app info, not plans
		displayAppInfo(detail.teeTime, detail.courseName);
		unClickableleRequestRegionMine();
		
		switch(detail1.displayStatus) {
			case Const.MY_INVITE_WAITAPPLY: // 等申请，可撤销
				displayAppRevoke();
				//displayRequestRegionMine(detail.applicants, false, null);
				//pxs 2015.07.24 注释修改
				displayRequestRegionMine(detail, false, null);
				break;
			case Const.MY_INVITE_HAVEAPPLY: // 有申请，可撤销，可接受，需要显示申请人列表
				addOperBarLayout(appRevokeAcceptBar);
				// 显示申请人列表
				//applicantsAdapter = displayRequestRegionMine(detail.applicants, true, null);
				//pxs 2015.07.24 注释修改
				applicantsAdapter = displayRequestRegionMine(detail, true, null);
				break;
			case Const.MY_INVITE_ACCEPTED: // 已接受,我接受了他人的申请。可撤约，仍显示申请人列表，可点不可选。
				displayAppCancel();
				// 显示申请人列表
//				displayRequestRegionMine(detail.applicants, false, detail.invitee.sn);
				dismissRequestRegion();
				
				displayRateRegion(detail);
				if(DISP_SCORE_) return ;
				break;
			case Const.MY_INVITE_CANCELED: // 已撤销,我撤销了约球
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
				// 显示申请人列表
//				displayRequestRegionMine(detail.applicants, false, null);

				/*pxs 2015.12.28 update */
				//dismissRequestRegion();
				break;
			case Const.MY_INVITE_REVOKED: // 已撤约,任一一方撤约
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				displayDisableBtn(R.string.str_invite_detail_oper_btn_app_cancel_done);
				// 显示申请人列表
//				displayRequestRegionMine(detail.applicants, false, detail.invitee.sn);

				/*pxs 2015.12.28 update */
				//dismissRequestRegion();
				break;
			case Const.MY_INVITE_WAITSIGN: // 待签到,约球单为接受，并当前时间小于开球时间
				displayAppMark();
				// 显示申请人列表
//				displayRequestRegionMine(detail.applicants, false, detail.invitee.sn);
				dismissRequestRegion();
				
				if(DISP_SCORE_) {
					palyingScoreAndRate(detail);
					return ;
				}
				break;
			case Const.MY_INVITE_SIGNED: // 已签到
				
			case Const.MY_INVITE_PLAYING: // 进行中,当前时间大于等于开球时间，并未记分或未评价对方
				palyingScoreAndRate(detail);
				// 显示申请人列表
//				displayRequestRegionMine(detail.applicants, false, detail.invitee.sn);
				dismissRequestRegion();
				// 直接return，break隐藏评分，记分
				return;
			case Const.MY_INVITE_COMPLETE: // 已完成,有记分并有已评价对方
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				// 已完成，显示评分，记分
				completeScoreAndRate(detail);
				dismissRequestRegion();
				// 显示申请人列表
//				displayRequestRegionMine(detail.applicants, false, detail.invitee.sn);
				// 直接return，break隐藏评分，记分
				return;
		}
		// 隐藏评分，记分
		dismissScoreRegion();
		dismissRateRegion();	
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.invite_detail_oper_btn_cancel:
				cancelInviteApp(invitation.sn, customer.sn);
				break;
			case R.id.invite_detail_oper_btn_mark:
				markInviteApp(invitation.sn, customer.sn);
				break;
			case R.id.invite_detail_oper_btn_revoke:
				clickAppRevoke();
				break;
			case R.id.invite_detail_oper_btn_accept:
				
				//doEndInviteApp(invitation.sn);
				//pxs 2015.07.24 update 接受申请人的动作移到，在这个页面结束的时候
				selectEndInviteApp ();
				
				break;
			default:
				super.onClick(v);
				break;
		}
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

	private void clickAppRevoke() {
		revokeInviteApp(invitation.sn, customer.sn, new revokeInviteAppCallback() {
			@Override
			public void callBack(int retId, String msg) {
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						// 撤销成功，更新按钮状态及内容；设置状态变化标记(同时，列表中不再显示，会被转移到约球历史)
						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
						toastShort(msg);
						setFinishResult(true);
						if(null != applicantsAdapter) {
							applicantsAdapter.clearApplicantSn();
						}

						/*pxs 2015.12.29 update*/
						//dismissRequestRegion();
						break;
					case BaseRequest.REQ_RET_F_APP_OVERDUE:
						// 我发起的开放式约球，点击撤销按钮，理论上，只可能是已经过期，系统自动撤销。
						// 不存在对方已接受及进入准备阶段的情况。
						// 更新按钮状态及内容；设置状态变化标记
						toastShort(msg);
						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
						setFinishResult(true);
						if(null != applicantsAdapter) {
							applicantsAdapter.clearApplicantSn();
						}
						break;
					default: // 其他错误状态
						toastShort(msg);
						break;
				}
			}
		});
	}

	//private void doAcceptInviteApp(final String appSn, final ApplicantsInfo apply) {
	private void doAcceptInviteApp(final String appSn, final ArrayList<InviteRoleInfo> applys) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		//WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			AcceptOpenInvite request = new AcceptOpenInvite(InviteDetailMyOpenActivity.this, appSn, applys);
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
						//toastShort(msg);
						
						finishWithResult(true);	
//						applicantsAdapter.setSelectable(false);
//						dismissRequestRegion();
//						if(DISP_SCORE_) {
//							displayScoreRegion();
//							displayRateRegion(detail);
//						}
						
						break;
					case BaseRequest.REQ_RET_F_APP_REVOKE: // 对方已取消申请
						//toastShort(msg);
						setFinishResult(true);
						break;
					case BaseRequest.REQ_RET_F_APP_OVERDUE: // 约球单已过期
						//toastShort(msg);
						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
						setFinishResult(true);

						if (applicantsAdapter != null) {

							applicantsAdapter.clearApplicantSn();
						}

						break;
					default:
						//toastShort(msg);
						break;
				}
				//WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private void doEndInviteApp(final String appSn,final ArrayList<String> applys) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		//WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			EndOpenInvite request = new EndOpenInvite(InviteDetailMyOpenActivity.this, appSn, applys);
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
						
						//InviteDetailMyOpenActivity.this.finish();
						
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
						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
						setFinishResult(true);

						if (applicantsAdapter != null) {

							applicantsAdapter.clearApplicantSn();
						}

						break;
					default:
						toastShort(msg);
						break;
				}
				//WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
}
