package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.cs.data.ConfigInfo;

public class GetConfigInfo extends BaseRequest {
	private String param;
	private ConfigInfo configInfo;

	public GetConfigInfo(Context context, int type) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_TYPE); s.append(KV_CONN); s.append(type);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getConfigInfo", param);
	}

	public ConfigInfo getConfigInfo(){
		return configInfo;
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
			configInfo = new ConfigInfo();
//			configInfo.type = jo.getString(RET_TYPE);
			configInfo.info = str;
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
