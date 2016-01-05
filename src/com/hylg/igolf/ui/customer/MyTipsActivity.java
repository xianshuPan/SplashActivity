package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.loader.GetCustomerFriendHotListLoader;
import com.hylg.igolf.cs.loader.GetCustomerFriendHotListLoader.GetCustomerFriendHotListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.ui.friend.FriendCircleAdapter;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

public class MyTipsActivity extends FragmentActivity implements OnClickListener{
	
	private final String              	TAG                          = "MyTipsActivity";

	private ImageButton  				mBack 						= null;

	private TextView 					mTitle                      = null;
	
	private EhecdListview 				mList                       = null;
	private FriendCircleAdapter			mFriendHotAdapter			= null;
	
	private GetCustomerFriendHotListLoader reqLoader 					= null;
	private LoadFail 					loadFail;
	
	private String 						sn							= "";
	private int 						startPage					= 0, 
										pageSize					= 0;
	
	private FragmentActivity            mContext                    = null;
	
	
	public static void startMyTipsActivity(Activity context) {

		Intent intent = new Intent(context, MyTipsActivity.class);
		context.startActivity(intent);
	}
	
	public static void startMyTipsActivity(Fragment context) {

		Intent intent = new Intent(context.getActivity(), MyTipsActivity.class);
		context.startActivity(intent);
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.customer_info_my_praise);
		
		mContext = this;
		loadFail = new LoadFail(mContext);
		loadFail.setOnRetryClickListener(retryListener);
		
		sn = MainApp.getInstance().getCustomer().sn;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
		initUI();
		initDataAysnc();
		
	}

	public void initUI() {
		
		mBack           	= (ImageButton) findViewById(R.id.customer_info_my_praise_back);
		mTitle          	= (TextView) findViewById(R.id.customer_info_my_praise_title_text);
		mList           	= (EhecdListview) findViewById(R.id.customer_info_my_praise_listview);
		
		
		mBack.setOnClickListener(this);
		mTitle.setText(R.string.str_my_tips_title);
		mList.setOnRefreshListener(new EhecdListview.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshData();
			}
		});

		mList.setOnLoadMoreListener(new EhecdListview.OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				
				loadMoreData();
			}
		});
		
		//api = WXAPIFactory.createWXAPI(mContext, "wx14da9bc6378845fe");
		
		//mFriendHotAdapter = new FriendHotAdapter();
		//mList.setAdapter(mFriendHotAdapter);
	}
	
	@Override
	public void onStart() {
		DebugTools.getDebug().debug_v(TAG, "onStart..");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		DebugTools.getDebug().debug_v(TAG, "onResume..");
		
			
//		Bundle data = getArguments();
		
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
		Debug.stopMethodTracing();
		DebugTools.getDebug().debug_v(TAG, "onDestroy..");
	}
	
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		DebugTools.getDebug().debug_v(TAG, "onLowMemory..");
	}

	
	private void clearLoader() {
		if(isLoading()) {
			GetCustomerFriendHotListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	/*初始化数据请求*/
	private void initDataAysnc() {
		
		DebugTools.getDebug().debug_v(TAG, "initDataAysnc__----执行了两次");
		
		
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		reqLoader = new GetCustomerFriendHotListLoader(mContext, sn, sn,startPage, pageSize, new GetCustomerFriendHotListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList) {

				// TODO Auto-generated method stub
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || hotList.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_friend_no_data);
					}
					// display reload page
					loadFail.displayNoDataRetry(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					//loadFail.displayNone();
					
					initListView(hotList);
					
					//listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else {
					loadFail.displayFail(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}

				mList.onRefreshComplete();
				WaitDialog.dismissWaitDialog();
				reqLoader = null;
			
				
			}
		});
		reqLoader.requestData();
	}
	
	/*下拉刷新请求数据*/
	private void refreshData() {
		if(!Utils.isConnected(mContext)) {
			
			mList.onRefreshComplete();
			return ;
		}
		
		//WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		reqLoader = new GetCustomerFriendHotListLoader(mContext, sn,sn, 1, startPage*pageSize, new GetCustomerFriendHotListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList) {
				// TODO Auto-generated method stub
				
				if(BaseRequest.REQ_RET_OK == retId) {
					
					if (mFriendHotAdapter == null) {
						
						initListView(hotList);
						
					} else {
						
						mFriendHotAdapter.refreshListInfo(hotList);
					}
					
					
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else {
					
					//Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				mList.onRefreshComplete();
				//WaitDialog.dismissWaitDialog();
				reqLoader = null;
			}
		});
		reqLoader.requestData();
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
		reqLoader = new GetCustomerFriendHotListLoader(mContext, sn, sn,startPage, pageSize, new GetCustomerFriendHotListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList) {
				// TODO Auto-generated method stub
				
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
				mList.onRefreshComplete();
				//WaitDialog.dismissWaitDialog();
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	/**/
	private onRetryClickListener retryListener = new onRetryClickListener() {
		@Override
		public void onRetryClick() {
			//Utils.logh(TAG, "onRetryClick ... ");
			mFriendHotAdapter = null;
			
			
			DebugTools.getDebug().debug_v(TAG, "onRetryClickListener------>>>>>被执行了");
			initDataAysnc();
		}
	};
	
	
	private void initListView(ArrayList<FriendHotItem> inviteList) {
		
		//mFriendHotAdapter.initListInfo(inviteList);
		
		DebugTools.getDebug().debug_d(TAG, "-----》》》为什么执行了两次");
		
		mFriendHotAdapter = new FriendCircleAdapter(mContext,inviteList, mList, null,true);
		
		if (Config.mFriendMessageNewItem != null ) {
			
			mFriendHotAdapter.appendFriendHotItem(Config.mFriendMessageNewItem);
			
		}
		
		mList.setAdapter(mFriendHotAdapter);
		//Utils.logh(TAG, "initListView myInviteAdapter " + myInviteAdapter);
	}
	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		if (arg0.getId() == mBack.getId()) {
			
			this.finish();
			
		}
			
	}  
}
