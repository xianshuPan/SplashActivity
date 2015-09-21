package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.GolferInfo;
import com.hylg.igolf.cs.request.GetGolfersList;
import com.hylg.igolf.ui.reqparam.GetGolfersReqParam;

public class GetGolfersListLoader extends BaseAsyncLoader {
	private GetGolfersList request;
	private GetGolfersListCallback callBack;
	
	public GetGolfersListLoader(Context context, GetGolfersReqParam data, GetGolfersListCallback callBack) {
		super();
		request = new GetGolfersList(context, data);
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
				callBack.callBack(result, request.getFailMsg(), request.getGolfersList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetGolfersListCallback {
		public abstract void callBack(int retId, String msg, ArrayList<GolferInfo> golfersList);
	}
}
