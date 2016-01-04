package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.ui.reqparam.CoachInviteReqParam;
import com.hylg.igolf.ui.reqparam.StartOpenReqParam;

public class CoachEndTeaching extends BaseRequest {
	private String param;

	public CoachEndTeaching(Context context, long id,long period,long pauseedTime) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append("appid"); s.append(KV_CONN); s.append(id);s.append(PARAM_CONN);
		s.append("period"); s.append(KV_CONN); s.append(period);s.append(PARAM_CONN);
		s.append("pauseTime"); s.append(KV_CONN); s.append(pauseedTime);
		
		param = s.toString();

	}
	
	@Override
	public String getRequestUrl() {
		
		return getReqParam("recordCoachPeriod", param);
		
	}

	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("教练结束教学", "----->>>"+jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		
		MainApp.getInstance().getGlobalData().setHasStartNewInvite(true);
		return REQ_RET_OK;
	}

}
