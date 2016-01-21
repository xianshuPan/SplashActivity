package com.hylg.igolf.ui.rank;

import java.util.ArrayList;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.RankingInfo;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.GetRankingListLoader;
import com.hylg.igolf.cs.loader.AsyncImageLoader.ImageCallback;
import com.hylg.igolf.cs.loader.GetRankingListLoader.GetRankingListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.common.RegionAndSexSelectFragment;
import com.hylg.igolf.ui.common.RegionAndSexSelectFragment.onRegionAndSexSelectListener;
import com.hylg.igolf.ui.common.SexSelectActivity;
import com.hylg.igolf.ui.member.MemDetailActivityNew;
import com.hylg.igolf.ui.reqparam.GetRankingReqParam;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.PullListView;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.view.PullListView.OnLoadMoreListener;
import com.hylg.igolf.ui.view.PullListView.OnRefreshListener;
import com.hylg.igolf.ui.view.ShareMenuRank;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class RankHomeFrg extends Fragment
						implements View.OnClickListener,onRegionAndSexSelectListener
							 {
	private static final String TAG = "RankHomeFrg";
	private static RankHomeFrg rankFrg = null;
	private LinearLayout ownInfo;
	private TextView ownInfoHintTv;
	private LinearLayout ownInfoLl;
	private TextView ownNumTv;
	private ImageView ownNumIv;
	private ImageView ownAvatar;
	private TextView ownNickname;
	private TextView ownHi;
	private TextView ownMatches;
	private TextView ownBest;
	private LinearLayout listDataLL;
	private LoadFail loadFail;
	private TextView regionTv, sexTv;
	private final static String BUNDLE_RANKING_REQ_DATA = "ranking_req_data";
	private final static int TYPE_NORMAL = 0;
	private final static int TYPE_OWN = 1;
	private GetRankingReqParam reqData;
	private GetRankingListLoader reqLoader = null;
	private PullListView listView;
	private ListFooter listFooter;
	private RankAdapter rankAdapter = null;
	private GlobalData gd;
	private RankingInfo myRank;
	private String outOfRankMsg;
	
	public static RankHomeFrg getInstance(GetRankingReqParam data) {
		if(null == rankFrg) {
			rankFrg = new RankHomeFrg();
		}
		Bundle b = new Bundle();
		b.putSerializable(BUNDLE_RANKING_REQ_DATA, data);
		rankFrg.setArguments(b);
		return rankFrg;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		DebugTools.getDebug().debug_v(TAG,"onCreate...");
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		reqData = (GetRankingReqParam) args.getSerializable(BUNDLE_RANKING_REQ_DATA);
		reqData.type = TYPE_NORMAL;
		listFooter = new ListFooter(getActivity());

		Utils.logh(TAG, "onCreate reqData: " + reqData.log());
		gd = MainApp.getInstance().getGlobalData();
		outOfRankMsg = "";
		myRank = null;
	}
	
	
	@Override
	public void onDestroy() {
		DebugTools.getDebug().debug_v(TAG, "onDestroy...");
		super.onDestroy();
		// 切换帐号时，需重新查询，清空数据
		rankAdapter = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		DebugTools.getDebug().debug_v(TAG, "onCreateView...");
		View view = inflater.inflate(R.layout.rank_frg_home, container, false);
		view.findViewById(R.id.rank_home_filter_region).setOnClickListener(this);
		regionTv = (TextView) view.findViewById(R.id.rank_home_filter_region_content_text);
		regionTv.setOnClickListener(this);
		sexTv = (TextView) view.findViewById(R.id.rank_home_filter_sex_content);
		ownInfo = (LinearLayout) view.findViewById(R.id.rank_own_info);
		ownInfoHintTv = (TextView) ownInfo.findViewById(R.id.rank_own_hint);
		ownInfoLl = (LinearLayout) ownInfo.findViewById(R.id.rank_own_ll);
		ownNumTv = (TextView) ownInfo.findViewById(R.id.rank_own_rank_num);
		ownNumIv = (ImageView) ownInfo.findViewById(R.id.rank_own_rank_img);
		ownAvatar = (ImageView) ownInfo.findViewById(R.id.rank_own_golfer_avatar);
		ownNickname = (TextView) ownInfo.findViewById(R.id.rank_own_golfer_nickname);
		ownHi = (TextView) ownInfo.findViewById(R.id.rank_own_handicapi);
		ownMatches = (TextView) ownInfo.findViewById(R.id.rank_own_matches);
		ownBest = (TextView) ownInfo.findViewById(R.id.rank_own_best);
		listDataLL = (LinearLayout) view.findViewById(R.id.rank_list_data_ll);
		listView = (PullListView) view.findViewById(R.id.rank_home_listview);

		loadFail = new LoadFail(getActivity(),(RelativeLayout) view.findViewById(R.id.rank_load_fail));
		loadFail.setOnRetryClickListener(retryListener);

		view.findViewById(R.id.rank_home_share_image).setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		DebugTools.getDebug().debug_v(TAG, "onViewCreated...");
		Utils.logh(TAG, "onViewCreated " + gd.getRegionName(reqData.region) + gd.getSexName(reqData.sex));
		//regionTv.setText(gd.getRegionName(reqData.region));
		sexTv.setText(gd.getSexName(reqData.sex));
		//listDataLL.addView(loadFail.getLoadFailView(), 0);
		listView.addFooterView(listFooter.getFooterView());
		listView.setonRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);


		if(null != rankAdapter) {
			listView.setAdapter(rankAdapter);
			Utils.logh(TAG, "exist rankAdapter " + rankAdapter);
			refreshOwnInfo(myRank, outOfRankMsg);
			setListViewHeightBasedOnChildren(listView);
		} else {
			Utils.logh(TAG, "note exist rankAdapter");
			initDataAysnc(reqData, null);
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();

		DebugTools.getDebug().debug_v(TAG, "onDestroyView...");
		listDataLL.removeAllViews();
		if(null != reqLoader) {
			reqLoader.stopTask(true);
			reqLoader = null;
		}
	}

	private OnRefreshListener pullRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
			reqData.type = TYPE_NORMAL;
			refreshDataAysnc(reqData);
		}
	};
	
	private onRetryClickListener retryListener = new onRetryClickListener() {
		@Override
		public void onRetryClick() {
			Utils.logh(TAG, "onRetryClick ... ");
			rankAdapter = null;
			initDataAysnc(reqData, null);
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
			reqData.pageNum = rankAdapter.getCount() / reqData.pageSize + 1;
			appendListDataAsync(reqData);
		}
	};

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.rank_home_filter_region_content_text:
				if(isLoading()) {
					Toast.makeText(getActivity(), R.string.str_toast_loading, Toast.LENGTH_SHORT).show();
					return;
				}
				getChildFragmentManager().popBackStack();
				RegionAndSexSelectFragment.startRegionSelect(RankHomeFrg.this, reqData.region, reqData.sex,R.id.rank_frg_container);
				break;
			case R.id.rank_home_filter_sex:
				if(isLoading()) {
					Toast.makeText(getActivity(), R.string.str_toast_loading, Toast.LENGTH_SHORT).show();
					return;
				}
				SexSelectActivity.startSexSelect(RankHomeFrg.this, true, reqData.sex);
				break;
			case R.id.rank_home_share_image:
//				Toast.makeText(getActivity(), R.string.str_rank_intro_hint, Toast.LENGTH_LONG).show();
				//showDesPopWin();

				ShareMenuRank share = new ShareMenuRank(getActivity(),listView,reqData.region,reqData.sex);
				share.showPopupWindow();
				break;
		}		
	}

//	private PopupWindow mDesPopWin;
//	private void showDesPopWin() {
//		if(null != mDesPopWin && mDesPopWin.isShowing()) {
//			return ;
//		}
//		LinearLayout sv = (LinearLayout) LayoutInflater.from(getActivity())
//				.inflate(R.layout.rank_hint_popwin, null);
//		mDesPopWin = new PopupWindow(sv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
//		mDesPopWin.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
//		mDesPopWin.setAnimationStyle(android.R.style.Animation_Dialog);
//		sv.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dismissExchgPopwin();
//			}
//		});
//		mDesPopWin.setOnDismissListener(new OnDismissListener() {
//			@Override
//			public void onDismiss() {
//				if(null != mDesPopWin) {
//					Utils.logh(TAG, "onDismiss ");
//					mDesPopWin = null;
//				}
//			}
//		});
//		mDesPopWin.showAtLocation(sv, Gravity.CENTER, 0, 0);
//	}
//
//	private void dismissExchgPopwin() {
//		if(null != mDesPopWin && mDesPopWin.isShowing()) {
//			mDesPopWin.dismiss();
////			mDesPopWin = null;
//		}
//	}
	
	private void clearLoader() {
		if(isLoading()) {
			GetRankingListLoader loader = reqLoader;
			loader.stopTask(true);
			Utils.logh(TAG, "reqLoader: " + reqLoader);
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
	private void initDataAysnc(final GetRankingReqParam data, final ChangeCallback callback) {
		if(!Utils.isConnected(getActivity())) {
			return ;
		}
		WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetRankingListLoader(getActivity(), data, new GetRankingListCallback() {
			@Override
			public void callBack(int retId, String msg, RankingInfo myRank, ArrayList<RankingInfo> rankingList) {
				listView.onRefreshComplete();
				switch(retId) {
					case BaseRequest.REQ_RET_F_RANK_OUT_OF:
					case BaseRequest.REQ_RET_F_RANK_NOT_FIT:
//						refreshOwnInfo(myRank, msg);
					case BaseRequest.REQ_RET_OK:
						loadFail.displayNone();
						refreshOwnInfo(myRank, msg); // diffrent by myRank in method
						initListView(rankingList);
						listFooter.refreshFooterView(rankingList.size(), data.pageSize);
						if(null != callback) {
							callback.callBack();
						}

						setListViewHeightBasedOnChildren(listView);

						break;
					case BaseRequest.REQ_RET_F_NO_DATA:
						Utils.setGone(ownInfo);
						loadFail.displayNoData(msg);
						if(null != callback) {
							callback.callBack();
						}
						// 开始有数据，更改条件返回，无数据，清空adapter，
						// 避免切换导航栏，再回来时，填充旧adapter中的数据
						if(null != rankAdapter) {
							rankAdapter = null;
						}
						refreshOwnInfo(null, null);
						break;
					default: // normal fail
						refreshOwnInfo(null, null);
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

	protected void refreshOwnInfo(RankingInfo own, String msg) {
		if(null == own) {
			if(null == msg || msg.isEmpty()) {
				Utils.setGone(ownInfo);
				outOfRankMsg = null;
			} else {
				Utils.setVisible(ownInfo);
				Utils.setVisibleGone(ownInfoHintTv, ownInfoLl);
				ownInfoHintTv.setText(msg);
				outOfRankMsg = msg;
			}
			myRank = null;
		} else {
			Utils.logh(TAG, "refreshOwnInfo " + own.log());
			Utils.setVisible(ownInfo);
			Utils.setVisibleGone(ownInfoLl, ownInfoHintTv);
			myRank = own;
			ownNumTv.setText(String.valueOf(own.rank));
			int txtColor = getResources().getColor(R.color.color_white);
//			if(rank > 3) {
//				Utils.setVisibleGone(ownNumTv, ownNumIv);
//				ownNumTv.setText(String.valueOf(rank));
//			} else {
//				Utils.setVisibleGone(ownNumIv, ownNumTv);
//				switch(rank) {
//					case 1:
//						ownNumIv.setImageResource(R.drawable.rank_gold);
//						txtColor = getResources().getColor(R.color.color_rank_gold);
//						break;
//					case 2:
//						ownNumIv.setImageResource(R.drawable.rank_silver);
//						txtColor = getResources().getColor(R.color.color_rank_silver);
//						break;
//					case 3:
//						ownNumIv.setImageResource(R.drawable.rank_bronze);
//						txtColor = getResources().getColor(R.color.color_rank_bronze);
//						break;
//				}
//			}
			Drawable avatar = AsyncImageLoader.getInstance().getAvatar(getActivity(), own.sn, own.avatar, 
					(int) getResources().getDimension(R.dimen.avatar_rank_li_size));
			if(null != avatar) {
				ownAvatar.setImageDrawable(avatar);
			} else {
				ownAvatar.setImageResource(R.drawable.avatar_loading);
				AsyncImageLoader.getInstance().loadAvatar(getActivity(), own.sn, own.avatar,
						new ImageCallback() {
							@Override
							public void imageLoaded(Drawable imageDrawable) {
								if(null != imageDrawable && null != ownAvatar) {
									ownAvatar.setImageDrawable(imageDrawable);
								}
							}
					});
			}
			ownNickname.setText(own.nickname);
			ownNickname.setTextColor(txtColor);
			ownHi.setText(Utils.getDoubleString(getActivity(), own.handicapIndex));
			ownHi.setTextColor(txtColor);
			ownMatches.setText(String.valueOf(own.matches));
			ownMatches.setTextColor(txtColor);
			ownBest.setText(Utils.getIntString(getActivity(), own.best));
			ownBest.setTextColor(txtColor);
			ownInfoLl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isLoading()) {
						Toast.makeText(getActivity(), R.string.str_toast_loading, Toast.LENGTH_SHORT).show();
						return;
					}
					Utils.logh(TAG, "own place clicked !!! ");
					reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
					reqData.type = TYPE_OWN;
					initDataAysnc(reqData, null);					
				}
			});
		}
	}

	private void initListView(ArrayList<RankingInfo> rankingList) {
		if(null == rankAdapter) {
			rankAdapter = new RankAdapter(rankingList);			
			listView.setAdapter(rankAdapter);
		} else {
			rankAdapter.refreshListInfo(rankingList);
		}
		Utils.logh(TAG, "initListView new rankAdapter: " + rankAdapter);
	}

	private void refreshDataAysnc(final GetRankingReqParam data) {
		if(!Utils.isConnected(getActivity())) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetRankingListLoader(getActivity(), data, new GetRankingListCallback() {
			@Override
			public void callBack(int retId, String msg, RankingInfo myRank, ArrayList<RankingInfo> rankingList) {
				listView.onRefreshComplete();
				Utils.logh(TAG, "retId: " + retId + " rankingList: " + rankingList.size());
				switch(retId) {
					case BaseRequest.REQ_RET_F_RANK_OUT_OF:
					case BaseRequest.REQ_RET_F_RANK_NOT_FIT:
//						refreshOwnInfo(myRank, msg);
					case BaseRequest.REQ_RET_OK:
						loadFail.displayNone();
						refreshOwnInfo(myRank, msg); // diffrent by myRank in method
						rankAdapter.refreshListInfo(rankingList);
						listFooter.refreshFooterView(rankingList.size(), data.pageSize);

						setListViewHeightBasedOnChildren(listView);
						break;
					default: // normal fail
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
						break;
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private void appendListDataAsync(final GetRankingReqParam data) {
		if(!Utils.isConnected(getActivity())) {
			return ;
		}
		listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetRankingListLoader(getActivity(), data, new GetRankingListCallback() {
			@Override
			public void callBack(int retId, String msg, RankingInfo myRank, ArrayList<RankingInfo> rankingList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {
					rankAdapter.appendListInfo(rankingList);
					listFooter.refreshFooterView(rankingList.size(), data.pageSize);

					setListViewHeightBasedOnChildren(listView);
				} else {
					if(BaseRequest.REQ_RET_F_NO_DATA == retId) {
						listFooter.displayLast();
					} else {
						listFooter.displayMore();
					}
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	public void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			// listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			// 计算子项View 的宽高
			listItem.measure(0, 0);
			// 统计所有子项的总高度
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	private void startMemDetail(String memSn) {
		MemDetailActivityNew.startMemDetailActivity(getActivity(), memSn,1);
	}

	private class RankAdapter extends IgBaseAdapter {
		private ArrayList<RankingInfo> list;
		
		public RankAdapter(ArrayList<RankingInfo> list) {
			this.list = list;
		}

		public void refreshListInfo(ArrayList<RankingInfo> list) {
			this.list.clear();
			this.list = list;
			notifyDataSetChanged();
		}

		public void appendListInfo(ArrayList<RankingInfo> list) {
			for(int i=0, size=list.size(); i<size; i++) {
				this.list.add(list.get(i));
			}
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHodler holder;
			if(null == convertView) {
				convertView = View.inflate(getActivity(), R.layout.rank_list_item, null);
				holder = new ViewHodler();
				holder.rankIv = (ImageView) convertView.findViewById(R.id.rank_li_rank_img);
				holder.rankTv = (TextView) convertView.findViewById(R.id.rank_li_rank_num);
				holder.avatarIv = (ImageView) convertView.findViewById(R.id.rank_li_golfer_avatar);
				holder.nicknameTv = (TextView) convertView.findViewById(R.id.rank_li_golfer_nickname);
				holder.handicapiTv = (TextView) convertView.findViewById(R.id.rank_li_handicapi);
				holder.matchesTv = (TextView) convertView.findViewById(R.id.rank_li_matches);
				holder.bestTv = (TextView) convertView.findViewById(R.id.rank_li_best);
				convertView.setTag(holder);
			} else {
				holder = (ViewHodler) convertView.getTag();
			}

			//holder.avatarIv.setEnabled(false);
			RankingInfo data = list.get(position);
			int txtColor = getResources().getColor(R.color.color_rank_def);

			holder.rankTv.setText(String.valueOf(data.rank));
			if(data.rank > 3) {
//				Utils.setVisibleGone(holder.rankTv, holder.rankIv);
			} else {
				//Utils.setVisibleGone(holder.rankIv, holder.rankTv);
				switch(data.rank) {
					case 1:
						//holder.rankIv.setImageResource(R.drawable.rank_gold);
						txtColor = getResources().getColor(R.color.color_rank_gold);
						break;
					case 2:
						//holder.rankIv.setImageResource(R.drawable.rank_silver);
						txtColor = getResources().getColor(R.color.color_rank_silver);
						break;
					case 3:
						//holder.rankIv.setImageResource(R.drawable.rank_bronze);
						txtColor = getResources().getColor(R.color.color_rank_bronze);
						break;
				}

				holder.rankTv.setTextColor(txtColor);
			}
			loadAvatar(getActivity(), data.sn, data.avatar, holder.avatarIv);
			holder.nicknameTv.setText(data.nickname);
			holder.nicknameTv.setTextColor(txtColor);
			holder.handicapiTv.setText(Utils.getDoubleString(getActivity(), data.handicapIndex));
			holder.handicapiTv.setTextColor(txtColor);
			holder.matchesTv.setText(String.valueOf(data.matches));
			holder.matchesTv.setTextColor(txtColor);
			holder.bestTv.setText(Utils.getIntString(getActivity(), data.best));
			holder.bestTv.setTextColor(txtColor);
//			if(position % 2 == 0) {
//				convertView.setBackgroundResource(R.drawable.list_item_even_bkg);
//			} else {
//				convertView.setBackgroundResource(R.drawable.list_item_odd_bkg);
//			}
			convertView.setOnClickListener(new OnItemChildClickListener(position));
			return convertView;
		}

		private class OnItemChildClickListener implements View.OnClickListener {
			private int position;
			
			public OnItemChildClickListener(int pos) {
				position = pos;
			}
			
			@Override
			public void onClick(View v) {
				String memSn = list.get(position).sn;
				// 自己不跳转
				if(memSn.endsWith(MainApp.getInstance().getCustomer().sn)) {
					return ;
				}
				startMemDetail(memSn);
			}
		}
		
		private class ViewHodler {
			protected ImageView rankIv;
			protected TextView rankTv;
			protected ImageView avatarIv;
			protected TextView nicknameTv;
			protected TextView handicapiTv;
			protected TextView matchesTv;
			protected TextView bestTv;
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
//
//	@Override
//	public void onSexSelect(final int newSex) {
//		reqData.sex = newSex;
//		initDataAysnc(reqData, new ChangeCallback() {
//			@Override
//			public void callBack() {
//				sexTv.setText(gd.getSexName(newSex));
//			}
//		});
//	}
//
//	@Override
//	public void onRegionSelect(String newRegion) {
//		reqData.region = newRegion;
//		initDataAysnc(reqData, new ChangeCallback() {
//			@Override
//			public void callBack() {
//				regionTv.setText(gd.getRegionName(reqData.region));
//			}
//		});
//	}

	private interface ChangeCallback {
		void callBack();
	}

	@Override
	public void onRegionAndSexSelect(String newRegion, int newSex) {

		reqData.sex = newSex;
		reqData.region = newRegion;
		initDataAysnc(reqData, null);
	}
}
