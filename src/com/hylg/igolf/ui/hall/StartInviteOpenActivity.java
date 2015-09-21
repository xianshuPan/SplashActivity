package com.hylg.igolf.ui.hall;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.gl.lib.impl.TextWatcherBkgVariable;
import cn.gl.lib.view.NestGridView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CourseInfo;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.GetCourseInfoList;
import com.hylg.igolf.cs.request.StartOpenInvite;
import com.hylg.igolf.ui.common.RegionSelectActivity;
import com.hylg.igolf.ui.common.RegionSelectActivity.onRegionSelectListener;
import com.hylg.igolf.ui.common.TeeDateSelectActivity;
import com.hylg.igolf.ui.common.TeeDateSelectActivity.onTeeDateSelectListener;
import com.hylg.igolf.ui.hall.CourseSelectActivity.onCourseSelectListener;
import com.hylg.igolf.ui.hall.adapter.PayTypeSelectAdapter;
import com.hylg.igolf.ui.hall.adapter.StakeSelectAdapter;
import com.hylg.igolf.ui.reqparam.GetOpenInviteReqParam;
import com.hylg.igolf.ui.reqparam.StartOpenReqParam;
import com.hylg.igolf.ui.widget.IgTimePickerDialog;
import com.hylg.igolf.ui.widget.IgTimePickerDialog.OnIgTimeSetListener;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class StartInviteOpenActivity extends Activity
									implements OnClickListener, onTeeDateSelectListener,
										onRegionSelectListener, onCourseSelectListener {
	private final static String TAG = "StartInviteOpenActivity";
	private TextView dateTv, timeTv, courseTv, regionTv;
	private EditText msgEt;
	private NestGridView payTypeGv, stakeGv;
	private PayTypeSelectAdapter payTypeAdapter;
	private StakeSelectAdapter stakeAdapter;
	private int curTeeDate;
	private String curTime;
	private CourseInfo curCourse;
	private String curRegion;
	private GlobalData gd;
	private static onStartRefreshListener refreshListener = null;

	/**
	 * 设置页面，发起约球，成功后，根据发起条件，查询大厅列表
	 * @param context
	 */
	public static void startOpenInviteNew(Context context) {
		refreshListener = null;
		Intent intent = new Intent(context, StartInviteOpenActivity.class);
		context.startActivity(intent);
	}
	
	/**
	 * 大厅列表中，发起约球，成功后，根据发起条件，更新大厅列表
	 * @param context
	 * @param listener
	 */
	public static void startOpenInviteRefresh(Context context) {
		try {
			refreshListener = (onStartRefreshListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onStartRefreshListener");
		}
		Intent intent = new Intent(context, StartInviteOpenActivity.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hall_ac_start_open_invite);
		getViews();
		setData();
	}

	private void setData() {
		gd = MainApp.getInstance().getGlobalData();
		payTypeAdapter = new PayTypeSelectAdapter(this);
		payTypeGv.setAdapter(payTypeAdapter);

		stakeAdapter = new StakeSelectAdapter(this);
		stakeGv.setAdapter(stakeAdapter);
		
		curRegion = MainApp.getInstance().getCustomer().state;
		regionTv.setText(gd.getRegionName(curRegion));
		
		curTeeDate = Integer.MAX_VALUE;
		curTime = "";
		curCourse = new CourseInfo();
	}

	private void getViews() {
		dateTv = (TextView) findViewById(R.id.hall_start_open_date_selection);
		timeTv = (TextView) findViewById(R.id.hall_start_open_time_selection);
		regionTv = (TextView) findViewById(R.id.hall_start_open_region_selection);
		courseTv = (TextView) findViewById(R.id.hall_start_open_course_selection);
		msgEt = (EditText) findViewById(R.id.hall_open_start_stake_invite_msg);
		msgEt.addTextChangedListener(new TextWatcherBkgVariable(msgEt));
		findViewById(R.id.start_open_invite_topbar_back).setOnClickListener(this);
		findViewById(R.id.hall_start_open_oper_start_invite).setOnClickListener(this);
		findViewById(R.id.hall_start_open_date_ll).setOnClickListener(this);
		findViewById(R.id.hall_start_open_time_ll).setOnClickListener(this);
		findViewById(R.id.hall_start_open_region_ll).setOnClickListener(this);
		findViewById(R.id.hall_start_open_course_ll).setOnClickListener(this);
		payTypeGv = (NestGridView) findViewById(R.id.hall_open_start_pay_type_gridview);
		stakeGv = (NestGridView) findViewById(R.id.hall_open_start_stake_gridview);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.start_open_invite_topbar_back:
				finishWithAnim();
				break;
			case R.id.hall_start_open_oper_start_invite:
				startOpenInvite();
				break;
			case R.id.hall_start_open_date_ll:
				TeeDateSelectActivity.startTeeDateSelect(StartInviteOpenActivity.this, false, curTeeDate);
				break;
			case R.id.hall_start_open_time_ll:
				setTimeForResult();
				break;
			case R.id.hall_start_open_region_ll:
				RegionSelectActivity.startRegionSelect(StartInviteOpenActivity.this, RegionSelectActivity.REGION_TYPE_SELECT_COURSE, curRegion);
				break;
			case R.id.hall_start_open_course_ll:
//				CourseSelectActivity.startCourseSelect(StartInviteOpenActivity.this, curRegion);
				getCourseList();
				break;
		}
	}
	
	private void setTimeForResult() {
		IgTimePickerDialog td = new IgTimePickerDialog(this, System.currentTimeMillis(), true);
		td.setOnIgTimeSetListener(new OnIgTimeSetListener() {
			@Override
			public void OnIgTimeSet(AlertDialog dialog, long date) {
				curTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(date));
				timeTv.setText(curTime);
			}
			
		});
		td.show();
	}
	
	private void getCourseList() {
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		new AsyncTask<Object, Object, Integer>() {
			GetCourseInfoList request = new GetCourseInfoList(StartInviteOpenActivity.this, curRegion);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					CourseSelectActivity.startCourseSelect(StartInviteOpenActivity.this, request.getCourseList());
				} else {
//					if(BaseRequest.REQ_RET_F_NO_DATA == result) { }
					Toast.makeText(StartInviteOpenActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}

	private void startOpenInvite() {
		if(Integer.MAX_VALUE == curTeeDate) {
			Toast.makeText(this, R.string.str_toast_date_select, Toast.LENGTH_SHORT).show();
			return ;
		}
		if(curTime.isEmpty()) {
			Toast.makeText(this, R.string.str_toast_time_select, Toast.LENGTH_SHORT).show();
			return ;
		}
		if(Long.MAX_VALUE == curCourse.id) {
			Toast.makeText(this, R.string.str_toast_course_select, Toast.LENGTH_SHORT).show();
			return ;
		}
		String msg = Utils.getEditTextString(msgEt);
		if(null == msg) {
			Toast.makeText(this, R.string.str_toast_open_invite_msg, Toast.LENGTH_SHORT).show();
			msgEt.requestFocus();
			return ;
		}
		int payType = payTypeAdapter.getSelectValue();
		int stake = stakeAdapter.getSelectValue();
		Utils.logh(TAG, "payType: " + payType + " stake: " + stake);
		StartOpenReqParam data = new StartOpenReqParam();
		data.sn = MainApp.getInstance().getCustomer().sn;
		data.day = curTeeDate;
		data.time = curTime;
		data.courseId = curCourse.id;
		data.payType = payType;
		data.stake = stake;
		data.msg = msg;
		startOpenInvite(data);
	}

	private void startOpenInvite(final StartOpenReqParam data) {
		WaitDialog.showWaitDialog(this, R.string.str_invite_starting_open);
		new AsyncTask<Object, Object, Integer>() {
			StartOpenInvite request = new StartOpenInvite(StartInviteOpenActivity.this, data);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				WaitDialog.dismissWaitDialog();
				if(BaseRequest.REQ_RET_OK == result) {
					Toast.makeText(StartInviteOpenActivity.this, R.string.str_start_invite_open_success, Toast.LENGTH_SHORT).show();
					startSuccess(data);
				} else {
	//				if(BaseRequest.REQ_RET_F_START_INVITE_LESS_TWO == result) { }
					Toast.makeText(StartInviteOpenActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(null, null, null);
	}

	private void startSuccess(StartOpenReqParam data) {
		GetOpenInviteReqParam reqParam = new GetOpenInviteReqParam();
		reqParam.sn = MainApp.getInstance().getCustomer().sn;
		reqParam.pageNum = MainApp.getInstance().getGlobalData().startPage;
		reqParam.pageSize = MainApp.getInstance().getGlobalData().pageSize;
		reqParam.date = data.day;
		try {
			int time = Integer.valueOf(curTime.split(":")[0]);
			if(time > 11) {
				reqParam.time = Const.TEE_TIME_PM;
			} else {
				reqParam.time = Const.TEE_TIME_AM;
			}
		} catch (NumberFormatException nfe) {
			reqParam.time = Const.TEE_TIME_ALL;			
		}
		reqParam.location = curRegion;
		reqParam.sex = MainApp.getInstance().getCustomer().sex;
		reqParam.stake = data.stake;
		reqParam.pay = data.payType;
		Utils.logh(TAG, "startSuccess refreshListener: " + refreshListener);
		Utils.logh(TAG, "startSuccess reqParam: " + reqParam.log());
		if(null == refreshListener) {
			OpenInviteListActivity.startOpenInvite(this, reqParam);
			finish();
			overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
		} else {
			refreshListener.startRefresh(reqParam);
			finishWithAnim();
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
	public void onTeeDateSelect(int newTeeDate) {
		curTeeDate = newTeeDate;
		dateTv.setText(MainApp.getInstance().getGlobalData().getTeeDateName(newTeeDate));
	}

	@Override
	public void onRegionSelect(String newRegion) {
		curRegion = newRegion;
		regionTv.setText(gd.getRegionName(newRegion));
		// 修改地区后，清楚球场信息
		if(Long.MAX_VALUE != curCourse.id) {
			curCourse.clearCourseInfo();
			courseTv.setText(R.string.str_comm_unset);
		}
	}

	@Override
	public void onCourseSelect(CourseInfo course) {
		curCourse.id = course.id;
		curCourse.abbr = course.abbr;
		curCourse.name = course.name;
		courseTv.setText(course.abbr);
	}
	
	public interface onStartRefreshListener {
		public abstract void startRefresh(GetOpenInviteReqParam reqParam);
	}

}
