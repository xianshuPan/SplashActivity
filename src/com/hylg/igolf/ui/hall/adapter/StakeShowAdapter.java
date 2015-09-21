package com.hylg.igolf.ui.hall.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.Utils;

public class StakeShowAdapter extends BaseAdapter {
	private Context context;
	private int selectType;
	private ArrayList<Integer> values;
	private GlobalData gd;
	
	public StakeShowAdapter(Context context, int selectType) {
		gd = MainApp.getInstance().getGlobalData();
		values = gd.getStakeKeyList(false);
		this.context = context;
		this.selectType = selectType;
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
			convertView = View.inflate(context, R.layout.invite_show_item, null);
			holder.itemBtn = (Button) convertView.findViewById(R.id.invite_show_btn);
			holder.selectedImg = (ImageView) convertView.findViewById(R.id.invite_show_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.itemBtn.setText(gd.getStakeName(values.get(position)));
		if(values.get(position) == selectType) {
			holder.itemBtn.setSelected(true);
			Utils.setVisible(holder.selectedImg);
		} else {
			holder.itemBtn.setSelected(false);
			Utils.setGone(holder.selectedImg);
		}
		return convertView;
	}
	
	class ViewHolder {
		protected Button itemBtn;
		protected ImageView selectedImg;
	}

}
