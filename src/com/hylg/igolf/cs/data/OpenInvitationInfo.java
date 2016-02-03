package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class OpenInvitationInfo implements Serializable {
	private static final long serialVersionUID = -5240374550546799013L;
	
	public long id; // 约球单ID
	public String sn; // 约球单号
	public int status; // 约球状态
	public long inviterId;
	public String avatar;
	public int displayStatus; // 显示状态
	public String inviterSn;
	public String inviterNickname;
	public int inviterSex; // 索引值，通过数组获取字符串
	public String displayMsg;
	public int payType;
	public int stake;
	public String courseName;
	public String teeTime;
	public int applicantsNum;
	public int inviteeId;
	public String inviteeSn;
	public String inviteeAvatar;
	public String invitee_sns;
	public int local_fans;
	public int sameProvencePerson;
	public boolean acceptMe;
}
