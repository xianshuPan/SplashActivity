package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.FriendHotItem;

public class GetFriendHotList extends BaseRequest {
	private String param;
	private ArrayList<FriendHotItem> hotList = null;

	public GetFriendHotList(Context context, String sn, int pageNum, int pageSize,String provence,String model) {
		super(context);
		hotList = new ArrayList<FriendHotItem>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PROVENCE); s.append(KV_CONN); s.append(provence);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_MODEL); s.append(KV_CONN); s.append(model);
		
		
		param = s.toString();
	}
	
	public ArrayList<FriendHotItem> getFriendHotList() {
		return hotList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam2("getTipsInfo", param);
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
			
			DebugTools.getDebug().debug_v("friendList", "------->>>>>"+jo);
			
			JSONArray ja = jo.optJSONArray("friendsCircles");
			
			for(int i=0, len=ja.length(); i<len; i++) {
				
				JSONObject obj = ja.optJSONObject(i);
				FriendHotItem item = new FriendHotItem();
				
				item.tipid = obj.optString("tipid");
				item.sn = obj.optString("sn");
				item.name = obj.optString("name");
				item.avatar = obj.optString("avatar");
				item.imageURL = obj.optString("imageURL");
				
				String contentStr = obj.optString("content");
				
				if (contentStr != null && contentStr.length() > 0 && !contentStr.equalsIgnoreCase("null")) {
					
					item.content = contentStr;
				}
				
				item.attention = obj.optInt("attention");
				item.praise = obj.optInt("praise");
				item.releaseTime = obj.optLong("releaseTime");
				
				item.praises = new ArrayList<HashMap<String,String>>();
				JSONArray praises = obj.optJSONArray("praises");
				
				for (int j =0 ; j < praises.length() ; j++) {
					
					JSONObject reviewItem = praises.optJSONObject(j);
					
					HashMap<String, String> reviewItemHash = new HashMap<String, String>();
					reviewItemHash.put("id", reviewItem.optString("id"));
					reviewItemHash.put("tipid", reviewItem.optString("tipid"));
					reviewItemHash.put("sn", reviewItem.optString("sn"));
					reviewItemHash.put("name", reviewItem.optString("name"));
					
					item.praises.add(reviewItemHash);
					
				}
				
				item.comments = new ArrayList<HashMap<String,String>>();
				JSONArray comments = obj.optJSONArray("comments");
				
				for (int j =0 ; j < comments.length() ; j++) {
					
					JSONObject reviewItem = comments.optJSONObject(j);
					
					HashMap<String, String> reviewItemHash = new HashMap<String, String>();
					reviewItemHash.put("id", reviewItem.optString("id"));
					reviewItemHash.put("tipid", reviewItem.optString("tipid"));
					reviewItemHash.put("sn", reviewItem.optString("sn"));
					reviewItemHash.put("content", reviewItem.optString("content"));
					reviewItemHash.put("name", reviewItem.optString("name"));
					reviewItemHash.put("tosn", reviewItem.optString("tosn"));
					reviewItemHash.put("toname", reviewItem.optString("toname"));
					
					item.comments.add(reviewItemHash);
					
				}
				
				hotList.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
