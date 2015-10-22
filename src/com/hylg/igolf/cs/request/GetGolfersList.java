package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.GolferInfo;
import com.hylg.igolf.ui.reqparam.GetGolfersReqParam;

public class GetGolfersList extends BaseRequest {
	private String param;
	private ArrayList<GolferInfo> golfersList = null;

	public GetGolfersList(Context context, GetGolfersReqParam data) {
		super(context);
		golfersList = new ArrayList<GolferInfo>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(data.sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_LABEL); s.append(KV_CONN); s.append(data.label);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_REGION); s.append(KV_CONN); s.append(data.region);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_SEX); s.append(KV_CONN); s.append(data.sex);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_INDUSTRY); s.append(KV_CONN); s.append(data.industry);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(data.pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(data.pageSize);
		param = s.toString();
	}
	
	public ArrayList<GolferInfo> getGolfersList() {
		return golfersList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getGolfersList", param);
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
			
			DebugTools.getDebug().debug_v("golferlist", "------->>>>"+jo);
			
			JSONArray ja = jo.getJSONArray(RET_GOLFERS_LIST);
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.getJSONObject(i);
				GolferInfo golfer = new GolferInfo();
				golfer.sn = obj.getString(RET_SN);
				golfer.nickname = obj.getString(RET_NICKNAME);
				golfer.avatar = obj.getString(RET_AVATAR);
				golfer.sex = obj.getInt(RET_SEX);
				golfer.yearsExp = obj.getInt(RET_YEAR_EXP);
				golfer.industry = obj.getString(RET_INDUSTRY);
				golfer.rate = obj.getDouble(RET_RATE);
				golfer.ratedCount = obj.getInt(RET_RATE_COUNT);
				golfer.region = obj.getString(RET_CITY);
				// do not occur in real data
				if(null == golfer.region || golfer.region.trim().length() == 0) {
					golfer.region = obj.getString(RET_STATE);
				}
				golfer.handicapIndex = obj.optDouble(RET_HANDICAP_INDEX, Double.MAX_VALUE);
				golfersList.add(golfer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
