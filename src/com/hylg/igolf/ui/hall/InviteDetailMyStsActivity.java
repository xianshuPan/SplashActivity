package com.hylg.igolf.ui.hall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.MyInviteDetail;
import com.hylg.igolf.cs.data.MyInviteInfo;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.utils.Const;

/**
 * 我发起的一对一邀约。
 * 后续状态：
 * 		显示状态		说明
		等接受		等待对方接受
		已接受		对方已接受邀请
		已撤销		我撤销了约球
		已撤约		任一一方撤约
		已拒绝		对方拒绝了邀请
		待签到		约球单为接受，并当前时间小于开球时间
		进行中		当前时间大于等于开球时间，并未记分或未评价对方
		已完成		有记分并有已评价对方
 * 
 */
public class InviteDetailMyStsActivity extends InviteDetailMineActivity {
//	private static final String TAG = "InviteDetailMyStsActivity";
	
//	public static void startInviteDetailMyStsForResult(Fragment fragment, MyInviteInfo invitation) {
//		Intent intent = new Intent(fragment.getActivity(), InviteDetailMyStsActivity.class);
//		intent.putExtra(BUNDLE_KEY_DETAIL_INFO, invitation);
//		fragment.startActivityForResult(intent, Const.REQUST_CODE_INVITE_DETAIL_MY_STS);
//		fragment.getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
//	}
	
	public static void startInviteDetailMyStsForCallback(Fragment fragment, MyInviteInfo invitation) {
		callback = (onResultCallback) fragment;
		Intent intent = new Intent(fragment.getActivity(), InviteDetailMyStsActivity.class);
		intent.putExtra(BUNDLE_KEY_DETAIL_INFO, invitation);
		fragment.startActivity(intent);
		fragment.getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getAndSetViews();
		getDetailAsync();
	}

	private void getAndSetViews() {
		// 撤销
		getAppRevokeAcceptBar();
		appRevokeBtn.setOnClickListener(this);
//		appAcceptBtn.setOnClickListener(this);
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

	private void appendMoreData(MyInviteDetail detail) {
		appendMyCommonData(detail.inviter, detail.invitee,detail.inviteeone,detail.inviteetwo,
			detail.message, detail.paymentType, detail.stake);
		dismissRequestRegion();
		switch(detail.displayStatus) {
			case Const.MY_INVITE_WAITACCEPT: // 等待对方接受，我可进行撤销操作
				displayAppRevoke();
				displayPlansShowListView(detail.planInfo); // 我发起的，只展示，但不可选
				break;
			case Const.MY_INVITE_ACCEPTED: // 对方已接受邀请
				displayAppInfo(detail.teeTime, invitation.courseName); // 已接受，只显示接受方案即可
				displayAppCancel();
				
				displayRateRegion(detail);
				if(DISP_SCORE_) return ;
				break;
			case Const.MY_INVITE_CANCELED: // 已撤销,我撤销了约球
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
				displayPlansShowListView(detail.planInfo); // 已撤销，展示，但不可选
				break;
			case Const.MY_INVITE_REVOKED: // 已撤约,任一一方撤约
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				displayDisableBtn(R.string.str_invite_detail_oper_btn_app_cancel_done);
//				displayPlansShowListView(detail.planInfo); // 已撤约，展示，但不可选
				displayAppInfo(detail.teeTime, invitation.courseName); // 已撤约，但已选，故显示信息非方案
				break;
			case Const.MY_INVITE_REFUSED: // 已拒绝,对方拒绝了邀请(相当于撤销了球单)
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				displayDisableBtn(R.string.str_invite_detail_oper_btn_app_refused);
				displayPlansShowListView(detail.planInfo); // 已拒绝，展示，但不可选
				break;
			case Const.MY_INVITE_WAITSIGN: // 待签到,约球单为接受，并当前时间小于开球时间
				displayAppMark();
				displayAppInfo(detail.teeTime, invitation.courseName); // 此时已接受，只显示接受方案即可
				if(DISP_SCORE_) {
					palyingScoreAndRate(detail);
					return ;
				}
				break;
			case Const.MY_INVITE_SIGNED: // 已签到
			case Const.MY_INVITE_PLAYING: // 进行中,当前时间大于等于开球时间，并未记分或未评价对方
				displayAppInfo(detail.teeTime, invitation.courseName); // 此时已接受，只显示接受方案即可
				palyingScoreAndRate(detail);
				// 直接return，显示评分，记分
				return;
			case Const.MY_INVITE_COMPLETE: // 已完成,有记分并有已评价对方
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				// 已完成，显示评分，记分
				completeScoreAndRate(detail);
				displayAppInfo(detail.teeTime, invitation.courseName);
				// 直接return，显示评分，记分
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
					case BaseRequest.REQ_RET_OK:
						// 撤销成功，更新按钮状态及内容；设置状态变化标记(同时，列表中不再显示，会被转移到约球历史)
						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
						toastShort(msg);
						setFinishResult(true);
						break;
					case BaseRequest.REQ_RET_F_APP_OVERDUE:
						// 已经过期，系统自动撤销，更新按钮状态及内容；设置状态变化标记
						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);
						toastShort(msg);
						setFinishResult(true);
						break;
					case BaseRequest.REQ_RET_F_APP_ACCEPT:
						// 对方已经接受，信息发生变化(开球信息代替方案信息)，需要返回刷新列表获取新信息后，重新进入
						toastShort(msg);
						finishWithResult(true);
						break;
					case BaseRequest.REQ_RET_F_APP_PREPARE:
						// 对方已接受，且进入了打球准备阶段(开球信息代替方案信息)，需要返回刷新列表获取新信息后，重新进入
						toastShort(msg);
						finishWithResult(true);
						break;
					default:
						toastShort(msg);
						break;
				}
			}
		});
	}

}
