package com.hylg.igolf.cs.loader;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.request.GetNewMsgCount;

public class GetNewMsgCountLoader extends BaseAsyncLoader {
	private GetNewMsgCount request;
	private GetNewMsgCountCallback callBack;
	
	public GetNewMsgCountLoader(Context context, String sn, GetNewMsgCountCallback callBack) {
		super();
		request = new GetNewMsgCount(context, sn);
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
				callBack.callBack(result, request.getFailMsg());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetNewMsgCountCallback {
		public abstract void callBack(int retId, String msg);
	}
}
