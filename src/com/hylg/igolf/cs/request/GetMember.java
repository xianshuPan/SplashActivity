package com.hylg.igolf.cs.request;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.MainApp;
import com.hylg.igolf.cs.data.CoachItem;
import com.hylg.igolf.cs.data.Customer;
import com.hylg.igolf.cs.data.Member;
import com.hylg.igolf.ui.reqparam.CoachApplyInfoReqParam;

public class GetMember extends BaseRequest {
	private String param;
	private String memSn;

	private Member member;

	public CoachItem coach_item;

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
				failMsg = jo.optString(RET_MSG);
				return rn;
			}
			
			DebugTools.getDebug().debug_v("会员详情", "-----》》》"+jo);
			
			if(memSn.equals(MainApp.getInstance().getCustomer().sn)){
				JSONObject cusObj = jo.optJSONObject(RET_CUSTOMER);
				Customer customer = MainApp.getInstance().getCustomer();
				customer.avatar = cusObj.optString(RET_AVATAR);
				customer.rate = cusObj.optDouble(RET_RATE);
				customer.ratedCount = cusObj.optInt(RET_RATE_COUNT);
				customer.heat = cusObj.optInt(RET_HEAT);
				customer.activity = cusObj.optInt(RET_ACTIVITY);
				customer.rank = cusObj.optInt(RET_RANK,Integer.MAX_VALUE);
				customer.matches = cusObj.optInt(RET_MATCHES);
				customer.handicapIndex = cusObj.optDouble(RET_HANDICAP_INDEX, Double.MAX_VALUE);
				customer.best = cusObj.optInt(RET_BEST, Integer.MAX_VALUE);

				JSONArray ja = cusObj.optJSONArray("album");
				customer.album.clear();
				for(int i=0, len=ja.length(); i<len; i++) {
					customer.album.add(ja.optString(i));
				}

			} else {
				JSONObject memObj = jo.optJSONObject("member");
				
				int attention = jo.optInt("attention");
				
				if(null != memObj){
					member = new Member();
					member.fightMsg = memObj.optString("fightMsg","");
					member.sn = memObj.optString(RET_SN);
					member.scoreMsg = memObj.optString("scoreMsg");
					member.nickname = memObj.optString(RET_NICKNAME);
					member.avatar = memObj.optString(RET_AVATAR);
					member.phone = memObj.optString(RET_PHONE,"");
					member.sex = memObj.optInt(RET_SEX);
					member.yearsExpStr = memObj.optString(RET_YEAR_EXP_STR);
					member.age = memObj.optInt(RET_AGE_STR);
					member.city = memObj.optString(RET_CITY);
					member.industry = memObj.optString(RET_INDUSTRY);
					member.signature = memObj.optString(RET_SIGNATURE,"");
					member.rate = memObj.optDouble(RET_RATE);
					member.ratedCount = memObj.optInt(RET_RATE_COUNT);
					member.heat = memObj.optInt(RET_HEAT);
					member.activity = memObj.optInt(RET_ACTIVITY);
					member.rank = memObj.optInt(RET_RANK,Integer.MAX_VALUE);
					member.matches = memObj.optInt(RET_MATCHES);
					member.winNum = memObj.optInt(RET_WIN_NUM);
					member.handicapIndex = memObj.optDouble(RET_HANDICAP_INDEX, Double.MAX_VALUE);
					member.best = memObj.optInt(RET_BEST, Integer.MAX_VALUE);
					member.attention = attention;
					JSONArray ja = memObj.optJSONArray("album");
					member.album.clear();
					for(int i=0, len=ja.length(); i<len; i++) {
						member.album.add(ja.optString(i));
					}
				}
			}

			JSONObject obj = jo.optJSONObject("coach");

			if (obj != null) {

				coach_item = new CoachItem();

				JSONObject courseJson = obj.optJSONObject("course");
				JSONObject customerJson = obj.optJSONObject("customer");
				JSONObject feeJson = obj.optJSONObject("fee");

				coach_item.sn = customerJson.optString("sn");
				coach_item.id = obj.optLong("id");
				coach_item.nickname = customerJson.optString("nickname");
				coach_item.avatar = customerJson.optString("avatar");
				coach_item.sex = customerJson.optInt("sex");

				coach_item.teachTimes = obj.optInt("experience");
				coach_item.teachYear = obj.optInt("teaching_age");
				coach_item.type = feeJson.optInt("type");
				coach_item.rate = obj.optInt("star");
				coach_item.audit = obj.optInt("audit");

				coach_item.award = obj.optString("award");
				coach_item.graduate = obj.optString("diploma");
				coach_item.certificate = obj.optString("certification");

				coach_item.handicapIndex = customerJson.optDouble("handicapIndex", Double.MAX_VALUE);
				coach_item.price = feeJson.optInt("price");
				coach_item.distance = obj.optDouble("distance");
				coach_item.distanceTime = obj.optLong("last_login");
				coach_item.special = obj.optString("specialty");

				coach_item.course_id = courseJson.optLong("id");
				coach_item.state = courseJson.optString("state");
				coach_item.course_name = courseJson.optString("abbr");
				coach_item.course_address = courseJson.optString("address");
				coach_item.course_tel = courseJson.optString("tel");

				coach_item.commentsAmount = obj.optInt("commentsAmount");
				coach_item.attention = obj.optInt("attention");

				coach_item.attention = obj.optInt("attention");

			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
