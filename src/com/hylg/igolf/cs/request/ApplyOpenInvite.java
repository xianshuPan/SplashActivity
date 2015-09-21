package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;


public class ApplyOpenInvite extends BaseRequest {
	private String param;
	private int applyNum;

	public ApplyOpenInvite(Context context, String appSn, String sn) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_APP_SN); s.append(KV_CONN); s.append(appSn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("applyOpenInvite02", param);
	}

	public int getApplyNum() {
		return applyNum;
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			failMsg = jo.optString(RET_MSG);
			if(REQ_RET_OK != rn) {
				return rn;
			}
			applyNum = jo.getInt(RET_APPLY_NUM);
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
