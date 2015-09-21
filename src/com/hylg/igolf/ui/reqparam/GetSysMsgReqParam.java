package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class GetSysMsgReqParam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2028258604170754359L;
	public String sn;
	public int pageNum;
	public int pageSize;
	
	public String log() {
		return "sn : " + sn + " pageNum: " + pageNum + " pageSize: " + pageSize;
	}
}
