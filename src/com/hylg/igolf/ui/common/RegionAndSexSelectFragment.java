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

public class RegionAndSexSelectFragment extends Fragment {
	private static final String TAG = "RegionAndSexSelectFragment";

	private final static String BUNDLE_CURR_CITY_TYPE = "current_city_type";

	private final static String BUNDLE_CURR_SEX_TYPE = "current_sex_type";

	public String curCityType;

	private int curSexType;

	private String curTypeProvince = "";

	protected static onRegionAndSexSelectListener listener = null;

	private ListView lv,lv1,sexlV;

	private LabelAdapter adapter;

	LabelAdapter1 adapter1;
	private SexAdapter1 sexAdapter;

	private ArrayList<ArrayList<RegionData>> filters;

	private static Fragment from ;

	private int select_parent_position;

	private boolean parent_item_click = false;


	private LinearLayout no_select_linear,man_select_linear,woman_select_linear;
	private ImageView no_select_image,man_select_image,woman_select_image;


	public static void startRegionSelect(Fragment fragment, String curType,int curSex,int container) {

		try {
			listener = (onRegionAndSexSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onLabelSelectListener");
		}

		from = fragment;
		FragmentManager fm = fragment.getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Bundle data = new Bundle();
		data.putString(BUNDLE_CURR_CITY_TYPE, curType);
		data.putInt(BUNDLE_CURR_SEX_TYPE, curSex);

		RegionAndSexSelectFragment to_fragment = new RegionAndSexSelectFragment();
		to_fragment.setArguments(data);
		ft.replace(container,to_fragment,TAG);
		ft.addToBackStack(TAG);
		ft.commit();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.common_frg_explist_select,null);

		getViews(view);
		getDataFrom();


		return view;
	}

	private void getDataFrom() {

		curCityType = getArguments().getString(BUNDLE_CURR_CITY_TYPE);

		if (curCityType != null && curCityType.length() >= 7) {

			curTypeProvince = curCityType.substring(0,7);
		}
		curSexType = getArguments().getInt(BUNDLE_CURR_SEX_TYPE);

		checkSexSelect();

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

				 adapter1 = new LabelAdapter1(select_parent_position);

				lv1.setAdapter(adapter1);

				adapter.notifyDataSetChanged();

				DebugTools.getDebug().debug_v(TAG, "onItemClick------>>>");
				DebugTools.getDebug().debug_v(TAG, "onItemClick------>>>" + select_parent_position);
//				}

			}
		});

		lv1 = (ListView) parent_view.findViewById(R.id.comm_dialog_list1);
		adapter1 = new LabelAdapter1(select_parent_position);
		lv1.setAdapter(adapter1);
		lv1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {

				RegionData child = (RegionData) filters.get(select_parent_position).get(pos);

				//listener.onRegionSelect(child.dictKey);
				curCityType = child.dictKey;
				adapter1.notifyDataSetChanged();

			}
		});

		//sexlV  = (ListView) parent_view.findViewById(R.id.comm_dialog_list_sex);
		//sexAdapter = new SexAdapter1();
		//sexlV.setAdapter(sexAdapter);
//		sexlV.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//				curSexType = (Integer) sexAdapter.getItem(i);
//			}
//		});


		no_select_linear = (LinearLayout) parent_view.findViewById(R.id.sex_no_select_linear);
		man_select_linear = (LinearLayout) parent_view.findViewById(R.id.man_linear);
		woman_select_linear = (LinearLayout) parent_view.findViewById(R.id.woman_linear);

		no_select_image = (ImageView) parent_view.findViewById(R.id.sex_no_select_image);
		man_select_image = (ImageView) parent_view.findViewById(R.id.man_select_image);
		woman_select_image = (ImageView) parent_view.findViewById(R.id.woman_select_image);

		no_select_linear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				curSexType = -1;
				checkSexSelect();
			}
		});

		man_select_linear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				curSexType = 0;
				checkSexSelect();
			}
		});

		woman_select_linear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				curSexType = 1;
				checkSexSelect();
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

		Button ok = (Button) parent_view.findViewById(R.id.comm_dialog_btn_ok);
		parent_view.findViewById(R.id.sex_select_linear).setVisibility(View.VISIBLE);

		ok.setVisibility(View.VISIBLE);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				DebugTools.getDebug().debug_v(TAG, "------>>>cancel_click");
				//finish();
				listener.onRegionAndSexSelect(curCityType, curSexType);
				finish();
			}
		});
	}

	private void checkSexSelect() {

		switch (curSexType) {

			case -1:
				no_select_image.setVisibility(View.VISIBLE);
				man_select_image.setVisibility(View.GONE);
				woman_select_image.setVisibility(View.GONE);
				break;

			case 0:
				no_select_image.setVisibility(View.GONE);
				man_select_image.setVisibility(View.VISIBLE);
				woman_select_image.setVisibility(View.GONE);
				break;

			case 1:
				no_select_image.setVisibility(View.GONE);
				man_select_image.setVisibility(View.GONE);
				woman_select_image.setVisibility(View.VISIBLE);
				break;

			default:
				break;

		}

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
				convertView = View.inflate(RegionAndSexSelectFragment.this.getActivity(), R.layout.common_select_list_frag_item, null);
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

				adapter1 = new LabelAdapter1(select_parent_position);
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
				convertView = View.inflate(RegionAndSexSelectFragment.this.getActivity(), R.layout.common_select_list_frag_item, null);
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

			if (curCityType.equalsIgnoreCase(key)) {

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

	private class SexAdapter1 extends BaseAdapter {
		private GlobalData gd;

		private ArrayList<Integer> sexs;

		public SexAdapter1() {

			gd = MainApp.getInstance().getGlobalData();
			sexs = gd.getSexKeyList(true);
		}

		@Override
		public int getCount() {
			return sexs.size();
		}

		@Override
		public Object getItem(int position) {
			return sexs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(RegionAndSexSelectFragment.this.getActivity(), R.layout.common_select_list_frag_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				holder.selected = (ImageView) convertView.findViewById(R.id.common_select_item_image);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}

			String name = gd.getSexName(sexs.get(position));
			holder.nameTv.setText(name);


			if (curSexType == sexs.get(position)) {

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

	public interface onRegionAndSexSelectListener {
		void onRegionAndSexSelect(String newRegion,int newSex);
	}

}
