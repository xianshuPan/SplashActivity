package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import com.hylg.igolf.DebugTools;

import android.content.Context;


public class RegisterSubmitPhone extends BaseRequest {
	private String param;
	private String verifyCode;
	
	String phone ;

	public RegisterSubmitPhone(Context context, String phone) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_PHONE); s.append(KV_CONN); s.append(phone);
		
		//s.append(PARAM_REQ_PHONE); s.append(KV_CONN); s.append(phone);
		param = s.toString();
		
		this.phone = phone;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("registerSubmitPhone", param);
		//return getReqParam2("sms/send", "tpl=reg&mobile="+phone);
	}

	public String getVerifyCode() {
		return verifyCode;
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("return", "jo??????"+jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			verifyCode = jo.getString(RET_VERIFY_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
