package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import com.hylg.igolf.DebugTools;

import android.content.Context;

public class FriendCommentsAdd extends BaseRequest {
	
	private String param;

	public FriendCommentsAdd(Context context, String sn, String name,String avatar,
						String tipId, String tosn, String toname,
						String content) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append("name"); s.append(KV_CONN); s.append(name);
		s.append(PARAM_CONN);
		s.append("avatar"); s.append(KV_CONN); s.append(avatar);
		s.append(PARAM_CONN);
		s.append("tipid"); s.append(KV_CONN); s.append(tipId);
		s.append(PARAM_CONN);
		s.append("tosn"); s.append(KV_CONN); s.append(tosn);
		s.append(PARAM_CONN);
		s.append("toname"); s.append(KV_CONN); s.append(toname);
		s.append(PARAM_CONN);
		s.append("content"); s.append(KV_CONN); s.append(content);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam2("doComment", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		
		String str = transferIs2String(is);
		
		try {
			
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("", "comments add----->>>"+jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
