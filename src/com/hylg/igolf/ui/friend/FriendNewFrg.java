package com.hylg.igolf.ui.friend;

import java.util.ArrayList;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.loader.GetFriendHotListLoader;
import com.hylg.igolf.cs.loader.GetFriendHotListLoader.GetFriendHotListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.EhecdListview.OnLoadMoreListener;
import com.hylg.igolf.ui.view.EhecdListview.OnRefreshListener;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.RefreshView;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FriendNewFrg extends Fragment {
	
	private final String              TAG                          = "FriendNewFrg";
//	OnBackListener mListener;

//	public interface OnBackListener {
//		public void backEvent();
//	}
	
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnBackListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
//        }
//    }
	
//	private RefreshView					mRefreshView                = null;
	
	private EhecdListview               mRefreshView                = null;
	private FriendCircleAdapter			mFriendHotAdapter			= null;
	
	private GetFriendHotListLoader 		reqLoader 					= null;
	private LoadFail 					loadFail;
	
	private String 						sn							= "";
	private int 						startPage					= 0, 
										pageSize					= 0;
	
	private RelativeLayout              mHeadRelative               = null;
	
	private ViewPager             		mTabHost                    = null;
	
	public void setHeadRelative (RelativeLayout relative) {
		
		mHeadRelative = relative;
	}
	
	public void setTabHost (ViewPager tabHost) {
		
		mTabHost = tabHost;
	}
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadFail = new LoadFail(getActivity());
		
		loadFail.setOnRetryClickListener(retryListener);
		
		sn = MainApp.getInstance().getCustomer().sn;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View parentView = inflater.inflate(R.layout.friend_frg_hot, container, false);
		
//		mRefreshView    = (RefreshView) parentView.findViewById(R.id.friend_frg_hot_refresh);
		
//		mRefreshView.setHeadRelative(mHeadRelative);
//		mRefreshView.setTabHost(mTabHost);
		
		mRefreshView    = (EhecdListview) parentView.findViewById(R.id.friend_frg_hot_listview);
		
		mRefreshView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshData();
			}
		});
		
		mRefreshView.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub

				loadMoreData();
			}
		});

		mRefreshView.setShowFootBottom(true);
		
		return parentView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		DebugTools.getDebug().debug_v(TAG, ">>>>>>>onViewCreated");

		if(null != mFriendHotAdapter) {
			
			mRefreshView.setAdapter(mFriendHotAdapter);
			//Utils.logh(TAG, "exist myInviteAdapter " + myInviteAdapter);
			
		} else {
			
			DebugTools.getDebug().debug_v(TAG, "onViewCreated----->>>执行了两次");
			initDataAysnc();
//			loadFail.displayFail("加载失败！");
		}
		
		// 注册监听
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY);
		//getActivity().registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onDestroyView() {
		DebugTools.getDebug().debug_v(TAG, "onDestroyView..");
		
		super.onDestroyView();
	}
	
	@Override
	public void onStart() {
		DebugTools.getDebug().debug_v(TAG, "onStart..");
		super.onStart();
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
			GetFriendHotListLoader loader = reqLoader;
			loader.stopTask(true);
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	/*初始化数据请求*/
	private void initDataAysnc() {
		
		DebugTools.getDebug().debug_v(TAG, "initDataAysnc__----执行了两次");
		
		
		if(!Utils.isConnected(getActivity())) {
			return ;
		}
		WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		reqLoader = new GetFriendHotListLoader(getActivity(),sn, startPage,pageSize, "",Config.FRIEND_NEW,
			new GetFriendHotListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList) {
				// TODO Auto-generated method stub
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || hotList.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_friend_no_data);
					}
					// display reload page
					loadFail.displayNoDataRetry(msg);
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					//loadFail.displayNone();
					
					initListView(hotList);

					//mTabHost.invalidate();
					
					//listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else {
					loadFail.displayFail(msg);
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
				mRefreshView.onRefreshComplete();
				WaitDialog.dismissWaitDialog();
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	/*下拉刷新请求数据*/
	private void refreshData() {
		if(!Utils.isConnected(getActivity())) {
			
			mRefreshView.onRefreshComplete();
			return ;
		}
		
		//WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		reqLoader = new GetFriendHotListLoader(getActivity(),sn, 1, startPage*pageSize, "",Config.FRIEND_NEW,
			new GetFriendHotListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList) {
				// TODO Auto-generated method stub
				
				if(BaseRequest.REQ_RET_OK == retId) {
					
					if(mFriendHotAdapter == null) {
						
						initListView(hotList);
						
					} else {
						
						mFriendHotAdapter.refreshListInfo(hotList);
					}

					//mTabHost.invalidate();
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else {
					
					//Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
				mRefreshView.onRefreshComplete();
				//WaitDialog.dismissWaitDialog();
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	/*加载更多数据*/
	private void loadMoreData() {
		
		if(!Utils.isConnected(getActivity())) {
			
			mRefreshView.onRefreshComplete();
			return ;
		}
		//WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();
		
		startPage++;
		
		/*sn 暂时等于1*/
		reqLoader = new GetFriendHotListLoader(getActivity(),sn, startPage, pageSize,"",Config.FRIEND_NEW,
			new GetFriendHotListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList) {
				// TODO Auto-generated method stub
				
				if(BaseRequest.REQ_RET_OK == retId) {
					
					if(mFriendHotAdapter == null) {
						
						initListView(hotList);
						
					} else {
						
						mFriendHotAdapter.appendListInfo(hotList);
					}

					//mTabHost.invalidate();
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
					
				} else {

					//Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
				mRefreshView.onRefreshComplete();
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

		if (getActivity() == null || inviteList == null || inviteList.size() <= 0) {

			return;
		}
		
		mFriendHotAdapter = new FriendCircleAdapter(getActivity(),inviteList,mRefreshView,null,false);
		
		if (Config.mFriendMessageNewItem != null ) {
			
			mFriendHotAdapter.appendFriendHotItem(Config.mFriendMessageNewItem);
			
		}
		
		mRefreshView.setAdapter(mFriendHotAdapter);
		//Utils.logh(TAG, "initListView myInviteAdapter " + myInviteAdapter);
	}
			
}
