package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import android.content.Context;


public class RegisterVerifyPhone extends BaseRequest {
	private String param;

	public RegisterVerifyPhone(Context context, String phone, String verifyCode) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_PHONE); s.append(KV_CONN); s.append(phone);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_VERIFY_CODE); s.append(KV_CONN); s.append(verifyCode);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("registerVerifyPhone", param);
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
		return REQ_RET_OK;
	}

}
