package com.hylg.igolf.ui.hall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.data.OpenInvitationInfo;
import com.hylg.igolf.cs.loader.GetOpenInviteListLoader;
import com.hylg.igolf.cs.loader.GetOpenInviteListLoader.GetOpenInviteListCallback;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.ui.common.PayTypeFragment;
import com.hylg.igolf.ui.common.PayTypeFragment.onPayTypeSelectListener;
import com.hylg.igolf.ui.common.RegionSelectFragment;
import com.hylg.igolf.ui.common.RegionSelectFragment.onRegionSelectListener;
import com.hylg.igolf.ui.common.SexSelectFragment;
import com.hylg.igolf.ui.common.SexSelectFragment.onSexSelectListener;
import com.hylg.igolf.ui.common.StakeSelectFragment;
import com.hylg.igolf.ui.common.StakeSelectFragment.onStakeSelectListener;
import com.hylg.igolf.ui.common.TeeDateSelectFragment;
import com.hylg.igolf.ui.common.TeeDateSelectFragment.onTeeDateSelectListener;
import com.hylg.igolf.ui.hall.StartInviteOpenActivity.onStartRefreshListener;
import com.hylg.igolf.ui.reqparam.GetOpenInviteReqParam;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.EhecdListview.OnLoadMoreListener;
import com.hylg.igolf.ui.view.EhecdListview.OnRefreshListener;
import com.hylg.igolf.ui.view.ListFooter;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.DownLoadImageTool;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;

public class OpenInviteListFrgNew extends Fragment implements OnClickListener,
													onStartRefreshListener,
													onPayTypeSelectListener,
													onStakeSelectListener,
													onSexSelectListener,
													onRegionSelectListener,
													onTeeDateSelectListener {

	private static final String TAG = "OpenInviteListFrgNew";
	private static final String BUNDLE_KEY_OPEN_LIST_PARAM = "open_list_param";
	private GetOpenInviteReqParam reqData;
	private TextView hintTv;
	private EhecdListview listView;

	private ViewPager horlistview;
	private OpenAdapter openAdapter;
	private GetOpenInviteListLoader reqLoader = null;
	private ListFooter listFooter;
	private LoadFail loadFail;

	private Activity mContext = null;

	private TextView paymentTv, regionTv, stakeTv, sexTv,dateTv;

	private GlobalData gd = null;
	private Customer   customer = null;

	private FinalBitmap finalBit;

	private static OpenInviteListFrgNew hallFrg = null;

	public static OpenInviteListFrgNew getInstance() {
		if(null == hallFrg) {
			hallFrg = new OpenInviteListFrgNew();
		}

		return hallFrg;
	}

	public static void startOpenInvite(Context context, GetOpenInviteReqParam reqParam) {
		Intent intent = new Intent(context, OpenInviteListFrgNew.class);
		intent.putExtra(BUNDLE_KEY_OPEN_LIST_PARAM, reqParam);
		context.startActivity(intent);
	}

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.hall_ac_open_invite_list);
//		getViews();
//		setData();
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getActivity();
		gd = MainApp.getInstance().getGlobalData();
		customer = MainApp.getInstance().getCustomer();
		View view = inflater.inflate(R.layout.hall_ac_open_invite_list, container, false);

		getViews(view);
		setData();

		finalBit = FinalBitmap.create(getActivity());

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		DebugTools.getDebug().debug_v(TAG, ">>>>>>>onViewCreated");

		if(null != openAdapter) {

			listView.setAdapter(openAdapter);
			//Utils.logh(TAG, "exist myInviteAdapter " + myInviteAdapter);

		} else {

			initListDataAsync(reqData);
		}

		paymentTv.setText(gd.getPayTypeName(reqData.pay));
		regionTv.setText(gd.getRegionName(reqData.location));
		stakeTv.setText(gd.getStakeName(reqData.stake));
		sexTv.setText(gd.getSexName(reqData.sex));
		dateTv.setText(gd.getTeeDateName(reqData.date));
	}

	public void onResume() {

		if ( !isLoading()) {

			refreshDataAysnc(reqData);
		}

		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(null != reqLoader) {
			reqLoader.stopTask(true);
			reqLoader = null;
		}

	}

	private void getViews(View view) {
		view.findViewById(R.id.hall_open_list_topbar_back).setOnClickListener(this);
		view.findViewById(R.id.hall_open_list_topbar_start_invite).setOnClickListener(this);
		hintTv = (TextView) view.findViewById(R.id.hall_open_list_hint);

		paymentTv = (TextView) view.findViewById(R.id.open_invite_list_filter_payment_content);
		regionTv = (TextView) view.findViewById(R.id.open_invite_list_filter_region_content);
		stakeTv = (TextView) view.findViewById(R.id.open_invite_list_filter_stake_content);
		sexTv = (TextView) view.findViewById(R.id.open_invite_list_filter_sex_content);
		dateTv = (TextView) view.findViewById(R.id.open_invite_list_filter_date_content);

		view.findViewById(R.id.open_invite_list_filter_payment).setOnClickListener(this);
		view.findViewById(R.id.open_invite_list_filter_region).setOnClickListener(this);
		view.findViewById(R.id.open_invite_list_filter_stake).setOnClickListener(this);
		view.findViewById(R.id.open_invite_list_filter_sex).setOnClickListener(this);
		view.findViewById(R.id.open_invite_list_filter_date).setOnClickListener(this);

		loadFail = new LoadFail(mContext, (RelativeLayout)view.findViewById(R.id.hall_open_load_fail));
		loadFail.setOnRetryClickListener(retryListener);
		listView = (EhecdListview) view.findViewById(R.id.hall_open_listview);
		//listFooter = new ListFooter(mContext);
		//listView.addFooterView(listFooter.getFooterView());
		listView.setOnRefreshListener(pullRefreshListener);
		listView.setOnLoadMoreListener(mOnLoadMoreListener);
		listView.setShowFootBottom(true);

		horlistview = (ViewPager)view.findViewById(R.id.hall_open_horlistview);

		//horlistview.setAdapter();

	}

	private void setData() {
		//reqData = (GetOpenInviteReqParam) getIntent().getSerializableExtra(BUNDLE_KEY_OPEN_LIST_PARAM);
		reqData = new GetOpenInviteReqParam();

		reqData.date = 0;
		reqData.pay = -1;
		reqData.sn =customer.sn;
		reqData.stake = -1;
		reqData.location = customer.state;
		reqData.sex = -1;
		reqData.pageSize = gd.pageSize;
		reqData.time = 0;

		Utils.logh(TAG, reqData.log());

	}

	private void refreshResultHintTv(int num) {
		if(num > 0) {
			hintTv.setText(String.format(
					getString(R.string.str_hall_open_list_hint), num));
		} else {
			hintTv.setText(getString(R.string.str_hall_open_list_no_hint));
		}

	}

	private void clearLoader() {
		if(isLoading()) {
			GetOpenInviteListLoader loader = reqLoader;
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

	private void initListDataAsync(final GetOpenInviteReqParam data) {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		WaitDialog.showWaitDialog(mContext, R.string.str_loading_msg);
		clearLoader();
		reqLoader = new GetOpenInviteListLoader(mContext, data, new GetOpenInviteListCallback() {
			@Override
			public void callBack(int retId, int retNum, String msg,
					ArrayList<OpenInvitationInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || inviteList.size() == 0) {
					if(msg.trim().length() == 0) {
						msg = getString(R.string.str_hall_open_list_no_hint);
					}
					// display reload page
					loadFail.displayNoDataRetry(msg);
					//refreshResultHintTv(retNum);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				} else if(BaseRequest.REQ_RET_OK == retId) {
					loadFail.displayNone();
					initListView(inviteList);
					//listFooter.refreshFooterView(inviteList.size(), data.pageSize);
					//refreshResultHintTv(retNum);

					if (inviteList != null && inviteList.size() > 0) {

						for (int i=0; i < inviteList.size();i++) {


						}
					}


				} else {
					loadFail.displayFail(msg);
					//refreshResultHintTv(retNum);
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
				WaitDialog.dismissWaitDialog();
			}
		});
		reqLoader.requestData();
	}

	private void initListView(ArrayList<OpenInvitationInfo> inviteList) {
		if(null == openAdapter) {
			openAdapter = new OpenAdapter(inviteList);
			listView.setAdapter(openAdapter);
		} else {
			openAdapter.refreshListData(inviteList);
		}
	}

	private void refreshDataAysnc(final GetOpenInviteReqParam data) {
		if(!Utils.isConnected(mContext)) {
			listView.onRefreshComplete();
			return ;
		}
		clearLoader();
		reqLoader = new GetOpenInviteListLoader(mContext, data, new GetOpenInviteListCallback() {
			@Override
			public void callBack(int retId, int retNum, String msg,
					ArrayList<OpenInvitationInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_OK == retId) {

					if (openAdapter != null) {

						openAdapter.refreshListData(inviteList);
					}
					else {

						initListView(inviteList);
					}

					//listFooter.refreshFooterView(inviteList.size(), data.pageSize);
					//refreshResultHintTv(retNum);
				} else {
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	private void appendListDataAsync(final GetOpenInviteReqParam data) {
		if(!Utils.isConnected(mContext)) {
			return ;
		}
		//listFooter.displayLoading();
		clearLoader();
		reqLoader = new GetOpenInviteListLoader(mContext, data, new GetOpenInviteListCallback() {
			@Override
			public void callBack(int retId, int retNum, String msg,
					ArrayList<OpenInvitationInfo> inviteList) {
				listView.onRefreshComplete();
				if(BaseRequest.REQ_RET_F_NO_DATA == retId || inviteList.size() == 0) {
					//listFooter.displayLast();
				} else if(BaseRequest.REQ_RET_OK == retId) {

					if (openAdapter != null) {

						openAdapter.appendListData(inviteList);
					}
					else {

						initListView(inviteList);
					}

					//listFooter.refreshFooterView(inviteList.size(), data.pageSize);
					//refreshResultHintTv(retNum);
				} else {
					if(BaseRequest.REQ_RET_F_OPEN_LIST_REFRESH == retId) {
						Log.e(TAG, "REQ_RET_F_OPEN_LIST_REFRESH should not occour !!!");
						openAdapter.refreshListData(inviteList);
						//listFooter.refreshFooterView(inviteList.size(), data.pageSize);
					} else {
						//listFooter.displayMore();
					}
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				}
				reqLoader = null;
			}
		});
		reqLoader.requestData();
	}

	@Override
	public void onPayTypeSelect(Object newType) {

		reqData.pay = Integer.valueOf(newType.toString());
		paymentTv.setText(gd.getPayTypeName(reqData.pay ));
		initListDataAsync(reqData);

	}

	@Override
	public void onRegionSelect(String newRegion) {

		reqData.location = newRegion;
		regionTv.setText(gd.getRegionName(newRegion));
		initListDataAsync(reqData);

	}

	@Override
	public void onSexSelect(Object newSex) {

		reqData.sex = Integer.valueOf(newSex.toString());
		sexTv.setText(gd.getSexName(reqData.sex));
		initListDataAsync(reqData);
	}

	@Override
	public void onStakeSelect(Object newStake) {

		reqData.stake = Integer.valueOf(newStake.toString());
		stakeTv.setText(gd.getStakeName(reqData.stake ));
		initListDataAsync(reqData);
	}

	@Override
	public void onTeeDateSelect(Object newTeeDate) {

		reqData.date = Integer.valueOf(newTeeDate.toString());
		dateTv.setText(gd.getTeeDateName(reqData.date));
		initListDataAsync(reqData);

	}

	private class OpenAdapter extends IgBaseAdapter {
		private ArrayList<OpenInvitationInfo> list;
		private GlobalData gd;
		private String cusSn;

		public OpenAdapter(ArrayList<OpenInvitationInfo> list) {
			this.list = list;
			gd = MainApp.getInstance().getGlobalData();
			cusSn = MainApp.getInstance().getCustomer().sn;
		}

		public void appendListData(ArrayList<OpenInvitationInfo> list) {
			for(OpenInvitationInfo data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}

		public void refreshListData(ArrayList<OpenInvitationInfo> list) {
			this.list.clear();
			for(OpenInvitationInfo data : list) {
				this.list.add(data);
			}
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(mContext, R.layout.invite_list_open_item_new, null);
				holder = new ViewHolder();
				holder.avatar = (ImageView) convertView.findViewById(R.id.invite_list_open_item_avatar);
				holder.nickname = (TextView) convertView.findViewById(R.id.invite_list_open_item_nickname);
				holder.teetime = (TextView) convertView.findViewById(R.id.invite_list_open_item_teetime);
				holder.course = (TextView) convertView.findViewById(R.id.invite_list_open_item_course);
				holder.sex = (ImageView) convertView.findViewById(R.id.invite_list_open_item_sex);
				holder.state = (TextView) convertView.findViewById(R.id.invite_list_open_item_state);

				holder.paytype = (TextView) convertView.findViewById(R.id.invite_list_open_item_paytye);
				holder.fans = (TextView) convertView.findViewById(R.id.invite_list_open_item_fans);
				holder.invitee_one = (ImageView) convertView.findViewById(R.id.invite_list_open_item_invitee_one);
				holder.invitee_two = (ImageView) convertView.findViewById(R.id.invite_list_open_item_invitee_two);
				holder.invitee_three = (ImageView) convertView.findViewById(R.id.invite_list_open_item_invitee_three);
				holder.state_divider = convertView.findViewById(R.id.invite_list_open_item_state_divider);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			OpenInvitationInfo data = list.get(position);

//			if(data.inviterSn.equals(cusSn)) { // 我发起的(显示默认图标，或最后申请人头像)
//				loadAvatar(mContext, data.inviteeSn, data.inviteeAvatar, holder.avatar, true,
//						(int) getResources().getDimension(R.dimen.avatar_detail_size));
//			} else { // 他人发起的，显示发起人头像

				loadAvatar(mContext, data.inviterSn, data.avatar, holder.avatar);
//			}

			if (data.invitee_sns != null && data.invitee_sns.length() > 0) {

				String[] sd = data.invitee_sns.split(",");
				if (sd != null && sd.length > 0) {

					//finalBit.display(holder.invitee_one,Utils.getAvatarURLString(sd[0]));
					DownLoadImageTool.getInstance(getActivity()).displayImage(Utils.getAvatarURLString(sd[0]),holder.invitee_one,null);
					//Utils.loadAvatar(mContext,sd[0],holder.invitee_one);
				}
				else {

					holder.invitee_one.setImageResource(R.drawable.avatar_no_golfer);
					holder.invitee_two.setImageResource(R.drawable.avatar_no_golfer);
					holder.invitee_three.setImageResource(R.drawable.avatar_no_golfer);
				}

				if (sd != null && sd.length > 1) {

					//finalBit.display(holder.invitee_two,Utils.getAvatarURLString(sd[1]));
					DownLoadImageTool.getInstance(getActivity()).displayImage(Utils.getAvatarURLString(sd[1]), holder.invitee_two, null);
					//Utils.loadAvatar(mContext,sd[1],holder.invitee_two);
				}
				else {

					holder.invitee_two.setImageResource(R.drawable.avatar_no_golfer);
					holder.invitee_three.setImageResource(R.drawable.avatar_no_golfer);
				}

				if (sd != null && sd.length > 2) {

					//finalBit.display(holder.invitee_three,Utils.getAvatarURLString(sd[2]));
					DownLoadImageTool.getInstance(getActivity()).displayImage(Utils.getAvatarURLString(sd[2]), holder.invitee_three, null);
					//Utils.loadAvatar(mContext,sd[2],holder.invitee_three);
				}
				else {

					holder.invitee_three.setImageResource(R.drawable.avatar_no_golfer);
				}

			}
			else {

				holder.invitee_one.setImageResource(R.drawable.avatar_no_golfer);
				holder.invitee_two.setImageResource(R.drawable.avatar_no_golfer);
				holder.invitee_three.setImageResource(R.drawable.avatar_no_golfer);
			}

			holder.nickname.setText(data.inviterNickname);
			if(Const.SEX_MALE == data.inviterSex) {
				holder.sex.setImageResource(R.drawable.ic_male);
			} else {
				holder.sex.setImageResource(R.drawable.ic_female);
			}
			holder.teetime.setText(data.teeTime);
			holder.course.setText(data.courseName);
			holder.paytype.setText(gd.getPayTypeName(data.payType));
			holder.fans.setText(String.valueOf(data.sameProvencePerson));

			boolean canEnter = false;
			switch(data.displayStatus) {
				case Const.INVITE_OPEN_GREEN_WAIT:
					canEnter = true;
					holder.state.setTextColor(getResources().getColor(R.color.green_5fb64e));
					holder.state_divider.setBackgroundColor(getResources().getColor(R.color.green_5fb64e));
//					convertView.setOnClickListener(new onInviteItemClickListner(position));
//					convertView.setBackgroundResource(R.drawable.invite_list_bkg);
					break;
				case Const.INVITE_OPEN_YELLOW_APPLYING:
					canEnter = true;
					holder.state.setTextColor(getResources().getColor(R.color.color_yellow));
					holder.state_divider.setBackgroundColor(getResources().getColor(R.color.color_yellow));
//					convertView.setOnClickListener(new onInviteItemClickListner(position));
//					convertView.setBackgroundResource(R.drawable.invite_list_bkg);
					break;
				case Const.INVITE_OPEN_RED_ACCEPTED:
					canEnter = false;
					holder.state.setTextColor(getResources().getColor(R.color.color_gold));
					holder.state_divider.setBackgroundColor(getResources().getColor(R.color.color_gold));
//					convertView.setOnClickListener(null);
//					convertView.setBackgroundResource(R.drawable.invite_list_bkg_disable);
					break;

			}
			convertView.setOnClickListener(new onInviteItemClickListner(canEnter, position));
			holder.state.setText(data.displayMsg);
			return convertView;
		}

		private class onInviteItemClickListner implements OnClickListener {
			private int position;
			private boolean canEnter;
			public onInviteItemClickListner(boolean canEnter, int position) {
				this.canEnter = canEnter;
				this.position = position;
			}

			@Override
			public void onClick(View v) {
				String sn = MainApp.getInstance().getCustomer().sn;
				if(canEnter) {
					startInviteDetail(sn, list.get(position));
				} else {
					if(sn.equals(list.get(position).inviterSn) || list.get(position).acceptMe) {
						Toast.makeText(mContext,
								R.string.str_hall_open_list_click_mine, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(mContext,
								R.string.str_hall_open_list_click_over, Toast.LENGTH_SHORT).show();						
					}
				}
			}
		}
		
		class ViewHolder {
			private ImageView avatar;
			private TextView nickname;
			private TextView teetime;
			private TextView course;
			private TextView paytype;
			private TextView fans;

			private ImageView sex;
			private ImageView invitee_one;
			private ImageView invitee_two;
			private ImageView invitee_three;
			private TextView state;
			private View state_divider;
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
	
	private void startInviteDetail(String sn, OpenInvitationInfo openInvitationInfo) {
		if(openInvitationInfo.inviterSn.equals(sn)) { // 我发起的约球
			InviteDetailOpenMineActivity.startInviteDetailOpenMineForResult(this, openInvitationInfo);
			mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
		} else { // 他人发起的
			InviteDetailOpenOtherActivity.startInviteDetailOpenOtherForResult(this, openInvitationInfo);
			mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, "resultCode: " + resultCode);

		DebugTools.getDebug().debug_v(TAG, "onActivityResult----->>>resultCode" + resultCode);

		DebugTools.getDebug().debug_v(TAG,"onActivityResult----->>>requestCode"+requestCode);

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
	
	private OnRefreshListener pullRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			reqData.pageNum = MainApp.getInstance().getGlobalData().startPage;
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
			reqData.pageNum = reqData.pageNum + 1;
			appendListDataAsync(reqData);
		}
	};
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.hall_open_list_topbar_back:
				finishWithAnim();
				break;
			case R.id.hall_open_list_topbar_start_invite:
				StartInviteOpenActivity.startOpenInviteRefresh(mContext);
				mContext.overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
				break;

			case R.id.open_invite_list_filter_payment:
				onMultiFilterClick(FILTER_TYPE_PAYMENT);
				break;

			case R.id.open_invite_list_filter_region:
				onMultiFilterClick(FILTER_TYPE_REGION);
				break;
			case R.id.open_invite_list_filter_stake:
				onMultiFilterClick(FILTER_TYPE_STAKE);
				break;
			case R.id.open_invite_list_filter_sex:
				onMultiFilterClick(FILTER_TYPE_SEX);
				break;

			case R.id.open_invite_list_filter_date:
				onMultiFilterClick(FILTER_TYPE_DATE);
				break;
		}
	}


	private final static int FILTER_TYPE_PAYMENT = 1;
	private final static int FILTER_TYPE_REGION = 2;
	private final static int FILTER_TYPE_STAKE = 3;
	private final static int FILTER_TYPE_SEX = 4;
	private final static int FILTER_TYPE_DATE = 5;
	private void onMultiFilterClick(int position) {

		Utils.logh(TAG, "onMultiFilterClick position: " + position);

		getChildFragmentManager().popBackStack();
		switch(position) {
			case FILTER_TYPE_PAYMENT: // date
				//PayTypeSelectActivity.startPayTypeSelect(this, true, reqData.stake);

				PayTypeFragment.startPayTypeSelect(this,reqData.pay,R.id.hall_ac_open_list_container);
				break;
			case FILTER_TYPE_REGION: // time

				//RegionSelectActivity.startRegionSelect(this, RegionSelectActivity.REGION_TYPE_FILTER_ALL, reqData.location);

				RegionSelectFragment.startRegionSelect(this,reqData.location,R.id.hall_ac_open_list_container);
				break;
			case FILTER_TYPE_STAKE: // region
				//StakeSelectActivity.startStakeSelect(this, true, reqData.stake);
				StakeSelectFragment.startStakeSelect(this, reqData.stake, R.id.hall_ac_open_list_container);
				break;
			case FILTER_TYPE_SEX: // sex
				//SexSelectActivity.startSexSelect(this, true, reqData.sex);
				SexSelectFragment.startSexSelect(this,reqData.sex,R.id.hall_ac_open_list_container);
				break;
			case FILTER_TYPE_DATE: // pay type

				//TeeDateSelectActivity.startTeeDateSelect(this, true, reqData.date);
				TeeDateSelectFragment.startTeeDateSelect(this,reqData.date,R.id.hall_ac_open_list_container);
				break;
		}
	}

	
	private void finishWithAnim() {
		mContext.finish();
		mContext.overridePendingTransition(R.anim.ac_slide_left_in, R.anim.ac_slide_right_out);
	}

	@Override
	public void startRefresh(GetOpenInviteReqParam reqParam) {
		reqData = reqParam;
		handler.sendEmptyMessageDelayed(MSG_INIT_LIST, INIT_DELAY);

	}

	public final static int MSG_INIT_LIST = 1;
	public final static long INIT_DELAY = 500;
	public Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			openAdapter = null;
			initListDataAsync(reqData);
			// 发送查看广播给MainActivity，主动获取当前消息数量，更新导航栏提示图标
			mContext.sendBroadcast(new Intent(Const.IG_ACTION_NEW_ALERTS));
			// 发送广播给我的约球，请求更新
			mContext.sendBroadcast(new Intent(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY));
			return false;
		}
	});
}
