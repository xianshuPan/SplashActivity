package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;

import org.json.JSONObject;

import java.io.InputStream;

public class StudentPinDanInvite extends BaseRequest {
	private String param;

	private int type = -1;

	public StudentPinDanInvite(Context context, long id,long user_id,String sn,int operation_type) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append("appid"); s.append(KV_CONN); s.append(id);

		s.append(PARAM_CONN);
		s.append("userid"); s.append(KV_CONN); s.append(user_id);

		s.append(PARAM_CONN);
		s.append("sn"); s.append(KV_CONN); s.append(sn);

		s.append(PARAM_CONN);
		s.append("apiVersion"); s.append(KV_CONN); s.append(200);

		s.append(PARAM_CONN);
		s.append("appVersion"); s.append(KV_CONN); s.append(MainApp.getInstance().getAppVersionCode());

		type = operation_type;

		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {

		String method = "";

		if (type == 0) {

			method = "doPinDan";
		}
		else if (type == 1) {

			method = "recallPinDan";
		}
		else {

			return "";
		}

		return getReqParam(method, param

		);
	}

	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("doPinDan", "----->>>"+jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);

			failMsg = jo.optString(RET_MSG);
			return rn;

		} catch (Exception e) {
			
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}

	}

}
