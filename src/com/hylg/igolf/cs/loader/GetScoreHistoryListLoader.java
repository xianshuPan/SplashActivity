package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.ScoreHistoryInfo;
import com.hylg.igolf.cs.request.GetScoreHistoryList;
import com.hylg.igolf.ui.reqparam.GetScoreHistoryReqParam;

public class GetScoreHistoryListLoader extends BaseAsyncLoader {
	private GetScoreHistoryList request;
	private GetScoreHistoryListCallback callBack;
	
	public GetScoreHistoryListLoader(Context context, GetScoreHistoryReqParam reqData, GetScoreHistoryListCallback callBack) {
		super();
		request = new GetScoreHistoryList(context, reqData.sn, reqData.pageNum, reqData.pageSize);
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
				callBack.callBack(result, request.getFailMsg(), request.getScoreHistoryList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetScoreHistoryListCallback {
		public abstract void callBack(int retId, String msg, ArrayList<ScoreHistoryInfo> inviteList);
	}
}
