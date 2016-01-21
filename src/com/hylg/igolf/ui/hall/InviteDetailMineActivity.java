package com.hylg.igolf.ui.hall;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.*;
import android.location.GpsStatus.Listener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings;
import android.view.View;
import android.widget.*;
import cn.gl.lib.img.AsyncImageSaver;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.*;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.request.*;
import com.hylg.igolf.cs.request.UpdateImageRequest.getRivalDataCallback;
import com.hylg.igolf.db.InviteScoreNotesData;
import com.hylg.igolf.db.InviteScoreNotesDatabase;
import com.hylg.igolf.ui.common.ImageSelectActivity;
import com.hylg.igolf.ui.common.ImageSelectActivity.onImageSelectListener;
import com.hylg.igolf.utils.*;

public class InviteDetailMineActivity extends InviteDetailActivity implements onImageSelectListener {
	private final static String TAG = "InviteDetailMineActivity";
	protected static onResultCallback callback;
	protected static final String BUNDLE_KEY_DETAIL_INFO = "detail_info";
	protected MyInviteInfo invitation;
	protected Customer customer;
	// 撤约，签到按钮
	protected LinearLayout appCancelMarkBar;
	protected Button appCancelBtn, appMarkBtn;
	protected View appCmSpaceView;
	
	// 撤销，接受按钮
	protected LinearLayout appRevokeAcceptBar;
	protected Button appRevokeBtn, appAcceptBtn;
	protected View appRaSpaceView;
	//private Location curLocation = null;
	
	private LocationManager mLocationManager;
	private String mLocationProvider = null;
	
	//约球成功就显示评分，上传，不做隐藏
	protected boolean DISP_SCORE_ = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setOnResultCallback(callback);
		getData();
		// 下列状态
		if(invitation.displayStatus == Const.MY_INVITE_WAITSIGN ||
				invitation.displayStatus == Const.MY_INVITE_PLAYING ||
				invitation.displayStatus == Const.MY_INVITE_SIGNED) {
			// 未存储gps信息
			InviteScoreNotesData dbScoreNote = getScoreNote(invitation.sn, customer.sn);
			if(null == dbScoreNote || dbScoreNote.lati == Double.MAX_VALUE) {
				imStartGps(false);
			}
		}

		if (invitation.type == Const.INVITE_TYPE_STS) {

			invitee_two_relative.setVisibility(View.INVISIBLE);
			invitee_three_relative.setVisibility(View.INVISIBLE);

		}
	}
	
	
	@Override
	protected void onResume() {
		Utils.logh(TAG, "onResume");
		if(null != mLocationManager) {
			Utils.logh(TAG, "onResume mLocationManager != null");
			try {
				//mLocationManager.requestLocationUpdates(mLocationProvider, 2000, 10, mLocationListener);
				mLocationManager.requestLocationUpdates(mLocationProvider, 500, 1, mLocationListener);
			    //Location location = mLocationManager.getLastKnownLocation(mLocationProvider);
			    //log("location-null?  " + (null == location));
		    	//updateWithNewLocation(location);
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
//				Toast.makeText(this, R.string.check_gps, Toast.LENGTH_SHORT).show();
				Utils.logh(TAG, "onResume param null mLocationProvider: " + mLocationProvider);
			}
		} 
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		removeLocation();
		super.onPause();
	}
	
	protected void removeLocation() {
		Utils.logh(TAG, "removeLocation mLocationManager: " + mLocationManager);
		if(null != mLocationManager) {
			mLocationManager.removeUpdates(mLocationListener);
		}
	}

	private final LocationListener mLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {   
//			updateWithNewLocation(location);   
			if (location != null) {
				Utils.logh(TAG, "onLocationChanged  location: " + location);
				//curLocation = location;
			}
		}
		public void onProviderDisabled(String provider){Utils.logh(TAG, " *** disabled");}
		public void onProviderEnabled(String provider){Utils.logh(TAG, " *** enabled");}
		public void onStatusChanged(String provider, int status, Bundle extras){Utils.logh(TAG, " *** change");}
	};
	
    // start gps search progress dialog
	public void imStartGps(boolean hint) {
		Utils.logh(TAG, "imStartGps >>>>>>>> mLocationProvider: " + mLocationProvider);
		if(null == mLocationManager) mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if(hint && !mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			showGpsSettingDialog();
			return ;
		}
//		if(null == mLocationProvider) {
//			Criteria criteria = new Criteria();
//		    criteria.setAccuracy(Criteria.ACCURACY_FINE);
//		    criteria.setAltitudeRequired(false);
//		    criteria.setBearingRequired(false);
//		    criteria.setCostAllowed(true);
//		    criteria.setPowerRequirement(Criteria.POWER_LOW);
//			mLocationProvider = mLocationManager.getBestProvider(criteria, true);
//		}
		try {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, mLocationListener);
	    	mLocationManager.addGpsStatusListener(mGpsListener);
	    	//updateWithNewLocation(mLocationManager.getLastKnownLocation(mLocationProvider));
	    	mLocationProvider = LocationManager.GPS_PROVIDER;
	    	Utils.logh(TAG, "mLocationProvider: " + mLocationProvider);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			Utils.logh(TAG, "imStartGps param null !");
		}
	}
	private final Listener mGpsListener = new Listener() {
		public void onGpsStatusChanged(int gpsStatus) {
			switch (gpsStatus) {
				case GpsStatus.GPS_EVENT_FIRST_FIX: // 3
					Utils.logh(TAG, "fixxxxxxxxxxxxxxxxxx");
					break;
			}
		}
    };
	/**
	 * 获取位置信息
	 * @param hint	是否提示用户打开设置
	 */
//	private void openLocation(boolean hint) {
//        if(null == locationMgr) {
//        	locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        }
//        
//        // 默认使用网络模式，获取更高经度，如果失败，与gps互换
//        Location location = null;
//        if(locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//        	providerName = LocationManager.NETWORK_PROVIDER;
//        	curLocation = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        } else if(locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//         	providerName = LocationManager.GPS_PROVIDER;
//         	curLocation = locationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//         }
//        Utils.logh(TAG, "openLocation providerName: " + providerName + " location: " + location);
//        
//        if(null == providerName && hint) {
//        	
//        }
//	}
	
	private void showGpsSettingDialog() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.str_comm_dialog_gps_title)
		.setMessage(R.string.str_comm_dialog_gps_msg)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) { }
		})
		.show();
	}

	private void getData() {
		invitation = (MyInviteInfo) getIntent().getSerializableExtra(BUNDLE_KEY_DETAIL_INFO);
		customer = MainApp.getInstance().getCustomer();
	}
	
	protected void appendMyCommonData(InviteRoleInfo left, InviteRoleInfo right,InviteRoleInfo right1,InviteRoleInfo right2,
			String inviteMsg, int payType, int stake) {
		
		appendCommonData(left, right,right1,right2, inviteMsg, payType, stake);
		// 新通知状态变化
		if(invitation.haveAlert) {
			setAlertChanged();
			// 发送查看广播给MainActivity，主动获取当前消息数量，更新导航栏提示图标
			sendBroadcast(new Intent(Const.IG_ACTION_NEW_ALERTS));
		}
	}
	
	// 撤约，签到按钮
	protected void getAppCancelMarkBar() {
		appCancelMarkBar = (LinearLayout) View.inflate(this, R.layout.invite_detail_oper_bar_cancel_mark, null);
		appCancelBtn = (Button) appCancelMarkBar.findViewById(R.id.invite_detail_oper_btn_cancel);
		appMarkBtn = (Button) appCancelMarkBar.findViewById(R.id.invite_detail_oper_btn_mark);
		appCmSpaceView = appCancelMarkBar.findViewById(R.id.invite_detail_oper_cm_btn_space);
	}
	
	protected void displayAppCancel() {
		addOperBarLayout(appCancelMarkBar);
		Utils.setVisibleGone(appCancelBtn, appMarkBtn, appCmSpaceView);
	}
	
	protected void displayAppMark() {
		addOperBarLayout(appCancelMarkBar);
		Utils.setVisibleGone(appMarkBtn, appCancelBtn, appCmSpaceView);
	}
	
	/**
	 *  撤销，接受按钮
	 *  注：
	 *  	可做替换公用，但需要根据时间情况，修改按钮文字，和是否显示的状态。
	 */
	protected void getAppRevokeAcceptBar() {
		appRevokeAcceptBar = (LinearLayout) View.inflate(this, R.layout.invite_detail_oper_bar_revoke_accept, null);
		appRevokeBtn = (Button) appRevokeAcceptBar.findViewById(R.id.invite_detail_oper_btn_revoke);
		appAcceptBtn = (Button) appRevokeAcceptBar.findViewById(R.id.invite_detail_oper_btn_accept);
		appRaSpaceView = appRevokeAcceptBar.findViewById(R.id.invite_detail_oper_ra_btn_space);
	}
	
	protected void displayAppRevoke() {
		addOperBarLayout(appRevokeAcceptBar);
		Utils.setVisibleGone(appRevokeBtn, appAcceptBtn, appRaSpaceView);
	}
	
	/**
	 * 显示不可操作状态按钮
	 * @param msgId 按钮显示文字id
	 */
	protected void displayDisableBtn(int msgId) {
		addOperBarLayout(appCancelMarkBar);
		Utils.setVisibleGone(appMarkBtn, appCancelBtn, appCmSpaceView);
		Utils.setDisable(appMarkBtn);
		appMarkBtn.setText(msgId);
	}
	
	private int curRate;
	
	/**
	 *  进行中,当前时间大于等于开球时间，并未记分或未评价对方
	 * @param detail
	 * @see Const.MY_INVITE_PLAYING
	 */
	protected void palyingScoreAndRate(MyInviteDetail detail) {
		Utils.logh(TAG, "score: " + detail.score + " scoreCardName: " + detail.scoreCardName + " rateStar: " + detail.rateStar);
		curRate = detail.rateStar;
		
		this.detail = detail;
		
		if(detail.score < 0) { // 未签到
			displayAppMark();
			//if(!DISP_SCORE_) {
				dismissRateRegion(); // 不显示评价
			//}
			displayScoreRegion(); // 只显示上传区域
			// 未签到，也可能上传失败
			InviteScoreNotesData dbScoreNote = getScoreNote(invitation.sn, customer.sn);
			if(null != dbScoreNote) {
				// 如果数据库有记录，在输入框填写默认值
				setScoreNumber(dbScoreNote.score);
			} else {
				displayScoreRegion();
				return ;
			}
			// 填充图片
			ImageView card = getScoreCard();
			String tmpPath = FileUtils.getScorecardPathBySn(this,
					FileUtils.getScorecardTmpName(customer.sn, invitation.sn), customer.sn);
			Utils.logh(TAG, "palyingScoreAndRate tmpPath " + tmpPath);
			if(null != tmpPath) {
				File file = new File(tmpPath);
				if(file.exists()) {
					int upSize = getResources().getInteger(R.integer.upload_scorecard_size);
					scoreBmp = FileUtils.getSmallBitmap(tmpPath, upSize, upSize);
					int reqSize = (int) getResources().getDimension(R.dimen.score_invite_detail_size);
					card.setImageBitmap(FileUtils.getSmallBitmap(tmpPath, reqSize, reqSize));
					// 已经点击“记分登记”记录过，不可修改图片
					if(null != dbScoreNote) {
						Utils.setDisable(card);
					}
					// 显示无效图片
					displayScorecardInvalid();
					return ;
				}
			}
			// 无图片，可设置
			Utils.setEnable(card);
//			hasScored = false;
		} else { // 已签到
			displayDisableBtn(R.string.str_invite_detail_oper_btn_app_mark_done);
			//displayRateRegion(detail.rateStar); // 签到后，才能显示评分
			
			displayRateRegion(detail); // 签到后，才能显示评分
			if(detail.score > 0) { // 已经 记分登记(杆数，卡全部成功)
				setScoreNumber(detail.score);
				loadScorecard(getScoreCard(), detail.scoreCardName);
				disableScoreItems();
			} else { // 未记分登记成功
				InviteScoreNotesData dbScoreNote = getScoreNote(invitation.sn, customer.sn);
				if(null != dbScoreNote) {
					// 如果数据库有记录，在输入框填写默认值
					setScoreNumber(dbScoreNote.score);
				} else {
					displayScoreRegion();
					return ;
				}
				// 填充图片
				ImageView card = getScoreCard();
				String tmpPath = FileUtils.getScorecardPathBySn(this,
						FileUtils.getScorecardTmpName(customer.sn, invitation.sn), customer.sn);
				Utils.logh(TAG, "palyingScoreAndRate tmpPath " + tmpPath);
				if(null != tmpPath) {
					File file = new File(tmpPath);
					if(file.exists()) {
						int upSize = getResources().getInteger(R.integer.upload_scorecard_size);
						scoreBmp = FileUtils.getSmallBitmap(tmpPath, upSize, upSize);
						int reqSize = (int) getResources().getDimension(R.dimen.score_invite_detail_size);
						card.setImageBitmap(FileUtils.getSmallBitmap(tmpPath, reqSize, reqSize));
						
						// 已经点击“记分登记”记录过，不可修改图片
						if(null != dbScoreNote) {
							Utils.setDisable(card);
						}
						// 显示无效图片
						displayScorecardInvalid();
						return ;
					}
				}
				// 无图片，可设置
				Utils.setEnable(card);
//				hasScored = false;
			}
		}

	}

	/**
	 *  已完成,有记分并有已评价对方
	 * @param detail
	 * @see Const.MY_INVITE_COMPLETE
	 */
	protected void completeScoreAndRate(MyInviteDetail detail) {
		// 测试状态下，检查数据合法性
		if(Utils.LOG_H && (detail.rateStar <= 0 || detail.score < 0)) {
			Toast.makeText(this, "completeScoreAndRate RATE and SCORED SHOULD > 0, TELL ME FOR SERVER ERROR", Toast.LENGTH_LONG).show();
			throw new java.lang.IllegalArgumentException("detail.rateStar <= 0 !");
		}
		
		if(detail.score > 0) { // 说明记分登记成功
			setScoreNumber(detail.score);
			loadScorecard(getScoreCard(), detail.scoreCardName);
		} else { // 由系统自动完成
			setScoreNumber(0);
		}
		
		/*完成之后，不能上传积分，也不能评价*/
		disableScoreItems();
		Utils.setDisable(rateDoBtn);
		disableRateBarItems();
		
		
		// 已完成，清除数据库数据
		InviteScoreNotesDatabase db = new InviteScoreNotesDatabase(this);
		if(db.isScoreNoteExists(invitation.sn, customer.sn)) {
			db.deleteScoreNote(invitation.sn, customer.sn);
		}
//		displayRateRegion(detail.rateStar);
		
		displayRateRegion(detail); // 签到后，才能显示评分
		displayDisableBtn(R.string.str_invite_detail_oper_btn_app_complete);
//		hasScored = true;
		// request
		Utils.logh(TAG, "score: " + detail.score + " scoreCardName: " + detail.scoreCardName + " rateStar: " + detail.rateStar);
		// 已经完成，清理数据，返回后，删除记分卡图片
		
	}
	
	private void loadScorecard(final ImageView iv, String name) {
		String scoreCard = FileUtils.getScorecardPathBySn(this, name, customer.sn);
		Utils.logh(TAG, "loadScorecard: " + scoreCard);
		Drawable card = AsyncImageLoader.getInstance().getScorecard(this, invitation.sn, customer.sn, name);
		if(null != card) {
			iv.setImageDrawable(card);
		} else {
			iv.setImageResource(R.drawable.avatar_loading);
			AsyncImageLoader.getInstance().loadScorecard(this, invitation.sn, customer.sn, name,
				new ImageCallback() {
					@Override
					public void imageLoaded(Drawable imageDrawable) {
						if(null != imageDrawable) {
							iv.setImageDrawable(imageDrawable);
						}
					}
			});
		}
	}

	/**
	 * 撤约约球 
	 * @param appSn 约球单号
	 * @param sn 用户会员编号
//	 * @param callback
	 */
	protected void cancelInviteApp(String appSn, String sn) {
		cancelInviteApp(appSn, sn, null);
	}
	
	protected void cancelInviteApp(final String appSn, final String sn, final cancelInviteAppCallback callback) {
		new AlertDialog.Builder(this)
			.setTitle(R.string.str_invite_detail_dialog_cacel_app_title)
			.setMessage(R.string.str_invite_detail_dialog_cacel_app_msg)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					doCancelInviteApp(appSn, sn, callback);
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { }
			})
			.show();
	}
	
	private void doCancelInviteApp(final String appSn, final String sn, final cancelInviteAppCallback callback) {
		WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			CancelInviteApp request = new CancelInviteApp(InviteDetailMineActivity.this, appSn, sn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(null == callback) {
					String msg = request.getFailMsg();
					switch(result) {
						case BaseRequest.REQ_RET_OK:
							
							displayDisableBtn(R.string.str_invite_detail_oper_btn_app_cancel_done);
							dismissRateRegion();
							dismissScoreRegion();
							setFinishResult(true);
							toastShort(msg);
							break;
						case BaseRequest.REQ_RET_F_APP_PREPARE: // 已进入准备阶段，无法撤约
							displayAppMark();
							setFinishResult(true);
							toastShort(msg);
							break;
						default:
							toastShort(msg);
							break;
					}
				} else {
					callback.callBack(result, request.getFailMsg());
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	protected interface cancelInviteAppCallback {
		public abstract void callBack(int retId, String msg);
	}
	
	/**
	 * 签到
	 * @param appSn 约球单号
	 * @param sn 用户会员编号
	 * @param latitude 纬度
	 * @param longitude 经度
	 * @param callback
	 */
	private void markInviteApp(final String appSn, final String sn, final markInviteAppCallback callback, boolean waitGps) {
		Utils.logh(TAG, "markInviteApp:: waitGps: " + waitGps + " mLocationProvider: " + mLocationProvider + " curLocation: " + curLocation);
		// 当前有位置信息
		if(null != curLocation) {
			doMarkInvite(appSn, sn, curLocation.getLatitude(), curLocation.getLongitude(), callback);
			return ;
		}
		
		/* updater pxs
		 * 开始定位
		 * 定位功能已换成 高德定位sdk
		 * 2015.09.1 14：25 修改
		 * */ 
		getLocation();
		
//		if(null == mLocationManagerProxy) {
//			
//			//imStartGps(true);
//			getLocation();
//			
//		} else {
			
			if(!waitGps) {
				Toast.makeText(this, R.string.str_toast_get_gps_fail, Toast.LENGTH_SHORT).show();
				return;
			}
			WaitDialog.showWaitDialog(this, R.string.str_loading_gps);
			new AsyncTask<Object, Object, Integer>() {
				@Override
				protected Integer doInBackground(Object... params) {
					// 休眠等待
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return 0;
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					markInviteApp(appSn, sn, callback, false);
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);

//		}
	}
	
	protected void markInviteApp(String appSn, String sn) {
		markInviteApp(appSn, sn, null, true);
	}
	
	private void doMarkInvite(final String appSn, final String sn, final double latitude, final double longitude,
								final markInviteAppCallback callback) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			MarkInviteApp request = new MarkInviteApp(InviteDetailMineActivity.this, appSn, sn,
//														30.59, 104.06);
														latitude, longitude);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(null == callback) {
					String msg = request.getFailMsg();
					switch(result) {
						case BaseRequest.REQ_RET_OK:
							displayDisableBtn(R.string.str_invite_detail_oper_btn_app_mark_done);
							setFinishResult(true);
							toastShort(msg);
							// 签到成功，显示评分，记分
							displayRateRegion(detail);
							displayScoreRegion();
							break;
						case BaseRequest.REQ_RET_F_APP_OUTSIDE_COURSE: // 不在球场范围内
//							displayAppMark();
//							setFinishResult(true);
							toastShort(msg);
							break;
						default:
							toastShort(msg);
							break;
					}
				} else {
					callback.callBack(result, request.getFailMsg());
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	protected interface markInviteAppCallback {
		void callBack(int result, String failMsg);
	}
	
	/**
	 * 评价球友
	 * @param appSn 约球单号
	 * @param sn 用户会员编号
	 * @param rate 球品星级
	 * @param callback
	 */
	protected void rateGolfer(final String appSn, final String sn, final int rate,
								final rateGolferCallback callback) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			RateGolfer request = new RateGolfer(InviteDetailMineActivity.this, appSn, sn, rate);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				String msg = request.getFailMsg();
				if(null == callback) {
					switch(result) {
						case BaseRequest.REQ_RET_OK:
							curRate = rate;
							
							//displayRateRegion(rate);
							
							rateDoBtn.setText(R.string.str_invite_detail_rate_btn_done);
							rateBarRb.setIsIndicator(true);
							Utils.setDisable(rateDoBtn);
							
							RivalData rivalData = request.getRivalData();
							if(invitation.inviterSn.equals(customer.sn)) {
								refreshPerInfoViews(rate, 0, rivalData.rivalRate, rivalData.rivalScore);
							} else {
								refreshPerInfoViews(rivalData.rivalRate, rivalData.rivalScore, rate, 0);
							}
							setFinishResult(true);
							toastShort(R.string.str_invite_detail_rate_success);
							break;
						default: // 评价失败，提示
							toastShort(msg);
							break;
					}
				} else {
					callback.callBack(result, msg);
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	/**
	 * 评价球友
	 * @param appSn 约球单号
	 * @param sn 用户会员编号
	 * @param rate 球品星级
	 * @param callback
	 */
	protected void rateGolferAll(final String appSn, final ArrayList<String> sn, final int[] rate,
								final rateGolferCallback callback) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			RateGolferAll request = new RateGolferAll(InviteDetailMineActivity.this, appSn, sn, rate);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				String msg = request.getFailMsg();
				if(null == callback) {
					switch(result) {
						case BaseRequest.REQ_RET_OK:
							//curRate = rate;
							
							//displayRateRegion(rate);
							
							rateDoBtn.setText(R.string.str_invite_detail_rate_btn_done);
							Utils.setDisable(rateDoBtn);
							
//							RivalData rivalData = request.getRivalData();
//							if(invitation.inviterSn.equals(customer.sn)) {
//								refreshPerInfoViews(rate, 0, rivalData.rivalRate, rivalData.rivalScore);
//							} else {
//								refreshPerInfoViews(rivalData.rivalRate, rivalData.rivalScore, rate, 0);
//							}

							rateBarRb.setIsIndicator(true);
							rateBarRb1.setIsIndicator(true);
							rateBarRb2.setIsIndicator(true);

							setFinishResult(true);
							toastShort(R.string.str_invite_detail_rate_success);
							break;
						default: // 评价失败，提示
							toastShort(msg);
							break;
					}
				} else {
					callback.callBack(result, msg);
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	protected void rateGolfer(String appSn, String sn, int rate) {
		rateGolfer(appSn, sn, rate, null);
	}
	
	protected interface rateGolferCallback {
		void callBack(int result, String failMsg);
	}
	
	private void clickRateGolfer() {
		
		if (detail.type == Const.INVITE_TYPE_STS) {
			
			int rate = (int) getCurrentRating();
			Utils.logh(TAG, "clickRateGolfer " + getCurrentRating());
			if(rate <= 0) {
				Toast.makeText(this, R.string.str_invite_detail_rate_invalid_zero, Toast.LENGTH_SHORT).show();
				return ;
			}
			
			rateGolfer(invitation.sn, customer.sn, rate);
			
		/*开放式约球*/
		} else if (detail.type == Const.INVITE_TYPE_OPEN) {
			
			
			//rateGolfer(invitation.sn, customer.sn, rate);
			
			/*当前登录这是约球发起人*/
			if (customer.sn.equalsIgnoreCase(detail.inviter.sn)) {
				
				ArrayList<String> sn= new ArrayList<String>();
				
				if (detail.invitee != null) {
					
					sn.add(0,detail.invitee.sn);
				}
				
				if (detail.inviteeone != null) {
					
					sn.add(1,detail.inviteeone.sn);
				}
		
				if (detail.inviteetwo != null) {
					
					sn.add(2,detail.inviteetwo.sn);
				}
				
				
				rateGolferAll(invitation.sn,sn,getCurrentRatingAll(), null);
				
			/*当前登陆者不是约球发起人*/
			} else {
				
				
				int rate = (int) getCurrentRating();
				Utils.logh(TAG, "clickRateGolfer " + getCurrentRating());
				if(rate <= 0) {
					Toast.makeText(this, R.string.str_invite_detail_rate_invalid_zero, Toast.LENGTH_SHORT).show();
					return ;
				}
				
				rateGolfer(invitation.sn, customer.sn, rate);
			}
			
			
		}
	}
	
	/**
	 * 记分登记
	 * @param appSn 约球单号
	 * @param sn 用户会员编号
	 * @param latitude 纬度
	 * @param longitude 经度
	 * @param score 成绩
	 * @param callback
	 */
	private void uploadScore(final String appSn, final String sn, 
								final int score, final uploadScoreCallback callback, boolean waitGps,
								final getRivalDataCallback rdCallback) {
		Utils.logh(TAG, "markInviteApp:: waitGps: " + waitGps + " mLocationProvider: " + mLocationProvider + " curLocation: " + curLocation);
		// 有存储的位置信息
		InviteScoreNotesData dbScoreNote = getScoreNote(appSn, sn);
		if(null != dbScoreNote && dbScoreNote.lati != Double.MAX_VALUE
				 				&& dbScoreNote.longi != Double.MAX_VALUE) {
			doUploadScore(appSn, sn, dbScoreNote.lati, dbScoreNote.longi,
					score, // 进入页面填写存储数据，但用户可能会更改
					callback, rdCallback);
			return ;
		}
		// 获取当前位置信息
		if(null != curLocation) {
			doUploadScore(appSn, sn, curLocation.getLatitude(), curLocation.getLongitude(), score, callback, rdCallback);
			return ;
		}
		
		// 没有位置信息，等待gps
		// 未打开Location
		if(null == mLocationProvider) {
			imStartGps(true);
		} else {
			if(!waitGps) {
				Toast.makeText(this, R.string.str_toast_get_gps_fail, Toast.LENGTH_SHORT).show();
				return;
			}
			WaitDialog.showWaitDialog(this, R.string.str_loading_gps);
			new AsyncTask<Object, Object, Integer>() {
				@Override
				protected Integer doInBackground(Object... params) {
					// 休眠等待
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return 0;
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					uploadScore(appSn, sn, score, callback, false, rdCallback);
					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);

		}
	}
	
//	protected void uploadScore(String appSn, String sn, int score) {
//		uploadScore(appSn, sn, score, null, true);
//	}
	
	protected interface uploadScoreCallback {
		public abstract void callBack(int result, String failMsg);
	}
	
	private void doUploadScore(final String appSn, final String sn,
								final double latitude, final double longitude,
								final int score, final uploadScoreCallback callback,
								final getRivalDataCallback rdCallback) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_invite_request);
		new AsyncTask<Object, Object, Integer>() {
			final UploadScorecard request = new UploadScorecard(InviteDetailMineActivity.this,
												scoreBmp, appSn, sn, score,
//												30.59, 104.06);
												latitude, longitude,
												rdCallback);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				String msg = request.getFailMsg();
				if(null == callback) {
					switch(result) {
						case BaseRequest.REQ_RET_OK:
//							displayRateRegion(rate);
							setFinishResult(true);
							toastShort(msg);
//							hasScored = true; // 设置上传标识
//							setScoreNumber(score);
							disableScoreItems();
							
							
							/*
							 * updater pxs 2015.07.30
							 * 上传积分过后，显示评价
							 * 
							 * */
							
							// 记分登记成功，自动签到
							displayDisableBtn(R.string.str_invite_detail_oper_btn_app_mark_done);
							if(DISP_SCORE_) {
								
								displayRateRegion(detail);
								//displayRateRegion(curRate);
							}
							break;
						// 不在球场范围内
						case BaseRequest.REQ_RET_F_APP_OUTSIDE_COURSE:
							toastShort(msg);
//							InviteScoreNotesDatabase db = new InviteScoreNotesDatabase(InviteDetailMineActivity.this);
//							if(db.isScoreNoteExists(appSn, sn)) {
//								db.deleteScoreNote(appSn, sn);
//							}
							// 不在球场范围内，设置经纬度无效
							InviteScoreNotesDatabase db = new InviteScoreNotesDatabase(InviteDetailMineActivity.this);
							InviteScoreNotesData data;
							if(db.isScoreNoteExists(appSn, sn)) {
								data = db.getScoreNote(appSn, sn);
							} else {
								data = new InviteScoreNotesData();
								data.appSn = appSn;
								data.sn = sn;
								data.score = score;
							}
							data.lati = Double.MAX_VALUE;
							data.longi = Double.MAX_VALUE;
							db.savesScoreNote(data);
							break;
						case BaseRequest.REQ_RET_F_UPDATE_AVATAR_FAIL:
							break;
						// 网络超时，或服务器异常，不确定gps是否正确，也记录，但可通过上面的错误返回清空数据来补救。
						case BaseRequest.T_RESULT_EX_CONNECT_TIMEOUT:
						case BaseRequest.REQ_RET_EXCEP:
							if(DISP_SCORE_) {
								toastShort(msg);
								setScoreNumber(score);
								Utils.setDisable(getScoreCard());
								saveScoreToDatabase(appSn, sn, score, latitude, longitude);
								break;
							}
//							toastShort(msg);
//							break;
						default: // 记分失败，提示，并记录信息(此时gps信息是正确的)
							toastShort(msg);
							if(!DISP_SCORE_) {
								setScoreNumber(score);
								Utils.setDisable(getScoreCard());
								saveScoreToDatabase(appSn, sn, score, latitude, longitude);
							} else {
								// 没到时间也显示，此处应该是不能进行操作提示，不保存
							}
							break;
					}
				} else {
					callback.callBack(result, msg);
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private InviteScoreNotesData getScoreNote(String appSn, String sn) {
		InviteScoreNotesDatabase db = new InviteScoreNotesDatabase(this);
		if(db.isScoreNoteExists(appSn, sn)) {
			return db.getScoreNote(appSn, sn);
		}
		return null;
	}
	
	private void saveScoreToDatabase(String appSn, String sn, int score, double lati, double longi) {
		InviteScoreNotesDatabase db = new InviteScoreNotesDatabase(this);
		InviteScoreNotesData data = new InviteScoreNotesData();
		data.appSn = appSn;
		data.sn = sn;
		data.score = score;
		data.lati = lati;
		data.longi = longi;
		db.savesScoreNote(data);
	}
	
	private void clickUploadScore() {
		String scoreStr = getScore();
		if(null == scoreStr) {
			return ;
		}
		final int score;
		{ // 输入限制了数字，故下面情况理论上不会出现，只为避免异常，及不同系统定制，可能出现的错误情况
			try {

				score = Integer.parseInt(scoreStr);

				if (score <= 60 || score > 150) {

					Toast.makeText(this, R.string.str_invite_detail_score_invalide_range, Toast.LENGTH_SHORT).show();
					return;
				}

			} catch (NumberFormatException ex) {
				Toast.makeText(this, R.string.str_invite_detail_score_ex, Toast.LENGTH_SHORT).show();
				return ;
			}
			if(score <= 0) {
				Toast.makeText(this, R.string.str_invite_detail_score_invalid, Toast.LENGTH_SHORT).show();
				return ;
			}
		}
		if(null == scoreBmp) {
			Toast.makeText(this, R.string.str_invite_detail_scorecard_null, Toast.LENGTH_SHORT).show();
			return ;
		}
		final String appSn = invitation.sn;
		final String sn = customer.sn;
		
		// 先获取当前经纬度
		double lati = Double.MAX_VALUE;
		double longi = Double.MAX_VALUE;
		InviteScoreNotesData scoreNote = getScoreNote(appSn, sn);
		if(null != scoreNote && scoreNote.lati != Double.MAX_VALUE
							 && scoreNote.longi != Double.MAX_VALUE) {
			lati = scoreNote.lati;
			longi = scoreNote.longi;
		} else if(null != curLocation) {
			lati = curLocation.getLatitude();
			longi = curLocation.getLongitude();
		}
		// 点击按钮，首先存储杆数，设置成绩不能更改
		if(!DISP_SCORE_) { // 成功即显示的时候，只要请求超时和异常才保存
			saveScoreToDatabase(appSn, sn, score, lati, longi);
			Utils.setDisable(getScoreCard());
		}
		setScoreNumber(score);
		uploadScore(appSn, sn, score, null, true, new getRivalDataCallback() {
			@Override
			public void getRivalData(RivalData rivalData) {
				
				RefreshData data = new RefreshData();
				if(invitation.inviterSn.equals(sn)) {
//					refreshPerInfoViews(0, score, rivalData.rivalRate, rivalData.rivalScore);
					data.param1 = 0;
					data.param2 = score;
					data.param3 = rivalData.rivalRate;
					data.param4 = rivalData.rivalScore;
				} else {
//					refreshPerInfoViews(rivalData.rivalRate, rivalData.rivalScore, 0, score);
					data.param1 = rivalData.rivalRate;
					data.param2 = rivalData.rivalScore;
					data.param3 = 0;
					data.param4 = score;
				}
				
				
				Message msg = new Message();
				msg.what = MSG_REFRESH_RIVAL;
				Bundle b = new Bundle();
				b.putSerializable("refreshData", data);
				msg.setData(b);
				refreshHandle.sendMessage(msg);
			}
		});
	}
	
	private class RefreshData implements Serializable {
		private static final long serialVersionUID = -7339097536004917939L;
		public int param1;
		public int param2;
		public int param3;
		public int param4;
	}
	
	private final static int MSG_REFRESH_RIVAL = 1;
	private Handler refreshHandle = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case MSG_REFRESH_RIVAL:
					RefreshData data = (RefreshData) msg.getData().getSerializable("refreshData");
					Utils.logh(TAG, "MSG_REFRESH_RIVAL: " + data.param1 + " , " + data.param2 + " , " + data.param3 + " , " + data.param4);
					refreshPerInfoViews(data.param1, data.param2, data.param3, data.param4);
					break;
			}
			return false;
		}
	});
	
	/**
	 * ------------- 合并 ------------------
	 * 上传记分卡图片
	 * @param appSn 约球单号
	 * @param sn 用户会员编号
	 * @param callback
	 */
//	protected void uploadScorecard(String appSn, String sn, uploadScorecardCallback callback) {
//		
//	}
//	
//	protected interface uploadScorecardCallback {
//		
//	}
//	
////	private boolean hasScored = false;
//	private void clickUploadScorecard() {
////		if(!hasScored) {
////			Toast.makeText(this, R.string.str_invite_detail_score_first, Toast.LENGTH_SHORT).show();
////			return ;
////		}
//		ImageSelectActivity.startImageSelect(this);
//	}
	
	/**
	 * 启程回执
	 * @param appSn 约球单号
	 * @param sn 用户会员编号
	 * @param callback
	 */
//	protected void notifyDeparture(String appSn, String sn, notifyDepartureCallback callback) {
//		
//	}
//	
//	protected interface notifyDepartureCallback {
//		
//	}
	
	@Override
	public boolean getResultByCallback() {
		return true;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.invite_detail_rate_btn_do:
				clickRateGolfer();
				break;
			case R.id.invite_detail_score_btn_do:
				clickUploadScore();
				break;
			case R.id.invite_detail_score_card:
//				clickUploadScorecard();
				ImageSelectActivity.startImageSelect(InviteDetailMineActivity.this);
				break;
			default:
				super.onClick(v);
				break;
		}
	}

	/** 上传记分卡 */
	private Uri mUri;
	private Bitmap scoreBmp = null;
	@Override
	public void onCameraTypeSelect() {
		mUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
				new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.getDefault())
					.format(new Date(System.currentTimeMillis())) + ".jpg"));
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
		startActivityForResult(intent, Const.REQUST_CODE_PHOTE_TAKE_PHOTO);
	}

	@Override
	public void onGalleryTypeSelect() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(Media.EXTERNAL_CONTENT_URI, "image/*");
		startActivityForResult(intent, Const.REQUST_CODE_PHOTE_GALLERY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, " ::: requestCode: " + requestCode + " resultCode: " + resultCode + " intent: " + intent);
		if(Const.REQUST_CODE_PHOTE_TAKE_PHOTO == requestCode) {
			if(RESULT_OK == resultCode) {
//				startPhotoCrop(mUri);
				try {
					saveScoredLocal(new File(new URI(mUri.toString())));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				return ;
			}
		} else if(Const.REQUST_CODE_PHOTE_GALLERY == requestCode) {
			if(RESULT_OK == resultCode && null != intent) {
//				startPhotoCrop(intent.getData());
				String img_path = FileUtils.getMediaImagePath(InviteDetailMineActivity.this, intent.getData());
				saveScoredLocal(new File(img_path));
				return ;
			}
//		} else if(Const.REQUST_CODE_PHOTE_CROP == requestCode) {
//			if(RESULT_OK == resultCode && null != intent) {
//				Bundle b = intent.getExtras();
//				if(null != b) {
//					Bitmap photo = b.getParcelable("data");
//					if(null != photo) {
//						scoreBmp = photo;
//						getScoreCard().setImageBitmap(photo);
//						saveScoredLocal(photo);
//						return ;
//					} else {
//						Log.w(TAG, "photo null ! ");
//					}
//				} else {
//					Log.w(TAG, "intent.getExtras() null ! ");
//				}
//			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	private void saveScoredLocal(File file) {
		int uploadScSize = getResources().getInteger(R.integer.upload_scorecard_size);
		int dispScSize = (int) getResources().getDimension(R.dimen.score_invite_detail_size);
		Utils.logh(TAG, "saveScoredLocal:"+file.getAbsolutePath());

		scoreBmp = FileUtils.getSmallBitmap(file.getAbsolutePath(), uploadScSize, uploadScSize);

		int angle = FileUtils.getExifOrientation(file.getAbsolutePath());
		if(angle != 0) {
			scoreBmp = FileUtils.getExifBitmap(scoreBmp, angle);						
		}
		getScoreCard().setImageBitmap(FileUtils.getSmallBitmap(file.getAbsolutePath(), dispScSize, dispScSize));
		saveScoredLocal(scoreBmp);
	}
	
	/**
	 * 选择后，即保存临时图片。
	 * 上传成功时，删除临时图片；
	 * 上传失败，停留在当前页面，可修改图片，退出后，已记录杆数，能找到存储文件，就不可修改。找不到才可添加。
	 * @param photo
	 */
	private void saveScoredLocal(Bitmap photo) {
		String tmpPath = FileUtils.getScorecardPathBySn(this,
				FileUtils.getScorecardTmpName(customer.sn, invitation.sn), customer.sn);
		Utils.logh(TAG, "saveScoredLocal tmpPath " + tmpPath);
		if(null != tmpPath) {
			File file = new File(tmpPath);
			if(file.exists()) {
				file.delete();
			}
			// 保存图片到本地
			AsyncImageSaver.getInstance().saveImage(photo, tmpPath);
		}

	}


//	private void startPhotoCrop(Uri uri) {
//		int width = getResources().getInteger(R.integer.upload_scorecard_size);//(int) getResources().getDimension(R.dimen.avatar_real_size);
//		int height = getResources().getInteger(R.integer.upload_scorecard_size);
//
//		Utils.logh(TAG, "startPhotoCrop width: " + width + " height: " + height);
//		
//		Intent intent = new Intent("com.android.camera.action.CROP");
//		intent.setDataAndType(uri, "image/*");
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", width);
//		intent.putExtra("aspectY", height);
//		intent.putExtra("outputX", width);
//		intent.putExtra("outputY", height);
//		intent.putExtra("return-data", true);
//
//		startActivityForResult(intent, Const.REQUST_CODE_PHOTE_CROP);
//	}
}
