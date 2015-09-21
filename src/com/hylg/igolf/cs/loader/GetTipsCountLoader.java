package com.hylg.igolf.cs.loader;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.request.GetTipsCount;

public class GetTipsCountLoader extends BaseAsyncLoader {
	private GetTipsCount request;
	//private GetNewMsgCountCallback callBack;
	
	//public GetTipsCountLoader(Context context, String sn, GetNewMsgCountCallback callBack) {
	public GetTipsCountLoader(Context context, String sn) {
		super();
		request = new GetTipsCount(context, sn);
		//this.callBack = callBack;
	}
	
	@Override
	public void requestData() {
		mTask = new AsyncTask<Object, Object, Integer>() {
			@Override
			protected Integer doInBackground(Object... params) {
				mRunning = true;
				return request.connectUrlGet();
			}
			
			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				//callBack.callBack(result, request.getFailMsg());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

//	public interface GetNewMsgCountCallback {
//		public abstract void callBack(int retId, String msg);
//	}
}
