package com.hylg.igolf.cs.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Customer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -927440815021434546L;
	public long id;
	public String sn ="";
	public String nickname;
	public String avatar;
	public String phone;
	public String addressName;
	public int sex; // 索引值，通过数组获取字符串
	public String yearsExpStr;
	public String state; // 关键字，据此获取实际名称
	public String city; // 关键字，据此获取实际名称
	public String industry; // 关键字，据此获取实际名称
	public String signature = "";
	public double rate;
	public int ratedCount;
	public int heat;
	public int activity;
	public int rank;
	public int matches;
	public int winNum;
	public double handicapIndex;
	public int best;
//	public String [] album = null; // 图片名称
	public ArrayList<String> album = new ArrayList<String>(); // 图片名称
	public String age;
	
	public int attention;
}
