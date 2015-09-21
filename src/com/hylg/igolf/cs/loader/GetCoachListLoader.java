package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.cs.data.GolferInfo;
import com.hylg.igolf.cs.request.GetCoachList;
import com.hylg.igolf.cs.request.GetGolfersList;
import com.hylg.igolf.ui.reqparam.CoachListReqParam;
import com.hylg.igolf.ui.reqparam.GetGolfersReqParam;

public class GetCoachListLoader extends BaseAsyncLoader {
	
	private GetCoachList request;
	private GetCoachListCallback callBack;
	
	public GetCoachListLoader(Context context, CoachListReqParam data, GetCoachListCallback callBack) {
		super();
		request = new GetCoachList(context, data);
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
				callBack.callBack(result, request.getFailMsg(), request.getCoachList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetCoachListCallback {
		public abstract void callBack(int retId, String msg, ArrayList<CoachItem> golfersList);
	}
}
