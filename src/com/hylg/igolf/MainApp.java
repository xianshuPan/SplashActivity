package com.hylg.igolf;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.hylg.igolf.broadcast.HylgReceiver;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.jpush.JpushReceiver;
import com.hylg.igolf.ui.friend.publish.AccountBean;
import com.hylg.igolf.ui.friend.publish.SdcardUtils;
import com.hylg.igolf.ui.friend.publish.SettingUtility;
import com.hylg.igolf.ui.friend.publish.WeiBoUser;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import cn.jpush.android.api.JPushInterface;

public class MainApp extends Application {

    private final String TAG = "MainApp";
    private static MainApp mainApp = null;
    private Customer customer;

    public AccountBean mAccount;

    private GlobalData globalData;

    private IWXAPI api;

    /*高德定位操作*/
    private LocationManagerProxy                mLocationManagerProxy;
    private myAMapLocationListener      		mAMapLocationListener;

    @Override
    public void onCreate() {
        super.onCreate();

        DebugTools.getDebug().debug_v(TAG, "---------->>>>>onCreate");
        init();
    }

    public Handler getHandler() {
        return mHandler;
    }

    Handler mHandler = new Handler() {

    };

    /**
     * 程序的文件目录，如果setting配置的是android，标志目录位于/sdcard/Application/PackageName/...下<br/>
     * 否则，就是/sdcard/setting[root_path]/...目录
     *
     * @return
     */
    public String getAppPath() {
        if ("android".equals(SettingUtility.getStringSetting("root_path")))
            return getExternalCacheDir().getAbsolutePath() + File.separator;

        return SdcardUtils.getSdcardPath() + File.separator + SettingUtility.getStringSetting("root_path") + File.separator;
    }

    public WeiBoUser getUser () {

       return mAccount.getUser();
    }

    public int getAppVersionCode () {

        int result = 200;
        try {
            result = this.getPackageManager().getPackageInfo(this.getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void init() {

		/*add a line*/

        Utils utils = new Utils();
        utils.LogH(false);
//		utils.LogN(true);
        // Set shared preferences file name
        Utils.logh("MainApp", SharedPref.getSpName());
        new SharedPref(MainApp.class.getPackage().getName());
        Utils.logh("MainApp", SharedPref.getSpName());
        mainApp = this;

        customer = new Customer();

        File file = this.getFilesDir();
        String path = file.getAbsolutePath()+ File.separator+"user.txt";
        File file1 = new File(path);
        FileInputStream fis;
        try {
            fis = new FileInputStream(file1);
            ObjectInputStream dis = new ObjectInputStream(fis);
            customer = (Customer)dis.readObject();

            DebugTools.getDebug().debug_v(TAG,"customer.id-------->>>>file_read-"+customer.id);

        } catch (Exception e) {

            customer = new Customer();
            e.printStackTrace();
        }


        mAccount = new AccountBean();
        WeiBoUser user = new WeiBoUser();
        user.setIdstr(customer.sn);
        mAccount.setUser(user);



        globalData = new GlobalData(this);
        Utils.ConnectionCheck(this);
        IntentFilter filter = new IntentFilter();

        api = WXAPIFactory.createWXAPI(this, "wx14da9bc6378845fe");

        api.registerApp("wx14da9bc6378845fe");

//		if(Utils.LOG_H) {
//			
//		}
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(hylgReceiver, filter);
        if (Utils.LOG_H) {
            JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        }
//        JPushInterface.init(getApplicationContext());     		// 初始化 JPush


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
    public void onTerminate() {
        super.onTerminate();

        DebugTools.getDebug().debug_v(TAG, "---------->>>>>onTerminate");
        unregisterReceiver(hylgReceiver);
        unregisterReceiver(jpushReceiver);
        Utils.logh("MainApp", "onTerminate");

        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(mAMapLocationListener);
            mLocationManagerProxy.destroy();
        }

        mAMapLocationListener = null;
        mLocationManagerProxy = null;

    }

    @Override
    public void onLowMemory() {

        DebugTools.getDebug().debug_v(TAG, "---------->>>>>onLowMemory");

        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {

        DebugTools.getDebug().debug_v(TAG, "---------->>>>>onTrimMemory");

        super.onTrimMemory(level);
    }

    private HylgReceiver hylgReceiver = new HylgReceiver();
    private JpushReceiver jpushReceiver = new JpushReceiver();

    public static MainApp getInstance() {

        return mainApp;
    }

    public Customer getCustomer() {
        return customer;
    }

    public GlobalData getGlobalData() {
        return globalData;
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

                MainApp.getInstance().getGlobalData().setLat(lat);
                MainApp.getInstance().getGlobalData().setLng(lng);

                DebugTools.getDebug().debug_v(TAG, "main_app_lat------------------>>>"+lat);
                DebugTools.getDebug().debug_v(TAG, "main_app_lng------------------>>>"+lng);

            } else {

                AlertDialog.Builder dialog = new AlertDialog.Builder(mainApp);

                dialog.setMessage("「爱高尔夫」需要获取您的位置，以提供更优质的服务。请开启您的定位服务或权限");
                dialog.setPositiveButton(R.string.str_setting_auth_title, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                });
                dialog.setNeutralButton(R.string.str_setting_gps_title, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
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
