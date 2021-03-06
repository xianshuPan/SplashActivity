package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.cs.data.CoachInviteOrderDetail;
import com.hylg.igolf.cs.data.MyTeachingItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class GetMyTeachingListNew extends BaseRequest {
	private String param;
	private ArrayList<MyTeachingItem> teachingList = null;

	public GetMyTeachingListNew(Context context, long id, String sn ,int pageNum, int pageSize) {
		super(context);
		teachingList = new ArrayList<MyTeachingItem>();
		StringBuilder s = new StringBuilder();
		s.append("userid"); s.append(KV_CONN); s.append(id);
		s.append(PARAM_CONN);
		s.append("sn"); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append("apiVersion"); s.append(KV_CONN); s.append(200);
		s.append(PARAM_CONN);
		s.append("appVersion"); s.append(KV_CONN); s.append(MainApp.getInstance().getAppVersionCode());
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		param = s.toString();
	}
	
	public ArrayList<MyTeachingItem> getMyTeachingList() {
		return teachingList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getMyTeachingList", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			DebugTools.getDebug().debug_v("getMyTeachingList", "------》》》"+jo);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);

			failMsg = jo.optString(RET_MSG);
			if(REQ_RET_OK != rn) {

				return rn;
			}
			
			JSONArray ja = jo.optJSONArray("myCoachList");
			
			if (ja == null || ja.length() <= 0) {
				
				return REQ_RET_F_NO_DATA;
			}

			for (int i=0;i < ja.length();i++) {

				JSONObject my_teaching_item_json = ja.optJSONObject(i);

				MyTeachingItem data = new MyTeachingItem();

				data.alone = my_teaching_item_json.optInt("alone");
				data.coachId = my_teaching_item_json.optLong("coachId");
				data.coachAppId = my_teaching_item_json.optLong("coachAppId");
				data.price = my_teaching_item_json.optDouble("price");
				data.times = my_teaching_item_json.optInt("times");
				data.courseAddress = my_teaching_item_json.optString("courseAddress");
				data.courseName = my_teaching_item_json.optString("courseName");
				data.coachTime = my_teaching_item_json.optString("coachTime");


				JSONArray student_json = my_teaching_item_json.optJSONArray("student");
				if (student_json != null && student_json.length() > 0) {

					JSONObject student1_json = student_json.optJSONObject(0);
					if (student1_json != null) {

						data.student1Id= student1_json.optInt("studentId");
						data.student1Sn = student1_json.optString("studentSn");
						data.student1Name = student1_json.optString("studentName");
						data.student1appStatus = student1_json.optInt("appStatus");
					}

					JSONObject student2_json = student_json.optJSONObject(1);
					if (student2_json != null) {


						data.student2Id= student2_json.optInt("studentId");
						data.student2Sn = student2_json.optString("studentSn");
						data.student2Name = student2_json.optString("studentName");
						data.student2appStatus = student2_json.optInt("appStatus");
					}

				}

				teachingList.add(data);
			}


		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
