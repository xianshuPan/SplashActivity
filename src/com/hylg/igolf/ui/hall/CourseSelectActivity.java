package com.hylg.igolf.ui.hall;

import java.util.ArrayList;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CourseInfo;
import com.hylg.igolf.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CourseSelectActivity extends Activity {
	private static final String TAG = "LabelSelectActivity";
	private static onCourseSelectListener listener = null;
	// 先请求，再显示选择列表页面，暂无需地区
//	private final static String BUNDLE_CURR_REGION = "current_region";
	private final static String BUNDLE_COURSE_LIST = "course_list";
//	private String curRegion;
	private ArrayList<CourseInfo> courseList;
	private ListView courseListView;

//	public static void startCourseSelect(Context context, String curRegion, ArrayList<CourseInfo> courseList) {
	public static void startCourseSelect(Context context, ArrayList<CourseInfo> courseList) {
		try {
			listener = (onCourseSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onCourseSelectListener");
		}
		Intent intent = new Intent(context, CourseSelectActivity.class);
//		intent.putExtra(BUNDLE_CURR_REGION, curRegion);
		intent.putExtra(BUNDLE_COURSE_LIST, courseList);
		context.startActivity(intent);


	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_ac_list_select);
		getViews();
		getData();

	}

	@SuppressWarnings("unchecked")
	private void getData() {
//		curRegion = getIntent().getExtras().getString(BUNDLE_CURR_REGION);
		courseList = (ArrayList<CourseInfo>) getIntent().getExtras().getSerializable(BUNDLE_COURSE_LIST);
//		courseList = (ArrayList<CourseInfo>) getIntent().getExtras().getSerializable(BUNDLE_COURSE_LIST);
//		getCourseList();
		setCourseList();
	}
	
	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_course);
		courseListView = (ListView) findViewById(R.id.comm_dialog_list);
	}
	
	public void onLayoutSpaceClick(View view) {
		Utils.logh(TAG, "onLayoutSpaceClick");
		finish();
	}
	
	public void onCancleBtnClick(View view) {
		Utils.logh(TAG, "onCancleBtnClick");
		finish();
	}
	
	// 提前到发起页面请求
//	private void getCourseList() {
//		WaitDialog.showWaitDialog(this, R.string.str_loading_msg);
//		new AsyncTask<Object, Object, Integer>() {
//			GetCourseInfoList request = new GetCourseInfoList(curRegion);
//			@Override
//			protected Integer doInBackground(Object... params) {
//				return request.connectUrl();
//			}
//			@Override
//			protected void onPostExecute(Integer result) {
//				super.onPostExecute(result);
//				if(BaseRequest.REQ_RET_OK == result) {
//					courseList = request.getCourseList();
//					if(!courseList.isEmpty()) {
//						setCourseList();
//						WaitDialog.dismissWaitDialog();
//						return ;
//					} else {
//						Toast.makeText(CourseSelectActivity.this, 
//								String.format(getResources().getString(R.string.str_invite_no_course),
//										MainApp.getInstance().getGlobalData().getRegionName(curRegion)),
//								Toast.LENGTH_SHORT).show();
//					}
//				} else {
////					if(BaseRequest.REQ_RET_F_NO_DATA == result) { }
//					Toast.makeText(CourseSelectActivity.this, request.getFailMsg(), Toast.LENGTH_SHORT).show();
//				}
//				WaitDialog.dismissWaitDialog();
//				finish();
//			}
//		}.execute(null, null, null);
//	}
	
	private void setCourseList() {
		courseListView.setAdapter(new CourseAdapter());
		courseListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
				Utils.logh(TAG, " ------- pos : " + pos);
				listener.onCourseSelect(courseList.get(pos));
				finish();
			}
		});
	}
	
	private class CourseAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return courseList.size();
		}

		@Override
		public Object getItem(int position) {
			return courseList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(CourseSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(courseList.get(position).name);
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface onCourseSelectListener {
		void onCourseSelect(CourseInfo course);
	}
}
