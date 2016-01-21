package com.hylg.igolf.cs.request;

import java.io.*;
import java.net.*;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.util.Log;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.R;
import com.hylg.igolf.utils.Base64;
import com.hylg.igolf.utils.Utils;

public abstract class BaseRequest implements RequestParam, ReturnParam, ReturnCode {
	
	private static final String TAG = "DataRequest";
	
	// set request to get data by http
	public abstract String getRequestUrl();
	
	// parse data from stream, and save data
	public abstract int parseBody(InputStream is);
	
	protected static final int TIMEOUT = 30 * 1000;
	
	/*获取朋友圈列表*/
//	public static final String SERVER_IP = "http://192.168.2.107:8080";
//	public static final String UPLOAD_PIC_PATH = "http://192.168.2.107:9988/DealPicture/pic/";
//	public static final String SERVER_PATH = SERVER_IP+"/gams/ci/0/";


	public static final String SERVER = "http://121.199.22.44";
	public static final String PORT = ":8080";
	public static final String SERVER_IP = SERVER+PORT;
	public static final String UPLOAD_PIC_PATH = "http://121.199.22.44:9988/DealPicture/pic/";

	public static final String SERVER_PATH = SERVER_IP+"/gams/ci/1/";
	public static final String TipsPic_Original_PATH = SERVER_IP+"/gams/tipspic/original/";
	public static final String TipsPic_Thum_PATH = SERVER_IP+"/gams/tipspic/thum/";

	public static final String CoachPic_Original_PATH = SERVER_IP+"/gams/coach/original/";
	public static final String CoachPic_Thum_PATH = SERVER_IP+"/gams/coach/thum/";

	protected static final String PARAM_CONN = "&";
	protected static final String KV_CONN = "=";
	protected static final String PARAM = PARAM_REQ_PARAM + KV_CONN;
	
	private String mMethodName = "";
	protected Context context;
	
	/*请求体，有些参数写在请求体，发送post 请求*/
	String requestBody ;
	
	public BaseRequest(Context context) {
		this.context = context;
	}
	
	// 服务器返回提示信息：失败信息(有些接口也返回成功信息)
	protected String failMsg = "";
	
	public String getFailMsg() {
		return failMsg;
	}
	
	protected String getReqParam(String method, String param) {
		Utils.logh(TAG, "getReqParam ---> " + "\n    request: " + method + "\n    param: " + param);
		try {
			
			mMethodName = method;
			String url =  SERVER_PATH + method + "?" + PARAM
					// 避免后台获取中文乱码，需utf-8编码
					+ URLEncoder.encode(Base64.encode(param.getBytes()),HTTP.UTF_8);
					//+URLEncoder.encode(param,HTTP.UTF_8);
			
			//String url = SERVER_PATH + method + "?" + param;
			//DebugTools.getDebug().debug_v(TAG, url);
			//return url;8
			String url1 = SERVER_PATH + method + "?" + PARAM+param;
			
			DebugTools.getDebug().debug_v(TAG, url);
			DebugTools.getDebug().debug_v(TAG, url1);
			
			return url;
					
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected String getReqParam2(String method, String param) {
		
		mMethodName = method;
		Utils.logh(TAG, "getReqParam ---> " + "\n    request: " + method + "\n    param: " + param);
		try {
			String url = SERVER_PATH + method + "?" + PARAM
					// 避免后台获取中文乱码，需utf-8编码
					+ URLEncoder.encode(Base64.encode(param.getBytes()),HTTP.UTF_8);
					//+URLEncoder.encode(param,HTTP.UTF_8);
			
			String url1 = SERVER_PATH + method + "?" + PARAM+param;
			
			requestBody = param;
			
			DebugTools.getDebug().debug_v(TAG, url);
			DebugTools.getDebug().debug_v(TAG, url1);
			return url;
					
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * use like this:
	 * int ret = requst.connectUrl()
	 * if(Const.T_RESULT_OK == ret) {} else {}
	 * note: do not in ui thread.
	 */
	public int connectUrl() {
		return connectUrl(TIMEOUT);
	}
	
	public int connectUrl(int timeOut) {


		/*
		 * ci: client interface;
		 * server api version: 1;
		 * os type:
		 * 		0: ios;
		 * 		1: android;
		 * 		2: ipad;
		 * 		3: wp.
		 */
		return connectUrl(getRequestUrl(), timeOut);
	}
	
	private int connectUrl(String addr, int timeOut) {
//		if(Utils.LOG_H) {
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			return REQ_RET_OK;
//		}
		if(timeOut <= 0) {
			timeOut = TIMEOUT;
		}
		InputStream input = null;
		Utils.logh(TAG, "connectUrl addr: " + addr);
		if(null == addr) {
//			return T_RESULT_EX_CHECK_SERVER_PATH;
			Log.e(TAG, "connectUrl addr = null --- UnsupportedEncodingException");
			
			return REQ_RET_F_URL_ERROR;
			
		}
		
		sendBroadCast();
		
		try {
			URL url = new URL(addr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(timeOut);
			conn.setReadTimeout(timeOut);
			conn.setRequestMethod("POST");
			input = conn.getInputStream();
			if(null != input) {
				
				return parseBody(input);
			} else {
				return REQ_RET_F_CUS_DEF;
			}
//		} catch(MalformedURLException e) {
//			Log.w(TAG, "MalformedURLException");
//			return T_RESULT_EX_MALFORMED_URL;
//			return REQ_RET_EXCEP;
		} catch (ConnectTimeoutException e) { // the request timeout
			Log.w(TAG, "ConnectTimeoutException");
			failMsg = context.getString(R.string.str_toast_request_timeout);
			return T_RESULT_EX_CONNECT_TIMEOUT;
//			return REQ_RET_EXCEP;	Q																																																																																															``````
		} catch (SocketTimeoutException e) { // the server response timeout, but the request is successful.
			Log.w(TAG, "SocketTimeoutException");
			failMsg = context.getString(R.string.str_toast_request_timeout);
			return T_RESULT_EX_CONNECT_TIMEOUT;		
//			return REQ_RET_EXCEP;
		} catch(IOException e) {
			Log.w(TAG, "IOException: "
					+ (null==e.getMessage()?"":e.getMessage().toString()));
			e.printStackTrace();
			failMsg = context.getString(R.string.str_toast_request_exc);
//			return REQ_RET_EXCEP;
		} finally {
			if(null != input){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return REQ_RET_F_CUS_DEF;
	}
	
	/*这个方法请求参数写到请求体里面*/
	public int connectUrlGet() {

		int timeOut = TIMEOUT;
		if(timeOut <= 0) {
			timeOut = TIMEOUT;
		}
		
		String addr = getRequestUrl();
		
		
		sendBroadCast();
		
		InputStream input = null;
		Utils.logh(TAG, "connectUrl addr: " + addr);
		if(null == addr) {
//			return T_RESULT_EX_CHECK_SERVER_PATH;
			Log.e(TAG, "connectUrl addr = null --- UnsupportedEncodingException");
			return REQ_RET_F_URL_ERROR;
		}
		
		try {
			URL url = new URL(addr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(timeOut);
			conn.setReadTimeout(timeOut);  
			
			
			input = conn.getInputStream();
			if(null != input) {
				return parseBody(input);
			} else {
				return REQ_RET_F_CUS_DEF;
			}
//		} catch(MalformedURLException e) {
//			Log.w(TAG, "MalformedURLException");
//			return T_RESULT_EX_MALFORMED_URL;
//			return REQ_RET_EXCEP;
		} catch (ConnectTimeoutException e) { // the request timeout
			Log.w(TAG, "ConnectTimeoutException");
			failMsg = context.getString(R.string.str_toast_request_timeout);
			return T_RESULT_EX_CONNECT_TIMEOUT;
//			return REQ_RET_EXCEP;
		} catch (SocketTimeoutException e) { // the server response timeout, but the request is successful.
			Log.w(TAG, "SocketTimeoutException");
			failMsg = context.getString(R.string.str_toast_request_timeout);
			return T_RESULT_EX_CONNECT_TIMEOUT;		
//			return REQ_RET_EXCEP;
		} catch(IOException e) {
			Log.w(TAG, "IOException: "
					+ (null==e.getMessage()?"":e.getMessage().toString()));
			e.printStackTrace();
			failMsg = context.getString(R.string.str_toast_request_exc);
//			return REQ_RET_EXCEP;
		} finally {
			if(null != input){
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return REQ_RET_F_CUS_DEF;
	}
	
	/*发出暂停广播*/
	private void sendBroadCast() {
		
		DebugTools.getDebug().debug_v(TAG, "能不能发送广播");
		if (mMethodName != null && !mMethodName.equalsIgnoreCase("releaseTips")) {
			
			//context.sendBroadcast(new Intent("com.hylg.igolf.stop"));
			
			DebugTools.getDebug().debug_v(TAG, "广播已经发出");
		}
		
		
	}
	
	protected String transferIs2String(InputStream is) {
		
		if (is ==  null) {
			
			return "";
		}
		String str = null;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			str = new String(outStream.toByteArray(), HTTP.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

//		if(Utils.LOG_H || Utils.TEST_MODE) {
//			String[] log = str.split(",");
//			if(Utils.TEST_MODE) {
//				StringBuilder b = new StringBuilder();
//				for(int i=0,len1=log.length; i<len1; i++) {
//					Utils.logh(TAG, log[i]);
//					b.append(log[i]);
//					b.append("\n");
//				}
//				mLogCache = b.toString();
//			} else {
//				for(int i=0,len1=log.length; i<len1; i++) {
//					Utils.logh(TAG, log[i]);
//				}
//			}
//		}
		return str;
	}
	
	/**
	 * 获取图片url接口
	 */
	private static final int IMG_PERSON = 0;
//	private static final int IMG_TEAM = 1;
	//
	public static String getAvatarUrl(String sn, String name) {
		StringBuilder s = new StringBuilder();
		s.append("type"); s.append(KV_CONN); s.append(IMG_PERSON);
		s.append(PARAM_CONN);
		s.append("sn"); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append("name"); s.append(KV_CONN); s.append(name);
		try {
			
			 String url = SERVER_PATH + "getAvatarImage?" + PARAM
						+ s.toString();
			 
			 String url1 = SERVER_PATH + "getAvatarImage?" + PARAM
					+ URLEncoder.encode(
							Base64.encode(s.toString().getBytes()),
							HTTP.UTF_8);
			 
			 DebugTools.getDebug().debug_v("", ""+url);
			 
			 return url1;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
//		return SERVER_PATH + "getAvatarImage?" + PARAM
//				+ Base64.encode(s.toString().getBytes());
	}
	
	
	public static String getAlbumUrl(String sn, String name,int type) {
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_TYPE); s.append(KV_CONN); s.append(0);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append("name"); s.append(KV_CONN); s.append(name);
		s.append(PARAM_CONN);
		s.append("albumType"); s.append(KV_CONN); s.append(type);
		try {
			return SERVER_PATH + "getAlbumImage?" + PARAM
					+ URLEncoder.encode(
							Base64.encode(s.toString().getBytes()),
							HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getScorecardUrl(String appSn, String userSn) {
		StringBuilder s = new StringBuilder();
		s.append("appSn"); s.append(KV_CONN); s.append(appSn);
		s.append(PARAM_CONN);
		s.append("userSn"); s.append(KV_CONN); s.append(userSn);
		try {
			return SERVER_PATH + "getScoreImage?" + PARAM
					+ URLEncoder.encode(
							Base64.encode(s.toString().getBytes()),
							HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String mLogCache = "";
	public static String getLogCache() {
		return mLogCache;
	}
	
}
