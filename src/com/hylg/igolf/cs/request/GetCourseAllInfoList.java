package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.cs.data.CourseInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;


public class GetCourseAllInfoList extends BaseRequest {
	private String param;
	private ArrayList<ArrayList<CourseInfo>> courseList = null;

	public GetCourseAllInfoList(Context context, String location) {
		super(context);
		courseList = new ArrayList<ArrayList<CourseInfo>>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_LOCATION); s.append(KV_CONN); s.append(location);
		s.append(PARAM_CONN);
		s.append("lat"); s.append(KV_CONN); s.append(MainApp.getInstance().getGlobalData().getLat());
		s.append(PARAM_CONN);
		s.append("lng"); s.append(KV_CONN); s.append(MainApp.getInstance().getGlobalData().getLng());
		param = s.toString();
	}
	
	public ArrayList<ArrayList<CourseInfo>> getCourseList() {
		return courseList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getAllCourseList", param);
	}

	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);

			DebugTools.getDebug().debug_v("getAllCourseList","----->>>"+jo);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}

			JSONArray ja = jo.optJSONArray("practiceCourse");

			if (ja != null && ja.length() > 0) {


				ArrayList<CourseInfo> practice = new ArrayList<CourseInfo>();

				for(int i=0, len=ja.length(); i<len; i++) {
					JSONObject obj = ja.optJSONObject(i);
					CourseInfo course = new CourseInfo();
					course.id = obj.optLong(RET_ID);
					course.abbr = obj.optString(RET_ABBR);
					course.name = obj.optString(RET_NAME);
					course.address = obj.optString("address");
					course.distance = obj.optDouble("distance")+"Km";
					practice.add(course);
				}

				courseList.add(practice);
			}

			JSONArray course18 = jo.optJSONArray("course18");
			if (course18 != null && course18.length() > 0) {


				ArrayList<CourseInfo> course18Array = new ArrayList<CourseInfo>();

				for(int i=0, len=course18.length(); i<len; i++) {
					JSONObject obj = course18.optJSONObject(i);

					CourseInfo course = new CourseInfo();
					course.id = obj.optLong(RET_ID);
					course.abbr = obj.optString(RET_ABBR);
					course.name = obj.optString(RET_NAME);
					course.address = obj.optString("address");
					course.distance = obj.optDouble("distance")+"Km";
					course18Array.add(course);
				}

				courseList.add(course18Array);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
