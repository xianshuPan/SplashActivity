package com.hylg.igolf;

import android.app.Application;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;

import cn.jpush.android.api.JPushInterface;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.hylg.igolf.broadcast.HylgReceiver;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.jpush.JpushReceiver;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainApp extends Application {

    private final String TAG = "MainApp";
    private static MainApp mainApp = null;
    private Customer customer;

    private GlobalData globalData;

    private IWXAPI api;

    /*高德定位操作*/
    private LocationManagerProxy                mLocationManagerProxy;
    private myAMapLocationListener      		mAMapLocationListener;

    @Override
    public void onCreate() {
        super.onCreate();

        DebugTools.getDebug().debug_v(TAG,"---------->>>>>onCreate");
        init();
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
            mLocationManagerProxy.destory();
        }

        mAMapLocationListener = null;
        mLocationManagerProxy = null;

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
            }
        }

    }
}
