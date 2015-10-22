package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;

public class CoachComplainOrRefuseList extends BaseRequest {
	private String param;
	
	private int type = -1; 
	
	private ArrayList<HashMap<String, String>> list = null;

	public CoachComplainOrRefuseList(Context context, long id,int type) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append("customer_id"); s.append(KV_CONN); s.append(id);
		
		param = s.toString();
		
		list = new ArrayList<HashMap<String,String>>();
		
		this.type = type;
	}
	
	@Override
	public String getRequestUrl() {
		
		if (type == 0) {
			
			return getReqParam("getComplainReason", param);
			
		} else {
			
			return getReqParam("getRefuseReason", param);
		}
		
	}
	
	public ArrayList<HashMap<String, String>> getSelectionList () {
		
		return list;
	}

	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("教练拒接或者是接受的", "----->>>"+jo);
			
			JSONArray ja = jo.optJSONArray("complainReasons");
			
			if (ja == null || ja.length() <= 0) {
				
				return REQ_RET_F_NO_DATA;
			}
			
			for (int i = 0; i < ja.length(); i++) {
				
				JSONObject jso = ja.getJSONObject(i);
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				map.put("type", jso.optString("type"));
				map.put("id", jso.optString("id"));
				map.put("reason", jso.optString("reason"));
				
				list.add(map);
			}
			
			
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
