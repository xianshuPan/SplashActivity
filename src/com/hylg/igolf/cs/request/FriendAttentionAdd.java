package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import com.hylg.igolf.DebugTools;

import android.content.Context;

public class FriendAttentionAdd extends BaseRequest {
	private String param;
	
	private int attention = -1;

	public FriendAttentionAdd(Context context, String sn, String tosn, int attention) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append("tosn"); s.append(KV_CONN); s.append(tosn);
		param = s.toString();
		
		this.attention = attention;
	}
	
	@Override
	public String getRequestUrl() {
		
		String result = "";
		
		if (attention == 0) {
			
			result = getReqParam2("attention", param);
			
		} else if (attention == 1) {
			
			result = getReqParam2("cancelAttention", param);
		}
		
		return result;
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("friend_attention", "----->>>"+jo);
			
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
