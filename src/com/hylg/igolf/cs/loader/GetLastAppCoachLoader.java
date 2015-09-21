package com.hylg.igolf.cs.loader;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.cs.request.GetLastAppCoach;

public class GetLastAppCoachLoader extends BaseAsyncLoader {
	
	private GetLastAppCoach request;
	
	private GetLastAppCoachCallback callBack;
	
	public GetLastAppCoachLoader(Context context, long id,double lat , double lng, GetLastAppCoachCallback callBack) {
		super();
		request = new GetLastAppCoach(context, id,lat,lng);
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
					
				callBack.callBack(result, request.getFailMsg(),request.getLastAppCoach());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetLastAppCoachCallback {
		public abstract void callBack(int retId, String msg,CoachItem item);
	}
}
