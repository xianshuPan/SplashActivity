package com.hylg.igolf.cs.loader;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.MyBalanceRecordInfo;
import com.hylg.igolf.cs.data.MyFolloweInfo;
import com.hylg.igolf.cs.request.GetMyBalanceRecordList;

import java.util.ArrayList;

public class GetMyBalanceRecordListLoader extends BaseAsyncLoader {
	private GetMyBalanceRecordList request;
	private GetBalanceRecordListCallback callBack;

	public GetMyBalanceRecordListLoader(Context context,
										long id,
										int pageNum,
										int pageSize,
										GetBalanceRecordListCallback callBack)
	{
		super();
		request = new GetMyBalanceRecordList(context, id, pageNum, pageSize);
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
				callBack.callBack(result, request.getFailMsg(), request.getMyBalanceRecordList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	/*回调接口*/
	public interface GetBalanceRecordListCallback {
		public abstract void callBack(int retId, String msg, ArrayList<MyBalanceRecordInfo> hotList);
	}
}
