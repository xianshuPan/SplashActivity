package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.data.Member;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.loader.GetMemberloader;
import com.hylg.igolf.cs.loader.GetMemberloader.GetMemberCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.GetSelfInfo;
import com.hylg.igolf.cs.request.ReturnCode;
import com.hylg.igolf.ui.coach.CoachApplyInfoActivity;
import com.hylg.igolf.ui.common.AlbumPagerActivity.OnAlbumSetAvatarListener;
import com.hylg.igolf.ui.common.ImageSelectActivity.onImageSelectListener;
import com.hylg.igolf.ui.friend.publish.PublishBean;
import com.hylg.igolf.ui.friend.publish.PublishDB;
import com.hylg.igolf.ui.friend.publish.TaskException;
import com.hylg.igolf.ui.friend.publish.WorkTask;
import com.hylg.igolf.ui.hall.HallMyInvitesActivity;
import com.hylg.igolf.ui.view.ShareMenuCutomerInfo;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CustomerInfoHomeFrgNew extends Fragment implements View.OnClickListener,
															onImageSelectListener,
															OnAlbumSetAvatarListener{
	
	private static final String 				TAG = "CustomerInfoHomeFrgNew";
	private static CustomerInfoHomeFrgNew 		customerFrg = null;
	
	//private TextView signatureTxtExpand;
	private Customer 							customer;
	
	private ImageView 							msgHint,customerAvatar,shareImage,settingImage,sexImage;//sex,addAlbumView;
	//private View addAlbumSpace;
	private TextView 							nickName,location,industry,yearsExp,handicapi,best,matches,heat,rank,act;

	private GetMemberloader 					reqLoader = null;

	private Uri 								mUri;

	private String 								sn  = "";
	
	private LinearLayout 						mPraiseLinear,mAttentionLinear,mFollowerLinear;
	private TextView                            mBalanceTxt,mPraiseCountTxt,mAttentionCountTxt,mFollowerCountTxt;

	private TextView 							mMyTipsTxt,mMyInviteTxt,mMyInviteHistoryTxt,mMyTeachingTxt,mMyBalanceTxt,mMyCoachTxt,mMyMessageTxt,mMyDraftTxt,mInviteFriendTxt;

	private boolean                             isDestroyed = false;
	
	public static final CustomerInfoHomeFrgNew getInstance() {
		if(null == customerFrg) {
			customerFrg = new CustomerInfoHomeFrgNew();
		}
		return customerFrg;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		Bundle args = getArguments();cusinfo_logout
		customer = MainApp.getInstance().getCustomer();
		
		sn = MainApp.getInstance().getCustomer().sn;
	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view  = inflater.inflate(R.layout.customer_info_frg, container, false);
		
		shareImage = (ImageView) view.findViewById(R.id.customer_info_share_image);
		shareImage.setOnClickListener(this);
		settingImage = (ImageView) view.findViewById(R.id.customer_info_setting_image);
		settingImage.setOnClickListener(this);
		sexImage = (ImageView) view.findViewById(R.id.customer_info_sex_image);

		nickName = (TextView) view.findViewById(R.id.customer_info_name_text);
		handicapi = (TextView) view.findViewById(R.id.customer_info_handicapi_txt);
		best = (TextView) view.findViewById(R.id.customer_info_best_txt);
		matches = (TextView) view.findViewById(R.id.customer_info_matches_txt);
		/**/
		yearsExp = (TextView) view.findViewById(R.id.customer_info_yearsexp_txt);
		rank = (TextView) view.findViewById(R.id.customer_info_cityrank_txt);
		heat = (TextView) view.findViewById(R.id.customer_info_heat_txt);
		act = (TextView) view.findViewById(R.id.customer_info_activity_txt);
		location = (TextView) view.findViewById(R.id.customer_info_location_txt);
		industry = (TextView) view.findViewById(R.id.customer_info_industry_txt);
		customerAvatar = (ImageView) view.findViewById(R.id.customer_info_avatar_image);
		msgHint = (ImageView) view.findViewById(R.id.customer_info_my_message_hint);
		sexImage = (ImageView) view.findViewById(R.id.customer_info_sex_image);

		mMyTipsTxt = (TextView) view.findViewById(R.id.customer_info_my_tips_content_text);
		mMyInviteTxt = (TextView) view.findViewById(R.id.customer_info_my_invite_content_text);
		mMyInviteHistoryTxt = (TextView) view.findViewById(R.id.customer_info_my_invite_history_content_text);
		mMyTeachingTxt = (TextView) view.findViewById(R.id.customer_info_my_teaching_content_text);
		mMyBalanceTxt = (TextView) view.findViewById(R.id.customer_info_my_balance_content_text);
		mMyCoachTxt = (TextView) view.findViewById(R.id.customer_info_my_coach_content_text);
		mMyMessageTxt = (TextView) view.findViewById(R.id.customer_info_my_message_content_text);
		mMyDraftTxt = (TextView) view.findViewById(R.id.customer_info_my_draft_content_text);
		mInviteFriendTxt = (TextView) view.findViewById(R.id.customer_info_invite_friend_content_text);
		mPraiseLinear = (LinearLayout) view.findViewById(R.id.customer_info_my_praise_linear);
		mFollowerLinear = (LinearLayout) view.findViewById(R.id.customer_info_my_follower_linear);
		mAttentionLinear = (LinearLayout) view.findViewById(R.id.customer_info_my_attention_linear);

		mPraiseCountTxt = (TextView) view.findViewById(R.id.customer_info_my_praise_txt);
		mFollowerCountTxt = (TextView) view.findViewById(R.id.customer_info_my_follower_txt);
		mAttentionCountTxt = (TextView) view.findViewById(R.id.customer_info_my_attention_txt);

		mMyTipsTxt.setOnClickListener(this);
		mMyInviteTxt.setOnClickListener(this);
		mMyInviteHistoryTxt.setOnClickListener(this);
		mMyTeachingTxt.setOnClickListener(this);
		mMyBalanceTxt.setOnClickListener(this);
		mMyCoachTxt.setOnClickListener(this);
		mMyMessageTxt.setOnClickListener(this);
		mMyDraftTxt.setOnClickListener(this);
		mInviteFriendTxt.setOnClickListener(this);

		mPraiseLinear.setOnClickListener(this);
		mFollowerLinear.setOnClickListener(this);
		mAttentionLinear.setOnClickListener(this);

		updateUiValue();
        
       // mFriendHotAdapter = new FriendHotAdapter();
		return view;
	}

	private void updateUiValue() {

		nickName.setText(customer.nickname);
		best.setText(Utils.getIntString(getActivity(), customer.best));
		matches.setText(String.valueOf(customer.matches));
		handicapi.setText(Utils.getDoubleString(getActivity(), customer.handicapIndex));
		yearsExp.setText(customer.yearsExpStr+getResources().getString(R.string.str_year));
		rank.setText(Utils.getCityRankString(getActivity(), customer.rank));
		heat.setText(String.valueOf(customer.heat));
		act.setText(String.valueOf(customer.activity));
		location.setText(MainApp.getInstance().getGlobalData().getRegionName(customer.city));
		industry.setText(MainApp.getInstance().getGlobalData().getIndustryName(customer.industry));

		if (customer.sex == 0) {

			sexImage.setImageResource(R.drawable.man);
		} else {

			sexImage.setImageResource(R.drawable.woman);
		}
		/*加载头像*/
		//Utils.loadAvatar(getActivity(),customer.sn, customerAvatar);

		//loadAvatar(customer.sn, customer.sn + ".jpg", customerAvatar);

		DownLoadImageTool.getInstance(getActivity()).displayImage(Utils.getAvatarURLString(customer.sn),customerAvatar,null);
		//Utils.addStrikeLine()

		DebugTools.getDebug().debug_v("customer.avatar", "---->>>>" + customer.avatar);
	}
	
	@Override
	public void onResume() {
		DebugTools.getDebug().debug_v(TAG, "onResume..");

		
		/*是否显示msg 的*/
		if(MainApp.getInstance().getGlobalData().msgNumSys == 0){
			
			Utils.setGone(msgHint);
			mMyMessageTxt.setText("");

		} else {
			
			Utils.setVisible(msgHint);
			mMyMessageTxt.setText(String.valueOf(MainApp.getInstance().getGlobalData().msgNumSys));
		}

		getMember();

		getSelfInfo();
		
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

		isDestroyed = true;
		clearLoader();
		Debug.stopMethodTracing();
		DebugTools.getDebug().debug_v(TAG, "onDestroy..");
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		DebugTools.getDebug().debug_v(TAG, "onAttach..");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		DebugTools.getDebug().debug_v(TAG, "onDetach..");
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		DebugTools.getDebug().debug_v(TAG, "onLowMemory..");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		DebugTools.getDebug().debug_v(TAG, "onActivityCreated..");
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
	


	private void getMember() {
		if(!Utils.isConnected(getActivity())){
			return;
		}
		// 正在请求，不重复请求
		if(isLoading()) {
			return ;
		}
		clearLoader();
		reqLoader = new GetMemberloader(getActivity(), customer.sn,customer.sn, new GetMemberCallback() {
			
			@Override
			public void callBack(int retId, String msg, Member member,CoachItem coach_item) {
				switch (retId) {
					case ReturnCode.REQ_RET_OK:

						if (!isDestroyed) {

							updateUiValue();

							if (coach_item == null) {

								return;
							}

							if(coach_item.audit == 0) {

								mMyCoachTxt.setText(R.string.str_coach_apply_status_waiting);
								mMyCoachTxt.setTextColor(getResources().getColor(R.color.color_yellow));

							} else if(coach_item.audit == 1) {

								mMyCoachTxt.setText(R.string.str_coach_apply_status_success);
								mMyCoachTxt.setTextColor(getResources().getColor(R.color.green_5fb64e));

							} else if(coach_item.audit == 2) {

								mMyCoachTxt.setTextColor(getResources().getColor(R.color.color_red));
								mMyCoachTxt.setText(R.string.str_coach_apply_status_fail);
							}

						}

						break;
					default:
						break;
				}
			}
		});
		reqLoader.requestData();
	}

	private void getSelfInfo() {
		if(!Utils.isConnected(getActivity())){
			return;
		}

		final GetSelfInfo request = new GetSelfInfo(getActivity(),customer.sn);
		new AsyncTask<Object, Object, Integer>() {
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

				if(BaseRequest.REQ_RET_OK == result) {

					mPraiseCountTxt.setText(String.valueOf(request.praiseAmount));
					mFollowerCountTxt.setText(String.valueOf(request.myFansAmount));
					mAttentionCountTxt.setText(String.valueOf(request.myAttentionAmount));

				}
				else {

					Toast.makeText(getActivity(),request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}

			}
		}.execute(null, null, null);

		new WorkTask<Void, Void, Integer>() {

			@Override
			protected void onSuccess(Integer result) {
				super.onSuccess(result);

				if (result > 0) {

					mMyDraftTxt.setText(String.valueOf(result));
				}

			}

			@Override
			public Integer workInBackground(Void... params) throws TaskException {
				int count = 0;
				if (MainApp.getInstance().getUser() != null) {

					ArrayList<PublishBean> sadf = PublishDB.getPublishList(MainApp.getInstance().getUser());
					count = PublishDB.getPublishList(MainApp.getInstance().getUser()).size();
				}

				return count;

			}
		}.execute();

	}
	
	private void loadAvatar(String sn,String filename,final ImageView iv){
		Drawable avatar = AsyncImageLoader.getInstance().getAvatar(getActivity(), sn, filename,
				(int) getResources().getDimension(R.dimen.avatar_detail_size));
//		if(null != avatar) {
//			iv.setImageDrawable(avatar);
//		} else {
			iv.setImageResource(R.drawable.avatar_loading);
			AsyncImageLoader.getInstance().loadAvatar(getActivity(), sn, filename,
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable) {
							if (null != imageDrawable && null != iv) {

								FadeInBitmapDisplayer.animate(iv, 500);
								iv.setImageDrawable(imageDrawable);
							}
						}
					});
		//}
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch(v.getId()) {

			case R.id.customer_info_share_image :

				ShareMenuCutomerInfo share = new ShareMenuCutomerInfo(getActivity(),mMyDraftTxt,customer.sn);
				share.showPopupWindow();

				break;

			case R.id.customer_info_my_tips_content_text:

				MyTipsActivity.startMyTipsActivity(this);

//				Uri smsToUri = Uri.parse("smsto:15295990599;13408458008");
//				Intent mIntent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri );
//				mIntent.putExtra("sms_body", "大家一起来使用爱高尔夫");
//				startActivity( mIntent );

				break;

			case R.id.customer_info_my_invite_content_text:
				intent = new Intent(getActivity(),HallMyInvitesActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);

				break;

			case R.id.customer_info_my_invite_history_content_text:
				intent = new Intent(getActivity(),InviteHistoryActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;

			case R.id.customer_info_my_teaching_content_text:

				MyTeachingHomeActivity.startCoachMyTeachingHomeActivity(getActivity());

				break;

			case R.id.customer_info_my_coach_content_text:

				CoachApplyInfoActivity.startCoachApplyInfoActivity(getActivity());

				break;

			case R.id.customer_info_invite_friend_content_text:

				//CoachApplyInfoActivity.startCoachApplyInfoActivity(getActivity());
				InviteFriendActivity.startInviteFriendActivity(getActivity(),InviteFriendActivity.CUSTOMER_INFO);

				break;

			case R.id.customer_info_my_message_content_text:

				MainApp.getInstance().getGlobalData().msgNumSys = 0;
				intent = new Intent(getActivity(),SysMsgActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);

				break;

			case R.id.customer_info_my_draft_content_text:

				DraftFragmentActivity.startDraftFragmentActivity(this);

				break;

			case R.id.customer_info_my_balance_content_text:
				MyBalanceRecordActivity.startMyBalanceRecordActivity(getActivity());
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
				
			case R.id.customer_info_my_follower_linear:
				
				MyFollowerActivity.startMyFollowerActivity(this,sn);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
				
			case R.id.customer_info_my_attention_linear:
				
				MyAttentionsActivity.startMyAttentionsActivity(this,sn);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
				
			case R.id.customer_info_my_praise_linear:
				
				MyPraiseActivity.startMyPraiseActivity(this,sn);
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;

			case R.id.customer_info_setting_image:

				SettingInfoActivity.startSettingInfoActivity(getActivity());
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
				String img_path = FileUtils.getMediaImagePath(getActivity(), uri);
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
				FileUtils.getAvatarPathBySn(getActivity(), sn, MainApp.getInstance().getCustomer().avatar));
		MainApp.getInstance().getCustomer().avatar = avatar;
		SharedPref.setString(SharedPref.SPK_AVATAR, avatar, getActivity());
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
		AsyncImageLoader.getInstance().deleteAlbum(getActivity(), sn, album);
	}
	
	public static void updateMsgAlert(int count) {
		Utils.logh(TAG, "updateMsgAlert count: " + count);
		if(count > 0) {
			
			//msgAlertIv.setVisibility(View.VISIBLE);
			//Utils.setVisible(msgAlertIv);
		} else {
			
			//msgAlertIv.setVisibility(View.GONE);
			//Utils.setGone(msgAlertIv);
		}
	}
}
