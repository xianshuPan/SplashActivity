package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class CoachInviteReqParam implements Serializable {
	
	private static final long serialVersionUID = 7785048433962175854L;

	public long studentid;
	
	public long coachid;
	public String courseid ;
	public int times;
	
	public int coachDate ;
	
	public String msg;
	
	public String coachTime;
	
}
