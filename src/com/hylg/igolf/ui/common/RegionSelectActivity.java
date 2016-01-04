package com.hylg.igolf.ui.common;

import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hylg.igolf.utils.Utils;

public class RegionSelectActivity extends Activity {
	private static final String TAG = "RegionSelectActivity";
	protected static onRegionSelectListener listener = null;
	private final static String BUNDLE_CUR_REGION = "current_region";
	// 球场设置时，只能选择到省
	public final static int REGION_TYPE_SELECT_COURSE = 1;
	// 个人信息设置时，只能设置到
	public final static int REGION_TYPE_SET_INFO = 2;
	// 查询选择，需要显示全国
	public final static int REGION_TYPE_FILTER_ALL = 3;
//	private int curType;
	protected String curRegion;

	public static void startRegionSelect(Context context, int type, String curRegion) {
		try {
			listener = (onRegionSelectListener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + 
					" must implements onRegionSelectListener");
		}
		Intent intent = getRegionIntent(context, type);
		intent.putExtra(BUNDLE_CUR_REGION, curRegion);
		context.startActivity(intent);
	}

	public static void startRegionSelect(Fragment fragment, int type, String curRegion) {
		try {
			listener = (onRegionSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() + 
					" must implements onRegionSelectListener");
		}
		Intent intent = getRegionIntent(fragment.getActivity(), type);
		intent.putExtra(BUNDLE_CUR_REGION, curRegion);
		fragment.startActivity(intent);
	}
	
	private static Intent getRegionIntent(Context context, int type) {
		switch(type) {
			case REGION_TYPE_FILTER_ALL:
				return new Intent(context, RegionFilterSelectActivity.class);
			case REGION_TYPE_SELECT_COURSE:
				return new Intent(context, RegionCourseSelectActivity.class);
			case REGION_TYPE_SET_INFO:
				return new Intent(context, RegionInfoSelectActivity.class);
		}
		return null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		curRegion = getIntent().getExtras().getString(BUNDLE_CUR_REGION);
		Utils.logh(TAG, " curRegion: " + curRegion);
	}

	public interface onRegionSelectListener {
		void onRegionSelect(String newRegion);
	}
	
	// 根据key排序
	public class RegionSort implements Comparator<RegionData> {
		public int compare(RegionData rd0, RegionData rd1) {
			return rd0.dictKey.compareToIgnoreCase(rd1.dictKey);
		}
	}
}
