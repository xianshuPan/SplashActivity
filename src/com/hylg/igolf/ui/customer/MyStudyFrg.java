package com.hylg.igolf.ui.customer;

import android.app.Activity;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.data.MyStudyItem;
import com.hylg.igolf.cs.data.MyTeachingItem;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.GetMyStudyListNew;
import com.hylg.igolf.cs.request.GetMyTeachingListNew;
import com.hylg.igolf.ui.coach.CoachInviteOrderDetailActivityNew;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.EhecdListview.OnLoadMoreListener;
import com.hylg.igolf.ui.view.EhecdListview.OnRefreshListener;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

public class MyStudyFrg extends Fragment {
	
	private final String              	TAG                          = "FriendLocalFrg";
	
	private EhecdListview               mRefreshView                 = null;
	private MyTeachingAdapter			mAdapter			= null;

	private LoadFail 					loadFail;
	
	
	private int 						startPage					= 0, 
										pageSize					= 0;


	private Customer                    customer;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadFail = new LoadFail(getActivity());
		
		loadFail.setOnRetryClickListener(retryListener);

		customer = MainApp.getInstance().getCustomer();
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		

		RelativeLayout parentView = (RelativeLayout)inflater.inflate(R.layout.my_teaching_list_frg, container, false);
		
		mRefreshView     = (EhecdListview) parentView.findViewById(R.id.my_teaching_frag_list);
		
		mRefreshView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshData();
			}
		});
		
		mRefreshView.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub

				loadMoreData();
			}
		});

		parentView.addView(loadFail.getLoadFailView());
		return parentView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		DebugTools.getDebug().debug_v(TAG, ">>>>>>>onViewCreated");

		if(null != mAdapter) {
			
			mRefreshView.setAdapter(mAdapter);
			//Utils.logh(TAG, "exist myInviteAdapter " + myInviteAdapter);
			
		} else {
			
			initDataAysnc();
//			loadFail.displayFail("加载失败！");
		}
		
		// 注册监听
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY);
		//getActivity().registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onDestroyView() {
		DebugTools.getDebug().debug_v(TAG, "onDestroyView..");
		super.onDestroyView();
	}
	
	@Override
	public void onStart() {
		DebugTools.getDebug().debug_v(TAG, "onStart..");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		DebugTools.getDebug().debug_v(TAG, "onResume..");
		refreshData();
		super.onResume();
	}


	@Override
	public void onPause() {
		super.onPause();

		DebugTools.getDebug().debug_v(TAG, "onPause..");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Debug.stopMethodTracing();
		DebugTools.getDebug().debug_v(TAG, "onDestroy..");
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		DebugTools.getDebug().debug_v(TAG, "onAttach..");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		DebugTools.getDebug().debug_v(TAG, "onDetach..");
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		DebugTools.getDebug().debug_v(TAG, "onLowMemory..");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		DebugTools.getDebug().debug_v(TAG, "onActivityCreated..");
	}
	


	
	/*初始化数据请求*/
	private void initDataAysnc() {
		if(!Utils.isConnected(getActivity())) {
			return ;
		}
		WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);
		/*sn 暂时等于1*/
		final GetMyStudyListNew request = new GetMyStudyListNew(getActivity(),customer.id,customer.sn,startPage,pageSize);
		new AsyncTask<Object,Object,Integer>(){

			@Override
			protected Integer doInBackground (Object... params) {

				return request.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer retId) {
				super.onPostExecute(retId);

				if(BaseRequest.REQ_RET_F_NO_DATA == retId ) {

					loadFail.displayNoData(request.getFailMsg());

				}
				else if (BaseRequest.REQ_RET_OK == retId) {

					loadFail.displayNone();
					initListView(request.getMyStudyList());
				}

				WaitDialog.dismissWaitDialog();
				mRefreshView.onRefreshComplete();
			}

		}.execute(null, null, null);
	}
	
	/*下拉刷新请求数据*/
	private void refreshData() {
		if(!Utils.isConnected(getActivity())) {
			
			mRefreshView.onRefreshComplete();
			return ;
		}
		startPage = 1;
		final GetMyStudyListNew request = new GetMyStudyListNew(getActivity(),customer.id,customer.sn,startPage,pageSize);
		new AsyncTask<Object,Object,Integer>(){

			@Override
			protected Integer doInBackground (Object... params) {

				return request.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer retId) {
				super.onPostExecute(retId);

				if(BaseRequest.REQ_RET_F_NO_DATA == retId ) {

					loadFail.displayNoData(request.getFailMsg());

				}
				else if (BaseRequest.REQ_RET_OK == retId) {

					loadFail.displayNone();
					if(mAdapter == null) {

						initListView(request.getMyStudyList());

					} else {

						mAdapter.refreshListData(request.getMyStudyList());
					}
				}
				else {

					Toast.makeText(getActivity(),request.getFailMsg(),Toast.LENGTH_SHORT).show();
				}

				mRefreshView.onRefreshComplete();
			}

		}.execute(null, null, null);

	}
	
	/*加载更多数据*/
	private void loadMoreData() {
		if(!Utils.isConnected(getActivity())) {
			
			mRefreshView.onRefreshComplete();
			return ;
		}
		
		startPage++;

		final GetMyStudyListNew request = new GetMyStudyListNew(getActivity(),customer.id,customer.sn,startPage,pageSize);
		new AsyncTask<Object,Object,Integer>(){

			@Override
			protected Integer doInBackground (Object... params) {

				return request.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer retId) {
				super.onPostExecute(retId);

				if(BaseRequest.REQ_RET_F_NO_DATA == retId ) {

					//loadFail.displayNoData(request.getFailMsg());
					Toast.makeText(getActivity(),request.getFailMsg(),Toast.LENGTH_SHORT).show();
				}
				else if (BaseRequest.REQ_RET_OK == retId) {

					loadFail.displayNone();
					if(mAdapter == null) {

						initListView(request.getMyStudyList());

					} else {

						mAdapter.appendListData(request.getMyStudyList());
					}
				}
				else {

					Toast.makeText(getActivity(),request.getFailMsg(),Toast.LENGTH_SHORT).show();
				}

				mRefreshView.onRefreshComplete();

			}

		}.execute(null, null, null);
	}
	
	
	private onRetryClickListener retryListener = new onRetryClickListener() {
		@Override
		public void onRetryClick() {
			//Utils.logh(TAG, "onRetryClick ... ");
			mAdapter = null;
			initDataAysnc();
		}
	};
	
	
	private void initListView(ArrayList<MyStudyItem> inviteList) {
		if (getActivity() == null || inviteList == null || inviteList.size() <= 0) {

			return;
		}
		mAdapter = new MyTeachingAdapter(inviteList);
		mRefreshView.setAdapter(mAdapter);
		//Utils.logh(TAG, "initListView myInviteAdapter " + myInviteAdapter);
	}


	private class MyTeachingAdapter extends IgBaseAdapter {
		private ArrayList<MyStudyItem> list;
		private GlobalData gd;
		private String cusSn;

		public MyTeachingAdapter(ArrayList<MyStudyItem> list) {
			this.list = list;
			gd = MainApp.getInstance().getGlobalData();
			cusSn = MainApp.getInstance().getCustomer().sn;
		}

		public void appendListData(ArrayList<MyStudyItem> list) {
			for(MyStudyItem data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}

		public void refreshListData(ArrayList<MyStudyItem> list) {
			this.list.clear();
			for(MyStudyItem data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(getActivity(), R.layout.my_study_item_new, null);
				holder = new ViewHolder();

				holder.coach_avatar = (ImageView) convertView.findViewById(R.id.my_study_item_avatar);
				holder.coach_nickname = (TextView)convertView.findViewById(R.id.my_study_item_name);
				holder.price = (TextView) convertView.findViewById(R.id.my_study_item_price_unit);
				holder.price_amount= (TextView)convertView.findViewById(R.id.my_study_item_price_amount);
				holder.status= (TextView)convertView.findViewById(R.id.my_study_item_state);
				holder.status_divider= convertView.findViewById(R.id.my_study_item_state_divider);
				holder.times = (TextView) convertView.findViewById(R.id.my_study_item_time);

				holder.course = (TextView)convertView.findViewById(R.id.my_study_item_course);
				holder.teetime = (TextView)convertView.findViewById(R.id.my_study_item_teetime);
				holder.type = (ImageView)convertView.findViewById(R.id.my_study_item_type_image);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			MyStudyItem data = list.get(position);


			loadAvatar(getActivity(),data.coachSn,data.coachSn+".jpg",holder.coach_avatar);
			holder.coach_nickname.setText(data.coachName);
			holder.price.setText(getResources().getString(R.string.str_rmb)+Utils.getMoney(data.price));
			holder.price_amount.setText(getResources().getString(R.string.str_rmb)+Utils.getMoney(data.price * data.times));
			setStatus(data.appStatus,holder.status,holder.status_divider);
			holder.times.setText("×" + String.valueOf(data.times));


			holder.course.setText(data.courseName);
			holder.teetime.setText(data.coachTime);
			if (data.alone == 1) {

				holder.type.setVisibility(View.VISIBLE);
			}
			else {

				holder.type.setVisibility(View.GONE);
			}

			convertView.setOnClickListener(new onInviteItemClickListner(position));

			return convertView;
		}

		private class onInviteItemClickListner implements View.OnClickListener {
			private int position;
			public onInviteItemClickListner(int position) {

				this.position = position;
			}

			@Override
			public void onClick(View v) {
				String sn = MainApp.getInstance().getCustomer().sn;

				//startInviteDetail(sn, list.get(position));
				CoachInviteOrderDetailActivityNew.startCoachInviteOrderDetailActivityNew(getActivity(), list.get(position).coachAppId, CoachInviteOrderDetailActivityNew.MY_STUDY);

			}
		}

		class ViewHolder {

			private ImageView coach_avatar;
			private TextView coach_nickname;
			private TextView price;
			private TextView times;
			private TextView price_amount;
			private TextView status;
			private View status_divider;

			private TextView teetime;
			private TextView course;
			private ImageView type;

		}

		private void setStatus (int status,TextView statusText,View status_divider) {

			switch (status) {
				case Const.MY_TEACHING_WAITAPPLY:

					statusText.setText("待接受");
					statusText.setTextColor(getResources().getColor(R.color.green_5fb64e));
					status_divider.setBackgroundColor(getResources().getColor(R.color.green_5fb64e));
					break;

				case Const.MY_TEACHING_REVOKE:

					statusText.setText("已撤销");
					statusText.setTextColor(getResources().getColor(R.color.color_red));
					status_divider.setBackgroundColor(getResources().getColor(R.color.color_red));

					break;

				case Const.MY_TEACHING_REFUSE:

					statusText.setText("已拒绝");
					statusText.setTextColor(getResources().getColor(R.color.color_red));
					status_divider.setBackgroundColor(getResources().getColor(R.color.color_red));


					break;

				case Const.MY_TEACHING_ACCEPTED:
					statusText.setText("待支付");
					statusText.setTextColor(getResources().getColor(R.color.color_yellow));
					status_divider.setBackgroundColor(getResources().getColor(R.color.color_yellow));


					break;

				case Const.MY_TEACHING_FINISHED:

					statusText.setText("已支付");
					statusText.setTextColor(getResources().getColor(R.color.color_blue));
					status_divider.setBackgroundColor(getResources().getColor(R.color.color_blue));

					break;



				case Const.MY_TEACHING_CANCEL:

					statusText.setText("已过期");
					statusText.setTextColor(getResources().getColor(R.color.color_red));
					status_divider.setBackgroundColor(getResources().getColor(R.color.color_red));

					break;
			}

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
	
}
