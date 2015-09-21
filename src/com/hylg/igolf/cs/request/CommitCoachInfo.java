package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.ui.reqparam.CoachApplyInfoReqParam;

import android.content.Context;


public class CommitCoachInfo extends BaseRequest {
	private String param;

	public CommitCoachInfo(Context context, long user_id,CoachApplyInfoReqParam para) {
		super(context);
		StringBuilder s = new StringBuilder();
		
		s.append("type"); s.append(KV_CONN); s.append(para.type); s.append(PARAM_CONN);
		s.append("sex"); s.append(KV_CONN); s.append(para.sex);s.append(PARAM_CONN);
		s.append("id"); s.append(KV_CONN); s.append(user_id);s.append(PARAM_CONN);
		s.append("age"); s.append(KV_CONN); s.append(para.age);s.append(PARAM_CONN);
		s.append("name"); s.append(KV_CONN); s.append(para.name);s.append(PARAM_CONN);
		s.append("idCards"); s.append(KV_CONN); s.append(para.id_fron_name+","+para.id_back_name);s.append(PARAM_CONN);
		s.append("courseid"); s.append(KV_CONN); s.append(para.courseid);s.append(PARAM_CONN);
		s.append("specialty"); s.append(KV_CONN); s.append(para.special);s.append(PARAM_CONN);
		s.append("teaching_age"); s.append(KV_CONN); s.append(para.teach_age);s.append(PARAM_CONN);
		s.append("award"); s.append(KV_CONN); s.append(para.award_name);s.append(PARAM_CONN);
		s.append("certification"); s.append(KV_CONN); s.append(para.certificate_name);s.append(PARAM_CONN);
		s.append("diploma"); s.append(KV_CONN); s.append(para.graduate_name);s.append(PARAM_CONN);
		s.append("lat"); s.append(KV_CONN); s.append(para.lat);s.append(PARAM_CONN);
		s.append("lng"); s.append(KV_CONN); s.append(para.lng);
		
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getAppCoachInfo", param);
	}

	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("提交教练信息", "----->>>"+jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
