package com.hylg.igolf.ui.golfers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.gl.lib.view.NestGridView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.ui.common.IndustrySelectActivity;
import com.hylg.igolf.ui.common.IndustrySelectActivity.onIndustrySelectListener;
import com.hylg.igolf.ui.common.RegionSelectActivity;
import com.hylg.igolf.ui.common.RegionSelectActivity.onRegionSelectListener;
import com.hylg.igolf.ui.common.SexSelectActivity;
import com.hylg.igolf.ui.common.SexSelectActivity.onSexSelectListener;
import com.hylg.igolf.ui.golfers.GolfersListActivity.OnClearSetupListener;
import com.hylg.igolf.ui.golfers.adapter.FiltersAdapter;
import com.hylg.igolf.ui.golfers.adapter.LabelAdapter;
import com.hylg.igolf.ui.golfers.adapter.FiltersAdapter.OnFilterItemClickListener;
import com.hylg.igolf.ui.golfers.adapter.LabelAdapter.OnLabelItemClickListener;
import com.hylg.igolf.ui.reqparam.GetGolfersReqParam;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;

public class GolfersHomeFrg extends Fragment
								implements onSexSelectListener, onIndustrySelectListener,
									onRegionSelectListener {
	private static final String TAG = "GolfersHomeFrg";
	private static GolfersHomeFrg golfersFrg = null;
	private NestGridView labelGv;
	private NestGridView filtersGv;
	private LabelAdapter labelAdapter = null;
	private FiltersAdapter filtersAdapter = null;
	
	
	private ImageView 	mCustomerImage = null;
	
	
	private String curRegion;
	private String curIndustry;
	private int curSex;
	private GlobalData gd;
	// 是否记录检索条件
	public static final boolean NOTE_FILTER = false;
	
	public static  GolfersHomeFrg getInstance() {
		if(null == golfersFrg) {
			golfersFrg = new GolfersHomeFrg();
			
		}
		return golfersFrg;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Bundle args = getArguments();
		gd = MainApp.getInstance().getGlobalData();
		initRequestParam();
		GolfersListActivity.setOnClearSetupListener(onClearSetupListener);
	}
	
	private OnClearSetupListener onClearSetupListener = new OnClearSetupListener() {
		@Override
		public void clearSetup() {
			// 从列表返回时，清空筛选条件
			initRequestParam();
			filtersAdapter = new FiltersAdapter(getActivity(), new OnFilterItemClickListener() {
				@Override
				public void onFilterItemClick(int position) {
					onFilterClick(position);
				}
			}, 
			gd.getRegionName(curRegion),
			gd.getIndustryName(curIndustry),
			gd.getSexName(curSex));
			Utils.logh(TAG, "clearSetup new filtersAdapter: " + filtersAdapter);
			filtersGv.setAdapter(filtersAdapter);
		}
	};
	
	private void initRequestParam() {
		curRegion = Const.CFG_ALL_REGION;
		if(NOTE_FILTER) {
			// 默认全国，记录上次选择
			String region = SharedPref.getString(SharedPref.PREFS_KEY_GOLFER_DEF_REGION, getActivity());
			if(!SharedPref.isInvalidPrefString(region)) {
				curRegion = region;
			}			
		}

		curSex = Const.SEX_ALL;
		if(NOTE_FILTER) {
			// 默认不限，记录上次选择
			int sex = SharedPref.getInt(SharedPref.PREFS_KEY_GOLFER_DEF_SEX, getActivity());
			if(SharedPref.PREFS_INT_INVALID != sex) {
				curSex = sex;
			}
		}
		curIndustry = Const.CFG_ALL_INDUSTRY;
		if(NOTE_FILTER) {
			// 默认全部行业，记录上次选择
			String industry = SharedPref.getString(SharedPref.PREFS_KEY_GOLFER_DEF_INDUSTRY, getActivity());
			if(!SharedPref.isInvalidPrefString(industry)) {
				curIndustry = industry;
			}
		}
//		curRegion = MainApp.getInstance().getCustomer().state;
//		curIndustry = Const.CFG_ALL_INDUSTRY;
//		curSex = Const.SEX_ALL;
	}
	
	@Override
	public void onDestroy() {
		Utils.logh(TAG, "onDestroy GolfersHomeFrg");
		super.onDestroy();
		// 切换帐号时，需重新查询，清空数据
//		labelAdapter = null;
		filtersAdapter = null;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.golfers_frg_home, container, false);
		getViews(view);
		return view;
	}

	private void getViews(View view) { 
		labelGv = (NestGridView) view.findViewById(R.id.golfers_label_gridview);
		filtersGv = (NestGridView) view.findViewById(R.id.golfers_filters_gridview);
		
//		mCustomerImageCustomerImage = (ImageView) view.findViewById(R.id.friend_frg_camera_customer_image);
//	    mCustomerImage.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					Intent intent = new Intent(getActivity(), CustomerHomeActivity.class);
//					startActivity(intent);
//				}
//	     });
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if(null == labelAdapter) {
			labelAdapter = new LabelAdapter(getActivity(), new OnLabelItemClickListener() {
				@Override
				public void onLabelItemClick(int type, boolean labelIndustry) {
					onLabelClick(type, labelIndustry);
				}
			});
			Utils.logh(TAG, "onViewCreated new labelAdapter: " + labelAdapter);
		} else {
			Utils.logh(TAG, "exist labelAdapter " + labelAdapter);
		}
		labelGv.setAdapter(labelAdapter);
		if(null == filtersAdapter) {
			filtersAdapter = new FiltersAdapter(getActivity(), new OnFilterItemClickListener() {
				@Override
				public void onFilterItemClick(int position) {
					onFilterClick(position);
				}
			}, 
			gd.getRegionName(curRegion),
			gd.getIndustryName(curIndustry),
			gd.getSexName(curSex));
			Utils.logh(TAG, "onViewCreated new filtersAdapter: " + filtersAdapter);
		} else {
			Utils.logh(TAG, "exist filtersAdapter " + filtersAdapter);
		}
		filtersGv.setAdapter(filtersAdapter);
	}

	private void onLabelClick(int type, boolean labelIndustry) {
		Utils.logh(TAG, "onLabelClick type: " + type);
		if(!Utils.isConnected(getActivity())) {
			return ;
		}
		GetGolfersReqParam data = new GetGolfersReqParam();
		data.sn = MainApp.getInstance().getCustomer().sn;
		data.label = type;
		data.region = curRegion;
		if(labelIndustry) {
			String myIndustry = MainApp.getInstance().getCustomer().industry;
			if(!myIndustry.equals(curIndustry)) {
				if(NOTE_FILTER) {
					SharedPref.setString(SharedPref.PREFS_KEY_GOLFER_DEF_INDUSTRY, myIndustry, getActivity());
				}
				curIndustry = myIndustry;
				filtersAdapter.refreshIndustry(gd.getIndustryName(myIndustry));
			}
		}
		data.industry = curIndustry;
		data.sex = curSex;
		data.pageNum = MainApp.getInstance().getGlobalData().startPage;
		data.pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
		GolfersListActivity.startGolfersList(getActivity(), data);
		getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}
	
	private void onFilterClick(int position) {
		Utils.logh(TAG, "onFilterClick position: " + position);
		switch(position) {
			case 0: // region
				RegionSelectActivity.startRegionSelect(GolfersHomeFrg.this,
						RegionSelectActivity.REGION_TYPE_FILTER_ALL, curRegion);
				break;
			case 1: // industry
				IndustrySelectActivity.startIndustrySelect(GolfersHomeFrg.this, true, curIndustry);
				break;
			case 2: // sex
				SexSelectActivity.startSexSelect(GolfersHomeFrg.this, true, curSex);
				break;				
		}
	}

	/**
	 * 蒙板引导点击，默认条件查询列表。
	 */
	public void onMaskGuideClick() {
		labelAdapter.onMaskGuideClick();
	}
	
	@Override
	public void onSexSelect(int newSex) {
		if(NOTE_FILTER) {
			// 存储性别选择
			SharedPref.setInt(SharedPref.PREFS_KEY_GOLFER_DEF_SEX, newSex, getActivity());
		}
		curSex = newSex;
		filtersAdapter.refreshSex(gd.getSexName(newSex));
	}

	@Override
	public void onIndustrySelect(String newIndustry) {
		if(NOTE_FILTER) {
			// 存储行业选择
			SharedPref.setString(SharedPref.PREFS_KEY_GOLFER_DEF_INDUSTRY, newIndustry, getActivity());
		}
		curIndustry = newIndustry;
		filtersAdapter.refreshIndustry(gd.getIndustryName(newIndustry));
	}

	@Override
	public void onRegionSelect(String newRegion) {
		if(NOTE_FILTER) {
			// 存储地区选择
			SharedPref.setString(SharedPref.PREFS_KEY_GOLFER_DEF_REGION, newRegion, getActivity());
		}
		curRegion = newRegion;
		filtersAdapter.refreshRegion(gd.getRegionName(newRegion));
	}

}
