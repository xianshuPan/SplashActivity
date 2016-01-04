package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.utils.Base64;


import org.json.JSONObject;

import java.io.InputStream;


public class CommitToCashInfo extends BaseRequest {
	private String param;

	public CommitToCashInfo(Context context,
							long custid,
							String code,
							String password,
							String amount) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append("custid"); s.append(KV_CONN); s.append(custid);
		s.append(PARAM_CONN);
		s.append("withdrwal_pwd"); s.append(KV_CONN); s.append(Base64.encode(password.getBytes()));
		s.append(PARAM_CONN);
		s.append("code"); s.append(KV_CONN); s.append(code);
		s.append(PARAM_CONN);
		s.append("amount"); s.append(KV_CONN); s.append(amount );

		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("saveWithdrwalInfo", param);
	}

	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			DebugTools.getDebug().debug_v("commit_to_cash_info", "----->>>>"+jo);

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
