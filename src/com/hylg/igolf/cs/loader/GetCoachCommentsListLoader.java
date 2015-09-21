package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.CoachComemntsItem;
import com.hylg.igolf.cs.data.MyInviteInfo;
import com.hylg.igolf.cs.request.GetCoachCommentsList;
import com.hylg.igolf.cs.request.GetMyInviteList;

public class GetCoachCommentsListLoader extends BaseAsyncLoader {
	
	private GetCoachCommentsList request;
	private GetCoachCommentsCallback callBack;
	
	public GetCoachCommentsListLoader(Context context, long id, int pageNum, int pageSize, GetCoachCommentsCallback callBack) {
		super();
		request = new GetCoachCommentsList(context, id, pageNum, pageSize);
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
				callBack.callBack(result, request.getFailMsg(), request.getCoachCommentsList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetCoachCommentsCallback {
		public abstract void callBack(int retId, String msg, ArrayList<CoachComemntsItem> inviteList);
	}
}
