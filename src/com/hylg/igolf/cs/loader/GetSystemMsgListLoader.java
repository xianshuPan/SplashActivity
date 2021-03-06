package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.SysMsgInfo;
import com.hylg.igolf.cs.request.GetSystemMsgList;
import com.hylg.igolf.ui.reqparam.GetSysMsgReqParam;

public class GetSystemMsgListLoader extends BaseAsyncLoader {
	private GetSystemMsgList request;
	private GetSystemMsgListCallback callBack;
	
	public GetSystemMsgListLoader(Context context, GetSysMsgReqParam reqData, GetSystemMsgListCallback callBack) {
		super();
		request = new GetSystemMsgList(context, reqData.sn, reqData.pageNum, reqData.pageSize);
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
				callBack.callBack(result, request.getFailMsg(), request.getSystemMsgList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetSystemMsgListCallback {
		public abstract void callBack(int retId, String msg, ArrayList<SysMsgInfo> sysMsgList);
	}
}
