package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import android.content.Context;


public class MarkInviteApp extends BaseRequest {
	private String param;

	public MarkInviteApp(Context context, String appSn, String sn, double latitude, double longitude) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_APP_SN); s.append(KV_CONN); s.append(appSn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_LATI); s.append(KV_CONN); s.append(latitude);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_LONG); s.append(KV_CONN); s.append(longitude);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("markInviteApp02", param);
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
