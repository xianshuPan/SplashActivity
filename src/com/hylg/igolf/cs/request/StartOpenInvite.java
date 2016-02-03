package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.ui.reqparam.StartOpenReqParam;

import org.json.JSONObject;

import java.io.InputStream;

public class StartOpenInvite extends BaseRequest {
	private String param;

	public long app_id = -1;

	public int local_fans_amount = 0;

	public StartOpenInvite(Context context, StartOpenReqParam reqParam) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(reqParam.sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_DAY); s.append(KV_CONN); s.append(reqParam.day);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_TIME); s.append(KV_CONN); s.append(reqParam.time);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_COURSE_ID); s.append(KV_CONN); s.append(reqParam.courseId);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_STAKE); s.append(KV_CONN); s.append(reqParam.stake);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAY_TYPE); s.append(KV_CONN); s.append(reqParam.payType);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_MSG); s.append(KV_CONN); s.append(reqParam.msg);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("startOpenInvite", param);
	}

	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);

		DebugTools.getDebug().debug_v("startOpenInvite","----->>>"+str);
		try {
			JSONObject jo = new JSONObject(str);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			app_id = jo.optLong("appId");
			local_fans_amount = jo.optInt("infoFans");

		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		MainApp.getInstance().getGlobalData().setHasStartNewInvite(true);
		return REQ_RET_OK;
	}

}
