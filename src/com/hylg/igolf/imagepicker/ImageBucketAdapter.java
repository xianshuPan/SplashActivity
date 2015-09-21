package com.hylg.igolf.imagepicker;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hylg.igolf.R;
import com.hylg.igolf.imagepicker.BitmapCache.ImageCallback;

public class ImageBucketAdapter extends BaseAdapter {

	private Context context;
	private List<ImageBucket> dataList;// ͼƬ���б�
	private BitmapCache cache;
	
	private String strPictrueUnit = "";
	
	
	private ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				}
			}
		}
	};

	public ImageBucketAdapter(Context context, List<ImageBucket> list) {
		this.context = context;
		dataList = list;
		cache = new BitmapCache(context);
		
		strPictrueUnit = context.getResources().getString(R.string.picture_unit);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	class Holder {
		private ImageView iv;
		// private ImageView selected;
		private TextView name;
		private TextView count;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Holder holder;
		if (arg1 == null) {
			holder = new Holder();
			arg1 = View.inflate(context, R.layout.item_image_bucket, null);
			holder.iv = (ImageView) arg1.findViewById(R.id.image);
			holder.name = (TextView) arg1.findViewById(R.id.name);
			holder.count = (TextView) arg1.findViewById(R.id.count);
			arg1.setTag(holder);
		} else {
			holder = (Holder) arg1.getTag();
		}
		ImageBucket item = dataList.get(arg0);
		holder.count.setText("" + item.count+strPictrueUnit);
		holder.name.setText(item.bucketName);
		if (item.imageList != null && item.imageList.size() > 0) {
			
			
			String thumbPath = item.imageList.get(0).thumbnailPath;
			String sourcePath = item.imageList.get(0).imagePath;
			if (item.bucketName.equalsIgnoreCase(Config.ALL_IMAGE)) {
				
				 thumbPath = item.imageList.get(1).thumbnailPath;
				 sourcePath = item.imageList.get(1).imagePath;
				
			} else {
				
				 thumbPath = item.imageList.get(0).thumbnailPath;
				sourcePath = item.imageList.get(0).imagePath;
			}
			
			holder.iv.setTag(sourcePath);
			cache.displayBmp(holder.iv, thumbPath, sourcePath, callback);
		} else {
			holder.iv.setImageBitmap(null);
		}
		return arg1;
	}

}
