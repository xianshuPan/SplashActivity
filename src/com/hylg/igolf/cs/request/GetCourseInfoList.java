package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.cs.data.CourseInfo;


public class GetCourseInfoList extends BaseRequest {
	private String param;
	private ArrayList<CourseInfo> courseList = null;

	public GetCourseInfoList(Context context, String location) {
		super(context);
		courseList = new ArrayList<CourseInfo>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_LOCATION); s.append(KV_CONN); s.append(location);
		param = s.toString();
	}
	
	public ArrayList<CourseInfo> getCourseList() {
		return courseList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getCourseInfoList", param);
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
			JSONArray ja = jo.getJSONArray("courseInfo");
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.getJSONObject(i);
				CourseInfo course = new CourseInfo();
				course.id = obj.getLong(RET_ID);
				course.abbr = obj.getString(RET_ABBR);
				course.name = obj.getString(RET_NAME);
				courseList.add(course);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
