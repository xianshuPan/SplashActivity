package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.ui.reqparam.ModifyCustomerReqParam;
import com.hylg.igolf.utils.Base64;


public class ModifyPassword extends BaseRequest {
	private String param;

	public ModifyPassword(Context context, ModifyCustomerReqParam reqParam) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(reqParam.sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_OLD_PWD); s.append(KV_CONN); s.append(Base64.encode(reqParam.oldPwd.getBytes()));
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_NEW_PWD); s.append(KV_CONN); s.append(Base64.encode(reqParam.newPwd.getBytes()));
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("modifyUserPassword", param);
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
