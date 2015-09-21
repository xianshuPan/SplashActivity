package com.hylg.igolf.ui.hall.adapter;

import java.util.ArrayList;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.utils.GlobalData;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;

public class PayTypeSelectAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Integer> values;
	private int selectIndex;
	private GlobalData gd;

	public PayTypeSelectAdapter(Context context) {
		gd = MainApp.getInstance().getGlobalData();
		values = gd.getPayTypeKeyList(false);
		this.context = context;
		selectIndex = 0;
	}

	public int getSelectValue() {
		return values.get(selectIndex);
	}
	
	private void changeSelectStatus(int position) {
		selectIndex = position;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return values.size();
	}

	@Override
	public Object getItem(int position) {
		return values.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(null == convertView) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.invite_select_item, null);
			holder.itemBtn = (Button) convertView.findViewById(R.id.invite_select_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.itemBtn.setText(gd.getPayTypeName(values.get(position)));
		if(position == selectIndex) {
			holder.itemBtn.setSelected(true);
		} else {
			holder.itemBtn.setSelected(false);
		}
		holder.itemBtn.setOnClickListener(new onPaymentItemClickListener(position));
		return convertView;
	}
	
	private class onPaymentItemClickListener implements OnClickListener {
		private int position;
		
		public onPaymentItemClickListener(int position) {
			this.position = position;
		}
		@Override
		public void onClick(View v) {
			changeSelectStatus(position);
		}
		
	}
	class ViewHolder {
		protected Button itemBtn;
	}
	
//	public interface onPaymentClickListener {
//		public abstract void onPaymentItemClicked(int value);
//	}
}
