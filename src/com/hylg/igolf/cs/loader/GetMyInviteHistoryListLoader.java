package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.InviteHistoryInfo;
import com.hylg.igolf.cs.request.GetMyInviteHistoryList;
import com.hylg.igolf.ui.reqparam.GetInviteHistoryReqParam;

public class GetMyInviteHistoryListLoader extends BaseAsyncLoader {
	private GetMyInviteHistoryList request;
	private GetMyInviteHistoryListCallback callBack;
	
	public GetMyInviteHistoryListLoader(Context context, GetInviteHistoryReqParam reqData, GetMyInviteHistoryListCallback callBack) {
		super();
		request = new GetMyInviteHistoryList(context, reqData.sn, reqData.pageNum, reqData.pageSize);
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
				callBack.callBack(result, request.getFailMsg(), request.getMyInviteHistoryList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetMyInviteHistoryListCallback {
		public abstract void callBack(int retId, String msg, ArrayList<InviteHistoryInfo> inviteList);
	}
}
