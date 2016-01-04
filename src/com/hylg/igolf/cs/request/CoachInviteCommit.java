package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.ui.reqparam.CoachInviteReqParam;
import com.hylg.igolf.ui.reqparam.StartOpenReqParam;

public class CoachInviteCommit extends BaseRequest {
	private String param;

	public CoachInviteCommit(Context context, CoachInviteReqParam reqParam) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append("studentid"); s.append(KV_CONN); s.append(reqParam.studentid);
		s.append(PARAM_CONN);
		
		s.append("coachid"); s.append(KV_CONN); s.append(reqParam.coachid);
		s.append(PARAM_CONN);
		
		s.append("courseid"); s.append(KV_CONN); s.append(reqParam.courseid);
		s.append(PARAM_CONN);
		
		s.append("msg"); s.append(KV_CONN); s.append(reqParam.msg);
		s.append(PARAM_CONN);
		
		s.append("times"); s.append(KV_CONN); s.append(reqParam.times);
		s.append(PARAM_CONN);
		
		s.append("coachDate"); s.append(KV_CONN); s.append(reqParam.coachDate );
		s.append(PARAM_CONN);
		
		s.append("coachTime"); s.append(KV_CONN); s.append(reqParam.coachTime );
		
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("applyCoach", param);
	}

	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("约教练", "----->>>"+jo);
			
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
