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
import com.hylg.igolf.cs.data.CoachInviteOrderDetail;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.loader.GetMyTeachingListLoader;
import com.hylg.igolf.cs.loader.GetMyTeachingListLoader.GetMyTeachingListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.coach.CoachInviteOrderDetailActivity;
import com.hylg.igolf.ui.hall.InviteDetailActivity.onResultCallback;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.EhecdListview.OnLoadMoreListener;
import com.hylg.igolf.ui.view.EhecdListview.OnRefreshListener;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class HallMyTeachingFrg extends Fragment implements onResultCallback {
	
	private static final String 					TAG = "HallMyTeachingFrg";
	
	private GetMyTeachingListLoader 				reqLoader = null;
	private LoadFail 								loadFail;
	private EhecdListview 							listView;
	
	/*我的数据适配器*/
	private MyTeachingAdapter 						myTeachingAdapter;
	private int 									startPage, pageSize;
	
	private Customer                                customer = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadFail = new LoadFail(getActivity());
		loadFail.setOnRetryClickListener(retryListener);
		customer = MainApp.getInstance().getCustomer();
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
		Utils.logh(TAG, "onCreate startPage: " + startPage + " pageSize: " + pageSize);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.hall_frg_my_teaching, container, false);
		listView = (EhecdListview) view.findViewById(R.id.hall_my_teaching_listview);

		view.addView(loadFail.getLoadFailView(), 0);
		return view;
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listView.setOnRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);
		
//		if(null != myTeachingAdapter && !MainApp.getInstance().getGlobalData().hasStartNewInvite()) {
		if(null != myTeachingAdapter ) {
			listView.setAdapter(myTeachingAdapter);
			Utils.logh(TAG, "exist myTeachingAdapter " + myTeachingAdapter);
		} else {
			//initDataAysnc();
//			loadFail.displayFail("加载失败！");
		}
		// 注册监听
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY);
		getActivity().registerReceiver(mReceiver, filter);
	}
	
	
	@Override
	public void onResume() {
	
		//initDataAysnc();

		startPage = MainApp.getInstance().getGlobalData().startPage;
		refreshDataAysnc(startPage);
		super.onResume();
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
	};
	

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
			myTeachingAdapter.changeAlert();
		}
	}
	
	//
	private void resetList() {
		
		myTeachingAdapter = null;
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
//						myTeachingAdapter = null;
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
//						myTeachingAdapter = null;
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
//						myTeachingAdapter = null;
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
//						myTeachingAdapter = null;
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
		
		startPage = 1;
		reqLoader = new GetMyTeachingListLoader(getActivity(), customer.id, startPage, pageSize, new GetMyTeachingListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<CoachInviteOrderDetail> inviteList) {

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

					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
				} else {
					loadFail.displayFail(msg);
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
				listView.onRefreshComplete();
				WaitDialog.dismissWaitDialog();
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	private void initListView(ArrayList<CoachInviteOrderDetail> inviteList) {
		myTeachingAdapter = new MyTeachingAdapter(inviteList);
		listView.setAdapter(myTeachingAdapter);
		Utils.logh(TAG, "initListView myTeachingAdapter " + myTeachingAdapter);
	}

	private void refreshDataAysnc(int startPage) {
		if(!Utils.isConnected(getActivity())) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetMyTeachingListLoader(getActivity(), customer.id, startPage, pageSize, new GetMyTeachingListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<CoachInviteOrderDetail> inviteList) {

				if(BaseRequest.REQ_RET_OK == retId) {


					if(myTeachingAdapter == null) {

						initListView(inviteList);

					} else {

						myTeachingAdapter.refreshListData(inviteList);
					}
	
				} else if(BaseRequest.REQ_RET_F_NO_DATA == retId) { // do not exists in common
					
				} else {
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
				listView.onRefreshComplete();
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	protected void appendListDataAsync(int startPage) {
		if(!Utils.isConnected(getActivity())) {
			return ;
		}

		clearLoader();
		reqLoader = new GetMyTeachingListLoader(getActivity(), customer.id, startPage, pageSize, new GetMyTeachingListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<CoachInviteOrderDetail> inviteList) {

				if(BaseRequest.REQ_RET_F_NO_DATA == retId || inviteList.size() == 0) {

				} else if(BaseRequest.REQ_RET_OK == retId) {
					myTeachingAdapter.appendListData(inviteList);

				} else {

					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
				listView.onRefreshComplete();
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private onRetryClickListener retryListener = new onRetryClickListener() {
		@Override
		public void onRetryClick() {
			Utils.logh(TAG, "onRetryClick ... ");
			myTeachingAdapter = null;
			initDataAysnc();
		}
	};
	
	private OnRefreshListener pullRefreshListener = new EhecdListview.OnRefreshListener() {
		
		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			startPage = MainApp.getInstance().getGlobalData().startPage;
			refreshDataAysnc(startPage);
		}
	};
	
	private OnLoadMoreListener mOnLoadMoreListener = new EhecdListview.OnLoadMoreListener() {
		
		@Override
		public void onLoadMore() {
			// TODO Auto-generated method stub
			startPage++;
			appendListDataAsync(startPage);
		}
	};
	
	private void clearLoader() {
		if(isLoading()) {
			GetMyTeachingListLoader loader = reqLoader;
			loader.stopTask(true);
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	
	private class MyTeachingAdapter extends IgBaseAdapter {
		private ArrayList<CoachInviteOrderDetail> list;
		private int clickPos;
		
		public MyTeachingAdapter(ArrayList<CoachInviteOrderDetail> list) {
			this.list = list;
			clickPos = -1;
		}
		
		public void appendListData(ArrayList<CoachInviteOrderDetail> list) {
			for(CoachInviteOrderDetail data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}
		
		public void refreshListData(ArrayList<CoachInviteOrderDetail> list) {
			this.list.clear();
			for(CoachInviteOrderDetail data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}
		
		public void changeAlert() {
//			CoachInviteOrderDetail info = list.get(clickPos);
//			
//			switch(info.displayStatus) {
//				// 直接新列表，下列类型信息，会转移到约球历史中。
//				case Const.MY_INVITE_CANCELED:
//				case Const.MY_INVITE_REVOKED:
//				case Const.MY_INVITE_REFUSED:
//				case Const.MY_INVITE_COMPLETE:
//					resetList();
//					break;
//				// 更新提示状态即可
//				default:
//					if(info.haveAlert) {
//						info.haveAlert = false;
//						notifyDataSetChanged();
//					}
//					break;
//			}
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
			CoachInviteOrderDetail data = list.get(position);
			
//			if(data.haveAlert) {
//				Utils.setVisible(holder.alert);
//			} else {
//				Utils.setGone(holder.alert);
//			}
			
			/*判断自己是不是教练*/

			if (data.teacher_sn.equalsIgnoreCase(customer.sn)) {
				
				loadAvatar(getActivity(), data.student_sn, data.student_avatar, holder.avatar, true,(int) getResources().getDimension(R.dimen.avatar_detail_size));
				holder.nickname.setText(data.student_name);
				
				holder.description.setText("您被学员邀请");
				
				if(Const.SEX_MALE == data.student_sex) {
					holder.sex.setImageResource(R.drawable.ic_male);
				} else {
					holder.sex.setImageResource(R.drawable.ic_female);
				}
			} else {
				
				loadAvatar(getActivity(), data.teacher_sn, data.teacher_avatar, holder.avatar, true,(int) getResources().getDimension(R.dimen.avatar_detail_size));
				holder.nickname.setText(data.teacher_name);
				holder.description.setText("您邀请的教练");
				if(Const.SEX_MALE == data.teacher_sex) {
					holder.sex.setImageResource(R.drawable.ic_male);
				} else {
					holder.sex.setImageResource(R.drawable.ic_female);
				}
			}

			holder.teetime.setText(data.coachDate+" "+data.coachTime);
			holder.course.setText(data.course_abbr);
			
			switch(data.status) {
				case Const.MY_TEACHING_WAITAPPLY:

					holder.state.setBackgroundResource(R.drawable.invite_list_status_green_bkg);
					break;
				case Const.MY_TEACHING_ACCEPTED:
//				case Const.MY_TEACHING_START:
//				case Const.MY_TEACHING_END:
					holder.state.setBackgroundResource(R.drawable.invite_list_status_yellow_bkg);
					break;

				case Const.MY_TEACHING_REVOKE:
				case Const.MY_TEACHING_REFUSE:
				case Const.MY_TEACHING_CANCEL:
					holder.state.setBackgroundResource(R.drawable.invite_list_status_red_bkg);
					break;
				case Const.MY_TEACHING_FINISHED:
					holder.state.setBackgroundResource(R.drawable.invite_list_status_blue_bkg);
					break;
			}
			holder.state.setText(data.status_str);
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
				
				//startInviteDetail(list.get(position));
				CoachInviteOrderDetailActivity.startCoachInviteOrderDetailActivity(getActivity(), list.get(position));
				
				getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
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
