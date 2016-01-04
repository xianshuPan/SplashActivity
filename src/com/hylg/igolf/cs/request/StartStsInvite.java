package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.ui.hall.data.PlanSubmitInfo;
import com.hylg.igolf.ui.reqparam.StartStsReqParam;
import com.hylg.igolf.utils.Utils;

public class StartStsInvite extends BaseRequest {
	private String param;

	public StartStsInvite(Context context, StartStsReqParam reqParam) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(reqParam.sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_INVITEE_SN); s.append(KV_CONN); s.append(reqParam.inviteeSn);
		s.append(PARAM_CONN);
		// 拼接方案json串
		ArrayList<PlanSubmitInfo> plans = reqParam.plans;
		JSONArray ja = new JSONArray();
		for(PlanSubmitInfo plan : plans) {
			JSONObject jo = new JSONObject();
			try {
				jo.put("index", plan.index);
				jo.put("teeCourse", plan.teeCourse);
				jo.put("teeTime", plan.teeTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ja.put(jo);
		}
		s.append("plans"); s.append(KV_CONN); s.append(ja.toString());
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_STAKE); s.append(KV_CONN); s.append(reqParam.stake);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAY_TYPE); s.append(KV_CONN); s.append(reqParam.payType);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_MSG); s.append(KV_CONN); s.append(reqParam.msg);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_APP_NAME); s.append(KV_CONN); s.append(reqParam.appName);
		param = s.toString();
		Utils.logh("", param);
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("startStsInvite", param);
	}

	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
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
