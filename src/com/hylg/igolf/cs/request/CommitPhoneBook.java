package com.hylg.igolf.cs.request;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.ui.customer.ContactEntity;
import com.hylg.igolf.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CommitPhoneBook extends BaseRequest {
	private String param;

	public ArrayList<HashMap<String,String>> registedPerson;

	public ArrayList<ContactEntity> contactList;

	private Context contex;

	public CommitPhoneBook(Context context, String phones, String sn) {
		super(context);

		StringBuilder s = new StringBuilder();
		s.append("phoneMembers"); s.append(KV_CONN); s.append(phones);
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
		s.append("sn"); s.append(KV_CONN); s.append(sn);

		contex = context;
		
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {

		return getReqParam2("savePhoneBook", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);


		Toast.makeText(contex, str, Toast.LENGTH_SHORT).show();

		try {

			DebugTools.getDebug().debug_v("savePhoneBook", "str------->>>>>"+str);
			JSONObject jo = new JSONObject(str);

			DebugTools.getDebug().debug_v("savePhoneBook", "------->>>>>"+jo);
			int rn = jo.optInt("result", REQ_RET_FAIL);

			//if(REQ_RET_OK != rn) {
				failMsg = jo.optString(RET_MSG);
				//return rn;
			//}

			JSONArray registedPersonJson = jo.optJSONArray("registedPerson");

			if (registedPersonJson != null && registedPersonJson.length() > 0) {

				registedPerson = new ArrayList<HashMap<String, String>>();
				for (int i = 0 ;i < registedPersonJson.length() ;i++) {

					HashMap<String, String> hash = new HashMap<String, String>();
					JSONObject registedPersonJsonItem = registedPersonJson.optJSONObject(i);

					hash.put("phone",registedPersonJsonItem.optString("phone"));
					hash.put("name",registedPersonJsonItem.optString("name"));
					hash.put("sn",registedPersonJsonItem.optString("sn"));
					hash.put("nickName",registedPersonJsonItem.optString("nickName"));
					hash.put("userId",registedPersonJsonItem.optString("userId"));

					registedPerson.add(hash);

				}

			}

			JSONArray unregistedPersonJson = jo.optJSONArray("unRegistedPerson");

			if (unregistedPersonJson != null && unregistedPersonJson.length() > 0) {

				ArrayList<ContactEntity> contactList1 = new ArrayList<ContactEntity>();
				for (int i = 0 ;i < unregistedPersonJson.length() ;i++) {

					ContactEntity entity = new ContactEntity();
					JSONObject registedPersonJsonItem = unregistedPersonJson.optJSONObject(i);

					entity.phone = registedPersonJsonItem.optString("phone");
					entity.userName = registedPersonJsonItem.optString("memberName");
					entity.lable = Utils.getPinYinHeadChar(entity.userName);

					contactList1.add(entity);

				}

				contactList = new ArrayList<ContactEntity>();
				for (int i = 0 ;i < contactList1.size() ;i++) {

					ContactEntity entity1 = contactList1.get(i);


					if (entity1!= null && contactList != null && contactList.size() > 0){

						boolean result1,result2,result3 = false;
						for (int j = 0 ;j < contactList.size() ;j++) {

							 result1 = entity1.getSortkey() >= contactList.get(j).getSortkey();

							if (!result1) {

								contactList.add(0,entity1);
								break;
							}

							result2 = j+1 < contactList.size();

							if (result2 ) {

								result3 = entity1.getSortkey() < contactList.get(j+1).getSortkey();
							}


							if (result1 && result2 && result3) {

								contactList.add(j+1,entity1);

								break;
							}

							if (j+1 >= contactList.size()) {

								contactList.add(contactList.size(),entity1);
								break;
							}



						}

					}
					else {

						contactList.add(entity1);
					}

				}

			}


		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
