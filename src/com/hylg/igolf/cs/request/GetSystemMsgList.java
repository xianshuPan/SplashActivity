package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.cs.data.SysMsgInfo;

public class GetSystemMsgList extends BaseRequest {
	private String param;
	private ArrayList<SysMsgInfo> sysMsgList = null;

	public GetSystemMsgList(Context context, String sn, int pageNum, int pageSize) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		param = s.toString();
	}
	
	public ArrayList<SysMsgInfo> getSystemMsgList() {
		return sysMsgList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getSystemMsgList", param);
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
			sysMsgList = new ArrayList<SysMsgInfo>();
			JSONArray ja = jo.getJSONArray(RET_SYSTEM_MSG);
			for(int i=0, len=ja.length(); i<len; i++) {
				JSONObject obj = ja.getJSONObject(i);
				SysMsgInfo msgInfo = new SysMsgInfo();
				msgInfo.title = obj.getString(RET_TITLE);
				msgInfo.status = obj.getString(RET_STATUS);
				msgInfo.id = obj.getLong(RET_ID);
				msgInfo.sendTimestamp = obj.getString(RET_SEND_TIMESTAMP);
				sysMsgList.add(msgInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
