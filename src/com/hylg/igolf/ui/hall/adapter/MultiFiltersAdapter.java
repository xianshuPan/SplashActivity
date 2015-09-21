package com.hylg.igolf.ui.hall.adapter;

import java.util.ArrayList;

import com.hylg.igolf.R;
import com.hylg.igolf.ui.hall.data.MultiFilterData;
import com.hylg.igolf.utils.Utils;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MultiFiltersAdapter extends BaseAdapter {
	private static final String TAG = "MultiFiltersAdapter";
	private ArrayList<MultiFilterData> filters;
	private String[] curValues;
	private FragmentActivity context;
	private OnMultiFilterItemClickListener listener;
	
	public MultiFiltersAdapter(FragmentActivity context, OnMultiFilterItemClickListener listener, String... args) {
		this.context = context;
		this.listener = listener;
		String [] items = args;
		final int len = items.length;
		curValues = new String[len];
		filters = new ArrayList<MultiFilterData>();
		Resources res = context.getResources();
		String[] titles = res.getStringArray(R.array.open_preset_title_array);
		TypedArray imgTa = res.obtainTypedArray(R.array.open_preset_img_array);
		for(int i=0; i<len; i++) {
			MultiFilterData data = new MultiFilterData();
			data.srcId = imgTa.getResourceId(i, R.drawable.ic_launcher);
			data.title = titles[i];
			filters.add(data);
			curValues[i] = items[i];
		}
		imgTa.recycle();
	}
	
	public void refreshTeeDate(String teeDate) {
		curValues[0] = teeDate;
		notifyDataSetChanged();
	}
	
	public void refreshTeeTime(String teeTime) {
		curValues[1] = teeTime;
		notifyDataSetChanged();
	}
	
	public void refreshRegion(String region) {
		curValues[2] = region;
		notifyDataSetChanged();
	}
	
	public void refreshSex(String sex) {
		curValues[3] = sex;
		notifyDataSetChanged();
	}
	
	public void refreshPayType(String payType) {
		curValues[4] = payType;
		notifyDataSetChanged();
	}
	
	public void refreshStake(String stake) {
		Utils.logh(TAG, "refreshStake stake: " + stake + " curValues[5]: " + curValues[5]);
		curValues[5] = stake;
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FilterViewHolder holder;
		if(null == convertView) {
			holder = new FilterViewHolder();
			convertView = View.inflate(context, R.layout.hall_open_filter_item, null);
			holder.image = (ImageView) convertView.findViewById(R.id.hall_open_filter_item_img);
			holder.content = (TextView) convertView.findViewById(R.id.hall_open_filter_item_content);
			holder.title = (TextView) convertView.findViewById(R.id.hall_open_filter_item_title);
			holder.titleHl = (TextView) convertView.findViewById(R.id.hall_open_filter_item_title_hl);
			holder.divLine = convertView.findViewById(R.id.hall_open_filter_item_line);
			holder.divHLine = convertView.findViewById(R.id.hall_open_filter_item_line_hl);
			convertView.setTag(holder);
		} else {
			holder = (FilterViewHolder) convertView.getTag();
		}
		MultiFilterData data = filters.get(position);
		if(position < 3) {
			convertView.setBackgroundResource(R.drawable.hall_filter_hl_bkg);
			Utils.setVisible(holder.titleHl, holder.divHLine);
			Utils.setGone(holder.title, holder.divLine);
			holder.titleHl.setText(data.title);
		} else {
			convertView.setBackgroundResource(R.drawable.hall_filter_bkg);
			Utils.setVisible(holder.title, holder.divLine);
			Utils.setGone(holder.titleHl, holder.divHLine);
			holder.title.setText(data.title);
		}
		holder.image.setImageResource(data.srcId);
		holder.content.setText(curValues[position]);
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
		TextView titleHl;
		View divLine;
		View divHLine;
	}
	
	private class onFilterClickListener implements OnClickListener {
		private int position;
		public onFilterClickListener(int position) {
			this.position = position;
		}
		@Override
		public void onClick(View view) {
			if(null != listener) {
				listener.onMultiFilterItemClick(position);
			}
		}
	}

	public interface OnMultiFilterItemClickListener {
		public abstract void onMultiFilterItemClick(int position);
	}
}