package com.hylg.igolf.imagepicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xc.lib.layout.LayoutUtils;
import com.hylg.igolf.R;
import com.hylg.igolf.imagepicker.BitmapCache.ImageCallback;
import com.hylg.igolf.ui.friend.CameraActivity;
import com.hylg.igolf.utils.FileTool;

public class ImageGridAdapter extends BaseAdapter {

	private TextCallback textcallback = null;
	private final String TAG = getClass().getSimpleName();
	private ImageGridActivity context;
	private List<ImageItem> dataList;
	public Map<String, String> map = new HashMap<String, String>();
	private BitmapCache cache;
	private Handler mHandler;
	private int selectTotal = 0;
	
	private String mBucketName = "";
	
	
	private int index = -1;
	private ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					(imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};

	public  interface TextCallback {
		 void onListen(int count);
	}

	public void setTextCallback(TextCallback listener) {
		textcallback = listener;
	}

	public ImageGridAdapter(ImageGridActivity context, List<ImageItem> dataList,
			Handler mHandler,String bucketName) {
		this.context = context;
		this.dataList = dataList;
		cache = new BitmapCache(context);
		this.mHandler = mHandler;
		
		mBucketName = bucketName;

		if (Config.drr == null) {

			Config.drr = new ArrayList<String>();
		}
	}

	@Override
	public int getCount() {
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class Holder {
		private ImageView iv;
		private ImageView selected;
		private TextView text;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		
		

		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(context, R.layout.item_image_grid, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.isselected);
			holder.text = (TextView) convertView
					.findViewById(R.id.item_image_grid_text);

			LayoutUtils.rateScale(context, holder.iv, true);
			LayoutUtils.rateScale(context, holder.selected, true);
			LayoutUtils.rateScale(context, holder.text, true);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final ImageItem item = dataList.get(position);

		holder.iv.setTag(item.imagePath);
		
		//if (mBucketName.equalsIgnoreCase(Config.ALL_IMAGE) && index == 0) { 
			
			//holder.iv.setImageResource(R.drawable.camera1);
			
		//} else {
			
			cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath,
					callback);
		//}
		
		
		if (item.isSelected) {
			holder.selected.setImageResource(R.drawable.icon_data_select);
			holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
		} else {
			holder.selected.setImageResource(0);
			holder.text.setBackgroundColor(Color.TRANSPARENT);
		}
		holder.iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				int index = position;
				
				if (mBucketName.equalsIgnoreCase(Config.ALL_IMAGE) && index == 0) {
					
					//Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// �����
					//context.startActivityForResult(intent, 101);
					
//					Intent intent = new Intent(context,CameraActivity.class);// �����
//					intent.putExtra("Model", 1);
//					context.startActivityForResult(intent, 101);
//					context.finish();
					
					Intent data = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					
					String mTimeStampStr = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
					
					Config.mTakePictrueFile = FileTool.getInstance(context).getInternalOutputMediaFile(0,mTimeStampStr);
					
					Uri imageFileUri =Uri.parse("file:///"+Config.mTakePictrueFile.getAbsolutePath());
					data.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
					context.startActivityForResult(data, 101);
					
					return ;
				}
				
				
				String path = dataList.get(position).imagePath;
				if ((Config.drr.size() ) < Config.SELECT_MAX_NUM) {
					item.isSelected = !item.isSelected;
					if (item.isSelected) {
						holder.selected
								.setImageResource(R.drawable.icon_data_select);
						holder.text
								.setBackgroundResource(R.drawable.bgd_relatly_line);
						//selectTotal++;


						//map.put(path, path);
						Config.drr.add(path);
						if (textcallback != null)
							textcallback.onListen(Config.drr.size());

					} else {
						holder.selected.setImageResource(-1);
						holder.text.setBackgroundColor(Color.TRANSPARENT);
						//selectTotal--;

						//map.remove(path);
						Config.drr.remove(path);

						if (textcallback != null)
							textcallback.onListen(Config.drr.size());
					}
				} else if ((Config.drr.size() ) >= Config.SELECT_MAX_NUM) {
					if (item.isSelected ) {
						item.isSelected = !item.isSelected;
						holder.selected.setImageResource(-1);
						//selectTotal--;
						//map.remove(path);

						Config.drr.remove(path);

					} else {
						Message message = Message.obtain(mHandler, 0);
						message.sendToTarget();
					}
				}
			}

		});

		return convertView;
	}
}
