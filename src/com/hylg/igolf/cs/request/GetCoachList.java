package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.ui.reqparam.CoachListReqParam;

public class GetCoachList extends BaseRequest {
	private String param;
	private ArrayList<CoachItem> coachList = null;

	public GetCoachList(Context context, CoachListReqParam data) {
		super(context);
		coachList = new ArrayList<CoachItem>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(data.sn);
		s.append(PARAM_CONN);
	
		s.append(PARAM_REQ_SEX); s.append(KV_CONN); s.append(data.sex);
		s.append(PARAM_CONN);
		
		s.append("rangeBy"); s.append(KV_CONN); s.append(data.rangeBy);
		s.append(PARAM_CONN);
		
		s.append("type"); s.append(KV_CONN); s.append(data.type);
		s.append(PARAM_CONN);
		
		s.append("lat"); s.append(KV_CONN); s.append(data.lat);
		s.append(PARAM_CONN);
		
		s.append("lng"); s.append(KV_CONN); s.append(data.lng);
		s.append(PARAM_CONN);	
		
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(data.pageNum);
		s.append(PARAM_CONN);
		
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(data.pageSize);
		param = s.toString();
	}
	
	public ArrayList<CoachItem> getCoachList() {
		return coachList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getCoachList", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("coachlist", "------->>>>"+jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			
			JSONArray ja = jo.optJSONArray("coaches");
			
			if (ja == null) {
				
				return REQ_RET_F_NO_DATA;
			}
			
			for(int i=0, len=ja.length(); i<len; i++) {
				
				JSONObject obj = ja.getJSONObject(i);
				
				CoachItem item = new CoachItem();
				
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
				//item.course_distance = courseJson.optString("specialty");

				coachList.add(item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
