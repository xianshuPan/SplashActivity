package com.hylg.igolf.ui.friend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.loader.GetNewTipsLoader;
import com.hylg.igolf.cs.loader.GetNewTipsLoader.GetNewTipsListCallback;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendNewTipsCountActivity extends FragmentActivity {
	
	private final String 				TAG 						= "FriendNewTipsCountActivity";
	
	private ImageButton  				mBack 						= null;
	
	private	ListView 					mList 						= null;
	private ArrayList<FriendHotItem> 	list;
	
	private FragmentActivity 			mContext 					= null;
	
	private String 						sn							= "";
	
	private GetNewTipsLoader     		reqLoader                   = null;
	
	
	private NewTipsAdapter           	mNewTipsAdapter			= null;
	
	
	public static void startFriendTipsCountActivity(Activity context) {

		Intent intent = new Intent(context, FriendNewTipsCountActivity.class);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
	}
	
	public static void startFriendTipsCountActivity(Fragment context) {

		Intent intent = new Intent(context.getActivity(), FriendNewTipsCountActivity.class);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend_new_tips_count);
		
		mBack =  (ImageButton) findViewById(R.id.friend_tips_count_back);
		mList = (ListView) findViewById(R.id.friend_tips_count_listview);
		
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Bundle data = new Bundle();
				
				String TipsId = list.get(arg2).tipid;
				data.putString("TipsId", TipsId);
				
				FriendTipsDetailActivity.startFriendTipsDetailActivity(mContext, data);
				
			}
		});
		
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContext.finish();
			}
		});
		//mList.addFooterView(new ListFooter(mContext).getFooterView());
		
		sn = MainApp.getInstance().getCustomer().sn;
		
		initDataAysnc();
		
	}
	
	@Override
	protected void onResume () {
		
		DebugTools.getDebug().debug_v(TAG, "sn??/、、？/????"+sn);
		
		super.onResume();
	}
	
	
	
	/*初始化数据请求*/
	private void initDataAysnc() {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		
		GlobalData sadf = MainApp.getInstance().getGlobalData();
		
		/*sn 暂时等于1*/
		reqLoader = new GetNewTipsLoader(mContext, sadf.tipsIds, MainApp.getInstance().getCustomer().sn,new GetNewTipsListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<FriendHotItem> List) {
				// TODO Auto-generated method stub
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || List.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_friend_no_data);
					}
					// display reload page
					//loadFail.displayNoDataRetry(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					MainApp.getInstance().getGlobalData().tipsAmount = 0;
					MainApp.getInstance().getGlobalData().tipsIds = "";
					
					//loadFail.displayNone();
					initListView(List);
					//listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else {
					//loadFail.displayFail(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
				reqLoader = null;	
			}
		});
		
		reqLoader.requestData();
	}
	
	

	private void clearLoader() {
		if(isLoading()) {
			GetNewTipsLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	
	private void initListView(ArrayList<FriendHotItem> inviteList) {
		
		mNewTipsAdapter = new NewTipsAdapter(inviteList);
		
		mList.setAdapter(mNewTipsAdapter);
		//Utils.logh(TAG, "initListView myInviteAdapter " + myInviteAdapter);
	}
	
	private class NewTipsAdapter extends IgBaseAdapter{
		
		
		public NewTipsAdapter(ArrayList<FriendHotItem> list1) {
			
			list = list1;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			
			if (arg1 == null) {
				
				holder = new ViewHolder();
				arg1 = mContext.getLayoutInflater().inflate(R.layout.friend_new_tips_count_item, null);
				
				holder.avatarImage = (ImageView) arg1.findViewById(R.id.friend_new_tips_item_headImage);
				
				holder.userName = (TextView)arg1.findViewById(R.id.friend_new_tips_item_nameText);
				holder.content = (TextView)arg1.findViewById(R.id.friend_new_tips_item_contentText);
				holder.time = (TextView)arg1.findViewById(R.id.friend_new_tips_item_timeText);
				holder.contentImage = (ImageView)arg1.findViewById(R.id.friend_new_tips_item_content_image);
				
				arg1.setTag(holder);
				
			} else {
				
				holder = (ViewHolder)arg1.getTag();
			}
			
			String imageUrlStr = list.get(arg0).imageURL;
			
			ArrayList<HashMap<String, String>> comments = list.get(arg0).comments;
			
			if (comments != null && comments.size() > 0) {
				
				holder.userName.setText(comments.get(comments.size()-1).get("name"));
				holder.content.setText(comments.get(comments.size()-1).get("content"));
				
				if (comments.get(comments.size()-1).get("commentstime") != null && 
					comments.get(comments.size()-1).get("commentstime").length() > 0) {
					
					
					Date date = new Date(Long.valueOf(comments.get(comments.size()-1).get("commentstime")));
					
					Calendar sd = Calendar.getInstance();
					
					sd.setTime(date);

					
					holder.time.setText(sd.get(Calendar.HOUR)+":"+sd.get(Calendar.MINUTE));
				}
			
				
				loadAvatar(mContext, list.get(arg0).sn, list.get(arg0).avatar, holder.avatarImage);
				
				if (imageUrlStr != null && imageUrlStr.indexOf(",") > 0) {
					
					String imageUrl = imageUrlStr.substring(0, imageUrlStr.indexOf(","));
					
					String imageUrlLoad = BaseRequest.TipsPic_Thum_PATH+imageUrl;
					
					DownLoadImageTool.getInstance(mContext).displayImage(imageUrlLoad, holder.contentImage, null);
				}
				
			}
			
			
			return arg1;
		}
		
	}
	
	private static class ViewHolder {

		public ImageView avatarImage ;
		public TextView userName;
		public TextView content;
		public TextView time ;
		public ImageView contentImage ;
	}
	

}
