package com.hylg.igolf.cs.request;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import cn.gl.lib.img.LocalMemory;

import com.hylg.igolf.utils.FileUtils;

public class UpdateAvatar extends UpdateImageRequest {
	private String sn;
	
	public UpdateAvatar(Context context, Bitmap bitmap, String sn) {
		super(context, bitmap,
				"updateAvatar",
				"sn=" + sn,
				null);
		this.sn = sn;
	}

	@Override
	protected String saveLocalFile(String imageName, Bitmap bitmap) {
		String path = FileUtils.getAvatarPathBySn(context, sn, imageName);
		new LocalMemory().saveBitmap(bitmap, path);
		// 清除旧头像，避免头像更换后，本地存储冗余图片累积;
		File file = new File(path).getParentFile();
		if(null != file) {
			File[] files = file.listFiles();
			if(null != files) {
				for(File f : files) {
					if(!f.getName().equals(FileUtils.getCusPicName(imageName))) {
						Log.i("UpdateAvatar", "saveLocalFile sn: " + sn + " delete old : " + f.getName() + " and new file: " + imageName);
						f.delete();
					}
				}
			}
		}
		return path;
	}

	@Override
	protected int getCompressQuality() {
		return 100;
	}

}
