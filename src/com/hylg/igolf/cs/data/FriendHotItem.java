package com.hylg.igolf.cs.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendHotItem implements Serializable {
	private static final long serialVersionUID = -8259616561683164141L;
	

//	public String headpic;
//	
//	public String nickname;
//	public int time;
//	public int focused;
//	public String content;
//	public int favored;
//
//	public int favoredCount;
//	
//	public int reviewCount;
//	
//	// 一对一约球方案信息
//	public ArrayList<String> images = null;
//
//	
//	public ArrayList<HashMap<String, String>> review = null;
//	
	
	/*头像路径*/
	public String avatar;
	
	/*帖子ID*/
	public String tipid;
	
	/*发帖人sn*/
	public String sn;
	
	/*发帖人名称*/
	public String name;
	
	/*发帖内容*/
	public String content;
	
	/*省*/
	public String provence;
	
	/*市*/
	public String city;
	
	/*区*/
	public String region;
	
	/*alais*/
	public String alais;
	
	/*详细地址*/
	public String detail;
	
	/*发帖图片*/
	public String imageURL = "";
	
	/*发帖时间*/
	public long releaseTime;
	
	/*点赞人*/
	public ArrayList<HashMap<String, String>> praises = null;
	
	/*评论*/
	public ArrayList<HashMap<String, String>> comments = null;
	
	/*local */
	public List<String> localImageURL = null;
	
	/*是否关注*/
	public int attention;
	
	/*是否点赞*/
	public int praise;

	public int praiseCount;

	public int commentsCount;

	public int status ;

	public List<String> failedlocalImageURL = null;
}
