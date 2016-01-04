package com.hylg.igolf.cs.request;


import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.cs.data.InviteRoleInfo;


public abstract class GetInviteDetail extends BaseRequest {

	public GetInviteDetail(Context context) {
		super(context);
	}

	protected InviteRoleInfo getInviteRoleInfo(JSONObject jo) throws Exception {
		if(null == jo) {
			return null;
		}
		InviteRoleInfo role = new InviteRoleInfo();
		role.id = jo.optLong(RET_ID);
		role.sn = jo.optString(RET_SN);
		role.avatar = jo.optString(RET_AVATAR);
		role.nickname = jo.optString(RET_NICKNAME);
		role.sex = jo.optInt(RET_SEX);
		role.rate = jo.optDouble(RET_RATE, Double.MAX_VALUE);
		role.ratedCount = jo.optInt(RET_RATE_COUNT);
		role.matches = jo.optInt(RET_MATCHES);
		role.handicapIndex = jo.optDouble(RET_HANDICAP_INDEX, Double.MAX_VALUE);
		role.best = jo.optInt(RET_BEST, Integer.MAX_VALUE);
		role.score = jo.optInt(RET_SCORE, Integer.MAX_VALUE);
		return role;
	}
}
