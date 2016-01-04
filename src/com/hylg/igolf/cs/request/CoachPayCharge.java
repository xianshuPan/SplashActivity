package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;

public class CoachPayCharge extends BaseRequest {
	private String param;
	
	public String charge;

	public CoachPayCharge(Context context, long id,String pay_chanel) {
		super(context);
		StringBuilder s = new StringBuilder();
		s.append("appid"); s.append(KV_CONN); s.append(id);s.append(PARAM_CONN);
		
		s.append("channel"); s.append(KV_CONN); s.append(pay_chanel);s.append(PARAM_CONN);
		
		s.append("client_ip"); s.append(KV_CONN); s.append("192.168.2.102");
		
		param = s.toString();

	}
	
	@Override
	public String getRequestUrl() {
		
		return getReqParam("coachPay", param);
		
	}

	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("获取支付信息", "----->>>"+jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			
			JSONObject chargeJson = jo.optJSONObject("charge");
			
			if (chargeJson != null) {
				
				charge = chargeJson.toString();
			}
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		
		MainApp.getInstance().getGlobalData().setHasStartNewInvite(true);
		return REQ_RET_OK;
	}

}
