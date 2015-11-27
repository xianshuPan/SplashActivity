package com.hylg.igolf.cs.request;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.imagepicker.Config;
import com.hylg.igolf.utils.Utils;

public class FriendMessageNewPictureUpload  extends Thread {

	private static final String TAG = "FriendMessageNewPictureUpload";
	
	//private String sn = "";
	//private String name = "";
	private String path= "";

	private FriendTipsPicUploadCallback callBack = null;
	
	public static final String UPLOAD_PIC_PATH = BaseRequest.UPLOAD_PIC_PATH+"recievePic";
	
	//private FriendHotItem	mFriendMessageNewItem   = null;
	
	public FriendMessageNewPictureUpload(Context context,String path_input,FriendTipsPicUploadCallback callBack) {

		path = path_input;
		this.callBack = callBack;
	}
	
	
	
	public int connectUrl() {

		if (path == null || path.length() <= 0) {

			return 2;
		}

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

			//if (Config.drr != null  && Config.drr.size() > 0) {
				         
	    		byte[] buffer =new byte[10*1024];  
	    		
	    		FileInputStream fStream = null;

				//for (int i=0 ; i < Config.drr.size(); i++) {

					//String path = Config.drr.get(i);

					Bitmap bitmap = BitmapFactory.decodeFile(path);

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);

					float zoom = (float)Math.sqrt(128 * 1024 / (float)out.toByteArray().length);

					Matrix matrix = new Matrix();
					matrix.setScale(zoom, zoom);

					Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

					result.compress(Bitmap.CompressFormat.JPEG, 85, out);

					while(out.toByteArray().length > 128 * 1024){
						System.out.println(out.toByteArray().length);
						matrix.setScale(0.9f, 0.9f);
						result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
						out.reset();
						result.compress(Bitmap.CompressFormat.JPEG, 85, out);
					}

					
					String fileName = path.substring(path.lastIndexOf("/"), path.length());

					out.toByteArray();
					
					//fStream = new FileInputStream(path);
					
					ds.writeBytes(twoHyphens + boundary + end);          
					ds.writeBytes("Content-Disposition:form-data;"+ "name=\"imagesData\";filename=\""+fileName+"\""+ end);
					ds.writeBytes(end);
					
//					int length =-1;
//					while ((length = out.read(buffer)) !=-1) {
//
//		    			ds.write(buffer, 0, length);
//		    		}

					ds.write(out.toByteArray());


					//if (i < Config.drr.size()-1) {
						
						//ds.writeBytes(end + twoHyphens + boundary + end);
					//}

				//ds.writeBytes(end+twoHyphens + boundary + end);

				//}
				
				//fStream.close();
			//}
			
			ds.writeBytes(end+twoHyphens + boundary + twoHyphens + end);
			
			ds.flush();
			
	        InputStream input = con.getInputStream();
	        parseBody(input);
	       
			ds.close();
			input.close();
			con.disconnect();
			
			DebugTools.getDebug().debug_v(TAG, "上传图片成功法？、、？？？？？？？？？");

		} catch (Exception e) {
			e.printStackTrace();
			
			DebugTools.getDebug().debug_v(TAG, "上传图片失败法？、、？？？？？？？？？");

			callBack.callBack(1,"exception");
			return 1;
		}

		return 0;
	}

	public int parseBody(InputStream is) {
		try {
			
			String str = Utils.transferIs2String(is);
			
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("", "朋友圈上传图片的结果 ----->>>"+jo);
			
			int rn = jo.optInt("result", 1);

			String msg = jo.optString("msg");

			callBack.callBack(rn,msg);
			
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


	public interface FriendTipsPicUploadCallback {
		void callBack(int retId, String msg);
	}
}
