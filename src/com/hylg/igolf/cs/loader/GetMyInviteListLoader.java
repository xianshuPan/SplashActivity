package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.MyInviteInfo;
import com.hylg.igolf.cs.request.GetMyInviteList;

public class GetMyInviteListLoader extends BaseAsyncLoader {
	private GetMyInviteList request;
	private GetMyInviteListCallback callBack;
	
	public GetMyInviteListLoader(Context context, String sn, int pageNum, int pageSize, GetMyInviteListCallback callBack) {
		super();
		request = new GetMyInviteList(context, sn, pageNum, pageSize);
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
				callBack.callBack(result, request.getFailMsg(), request.getMyInviteList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetMyInviteListCallback {
		public abstract void callBack(int retId, String msg, ArrayList<MyInviteInfo> inviteList);
	}
}
