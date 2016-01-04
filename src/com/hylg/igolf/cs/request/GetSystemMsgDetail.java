package com.hylg.igolf.cs.request;

import java.io.InputStream;


import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.cs.data.SysMsgDetail;
import com.hylg.igolf.utils.Utils;

public class GetSystemMsgDetail extends BaseRequest {
	private String param;
	private SysMsgDetail detail = null;

	public GetSystemMsgDetail(Context context, String id) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_ID); 
		s.append(KV_CONN);
		s.append(id);
		param = s.toString();
	}
	
	public SysMsgDetail getSysMsgDetail() {
		return detail;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getSystemMsgDetail", param);
	}

	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			Utils.logh("GetSystemMsgDetail", rn+"");
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			String content = jo.optString(RET_CONTENT);
			detail = new SysMsgDetail();
			detail.content = content;
			
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
