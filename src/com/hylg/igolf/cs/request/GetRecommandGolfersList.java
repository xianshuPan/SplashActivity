package com.hylg.igolf.cs.request;

import android.content.Context;
import android.content.pm.PackageManager;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.GolferInfo;
import com.hylg.igolf.ui.reqparam.GetGolfersReqParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class GetRecommandGolfersList extends BaseRequest {
	private String param;
	private ArrayList<GolferInfo> golfersList = null;

	public GetRecommandGolfersList(Context context, GetGolfersReqParam data) {
		super(context);
		golfersList = new ArrayList<GolferInfo>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(data.sn);
		s.append(PARAM_CONN);
		s.append("state"); s.append(KV_CONN); s.append(data.region);
		s.append(PARAM_CONN);
		s.append("apiVersion"); s.append(KV_CONN); s.append(200);
		s.append(PARAM_CONN);
		s.append("appVersion"); s.append(KV_CONN); try {
			s.append(context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
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
		return getReqParam("getReferees", param);
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
			
			DebugTools.getDebug().debug_v("getReferees", "------->>>>"+jo);
			
			JSONArray ja = jo.optJSONArray("referee");
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.optJSONObject(i);
				GolferInfo golfer = new GolferInfo();
				golfer.sn = obj.optString(RET_SN);
				golfer.nickname = obj.optString(RET_NICKNAME);
				golfer.avatar = obj.optString(RET_AVATAR);
				golfer.sex = obj.optInt(RET_SEX);
				golfer.yearsExp = obj.optInt(RET_YEAR_EXP);
				golfer.industry = obj.optString(RET_INDUSTRY);
				golfer.rate = obj.optDouble(RET_RATE);
				golfer.ratedCount = obj.optInt(RET_RATE_COUNT);
				golfer.region = obj.optString(RET_CITY);
				// do not occur in real data
				if(null == golfer.region || golfer.region.trim().length() == 0) {
					golfer.region = obj.optString(RET_STATE);
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
