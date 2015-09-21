package com.hylg.igolf.ui.coach;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CoachInviteOrderDetail;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.loader.GetMyTeachingListLoader;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.CoachComplainOrRefuseCommit;
import com.hylg.igolf.cs.request.CoachComplainOrRefuseList;
import com.hylg.igolf.ui.view.EhecdListview;
import com.hylg.igolf.ui.view.LoadFail;
import com.hylg.igolf.ui.view.LoadFail.onRetryClickListener;
import com.hylg.igolf.ui.widget.IgBaseAdapter;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;
import com.tencent.mm.sdk.openapi.BaseReq;

public class CoachComplainOrRefuseFrg extends Fragment {
	
	private static final String 					TAG = "HallMyTeachingFrg";
	
	private GetMyTeachingListLoader 				reqLoader = null;
	private LoadFail 								loadFail;
	private ListView 								listView;
	
	private EditText                                mReasonEdit ;
	
	private TextView                                mTitleTxt,mCommitTxt;
	
	/*我的数据适配器*/
	private complainSelectionAdapter 				mAdapter;
	
	private Customer                                customer = null;
	
	private int                                     mType = -1;
	
	private ArrayList<String>                       mSelectedReasonArray = null;
	
	private long                                    mTeacherID =0,mStudentId = 0,mAppID= 0;
	
	private String                                  mSelectReasonIds = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadFail = new LoadFail(getActivity());
		loadFail.setOnRetryClickListener(retryListener);
		customer = MainApp.getInstance().getCustomer();

	}
	
	public CoachComplainOrRefuseFrg (int type,long teacher_id,long student_id,long app_id) {
		
		mType = type;
		mTeacherID = teacher_id;
		mStudentId = student_id;
		mAppID = app_id;
		mSelectedReasonArray = new ArrayList<String>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.coach_complain_list_frg, container, false);
		listView = (ListView) view.findViewById(R.id.coach_complain_list_listview);
		
		mTitleTxt = (TextView) view.findViewById(R.id.coach_complain_list_title_text);
		mReasonEdit = (EditText) view.findViewById(R.id.coach_complain_reason_edit);
		mCommitTxt = (TextView) view.findViewById(R.id.coach_complain_commit_text);
		
		mCommitTxt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				coachComplainOrRefuseCommit();
			}
		});
		
		if (mType == 0) {
			
			mTitleTxt.setText(R.string.str_coach_complain_title);
		} else {
			
			mTitleTxt.setText(R.string.str_coach_refuse_title);
		}
		
		view.addView(loadFail.getLoadFailView(), 0);
		return view;
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
//		if(null != myTeachingAdapter && !MainApp.getInstance().getGlobalData().hasStartNewInvite()) {
		if(null != mAdapter ) {
			listView.setAdapter(mAdapter);
			Utils.logh(TAG, "exist myTeachingAdapter " + mAdapter);
		} else {
			initDataAysnc();
			//loadFail.displayFail("加载失败！");
		}
		
		// 注册监听
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY);
	}
	
	
	@Override
	public void onResume() {
	
		super.onResume();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		LinearLayout parent = (LinearLayout) loadFail.getLoadFailView().getParent();
		Utils.logh(TAG, " --- onDestroyViewparent: " + parent);
		if(null != parent) {
			parent.removeAllViews();
		}
		if(null != reqLoader) {
			reqLoader.stopTask(true);
			reqLoader = null;
		}

	}

//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		// 注册监听
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY);
//		activity.registerReceiver(mReceiver, filter);
//	}
//
//	@Override
//	public void onDetach() {
//		getActivity().unregisterReceiver(mReceiver);
//		super.onDetach();
//	}
	
	private void initDataAysnc() {
		if(!Utils.isConnected(getActivity())) {
			return ;
		}
		WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);

		new AsyncTask<Object, Object, Integer>() {
			
			CoachComplainOrRefuseList request = new CoachComplainOrRefuseList(getActivity(), customer.id,mType);
			
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrl();	
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				WaitDialog.dismissWaitDialog();
				
				if ( result == BaseRequest.REQ_RET_OK){
					
					initListView(request.getSelectionList());
				}
				
			}
		}.execute(null, null, null);
	}
	
	/*
	 * 投诉或者拒接
	 * */
	private void coachComplainOrRefuseCommit() {
		if(!Utils.isConnected(getActivity())) {
			return ;
		}
		
		if (mSelectedReasonArray == null || mSelectedReasonArray.size() <= 0) {
			
			Toast.makeText(getActivity(), "请至少选择一条理由", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < mSelectedReasonArray.size(); i++) {
			
			sb.append(mSelectedReasonArray.get(i));
			sb.append(",");
		}
		mSelectReasonIds = sb.toString();
		
		WaitDialog.showWaitDialog(getActivity(), R.string.str_loading_msg);

		new AsyncTask<Object, Object, Integer>() {
			
			CoachComplainOrRefuseCommit request = new CoachComplainOrRefuseCommit(getActivity(), mType, mTeacherID, mStudentId, mAppID,
					mSelectReasonIds, mReasonEdit.getText().toString());
			
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrl();	
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				
				WaitDialog.dismissWaitDialog();
				
				if ( result == BaseRequest.REQ_RET_OK){
					
					
				}
				
			}
		}.execute(null, null, null);
	}

	private void initListView(ArrayList<HashMap<String, String>> inviteList) {
		mAdapter = new complainSelectionAdapter(inviteList);
		listView.setAdapter(mAdapter);
		Utils.logh(TAG, "initListView myTeachingAdapter " + mAdapter);
	}

	private onRetryClickListener retryListener = new onRetryClickListener() {
		@Override
		public void onRetryClick() {
			Utils.logh(TAG, "onRetryClick ... ");
			mAdapter = null;
			initDataAysnc();
		}
	};
	
	
	private class complainSelectionAdapter extends IgBaseAdapter {
		private ArrayList<HashMap<String, String>> list;
		
		public complainSelectionAdapter(ArrayList<HashMap<String, String>> list) {
			this.list = list;

		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(getActivity(), R.layout.coach_complain_or_refuse_item, null);
				holder = new ViewHolder();
				
				holder.selectImage = (ImageView) convertView.findViewById(R.id.coach_complain_or_refuse_item_select_image);
				holder.reason = (TextView) convertView.findViewById(R.id.coach_complain_or_refuse_item_reason_text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			
			final HashMap<String, String> data = list.get(position);
			
			holder.reason.setText(data.get("reason"));
			
			if (mSelectedReasonArray.contains(data.get("id"))) {
				
				holder.selectImage.setImageResource(R.drawable.selected);
				
			} else {
				
				holder.selectImage.setImageResource(R.drawable.select);
			}
			
			holder.selectImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (mSelectedReasonArray.contains(data.get("id"))) {
						
						mSelectedReasonArray.remove(data.get("id"));
						
					} else {
						
						mSelectedReasonArray.add(data.get("id"));
					}
					
					mAdapter.notifyDataSetChanged();
				}
			});
			
			return convertView;
		}
	
		
	class ViewHolder {
		
			private ImageView selectImage;
			private TextView reason;
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
