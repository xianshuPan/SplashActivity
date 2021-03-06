package com.hylg.igolf.ui.member;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.ScoreHistoryInfo;
import com.hylg.igolf.cs.loader.GetScoreHistoryListLoader;
import com.hylg.igolf.cs.loader.GetScoreHistoryListLoader.GetScoreHistoryListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.common.ScorecardPagerActivity;
import com.hylg.igolf.ui.reqparam.GetScoreHistoryReqParam;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.ListviewBottomRefresh;
import com.hylg.igolf.ui.view.ListviewBottomRefresh.OnRefreshListener;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

public class MemScoreHistoryFrg extends Fragment implements OnClickListener{
	private static final String TAG = "MemScoreHistoryActivity";
	private final static String BUNDLE_MEM_SN = "memSn";
	private final static String BUNDLE_SCORE_MSG = "scoreMsg";
	private LoadFail loadFail;
	private GetScoreHistoryReqParam reqData;
	private GetScoreHistoryListLoader reqLoader = null;
	private ListviewBottomRefresh listView;
	private ListFooter listFooter;
	private ScoreHistoryAdapter scoreHistoryAdapter = null;
	private TextView scroeMsgView;
	private String memSn;

	private FragmentActivity mContext = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View parentView = inflater.inflate(R.layout.mem_ac_score_history, container, false);

		getViews(parentView);

		return parentView;
	}


	private class ScoreHistoryAdapter extends BaseAdapter {
		private ArrayList<ScoreHistoryInfo> list;

		public ScoreHistoryAdapter(ArrayList<ScoreHistoryInfo> list) {
			if(null == list){
				this.list = new ArrayList<ScoreHistoryInfo>();
			} else {
				this.list = list;
			}
		}

		public void refreshListInfo(ArrayList<ScoreHistoryInfo> list) {
			this.list.clear();
			this.list = list;
			notifyDataSetChanged();
		}

		public void appendListInfo(ArrayList<ScoreHistoryInfo> list) {
			for(int i=0, size=list.size(); i<size; i++) {
				this.list.add(list.get(i));
			}
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHodler holder = null;
			if(null == convertView) {
				convertView = View.inflate(mContext, R.layout.mem_score_history_item, null);
				holder = new ViewHodler();
				holder.timeIv = (TextView) convertView.findViewById(R.id.score_history_li_teeTime);
				holder.handicapIv = (TextView) convertView.findViewById(R.id.score_history_li_handicap);
				holder.courseIv = (TextView) convertView.findViewById(R.id.score_history_li_course);
				holder.scroingBtn = (Button) convertView.findViewById(R.id.score_history_li_request);
				convertView.setTag(holder);
			} else {
				holder = (ViewHodler) convertView.getTag();
			}
			ScoreHistoryInfo data = list.get(position);
			holder.handicapIv.setText(data.handicap);
			holder.timeIv.setText(data.teeTime);
			holder.courseIv.setText(data.courseName);
			if(!data.imgName.isEmpty()) {
				Utils.setEnable(holder.scroingBtn);
				holder.scroingBtn.setOnClickListener(new OnCardClickListener(position));
			} else {
				Utils.setDisable(holder.scroingBtn);
				holder.scroingBtn.setOnClickListener(null);
			}

			if(position % 2 == 0) {
				convertView.setBackgroundResource(R.drawable.list_item_even_bkg);
			} else {
				convertView.setBackgroundResource(R.drawable.list_item_odd_bkg);
			}
			return convertView;
		}

		private class OnCardClickListener implements OnClickListener {
			private int position;
			
			public OnCardClickListener(int position) {
				this.position = position;
			}
	
			@Override
			public void onClick(View v) {
				ScoreHistoryInfo data = list.get(position);
				String[] urls = new String[] {data.imgName};
				Utils.logh(TAG, "data.cardName: " + data.imgName);
				ScorecardPagerActivity.startScorecardPagerActivity(mContext, 0, urls,
						ScorecardPagerActivity.TYPE_MEMEBER_VIEW, memSn, data.appSn);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
			}
			
		}
		private class ViewHodler {
			protected TextView courseIv;
			protected TextView timeIv;
			protected TextView handicapIv;
			protected Button scroingBtn;
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
	
	private void getViews(View view){
		listFooter = new ListFooter(mContext);
		loadFail = new LoadFail(mContext, (RelativeLayout) view.findViewById(R.id.score_history_list_load_fail));
		loadFail.setOnRetryClickListener(retryListener);
		listView = (ListviewBottomRefresh) view.findViewById(R.id.score_history_listview);
		//listView.addFooterView(listFooter.getFooterView());
		listView.setOnRefreshListener(pullRefreshListener);
		//listView.setOn(mOnLoadMoreListener);
		memSn = getArguments().getString(BUNDLE_MEM_SN);
		reqData = new GetScoreHistoryReqParam();
		reqData.sn = memSn;
		reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
		reqData.pageSize = MainApp.getInstance().getGlobalData().pageSize;
		if(null != scoreHistoryAdapter) {
			listView.setAdapter(scoreHistoryAdapter);
			Utils.logh(TAG, "exist inviteAdapter " + scoreHistoryAdapter);
		} else {
			initDataAysnc(reqData);
		}
		scroeMsgView = (TextView)view.findViewById(R.id.score_history_top_msg);

		if (getArguments().getString(BUNDLE_SCORE_MSG) != null &&
				getArguments().getString(BUNDLE_SCORE_MSG).length() > 0) {

			scroeMsgView.setText(getArguments().getString(BUNDLE_SCORE_MSG));
			scroeMsgView.setVisibility(View.VISIBLE);
		}

		view.findViewById(R.id.member_score_history_topbar_back).setOnClickListener(this);
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
			scoreHistoryAdapter = null;
			reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
			initDataAysnc(reqData);
		}
	};
	
//	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {
//		@Override
//		public void onLoadMore() {
//			if(listFooter.getStatus() == ListFooter.STATUS_LAST) {
//				Utils.logh(TAG, "last");
//				return ;
//			}
//			if(listFooter.getStatus() == ListFooter.STATUS_LOADING || isLoading()) {
//				Utils.logh(TAG, "loading");
//				return ;
//			}
//			reqData.pageNum = scoreHistoryAdapter.getCount() / reqData.pageSize + 1;
//			appendListDataAsync(reqData);
//		}
//	};
	
	private void initListView(ArrayList<ScoreHistoryInfo> fightRecordList) {
		if(null == scoreHistoryAdapter) {
			scoreHistoryAdapter = new ScoreHistoryAdapter(fightRecordList);
			listView.setAdapter(scoreHistoryAdapter);			
		} else {
			scoreHistoryAdapter.refreshListInfo(fightRecordList);
		}
	}
	
	private void appendListDataAsync(final GetScoreHistoryReqParam data) {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetScoreHistoryListLoader(mContext, data, new GetScoreHistoryListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<ScoreHistoryInfo> scoreHistoryList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {
					scoreHistoryAdapter.appendListInfo(scoreHistoryList);
					listFooter.refreshFooterView(scoreHistoryList.size(), data.pageSize);
				} else {
					if(BaseRequest.REQ_RET_F_NO_DATA == retId) {
						listFooter.displayLast();
					} else {
						listFooter.displayMore();
					}
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private void refreshDataAysnc(final GetScoreHistoryReqParam data) {
		if(!Utils.isConnected(mContext)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetScoreHistoryListLoader(mContext, data, new GetScoreHistoryListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<ScoreHistoryInfo> scoreHistoryList) {
				listView.onRefreshComplete();
				Utils.logh(TAG, "retId: " + retId + " scoreHistoryList: " + scoreHistoryList.size());
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						loadFail.displayNone();
						scoreHistoryAdapter.refreshListInfo(scoreHistoryList);
						listFooter.refreshFooterView(scoreHistoryList.size(), data.pageSize);
						break;
					case BaseRequest.REQ_RET_F_NO_DATA: // do not exists in common
						loadFail.displayFail(msg);
						break;
					default: // normal fail
						Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
						break;
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private void initDataAysnc(final GetScoreHistoryReqParam data) {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetScoreHistoryListLoader(mContext, data, new GetScoreHistoryListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<ScoreHistoryInfo> scoreHistoryList) {
				listView.onRefreshComplete();
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						loadFail.displayNone();
						initListView(scoreHistoryList);
						listFooter.refreshFooterView(scoreHistoryList.size(), data.pageSize);
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
			GetScoreHistoryListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
			Utils.logh(TAG, "reqLoader: " + reqLoader);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.member_score_history_topbar_back:
			//finishWithAnim();
			break;
		}
		
	}


}
