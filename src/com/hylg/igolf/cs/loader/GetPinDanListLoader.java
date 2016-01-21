package com.hylg.igolf.cs.loader;

import android.content.Context;
import android.os.AsyncTask;

import com.hylg.igolf.cs.data.OpenInvitationInfo;
import com.hylg.igolf.cs.data.PinDanDetailInfo;
import com.hylg.igolf.cs.request.GetOpenInviteList;
import com.hylg.igolf.cs.request.GetPinDanList;
import com.hylg.igolf.ui.reqparam.GetOpenInviteReqParam;
import com.hylg.igolf.ui.reqparam.GetPinDanReqParam;

import java.util.ArrayList;

public class GetPinDanListLoader extends BaseAsyncLoader {
	private GetPinDanList request;
	private GetPinDanListCallback callBack;

	public GetPinDanListLoader(Context context, GetPinDanReqParam data, GetPinDanListCallback callBack) {
		super();
		request = new GetPinDanList(context, data);
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
				callBack.callBack(result, request.getRetNum(), request.getFailMsg(), request.getPinDanList());
				mRunning = false;
			}
		};
		mTask.execute(null, null, null);
	}

	public interface GetPinDanListCallback {
		void callBack(int retId, int retNum, String msg, ArrayList<PinDanDetailInfo> inviteList);
	}
}
