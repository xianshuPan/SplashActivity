package com.hylg.igolf.ui.golfers.adapter;

import java.util.ArrayList;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hylg.igolf.R;

public class LabelAdapter extends BaseAdapter {
	private FragmentActivity context;
	private OnLabelItemClickListener listener;
	private ArrayList<LabelData> labels;
	
	public LabelAdapter(FragmentActivity context, OnLabelItemClickListener listener) {
		this.context = context;
		this.listener = listener;
		labels = new ArrayList<LabelData>();
		Resources res = context.getResources();
		String[] names = res.getStringArray(R.array.golfers_label_name_array);
		TypedArray imgTa = res.obtainTypedArray(R.array.golfers_label_img_array);
		TypedArray typeTa = res.obtainTypedArray(R.array.golfers_label_type_array);
		int len = names.length;
		for(int i=0; i<len; i++) {
			LabelData data = new LabelData();
			data.type = typeTa.getInt(i, 0);
			data.srcId = imgTa.getResourceId(i, R.drawable.ic_launcher);
			data.name = names[i];
			labels.add(data);
		}
		imgTa.recycle();
		typeTa.recycle();
	}
	
	public void onMaskGuideClick() {
		if(null != listener) {
			listener.onLabelItemClick(labels.get(0).type, isLabelIndustry(labels.get(0).type));
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LabelViewHolder holder;
		if(null == convertView) {
			holder = new LabelViewHolder();
			convertView = View.inflate(context, R.layout.golfers_label_item, null);
			holder.image = (ImageView) convertView.findViewById(R.id.golfers_label_item_img);
			holder.name = (TextView) convertView.findViewById(R.id.golfers_label_item_name);
			convertView.setTag(holder);
		} else {
			holder = (LabelViewHolder) convertView.getTag();
		}
		LabelData data = labels.get(position);
		holder.image.setImageResource(data.srcId);
		holder.name.setText(data.name);
		convertView.setOnClickListener(new onLabelClickListener(data.type));
		return convertView;
	}

	public static boolean isLabelIndustry(int type) {
		return (type == 5);
	}
	
	@Override
	public int getCount() {
		return labels.size();
	}

	@Override
	public Object getItem(int position) {
		return labels.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private class LabelViewHolder {
		ImageView image;
		TextView name;
	}
	
	private class onLabelClickListener implements OnClickListener {
		private int type;
		public onLabelClickListener(int type) {
			this.type = type;
		}
		@Override
		public void onClick(View view) {
			if(null != listener) {
				listener.onLabelItemClick(type, isLabelIndustry(type));
			}
		}
	}

	public interface OnLabelItemClickListener {
		public abstract void onLabelItemClick(int type, boolean labelIndustry);
	}
	
	private class LabelData {
		public int type;
		public int srcId;
		public String name;
	}
}

