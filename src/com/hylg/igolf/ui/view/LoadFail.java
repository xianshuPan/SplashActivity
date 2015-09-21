package com.hylg.igolf.ui.view;

import com.hylg.igolf.R;
import com.hylg.igolf.utils.Utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoadFail implements View.OnClickListener {
	private onRetryClickListener mListener = null;
	private RelativeLayout failLayout;
	private TextView hintMsg;
	private Button retryBtn;
	
	public LoadFail(Context context) {
		
		failLayout = (RelativeLayout) View.inflate(context, R.layout.common_load_fail, null);
		failLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		hintMsg = (TextView) failLayout.findViewById(R.id.common_hint_message);
		retryBtn = (Button) failLayout.findViewById(R.id.common_button_retry);
		retryBtn.setOnClickListener(this);
		displayNone();
	}

	public LoadFail(Context context, RelativeLayout failLayout) {
		this.failLayout = failLayout;
		hintMsg = (TextView) failLayout.findViewById(R.id.common_hint_message);
		retryBtn = (Button) failLayout.findViewById(R.id.common_button_retry);
		retryBtn.setOnClickListener(this);
		displayNone();
	}
	
	public View getLoadFailView() {
		return failLayout;
	}
	
	public void setOnRetryClickListener(onRetryClickListener listener) {
		mListener = listener;
	}
	
	public void displayNoData(String msg) {
		Utils.setVisible(failLayout);
		Utils.setVisibleGone(hintMsg, retryBtn);
		hintMsg.setText(msg);
	}
	
	public void displayNoDataRetry(String msg) {
		Utils.setVisible(failLayout);
		Utils.setVisible(hintMsg, retryBtn);
		hintMsg.setText(msg);
	}
	
	public void displayFail(String msg) {
		Utils.setVisible(failLayout);
		Utils.setVisible(hintMsg, retryBtn);
		hintMsg.setText(msg);
	}
	
	public void displayNone() {
		Utils.setGone(failLayout);
	}
	
	public interface onRetryClickListener {
		public abstract void onRetryClick();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.common_button_retry) {
			if(null != mListener) {
				mListener.onRetryClick();
			}
		}
	}
}
