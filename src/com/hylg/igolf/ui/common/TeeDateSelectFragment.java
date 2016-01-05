package com.hylg.igolf.ui.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hylg.igolf.MainApp;

import java.util.ArrayList;

public class TeeDateSelectFragment extends BaseSelectFragment {
	private static final String TAG = "TeeDateSelectFragment";
	private static onTeeDateSelectListener listener = null;
	private final static String BUNDLE_CURR_TYPE = "current_type";

	private static Fragment from;

	public static void startTeeDateSelect(Fragment fragment, int curType,int container) {
		try {
			listener = (onTeeDateSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onLabelSelectListener");
		}

		from = fragment;
		FragmentManager fm = fragment.getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Bundle data = new Bundle();
		data.putInt(BUNDLE_CURR_TYPE, curType);

		TeeDateSelectFragment to_fragment = new TeeDateSelectFragment();
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
		return MainApp.getInstance().getGlobalData().getTeeDateKeyList(true);
	}

	@Override
	String getDataItem(Object positon) {
		return MainApp.getInstance().getGlobalData().getTeeDateName(Integer.valueOf(labels.get(Integer.valueOf(positon.toString())).toString()));
	}

	@Override
	void onSelect(Object selectValu) {

		listener.onTeeDateSelect(selectValu);
	}

	@Override
	void finish() {

		from.getChildFragmentManager().popBackStack();
	}


	public interface onTeeDateSelectListener {
		void onTeeDateSelect(Object newTeeDate);
	}
}
