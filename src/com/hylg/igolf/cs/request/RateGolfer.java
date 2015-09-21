package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.hylg.igolf.cs.data.RivalData;

import android.content.Context;


public class RateGolfer extends BaseRequest {
	private String param;
	private RivalData rivalData;

	public RateGolfer(Context context, String appSn, String sn, int rate) {
		super(context);
		rivalData = new RivalData();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_APP_SN); s.append(KV_CONN); s.append(appSn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_RATE); s.append(KV_CONN); s.append(rate);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("rateGolfer", param);
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
			rivalData.rivalRate = jo.optInt(RET_RIVAL_RATE);
			rivalData.rivalScore = jo.optInt(RET_RIVAL_SCORE);
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
