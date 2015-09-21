package com.hylg.igolf.ui.customer;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.InviteHistoryInfo;
import com.hylg.igolf.cs.loader.GetMyInviteHistoryListLoader;
import com.hylg.igolf.cs.loader.GetMyInviteHistoryListLoader.GetMyInviteHistoryListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.common.ScorecardPagerActivity;
import com.hylg.igolf.ui.hall.StartInviteStsActivity;
import com.hylg.igolf.ui.member.MemDetailActivity;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.reqparam.GetInviteHistoryReqParam;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.view.PullListView;
import com.hylg.igolf.ui.view.PullListView.OnLoadMoreListener;
import com.hylg.igolf.ui.view.PullListView.OnRefreshListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class InviteHistoryActivity extends Activity {
	
	private static final String TAG = "InviteHistoryActivity";
	private LoadFail loadFail;
	private GetInviteHistoryReqParam reqData;
	private GetMyInviteHistoryListLoader reqLoader = null;
	private PullListView listView;
	private ListFooter listFooter;
	private InviteHistoryAdapter inviteAdapter = null;
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetMyInviteHistoryListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
			Utils.logh(TAG, "reqLoader: " + reqLoader);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ExitToLogin.getInstance().addActivity(this);
		setContentView(R.layout.customer_ac_invite_history_list);
		getViews();
	}
	
	private void getViews(){
		listFooter = new ListFooter(this);
		loadFail = new LoadFail(this, (RelativeLayout) findViewById(R.id.invite_history_list_load_fail));
		loadFail.setOnRetryClickListener(retryListener);
		listView = (PullListView) findViewById(R.id.invite_history_listview);
		listView.addFooterView(listFooter.getFooterView());
		listView.setonRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);
		reqData = new GetInviteHistoryReqParam();
		reqData.sn = MainApp.getInstance().getCustomer().sn;
		reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
		reqData.pageSize = MainApp.getInstance().getGlobalData().pageSize;
		if(null != inviteAdapter) {
			listView.setAdapter(inviteAdapter);
			Utils.logh(TAG, "exist inviteAdapter " + inviteAdapter);
		} else {
			initDataAysnc(reqData);
		}
		Utils.logh(TAG, "onCreate reqData: " + reqData.log());
	}
	
	private class InviteHistoryAdapter extends IgBaseAdapter {
		private ArrayList<InviteHistoryInfo> list;
		
		public InviteHistoryAdapter(ArrayList<InviteHistoryInfo> list) {
			if(null == list){
				this.list = new ArrayList<InviteHistoryInfo>();
			}else{
				this.list = list;
			}
		}

		public void refreshListInfo(ArrayList<InviteHistoryInfo> list) {
			this.list.clear();
			this.list = list;
			notifyDataSetChanged();
		}

		public void appendListInfo(ArrayList<InviteHistoryInfo> list) {
			for(int i=0, size=list.size(); i<size; i++) {
				this.list.add(list.get(i));
			}
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHodler holder = null;
			if(null == convertView) {
				convertView = View.inflate(InviteHistoryActivity.this, R.layout.customer_invite_history_list_item, null);
				holder = new ViewHodler();
				holder.avatarIv = (ImageView) convertView.findViewById(R.id.invite_history_li_avatar);
				holder.teeTimeIv = (TextView) convertView.findViewById(R.id.invite_history_li_time);
				holder.myHCPIIv = (TextView) convertView.findViewById(R.id.invite_history_li_myhcpi);
				holder.palHCPIIv = (TextView) convertView.findViewById(R.id.invite_history_li_palhcpi);
				holder.nicknameIv = (TextView) convertView.findViewById(R.id.invite_history_li_nickname);
				holder.inviteBtnIv = (TextView) convertView.findViewById(R.id.invite_history_li_request);
				convertView.setTag(holder);
			} else {
				holder = (ViewHodler) convertView.getTag();
			}
			final InviteHistoryInfo data = list.get(position);
			holder.teeTimeIv.setText(data.teeTime);
			loadAvatar(InviteHistoryActivity.this, data.sn, data.avatar, holder.avatarIv,
					(int) getResources().getDimension(R.dimen.avatar_rank_li_size));
			holder.nicknameIv.setText(data.nickname);
			setMyScoreInfo(holder.myHCPIIv, data);
			setGolferScoreInfo(holder.palHCPIIv, data);
			holder.inviteBtnIv.setOnClickListener(new onInviteBtnClickListener(position));
			if(position % 2 == 0) {
				convertView.setBackgroundResource(R.drawable.list_item_even_bkg);
			} else {
				convertView.setBackgroundResource(R.drawable.list_item_odd_bkg);
			}
			convertView.setOnClickListener(new onListItemClickListener(position));
			return convertView;
		}
		
		/**
		 * 球友成绩
		 * @param hi
		 * @param tv
		 * @param data
		 */
		private void setGolferScoreInfo(TextView tv, InviteHistoryInfo data) {
			if(data.palHCPI == Integer.MAX_VALUE) {
				tv.setTextColor(getResources().getColor(R.color.color_hint_txt));
				tv.setBackgroundColor(Color.TRANSPARENT);
				tv.setClickable(false);
			} else {
				if(data.palHCPIStatus == Const.SCORE_VALIDE) {
					tv.setBackgroundResource(R.drawable.xbtn_green);
				} else {
					tv.setBackgroundResource(R.drawable.btn_red_normal);
				}
				tv.setTextColor(getResources().getColor(android.R.color.white));
				tv.setClickable(true);
				tv.setOnClickListener(new onScoreClickListener(
						data.sn, data.appSn, data.imgName));
			}
			tv.setText(Utils.getIntString(InviteHistoryActivity.this, data.palHCPI));
		}
		
		private void setMyScoreInfo(TextView tv, InviteHistoryInfo data) {
			if(data.myHCPI == Integer.MAX_VALUE) {
				tv.setTextColor(getResources().getColor(R.color.color_hint_txt));
				tv.setBackgroundColor(Color.TRANSPARENT);
				tv.setClickable(false);
			} else {
				if(data.myHCPIStatus == Const.SCORE_VALIDE) {
					tv.setBackgroundResource(R.drawable.xbtn_green);
				} else {
					tv.setBackgroundResource(R.drawable.btn_red_normal);
				}
				tv.setTextColor(getResources().getColor(android.R.color.white));
				tv.setClickable(true);
				tv.setOnClickListener(new onScoreClickListener(
						MainApp.getInstance().getCustomer().sn,
						data.appSn, data.imgName));
			}
			tv.setText(Utils.getIntString(InviteHistoryActivity.this, data.myHCPI));
		}
		
		private class onListItemClickListener implements OnClickListener {
			private int position;
			public onListItemClickListener(int position) {
				this.position = position;
			}
			@Override
			public void onClick(View v) {
				MemDetailActivityNew.startMemDetailActivity(InviteHistoryActivity.this, list.get(position).sn);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
			}
			
		}
		
		private class onScoreClickListener implements OnClickListener {
			private String sn, appSn, imgName;
			public onScoreClickListener(String sn, String appSn, String imgName) {
				this.sn = sn;
				this.appSn = appSn;
				this.imgName = imgName;
			}
			@Override
			public void onClick(View v) {
				String[] urls = new String[] {imgName};
				ScorecardPagerActivity.startScorecardPagerActivity(InviteHistoryActivity.this, 0, urls, 
						ScorecardPagerActivity.TYPE_MY_HISTORY_VIEW, sn, appSn);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
			}
			
		}
		
		private class onInviteBtnClickListener implements OnClickListener {
			private int position;
			public onInviteBtnClickListener(int position) {
				this.position = position;
			}
			@Override
			public void onClick(View v) {
				StartInviteStsActivity.startOpenInvite(InviteHistoryActivity.this, list.get(position).sn);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
			}
			
		}
		
		private class ViewHodler {
			protected TextView teeTimeIv;
			protected ImageView avatarIv;
			protected TextView nicknameIv;
			protected TextView myHCPIIv;
			protected TextView palHCPIIv;
			protected TextView inviteBtnIv;
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(null != reqLoader) {
			reqLoader.stopTask(true);
			reqLoader = null;
		}
	}
	
	private OnRefreshListener pullRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
			refreshDataAysnc(reqData);
		}
	};
	
	private onRetryClickListener retryListener = new onRetryClickListener() {
		@Override
		public void onRetryClick() {
			Utils.logh(TAG, "onRetryClick ... ");
			inviteAdapter = null;
			reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
			initDataAysnc(reqData);
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
			reqData.pageNum = inviteAdapter.getCount() / reqData.pageSize + 1;
			appendListDataAsync(reqData);
		}
	};
	
	private void initListView(ArrayList<InviteHistoryInfo> inviteHistoryList) {
		if(null == inviteAdapter) {
			inviteAdapter = new InviteHistoryAdapter(inviteHistoryList);
			listView.setAdapter(inviteAdapter);			
		} else {
			inviteAdapter.refreshListInfo(inviteHistoryList);
		}
	}
	
	private void appendListDataAsync(final GetInviteHistoryReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetMyInviteHistoryListLoader(this, data, new GetMyInviteHistoryListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<InviteHistoryInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {
					inviteAdapter.appendListInfo(inviteList);
					listFooter.refreshFooterView(inviteList.size(), data.pageSize);
				} else {
					if(BaseRequest.REQ_RET_F_NO_DATA == retId) {
						listFooter.displayLast();
					}
//					Toast.makeText(InviteHistoryActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private void refreshDataAysnc(final GetInviteHistoryReqParam data) {
		if(!Utils.isConnected(this)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetMyInviteHistoryListLoader(this, data, new GetMyInviteHistoryListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<InviteHistoryInfo> inviteList) {
				listView.onRefreshComplete();
				Utils.logh(TAG, "refreshDataAysnc---"+"retId: " + retId + " inviteList: " + inviteList.size());
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						loadFail.displayNone();
						inviteAdapter.refreshListInfo(inviteList);
						listFooter.refreshFooterView(inviteList.size(), data.pageSize);
						break;
					case BaseRequest.REQ_RET_F_NO_DATA: // do not exists in common
//						loadFail.displayFail(msg);
						listFooter.displayLast();
						break;
					default: // normal fail
						Toast.makeText(InviteHistoryActivity.this, msg, Toast.LENGTH_SHORT).show();
						break;
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	/**
	 * 
	 * @param data
	 * @param init
	 * 	true: do init the first time, or fail retry.
	 * 	false: init by change the filter condition.
	 */
	private void initDataAysnc(final GetInviteHistoryReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetMyInviteHistoryListLoader(this, data, new GetMyInviteHistoryListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<InviteHistoryInfo> inviteList) {
				listView.onRefreshComplete();
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						loadFail.displayNone();
						initListView(inviteList);
						listFooter.refreshFooterView(inviteList.size(), data.pageSize);
						break;
					case BaseRequest.REQ_RET_F_NO_DATA:
//						loadFail.displayNoData(msg);
						listFooter.displayLast();
						break;
					default: // normal fail
						loadFail.displayFail(msg);
						break;
				}
//				Toast.makeText(InviteHistoryActivity.this, msg, Toast.LENGTH_SHORT).show();
				reqLoader = null;
				WaitDialog.dismissWaitDialog();				
			}
		});
		
		reqLoader.requestData();
	}

	public void onInviteHistoryBackClick(View view) {
		finishWithAnim();
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

}
