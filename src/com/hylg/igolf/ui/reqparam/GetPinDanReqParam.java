package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class GetPinDanReqParam implements Serializable {
	private static final long serialVersionUID = 1296775936668431819L;
	
	public String sn;
	public int date; // 常量索引：不限，今日，明日
	public int time; // 常量索引：不限，上午，下午
	public String location; // 地区关键字，可全国、省级、市级
	public int sex;
	public int pageNum;
	public int pageSize;

	public double lat,lng;

	public int apiVersion;

	public int appVersion;
	
	public String log() {
		return "sn : " + sn +" date: " + date + " time: " + time + " location: " + location + " sex: " + sex
				+ " pageNum: " + pageNum + " pageSize: " + pageSize;
	}
}
