package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class GolferInfo implements Serializable {
	private static final long serialVersionUID = -5823001473869645536L;
	
//	public long id;
	public String sn;
	public String nickname;
	public String avatar;
	public int sex; // 索引值，通过数组获取字符串
	public int yearsExp;

	public int attention;
	public String industry; // 关键字，据此获取实际名称
	public String region; //地区关键字，据此获取实际名称
	public double rate;
	public int ratedCount;
	public double handicapIndex;
}
