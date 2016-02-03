package com.hylg.igolf.cs.request;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.RivalData;
import com.hylg.igolf.ui.customer.ContactEntity;
import com.hylg.igolf.utils.Base64;
import com.hylg.igolf.utils.Utils;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SavePhoneBook {

	private static final String TAG = "FriendMessageNew";

	public String failMsg = "";

	private String param;

	public static final String SERVER_PATH = BaseRequest.SERVER_PATH;


	public ArrayList<HashMap<String,String>> registedPerson;

	public ArrayList<ContactEntity> contactList;

	public SavePhoneBook(Context context, String phones, String sn) {


		StringBuilder s = new StringBuilder();
		s.append("phoneMembers"); s.append("="); s.append(phones);
		s.append("&");
		s.append("appVersion"); s.append("=");
		try {
			s.append(context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		s.append("&");
		s.append("apiVersion"); s.append("="); s.append(200);
		s.append("&");
		s.append("sn"); s.append("="); s.append(sn);

		//contex = context;

		param = s.toString();
		try {

			String path = Environment.getExternalStorageDirectory()+"/savePHoneBoookParam.txt";
			FileOutputStream fis = new FileOutputStream(path);

			byte[] sdfsd= param.getBytes();
			fis.write(sdfsd);

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
	

	public int connectUrl() {
		try {
			String url_str = SERVER_PATH + "savePhoneBook" ;
			
			String end ="\r\n";
			String twoHyphens ="--";
			String boundary ="----WebKitFormBoundaryQq8qE09gQABIfkff";
	        URL url = new URL(url_str);
	        HttpURLConnection con = (HttpURLConnection)url.openConnection();
	        con.setDoInput(true);
	        con.setDoOutput(true);
	        con.setUseCaches(false);
	        con.setRequestMethod("POST");
	        con.setRequestProperty(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);   
			con.setRequestProperty("Charset", HTTP.UTF_8);
	        
			con.setRequestProperty(HTTP.CONTENT_TYPE, "multipart/form-data;boundary=" + boundary);
			
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());

			ds.writeBytes(twoHyphens + boundary + end);
			
			ds.writeBytes("Content-Disposition:form-data;" + "name=\"param\"" + end + end);

			ds.write(Base64.encode(param.getBytes()).getBytes("utf-8"));

			ds.writeBytes(end + twoHyphens + boundary + twoHyphens + end);
			
			ds.flush();
			
	        InputStream input = con.getInputStream();
	        parseBody(input);
	       
			ds.close();
			input.close();
			con.disconnect();
			
			return 0;

		} catch (IOException e) {
			e.printStackTrace();

			return 1;
		}


	}

	public int parseBody(InputStream is) {
		String str = Utils.transferIs2String(is);


		//Toast.makeText(contex, str, Toast.LENGTH_SHORT).show();

		try {

			DebugTools.getDebug().debug_v("savePhoneBook", "str------->>>>>"+str);
			JSONObject jo = new JSONObject(str);

			DebugTools.getDebug().debug_v("savePhoneBook", "------->>>>>"+jo);
			int rn = jo.optInt("result", -1);

			failMsg = jo.optString("msg");

			if(0 != rn) {

				return rn;
			}

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
			return 1;
		}
		return 0;
	}

	public interface getRivalDataCallback {
		void getRivalData(RivalData rivalData);
	}
}
