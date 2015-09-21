package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class CoachItem implements Serializable{
	
	private static final long serialVersionUID = -5823001473869645536L;
	
	public long id;
	public String sn;
	public String nickname;
	public String avatar;
	
	public String special;
	
	public String award;
	
	public int teachTimes;
	
	public int teachYear;
	
	public int type;

	public float rate;
	
	public double handicapIndex;
	
	public int price;
	
	public double distance;
	
	public double distanceTime;
	
	public String course_id;
	public String course_name;
	public String course_address;
	public String course_tel;
	public String course_distance;
	
	
}
