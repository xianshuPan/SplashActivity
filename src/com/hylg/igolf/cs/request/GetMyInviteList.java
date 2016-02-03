package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.MyInviteInfo;
import com.hylg.igolf.ui.hall.data.Applicant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class GetMyInviteList extends BaseRequest {
	private String param;
	private ArrayList<MyInviteInfo> inviteList = null;

	public GetMyInviteList(Context context, String sn, int pageNum, int pageSize) {
		super(context);
		inviteList = new ArrayList<MyInviteInfo>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		param = s.toString();
	}
	
	public ArrayList<MyInviteInfo> getMyInviteList() {
		return inviteList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getMyInviteList02", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			
			DebugTools.getDebug().debug_v("我的约球列表", "------》》》"+jo);
			
			JSONArray ja = jo.optJSONArray(RET_MY_INVITES_LIST);
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.optJSONObject(i);
				MyInviteInfo mii = new MyInviteInfo();
				mii.id = obj.optLong(RET_ID);
				mii.sn = obj.optString(RET_SN);
				mii.type = obj.optInt(RET_TYPE);
				mii.payType = obj.optInt("payType");
				mii.local_fans = obj.optInt("infoFans");
				mii.sameProvencePerson = obj.optInt("sameProvencePerson");
				mii.status = obj.optInt(RET_STATUS);
				mii.displayStatus = obj.optInt(RET_DISPLAY_STATUS);
				mii.displayStatusStr = obj.optString(RET_DISPLAY_STATUS_STR);
				mii.planNum = obj.optInt(RET_PLAN_NUM, 1);
				mii.courseName = obj.optString(RET_COURSE_NAME);
				mii.teeTime = obj.optString(RET_TEE_TIME);
				mii.applicantsNum = obj.optInt(RET_APPLICANTS_NUM, 0);
				mii.palId = obj.optInt(RET_PAL_ID, Integer.MAX_VALUE);
				mii.palSn = obj.optString(RET_PAL_SN);
				mii.palAvatar = obj.optString(RET_PAL_AVATAR);
				mii.palNickname = obj.optString(RET_PAL_NICKNAME);
				mii.palSex = obj.optInt(RET_PAL_SEX);
				mii.palMsg = obj.optString(RET_PAL_MSG);
				mii.haveAlert = obj.optBoolean(RET_HAVE_ALERT);
				mii.inviterSn = obj.optString("inviterSn");
				mii.inviterSex = obj.optInt("inviterSex");
				mii.inviterName = obj.optString("inviterName");

				inviteList.add(mii);

				JSONArray applicantsJson = obj.optJSONArray("applicants");

				if (applicantsJson != null && applicantsJson.length() > 0) {

					mii.applicants = new ArrayList<Applicant>();
					for (int j = 0;j < applicantsJson.length();j++) {

						JSONObject applicantsItemJson = applicantsJson.optJSONObject(j);
						Applicant applicantItem = new Applicant();

						applicantItem.applicant_id = applicantsItemJson.optInt("id");
						applicantItem.applicant_sex = applicantsItemJson.optInt("sex");
						applicantItem.applicant_nick_name = applicantsItemJson.optString("nickname");
						applicantItem.applicant_sn = applicantsItemJson.optString("sn");

						mii.applicants.add(applicantItem);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
