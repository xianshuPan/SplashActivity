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

public class SexSelectActivity extends Activity {
	private static final String TAG = "SexSelectActivity";
	private static onSexSelectListener listener = null;
	private final static String BUNDLE_SEX_EXTRA = "sex_extra";
	private final static String BUNDLE_CUR_SEX = "current_sex";
	private boolean extraSex;
	private int curSex;

	public static void startSexSelect(Context context, boolean extra, int curSex) {
		try {
			listener = (onSexSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onSexSelectListener");
		}
		Intent intent = new Intent(context, SexSelectActivity.class);
		intent.putExtra(BUNDLE_SEX_EXTRA, extra);
		intent.putExtra(BUNDLE_CUR_SEX, curSex);
		context.startActivity(intent);
	}

	public static void startSexSelect(Fragment fragment, boolean extra, int curSex) {
		try {
			listener = (onSexSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements onSexSelectListener");
		}
		Intent intent = new Intent(fragment.getActivity(), SexSelectActivity.class);
		intent.putExtra(BUNDLE_SEX_EXTRA, extra);
		intent.putExtra(BUNDLE_CUR_SEX, curSex);
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
		extraSex = getIntent().getExtras().getBoolean(BUNDLE_SEX_EXTRA);
		curSex = getIntent().getExtras().getInt(BUNDLE_CUR_SEX);
		Utils.logh(TAG, " extraSex: " + extraSex + " curSex: " + curSex);
	}

	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_sex);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final SexAdapter adapter = new SexAdapter(extraSex);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
				int value = (Integer) adapter.getItem(pos);
				Utils.logh(TAG, " ------- pos : " + pos + " value: " + value + " curSex: " + curSex);
				if(curSex != value) {
					listener.onSexSelect(value);				
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
	
	private class SexAdapter extends BaseAdapter {
		private GlobalData gd;
		private ArrayList<Integer> sexs;
		
		public SexAdapter(boolean extra) {
			gd = MainApp.getInstance().getGlobalData();
			sexs = gd.getSexKeyList(extra);
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
				convertView = View.inflate(SexSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(gd.getSexName(sexs.get(position)));
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface onSexSelectListener {
		public abstract void onSexSelect(int newSex);
	}
}
