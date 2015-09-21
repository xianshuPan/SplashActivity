package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.ApplicantsInfo;
import com.hylg.igolf.cs.data.InviteRoleInfo;

import android.content.Context;


public class EndOpenInvite01 extends BaseRequest {
	private String param;
	private InviteRoleInfo inviteRole;
	
	private ArrayList<InviteRoleInfo> inviteRoleList;

	//public AcceptOpenInvite(Context context, String appSn, ApplicantsInfo ai) {
	public EndOpenInvite01(Context context, String appSn) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_APP_SN); s.append(KV_CONN); s.append(appSn);
		
		param = s.toString();
		
	}
	
	public ArrayList<InviteRoleInfo> getAcceptRoleInfo() {
		return inviteRoleList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("endOpenInvite02", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			failMsg = jo.optString(RET_MSG);
			if(REQ_RET_OK != rn) {
				return rn;
			}
			
			DebugTools.getDebug().debug_v("", ""+jo);
			
			//inviteRole.handicapIndex = jo.optDouble("handicapIdx", Double.MAX_VALUE);
			//inviteRole.matches = jo.optInt(RET_MATCHES, Integer.MAX_VALUE);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
