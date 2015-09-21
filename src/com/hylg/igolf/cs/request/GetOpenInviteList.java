package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.cs.data.OpenInvitationInfo;
import com.hylg.igolf.ui.reqparam.GetOpenInviteReqParam;

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
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn && REQ_RET_F_OPEN_LIST_REFRESH != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			
			retNum = jo.getInt(RET_OPEN_RET_NUM);
			JSONArray ja = jo.getJSONArray(RET_OPEN_INVITES_LIST);
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.getJSONObject(i);
				OpenInvitationInfo invitation = new OpenInvitationInfo();
				invitation.id = obj.getLong(RET_ID);
				invitation.sn = obj.getString(RET_SN);
				invitation.status = obj.getInt(RET_STATUS);
				invitation.inviterId = obj.getLong(RET_INVITER_ID);
				invitation.inviterSn = obj.getString(RET_INVITER_SN);
				invitation.inviterNickname = obj.getString(RET_INVITER_NICKNAME);
				invitation.inviterSex = obj.getInt(RET_INVITER_SEX);
				invitation.avatar = obj.getString(RET_AVATAR);
				invitation.courseName = obj.getString(RET_COURSE_NAME);
				invitation.teeTime = obj.getString(RET_TEE_TIME);
				invitation.applicantsNum = obj.getInt(RET_APPLICANTS_NUM);
				invitation.displayMsg = obj.getString(RET_DISPLAY_MSG);
				invitation.displayStatus = obj.getInt(RET_DISPLAY_STATUS);
				invitation.stake = obj.getInt(RET_STAKE);
				invitation.payType = obj.getInt(RET_PAY_TYPE);
				invitation.inviteeId = obj.optInt("inviteeId");
				invitation.inviteeSn = obj.optString("inviteeSn");
				invitation.inviteeAvatar = obj.optString("inviteeAvatar");
				invitation.acceptMe = obj.optBoolean("accepted");
				inviteList.add(invitation);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
