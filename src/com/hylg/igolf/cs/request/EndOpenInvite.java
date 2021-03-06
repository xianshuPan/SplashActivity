package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;


import org.json.JSONObject;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.ApplicantsInfo;
import com.hylg.igolf.cs.data.InviteRoleInfo;

import android.content.Context;


public class EndOpenInvite extends BaseRequest {
	private String param;
	private InviteRoleInfo inviteRole;
	
	private ArrayList<InviteRoleInfo> inviteRoleList;

	//public AcceptOpenInvite(Context context, String appSn, ApplicantsInfo ai) {
	public EndOpenInvite(Context context, String appSn, ArrayList<String> ai) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_APP_SN); s.append(KV_CONN); s.append(appSn);
		
		if (ai != null && ai.size() > 0) {
			
			s.append(PARAM_CONN);
			s.append(PARAM_REQ_APPLY_SN); s.append(KV_CONN); s.append(ai.get(0));
		}
		
		if (ai != null && ai.size() > 1) {
			
			s.append(PARAM_CONN);
			s.append(PARAM_REQ_APPLY_SN1); s.append(KV_CONN); s.append(ai.get(1));
			
		} else {
			
			s.append(PARAM_CONN);
			s.append(PARAM_REQ_APPLY_SN1); s.append(KV_CONN); s.append("null");
		}

		if (ai != null && ai.size() > 2) {
	
			s.append(PARAM_CONN);
			s.append(PARAM_REQ_APPLY_SN2); s.append(KV_CONN); s.append(ai.get(2));
		} else {
			
			s.append(PARAM_CONN);
			s.append(PARAM_REQ_APPLY_SN2); s.append(KV_CONN); s.append("null");
		}
		
		param = s.toString();
		
//		inviteRoleList = new ArrayList<InviteRoleInfo>();
//		if (ai != null) {
//
//			for (int i = 0; i< ai.size(); i++) {
//
//				InviteRoleInfo inviteRole = ai.get(i);
//				inviteRoleList.add(inviteRole);
//
//			}
//			//inviteRole = new InviteRoleInfo(ai);
//
//		}
		
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

			DebugTools.getDebug().debug_v("endOpenInvite02", "--->>>"+jo);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			failMsg = jo.optString(RET_MSG);
			if(REQ_RET_OK != rn) {
				return rn;
			}
			

			
			//inviteRole.handicapIndex = jo.optDouble("handicapIdx", Double.MAX_VALUE);
			//inviteRole.matches = jo.optInt(RET_MATCHES, Integer.MAX_VALUE);
			
//			String handicapIndexStr = jo.getString("handicapIdxs");
//
//			if (handicapIndexStr != null && handicapIndexStr.length() > 0) {
//
//				String[] handicapIndexArray = handicapIndexStr.split(",");
//
//				if (handicapIndexArray != null && handicapIndexArray.length > 0) {
//
//					for (int i = 0; i< handicapIndexArray.length; i++) {
//
//						String handicapIndex = handicapIndexArray[i];
//						if (handicapIndex != null && handicapIndex.length() > 0 && !handicapIndex.equalsIgnoreCase("null")) {
//
//							inviteRoleList.get(i).handicapIndex = Double.valueOf(handicapIndexArray[i]);
//						}
//
//					}
//
//				}
//
//			}
//
//			String matchesStr = jo.getString("matchess");
//
//			if (matchesStr != null && matchesStr.length() > 0) {
//
//				String[] matchesArray = matchesStr.split(",");
//
//				if (matchesArray != null && matchesArray.length > 0) {
//
//					for (int i = 0; i< matchesArray.length; i++) {
//
//						inviteRoleList.get(i).matches = Integer.valueOf(matchesArray[i]);
//
//					}
//
//				}
//
//			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
