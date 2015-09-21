package com.hylg.igolf;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import cn.jpush.android.api.JPushInterface;

import com.hylg.igolf.broadcast.HylgReceiver;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.jpush.JpushReceiver;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainApp extends Application {
	private static MainApp mainApp = null;
	private Customer customer;
	
	private GlobalData globalData;
	
	 private IWXAPI api;
	
	@Override
	public void onCreate() {
		super.onCreate();
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
        if(Utils.LOG_H) {
        	JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        }
//        JPushInterface.init(getApplicationContext());     		// 初始化 JPush
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		unregisterReceiver(hylgReceiver);
		unregisterReceiver(jpushReceiver);
		Utils.logh("MainApp", "onTerminate");
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
}
