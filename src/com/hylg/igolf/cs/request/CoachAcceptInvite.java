package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.ui.reqparam.CoachInviteReqParam;
import com.hylg.igolf.ui.reqparam.StartOpenReqParam;

public class CoachAcceptInvite extends BaseRequest {
	private String param;
	
	public int type;

	public CoachAcceptInvite(Context context, long id,int type) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append("appid"); s.append(KV_CONN); s.append(id);
		
		param = s.toString();
		this.type = type;
	}
	
	@Override
	public String getRequestUrl() {
		
		if (type == 0) {
			
			return getReqParam("coachRecieve", param);
			
		} else if (type == 1) {
			
			return getReqParam("recordStartCoachTime", param);
		} else {
			
			return "";
		}
		
	}

	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("教练接受邀请", "----->>>"+jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
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
