package com.hylg.igolf.cs.loader;


import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.request.GetTipsDetial;

public class GetTipsDetialLoader extends BaseAsyncLoader {
	
	private GetTipsDetial request;
	private GetTipsDetialCallback callBack;
	
	private Context mContext;
	
	public GetTipsDetialLoader(Context context, String sn, String tipid,
			GetTipsDetialCallback callBack) {
		super();
		
		mContext = context;
		request = new GetTipsDetial(context, sn, tipid);
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
				callBack.callBack(result, request.getFailMsg(), request.getTipsDetial());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	/*回调接口*/
	public interface GetTipsDetialCallback {
		public abstract void callBack(int retId, String msg, FriendHotItem item);
	}
}
