package com.hylg.igolf.ui.hall;

import java.util.ArrayList;

import cn.gl.lib.view.RoundedImageView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.OpenInvitationInfo;
import com.hylg.igolf.cs.loader.GetOpenInviteListLoader;
import com.hylg.igolf.cs.loader.GetOpenInviteListLoader.GetOpenInviteListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.hall.StartInviteOpenActivity.onStartRefreshListener;
import com.hylg.igolf.ui.reqparam.GetOpenInviteReqParam;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.PullListView;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.view.PullListView.OnLoadMoreListener;
import com.hylg.igolf.ui.view.PullListView.OnRefreshListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

public class OpenInviteListActivity extends Activity implements OnClickListener, onStartRefreshListener {
	private static final String TAG = "OpenInviteListActivity";
	private static final String BUNDLE_KEY_OPEN_LIST_PARAM = "open_list_param";
	private GetOpenInviteReqParam reqData;
	private TextView hintTv;
	private PullListView listView;
	private OpenAdapter openAdapter;
	private GetOpenInviteListLoader reqLoader = null;
	private ListFooter listFooter;
	private LoadFail loadFail;

	public static void startOpenInvite(Context context, GetOpenInviteReqParam reqParam) {
		Intent intent = new Intent(context, OpenInviteListActivity.class);
		intent.putExtra(BUNDLE_KEY_OPEN_LIST_PARAM, reqParam);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hall_ac_open_invite_list);
		getViews();
		setData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(null != reqLoader) {
			reqLoader.stopTask(true);
			reqLoader = null;
		}
		
	}
	
	private void getViews() {
		findViewById(R.id.hall_open_list_topbar_back).setOnClickListener(this);
		findViewById(R.id.hall_open_list_topbar_start_invite).setOnClickListener(this);
		hintTv = (TextView) findViewById(R.id.hall_open_list_hint);
		loadFail = new LoadFail(this, (RelativeLayout)findViewById(R.id.hall_open_load_fail));
		loadFail.setOnRetryClickListener(retryListener);
		listView = (PullListView) findViewById(R.id.hall_open_listview);
		listFooter = new ListFooter(this);
		listView.addFooterView(listFooter.getFooterView());
		listView.setonRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);
		
	}

	private void setData() {
		reqData = (GetOpenInviteReqParam) getIntent().getSerializableExtra(BUNDLE_KEY_OPEN_LIST_PARAM);
		Utils.logh(TAG, reqData.log());
		initListDataAsync(reqData);
	}
	
	private void refreshResultHintTv(int num) {
		if(num > 0) {
			hintTv.setText(String.format(
					getString(R.string.str_hall_open_list_hint), num));
		} else {
			hintTv.setText(getString(R.string.str_hall_open_list_no_hint));
		}
		
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetOpenInviteListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	private onRetryClickListener retryListener = new onRetryClickListener() {
		@Override
		public void onRetryClick() {
			Utils.logh(TAG, "onRetryClick ... ");
			openAdapter = null;
			reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
			initListDataAsync(reqData);
		}
	};
	
	private void initListDataAsync(final GetOpenInviteReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetOpenInviteListLoader(OpenInviteListActivity.this, data, new GetOpenInviteListCallback() {
			@Override
			public void callBack(int retId, int retNum, String msg,
					ArrayList<OpenInvitationInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || inviteList.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_hall_open_list_no_hint);
					}
					// display reload page
					loadFail.displayNoDataRetry(msg);
					refreshResultHintTv(retNum);
					Toast.makeText(OpenInviteListActivity.this, msg, Toast.LENGTH_SHORT).show();
				} else if(BaseRequest.REQ_RET_OK == retId) {
					loadFail.displayNone();
					initListView(inviteList);
					listFooter.refreshFooterView(inviteList.size(), data.pageSize);
					refreshResultHintTv(retNum);
				} else {
					loadFail.displayFail(msg);
					refreshResultHintTv(retNum);
					Toast.makeText(OpenInviteListActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
				WaitDialog.dismissWaitDialog();
			}
		});
		reqLoader.requestData();
	}
	
	private void initListView(ArrayList<OpenInvitationInfo> inviteList) {
		if(null == openAdapter) {
			openAdapter = new OpenAdapter(inviteList);
			listView.setAdapter(openAdapter);
		} else {
			openAdapter.refreshListData(inviteList);
		}
	}

	private void refreshDataAysnc(final GetOpenInviteReqParam data) {
		if(!Utils.isConnected(this)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetOpenInviteListLoader(OpenInviteListActivity.this, data, new GetOpenInviteListCallback() {
			@Override
			public void callBack(int retId, int retNum, String msg,
					ArrayList<OpenInvitationInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {
					openAdapter.refreshListData(inviteList);
					listFooter.refreshFooterView(inviteList.size(), data.pageSize);
					refreshResultHintTv(retNum);
				} else {
					Toast.makeText(OpenInviteListActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private void appendListDataAsync(final GetOpenInviteReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetOpenInviteListLoader(OpenInviteListActivity.this, data, new GetOpenInviteListCallback() {
			@Override
			public void callBack(int retId, int retNum, String msg,
					ArrayList<OpenInvitationInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || inviteList.size() == 0) {
					listFooter.displayLast();
				} else if(BaseRequest.REQ_RET_OK == retId) {
					openAdapter.appendListData(inviteList);
					listFooter.refreshFooterView(inviteList.size(), data.pageSize);
					refreshResultHintTv(retNum);
				} else {
					if(BaseRequest.REQ_RET_F_OPEN_LIST_REFRESH == retId) {
						Log.e(TAG, "REQ_RET_F_OPEN_LIST_REFRESH should not occour !!!");
						openAdapter.refreshListData(inviteList);
						listFooter.refreshFooterView(inviteList.size(), data.pageSize);
					} else {
						listFooter.displayMore();
					}
					Toast.makeText(OpenInviteListActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	private class OpenAdapter extends IgBaseAdapter {
		private ArrayList<OpenInvitationInfo> list;
		private GlobalData gd;
		private String cusSn;
		
		public OpenAdapter(ArrayList<OpenInvitationInfo> list) {
			this.list = list;
			gd = MainApp.getInstance().getGlobalData();
			cusSn = MainApp.getInstance().getCustomer().sn;
		}

		public void appendListData(ArrayList<OpenInvitationInfo> list) {
			for(OpenInvitationInfo data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}
		
		public void refreshListData(ArrayList<OpenInvitationInfo> list) {
			this.list.clear();
			for(OpenInvitationInfo data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(OpenInviteListActivity.this, R.layout.invite_list_open_item, null);
				holder = new ViewHolder();
				holder.avatar = (RoundedImageView) convertView.findViewById(R.id.invite_list_open_item_avatar);
				holder.nickname = (TextView) convertView.findViewById(R.id.invite_list_open_item_nickname);
				holder.teetime = (TextView) convertView.findViewById(R.id.invite_list_open_item_teetime);
				holder.course = (TextView) convertView.findViewById(R.id.invite_list_open_item_course);
				holder.paytype = (TextView) convertView.findViewById(R.id.invite_list_open_item_paytye);
				holder.stake = (TextView) convertView.findViewById(R.id.invite_list_open_item_stake);
				holder.sex = (ImageView) convertView.findViewById(R.id.invite_list_open_item_sex);
				holder.state = (TextView) convertView.findViewById(R.id.invite_list_open_item_state);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			OpenInvitationInfo data = list.get(position);
			if(data.inviterSn.equals(cusSn)) { // 我发起的(显示默认图标，或最后申请人头像)
				loadAvatar(OpenInviteListActivity.this, data.inviteeSn, data.inviteeAvatar, holder.avatar, true,
						(int) getResources().getDimension(R.dimen.avatar_detail_size));
			} else { // 他人发起的，显示发起人头像
				loadAvatar(OpenInviteListActivity.this, data.inviterSn, data.avatar, holder.avatar);
			}

			holder.nickname.setText(data.inviterNickname);
			if(Const.SEX_MALE == data.inviterSex) {
				holder.sex.setImageResource(R.drawable.ic_male);
			} else {
				holder.sex.setImageResource(R.drawable.ic_female);
			}
			holder.teetime.setText(data.teeTime);
			holder.course.setText(data.courseName);
			holder.paytype.setText(gd.getPayTypeName(data.payType));
			holder.stake.setText(gd.getStakeName(data.stake));
			boolean canEnter = false;
			switch(data.displayStatus) {
				case Const.INVITE_OPEN_GREEN_WAIT:
					canEnter = true;
					holder.state.setBackgroundResource(R.drawable.invite_list_status_green_bkg);
//					convertView.setOnClickListener(new onInviteItemClickListner(position));
//					convertView.setBackgroundResource(R.drawable.invite_list_bkg);
					break;
				case Const.INVITE_OPEN_YELLOW_APPLYING:
					canEnter = true;
					holder.state.setBackgroundResource(R.drawable.invite_list_status_yellow_bkg);
//					convertView.setOnClickListener(new onInviteItemClickListner(position));
//					convertView.setBackgroundResource(R.drawable.invite_list_bkg);
					break;
				case Const.INVITE_OPEN_RED_ACCEPTED:
					canEnter = false;
					holder.state.setBackgroundResource(R.drawable.invite_list_status_red_bkg);
//					convertView.setOnClickListener(null);
//					convertView.setBackgroundResource(R.drawable.invite_list_bkg_disable);
					break;
			}
			convertView.setOnClickListener(new onInviteItemClickListner(canEnter, position));
			convertView.setBackgroundResource(R.drawable.invite_list_bkg);
			holder.state.setText(data.displayMsg);
			return convertView;
		}
		
		private class onInviteItemClickListner implements View.OnClickListener {
			private int position;
			private boolean canEnter;
			public onInviteItemClickListner(boolean canEnter, int position) {
				this.canEnter = canEnter;
				this.position = position;
			}

			@Override
			public void onClick(View v) {
				String sn = MainApp.getInstance().getCustomer().sn;
				if(canEnter) {
					startInviteDetail(sn, list.get(position));
				} else {
					if(sn.equals(list.get(position).inviterSn) || list.get(position).acceptMe) {
						Toast.makeText(OpenInviteListActivity.this, 
								R.string.str_hall_open_list_click_mine, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(OpenInviteListActivity.this, 
								R.string.str_hall_open_list_click_over, Toast.LENGTH_SHORT).show();						
					}
				}
			}
		}
		
		class ViewHolder {
			private RoundedImageView avatar;
			private TextView nickname;
			private TextView teetime;
			private TextView course;
			private TextView paytype;
			private TextView stake;
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
	
	private void startInviteDetail(String sn, OpenInvitationInfo openInvitationInfo) {
		if(openInvitationInfo.inviterSn.equals(sn)) { // 我发起的约球
			InviteDetailOpenMineActivity.startInviteDetailOpenMineForResult(OpenInviteListActivity.this, openInvitationInfo);
			overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
		} else { // 他人发起的
			InviteDetailOpenOtherActivity.startInviteDetailOpenOtherForResult(OpenInviteListActivity.this, openInvitationInfo);
			overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, "resultCode: " + resultCode);
		if(Activity.RESULT_OK == resultCode && null != intent) {
			switch(requestCode) {
				case Const.REQUST_CODE_INVITE_DETAIL_OPEN: {
					if(!intent.getExtras().getBoolean(InviteDetailOpenOtherActivity.BUNDLE_KEY_STATUS_CHANGED)) {
						Log.w(TAG, "REQUST_CODE_INVITE_DETAIL_OPEN false should be true !!!");
					} else {
						// 状态变化了，目前重刷列表
						Utils.logh(TAG, "REQUST_CODE_INVITE_DETAIL_OPEN status changed ... ");
						reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
						handler.sendEmptyMessageDelayed(MSG_INIT_LIST, INIT_DELAY);
					}
					return ;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	private OnRefreshListener pullRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
			refreshDataAysnc(reqData);
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
			reqData.pageNum = openAdapter.getCount() / reqData.pageSize + 1;
			appendListDataAsync(reqData);
		}
	};
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.hall_open_list_topbar_back:
				finishWithAnim();
				break;
			case R.id.hall_open_list_topbar_start_invite:
				StartInviteOpenActivity.startOpenInviteRefresh(OpenInviteListActivity.this);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			finishWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void finishWithAnim() {
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}

	@Override
	public void startRefresh(GetOpenInviteReqParam reqParam) {
		reqData = reqParam;
		handler.sendEmptyMessageDelayed(MSG_INIT_LIST, INIT_DELAY);
	}
	
	private final static int MSG_INIT_LIST = 1;
	private final static long INIT_DELAY = 500;
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			openAdapter = null;
			initListDataAsync(reqData);
			// 发送查看广播给MainActivity，主动获取当前消息数量，更新导航栏提示图标
			sendBroadcast(new Intent(Const.IG_ACTION_NEW_ALERTS));
			// 发送广播给我的约球，请求更新
			sendBroadcast(new Intent(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY));
			return false;
		}
	});
}
