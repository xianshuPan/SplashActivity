package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import com.hylg.igolf.DebugTools;

import android.content.Context;

public class FriendPraiseAdd extends BaseRequest {
	private String param;
	
	public String praiseId;
	
	private int praise = -1;

	public FriendPraiseAdd(Context context, String sn, String name,String tipid,int praise) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append("name"); s.append(KV_CONN); s.append(name);
		s.append(PARAM_CONN);
		s.append("tipid"); s.append(KV_CONN); s.append(tipid);
		param = s.toString();
		
		this.praise = praise;
	}
	
	@Override
	public String getRequestUrl() {
		
		String result = "";
		
		if (praise == 0) {
			
			result = getReqParam2("praise", param);
			
		} else if (praise == 1) {
			
			result = getReqParam2("cancelPraise", param);
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
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			
			praiseId = jo.getString("id");
			
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
