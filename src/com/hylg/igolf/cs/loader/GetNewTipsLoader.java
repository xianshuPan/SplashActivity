package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.request.GetNewTipsList;

public class GetNewTipsLoader extends BaseAsyncLoader {
	
	private GetNewTipsList request;
	
	private GetNewTipsListCallback callBack;
	
	public GetNewTipsLoader(Context context, String tipsIds,String sn, GetNewTipsListCallback callBack) {
		super();
		request = new GetNewTipsList(context, tipsIds,sn,1,MainApp.getInstance().getGlobalData().tipsAmount);
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
					
				callBack.callBack(result, request.getFailMsg(),request.hotList);
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetNewTipsListCallback {
		void callBack(int retId, String msg,ArrayList<FriendHotItem> list);
	}
}
