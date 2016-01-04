package com.hylg.igolf.cs.request;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.protocol.HTTP;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.data.RivalData;
import com.hylg.igolf.imagepicker.Config;
import com.xc.lib.view.ToggleButton;

public class FriendMessageNew extends BaseRequest {

	private static final String TAG = "FriendMessageNew";
	
	private String sn = "";
	private String name = "";
	private String content = "";
	
	private FriendHotItem	mFriendMessageNewItem   = null;
	
	public FriendMessageNew(Context context, FriendHotItem item) {
		super(context);
		
		this.sn = item.sn;
		this.name = item.name;
		this.content = item.content;
		
		mFriendMessageNewItem = item;
	}
	
	
	@Override
	public String getRequestUrl() {
		return null;
	}
	
	@Override
	public int connectUrl() {
		try {
			String url_str = SERVER_PATH + "releaseTips" ;
			//Utils.logh(TAG, "connectUrl method: " + method + " param: " + param);
			
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
	        //con.setRequestProperty("Charset", "UTF-8"); 
	        
			con.setRequestProperty(HTTP.CONTENT_TYPE,"multipart/form-data;boundary="+boundary);
			
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());

			ds.writeBytes(twoHyphens + boundary + end);

			if (mFriendMessageNewItem != null && mFriendMessageNewItem.localImageURL != null && mFriendMessageNewItem.localImageURL.size() > 0) {
				         
	   
	    		StringBuilder ab = new StringBuilder();
				for (int i=0 ; i < mFriendMessageNewItem.localImageURL.size(); i++) {
					
					String path = mFriendMessageNewItem.localImageURL.get(i);
					
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
			
			      
			ds.writeBytes("Content-Disposition:form-data;"+ "name=\"sn\""+ end+ end);
			ds.write(sn.getBytes("utf-8"));
			ds.writeBytes(end+twoHyphens + boundary + end); 
			
			ds.writeBytes("Content-Disposition:form-data;"+ "name=\"name\""+ end+ end);
			ds.write(name.getBytes("utf-8"));
			ds.writeBytes(end+twoHyphens + boundary + end); 
			
			ds.writeBytes("Content-Disposition:form-data;"+ "name=\"content\""+ end+ end);
			ds.write(content.getBytes("utf-8"));
			ds.writeBytes(end+twoHyphens + boundary  + end); 
			
			ds.writeBytes("Content-Disposition:form-data;"+ "name=\"provence\""+ end+ end);
			ds.write(mFriendMessageNewItem.provence.getBytes("utf-8"));
			ds.writeBytes(end+twoHyphens + boundary + end); 
			
			ds.writeBytes("Content-Disposition:form-data;"+ "name=\"city\""+ end+ end);
			ds.write(mFriendMessageNewItem.city.getBytes("utf-8"));
			ds.writeBytes(end+twoHyphens + boundary  + end); 
			
			ds.writeBytes("Content-Disposition:form-data;"+ "name=\"region\""+ end+ end);
			ds.write(mFriendMessageNewItem.region.getBytes("utf-8"));
			ds.writeBytes(end+twoHyphens + boundary  + end); 
			
			ds.writeBytes("Content-Disposition:form-data;"+ "name=\"alias\""+ end+ end);
			ds.write(mFriendMessageNewItem.alais.getBytes("utf-8"));
			ds.writeBytes(end+twoHyphens + boundary  + end); 
			
			ds.writeBytes("Content-Disposition:form-data;"+ "name=\"detail\""+ end+ end);
			ds.write(mFriendMessageNewItem.detail.getBytes("utf-8"));
			ds.writeBytes(end+twoHyphens + boundary + twoHyphens + end); 
			
			ds.flush();
			
	        InputStream input = con.getInputStream();
	        parseBody(input);
	       
			ds.close();
			input.close();
			con.disconnect();
			
			

//			new LocalMemory().saveDrawable(Drawable.createFromStream(is, srcName), getLocalFileName());
		} catch (IOException e) {
			e.printStackTrace();
//			failMsg = context.getString(R.string.str_toast_image_update_fail);
//			if(Utils.LOG_H) {
//				failMsg += "REQ_RET_F_UPDATE_AVATAR_FAIL";
//			}
			return REQ_RET_F_UPDATE_AVATAR_FAIL;
//			return REQ_RET_OK; // 异常，但实际上传成功
		}

		return REQ_RET_OK;
	}

	@Override
	public int parseBody(InputStream is) {
		try {
			
			String str = transferIs2String(is);
			
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("", "friend message new ----->>>"+jo);
			
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

	public interface getRivalDataCallback {
		void getRivalData(RivalData rivalData);
	}
}
