package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.CoachInviteOrderDetail;
import com.hylg.igolf.cs.data.MyInviteInfo;

public class GetMyTeachingList extends BaseRequest {
	private String param;
	private ArrayList<CoachInviteOrderDetail> teachingList = null;

	public GetMyTeachingList(Context context, long id, int pageNum, int pageSize) {
		super(context);
		teachingList = new ArrayList<CoachInviteOrderDetail>();
		StringBuilder s = new StringBuilder();
		s.append("custid"); s.append(KV_CONN); s.append(id);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		param = s.toString();
	}
	
	public ArrayList<CoachInviteOrderDetail> getMyTeachingList() {
		return teachingList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("myCoachAppHistory", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			
			DebugTools.getDebug().debug_v("我的教学列表", "------》》》"+jo);
			
			JSONArray ja = jo.optJSONArray("coachApps");
			
			if (ja == null || ja.length() <= 0) {
				
				return REQ_RET_F_NO_DATA;
			}
			
			
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.getJSONObject(i);
				
				CoachInviteOrderDetail item = new CoachInviteOrderDetail();
				
				JSONObject courseJson = obj.optJSONObject("course");
				JSONObject studentJson = obj.optJSONObject("student");
				JSONObject teacherJson = obj.optJSONObject("teacher");
				
				JSONObject teacherFeeJson = teacherJson.optJSONObject("fee");
				
				
				item.appTime = obj.optLong("appTime");
				item.status = obj.optInt("status");
				item.coachDate = obj.optString("coachDate");
				item.times = obj.optInt("times");
				item.fee = obj.optDouble("fee");
				item.start_time = obj.optLong("startTime");
				item.end_time = obj.optLong("endTime");
				item.pause_time = obj.optLong("pauseTime");
				item.period_time = obj.optLong("period");
				item.id = obj.optLong("id");
				item.msg = obj.optString("msg");
				item.coachTime = obj.optString("coachTime");
				item.status_str = obj.optString("statusStr");

				item.teacher_id = teacherJson.optLong("id");
				JSONObject teacher_customer_json = teacherJson.optJSONObject("customer");
				item.teacher_sn = teacher_customer_json.optString("sn");
				item.teacher_avatar = teacher_customer_json.optString("avatar");
				item.teacher_name = teacher_customer_json.optString("nickname");
				item.teacher_star = teacherJson.optLong("star");
				item.teacher_phone = teacher_customer_json.optLong("phone");
				item.teacher_sex = teacher_customer_json.optInt("sex");
				item.teacher_price = teacherFeeJson.optDouble("price"); 
				
				item.student_id = studentJson.optLong("id");
				item.student_sn = studentJson.optString("sn");
				item.student_avatar = studentJson.optString("avatar");
				item.student_name = studentJson.optString("nickname");
				item.student_phone = studentJson.optLong("phone");
				item.student_sex = studentJson.optInt("sex");
				
				item.course_id = courseJson.optLong("id");
				item.course_abbr = courseJson.optString("abbr");
						

				teachingList.add(item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
