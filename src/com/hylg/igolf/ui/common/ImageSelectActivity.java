package com.hylg.igolf.ui.common;

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

import com.hylg.igolf.R;
import com.hylg.igolf.utils.Utils;

public class ImageSelectActivity extends Activity {
	private static final String TAG = "ImageSelectActivity";
	private static onImageSelectListener listener = null;
	// 与image_select_name_array顺序保持一致
	public final static int IMAGE_SELECT_TYPE_CAMERA = 0;
	public final static int IMAGE_SELECT_TYPE_GALLERY = 1;

	public static void startImageSelect(Context context) {
		try {
			listener = (onImageSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onImageSelectListener");
		}
		Intent intent = new Intent(context, ImageSelectActivity.class);
		context.startActivity(intent);
	}

	public static void startImageSelect(Fragment fragment) {
		try {
			listener = (onImageSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements onImageSelectListener");
		}
		Intent intent = new Intent(fragment.getActivity(), ImageSelectActivity.class);
		fragment.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_ac_list_select);
		setFinishOnTouchOutside(true);
		getViews();
	}

	private void getViews() {
		TextView title = (TextView) findViewById(R.id.comm_dialog_title);
		title.setText(R.string.str_dialog_select_title_image);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final AgeAdapter adapter = new AgeAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				Utils.logh(TAG, " ------- pos : " + pos);
				switch(pos) {
					case IMAGE_SELECT_TYPE_CAMERA:
						listener.onCameraTypeSelect();
						break;
					case IMAGE_SELECT_TYPE_GALLERY:
						listener.onGalleryTypeSelect();
						break;
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
	
	private class AgeAdapter extends BaseAdapter {
		private String[] types;
		
		public AgeAdapter() {
			types = getResources().getStringArray(R.array.image_select_name_array);
		}
		
		@Override
		public int getCount() {
			return types.length;
		}

		@Override
		public Object getItem(int position) {
			return types[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SexViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(ImageSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new SexViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (SexViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(types[position]);
			
			return convertView;
		}
		
		private class SexViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface onImageSelectListener {
		public abstract void onCameraTypeSelect();
		public abstract void onGalleryTypeSelect();
	}
}
