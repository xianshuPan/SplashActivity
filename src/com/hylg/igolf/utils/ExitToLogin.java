package com.hylg.igolf.utils;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

public class ExitToLogin {
	// Single instance
	private static ExitToLogin instance = null;
	// Activity list
	private List<Activity> mActivitys = new LinkedList<Activity>();
	
	private ExitToLogin() {};
	
	/**
	 * Get single instance.
	 * twice judgment, and synchronized for multiple thread.
	 */
	public static ExitToLogin getInstance() {
		if(null == instance) {
			synchronized(ExitToLogin.class) {
				if(null == instance) {
					instance = new ExitToLogin();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Add activity to list, so, we can finish all while need exit
	 *  the application.
	 * @param activity The activity that application start now
	 * @see exit()
	 */
	public void addActivity(Activity activity) {
		mActivitys.add(activity);
	}
	
	/**
	 * reload customer, so all activity above login, Also, do clear other relative data.
	 * 
	 */
	public void exitToLogin() {
		if(!mActivitys.isEmpty()) {
			for(Activity activity : mActivitys) {
				activity.finish();
			}
			mActivitys.clear();
		}
	}

}
