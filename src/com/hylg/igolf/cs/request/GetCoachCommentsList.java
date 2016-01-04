package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.CoachComemntsItem;
import com.hylg.igolf.cs.data.MyInviteInfo;

public class GetCoachCommentsList extends BaseRequest {
	private String param;
	private ArrayList<CoachComemntsItem> commentsList = null;

	public GetCoachCommentsList(Context context, long id, int pageNum, int pageSize) {
		super(context);
		commentsList = new ArrayList<CoachComemntsItem>();
		StringBuilder s = new StringBuilder();
		s.append("coachId"); s.append(KV_CONN); s.append(id);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		param = s.toString();
	}

	public ArrayList<CoachComemntsItem> getCoachCommentsList() {
		return commentsList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getCoachCommentsList", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("教练评价列表", "------》》》"+jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			
			JSONArray ja = jo.optJSONArray("coachComments");
			
			if (ja == null || ja.length() <= 0) {
				
				return REQ_RET_F_NO_DATA;
			}
			
			for(int i=0, len=ja.length(); i<len; i++) {
				
				JSONObject obj = ja.optJSONObject(i);
				
				CoachComemntsItem item = new CoachComemntsItem();
				
				item.id = obj.optLong("id");
				item.commentTime = obj.optLong("commentTime");
				item.star = obj.optInt("star");
				item.content = obj.optString("content");
				item.coach_id = obj.optInt("coachId");
				item.appid = obj.optInt("appid");
				
				JSONObject studentJson = obj.optJSONObject("student");
				
				item.student_id = studentJson.optInt("id");
				item.student_avatar = studentJson.optString("avatar");
				item.student_nick_name = studentJson.optString("nickname");
				item.student_sn = studentJson.optString("sn");
				
				commentsList.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
