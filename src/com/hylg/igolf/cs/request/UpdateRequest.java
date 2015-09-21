package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.VersionInfo;

public class UpdateRequest extends BaseRequest {
	private String param;
	private VersionInfo versionInfo;

	public UpdateRequest(Context context) {
		super(context);
		StringBuilder s = new StringBuilder();
		param = s.toString();
		versionInfo = new VersionInfo();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("checkUpdate", param);
	}
	
	public VersionInfo getVersionInfo(){
		return versionInfo;
	}

	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("版本检查", "------》》》"+jo);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			versionInfo.version = jo.getInt(RET_VERSION);
			versionInfo.updateType = jo.getInt(RET_UPDATE_TYPE);
			versionInfo.updateInfo = jo.getString(RET_UPDATE_INFO);
			versionInfo.region = jo.getInt(RET_REGION);
			versionInfo.industry = jo.getInt(RET_INDUSTRY);
			versionInfo.downLoadUrl = jo.optString("downLoadUrl");
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
