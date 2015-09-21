package com.hylg.igolf.ui.customer;

import java.util.ArrayList;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.MyFolloweInfo;
import com.hylg.igolf.cs.loader.GetMyFollowerListLoader;
import com.hylg.igolf.cs.loader.GetMyFollowerListLoader.GetMyFollowerListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.FriendAttentionAdd;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.EhecdListview.OnLoadMoreListener;
import com.hylg.igolf.ui.view.EhecdListview.OnRefreshListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyFollowerActivity extends FragmentActivity {
	
	private final String 				TAG 						= "MyFollowerActivity";
	
	private ImageButton  				mBack 						= null;
	
	private EhecdListview 				mList 						= null;
	
	private FragmentActivity 			mContext 					= null;
	
	private String 						sn							= "",
										attention_sn				= "";
	
	private int 						startPage					= 0, 
										pageSize					= 10,
										mCurrentPositionInt			= 0;
	
	private GetMyFollowerListLoader     reqLoader                   = null;
	
	
	private MyFollowerAdapter           mMyFollowerAdapter			= null;
	
	
	public static void startMyFollowerActivity(Activity context) {

		Intent intent = new Intent(context, MyFollowerActivity.class);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
	}
	
	public static void startMyFollowerActivity(Fragment context) {

		Intent intent = new Intent(context.getActivity(), MyFollowerActivity.class);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.customer_info_my_follower);
		
		mBack =  (ImageButton)  findViewById(R.id.customer_info_my_follower_back);
		mList = (EhecdListview) findViewById(R.id.customer_info_my_follower_listview);
		
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContext.finish();
			}
		});
		//mList.addFooterView(new ListFooter(mContext).getFooterView());
		
		
		
		sn = MainApp.getInstance().getCustomer().sn;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
		mList.setOnLoadMoreListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				loadMoreData();
			}
		});
		
		mList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshData();
			}
		});
		
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
		/*sn 暂时等于1*/
		reqLoader = new GetMyFollowerListLoader(mContext,sn, startPage,pageSize,
				
		new GetMyFollowerListCallback() {
					
			@Override
			public void callBack(int retId, String msg, ArrayList<MyFolloweInfo> List) {
				// TODO Auto-generated method stub
				mList.onRefreshComplete();
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || List.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_friend_no_data);
					}
					// display reload page
					//loadFail.displayNoDataRetry(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
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
	
	/*加载更多*/
	private void loadMoreData() {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		//WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		
		startPage++;
		/*sn 暂时等于1*/
		reqLoader = new GetMyFollowerListLoader(mContext,sn, startPage,pageSize,
				
		new GetMyFollowerListCallback() {
					
			@Override
			public void callBack(int retId, String msg, ArrayList<MyFolloweInfo> List) {
				// TODO Auto-generated method stub
				mList.onRefreshComplete();
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || List.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_friend_no_data);
					}
					// display reload page
					//loadFail.displayNoDataRetry(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					//loadFail.displayNone();
					
					//initListView(List);
					mMyFollowerAdapter.appendListInfo(List);
					
					//listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
				} else {
					//loadFail.displayFail(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				//WaitDialog.dismissWaitDialog();
				reqLoader = null;	
			}
		});
		reqLoader.requestData();
	}
	
	/*初始化数据请求*/
	private void refreshData() {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		//WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		reqLoader = new GetMyFollowerListLoader(mContext,sn, 1,startPage*pageSize,
				
		new GetMyFollowerListCallback() {
					
			@Override
			public void callBack(int retId, String msg, ArrayList<MyFolloweInfo> List) {
				// TODO Auto-generated method stub
				mList.onRefreshComplete();
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || List.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_friend_no_data);
					}
					// display reload page
					//loadFail.displayNoDataRetry(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					//loadFail.displayNone();
					
					initListView(List);
					
					//listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
				} else {
					//loadFail.displayFail(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				//WaitDialog.dismissWaitDialog();
				reqLoader = null;	
			}
		});
		reqLoader.requestData();
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetMyFollowerListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	
	private void initListView(ArrayList<MyFolloweInfo> inviteList) {
		
		mMyFollowerAdapter = new MyFollowerAdapter(inviteList);
		
		mList.setAdapter(mMyFollowerAdapter);
		//Utils.logh(TAG, "initListView myInviteAdapter " + myInviteAdapter);
	}
	
	private class MyFollowerAdapter extends IgBaseAdapter{
		
		private ArrayList<MyFolloweInfo> list;
		
		public MyFollowerAdapter(ArrayList<MyFolloweInfo> list) {
			
			this.list = list;
		}
		
		public void refreshListInfo(ArrayList<MyFolloweInfo> list) {
			this.list.clear();
			this.list = list;
			notifyDataSetChanged();
		}
		
		public void appendListInfo(ArrayList<MyFolloweInfo> list) {
			for(int i=0, size=list.size(); i<size; i++) {
				this.list.add(list.get(i));
			}
			notifyDataSetChanged();
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
			
			final int index = arg0;
			ViewHolder holder = null;
			
			if (arg1 == null) {
				
				holder = new ViewHolder();
				arg1 = mContext.getLayoutInflater().inflate(R.layout.customer_info_my_follower_item, null);
				
				holder.avatarImage = (ImageView) arg1.findViewById(R.id.customer_info_my_follower_headImage);
				holder.userName = (TextView)arg1.findViewById(R.id.customer_info_my_follower_nameText);
				holder.dynamic = (TextView)arg1.findViewById(R.id.customer_info_my_follower_dynamicText);
				holder.praise = (TextView)arg1.findViewById(R.id.customer_info_my_follower_praiseText);
				holder.attention = (TextView)arg1.findViewById(R.id.customer_info_my_follower_is_attention_text);
				
				arg1.setTag(holder);
				
			} else {
				
				holder = (ViewHolder)arg1.getTag();
			}
			
			loadAvatar(mContext, list.get(arg0).sn, list.get(arg0).avatar, holder.avatarImage);
			
			holder.userName.setText(list.get(arg0).nickName);
			holder.dynamic.setText(list.get(arg0).dynamic+"动态");
			holder.praise.setText(list.get(arg0).praises+"赞");
			
			if(list.get(arg0).attentionOrNot == 0) {
				
				holder.attention.setText(R.string.str_friend_attention);//color_tab_green
				holder.attention.setTextColor(getResources().getColor(R.color.color_tab_green));
				holder.attention.setBackgroundResource(R.drawable.attent_color);
				
			} else if (list.get(arg0).attentionOrNot == 1) {
				
				holder.attention.setText(R.string.str_friend_attented);
				holder.attention.setTextColor(getResources().getColor(R.color.color_white));
				holder.attention.setBackgroundResource(R.drawable.attented_color);
			}
			
			
			final String sn_tips = list.get(arg0).sn;
			holder.attention.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					String snStr = sn_tips;
					attention_sn = snStr;
					
					int indexInt = index;
					mCurrentPositionInt = indexInt;
					
					/*添加关注*/
					attention();
				}
			});
			

			return arg1;
		}
		
	}
	
	private static class ViewHolder {

		public ImageView avatarImage ;
		public TextView userName ;
		public TextView dynamic;
		public TextView praise ;
		public TextView attention ;
	}
	
	/*
	 * 添加关注
	 * */
	private void attention() {
		
		/*添加或取消关注*/
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_waiting);
		new AsyncTask<Object, Object, Integer>() {
		
			FriendAttentionAdd request = new FriendAttentionAdd(mContext,sn,attention_sn,mMyFollowerAdapter.list.get(mCurrentPositionInt).attentionOrNot);
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrlGet();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				if(BaseRequest.REQ_RET_OK == result) {
					
					/*已经关注过*/
					int attention = mMyFollowerAdapter.list.get(mCurrentPositionInt).attentionOrNot;
					
					mMyFollowerAdapter.list.get(mCurrentPositionInt).attentionOrNot = attention == 1 ? 0 : 1;
					
					mMyFollowerAdapter.notifyDataSetChanged();
					
				} else {
					

				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

}
