package com.hylg.igolf.ui.golfers;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.data.GolferInfo;
import com.hylg.igolf.cs.loader.GetGolfersListLoader;
import com.hylg.igolf.cs.loader.GetGolfersListLoader.GetGolfersListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.common.IndustrySelectFragment;
import com.hylg.igolf.ui.common.IndustrySelectFragment.onIndustrySelectListener;
import com.hylg.igolf.ui.common.LabelSelectFragment;
import com.hylg.igolf.ui.common.LabelSelectFragment.onLabelSelectListener;
import com.hylg.igolf.ui.common.RegionSelectFragment;
import com.hylg.igolf.ui.common.RegionSelectFragment.onRegionSelectListener;
import com.hylg.igolf.ui.common.SexSelectFragment;
import com.hylg.igolf.ui.common.SexSelectFragment.onSexSelectListener;
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

public class GolfersHomeFrgNew extends Fragment
									implements View.OnClickListener,
										onSexSelectListener, onIndustrySelectListener,
										onLabelSelectListener, onRegionSelectListener {
	private static final String TAG = "GolfersHomeFrgNew";
	private GetGolfersReqParam reqData;
	private final static String BUNDLE_REQ_DATA = "golfers_req_data";
	private EhecdListview listView;
	private ListFooter listFooter;
	private TextView labelTv, regionTv, industryTv, sexTv;
	private GetGolfersListLoader reqLoader = null;
	private GolfersAdapter golfersAdapter;
	private View searchBar, flowBar;
	private EditText searchEt;
	private GlobalData gd;
	private Customer customer;
	private LoadFail loadFail;
	private static OnClearSetupListener clearSetupListener = null;

	private static GolfersHomeFrgNew hallFrg = null;


	private Activity mContext = null;
	
//	public static void startGolfersList(Context context, GetGolfersReqParam data) {
//		Intent intent = new Intent();
//		intent.setClass(context, GolfersHomeFrgNew.class);
//		intent.putExtra(BUNDLE_REQ_DATA, data);
//		context.startActivity(intent);
//	}

	public static GolfersHomeFrgNew getInstance() {
		if(null == hallFrg) {
			hallFrg = new GolfersHomeFrgNew();
		}

		return hallFrg;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DebugTools.getDebug().debug_v(TAG,"----onCreateView");

		mContext = getActivity();
		gd = MainApp.getInstance().getGlobalData();
		customer = MainApp.getInstance().getCustomer();
		View view = inflater.inflate(R.layout.golfers_ac_list, container, false);
		//setContentView(R.layout.golfers_ac_list);
		//reqData = (GetGolfersReqParam) getIntent().getExtras().getSerializable(BUNDLE_REQ_DATA);

		reqData = new GetGolfersReqParam();

		reqData.industry = "INDUSTRY_00";
		reqData.region = customer.state;
		reqData.sex = -1;
		reqData.sn = customer.sn;
		reqData.pageNum = gd.startPage;
		reqData.pageSize = gd.pageSize;

		Utils.logh(TAG, "reqData{ " + reqData.log() + "}");
		getViews(reqData, view);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		DebugTools.getDebug().debug_v(TAG, ">>>>>>>onViewCreated");

		if(null != golfersAdapter) {

			listView.setAdapter(golfersAdapter);
			//Utils.logh(TAG, "exist myInviteAdapter " + myInviteAdapter);

		} else {

			initListDataAsync(reqData, null);
		}

		labelTv.setText(gd.getLabelName(reqData.label));
		regionTv.setText(gd.getRegionName(reqData.region));
		industryTv.setText(gd.getIndustryName(reqData.industry));
		sexTv.setText(gd.getSexName(reqData.sex));

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(null != reqLoader) {
			reqLoader.stopTask(true);
			
			reqLoader = null;
		}
	}
	
	public static void setOnClearSetupListener(OnClearSetupListener listener) {
		clearSetupListener = listener;
	}

	private void getViews(GetGolfersReqParam data,View view) {
		view.findViewById(R.id.golfers_list_topbar_back).setOnClickListener(this);
		view.findViewById(R.id.golfers_list_topbar_search).setOnClickListener(this);
		view.findViewById(R.id.golfers_list_filter_label).setOnClickListener(this);
		labelTv = (TextView) view.findViewById(R.id.golfers_list_filter_label_content);
		labelTv.setText(gd.getLabelName(data.label));
		view.findViewById(R.id.golfers_list_filter_region).setOnClickListener(this);
		regionTv = (TextView) view.findViewById(R.id.golfers_list_filter_region_content);
		regionTv.setText(gd.getRegionName(data.region));

		view.findViewById(R.id.golfers_list_filter_industry).setOnClickListener(this);
		industryTv = (TextView) view.findViewById(R.id.golfers_list_filter_industry_content);

		String industryStr = gd.getIndustryName(data.industry);
		industryTv.setText(industryStr);
		view.findViewById(R.id.golfers_list_filter_sex).setOnClickListener(this);
		sexTv = (TextView) view.findViewById(R.id.golfers_list_filter_sex_content);
		sexTv.setText(gd.getSexName(data.sex));
		
		loadFail = new LoadFail(mContext, (RelativeLayout) view.findViewById(R.id.golfers_list_load_fail));
		loadFail.setOnRetryClickListener(retryListener);

		listView = (EhecdListview) view.findViewById(R.id.golfers_listview);
		//listFooter = new ListFooter(mContext);
		//listView.addFooterView(listFooter.getFooterView());
		listView.setOnRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);
		listView.setShowFootBottom(true);
		
		// Add flow buttons layout, and buttons click listener
		flowBar = View.inflate(mContext, R.layout.golfers_ac_search_bar, null);
		mContext.addContentView(flowBar, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		searchBar = flowBar.findViewById(R.id.golfers_list_search_bar);
		searchBar.findViewById(R.id.golfers_list_search_bar_cancel).setOnClickListener(this);
		searchEt = (EditText) searchBar.findViewById(R.id.golfers_list_search_box_input);
		searchEt.setOnEditorActionListener(mOnEditorActionListener);
		flowBar.findViewById(R.id.golfers_list_search_box_cancel).setOnClickListener(this);
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
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
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
				InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
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
//			Toast.makeText(GolfersListActivity.mContext, ("......" + event.getKeyCode() + "  " + event.getAction()), Toast.LENGTH_SHORT).show();
//			Utils.logh(TAG, "......" + event.getKeyCode() + "  " + event.getAction());
//			if( event != null && event.getAction()  == KeyEvent.ACTION_DOWN) {
//				Toast.makeText(GolfersListActivity.mContext, ("======" + event.getKeyCode() + "  " + event.getAction()), Toast.LENGTH_SHORT).show();
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
			Toast.makeText(mContext, R.string.str_toast_keyword_null, Toast.LENGTH_SHORT).show();
		} else if(!Utils.isConnected(mContext)) {
			;
		} else {
			dismissSearchBar();
			searchByKeyword(Utils.getEditTextString(searchEt));
		}
	}
	
	private void searchByKeyword(String keyword) {
		searchEt.setText("");
		GolfersSearchResultActivity.startGolfersSearchResult(mContext, keyword);
		mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(KeyEvent.KEYCODE_BACK == keyCode) {
//			if(flowBar.getVisibility() == View.VISIBLE) {
//				searchEt.setText("");
//				dismissSearchBar();
//				return true;
//			}
//			finishWithAnim();
//			return true;
//		}
//		return false;
//	}
	
	private void finishWithAnim() {
		if(null != clearSetupListener) {
			clearSetupListener.clearSetup();
		}
		mContext.finish();
		mContext.overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
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
//			if(listFooter.getStatus() == ListFooter.STATUS_LAST) {
//				Utils.logh(TAG, "last");
//				return ;
//			}
//			if(listFooter.getStatus() == ListFooter.STATUS_LOADING || isLoading()) {
//				Utils.logh(TAG, "loading");
//				return ;
//			}
			reqData.pageNum = golfersAdapter.getCount() / reqData.pageSize + 1;
			appendListDataAsync(reqData);
		}
	};
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.golfers_list_topbar_back:
				finishWithAnim();
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
		}
	}

	private final static int FILTER_TYPE_LABEL = 1;
	private final static int FILTER_TYPE_REGION = 2;
	private final static int FILTER_TYPE_INDUSTRY = 3;
	private final static int FILTER_TYPE_SEX = 4;
	private void changeFilter(int type) {
		if(isLoading()) {
			Toast.makeText(mContext, R.string.str_toast_loading, Toast.LENGTH_SHORT).show();
			return;
		}

		getChildFragmentManager().popBackStack();

		switch(type) {
			case FILTER_TYPE_LABEL:
				labelTv.setSelected(true);
				regionTv.setSelected(false);
				industryTv.setSelected(false);
				sexTv.setSelected(false);
				//LabelSelectActivity.startLabelSelect(this,reqData.label);
				LabelSelectFragment.startLabelSelect(this,reqData.label,R.id.golfers_list_container);
				break;
			case FILTER_TYPE_REGION:
				labelTv.setSelected(false);
				regionTv.setSelected(true);
				industryTv.setSelected(false);
				sexTv.setSelected(false);
				//RegionSelectActivity.startRegionSelect(this, RegionSelectActivity.REGION_TYPE_FILTER_ALL, reqData.region);

				RegionSelectFragment.startRegionSelect(this,reqData.region,R.id.golfers_list_container);
				break;
			case FILTER_TYPE_INDUSTRY:
				labelTv.setSelected(false);
				regionTv.setSelected(false);
				industryTv.setSelected(true);
				sexTv.setSelected(false);
				if(LabelAdapter.isLabelIndustry(reqData.label)) {
					Toast.makeText(mContext, R.string.str_golfers_toast_label_industry, Toast.LENGTH_SHORT).show();
				} else {
					//IndustrySelectActivity.startIndustrySelect(this, true, reqData.industry);
					IndustrySelectFragment.startIndustrySelect(this,reqData.industry,R.id.golfers_list_container);
				}
				break;
			case FILTER_TYPE_SEX:
				labelTv.setSelected(false);
				regionTv.setSelected(false);
				industryTv.setSelected(false);
				sexTv.setSelected(true);
				//SexSelectActivity.startSexSelect(this, true, reqData.sex);

				SexSelectFragment.startSexSelect(this,reqData.sex,R.id.golfers_list_container);
				break;
		}
	}
	
	private void filterChgLoad(final int label, final String region, final String industry, final int sex) {
		if(!Utils.isConnected(mContext)) {
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
							SharedPref.setString(SharedPref.PREFS_KEY_GOLFER_DEF_INDUSTRY, myIndustry, mContext);
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
						SharedPref.setString(SharedPref.PREFS_KEY_GOLFER_DEF_REGION, region, mContext);
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
						SharedPref.setString(SharedPref.PREFS_KEY_GOLFER_DEF_INDUSTRY, industry, mContext);
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
						SharedPref.setInt(SharedPref.PREFS_KEY_GOLFER_DEF_SEX, sex, mContext);
					}
					sexTv.setText(gd.getSexName(sex));
				}
			});
			return;
		}
	}


	private interface ChangeCallback {
		public abstract void callBack();
	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetGolfersListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}
	
	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}

	/**
	 *
	 * 			true: do init the first time, or fail retry.
	 * 			false: init by change the filter condition.
	 */
	private void initListDataAsync(final GetGolfersReqParam data, final ChangeCallback callback) {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetGolfersListLoader(mContext, data, new GetGolfersListCallback() {
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
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				} else if(BaseRequest.REQ_RET_OK == retId) {
					loadFail.displayNone();
					initListView(golfersList);
					//listFooter.refreshFooterView(golfersList.size(), data.pageSize);
					if(null != callback) {
						callback.callBack();
					}
				} else {
					loadFail.displayFail(msg);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
				WaitDialog.dismissWaitDialog();
			}
		});
		reqLoader.requestData();
	}
	
	private void initListView(ArrayList<GolferInfo> golfersList) {
		if(null == golfersAdapter) {
			golfersAdapter = new GolfersAdapter(mContext, mHandle, GolfersAdapter.BUNDLE_KEY_GOLFERS_LIST, golfersList);
			listView.setAdapter(golfersAdapter);
		} else {
			golfersAdapter.refreshListInfo(golfersList);
		}
	}

	private void refreshDataAysnc(final GetGolfersReqParam data) {
		if(!Utils.isConnected(mContext)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetGolfersListLoader(mContext, data, new GetGolfersListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<GolferInfo> golfersList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {
					golfersAdapter.refreshListInfo(golfersList);
					//listFooter.refreshFooterView(golfersList.size(), data.pageSize);
				} else {
					// do not change previous data if fail, just toast fail message.
//					if(BaseRequest.REQ_RET_F_NO_DATA == retId) { }
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	private void appendListDataAsync(final GetGolfersReqParam data) {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		//listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetGolfersListLoader(mContext, data, new GetGolfersListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<GolferInfo> golfersList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || golfersList.size() == 0) {
					//listFooter.displayLast();
					Toast.makeText(mContext, R.string.str_golfers_li_no_more, Toast.LENGTH_SHORT).show();
				} else if(BaseRequest.REQ_RET_OK == retId) {
					golfersAdapter.appendListInfo(golfersList);
					//listFooter.refreshFooterView(golfersList.size(), data.pageSize);
				} else {
					//listFooter.displayMore();
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
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
		MemDetailActivityNew.startMemDetailActivity(mContext, data.sn);
		mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	private void onGolfersInviteClicked(GolferInfo data) {
		Utils.logh(TAG, "onGolfersInviteClicked ");
		StartInviteStsActivity.startOpenInvite(mContext, data.sn);
		mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	private void onGolfersAvatarClicked(GolferInfo data) {
		MemDetailActivityNew.startMemDetailActivity(mContext, data.sn);
		mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	@Override
	public void onSexSelect(Object newSex) {

		filterChgLoad(Const.INVALID_SELECTION_INT, null, null, Integer.valueOf(newSex.toString()));
	}

	@Override
	public void onIndustrySelect(Object newIndustry) {

		DebugTools.getDebug().debug_v(TAG,"newIndustry----->>>"+newIndustry);
		filterChgLoad(Const.INVALID_SELECTION_INT, null, newIndustry.toString(), Const.INVALID_SELECTION_INT);
	}

	@Override
	public void onLabelSelect(Object newLabel) {
		filterChgLoad(Integer.valueOf(newLabel.toString()), null, null, Const.INVALID_SELECTION_INT);
	}

	@Override
	public void onRegionSelect(String newRegion) {
		filterChgLoad(Const.INVALID_SELECTION_INT, newRegion, null, Const.INVALID_SELECTION_INT);
	}
	
	public interface OnClearSetupListener {
		void clearSetup();
	}

}
