package com.hylg.igolf.cs.request;

import java.io.InputStream;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.MainApp;
import com.hylg.igolf.utils.GlobalData;


public class GetNewMsgCount extends BaseRequest {
	private String param;

	public GetNewMsgCount(Context context, String sn) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getNewMsgCount", param);
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
			GlobalData gd = MainApp.getInstance().getGlobalData();
			gd.msgNumSys = jo.getInt(RET_MSG_NUM_SYS);
			gd.msgNumInvite = jo.getInt(RET_MSG_NUM_INVITE);
			
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
