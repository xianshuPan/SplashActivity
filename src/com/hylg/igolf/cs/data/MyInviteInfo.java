package com.hylg.igolf.cs.data;

import com.hylg.igolf.ui.hall.data.Applicant;

import java.io.Serializable;
import java.util.ArrayList;

public class MyInviteInfo implements Serializable {
	private static final long serialVersionUID = 1356316048962664806L;
	
	public long id; // 约球单ID
	public String sn; // 约球单号
	public int type; // 约球类型：开放式，一对一
	public int status; // 约球状态
	public int displayStatus; // 显示状态
	public String displayStatusStr; // 显示内容
	public int planNum; // 方案个数
	public String courseName;
	public String teeTime;
	public int applicantsNum; // 开放式，申请人数
	public int palId; // 显示会员ID
	public String palSn; // 显示会员编号
	public String palAvatar; // 显示会员头像
	public String palNickname; // 显示会员昵称
	public int palSex; // 显示会员性别
	public String palMsg; // 约球描述
	public boolean haveAlert; // 是否有状态变化
	public String inviterSn; // 发起者的sn，与type一起，判断调用哪个类

	public int inviterSex;
	public String inviterName;
	public int payType;
	public int local_fans;

	public ArrayList<Applicant> applicants;
}
