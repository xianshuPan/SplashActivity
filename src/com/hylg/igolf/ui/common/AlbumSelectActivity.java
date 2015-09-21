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

public class AlbumSelectActivity extends Activity {
	private static final String TAG = "AlbumSelectActivity";
	private static OnAlbumSelectListener listener = null;
	// 与album_select_name_array顺序保持一致
	public final static int ALBUM_SELECT_TYPE_AVATAR = 0;
	public final static int ALBUM_SELECT_TYPE_DELETE = 1;

	public static void startAlbumSelect(Context context) {
		try {
			listener = (OnAlbumSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements OnAlbumSelectListener");
		}
		Intent intent = new Intent(context, AlbumSelectActivity.class);
		context.startActivity(intent);
	}

	public static void startImageSelect(Fragment fragment) {
		try {
			listener = (OnAlbumSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements OnAlbumSelectListener");
		}
		Intent intent = new Intent(fragment.getActivity(), AlbumSelectActivity.class);
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
		title.setText(R.string.str_dialog_select_title_album);
		ListView lv = (ListView) findViewById(R.id.comm_dialog_list);
		final AlbumAdapter adapter = new AlbumAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				Utils.logh(TAG, " ------- pos : " + pos);
				switch(pos) {
					case ALBUM_SELECT_TYPE_AVATAR:
						listener.onAvatarSelect();
						break;
					case ALBUM_SELECT_TYPE_DELETE:
						listener.onDeleteSelect();
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
	
	private class AlbumAdapter extends BaseAdapter {
		private String[] types;
		
		public AlbumAdapter() {
			types = getResources().getStringArray(R.array.album_select_name_array);
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
			AlbumViewHolder holder;
			if(null == convertView) {
				convertView = View.inflate(AlbumSelectActivity.this, R.layout.common_select_list_item, null);
				holder = new AlbumViewHolder();
				holder.nameTv = (TextView) convertView.findViewById(R.id.common_select_item_name);
				convertView.setTag(holder);
			} else {
				holder = (AlbumViewHolder) convertView.getTag();
			}
			holder.nameTv.setText(types[position]);
			
			return convertView;
		}
		
		private class AlbumViewHolder {
			protected TextView nameTv;
		}
	}
	
	public interface OnAlbumSelectListener {
		public abstract void onAvatarSelect();
		public abstract void onDeleteSelect();
	}
}
