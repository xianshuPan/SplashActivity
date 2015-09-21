package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class GetScoreHistoryReqParam implements Serializable {
	private static final long serialVersionUID = 8300964623270947216L;
	public String sn;
	public int pageNum;
	public int pageSize;
	
	public String log() {
		return "sn : " + sn ;
	}
}
