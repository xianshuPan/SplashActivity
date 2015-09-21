package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.request.GetFriendHotList;

public class GetFriendHotListLoader extends BaseAsyncLoader {
	private GetFriendHotList request;
	private GetFriendHotListCallback callBack;
	
	public GetFriendHotListLoader(Context context, String sn, int pageNum, int pageSize, String provence,String model,
			GetFriendHotListCallback callBack) {
		super();
		request = new GetFriendHotList(context, sn, pageNum, pageSize,provence,model);
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
				callBack.callBack(result, request.getFailMsg(), request.getFriendHotList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	/*回调接口*/
	public interface GetFriendHotListCallback {
		public abstract void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList);
	}
}
