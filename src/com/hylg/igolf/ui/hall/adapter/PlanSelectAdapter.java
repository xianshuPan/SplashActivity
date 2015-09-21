package com.hylg.igolf.ui.hall.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.PlanShowInfo;
import com.hylg.igolf.utils.Utils;

public class PlanSelectAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<PlanShowInfo> plans;
	private int curSelect;
	private boolean selectable;

	public PlanSelectAdapter(Context context, ArrayList<PlanShowInfo> plans) {
		this.context = context;
		this.plans = plans;
		curSelect = 0;
		selectable = true;
	}
	
	public PlanShowInfo getSelectedPlan() {
		return plans.get(curSelect);
	}
	
	public int getSelectedPlanIndex() {
		return getSelectedPlan().index;
	}
	
	/**
	 * 操作导致状态变化后，临时设置不可选择。
	 * 退出再进入时，根据实际状态更新页面。
	 * @param able
	 */
	public void setSelectable(boolean able) {
		selectable = able;
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
			convertView = View.inflate(context, R.layout.plan_select_item, null);
			holder.itemCourse = (TextView) convertView.findViewById(R.id.plan_select_course);
			holder.itemTeetime = (TextView) convertView.findViewById(R.id.plan_select_teetime);
			holder.itemImg = (ImageView) convertView.findViewById(R.id.plan_select_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PlanShowInfo plan = plans.get(position);
		holder.itemCourse.setText(plan.courseName);
		holder.itemTeetime.setText(plan.teeTime);
		if(curSelect == position) {
			Utils.setVisible(holder.itemImg);
		} else {
			Utils.setInvisible(holder.itemImg);
		}
		convertView.setOnClickListener(new onPlanItemClickListener(position));
		return convertView;
	}
	
	private class onPlanItemClickListener implements OnClickListener {
		private int position;
		
		public onPlanItemClickListener(int position) {
			this.position = position;
		}
		@Override
		public void onClick(View v) {
			if(!selectable || curSelect == position) {
				return ;
			}
			changeSelectStatus(position);
		}
		
	}
	
	class ViewHolder {
		protected TextView itemCourse;
		protected TextView itemTeetime;
		protected ImageView itemImg;
	}

	private void changeSelectStatus(int position) {
		curSelect = position;
		notifyDataSetChanged();
	}
	
}
