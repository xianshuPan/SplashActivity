package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import android.content.Context;


public class ResetVerifyPhone extends BaseRequest {
	private String param;
	private String sn;

	public ResetVerifyPhone(Context context, String phone, String verifyCode) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_PHONE); s.append(KV_CONN); s.append(phone);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_VERIFY_CODE); s.append(KV_CONN); s.append(verifyCode);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("resetVerifyPhone", param);
	}

	public String getSn() {
		return sn;
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
			sn = jo.getString(RET_SN);
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
