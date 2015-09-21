package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class GetFightsReqParam implements Serializable {
	
	private static final long serialVersionUID = 6218510528723579916L;
	public String sn;
	public String memSn;
	public int pageNum;
	public int pageSize;
	
	public String log() {
		return "sn : " + sn + " memSn: " + memSn
				+ " pageNum: " + pageNum + " pageSize: " + pageSize;
	}
}
