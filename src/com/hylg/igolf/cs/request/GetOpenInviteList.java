package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.OpenInvitationInfo;
import com.hylg.igolf.ui.reqparam.GetOpenInviteReqParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class GetOpenInviteList extends BaseRequest {
	private String param;
	private ArrayList<OpenInvitationInfo> inviteList = null;
	private int retNum;

	public GetOpenInviteList(Context context, GetOpenInviteReqParam data) {
		super(context);
		inviteList = new ArrayList<OpenInvitationInfo>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(data.sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(data.pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(data.pageSize);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_DATE); s.append(KV_CONN); s.append(data.date);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_TIME); s.append(KV_CONN); s.append(data.time);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_LOCATION); s.append(KV_CONN); s.append(data.location);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_SEX); s.append(KV_CONN); s.append(data.sex);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAY); s.append(KV_CONN); s.append(data.pay);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_STAKE); s.append(KV_CONN); s.append(data.stake);
		param = s.toString();
	}
	
	public ArrayList<OpenInvitationInfo> getOpenInviteList() {
		return inviteList;
	}
	
	public int getRetNum() {
		return retNum;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getOpenInviteList02", param);
	}

	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);

			DebugTools.getDebug().debug_v("getOpenInviteList02","------>>>"+jo);

			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn && REQ_RET_F_OPEN_LIST_REFRESH != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			
			retNum = jo.optInt(RET_OPEN_RET_NUM);
			JSONArray ja = jo.optJSONArray(RET_OPEN_INVITES_LIST);
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.optJSONObject(i);
				OpenInvitationInfo invitation = new OpenInvitationInfo();
				invitation.id = obj.optLong(RET_ID);
				invitation.sn = obj.optString(RET_SN);
				invitation.status = obj.optInt(RET_STATUS);
				invitation.inviterId = obj.optLong(RET_INVITER_ID);
				invitation.inviterSn = obj.optString(RET_INVITER_SN);
				invitation.inviterNickname = obj.optString(RET_INVITER_NICKNAME);
				invitation.inviterSex = obj.optInt(RET_INVITER_SEX);
				invitation.avatar = obj.optString(RET_AVATAR);
				invitation.courseName = obj.optString(RET_COURSE_NAME);
				invitation.teeTime = obj.optString(RET_TEE_TIME);
				invitation.applicantsNum = obj.optInt(RET_APPLICANTS_NUM);
				invitation.displayMsg = obj.optString(RET_DISPLAY_MSG);
				invitation.displayStatus = obj.optInt(RET_DISPLAY_STATUS);
				invitation.stake = obj.optInt(RET_STAKE);
				invitation.payType = obj.optInt(RET_PAY_TYPE);
				invitation.inviteeId = obj.optInt("inviteeId");
				invitation.inviteeSn = obj.optString("inviteeSn");
				invitation.inviteeAvatar = obj.optString("inviteeAvatar");
				invitation.invitee_sns = obj.optString("sns");
				invitation.acceptMe = obj.optBoolean("accepted");
				invitation.sameProvencePerson = obj.optInt("sameProvencePerson");
				invitation.local_fans = obj.optInt("infoFans");
				inviteList.add(invitation);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
