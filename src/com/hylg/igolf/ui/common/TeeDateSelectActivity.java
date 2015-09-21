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

public class TeeDateSelectActivity extends Activity {
	private static final String TAG = "TeeDateSelectActivity";
	private static onTeeDateSelectListener listener = null;
	private final static String BUNDLE_TEED_EXTRA = "tee_date_extra";
	private final static String BUNDLE_CUR_TEED = "current_tee_date";
	private boolean extraTeeDate;
	private int curTeeDate;

	public static void startTeeDateSelect(Context context, boolean extra, int curTeeDate) {
		try {
			listener = (onTeeDateSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onTeeDateSelectListener");
		}
		Intent intent = new Intent(context, TeeDateSelectActivity.class);
		intent.putExtra(BUNDLE_TEED_EXTRA, extra);
		intent.putExtra(BUNDLE_CUR_TEED, curTeeDate);
		context.startActivity(intent);
	}

	public static void startTeeDateSelect(Fragment fragment, boolean extra, int curTeeDate) {
		try {
			listener = (onTeeDateSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements onTeeDateSelectListener");
		}
		Intent intent = new Intent(fragment.getActivity(), TeeDateSelectActivity.class);
		intent.putExtra(BUNDLE_TEED_EXTRA, extra);
		intent.putExtra(BUNDLE_CUR_TEED, curTeeDate);
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
		extraTeeDate = getIntent().getExtras().getBoolean(BUNDLE_TEED_EXTRA);
		curTeeDate = getIntent().getExtras().getInt(BUNDLE_CUR_TEED);
		Utils.logh(TAG, " extraTeeDate: " + extraTeeDate + " curTeeDate: " + curTeeDate);
	}

	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_tee_date);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final TeeDateAdapter adapter = new TeeDateAdapter(extraTeeDate);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
				int value = (Integer) adapter.getItem(pos);
				Utils.logh(TAG, " ------- pos : " + pos + " value: " + value + " curTeeDate: " + curTeeDate);
				if(curTeeDate != value) {
					listener.onTeeDateSelect(value);				
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
		private ArrayList<Integer> teeDates;
		
		public TeeDateAdapter(boolean extra) {
			gd = MainApp.getInstance().getGlobalData();
			teeDates = gd.getTeeDateKeyList(extra);
		}
		
		@Override
		public int getCount() {
			return teeDates.size();
		}

		@Override
		public Object getItem(int position) {
			return teeDates.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(TeeDateSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(gd.getTeeDateName(teeDates.get(position)));
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface onTeeDateSelectListener {
		public abstract void onTeeDateSelect(int newTeeDate);
	}
}
