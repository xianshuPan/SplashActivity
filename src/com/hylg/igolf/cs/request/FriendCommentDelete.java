package com.hylg.igolf.cs.request;

import android.content.Context;
import android.content.pm.PackageManager;

import com.hylg.igolf.DebugTools;

import org.json.JSONObject;

import java.io.InputStream;

public class FriendCommentDelete extends BaseRequest {
	private String param;

	public FriendCommentDelete(Context context, String commentId,String sn) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append("commentId"); s.append(KV_CONN); s.append(commentId);

		s.append(PARAM_CONN);
		s.append("appVersion"); s.append(KV_CONN);
		try {
			s.append(context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		s.append(PARAM_CONN);
		s.append("apiVersion"); s.append(KV_CONN); s.append(200);
		s.append(PARAM_CONN);
		s.append("sn"); s.append(KV_CONN); s.append(sn);

		param = s.toString();
	
	}
	
	@Override
	public String getRequestUrl() {
		
		String result = "";
		
		result = getReqParam2("deleteFriendsCircleComment", param);
			
		return result;
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("deleteFriendsCircleComment", "----->>>"+jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
