package com.hylg.igolf.ui.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;

import com.hylg.igolf.MainApp;

import java.util.ArrayList;

public class CoacherSortItemSelectFragment extends BaseSelectFragment {
	private static final String TAG = "CoacherSortItemSelectFragment";
	private static onCoacherSortItemSelectListener listener = null;
	private final static String BUNDLE_CURR_TYPE = "current_type";

	private static FragmentActivity from;

	public static void startCoacherSortItemSelect(FragmentActivity fragment, int curType,int container) {
		try {
			listener = (onCoacherSortItemSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onCoacherSortItemSelectListener");
		}

		from = fragment;
		FragmentManager fm = fragment.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Bundle data = new Bundle();
		data.putInt(BUNDLE_CURR_TYPE, curType);

		CoacherSortItemSelectFragment to_fragment = new CoacherSortItemSelectFragment();
		to_fragment.setArguments(data);
		ft.replace(container,to_fragment,TAG);
		ft.addToBackStack(TAG);
		ft.commit();
	}

	@Override
	void getDataFrom() {


		curType = getArguments().getInt(BUNDLE_CURR_TYPE);
	}

	@Override
	ArrayList<Integer> getData() {
		return MainApp.getInstance().getGlobalData().getCoachSortKeyList(true);
	}

	@Override
	String getDataItem(Object positon) {
		return MainApp.getInstance().getGlobalData().getCoachSortItemName(Integer.valueOf(labels.get(Integer.valueOf(positon.toString())).toString()));
	}

	@Override
	void onSelect(Object selectValu) {

		listener.onCoacherSortItemSelect(selectValu);
	}

	@Override
	void finish() {

		from.getSupportFragmentManager().popBackStack();
	}


	public interface onCoacherSortItemSelectListener {
		void onCoacherSortItemSelect(Object newCoacherSortItem);
	}
}
