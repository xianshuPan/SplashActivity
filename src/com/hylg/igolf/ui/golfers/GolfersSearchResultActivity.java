package com.hylg.igolf.ui.golfers;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.GolferInfo;
import com.hylg.igolf.cs.loader.SearchGolfersListLoader;
import com.hylg.igolf.cs.loader.SearchGolfersListLoader.SearchGolfersListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.golfers.adapter.GolfersAdapter;
import com.hylg.igolf.ui.hall.StartInviteStsActivity;
import com.hylg.igolf.ui.member.MemDetailActivity;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.view.PullListView;
import com.hylg.igolf.ui.view.PullListView.OnLoadMoreListener;
import com.hylg.igolf.ui.view.PullListView.OnRefreshListener;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class GolfersSearchResultActivity extends Activity implements OnClickListener{
	public static final String TAG = "GolfersSearchResultActivity";
	public static final String BUNDLE_KEY_SEARCH_KEYWORD = "search_keyword";
	private String keyWord;
	private String sn;
	private int pageSize;
	private int pageNum;
	private SearchGolfersListLoader reqLoader = null;
	private GolfersAdapter golfersAdapter;
	private PullListView listView;
	private ListFooter listFooter;
	private TextView resultHintTv;
	private LoadFail loadFail;
	
	public static void startGolfersSearchResult(Context context, String keyWord) {
		Intent intent = new Intent();
		intent.setClass(context, GolfersSearchResultActivity.class);
		intent.putExtra(BUNDLE_KEY_SEARCH_KEYWORD, keyWord);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.golfers_ac_search_result);
		keyWord = getIntent().getExtras().getString(BUNDLE_KEY_SEARCH_KEYWORD);
		MainApp app = MainApp.getInstance();
		sn = app.getCustomer().sn;
		pageSize = app.getGlobalData().pageSize;
		pageNum = app.getGlobalData().startPage;
		Utils.logh(TAG, "keyWord: " + keyWord);
		getViews();
		initListDataAsync(sn, pageNum, pageSize, keyWord);
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
		resultHintTv = (TextView) findViewById(R.id.golfers_search_result_hint);
		loadFail = new LoadFail(this, (RelativeLayout)findViewById(R.id.golfers_search_result_load_fail));
		loadFail.setOnRetryClickListener(retryListener);
		listView = (PullListView) findViewById(R.id.golfers_search_result_listview);
		listFooter = new ListFooter(this);
		listView.addFooterView(listFooter.getFooterView());
		listView.setonRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);
		findViewById(R.id.golfers_search_result_topbar_back).setOnClickListener(this);
	}

	private void refreshResultHintTv(int num) {
		if(num > 0) {
			resultHintTv.setText(String.format(
					getString(R.string.str_golfers_search_result_hint), num, keyWord));
		} else {
			resultHintTv.setText(String.format(
					getString(R.string.str_golfers_search_result_no_hint), keyWord));
		}
		
	}
	private void initListDataAsync(final String sn, final int pageNum, final int pageSize, final String keyWord) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new SearchGolfersListLoader(GolfersSearchResultActivity.this, sn, pageNum, pageSize, keyWord,
				new SearchGolfersListCallback() {
					@Override
					public void callBack(int retId, int retNum, String msg,
							ArrayList<GolferInfo> golfersList) {
						listView.onRefreshComplete();
						if(BaseRequest.REQ_RET_OK == retId) {
							loadFail.displayNone();
							initListView(golfersList);
							listFooter.refreshFooterView(golfersList.size(), pageSize);
							refreshResultHintTv(retNum);
						} else {
							if(BaseRequest.REQ_RET_F_NO_DATA == retId || golfersList.size() == 0) {
								// display reload page
								loadFail.displayNoDataRetry(msg);
							} else {
								loadFail.displayFail(msg);
							}
							refreshResultHintTv(retNum);
							Toast.makeText(GolfersSearchResultActivity.this, msg, Toast.LENGTH_SHORT).show();
						}
						reqLoader = null;
						WaitDialog.dismissWaitDialog();
					}
		});
		reqLoader.requestData();
	}
	
	private void initListView(ArrayList<GolferInfo> golfersList) {
		if(null == golfersAdapter) {
			golfersAdapter = new GolfersAdapter(this, mHandle, GolfersAdapter.BUNDLE_KEY_GOLFERS_LIST, golfersList);
			listView.setAdapter(golfersAdapter);
		} else {
			golfersAdapter.refreshListInfo(golfersList);
		}
	}
	
	private void refreshDataAysnc(final String sn, final int pageNum, final int pageSize, final String keyWord) {
		if(!Utils.isConnected(this)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new SearchGolfersListLoader(GolfersSearchResultActivity.this, sn, pageNum, pageSize, keyWord,
				new SearchGolfersListCallback() {
					@Override
					public void callBack(int retId, int retNum, String msg,
							ArrayList<GolferInfo> golfersList) {
						listView.onRefreshComplete();
						if(BaseRequest.REQ_RET_OK == retId) {
							golfersAdapter.refreshListInfo(golfersList);
							listFooter.refreshFooterView(golfersList.size(), pageSize);
							refreshResultHintTv(retNum);
						} else {
							// do not change previous data if fail, just toast fail message.
			//				if(BaseRequest.REQ_RET_F_NO_DATA == retId) { }
							Toast.makeText(GolfersSearchResultActivity.this, msg, Toast.LENGTH_SHORT).show();
						}
						reqLoader = null;
					}
		});
		reqLoader.requestData();
	}
	
	private void appendListDataAsync(final String sn, final int pageNum, final int pageSize, final String keyWord) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		listFooter.displayLoading();
		clearLoader();
		reqLoader = new SearchGolfersListLoader(GolfersSearchResultActivity.this, sn, pageNum, pageSize, keyWord,
				new SearchGolfersListCallback() {
					@Override
					public void callBack(int retId, int retNum, String msg,
							ArrayList<GolferInfo> golfersList) {
						listView.onRefreshComplete();
						if(BaseRequest.REQ_RET_OK == retId) {
							golfersAdapter.appendListInfo(golfersList);
							listFooter.refreshFooterView(golfersList.size(), pageSize);
							refreshResultHintTv(retNum);
						} else {
							if(BaseRequest.REQ_RET_F_NO_DATA == retId || golfersList.size() == 0) {
								listFooter.displayLast();
							} else {
								listFooter.displayMore();
							}
							Toast.makeText(GolfersSearchResultActivity.this, msg, Toast.LENGTH_SHORT).show();
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
			golfersAdapter = null;
			pageNum = MainApp.getInstance().getGlobalData().startPage;
			initListDataAsync(sn, pageNum, pageSize, keyWord);
		}
	};
	
	private OnRefreshListener pullRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			pageNum = MainApp.getInstance().getGlobalData().startPage;
			refreshDataAysnc(sn, pageNum, pageSize, keyWord);
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
			pageNum = golfersAdapter.getCount() / pageSize + 1;
			appendListDataAsync(sn, pageNum, pageSize, keyWord);
		}
	};
	
	private void clearLoader() {
		if(isLoading()) {
			SearchGolfersListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}

	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	private Handler mHandle = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			GolferInfo data = (GolferInfo) msg.getData().getSerializable(GolfersAdapter.BUNDLE_KEY_GOLFERS_LIST);
			switch(msg.what) {
				case GolfersAdapter.GOLFERS_INDEX_ITEM:
					onGolfersItemClicked(data);
					break;
				case GolfersAdapter.GOLFERS_INDEX_AVATAR:
					onGolfersAvatarClicked(data);
					break;
				case GolfersAdapter.GOLFERS_INDEX_INVITE:
					onGolfersInviteClicked(data);
					break;
			}
			return false;
		}
		
	});
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			finishWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void onGolfersInviteClicked(GolferInfo data) {
		Utils.logh(TAG, "onGolfersInviteClicked ");
		StartInviteStsActivity.startOpenInvite(this, data.sn);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	private void onGolfersAvatarClicked(GolferInfo data) {
		MemDetailActivityNew.startMemDetailActivity(this, data.sn);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	private void onGolfersItemClicked(GolferInfo data) {
		MemDetailActivityNew.startMemDetailActivity(this, data.sn);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	private void finishWithAnim() {
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.golfers_search_result_topbar_back:
			finishWithAnim();
			break;
		}
	}
}
