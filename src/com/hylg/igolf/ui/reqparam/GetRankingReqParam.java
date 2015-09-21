package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class GetRankingReqParam implements Serializable {

	private static final long serialVersionUID = 3428958042782677857L;
	public String sn;
	public int type;
	public String region;
	public int sex;
	public int pageNum;
	public int pageSize;
	
	public String log() {
		return "sn : " + sn + " type: " + type + " location: " + region + " sex: " + sex
				+ " pageNum: " + pageNum + " pageSize: " + pageSize;
	}
}
