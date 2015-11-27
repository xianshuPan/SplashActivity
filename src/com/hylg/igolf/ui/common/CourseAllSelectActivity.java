package com.hylg.igolf.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.CourseInfo;
import com.hylg.igolf.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

public class CourseAllSelectActivity extends Activity {
	private static final String TAG = "CourseAllSelectActivity";
	private ExpandableListView expListView;
	private CourseAllAdapter regionFilterAdapter;

	private static onCourseAllSelectListener listener = null;

	private final static String BUNDLE_COURSE_LIST = "course_list";

	private ArrayList<ArrayList<CourseInfo>> courseList;


	public static void startCourseSelect(Context context, ArrayList<ArrayList<CourseInfo>> courseList) {
		try {
			listener = (onCourseAllSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() +
					" must implements onCourseSelectListener");
		}
		Intent intent = new Intent(context, CourseAllSelectActivity.class);
//		intent.putExtra(BUNDLE_CURR_REGION, curRegion);
		intent.putExtra(BUNDLE_COURSE_LIST, courseList);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_ac_explist_select);
		setFinishOnTouchOutside(true);
		getViews();
	}
	
	private void getViews() {

		courseList = (ArrayList<ArrayList<CourseInfo>>) getIntent().getExtras().getSerializable(BUNDLE_COURSE_LIST);

		TextView title = (TextView) findViewById(R.id.comm_elv_dialog_title);
		title.setText(R.string.str_dialog_select_title_course);

		expListView = (ExpandableListView) findViewById(R.id.comm_elv_dialog_list);

		if (courseList != null && courseList.size() > 0) {

			regionFilterAdapter = new CourseAllAdapter(courseList);
			expListView.setAdapter(regionFilterAdapter);
			expListView.setOnChildClickListener(mOnChildClickListener);

		}
	}
	
//	private OnGroupClickListener mOnGroupClickListener = new OnGroupClickListener() {
//		@Override
//		public boolean onGroupClick(ExpandableListView parent, View v,
//				int groupPosition, long id) {
//			if(!expListView.isGroupExpanded(groupPosition)) {
////				Utils.logh(TAG, "onGroupClick did: " + did);
//			}
//			return false;
//		}
//	};
	
	private OnChildClickListener mOnChildClickListener = new OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

			listener.onCourseAllSelect(courseList.get(groupPosition).get(childPosition));

			finish();
			return true;
		}
	};
	
	public void onElvLayoutSpaceClick(View view) {
		Utils.logh(TAG, "onElvLayoutSpaceClick");
		finish();
	}
	
	public void onElvCancleBtnClick(View view) {
		Utils.logh(TAG, "onElvCancleBtnClick");
		finish();
	}
	
	private class CourseAllAdapter extends BaseExpandableListAdapter {
		private ArrayList<ArrayList<CourseInfo>> filters;

		private ArrayList<String> nameArray;
		
		public CourseAllAdapter(ArrayList<ArrayList<CourseInfo>> cousrse) {

			filters= cousrse;

			nameArray = new ArrayList<String>();
			nameArray.add("练习场");
			nameArray.add("18洞球场");
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {

			return filters.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			FilterChildViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(CourseAllSelectActivity.this, R.layout.common_select_exp_list_item, null);
				holder = new FilterChildViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_exp_item_name);
				convertView.setTag(holder);
			} else {
				holder = (FilterChildViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(filters.get(groupPosition).get(childPosition).name);
			
			return convertView;
		}
		
		private class FilterChildViewHolder {
			protected TextView nameTv;
		}
		
		@Override
		public int getChildrenCount(int groupPosition) {
			return filters.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return filters.get(groupPosition).get(0);
		}

		@Override
		public int getGroupCount() {
			return filters.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			FilterGroupViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(CourseAllSelectActivity.this, R.layout.common_select_exp_list_group, null);
				holder = new FilterGroupViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_exp_list_group_name);
				convertView.setTag(holder);
			} else {
				holder = (FilterGroupViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(nameArray.get(groupPosition));
			
			return convertView;
		}
		
		private class FilterGroupViewHolder {
			protected TextView nameTv;
		}
		
		@Override
		public void onGroupExpanded(int groupPosition) {
			for(int i=0, cnt=getGroupCount(); i<cnt; i++) {
				if(groupPosition != i && expListView.isGroupExpanded(i)) {
					expListView.collapseGroup(i);
				}
			}
			super.onGroupExpanded(groupPosition);
		}
		
		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	public interface onCourseAllSelectListener {
		void onCourseAllSelect(CourseInfo course);
	}
}
