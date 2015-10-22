package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import android.content.Context;


public class RefuseStsInvite extends BaseRequest {
	private String param;

	public RefuseStsInvite(Context context, String appSn) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_APP_SN); s.append(KV_CONN); s.append(appSn);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("refuseStsInvite", param);
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
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
