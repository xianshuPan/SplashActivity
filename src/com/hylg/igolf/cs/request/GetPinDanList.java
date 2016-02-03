package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.PinDanDetailInfo;
import com.hylg.igolf.ui.reqparam.GetPinDanReqParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class GetPinDanList extends BaseRequest {
	private String param;
	private ArrayList<PinDanDetailInfo> inviteList = null;
	private int retNum;

	public GetPinDanList(Context context, GetPinDanReqParam data) {
		super(context);
		inviteList = new ArrayList<PinDanDetailInfo>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(data.sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(data.pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(data.pageSize);
		s.append(PARAM_CONN);
		s.append("lat"); s.append(KV_CONN); s.append(data.lat);
		s.append(PARAM_CONN);
		s.append("lng"); s.append(KV_CONN); s.append(data.lng);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_LOCATION); s.append(KV_CONN); s.append(data.location);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_SEX); s.append(KV_CONN); s.append(data.sex);
		s.append(PARAM_CONN);
		s.append("apiVersion"); s.append(KV_CONN); s.append(data.apiVersion);
		s.append(PARAM_CONN);
		s.append("appVersion"); s.append(KV_CONN); s.append(data.appVersion);
		param = s.toString();
	}
	
	public ArrayList<PinDanDetailInfo> getPinDanList() {
		return inviteList;
	}
	
	public int getRetNum() {
		return retNum;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getCanPinDanList", param);
	}

	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);

			DebugTools.getDebug().debug_v("getCanPinDanList","------>>>"+jo);

			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			//if(REQ_RET_OK != rn && REQ_RET_F_OPEN_LIST_REFRESH != rn) {
				failMsg = jo.optString(RET_MSG);
				//return rn;
			//}
			
			retNum = jo.optInt("amount");
			JSONArray ja = jo.optJSONArray("pinDanList");

			if (ja == null) {

				return REQ_RET_F_NO_DATA;
			}
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.optJSONObject(i);
				PinDanDetailInfo invitation = new PinDanDetailInfo();

				invitation.courseName = obj.optString("courseName");
				invitation.times = obj.optInt("times");
				invitation.studentId = obj.optLong("studentId");
				invitation.coachId = obj.optLong("coachId");
				invitation.courseAddress = obj.optString("courseAddress");
				invitation.coachTime = obj.optString("coachTime");
				invitation.coachSn = obj.optString("coachSn");
				invitation.studentSn = obj.optString("studentSn");
				invitation.courseName = obj.optString("courseName");
				invitation.studentName = obj.optString("studentName");
				invitation.coachAppId = obj.optLong("coachAppId");
				invitation.coachName = obj.optString("coachName");
				invitation.refer = obj.optInt("refer");
				invitation.emergemcy = obj.optInt("emergemcy");
				invitation.price = obj.optDouble("price");

				inviteList.add(invitation);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
