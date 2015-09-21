package com.hylg.igolf.ui.hall;

import cn.gl.lib.view.NestGridView;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;
import com.hylg.igolf.ui.common.PayTypeSelectActivity;
import com.hylg.igolf.ui.common.PayTypeSelectActivity.onPayTypeSelectListener;
import com.hylg.igolf.ui.common.RegionSelectActivity;
import com.hylg.igolf.ui.common.RegionSelectActivity.onRegionSelectListener;
import com.hylg.igolf.ui.common.SexSelectActivity;
import com.hylg.igolf.ui.common.SexSelectActivity.onSexSelectListener;
import com.hylg.igolf.ui.common.StakeSelectActivity;
import com.hylg.igolf.ui.common.StakeSelectActivity.onStakeSelectListener;
import com.hylg.igolf.ui.common.TeeDateSelectActivity;
import com.hylg.igolf.ui.common.TeeDateSelectActivity.onTeeDateSelectListener;
import com.hylg.igolf.ui.common.TeeTimeSelectActivity;
import com.hylg.igolf.ui.common.TeeTimeSelectActivity.onTeeTimeSelectListener;
import com.hylg.igolf.ui.hall.adapter.MultiFiltersAdapter;
import com.hylg.igolf.ui.hall.adapter.MultiFiltersAdapter.OnMultiFilterItemClickListener;
import com.hylg.igolf.ui.reqparam.GetOpenInviteReqParam;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.GlobalData;
import com.hylg.igolf.utils.SharedPref;
import com.hylg.igolf.utils.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class HallOpenPresetFrg extends Fragment 
								implements OnClickListener,
									onPayTypeSelectListener, onStakeSelectListener,
									onSexSelectListener, onRegionSelectListener,
									onTeeTimeSelectListener, onTeeDateSelectListener {
	private static final String TAG = "HallOpenPresetFrg";
	private NestGridView filtersGv;
	private MultiFiltersAdapter filtersAdapter = null;
	private int curTeeDate;
	private int curTeeTime;
	private String curRegion;
	private int curStake;
	private int curPayType;
	private int curSex;
	private GlobalData gd;
	// 是否记录检索条件
	private static final boolean NOTE_FILTER = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Bundle args = getArguments();
		gd = MainApp.getInstance().getGlobalData();
		curTeeDate = Const.TEE_DATE_ALL;
		curTeeTime = Const.TEE_TIME_ALL;
//		curRegion = MainApp.getInstance().getCustomer().state;
		curRegion = Const.CFG_ALL_REGION;
		if(NOTE_FILTER) {
			// 默认全国，记录上次选择
			String region = SharedPref.getString(SharedPref.PREFS_KEY_HALL_DEF_REGION, getActivity());
			if(!SharedPref.isInvalidPrefString(region)) {
				curRegion = region;
			}			
		}
		curSex = Const.SEX_ALL;
		curStake = Const.STAKE_ALL;
		curPayType = Const.PAY_TYPE_ALL;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.hall_frg_open_preset, container, false);
		view.findViewById(R.id.hall_open_preset_retrieve).setOnClickListener(this);
		view.findViewById(R.id.hall_open_preset_start_invite).setOnClickListener(this);
		filtersGv = (NestGridView) view.findViewById(R.id.hall_open_preset_filter_gridview);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if(null == filtersAdapter) {
			filtersAdapter = new MultiFiltersAdapter(getActivity(), 
						new OnMultiFilterItemClickListener() {
							@Override
							public void onMultiFilterItemClick(int position) {
								Utils.logh(TAG, "onMultiFilterItemClick position: " + position);
								onMultiFilterClick(position);
							}
						},
					gd.getTeeDateName(curTeeDate),
					gd.getTeeTimeName(curTeeTime),
					gd.getRegionName(curRegion),
					gd.getSexName(curSex),
					gd.getPayTypeName(curPayType),
					gd.getStakeName(curStake));
			Utils.logh(TAG, "onViewCreated new filtersAdapter: " + filtersAdapter);
		} else {
			Utils.logh(TAG, "exist filtersAdapter " + filtersAdapter);
		}
		filtersGv.setAdapter(filtersAdapter);
	}
	
	private void onMultiFilterClick(int position) {
		Utils.logh(TAG, "onMultiFilterClick position: " + position);
		switch(position) {
			case 0: // date
				TeeDateSelectActivity.startTeeDateSelect(HallOpenPresetFrg.this, true, curTeeDate);
				break;
			case 1: // time
				TeeTimeSelectActivity.startTeeDateSelect(HallOpenPresetFrg.this, true, curTeeTime);
				break;
			case 2: // region
				RegionSelectActivity.startRegionSelect(HallOpenPresetFrg.this,
							RegionSelectActivity.REGION_TYPE_FILTER_ALL, curRegion);
				break;
			case 3: // sex
				SexSelectActivity.startSexSelect(HallOpenPresetFrg.this, true, curSex);
				break;
			case 4: // pay type
				PayTypeSelectActivity.startPayTypeSelect(HallOpenPresetFrg.this, true, curPayType);
				break;
			case 5: // stake
				StakeSelectActivity.startStakeSelect(HallOpenPresetFrg.this, true, curStake);
				break;
		}
	}



	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.hall_open_preset_retrieve:
				doRetrieve();
				break;
			case R.id.hall_open_preset_start_invite:
				startInviteOpenDirect();
				break;
		}
	}

	private void startInviteOpenDirect() {
		Utils.logh(TAG, "startInviteOpenDirect");
		StartInviteOpenActivity.startOpenInviteNew(getActivity());
		getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	private void doRetrieve() {
		Utils.logh(TAG, "doRetrieve");
		GetOpenInviteReqParam reqParam = new GetOpenInviteReqParam();
		reqParam.sn = MainApp.getInstance().getCustomer().sn;
		reqParam.date = curTeeDate;
		reqParam.time = curTeeTime;
		reqParam.location = curRegion;
		reqParam.sex = curSex;
		reqParam.pay = curPayType;
		reqParam.stake = curStake;
		reqParam.pageNum = MainApp.getInstance().getGlobalData().startPage;
		reqParam.pageSize = MainApp.getInstance().getGlobalData().pageSize;
		
		OpenInviteListActivity.startOpenInvite(getActivity(), reqParam);
		getActivity().overridePendingTransition(R.anim.ac_slide_right_in, R.anim.ac_slide_left_out);
	}

	@Override
	public void onPayTypeSelect(int newType) {
		curPayType = newType;
		filtersAdapter.refreshPayType(gd.getPayTypeName(newType));
	}

	@Override
	public void onStakeSelect(int newStake) {
		curStake = newStake;
		filtersAdapter.refreshStake(gd.getStakeName(newStake));
	}

	@Override
	public void onSexSelect(int newSex) {
		curSex = newSex;
		filtersAdapter.refreshSex(gd.getSexName(newSex));
	}

	@Override
	public void onRegionSelect(String newRegion) {
		if(NOTE_FILTER) {
			// 存储地区选择
			SharedPref.setString(SharedPref.PREFS_KEY_HALL_DEF_REGION, newRegion, getActivity());
		}
		curRegion = newRegion;
		filtersAdapter.refreshRegion(gd.getRegionName(newRegion));
	}

	@Override
	public void onTeeDateSelect(int newTeeDate) {
		curTeeDate = newTeeDate;
		filtersAdapter.refreshTeeDate(gd.getTeeDateName(newTeeDate));
	}

	@Override
	public void onTeeTimeSelect(int newTeeTime) {
		curTeeTime = newTeeTime;
		filtersAdapter.refreshTeeTime(gd.getTeeTimeName(newTeeTime));
	}
}
