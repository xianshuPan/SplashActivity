package com.hylg.igolf.cs.loader;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.RankingInfo;
import com.hylg.igolf.cs.request.GetRankingList;
import com.hylg.igolf.ui.reqparam.GetRankingReqParam;

public class GetRankingListLoader extends BaseAsyncLoader {
	private GetRankingList request;
	private GetRankingListCallback callBack;
	
	public GetRankingListLoader(Context context, GetRankingReqParam data, GetRankingListCallback callBack) {
		super();
		request = new GetRankingList(context, data);
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
				callBack.callBack(result, request.getFailMsg(), request.getMyRank(), request.getRankList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetRankingListCallback {
		public abstract void callBack(int retId, String msg, RankingInfo myRank, ArrayList<RankingInfo> golfersList);
	}
}
