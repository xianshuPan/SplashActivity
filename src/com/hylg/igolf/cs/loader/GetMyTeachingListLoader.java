package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.CoachInviteOrderDetail;
import com.hylg.igolf.cs.data.MyInviteInfo;
import com.hylg.igolf.cs.request.GetMyInviteList;
import com.hylg.igolf.cs.request.GetMyTeachingList;

public class GetMyTeachingListLoader extends BaseAsyncLoader {
	
	private GetMyTeachingList request;
	private GetMyTeachingListCallback callBack;
	
	public GetMyTeachingListLoader(Context context, long id, int pageNum, int pageSize, GetMyTeachingListCallback callBack) {
		super();
		request = new GetMyTeachingList(context, id, pageNum, pageSize);
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
				callBack.callBack(result, request.getFailMsg(), request.getMyTeachingList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetMyTeachingListCallback {
		public abstract void callBack(int retId, String msg, ArrayList<CoachInviteOrderDetail> inviteList);
	}
}
