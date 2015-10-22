package com.hylg.igolf.ui.member;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.PopupWindow.OnDismissListener;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.data.Member;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.GetCustomerFriendHotListLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.loader.GetMemberloader;
import com.hylg.igolf.cs.loader.GetCustomerFriendHotListLoader.GetCustomerFriendHotListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.FriendAttentionAdd;
import com.hylg.igolf.cs.request.GetMember;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.ui.common.ImageSelectActivity.onImageSelectListener;
import com.hylg.igolf.ui.friend.FriendCircleAdapter;
import com.hylg.igolf.ui.view.ListviewBottomRefresh;
import com.hylg.igolf.ui.view.ListviewBottomRefresh.OnRefreshListener;
import com.hylg.igolf.utils.*;

public class MemDetailActivityNew extends Activity implements View.OnClickListener,onImageSelectListener{
	
	private static final String 				TAG = "MemDetailActivityNew";
	
	private static final String 				BUNDLE_KEY_MEM_SN = "memSn";
	
	private ImageView 							customerAvatar,moreImage;//sex,addAlbumView;

	private TextView 							nickName,attention,yearsExp,handicapi,best,matches,heat,rank,act;
	

	private GetMemberloader 					reqLoader = null;
	
	private GetCustomerFriendHotListLoader 		Loader1 = null;

	private Uri 								mUri;
	
	private ListviewBottomRefresh 				mList ;
	
	private FriendCircleAdapter 				mFriendHotAdapter ;
	
	private int 								startPage = 0, 
												pageSize = 0,
												mAttentionState = -1;
	
	private String 								sn      = "",
												memSn	= "",
												scroeMsg ="",
												fightMsg ="";
	
	private PopupWindow							mMainMenuPop        		= null;
	private View 								mPopupWindowView			= null;  
	
	private LinearLayout 						mScoreHistoryLinear,
												mFightRecordLinear;
	
	private LayoutInflater 						mLayoutInflater ;
	
	private Activity 							mContext ;
	
	public static void startMemDetailActivity(Context context,String memSn) {
		Intent intent = new Intent(context, MemDetailActivityNew.class);
		intent.putExtra(BUNDLE_KEY_MEM_SN, memSn);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mem_ac_detail_new);
		
		mContext = this;
	
		sn = MainApp.getInstance().getCustomer().sn;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
		getViews();
		
		getMemberInfo();
		
		if(null != mFriendHotAdapter && mFriendHotAdapter.list != null && mFriendHotAdapter.list.size() > 0) {
			
			mList.setAdapter(mFriendHotAdapter);
			//Utils.logh(TAG, "exist myInviteAdapter " + myInviteAdapter);
			
		} else {
			
			initDataAysnc();
		}

	}
	
	private void getViews() {
		
		mLayoutInflater = getLayoutInflater();
		
		View head  = mLayoutInflater.inflate(R.layout.customer_info_head, null);
		
		attention = (TextView)head.findViewById(R.id.cusinfo_myinfo_ll);
		attention.setOnClickListener(this);
		attention.setText("");
		
		findViewById(R.id.customer_home_head_back_image).setOnClickListener(this);  
		
		moreImage = (ImageView) findViewById(R.id.customer_home_head_more_image);
		moreImage.setOnClickListener(this);
		nickName = (TextView) findViewById(R.id.customer_home_head_nick_text);
		//nickName.setText(customer.nickname);
		
		handicapi = (TextView) head.findViewById(R.id.cusinfo_handicapi_txt);
		//handicapi.setText(Utils.getDoubleString(this, customer.handicapIndex));
		best = (TextView) head.findViewById(R.id.cusinfo_best_txt);
		//best.setText(Utils.getIntString(this, customer.best));
		matches = (TextView) head.findViewById(R.id.cusinfo_matches_txt);
		//matches.setText(String.valueOf(customer.matches));
		
		/**/
		yearsExp = (TextView) head.findViewById(R.id.cusinfo_yearsexp_txt);
		//yearsExp.setText(customer.yearsExpStr);
		rank = (TextView) head.findViewById(R.id.cusinfo_cityrank_txt);
		//rank.setText(Utils.getCityRankString(this, customer.rank));
		heat = (TextView) head.findViewById(R.id.cusinfo_heat_txt);
		//heat.setText(String.valueOf(customer.heat));
		act = (TextView) head.findViewById(R.id.cusinfo_activity_txt);
		//act.setText(String.valueOf(customer.activity));
		customerAvatar = (ImageView) head.findViewById(R.id.customer_avatar);
		//msgAlertIv = (ImageView) head.findViewById(R.id.cusinfo_msg_img);
		
		mList = (ListviewBottomRefresh) findViewById(R.id.customer_friend_message_listview);
		
		ListView.LayoutParams LP=new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		head.setLayoutParams(LP);
		mList.addHeaderView(head,null,false);
		
		mList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				loadMoreData();
			}
		});
		
		/*是否显示msg 的*/
//		if(MainApp.getInstance().getGlobalData().msgNumSys == 0){
//			Utils.setGone(msgAlertIv);
//		}else{
//			Utils.setVisible(msgAlertIv);
//		}
		
		mPopupWindowView = mLayoutInflater.inflate(R.layout.member_more_menu, null);
		mScoreHistoryLinear = (LinearLayout) mPopupWindowView.findViewById(R.id.member_menu_score_history_linear);
		mScoreHistoryLinear.setOnClickListener(this);
		mFightRecordLinear = (LinearLayout) mPopupWindowView.findViewById(R.id.member_menu_fight_record_linear);
		mFightRecordLinear.setOnClickListener(this);
		
		int densityDpi = this.getResources().getDisplayMetrics().densityDpi;
		
		mMainMenuPop = new PopupWindow(mPopupWindowView,densityDpi*120/160, densityDpi*80/160);  
        mMainMenuPop.setFocusable(true);  
        mMainMenuPop.setOutsideTouchable(true);  
        
        // �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı�����ʹ�ø÷����������֮�⣬�ſɹرմ���  
        mMainMenuPop.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_gray));  

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
        
        this.memSn = getIntent().getExtras().getString(BUNDLE_KEY_MEM_SN);
	}
	
	private void getMemberInfo(){
		new AsyncTask<Object, Object, Integer>() {
			GetMember request = new GetMember(mContext, MainApp.getInstance().getCustomer().sn, memSn);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					
					attention.setVisibility(View.VISIBLE);
					initMemberInfo(request.getMember());
					
				} else {
					
					attention.setVisibility(View.GONE);
					Toast.makeText(mContext,request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
	}
	
	private void initMemberInfo(Member member){
		
		//loadAvatar(member.sn,member.avatar);
		/*加载头像*/
		loadAvatar(member.sn,member.avatar,customerAvatar);
		
		nickName.setText(member.nickname);
		
		//Utils.setLevel(MemDetailActivity.this, ratell, (int) getResources().getDimension(R.dimen.personal_detail_rate_star_size), member.rate);
		yearsExp.setText(member.yearsExpStr);
		rank.setText(Utils.getCityRankString(this, member.rank));
		heat.setText(String.valueOf(member.heat));
		act.setText(String.valueOf(member.activity));
		best.setText(Utils.getIntString(this, member.best));
		handicapi.setText(Utils.getDoubleString(this, member.handicapIndex));
		matches.setText(String.valueOf(member.matches));
		
		mAttentionState = member.attention;
		if (member.attention == 0) {
			
			attention.setText(R.string.str_friend_attention);
			attention.setTextColor(getResources().getColor(R.color.color_tab_green));
			attention.setBackgroundResource(R.drawable.attent_color);
			//attention.setBackgroundColor(getResources().getColor(R.color.color_title_txt));
			
		} else if (member.attention == 1) {
			
			attention.setText(R.string.str_friend_attented);
			attention.setTextColor(getResources().getColor(R.color.color_white));
			attention.setBackgroundResource(R.drawable.attented_color);
			//attention.setBackgroundColor(getResources().getColor(R.color.color_hint_txt));
		}
		
		//scoreView.setText(member.scoreMsg);
		scroeMsg = member.scoreMsg;
		fightMsg = member.fightMsg;
		//initAlbum(member);
	}
	
	
	@Override
	public void onResume() {
		DebugTools.getDebug().debug_v(TAG, "onResume..");
		
		
		if (Config.mFriendMessageNewItem != null && mFriendHotAdapter != null) {
			
			mFriendHotAdapter.appendFriendHotItem(Config.mFriendMessageNewItem);
			
		}
		
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
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		Loader1 = new GetCustomerFriendHotListLoader(this,sn,memSn, startPage,pageSize, 
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
		
		mFriendHotAdapter = new FriendCircleAdapter(mContext, inviteList, mList, null,false);
		
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
		Loader1 = new GetCustomerFriendHotListLoader(mContext, sn,memSn, startPage, pageSize, new GetCustomerFriendHotListCallback() {
			
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

		switch(v.getId()) {
			case R.id.customer_home_head_back_image:
				
				this.finish();
				
				break;
			case R.id.member_menu_score_history_linear:
				
				MemScoreHistoryActivity.startMemScoreHistoryActivity(mContext, scroeMsg,memSn);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				
				break;
			case R.id.member_menu_fight_record_linear:
				MemFightRecordActivity.startMemFightRecordActivity(mContext, fightMsg,memSn);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
				
			case R.id.customer_home_head_more_image:
				
				showPopupWindow();
				
				break;
				
			case R.id.cusinfo_myinfo_ll:
				
				attention();
				
				break;
		}
	}
	
	/*
	 * 添加关注
	 * */
	private void attention() {
		
		/*添加或取消关注*/
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_waiting);
		new AsyncTask<Object, Object, Integer>() {
		
			FriendAttentionAdd request = new FriendAttentionAdd(mContext,sn,memSn,mAttentionState);
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				if(BaseRequest.REQ_RET_OK == result) {
					
					mAttentionState = mAttentionState == 1 ? 0 : 1;
					
					if (mAttentionState == 0) {
						
						attention.setText(R.string.str_friend_attention);
						attention.setTextColor(getResources().getColor(R.color.color_tab_green));
						attention.setBackgroundResource(R.drawable.attent_color);
						//attention.setBackgroundColor(getResources().getColor(R.color.color_title_txt));
						
					} else if (mAttentionState == 1) {
						
						attention.setText(R.string.str_friend_attented);
						attention.setTextColor(getResources().getColor(R.color.color_white));
						attention.setBackgroundResource(R.drawable.attented_color);
						//attention.setBackgroundColor(getResources().getColor(R.color.color_hint_txt));
					}
					
				} else {
					

				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, " ::: requestCode: " + requestCode + " resultCode: " + resultCode + " intent: " + intent);
		if(resultCode != Activity.RESULT_OK) {
			super.onActivityResult(requestCode, resultCode, intent);
			return ;
		}
		if(Const.REQUST_CODE_SIGNATURE_MY == requestCode) {}
		if(Const.REQUST_CODE_MODIFY_MY_INFO == requestCode && null != intent){}
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

}
