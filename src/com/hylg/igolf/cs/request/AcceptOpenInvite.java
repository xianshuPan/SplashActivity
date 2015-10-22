package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONObject;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.ApplicantsInfo;
import com.hylg.igolf.cs.data.InviteRoleInfo;

import android.content.Context;


public class AcceptOpenInvite extends BaseRequest {
	private String param;
	private InviteRoleInfo inviteRole;
	
	private ArrayList<InviteRoleInfo> inviteRoleList;

	//public AcceptOpenInvite(Context context, String appSn, ApplicantsInfo ai) {
	public AcceptOpenInvite(Context context, String appSn, ArrayList<InviteRoleInfo> ai) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_APP_SN); s.append(KV_CONN); s.append(appSn);
		
		if (ai != null && ai.size() > 0) {
			
			s.append(PARAM_CONN);
			s.append(PARAM_REQ_APPLY_SN); s.append(KV_CONN); s.append(ai.get(0).sn);
		}
		
		if (ai != null && ai.size() > 1) {
			
			s.append(PARAM_CONN);
			s.append(PARAM_REQ_APPLY_SN1); s.append(KV_CONN); s.append(ai.get(1).sn);
		} else {
			
			s.append(PARAM_CONN);
			s.append(PARAM_REQ_APPLY_SN1); s.append(KV_CONN); s.append("");
		}

		if (ai != null && ai.size() > 2) {
	
			s.append(PARAM_CONN);
			s.append(PARAM_REQ_APPLY_SN2); s.append(KV_CONN); s.append(ai.get(2).sn);
		} else {
			
			s.append(PARAM_CONN);
			s.append(PARAM_REQ_APPLY_SN2); s.append(KV_CONN); s.append("");
		}
		
		param = s.toString();
		
		inviteRoleList = new ArrayList<InviteRoleInfo>();
		if (ai != null) {
			
			for (int i = 0; i< ai.size(); i++) {
				
				InviteRoleInfo inviteRole = ai.get(i);
				inviteRoleList.add(inviteRole);
				
			}
			//inviteRole = new InviteRoleInfo(ai);
			
		}
		
	}
	
	public ArrayList<InviteRoleInfo> getAcceptRoleInfo() {
		return inviteRoleList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("acceptOpenInvite02", param);
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
			
			String handicapIndexStr = jo.getString("handicapIdxs");
			
			if (handicapIndexStr != null && handicapIndexStr.length() > 0) {
				
				String[] handicapIndexArray = handicapIndexStr.split(",");
				
				if (handicapIndexArray != null && handicapIndexArray.length > 0) {
					
					for (int i = 0; i< handicapIndexArray.length; i++) {
						
						String handicapIndex = handicapIndexArray[i];
						if (handicapIndex != null && handicapIndex.length() > 0 && !handicapIndex.equalsIgnoreCase("null")) {
							
							inviteRoleList.get(i).handicapIndex = Double.valueOf(handicapIndexArray[i]);
						}
						
					}
					
				}
				
			}
			
			String matchesStr = jo.getString("matchess");
			
			if (matchesStr != null && matchesStr.length() > 0) {
				
				String[] matchesArray = matchesStr.split(",");
				
				if (matchesArray != null && matchesArray.length > 0) {
					
					for (int i = 0; i< matchesArray.length; i++) {
						
						inviteRoleList.get(i).matches = Integer.valueOf(matchesArray[i]);
						
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
