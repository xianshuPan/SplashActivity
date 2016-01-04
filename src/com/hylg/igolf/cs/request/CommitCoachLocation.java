package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.CoachItem;


import org.json.JSONObject;

import java.io.InputStream;

public class CommitCoachLocation extends BaseRequest {
	private String param;

	public CommitCoachLocation(Context context, long id, double lat, double lng) {
		super(context);
		
		StringBuilder s = new StringBuilder();

		s.append("lat"); s.append(KV_CONN); s.append(lat);
		s.append(PARAM_CONN);
		s.append("lng"); s.append(KV_CONN); s.append(lng);
		
		s.append(PARAM_CONN);
		s.append("custid"); s.append(KV_CONN); s.append(id);
		
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam2("saveCoachLocation", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			DebugTools.getDebug().debug_v("saveCoachLocation", "------->>>>>"+jo);
			int rn = jo.optInt("result", REQ_RET_FAIL);
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
