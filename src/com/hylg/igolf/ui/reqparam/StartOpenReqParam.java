package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class StartOpenReqParam implements Serializable {
	private static final long serialVersionUID = 201882172656088718L;
	
	public String sn;
	public int day;
	public String time;
	public long courseId;
	public int stake;
	public int payType;
	public String msg;
}
