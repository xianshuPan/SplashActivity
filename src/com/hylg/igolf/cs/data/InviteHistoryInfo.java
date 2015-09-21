package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class InviteHistoryInfo implements Serializable {
	private static final long serialVersionUID = 8457574924520671856L;
	
	public long id;
	public String sn;
	public String appSn;
	public String imgName;
	public String teeTime;
	public String nickname;
	public String avatar;
	public int palHCPI;
	public int myHCPI;
	public int palHCPIStatus;
	public int myHCPIStatus;
}
