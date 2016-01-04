package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.FriendHotItem;
import com.hylg.igolf.cs.data.MyFolloweInfo;

public class GetMyFollowerList extends BaseRequest {
	private String param;
	private ArrayList<MyFolloweInfo> myFollowerList = null;

	public GetMyFollowerList(Context context, String sn,  String mem_sn, int pageNum, int pageSize) {
		super(context);
		myFollowerList = new ArrayList<MyFolloweInfo>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append("otherSn"); s.append(KV_CONN); s.append(mem_sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		
		param = s.toString();
	}
	
	public ArrayList<MyFolloweInfo> getMyFollowerList() {
		return myFollowerList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam2("getMyFans", param);
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			
//			int rn = jo.optInt("result", REQ_RET_FAIL);
//			if(200 != rn) {
//				failMsg = jo.getString(RET_MSG);
//				return rn;
//			}
			
			DebugTools.getDebug().debug_v("myFollower----->>>", "------->>>>>"+jo);
			
			JSONArray ja = jo.optJSONArray("fans");
			
			for(int i=0, len=ja.length(); i<len; i++) {
				
				JSONObject obj = ja.optJSONObject(i);
				MyFolloweInfo temp = new MyFolloweInfo();
				
				temp.attentionOrNot = obj.optInt("attentionOrNot");
				temp.sn =  obj.optString("sn");
				temp.nickName =  obj.optString("nickName");
				temp.avatar = obj.optString("avater");
				
				temp.dynamic = obj.optInt("dynamic");
				temp.praises = obj.optInt("praises");

				
				myFollowerList.add(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
