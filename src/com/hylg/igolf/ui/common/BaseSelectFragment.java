package com.hylg.igolf.ui.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.hylg.igolf.utils.Utils;

import java.util.ArrayList;

public abstract class BaseSelectFragment extends Fragment {
	private static final String TAG = "BaseSelectFragment";

	private final static String BUNDLE_CURR_TYPE = "current_type";
	public Object curType;


	public ArrayList labels;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.common_frg_list_select,null);

		getDataFrom();
		getViews(view);

		return view;
	}

	abstract void getDataFrom() ;
	
	private void getViews(View parent_view) {
		TextView title = (TextView) parent_view.findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_label);
		ListView lv = (ListView) parent_view.findViewById(R.id.comm_dialog_list);
		final LabelAdapter adapter = new LabelAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				Object value = adapter.getItem(pos);
				Utils.logh(TAG, " ------- pos : " + pos + " value: " + value + " curType: " + curType);
				if (!curType.equals(value)) {

					onSelect(value);
				}
				finish();
			}
		});

		LinearLayout bg_linear = (LinearLayout)parent_view.findViewById(R.id.comm_dialog_bg);
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
	
	private class LabelAdapter extends BaseAdapter {
		private GlobalData gd;

		public LabelAdapter() {
			gd = MainApp.getInstance().getGlobalData();
			labels = getData () ;
		}
		
		@Override
		public int getCount() {
			return labels.size();
		}

		@Override
		public Object getItem(int position) {
			return labels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(BaseSelectFragment.this.getActivity(), R.layout.common_select_list_frag_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				holder.selected = (ImageView) convertView.findViewById(R.id.common_select_item_image);
				convertView.setTag(holder);
			}
			else {
				holder = (SexViewHolder) convertView.getTag();
			}

			holder.nameTv.setText(getDataItem(position));


			if (curType != labels.get(position)) {

				holder.nameTv.setTextColor(getResources().getColor(R.color.gray));
				holder.selected.setVisibility(View.GONE);
			}
			else {

				holder.nameTv.setTextColor(getResources().getColor(R.color.color_friend_item_praiser_name));
				holder.selected.setVisibility(View.VISIBLE);
			}
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;

			protected ImageView selected;
		}
	}


	abstract ArrayList getData () ;

	abstract String getDataItem (Object positon) ;

	abstract void onSelect(Object selectValu) ;

	abstract void finish() ;

}
