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
import android.widget.ImageView;
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
import com.hylg.igolf.ui.view.ListviewBottomRefresh;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.ListviewBottomRefresh.OnRefreshListener;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;

/*
* pxs last update 2015.12.28 19:00
* */
public class MemFightRecordFrg extends Fragment {
	private static final String TAG = "MemFightRecordActivity";
	private final static String BUNDLE_MEM_SN = "memSn";
	private final static String BUNDLE_FIGHT_MSG = "fightMsg";
	private LoadFail loadFail;
	private GetFightsReqParam reqData;
	private GetFightsListLoader reqLoader = null;
	private ListviewBottomRefresh listView;
	private ListFooter listFooter;
	private FightRecordAdapter fightRecordAdapter = null;
	//private TextView fightMsgView;

	private FinalBitmap finalBit;
	private FragmentActivity mContext = null;

	private static MemFightRecordFrg frag = null;

	private String my_avatar_url ,pal_avatar_url;
	
	public static MemFightRecordFrg startMemFightRecordFrg(Context context,String fightMsg,String memSn) {
		if (frag == null) {

			frag = new MemFightRecordFrg();
		}

		Bundle data = new Bundle();
		data.putString(BUNDLE_MEM_SN, memSn);
		data.putString(BUNDLE_FIGHT_MSG, fightMsg);

		frag.setArguments(data);

		return frag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View parentView = inflater.inflate(R.layout.mem_frg_fight_record, container, false);
		mContext = getActivity();
		getViews(parentView);

		return parentView;
	}


	
	private void getViews(View view){

		finalBit = FinalBitmap.create(mContext);
		listFooter = new ListFooter(mContext);
		loadFail = new LoadFail(mContext,(RelativeLayout) view.findViewById(R.id.fight_record_list_load_fail));
		loadFail.setOnRetryClickListener(retryListener);
		listView = (ListviewBottomRefresh) view.findViewById(R.id.fight_record_listview);
		//listView.addFooterView(listFooter.getFooterView());
		listView.setOnRefreshListener(pullRefreshListener);
		//listView.setOnLoadMoreListener(mOnLoadMoreListener);
		reqData = new GetFightsReqParam();
		reqData.sn = MainApp.getInstance().getCustomer().sn;
		reqData.memSn = getArguments().getString(BUNDLE_MEM_SN);
		reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
		reqData.pageSize = MainApp.getInstance().getGlobalData().pageSize;

		my_avatar_url = "http://121.199.22.44:8080/gams/person/"+reqData.sn+"/avatar/original/"+reqData.sn+".jpg";
		pal_avatar_url = "http://121.199.22.44:8080/gams/person/"+reqData.memSn+"/avatar/original/"+reqData.memSn+".jpg";
		if(null != fightRecordAdapter) {
			listView.setAdapter(fightRecordAdapter);
			Utils.logh(TAG, "exist inviteAdapter " + fightRecordAdapter);
		} else {
			initDataAysnc(reqData);
		}

//		fightMsgView = (TextView) view.findViewById(R.id.member_fight_record_fightMsg);
//		String fightMsg = getArguments().getString(BUNDLE_FIGHT_MSG);
//		if(fightMsg.isEmpty()){
////			Utils.setGone(fightMsgView);
//		}else{
//			Utils.setVisible(fightMsgView);
//			fightMsgView.setText(fightMsg);
//		}
		
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
//			reqData.pageNum = fightRecordAdapter.getCount() / reqData.pageSize + 1;
//			appendListDataAsync(reqData);
//		}
//	};
	
	private void initListView(ArrayList<MemMyFightInfo> fightRecordList) {
		if(null == fightRecordAdapter) {
			fightRecordAdapter = new FightRecordAdapter(fightRecordList);
			listView.setAdapter(fightRecordAdapter);			
		} else {
			fightRecordAdapter.refreshListInfo(fightRecordList);
		}
	}
	
	private void appendListDataAsync(final GetFightsReqParam data) {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetFightsListLoader(mContext, data, new GetFightsListCallback() {
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
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private void refreshDataAysnc(final GetFightsReqParam data) {
		if(!Utils.isConnected(mContext)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetFightsListLoader(mContext, data, new GetFightsListCallback() {
			@Override
			public void callBack(int retId, String msg, ArrayList<MemMyFightInfo> fightRecordList) {
				listView.onRefreshComplete();
				Utils.logh(TAG, "retId: " + retId + " fightRecordList: " + fightRecordList.size());
				switch(retId) {
					case BaseRequest.REQ_RET_OK:
						loadFail.displayNone();
						fightRecordAdapter.refreshListInfo(fightRecordList);
						listFooter.refreshFooterView(fightRecordList.size(), data.pageSize);
						break;
					case BaseRequest.REQ_RET_F_NO_DATA:
						loadFail.displayNoData(msg);// do not exists in common
						break;
					default: // normal fail
						loadFail.displayFail(msg);
						Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
						break;
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}
	
	private void initDataAysnc(final GetFightsReqParam data) {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetFightsListLoader(mContext, data, new GetFightsListCallback() {
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
			ViewHodler holder ;
			if(null == convertView) {
				convertView = View.inflate(mContext, R.layout.mem_fight_record_item_new, null);
				holder = new ViewHodler();
				holder.timeIv = (TextView) convertView.findViewById(R.id.fight_record_li_time);
				holder.myHCPIIv = (TextView) convertView.findViewById(R.id.fight_record_li_myhcpi);
				holder.palHCPIIv = (TextView) convertView.findViewById(R.id.fight_record_li_palhcpi);
				holder.courseIv = (TextView) convertView.findViewById(R.id.fight_record_li_course);

				holder.myAvater = (ImageView)convertView.findViewById(R.id.fight_record_li_my_avatar);
				holder.myWinner = (ImageView)convertView.findViewById(R.id.fight_record_li_my_winner);

				holder.palAvater = (ImageView)convertView.findViewById(R.id.fight_record_li_pal_avatar);
				holder.palWinner = (ImageView)convertView.findViewById(R.id.fight_record_li_pal_winner);
				holder.time_line = (ImageView)convertView.findViewById(R.id.time_line);


				convertView.setTag(holder);
			} else {
				holder = (ViewHodler) convertView.getTag();
			}
			MemMyFightInfo data = list.get(position);
			holder.myHCPIIv.setText(data.myScoreInfo);
			holder.palHCPIIv.setText( data.palScoreInfo);
			holder.timeIv.setText(data.teeTime);
			holder.courseIv.setText(data.courseName);

			finalBit.display(holder.myAvater,my_avatar_url);
			finalBit.display(holder.palAvater,pal_avatar_url);



			if (!data.myScoreInfo.equalsIgnoreCase("-") && !data.palScoreInfo.equalsIgnoreCase("-")){

				if (Float.valueOf(data.myScoreInfo) > Float.valueOf(data.myScoreInfo)) {

					holder.myWinner.setVisibility(View.VISIBLE);
					holder.palWinner.setVisibility(View.GONE);
				} else {

					holder.myWinner.setVisibility(View.GONE);
					holder.palWinner.setVisibility(View.VISIBLE);
				}

			} else {

				if (data.myScoreInfo.equalsIgnoreCase("-")) {

					holder.myWinner.setVisibility(View.GONE);
				}

				if (data.palScoreInfo.equalsIgnoreCase("-")) {

					holder.palWinner.setVisibility(View.GONE);
				}
			}

			return convertView;
		}

		private class ViewHodler {
			protected TextView courseIv;
			protected TextView timeIv;
			protected TextView myHCPIIv;
			protected TextView palHCPIIv;

			protected ImageView myAvater;
			protected ImageView myWinner;
			protected ImageView palAvater;
			protected ImageView palWinner;

			protected ImageView time_line;
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

}
