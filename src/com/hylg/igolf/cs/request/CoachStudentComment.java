package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.ui.reqparam.CoachInviteReqParam;
import com.hylg.igolf.ui.reqparam.StartOpenReqParam;

public class CoachStudentComment extends BaseRequest {
	private String param;

	public CoachStudentComment(Context context, long id,long student_id,long coach_id,float star ,String comments) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append("appid"); s.append(KV_CONN); s.append(id);s.append(PARAM_CONN);
		s.append("custId"); s.append(KV_CONN); s.append(student_id);s.append(PARAM_CONN);
		s.append("star"); s.append(KV_CONN); s.append(star);s.append(PARAM_CONN);
		s.append("content"); s.append(KV_CONN); s.append(comments);s.append(PARAM_CONN);
		s.append("coachid"); s.append(KV_CONN); s.append(coach_id);
		
		param = s.toString();

	}
	
	@Override
	public String getRequestUrl() {
		
		return getReqParam("commentCoach", param);
		
	}

	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("学员评价教练", "----->>>"+jo);
			
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
