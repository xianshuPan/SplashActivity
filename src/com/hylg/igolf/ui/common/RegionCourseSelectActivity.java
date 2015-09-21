package com.hylg.igolf.ui.common;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.utils.Utils;

public class RegionCourseSelectActivity extends RegionSelectActivity {
	private static final String TAG = "RegionCourseSelectActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_ac_list_select);
		setFinishOnTouchOutside(true);
		getViews();
	}
	
	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_region);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final RegionCourseAdapter adapter = new RegionCourseAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
				String key = adapter.getCurKey(pos);
				Utils.logh(TAG, " ------- pos : " + pos + " select region key: " + key + " curRegion: " + curRegion);
				if(!key.equals(curRegion)) {
					listener.onRegionSelect(key);
				}
				finish();
			}
		});
	}
	
	public void onLayoutSpaceClick(View view) {
		Utils.logh(TAG, "onLayoutSpaceClick");
		finish();
	}
	
	public void onCancleBtnClick(View view) {
		Utils.logh(TAG, "onCancleBtnClick");
		finish();
	}
	
	private class RegionCourseAdapter extends BaseAdapter {
		private ArrayList<RegionData> province;
		
		public RegionCourseAdapter() {
			province = MainApp.getInstance().getGlobalData().getBaseRegion().province;
		}
		
		public String getCurKey(int position) {
			Utils.logh(TAG, "getCurKey " + province.get(position).name);
			return province.get(position).dictKey;
		}
		
		@Override
		public int getCount() {
			return province.size();
		}
	
		@Override
		public Object getItem(int position) {
			return province.get(position);
		}
	
		@Override
		public long getItemId(int position) {
			return position;
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(RegionCourseSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(province.get(position).name);
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;
		}
	}
}
