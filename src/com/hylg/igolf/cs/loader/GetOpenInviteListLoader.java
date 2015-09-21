package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.OpenInvitationInfo;
import com.hylg.igolf.cs.request.GetOpenInviteList;
import com.hylg.igolf.ui.reqparam.GetOpenInviteReqParam;

public class GetOpenInviteListLoader extends BaseAsyncLoader {
	private GetOpenInviteList request;
	private GetOpenInviteListCallback callBack;
	
	public GetOpenInviteListLoader(Context context, GetOpenInviteReqParam data, GetOpenInviteListCallback callBack) {
		super();
		request = new GetOpenInviteList(context, data);
		this.callBack = callBack;
	}
	
	@Override
	public void requestData() {
		mTask = new AsyncTask<Object, Object, Integer>() {
			@Override
			protected Integer doInBackground(Object... params) {
				mRunning = true;
				return request.connectUrl();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				callBack.callBack(result, request.getRetNum(), request.getFailMsg(), request.getOpenInviteList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetOpenInviteListCallback {
		public abstract void callBack(int retId, int retNum, String msg, ArrayList<OpenInvitationInfo> inviteList);
	}
}
