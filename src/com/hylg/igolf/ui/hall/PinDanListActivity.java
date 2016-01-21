package com.hylg.igolf.ui.hall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.PinDanDetailInfo;
import com.hylg.igolf.cs.loader.GetPinDanListLoader;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.coach.CoachInviteOrderDetailActivityNew;
import com.hylg.igolf.ui.hall.StartInviteOpenActivity.onStartRefreshListener;
import com.hylg.igolf.ui.reqparam.GetOpenInviteReqParam;
import com.hylg.igolf.ui.reqparam.GetPinDanReqParam;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import java.util.ArrayList;

public class PinDanListActivity extends Activity implements OnClickListener, onStartRefreshListener {
	private static final String TAG = "OpenInviteListActivity";
	private GetPinDanReqParam reqData;
	private TextView countTv;
	private EhecdListview listView;
	private OpenAdapter openAdapter;
	private GetPinDanListLoader reqLoader = null;
	//private ListFooter listFooter;
	private LoadFail loadFail;

	private PinDanListActivity mContext;

	public static void startPinDanListActivity(Activity context) {
		Intent intent = new Intent(context, PinDanListActivity.class);
		context.startActivity(intent);
		context.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hall_ac_pin_dan_list);
		getViews();
		setData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshDataAysnc(reqData);
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

		mContext = this;
		findViewById(R.id.pin_dan_list_topbar_back).setOnClickListener(this);
		countTv = (TextView) findViewById(R.id.pin_dan_count_text);
		loadFail = new LoadFail(this, (RelativeLayout)findViewById(R.id.pin_dan_load_fail));
		loadFail.setOnRetryClickListener(retryListener);
		listView = (EhecdListview) findViewById(R.id.pin_dan_listview);
		//listFooter = new ListFooter(this);
		//listView.addFooterView(listFooter.getFooterView());
		listView.setOnRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);

	}

	private void setData() {
		reqData = new GetPinDanReqParam();

		reqData.apiVersion = 200;
		reqData.appVersion = 200;
		reqData.lat = MainApp.getInstance().getGlobalData().getLat();
		reqData.lng = MainApp.getInstance().getGlobalData().getLng();
		reqData.pageNum =  MainApp.getInstance().getGlobalData().startPage;
		reqData.pageSize =  MainApp.getInstance().getGlobalData().pageSize;
		reqData.sn =  MainApp.getInstance().getCustomer().sn;


		Utils.logh(TAG, reqData.log());
		//initListDataAsync(reqData);
	}

	private void refreshResultHintTv(int num) {
		if(num > 0) {
			//countTv.setText(String.format(getString(R.string.str_hall_open_list_hint), num));
			countTv.setText(String.valueOf(num));
			findViewById(R.id.pin_dan_count_linear).setVisibility(View.VISIBLE);

		} else {

			findViewById(R.id.pin_dan_count_linear).setVisibility(View.GONE);
			//countTv.setText(getString(R.string.str_hall_open_list_no_hint));
		}

	}

	private void clearLoader() {
		if(isLoading()) {
			GetPinDanListLoader loader = reqLoader;
			loader.stopTask(true);
			loader = null;
		}
	}

	private boolean isLoading() {
		return (null != reqLoader && reqLoader.isRunning());
	}

	private onRetryClickListener retryListener = new onRetryClickListener() {
		@Override
		public void onRetryClick() {
			Utils.logh(TAG, "onRetryClick ... ");
			openAdapter = null;
			reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
			initListDataAsync(reqData);
		}
	};

	private void initListDataAsync(final GetPinDanReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetPinDanListLoader(PinDanListActivity.this, data, new GetPinDanListLoader.GetPinDanListCallback() {
			@Override
			public void callBack(int retId, int retNum, String msg,
					ArrayList<PinDanDetailInfo> inviteList) {

				if(BaseRequest.REQ_RET_F_NO_DATA == retId || inviteList.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_hall_open_list_no_hint);
					}
					// display reload page
					loadFail.displayNoDataRetry(msg);
					refreshResultHintTv(retNum);
					Toast.makeText(PinDanListActivity.this, msg, Toast.LENGTH_SHORT).show();
				} else if(BaseRequest.REQ_RET_OK == retId) {
					loadFail.displayNone();
					initListView(inviteList);
					//listFooter.refreshFooterView(inviteList.size(), data.pageSize);
					refreshResultHintTv(retNum);
				} else {
					loadFail.displayFail(msg);
					refreshResultHintTv(retNum);
					Toast.makeText(PinDanListActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				listView.onRefreshComplete();
				reqLoader = null;
				WaitDialog.dismissWaitDialog();
			}
		});
		reqLoader.requestData();
	}

	private void initListView(ArrayList<PinDanDetailInfo> inviteList) {
		if(null == openAdapter) {
			openAdapter = new OpenAdapter(inviteList);
			listView.setAdapter(openAdapter);
		} else {
			openAdapter.refreshListData(inviteList);
		}
	}

	private void refreshDataAysnc(final GetPinDanReqParam data) {
		if(!Utils.isConnected(this)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetPinDanListLoader(PinDanListActivity.this, data, new GetPinDanListLoader.GetPinDanListCallback() {
			@Override
			public void callBack(int retId, int retNum, String msg,
					ArrayList<PinDanDetailInfo> inviteList) {
				listView.onRefreshComplete();

				if(BaseRequest.REQ_RET_F_NO_DATA == retId || inviteList.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_hall_open_list_no_hint);
					}
					// display reload page
					loadFail.displayNoDataRetry(msg);
					refreshResultHintTv(retNum);
					Toast.makeText(PinDanListActivity.this, msg, Toast.LENGTH_SHORT).show();
				} else if(BaseRequest.REQ_RET_OK == retId) {

					loadFail.displayNone();
					if (openAdapter != null) {

						openAdapter.refreshListData(inviteList);
					}
					else {

						initListView(inviteList);
					}

					refreshResultHintTv(retNum);

				} else {

					Toast.makeText(PinDanListActivity.this, msg, Toast.LENGTH_SHORT).show();
					loadFail.displayFail(msg);
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	private void appendListDataAsync(final GetPinDanReqParam data) {
		if(!Utils.isConnected(this)) {
			return ;
		}
		//listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetPinDanListLoader(PinDanListActivity.this, data, new GetPinDanListLoader.GetPinDanListCallback() {
			@Override
			public void callBack(int retId, int retNum, String msg,
					ArrayList<PinDanDetailInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || inviteList.size() == 0) {

					Toast.makeText(PinDanListActivity.this, msg, Toast.LENGTH_SHORT).show();

				} else if(BaseRequest.REQ_RET_OK == retId) {

					loadFail.displayNone();

					if (openAdapter != null) {

						openAdapter.appendListData(inviteList);
					}
					else {

						initListView(inviteList);
					}

					refreshResultHintTv(retNum);
				} else {

					if(BaseRequest.REQ_RET_F_OPEN_LIST_REFRESH == retId) {
						Log.e(TAG, "REQ_RET_F_OPEN_LIST_REFRESH should not occour !!!");
						openAdapter.refreshListData(inviteList);
						//listFooter.refreshFooterView(inviteList.size(), data.pageSize);
					} else {
						//listFooter.displayMore();
					}
					Toast.makeText(PinDanListActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	private class OpenAdapter extends IgBaseAdapter {
		private ArrayList<PinDanDetailInfo> list;
		private GlobalData gd;
		private String cusSn;

		public OpenAdapter(ArrayList<PinDanDetailInfo> list) {
			this.list = list;
			gd = MainApp.getInstance().getGlobalData();
			cusSn = MainApp.getInstance().getCustomer().sn;
		}

		public void appendListData(ArrayList<PinDanDetailInfo> list) {
			for(PinDanDetailInfo data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}

		public void refreshListData(ArrayList<PinDanDetailInfo> list) {
			this.list.clear();
			for(PinDanDetailInfo data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(PinDanListActivity.this, R.layout.pin_dan_item_new, null);
				holder = new ViewHolder();

				holder.coach_avatar = (ImageView) convertView.findViewById(R.id.pin_dan_coach_avatar_image);
				holder.coach_nickname = (TextView)convertView.findViewById(R.id.pin_dan_coach_name_text);
				holder.student_avatar = (ImageView) convertView.findViewById(R.id.pin_dan_student_avatar_image);
				holder.student_nickname = (TextView)convertView.findViewById(R.id.pin_dan_student_name_text);
				holder.badge_image = (ImageView) convertView.findViewById(R.id.pin_dan_item_badge_image);
				holder.course = (TextView)convertView.findViewById(R.id.pin_dan_item_course);
				holder.teetime = (TextView)convertView.findViewById(R.id.pin_dan_item_teetime);
				holder.teaching_hours = (TextView)convertView.findViewById(R.id.pin_dan_item_teaching_hours);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			PinDanDetailInfo data = list.get(position);

			loadAvatar(mContext,data.coachSn,data.coachSn+".jpg",holder.coach_avatar);

			loadAvatar(mContext,data.studentSn,data.studentSn+".jpg",holder.student_avatar);

			holder.coach_nickname.setText(data.coachName);
			holder.student_nickname.setText(data.studentName);
			holder.course.setText(data.courseName);
			holder.teetime.setText(data.coachTime);
			holder.teaching_hours.setText(String.valueOf(data.times));

			if (data.refer == 1) {

				holder.badge_image.setImageResource(R.drawable.pin_dan_refer);
			}
			else {

				if (data.emergemcy == 1) {

					holder.badge_image.setImageResource(R.drawable.pin_dan_emergency);
				}
				else {

					holder.badge_image.setImageResource(R.drawable.white_bg);
				}

			}

			convertView.setOnClickListener(new onInviteItemClickListner(position));

			return convertView;
		}

		private class onInviteItemClickListner implements OnClickListener {
			private int position;
			public onInviteItemClickListner(int position) {

				this.position = position;
			}

			@Override
			public void onClick(View v) {
				String sn = MainApp.getInstance().getCustomer().sn;

				//startInviteDetail(sn, list.get(position));

				if ( !cusSn.equalsIgnoreCase(list.get(position).coachSn) && !cusSn.equalsIgnoreCase(list.get(position).studentSn)) {

					CoachInviteOrderDetailActivityNew.startCoachInviteOrderDetailActivityNew(mContext,list.get(position).coachAppId,CoachInviteOrderDetailActivityNew.PIN_DAN);
				}
				else {

					Toast.makeText(mContext,"请到个人中心,我的教学中查看",Toast.LENGTH_SHORT).show();
				}

			}
		}
		
		class ViewHolder {
			private ImageView coach_avatar;
			private TextView coach_nickname;

			private ImageView student_avatar;
			private TextView student_nickname;

			private ImageView badge_image;

			private TextView teetime;
			private TextView course;
			private TextView teaching_hours;

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

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, "resultCode: " + resultCode);
		if(Activity.RESULT_OK == resultCode && null != intent) {
			switch(requestCode) {
				case Const.REQUST_CODE_INVITE_DETAIL_OPEN: {
					if(!intent.getExtras().getBoolean(InviteDetailOpenOtherActivity.BUNDLE_KEY_STATUS_CHANGED)) {
						Log.w(TAG, "REQUST_CODE_INVITE_DETAIL_OPEN false should be true !!!");
					} else {
						// 状态变化了，目前重刷列表
						Utils.logh(TAG, "REQUST_CODE_INVITE_DETAIL_OPEN status changed ... ");
						reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
						handler.sendEmptyMessageDelayed(MSG_INIT_LIST, INIT_DELAY);
					}
					return ;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	private EhecdListview.OnRefreshListener pullRefreshListener = new EhecdListview.OnRefreshListener() {
		@Override
		public void onRefresh() {
			reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
			refreshDataAysnc(reqData);
		}
	};
	
	private EhecdListview.OnLoadMoreListener mOnLoadMoreListener = new EhecdListview.OnLoadMoreListener() {
		@Override
		public void onLoadMore() {

			reqData.pageNum = openAdapter.getCount() / reqData.pageSize + 1;
			appendListDataAsync(reqData);
		}
	};
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.pin_dan_list_topbar_back:
				finishWithAnim();
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {
			finishWithAnim();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void finishWithAnim() {
		finish();
		overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}

	@Override
	public void startRefresh(GetOpenInviteReqParam reqParam) {
		//reqData = reqParam;
		handler.sendEmptyMessageDelayed(MSG_INIT_LIST, INIT_DELAY);
	}
	
	private final static int MSG_INIT_LIST = 1;
	private final static long INIT_DELAY = 500;
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			openAdapter = null;
			initListDataAsync(reqData);
			// 发送查看广播给MainActivity，主动获取当前消息数量，更新导航栏提示图标
			sendBroadcast(new Intent(Const.IG_ACTION_NEW_ALERTS));
			// 发送广播给我的约球，请求更新
			sendBroadcast(new Intent(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY));
			return false;
		}
	});
}
