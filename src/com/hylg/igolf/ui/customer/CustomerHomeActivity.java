package com.hylg.igolf.ui.customer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.PopupWindow.OnDismissListener;

import cn.jpush.android.api.JPushInterface;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.data.Member;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.GetCustomerFriendHotListLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.loader.GetMemberloader;
import com.hylg.igolf.cs.loader.GetCustomerFriendHotListLoader.GetCustomerFriendHotListCallback;
import com.hylg.igolf.cs.loader.GetMemberloader.GetMemberCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.ReturnCode;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.ui.account.LoginActivity;
import com.hylg.igolf.ui.common.AlbumPagerActivity.OnAlbumSetAvatarListener;
import com.hylg.igolf.ui.common.ImageSelectActivity;
import com.hylg.igolf.ui.common.ImageSelectActivity.onImageSelectListener;
import com.hylg.igolf.ui.friend.FriendCircleAdapter;
import com.hylg.igolf.ui.view.ListviewBottomRefresh;
import com.hylg.igolf.ui.view.ListviewBottomRefresh.OnRefreshListener;
import com.hylg.igolf.utils.*;

public class CustomerHomeActivity extends FragmentActivity implements View.OnClickListener,
															onImageSelectListener,
															OnAlbumSetAvatarListener{
	
	private static final String 				TAG = "CustomerHomeActivity";
	private static CustomerHomeActivity 			customerFrg = null;
	
	//private TextView signatureTxtExpand;
	private Customer 							customer;
	
	private ImageView 							customerAvatar,moreImage;//sex,addAlbumView;
	//private View addAlbumSpace;
	private TextView 							nickName,yearsExp,handicapi,best,matches,heat,rank,act;
	
	private static ImageView 					msgAlertIv;
	private GetMemberloader 					reqLoader = null;
	
	private GetCustomerFriendHotListLoader 		Loader1 = null;
	//private LinearLayout albumLayout;
	private Uri 								mUri;
	
	private ListviewBottomRefresh 				mList ;
	
	private FriendCircleAdapter 				mFriendHotAdapter ;
	
	private int 								startPage = 0, 
												pageSize = 0;
	
	private String 								sn      = "";
	
	
	private PopupWindow							mMainMenuPop        		= null;
	private View 								mPopupWindowView			= null;  
	
	private LinearLayout 						mHistoryLinear,mMyBalanceRecordLinear,mAboutLinear,mExitLinear;
	
	private Activity                            mContext;
	
	private LayoutInflater                      mLayoutInflater = null;
	
	public static final CustomerHomeActivity getInstance() {
		if(null == customerFrg) {
			customerFrg = new CustomerHomeActivity();
		}
		return customerFrg;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.customer_frg_home_new);
		
		customer = MainApp.getInstance().getCustomer();
		
		mContext = this;
		mLayoutInflater = this.getLayoutInflater();
		sn = MainApp.getInstance().getCustomer().sn;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
	
		intiUI();
	}
	
	/*
	 * 初始化Ui控件
	 * */
	private void intiUI() {
		
		View head  = mLayoutInflater.inflate(R.layout.customer_info_head, null, false);
		
		
		head.findViewById(R.id.cusinfo_myinfo_ll).setOnClickListener(this);
		head.findViewById(R.id.cusinfo_more_relative).setVisibility(View.VISIBLE);
		head.findViewById(R.id.cusinfo_praise_text).setOnClickListener(this);
		head.findViewById(R.id.cusinfo_follower_text).setOnClickListener(this);
		head.findViewById(R.id.cusinfo_attention_text).setOnClickListener(this);
		
		findViewById(R.id.customer_home_head_message_image).setOnClickListener(this);
		
		moreImage = (ImageView) findViewById(R.id.customer_home_head_more_image);
		moreImage.setOnClickListener(this);
		nickName = (TextView) findViewById(R.id.customer_home_head_nick_text);
		nickName.setText(customer.nickname);
		
		handicapi = (TextView) head.findViewById(R.id.cusinfo_handicapi_txt);
		handicapi.setText(Utils.getDoubleString(mContext, customer.handicapIndex));
		best = (TextView) head.findViewById(R.id.cusinfo_best_txt);
		best.setText(Utils.getIntString(mContext, customer.best));
		matches = (TextView) head.findViewById(R.id.cusinfo_matches_txt);
		matches.setText(String.valueOf(customer.matches));
		
		/**/
		yearsExp = (TextView) head.findViewById(R.id.cusinfo_yearsexp_txt);
		yearsExp.setText(customer.yearsExpStr);
		rank = (TextView) head.findViewById(R.id.cusinfo_cityrank_txt);
		rank.setText(Utils.getCityRankString(mContext, customer.rank));
		heat = (TextView) head.findViewById(R.id.cusinfo_heat_txt);
		heat.setText(String.valueOf(customer.heat));
		act = (TextView) head.findViewById(R.id.cusinfo_activity_txt);
		act.setText(String.valueOf(customer.activity));
		customerAvatar = (ImageView) head.findViewById(R.id.customer_avatar);
		msgAlertIv = (ImageView) findViewById(R.id.cusinfo_msg_img);
		
		mList = (ListviewBottomRefresh) findViewById(R.id.customer_friend_message_listview);
		
		ListView.LayoutParams LP=new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		head.setLayoutParams(LP);
		mList.addHeaderView(head, null, false);
		
		mList.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				loadMoreData();
			}
		});
		
		
		/*加载头像*/
		loadAvatar(customer.sn, customer.avatar, customerAvatar);
		
		DebugTools.getDebug().debug_v("customer.avatar", "---->>>>" + customer.avatar);
		
		mPopupWindowView = mLayoutInflater.inflate(R.layout.customer_more_menu, null);
		mHistoryLinear = (LinearLayout) mPopupWindowView.findViewById(R.id.customer_menu_history_linear);
		mHistoryLinear.setOnClickListener(this);
		mMyBalanceRecordLinear = (LinearLayout) mPopupWindowView.findViewById(R.id.customer_menu_my_balance_linear);
		mMyBalanceRecordLinear.setOnClickListener(this);

		mAboutLinear = (LinearLayout) mPopupWindowView.findViewById(R.id.customer_menu_about_linear);
		mAboutLinear.setOnClickListener(this);
		mExitLinear = (LinearLayout) mPopupWindowView.findViewById(R.id.customer_menu_exit_linear);
		mExitLinear.setOnClickListener(this);
		
		int densityDpi = mContext.getResources().getDisplayMetrics().densityDpi;
		
		mMainMenuPop = new PopupWindow(mPopupWindowView,densityDpi*120/160, densityDpi*160/160);

        mMainMenuPop.setFocusable(true);  
        mMainMenuPop.setOutsideTouchable(true);  
        
        // �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı�����ʹ�ø÷����������֮�⣬�ſɹرմ���  
        mMainMenuPop.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_gray));  

        //���ý��롢��������Ч��  
        //mMainMenuPop.setAnimationStyle(R.style.popupwindow);  
        mMainMenuPop.update();  
        //popupWindow����dismissʱ������������setOutsideTouchable(true)�����view֮��/����back�ĵط�Ҳ�ᴥ��  
        mMainMenuPop.setOnDismissListener(new OnDismissListener() {  
              
            @Override  
            public void onDismiss() {  
                // TODO Auto-generated method stub  
 
            }  
        });  
        
	}
	
	
	@Override
	public void onResume() {
		DebugTools.getDebug().debug_v(TAG, "onResume..");
		
		startPage = MainApp.getInstance().getGlobalData().startPage;
		
		if(null != mFriendHotAdapter && mFriendHotAdapter.list != null && mFriendHotAdapter.list.size() > 0) {
			
			mList.setAdapter(mFriendHotAdapter);
			//Utils.logh(TAG, "exist myInviteAdapter " + myInviteAdapter);
			
		} else {
			
			WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
			initDataAysnc();
//			loadFail.displayFail("加载失败！");
		}
		
		initDataAysnc();
		
		refreshDataAysnc();
		
		
		if (Config.mFriendMessageNewItem != null && mFriendHotAdapter != null) {
			
			mFriendHotAdapter.appendFriendHotItem(Config.mFriendMessageNewItem);
			
		}
		
		/*是否显示msg 的*/
		if(MainApp.getInstance().getGlobalData().msgNumSys == 0){
			
			//Utils.setGone(msgAlertIv);
			msgAlertIv.setVisibility(View.GONE);
		}else{
			
			//Utils.setVisible(msgAlertIv);
			msgAlertIv.setVisibility(View.VISIBLE);
		}
		
		DebugTools.getDebug().debug_v(TAG, "MainApp.getInstance().getGlobalData().msgNumSys......>>>>"+MainApp.getInstance().getGlobalData().msgNumSys);
		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		DebugTools.getDebug().debug_v(TAG, "onPause..");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		clearLoader();
		Debug.stopMethodTracing();
		DebugTools.getDebug().debug_v(TAG, "onDestroy..");
	}
	

	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		DebugTools.getDebug().debug_v(TAG, "onLowMemory..");
	}


	
	/*初始化数据请求*/
	private void initDataAysnc() {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		
		clearLoader();
		/*sn 暂时等于1*/
		Loader1 = new GetCustomerFriendHotListLoader(mContext,sn,sn, startPage,pageSize, 
			new GetCustomerFriendHotListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList) {
				// TODO Auto-generated method stub
				mList.onRefreshComplete();
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || hotList.size() == 0) {
					if(msg.trim().length() == 0) {
						
						msg = getString(R.string.str_friend_no_data);
					}
					// display reload page
					//loadFail.displayNoDataRetry(msg);
					
					initListView(hotList);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					//loadFail.displayNone();
					
					initListView(hotList);
					
					//listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
				} else {
					
					//loadFail.displayFail(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
				Loader1 = null;
			}
		});
		Loader1.requestData();
	}
	
	private void initListView(ArrayList<FriendHotItem> inviteList) {
		
		mFriendHotAdapter = new FriendCircleAdapter(mContext, inviteList,mList, null,true);
		
		if (Config.mFriendMessageNewItem != null) {
			
			mFriendHotAdapter.appendFriendHotItem(Config.mFriendMessageNewItem);
		}
		
		mList.setAdapter(mFriendHotAdapter);
		//Utils.logh(TAG, "initListView myInviteAdapter " + myInviteAdapter);
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetMemberloader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
			Utils.logh(TAG, "clearLoader reqLoader: " + reqLoader);
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	


	private void refreshDataAysnc() {
		if(!Utils.isConnected(mContext)){
			return;
		}
		// 正在请求，不重复请求
		if(isLoading()) {
			return ;
		}
		clearLoader();
		reqLoader = new GetMemberloader(mContext, customer.sn,customer.sn, new GetMemberCallback() {
			
			@Override
			public void callBack(int retId, String msg, Member member) {
				switch (retId) {
					case ReturnCode.REQ_RET_OK:
						handicapi.setText(Utils.getDoubleString(mContext, customer.handicapIndex));
						best.setText(Utils.getIntString(mContext, customer.best));
						heat.setText(String.valueOf(customer.heat));
						rank.setText(Utils.getCityRankString(mContext, customer.rank));
						act.setText(String.valueOf(customer.activity));
						matches.setText(String.valueOf(customer.matches));
						break;
					default:
						break;
				}
			}
		});
		reqLoader.requestData();
	}
	
	private void loadAvatar(String sn,String filename,final ImageView iv){
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(mContext, sn, filename,
				(int) getResources().getDimension(R.dimen.avatar_detail_size));
//		if(null != avatar) {
//			iv.setImageDrawable(avatar);
//		} else {
			iv.setImageResource(R.drawable.avatar_loading);
			AsyncImageLoader.getInstance().loadAvatar(mContext, sn, filename,
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if(null != imageDrawable && null != iv) {
								iv.setImageDrawable(imageDrawable);
							}
						}
				});
		//}
	}
	
	/*加载更多数据*/
	private void loadMoreData() {
		
		if(!Utils.isConnected(mContext)) {
			
			mList.onRefreshComplete();
			return ;
		}
		//WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		
	
		startPage++;
		
		/*sn 暂时等于1*/
		Loader1 = new GetCustomerFriendHotListLoader(mContext, sn, sn,startPage, pageSize, new GetCustomerFriendHotListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList) {
				// TODO Auto-generated method stub
				
				mList.onRefreshComplete();
				
				if(BaseRequest.REQ_RET_OK == retId) {
	
					if (mFriendHotAdapter == null) {
						
						initListView(hotList);
					} else {
						
						mFriendHotAdapter.appendListInfo(hotList);
					}
					
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else {

					//Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				//WaitDialog.dismissWaitDialog();
				Loader1 = null;
			}
		});
		
		Loader1.requestData();
	}
	
	 /**��ʾpopupwindow*/  
    public void showPopupWindow(){  
  
        if(!mMainMenuPop.isShowing()) {  
        	mMainMenuPop.showAsDropDown(moreImage, moreImage.getLayoutParams().width/2, 0);  
        	
        } else{  
        	
        	mMainMenuPop.dismiss();  
        }  
    }  
	
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch(v.getId()) {
			case R.id.customer_menu_exit_linear:
				LoginActivity.backWithPhone(mContext, MainApp.getInstance().getCustomer().phone);
				ExitToLogin.getInstance().exitToLogin();
				JPushInterface.stopPush(mContext);
				break;
			case R.id.cusinfo_myinfo_ll:
				intent = new Intent(mContext,ModifyInfoActivity.class);
				startActivityForResult(intent, Const.REQUST_CODE_MODIFY_MY_INFO);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.customer_home_head_message_image:
				
				MainApp.getInstance().getGlobalData().msgNumSys = 0;
				intent = new Intent(mContext,SysMsgActivity.class);
				startActivity(intent);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.customer_menu_history_linear:
				intent = new Intent(mContext,InviteHistoryActivity.class);
				startActivity(intent);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.customer_menu_my_balance_linear:
				MyBalanceRecordActivity.startMyBalanceRecordActivity(this);
				this.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.customer_menu_about_linear:
				intent = new Intent(mContext,AboutIgolfActivity.class);
				startActivity(intent);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.cusinfo_expand_btn:
				ModifySignatureActivity.startModifySignatureActivity(CustomerHomeActivity.this, customer.signature);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
			case R.id.customer_album_add:
				ImageSelectActivity.startImageSelect(CustomerHomeActivity.this);
				break;
				
			case R.id.customer_home_head_more_image:
				
				showPopupWindow();
				
				break;
				
			case R.id.cusinfo_follower_text:
				
				MyFollowerActivity.startMyFollowerActivity(this);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
				
			case R.id.cusinfo_attention_text:
				
				MyAttentionsActivity.startMyAttentionsActivity(this);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
				
			case R.id.cusinfo_praise_text:
				
				MyPraiseActivity.startMyPraiseActivity(this);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, " ::: requestCode: " + requestCode + " resultCode: " + resultCode + " intent: " + intent);
		if(resultCode != Activity.RESULT_OK) {
			super.onActivityResult(requestCode, resultCode, intent);
			return ;
		}
		if(Const.REQUST_CODE_SIGNATURE_MY == requestCode) {
			String sign = customer.signature.trim();
			Utils.logh(TAG, "sign: " + sign);
			if(sign.length() > 0) {
				//signatureTxtExpand.setText(sign);
			}else{
				//signatureTxtExpand.setText(R.string.str_comm_def_signature);
			}
			return ;
		}
		if(Const.REQUST_CODE_MODIFY_MY_INFO == requestCode && null != intent){
		    boolean changedFlag = intent.getBooleanExtra(Const.BUNDLE_KEY_MY_INFO_CHANGED, false);
		    if(changedFlag){
		    	loadAvatar(customer.sn,customer.avatar,customerAvatar);
		    	//signatureTxtExpand.setText(customer.signature);
		    	yearsExp.setText(customer.yearsExpStr);
			    if(Const.SEX_MALE == customer.sex) {
					//sex.setImageResource(R.drawable.ic_male);
				} else {
					//sex.setImageResource(R.drawable.ic_female);
				}
			   // region.setText(gd.getRegionName(customer.city));
			    //age.setText(String.format(getResources().getString(R.string.str_member_age_wrap), customer.age));
		    }
		}
		if(Const.REQUST_CODE_PHOTE_TAKE_PHOTO == requestCode) {
			if(Activity.RESULT_OK == resultCode) {
//				startPhotoCrop(mUri);
				try {
					//addAlbum(new File(new URI(mUri.toString())));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return ;
			}
		} else if(Const.REQUST_CODE_PHOTE_GALLERY == requestCode) {
			if(Activity.RESULT_OK == resultCode && null != intent) {
//				startPhotoCrop(intent.getData());
				Uri uri = intent.getData();
				String img_path = FileUtils.getMediaImagePath(mContext, uri);
				//addAlbum(new File(img_path));
				return ;
			}
		} else if(Const.REQUST_CODE_PHOTE_CROP == requestCode) {
			if(Activity.RESULT_OK == resultCode && null != intent) {
				Bundle b = intent.getExtras();
				if(null != b) {
					Bitmap photo = b.getParcelable("data");
					if(null != photo) {
						//doAddAlbum(photo,customer.sn);
						return ;
					} else {
						Log.w(TAG, "photo null ! ");
					}
				} else {
					Log.w(TAG, "intent.getExtras() null ! ");
				}
			}
		}
	}
	
	
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
	public void onAvatarReset(String sn, String avatar, Bitmap bitmap) {
		Utils.logh(TAG, "onAvatarReset customer sn: " + customer.sn + " sn: " + sn + " avatar:  " + avatar);
		if(!customer.sn.equals(sn)) {
			Log.w(TAG, "onAvatarReset sn do not fit !!!");
			return ;
		}
		AsyncImageLoader.getInstance().clearOverDueCache(sn, 
				FileUtils.getAvatarPathBySn(mContext, sn, MainApp.getInstance().getCustomer().avatar));
		MainApp.getInstance().getCustomer().avatar = avatar;
		SharedPref.setString(SharedPref.SPK_AVATAR, avatar, mContext);
		customerAvatar.setImageBitmap(bitmap);
	}

	@Override
	public void onAlbumDelete(String sn, String album) {
		Utils.logh(TAG, "onAlbumDelete sn: " + sn + " album:  " + album);
		if(!customer.sn.equals(sn)) {
			Log.w(TAG, "onAlbumDelete sn do not fit !!!");
			return ;
		}
		int pos = -1;
		for(String item : customer.album) {
			pos ++;
			if(item.equals(album)) {
				customer.album.remove(item);
				break;
			}
		}
		Utils.logh(TAG, "delete : " + pos);
		//albumLayout.removeViewAt(pos);
		//refreshAlbumAdd();
		// 异步删除
		AsyncImageLoader.getInstance().deleteAlbum(mContext, sn, album);
	}
	
	public static void updateMsgAlert(int count) {
		Utils.logh(TAG, "updateMsgAlert count: " + count);
		if(count > 0) {
			
			//msgAlertIv.setVisibility(View.VISIBLE);
			Utils.setVisible(msgAlertIv);
		} else {
			
			//msgAlertIv.setVisibility(View.GONE);
			Utils.setGone(msgAlertIv);
		}
	}
}
