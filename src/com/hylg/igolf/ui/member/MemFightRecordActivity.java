package com.hylg.igolf.ui.member;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.MemMyFightInfo;
import com.hylg.igolf.cs.loader.GetFightsListLoader;
import com.hylg.igolf.cs.loader.GetFightsListLoader.GetFightsListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.reqparam.GetFightsReqParam;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.view.PullListView;
import com.hylg.igolf.ui.view.PullListView.OnLoadMoreListener;
import com.hylg.igolf.ui.view.PullListView.OnRefreshListener;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class MemFightRecordActivity extends Activity implements OnClickListener{
	private static final String TAG = "MemFightRecordActivity";
	private final static String BUNDLE_MEM_SN = "memSn";
	private final static String BUNDLE_FIGHT_MSG = "fightMsg";
	private LoadFail loadFail;
	private GetFightsReqParam reqData;
	private GetFightsListLoader reqLoader = null;
	private PullListView listView;
	private ListFooter listFooter;
	private FightRecordAdapter fightRecordAdapter = null;
	private TextView fightMsgView;
	
	public static void startMemFightRecordActivity(Context context,String fightMsg,String memSn) {
		Intent intent = new Intent(context, MemFightRecordActivity.class);
		intent.putExtra(BUNDLE_MEM_SN, memSn);
		intent.putExtra(BUNDLE_FIGHT_MSG, fightMsg);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mem_ac_fight_record);
		getViews();
	}
	
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
				convertView = View.inflate(MemFightRecordActivity.this, R.layout.mem_fight_record_item, null);
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
	
	private void getViews(){
		listFooter = new ListFooter(this);
		loadFail = new LoadFail(this, (RelativeLayout) findViewById(R.id.fight_record_list_load_fail));
		loadFail.setOnRetryClickListener(retryListener);
		listView = (PullListView) findViewById(R.id.fight_record_listview);
		listView.addFooterView(listFooter.getFooterView());
		listView.setonRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);
		reqData = new GetFightsReqParam();
		reqData.sn = MainApp.getInstance().getCustomer().sn;
		reqData.memSn = getIntent().getExtras().getString(BUNDLE_MEM_SN);
		reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
		reqData.pageSize = MainApp.getInstance().getGlobalData().pageSize;
		if(null != fightRecordAdapter) {
			listView.setAdapter(fightRecordAdapter);
			Utils.logh(TAG, "exist inviteAdapter " + fightRecordAdapter);
		} else {
			initDataAysnc(reqData);
		}
		findViewById(R.id.member_fight_record_topbar_back).setOnClickListener(this);
		fightMsgView = (TextView) findViewById(R.id.member_fight_record_fightMsg);
		String fightMsg = getIntent().getExtras().getString(BUNDLE_FIGHT_MSG);
		if(fightMsg.isEmpty()){
			Utils.setGone(fightMsgView);
		}else{
			Utils.setVisible(fightMsgView);
			fightMsgView.setText(fightMsg);
		}
		
		Utils.logh(TAG, "onCreate reqData: " + reqData.log());
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
			fightRecordAdapter = null;
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
			reqData.pageNum = fightRecordAdapter.getCount() / reqData.pageSize + 1;
			appendListDataAsync(reqData);
		}
	};
	
	private void initListView(ArrayList<MemMyFightInfo> fightRecordList) {
		if(null == fightRecordAdapter) {
			fightRecordAdapter = new FightRecordAdapter(fightRecordList);
			listView.setAdapter(fightRecordAdapter);			
		} else {
			fightRecordAdapter.refreshListInfo(fightRecordList);
		}
	}
	
	private void appendListDataAsync(final GetFightsReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetFightsListLoader(MemFightRecordActivity.this, data, new GetFightsListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<MemMyFightInfo> fightRecordList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {
					fightRecordAdapter.appendListInfo(fightRecordList);
					listFooter.refreshFooterView(fightRecordList.size(), data.pageSize);
				} else {
					if(BaseRequest.REQ_RET_F_NO_DATA == retId) {
						listFooter.displayLast();
					} else {
						listFooter.displayMore();
					}
					Toast.makeText(MemFightRecordActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private void refreshDataAysnc(final GetFightsReqParam data) {
		if(!Utils.isConnected(this)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetFightsListLoader(MemFightRecordActivity.this, data, new GetFightsListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<MemMyFightInfo> fightRecordList) {
				listView.onRefreshComplete();
				Utils.logh(TAG, "retId: " + retId + " fightRecordList: " + fightRecordList.size());
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						fightRecordAdapter.refreshListInfo(fightRecordList);
						listFooter.refreshFooterView(fightRecordList.size(), data.pageSize);
						break;
					case BaseRequest.REQ_RET_F_NO_DATA: // do not exists in common
						break;
					default: // normal fail
						Toast.makeText(MemFightRecordActivity.this, msg, Toast.LENGTH_SHORT).show();
						break;
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private void initDataAysnc(final GetFightsReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetFightsListLoader(MemFightRecordActivity.this, data, new GetFightsListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<MemMyFightInfo> fightRecordList) {
				listView.onRefreshComplete();
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						loadFail.displayNone();
						initListView(fightRecordList);
						listFooter.refreshFooterView(fightRecordList.size(), data.pageSize);
						break;
					case BaseRequest.REQ_RET_F_NO_DATA:
						loadFail.displayNoData(msg);
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
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetFightsListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
			Utils.logh(TAG, "reqLoader: " + reqLoader);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.member_fight_record_topbar_back:
			finishWithAnim();
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
}
