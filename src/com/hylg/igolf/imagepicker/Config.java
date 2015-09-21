package com.hylg.igolf.imagepicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hylg.igolf.cs.data.FriendHotItem;

public class Config {
	public static final int TITLESIZE = 40;
	public static int SELECT_MAX_NUM = 9;
	public static int SELECT_MAX = 0;
	// public static List<Bitmap> bitmapdatas = new ArrayList<Bitmap>();
	public static ArrayList<String> drr = new ArrayList<String>();
	public static List<String> delimg = new ArrayList<String>();
	
	public static String ALL_IMAGE = "全部图片";
	
	public static String 	mLocationCityStr  		= "",
							mLocationProvinceStr    = "",
							mLocationRegionStr      = "",
							mLocationAliasStr       = "",
							mLocationAddrStr        = "";
	
	
	public static String    FRIEND_HOT              = "0",
							FRIEND_LOCAL            = "1",
							FRIEND_ATTENTION        = "2",
							FRIEND_NEW        		= "3";
	
	
	public static File      mTakePictrueFile        = null;
	
	
	public static FriendHotItem mFriendMessageNewItem = null;
	
	public static int tipsAmount = 0;
	public static String tipsIds = "";

}
