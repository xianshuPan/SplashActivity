package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.MyFolloweInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class GetSelfInfo extends BaseRequest {
	private String param;

	public int praiseAmount,myAttentionAmount,myFansAmount;

	public double balance;

	public GetSelfInfo(Context context, String sn) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append("appVersion"); s.append(KV_CONN); s.append(145);
		
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam2("getMyPAFAmount", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			int rn = jo.optInt("result", REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}

			praiseAmount = jo.optInt("praiseAmount");
			myAttentionAmount = jo.optInt("myAttentionAmount");
			myFansAmount = jo.optInt("myFansAmount");
			balance = jo.optDouble("balance");
			
			DebugTools.getDebug().debug_v("getMyPAFAmount", "------->>>>>"+jo);

		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
