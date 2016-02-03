package com.hylg.igolf.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.hylg.igolf.DebugTools;
import com.hylg.igolf.HdService;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.VersionInfo;
import com.hylg.igolf.cs.loader.AsyncImageLoader;
import com.hylg.igolf.cs.loader.GetNewMsgCountLoader;
import com.hylg.igolf.cs.loader.GetNewMsgCountLoader.GetNewMsgCountCallback;
import com.hylg.igolf.cs.loader.GetTipsCountLoader;
import com.hylg.igolf.cs.request.BaseRequest;
import com.hylg.igolf.cs.request.CommitCoachLocation;
import com.hylg.igolf.cs.request.SendPushMsg;
import com.hylg.igolf.jpush.JpushVariable;
import com.hylg.igolf.ui.coach.CoachHomeFrgNew;
import com.hylg.igolf.ui.customer.CustomerInfoHomeFrgNew;
import com.hylg.igolf.ui.friend.FriendHomeFrg;
import com.hylg.igolf.ui.golfers.GolfersAndInviteHomeFrg;
import com.hylg.igolf.ui.hall.HallHomeFrg;
import com.hylg.igolf.ui.rank.RankHomeFrg;
import com.hylg.igolf.ui.reqparam.GetRankingReqParam;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.ExitToLogin;
import com.hylg.igolf.utils.FileUtils;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.umeng.update.UmengUpdateAgent;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MainActivity extends FragmentActivity implements OnClickListener, TagAliasCallback {
	private final static String TAG = "MainActivity";

	public static final String ACTION_NOTIFICATION = "org.aisen.sina.weibo.ACTION_NOTIFICATION";

	private final static int CONTAINER = R.id.navigate_container;
	private HashMap<String, View> mSelectViewMap = null;
	private String mCurTag;
	private final static String TAG_GOLFERS = "golfers";
	private final static String TAG_HALL = "hall";
	private final static String TAG_RANK = "rank";
	private final static String TAG_COACH = "coach";
	private final static String TAG_FRIEND = "friend";

	// 当有Fragment Attach到这个Activity的时候，就会保存
	private Map<String, WeakReference<onKeyDownClick>> fragmentRefs;
	
	private String down_url;
	private final static int DIALOG_FORCED_UPDATING = 0;
	private int industry_version;
	private int region_version;
	private long loadTime = 0; 
	
	//private ImageView naviNewMsg, naviNewInvite;
	private static boolean isForeground = false;
	private final static String BUNDLE_KEY_FROM_SETUP = "from_setup";

	/*高德定位操作*/
	private LocationManagerProxy 				mLocationManagerProxy;
	private myAMapLocationListener      		mAMapLocationListener;

	private FragmentActivity                    mContext;

	public static void startMainActivity(Context context) {
		
		Intent intent = new Intent(context, MainActivity.class);
//		intent.putExtra(BUNDLE_KEY_FROM_SETUP, true);
		context.startActivity(intent);

	}
	
	public static void startMainActivityFromSetup(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra(BUNDLE_KEY_FROM_SETUP, true);
		context.startActivity(intent);

	}
	
//	public static void startMainActivityFromSplash(Context context, Bundle bundle) {
//		Intent intent = new Intent(context, MainActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.putExtras(bundle);
//		context.startActivity(intent);
//	}
	
	public static void startMainActivityFromReceiver(Context context, Bundle bundle) {
		context.startActivity(
			new Intent(context, MainActivity.class)
				.putExtras(bundle)
	//			.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Utils.logh(TAG, "resultCode: " + resultCode);

		super.onActivityResult(requestCode, resultCode, intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;

		SharedPref.setBoolean(SharedPref.PREFS_KEY_SHOW_LOCATION_SETTING,false,mContext);
		ExitToLogin.getInstance().addActivity(this);

		fragmentRefs = new HashMap<String, WeakReference<onKeyDownClick>>();

		DisplayMetrics sd = getResources().getDisplayMetrics();
		
		/*
		 * pxs 2015.04.13 注释了下面的 代码
		 * */
		
        // 首先检测是否被强制关闭过，重启
//        int ppid = SharedPref.getInt(SharedPref.PREFS_KEY_PID, this);
//        
//        final int pid = Process.myPid();
//        Utils.logh(TAG, "ppid: " + ppid + " cur pid: " + pid);
//        if(0 != ppid && ppid != pid) {
//        	SplashActivity.startSplashActivityReset(this);
//        	finish();
//        }
		
		setContentView(R.layout.activity_main);

		//naviNewMsg.setBackgroundResource(R.drawable.about_logo);
		
		if(null == savedInstanceState) {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
			
			//Fragment f = GolfersAndInviteHomeFrg.getInstance();
			Fragment f = new  FriendHomeFrg();
			ft.add(CONTAINER, f, TAG_FRIEND);
			ft.commit();
		}
		getViews();
		
		// 设置极光推送
//		Set<String> tagSet = new LinkedHashSet<String>();
//		tagSet.add();
		//JPushInterface.setAliasAndTags(getApplicationContext(), MainApp.getInstance().getCustomer().state, null, this);
		JPushInterface.init(this);
        mHandle.sendMessage(mHandle.obtainMessage(MSG_SET_ALIAS, MainApp.getInstance().getCustomer().sn));
        
		// 注册监听
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.IG_ACTION_NEW_ALERTS);
		filter.addAction(Const.IG_ACTION_FORE_JPUSH_NOTIFY);
		filter.addAction(Intent.ACTION_DEVICE_STORAGE_LOW);
		registerReceiver(mReceiver, filter);
		
		// 初始化更新提醒
		updateAlerts();
		
		// 从注册页面进入，展示蒙板
//		Intent intent = getIntent();
//		if(null != intent && intent.getBooleanExtra(BUNDLE_KEY_FROM_SETUP, false)) {
//			final ViewStub maskGuide = (ViewStub) findViewById(R.id.mask_guide);
//			maskGuide.inflate();
//			ImageView vs = (ImageView) findViewById(R.id.main_mask_img);
//			vs.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Utils.logh(TAG, "click on mask guide !");
//					maskGuide.setVisibility(View.GONE);
////					GolfersHomeFrg.getInstance().onMaskGuideClick();
//				}
//			});
//		}
		
		/*从应用中拷贝图片到sd card 中*/
		copyCameraIconToSdcard();
		
		/*开启检查更新的任务*/
		asynTask();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Utils.logh(TAG, "onNewIntent " + intent);

//		MainApp.getInstance().getGlobalData().setHasStartNewInvite(true);
//		if(null != intent && null != intent.getExtras()) {
//			bundle = intent.getExtras();
//		} else {
//			bundle = JpushReceiver.clickBundle();
//		}
//		
//		navigateToHall(true, bundle);
//		JpushReceiver.clearClickBundle();
		
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出应用，注销监听
		unregisterReceiver(mReceiver);
		// 退出登录时，会关闭Activity，清空map数据，避免重新进入时，残留无效数据导致加载异常
		// java.lang.IllegalStateException: Activity has been destroyed
		
		if(null != mSelectViewMap && !mSelectViewMap.isEmpty()) {
			
			mSelectViewMap.clear();
		}

		SharedPref.setBoolean(SharedPref.PREFS_KEY_SHOW_LOCATION_SETTING,false,mContext);
		// 检查内存，删除不常用图片
		AsyncImageLoader.checkSpace(MainActivity.this, false);
	}

	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
		JPushInterface.onResume(this);
		
		/*获取未读消息数量*/
		getTipsCount();

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
		isForeground = false;
		super.onPause();
		JPushInterface.onPause(this);

		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.removeUpdates(mAMapLocationListener);
			mLocationManagerProxy.destory();
		}

		mAMapLocationListener = null;
		mLocationManagerProxy = null;


		if (MainApp.getInstance().getGlobalData().getLng() > 0) {

			saveCoachLocation();
		}


	}
	
	public static boolean isMainForeground() {
		return isForeground;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Utils.logh(TAG, "onStart >>> ");
		start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Utils.logh(TAG, "onStop <<< ");
		stop();
	}

	private void getViews() {
		mCurTag = TAG_FRIEND;

		if(null == mSelectViewMap) {
			mSelectViewMap = new HashMap<String, View>();
		}
		if(!mSelectViewMap.isEmpty()) {
			mSelectViewMap.clear();
		}
		RelativeLayout golfers = (RelativeLayout) findViewById(R.id.navi_item_customer);
		golfers.setOnClickListener(this);
		addSelectView(golfers, TAG_GOLFERS);
		RelativeLayout hall = (RelativeLayout) findViewById(R.id.navi_item_hall);
		hall.setOnClickListener(this);
		addSelectView(hall, TAG_HALL);
		RelativeLayout rank = (RelativeLayout) findViewById(R.id.navi_item_rank);
		rank.setOnClickListener(this);
		addSelectView(rank, TAG_RANK);
		RelativeLayout coach = (RelativeLayout) findViewById(R.id.navi_item_coach);
		coach.setOnClickListener(this);
		addSelectView(coach, TAG_COACH);
		RelativeLayout friend = (RelativeLayout) findViewById(R.id.navi_item_friend);
		friend.setOnClickListener(this);
		addSelectView(friend, TAG_FRIEND);
		
		// 新提醒
		//naviNewInvite = (ImageView) findViewById(R.id.navi_item_hall_msg_hint);
		//naviNewMsg = (ImageView) findViewById(R.id.navi_item_coach_msg_hint);

		mSelectViewMap.get(mCurTag).setSelected(true);
	}

	private void saveCoachLocation() {

		new AsyncTask<Object, Object, Integer>() {
			final long start = System.currentTimeMillis();

			final CommitCoachLocation request = new CommitCoachLocation(MainActivity.this,MainApp.getInstance().getCustomer().id,
					MainApp.getInstance().getGlobalData().getLat(),MainApp.getInstance().getGlobalData().getLng());
			@Override
			protected Integer doInBackground(Object... params) {

				return request.connectUrl();
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);

			}
		}.execute(null, null, null);
	}
	
	/**/
	private void copyCameraIconToSdcard() {
		
		 File mediaStorageDir = new File("/sdcard/IGolf");
		
		  if (! mediaStorageDir.exists()){
	            if (! mediaStorageDir.mkdirs()){
	                //Log.d("MyCameraApp", "failed to create directory");
	                return ;
	            }
	        }
			String asdf = 	Environment.getExternalStorageDirectory().getAbsolutePath();
			String cameraPath = asdf+"/IGolf/camera.png";
			
			File file = new File(cameraPath);
			
	        
	      Bitmap camera1 =  BitmapFactory.decodeResource(getResources(), R.drawable.camera1);
	      
	      try {
			camera1.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.navi_item_customer:

				navigateToCustomer();
				break;

			case R.id.navi_item_hall:

				navigateToHall();
				break;

			case R.id.navi_item_rank:

				navigateToRank();
				break;

			case R.id.navi_item_coach:

				navigateToCoach();
				break;
				
			case R.id.navi_item_friend:
				
				navigateToFriend(true);
				break;
		}
	}
	
	private void asynTask() {
	if(!Utils.isConnected(this)){
		//startNewActivity();
		return;
	}
//	WaitDialog.showWaitDialog(this, R.string.str_loading_version_request);
	new AsyncTask<Object, Object, Integer>() {
		final long start = System.currentTimeMillis();
		
		//final UpdateRequest request = new UpdateRequest(MainActivity.this);
		@Override
		protected Integer doInBackground(Object... params) {
	        Class<?> c = null;
	        Object obj = null;
	        Field field = null;
	        int x = 0, sbar = 0;
			try {
				c = Class.forName("com.android.internal.R$dimen");
				obj = c.newInstance();
				field = c.getField("status_bar_height");
				x = Integer.parseInt(field.get(obj).toString());
				sbar = getResources().getDimensionPixelSize(x);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
	        DisplayMetrics outMetrics = new DisplayMetrics();
	        
	        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
	        
	        MainApp.getInstance().getGlobalData().setDisplayMetrics(outMetrics, sbar);
	        
	        Utils.logh(TAG, "outMetrics " + outMetrics.widthPixels + " x " + outMetrics.heightPixels);
	        
			copyAssertCfgToSdcard();
			
			
			/* pxs  2015.04.13.09 
			 * 注释下面一行代码 ，并直接返回 0  //return 0;
			 * */
			//return request.connectUrl();
			UmengUpdateAgent.update(MainActivity.this);
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			/* pxs  2015.04.13.09 
			 * 注释下面一行代码 ,加上startMainActivity()，直接调转到 主页 //startMainActivity();
			 * */
			//dealWithUpdate(result, request.getFailMsg(), request.getVersionInfo());
			
			
//			WaitDialog.dismissWaitDialog();
			loadTime += System.currentTimeMillis() - start;
		}
	}.execute(null, null, null);
}
	
	private void dealWithUpdate(int result, String msg, VersionInfo versionInfo) {
		switch (result) {
		case BaseRequest.REQ_RET_OK:
			industry_version = versionInfo.industry;
			region_version = versionInfo.region;
			// 判断更新
			PackageInfo pagInfo;
			try {
				industry_version = versionInfo.industry;
				region_version = versionInfo.region;
				pagInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				Utils.logh("application info: ", "server_version: "
						+ versionInfo.version + " current_version: "
						+ pagInfo.versionCode);
				Utils.logh("version result: ", String.valueOf(versionInfo.version > pagInfo.versionCode));
				if (versionInfo.version > pagInfo.versionCode) {
//					down_url = "http://221.236.10.53/file.data.weipan.cn/92555056/2f4014983d6d86e1559969ca3a833e711e13e21c?ssig=IYuz80eYSb&Expires=1394820000&KID=sae,l30zoo1wmz&fn=ProMonkey1.5.apk&corp=1";
					down_url = versionInfo.downLoadUrl;
					if (versionInfo.updateType == DIALOG_FORCED_UPDATING) {
						showForceUpdate(versionInfo.updateInfo);
					} else {
						showUpdate(versionInfo.updateInfo);
					}
				} else {
					//checkIndustry();
				}
			} catch (NameNotFoundException e) {
				//startNewActivity();
				e.printStackTrace();
			}
			break;
		default:
			//checkIndustry();
			break;
		}
	}

	private void showForceUpdate(String updateInfo) {
		new AlertDialog.Builder(this)
			.setTitle(R.string.str_update_title)
			.setMessage(updateInfo)
			.setPositiveButton(R.string.str_dialog_apk_download_now, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					downloadApk();
				}
			})
			.setCancelable(false)
			.show();
	}
	
	private void showUpdate(String updateInfo) {
		new AlertDialog.Builder(this)
			.setTitle(R.string.str_update_title)
			.setMessage(updateInfo)
			.setPositiveButton(R.string.str_dialog_apk_download_now, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					downloadApk();
				}
			})
			.setNegativeButton(R.string.str_dialog_apk_download_after, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//checkIndustry();
				}
			})
			.setCancelable(true)
			.show();
	}
	
	private void downloadApk() {

		Intent data = new Intent(this, HdService.class);
		data.putExtra("DownLoadUrl", down_url);
		startService(data);
	}
	
	private void copyAssertCfgToSdcard() {
//		if(Utils.LOG_H) {
//			FileUtils.deleteExternalFiles();
//		}
		// 卡不存在，或创建目录失败
		String pathInd = FileUtils.getExternalCfgIndustryPath(this);
		if (null == pathInd) {
			return;
		}
		File industry = new File(pathInd);

		//if (!industry.exists()) {
			InputStream is ;
			try {
				String asdf= FileUtils.getAssetsCfgIndustryPath();
				is = getAssets().open(asdf);

				FileUtils.save(industry, is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		//}

		String pathReg = FileUtils.getExternalCfgRegionPath(this);
		if (null == pathReg) {
			return;
		}
		File region = new File(pathReg);
		//if (!region.exists()) {
			InputStream is_region = null;
			try {
				String sdf = FileUtils.getAssetsCfgRegionPath();
				is_region = getAssets().open(sdf);
				FileUtils.save(region, is_region);
			} catch (IOException e) {
				e.printStackTrace();
			}
		//}
	}
	
	
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
	
	private GetTipsCountLoader getTipsCountLoader = null;
	private void getTipsCount() {
		
		if (getTipsCountLoader == null) {
			
			getTipsCountLoader = new GetTipsCountLoader(MainActivity.this, MainApp.getInstance().getCustomer().sn);
		}
		
		if (getTipsCountLoader != null && getTipsCountLoader.isRunning()) {
			
			return;
		}
		
		getTipsCountLoader.requestData();
	}
	
	private void navigateToFriend(boolean clicked) {
		String tag = TAG_FRIEND;
		if(!resetSelection(tag) && clicked) {
			return ;
		}
		
		//startActivity(new Intent(this, FriendHotActivity.class));
		
		FragmentManager fm = getSupportFragmentManager();
		
		Fragment frg = fm.findFragmentByTag(tag);
		
		
		//Utils.logh(TAG , "FriendHomeFrg Selected ...... exist " + (null!=frg));
		
		if (frg == null ) {
			
			frg = new FriendHomeFrg();
		}
		
		if (frg != null && !clicked) {
			
			FragmentTransaction asdf = fm.beginTransaction();
			asdf.remove(frg);
			asdf.commit();
			
			frg = new FriendHomeFrg();
		}
		    
		replaceFragment(fm, frg, tag);
		   
	}

	private void navigateToCoach() {
		String tag = TAG_COACH;
		if(!resetSelection(tag)) {
			return ;
		}
		FragmentManager fm = getSupportFragmentManager();
		Fragment frg = fm.findFragmentByTag(tag);
		Utils.logh(TAG , "CoachHomeFrg Selected ...... exist " + (null!=frg));
		if(null == frg) {
			frg = new CoachHomeFrgNew();
		}
		replaceFragment(fm, frg, tag);
	}

	private void navigateToRank() {
		String tag = TAG_RANK;
		if(!resetSelection(tag)) {
			return ;
		}
		FragmentManager fm = getSupportFragmentManager();
		Fragment frg = fm.findFragmentByTag(tag);
		Utils.logh(TAG , "RankHomeFrg Selected ...... exist " + (null!=frg));
		if(null == frg) {
			MainApp ins = MainApp.getInstance();
			GetRankingReqParam data = new GetRankingReqParam();
			data.pageNum = ins.getGlobalData().startPage;
			data.pageSize = ins.getGlobalData().pageSize;
//			data.region = ins.getCustomer().state;
			data.region = Const.CFG_ALL_REGION;
			data.sex = -1;
			data.sn = ins.getCustomer().sn;
			frg = RankHomeFrg.getInstance(data);
		}
		replaceFragment(fm, frg, tag);
	}

	private void navigateToHall() {
		navigateToHall(false, null);
	}

	private void navigateToHall(boolean jpush, Bundle bundle) {
		if(jpush) {
			dealWithNofity(bundle);
		}
		String tag = TAG_HALL;
		if(!resetSelection(tag)) {
//			if(jpush) {
//				sendBroadcast(new Intent(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY));
//			}
			return ;
		}
		FragmentManager fm = getSupportFragmentManager();
		Fragment frg = fm.findFragmentByTag(tag);
		Utils.logh(TAG , "GolfersAndInviteHomeFrg Selected ...... exist " + (null!=frg));
		if(null == frg) {

			frg = GolfersAndInviteHomeFrg.getInstance();

		} else {
//			if(jpush) {
//				sendBroadcast(new Intent(Const.IG_ACTION_MY_INVITE_JPUSH_NOTIFY));
//			}
		}

		if (frg instanceof onKeyDownClick){

			fragmentRefs.put(TAG_HALL,new WeakReference<onKeyDownClick>((onKeyDownClick)frg));
		}

		replaceFragment(fm, frg, tag);
	}

	private void navigateToCustomer() {
		String tag = TAG_GOLFERS;
		if(!resetSelection(tag)) {
			return ;
		}
		FragmentManager fm = getSupportFragmentManager();
		Fragment frg = fm.findFragmentByTag(tag);
		Utils.logh(TAG, "GolfersHomeFrg Selected ...... exist " + (null != frg));
		if(null == frg) {

			frg = CustomerInfoHomeFrgNew.getInstance();
		}
		replaceFragment(fm, frg, tag);

//		WeiBoUser user = new WeiBoUser();
//		user.setScreen_name("sdfsdfsdf");
//		UserProfileActivity.launch(this, user);

		//startActivity(new Intent(this,ActivityMain.class));
	}
	
	private void dealWithNofity(Bundle bundle) {
		if(null == bundle) {
			Log.w(TAG, "dealWithNofity bundle null");
			return ;
		}
		for (String key : bundle.keySet()) {
			if("cn.jpush.android.EXTRA".equals(key)) {
				String extra = bundle.getString(key);
				try {
					JSONObject jo = new JSONObject(extra);
					int type = jo.optInt(JpushVariable.RCECEIPT_TYPE, -1);
					Utils.logh(TAG, "type: " + type);
					switch(type) {
						case JpushVariable.TYPE_NOTIFY_DEPARTURE:
							showNotifyDeparture(bundle.getString("cn.jpush.android.ALERT"),
												jo.getString(JpushVariable.APP_SN),
												jo.getString(JpushVariable.INVITER),
												jo.getString(JpushVariable.INVITEE),
												type);
							break;
						default :
							showConfirmDialog(bundle.getString("cn.jpush.android.ALERT"));
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * 弹出去人对话框
	 * */
	private void showConfirmDialog(String msg) {
		new AlertDialog.Builder(this)
		.setTitle(R.string.app_name)
		.setMessage(msg)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		})
		.show();
	}
	
	private void showNotifyDeparture(String msg, final String appSn, String inviter, String invitee, final int type) {
		Utils.logh(TAG, "showNotifyDeparture type: " + type + " appSn: " + appSn + " inviter: " + inviter +
					" invitee: " + invitee + " msg: " + msg);
		final String pushSn; // 对方会员编号
		if(inviter.equals(MainApp.getInstance().getCustomer().sn)) {
			pushSn = invitee;
		} else {
			pushSn = inviter;
		}
		new AlertDialog.Builder(this)
			.setTitle(R.string.str_dialog_title_notify_departure)
			.setMessage(msg)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new AsyncTask<Object, Object, Integer>() {
						SendPushMsg request = new SendPushMsg(MainActivity.this, pushSn, appSn, type);

						@Override
						protected Integer doInBackground(Object... params) {
							return request.connectUrl();
						}
//						@Override
//						protected void onPostExecute(Integer result) {
//							super.onPostExecute(result);
//						}
					}.execute(null, null, null);
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();

	}


	
	/**
	 * Replace fragment of activity, to display different content
	 * while click navigation button.
	 * @param fragment The fragment to display
	 * @param tag The fragment tag
	 */
	private void replaceFragment(FragmentManager fm, Fragment fragment, String tag) {
		FragmentTransaction ft = fm.beginTransaction();
//		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		
		ft.replace(CONTAINER, fragment, tag);
		
		if (!tag.equalsIgnoreCase(TAG_FRIEND)) {
			
			ft.addToBackStack(tag);
		}
		
		ft.commit();
	}
	
	/**
	 * Add view to hash map by tag while get views from resources,
	 * so, we can change the select status when click navigation item,
	 * to display different background.
	 * @param view The map value.
	 * @param tag The map key.
	 * @see
	 */
	private void addSelectView(View view, String tag) {
		Utils.logh(TAG, "addSelectView :  " + tag);
		view.setTag(tag);
		view.setSelected(false);
		mSelectViewMap.put(tag, view);
	}
	
	/**
	 * Reset the selection background by select status
	 * @param tag The map key
	 * @return True if the same as current selection;
	 * @see
	 */
	private boolean resetSelection(String tag) {
		if(!tag.equals(mCurTag)) {
			final String pre = mCurTag;
			mCurTag = tag;
			mSelectViewMap.get(pre).setSelected(false);
			mSelectViewMap.get(mCurTag).setSelected(true);
			return true;
		} else {
			Utils.logh(TAG, "resetSelection not changed ~~~");
			return false;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_BACK == keyCode) {

			if (onBackClick())
				return true;

			mHandle.sendEmptyMessage(MSG_EXIT);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onBackClick() {

		Set<String> keys = fragmentRefs.keySet();
		for (String key : keys) {
			WeakReference<onKeyDownClick> fragmentRef = fragmentRefs.get(key);
			onKeyDownClick fragment = fragmentRef.get();
			if (fragment != null && fragment.onKeyDown())
				return true;
		}

		return false;
	}
	
	private static final int MSG_EXIT = 1;
	private static final int MSG_EXIT_WAIT = 2;
	private static final long EXIT_DELAY_TIME = 2000;
	private final static int MSG_UPDATE_ALERTS = 3;
	private final static long UPDATE_ALERTS_DELAY = 60 * 1000; // 1分钟
	// 极光设置别名失败重试
	private static final int MSG_SET_ALIAS = 1001;
	private Handler mHandle = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
				case MSG_EXIT:
					if(mHandle.hasMessages(MSG_EXIT_WAIT)) {
						// 检查内存，删除不常用图片
						AsyncImageLoader.checkSpace(MainActivity.this, false);
						moveTaskToBack(true);
					} else {
						Toast.makeText(MainActivity.this, R.string.str_exit_msg, Toast.LENGTH_SHORT).show();
						mHandle.sendEmptyMessageDelayed(MSG_EXIT_WAIT, EXIT_DELAY_TIME);
					}
					break;
				case MSG_EXIT_WAIT:
					break;
				case MSG_UPDATE_ALERTS:
					getUpdateNumberAysnc();
//					mHandle.sendEmptyMessageDelayed(MSG_UPDATE_ALERTS, UPDATE_ALERTS_DELAY);
					break;
				case MSG_SET_ALIAS:
					JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, MainActivity.this);
					break;
			}
			return false;
		}

	});
	
	/**
	 * 更新消息
	 */
	private void updateAlerts() {
		GlobalData gd = MainApp.getInstance().getGlobalData();
		// 更新消息提醒
//		if(gd.msgNumSys > 0) {
//			Utils.setVisible(naviNewMsg);
//		} else {
//			Utils.setGone(naviNewMsg);
//		}
		// 更新约球提醒
//		if(gd.msgNumInvite > 0) {
//			Utils.setVisible(naviNewInvite);
//		} else {
//			Utils.setGone(naviNewInvite);
//		}
		
		// 更新我的约球
		HallHomeFrg.updateTabAlert(gd.msgNumInvite);
		// 更新系统消息
		//CustomerHomeFrgNew.updateMsgAlert(gd.msgNumSys);
		
		
		if(mHandle.hasMessages(MSG_UPDATE_ALERTS)) {
			mHandle.removeMessages(MSG_UPDATE_ALERTS);
		}
		mHandle.sendEmptyMessageDelayed(MSG_UPDATE_ALERTS, UPDATE_ALERTS_DELAY);
	}


	private GetNewMsgCountLoader getNewMsgLoader = null;
	private boolean isGetNewMsgLoading() {
		return (null != getNewMsgLoader && getNewMsgLoader.isRunning());
	}
	
	private void getUpdateNumberAysnc() {
		if(!Utils.isConnected(this)) {
			Utils.logh(TAG, "getUpdateNumberAysnc network disconnect !");
			//Utils.toastShort(R.string.str_toast_network_disconnect, MainActivity.this);
			mHandle.sendEmptyMessageDelayed(MSG_UPDATE_ALERTS, UPDATE_ALERTS_DELAY);
			return ;
		}
		// 正在请求，不重新请求
		if(isGetNewMsgLoading()) {
			return ;
		}
		getNewMsgLoader = new GetNewMsgCountLoader(MainActivity.this, MainApp.getInstance().getCustomer().sn,
			new GetNewMsgCountCallback() {
				@Override
				public void callBack(int retId, String msg) {
					if(BaseRequest.REQ_RET_OK == retId) {
						updateAlerts();
					} else {
						mHandle.sendEmptyMessageDelayed(MSG_UPDATE_ALERTS, UPDATE_ALERTS_DELAY);
					}
					// do nothing if fail, wait for next message to update again
					getNewMsgLoader = null;
				}
		});
		getNewMsgLoader.requestData();
	}
	
	private Receiver mReceiver = new Receiver();
	private class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(Const.IG_ACTION_NEW_ALERTS.equals(action)) {
				getUpdateNumberAysnc();
			}
			else if(Const.IG_ACTION_FORE_JPUSH_NOTIFY.equals(action)) {
				navigateToHall(true, intent.getExtras());
				getUpdateNumberAysnc();
			}
//			else if(Const.IG_ACTION_BACK_JPUSH_NOTIFY.equals(action)) {
//				Utils.logh(TAG, "IG_ACTION_BACK_JPUSH_NOTIFY");
//				startMainActivity(context, intent.getExtras());
//			}
			else if(Intent.ACTION_DEVICE_STORAGE_LOW.equals(action)) {
				AsyncImageLoader.checkSpace(MainActivity.this, true);
			}
		}
	};
	
	private void start() {
		// 启动更新
		if(!mHandle.hasMessages(MSG_UPDATE_ALERTS)) {
			mHandle.sendEmptyMessageDelayed(MSG_UPDATE_ALERTS, UPDATE_ALERTS_DELAY);
		}
	}
	
	private void stop() {
		// 移除更新
		if(mHandle.hasMessages(MSG_UPDATE_ALERTS)) {
			mHandle.removeMessages(MSG_UPDATE_ALERTS);
		}
		// 清除请求(start启动更新后，会获取最新信息，
		// 并启动消息轮询获取机制，loader无需额外请求)
		if(isGetNewMsgLoading()) {
			getNewMsgLoader.stopTask(true);
			getNewMsgLoader = null;
		}
		
	}

	@Override
	public void gotResult(int code, String alias, Set<String> tags) {
		switch(code) {
			case 0: // success
				Utils.logh(TAG, "push setAliasAndTags success! \r\n" +
						"Set tag and alias success, alias = " + alias + "; tags = " + tags);
				break;
			case 6002: // 设置超时
				mHandle.sendMessageDelayed(mHandle.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
//				Toast.makeText(MainActivity.this, "setAliasAndTags fail 6002 alias: " + alias, Toast.LENGTH_LONG).show();
				break;
			default:
				if(Utils.LOG_H) {
					Toast.makeText(MainActivity.this, "setAliasAndTags fail " + code + " alias: " + alias, Toast.LENGTH_LONG).show();
				}
//				Utils.logh(TAG, "push setAliasAndTags Failed with errorCode = " + code + " alias = " + alias + "; tags = " + tags);
				break;
		}
	}

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

				String province = amapLocation.getProvince();
				MainApp.getInstance().getGlobalData().setLat(lat);
				MainApp.getInstance().getGlobalData().setLng(lng);
				MainApp.getInstance().getGlobalData().setProvinceStr(province);

				DebugTools.getDebug().debug_v(TAG, "lat------------------>>>" + lat);
				DebugTools.getDebug().debug_v(TAG, "lng------------------>>>"+lng);
				DebugTools.getDebug().debug_v(TAG, "province------------------>>>"+province);
				saveCoachLocation();
			}else {


				boolean industry = SharedPref.getBoolean(SharedPref.PREFS_KEY_SHOW_LOCATION_SETTING, mContext);

				if (!industry) {

					SharedPref.setBoolean(SharedPref.PREFS_KEY_SHOW_LOCATION_SETTING,true,mContext);
					AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
					dialog.setMessage("「爱高尔夫」需要获取您的位置，以提供更优质的服务。请开启您的定位服务或权限");
					dialog.setPositiveButton(R.string.str_setting_auth_title, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(Settings.ACTION_SETTINGS);
							startActivity(intent);
							SharedPref.setBoolean(SharedPref.PREFS_KEY_SHOW_LOCATION_SETTING, false, mContext);
						}
					});
					dialog.setNeutralButton(R.string.str_setting_gps_title, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(intent);
							SharedPref.setBoolean(SharedPref.PREFS_KEY_SHOW_LOCATION_SETTING, false, mContext);
						}
					});
					dialog.setNegativeButton(R.string.str_photo_cancel, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							//CartActivity.this.finish();
						}
					});
					dialog.show();
				}

			}

		}

	}

	public interface onKeyDownClick{

		boolean onKeyDown ();

	}
	
}
