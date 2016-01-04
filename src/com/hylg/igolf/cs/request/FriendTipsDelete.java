package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import com.hylg.igolf.DebugTools;

import android.content.Context;

public class FriendTipsDelete extends BaseRequest {
	private String param;

	public FriendTipsDelete(Context context, String tipid) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append("tipid"); s.append(KV_CONN); s.append(tipid);
		param = s.toString();
	
	}
	
	@Override
	public String getRequestUrl() {
		
		String result = "";
		
		result = getReqParam2("deleteTips", param);
			
		return result;
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("deleteTips", "----->>>"+jo);
			
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
