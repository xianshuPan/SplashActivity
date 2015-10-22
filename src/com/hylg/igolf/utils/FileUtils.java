package com.hylg.igolf.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.protocol.HTTP;

import com.hylg.igolf.MainApp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import cn.gl.lib.utils.BaseFileUtils;

public class FileUtils extends BaseFileUtils {
	/**
	 * - IGolf
	 *   - cfg
	 *     - industy(file)
	 *     - region(file)
//	 *   - customer
//	 *     - sn
//	 *       - avatar
//	 *       - ablum
//	 *       - scorecard
	 *   - membership
	 *     - sn001
	 *       - avatar
	 *       - ablum
	 *       	- 0
	 *       	- 1
	 *       - scorecard
	 *     - sn002
	 *       - avatar
	 *       - ablum
	 *       - scorecard
	 *   - team
	 */
	private final static String BASE_PATH = "IGolf";
	public final static String CACHE_DIR_APK = "apk";
	private final static String CONFIG_PATH = "cfg";
	private final static String DB_PATH = "db";
//	private final static String CUSTOMER_PATH = "customer";
	private final static String IMG_PATH_MEMSHIP = "membership";
//	private final static String IMG_PATH_TEAM = "team";
	private final static String IMG_AVATAR = "avatar";
	private final static String IMG_ALBUM = "ablum";
	private final static String IMG_ALBUM_THUMBNAIL = "0"; // 缩略图
	private final static String IMG_ALBUM_ORIGIN = "1"; // 原图
	private final static String IMG_SCORDCARD = "scorecard";
	
	private final static String CFG_INDUSTRY = "industry";
	private final static String CFG_REGION = "region";
	
	public static String externalFilesDir(Context context) {
		if(externalExist()) {
			return getFilePathName(Environment.getExternalStorageDirectory().getAbsolutePath(), BASE_PATH);
		}
		return null;
//		// use this directory instead of Storage upside in mkTmpDirs(),
//		// for this directory will be deleted auto while application is uninstalled.
//		File file = context.getExternalFilesDir(null);
//		if(null == file) {
//			return null;
//		}
//		return file.getAbsolutePath();
	}
	
	public static String getApkPath(Context context) {
		String path = getFilePathName(externalFilesDir(context), CACHE_DIR_APK);
		return path;
	}
	
	public static File createDatabaseFile(Context context, String db) {
		// create directory
		String path = getFilePathName(externalFilesDir(context), DB_PATH);
		File p = new File(path);
		if(!p.exists() && !p.mkdirs()) {
			return null;
		}
		// create database file
		File f = new File(path, db);
		if(!f.exists()) {
			try {
				if(!f.createNewFile()) {
					return null;
				}
			} catch (IOException e) {
				return null;
			}
		}
		return f;
	}
	
	/**
	 * 用户图片存储路径
	 * @param sn
	 * @return
	 */
	public static String getMemberPath(Context context) {
		String path = getFilePathName(externalFilesDir(context),
				IMG_PATH_MEMSHIP);
		return path;
	}

	/**
	 * 获取用户记分卡路径
	 * @param context
	 * @return
	 */
	public static String getCustomerScorecardPath(Context context) {
		return getFilePathName(externalFilesDir(context),
				IMG_PATH_MEMSHIP,
				MainApp.getInstance().getCustomer().sn);
	}
	
	/**
	 * 获取用户头像存储全路径
	 * @param sn
	 * @return
	 */
//	public static String getCustomerAvatarBySn(String sn) {
//		String path = getCustomerPathBySn(sn);
//		if(null != path) {
//			path = getFilePathName(path, IMG_AVATAR, IMG_AVATAR);
//			File file = new File(path);
//			if(file.exists()) {
//				return path;
//			}
//		}
//		return null;
//	}

	/**
	 * 获取会员头像存储全路径
	 * @param sn 会员编号
	 * @param name 头像名称
	 * @return
	 */
	public static String getAvatarPathBySn(Context context, String sn, String name) {
		if(Utils.LOG_H && name.isEmpty()) {
			// 测试中，有无头像情况
			return null;
		}
		String path = getFilePathName(externalFilesDir(context),
				IMG_PATH_MEMSHIP, sn, IMG_AVATAR);
		if(null == path || !mkdirs(path)) { // 路径创建失败
			return null;
		}
		return getFilePathName(path, getCusPicName(name));
	}
	
	public static String getCusPicName(String name) {

		if (name == null || name.length() <= 0) {

			return "";
		}

		int index = name.lastIndexOf(".");
		if(-1 == index) {
			return name;
		}
		return name.substring(0, index);
	}
	
	/**
	 * 获取记分卡存储全路径
	 * @param userSn 会员编号
	 * @param card 记分卡名称
	 * @return
	 */
	public static String getScorecardPathBySn(Context context, String card, String userSn) {
		if(Utils.LOG_H && card.isEmpty()) {
			// 测试中，有无头像情况
			return null;
		}
		String path = getFilePathName(externalFilesDir(context),
				IMG_PATH_MEMSHIP, userSn, IMG_SCORDCARD);
		if(null == path || !mkdirs(path)) { // 路径创建失败
			return null;
		}
		return getFilePathName(path, getCusPicName(card));
	}
	
	public static String getScorecardTmpName(String userSn, String appSn) {
		return userSn + "_" + appSn;
	}
	
	/**
	 * 获取会员相册存储全路径
	 * @param sn 会员编号
	 * @param name 头像名称
	 * @return
	 */
	private static String getAlbumPathBySn(Context context, String sn, String name,String type) {
		if(Utils.LOG_H && name.isEmpty()) {
			// 测试中，有无头像情况
			return null;
		}
		String path = getFilePathName(externalFilesDir(context),
				IMG_PATH_MEMSHIP, sn, IMG_ALBUM , type);
		if(null == path || !mkdirs(path)) { // 路径创建失败
			return null;
		}
		return getFilePathName(path, getCusPicName(name));
	}
	
	public static String getAlbumThumb(Context context, String sn, String name){
		return getAlbumPathBySn(context,sn,name,IMG_ALBUM_THUMBNAIL);
	}
	public static String getAlbumOriginal(Context context, String sn, String name){
		return getAlbumPathBySn(context,sn,name,IMG_ALBUM_ORIGIN);
	}
	
	/**
	 * 获取配置文件信息
	 * @return
	 */
	private static String getExternalCfgPath(Context context) {
		String path = getFilePathName(externalFilesDir(context), CONFIG_PATH);
		if(null == path || !mkdirs(path)) {
			return null;
		}
		return path;
	}
	
	private static File getExternalCfgFile(Context context, String name) {
		String path = getExternalCfgPath(context);
		Utils.logh("", "getExternalCfgFile path: " + path + " name: " + name);
		if(null != path) {
			File file = new File(path, name);
			if(file.exists()) {
				return file;
			}
		}
		return null;
	}
	
	// 外设中的文件
	public static File getExternalCfgIndustry(Context context) {
		return getExternalCfgFile(context, CFG_INDUSTRY);
	}
	
	public static String getExternalCfgIndustryPath(Context context) {
		String path = getExternalCfgPath(context);
		if(null == path) {
			return null;
		}
		return getFilePathName(path, CFG_INDUSTRY);
	}
	
	public static File getExternalCfgRegion(Context context) {
		return getExternalCfgFile(context, CFG_REGION);
	}
	
	public static String getExternalCfgRegionPath(Context context) {
		String path = getExternalCfgPath(context);
		if(null == path) {
			return null;
		}
		return getFilePathName(path, CFG_REGION);
	}
	
	// 应用assets目录文件
	public static String getAssetsCfgIndustryPath() {
		return getFilePathName(CONFIG_PATH, CFG_INDUSTRY);
	}

	public static String getAssetsCfgRegionPath() {
		return getFilePathName(CONFIG_PATH, CFG_REGION);
	}
	
	public static String transferIs2String(InputStream is) {
		String str = null;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		try {
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			str = new String(outStream.toByteArray(), HTTP.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		if(Utils.LOG_H) {
//			String[] log = str.split(",");
//			for(int i=0,len1=log.length; i<len1; i++) {
//				Utils.logh("FileUtils", log[i]);
//			}
//		}
		return str;
	}
	
	// 删除旧版本存储在sd卡上的图片
	public static void deleteExternalFiles() {
		if(externalExist()) {
			File files = new File(getFilePathName(Environment.getExternalStorageDirectory().getAbsolutePath(), BASE_PATH));
			if(null != files && files.exists()) {
				deleteFile(files);
			}
		}
	}
	
	public static String getMediaImagePath(Context context, Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader cursorLoader = new CursorLoader(context, uri, proj, null, null, null);
		Cursor actualimagecursor = cursorLoader.loadInBackground();
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String path = actualimagecursor.getString(actual_image_column_index);
		actualimagecursor.close();
		return path;
	}
	
	/**
	 * 机型底层相机定制，导致图片反转
	 * @param photoViewBitmap
	 * @return
	 */
	public static Bitmap getExifBitmap(Bitmap photoViewBitmap, int angle) {
		Utils.logh("", "getExifBitmap angle: " + angle + " photoViewBitmap: " + photoViewBitmap.getWidth() + " x " + photoViewBitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
		return Bitmap.createBitmap(photoViewBitmap,
				0, 0, photoViewBitmap.getWidth(), photoViewBitmap.getHeight(), matrix, true);
	}
	public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            Log.e("test", "cannot read exif", ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }
	
	
}
