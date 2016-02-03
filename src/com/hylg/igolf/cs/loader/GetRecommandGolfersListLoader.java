package com.hylg.igolf.cs.loader;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.GolferInfo;
import com.hylg.igolf.cs.request.GetRecommandGolfersList;
import com.hylg.igolf.ui.reqparam.GetGolfersReqParam;

import java.util.ArrayList;

public class GetRecommandGolfersListLoader extends BaseAsyncLoader {
	private GetRecommandGolfersList request;
	private GetRecommandGolfersListCallback callBack;

	public GetRecommandGolfersListLoader(Context context, GetGolfersReqParam data, GetRecommandGolfersListCallback callBack) {
		super();
		request = new GetRecommandGolfersList(context, data);
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

	public interface GetRecommandGolfersListCallback {
		void callBack(int retId, String msg, ArrayList<GolferInfo> golfersList);
	}
}
