package com.hylg.igolf.cs.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MyInviteDetail implements Serializable {
	private static final long serialVersionUID = -8259616561683164141L;
	
	public InviteRoleInfo inviter = null;
	public InviteRoleInfo invitee = null;
	public InviteRoleInfo inviteeone = null;
	public InviteRoleInfo inviteetwo = null;
	public String message;
	public int stake;
	public int paymentType;
	public int type;
	public int displayStatus;
	/**
	 *  评分：
	 *  	-1： 未签到；
	 *  	0 ： 已签到。
	 *     >0：已记分
	 */
	public int score;
	public String scoreCardName;
	public int rateStar;
	// 一对一约球方案信息
	public ArrayList<PlanShowInfo> planInfo = null;
	public String addressName;
	// 开放式约球信息
	public String courseName;
	public String teeTime;
	public ArrayList<InviteRoleInfo> applicants = null;
	
	public ArrayList<InviteRoleInfo> selectApplicants = null;


	public ArrayList<String> select_menber_sn = null;
	
	public HashMap<Long, Integer> ratesIdHash = null;
}
