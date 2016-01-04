package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CoachRefuseContent extends BaseRequest {
	private String param;

	private ArrayList<HashMap<String, String>> list = null;

	public CoachRefuseContent(Context context, long id) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append("appid"); s.append(KV_CONN); s.append(id);
		
		param = s.toString();
		
		list = new ArrayList<HashMap<String,String>>();

	}
	
	@Override
	public String getRequestUrl() {

		return getReqParam("getCoachRefuseReason", param);
	}
	
	public ArrayList<HashMap<String, String>> getSelectionList () {
		
		return list;
	}

	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("getCoachRefuseReason", "----->>>"+jo);
			
			JSONArray ja = jo.optJSONArray("reasons");
			
			if (ja == null || ja.length() <= 0) {
				
				return REQ_RET_F_NO_DATA;
			}
			
			for (int i = 0; i < ja.length(); i++) {
				
				JSONObject jso = ja.optJSONObject(i);
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				map.put("type", jso.optString("type"));
				map.put("id", jso.optString("id"));
				map.put("reason", jso.optString("reason"));
				
				list.add(map);
			}
			
			
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
