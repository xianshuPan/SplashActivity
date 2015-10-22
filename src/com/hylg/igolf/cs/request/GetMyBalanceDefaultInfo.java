package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.MyBalanceRecordInfo;


import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class GetMyBalanceDefaultInfo extends BaseRequest {
	private String param;

	private ArrayList<MyBalanceRecordInfo> myBalanceRecordList = null;

	public GetMyBalanceDefaultInfo(Context context, long id) {
		super(context);
		myBalanceRecordList = new ArrayList<MyBalanceRecordInfo>();
		StringBuilder s = new StringBuilder();
		s.append("custid"); s.append(KV_CONN); s.append(id);
		
		param = s.toString();
	}
	
	public ArrayList<MyBalanceRecordInfo> getMyBalanceRecordList() {

		return myBalanceRecordList;
	}
	
	@Override
	public String getRequestUrl() {

		return getReqParam2("getWithdrwalDefaultInfo", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);

			DebugTools.getDebug().debug_v("getWithdrwalDefaultInfo----->>>", "------->>>>>"+jo);
			int rn = jo.optInt("result", REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
