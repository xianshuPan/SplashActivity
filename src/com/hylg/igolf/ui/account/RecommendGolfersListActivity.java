package com.hylg.igolf.ui.account;

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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.GolferInfo;
import com.hylg.igolf.cs.loader.GetRecommandGolfersListLoader;
import com.hylg.igolf.cs.loader.GetRecommandGolfersListLoader.GetRecommandGolfersListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.MainActivity;
import com.hylg.igolf.ui.common.IndustrySelectActivity;
import com.hylg.igolf.ui.common.IndustrySelectActivity.onIndustrySelectListener;
import com.hylg.igolf.ui.common.LabelSelectActivity;
import com.hylg.igolf.ui.common.LabelSelectActivity.onLabelSelectListener;
import com.hylg.igolf.ui.common.RegionSelectActivity;
import com.hylg.igolf.ui.common.RegionSelectActivity.onRegionSelectListener;
import com.hylg.igolf.ui.common.SexSelectActivity;
import com.hylg.igolf.ui.common.SexSelectActivity.onSexSelectListener;
import com.hylg.igolf.ui.golfers.GolfersHomeFrg;
import com.hylg.igolf.ui.golfers.GolfersSearchResultActivity;
import com.hylg.igolf.ui.golfers.adapter.GolfersAdapter;
import com.hylg.igolf.ui.golfers.adapter.LabelAdapter;
import com.hylg.igolf.ui.hall.StartInviteStsActivity;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.reqparam.GetGolfersReqParam;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.EhecdListview.OnLoadMoreListener;
import com.hylg.igolf.ui.view.EhecdListview.OnRefreshListener;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

public class RecommendGolfersListActivity extends Activity
									implements View.OnClickListener,
										onSexSelectListener, onIndustrySelectListener,
										onLabelSelectListener, onRegionSelectListener {
	private static final String TAG = "GolfersListActivity";
	private GetGolfersReqParam reqData;
	private final static String BUNDLE_REQ_DATA = "golfers_req_data";
	private EhecdListview listView;
	private ListFooter listFooter;
	private TextView labelTv, regionTv, industryTv, sexTv,overTxt;
	private GetRecommandGolfersListLoader reqLoader = null;
	private GolfersAdapter golfersAdapter;
	private View searchBar, flowBar;
	private EditText searchEt;
	private GlobalData gd;
	private LoadFail loadFail;
	private static OnClearSetupListener clearSetupListener = null;
	
	public static void startRecommendGolfersListActivity(Context context, GetGolfersReqParam data) {
		Intent intent = new Intent();
		intent.setClass(context, RecommendGolfersListActivity.class);
		intent.putExtra(BUNDLE_REQ_DATA, data);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommand_golfers_ac_list);
		reqData = (GetGolfersReqParam) getIntent().getExtras().getSerializable(BUNDLE_REQ_DATA);
		gd = MainApp.getInstance().getGlobalData();
		Utils.logh(TAG, "reqData{ " + reqData.log() + "}");
		getViews(reqData);
		initListDataAsync(reqData, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(null != reqLoader) {
			reqLoader.stopTask(true);
			
			reqLoader = null;
		}
	}
	
	public static void setOnClearSetupListener(OnClearSetupListener listener) {
		clearSetupListener = listener;
	}

	private void getViews(GetGolfersReqParam data) {
		findViewById(R.id.golfers_list_topbar_back).setOnClickListener(this);
		findViewById(R.id.golfers_list_topbar_search).setOnClickListener(this);
		findViewById(R.id.golfers_list_filter_label).setOnClickListener(this);
		labelTv = (TextView) findViewById(R.id.golfers_list_filter_label_content);
		labelTv.setText(gd.getLabelName(data.label));
		findViewById(R.id.golfers_list_filter_region).setOnClickListener(this);
		regionTv = (TextView) findViewById(R.id.golfers_list_filter_region_content);
		regionTv.setText(gd.getRegionName(data.region));
		findViewById(R.id.golfers_list_filter_industry).setOnClickListener(this);
		industryTv = (TextView) findViewById(R.id.golfers_list_filter_industry_content);
		industryTv.setText(gd.getIndustryName(data.industry));
		findViewById(R.id.golfers_list_filter_sex).setOnClickListener(this);
		sexTv = (TextView) findViewById(R.id.golfers_list_filter_sex_content);
		sexTv.setText(gd.getSexName(data.sex));

		overTxt  = (TextView) findViewById(R.id.recommand_golfers_next_text);
		overTxt.setOnClickListener(this);
		
		loadFail = new LoadFail(this, (RelativeLayout)findViewById(R.id.golfers_list_load_fail));
		loadFail.setOnRetryClickListener(retryListener);
		listView = (EhecdListview) findViewById(R.id.golfers_listview);
		listFooter = new ListFooter(this);
		//listView.addFooterView(listFooter.getFooterView());
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
					imm.hideSoftInputFromWindow(searchEt.getWindowToken(),
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
			golfersAdapter = null;
			reqData.pageNum = gd.startPage;
			initListDataAsync(reqData, null);
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
			Toast.makeText(RecommendGolfersListActivity.this, R.string.str_toast_keyword_null, Toast.LENGTH_SHORT).show();
		} else if(!Utils.isConnected(RecommendGolfersListActivity.this)) {

		} else {
			dismissSearchBar();
			searchByKeyword(Utils.getEditTextString(searchEt));
		}
	}
	
	private void searchByKeyword(String keyword) {
		searchEt.setText("");
		GolfersSearchResultActivity.startGolfersSearchResult(RecommendGolfersListActivity.this, keyword);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
//			if(flowBar.getVisibility() == View.VISIBLE) {
//				searchEt.setText("");
//				dismissSearchBar();
//				return true;
//			}
//			finishWithAnim();

			startMainActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void finishWithAnim() {
		if(null != clearSetupListener) {
			clearSetupListener.clearSetup();
		}
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}
	
	private OnRefreshListener pullRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			reqData.pageNum = gd.startPage;
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
			reqData.pageNum = reqData.pageNum+ 1;
			appendListDataAsync(reqData);
		}
	};

	private void startMainActivity() {
		MainActivity.startMainActivityFromSetup(RecommendGolfersListActivity.this);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.golfers_list_topbar_back:
				//finishWithAnim();

				startMainActivity();
				break;
			case R.id.golfers_list_topbar_search:
				displaySearchBar();
				break;
			case R.id.golfers_list_filter_label:
				changeFilter(FILTER_TYPE_LABEL);
				break;
			case R.id.golfers_list_filter_region:
				changeFilter(FILTER_TYPE_REGION);
				break;
			case R.id.golfers_list_filter_industry:
				changeFilter(FILTER_TYPE_INDUSTRY);
				break;
			case R.id.golfers_list_filter_sex:
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

			case R.id.recommand_golfers_next_text:

				startMainActivity();
				break;
		}
	}

	private final static int FILTER_TYPE_LABEL = 1;
	private final static int FILTER_TYPE_REGION = 2;
	private final static int FILTER_TYPE_INDUSTRY = 3;
	private final static int FILTER_TYPE_SEX = 4;
	private void changeFilter(int type) {
		if(isLoading()) {
			Toast.makeText(this, R.string.str_toast_loading, Toast.LENGTH_SHORT).show();
			return;
		}
		switch(type) {
			case FILTER_TYPE_LABEL:
				LabelSelectActivity.startLabelSelect(RecommendGolfersListActivity.this, reqData.label);
				break;
			case FILTER_TYPE_REGION:
				RegionSelectActivity.startRegionSelect(RecommendGolfersListActivity.this,
						RegionSelectActivity.REGION_TYPE_FILTER_ALL, reqData.region);
				break;
			case FILTER_TYPE_INDUSTRY:
				if(LabelAdapter.isLabelIndustry(reqData.label)) {
					Toast.makeText(RecommendGolfersListActivity.this, R.string.str_golfers_toast_label_industry, Toast.LENGTH_SHORT).show();
				} else {
					IndustrySelectActivity.startIndustrySelect(RecommendGolfersListActivity.this, true, reqData.industry);
				}
				break;
			case FILTER_TYPE_SEX:
				SexSelectActivity.startSexSelect(RecommendGolfersListActivity.this, true, reqData.sex);
				break;
		}
	}
	
	private void filterChgLoad(final int label, final String region, final String industry, final int sex) {
		if(!Utils.isConnected(RecommendGolfersListActivity.this)) {
			return ;
		}
		golfersAdapter = null;
		reqData.pageNum = gd.startPage;
		if(Const.INVALID_SELECTION_INT != label) {
			reqData.label = label;
			initListDataAsync(reqData, new ChangeCallback() {
				@Override
				public void callBack() {
					if(LabelAdapter.isLabelIndustry(label)) {
						String myIndustry = MainApp.getInstance().getCustomer().industry;
						if(GolfersHomeFrg.NOTE_FILTER) {
							SharedPref.setString(SharedPref.PREFS_KEY_GOLFER_DEF_INDUSTRY, myIndustry, RecommendGolfersListActivity.this);
						}
						industryTv.setText(gd.getIndustryName(myIndustry));	
						reqData.industry = myIndustry;
					}
					labelTv.setText(gd.getLabelName(label));
				}
			});
			return ;
		}
		if(null != region) {
			reqData.region = region;
			initListDataAsync(reqData, new ChangeCallback() {
				@Override
				public void callBack() {
					if(GolfersHomeFrg.NOTE_FILTER) {
						// 存储地区选择
						SharedPref.setString(SharedPref.PREFS_KEY_GOLFER_DEF_REGION, region, RecommendGolfersListActivity.this);
					}
					regionTv.setText(gd.getRegionName(region));					
				}
			});
			return;
		}
		if(null != industry) {
			reqData.industry = industry;
			initListDataAsync(reqData, new ChangeCallback() {
				@Override
				public void callBack() {
					if(GolfersHomeFrg.NOTE_FILTER) {
						// 存储行业选择
						SharedPref.setString(SharedPref.PREFS_KEY_GOLFER_DEF_INDUSTRY, industry, RecommendGolfersListActivity.this);
					}
					industryTv.setText(gd.getIndustryName(industry));					
				}
			});
			return;
		}
		if(Const.INVALID_SELECTION_INT != sex) {
			reqData.sex = sex;
			initListDataAsync(reqData, new ChangeCallback() {
				@Override
				public void callBack() {
					if(GolfersHomeFrg.NOTE_FILTER) {
						// 存储性别选择
						SharedPref.setInt(SharedPref.PREFS_KEY_GOLFER_DEF_SEX, sex, RecommendGolfersListActivity.this);
					}
					sexTv.setText(gd.getSexName(sex));
				}
			});
			return;
		}
	}
	
	private interface ChangeCallback {
		void callBack();
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetRecommandGolfersListLoader loader = reqLoader;
			loader.stopTask(true);
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}

	/**
	 * 
	 * @param data
	 * 			true: do init the first time, or fail retry.
	 * 			false: init by change the filter condition.
	 */
	private void initListDataAsync(final GetGolfersReqParam data, final ChangeCallback callback) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetRecommandGolfersListLoader(RecommendGolfersListActivity.this, data, new GetRecommandGolfersListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<GolferInfo> golfersList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || golfersList.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_golfers_req_no_data_hint);
					}
					// display reload page
					loadFail.displayNoDataRetry(msg);
					if(null != callback) {
						callback.callBack();
					}
					Toast.makeText(RecommendGolfersListActivity.this, msg, Toast.LENGTH_SHORT).show();
				} else if(BaseRequest.REQ_RET_OK == retId) {
					loadFail.displayNone();
					initListView(golfersList);
					findViewById(R.id.recommand_golfers_next_relative).setVisibility(View.VISIBLE);
					listFooter.refreshFooterView(golfersList.size(), data.pageSize);
					if(null != callback) {
						callback.callBack();
					}
				} else {
					loadFail.displayFail(msg);
					Toast.makeText(RecommendGolfersListActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
				WaitDialog.dismissWaitDialog();
			}
		});
		reqLoader.requestData();
	}
	
	private void initListView(ArrayList<GolferInfo> golfersList) {
		if(null == golfersAdapter) {
			golfersAdapter = new GolfersAdapter(this, mHandle, GolfersAdapter.BUNDLE_KEY_GOLFERS_LIST, golfersList,true);
			listView.setAdapter(golfersAdapter);
		} else {
			golfersAdapter.refreshListInfo(golfersList);
		}
	}

	private void refreshDataAysnc(final GetGolfersReqParam data) {
		if(!Utils.isConnected(this)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetRecommandGolfersListLoader(RecommendGolfersListActivity.this, data, new GetRecommandGolfersListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<GolferInfo> golfersList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {
					golfersAdapter.refreshListInfo(golfersList);
					listFooter.refreshFooterView(golfersList.size(), data.pageSize);
				} else {
					// do not change previous data if fail, just toast fail message.
//					if(BaseRequest.REQ_RET_F_NO_DATA == retId) { }
					Toast.makeText(RecommendGolfersListActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	private void appendListDataAsync(final GetGolfersReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetRecommandGolfersListLoader(RecommendGolfersListActivity.this, data, new GetRecommandGolfersListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<GolferInfo> golfersList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || golfersList.size() == 0) {
					listFooter.displayLast();
					Toast.makeText(RecommendGolfersListActivity.this, R.string.str_golfers_li_no_more, Toast.LENGTH_SHORT).show();
				} else if(BaseRequest.REQ_RET_OK == retId) {
					golfersAdapter.appendListInfo(golfersList);
					listFooter.refreshFooterView(golfersList.size(), data.pageSize);
				} else {
					listFooter.displayMore();
					Toast.makeText(RecommendGolfersListActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
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

	private void onGolfersItemClicked(GolferInfo data) {
		Utils.logh(TAG, "onGolfersItemClicked ");
		MemDetailActivityNew.startMemDetailActivity(this, data.sn);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
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

	@Override
	public void onSexSelect(int newSex) {
		filterChgLoad(Const.INVALID_SELECTION_INT, null, null, newSex);
	}

	@Override
	public void onIndustrySelect(String newIndustry) {
		filterChgLoad(Const.INVALID_SELECTION_INT, null, newIndustry, Const.INVALID_SELECTION_INT);
	}

	@Override
	public void onLabelSelect(int newLabel) {
		filterChgLoad(newLabel, null, null, Const.INVALID_SELECTION_INT);
	}

	@Override
	public void onRegionSelect(String newRegion) {
		filterChgLoad(Const.INVALID_SELECTION_INT, newRegion, null, Const.INVALID_SELECTION_INT);
	}
	
	public interface OnClearSetupListener {
		void clearSetup();
	}
}
