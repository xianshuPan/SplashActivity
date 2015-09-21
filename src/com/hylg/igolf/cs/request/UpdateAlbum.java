package com.hylg.igolf.cs.request;

import android.content.Context;
import android.graphics.Bitmap;

import com.hylg.igolf.utils.FileUtils;

public class UpdateAlbum extends UpdateImageRequest {
	private String sn;
	
	public UpdateAlbum(Context context, Bitmap bmp, String sn) {
		super(context, bmp,
				"updateAlbum",
				"sn=" + sn,
				null);
		this.sn = sn;
	}

	@Override
	protected String saveLocalFile(String imageName, Bitmap bitmap) {
		// 上传在主页面进行，显示的为缩略图
		String path = FileUtils.getAlbumThumb(context, sn, imageName);
//		new LocalMemory().saveBitmap(bitmap, path); // 不存储下载后台生产的缩略图
//		AsyncImageLoader.getInstance().getAlbum(context, sn, imageName,0);
//		File tmp = new File(FileUtils.getAlbumThumb(context, sn, "upload.tmp"));
//		if(tmp.exists()) {
//			tmp.delete();
//			Utils.logh("", "delete upload.tmp");
//		}
		return path;
	}

	@Override
	protected int getCompressQuality() {
		return 50;
	}

}
