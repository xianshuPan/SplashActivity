package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class CoachItem implements Serializable{
	
	private static final long serialVersionUID = -5823001473869645536L;
	
	public long id;
	public int sex;
	public String sn;
	public String nickname;
	public String avatar;
	
	public String special;
	
	public String award;
	public String certificate;
	public String graduate;
	
	public int teachTimes;
	
	public int teachYear;
	
	public int type;

	public float rate;
	
	public double handicapIndex;
	
	public int price;
	
	public double distance;
	
	public long distanceTime;
	
	public long course_id;
	public String state;
	public String course_name;
	public String course_address;
	public String course_tel;
	public String course_distance;

	public int audit;
	
	public int commentsAmount = 0;

	public int attention = -1;
}
