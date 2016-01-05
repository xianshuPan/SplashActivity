package com.hylg.igolf.ui.member;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.data.MemMyFightInfo;
import com.hylg.igolf.cs.loader.GetFightsListLoader;
import com.hylg.igolf.cs.loader.GetFightsListLoader.GetFightsListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.ui.friend.FriendCircleAdapter;
import com.hylg.igolf.ui.reqparam.GetFightsReqParam;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.EhecdListview.OnLoadMoreListener;
import com.hylg.igolf.ui.view.EhecdListview.OnRefreshListener;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

public class MemFightFrgNew extends Fragment {
	
	private final String              TAG                          = "MemTipsFrg";
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
	private FightRecordAdapter			mFriendHotAdapter			= null;
	
	private GetFightsListLoader 		reqLoader 					= null;
	private LoadFail 					loadFail;

	private GetFightsReqParam 			reqData;
	
	private String 						sn							= "",
										mem_sn                      = "";
	private int 						startPage					= 0, 
										pageSize					= 0;

	private FragmentActivity mContext = null;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadFail = new LoadFail(getActivity());
		
		loadFail.setOnRetryClickListener(retryListener);
		
		sn = MainApp.getInstance().getCustomer().sn;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;

		mem_sn = getArguments().getString("mem_sn");

		reqData = new GetFightsReqParam();
		reqData.sn = MainApp.getInstance().getCustomer().sn;
		reqData.memSn = mem_sn;
		reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
		reqData.pageSize = MainApp.getInstance().getGlobalData().pageSize;


		mContext = getActivity();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View parentView = inflater.inflate(R.layout.friend_frg_hot, container, false);
		
		mRefreshView    = (EhecdListview) parentView.findViewById(R.id.friend_frg_hot_listview);

		mRefreshView.setDividerHeight(0);
		
		mRefreshView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshData(reqData);
			}
		});
		
		mRefreshView.setOnLoadMoreListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				
				loadMoreData(reqData);
			}
		});
		
		return mRefreshView;
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
			initDataAysnc(reqData);
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
			GetFightsListLoader loader = reqLoader;
			loader.stopTask(true);
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	/*初始化数据请求*/
	private void initDataAysnc(final GetFightsReqParam data) {
		
		DebugTools.getDebug().debug_v(TAG, "initDataAysnc__----执行了两次");
		
		
		if(!Utils.isConnected(getActivity())) {
			return ;
		}
		WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		reqLoader = new GetFightsListLoader(getActivity(),data,
			new GetFightsListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<MemMyFightInfo> fightRecordList) {
				mRefreshView.onRefreshComplete();
				WaitDialog.dismissWaitDialog();
				Utils.logh(TAG, "retId: " + retId + " fightRecordList: " + fightRecordList.size());
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						//fightRecordAdapter.refreshListInfo(fightRecordList);
						loadFail.displayNone();
						initListView(fightRecordList);
						//listFooter.refreshFooterView(fightRecordList.size(), data.pageSize);
						break;
					case BaseRequest.REQ_RET_F_NO_DATA: // do not exists in common

						loadFail.displayNoData(msg);
						break;
					default: // normal fail
						Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
						loadFail.displayNoData(msg);
						break;
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	/*下拉刷新请求数据*/
	private void refreshData(final GetFightsReqParam data) {
		if(!Utils.isConnected(getActivity())) {
			
			mRefreshView.onRefreshComplete();
			return ;
		}
		
		//WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		reqLoader = new GetFightsListLoader(getActivity(),data,
			new GetFightsListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<MemMyFightInfo> fightRecordList) {
				mRefreshView.onRefreshComplete();
				Utils.logh(TAG, "retId: " + retId + " fightRecordList: " + fightRecordList.size());
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						mFriendHotAdapter.refreshListInfo(fightRecordList);
						loadFail.displayNone();
						//listFooter.refreshFooterView(fightRecordList.size(), data.pageSize);
						break;
					case BaseRequest.REQ_RET_F_NO_DATA: // do not exists in common

						loadFail.displayNoData(msg);
						break;
					default: // normal fail
						Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
						loadFail.displayNoData(msg);
						break;
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	/*加载更多数据*/
	private void loadMoreData(final GetFightsReqParam data) {
		
		if(!Utils.isConnected(getActivity())) {
			
			mRefreshView.onRefreshComplete();
			return ;
		}
		//WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();
		
		startPage++;
		
		/*sn 暂时等于1*/
		reqLoader = new GetFightsListLoader(getActivity(),data,
			new GetFightsListCallback() {
			
			@Override
			public void callBack(int retId, String msg, ArrayList<MemMyFightInfo> fightRecordList) {
				mRefreshView.onRefreshComplete();
				Utils.logh(TAG, "retId: " + retId + " fightRecordList: " + fightRecordList.size());
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						mFriendHotAdapter.refreshListInfo(fightRecordList);
						loadFail.displayNone();
						//listFooter.refreshFooterView(fightRecordList.size(), data.pageSize);
						break;
					case BaseRequest.REQ_RET_F_NO_DATA: // do not exists in common

						loadFail.displayNoData(msg);
						break;
					default: // normal fail
						Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

						loadFail.displayNoData(msg);
						break;
				}
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
			initDataAysnc(reqData);
		}
	};
	
	
	private void initListView(ArrayList<MemMyFightInfo> inviteList) {
		
		//mFriendHotAdapter.initListInfo(inviteList);
		
		DebugTools.getDebug().debug_d(TAG, "-----》》》为什么执行了两次");
		
		mFriendHotAdapter = new FightRecordAdapter(inviteList);
		
		mRefreshView.setAdapter(mFriendHotAdapter);
		//Utils.logh(TAG, "initListView myInviteAdapter " + myInviteAdapter);
	}
//
//	private class FightRecordAdapter extends BaseAdapter {
//		private ArrayList<MemMyFightInfo> list;
//
//		public FightRecordAdapter(ArrayList<MemMyFightInfo> list) {
//			if(null == list){
//				this.list = new ArrayList<MemMyFightInfo>();
//			}else{
//				this.list = list;
//			}
//		}
//
//		public void refreshListInfo(ArrayList<MemMyFightInfo> list) {
//			this.list.clear();
//			this.list = list;
//			notifyDataSetChanged();
//		}
//
//		public void appendListInfo(ArrayList<MemMyFightInfo> list) {
//			for(int i=0, size=list.size(); i<size; i++) {
//				this.list.add(list.get(i));
//			}
//			notifyDataSetChanged();
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHodler holder ;
//			if(null == convertView) {
//				convertView = View.inflate(mContext, R.layout.mem_fight_record_item_new, null);
//				holder = new ViewHodler();
//				holder.timeIv = (TextView) convertView.findViewById(R.id.fight_record_li_time);
//				holder.myHCPIIv = (TextView) convertView.findViewById(R.id.fight_record_li_myhcpi);
//				holder.palHCPIIv = (TextView) convertView.findViewById(R.id.fight_record_li_palhcpi);
//				holder.courseIv = (TextView) convertView.findViewById(R.id.fight_record_li_course);
//
//				holder.myAvater = (ImageView)convertView.findViewById(R.id.fight_record_li_my_avatar);
//				holder.myWinner = (ImageView)convertView.findViewById(R.id.fight_record_li_my_winner);
//
//				holder.palAvater = (ImageView)convertView.findViewById(R.id.fight_record_li_pal_avatar);
//				holder.palWinner = (ImageView)convertView.findViewById(R.id.fight_record_li_pal_winner);
//				holder.time_line = (ImageView)convertView.findViewById(R.id.time_line);
//
//
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHodler) convertView.getTag();
//			}
//			MemMyFightInfo data = list.get(position);
//			holder.myHCPIIv.setText(data.myScoreInfo);
//			holder.palHCPIIv.setText( data.palScoreInfo);
//			holder.timeIv.setText(data.teeTime);
//			holder.courseIv.setText(data.courseName);
//
//			//finalBit.display(holder.myAvater,my_avatar_url);
//			//finalBit.display(holder.palAvater,pal_avatar_url);
//
//
//
//			if (!data.myScoreInfo.equalsIgnoreCase("-") && !data.palScoreInfo.equalsIgnoreCase("-")){
//
//				if (Float.valueOf(data.myScoreInfo) > Float.valueOf(data.myScoreInfo)) {
//
//					holder.myWinner.setVisibility(View.VISIBLE);
//					holder.palWinner.setVisibility(View.GONE);
//				} else {
//
//					holder.myWinner.setVisibility(View.GONE);
//					holder.palWinner.setVisibility(View.VISIBLE);
//				}
//
//			} else {
//
//				if (data.myScoreInfo.equalsIgnoreCase("-")) {
//
//					holder.myWinner.setVisibility(View.GONE);
//				}
//
//				if (data.palScoreInfo.equalsIgnoreCase("-")) {
//
//					holder.palWinner.setVisibility(View.GONE);
//				}
//			}
//
//			return convertView;
//		}
//
//		private class ViewHodler {
//			protected TextView courseIv;
//			protected TextView timeIv;
//			protected TextView myHCPIIv;
//			protected TextView palHCPIIv;
//
//			protected ImageView myAvater;
//			protected ImageView myWinner;
//			protected ImageView palAvater;
//			protected ImageView palWinner;
//
//			protected ImageView time_line;
//		}
//
//		@Override
//		public int getCount() {
//			return list.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return list.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//	}

	private class FightRecordAdapter extends BaseAdapter {
		private ArrayList<MemMyFightInfo> list;

		public FightRecordAdapter(ArrayList<MemMyFightInfo> list) {
			if(null == list){
				this.list = new ArrayList<MemMyFightInfo>();
			}else{
				this.list = list;
			}
		}

		public void refreshListInfo(ArrayList<MemMyFightInfo> list) {
			this.list.clear();
			this.list = list;
			notifyDataSetChanged();
		}

		public void appendListInfo(ArrayList<MemMyFightInfo> list) {
			for(int i=0, size=list.size(); i<size; i++) {
				this.list.add(list.get(i));
			}
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHodler holder = null;
			if(null == convertView) {
				convertView = View.inflate(mContext, R.layout.mem_fight_record_item, null);
				holder = new ViewHodler();
				holder.timeIv = (TextView) convertView.findViewById(R.id.fight_record_li_time);
				holder.myHCPIIv = (TextView) convertView.findViewById(R.id.fight_record_li_myhcpi);
				holder.palHCPIIv = (TextView) convertView.findViewById(R.id.fight_record_li_palhcpi);
				holder.courseIv = (TextView) convertView.findViewById(R.id.fight_record_li_course);
				convertView.setTag(holder);
			} else {
				holder = (ViewHodler) convertView.getTag();
			}
			MemMyFightInfo data = list.get(position);
			holder.myHCPIIv.setText(String.format(getResources().getString(R.string.str_member_myhcpi_wrap), data.myScoreInfo));
			holder.palHCPIIv.setText(String.format(getResources().getString(R.string.str_member_palhcpi_wrap), data.palScoreInfo));
			holder.timeIv.setText(data.teeTime);
			holder.courseIv.setText(data.courseName);
			return convertView;
		}

		private class ViewHodler {
			protected TextView courseIv;
			protected TextView timeIv;
			protected TextView myHCPIIv;
			protected TextView palHCPIIv;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}


}
