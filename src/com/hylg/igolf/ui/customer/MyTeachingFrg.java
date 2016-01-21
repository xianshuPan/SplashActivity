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
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.data.MyTeachingItem;
import com.hylg.igolf.cs.data.PinDanDetailInfo;
import com.hylg.igolf.cs.loader.GetFriendHotListLoader;
import com.hylg.igolf.cs.loader.GetFriendHotListLoader.GetFriendHotListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.GetCoachInviteOrderDetail;
import com.hylg.igolf.cs.request.GetMyTeachingListNew;
import com.hylg.igolf.imagepicker.Config;
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

public class MyTeachingFrg extends Fragment {
	
	private final String              	TAG                          = "FriendLocalFrg";
	
	private EhecdListview               mRefreshView                 = null;
	private MyTeachingAdapter			mAdapter			= null;

	private LoadFail 					loadFail;
	
	private String 						sn							= "",
										mProvenceStr                = "";
	
	
	private int 						startPage					= 0, 
										pageSize					= 0;


	/*高德定位操作*/
	private LocationManagerProxy 		mLocationManagerProxy;
	private myAMapLocationListener      mAMapLocationListener;

	private Customer                    customer;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadFail = new LoadFail(getActivity());
		
		loadFail.setOnRetryClickListener(retryListener);

		customer = MainApp.getInstance().getCustomer();
		startPage = MainApp.getInstance().getGlobalData().startPage;
		pageSize = MainApp.getInstance().getGlobalData().pageSize;
		mProvenceStr = MainApp.getInstance().getGlobalData().getProvinceStr();
		
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
		
		 mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
		 
		 mAMapLocationListener = new myAMapLocationListener();
		 
	     //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
	     //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
	     //在定位结束后，在合适的生命周期调用destroy()方法     
	     //其中如果间隔时间为-1，则定位只定一次
	     mLocationManagerProxy.requestLocationData(
	                LocationProviderProxy.AMapNetwork, 60*1000, 15,mAMapLocationListener);
	 
	     mLocationManagerProxy.setGpsEnable(false);
		
		super.onResume();
	}

	
	private class myAMapLocationListener implements AMapLocationListener {

		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			// TODO Auto-generated method stub
			if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
		           //获取位置信息
				mProvenceStr = amapLocation.getProvince();
				
		      }
		}
		
	}

	@Override
	public void onPause() {
		super.onPause();
		
		 if (mLocationManagerProxy != null) {
			 mLocationManagerProxy.removeUpdates(mAMapLocationListener);
			 mLocationManagerProxy.destroy();
		  }
		 
		 mAMapLocationListener = null;
		 mLocationManagerProxy = null;
		
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
		final GetMyTeachingListNew request = new GetMyTeachingListNew(getActivity(),customer.id,customer.sn,startPage,pageSize);
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
					initListView(request.getMyTeachingList());
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
		final GetMyTeachingListNew request = new GetMyTeachingListNew(getActivity(),customer.id,customer.sn,startPage,pageSize);
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

						initListView(request.getMyTeachingList());

					} else {

						mAdapter.refreshListData(request.getMyTeachingList());
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

		final GetMyTeachingListNew request = new GetMyTeachingListNew(getActivity(),customer.id,customer.sn,startPage,pageSize);
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

						initListView(request.getMyTeachingList());

					} else {

						mAdapter.appendListData(request.getMyTeachingList());
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
	
	
	private void initListView(ArrayList<MyTeachingItem> inviteList) {
		if (getActivity() == null || inviteList == null || inviteList.size() <= 0) {

			return;
		}
		mAdapter = new MyTeachingAdapter(inviteList);
		mRefreshView.setAdapter(mAdapter);
		//Utils.logh(TAG, "initListView myInviteAdapter " + myInviteAdapter);
	}


	private class MyTeachingAdapter extends IgBaseAdapter {
		private ArrayList<MyTeachingItem> list;
		private GlobalData gd;
		private String cusSn;

		public MyTeachingAdapter(ArrayList<MyTeachingItem> list) {
			this.list = list;
			gd = MainApp.getInstance().getGlobalData();
			cusSn = MainApp.getInstance().getCustomer().sn;
		}

		public void appendListData(ArrayList<MyTeachingItem> list) {
			for(MyTeachingItem data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}

		public void refreshListData(ArrayList<MyTeachingItem> list) {
			this.list.clear();
			for(MyTeachingItem data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(getActivity(), R.layout.my_taching_item_new, null);
				holder = new ViewHolder();

				holder.student1_avatar = (ImageView) convertView.findViewById(R.id.my_teaching_item_student1_avatar);
				holder.student1_nickname = (TextView)convertView.findViewById(R.id.my_teaching_item_student1_name);
				holder.student1_price = (TextView) convertView.findViewById(R.id.my_teaching_item_student1_price_unit);
				holder.student1_price_amount= (TextView)convertView.findViewById(R.id.my_teaching_item_student1_price_amount);
				holder.student1_status= (TextView)convertView.findViewById(R.id.my_teaching_item_student1_status);
				holder.student1_times = (TextView) convertView.findViewById(R.id.my_teaching_item_student1_time);

				holder.student2_relatice = (RelativeLayout) convertView.findViewById(R.id.my_teaching_item_student2_relative);
				holder.student2_avatar = (ImageView) convertView.findViewById(R.id.my_teaching_item_student2_avatar);
				holder.student2_nickname = (TextView)convertView.findViewById(R.id.my_teaching_item_student2_name);
				holder.student2_price = (TextView) convertView.findViewById(R.id.my_teaching_item_student2_price_unit);
				holder.student2_price_amount= (TextView)convertView.findViewById(R.id.my_teaching_item_student2_price_amount);
				holder.student2_status= (TextView)convertView.findViewById(R.id.my_teaching_item_student2_status);
				holder.student2_times = (TextView) convertView.findViewById(R.id.my_teaching_item_student2_time);

				holder.course = (TextView)convertView.findViewById(R.id.my_teaching_item_course);
				holder.teetime = (TextView)convertView.findViewById(R.id.my_teaching_item_teetime);
				holder.type = (ImageView)convertView.findViewById(R.id.my_teaching_item_type_image);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			MyTeachingItem data = list.get(position);

			if (data.student1Id > 0) {

				loadAvatar(getActivity(),data.student1Sn,data.student1Sn+".jpg",holder.student1_avatar);
				holder.student1_nickname.setText(data.student1Name);
				holder.student1_price.setText(getResources().getString(R.string.str_rmb)+Utils.getMoney(data.price));
				holder.student1_price_amount.setText(getResources().getString(R.string.str_rmb)+Utils.getMoney(data.price * data.times));
				setStatus(data.student1appStatus,holder.student1_status);
				holder.student1_times.setText("×" + String.valueOf(data.times));

			}

			if (data.student2Id > 0) {

				holder.student2_relatice.setVisibility(View.VISIBLE);
				loadAvatar(getActivity(),data.student2Sn,data.student2Sn+".jpg",holder.student2_avatar);
				holder.student2_nickname.setText(data.student2Name);
				holder.student2_price.setText(getResources().getString(R.string.str_rmb)+Utils.getMoney(data.price));
				holder.student2_price_amount.setText(getResources().getString(R.string.str_rmb)+Utils.getMoney(data.price*data.times));
				setStatus(data.student2appStatus,holder.student2_status);
				holder.student2_times.setText("×"+String.valueOf(data.times));

			} else {

				holder.student2_relatice.setVisibility(View.GONE);
			}

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
				CoachInviteOrderDetailActivityNew.startCoachInviteOrderDetailActivityNew(getActivity(), list.get(position).coachAppId, CoachInviteOrderDetailActivityNew.MY_TEACHING);

			}
		}

		class ViewHolder {


			private ImageView student1_avatar;
			private TextView student1_nickname;
			private TextView student1_price;
			private TextView student1_times;
			private TextView student1_price_amount;
			private TextView student1_status;

			private RelativeLayout student2_relatice;
			private ImageView student2_avatar;
			private TextView student2_nickname;
			private TextView student2_price;
			private TextView student2_times;
			private TextView student2_price_amount;
			private TextView student2_status;

			private TextView teetime;
			private TextView course;
			private ImageView type;

		}

		private void setStatus (int status,TextView statusText) {

			switch (status) {
				case Const.MY_TEACHING_WAITAPPLY:

					statusText.setText("待接受");
					statusText.setTextColor(getResources().getColor(R.color.green_5fb64e));
					break;

				case Const.MY_TEACHING_REVOKE:

					statusText.setText("已撤销");
					statusText.setTextColor(getResources().getColor(R.color.color_red));

					break;

				case Const.MY_TEACHING_REFUSE:

					statusText.setText("已拒绝");
					statusText.setTextColor(getResources().getColor(R.color.color_red));


					break;

				case Const.MY_TEACHING_ACCEPTED:
					statusText.setText("待支付");
					statusText.setTextColor(getResources().getColor(R.color.color_yellow));


					break;

				case Const.MY_TEACHING_FINISHED:

					statusText.setText("已支付");
					statusText.setTextColor(getResources().getColor(R.color.color_blue));

					break;



				case Const.MY_TEACHING_CANCEL:

					statusText.setText("已过期");
					statusText.setTextColor(getResources().getColor(R.color.color_red));

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
