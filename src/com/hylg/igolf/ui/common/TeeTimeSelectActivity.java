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

public class TeeTimeSelectActivity extends Activity {
	private static final String TAG = "TeeTimeSelectActivity";
	private static onTeeTimeSelectListener listener = null;
	private final static String BUNDLE_TEET_EXTRA = "tee_time_extra";
	private final static String BUNDLE_CUR_TEET = "current_tee_time";
	private boolean extraTeeTime;
	private int curTeeTime;

	public static void startTeeDateSelect(Context context, boolean extra, int curTeeTime) {
		try {
			listener = (onTeeTimeSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onTeeTimeSelectListener");
		}
		Intent intent = new Intent(context, TeeTimeSelectActivity.class);
		intent.putExtra(BUNDLE_TEET_EXTRA, extra);
		intent.putExtra(BUNDLE_CUR_TEET, curTeeTime);
		context.startActivity(intent);
	}

	public static void startTeeDateSelect(Fragment fragment, boolean extra, int curTeeTime) {
		try {
			listener = (onTeeTimeSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements onTeeTimeSelectListener");
		}
		Intent intent = new Intent(fragment.getActivity(), TeeTimeSelectActivity.class);
		intent.putExtra(BUNDLE_TEET_EXTRA, extra);
		intent.putExtra(BUNDLE_CUR_TEET, curTeeTime);
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
		extraTeeTime = getIntent().getExtras().getBoolean(BUNDLE_TEET_EXTRA);
		curTeeTime = getIntent().getExtras().getInt(BUNDLE_CUR_TEET);
		Utils.logh(TAG, " extraTeeTime: " + extraTeeTime + " curTeeTime: " + curTeeTime);
	}

	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_tee_time);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final TeeDateAdapter adapter = new TeeDateAdapter(extraTeeTime);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
				int value = (Integer) adapter.getItem(pos);
				Utils.logh(TAG, " ------- pos : " + pos + " value: " + value + " curTeeTime: " + curTeeTime);
				if(curTeeTime != value) {
					listener.onTeeTimeSelect(value);				
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
	
	private class TeeDateAdapter extends BaseAdapter {
		private GlobalData gd;
		private ArrayList<Integer> teeTimes;
		
		public TeeDateAdapter(boolean extra) {
			gd = MainApp.getInstance().getGlobalData();
			teeTimes = gd.getTeeTimeKeyList(extra);
		}
		
		@Override
		public int getCount() {
			return teeTimes.size();
		}

		@Override
		public Object getItem(int position) {
			return teeTimes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(TeeTimeSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(gd.getTeeTimeName(teeTimes.get(position)));
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface onTeeTimeSelectListener {
		public abstract void onTeeTimeSelect(int newTeeTime);
	}
}
