package com.hylg.igolf.ui.hall.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.PlanShowInfo;

public class PlanShowAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<PlanShowInfo> plans;

	public PlanShowAdapter(Context context, ArrayList<PlanShowInfo> plans) {
		this.context = context;
		this.plans = plans;
	}
	
	@Override
	public int getCount() {
		return plans.size();
	}

	@Override
	public Object getItem(int position) {
		return plans.get(position);
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
			convertView = View.inflate(context, R.layout.plan_show_item, null);
			holder.itemCourse = (TextView) convertView.findViewById(R.id.plan_show_course);
			holder.itemTeetime = (TextView) convertView.findViewById(R.id.plan_show_teetime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PlanShowInfo plan = plans.get(position);
		holder.itemCourse.setText(plan.courseName);
		holder.itemTeetime.setText(plan.teeTime);
		return convertView;
	}
	
	class ViewHolder {
		protected TextView itemCourse;
		protected TextView itemTeetime;
	}
	
}
