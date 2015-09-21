package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.cs.data.RankingInfo;
import com.hylg.igolf.ui.reqparam.GetRankingReqParam;

public class GetRankingList extends BaseRequest {
	private String param;
	private ArrayList<RankingInfo> rankList = null;
	private RankingInfo myRank = null;

	public GetRankingList(Context context, GetRankingReqParam data) {
		super(context);
		rankList = new ArrayList<RankingInfo>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(data.sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_LOCATION); s.append(KV_CONN); s.append(data.region);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_SEX); s.append(KV_CONN); s.append(data.sex);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(data.pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(data.pageSize);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_TYPE); s.append(KV_CONN); s.append(data.type);
		param = s.toString();
	}
	
	public ArrayList<RankingInfo> getRankList() {
		return rankList;
	}
	
	public RankingInfo getMyRank() {
		return myRank;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getRankingList", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			failMsg = jo.optString(RET_MSG);
			if(REQ_RET_OK != rn
					// 用户不在榜单，但榜单列表存在
					&& REQ_RET_F_RANK_OUT_OF != rn && REQ_RET_F_RANK_NOT_FIT != rn) {
				return rn;
			}
			JSONObject mr = jo.optJSONObject("rankingOfMe");
			if(null != mr) {
				myRank = getRankInfo(mr);
			}
			JSONArray ja = jo.getJSONArray(RET_RANKING_LIST);
			for(int i=0, len=ja.length(); i<len; i++) {
				rankList.add(getRankInfo(ja.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}
	
	private RankingInfo getRankInfo(JSONObject obj) throws JSONException {
		RankingInfo ri = new RankingInfo();
		ri.id = obj.getLong(RET_ID);
		ri.sn = obj.getString(RET_SN);
		ri.rank = obj.getInt(RET_RANK);
		ri.nickname = obj.getString(RET_NICKNAME);
		ri.avatar = obj.getString(RET_AVATAR);
		ri.location = obj.getString(RET_CITY);
		ri.handicapIndex = obj.optDouble(RET_HANDICAP_INDEX, Double.MAX_VALUE);
		ri.matches = obj.getInt(RET_MATCHES);
		ri.best = obj.optInt(RET_BEST, Integer.MAX_VALUE);
		return ri;
	}

}
