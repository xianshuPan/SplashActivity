package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class CoachListReqParam implements Serializable {
	private static final long serialVersionUID = 7785048433962175854L;

	public String sn;
	
	public int rangeBy;
	public int type;
	public int sex;
	
	public int pageNum;
	public int pageSize;
	
	public double lat = 104.55555;
	
	public double lng = 30.55555;
}
