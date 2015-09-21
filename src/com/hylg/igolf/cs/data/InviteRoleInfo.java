package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class InviteRoleInfo implements Serializable {
	private static final long serialVersionUID = 6446652987084963650L;
	
	public long id;
	public String sn;
	public String avatar;
	public String nickname;
	public int sex; // 索引值，通过数组获取字符串
	public double rate;
	public int ratedCount;
	public int matches;
	public double handicapIndex;
	public int best;
	public int score;
	
	public InviteRoleInfo() {
		
	}
	
	/**
	 * 接受申请，构造部分默认信息
	 * @param ai
	 */
	public InviteRoleInfo(ApplicantsInfo ai) {
		if(null == ai) {
			return ;
		}
		// 已存在的值
		id = ai.id;
		sn = ai.sn;
		avatar = ai.avatar;
		nickname = ai.nickname;
		sex = ai.sex;
		// 真正需要获取的值
		handicapIndex = Double.MAX_VALUE;
		matches = 0;
		// 指本次数据，尚未开始，为无效值。
		score = Integer.MAX_VALUE;
		rate = 0.0;
	}
	
	/**
	 * 根据登录用户信息，填充内容。
	 * 	如，他人发起的大厅约球，约球详情中，不返回申请者信息，右侧信息显示用户。
	 * @param customer
	 */
	public InviteRoleInfo(Customer customer) {
		id = customer.id;
		sn = customer.sn;
		avatar = customer.avatar;
		nickname = customer.nickname;
		sex = customer.sex;
		rate = customer.rate;
		ratedCount = customer.ratedCount;
		matches = customer.matches;
		handicapIndex = customer.handicapIndex;
//		best = customer.best;
	}
	
	public String log() {
		return "InviteRoleInfo {" +
					"\n	id: " + id +
					"\n sn: " + sn +
					"\n avatar: " + avatar +
					"\n nickname: " + nickname +
					"\n sex: " + sex +
					"\n rate: " + rate +
					"\n matches: " + matches +
					"\n handicapIndex: " + handicapIndex +
					"\n best: " + best +
					"\n score: " + score;
	}
}
