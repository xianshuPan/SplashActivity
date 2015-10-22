package com.hylg.igolf.cs.request;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.protocol.HTTP;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;
import com.hylg.igolf.cs.data.RivalData;
import com.hylg.igolf.utils.Base64;
import com.hylg.igolf.utils.Utils;

public abstract class UpdateImageRequest extends BaseRequest {
	protected abstract String saveLocalFile(String imageName, Bitmap bitmap);
	protected abstract int getCompressQuality();
	private static final String TAG = "UpdateImageRequest";
	private Bitmap bitmap;
	private String method;
	private String param;
	private String imageName;
	private getRivalDataCallback rivalDataCallback;
	
	public UpdateImageRequest(Context context, Bitmap bitmap, String method, String param, getRivalDataCallback callback) {
		super(context);
		this.bitmap = bitmap;
		this.method = method;
		this.param = param;
		imageName = "";
		rivalDataCallback = callback;
	}
	
	public String getImageName() {
		return imageName;
	}
	
	@Override
	public String getRequestUrl() {
		return null;
	}
	
	@Override
	public int connectUrl() {
		try {
			String url_str = SERVER_PATH + method + "?" + PARAM +
					Base64.encode(param.getBytes());
			Utils.logh(TAG, "connectUrl method: " + method + " param: " + param);
			
			DebugTools.getDebug().debug_v(TAG, "上传积分卡-----》》》》"+url_str);
			
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
			con.setRequestProperty(HTTP.CONTENT_TYPE,"multipart/form-data;boundary="+boundary);
			
			ByteArrayOutputStream  baos = new ByteArrayOutputStream();
//			int quality = getCompressQuality();
//			if(quality < 30) {
//				quality = 30;
//			}
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			
			DataOutputStream ds = new DataOutputStream(con.getOutputStream()); 
			ds.writeBytes(twoHyphens + boundary + end);          
//			ds.writeBytes("Content-Disposition:form-data;"+ "sn=\""+sn+"\""+ end);
			ds.writeBytes("Content-Disposition:form-data;"+ "name=\"imageData\";filename=\"imageData\""+ end);
			ds.writeBytes(end);
			ds.write(baos.toByteArray(), 0, baos.toByteArray().length);
			ds.writeBytes(end); 
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);          
			ds.flush();
			
	        InputStream input = con.getInputStream();
	        StringBuffer sb = new StringBuffer();
			InputStreamReader inputReader = new InputStreamReader(input);
			BufferedReader reader = new BufferedReader(inputReader);
			String inputLine = null;
			while ((inputLine = reader.readLine()) != null) {
				sb.append(inputLine);
			}
			Utils.logh(TAG, "result: " + sb.toString());
			reader.close();
			ds.close();
			inputReader.close();
			input.close();
			con.disconnect();
			
			try {
				JSONObject jo = new JSONObject(sb.toString());
				int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
				failMsg = jo.optString(RET_MSG);
				if(REQ_RET_OK != rn) {
					return rn;
				}
				imageName = jo.optString("imgName");
				if(null != rivalDataCallback) {
					RivalData rivalData = new RivalData();
					rivalData.rivalRate = jo.optInt(RET_RIVAL_RATE);
					rivalData.rivalScore = jo.optInt(RET_RIVAL_SCORE);
					rivalDataCallback.getRivalData(rivalData);
				}
			} catch (Exception e) {
				e.printStackTrace();
				failMsg = context.getString(R.string.str_toast_image_update_fail);
				if(Utils.LOG_H) failMsg += "REQ_RET_F_JSON_EXCEP";
				return REQ_RET_F_JSON_EXCEP;
			}
			
			String lfn = saveLocalFile(imageName, bitmap);
			Utils.logh(TAG, "lfn: " + lfn);
//			new LocalMemory().saveDrawable(Drawable.createFromStream(is, srcName), getLocalFileName());
		} catch (IOException e) {
			e.printStackTrace();
//			failMsg = context.getString(R.string.str_toast_image_update_fail);
//			if(Utils.LOG_H) {
//				failMsg += "REQ_RET_F_UPDATE_AVATAR_FAIL";
//			}
			return REQ_RET_F_UPDATE_AVATAR_FAIL;
//			return REQ_RET_OK; // 异常，但实际上传成功
		};

		return REQ_RET_OK;
	}

	@Override
	public int parseBody(InputStream is) {
		return REQ_RET_OK;
	}

	public interface getRivalDataCallback {
		public abstract void getRivalData(RivalData rivalData);
	}
}
