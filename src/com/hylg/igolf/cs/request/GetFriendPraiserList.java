package com.hylg.igolf.cs.request;

import android.content.Context;

import com.hylg.igolf.DebugTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GetFriendPraiserList extends BaseRequest {
	private String param;
	 ArrayList<HashMap<String,String>> List = null;

	public GetFriendPraiserList(Context context, String sn, int pageNum, int pageSize, String tipsId) {
		super(context);
		List = new ArrayList<HashMap<String,String>>();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_NUM); s.append(KV_CONN); s.append(pageNum);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_PAGE_SIZE); s.append(KV_CONN); s.append(pageSize);
		s.append(PARAM_CONN);
		s.append("tipsId"); s.append(KV_CONN); s.append(tipsId);

		param = s.toString();
	}
	
	public ArrayList<HashMap<String,String>> getFriendCommentsList() {
		return List;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam2("getPraiseByPage", param);
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
			
			JSONArray ja = jo.optJSONArray("praises");
			
			if (ja != null && ja.length() > 0) {

				for (int j =0 ; j < ja.length() ; j++) {

					JSONObject reviewItem = ja.optJSONObject(j);

					HashMap<String, String> reviewItemHash = new HashMap<String, String>();
					reviewItemHash.put("id", reviewItem.optString("id"));
					reviewItemHash.put("tipid", reviewItem.optString("tipid"));
					reviewItemHash.put("sn", reviewItem.optString("sn"));
					reviewItemHash.put("name", reviewItem.optString("name"));

					List.add(reviewItemHash);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
