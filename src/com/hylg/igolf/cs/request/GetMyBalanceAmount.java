package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.MyBalanceRecordInfo;

import org.json.JSONArray;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class GetMyBalanceAmount extends BaseRequest {
	private String param;

	private double balance;

	public String card_no;

	public GetMyBalanceAmount(Context context, long id) {
		super(context);

		StringBuilder s = new StringBuilder();
		s.append("custid"); s.append(KV_CONN); s.append(id);
		
		param = s.toString();
	}
	
	public double getMyBalance() {

		return balance;
	}
	
	@Override
	public String getRequestUrl() {

		return getReqParam2("getBalance", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);

			DebugTools.getDebug().debug_v("myBalanceAmount----->>>", "------->>>>>"+jo);
			int rn = jo.optInt("result", REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}

			balance = jo.optDouble("balance");

			card_no = jo.optString("union_num");

		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
