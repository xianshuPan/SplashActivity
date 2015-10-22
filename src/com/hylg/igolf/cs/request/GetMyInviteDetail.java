package com.hylg.igolf.cs.request;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import org.json.JSONObject;

import android.content.Context;

import com.hylg.igolf.DebugTools;
import com.hylg.igolf.cs.data.ApplicantsInfo;
import com.hylg.igolf.cs.data.InviteRoleInfo;
import com.hylg.igolf.cs.data.MyInviteDetail;
import com.hylg.igolf.cs.data.PlanShowInfo;
import com.hylg.igolf.utils.Const;

public class GetMyInviteDetail extends GetInviteDetail {
	private String param;
	private MyInviteDetail detail = null;

	public GetMyInviteDetail(Context context, String appSn, String sn) {
		super(context);
		detail = new MyInviteDetail();
		StringBuilder s = new StringBuilder();
		s.append(PARAM_REQ_APP_SN); s.append(KV_CONN); s.append(appSn);
		s.append(PARAM_CONN);
		s.append(PARAM_REQ_SN); s.append(KV_CONN); s.append(sn);
		param = s.toString();
	}
	
	public MyInviteDetail getMyInviteDetail() {
		return detail;
	}
	
	@Override
	public String getRequestUrl() {
		return getReqParam("getMyInviteDetail02", param);
	}
	
	
	protected InviteRoleInfo getInviteRoleInfo(JSONObject jo,boolean applicants) throws Exception {
		if(null == jo) {
			return null;
		}
		InviteRoleInfo role = new InviteRoleInfo();
		role.id = jo.getLong(RET_ID);
		role.sn = jo.getString(RET_SN);
		role.avatar = jo.getString(RET_AVATAR);
		role.nickname = jo.getString(RET_NICKNAME);
		role.sex = jo.getInt(RET_SEX);
		
		
		role.rate = jo.optDouble(RET_RATE, Double.MAX_VALUE);
		role.ratedCount = jo.getInt(RET_RATE_COUNT);
		role.matches = jo.getInt(RET_MATCHES);
		role.handicapIndex = jo.optDouble(RET_HANDICAP_INDEX, Double.MAX_VALUE);
		role.best = jo.optInt(RET_BEST, Integer.MAX_VALUE);
		role.score = jo.optInt(RET_SCORE, Integer.MAX_VALUE);
		
		
		if (detail.selectApplicants == null) {
			
			detail.selectApplicants =  new ArrayList<InviteRoleInfo>();
		}
		
		if (applicants) {
			
			InviteRoleInfo asi = new InviteRoleInfo();
			asi.id = (int)jo.getLong(RET_ID);
			asi.sn = jo.getString(RET_SN);
			asi.avatar = jo.getString(RET_AVATAR);
			asi.nickname = jo.getString(RET_NICKNAME);
			asi.sex = jo.getInt(RET_SEX);
			
			asi.rate = jo.optDouble(RET_RATE, Double.MAX_VALUE);
			asi.ratedCount = jo.getInt(RET_RATE_COUNT);
			asi.matches = jo.getInt(RET_MATCHES);
			asi.handicapIndex = jo.optDouble(RET_HANDICAP_INDEX, Double.MAX_VALUE);
			asi.best = jo.optInt(RET_BEST, Integer.MAX_VALUE);
			asi.score = jo.optInt(RET_SCORE, Integer.MAX_VALUE);
			
			detail.selectApplicants.add(asi);
		}
		
		return role;
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
			
			DebugTools.getDebug().debug_v("我的约球详情", "----->>>>"+jo);
			
			JSONObject mid = jo.getJSONObject(RET_MY_INVITE_DETAIL);
			detail.inviter = getInviteRoleInfo(mid.getJSONObject(RET_INVITER),false);
			detail.invitee = getInviteRoleInfo(mid.optJSONObject(RET_INVITEE),true);
			detail.inviteeone = getInviteRoleInfo(mid.optJSONObject(RET_INVITEE_ONE),true);
			detail.inviteetwo = getInviteRoleInfo(mid.optJSONObject(RET_INVITEE_TWO),true);
			detail.message = mid.getString(RET_MESSAGE).replaceAll("[\\n|\\r]","");
			detail.stake = mid.getInt(RET_STAKE);
			detail.paymentType = mid.getInt(RET_PAYMENT_TYPE);
			
			detail.type = mid.getInt(RET_TYPE);
			detail.displayStatus = mid.getInt(RET_DISPLAY_STATUS);
			detail.score = mid.getInt(RET_SCORE);
			detail.scoreCardName = mid.getString(RET_SCORE_CARD_NAME);
			detail.rateStar = mid.getInt(RET_RATE_START);
			// 一对一必填，开放无。
			detail.addressName = mid.optString(RET_ADDRESS_NAME);
			
			// 多方案
			JSONArray pi= mid.optJSONArray(RET_PLAN_INFO);
			
			if(null != pi) {
				if(null == detail.planInfo) {
					detail.planInfo = new ArrayList<PlanShowInfo>();
				}
				detail.planInfo.clear();
				for(int i=0, size=pi.length(); i<size; i++) {
					JSONObject piJo = pi.getJSONObject(i);
					PlanShowInfo psi = new PlanShowInfo();
					psi.index = piJo.getInt(RET_INDEX);
					psi.courseName = piJo.getString(RET_COURSE_NAME);
					psi.teeTime = piJo.getString(RET_TEE_TIME);
					detail.planInfo.add(psi);
				}
			}
			
			String ratesRateStr =  mid.optString(RET_COURSE_ratesRateStr);
			
			String ratesIdStr =  mid.optString(RET_COURSE_ratesIdStr);
			
			if (ratesRateStr != null && ratesRateStr.length() > 0) {
				
				detail.ratesIdHash = new HashMap<Long, Integer>();
				
				String[] ratesRate = ratesRateStr.split(",");
				
				String[] ratesIdRate = ratesIdStr.split(",");
				
				if (ratesRate != null && ratesIdRate != null) {
					
					for (int i =0 ; i < ratesRate.length;i++ ) {
						
						detail.ratesIdHash.put(Long.valueOf(ratesIdRate[i]), Integer.valueOf(ratesRate[i]));
						
					}
				}
				
			}
			
			
			
			// 一对一或方案已选
			detail.courseName = mid.optString(RET_COURSE_NAME);
			detail.teeTime = mid.optString(RET_TEE_TIME);
			
			// 我发起的开放式约球，申请人信息
			JSONArray ai = mid.optJSONArray(RET_APPLICANTS);
			if(null != ai) {
				if(null == detail.applicants) {
					detail.applicants = new ArrayList<InviteRoleInfo>();
				}
				detail.applicants.clear();
				
				for(int i=0, size=ai.length(); i<size; i++) {
					JSONObject aiJo = ai.getJSONObject(i);
					InviteRoleInfo asi = new InviteRoleInfo();
					asi.id = aiJo.getInt(RET_ID);
					asi.sn = aiJo.getString(RET_SN);
					asi.avatar = aiJo.getString(RET_AVATAR);
					asi.nickname = aiJo.getString(RET_NICKNAME);
					asi.sex = aiJo.optInt(RET_SEX);
					
					asi.rate = aiJo.optDouble(RET_RATE, Double.MAX_VALUE);
					asi.ratedCount = aiJo.getInt(RET_RATE_COUNT);
					asi.matches = aiJo.getInt(RET_MATCHES);
					asi.handicapIndex = aiJo.optDouble(RET_HANDICAP_INDEX, Double.MAX_VALUE);
					asi.best = aiJo.optInt(RET_BEST, Integer.MAX_VALUE);
					asi.score = aiJo.optInt(RET_SCORE, Integer.MAX_VALUE);
					
					/*
					 * invitee 为什么会设置成第一个申请人???
					 * 
					 * 申请人的第一个是默认选中的？？？？
					 * 
					 * 约球的状态 必须是正在申请状态
					 * 
					 * */
					if(detail.invitee == null && i == 0 && detail.displayStatus == Const.MY_INVITE_APPlYED) {
						
						detail.invitee = asi;
					}
					
					detail.applicants.add(asi);
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return REQ_RET_F_JSON_EXCEP;
		}
		return REQ_RET_OK;
	}

}
