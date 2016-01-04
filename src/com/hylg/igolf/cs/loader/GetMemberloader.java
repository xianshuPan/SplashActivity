package com.hylg.igolf.cs.loader;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.cs.data.Member;
import com.hylg.igolf.cs.request.GetMember;

public class GetMemberloader extends BaseAsyncLoader {
	private GetMember request;
	private GetMemberCallback callBack;
	
	public GetMemberloader(Context context, String sn,String memSn, GetMemberCallback callBack) {
		super();
		request = new GetMember(context, sn,memSn);
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
				callBack.callBack(result, request.getFailMsg(), request.getMember(),request.coach_item);
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetMemberCallback {
		void callBack(int retId, String msg, Member member,CoachItem coach_item);
	}
}
