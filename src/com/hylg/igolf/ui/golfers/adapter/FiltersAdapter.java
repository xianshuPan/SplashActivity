package com.hylg.igolf.ui.golfers.adapter;

import java.util.ArrayList;

import com.hylg.igolf.R;
import com.hylg.igolf.ui.golfers.data.FilterData;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FiltersAdapter extends BaseAdapter {
	private FragmentActivity context;
	private OnFilterItemClickListener listener;
	private ArrayList<FilterData> filters;
	
	public FiltersAdapter(FragmentActivity context, OnFilterItemClickListener listener, String... args) {
		this.context = context;
		this.listener = listener;
		String [] items = args;
		final int len = items.length;
		filters = new ArrayList<FilterData>();
		Resources res = context.getResources();
		String[] titles = res.getStringArray(R.array.golfers_filter_title_array);
		TypedArray imgTa = res.obtainTypedArray(R.array.golfers_filter_img_array);
		for(int i=0; i<len; i++) {
			FilterData data = new FilterData();
			data.srcId = imgTa.getResourceId(i, R.drawable.ic_launcher);
			data.title = titles[i];
			data.content = items[i];
			filters.add(data);
		}
		imgTa.recycle();
	}

	public void refreshRegion(String region) {
		filters.get(0).content = region;
		notifyDataSetChanged();
	}
	
	public void refreshIndustry(String industry) {
		filters.get(1).content = industry;
		notifyDataSetChanged();
	}
	
	public void refreshSex(String sex) {
		filters.get(2).content = sex;
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FilterViewHolder holder;
		if(null == convertView) {
			holder = new FilterViewHolder();
			convertView = View.inflate(context, R.layout.golfers_filter_item, null);
			holder.image = (ImageView) convertView.findViewById(R.id.golfers_filter_item_img);
			holder.content = (TextView) convertView.findViewById(R.id.golfers_filter_item_content);
			holder.title = (TextView) convertView.findViewById(R.id.golfers_filter_item_title);
			convertView.setTag(holder);
		} else {
			holder = (FilterViewHolder) convertView.getTag();
		}
		FilterData data = filters.get(position);
		holder.image.setImageResource(data.srcId);
		holder.title.setText(data.title);
		holder.content.setText(data.content);
		convertView.setOnClickListener(new onFilterClickListener(position));
		return convertView;
	}

	@Override
	public int getCount() {
		return filters.size();
	}

	@Override
	public Object getItem(int position) {
		return filters.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private class FilterViewHolder {
		ImageView image;
		TextView content;
		TextView title;
	}
	
	private class onFilterClickListener implements OnClickListener {
		private int position;
		public onFilterClickListener(int position) {
			this.position = position;
		}
		@Override
		public void onClick(View view) {
			if(null != listener) {
				listener.onFilterItemClick(position);
			}
		}
	}

	public interface OnFilterItemClickListener {
		public abstract void onFilterItemClick(int position);
	}

}
