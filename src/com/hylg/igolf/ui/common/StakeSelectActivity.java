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

public class StakeSelectActivity extends Activity {
	private static final String TAG = "StakeSelectActivity";
	private static onStakeSelectListener listener = null;
	private final static String BUNDLE_STAKE_EXTRA = "stake_extra";
	private final static String BUNDLE_CUR_STAKE = "current_stake";
	private boolean extraStake;
	private int curStake;

	public static void startStakeSelect(Context context, boolean extra, int curStake) {
		try {
			listener = (onStakeSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onStakeSelectListener");
		}
		Intent intent = new Intent(context, StakeSelectActivity.class);
		intent.putExtra(BUNDLE_STAKE_EXTRA, extra);
		intent.putExtra(BUNDLE_CUR_STAKE, curStake);
		context.startActivity(intent);
	}

	public static void startStakeSelect(Fragment fragment, boolean extra, int curStake) {
		try {
			listener = (onStakeSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements onStakeSelectListener");
		}		
		Intent intent = new Intent(fragment.getActivity(), StakeSelectActivity.class);
		intent.putExtra(BUNDLE_STAKE_EXTRA, extra);
		intent.putExtra(BUNDLE_CUR_STAKE, curStake);
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
		extraStake = getIntent().getExtras().getBoolean(BUNDLE_STAKE_EXTRA);
		curStake = getIntent().getExtras().getInt(BUNDLE_CUR_STAKE);
		Utils.logh(TAG, " extraStake: " + extraStake + " curStake: " + curStake);
	}

	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_stake);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final StakeAdapter adapter = new StakeAdapter(extraStake);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
				int value = (Integer) adapter.getItem(pos);
				Utils.logh(TAG, " ------- pos : " + pos + " value: " + value + " curStake: " + curStake);
				if(curStake != value) {
					listener.onStakeSelect(value);					
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
	
	private class StakeAdapter extends BaseAdapter {
		private GlobalData gd;
		private ArrayList<Integer> stakes;
		
		public StakeAdapter(boolean extra) {
			gd = MainApp.getInstance().getGlobalData();
			stakes = gd.getStakeKeyList(extra);
		}
		
		@Override
		public int getCount() {
			return stakes.size();
		}

		@Override
		public Object getItem(int position) {
			return stakes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(StakeSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(gd.getStakeName(stakes.get(position)));
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface onStakeSelectListener {
		public abstract void onStakeSelect(int newStake);
	}
}
