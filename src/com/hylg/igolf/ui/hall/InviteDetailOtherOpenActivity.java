package com.hylg.igolf.ui.hall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.MyInviteDetail;
import com.hylg.igolf.cs.data.MyInviteInfo;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;


/**
 * 他人发起的开放式约球，我进行了申请。
 * 后续状态：
 * 		状态			说明
		已申请		我申请了约球
		已接受		对方接受了我的申请
		已撤销		对方撤销了约球
		已撤约		任一一方撤约
		待签到		约球单为接受，并当前时间小于开球时间
		进行中		当前时间大于等于开球时间，并未记分或未评价对方
		已完成		有记分并有已评价对方

 * 	注：他人发起的开放式约球，我无法进行撤销（在对方接受自己之前，只能取消申请；接受自己后，可撤约(2小时之前)或签到(2小时之后)）
 * 
 */
public class InviteDetailOtherOpenActivity extends InviteDetailMineActivity {
//	private static final String TAG = "InviteDetailOtherOpenActivity";
	private RelativeLayout reqApplyCancelBar;
	private Button reqApplyBtn, reqCancelBtn;
	
//	public static void startInviteDetailOtherOpenForResult(Fragment fragment, MyInviteInfo invitation) {
//		Intent intent = new Intent(fragment.getActivity(), InviteDetailOtherOpenActivity.class);
//		intent.putExtra(BUNDLE_KEY_OTHER_OPEN_INFO, invitation);
//		fragment.startActivityForResult(intent, Const.REQUST_CODE_INVITE_DETAIL_OTHER_OPEN);
//		fragment.getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
//	}
	
	public static void startInviteDetailOtherOpenForCallback(Fragment fragment, MyInviteInfo invitation) {
		callback = (onResultCallback) fragment;
		Intent intent = new Intent(fragment.getActivity(), InviteDetailOtherOpenActivity.class);
		intent.putExtra(BUNDLE_KEY_DETAIL_INFO, invitation);
		fragment.startActivity(intent);
		fragment.getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	public static void startInviteDetailOtherOpenForCallback(FragmentActivity fragment, MyInviteInfo invitation) {
		callback = (onResultCallback) fragment;
		Intent intent = new Intent(fragment, InviteDetailOtherOpenActivity.class);
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
		// 申请，取消申请
		reqApplyCancelBar = (RelativeLayout) View.inflate(this, R.layout.invite_detail_oper_bar_apply_cancel, null);
		reqApplyBtn = (Button) reqApplyCancelBar.findViewById(R.id.invite_detail_oper_btn_req_apply);
		reqCancelBtn = (Button) reqApplyCancelBar.findViewById(R.id.invite_detail_oper_btn_req_cancel);
		reqApplyBtn.setOnClickListener(this);
		reqCancelBtn.setOnClickListener(this);
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
						return;
					case BaseRequest.REQ_RET_F_OPEN_ACCEPT_OTHER:
						setFinishResult(true);
						toastShort(msg);
						displayDisableBtn(R.string.str_invite_detail_oper_btn_app_accept_other);
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
	 * 根据详情信息，初始化页面
	 * @param detail
	 */
	private void appendMoreData(MyInviteDetail detail) {
		appendMyCommonData(detail.inviter, detail.invitee,detail.inviteeone,detail.inviteetwo,
				detail.message, detail.paymentType, detail.stake);
		// open: app info, not plans
		displayAppInfo(detail.teeTime, detail.courseName);
		// 申请状况
		displayRequestRegionOther(invitation.applicantsNum);

		unClickableleRequestRegionMine();
		
		switch(invitation.displayStatus) {
			case Const.MY_INVITE_APPlYED: // 已申请,我申请了约球
				addOperBarLayout(reqApplyCancelBar); // 进入时，默认：取消申请，需添加。
				Utils.setVisibleGone(reqCancelBtn, reqApplyBtn);
				break;
			case Const.MY_INVITE_ACCEPTED: // 已接受,对方接受了我的申请
				displayAppCancel();
				 // appendMyCommonData数据已经填充，无需再次设置
//				InviteRoleInfo right = new InviteRoleInfo(customer);
//				setRightInfoViews(right);
//				setRightPerInfoViews(right);
				dismissRequestRegion();
				
				displayRateRegion(detail);
				if(DISP_SCORE_) return ;
				break;
			case Const.MY_INVITE_CANCELED: // 已撤销,对方撤销了约球
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				displayDisableBtn(R.string.str_invite_detail_oper_btn_app_revoke_done);

				/*pxs 2015.12.28 update */
				//dismissRequestRegion();
				break;
			case Const.MY_INVITE_REVOKED: // 已撤约,任一一方撤约
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				displayDisableBtn(R.string.str_invite_detail_oper_btn_app_cancel_done);

				/*pxs 2015.12.28 update */
				//dismissRequestRegion();
				break;
			case Const.MY_INVITE_WAITSIGN: // 待签到,约球单为接受，并当前时间小于开球时间
				displayAppMark();
				dismissRequestRegion();
				if(DISP_SCORE_) {
					palyingScoreAndRate(detail);
					return ;
				}
				break;
			case Const.MY_INVITE_SIGNED: // 已签到
			case Const.MY_INVITE_PLAYING: // 进行中,当前时间大于等于开球时间，并未记分或未评价对方
				palyingScoreAndRate(detail);
				dismissRequestRegion();
				// 直接return，显示评分，记分
				return;
			case Const.MY_INVITE_COMPLETE: // 已完成,有记分并有已评价对方
				// 实际列表中，非提醒，无此类信息，已经转移到约球历史
				// 已完成，显示评分，记分
				completeScoreAndRate(detail);
				dismissRequestRegion();
				// 直接return，显示评分，记分
				return;
		}
		// 隐藏评分，记分
		dismissScoreRegion();
		dismissRateRegion();
	}

	private void clickReqCancel() {
		cancelOpenInvite(invitation.sn, customer.sn, new cancelOpenInviteCallback() {
			@Override
			public void callBack(int retId, int applyNum, String msg) {
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						// 按钮栏已添加，只需更改状态
						Utils.setVisibleGone(reqApplyBtn, reqCancelBtn);
						toastShort(msg);
						setFinishResult(true);
						refreshRequestTitle(applyNum);
						break;
					case BaseRequest.REQ_RET_F_APP_ACCEPT: {
//						Utils.setDisable(reqCancelBtn);
						// 对方已经接受自己的请求。
						toastShort(msg);
//						finishWithResult(true);
						setFinishResult(true);
//						refreshRequestTitle(applyNum);

						/*pxs 2015.12.29 update*/
						//dismissRequestRegion();
						addOperBarLayout(appCancelMarkBar);
						Utils.setVisibleGone(appCancelBtn, appMarkBtn, appCmSpaceView);
						break;
					}
					case BaseRequest.REQ_RET_F_APP_PREPARE: {
//						Utils.setDisable(reqCancelBtn);
						// 对方已经接受自己的请求，且进入打球准备阶段。
						toastShort(msg);
//						finishWithResult(true);
						setFinishResult(true);
//						refreshRequestTitle(applyNum);

						/*pxs 2015.12.29 update*/
						//dismissRequestRegion();
						addOperBarLayout(appCancelMarkBar);
						Utils.setVisibleGone(appMarkBtn, appCancelBtn, appCmSpaceView);
						break;
					}
					case BaseRequest.REQ_RET_F_OPEN_ACCEPT_OTHER: {
						Utils.setDisable(reqCancelBtn);
						// 对方接受了其他人
						toastShort(msg);
						finishWithResult(true);
						break;
					}
					case BaseRequest.REQ_RET_F_APP_OVERDUE: {
						Utils.setDisable(reqCancelBtn);
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
						// 按钮栏已添加，只需更改状态
						Utils.setVisibleGone(reqCancelBtn, reqApplyBtn);
						toastShort(msg);
						setFinishResult(false);
						refreshRequestTitle(applyNum);
						break;
					case BaseRequest.REQ_RET_F_OPEN_ACCEPT_OTHER: {
						Utils.setDisable(reqApplyBtn);
						// 对方接受了其他人
						toastShort(msg);
						finishWithResult(true);
						break;
					}
					case BaseRequest.REQ_RET_F_APP_OVERDUE: {
						Utils.setDisable(reqApplyBtn);
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

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.invite_detail_oper_btn_req_apply:
				clickReqApply();
				break;
			case R.id.invite_detail_oper_btn_req_cancel:
				clickReqCancel();
				break;
			case R.id.invite_detail_oper_btn_cancel:
				cancelInviteApp(invitation.sn, customer.sn);
				break;
			case R.id.invite_detail_oper_btn_mark:
				markInviteApp(invitation.sn, customer.sn);
				break;
			default:
				super.onClick(v);
				break;
		}
	}

}
