package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.ui.reqparam.RegisterCustomerReqParam;
import com.hylg.igolf.utils.Base64;


public class RegisterCustomer extends BaseRequest {
	private String sn;
	private String param;

	public RegisterCustomer(Context context, RegisterCustomerReqParam reqParam) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_PHONE); s.append(KV_CONN); s.append(reqParam.phone);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_NICKNAME); s.append(KV_CONN); s.append(reqParam.nickname);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PWD); s.append(KV_CONN); s.append(Base64.encode(reqParam.password.getBytes()));
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_SEX); s.append(KV_CONN); s.append(reqParam.sex);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_YEARSEXP_STR); s.append(KV_CONN); s.append(reqParam.yearsExp);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_CITY); s.append(KV_CONN); s.append(reqParam.city);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_INDUSTRY); s.append(KV_CONN); s.append(reqParam.industry);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_AGE); s.append(KV_CONN); s.append(reqParam.age);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("registerCustomer", param);
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

			DebugTools.getDebug().debug_v("registerCustomer","----->>>"+jo);
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			sn = jo.optString(RET_SN);
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
