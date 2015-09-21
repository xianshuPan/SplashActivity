package com.hylg.igolf.cs.request;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;

import cn.gl.lib.img.LocalMemory;

import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.Utils;

public class UploadScorecard extends UpdateImageRequest {
	private String sn;
	private String appSn;
	
	public UploadScorecard(Context context, Bitmap bitmap, String appSn, String sn, int score,
							double latitude, double longitude, getRivalDataCallback callback) {
		super(context, bitmap,
				"uploadScorecard02",
				"appSn=" + appSn + "&sn=" + sn + "&score=" + score +
					"&latitude=" + latitude + "&longitude=" + longitude,
				callback);
		this.sn = sn;
		this.appSn = appSn;
	}

	@Override
	protected String saveLocalFile(String imageName, Bitmap bitmap) {
		// 为空，直接返回
		if(null == imageName || imageName.isEmpty()) {
			imageName = appSn;
		}
		String path = FileUtils.getScorecardPathBySn(context, imageName, sn);
		String tmp = FileUtils.getScorecardPathBySn(context, FileUtils.getScorecardTmpName(sn, appSn), sn);
		File tmpFile = new File(tmp);
		boolean needSave = false;
		if(tmpFile.exists()) {
			// 修改失败
			if(!tmpFile.renameTo(new File(path))) {
				needSave = true;
			}
			boolean delete = tmpFile.delete();
			Utils.logh("UploadScorecard02", "renameTo " + needSave + " delete: " + delete);
		} else {
			needSave = true;
		}
		if(needSave) {
			new LocalMemory().saveBitmap(bitmap, path);			
		}
		Utils.logh("UploadScorecard", "path: " + path + " tmp: " + tmp + " needSave: " + needSave);
		return path;
	}

	@Override
	protected int getCompressQuality() {
		return 50;
	}
}
