package com.hylg.igolf.ui.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hylg.igolf.MainApp;

import java.util.ArrayList;

public class CoacherTypeSelectFragment extends BaseSelectFragment {
	private static final String TAG = "CoacherTypeSelectFragment";
	private static onCoacherTypeSelectListener listener = null;
	private final static String BUNDLE_CURR_TYPE = "current_type";

	private static FragmentActivity from;

	public static void startCoacherTypeSelect(Fragment fragment, int curType,int container) {
		try {
			listener = (onCoacherTypeSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onCoacherTypeSelectListener");
		}

		from = fragment.getActivity();
		FragmentManager fm = fragment.getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Bundle data = new Bundle();
		data.putInt(BUNDLE_CURR_TYPE, curType);

		CoacherTypeSelectFragment to_fragment = new CoacherTypeSelectFragment();
		to_fragment.setArguments(data);
		ft.replace(container,to_fragment,TAG);
		ft.addToBackStack(TAG);
		ft.commit();
	}

	public static void startCoacherTypeSelect(FragmentActivity fragment, int curType,int container) {
		try {
			listener = (onCoacherTypeSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onCoacherTypeSelectListener");
		}

		from = fragment;
		FragmentManager fm = fragment.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Bundle data = new Bundle();
		data.putInt(BUNDLE_CURR_TYPE, curType);

		CoacherTypeSelectFragment to_fragment = new CoacherTypeSelectFragment();
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
		return MainApp.getInstance().getGlobalData().getCoachTypeKeyList(true);
	}

	@Override
	String getDataItem(Object positon) {
		return MainApp.getInstance().getGlobalData().getCoachTypeName(Integer.valueOf(labels.get(Integer.valueOf(positon.toString())).toString()));
	}

	@Override
	void onSelect(Object selectValu) {

		listener.onCoacherTypeSelect(selectValu);
	}

	@Override
	void finish() {

		from.getSupportFragmentManager().popBackStack();
	}


	public interface onCoacherTypeSelectListener {
		void onCoacherTypeSelect(Object newCoacherType);
	}
}
