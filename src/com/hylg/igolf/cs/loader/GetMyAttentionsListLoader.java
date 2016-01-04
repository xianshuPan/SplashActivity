package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.MyFolloweInfo;
import com.hylg.igolf.cs.request.GetMyAttentionsList;
import com.hylg.igolf.cs.request.GetMyFollowerList;

public class GetMyAttentionsListLoader extends BaseAsyncLoader {
	private GetMyAttentionsList request;
	private GetMyAttentionsListCallback callBack;
	
	public GetMyAttentionsListLoader(Context context, String sn,  String mem_sn,int pageNum, int pageSize,
			GetMyAttentionsListCallback callBack) {
		super();
		request = new GetMyAttentionsList(context, sn,mem_sn, pageNum, pageSize);
		this.callBack = callBack;
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
				callBack.callBack(result, request.getFailMsg(), request.getMyFollowerList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	/*回调接口*/
	public interface GetMyAttentionsListCallback {
		void callBack(int retId, String msg, ArrayList<MyFolloweInfo> hotList);
	}
}
