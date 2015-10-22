package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONObject;

import com.hylg.igolf.MainApp;

import android.content.Context;


public class ModifySignature extends BaseRequest {
	private String param;
	private String signature;

	public ModifySignature(Context context, String sn, String signature) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_SIGNATURE); s.append(KV_CONN); s.append(signature);
		param = s.toString();
		this.signature = signature;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("modifySignature", param);
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
			MainApp.getInstance().getCustomer().signature = signature;
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
