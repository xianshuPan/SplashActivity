package com.hylg.igolf.cs.request;

import android.content.Context;
import android.content.pm.PackageManager;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.CoachInviteOrderDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;

public class GetCoachInviteOrderDetail extends BaseRequest {
	private String param;

	public CoachInviteOrderDetail data = null;


	public GetCoachInviteOrderDetail(Context context, long appCoachId,String sn ) {
		super(context);

		StringBuilder s = new StringBuilder();
		s.append("appCoachId"); s.append(KV_CONN); s.append(appCoachId);
		s.append(PARAM_CONN);
		s.append("appVersion"); s.append(KV_CONN);
		try {
			s.append(context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		s.append(PARAM_CONN);
		s.append("apiVersion"); s.append(KV_CONN); s.append(200);
		s.append(PARAM_CONN);
		s.append("sn"); s.append(KV_CONN); s.append(sn);
		
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {

		return getReqParam2("getPinDanAllDetail", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);

			DebugTools.getDebug().debug_v("getPinDanAllDetail", "------->>>>>"+jo);
			int rn = jo.optInt("result", REQ_RET_FAIL);

			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}

			data = new CoachInviteOrderDetail();

			data.course_abbr = jo.optString("courseName");
			data.course_address = jo.optString("courseAddress");
			data.msg = jo.optString("applyMsg");
			data.coachDate = jo.optString("coachTime");
			data.originalPrice = jo.optDouble("originalPrice");
			data.discountPrice = jo.optDouble("discountPrice");
			data.times = jo.optInt("times");
			data.type = jo.optInt("alone");
			data.alone_status = jo.optInt("aloneStatus");
			data.teacher_type = jo.optInt("coachType");
			data.status = jo.optInt("appStatus");

			//DecimalFormat df = new DecimalFormat("#.00");
			//data.ratio = Double.valueOf(df.format(jo.optDouble("ratio")));
			data.ratio =jo.optDouble("ratio");

			JSONObject coach_json = jo.optJSONObject("coach");
			if (coach_json != null) {

				data.teacher_id = coach_json.optInt("id");
				data.teacher_coach_id = coach_json.optInt("coachId");
				data.teacher_sn = coach_json.optString("sn");
				data.teacher_name = coach_json.optString("name");
				data.teacher_phone = coach_json.optString("phone");
				data.teacher_sex = coach_json.optInt("sex");
				data.teacher_star = coach_json.optInt("personStar");
				data.teacher_experience = coach_json.optInt("coachOrStudyTimes");
				data.teacher_ball_age = coach_json.optInt("ballAge");

			}


			JSONArray student_json = jo.optJSONArray("students");
			if (student_json != null && student_json.length() > 0) {

				JSONObject student1_json = student_json.optJSONObject(0);
				if (student1_json != null) {

					data.student1_id = student1_json.optInt("id");
					data.student1_sn = student1_json.optString("sn");
					data.student1_name = student1_json.optString("name");
					data.student1_phone = student1_json.optString("phone");
					data.student1_sex = student1_json.optInt("sex");
					data.student1_comment_rating = student1_json.optInt("commentStar");
					data.student1_experiment = student1_json.optInt("coachOrStudyTimes");
					data.student1_comment_status = student1_json.optInt("commentStatus");
					data.student1_ball_age = student1_json.optInt("ballAge");
					data.student1_payment_amount = student1_json.optDouble("fee");
					data.student1_payment_status = student1_json.optInt("appStatus");
					data.student1_star = Float.valueOf(student1_json.opt("personStar").toString());
				}

				JSONObject student2_json = student_json.optJSONObject(1);
				if (student2_json != null) {

					data.student2_id = student2_json.optInt("id");
					data.student2_sn = student2_json.optString("sn");
					data.student2_name = student2_json.optString("name");
					data.student2_phone = student2_json.optString("phone");
					data.student2_sex = student2_json.optInt("sex");
					data.student2_comment_rating = student2_json.optInt("commentStar");
					data.student2_experiment = student2_json.optInt("coachOrStudyTimes");
					data.student2_comment_status = student2_json.optInt("commentStatus");
					data.student2_ball_age = student2_json.optInt("ballAge");
					data.student2_payment_amount = student2_json.optDouble("fee");
					data.student2_payment_status = student2_json.optInt("appStatus");
					data.student2_star = Float.valueOf(student2_json.opt("personStar").toString());
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
