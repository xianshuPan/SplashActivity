package com.hylg.igolf.cs.loader;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import cn.gl.lib.img.LocalMemory;
import cn.gl.lib.utils.FileModifySortComparator;
import cn.gl.lib.utils.MD5;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.Utils;

/**
 * 异步加载图像<br>
 *
 */
public class AsyncImageLoader {
	protected static final String TAG = "AsyncImageLoader";
	private ExecutorService executorService = Executors.newFixedThreadPool(3);
	private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	private Handler handler = new Handler();
	private LocalMemory localMemory = new LocalMemory();
	// Avoid request the same image.
	public static HashSet<String> loadingName = new HashSet<String>();

	private static AsyncImageLoader asyncImageLoader;

	public static AsyncImageLoader getInstance() {
		if(null == asyncImageLoader) {
			synchronized(AsyncImageLoader.class) {
				if(null == asyncImageLoader) {
					asyncImageLoader = new AsyncImageLoader();
				}
			}
		}
		return asyncImageLoader;
	}
	
	/* Constructor that should not be called directly */
	private AsyncImageLoader() {}
	
	/**
	 * 首先，从内存软引用中，或本地，获取会员头像。(返回为空，再加载)
	 * @param sn 会员编号
	 * @param avatar 头像名称
	 * @return
	 */
	public Drawable getAvatar(Context context, String sn, String avatar, int size) {
		String fileName = FileUtils.getAvatarPathBySn(context, sn, avatar);
		if(null == fileName || fileName.isEmpty()) {
			// 真实环境中，必须有头像，不应触发此条件
			return null;
		}
		//图像存在软引用中
		String key = getKey(sn, fileName);
		if (imageCache.containsKey(key)) {
			SoftReference<Drawable> softReference = imageCache.get(key);
			if (softReference != null && softReference.get() != null) {
//				Utils.logh(TAG, "load avatar SoftReference not null");
				return softReference.get();
			}
		}
		// 获取本地图片
		// 不同页面，显示头像大小不同，通过size，节约内存
		Bitmap bitmap = FileUtils.getSmallBitmap(fileName, size, size);
		if(null != bitmap) {
			return new BitmapDrawable(context.getResources(), bitmap);
		}
//		Drawable localDrawable = localMemory.getDrawable(fileName);
//		if(null != localDrawable) {
//			return localDrawable;
//		}
		return null;
	}
	
	/**
	 * 加载头像
	 * @param sn 会员编号
	 * @param url 头像url
	 * @param callback 回调
	 * @param avatar 头像名称
	 */
	public void loadAvatar(Context context, String sn, String avatar, ImageCallback callback) {
		String fileName = FileUtils.getAvatarPathBySn(context, sn, avatar);
		if(null == fileName || fileName.isEmpty()) {
			// 真实环境中，必须有头像，不应触发此条件
			return ;
		}
		String url = BaseRequest.getAvatarUrl(sn, avatar);
		
		
		if (null == url || "".equals(url)) {
			return ;
		}
		// 加载新头像
		loadDrawable(getKey(sn, fileName), fileName, url, callback);
		
		// 清除旧头像，避免头像更换后，本地存储冗余图片累积;
		File file = new File(fileName).getParentFile();
		if(null != file) {
			File[] files = file.listFiles();
			if(null != files) {
				for(File f : files) {
					if(!f.getName().equals(FileUtils.getCusPicName(avatar))) {
						Log.i(TAG, "loadAvatar sn: " + sn + " delete old : " + f.getName() + " and new file: " + avatar);
						f.delete();
					}
				}
			}
		}
	}

	/**
	 * 首先，从内存软引用中，或本地，获取记分卡。(返回为空，再加载)
	 * @param appSn 球单编号
	 * @param userSn 会员编号
	 * @param card 记分卡名称
	 * @return
	 */
	public Drawable getScorecard(Context context, String appSn, String userSn, String card) {
		return getScorecard(context, appSn, userSn, card, Const.SCORE_THUMBNAIL,
				(int) context.getResources().getDimension(R.dimen.score_invite_detail_size));
	}
	
	public Drawable getScorecardDetail(Context context, String appSn, String userSn, String card) {
		return getScorecard(context, appSn, userSn, card, Const.SCORE_ORIGINAL, 0);
	}
	
	public boolean isScorecardLoading(Context context, String appSn, String userSn, String card) {
		String fileName = FileUtils.getScorecardPathBySn(context, card, userSn);
		String key = getKey(userSn, fileName);
		return isDrawableLoading(key);
	}
	
	public Drawable getScorecard(Context context, String appSn, String userSn, String card, int type, int size) {
		String fileName = FileUtils.getScorecardPathBySn(context, card, userSn);
		if(null == fileName || fileName.isEmpty()) {
			return null;
		}

		//图像存在软引用中
		String key = getKey(userSn, fileName);
		if (imageCache.containsKey(key)) {
			SoftReference<Drawable> softReference = imageCache.get(key);
			if (softReference != null && softReference.get() != null) {
//				Utils.logh(TAG, "load avatar SoftReference not null");
				return softReference.get();
			}
		}
		Utils.logh(TAG , "getScorecard localMemory fileName:" + fileName + " type: " + type);
		// 获取本地图片
		if(type == Const.SCORE_ORIGINAL) {
			// 单页面展示记分卡，返回屏幕比例大小
			DisplayMetrics dm = MainApp.getInstance().getGlobalData().getDisplayMetrics();
			int sbar = MainApp.getInstance().getGlobalData().getStatusBarHeight();
			int w = null == dm ? 400 : dm.widthPixels;
			int h = null == dm ? 0 : 
				(dm.heightPixels - sbar -
					(int) context.getResources().getDimension(R.dimen.title_height));
			if(Utils.LOG_H) {
				Utils.logh(TAG, "-------- w : " + w + " h: " + h + " sbar: " + sbar + " dm: " + dm);
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(fileName, options);
				Utils.logh(TAG, "befor inSampleSize: " + options.inSampleSize + " w: " + options.outWidth + " h: " + options.outHeight);
				Utils.logh(TAG, "after inSampleSize: " + FileUtils.calculateInSampleSize(options, w, h));
			}
			Drawable localDrawable = localMemory.getDrawable(fileName, context, w, h);
			if(null != localDrawable) {
				return localDrawable;
			}
		} else { //Const.SCORE_THUMBNAIL
			// 详情中显示缩略图，返回size小图
			Bitmap bitmap = FileUtils.getSmallBitmap(fileName, size, size);
			if(null != bitmap) {
				return new BitmapDrawable(context.getResources(), bitmap);
			}
		}
		return null;
	}
	
	/**
	 * 加载记分卡
	 * @param appSn 球单编号
	 * @param userSn 会员编号
	 * @param callback 回调
	 */
	public void loadScorecard(Context context, String appSn, String userSn, String card, ImageCallback callback) {
		String fileName = FileUtils.getScorecardPathBySn(context, card, userSn);
		if(null == fileName || fileName.isEmpty()) {
			return ;
		}
		String url = BaseRequest.getScorecardUrl(appSn, userSn);
		if (null == url || "".equals(url)) {
			return ;
		}
		// 加载新头像
		loadDrawable(getKey(userSn, fileName), fileName, url, callback);
		
	}
	
	/**
	 * 加载相册图片
	 * @param id 图片所在微博id
	 * @param url 图片url
	 * @param iv 显示图片的ImageView
	 */
	public Drawable getAlbum(Context context, String sn, String album,int type) {
		return getAlbum(context, sn, album, type, 
				(int) context.getResources().getDimension(R.dimen.detail_ablum_size));
	}
	
	public Drawable getAlbum(Context context, String sn, String album,int type, int size) {
		String fileName = "";
		if(Const.ALBUM_THUMBNAIL == type){
			fileName = FileUtils.getAlbumThumb(context, sn, album);
		}else{
			fileName = FileUtils.getAlbumOriginal(context, sn, album);
		}
		if(null == fileName || fileName.isEmpty()) {
			// 真实环境中，必须有头像，不应触发此条件
			return null;
		}

		//图像存在软引用中
		String key = getKey(sn, fileName + "_" + type);
		if (imageCache.containsKey(key)) {
			SoftReference<Drawable> softReference = imageCache.get(key);
			if (softReference != null && softReference.get() != null) {
//				Utils.logh(TAG, "load avatar SoftReference not null");
				return softReference.get();
			}
		}
		Utils.logh(TAG , "getAlbum localMemory fileName:" + fileName + " type: " + type);
		// 获取本地图片
		if(type == Const.ALBUM_ORIGINAL) {
			// 单页面图片浏览，返回屏幕比例大小
			DisplayMetrics dm = MainApp.getInstance().getGlobalData().getDisplayMetrics();
			int sbar = MainApp.getInstance().getGlobalData().getStatusBarHeight();
			int w = null == dm ? 400 : dm.widthPixels;
			int h = null == dm ? 0 : 
				(dm.heightPixels - sbar -
					(int) context.getResources().getDimension(R.dimen.title_height));
			if(Utils.LOG_H) {
				Utils.logh(TAG, "-------- w : " + w + " h: " + h + " sbar: " + sbar + " dm: " + dm);
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(fileName, options);
				Utils.logh(TAG, "befor inSampleSize: " + options.inSampleSize + " w: " + options.outWidth + " h: " + options.outHeight);
				Utils.logh(TAG, "after inSampleSize: " + FileUtils.calculateInSampleSize(options, w, h));
			}
			Drawable localDrawable = localMemory.getDrawable(fileName, context, w, h);
//			Drawable localDrawable = localMemory.getDrawable(fileName);
			if(null != localDrawable) {
				return localDrawable;
			}
		} else { //Const.ALBUM_THUMBNAIL
			// 详情缩略图，返回size小图
			Bitmap bitmap = FileUtils.getSmallBitmap(fileName, size, size);
			if(null != bitmap) {
				return new BitmapDrawable(context.getResources(), bitmap);
			}
		}

		return null;
	}
	
	public boolean isAlbumLoading(Context context, String sn, String album, int type) {
		String fileName = "";
		if(Const.ALBUM_THUMBNAIL == type){
			fileName = FileUtils.getAlbumThumb(context, sn, album);
		}else{
			fileName = FileUtils.getAlbumOriginal(context, sn, album);
		}
		String key = getKey(sn, fileName + "_" + type);
		return isDrawableLoading(key);
	}
	
	/**
	 * 加载记分卡图片
	 * @param id 图片所在微博id
	 * @param url 图片url
	 * @param iv 显示图片的ImageView
	 */
	public void loadAlbum(Context context, String sn, String album,int type, ImageCallback callback) {
		String fileName = "";
		if(0 == type){
			fileName = FileUtils.getAlbumThumb(context, sn, album);
		}else{
			fileName = FileUtils.getAlbumOriginal(context, sn, album);
		}
		if(null == fileName || fileName.isEmpty()) {
			// 真实环境中，必须有头像，不应触发此条件
			return ;
		}
		String url = BaseRequest.getAlbumUrl(sn, album, type);
		if (null == url || "".equals(url)) {
			return ;
		}
		// 加载新头像
		loadDrawable(getKey(sn, fileName + "_" + type), fileName, url, callback);
		
	}
	
	public void deleteAlbum(Context context, String sn, String album) {
		final String thumb = FileUtils.getAlbumThumb(context, sn, album);
		final String original = FileUtils.getAlbumOriginal(context, sn, album);
		new AsyncTask<Object, Object, Integer>() {
			@Override
			protected Integer doInBackground(Object... params) {
				File t = new File(thumb);
				if(t.exists()) {
					Utils.logh(TAG, "deleteAlbum thumb: " + thumb);
					t.delete();
				}
				File o = new File(original);
				if(o.exists()) {
					Utils.logh(TAG, "deleteAlbum original: " + original);
					o.delete();
				}
				return 0;
			}
		}.execute(null, null, null);
	}
	
	private boolean isDrawableLoading(String key) {
		return loadingName.contains(key);
	}
	
	/**
	 * 图片加载请求
	 * @param key 图片缓存关键字
	 * @param fileName 图片文件完整路径名称
	 * @param url 图片请求url
	 * @param callback
	 */
	private void loadDrawable(final String key, final String fileName, final String url, final ImageCallback callback) {
		
		DebugTools.getDebug().debug_v(TAG, "url----->>>"+url);
		
		//开新线程,从网络地址异步加载图像
		executorService.submit(new Runnable() {
			BitmapDrawable bitmapDrawable = null;
			@Override
			public void run() {
				if(loadingName.contains(key)) {
					Utils.logh(TAG , "The same image is loading ... " + fileName);
					return ;
				}
				loadingName.add(key);
				try {
					Utils.logh(TAG, "load new key: " + key + "\n   fileName: " + fileName + "\n   url: " + url);
					bitmapDrawable = (BitmapDrawable) Drawable.createFromStream(new URL(url).openStream(), fileName);
					if(null == bitmapDrawable) {
						Utils.logh(TAG, "saveImage bitmapDrawable null ");
					} else {
	//					bitmapDrawable = new BitmapDrawable(new URL(url).openStream()); // 获取图片
						imageCache.put(key, new SoftReference<Drawable>(bitmapDrawable)); // 加入软引用
						Utils.logh(TAG , "saveImage fileName:" + fileName);
	//					AsyncImageSaver.getInstance().saveImage(bitmapDrawable, fileName); // 异步保存至本地
						localMemory.saveDrawable(bitmapDrawable, fileName); // 同步保存，本身即异步加载
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(loadingName.contains(key)) {
						loadingName.remove(key);
					}
				}
				
				//hanlder在主线程加载图像
				handler.post(new Runnable() {
					@Override
					public void run() {
						callback.imageLoaded(bitmapDrawable);
					}
				});
			}
		});
	}
	
	/**
	 * MD5拼接缓存关键字，避免直接拼接可能存在的特殊字符
	 * @param sn
	 * @param fileName 文件的完整路径名称
	 * @return
	 */
	private String getKey(String sn, String fileName) {
		return MD5.Md5(sn + fileName);
	}
	
	/**
	 * 适当的时机，可主动清除缓存中的无效图片信息：
	 * 		比如，用户修改新头像后，清除旧头像的缓存；
	 * 		删除相册图片后，清除就图片的缓存。
	 * @param sn
	 * @param fileName
	 */
	public void clearOverDueCache(String sn, String fileName) {
		String key = getKey(sn, fileName);
		if(imageCache.containsKey(key)) {
			imageCache.remove(key);
		}
	}
	
	// 加载成功回调接口
	public interface ImageCallback {
		// 设置目标对象的图像资源
		public void imageLoaded(Drawable imageDrawable);
	}

	/**
	 * 清空缓存中的图片
	 */
//	private static void clearCacheImage() {
//		if(null != imageCache && !imageCache.isEmpty()) {
//			imageCache.clear();
//		}
//	}
	
	private static boolean mChecking;
	private final static int MB = 1024*1024;
	/**
	 * 删除图片
	 * @param force 
	 * 			true 强制删除，比如，内存不足的情况下
	 * 			false 检查是否超过存储上限
	 */
	public static void checkSpace(final Context context, final boolean force) {
		if(mChecking) return;
		new AsyncTask<Object, Object, Object>() {
			@Override
			protected void onPreExecute() {
				mChecking = true;
				Log.d(TAG , "checkSpace onPreExecute ...");
				super.onPreExecute();
			}

			@Override
			protected Object doInBackground(Object... params) {
				int delete = 0; // 是否需要删除
				Log.d(TAG , "checkSpace doInBackground ...");
//				clearCacheImage();
				String path = FileUtils.externalFilesDir(context);
				if(null == path) {
					Log.w(TAG, "checkSpace externalFilesDir() : null");
					return delete;
				}
				// 首先，如果存在，删除下载的apk
				String apk = FileUtils.getApkPath(context);
				if(null != apk) {
					File apkFile = new File(apk);
					if(apkFile.exists()) {
						apkFile.delete();
					}
				}
				// 删除图片
				String mem = FileUtils.getMemberPath(context);
				File memFile = new File(mem);
				if(!memFile.exists() || !memFile.isDirectory()) {
					Log.e(TAG, "checkSpace " + mem + " not exist or not dir");
					return delete;
				}
				long size = FileUtils.getFileSize(memFile);
				if(force) {// 强制删除
					delete ++;
				} else {
					// 计算目前大小,判断是否需要删除
					Utils.logh(TAG, "files size: " + (float)size / MB + "MB");
					if(size > 50 * MB) {
						delete ++;
					}
				}
				String sn = MainApp.getInstance().getCustomer().sn;
				// 如果需要，进行删除操作
				if(delete > 0) {
					// 获取当前目录文件及文件夹数组，进行排序
					final File[] files = memFile.listFiles();
					// 删除部分存储文件夹
					int remove = (int) ((0.4 *files.length) + 1);
					Arrays.sort(files, new FileModifySortComparator());
					for(int j=0; j<remove; j++) {
						// 用户自己的文件夹，另行删除
						if(!sn.equals(files[j].getName())) {
							FileUtils.deleteFile(files[j]);
						}
					}
				}
				// 删除部分用户文件夹数据
				String cusScPath = FileUtils.getCustomerScorecardPath(context);
				File cusScFile = new File(cusScPath);
				if(cusScFile.exists() &&
						FileUtils.getFileSize(cusScFile) > 5 * MB) {
					final File[] scs = cusScFile.listFiles();
					for(File f : scs) {
						// 非临时文件
						if(!f.getName().startsWith(sn)) {
							f.delete();
							delete ++;
						}
					}
				}
				return delete;
			}
			
			@Override
			protected void onPostExecute(Object result) {
				Log.d(TAG , "checkSpace onPostExecute ... delete: " + result);
				super.onPostExecute(result);
				mChecking = false;
			}
			
		}.execute(null, null, null);

	}
	
}
