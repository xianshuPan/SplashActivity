package com.hylg.igolf.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.utils.Utils;

import java.util.ArrayList;

public class LabelSelectFragment extends BaseSelectFragment {
	private static final String TAG = "LabelSelectFragment";
	private static onLabelSelectListener listener = null;
	private final static String BUNDLE_CURR_TYPE = "current_type";

	private static Fragment from;

	public static void startLabelSelect(Fragment fragment, int curType,int container) {
		try {
			listener = (onLabelSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onLabelSelectListener");
		}

		from = fragment;
		FragmentManager fm = fragment.getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Bundle data = new Bundle();
		data.putInt(BUNDLE_CURR_TYPE, curType);

		LabelSelectFragment to_fragment = new LabelSelectFragment();
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
		return MainApp.getInstance().getGlobalData().getLabelKeyList();
	}

	@Override
	String getDataItem(Object positon) {
		return MainApp.getInstance().getGlobalData().getLabelName(Integer.valueOf(labels.get(Integer.valueOf(positon.toString())).toString()));
	}

	@Override
	void onSelect(Object selectValu) {

		listener.onLabelSelect(selectValu);
	}

	@Override
	void finish() {

		from.getChildFragmentManager().popBackStack();
	}


	public interface onLabelSelectListener {
		void onLabelSelect(Object newLabel);
	}
}
