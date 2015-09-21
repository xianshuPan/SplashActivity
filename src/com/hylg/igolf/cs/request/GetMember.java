package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.data.Member;

public class GetMember extends BaseRequest {
	private String param;
	private String memSn;
	private Member member;

	public GetMember(Context context, String sn, String memSn) {
		super(context);
		this.memSn = memSn;
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_MEM_SN); s.append(KV_CONN); s.append(memSn);
		param = s.toString();
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getMember", param);
	}

	public Member getMember(){
		return member;
	}
	
	@Override
	public int parseBody(InputStream is) {
		String str = transferIs2String(is);
		try {
			JSONObject jo = new JSONObject(str);
			int rn = jo.optInt(RET_NUM, REQ_RET_FAIL);
			if(REQ_RET_OK != rn) {
				failMsg = jo.getString(RET_MSG);
				return rn;
			}
			
			DebugTools.getDebug().debug_v("会员详情", "-----》》》"+jo);
			
			if(memSn.equals(MainApp.getInstance().getCustomer().sn)){
				JSONObject cusObj = jo.getJSONObject(RET_CUSTOMER);
				Customer customer = MainApp.getInstance().getCustomer();
				customer.avatar = cusObj.getString(RET_AVATAR);
				customer.rate = cusObj.getDouble(RET_RATE);
				customer.ratedCount = cusObj.getInt(RET_RATE_COUNT);
				customer.heat = cusObj.getInt(RET_HEAT);
				customer.activity = cusObj.getInt(RET_ACTIVITY);
				customer.rank = cusObj.optInt(RET_RANK,Integer.MAX_VALUE);
				customer.matches = cusObj.getInt(RET_MATCHES);
				customer.handicapIndex = cusObj.optDouble(RET_HANDICAP_INDEX, Double.MAX_VALUE);
				customer.best = cusObj.optInt(RET_BEST, Integer.MAX_VALUE);
				JSONArray ja = cusObj.getJSONArray("album");
				customer.album.clear();
				for(int i=0, len=ja.length(); i<len; i++) {
					customer.album.add(ja.getString(i));
				}
			}else{
				JSONObject memObj = jo.getJSONObject("member");
				
				int attention = jo.getInt("attention");
				
				if(null != memObj){
					member = new Member();
					member.fightMsg = memObj.optString("fightMsg","");
					member.sn = memObj.getString(RET_SN);
					member.scoreMsg = memObj.getString("scoreMsg");
					member.nickname = memObj.getString(RET_NICKNAME);
					member.avatar = memObj.getString(RET_AVATAR);
					member.phone = memObj.optString(RET_PHONE,"");
					member.sex = memObj.getInt(RET_SEX);
					member.yearsExpStr = memObj.getString(RET_YEAR_EXP_STR);
					member.age = memObj.getString(RET_AGE_STR);
					member.city = memObj.getString(RET_CITY);
					member.industry = memObj.getString(RET_INDUSTRY);
					member.signature = memObj.optString(RET_SIGNATURE,"");
					member.rate = memObj.getDouble(RET_RATE);
					member.ratedCount = memObj.getInt(RET_RATE_COUNT);
					member.heat = memObj.getInt(RET_HEAT);
					member.activity = memObj.getInt(RET_ACTIVITY);
					member.rank = memObj.optInt(RET_RANK,Integer.MAX_VALUE);
					member.matches = memObj.getInt(RET_MATCHES);
					member.winNum = memObj.getInt(RET_WIN_NUM);
					member.handicapIndex = memObj.optDouble(RET_HANDICAP_INDEX, Double.MAX_VALUE);
					member.best = memObj.optInt(RET_BEST, Integer.MAX_VALUE);
					member.attention = attention;
					JSONArray ja = memObj.getJSONArray("album");
					member.album.clear();
					for(int i=0, len=ja.length(); i<len; i++) {
						member.album.add(ja.getString(i));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
