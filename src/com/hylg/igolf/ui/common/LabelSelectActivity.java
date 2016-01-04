package com.hylg.igolf.ui.common;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;

public class LabelSelectActivity extends Activity {
	private static final String TAG = "LabelSelectActivity";
	private static onLabelSelectListener listener = null;
	private final static String BUNDLE_CURR_TYPE = "current_type";
	private int curType;

	public static void startLabelSelect(Context context, int curType) {
		try {
			listener = (onLabelSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onLabelSelectListener");
		}
		Intent intent = new Intent(context, LabelSelectActivity.class);
		intent.putExtra(BUNDLE_CURR_TYPE, curType);
		context.startActivity(intent);
	}

	public static void startLabelSelect(Fragment fragment, int curType) {
		try {
			listener = (onLabelSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onLabelSelectListener");
		}
		Intent intent = new Intent(fragment.getActivity(), LabelSelectActivity.class);
		intent.putExtra(BUNDLE_CURR_TYPE, curType);
		fragment.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_ac_list_select);
		getData();
		getViews();
	}

	private void getData() {
		curType = getIntent().getExtras().getInt(BUNDLE_CURR_TYPE);
		Utils.logh(TAG, " curType: " + curType);
	}
	
	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_label);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final LabelAdapter adapter = new LabelAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
				int value = (Integer) adapter.getItem(pos);
				Utils.logh(TAG, " ------- pos : " + pos + " value: " + value + " curType: " + curType);
				if(curType != value) {
					listener.onLabelSelect(value);
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
	
	private class LabelAdapter extends BaseAdapter {
		private GlobalData gd;
		private ArrayList<Integer> labels;

		public LabelAdapter() {
			gd = MainApp.getInstance().getGlobalData();
			labels = gd.getLabelKeyList();
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
				convertView = View.inflate(LabelSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(gd.getLabelName(labels.get(position)));
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface onLabelSelectListener {
		public abstract void onLabelSelect(int newLabel);
	}
}
