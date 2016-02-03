package com.hylg.igolf.ui.hall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

public class HallMyInvitesActivity extends FragmentActivity implements onResultCallback,View.OnClickListener {

	private static final String TAG = "HallMyInvitesFrg";
	private GetMyInviteListLoader reqLoader = null;
	private LoadFail loadFail;
	private PullListView listView;
	private ListFooter listFooter;

	/*我的数据适配器*/
	private MyInviteAdapter myInviteAdapter;
	private String sn;
	private int startPage, pageSize;

	FragmentActivity mContext = null;


	public static void startHallMyInvitesActivity(Fragment fragment) {
		Intent intent = new Intent(fragment.getActivity(), HallMyInvitesActivity.class);
		fragment.startActivity(intent);
		fragment.getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	public static void startHallMyInvitesActivity(FragmentActivity fragment) {
		Intent intent = new Intent(fragment, HallMyInvitesActivity.class);
		fragment.startActivity(intent);
		fragment.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.hall_frg_my_invites);

		mContext = this;

		listFooter = new ListFooter(this);
		loadFail = new LoadFail(this);
		listView = (PullListView) findViewById(R.id.my_invites_listview);
		listView.addFooterView(listFooter.getFooterView());

		loadFail.setOnRetryClickListener(retryListener);
		sn = MainApp.getInstance().getCustomer().sn;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;

		findViewById(R.id.my_invites_back).setOnClickListener(this);

		Utils.logh(TAG, "onCreate startPage: " + startPage + " pageSize: " + pageSize);
	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.hall_frg_my_invites, container, false);
//		listView = (PullListView) view.findViewById(R.id.my_invites_listview);
//		listView.addFooterView(listFooter.getFooterView());
//		view.addView(loadFail.getLoadFailView(), 0);
//		return view;
//	}

	@Override
	public void onResume() {
		super.onResume();
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
		mContext.registerReceiver(mReceiver, filter);
	}

//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//		LinearLayout parent = (LinearLayout) loadFail.getLoadFailView().getParent();
//		Utils.logh(TAG, " --- onDestroyViewparent: " + parent);
//		if(null != parent) {
//			parent.removeAllViews();
//		}
//		if(null != reqLoader) {
//			reqLoader.stopTask(true);
//			reqLoader = null;
//		}
//		getActivity().unregisterReceiver(mReceiver);
//	}

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

	@Override
	public void onClick(View view) {

		switch (view.getId()) {

			case R.id.my_invites_back:

				mContext.finish();
				mContext.overridePendingTransition(0,R.anim.ac_slide_right_out);
				break;
		}

	}

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

				//serialize object can not contain Generic
				myInviteInfo.applicants = null;
				InviteDetailMyOpenActivity.startInviteDetailMyOpenForCallback(this, myInviteInfo);
			} else {

				//serialize object can not contain Generic
				myInviteInfo.applicants = null;
				InviteDetailOtherOpenActivity.startInviteDetailOtherOpenForCallback(this, myInviteInfo);
			}

		} else {
			if(Utils.LOG_H) {
				throw new IllegalArgumentException("startInviteDetail type error!");
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
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetMyInviteListLoader(mContext, sn, startPage, pageSize, new GetMyInviteListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<MyInviteInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || inviteList.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_hall_invite_mine_no_hint);
					}
					// display reload page
					loadFail.displayNoDataRetry(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
				} else if(BaseRequest.REQ_RET_OK == retId) {
					loadFail.displayNone();
					initListView(inviteList);
					listFooter.refreshFooterView(inviteList.size(), pageSize);
					MainApp.getInstance().getGlobalData().setHasStartNewInvite(false);
				} else {
					loadFail.displayFail(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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
		if(!Utils.isConnected(mContext)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetMyInviteListLoader(mContext, sn, startPage, pageSize, new GetMyInviteListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<MyInviteInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {
					myInviteAdapter.refreshListData(inviteList);
					listFooter.refreshFooterView(inviteList.size(), pageSize);
				} else if(BaseRequest.REQ_RET_F_NO_DATA == retId) { // do not exists in common
					
				} else {
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	protected void appendListDataAsync(int startPage) {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetMyInviteListLoader(mContext, sn, startPage, pageSize, new GetMyInviteListCallback() {
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
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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
			startPage = startPage + 1;
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

		private GlobalData gd;
		
		public MyInviteAdapter(ArrayList<MyInviteInfo> list) {
			this.list = list;
			clickPos = -1;

			gd = MainApp.getInstance().getGlobalData();
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

			if (list != null && list.size() > 0 && clickPos < list.size()) {

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

		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(mContext, R.layout.invite_list_open_item_new, null);
				holder = new ViewHolder();
				//holder.alert = (ImageView) convertView.findViewById(R.id.invite_list_mime_item_alert);
				holder.avatar = (ImageView) convertView.findViewById(R.id.invite_list_open_item_avatar);
				holder.nickname = (TextView) convertView.findViewById(R.id.invite_list_open_item_nickname);
				holder.teetime = (TextView) convertView.findViewById(R.id.invite_list_open_item_teetime);
				holder.course = (TextView) convertView.findViewById(R.id.invite_list_open_item_course);
				holder.state = (TextView) convertView.findViewById(R.id.invite_list_open_item_state);
				holder.sex = (ImageView) convertView.findViewById(R.id.invite_list_open_item_sex);
				holder.state = (TextView) convertView.findViewById(R.id.invite_list_open_item_state);

				holder.paytype = (TextView) convertView.findViewById(R.id.invite_list_open_item_paytye);
				holder.paytype1 = (TextView) convertView.findViewById(R.id.invite_list_open_item_paytye1);
				holder.fans = (TextView) convertView.findViewById(R.id.invite_list_open_item_fans);
				holder.fans_linear = (LinearLayout) convertView.findViewById(R.id.invite_list_open_item_fans_linear);
				holder.invitee_one = (ImageView) convertView.findViewById(R.id.invite_list_open_item_invitee_one);
				holder.invitee_two = (ImageView) convertView.findViewById(R.id.invite_list_open_item_invitee_two);
				holder.invitee_three = (ImageView) convertView.findViewById(R.id.invite_list_open_item_invitee_three);
				holder.state_divider = convertView.findViewById(R.id.invite_list_open_item_state_divider);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			MyInviteInfo data = list.get(position);
//			if(data.haveAlert) {
//				Utils.setVisible(holder.alert);
//			} else {
//				Utils.setGone(holder.alert);
//			}

			if (data.type == 1) {
				//open
				holder.fans_linear.setVisibility(View.VISIBLE);
				holder.paytype1.setVisibility(View.GONE);
				holder.paytype.setVisibility(View.VISIBLE);
				holder.invitee_two.setVisibility(View.VISIBLE);
				holder.invitee_three.setVisibility(View.VISIBLE);

				holder.fans.setText(String.valueOf(data.sameProvencePerson));


				if (data.applicants != null && data.applicants.size()> 0) {


					if (data.applicants != null && data.applicants.size() > 0) {

						//finalBit.display(holder.invitee_one,Utils.getAvatarURLString(sd[0]));
						String avatarUrl = Utils.getAvatarURLString(data.applicants.get(0).applicant_sn);
						//DownLoadImageTool.getInstance(mContext).displayImage(avatarUrl,holder.invitee_one,null);
						loadAvatar(mContext,data.applicants.get(0).applicant_sn,holder.invitee_one);
					}
					else {

						holder.invitee_one.setImageResource(R.drawable.avatar_no_golfer);
						holder.invitee_two.setImageResource(R.drawable.avatar_no_golfer);
						holder.invitee_three.setImageResource(R.drawable.avatar_no_golfer);
					}

					if (data.applicants != null && data.applicants.size() > 1) {

						//finalBit.display(holder.invitee_two,Utils.getAvatarURLString(sd[1]));
						String avatarUrl = Utils.getAvatarURLString(data.applicants.get(1).applicant_sn);
						//DownLoadImageTool.getInstance(mContext).displayImage(avatarUrl, holder.invitee_two, null);
						loadAvatar(mContext,data.applicants.get(1).applicant_sn,holder.invitee_two);
					}
					else {

						holder.invitee_two.setImageResource(R.drawable.avatar_no_golfer);
						holder.invitee_three.setImageResource(R.drawable.avatar_no_golfer);
					}

					if (data.applicants != null && data.applicants.size() > 2) {

						//finalBit.display(holder.invitee_three,Utils.getAvatarURLString(sd[2]));
						String avatarUrl = Utils.getAvatarURLString(data.applicants.get(2).applicant_sn);
						//DownLoadImageTool.getInstance(mContext).displayImage(avatarUrl, holder.invitee_three, null);
						loadAvatar(mContext,data.applicants.get(2).applicant_sn,holder.invitee_three);
					}
					else {

						holder.invitee_three.setImageResource(R.drawable.avatar_no_golfer);
					}

				}
				else {

					holder.invitee_one.setImageResource(R.drawable.avatar_no_golfer);
					holder.invitee_two.setImageResource(R.drawable.avatar_no_golfer);
					holder.invitee_three.setImageResource(R.drawable.avatar_no_golfer);
				}


			}
			else {
				// sts
				holder.fans_linear.setVisibility(View.GONE);
				holder.paytype1.setVisibility(View.VISIBLE);
				holder.paytype.setVisibility(View.GONE);
				holder.invitee_two.setVisibility(View.GONE);
				holder.invitee_three.setVisibility(View.GONE);

				holder.paytype1.setText(gd.getPayTypeName(data.payType));

				String avatarUrl = Utils.getAvatarURLString(data.palSn);
				//DownLoadImageTool.getInstance(mContext).displayImage(avatarUrl, holder.invitee_one, null);
				loadAvatar(mContext,data.palSn,holder.invitee_one);
			}

			loadAvatar(mContext, data.inviterSn, data.inviterSn+".jpg", holder.avatar);

			holder.nickname.setText(data.inviterName);
			if(Const.SEX_MALE == data.inviterSex) {
				holder.sex.setImageResource(R.drawable.ic_male);
			} else {
				holder.sex.setImageResource(R.drawable.ic_female);
			}
			holder.teetime.setText(data.teeTime);
			holder.course.setText(data.courseName);
			holder.state.setText(data.palMsg);
			holder.paytype.setText(gd.getPayTypeName(data.payType));


			switch(data.displayStatus) {
				case Const.MY_INVITE_WAITAPPLY:
				case Const.MY_INVITE_APPlYED:
				case Const.MY_INVITE_ACCEPTED:
					holder.state.setTextColor(getResources().getColor(R.color.color_tab_green));
					holder.state_divider.setBackgroundColor(getResources().getColor(R.color.color_tab_green));
					break;
				case Const.MY_INVITE_WAITACCEPT:
					// 待接受：我发起的是绿颜色，他人发起的是黄颜色
					if(data.inviterSn.equals(MainApp.getInstance().getCustomer().sn)) {
						holder.state.setTextColor(getResources().getColor(R.color.color_tab_green));
						holder.state_divider.setBackgroundColor(getResources().getColor(R.color.color_tab_green));
						break;
					}
				case Const.MY_INVITE_HAVEAPPLY:
				case Const.MY_INVITE_WAITSIGN:
				case Const.MY_INVITE_PLAYING:
				case Const.MY_INVITE_SIGNED:
					holder.state.setTextColor(getResources().getColor(R.color.color_yellow));
					holder.state_divider.setBackgroundColor(getResources().getColor(R.color.color_yellow));
					break;
				case Const.MY_INVITE_CANCELED:
				case Const.MY_INVITE_REVOKED:
				case Const.MY_INVITE_REFUSED:
					holder.state.setTextColor(getResources().getColor(R.color.color_red));
					holder.state_divider.setBackgroundColor(getResources().getColor(R.color.color_red));
					break;
				case Const.MY_INVITE_COMPLETE:
					holder.state.setTextColor(getResources().getColor(R.color.color_blue));
					holder.state_divider.setBackgroundColor(getResources().getColor(R.color.color_blue));
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
			private ImageView avatar;
			private TextView nickname;
			private TextView teetime;
			private TextView course;
			private TextView paytype;
			private TextView paytype1;
			private LinearLayout fans_linear;
			private TextView fans;

			private ImageView sex;
			private ImageView invitee_one;
			private ImageView invitee_two;
			private ImageView invitee_three;
			private TextView state;
			private View state_divider;
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
