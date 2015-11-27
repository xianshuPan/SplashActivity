package com.hylg.igolf.ui.coach;

import java.util.ArrayList;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachComemntsItem;
import com.hylg.igolf.cs.data.MyFolloweInfo;
import com.hylg.igolf.cs.loader.GetCoachCommentsListLoader;
import com.hylg.igolf.cs.loader.GetMyFollowerListLoader;
import com.hylg.igolf.cs.loader.GetCoachCommentsListLoader.GetCoachCommentsCallback;
import com.hylg.igolf.cs.loader.GetMyFollowerListLoader.GetMyFollowerListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.FriendAttentionAdd;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.EhecdListview.OnLoadMoreListener;
import com.hylg.igolf.ui.view.EhecdListview.OnRefreshListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CoachCommentsListActivity extends FragmentActivity {
	
	private final String 				TAG 						= "MyFollowerActivity";
	
	private static final String         Bundle_Key                  = "coach_id";
	
	private ImageButton  				mBack 						= null;
	
	private EhecdListview 				mList 						= null;
	
	private FragmentActivity 			mContext 					= null;
	
	private String 						sn							= "";
	
	private int 						startPage					= 0, 
										pageSize					= 10;
	
	private GetCoachCommentsListLoader  reqLoader                   = null;
	
	
	private CoachCommentsAdapter        mAdapter					= null;
	
	private long                        mCoachID                    = 0;
	
	
	public static void startCoachCommentsListActivity(Activity context,long coach_id) {

		Intent intent = new Intent(context, CoachCommentsListActivity.class);
		
		intent.putExtra(Bundle_Key, coach_id);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
	}
	
	public static void startCoachCommentsListActivity(Fragment context,long coach_id) {

		Intent intent = new Intent(context.getActivity(), CoachCommentsListActivity.class);
		intent.putExtra(Bundle_Key, coach_id);
		context.startActivityForResult(intent, Const.REQUST_CODE_SIGNATURE_MY);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.coach_comments_list);
		
		mBack =  (ImageButton)  findViewById(R.id.coach_comments_list_back);
		mList = (EhecdListview) findViewById(R.id.coach_comments_list_listview);
		
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mContext.finish();
			}
		});
		//mList.addFooterView(new ListFooter(mContext).getFooterView());
		
		sn = MainApp.getInstance().getCustomer().sn;
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
		mList.setOnLoadMoreListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				loadMoreData();
			}
		});
		
		mList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshData();
			}
		});
		
		mCoachID = getIntent().getLongExtra(Bundle_Key, 0);
		
		initDataAysnc();
		
	}
	
	@Override
	protected void onResume () {
		
		DebugTools.getDebug().debug_v(TAG, "sn??/、、？/????"+sn);
		
		super.onResume();
	}
	
	
	
	/*初始化数据请求*/
	private void initDataAysnc() {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		clearLoader();
		reqLoader = new GetCoachCommentsListLoader(this, mCoachID, startPage, pageSize, new GetCoachCommentsCallback() {

			@Override
			public void callBack(int retId, String msg, ArrayList<CoachComemntsItem> commentsList) {
				

					
					if(BaseRequest.REQ_RET_F_NO_DATA == retId || commentsList.size() == 0) {
						if(msg.trim().length() == 0) {
							msg = getString(R.string.str_golfers_req_no_data_hint);
						}
		
						Toast.makeText(CoachCommentsListActivity.this, msg, Toast.LENGTH_SHORT).show();
						
					} else if(BaseRequest.REQ_RET_OK == retId) {
						
						initListView(commentsList);
						
					} else {

						Toast.makeText(CoachCommentsListActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
					reqLoader = null;
					
					WaitDialog.dismissWaitDialog();
					mList.onRefreshComplete();
				}
			
		});
		reqLoader.requestData();
	}
	
	/*加载更多*/
	private void loadMoreData() {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		//WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		
		startPage++;
		/*sn 暂时等于1*/
		reqLoader = new GetCoachCommentsListLoader(this, mCoachID, startPage, pageSize, new GetCoachCommentsCallback() {

			@Override
			public void callBack(int retId, String msg, ArrayList<CoachComemntsItem> commentsList) {
					

				if(BaseRequest.REQ_RET_F_NO_DATA == retId || commentsList.size() == 0) {
						if(msg.trim().length() == 0) {
							msg = getString(R.string.str_golfers_req_no_data_hint);
						}
		
						Toast.makeText(CoachCommentsListActivity.this, msg, Toast.LENGTH_SHORT).show();
						
					} else if(BaseRequest.REQ_RET_OK == retId) {
						
						//initListView(commentsList);
						mAdapter.appendListInfo(commentsList);
						
					} else {

						Toast.makeText(CoachCommentsListActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
					reqLoader = null;
					mList.onRefreshComplete();
					WaitDialog.dismissWaitDialog();
				}
			
		});
		reqLoader.requestData();
	}
	
	/*初始化数据请求*/
	private void refreshData() {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		//WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		/*sn 暂时等于1*/
		
		startPage = 1;
		reqLoader = new GetCoachCommentsListLoader(this, mCoachID, startPage, pageSize, new GetCoachCommentsCallback() {

			@Override
			public void callBack(int retId, String msg, ArrayList<CoachComemntsItem> commentsList) {
				
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || commentsList.size() == 0) {
						if(msg.trim().length() == 0) {
							msg = getString(R.string.str_golfers_req_no_data_hint);
						}
		
						Toast.makeText(CoachCommentsListActivity.this, msg, Toast.LENGTH_SHORT).show();
						
					} else if(BaseRequest.REQ_RET_OK == retId) {
						
						//initListView(commentsList);
						mAdapter.refreshListInfo(commentsList);
						
					} else {

						Toast.makeText(CoachCommentsListActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
					reqLoader = null;
					mList.onRefreshComplete();
					WaitDialog.dismissWaitDialog();
				} 
			
		});
		reqLoader.requestData();
	}
	
	private void clearLoader() {
		if(isLoading()) {
			
			GetCoachCommentsListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}
	
	
	private void initListView(ArrayList<CoachComemntsItem> inviteList) {
		
		mAdapter = new CoachCommentsAdapter(mContext, inviteList);
		
		mList.setAdapter(mAdapter);
		//Utils.logh(TAG, "initListView myInviteAdapter " + myInviteAdapter);
	}
	

}
