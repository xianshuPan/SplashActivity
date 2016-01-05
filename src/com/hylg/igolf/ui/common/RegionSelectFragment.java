package com.hylg.igolf.ui.common;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.utils.GlobalData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

public class RegionSelectFragment extends Fragment {
	private static final String TAG = "BaseSelectFragment";

	private final static String BUNDLE_CURR_TYPE = "current_type";
	public String curType;

	private String curTypeProvince = "";

	protected static onRegionSelectListener listener = null;

	private ListView lv,lv1;

	private LabelAdapter adapter;

	private ArrayList<ArrayList<RegionData>> filters;

	private static Fragment from ;

	private int select_parent_position;

	private boolean parent_item_click = false;

	public static void startRegionSelect(Fragment fragment, String curType,int container) {

		try {
			listener = (onRegionSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onLabelSelectListener");
		}

		from = fragment;
		FragmentManager fm = fragment.getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Bundle data = new Bundle();
		data.putString(BUNDLE_CURR_TYPE, curType);

		RegionSelectFragment to_fragment = new RegionSelectFragment();
		to_fragment.setArguments(data);
		ft.replace(container,to_fragment,TAG);
		ft.addToBackStack(TAG);
		ft.commit();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.common_frg_explist_select,null);

		getDataFrom();
		getViews(view);

		return view;
	}

	private void getDataFrom() {

		curType = getArguments().getString(BUNDLE_CURR_TYPE);

		if (curType != null && curType.length() >= 7) {

			curTypeProvince = curType.substring(0,7);
		}

	}
	
	private void getViews(View parent_view) {

		lv = (ListView) parent_view.findViewById(R.id.comm_dialog_list);
		adapter = new LabelAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {

//				if (select_parent_position != pos) {


				parent_item_click = true;
					select_parent_position = pos;

					LabelAdapter1 adapter1 = new LabelAdapter1(select_parent_position);

					lv1.setAdapter(adapter1);

				adapter.notifyDataSetChanged();

				DebugTools.getDebug().debug_v(TAG, "onItemClick------>>>");
				DebugTools.getDebug().debug_v(TAG,"onItemClick------>>>"+select_parent_position);
//				}

			}
		});

		lv1 = (ListView) parent_view.findViewById(R.id.comm_dialog_list1);
		LabelAdapter1 adapter1 = new LabelAdapter1(select_parent_position);
		lv1.setAdapter(adapter1);
		lv1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {

				RegionData child = (RegionData) filters.get(select_parent_position).get(pos);

				listener.onRegionSelect(child.dictKey);

				finish();
			}
		});

		View bg_linear = parent_view.findViewById(R.id.comm_dialog_bg);
		bg_linear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				DebugTools.getDebug().debug_v(TAG,"------>>>bg_click");
				finish();
			}
		});

		Button cancel = (Button) parent_view.findViewById(R.id.comm_dialog_btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				DebugTools.getDebug().debug_v(TAG,"------>>>cancel_click");
				finish();
			}
		});
	}

	void finish() {

		from.getChildFragmentManager().popBackStack();
	}
	
	private class LabelAdapter extends BaseAdapter {
		private GlobalData gd;

		public LabelAdapter() {
			gd = MainApp.getInstance().getGlobalData();
			filters = getData () ;
		}
		
		@Override
		public int getCount() {
			return filters.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(RegionSelectFragment.this.getActivity(), R.layout.common_select_list_frag_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}

			String name = filters.get(position).get(0).name;

			holder.nameTv.setText(name);

			String key = filters.get(position).get(0).dictKey;

			if (curTypeProvince.equalsIgnoreCase(key) ) {

				holder.nameTv.setTextColor(getResources().getColor(R.color.color_friend_item_praiser_name));

			}

			if (curTypeProvince.equalsIgnoreCase(key) && !parent_item_click) {

				holder.nameTv.setTextColor(getResources().getColor(R.color.color_friend_item_praiser_name));
				select_parent_position = position;

				LabelAdapter1 adapter1 = new LabelAdapter1(select_parent_position);
				lv1.setAdapter(adapter1);
			}
			else {


				holder.nameTv.setTextColor(getResources().getColor(R.color.gray));

				if (position == select_parent_position) {

					convertView.setBackgroundColor(Color.parseColor("#eeeded"));
				}
				else {

					convertView.setBackgroundColor(getResources().getColor(R.color.color_white));
				}

			}
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;

			protected ImageView selected;
		}
	}

	private class LabelAdapter1 extends BaseAdapter {
		private GlobalData gd;

		private int  parent_index;

		public LabelAdapter1(int index) {

			parent_index = index;
		}

		@Override
		public int getCount() {
			return filters.get(parent_index).size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(RegionSelectFragment.this.getActivity(), R.layout.common_select_list_frag_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				holder.selected = (ImageView) convertView.findViewById(R.id.common_select_item_image);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}

			String name = filters.get(parent_index).get(position).name;
			holder.nameTv.setText(name);

			String key = filters.get(parent_index).get(position).dictKey;

			if (curType.equalsIgnoreCase(key)) {

				holder.nameTv.setTextColor(getResources().getColor(R.color.color_friend_item_praiser_name));
				holder.selected.setVisibility(View.VISIBLE);
			}
			else {

				holder.nameTv.setTextColor(getResources().getColor(R.color.gray));
				holder.selected.setVisibility(View.GONE);
			}

			return convertView;
		}

		private class SexViewHolder {
			protected TextView nameTv;

			protected ImageView selected;
		}
	}


	ArrayList<ArrayList<RegionData>> getData (){

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
				Iterator<Map.Entry<String, String>> iter = province.children.entrySet().iterator();
				while(iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					citys.add(new RegionData(entry.getValue(), entry.getKey()));
				}
			}
			Collections.sort(citys, new RegionSort());
			filters.add(citys);
		}

		return filters;
	}

	// 根据key排序
	public class RegionSort implements Comparator<RegionData> {
		public int compare(RegionData rd0, RegionData rd1) {
			return rd0.dictKey.compareToIgnoreCase(rd1.dictKey);
		}
	}

	public interface onRegionSelectListener {
		void onRegionSelect(String newRegion);
	}

}
