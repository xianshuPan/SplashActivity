package com.hylg.igolf.ui.hall;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.gl.lib.view.RoundedImageView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.MyInviteInfo;
import com.hylg.igolf.cs.loader.GetMyInviteListLoader;
import com.hylg.igolf.cs.loader.GetMyInviteListLoader.GetMyInviteListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.hall.InviteDetailActivity.onResultCallback;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.view.PullListView;
import com.hylg.igolf.ui.view.PullListView.OnLoadMoreListener;
import com.hylg.igolf.ui.view.PullListView.OnRefreshListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class HallMyInvitesFrg extends Fragment implements onResultCallback {
	
	private static final String TAG = "HallMyInvitesFrg";
	private GetMyInviteListLoader reqLoader = null;
	private LoadFail loadFail;
	private PullListView listView;
	private ListFooter listFooter;
	
	/*我的数据适配器*/
	private MyInviteAdapter myInviteAdapter;
	private String sn;
	private int startPage, pageSize;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listFooter = new ListFooter(getActivity());
		loadFail = new LoadFail(getActivity());
		loadFail.setOnRetryClickListener(retryListener);
		sn = MainApp.getInstance().getCustomer().sn;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
		Utils.logh(TAG, "onCreate startPage: " + startPage + " pageSize: " + pageSize);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.hall_frg_my_invites, container, false);
		listView = (PullListView) view.findViewById(R.id.my_invites_listview);
		listView.addFooterView(listFooter.getFooterView());
		view.addView(loadFail.getLoadFailView(), 0);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listView.setonRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);
		if(null != myInviteAdapter && !MainApp.getInstance().getGlobalData().hasStartNewInvite()) {
			listView.setAdapter(myInviteAdapter);
			Utils.logh(TAG, "exist myInviteAdapter " + myInviteAdapter);
		} else {
			initDataAysnc();
//			loadFail.displayFail("加载失败！");
		}
		// 注册监听
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY);
		getActivity().registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		LinearLayout parent = (LinearLayout) loadFail.getLoadFailView().getParent();
		Utils.logh(TAG, " --- onDestroyViewparent: " + parent);
		if(null != parent) {
			parent.removeAllViews();
		}
		if(null != reqLoader) {
			reqLoader.stopTask(true);
			reqLoader = null;
		}
		getActivity().unregisterReceiver(mReceiver);
	}

//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		// 注册监听
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY);
//		activity.registerReceiver(mReceiver, filter);
//	}
//
//	@Override
//	public void onDetach() {
//		getActivity().unregisterReceiver(mReceiver);
//		super.onDetach();
//	}

	private Receiver mReceiver = new Receiver();
	private class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY.equals(action)) {
				Utils.logh(TAG, "++++++++++++++++++++++++");
				resetList();
			}
		}
	}
	
//	private MyInviteInfo curInfo;
	private void startInviteDetail(MyInviteInfo myInviteInfo) {
//		curInfo = myInviteInfo;
		String inviterSn = myInviteInfo.inviterSn;
		if(Const.INVITE_TYPE_STS == myInviteInfo.type) {
			if(sn.equalsIgnoreCase(inviterSn)) {
				InviteDetailMyStsActivity.startInviteDetailMyStsForCallback(this, myInviteInfo);
			} else {
				InviteDetailOtherStsActivity.startInviteDetailOtherStsForCallback(this, myInviteInfo);
			}
		} else if(Const.INVITE_TYPE_OPEN == myInviteInfo.type) {
			
			if(sn.equalsIgnoreCase(inviterSn)) {
				InviteDetailMyOpenActivity.startInviteDetailMyOpenForCallback(this, myInviteInfo);
			} else {
				InviteDetailOtherOpenActivity.startInviteDetailOtherOpenForCallback(this, myInviteInfo);
			}
			
		} else {
			if(Utils.LOG_H) {
				throw new java.lang.IllegalArgumentException("startInviteDetail type error!");
			}
		}
	}

	@Override
	public void callback(boolean status, boolean alert) {
		// 操作状态发生变化，直接更新列表，无需处理通知
		if(status) {
			// 目前处理都一致。若后续处理不一致，通过后面方式，分开处理
			resetList();
//			String inviterSn = curInfo.inviterSn;
//			if(Const.INVITE_TYPE_STS == curInfo.type) {
//				if(sn.equalsIgnoreCase(inviterSn)) {
//					// 状态变化了，目前重刷列表
//					Utils.logh(TAG, "status changed ... my sts  ");
//				} else {
//					// 状态变化了，目前重刷列表
//					Utils.logh(TAG, "status changed ... other sts  ");
//				}
//			} else if(Const.INVITE_TYPE_OPEN == curInfo.type) {
//				if(sn.equalsIgnoreCase(inviterSn)) {
//					// 状态变化了，目前重刷列表
//					Utils.logh(TAG, "status changed ... my open  ");
//				} else {
//					// 状态变化了，目前重刷列表
//					Utils.logh(TAG, "status changed ... other open  ");
//				}
//			}
			return ;
		}
		// 无操作状态变化，只要新通知状态更新
		if(alert) {
			myInviteAdapter.changeAlert();
		}
	}
	
	//
	private void resetList() {
		
		myInviteAdapter = null;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		initDataAysnc();
	}
	
//	结果被HomeFragment截获，无法返回到当前，用上面的callback实现
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//		Utils.logh(TAG, "resultCode: " + resultCode);
//		if(Activity.RESULT_OK == resultCode && null != intent) {
//			switch(requestCode) {
//				// 目前处理都一致，避免后续不同，分开处理
//				case Const.REQUST_CODE_INVITE_DETAIL_OTHER_OPEN: {
//					if(!intent.getExtras().getBoolean(InviteDetailOtherOpenActivity.BUNDLE_KEY_STATUS_CHANGED)) {
//						Log.w(TAG, "REQUST_CODE_INVITE_DETAIL_OTHER_OPEN false should be true !!!");
//					} else {
//						// 状态变化了，目前重刷列表
//						Utils.logh(TAG, "REQUST_CODE_INVITE_DETAIL_OTHER_OPEN status changed ... ");
//						myInviteAdapter = null;
//						startPage = MainApp.getInstance().getGlobalData().startPage;
//						initDataAysnc();
//					}
//					return ;
//				}
//				case Const.REQUST_CODE_INVITE_DETAIL_OTHER_STS: {
//					if(!intent.getExtras().getBoolean(InviteDetailOtherStsActivity.BUNDLE_KEY_STATUS_CHANGED)) {
//						Log.w(TAG, "REQUST_CODE_INVITE_DETAIL_OTHER_STS false should be true !!!");
//					} else {
//						// 状态变化了，目前重刷列表
//						Utils.logh(TAG, "REQUST_CODE_INVITE_DETAIL_OTHER_STS status changed ... ");
//						myInviteAdapter = null;
//						startPage = MainApp.getInstance().getGlobalData().startPage;
//						initDataAysnc();
//					}
//					return ;
//				}
//				case Const.REQUST_CODE_INVITE_DETAIL_MY_OPEN: {
//					if(!intent.getExtras().getBoolean(InviteDetailMyOpenActivity.BUNDLE_KEY_STATUS_CHANGED)) {
//						Log.w(TAG, "REQUST_CODE_INVITE_DETAIL_MY_OPEN false should be true !!!");
//					} else {
//						// 状态变化了，目前重刷列表
//						Utils.logh(TAG, "REQUST_CODE_INVITE_DETAIL_MY_OPEN status changed ... ");
//						myInviteAdapter = null;
//						startPage = MainApp.getInstance().getGlobalData().startPage;
//						initDataAysnc();
//					}
//					return ;
//				}
//				case Const.REQUST_CODE_INVITE_DETAIL_MY_STS: {
//					if(!intent.getExtras().getBoolean(InviteDetailMyStsActivity.BUNDLE_KEY_STATUS_CHANGED)) {
//						Log.w(TAG, "REQUST_CODE_INVITE_DETAIL_MY_STS false should be true !!!");
//					} else {
//						// 状态变化了，目前重刷列表
//						Utils.logh(TAG, "REQUST_CODE_INVITE_DETAIL_MY_STS status changed ... ");
//						myInviteAdapter = null;
//						startPage = MainApp.getInstance().getGlobalData().startPage;
//						initDataAysnc();
//					}
//					return ;
//				}
//			}
//		}
//		super.onActivityResult(requestCode, resultCode, intent);
//	}
	
	private void initDataAysnc() {
		if(!Utils.isConnected(getActivity())) {
			return ;
		}
		WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetMyInviteListLoader(getActivity(), sn, startPage, pageSize, new GetMyInviteListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<MyInviteInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || inviteList.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_hall_invite_mine_no_hint);
					}
					// display reload page
					loadFail.displayNoDataRetry(msg);
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
				} else if(BaseRequest.REQ_RET_OK == retId) {
					loadFail.displayNone();
					initListView(inviteList);
					listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
				} else {
					loadFail.displayFail(msg);
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	private void initListView(ArrayList<MyInviteInfo> inviteList) {
		myInviteAdapter = new MyInviteAdapter(inviteList);
		listView.setAdapter(myInviteAdapter);
		Utils.logh(TAG, "initListView myInviteAdapter " + myInviteAdapter);
	}

	private void refreshDataAysnc(int startPage) {
		if(!Utils.isConnected(getActivity())) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetMyInviteListLoader(getActivity(), sn, startPage, pageSize, new GetMyInviteListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<MyInviteInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {
					myInviteAdapter.refreshListData(inviteList);
					listFooter.refreshFooterView(inviteList.size(), pageSize);
				} else if(BaseRequest.REQ_RET_F_NO_DATA == retId) { // do not exists in common
					
				} else {
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	protected void appendListDataAsync(int startPage) {
		if(!Utils.isConnected(getActivity())) {
			return ;
		}
		listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetMyInviteListLoader(getActivity(), sn, startPage, pageSize, new GetMyInviteListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<MyInviteInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || inviteList.size() == 0) {
					listFooter.displayLast();
				} else if(BaseRequest.REQ_RET_OK == retId) {
					myInviteAdapter.appendListData(inviteList);
					listFooter.refreshFooterView(inviteList.size(), pageSize);
				} else {
					listFooter.displayMore();
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private onRetryClickListener retryListener = new onRetryClickListener() {
		@Override
		public void onRetryClick() {
			Utils.logh(TAG, "onRetryClick ... ");
			myInviteAdapter = null;
			initDataAysnc();
		}
	};
	
	private OnRefreshListener pullRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			startPage = MainApp.getInstance().getGlobalData().startPage;
			refreshDataAysnc(startPage);
		}
	};
	
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
		@Override
		public void onLoadMore() {
			if(listFooter.getStatus() == ListFooter.STATUS_LAST) {
				Utils.logh(TAG, "last");
				return ;
			}
			if(listFooter.getStatus() == ListFooter.STATUS_LOADING || isLoading()) {
				Utils.logh(TAG, "loading");
				return ;
			}
			startPage = myInviteAdapter.getCount() / pageSize + 1;
			appendListDataAsync(startPage);
		}
	};
	private void clearLoader() {
		if(isLoading()) {
			GetMyInviteListLoader loader = reqLoader;
			loader.stopTask(true);
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	
	private class MyInviteAdapter extends IgBaseAdapter {
		private ArrayList<MyInviteInfo> list;
		private int clickPos;
		
		public MyInviteAdapter(ArrayList<MyInviteInfo> list) {
			this.list = list;
			clickPos = -1;
		}
		
		public void appendListData(ArrayList<MyInviteInfo> list) {
			for(MyInviteInfo data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}
		
		public void refreshListData(ArrayList<MyInviteInfo> list) {
			this.list.clear();
			for(MyInviteInfo data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}
		
		public void changeAlert() {
			MyInviteInfo info = list.get(clickPos);
			switch(info.displayStatus) {
				// 直接新列表，下列类型信息，会转移到约球历史中。
				case Const.MY_INVITE_CANCELED:
				case Const.MY_INVITE_REVOKED:
				case Const.MY_INVITE_REFUSED:
				case Const.MY_INVITE_COMPLETE:
					resetList();
					break;
				// 更新提示状态即可
				default:
					if(info.haveAlert) {
						info.haveAlert = false;
						notifyDataSetChanged();
					}
					break;
			}
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(getActivity(), R.layout.invite_list_mime_item, null);
				holder = new ViewHolder();
				holder.alert = (ImageView) convertView.findViewById(R.id.invite_list_mime_item_alert);
				holder.avatar = (ImageView) convertView.findViewById(R.id.invite_list_mime_item_avatar);
				holder.nickname = (TextView) convertView.findViewById(R.id.invite_list_mime_item_nickname);
				holder.teetime = (TextView) convertView.findViewById(R.id.invite_list_mime_item_teetime);
				holder.course = (TextView) convertView.findViewById(R.id.invite_list_mime_item_course);
				holder.description = (TextView) convertView.findViewById(R.id.invite_list_mime_item_desc);
				holder.sex = (ImageView) convertView.findViewById(R.id.invite_list_mime_item_sex);
				holder.state = (TextView) convertView.findViewById(R.id.invite_list_mime_item_state);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MyInviteInfo data = list.get(position);
			if(data.haveAlert) {
				Utils.setVisible(holder.alert);
			} else {
				Utils.setGone(holder.alert);
			}
			loadAvatar(getActivity(), data.palSn, data.palAvatar, holder.avatar, true,
					(int) getResources().getDimension(R.dimen.avatar_detail_size));
			holder.nickname.setText(data.palNickname);
			if(Const.SEX_MALE == data.palSex) {
				holder.sex.setImageResource(R.drawable.ic_male);
			} else {
				holder.sex.setImageResource(R.drawable.ic_female);
			}
			holder.teetime.setText(data.teeTime);
			holder.course.setText(data.courseName);
			holder.description.setText(data.palMsg);
			switch(data.displayStatus) {
				case Const.MY_INVITE_WAITAPPLY:
				case Const.MY_INVITE_APPlYED:
				case Const.MY_INVITE_ACCEPTED:
					holder.state.setBackgroundResource(R.drawable.invite_list_status_green_bkg);
					break;
				case Const.MY_INVITE_WAITACCEPT:
					// 待接受：我发起的是绿颜色，他人发起的是黄颜色
					if(data.inviterSn.equals(MainApp.getInstance().getCustomer().sn)) {
						holder.state.setBackgroundResource(R.drawable.invite_list_status_green_bkg);
						break;
					}
				case Const.MY_INVITE_HAVEAPPLY:
				case Const.MY_INVITE_WAITSIGN:
				case Const.MY_INVITE_PLAYING:
				case Const.MY_INVITE_SIGNED:
					holder.state.setBackgroundResource(R.drawable.invite_list_status_yellow_bkg);
					break;
				case Const.MY_INVITE_CANCELED:
				case Const.MY_INVITE_REVOKED:
				case Const.MY_INVITE_REFUSED:
					holder.state.setBackgroundResource(R.drawable.invite_list_status_red_bkg);
					break;
				case Const.MY_INVITE_COMPLETE:
					holder.state.setBackgroundResource(R.drawable.invite_list_status_blue_bkg);
					break;
			}
			holder.state.setText(data.displayStatusStr);
			convertView.setOnClickListener(new onInviteItemClickListner(position));
			return convertView;
		}
		
		private class onInviteItemClickListner implements View.OnClickListener {
			private int position;
			public onInviteItemClickListner(int position) {
				this.position = position;
			}

			@Override
			public void onClick(View v) {
				clickPos = position;
				startInviteDetail(list.get(position));
			}
		}
		
		class ViewHolder {
			private ImageView alert;
			private ImageView avatar;
			private TextView nickname;
			private TextView teetime;
			private TextView course;
			private TextView description;
			private ImageView sex;
			private TextView state;
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

	}


}
