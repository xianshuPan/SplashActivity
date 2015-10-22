package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.FriendHotItem;

public class GetMyPraiseTipsList extends BaseRequest {
	private String param;
	private ArrayList<FriendHotItem> hotList = new ArrayList<FriendHotItem>();

	public GetMyPraiseTipsList(Context context, String sn, int pageNum, int pageSize) {
		super(context);
		hotList = new ArrayList<FriendHotItem>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		
		param = s.toString();
	}
	
	public ArrayList<FriendHotItem> getFriendHotList() {
		return hotList;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam2("getPraisedTipsInfo", param);
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
			
			DebugTools.getDebug().debug_v("getPraisedTipsInfo", "------->>>>>"+jo);
			
			JSONArray ja = jo.getJSONArray("friendsCircles");
			
			for(int i=0, len=ja.length(); i<len; i++) {
				
				JSONObject obj = ja.getJSONObject(i);
				FriendHotItem item = new FriendHotItem();
				
				item.tipid = obj.getString("tipid");
				item.sn = obj.getString("sn");
				item.name = obj.getString("name");
				item.avatar = obj.getString("avatar");
				item.imageURL = obj.getString("imageURL");
				
				String contentStr = obj.getString("content");
				
				if (contentStr != null && contentStr.length() > 0 && !contentStr.equalsIgnoreCase("null")) {
					
					item.content = contentStr;
				}
				
				item.attention = obj.getInt("attention");
				item.praise = obj.getInt("praise");
				item.releaseTime = obj.getLong("releaseTime");	
				
				item.praises = new ArrayList<HashMap<String,String>>();
				JSONArray praises = obj.getJSONArray("praises");
				
				for (int j =0 ; j < praises.length() ; j++) {
					
					JSONObject reviewItem = praises.getJSONObject(j);
					
					HashMap<String, String> reviewItemHash = new HashMap<String, String>();
					reviewItemHash.put("id", reviewItem.getString("id"));	
					reviewItemHash.put("tipid", reviewItem.getString("tipid"));
					reviewItemHash.put("sn", reviewItem.getString("sn"));
					reviewItemHash.put("name", reviewItem.getString("name"));	
					
					item.praises.add(reviewItemHash);
					
				}
				
				item.comments = new ArrayList<HashMap<String,String>>();
				JSONArray comments = obj.getJSONArray("comments");
				
				for (int j =0 ; j < comments.length() ; j++) {
					
					JSONObject reviewItem = comments.getJSONObject(j);
					
					HashMap<String, String> reviewItemHash = new HashMap<String, String>();
					reviewItemHash.put("id", reviewItem.getString("id"));	
					reviewItemHash.put("tipid", reviewItem.getString("tipid"));
					reviewItemHash.put("sn", reviewItem.getString("sn"));
					reviewItemHash.put("content", reviewItem.getString("content"));	
					reviewItemHash.put("name", reviewItem.getString("name"));
					reviewItemHash.put("tosn", reviewItem.getString("tosn"));
					reviewItemHash.put("toname", reviewItem.getString("toname"));
					
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
