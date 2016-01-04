package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.request.GetCustomerFriendList;
import com.hylg.igolf.cs.request.GetFriendHotList;

public class GetCustomerFriendHotListLoader extends BaseAsyncLoader {
	
	private GetCustomerFriendList request;
	private GetCustomerFriendHotListCallback callBack;
	
	private Context mContext;
	
	public GetCustomerFriendHotListLoader(Context context, String sn, String tosn ,int pageNum, int pageSize,
			GetCustomerFriendHotListCallback callBack) {
		super();
		
		mContext = context;
		request = new GetCustomerFriendList(context, sn, tosn,pageNum, pageSize);
		this.callBack = callBack;
	}



	@Override
	public void requestData() {
		
		//mContext.sendBroadcast(new Intent("com.hylg.igolf.stop"));
		
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
	public interface GetCustomerFriendHotListCallback {
		void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList);
	}
}
