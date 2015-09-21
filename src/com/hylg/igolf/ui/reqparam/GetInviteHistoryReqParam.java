package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class GetInviteHistoryReqParam implements Serializable {
	private static final long serialVersionUID = 967980095653351176L;
	public long id;
	public String sn;
	public String avatar;
	public String nickname;
	public String teeTime;
	public String palHCPI;
	public String myHCPI;
	public int pageNum;
	public int pageSize;
	
	public String log() {
		return "sn : " + sn + " avatar: " + avatar + " nickname: " + nickname + " teeTime: " + teeTime
				+ " palHCPI: " + palHCPI + " myHCPI: " + myHCPI;
	}
}
