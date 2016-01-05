package com.hylg.igolf.ui.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.hylg.igolf.MainApp;

import java.util.ArrayList;

public class SexSelectFragment extends BaseSelectFragment {
	private static final String TAG = "SexSelectFragment";
	private static onSexSelectListener listener = null;
	private final static String BUNDLE_CURR_TYPE = "current_type";

	private static FragmentActivity from;

	private static Fragment from_frag;

	public static void startSexSelect(Fragment fragment, int curType,int container) {
		try {
			listener = (onSexSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onLabelSelectListener");
		}

		from_frag = fragment;
		from = null;
		FragmentManager fm = fragment.getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Bundle data = new Bundle();
		data.putInt(BUNDLE_CURR_TYPE, curType);

		SexSelectFragment to_fragment = new SexSelectFragment();
		to_fragment.setArguments(data);
		ft.replace(container,to_fragment,TAG);
		ft.addToBackStack(TAG);
		ft.commit();
	}

	public static void startSexSelect(FragmentActivity fragment, int curType,int container) {
		try {
			listener = (onSexSelectListener) fragment;
		} catch (ClassCastException e) {
			throw new ClassCastException(fragment.toString() +
					" must implements onLabelSelectListener");
		}

		from = fragment;
		from_frag = null;
		FragmentManager fm = fragment.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		Bundle data = new Bundle();
		data.putInt(BUNDLE_CURR_TYPE, curType);

		SexSelectFragment to_fragment = new SexSelectFragment();
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
		return MainApp.getInstance().getGlobalData().getSexKeyList(true);
	}

	@Override
	String getDataItem(Object positon) {
		return MainApp.getInstance().getGlobalData().getSexName(Integer.valueOf(labels.get(Integer.valueOf(positon.toString())).toString()));
	}

	@Override
	void onSelect(Object selectValu) {

		listener.onSexSelect(selectValu);
	}

	@Override
	void finish() {

		if (from != null) {

			from.getSupportFragmentManager().popBackStack();
		}

		if (from_frag != null ) {

			from_frag.getChildFragmentManager().popBackStack();
		}

	}


	public interface onSexSelectListener {
		void onSexSelect(Object newSex);
	}
}
