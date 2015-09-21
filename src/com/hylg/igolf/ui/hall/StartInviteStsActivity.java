package com.hylg.igolf.ui.hall;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import cn.gl.lib.impl.TextWatcherBkgVariable;
import cn.gl.lib.view.NestGridView;
import cn.gl.lib.view.NestListView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.StartStsInvite;
import com.hylg.igolf.ui.hall.adapter.PayTypeSelectAdapter;
import com.hylg.igolf.ui.hall.adapter.StakeSelectAdapter;
import com.hylg.igolf.ui.hall.data.PlanSubmitInfo;
import com.hylg.igolf.ui.reqparam.StartStsReqParam;
import com.hylg.igolf.utils.*;

public class StartInviteStsActivity extends Activity implements OnClickListener {
	private static final String TAG = "StartInviteStsActivity";
	private final static String BUNDLE_KEY_INVITEE_SN = "inviteeSn";
	private StartStsReqParam reqParam;
	private NestListView plansListView;
	private PlansAddAdapter plansAdapter;
	private NestGridView payTypeGv, stakeGv;
	private PayTypeSelectAdapter payTypeAdapter;
	private StakeSelectAdapter stakeAdapter;
	private EditText addrNameEt, msgEt;
	
	public static void startOpenInvite(Context context, String inviteeSn) {
		Intent intent = new Intent(context, StartInviteStsActivity.class);
		intent.putExtra(BUNDLE_KEY_INVITEE_SN, inviteeSn);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hall_ac_start_sts_invite);
		getViews();
		setData();
	}
	
	private void getViews() {
		plansListView = (NestListView) findViewById(R.id.start_sts_plan_list);
		msgEt = (EditText) findViewById(R.id.start_sts_invite_msg);
		msgEt.addTextChangedListener(new TextWatcherBkgVariable(msgEt));
		addrNameEt = (EditText) findViewById(R.id.start_sts_invite_addressname);
		addrNameEt.addTextChangedListener(new TextWatcherBkgVariable(addrNameEt, true));
		findViewById(R.id.start_sts_invite_topbar_back).setOnClickListener(this);
		findViewById(R.id.start_sts_oper_start_invite).setOnClickListener(this);
		payTypeGv = (NestGridView) findViewById(R.id.start_sts_pay_type_gridview);
		stakeGv = (NestGridView) findViewById(R.id.start_sts_stake_gridview);
	}

	private void setData() {
//		inviteeSn = getIntent().getExtras().getString(BUNDLE_KEY_INVITEE_SN);
		reqParam = new StartStsReqParam();
		reqParam.inviteeSn = getIntent().getExtras().getString(BUNDLE_KEY_INVITEE_SN);
		reqParam.sn = MainApp.getInstance().getCustomer().sn;
		
		payTypeAdapter = new PayTypeSelectAdapter(this);
		payTypeGv.setAdapter(payTypeAdapter);

		stakeAdapter = new StakeSelectAdapter(this);
		stakeGv.setAdapter(stakeAdapter);
		
		plansAdapter = new PlansAddAdapter();
		plansListView.setAdapter(plansAdapter);

	}

	private void startStsInvite() {
		ArrayList<PlanSubmitInfo> validPlans = plansAdapter.getValidPlans();
		if(null == validPlans) {
			Toast.makeText(this, R.string.str_toast_sts_invite_add_plan, Toast.LENGTH_SHORT).show();
			return ;
		}
		String addrName = Utils.getEditTextString(addrNameEt);
		if(null == addrName) {
			Toast.makeText(this, R.string.str_toast_sts_invite_addr_name, Toast.LENGTH_SHORT).show();
			addrNameEt.requestFocus();
			return ;
		}
		int minLength = getResources().getInteger(R.integer.invite_addressname_min_length);
		int maxLength = getResources().getInteger(R.integer.invite_addressname_max_length);
		if(addrName.length() < minLength) {
			Toast.makeText(this, String.format(getString(R.string.str_toast_sts_invite_addr_name_len),
					minLength,maxLength), Toast.LENGTH_SHORT).show();
			return ;
		}
		String msg = Utils.getEditTextString(msgEt);
		if(null == msg) {
			Toast.makeText(this, R.string.str_toast_sts_invite_msg, Toast.LENGTH_SHORT).show();
			msgEt.requestFocus();
			return ;
		}

		
		WaitDialog.showWaitDialog(this, R.string.str_invite_starting_sts);
		reqParam.plans = validPlans;
		reqParam.msg = msg;
		reqParam.appName = addrName;
		reqParam.payType = payTypeAdapter.getSelectValue();
		reqParam.stake = stakeAdapter.getSelectValue();
		startStsInvite(reqParam);
	}
	
	private void startStsInvite(final StartStsReqParam data) {
		WaitDialog.showWaitDialog(this, R.string.str_invite_starting_sts);
		new AsyncTask<Object, Object, Integer>() {
			StartStsInvite request = new StartStsInvite(StartInviteStsActivity.this, data);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				WaitDialog.dismissWaitDialog();
				if(BaseRequest.REQ_RET_OK == result) {
					Toast.makeText(StartInviteStsActivity.this, R.string.str_start_invite_sts_success, Toast.LENGTH_SHORT).show();
					finishWithAnim();
				} else {
	//				if(BaseRequest.REQ_RET_F_START_INVITE_LESS_TWO == result) { }
					Toast.makeText(StartInviteStsActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, "resultCode: " + resultCode);
		if(RESULT_OK == resultCode && null != intent) {
			switch(requestCode) {
				case Const.REQUST_CODE_INVITE_PLAN:
					refreshPlan(intent);
					break;
			}
		}
	}
	
	private void refreshPlan(Intent intent) {
		PlanSubmitInfo plan = (PlanSubmitInfo) intent.getSerializableExtra(
										InvitePlanActivity.BUNDLE_KEY_SETUP_PLAN);
		Utils.logh(TAG, plan.log());
		plansAdapter.planAddRefresh(plan);
	}
	
	private class PlansAddAdapter extends BaseAdapter {
		private String[] defInfo;
		private ArrayList<PlanSubmitInfo> plans;
		
		public PlansAddAdapter() {
			defInfo = getResources().getStringArray(R.array.sts_plan_name_array);
			plans = new ArrayList<PlanSubmitInfo>();
			for(int i=0, len=defInfo.length; i<len; i++) {
				plans.add(new PlanSubmitInfo());
			}
		}

		public void planAddRefresh(PlanSubmitInfo plan) {
			plans.get(plan.index).refreshPlan(plan);
			notifyDataSetChanged();
		}

		public void planDelRefresh(int position) {
			plans.get(position).index = -1;
			notifyDataSetChanged();
		}
		
		public ArrayList<PlanSubmitInfo> getValidPlans() {
			ArrayList<PlanSubmitInfo> plans = new ArrayList<PlanSubmitInfo>();
			int index = 0;
			for(PlanSubmitInfo plan : this.plans) {
				if(plan.index != -1) {
					plan.index = index ++;
					plans.add(plan);
					Utils.logh(TAG, "plan ------- \n" + plan.log());
				}
			}
			if(index > 0) {
				return plans;
			}
			return null;
		}
		
		@Override
		public int getCount() {
			return plans.size();
		}

		@Override
		public Object getItem(int position) {
			return plans.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PlanViewHolder holder = null;
			if(null == convertView) {
				convertView = View.inflate(StartInviteStsActivity.this, R.layout.plan_add_item, null);
				holder = new PlanViewHolder();
				holder.planImg = (ImageView) convertView.findViewById(R.id.start_sts_plan_img);
				holder.planCourse = (TextView) convertView.findViewById(R.id.start_sts_plan_course);
				holder.planTime = (TextView) convertView.findViewById(R.id.start_sts_plan_teetime);
				convertView.setTag(holder);
			} else {
				holder = (PlanViewHolder) convertView.getTag();
			}
			
			PlanSubmitInfo plan = plans.get(position);
			if(plan.index == -1) {
				holder.planImg.setImageResource(R.drawable.ic_plan_add);
				holder.planCourse.setText(defInfo[position]);
				holder.planTime.setText("");
				convertView.setOnClickListener(new onAddClickListener(position));
				holder.planImg.setOnClickListener(new onAddClickListener(position));
			} else {
				holder.planImg.setImageResource(R.drawable.ic_plan_delete);
				holder.planCourse.setText(plan.courseStr);
				holder.planTime.setText(plan.timeStr);
				convertView.setOnClickListener(null);
				holder.planImg.setOnClickListener(new onDelClickListener(position));
			}
			return convertView;
		}
		
		private class onAddClickListener implements OnClickListener {
			private int position;
			
			public onAddClickListener(int position) {
				this.position = position;
			}
			@Override
			public void onClick(View v) {
				planAdd(position);
			}
			
		}
		
		private class onDelClickListener implements OnClickListener {
			private int position;
			
			public onDelClickListener(int position) {
				this.position = position;
			}
			@Override
			public void onClick(View v) {
				planDel(position);
			}
			
		}
		
		private class PlanViewHolder {
			public ImageView planImg;
			public TextView planTime;
			public TextView planCourse;
		}

	}

	private void planAdd(int position) {
		InvitePlanActivity.startSexSelectForResult(this, position);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	private void planDel(final int position) {
		new AlertDialog.Builder(this)
			.setTitle(R.string.str_invite_sts_plan_del_title)
			.setMessage(R.string.str_invite_sts_plan_del_msg)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					plansAdapter.planDelRefresh(position);
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { }
			})
			.show();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.start_sts_invite_topbar_back:
				finishWithAnim();
				break;
			case R.id.start_sts_oper_start_invite:
				startStsInvite();
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
	
}
