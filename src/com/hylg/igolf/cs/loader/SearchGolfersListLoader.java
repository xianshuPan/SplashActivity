package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.GolferInfo;
import com.hylg.igolf.cs.request.SearchGolfersList;

public class SearchGolfersListLoader extends BaseAsyncLoader {
	private SearchGolfersList request;
	private SearchGolfersListCallback callBack;
	
	public SearchGolfersListLoader(Context context, String sn, int pageNum, int pageSize,
									String key,SearchGolfersListCallback callBack) {
		super();
		request = new SearchGolfersList(context, sn, pageNum, pageSize, key);
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
				callBack.callBack(result, request.getRetNum(),
							request.getFailMsg(), request.getGolfersList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface SearchGolfersListCallback {
		public abstract void callBack(int retId, int retNum, String msg,
										ArrayList<GolferInfo> golfersList);
	}
}
