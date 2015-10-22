package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;


import org.json.JSONObject;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.RivalData;

import android.content.Context;


public class RateGolferAll extends BaseRequest {
	private String param;
	private RivalData rivalData;

	public RateGolferAll(Context context, String appSn, ArrayList<String> sn, int[] rate) {
		super(context);
		rivalData = new RivalData();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_APP_SN); s.append(KV_CONN); s.append(appSn);
		s.append(PARAM_CONN);
		
		StringBuilder snStr = new StringBuilder();
		StringBuilder rateStr = new StringBuilder();
		
		if (sn != null && sn.size() > 0 && rate != null && rate.length > 0) {
			
			
			for (int i=0 ; i < sn.size() ; i++) {
				
				snStr.append(sn.get(i));
				snStr.append(",");
				
				rateStr.append(rate[i]);
				rateStr.append(",");
				
			}
		}
		
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(snStr);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_RATE); s.append(KV_CONN); s.append(rateStr);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("rateHallGolfer02", param);
	}
	
	public RivalData getRivalData() {
		return rivalData;
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
			
			DebugTools.getDebug().debug_v("rateHallGolfer02", "----->>>>"+jo);
			rivalData.rivalRate = jo.optInt(RET_RIVAL_RATE);
			rivalData.rivalScore = jo.optInt(RET_RIVAL_SCORE);
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
