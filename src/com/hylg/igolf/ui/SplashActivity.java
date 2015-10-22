package com.hylg.igolf.ui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.ConfigInfo;
import com.hylg.igolf.cs.data.VersionInfo;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.GetConfigInfo;
import com.hylg.igolf.cs.request.LoginUser;
import com.hylg.igolf.cs.request.UpdateRequest;
import com.hylg.igolf.ui.account.LoginActivity;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.umeng.analytics.MobclickAgent;

public class SplashActivity extends Activity {



	/*高德定位操作*/
	private LocationManagerProxy 				mLocationManagerProxy;
	private myAMapLocationListener      		mAMapLocationListener;
	
	public static void startSplashActivityFromReceiver(Context context, Bundle bundle) {
		context.startActivity(
			new Intent(context, SplashActivity.class)
				.putExtras(bundle)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				);		
	}
	
	public static void startSplashActivityReset(Context context) {
		context.startActivity(
			new Intent(context, SplashActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				);		
	}
	
	private static final String TAG = "SplashActivity";
	private String down_url;
	private final static int DIALOG_FORCED_UPDATING = 0;
	private TextView mApkStep;
	private int industry_version;
	private int region_version;
	private static final int TIMEOUT = 10 * 1000;
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;
	private static final int DOWN_STEP = 2;
	private static final int START_LOGIN_ACTIVITY = 3;
	private static final int START_MAIN_ACTIVITY = 4;
	private static final long MIN_LOAD_DELAY = 1500; // 页面最短停留时间
	private long loadTime = 0; // 记录启动新页面之前，花费的总时间
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		MobclickAgent.updateOnlineConfig(this);
		/*
		 * Get previously and current version code, 
		 * to check whether display user guide.
		 * Just do check there, save data in UserGuideActivity.java.
		 */
		try {
			int curVer = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			//new ProjDatabase(mVerCode);
			int oldVer = SharedPref.getInt(SharedPref.PREFS_KEY_VER_CODE, this);
			Utils.logh(TAG, "curVer: " + curVer + " oldVer: " + oldVer);
			if(curVer > oldVer) {
				SharedPref.setInt(SharedPref.PREFS_KEY_VER_CODE, curVer, this);
				Intent intent = new Intent();
				intent.setClass(this, UserGuideActivity.class);
				startActivityForResult(intent, Const.REQUST_CODE_USER_GUIDE);
				overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);

				
			} else {
				
				//asynTask();
				
				/*
				 * pxs 2015.04.13 修改
				 * 不用检测版本，直接跳转到主界面,到主界面检测更新
				 * */
				//startMainActivity();
				
				startNewActivity();
				
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

		mLocationManagerProxy = LocationManagerProxy.getInstance(this);

		mAMapLocationListener = new myAMapLocationListener();

		//此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
		//注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
		//在定位结束后，在合适的生命周期调用destroy()方法
		//其中如果间隔时间为-1，则定位只定一次
		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, 60 * 1000, 15, mAMapLocationListener);

		mLocationManagerProxy.setGpsEnable(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);

		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.removeUpdates(mAMapLocationListener);
			mLocationManagerProxy.destory();
		}

		mAMapLocationListener = null;
		mLocationManagerProxy = null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, " ::: requestCode: " + requestCode + " resultCode: " + resultCode + " intent: " + intent);
		if(requestCode == Const.REQUST_CODE_USER_GUIDE) {
			//asynTask();
			
			startNewActivity();
			return ;
		} else if(requestCode == Const.REQUST_CODE_UPDATE) {
			finish();
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	
//	private void asynTask() {
//		if(!Utils.isConnected(this)){
//			startNewActivity();
//			return;
//		}
////		WaitDialog.showWaitDialog(this, R.string.str_loading_version_request);
//		new AsyncTask<Object, Object, Integer>() {
//			final long start = System.currentTimeMillis();
//			
//			final UpdateRequest request = new UpdateRequest(SplashActivity.this);
//			@Override
//			protected Integer doInBackground(Object... params) {
//		        Class<?> c = null;
//		        Object obj = null;
//		        Field field = null;
//		        int x = 0, sbar = 0;
//				try {
//					c = Class.forName("com.android.internal.R$dimen");
//					obj = c.newInstance();
//					field = c.getField("status_bar_height");
//					x = Integer.parseInt(field.get(obj).toString());
//					sbar = getResources().getDimensionPixelSize(x);
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//				
//		        DisplayMetrics outMetrics = new DisplayMetrics();
//		        
//		        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
//		        
//		        MainApp.getInstance().getGlobalData().setDisplayMetrics(outMetrics, sbar);
//		        
//		        Utils.logh(TAG, "outMetrics " + outMetrics.widthPixels + " x " + outMetrics.heightPixels);
//		        
//				copyAssertCfgToSdcard();
//				
//				
//				/* pxs  2015.04.13.09 
//				 * 注释下面一行代码 ，并直接返回 0  //return 0;
//				 * */
//				return request.connectUrl();
//				
//				
//			}
//
//			@Override
//			protected void onPostExecute(Integer result) {
//				super.onPostExecute(result);
//				
//				/* pxs  2015.04.13.09 
//				 * 注释下面一行代码 ,加上startMainActivity()，直接调转到 主页 //startMainActivity();
//				 * */
//				dealWithUpdate(result, request.getFailMsg(), request.getVersionInfo());
//				
//				
////				WaitDialog.dismissWaitDialog();
//				loadTime += System.currentTimeMillis() - start;
//			}
//		}.execute(null, null, null);
//	}

//	private void dealWithUpdate(int result, String msg, VersionInfo versionInfo) {
//		switch (result) {
//		case BaseRequest.REQ_RET_OK:
//			industry_version = versionInfo.industry;
//			region_version = versionInfo.region;
//			// 判断更新
//			PackageInfo pagInfo;
//			try {
//				industry_version = versionInfo.industry;
//				region_version = versionInfo.region;
//				pagInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//				Utils.logh("application info: ", "server_version: "
//						+ versionInfo.version + " current_version: "
//						+ pagInfo.versionCode);
//				Utils.logh("version result: ", String.valueOf(versionInfo.version > pagInfo.versionCode));
//				if (versionInfo.version > pagInfo.versionCode) {
////					down_url = "http://221.236.10.53/file.data.weipan.cn/92555056/2f4014983d6d86e1559969ca3a833e711e13e21c?ssig=IYuz80eYSb&Expires=1394820000&KID=sae,l30zoo1wmz&fn=ProMonkey1.5.apk&corp=1";
//					down_url = versionInfo.downLoadUrl;
//					if (versionInfo.updateType == DIALOG_FORCED_UPDATING) {
//						showForceUpdate(versionInfo.updateInfo);
//					} else {
//						showUpdate(versionInfo.updateInfo);
//					}
//				} else {
//					checkIndustry();
//				}
//			} catch (NameNotFoundException e) {
//				startNewActivity();
//				e.printStackTrace();
//			}
//			break;
//		default:
//			checkIndustry();
//			break;
//		}
//	}

//	private void showForceUpdate(String updateInfo) {
//		new AlertDialog.Builder(this)
//			.setTitle(R.string.str_update_title)
//			.setMessage(updateInfo)
//			.setPositiveButton(R.string.str_dialog_apk_download_now, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					downloadApk();
//				}
//			})
//			.setCancelable(false)
//			.show();
//	}
//	
//	private void showUpdate(String updateInfo) {
//		new AlertDialog.Builder(this)
//			.setTitle(R.string.str_update_title)
//			.setMessage(updateInfo)
//			.setPositiveButton(R.string.str_dialog_apk_download_now, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					downloadApk();
//				}
//			})
//			.setNegativeButton(R.string.str_dialog_apk_download_after, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					checkIndustry();
//				}
//			})
//			.setCancelable(true)
//			.show();
//	}
	
//	private void checkRegion() {
//		File f = FileUtils.getExternalCfgRegion(this);
//		// sd卡不存在,跳过验证配置信息版本到登录页面
//		if (null == f) {
//			startNewActivity();
//		} else {
//			InputStream is = null;
//			int current_version = 0;
//			try {
//				is = new FileInputStream(f);
//				JSONObject jo = new JSONObject(FileUtils.transferIs2String(is));
//				current_version = jo.getInt("region");
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			Utils.logh("region info: ", "server_version: " + region_version
//					+ " current_version: " + current_version);
//			// 地区版本比较
//			if (region_version > current_version) {
////				WaitDialog.showWaitDialog(this, R.string.str_loading_region_request);
//				try {
//					new AsyncTask<Object, Object, Integer>() {
//						final long start = System.currentTimeMillis();
//						final GetConfigInfo request = new GetConfigInfo(SplashActivity.this, 0);
//						@Override
//						protected Integer doInBackground(Object... params) {
//							return request.connectUrl();
//						}
//
//						@Override
//						protected void onPostExecute(Integer result) {
//							super.onPostExecute(result);
//							doWithUpdateConfig(result, request.getFailMsg(), request.getConfigInfo(),
//									FileUtils.getExternalCfgRegionPath(SplashActivity.this));
////							WaitDialog.dismissWaitDialog();
//							loadTime += System.currentTimeMillis() - start;
//							startNewActivity();
//						}
//					}.execute(null, null, null);
//				} catch (Exception e) {
//					startNewActivity();
//					e.printStackTrace();
//				}
//			} else {
//				startNewActivity();
//			}
//		}
//	}
	
//	private void doWithUpdateConfig(int result, String msg, ConfigInfo configInfo,String filePath) {
//		switch (result) {
//		case BaseRequest.REQ_RET_OK:
//			File industrySdkFile = new File(filePath + "_bak");
//			InputStream inputStream = new ByteArrayInputStream(
//					configInfo.info.getBytes());
//			FileUtils.save(industrySdkFile, inputStream);
//			File oldIndustryFile = new File(filePath);
//			if (oldIndustryFile.exists()) {
//				oldIndustryFile.delete();
//			}
//			industrySdkFile.renameTo(oldIndustryFile);
//			break;
//		default:
//
//			break;
//		}
//	}

//	private void checkIndustry() {
//		File f = FileUtils.getExternalCfgIndustry(this);
//		// sd卡不存在,跳过验证配置信息版本到登录页面
//		if (null == f) {
//			startNewActivity();
//		} else {
//			InputStream is = null;
//			int current_version = 0;
//			try {
//				is = new FileInputStream(f);
//				JSONObject jo = new JSONObject(FileUtils.transferIs2String(is));
//				current_version = jo.getInt("industry");
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			Utils.logh("industry info: ", "server_version: " + industry_version
//					+ " current_version: " + current_version);
//			Utils.logh("industry version result: ",
//					String.valueOf(industry_version > current_version));
//			// 行业版本比较
//			if (industry_version > current_version) {
////				WaitDialog.showWaitDialog(this, R.string.str_loading_industry_request);
//				try {
//					final long start = System.currentTimeMillis();
//					new AsyncTask<Object, Object, Integer>() {
//						final GetConfigInfo request = new GetConfigInfo(SplashActivity.this, 1);
//						@Override
//						protected Integer doInBackground(Object... params) {
//							return request.connectUrl();
//						}
//
//						@Override
//						protected void onPostExecute(Integer result) {
//							super.onPostExecute(result);
//							doWithUpdateConfig(result,request.getFailMsg(),request.getConfigInfo(),
//									FileUtils.getExternalCfgIndustryPath(SplashActivity.this));
////							WaitDialog.dismissWaitDialog();
//							loadTime += System.currentTimeMillis() - start;
//							checkRegion();
//						}
//					}.execute(null, null, null);
//				} catch (Exception e) {
//					checkRegion();
//					e.printStackTrace();
//				}
//			} else {
//				checkRegion();
//			}
//		}
//	}


//	private void copyAssertCfgToSdcard() {
////		if(Utils.LOG_H) {
////			FileUtils.deleteExternalFiles();
////		}
//		// 卡不存在，或创建目录失败
//		String pathInd = FileUtils.getExternalCfgIndustryPath(this);
//		if (null == pathInd) {
//			return;
//		}
//		File industry = new File(pathInd);
//		if (!industry.exists()) {
//			InputStream is = null;
//			try {
//				is = getAssets().open(FileUtils.getAssetsCfgIndustryPath());
//				FileUtils.save(industry, is);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		String pathReg = FileUtils.getExternalCfgRegionPath(this);
//		if (null == pathReg) {
//			return;
//		}
//		File region = new File(pathReg);
//		if (!region.exists()) {
//			InputStream is = null;
//			try {
//				is = getAssets().open(FileUtils.getAssetsCfgRegionPath());
//				FileUtils.save(region, is);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

//	private void doAsyncOperations() {
//		MainApp.getInstance().getGlobalData().init();
//	}

	private void startNewActivity() {
//		doAsyncOperations();
//		String sn = SharedPref.getString(SharedPref.SPK_SN, this);
		final String phone = SharedPref.getPrefPhone(this);
		final String password = SharedPref.getPrefPwd(this);
		Utils.logh(TAG, "startNewActivity: " + phone);
		if(!SharedPref.isInvalidPrefString(phone) && !SharedPref.isInvalidPrefString(password)) {
//			WaitDialog.showWaitDialog(this, R.string.str_loading_login);
			new AsyncTask<Object, Object, Integer>() {
				final long start = System.currentTimeMillis();
				LoginUser request = new LoginUser(SplashActivity.this, phone, password);
				@Override
				protected Integer doInBackground(Object... params) {
					return request.connectUrl();
				}
				@Override
				protected void onPostExecute(Integer result) {
					super.onPostExecute(result);
					loadTime += System.currentTimeMillis() - start;
					if(BaseRequest.REQ_RET_OK == result) {
						startMainActivity();
					} else {
						startLoginActivity();
					}
//					WaitDialog.dismissWaitDialog();
				}
			}.execute(null, null, null);
		} else {
			
			startLoginActivity();
		}
	}

	private void startLoginActivity() {
		Utils.logh(TAG, "startLoginActivity loadTime: " + loadTime);
		if(MIN_LOAD_DELAY > loadTime) {
			handler.sendEmptyMessageDelayed(START_LOGIN_ACTIVITY, (MIN_LOAD_DELAY - loadTime));
		} else {
			doStartLoginActivity();
		}
	}
	
	private void doStartLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(SplashActivity.this, LoginActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
		finish();
	}
	
	private void startMainActivity() {
		Utils.logh(TAG, "startMainActivity loadTime: " + loadTime);
		
		if(MIN_LOAD_DELAY > loadTime) {
			
			handler.sendEmptyMessageDelayed(START_MAIN_ACTIVITY, (MIN_LOAD_DELAY - loadTime));
			
		} else {
			
			doStartMainActivity();
		}
	}
	
	private void doStartMainActivity() {
		MainActivity.startMainActivity(SplashActivity.this);
		
		overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
		finish();
	}
	
//	private Dialog downloadDialog = null;
//	
//	private void downloadApk() {
//		View view = LayoutInflater.from(SplashActivity.this).inflate(
//				R.layout.loading, null);
//		mApkStep = (TextView) view.findViewById(R.id.loading_msg);
//		mApkStep.setText(String.format(
//				getResources().getString(R.string.str_dialog_apk_downloding_msg), 0, "%"));
//		downloadDialog = new AlertDialog.Builder(this)
//			.setView(view)
//			.setCancelable(false)
//			.create();
//		downloadDialog.show();
//		
//		updateFilePath = createFile("igolf.apk");
//		
//		doDownloadApk(updateFilePath);
//	}

//	private String updateFilePath = null;
//	private String createFile(String name) {
//		String path = FileUtils.getApkPath(this);
//		if(null == path) {
//			path = FileUtils.getFilePathName(getFilesDir().getAbsolutePath(), FileUtils.CACHE_DIR_APK);
//		}
//		Utils.logh(TAG, "createFile path : " + path);
//		File updateDir = new File(path);
//		if (!updateDir.exists()) {
//			updateDir.mkdirs();
//		}
//
//		File updateFile = new File(FileUtils.getFilePathName(path, name));
//		if(updateFile.exists()) {
//			updateFile.delete();
//		}
//		try {
//			updateFile.createNewFile();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return updateFile.getAbsolutePath();
//	}

	public void doDownloadApk(final String filePath) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean error = false;
				final Message message = new Message();
				try {
					if (downloadUpdateFile(down_url, filePath) > 0) {
						message.what = DOWN_OK;
						handler.sendMessage(message);
						error = false;
					} else {
						error = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
					error = true;
				}
				if(error) {
					File file = new File(filePath);
					if(null != file && file.exists()) {
						file.delete();
					}
					message.what = DOWN_ERROR;
					handler.sendMessage(message);
					startNewActivity();
				}
			}
		}).start();
	}

	public long downloadUpdateFile(String down_url, String file)
			throws IOException {
		Utils.logh(TAG, "downloadUpdateFile down_url: " + down_url + " file: " + file);
		URL url;
		try {
			url = new URL(down_url);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

		int down_step = 5;
		int totalSize;
		int downloadCount = 0;
		int updateCount = 0;
		InputStream inputStream;
		OutputStream outputStream;

		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		// get the total size of file
		totalSize = httpURLConnection.getContentLength();
		if (httpURLConnection.getResponseCode() == 404) {
			return 0;
		}
		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);// overlay if file
															// exist
		byte buffer[] = new byte[1024];
		int readsize = 0;
		while ((readsize = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;
			if (updateCount == 0 ||
					(downloadCount * 100 / totalSize - down_step) >= updateCount) {
				updateCount += down_step;
				final Message message = new Message();
				message.what = DOWN_STEP;
				message.arg1 = updateCount;
				handler.sendMessage(message);
				Utils.logh(TAG, "updateCount: " + updateCount);
			}

		}
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();

		return downloadCount;

	}

//	private void installFile() {
//		File f = new File(updateFilePath);
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setAction(Intent.ACTION_VIEW);
//		intent.setDataAndType(Uri.fromFile(f),
//				"application/vnd.android.package-archive");
//		startActivityForResult(intent, Const.REQUST_CODE_UPDATE);
////		startActivity(intent);
//	}
	
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
//				case DOWN_STEP:
//					mApkStep.setText(String.format(getResources().getString(R.string.str_dialog_apk_downloding_msg), msg.arg1, "%"));
//				
//					
//					break;
//				case DOWN_OK:
//					Utils.logh(TAG, "DOWN_OK");
//					installFile();
//					if(null != downloadDialog && downloadDialog.isShowing()) {
//						downloadDialog.dismiss();
//					}
//					downloadDialog = null;
//					break;
//				case DOWN_ERROR:
//					Utils.logh(TAG, "DOWN_ERROR");
//					if(null != downloadDialog && downloadDialog.isShowing()) {
//						downloadDialog.dismiss();
//					}
//					downloadDialog = null;
//					Toast.makeText(SplashActivity.this,
//							R.string.str_toast_apk_download_fail,
//							Toast.LENGTH_SHORT).show();
//					break;
				case START_LOGIN_ACTIVITY:
					doStartLoginActivity();
					break;
				case START_MAIN_ACTIVITY:
					doStartMainActivity();
					break;
			}
			return false;
		}
		
	});

	private class myAMapLocationListener implements AMapLocationListener {

		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(AMapLocation amapLocation) {
			// TODO Auto-generated method stub
			if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
				//获取位置信息
				double lat = amapLocation.getLatitude();
				double lng = amapLocation.getLongitude();

				MainApp.getInstance().getGlobalData().setLat(lat);
				MainApp.getInstance().getGlobalData().setLng(lng);

				DebugTools.getDebug().debug_v(TAG, "splash_lat------------------>>>"+lat);
				DebugTools.getDebug().debug_v(TAG, "splash_lng------------------>>>"+lng);
			}
		}

	}
}
