package com.hylg.igolf.cs.request;

import android.content.Context;
import android.content.pm.PackageManager;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.ui.customer.ContactEntity;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SendMsgToFriend extends BaseRequest {
	private String param;

	public ArrayList<HashMap<String,String>> registedPerson;

	public ArrayList<ContactEntity> contactList;


	public SendMsgToFriend(Context context, String phones, String sn,int type,long appId) {
		super(context);

		StringBuilder s = new StringBuilder();
		s.append("phoneNumbers"); s.append(KV_CONN); s.append(phones);
		s.append(PARAM_CONN);
		s.append("appVersion"); s.append(KV_CONN);
		try {
			s.append(context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		s.append(PARAM_CONN);
		s.append("apiVersion"); s.append(KV_CONN); s.append(200);
		s.append(PARAM_CONN);

		s.append("type"); s.append(KV_CONN); s.append(type);
		s.append(PARAM_CONN);

		s.append("appId"); s.append(KV_CONN); s.append(appId);
		s.append(PARAM_CONN);
		s.append("sn"); s.append(KV_CONN); s.append(sn);

		
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {

		return getReqParam2("sendInvitationToSelected", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {

			DebugTools.getDebug().debug_v("sendInvitationToSelected", "sendInvitationToSelected------->>>>>" + str);
			JSONObject jo = new JSONObject(str);

			int rn = jo.optInt("result", REQ_RET_FAIL);

			//if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				return rn;
			//}


		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		//return REQ_RET_OK;
	}

}
