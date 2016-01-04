package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.ui.reqparam.CoachInviteReqParam;

public class CoachComplainOrRefuseCommit extends BaseRequest {
	private String param;
	
	private int Type;

	public CoachComplainOrRefuseCommit(Context context, int type,long teacher_id,long student_id,long app_id,String reasonIds,String otherReason) {
		super(context);
		StringBuilder s = new StringBuilder();
		
		s.append("studentid"); s.append(KV_CONN); s.append(student_id);
		s.append(PARAM_CONN);
		
		s.append("coachid"); s.append(KV_CONN); s.append(teacher_id);
		s.append(PARAM_CONN);
		
		s.append("appid"); s.append(KV_CONN); s.append(app_id);
		s.append(PARAM_CONN);
		
		s.append("reasonids"); s.append(KV_CONN); s.append(reasonIds);
		s.append(PARAM_CONN);
		
		s.append("content"); s.append(KV_CONN); s.append(otherReason);
		
		Type = type;
		
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		
		if (Type == 0) {
			
			return getReqParam("complainCoach", param);
		
		} else if (Type == 1) {
			
			return getReqParam("coachRefuse", param);
		} else {
			
			return "";
		}
		
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("投诉或者是拒绝", "----->>>"+jo);
			
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
