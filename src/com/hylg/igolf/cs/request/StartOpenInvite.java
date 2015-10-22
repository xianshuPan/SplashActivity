package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.ui.reqparam.StartOpenReqParam;

public class StartOpenInvite extends BaseRequest {
	private String param;

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
		try {
			JSONObject jo = new JSONObject(str);
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
