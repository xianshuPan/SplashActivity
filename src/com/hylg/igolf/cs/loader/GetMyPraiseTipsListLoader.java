package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.request.GetCustomerFriendList;
import com.hylg.igolf.cs.request.GetFriendHotList;
import com.hylg.igolf.cs.request.GetMyPraiseTipsList;

public class GetMyPraiseTipsListLoader extends BaseAsyncLoader {
	
	private GetMyPraiseTipsList request;
	private GetMyPraisedTipsListCallback callBack;
	
	private Context mContext;
	
	public GetMyPraiseTipsListLoader(Context context, String sn, int pageNum, int pageSize,
			GetMyPraisedTipsListCallback callBack) {
		super();
		
		mContext = context;
		request = new GetMyPraiseTipsList(context, sn, pageNum, pageSize);
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
	public interface GetMyPraisedTipsListCallback {
		public abstract void callBack(int retId, String msg, ArrayList<FriendHotItem> hotList);
	}
}
