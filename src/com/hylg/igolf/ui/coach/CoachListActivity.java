package com.hylg.igolf.ui.coach;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.data.GolferInfo;
import com.hylg.igolf.cs.loader.GetCoachListLoader;
import com.hylg.igolf.cs.loader.GetCoachListLoader.GetCoachListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.common.*;
import com.hylg.igolf.ui.common.CoacherSortItemSelectActivity.onCoacherSortItemSelectListener;
import com.hylg.igolf.ui.common.CoacherTypeSelectActivity.onCoacherTypeSelectListener;
import com.hylg.igolf.ui.common.SexSelectActivity.onSexSelectListener;
import com.hylg.igolf.ui.golfers.adapter.GolfersAdapter;
import com.hylg.igolf.ui.hall.StartInviteStsActivity;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.reqparam.CoachListReqParam;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.utils.*;

public class CoachListActivity extends Activity implements 
													View.OnClickListener,
													onSexSelectListener, 
													onCoacherTypeSelectListener,
													onCoacherSortItemSelectListener{
	
	private static final String 					TAG = "CoachersListActivity";
	
	private CoachListReqParam 						reqData;
	
	private final static String 					BUNDLE_REQ_DATA = "coach_req_data";
	
	/*
	 * 教练显示列表
	 * */
	private EhecdListview 							listView;
	private CoachListAdapter 						coachAdapter;
	
	/*
	 * filters 选项
	 * */
	private TextView 								orderTv, typeTv, sexTv;
	
	private GetCoachListLoader 						reqLoader = null;
	
	/*
	 * 搜索教练
	 * 
	 * */
	private View 									searchBar, flowBar;
	private EditText 								searchEt;
	
	
	private GlobalData 								gd;
	private Customer                                customer;
	private LoadFail 								loadFail;
	
	public static void startCoachList(Context context, int coachType) {
		Intent intent = new Intent();
		intent.setClass(context, CoachListActivity.class);
		intent.putExtra(BUNDLE_REQ_DATA, coachType);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coach_list);
		
		reqData = new CoachListReqParam();
		
		reqData.type = getIntent().getIntExtra(BUNDLE_REQ_DATA, -1);
		gd = MainApp.getInstance().getGlobalData();
		customer = MainApp.getInstance().getCustomer();
		
		reqData.sn = customer.sn;
		reqData.pageSize = gd.pageSize;
		reqData.pageNum = 1;
		
		getViews(reqData);
		initListDataAsync(reqData);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(null != reqLoader) {
			reqLoader.stopTask(true);
			reqLoader = null;
		}
	}

	private void getViews(CoachListReqParam data) {
		
		findViewById(R.id.coach_list_topbar_back).setOnClickListener(this);
		findViewById(R.id.coach_list_topbar_search).setOnClickListener(this);
		findViewById(R.id.coach_list_filter_order_linear).setOnClickListener(this);
		orderTv = (TextView) findViewById(R.id.coach_list_filter_order_text);
		orderTv.setText(gd.getCoachSortItemName(data.rangeBy));

		findViewById(R.id.coach_list_filter_type_linear).setOnClickListener(this);
		typeTv = (TextView) findViewById(R.id.coach_list_filter_type_text);
		typeTv.setText(gd.getCoachTypeName(data.type));
		
		findViewById(R.id.coach_list_filter_sex_linear).setOnClickListener(this);
		sexTv = (TextView) findViewById(R.id.coach_list_filter_sex_text);
		sexTv.setText(gd.getSexName(data.sex));
		
		loadFail = new LoadFail(this, (RelativeLayout)findViewById(R.id.coach_list_load_fail));
		loadFail.setOnRetryClickListener(retryListener);
		listView = (EhecdListview) findViewById(R.id.coach_listview);
		
		listView.setOnRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);
		
		// Add flow buttons layout, and buttons click listener
		flowBar = View.inflate(this, R.layout.golfers_ac_search_bar, null);
		
		addContentView(flowBar, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		searchBar = flowBar.findViewById(R.id.golfers_list_search_bar);
		searchBar.findViewById(R.id.golfers_list_search_bar_cancel).setOnClickListener(this);
		searchEt = (EditText) searchBar.findViewById(R.id.golfers_list_search_box_input);
		searchEt.setOnEditorActionListener(mOnEditorActionListener);
		findViewById(R.id.golfers_list_search_box_cancel).setOnClickListener(this);
		Utils.setGone(flowBar);
		
	}

	private void displaySearchBar() {
		if(flowBar.getVisibility() == View.VISIBLE) {
			return;
		}
		Utils.setVisible(flowBar);
		Animation anim = new TranslateAnimation(0, 0, -searchBar.getHeight(), 0);
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				Utils.setVisible(searchBar);				
			}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
	            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
				searchEt.requestFocus();				
			}
		});
		searchBar.startAnimation(anim);
	}
	
	private void dismissSearchBar() {
		if(flowBar.getVisibility() != View.VISIBLE) {
			return;
		}
		Animation anim = new TranslateAnimation(0, 0, 0, -searchBar.getHeight());
		anim.setDuration(200);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {
				Utils.setInvisible(searchBar);
				Utils.setGone(flowBar);
				InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
				if(imm.isActive()) {
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});
		searchBar.startAnimation(anim);
	}
	
	private onRetryClickListener retryListener = new onRetryClickListener() {
		@Override
		public void onRetryClick() {
			Utils.logh(TAG, "onRetryClick ... ");
			coachAdapter = null;
			reqData.pageNum = gd.startPage;
			initListDataAsync(reqData);
		}
	};
	
	private OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//			Toast.makeText(GolfersListActivity.this, ("......" + event.getKeyCode() + "  " + event.getAction()), Toast.LENGTH_SHORT).show();
//			Utils.logh(TAG, "......" + event.getKeyCode() + "  " + event.getAction());
//			if( event != null && event.getAction()  == KeyEvent.ACTION_DOWN) {
//				Toast.makeText(GolfersListActivity.this, ("======" + event.getKeyCode() + "  " + event.getAction()), Toast.LENGTH_SHORT).show();
//			}
			if(event != null &&
			   event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
			   event.getAction()  == KeyEvent.ACTION_DOWN ) {
				Utils.logh(TAG, "======" + event.getKeyCode() + "  " + event.getAction());
				doSearch();
				return true;
			}
			return false;
		}
		
	};
	
	private void doSearch() {
		if(null == Utils.getEditTextString(searchEt)) {
			Toast.makeText(CoachListActivity.this, R.string.str_toast_keyword_null, Toast.LENGTH_SHORT).show();
		} else if(!Utils.isConnected(CoachListActivity.this)) {
			;
		} else {
			dismissSearchBar();
			searchByKeyword(Utils.getEditTextString(searchEt));
		}
	}
	
	private void searchByKeyword(String keyword) {
		searchEt.setText("");
		//GolfersSearchResultActivity.startGolfersSearchResult(CoachersListActivity.this, keyword);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			if(flowBar.getVisibility() == View.VISIBLE) {
				searchEt.setText("");
				dismissSearchBar();
				return true;
			}
			finishWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void finishWithAnim() {

		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}
	
	private EhecdListview.OnRefreshListener pullRefreshListener = new EhecdListview.OnRefreshListener() {
		
		@Override
		public void onRefresh() {
			// TODO Auto-generated method stub
			reqData.pageNum = gd.startPage;
			refreshDataAysnc(reqData);
		}
	};
	
	private EhecdListview.OnLoadMoreListener mOnLoadMoreListener = new EhecdListview.OnLoadMoreListener() {
		@Override
		public void onLoadMore() {
			
			reqData.pageNum = coachAdapter.getCount() / reqData.pageSize + 1;
			appendListDataAsync(reqData);
		}
	};
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.coach_list_topbar_back:
				finishWithAnim();
				break;
			case R.id.coach_list_topbar_search:
				displaySearchBar();
				break;
			case R.id.coach_list_filter_order_linear:
				changeFilter(FILTER_TYPE_ORDER);
				break;
			case R.id.coach_list_filter_type_linear:
				changeFilter(FILTER_TYPE_TYPE);
				break;
			case R.id.coach_list_filter_sex_linear:
				changeFilter(FILTER_TYPE_SEX);
				break;
			// 三星机型有处理不到确认键的，区别于ios，手机返回键取消，此处设置为搜索
			// 也处理键盘，也处理按钮
			case R.id.golfers_list_search_bar_cancel:
//				searchEt.setText("");
//				dismissSearchBar();
				doSearch();
				break;
			case R.id.golfers_list_search_box_cancel:
				if(null != Utils.getEditTextString(searchEt)) {
					searchEt.setText("");
				}
				break;
		}
	}

	private final static int FILTER_TYPE_ORDER = 1;
	private final static int FILTER_TYPE_TYPE = 2;
	private final static int FILTER_TYPE_SEX = 3;
	private void changeFilter(int type) {
		if(isLoading()) {
			Toast.makeText(this, R.string.str_toast_loading, Toast.LENGTH_SHORT).show();
			return;
		}
		switch(type) {
			case FILTER_TYPE_ORDER:
				
				CoacherSortItemSelectActivity.startCoacherSortItemSelect(CoachListActivity.this, reqData.rangeBy);
				break;
				
			case FILTER_TYPE_TYPE:
				CoacherTypeSelectActivity.startCoacherTypeSelect(CoachListActivity.this, reqData.type);
				break;

			case FILTER_TYPE_SEX:
				SexSelectActivity.startSexSelect(CoachListActivity.this, true, reqData.sex);
				break;
		}
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetCoachListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}

	/**
	 * 
	 * @param data
	 * @param init
	 * true: do init the first time, or fail retry.
	 * false: init by change the filter condition.
	 */
	private void initListDataAsync(final CoachListReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetCoachListLoader(CoachListActivity.this, data, new GetCoachListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<CoachItem> golfersList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || golfersList.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_golfers_req_no_data_hint);
					}
					// display reload page
					loadFail.displayNoDataRetry(msg);
	
					Toast.makeText(CoachListActivity.this, msg, Toast.LENGTH_SHORT).show();
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					loadFail.displayNone();
					initListView(golfersList);
					
				} else {
					loadFail.displayFail(msg);
					Toast.makeText(CoachListActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
				WaitDialog.dismissWaitDialog();
			}
		});
		reqLoader.requestData();
	}
	
	private void initListView(ArrayList<CoachItem> golfersList) {
		if(null == coachAdapter) {
			
			coachAdapter = new CoachListAdapter(this, mHandle, CoachListAdapter.BUNDLE_KEY_COACH_LIST, golfersList);
			listView.setAdapter(coachAdapter);
			
		} else {
			
			coachAdapter.refreshListInfo(golfersList);
		}
	}

	private void refreshDataAysnc(final CoachListReqParam data) {
		if(!Utils.isConnected(this)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetCoachListLoader(CoachListActivity.this, data, new GetCoachListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<CoachItem> golfersList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {
					   
					coachAdapter.refreshListInfo(golfersList);

				} else {
					// do not change previous data if fail, just toast fail message.
//					if(BaseRequest.REQ_RET_F_NO_DATA == retId) { }
					Toast.makeText(CoachListActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	private void appendListDataAsync(final CoachListReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}

		clearLoader();
		reqLoader = new GetCoachListLoader(CoachListActivity.this, data, new GetCoachListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<CoachItem> golfersList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || golfersList.size() == 0) {

					Toast.makeText(CoachListActivity.this, R.string.str_golfers_li_no_more, Toast.LENGTH_SHORT).show();
				} else if(BaseRequest.REQ_RET_OK == retId) {
					
					//golfersAdapter.appendListInfo(golfersList);

				} else {
					Toast.makeText(CoachListActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}

		});
		reqLoader.requestData();
	}
	
	private Handler mHandle = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			CoachItem data = (CoachItem) msg.getData().getSerializable(CoachListAdapter.BUNDLE_KEY_COACH_LIST);
			switch(msg.what) {
				case GolfersAdapter.GOLFERS_INDEX_ITEM:
					onGolfersItemClicked(data);
					break;
			}
			return false;
		}
		
	});

	private void onGolfersItemClicked(CoachItem data) {
		Utils.logh(TAG, "onCoachItemClicked ");
		
		CoachInfoDetailActivity.startCoachInfoDetail(this, data);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}


	@Override
	public void onSexSelect(int newSex) {
		
		//filterChgLoad(Const.INVALID_SELECTION_INT, null, null, newSex);
		sexTv.setText(gd.getSexName(newSex));
		reqData.sex = newSex;
		initListDataAsync(reqData);
	}


	@Override
	public void onCoacherTypeSelect(int newType) {
		// TODO Auto-generated method stub
		
		typeTv.setText(gd.getCoachTypeName(newType));
		reqData.type = newType;
		
		initListDataAsync(reqData);
	}

	@Override
	public void onCoacherSortItemSelect(int newSortItem) {
		// TODO Auto-generated method stub
		
		orderTv.setText(gd.getCoachSortItemName(newSortItem));
		reqData.rangeBy = newSortItem;
		initListDataAsync(reqData);
	}
	
}
