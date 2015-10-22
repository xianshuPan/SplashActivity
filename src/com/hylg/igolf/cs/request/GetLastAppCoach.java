package com.hylg.igolf.cs.request;

import java.io.InputStream;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.CoachItem;

public class GetLastAppCoach extends BaseRequest {
	private String param;
	
	private CoachItem item;

	public GetLastAppCoach(Context context, long id, double lat , double lng) {
		super(context);
		item = new CoachItem();
		
		StringBuilder s = new StringBuilder();

		s.append("lat"); s.append(KV_CONN); s.append(lat);
		s.append(PARAM_CONN);
		s.append("lng"); s.append(KV_CONN); s.append(lng);
		
		s.append(PARAM_CONN);
		s.append("custId"); s.append(KV_CONN); s.append(id);
		
		param = s.toString();
	}
	
	public CoachItem getLastAppCoach() {
		return item;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam2("getLastAppCoach", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			DebugTools.getDebug().debug_v("最近越过的教练", "------->>>>>"+jo);

			int rn = jo.optInt("result", REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			
			JSONObject obj = jo.optJSONObject("coach");
			
			if (obj != null) {

				item = new CoachItem();

				JSONObject courseJson = obj.optJSONObject("course");
				JSONObject customerJson = obj.optJSONObject("customer");
				JSONObject feeJson = obj.optJSONObject("fee");

				item.sn = customerJson.optString("sn");
				item.id = obj.optLong("id");
				item.nickname = customerJson.optString("nickname");
				item.avatar = customerJson.optString("avatar");

				item.teachTimes = obj.optInt("experience");
				item.teachYear = obj.optInt("teaching_age");
				item.type = feeJson.optInt("type");
				item.rate = obj.optInt("star");

				item.award = obj.optString("award");

				item.handicapIndex = customerJson.optDouble("handicapIndex");
				item.price = feeJson.optInt("price");
				item.distance = obj.optDouble("distance");
				item.distanceTime = obj.optLong("last_login");

				item.course_id = courseJson.optString("id");
				item.course_name = courseJson.optString("name");
				item.course_address = courseJson.optString("address");
				item.course_tel = courseJson.optString("tel");

				item.commentsAmount = obj.optInt("commentsAmount");
				item.attention = obj.optInt("attention");
			}

		} catch (Exception e) {
			
			
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
