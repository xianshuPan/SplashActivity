package com.hylg.igolf.utils;

import android.content.Context;
import android.util.Base64;

import cn.gl.lib.utils.BaseSharedPref;

public class SharedPref extends BaseSharedPref {

	public SharedPref(String name) {
		super(name);
	}

	public static String getSpName() {
		return PREFS_NAME;
	}
	
	public final static String SPK_LAST_VER = "last_version";

	private final static String SPK_PHONE = "phone";
	private final static String SPK_PWD = "password";
	
	public final static String SPK_SN = "sn";
	public final static String SPK_AVATAR = "avatar";
	
	/**
	 *  Progress id, check for accident killed by system, eg: for low memory.
	 */
	public final static String PREFS_KEY_PID = "last_pid";
	public final static String PREFS_KEY_VER_CODE = "ver_code";
	
	public final static String PREFS_KEY_GOLFER_DEF_REGION = "golfer_def_region";
	public final static String PREFS_KEY_GOLFER_DEF_SEX = "golfer_def_sex";
	public final static String PREFS_KEY_GOLFER_DEF_INDUSTRY = "golfer_def_industry";
	public final static String PREFS_KEY_GOLFER_DEF_LABLE = "golfer_def_lable";
	
	public final static String PREFS_KEY_HALL_DEF_REGION = "hall_def_region";

	public final static String PREFS_KEY_SHOW_LOCATION_SETTING = "show_location_setting";
	
	public static String getPrefPhone(Context c) {
		String phone = getString(SPK_PHONE, c);
		if(isInvalidPrefString(phone)) {
			return PREFS_STR_INVALID;
		}
		return new String(Base64.decode(phone.getBytes(), Base64.DEFAULT));
	}
	
	public static void setPrefPhone(Context c, String phone) {
		setString(SPK_PHONE, new String(Base64.encodeToString(phone.getBytes(), Base64.DEFAULT)), c);
	}

	public static String getPrefPwd(Context c) {
		String pwd = getString(SPK_PWD, c);
		if(isInvalidPrefString(pwd)) {
			return PREFS_STR_INVALID;
		}
		return new String(Base64.decode(pwd.getBytes(), Base64.DEFAULT));
	}
	
	public static void setPrefPwd(Context c, String pwd) {
		setString(SPK_PWD, new String(Base64.encodeToString(pwd.getBytes(), Base64.DEFAULT)), c);
	}
	
	public static void clearPrefPwd(Context c) {
		setInvalidPreString(SPK_PWD, c);
	}
}
