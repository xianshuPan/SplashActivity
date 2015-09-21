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

public class PayTypeSelectActivity extends Activity {
	private static final String TAG = "PayTypeSelectActivity";
	private static onPayTypeSelectListener listener = null;
	private final static String BUNDLE_PAY_TYPE_EXTRA = "pay_type_extra";
	private final static String BUNDLE_CUR_PAY_TYPE = "current_pay_type";
	private boolean extraPayType;
	private int curPayType;

	public static void startPayTypeSelect(Context context, boolean extra, int curPayType) {
		try {
			listener = (onPayTypeSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onPayTypeSelectListener");
		}
		Intent intent = new Intent();
		intent.setClass(context, PayTypeSelectActivity.class);
		intent.putExtra(BUNDLE_PAY_TYPE_EXTRA, extra);
		intent.putExtra(BUNDLE_CUR_PAY_TYPE, curPayType);
		context.startActivity(intent);
	}

	public static void startPayTypeSelect(Fragment fragment, boolean extra, int curPayType) {
		try {
			listener = (onPayTypeSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements onPayTypeSelectListener");
		}
		Intent intent = new Intent(fragment.getActivity(), PayTypeSelectActivity.class);
		intent.putExtra(BUNDLE_PAY_TYPE_EXTRA, extra);
		intent.putExtra(BUNDLE_CUR_PAY_TYPE, curPayType);
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
		extraPayType = getIntent().getExtras().getBoolean(BUNDLE_PAY_TYPE_EXTRA);
		curPayType = getIntent().getExtras().getInt(BUNDLE_CUR_PAY_TYPE);
		Utils.logh(TAG, " extraPayType: " + extraPayType + " curPayType: " + curPayType);
	}

	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_paytype);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final PayTypeAdapter adapter = new PayTypeAdapter(extraPayType);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,long id) {
				int value = (Integer) adapter.getItem(pos);
				Utils.logh(TAG, " ------- pos : " + pos + " value: " + value + " curPayType: " + curPayType);
				if(curPayType != value) {
					listener.onPayTypeSelect(value);
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
	
	private class PayTypeAdapter extends BaseAdapter {
		private GlobalData gd;
		private ArrayList<Integer> payTypes;
		
		public PayTypeAdapter(boolean extra) {
			gd = MainApp.getInstance().getGlobalData();
			payTypes = gd.getPayTypeKeyList(extra);
		}
		
		@Override
		public int getCount() {
			return payTypes.size();
		}

		@Override
		public Object getItem(int position) {
			return payTypes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(PayTypeSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(gd.getPayTypeName(payTypes.get(position)));
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface onPayTypeSelectListener {
		public abstract void onPayTypeSelect(int newType);
	}
}
