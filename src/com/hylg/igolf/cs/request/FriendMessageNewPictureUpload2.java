package com.hylg.igolf.cs.request;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.ui.friend.publish.UploadPictureResultBean;
import com.hylg.igolf.utils.Const;
import com.hylg.igolf.utils.Utils;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FriendMessageNewPictureUpload2  {

	private static final String TAG = "FriendMessageNewPictureUpload2";

	private String path= "";

	public static final String UPLOAD_PIC_PATH = BaseRequest.UPLOAD_PIC_PATH+"recievePic";

	UploadPictureResultBean resultBean;

	private int pic_size;

	private long time;

	public FriendMessageNewPictureUpload2(String path_input,long timing,int picSize) {

		path = path_input;

		pic_size = picSize;

		resultBean = new UploadPictureResultBean();

		time = timing;
	}
	
	
	
	public UploadPictureResultBean connectUrl() {

		if (path == null || path.length() <= 0) {

			return null;
		}

		try {
			
			DebugTools.getDebug().debug_v(TAG, "上传图片"+path);
			
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
	        
			con.setRequestProperty(HTTP.CONTENT_TYPE,"multipart/form-data;boundary="+boundary);
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());

			Bitmap bitmap = BitmapFactory.decodeFile(path);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);

			float zoom = (float)Math.sqrt(pic_size * 1024 / (float)out.toByteArray().length);
			Matrix matrix = new Matrix();
			matrix.setScale(zoom, zoom);

			Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			result.compress(Bitmap.CompressFormat.JPEG, 85, out);

			while(out.toByteArray().length > pic_size * 1024){
				System.out.println(out.toByteArray().length);
				matrix.setScale(0.9f, 0.9f);
				result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
				out.reset();
				result.compress(Bitmap.CompressFormat.JPEG, 85, out);
			}

		//	String fileName = path.substring(path.lastIndexOf("/"), path.length());

			String fileName = Utils.getBase64FileName(path,time);
			//out.toByteArray();

			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition:form-data;"+ "name=\"imagesData\";filename=\""+fileName+"\""+ end);
			ds.writeBytes(end);

			ds.write(out.toByteArray());
			ds.writeBytes(end+twoHyphens + boundary + twoHyphens + end);
			ds.flush();
			
	        InputStream input = con.getInputStream();
	        int resultCode = parseBody(input);

			if (resultCode == 0) {

				resultBean.setPic_id(fileName);
			}
			else {

				throw new Exception();
			}
	       
			ds.close();
			input.close();
			con.disconnect();
			
			DebugTools.getDebug().debug_v(TAG, "上传图片成功");

		} catch (Exception e) {
			e.printStackTrace();

			DebugTools.getDebug().debug_v(TAG, "上传图片失败");

			return resultBean;
		}

		return resultBean;
	}

	public int parseBody(InputStream is) {
		try {
			
			String str = Utils.transferIs2String(is);
			
			JSONObject jo = new JSONObject(str);
			
			DebugTools.getDebug().debug_v("", "朋友圈上传图片的结果 ----->>>"+jo);
			
			int rn = jo.optInt("result", 1);

			String msg = jo.optString("msg");
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return 1;
		}
		return 0;
	}



	public interface FriendTipsPicUploadCallback {
		void callBack(int retId, String msg);
	}
}
