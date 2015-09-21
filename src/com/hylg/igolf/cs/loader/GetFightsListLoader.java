package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.MemMyFightInfo;
import com.hylg.igolf.cs.request.GetFightsList;
import com.hylg.igolf.ui.reqparam.GetFightsReqParam;

public class GetFightsListLoader extends BaseAsyncLoader {
	private GetFightsList request;
	private GetFightsListCallback callBack;
	
	public GetFightsListLoader(Context context, GetFightsReqParam reqData, GetFightsListCallback callBack) {
		super();
		request = new GetFightsList(context, reqData.sn,reqData.memSn, reqData.pageNum, reqData.pageSize);
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
				callBack.callBack(result, request.getFailMsg(), request.getFightRecordList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetFightsListCallback {
		public abstract void callBack(int retId, String msg, ArrayList<MemMyFightInfo> fightRecordList);
	}
}
