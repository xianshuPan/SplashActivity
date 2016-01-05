package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.data.RivalData;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.ui.friend.publish.PublishBean;
import com.hylg.igolf.utils.Utils;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FriendMessageNew2 {

	private static final String TAG = "FriendMessageNew";

	private String content = "";

	public static final String SERVER_PATH = BaseRequest.SERVER_PATH;

	private PublishBean	mFriendMessageNewItem   = null;

	public FriendMessageNew2( PublishBean item) {
		
		mFriendMessageNewItem = item;
	}
	

	public int connectUrl() {
		try {
			String url_str = SERVER_PATH + "releaseTips" ;
			
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
	        
			con.setRequestProperty(HTTP.CONTENT_TYPE,"multipart/form-data;boundary="+boundary);
			
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());

			ds.writeBytes(twoHyphens + boundary + end);

			if (mFriendMessageNewItem != null && mFriendMessageNewItem.getPics() != null && mFriendMessageNewItem.getPics().length > 0) {
				         
	   
	    		StringBuilder ab = new StringBuilder();
				for (int i=0 ; i < mFriendMessageNewItem.getPics().length; i++) {
					
					String path = mFriendMessageNewItem.getPics()[i];
					
					String fileName = path.substring(path.lastIndexOf("/")+1, path.length());
					
					ab.append(fileName);
					ab.append(",");

				}

				ds.writeBytes("Content-Disposition:form-data;"+ "name=\"imgNames\""+ end);
				ds.writeBytes(end);
				 
				String fileNames = ab.toString();
				ds.write(fileNames.getBytes("utf-8"));
				
				ds.writeBytes(end); 
				ds.writeBytes(twoHyphens + boundary + end);  
				
			}
			
			      
			ds.writeBytes("Content-Disposition:form-data;" + "name=\"sn\"" + end + end);
			if (mFriendMessageNewItem.sn != null) {

				ds.write(mFriendMessageNewItem.sn.getBytes("utf-8"));
			}
			else {

				ds.write("".getBytes("utf-8"));
			}
			ds.writeBytes(end+twoHyphens + boundary + end); 

			ds.writeBytes("Content-Disposition:form-data;" + "name=\"name\"" + end + end);
			if (mFriendMessageNewItem.name != null) {

				ds.write(mFriendMessageNewItem.name.getBytes("utf-8"));
			}
			else {

				ds.write("".getBytes("utf-8"));
			}
			ds.writeBytes(end+twoHyphens + boundary + end); 
			
			ds.writeBytes("Content-Disposition:form-data;" + "name=\"content\"" + end + end);
			if (mFriendMessageNewItem.getText() != null) {

				ds.write(mFriendMessageNewItem.getText().getBytes("utf-8"));
			}
			else {

				ds.write("".getBytes("utf-8"));
			}
			ds.writeBytes(end+twoHyphens + boundary  + end); 
			
			ds.writeBytes("Content-Disposition:form-data;" + "name=\"provence\"" + end + end);
			if(mFriendMessageNewItem.province!=null) {

				ds.write(mFriendMessageNewItem.province.getBytes("utf-8"));
			}
			else {

				ds.write("".getBytes("utf-8"));
			}
			ds.writeBytes(end+twoHyphens + boundary + end); 
			
			ds.writeBytes("Content-Disposition:form-data;" + "name=\"city\"" + end + end);
			if (mFriendMessageNewItem.city != null) {

				ds.write(mFriendMessageNewItem.city.getBytes("utf-8"));
			}
			else {

				ds.write("".getBytes("utf-8"));
			}
			ds.writeBytes(end+twoHyphens + boundary  + end); 
			
			ds.writeBytes("Content-Disposition:form-data;" + "name=\"region\"" + end + end);
			if (mFriendMessageNewItem.region !=null) {

				ds.write(mFriendMessageNewItem.region.getBytes("utf-8"));
			}
			else {

				ds.write("".getBytes("utf-8"));
			}
			ds.writeBytes(end+twoHyphens + boundary  + end); 
			
			ds.writeBytes("Content-Disposition:form-data;" + "name=\"alias\"" + end + end);
			if (mFriendMessageNewItem.alias !=null) {

				ds.write(mFriendMessageNewItem.alias.getBytes("utf-8"));
			}
			else {

				ds.write("".getBytes("utf-8"));
			}
			ds.writeBytes(end+twoHyphens + boundary  + end); 
			
			ds.writeBytes("Content-Disposition:form-data;" + "name=\"detail\"" + end + end);
			if (mFriendMessageNewItem.detail != null) {

				ds.write(mFriendMessageNewItem.detail.getBytes("utf-8"));
			}
			else {

				ds.write("".getBytes("utf-8"));
			}
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
		try {
			
			String str = Utils.transferIs2String(is);
			
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("", "friend message new ----->>>"+jo);

			int resultCode = jo.optInt("result", 1);

			if (resultCode == 1) {

				throw new Exception();
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
