package com.hylg.igolf.cs.request;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.protocol.HTTP;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;
import com.hylg.igolf.utils.Utils;

public class UploadImageRequest extends BaseRequest {
	
	private static final String TAG = "UploadImageRequest";
	public Bitmap bitmap;
	private String method;
	private String bodyName;
	
	private String imageFileName ;
	
	public ImageView okImageView;

	public ImageView deleteImageView;
	
	public ProgressBar progress;
	
	public ImageView selectedImage;
	
	public int type;

	private Context mContext ;

	private Uri uri;
	
	public UploadImageRequest(Context context, Uri uri, int type, String method, String bodyName,
								String fileName,ImageView okImage,ImageView deleteImage,ProgressBar progress,ImageView selectedImage) {
		super(context);

		mContext = context;
		this.type = type;
		this.method = method;
		this.bodyName = bodyName;
		this.imageFileName = fileName;
		this.progress = progress;
		this.okImageView = okImage;
		this.deleteImageView = deleteImage;
		this.selectedImage = selectedImage;
		this.uri = uri;
	}
	
//	public UploadImageRequest(Context context, Bitmap bitmap, int type,String fileName) {
//		super(context);
//		this.bitmap = bitmap;
//		this.imageFileName = fileName;
//		//this.progress = progress;
//		//this.okImageView = okImage;
//		//this.selectedImage = selectedImage;
//		this.type = type;
//		
//		if (Const.ID_BACK== type || Const.ID_FRONT== type) {
//			
//			method = "recieveIdCard";
//			bodyName= "idCards";
//			
//		} else if (Const.GRADUATE== type || Const.CERTIFICATE== type|| Const.AWARD== type) {
//			
//			method = "recieveOtherCards";
//			bodyName= "otherCards";
//		}
//		
//	}
	
	@Override
	public String getRequestUrl() {
		return null;
	}
	
	@Override
	public int connectUrl() {
		try {
			String url_str = UPLOAD_PIC_PATH + method ;
			
			DebugTools.getDebug().debug_v(TAG, "上传图片路径-----》》》》"+url_str);
			
			String end ="\r\n";
			String twoHyphens ="--";
			String boundary ="*****";
	        URL url = new URL(url_str);
	        HttpURLConnection con = (HttpURLConnection)url.openConnection();
	        con.setDoInput(true);
	        con.setDoOutput(true);
	        con.setUseCaches(false);
	        con.setRequestMethod("POST");
	        con.setRequestProperty(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);          
			con.setRequestProperty("Charset", HTTP.UTF_8);          
			con.setRequestProperty(HTTP.CONTENT_TYPE, "multipart/form-data;boundary=" + boundary);

			bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);

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

			bitmap = result;


			DataOutputStream ds = new DataOutputStream(con.getOutputStream()); 
			ds.writeBytes(twoHyphens + boundary + end);          
			ds.writeBytes("Content-Disposition:form-data;"+ "name=\""+bodyName+"\";filename=\""+imageFileName+"\""+ end);
			ds.writeBytes(end);
			ds.write(out.toByteArray());
			ds.writeBytes(end); 
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);          
			ds.flush();
			
	        InputStream input = con.getInputStream();

	        parseBody(input);
	        
			ds.close();
			input.close();
			con.disconnect();

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
		
		String sb = transferIs2String(is);
		
		try {
			JSONObject jo = new JSONObject(sb);
			
			DebugTools.getDebug().debug_v(TAG, "上传图片的结果-----》》》"+jo);
			
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			failMsg = jo.optString(RET_MSG);
			if(REQ_RET_OK != rn) {
				return rn;
			}

		} catch (Exception e) {
			e.printStackTrace();
			failMsg = context.getString(R.string.str_toast_image_update_fail);
			if(Utils.LOG_H) failMsg += "REQ_RET_F_JSON_EXCEP";
			return REQ_RET_F_JSON_EXCEP;
		}
		
		return REQ_RET_OK;
	}

}
