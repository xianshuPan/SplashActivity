package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.utils.GlobalData;

import org.json.JSONObject;

import java.io.InputStream;


public class CommitFeedback extends BaseRequest {
	private String param;

	public CommitFeedback(Context context, String sn,String content) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append("content"); s.append(KV_CONN); s.append(content);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("feedback", param);
	}

	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {  
			JSONObject jo = new JSONObject(str);

			DebugTools.getDebug().debug_v("feedback","----->>>"+jo);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
