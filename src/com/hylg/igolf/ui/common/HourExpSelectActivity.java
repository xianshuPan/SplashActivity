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

import com.hylg.igolf.R;
import com.hylg.igolf.utils.Utils;

public class HourExpSelectActivity extends Activity {
	private static final String TAG = "HourExpSelectActivity";
	private static onHourExpSelectListener listener = null;
	private final static String BUNDLE_CUR_Hour_EXP = "current_Hour_exp";
	private int curHourExp;

	public static void startHourExpSelect(Context context, int curHourExp) {
		try {
			listener = (onHourExpSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onHourExpSelectListener");
		}
		Intent intent = new Intent(context, HourExpSelectActivity.class);
		intent.putExtra(BUNDLE_CUR_Hour_EXP, curHourExp);
		context.startActivity(intent);
	}

	public static void startHourExpSelect(Fragment fragment, int curHourExp) {
		try {
			listener = (onHourExpSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements onHourExpSelectListener");
		}
		Intent intent = new Intent(fragment.getActivity(), HourExpSelectActivity.class);
		intent.putExtra(BUNDLE_CUR_Hour_EXP, curHourExp);
		fragment.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_ac_list_select);
		setFinishOnTouchOutside(true);
		getData();
		getViews();
	}

	private void getData() {
		curHourExp = getIntent().getExtras().getInt(BUNDLE_CUR_Hour_EXP);
		Utils.logh(TAG, " curHourExp: " + curHourExp);
	}

	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_hour_exp);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final AgeAdapter adapter = new AgeAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
				int value = (Integer) adapter.getItem(pos);
				Utils.logh(TAG, " ------- pos : " + pos + " value: " + value + " curHourExp: " + curHourExp);
				if(curHourExp != value) {
					listener.onHourExpSelect(value);				
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
	
	private class AgeAdapter extends BaseAdapter {
		private ArrayList<Integer> HourExps;
		
		public AgeAdapter() {
			HourExps = new ArrayList<Integer>();
			for(int i=1; i<=8; i++) {
				HourExps.add(i);
			}
		}
		
		@Override
		public int getCount() {
			return HourExps.size();
		}

		@Override
		public Object getItem(int position) {
			return HourExps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				
				convertView = View.inflate(HourExpSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
				
			} else {
				
				holder = (SexViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(String.format(getString(R.string.str_account_hoursexp_wrap), HourExps.get(position)));
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface onHourExpSelectListener {
		void onHourExpSelect(int newHourExp);
	}
}
