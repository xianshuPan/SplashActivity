package com.hylg.igolf.imagepicker;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.hylg.igolf.R;
import com.xc.lib.imageloader.ImageLoader;
import com.xc.lib.layout.LayoutUtils;

public class AddImageGridAdapter extends BaseAdapter {
	private Context context;
	private List<String> datas;
	private ImageLoader loader;
	private int size;
	
	private GridView list ;

	public AddImageGridAdapter(Context context, List<String> datas, GridView list) {
		this.context = context;
		this.datas = datas;
		loader = new ImageLoader("root");
		size = LayoutUtils.getRate4px(140);
		
		this.list = list;
	}

	@Override
	public int getCount() {
		
		int size =  datas.size() > 9 ? 9 :  datas.size();
		
		return datas == null ? 0 : size + 1;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.addimage_grid, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
			
			viewHolder.deleteImage = (ImageView) convertView.findViewById(R.id.delete_image);
			
			LayoutUtils.rateScale(context, viewHolder.imageView, true);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.deleteImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				int index = position;
				
				if (datas != null && index < datas.size() && index >= 0) {
					
					datas.remove(index);
					//list.getAdapter().notify();
					//this.notify();
				}
				
			}
		});
		
		if (position == datas.size()) {
			
			viewHolder.deleteImage.setVisibility(View.GONE);
			viewHolder.imageView.setImageResource(R.drawable.addpic);
			if (position == Config.SELECT_MAX_NUM) {
				viewHolder.imageView.setVisibility(View.GONE);
			}
		} else {
			
			viewHolder.deleteImage.setVisibility(View.VISIBLE);
			 loader.displayImage(datas.get(position), viewHolder.imageView,
			 size, size, R.drawable.ic_launcher);
			 
		}
		
		if (position >= Config.SELECT_MAX_NUM) {
			
			convertView.setVisibility(View.GONE);
		}

		return convertView;
	}

	public static class ViewHolder {
		public ImageView imageView;
		
		public ImageView deleteImage;
 
	}

	public void setData(List<String> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	public List<String> getData() {
		return this.datas;
	}

}
