package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.ScoreHistoryInfo;

public class GetScoreHistoryList extends BaseRequest {
	private String param;
	private ArrayList<ScoreHistoryInfo> scoreHistoryList = null;

	public GetScoreHistoryList(Context context, String sn, int pageNum, int pageSize) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		param = s.toString();
		scoreHistoryList = new ArrayList<ScoreHistoryInfo>();
	}
	
	public ArrayList<ScoreHistoryInfo> getScoreHistoryList() {
		return scoreHistoryList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getScoreHistoryList", param);
	}

	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);

			DebugTools.getDebug().debug_v("getScoreHistoryList","----->>>"+jo);

			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			JSONArray ja = jo.optJSONArray(RET_SCORE_HISTORY);
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.optJSONObject(i);
				ScoreHistoryInfo scoreHistoryInfo = new ScoreHistoryInfo();
				scoreHistoryInfo.handicap = obj.optString(RET_HANDICAP);
				scoreHistoryInfo.courseName = obj.optString(RET_COURSE_NAME);
				scoreHistoryInfo.teeTime = obj.optString(RET_TEE_TIME);
				scoreHistoryInfo.appSn = obj.optString(RET_APP_SN);
				scoreHistoryInfo.imgName = obj.optString("imgName");
				scoreHistoryList.add(scoreHistoryInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
