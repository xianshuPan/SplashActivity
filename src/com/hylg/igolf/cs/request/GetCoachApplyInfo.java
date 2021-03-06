package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONException;
import org.json.JSONObject;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.ui.reqparam.CoachApplyInfoReqParam;

import android.content.Context;


public class GetCoachApplyInfo extends BaseRequest {
	private String param;
	
	public CoachApplyInfoReqParam item ;

	public GetCoachApplyInfo(Context context, long user_id) {
		super(context);
		StringBuilder s = new StringBuilder();
		
		s.append("custId"); s.append(KV_CONN); s.append(user_id);
		
		param = s.toString();
		
		item = new CoachApplyInfoReqParam();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getAppCoachDefaultInfo", param);
	}

	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("我的教练信息", "----->>>>"+ jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			
			JSONObject coachJson = jo.optJSONObject("coach");
			
			if (coachJson != null) {
				
				JSONObject customerJson = coachJson.optJSONObject("customer");
				JSONObject courseJson = coachJson.optJSONObject("course");
				
				JSONObject feeJson = coachJson.optJSONObject("fee");
				
				item.id = coachJson.optInt("id");
				item.sn = customerJson.optString("sn");
				item.age = customerJson.optInt("ageStr");
				item.age_str = customerJson.optString("ageStr")+"岁";
				item.sex = customerJson.optInt("sex");
				item.ball_age = customerJson.optInt("yearsExpStr");
				item.industry = customerJson.optString("industry");
				item.customer_region = customerJson.optString("city");
				item.type = feeJson.optInt("type");
				item.teach_age = coachJson.optInt("teaching_age");
				item.special = coachJson.optString("specialty");
				item.avatar = customerJson.optString("avatar");
				item.star = coachJson.optInt("star");
				item.teacing_count = coachJson.optLong("experience");

				String id_card_str = coachJson.optString("idcard");
				
				if (id_card_str != null && id_card_str.length() > 0) {
					
					String [] result = id_card_str.split(",");
					
					if (result != null && result.length > 0) {
						
						item.id_fron_name = result[0];
						item.id_back_name = result[1];
					}
				}
				
				item.award_name = coachJson.optString("award");
				item.graduate_name = coachJson.optString("diploma");
				item.certificate_name = coachJson.optString("certification");

				if (courseJson != null) {

					item.courseid = courseJson.optLong("id");

					item.course_abbr = courseJson.optString("abbr");

					item.course_name = courseJson.optString("name");

					item.course_address = courseJson.optString("address");

					item.state = courseJson.optString("state");
				}
				
				item.lat = coachJson.optDouble("lat");
				
				item.lng = coachJson.optDouble("lng");
				
				item.audit = coachJson.optInt("audit");
				
				item.name = coachJson.optString("name");
				
				item.auditString = jo.optString("auditResult");
				
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
