package com.hylg.igolf.ui.customer;

import java.util.ArrayList;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.loader.GetMyPraiseTipsListLoader;
import com.hylg.igolf.cs.loader.GetMyPraiseTipsListLoader.GetMyPraisedTipsListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.ui.friend.FriendCircleAdapter;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class MyPraiseActivity extends FragmentActivity implements OnClickListener{
	
	private final String              	TAG                          = "MyPraiseActivity";

	private static String               SN_KEY                      = "sn";

	private ImageButton  				mBack 						= null;
	
	private EhecdListview 				mList                       = null;
	private FriendCircleAdapter mFriendHotAdapter			= null;
	
	private GetMyPraiseTipsListLoader 	reqLoader 					= null;
	private LoadFail 					loadFail;
	
	private String 						sn							= "",
										mem_sn                      = "";
	private int 						startPage					= 0, 
										pageSize					= 0;
	
	private FragmentActivity            mContext                    = null;
	
	
	public static void startMyPraiseActivity(Activity context,String sn) {

		if (sn == null || sn.length() <= 0) {

			return;
		}
		Intent intent = new Intent(context, MyPraiseActivity.class);
		intent.putExtra(SN_KEY,sn);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
	}
	
	public static void startMyPraiseActivity(Fragment context,String sn) {

		if (sn == null || sn.length() <= 0) {

			return;
		}
		Intent intent = new Intent(context.getActivity(), MyPraiseActivity.class);
		intent.putExtra(SN_KEY,sn);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.customer_info_my_praise);
		
		mContext = this;
		loadFail = new LoadFail(mContext);
		loadFail.setOnRetryClickListener(retryListener);

		mem_sn = getIntent().getStringExtra(SN_KEY);
		sn = MainApp.getInstance().getCustomer().sn;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
		initUI();
		initDataAysnc();
		
	}

	public void initUI() {
		
		mBack           	= (ImageButton) findViewById(R.id.customer_info_my_praise_back);
		mList           	= (EhecdListview) findViewById(R.id.customer_info_my_praise_listview);

		mBack.setOnClickListener(this);
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
		refreshData();
		
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
			GetMyPraiseTipsListLoader loader = reqLoader;
			loader.stopTask(true);
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
		reqLoader = new GetMyPraiseTipsListLoader(mContext, sn, mem_sn,startPage, pageSize, new GetMyPraisedTipsListCallback() {
			
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
		reqLoader = new GetMyPraiseTipsListLoader(mContext, sn,  mem_sn,1, startPage*pageSize, new GetMyPraisedTipsListCallback() {
			
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
		reqLoader = new GetMyPraiseTipsListLoader(mContext, sn, mem_sn, startPage, pageSize, new GetMyPraisedTipsListCallback() {
			
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
		
		mFriendHotAdapter = new FriendCircleAdapter(mContext,inviteList, mList, null,false);
		
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
