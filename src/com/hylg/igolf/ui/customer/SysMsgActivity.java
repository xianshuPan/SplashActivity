package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.SysMsgInfo;
import com.hylg.igolf.cs.loader.GetSystemMsgListLoader;
import com.hylg.igolf.cs.loader.GetSystemMsgListLoader.GetSystemMsgListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.customer.SysMsgDetailActivity.GetSysMsgDetailCallback;
import com.hylg.igolf.ui.reqparam.GetSysMsgReqParam;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.view.PullListView;
import com.hylg.igolf.ui.view.PullListView.OnLoadMoreListener;
import com.hylg.igolf.ui.view.PullListView.OnRefreshListener;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

public class SysMsgActivity extends Activity implements GetSysMsgDetailCallback {
	
	private static final String TAG = "SysMsgActivity";
	private LoadFail loadFail;
	private GetSysMsgReqParam reqData;
	private GetSystemMsgListLoader reqLoader = null;
	private PullListView listView;
	private ListFooter listFooter;
	private SysMsgAdapter sysMsgAdapter = null;
	public static final String MSG_STATUS_READ = "1";
	public static final String MSG_STATUS_UNREAD = "0";
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetSystemMsgListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
			Utils.logh(TAG, "reqLoader: " + reqLoader);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_ac_sys_msg_list);
		getViews();
		getData();
	}
	
	private void getData(){
		reqData = new GetSysMsgReqParam();
		reqData.sn = MainApp.getInstance().getCustomer().sn;
		reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
		reqData.pageSize = MainApp.getInstance().getGlobalData().pageSize;
		if(null != sysMsgAdapter) {
			listView.setAdapter(sysMsgAdapter);
			Utils.logh(TAG, "exist sysMsgAdapter " + sysMsgAdapter);
		} else {
			initDataAysnc(reqData);
		}
		Utils.logh(TAG, "onCreate reqData: " + reqData.log());
	}
	
	private void getViews(){
		listFooter = new ListFooter(this);
		loadFail = new LoadFail(this, (RelativeLayout) findViewById(R.id.system_msg_list_load_fail));
		loadFail.setOnRetryClickListener(retryListener);
		listView = (PullListView) findViewById(R.id.system_msg_listview);
		listView.addFooterView(listFooter.getFooterView());
		listView.setonRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);
	}
	
	private class SysMsgAdapter extends BaseAdapter {
		private ArrayList<SysMsgInfo> list;
		
		public SysMsgAdapter(ArrayList<SysMsgInfo> list) {
			if(null == list){
				this.list = new ArrayList<SysMsgInfo>();
			}else{
				this.list = list;
			}
		}

		public void refreshListInfo(ArrayList<SysMsgInfo> list) {
			this.list.clear();
			this.list = list;
			notifyDataSetChanged();
		}

		public void appendListInfo(ArrayList<SysMsgInfo> list) {
			for(int i=0, size=list.size(); i<size; i++) {
				this.list.add(list.get(i));
			}
			notifyDataSetChanged();
		}
		
		public void refreshStatus(int position) {
			list.get(position).status = MSG_STATUS_READ;
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHodler holder = null;
			if(null == convertView) {
				convertView = View.inflate(SysMsgActivity.this, R.layout.customer_system_msg_list_item, null);
				holder = new ViewHodler();
				holder.imgIv = (ImageView) convertView.findViewById(R.id.system_msg_li_img);
				holder.titleIv = (TextView) convertView.findViewById(R.id.system_msg_li_title);
				holder.sendTimestampIv = (TextView) convertView.findViewById(R.id.system_msg_li_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHodler) convertView.getTag();
			}
			SysMsgInfo data = list.get(position);
			holder.titleIv.setText(data.title);
			holder.sendTimestampIv.setText(data.sendTimestamp);
			if(data.status.equals("0")){
				Utils.setVisible(holder.imgIv);
			}else{
				Utils.setInvisible(holder.imgIv);
			}
			convertView.setOnClickListener(new OnItemClickListener(position));
			return convertView;
		}

		private class ViewHodler {
			protected ImageView imgIv;
			protected TextView titleIv;
			protected TextView sendTimestampIv;
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
		
		private class OnItemClickListener implements View.OnClickListener {
			private int position;
			public OnItemClickListener(int position) {
				this.position = position;
			}
			@Override
			public void onClick(View v) {
				SysMsgDetailActivity.startSysMsgDetailActivity(SysMsgActivity.this, list.get(position), position);
			}
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
			sysMsgAdapter = null;
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
			reqData.pageNum = reqData.pageNum + 1;
			appendListDataAsync(reqData);
		}
	};
	
	private void initListView(ArrayList<SysMsgInfo> sysMsgList) {
		if(null == sysMsgAdapter) {
			sysMsgAdapter = new SysMsgAdapter(sysMsgList);
			listView.setAdapter(sysMsgAdapter);
		} else {
			sysMsgAdapter.refreshListInfo(sysMsgList);
		}
		Utils.logh(TAG, "initListView new sysMsgAdapter: " + sysMsgAdapter);
	}
	
	private void appendListDataAsync(final GetSysMsgReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		clearLoader();
		reqLoader = new GetSystemMsgListLoader(this, data, new GetSystemMsgListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<SysMsgInfo> sysMsgList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {
					sysMsgAdapter.appendListInfo(sysMsgList);
					listFooter.refreshFooterView(sysMsgList.size(), data.pageSize);
				} else {
					if(BaseRequest.REQ_RET_F_NO_DATA == retId) {
						listFooter.displayLast();
					}
//					Toast.makeText(SysMsgActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private void refreshDataAysnc(final GetSysMsgReqParam data) {
		if(!Utils.isConnected(this)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetSystemMsgListLoader(this, data, new GetSystemMsgListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<SysMsgInfo> sysMsgList) {
				listView.onRefreshComplete();
				Utils.logh(TAG, "retId: " + retId + " inviteList: " + sysMsgList.size());
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						loadFail.displayNone();
						sysMsgAdapter.refreshListInfo(sysMsgList);
						listFooter.refreshFooterView(sysMsgList.size(), data.pageSize);
						break;
//					case BaseRequest.REQ_RET_F_NO_DATA: // do not exists in common
//						break;
					default: // normal fail
						Toast.makeText(SysMsgActivity.this, msg, Toast.LENGTH_SHORT).show();
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
	 * 			true: do init the first time, or fail retry.
	 * 			false: init by change the filter condition.
	 */
	private void initDataAysnc(final GetSysMsgReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetSystemMsgListLoader(this, data, new GetSystemMsgListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<SysMsgInfo> sysMsgList) {
				listView.onRefreshComplete();
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						loadFail.displayNone();
						initListView(sysMsgList);
						listFooter.refreshFooterView(sysMsgList.size(), data.pageSize);
						break;
					case BaseRequest.REQ_RET_F_NO_DATA:
						loadFail.displayNoData(msg);
						break;
					default: // normal fail
						loadFail.displayFail(msg);
						break;
				}
//				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				reqLoader = null;
				WaitDialog.dismissWaitDialog();				
			}
		});
		
		reqLoader.requestData();
	}

	public void onSysMsgListBackClick(View view) {
		finishWithAnim();
	}

	private void finishWithAnim() {
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			finishWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void callBack(int position) {
		sysMsgAdapter.refreshStatus(position);
		// 发送查看广播给MainActivity，主动获取当前消息数量，更新导航栏提示图标
		sendBroadcast(new Intent(Const.IG_ACTION_NEW_ALERTS));
	}

}
