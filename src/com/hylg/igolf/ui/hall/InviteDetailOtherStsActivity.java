package com.hylg.igolf.ui.hall;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.MyInviteDetail;
import com.hylg.igolf.cs.data.MyInviteInfo;
import com.hylg.igolf.cs.data.PlanShowInfo;
import com.hylg.igolf.cs.request.AcceptStsInvite;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.RefuseStsInvite;
import com.hylg.igolf.ui.hall.adapter.PlanSelectAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

/**
 * 他人发起的一对一邀约
 * 后续状态：
 * 		显示状态		说明
		等接受		我可以接受约球
		已接受		我接受了约球
		已撤销		对方撤销了约球
		已撤约		任一一方撤约
		已拒绝		我拒绝了邀请
		待签到		约球单为接受，并当前时间小于开球时间
		进行中		当前时间大于等于开球时间，并未记分或未评价对方
		已完成		有记分并有已评价对方

 * 	注：他人发起的一对一邀约，我无法进行撤销（为拒绝，接受。接受后，可撤约(2小时之前)或签到(2小时之后)）
 * 
 */
public class InviteDetailOtherStsActivity extends InviteDetailMineActivity {
//	private static final String TAG = "InviteDetailOtherStsActivity";
	private PlanSelectAdapter planAdapter;
//	
	
	private MyInviteDetail detail;
//	public static void startInviteDetailOtherStsForResult(Fragment fragment, MyInviteInfo invitation) {
//		Intent intent = new Intent(fragment.getActivity(), InviteDetailOtherStsActivity.class);
//		intent.putExtra(BUNDLE_KEY_DETAIL_INFO, invitation);
//		fragment.startActivityForResult(intent, Const.REQUST_CODE_INVITE_DETAIL_OTHER_STS);
//		fragment.getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
//	}

	public static void startInviteDetailOtherStsForCallback(Fragment fragment, MyInviteInfo invitation) {
		callback = (onResultCallback) fragment;
		Intent intent = new Intent(fragment.getActivity(), InviteDetailOtherStsActivity.class);
		intent.putExtra(BUNDLE_KEY_DETAIL_INFO, invitation);
		fragment.startActivity(intent);
		fragment.getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	public static void startInviteDetailOtherStsForCallback(FragmentActivity fragment, MyInviteInfo invitation) {
		callback = (onResultCallback) fragment;
		Intent intent = new Intent(fragment, InviteDetailOtherStsActivity.class);
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
	
	private void getAndSetViews() {
		// 接受，拒绝
		getAppRevokeAcceptBar();
		appRevokeBtn.setOnClickListener(this);
		appRevokeBtn.setText(R.string.str_invite_detail_oper_btn_app_refuse);
		appAcceptBtn.setOnClickListener(this);
		// 撤约，签到
		getAppCancelMarkBar();
		appCancelBtn.setOnClickListener(this);
		appMarkBtn.setOnClickListener(this);
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

	/**
	 * 
	 * @param detail
	 */
	private void appendMoreData(MyInviteDetail detail) {
		
		this.detail = detail;
		
		appendMyCommonData(detail.inviter, detail.invitee,detail.inviteeone,detail.inviteetwo,
			String.format(getString(R.string.str_invite_detail_msg_other), detail.addressName, detail.message),
			detail.paymentType, detail.stake);

		/*pxs 2015.12.28 update*/
		//dismissRequestRegion();
		unClickableleRequestRegionMine();
		switch(invitation.displayStatus) {
			case Const.MY_INVITE_WAITACCEPT: // 等接受,我可以接受、拒绝(相当于撤销)约球
				addOperBarLayout(appRevokeAcceptBar);
				displayPlans(detail.planInfo);
				break;
			case Const.MY_INVITE_ACCEPTED: // 已接受,我接受了约球；可撤约
				displayAppInfo(detail.teeTime, invitation.courseName); // 已接受，只显示接受方案即可
				displayAppCancel();

				displayRateRegion(detail);

				/*pxs 2015.12.28 update*/
				dismissRequestRegion();

				if(DISP_SCORE_) return ;
				break;
			case Const.MY_INVITE_CANCELED: // 已撤销,对方撤销了约球
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
				displayPlansShowListView(detail.planInfo); // 已撤销，展示，但不可选
				displayAppInfo(detail.teeTime, invitation.courseName);
				break;
			case Const.MY_INVITE_REVOKED: // 已撤约,任一一方撤约
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				displayDisableBtn(R.string.str_invite_detail_oper_btn_app_cancel_done);
//				displayPlansShowListView(detail.planInfo); // 已撤约，展示，但不可选
				displayAppInfo(detail.teeTime, invitation.courseName); // 已撤约，但已选，故显示信息非方案
				break;
			case Const.MY_INVITE_REFUSED: // 已拒绝,我拒绝了邀请(相当于撤销了球单)
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				displayDisableBtn(R.string.str_invite_detail_oper_btn_app_refused);
				displayPlansShowListView(detail.planInfo); // 已拒绝，展示，但不可选
				break;
			case Const.MY_INVITE_WAITSIGN: // 待签到,约球单为接受，并当前时间小于开球时间
				displayAppMark();
				displayAppInfo(detail.teeTime, invitation.courseName); // 此时已接受，只显示接受方案即可

				/*pxs 2015.12.28 update*/
				dismissRequestRegion();
				if(DISP_SCORE_) {
					palyingScoreAndRate(detail);
					return ;
				}
				break;
			case Const.MY_INVITE_SIGNED: // 已签到
			case Const.MY_INVITE_PLAYING: // 进行中,当前时间大于等于开球时间，并未记分或未评价对方
				displayAppInfo(detail.teeTime, invitation.courseName);

				/*pxs 2015.12.28 update*/
				dismissRequestRegion();
				palyingScoreAndRate(detail);
				// 直接return，显示评分，记分
				return;
			case Const.MY_INVITE_COMPLETE: // 已完成,有记分并有已评价对方
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				// 已完成，显示评分，记分
				completeScoreAndRate(detail);
				displayAppInfo(detail.teeTime, invitation.courseName);

				/*pxs 2015.12.28 update*/
				dismissRequestRegion();

				// 直接return，显示评分，记分
				return;
		}
		// 隐藏评分，记分
		dismissScoreRegion();
		dismissRateRegion();
	}
	
	/**
	 * 显示方案列表
	 * @param planInfo
	 */
	private void displayPlans(ArrayList<PlanShowInfo> planInfo) {
		planAdapter = displayPlansSelectListView(planInfo);
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
				// 公用按钮，此处为拒绝操作
				clickAppRefruse();
				break;
			case R.id.invite_detail_oper_btn_accept:
				clickAppAccept();
				break;
			default:
				super.onClick(v);
				break;
		}
	}

	private void clickAppAccept() {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			AcceptStsInvite request = new AcceptStsInvite(InviteDetailOtherStsActivity.this, invitation.sn, planAdapter.getSelectedPlanIndex());
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				switch(result) {
					case BaseRequest.REQ_RET_OK:
						// 接受成功，可撤约；更改方案为信息显示；状态变化，设置返回列表时更新。
						displayAppCancel();
						PlanShowInfo plan = planAdapter.getSelectedPlan();
						displayAppInfo(plan.teeTime, plan.courseName);
						setFinishResult(true);
						if(DISP_SCORE_) {
							displayScoreRegion();
							displayRateRegion(detail);
						}
						break;
					case BaseRequest.REQ_RET_F_APP_REVOKE: // 对方已撤销
					case BaseRequest.REQ_RET_F_APP_OVERDUE: // 已过期，系统自动撤销撤销
						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
						planAdapter.setSelectable(false);
						setFinishResult(true);
						break;
					default:
						toastShort(request.getFailMsg());
						break;
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

	private void clickAppRefruse() {
		new AlertDialog.Builder(this)
			.setTitle(R.string.str_invite_detail_dialog_refuse_app_title)
			.setMessage(R.string.str_invite_detail_dialog_refuse_app_msg)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doAppRefruse();
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { }
			})
			.show();
	}
	private void doAppRefruse() {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			RefuseStsInvite request = new RefuseStsInvite(InviteDetailOtherStsActivity.this, invitation.sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				switch(result) {
					case BaseRequest.REQ_RET_OK:
						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_refused);
						planAdapter.setSelectable(false);
						setFinishResult(true);
						break;
					case BaseRequest.REQ_RET_F_APP_REVOKE: // 对方已撤销
					case BaseRequest.REQ_RET_F_APP_OVERDUE: // 已过期，系统自动撤销撤销
						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
						planAdapter.setSelectable(false);
						setFinishResult(true);
						break;
					default:
						toastShort(request.getFailMsg());
						break;
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

}
