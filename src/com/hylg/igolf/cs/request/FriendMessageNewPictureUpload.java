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
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.utils.Utils;

public class FriendMessageNewPictureUpload  extends Thread {

	private static final String TAG = "FriendMessageNewPictureUpload";
	
	//private String sn = "";
	//private String name = "";
	//private String content = "";
	
	public static final String UPLOAD_PIC_PATH = BaseRequest.UPLOAD_PIC_PATH+"recievePic";
	
	//private FriendHotItem	mFriendMessageNewItem   = null;
	
	public FriendMessageNewPictureUpload(Context context) {
		
//		this.sn = item.sn;
//		this.name = item.name;
//		this.content = item.content;
//		
//		mFriendMessageNewItem = item;
	}
	
	
	
	public int connectUrl() {
		try {
			
			DebugTools.getDebug().debug_v(TAG, "上传图片kaishi法？、、？？？？？？？？？");
			
			String end ="\r\n";
			String twoHyphens ="--";
			String boundary ="----WebKitFormBoundaryQq8qE09gQABIfkff";
	        URL url = new URL(UPLOAD_PIC_PATH);
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
			
			if (Config.drr != null  && Config.drr.size() > 0) {
				         
	    		byte[] buffer =new byte[10*1024];  
	    		
	    		FileInputStream fStream = null;
	    		
				for (int i=0 ; i < Config.drr.size(); i++) {
					
					String path = Config.drr.get(i);
					
					String fileName = path.substring(path.lastIndexOf("/"), path.length());
					
					fStream = new FileInputStream(path);
					
					ds.writeBytes(twoHyphens + boundary + end);          
					ds.writeBytes("Content-Disposition:form-data;"+ "name=\"imagesData\";filename=\""+fileName+"\""+ end);
					ds.writeBytes(end);
					
					int length =-1;   
					while ((length = fStream.read(buffer)) !=-1) {
						   
		    			ds.write(buffer, 0, length);    
		    		}       
					
					if (i < Config.drr.size()-1) {
						
						ds.writeBytes(end+twoHyphens + boundary + end);    
					}
					      
					
				}
				
				fStream.close();
			}
			
			ds.writeBytes(end+twoHyphens + boundary + twoHyphens + end); 
			
			ds.flush();
			
	        InputStream input = con.getInputStream();
	        parseBody(input);
	       
			ds.close();
			input.close();
			con.disconnect();
			
			DebugTools.getDebug().debug_v(TAG, "上传图片成功法？、、？？？？？？？？？");

		} catch (IOException e) {
			e.printStackTrace();
//			failMsg = context.getString(R.string.str_toast_image_update_fail);
//			if(Utils.LOG_H) {
//				failMsg += "REQ_RET_F_UPDATE_AVATAR_FAIL";
//			}
			
			e.printStackTrace();
			
			DebugTools.getDebug().debug_v(TAG, "上传图片失败法？、、？？？？？？？？？");
			return 1;
//			return REQ_RET_OK; // 异常，但实际上传成功
		};

		return 0;
	}

	public int parseBody(InputStream is) {
		try {
			
			String str = Utils.transferIs2String(is);
			
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("", "朋友圈上传图片的结果 ----->>>"+jo);
			
			//int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return 1;
		}
		return 0;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		connectUrl();
	}
}
