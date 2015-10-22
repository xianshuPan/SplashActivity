package com.hylg.igolf.cs.request;

import java.io.InputStream;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.utils.GlobalData;


public class GetTipsCount extends BaseRequest {
	private String param;
	
	private Context mContext;

	public GetTipsCount(Context context, String sn) {
		super(context);
		
		
		mContext = context;
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getTipsCount", param);
	}

	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {  
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("未读消息条数", "----->>>"+jo);
			
			//Toast.makeText(mContext, jo.toString(), Toast.LENGTH_SHORT).show();
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			//GlobalData gd = MainApp.getInstance().getGlobalData();
			
			int tipsAmount = jo.optInt("tipsAmount");
			MainApp.getInstance().getGlobalData().tipsAmount = tipsAmount;
			
			String tipsIds = jo.optString("tipsIds");
			MainApp.getInstance().getGlobalData().tipsIds = tipsIds;
			
			//Config.tipsIds = tipsIds;
			//Config.tipsAmount = tipsAmount;
			
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
