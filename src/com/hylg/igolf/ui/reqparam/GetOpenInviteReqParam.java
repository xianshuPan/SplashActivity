package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class GetOpenInviteReqParam implements Serializable {
	private static final long serialVersionUID = 1296775936668431819L;
	
	public String sn;
	public int date; // 常量索引：不限，今日，明日
	public int time; // 常量索引：不限，上午，下午
	public String location; // 地区关键字，可全国、省级、市级
	public int sex;
	public int pay; // 付款方式，常量索引：不限，AA制，…
	public int stake; // 球注，常量索引：不限，不下注，…
	public int pageNum;
	public int pageSize;
	
	public String log() {
		return "sn : " + sn + " pay:"+pay+" stake"+stake+" date: " + date + " time: " + time + " location: " + location + " sex: " + sex
				+ " pageNum: " + pageNum + " pageSize: " + pageSize;
	}
}
