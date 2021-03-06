package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class CoachApplyInfoReqParam implements Serializable {
	
	private static final long serialVersionUID = -3184496627734075998L;

	public String sn;
	
	public int id = -1 ;
	
	public String avatar;
	public String name;
	public int age = -1;
	public String age_str = "";
	public int sex = -1;
	public int type;
	
	public String id_fron_name;
	public String id_back_name;
	public int teach_age;

	public int ball_age;

	public String customer_region;

	public String industry;
	
	public long courseid;

	public String state;
	
	public String special;
	public String course_abbr;
	public String course_name;
	public String course_address;
	public String graduate_name;
	public String certificate_name;
	public String award_name;
	
	public double lat;
	
	public double lng;

	public float star;

	public long teacing_count;
	
	public int audit;
	
	public String auditString ;
	
}
