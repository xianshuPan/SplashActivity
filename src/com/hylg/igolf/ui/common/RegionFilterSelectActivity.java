package com.hylg.igolf.ui.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.utils.Utils;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ExpandableListView.OnChildClickListener;

public class RegionFilterSelectActivity extends RegionSelectActivity {
	private static final String TAG = "RegionFilterSelectActivity";
	private ExpandableListView expListView;
	private RegionFilterAdapter regionFilterAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_ac_explist_select);
		setFinishOnTouchOutside(true);
		getViews();
	}
	
	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_elv_dialog_title);
		title.setText(R.string.str_dialog_select_title_region);
		expListView = (ExpandableListView) findViewById(R.id.comm_elv_dialog_list);
		regionFilterAdapter = new RegionFilterAdapter();
		expListView.setAdapter(regionFilterAdapter);
		expListView.setOnChildClickListener(mOnChildClickListener);
//		expListView.setOnGroupClickListener(mOnGroupClickListener);
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
//			mExpListAdapter.onChildClick(groupPosition, childPosition);
			RegionData child = (RegionData) regionFilterAdapter.getChild(groupPosition, childPosition);
			Utils.logh(TAG, "----- " + child.dictKey + "  " + child.name + "   curRegion: " + curRegion);
			if(!child.dictKey.equals(curRegion)) {
				listener.onRegionSelect(child.dictKey);
			}
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
	
	private class RegionFilterAdapter extends BaseExpandableListAdapter {
		private ArrayList<ArrayList<RegionData>> filters;
		
		public RegionFilterAdapter() {
			BaseRegion br = MainApp.getInstance().getGlobalData().getBaseRegion();
			filters = new ArrayList<ArrayList<RegionData>>();
			 // 添加全国
			ArrayList<RegionData> nation = new ArrayList<RegionData>();
			nation.add(br.nation);
			filters.add(nation);
			// 添加省及市
			ArrayList<RegionData> provinces = br.province;
			for(RegionData province : provinces) {
				ArrayList<RegionData> citys = new ArrayList<RegionData>();
				citys.add(province); // 可选省级，亦添加到child市级列表
				if(null != province.children && !province.children.isEmpty()) {
					 Iterator<Entry<String, String>> iter = province.children.entrySet().iterator();
					while(iter.hasNext()) {
						Entry<String, String> entry = iter.next();
						citys.add(new RegionData(entry.getValue(), entry.getKey()));
					}
				}
				Collections.sort(citys, new RegionSort());
				filters.add(citys);
			}
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
				convertView = View.inflate(RegionFilterSelectActivity.this, R.layout.common_select_exp_list_item, null);
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
				convertView = View.inflate(RegionFilterSelectActivity.this, R.layout.common_select_exp_list_group, null);
				holder = new FilterGroupViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_exp_list_group_name);
				convertView.setTag(holder);
			} else {
				holder = (FilterGroupViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(filters.get(groupPosition).get(0).name);
			
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
}
