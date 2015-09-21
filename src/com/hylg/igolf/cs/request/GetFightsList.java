package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.cs.data.MemMyFightInfo;

public class GetFightsList extends BaseRequest {
	private String param;
	private ArrayList<MemMyFightInfo> fightRecordList = null;

	public GetFightsList(Context context, String sn,String memSn, int pageNum, int pageSize) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_MEM_SN); s.append(KV_CONN); s.append(memSn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		param = s.toString();
		fightRecordList = new ArrayList<MemMyFightInfo>();
	}
	
	public ArrayList<MemMyFightInfo> getFightRecordList() {
		return fightRecordList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getFights", param);
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
			JSONArray ja = jo.getJSONArray(RET_MEMBER_FIGHTS);
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.getJSONObject(i);
				MemMyFightInfo fightInfo = new MemMyFightInfo();
				fightInfo.myScoreInfo = obj.optString(RET_MY_SCORE_INFO, "-");
				fightInfo.courseName = obj.getString(RET_COURSE_NAME);
				fightInfo.teeTime = obj.getString(RET_TEE_TIME);
				fightInfo.palScoreInfo = obj.optString(RET_PAL_SCORE_INFO, "-");
				fightRecordList.add(fightInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
