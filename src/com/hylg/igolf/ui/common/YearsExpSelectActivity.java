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

public class YearsExpSelectActivity extends Activity {
	private static final String TAG = "YearsExpSelectActivity";
	private static onYearsExpSelectListener listener = null;
	private final static String BUNDLE_CUR_YEARS_EXP = "current_years_exp";
	private int curYearsExp;

	public static void startYearsExpSelect(Context context, int curYearsExp) {
		try {
			listener = (onYearsExpSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onYearsExpSelectListener");
		}
		Intent intent = new Intent(context, YearsExpSelectActivity.class);
		intent.putExtra(BUNDLE_CUR_YEARS_EXP, curYearsExp);
		context.startActivity(intent);
	}

	public static void startYearsExpSelect(Fragment fragment, int curYearsExp) {
		try {
			listener = (onYearsExpSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements onYearsExpSelectListener");
		}
		Intent intent = new Intent(fragment.getActivity(), YearsExpSelectActivity.class);
		intent.putExtra(BUNDLE_CUR_YEARS_EXP, curYearsExp);
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
		curYearsExp = getIntent().getExtras().getInt(BUNDLE_CUR_YEARS_EXP);
		Utils.logh(TAG, " curYearsExp: " + curYearsExp);
	}

	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_years_exp);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final AgeAdapter adapter = new AgeAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
				int value = (Integer) adapter.getItem(pos);
				Utils.logh(TAG, " ------- pos : " + pos + " value: " + value + " curYearsExp: " + curYearsExp);
				if(curYearsExp != value) {
					listener.onYearsExpSelect(value);				
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
		private ArrayList<Integer> yearsExps;
		
		public AgeAdapter() {
			yearsExps = new ArrayList<Integer>();
			for(int i=0; i<=60; i++) {
				yearsExps.add(i);
			}
		}
		
		@Override
		public int getCount() {
			return yearsExps.size();
		}

		@Override
		public Object getItem(int position) {
			return yearsExps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				
				convertView = View.inflate(YearsExpSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
				
			} else {
				
				holder = (SexViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(String.format(getString(R.string.str_account_yearsexp_wrap), yearsExps.get(position)));
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface onYearsExpSelectListener {
		public abstract void onYearsExpSelect(int newYearsExp);
	}
}
