package com.hylg.igolf.ui.view;

import com.hylg.igolf.R;
import com.hylg.igolf.utils.Utils;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;

public class ListFooter {
	private LinearLayout mLast;
	private LinearLayout mMore;
	private LinearLayout mLoading;
	private View mFooter;
	private int status;
	public final static int STATUS_NONE = 0;
	public final static int STATUS_LAST = 1;
	public final static int STATUS_LOADING = 2;
	public final static int STATUS_MORE = 3;
	
	public ListFooter(Activity activity) {
		mFooter = View.inflate(activity, R.layout.gllib_list_footer, null);
		mLast = (LinearLayout) mFooter.findViewById(R.id.footer_last);
		mMore = (LinearLayout) mFooter.findViewById(R.id.footer_more);
		mLoading = (LinearLayout) mFooter.findViewById(R.id.footer_loading);
		status = STATUS_NONE;
	}
	
	public View getFooterView() {
		return mFooter;
	}

	public void displayNone() {
		Utils.setGone(mLoading, mLast, mMore);
		status = STATUS_NONE;
	}
	
	public void displayLoading() {
		Utils.setVisibleGone(mLoading, mLast, mMore);
		status = STATUS_LOADING;
	}
	
	public void displayLast() {
//		Utils.setVisibleGone(mLast, mLoading, mMore);
		Utils.setGone(mLoading, mLast, mMore);
		status = STATUS_LAST;
	}
	
	public void displayMore() {
		Utils.setVisibleGone(mMore, mLoading, mLast);
		status = STATUS_MORE;
	}
	
	public void refreshFooterView(int listSize, int pageSize) {
		Utils.logh("", "refreshFooterView listSize: " + listSize + " pageSize: " + pageSize);
		if(listSize < pageSize) {
			displayLast();
		} else {
			displayMore();
		}
	}
	
	public int getStatus() {
		return status;
	}
	
	public interface onMoreClickListener {
		public abstract void onMoreClick();
	}

}
