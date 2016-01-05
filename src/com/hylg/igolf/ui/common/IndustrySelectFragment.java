package com.hylg.igolf.ui.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hylg.igolf.MainApp;

import java.util.ArrayList;

public class IndustrySelectFragment extends BaseSelectFragment {
	private static final String TAG = "IndustrySelectFragment";
	private static onIndustrySelectListener listener = null;
	private final static String BUNDLE_CURR_TYPE = "current_type";

	private static Fragment from;

	public static void startIndustrySelect(Fragment fragment, String curType,int container) {
		try {
			listener = (onIndustrySelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onLabelSelectListener");
		}

		from = fragment;
		FragmentManager fm = fragment.getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Bundle data = new Bundle();
		data.putString(BUNDLE_CURR_TYPE, curType);

		IndustrySelectFragment to_fragment = new IndustrySelectFragment();
		to_fragment.setArguments(data);
		ft.replace(container,to_fragment,TAG);
		ft.addToBackStack(TAG);
		ft.commit();
	}

	@Override
	void getDataFrom() {


		curType = getArguments().getString(BUNDLE_CURR_TYPE);
	}

	@Override
	ArrayList getData() {
		return MainApp.getInstance().getGlobalData().getIndustryKeyList(true);
	}

	@Override
	String getDataItem(Object position) {
		return MainApp.getInstance().getGlobalData().getIndustryName(labels.get(Integer.valueOf(position.toString())).toString());
	}

	@Override
	void onSelect(Object selectValu) {

		listener.onIndustrySelect(selectValu);
	}

	@Override
	void finish() {

		from.getChildFragmentManager().popBackStack();
	}


	public interface onIndustrySelectListener {
		void onIndustrySelect(Object newLabel);
	}
}
