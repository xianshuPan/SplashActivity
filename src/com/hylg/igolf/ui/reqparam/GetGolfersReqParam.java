package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class GetGolfersReqParam implements Serializable {
	private static final long serialVersionUID = 7785048433962175854L;

	public String sn;
	public int label;
	public String region;
	public String industry;
	public int sex;
	public int pageNum;
	public int pageSize;
	
	public String log() {
		return "sn : " + sn + " label: " + label + " region: " + region + " industry: " + industry
				+ " sex: " + sex + " pageNum: " + pageNum + " pageSize: " + pageSize;
	}
}
