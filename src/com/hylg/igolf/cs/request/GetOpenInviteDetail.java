package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.InviteRoleInfo;
import com.hylg.igolf.cs.data.OpenInviteDetail;

public class GetOpenInviteDetail extends GetInviteDetail {
	private String param;
	private OpenInviteDetail detail = null;

	public GetOpenInviteDetail(Context context, String sn, String appSn) {
		super(context);
		detail = new OpenInviteDetail();

		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_APP_SN); s.append(KV_CONN); s.append(appSn);
		param = s.toString();
	}
	
	public OpenInviteDetail getOpenInviteDetail() {
		return detail;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getOpenInviteDetail02", param);
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
			
			DebugTools.getDebug().debug_v("getOpenInviteDetail别人","----->>>"+jo);
			
			JSONObject oid = jo.getJSONObject("openInviteDetail");
			
			detail.inviter = getInviteRoleInfo(oid.getJSONObject(RET_INVITER));
			detail.invitee = getInviteRoleInfo(oid.optJSONObject(RET_INVITEE));
			detail.inviteeone = getInviteRoleInfo(oid.optJSONObject(RET_INVITEE_ONE));
			detail.inviteetwo = getInviteRoleInfo(oid.optJSONObject(RET_INVITEE_TWO));
			detail.message = oid.getString(RET_MESSAGE).replaceAll("[\\n|\\r]","");
			detail.stake = oid.getInt(RET_STAKE);
			detail.paymentType = oid.getInt(RET_PAYMENT_TYPE);
			detail.isApplied = oid.optBoolean("isApplied");
			
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
