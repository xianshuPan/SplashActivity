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

public class IndustrySelectActivity extends Activity {
	private static final String TAG = "IndustrySelectActivity";
	private static onIndustrySelectListener listener = null;
	private final static String BUNDLE_INDUSTRY_EXTRA = "industry_extra";
	private final static String BUNDLE_CUR_INDUSTRY = "current_industry";
	private boolean extraIndustry;
	private String curIndustry;

	public static void startIndustrySelect(Context context, boolean extraIndustry, String curIndustry) {
		try {
			listener = (onIndustrySelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onIndustrySelectListener");
		}
		Intent intent = new Intent(context, IndustrySelectActivity.class);
		intent.putExtra(BUNDLE_INDUSTRY_EXTRA, extraIndustry);
		intent.putExtra(BUNDLE_CUR_INDUSTRY, curIndustry);
		context.startActivity(intent);
	}
	
	public static void startIndustrySelect(Fragment fragment, boolean extraIndustry, String curIndustry) {
		try {
			listener = (onIndustrySelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements onIndustrySelectListener");
		}
		Intent intent = new Intent(fragment.getActivity(), IndustrySelectActivity.class);
		intent.putExtra(BUNDLE_INDUSTRY_EXTRA, extraIndustry);
		intent.putExtra(BUNDLE_CUR_INDUSTRY, curIndustry);
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
		extraIndustry = getIntent().getExtras().getBoolean(BUNDLE_INDUSTRY_EXTRA);
		curIndustry = getIntent().getExtras().getString(BUNDLE_CUR_INDUSTRY);
		Utils.logh(TAG, " extraIndustry: " + extraIndustry + " curIndustry: " + curIndustry);
	}


	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_industry);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final IndustryAdapter adapter = new IndustryAdapter(extraIndustry);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
				String key = (String) adapter.getItem(pos);
				Utils.logh(TAG, " ------- pos : " + pos + " key: " + key + " curIndustry: " + curIndustry);
				if(!curIndustry.equals(key)) {
					listener.onIndustrySelect(key);				
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
	
	private class IndustryAdapter extends BaseAdapter {
		private GlobalData gd;
		private ArrayList<String> industrys;
		
		public IndustryAdapter(boolean extra) {
			gd = MainApp.getInstance().getGlobalData();
			industrys = gd.getIndustryKeyList(extra);
		}
		
		@Override
		public int getCount() {
			return industrys.size();
		}

		@Override
		public Object getItem(int position) {
			return industrys.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			IndustryViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(IndustrySelectActivity.this, R.layout.common_select_list_item, null);
				holder = new IndustryViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (IndustryViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(gd.getIndustryName(industrys.get(position)));
			
			return convertView;
		}
		
		private class IndustryViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface onIndustrySelectListener {
		public abstract void onIndustrySelect(String newIndustry);
	}
}
