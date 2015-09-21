package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class CoachInviteOrderDetail implements Serializable {
	private static final long serialVersionUID = 1356316048962664806L;
	
	public int status;
	
	public String status_str;
	
	public long updateTime; 
	
	public String coachDate; 
	
	public String msg;
	
	public long id;
	
	public String coachTime;
	
	public long appTime;
	
	public int times;
	
	
	/*教练的相关信息*/
	public long teacher_id;
	
	public String teacher_sn;
	
	public String teacher_avatar;
	
	public String teacher_name;
	
	public float teacher_star;
	
	public long teacher_phone;
	
	public int teacher_sex;
	
	public double teacher_price;
	
	
	/*学员信息*/
	public long student_id;
	
	public String student_sn;
	
	public String student_avatar;
	
	public String student_name;
	
	public float student_star;
	
	public long student_phone;
	
	public int student_sex;
	
	/*约的球场信息*/
	public long course_id;
	public String course_abbr;
	
	public long start_time ;
	public long end_time ;
	public long pause_time ;
	public long period_time ;
	
	public double fee ;
	
	
}
