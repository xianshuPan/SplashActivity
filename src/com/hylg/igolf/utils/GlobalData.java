package com.hylg.igolf.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;

import com.hylg.igolf.R;
import com.hylg.igolf.ui.common.BaseRegion;
import com.hylg.igolf.ui.common.RegionData;

public class GlobalData {
	private static final String TAG = "GlobalData";
	private Context context;
	private DisplayMetrics metrics = null;
	private int sbar;
	public GlobalData(Context context) {
		this.context = context;
	}
	
	public DisplayMetrics getDisplayMetrics() {
		return metrics;
	}
	
	public int getStatusBarHeight() {
		return sbar;
	}
	
	public void setDisplayMetrics(DisplayMetrics metrics, int sbar) {
		this.sbar = sbar;
		this.metrics = metrics;
	}
	
	public void init() {
		initIndustryData(context);
	}
	
	public int pageSize = 20;
	public int startPage = 1;
	
	public int msgNumSys = 0;
	public int msgNumInvite = 0;

	public String card_no = "";
	public double balance = 0;
	public String bank_name = "";
	
	/*
	 * 2015.08.17
	 * auther pxs
	 * 添加下面两个字段，表示未读消息的条数及相关的帖子ID
	 * */
	public int tipsAmount = 0;
	public String tipsIds = "";
	
	// 自己发起开放式约球后，在切换到我的约球列表时，根据此标记判断更新
	// 在大厅列表中，申请+1、取消-1，计数判断标记更新
//	private boolean hasStartNew = false;
	private int inviteRefreshCount = 0;


	public double lat = 0;
	public double lng = 0;
	public String province_Str = "";

	public void setCardNo (String no) {

		if (no == null || no.length() < 0) {
			 return;
		}

		card_no = no;
	}

	public String getCardNo () {

		return card_no;
	}

	public void setBankName (String name) {

		if (name == null || name.length() < 0) {
			return;
		}

		bank_name = name;
	}

	public String getBankName () {

		return bank_name;
	}


	public void setProvinceStr (String name) {

		if (name == null || name.length() <= 0) {
			return;
		}

		province_Str = name;
	}

	public String getProvinceStr () {

		return province_Str;
	}

	public void setBalance (double latt) {

		if (latt < 0) {
			return;
		}

		balance  = latt;
	}

	public double getBalance  () {

		return balance ;
	}

	public void setLat (double latt) {

		if (latt <= 0) {
			return;
		}

		lat = latt;
	}

	public double getLat () {

		return lat;
	}

	public void setLng (double long1) {

		if (long1 <= 0) {
			return;
		}

		lng = long1;
	}

	public double getLng () {

		return lng;
	}
	
	public boolean hasStartNewInvite() {
		if(inviteRefreshCount > 0) {
			inviteRefreshCount = 0;
			return true;
		} else {
			return false;
		}
	}
	
	public void setHasStartNewInvite(boolean hasNew) {
		if(hasNew) {
			inviteRefreshCount ++;
		} else {
			inviteRefreshCount --;
		}
//		if(hasStartNew != hasNew) {
//			hasStartNew = hasNew;
//		}
	}
	
	// 只做信息获取，文件复制在检测更新时进行。
	private HashMap<String, String> industryMap = null;
	public ArrayList<String> getIndustryKeyList(boolean extra) {
		ArrayList<String> list = new ArrayList<String>();
		if(null == industryMap) {
			initIndustryData(context);
		}
		if(!industryMap.isEmpty()) {
			for(String key : industryMap.keySet()) {
				if(Const.CFG_ALL_INDUSTRY.equalsIgnoreCase(key)) {
					if(extra) {
						list.add(0, key);
					}
				} else {
					list.add(key);
				}
			}
		}
		return list;
	}
	
	public String getIndustryName(String key) {
		if(null == industryMap) {
			initIndustryData(context);
		}
		return industryMap.get(key);
	}
	private void initIndustryData(Context context) {
		industryMap = new HashMap<String, String>();
		InputStream is = null;
		File f = FileUtils.getExternalCfgIndustry(context);
		// 外设文件不存在，直接读取默认配置
		if(null == f) {
			Utils.logh(TAG, "initIndustryData get file from assets");
			try {
				is = context.getAssets().open(FileUtils.getAssetsCfgIndustryPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Utils.logh(TAG, "initIndustryData get file from external");
			try {
				is = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			JSONObject jo = new JSONObject(FileUtils.transferIs2String(is));
			JSONObject infoObj = jo.getJSONObject("info");
			JSONArray ja = infoObj.getJSONArray("industries");
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.getJSONObject(i);
				industryMap.put(obj.getString("dictKey"), obj.getString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 只做信息获取，文件复制在检测更新时进行。
	private BaseRegion regionData = null;
	public BaseRegion getBaseRegion() {
		if(null == regionData) {
			initRegionData(context);
		}
		return regionData;
	}
	private void initRegionData(Context context) {
		regionData = new BaseRegion();
		InputStream is = null;
		File f = FileUtils.getExternalCfgRegion(context);
		// 外设文件不存在，直接读取默认配置
		if(null == f) {
			Utils.logh(TAG, "initRegionData get file from assets");
			try {
				is = context.getAssets().open(FileUtils.getAssetsCfgRegionPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Utils.logh(TAG, "initRegionData get file from external");
			try {
				is = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			JSONObject jo = new JSONObject(FileUtils.transferIs2String(is));
			JSONObject infoObj = jo.getJSONObject("info");
			JSONArray states = infoObj.getJSONArray("states");
			for(int i=0, len=states.length(); i<len; i++) {
				JSONObject obj = states.getJSONObject(i);
				String key = obj.getString("dictKey");
				if(!Const.CFG_ALL_REGION.equals(key)) { // 省级
					// 获取该省级下，城市映射
//					HashMap<String, RegionData> children = null;
					HashMap<String, String> children = null; 
					JSONArray cities = obj.optJSONArray("cities");
					if(null != cities) {
//						children = new HashMap<String, RegionData>();
						children = new HashMap<String, String>();
						String cityKey;
						for(int j=0, length=cities.length(); j<length; j++) {
							JSONObject city = cities.getJSONObject(j);
							cityKey = city.getString("dictKey");
//							children.put(cityKey, new RegionData(city.getString("name"), cityKey));
							children.put(cityKey, city.getString("name"));
						}
					}
					// 添加到省列表
					regionData.province.add(new RegionData(obj.getString("name"), key, children));
				} else {
					regionData.nation = new RegionData(obj.getString("name"), key);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getRegionName(String key) {
		if(null == regionData) {
			initRegionData(context);
		}
		// 全国
		if(Const.CFG_ALL_REGION.equals(key)) {
			return regionData.nation.name;
		}
		// 省份
		ArrayList<RegionData> pl = regionData.province;
		for(int p=0, size=pl.size(); p<size; p++) {
			RegionData province = pl.get(p);
			if(province.dictKey.equals(key)) {
				return province.name;
			}
			// 城市
			if(null != province.children && province.children.containsKey(key)) {
//				return province.children.get(key).name;
				return province.children.get(key);
			}
		}
		return "";
	}
	
	// 性别，固定值，筛选有不限
	private HashMap<Integer, String> sexMap = null;
	public ArrayList<Integer> getSexKeyList(boolean extra) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if(null == sexMap) {
			initSexMap(context);
		}
		if(!sexMap.isEmpty()) {
			for(int key : sexMap.keySet()) {
				if(Const.SEX_ALL == key) {
					if(extra) {
						list.add(0, key);
					}
				} else {
					list.add(key);
				}
			}
		}
		return list;
	}
	public String getSexName(int value) {
		if(null == sexMap) {
			initSexMap(context);
		}
		return sexMap.get(value);
	}
	private void initSexMap(Context context) {
		Resources res = context.getResources();
		sexMap = new HashMap<Integer, String>();
		TypedArray sexTa = res.obtainTypedArray(R.array.sex_index_array);
		String[] sexNames = res.getStringArray(R.array.sex_name_array);
		for(int i=0,len=sexTa.length(); i<len; i++) {
			sexMap.put(sexTa.getInt(i, 1), sexNames[i]);
		}
		sexTa.recycle();
	}
	
	// 教练类型，固定值，筛选有不限
	private HashMap<Integer, String> coachTypeMap = null;
	public ArrayList<Integer> getCoachTypeKeyList(boolean extra) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if(null == coachTypeMap) {
			initCoachTypeMap(context);
		}
		if(!coachTypeMap.isEmpty()) {
			for(int key : coachTypeMap.keySet()) {
				if(Const.COACH_TYPE_ALL == key) {
					if(extra) {
						list.add(0, key);
					}
				} else {
					list.add(key);
				}
			}
		}
		return list;
	}
	
	public String getCoachTypeName(int value) {
		if(null == coachTypeMap) {
			initCoachTypeMap(context);
		}
		return coachTypeMap.get(value);
	}
	
	private void initCoachTypeMap(Context context) {
		Resources res = context.getResources();
		coachTypeMap = new HashMap<Integer, String>();
		TypedArray sexTa = res.obtainTypedArray(R.array.coacher_type_index_array);
		String[] sexNames = res.getStringArray(R.array.coacher_type_name_array);
		for(int i=0,len=sexTa.length(); i<len; i++) {
			coachTypeMap.put(sexTa.getInt(i, 1), sexNames[i]);
		}
		sexTa.recycle();
	}
	
	/*教练列表的排序选项*/
	private HashMap<Integer, String> coachSortMap = null;
	public ArrayList<Integer> getCoachSortKeyList(boolean extra) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if(null == coachSortMap) {
			initCoachSortItemMap(context);
		}
		if(!coachSortMap.isEmpty()) {
			for(int key : coachSortMap.keySet()) {
				if(Const.COACH_TYPE_ALL == key) {
					if(extra) {
						list.add(0, key);
					}
				} else {
					list.add(key);
				}
			}
		}
		return list;
	}
	
	public String getCoachSortItemName(int value) {
		if(null == coachSortMap) {
			initCoachSortItemMap(context);
		}
		return coachSortMap.get(value);
	}
	
	private void initCoachSortItemMap(Context context) {
		Resources res = context.getResources();
		coachSortMap = new HashMap<Integer, String>();
		TypedArray sexTa = res.obtainTypedArray(R.array.coacher_sort_index_array);
		String[] sexNames = res.getStringArray(R.array.coacher_sort_name_array);
		for(int i=0,len=sexTa.length(); i<len; i++) {
			coachSortMap.put(sexTa.getInt(i, 1), sexNames[i]);
		}
		sexTa.recycle();
	}
	
	// 兴趣标签，固定值
	private HashMap<Integer, String> labelMap = null;
	public ArrayList<Integer> getLabelKeyList() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if(null == labelMap) {
			initLabelMap(context);
		}
		if(!labelMap.isEmpty()) {
			for(int key : labelMap.keySet()) {
				list.add(key);
			}
		}
		return list;
	}
	public String getLabelName(int value) {
		if(null == labelMap) {
			initLabelMap(context);
		}
		return labelMap.get(value);
	}
	private void initLabelMap(Context context) {
		Resources res = context.getResources();
		labelMap = new HashMap<Integer, String>();
		TypedArray ta = res.obtainTypedArray(R.array.golfers_label_type_array);
		String[] names = res.getStringArray(R.array.golfers_label_name_array);
		for(int i=0,len=ta.length(); i<len; i++) {
			labelMap.put(ta.getInt(i, 0), names[i]);
		}
		ta.recycle();
	}
	
	// 球注，固定值，筛选有不限
	private HashMap<Integer, String> stakeMap = null;
	public ArrayList<Integer> getStakeKeyList(boolean extra) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if(null == stakeMap) {
			initStakeMap(context);
		}
		if(!stakeMap.isEmpty()) {
			for(int key : stakeMap.keySet()) {
				if(Const.STAKE_ALL == key) {
					if(extra) {
						list.add(0, key);
					}
				} else {
					list.add(key);
				}
			}
		}
		return list;
	}
	public String getStakeName(int value) {
		if(null == stakeMap) {
			initStakeMap(context);
		}
		return stakeMap.get(value);
	}
	private void initStakeMap(Context context) {
		Resources res = context.getResources();
		stakeMap = new HashMap<Integer, String>();
		TypedArray payTypeTa = res.obtainTypedArray(R.array.stake_value_array);
		String[] payTypeNames = res.getStringArray(R.array.stake_name_array);
		for(int i=0,len=payTypeTa.length(); i<len; i++) {
			stakeMap.put(payTypeTa.getInt(i, 1), payTypeNames[i]);
		}
		payTypeTa.recycle();
	}
	
	// 付款方式，固定值，筛选有不限
	private HashMap<Integer, String> payTypeMap = null;
	public ArrayList<Integer> getPayTypeKeyList(boolean extra) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if(null == payTypeMap) {
			initPayTypeMap(context);
		}
//		if(!payTypeMap.isEmpty()) {
//			for(int key : payTypeMap.keySet()) {
//				if(Const.PAY_TYPE_ALL == key) {
//					if(extra) {
//						list.add(0, key);
//					}
//				} else {
//					list.add(key);
//				}
//			}
//		}
		// 文字对应值的顺序，非递增顺序，需根据数组中值的顺序排序
		if(!payTypeMap.isEmpty()) {
			// 首先，添加 不限 项
			if(extra) {
				list.add(Const.PAY_TYPE_ALL);
			}
			TypedArray payTypeTa = context.getResources().obtainTypedArray(R.array.payment_value_array);
			// 从1开始，添加其他项
			for(int i=1,len=payTypeTa.length(); i<len; i++) {
				list.add(payTypeTa.getInt(i, 1));
			}
			payTypeTa.recycle();
		}
		return list;
	}
	public String getPayTypeName(int value) {
		if(null == payTypeMap) {
			initPayTypeMap(context);
		}
		return payTypeMap.get(value);
	}
	private void initPayTypeMap(Context context) {
		Resources res = context.getResources();
		payTypeMap = new HashMap<Integer, String>();
		TypedArray payTypeTa = res.obtainTypedArray(R.array.payment_value_array);
		String[] payTypeNames = res.getStringArray(R.array.payment_name_array);
		for(int i=0,len=payTypeTa.length(); i<len; i++) {
			payTypeMap.put(payTypeTa.getInt(i, 1), payTypeNames[i]);
		}
		payTypeTa.recycle();
	}
	
	// 开球日期，固定值，筛选有不限
	private HashMap<Integer, String> teeDateMap = null;
	public ArrayList<Integer> getTeeDateKeyList(boolean extra) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if(null == teeDateMap) {
			initTeeDateMap(context);
		}
		if(!teeDateMap.isEmpty()) {
			for(int key : teeDateMap.keySet()) {
				if(Const.TEE_DATE_ALL == key) {
					if(extra) {
						list.add(0, key);
					}
				} else {
					list.add(key);
				}
			}
		}
		return list;
	}
	public String getTeeDateName(int value) {
		if(null == teeDateMap) {
			initTeeDateMap(context);
		}
		return teeDateMap.get(value);
	}
	private void initTeeDateMap(Context context) {
		Resources res = context.getResources();
		teeDateMap = new HashMap<Integer, String>();
		TypedArray sexTa = res.obtainTypedArray(R.array.tee_date_index_array);
		String[] sexNames = res.getStringArray(R.array.tee_date_name_array);
		for(int i=0,len=sexTa.length(); i<len; i++) {
			teeDateMap.put(sexTa.getInt(i, 1), sexNames[i]);
		}
		sexTa.recycle();
	}
	
	// 开球时间，固定值，筛选有不限
	private HashMap<Integer, String> teeTimeMap = null;
	public ArrayList<Integer> getTeeTimeKeyList(boolean extra) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if(null == teeTimeMap) {
			initTeeTimeMap(context);
		}
		if(!teeTimeMap.isEmpty()) {
			for(int key : teeTimeMap.keySet()) {
				if(Const.TEE_TIME_ALL == key) {
					if(extra) {
						list.add(0, key);
					}
				} else {
					list.add(key);
				}
			}
		}
		return list;
	}
	public String getTeeTimeName(int value) {
		if(null == teeTimeMap) {
			initTeeTimeMap(context);
		}
		return teeTimeMap.get(value);
	}
	private void initTeeTimeMap(Context context) {
		Resources res = context.getResources();
		teeTimeMap = new HashMap<Integer, String>();
		TypedArray sexTa = res.obtainTypedArray(R.array.tee_time_index_array);
		String[] sexNames = res.getStringArray(R.array.tee_time_name_array);
		for(int i=0,len=sexTa.length(); i<len; i++) {
			teeTimeMap.put(sexTa.getInt(i, 1), sexNames[i]);
		}
		sexTa.recycle();
	}
}
