package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.MyBalanceRecordInfo;
import com.hylg.igolf.cs.data.MyFolloweInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;

public class GetMyBalanceRecordList extends BaseRequest {
	private String param;

	private ArrayList<MyBalanceRecordInfo> myBalanceRecordList = null;

	public GetMyBalanceRecordList(Context context, long id, int pageNum, int pageSize) {
		super(context);
		myBalanceRecordList = new ArrayList<MyBalanceRecordInfo>();
		StringBuilder s = new StringBuilder();
		s.append("custid"); s.append(KV_CONN); s.append(id);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		
		param = s.toString();
	}
	
	public ArrayList<MyBalanceRecordInfo> getMyBalanceRecordList() {

		return myBalanceRecordList;
	}
	
	@Override
	public String getRequestUrl() {

		return getReqParam2("getWithdrawlHistory", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			int rn = jo.optInt("result", REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			
			DebugTools.getDebug().debug_v("myBalanceRecordList----->>>", "------->>>>>"+jo);
			
			JSONArray ja = jo.getJSONArray("withdrawlHistory");

			if (ja == null || ja.length() <= 0) {

				return REQ_RET_F_NO_DATA;
			}

			long sdf = System.currentTimeMillis();
			
			for(int i=0, len=ja.length(); i<len; i++) {
				
				JSONObject obj = ja.getJSONObject(i);

				MyBalanceRecordInfo item = new MyBalanceRecordInfo();

				item.id = obj.optInt("id");
				item.custid = obj.optInt("customid");
				item.amount = obj.optDouble("amount");
				item.dealTime = obj.optLong("transactionTime");
				item.status = obj.optInt("status");
				item.type = obj.optInt("type");

				JSONObject from_who_obj = obj.optJSONObject("formWho");

				if (from_who_obj != null) {

					item.from_who = from_who_obj.optString("nickname");
				}

				myBalanceRecordList.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
