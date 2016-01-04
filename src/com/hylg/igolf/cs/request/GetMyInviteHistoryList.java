package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.InviteHistoryInfo;
import com.hylg.igolf.utils.Const;

public class GetMyInviteHistoryList extends BaseRequest {
	private String param;
	private ArrayList<InviteHistoryInfo> inviteList = null;

	public GetMyInviteHistoryList(Context context, String sn, int pageNum, int pageSize) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		param = s.toString();
		inviteList = new ArrayList<InviteHistoryInfo>();
	}
	
	public ArrayList<InviteHistoryInfo> getMyInviteHistoryList() {
		return inviteList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getMyInviteHistoryList", param);
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
			
			DebugTools.getDebug().debug_v("我的约球历史", "-----》》》》》"+jo);
			
			JSONArray ja = jo.optJSONArray(RET_MY_INVITES_HISTORY_LIST);
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.optJSONObject(i);
				InviteHistoryInfo golfer = new InviteHistoryInfo();
				golfer.avatar = obj.optString(RET_AVATAR);
				golfer.nickname = obj.optString(RET_NICKNAME);
				golfer.teeTime = obj.optString(RET_TEE_TIME);
				golfer.sn = obj.optString(RET_SN);
				golfer.appSn = obj.optString(RET_APP_SN);
				// 目前使用appSn作为记分卡图片名称
				golfer.imgName = golfer.appSn + ".jpg";
				golfer.myHCPI = obj.optInt(RET_MY_HCPI,  Integer.MAX_VALUE);
				golfer.palHCPI = obj.optInt(RET_PAL_HCPI,  Integer.MAX_VALUE);
				golfer.myHCPIStatus = obj.optInt("myHCPIStatus",  Const.SCORE_VALIDE);
				golfer.palHCPIStatus = obj.optInt("palHCPIStatus",  Const.SCORE_VALIDE);
				inviteList.add(golfer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
