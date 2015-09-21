package com.hylg.igolf.ui.hall;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CourseInfo;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.GetCourseInfoList;
import com.hylg.igolf.ui.common.RegionSelectActivity;
import com.hylg.igolf.ui.common.RegionSelectActivity.onRegionSelectListener;
import com.hylg.igolf.ui.hall.CourseSelectActivity.onCourseSelectListener;
import com.hylg.igolf.ui.hall.data.PlanSubmitInfo;
import com.hylg.igolf.ui.widget.IgTimePickerDialog;
import com.hylg.igolf.ui.widget.IgTimePickerDialog.OnIgTimeSetListener;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;
import com.hylg.igolf.utils.WaitDialog;

public class InvitePlanActivity extends Activity implements OnClickListener,
													onRegionSelectListener, onCourseSelectListener {
	private final static String TAG = "InvitePlanActivity";
	public final static String BUNDLE_KEY_SETUP_PLAN = "setup_plan";
	private final static String BUNDLE_KEY_PLAN_INDEX = "plan_index";
	private PlanSubmitInfo curPlan;
	private TextView dateTv, timeTv, regionTv, courseTv;
	private Calendar curDate = null, curTime = null;
	private String curRegion;
	
	public static void startOpenInvite(Context context) {
		Intent intent = new Intent(context, InvitePlanActivity.class);
		context.startActivity(intent);
	}
	public static void startSexSelectForResult(Context context, int index) {
		Intent intent = new Intent();
		intent.setClass(context, InvitePlanActivity.class);
		intent.putExtra(BUNDLE_KEY_PLAN_INDEX, index);
		((Activity) context).startActivityForResult(intent, Const.REQUST_CODE_INVITE_PLAN);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hall_ac_invite_plan);
		getViews();
		setData();
	}

	private void getViews() {
		findViewById(R.id.invite_sts_plan_topbar_back).setOnClickListener(this);
		findViewById(R.id.invite_sts_plan_confirm).setOnClickListener(this);
		findViewById(R.id.invite_sts_plan_date_ll).setOnClickListener(this);
		dateTv = (TextView) findViewById(R.id.invite_sts_plan_date_selection);
		findViewById(R.id.invite_sts_plan_time_ll).setOnClickListener(this);
		timeTv = (TextView) findViewById(R.id.invite_sts_plan_time_selection);
		findViewById(R.id.invite_sts_plan_region_ll).setOnClickListener(this);
		regionTv = (TextView) findViewById(R.id.invite_sts_plan_region_selection);
		findViewById(R.id.invite_sts_plan_course_ll).setOnClickListener(this);
		courseTv = (TextView) findViewById(R.id.invite_sts_plan_course_selection);
	}

	private void setData() {
		int index = getIntent().getExtras().getInt(BUNDLE_KEY_PLAN_INDEX);
		Utils.logh(TAG, "index: " + index);
		curPlan = new PlanSubmitInfo(index);
		// 设置默认省份为用户所在省份
		curRegion = MainApp.getInstance().getCustomer().state;
		regionTv.setText(MainApp.getInstance().getGlobalData().getRegionName(curRegion));
	}

	private void onPlanConfirm() {
		if(null == curDate) {
			Toast.makeText(this, R.string.str_toast_date_select, Toast.LENGTH_SHORT).show();
			return ;
		}
		if(null == curTime) {
			Toast.makeText(this, R.string.str_toast_time_select, Toast.LENGTH_SHORT).show();
			return ;
		}
		if(0 == curPlan.teeCourse) {
			Toast.makeText(this, R.string.str_toast_course_select, Toast.LENGTH_SHORT).show();
			return ;
		}
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, curDate.get(Calendar.YEAR));
		c.set(Calendar.MONTH, curDate.get(Calendar.MONTH));
		c.set(Calendar.DAY_OF_MONTH, curDate.get(Calendar.DAY_OF_MONTH));
		c.set(Calendar.HOUR_OF_DAY, curTime.get(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, curTime.get(Calendar.MINUTE));
		c.set(Calendar.SECOND, 0);
		curPlan.teeTime = c.getTimeInMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
		curPlan.timeStr = sdf.format(new Date(curPlan.teeTime));
		
		Intent intent = new Intent();
		intent.putExtra(BUNDLE_KEY_SETUP_PLAN, curPlan);
		setResult(RESULT_OK, intent);
		finishWithAnim();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.invite_sts_plan_topbar_back:
				finishWithAnim();
				break;
			case R.id.invite_sts_plan_confirm:
				onPlanConfirm();
				break;
			case R.id.invite_sts_plan_date_ll:
				setupDate();
				break;
			case R.id.invite_sts_plan_time_ll:
				setupTime();
				break;
			case R.id.invite_sts_plan_region_ll:
				setupRegion();
				break;
			case R.id.invite_sts_plan_course_ll:
				setupCourse();
				break;
		}
	}
	
	private void setupCourse() {
		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
		new AsyncTask<Object, Object, Integer>() {
			GetCourseInfoList request = new GetCourseInfoList(InvitePlanActivity.this, curRegion);
			@Override
			protected Integer doInBackground(Object... params) {
				return request.connectUrl();
			}
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				if(BaseRequest.REQ_RET_OK == result) {
					CourseSelectActivity.startCourseSelect(InvitePlanActivity.this, request.getCourseList());
				} else {
//					if(BaseRequest.REQ_RET_F_NO_DATA == result) { }
					Toast.makeText(InvitePlanActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
				}
				WaitDialog.dismissWaitDialog();
			}
		}.execute(null, null, null);
	}
	
	private void setupRegion() {
		RegionSelectActivity.startRegionSelect(this, RegionSelectActivity.REGION_TYPE_SELECT_COURSE, curRegion);
	}
	
	private void setupTime() {
		IgTimePickerDialog td = new IgTimePickerDialog(this, System.currentTimeMillis(), true);
		td.setOnIgTimeSetListener(new OnIgTimeSetListener() {
			@Override
			public void OnIgTimeSet(AlertDialog dialog, long date) {
				timeTv.setText(new SimpleDateFormat("HH:mm",
									Locale.getDefault()).format(new Date(date)));
				curTime = Calendar.getInstance();
				curTime.setTimeInMillis(date);
			}
			
		});
		td.show();
	}
	
	private void setupDate() {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		DatePickerDialog dpDialog = new DatePickerDialog(this,
				new OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						Utils.logh(TAG, "onDateSet year: " + year + " monthOfYear: " + monthOfYear + " dayOfMonth: " + dayOfMonth);
						if(null == curDate) {
							curDate = Calendar.getInstance();
						}
						curDate.set(Calendar.YEAR, year);
						curDate.set(Calendar.MONTH, monthOfYear);
						curDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
						
						dateTv.setText(new SimpleDateFormat("yyyy-MM-dd", 
								Locale.getDefault()).format(
										new Date(curDate.getTimeInMillis())));
					}
				},
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		dpDialog.show();
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
	public void onRegionSelect(String newRegion) {
		curRegion = newRegion;
		regionTv.setText(MainApp.getInstance().getGlobalData().getRegionName(newRegion));
		// 修改地区后，清楚球场信息
		if(0 != curPlan.teeCourse) {
			curPlan.teeCourse = 0;
			courseTv.setText(R.string.str_comm_unset);
		}
	}
	@Override
	public void onCourseSelect(CourseInfo course) {
		courseTv.setText(course.abbr);
		curPlan.courseStr = course.abbr;
		curPlan.teeCourse = course.id;
	}
}
