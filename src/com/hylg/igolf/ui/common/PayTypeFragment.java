package com.hylg.igolf.ui.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hylg.igolf.MainApp;

import java.util.ArrayList;

public class PayTypeFragment extends BaseSelectFragment {
	private static final String TAG = "PayTypeSelectFragment";
	private static onPayTypeSelectListener listener = null;
	private final static String BUNDLE_CURR_TYPE = "current_type";

	private static Fragment from;

	public static void startPayTypeSelect(Fragment fragment, int curType,int container) {
		try {
			listener = (onPayTypeSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onPayTypeSelectListener");
		}

		from = fragment;
		FragmentManager fm = fragment.getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Bundle data = new Bundle();
		data.putInt(BUNDLE_CURR_TYPE, curType);

		PayTypeFragment to_fragment = new PayTypeFragment();
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
		return MainApp.getInstance().getGlobalData().getPayTypeKeyList(true);
	}

	@Override
	String getDataItem(Object positon) {
		return MainApp.getInstance().getGlobalData().getPayTypeName(Integer.valueOf(labels.get(Integer.valueOf(positon.toString())).toString()));
	}

	@Override
	void onSelect(Object selectValu) {

		listener.onPayTypeSelect(selectValu);
	}

	@Override
	void finish() {

		from.getChildFragmentManager().popBackStack();
	}


	public interface onPayTypeSelectListener {
		void onPayTypeSelect(Object newLabel);
	}
}
