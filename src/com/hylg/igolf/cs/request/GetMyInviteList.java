package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.MyInviteInfo;

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
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			
			DebugTools.getDebug().debug_v("我的约球列表", "------》》》"+jo);
			
			JSONArray ja = jo.getJSONArray(RET_MY_INVITES_LIST);
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.getJSONObject(i);
				MyInviteInfo mii = new MyInviteInfo();
				mii.id = obj.getLong(RET_ID);
				mii.sn = obj.getString(RET_SN);
				mii.type = obj.getInt(RET_TYPE);
				mii.status = obj.getInt(RET_STATUS);
				mii.displayStatus = obj.getInt(RET_DISPLAY_STATUS);
				mii.displayStatusStr = obj.getString(RET_DISPLAY_STATUS_STR);
				mii.planNum = obj.optInt(RET_PLAN_NUM, 1);
				mii.courseName = obj.getString(RET_COURSE_NAME);
				mii.teeTime = obj.getString(RET_TEE_TIME);
				mii.applicantsNum = obj.optInt(RET_APPLICANTS_NUM, 0);
				mii.palId = obj.optInt(RET_PAL_ID, Integer.MAX_VALUE);
				mii.palSn = obj.getString(RET_PAL_SN);
				mii.palAvatar = obj.getString(RET_PAL_AVATAR);
				mii.palNickname = obj.getString(RET_PAL_NICKNAME);
				mii.palSex = obj.getInt(RET_PAL_SEX);
				mii.palMsg = obj.getString(RET_PAL_MSG);
				mii.haveAlert = obj.getBoolean(RET_HAVE_ALERT);
				mii.inviterSn = obj.getString("inviterSn");
				inviteList.add(mii);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
