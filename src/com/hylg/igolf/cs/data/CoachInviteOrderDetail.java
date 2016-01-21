package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class CoachInviteOrderDetail implements Serializable {
	private static final long serialVersionUID = 1356316048962664806L;
	
	public int status;
	public String status_str;
	public String coachDate;
	public String msg;
	public long id;
	public int times;
	public int type;
	public int alone_status;
	public double ratio;

	public double originalPrice ;
	public double discountPrice ;

	/*约的球场信息*/
	public long course_id;
	public String course_abbr;
	public String course_state;
	public String course_address;
	//public String coachTime;
	
	//public long appTime;

	/*教练的相关信息*/
	public long teacher_id;
	public long teacher_coach_id;
	public String teacher_sn;
	public String teacher_avatar;
	public String teacher_name;
	public float teacher_star;
	public String teacher_phone;
	public long teacher_experience;
	public int teacher_sex;
	public int teacher_type;
	public int teacher_ball_age;
	public double teacher_price;
	
	
	/*学员信息*/
	public long student1_id;
	public String student1_sn;
	public String student1_avatar;
	public String student1_name;
	public float student1_star;
	public String student1_phone;
	public int student1_sex;
	public double student1_payment_amount;
	public int student1_payment_status;
	public int student1_ball_age;
	public int student1_experiment;
	public int student1_comment_status;
	public float student1_comment_rating;
	public String student1_comment_content;

	/*学员信息*/
	public long student2_id;
	public String student2_sn;
	public String student2_avatar;
	public String student2_name;
	public float student2_star;
	public String student2_phone;
	public int student2_sex;
	public double student2_payment_amount;
	public int student2_payment_status;
	public int student2_ball_age;
	public int student2_experiment;
	public int student2_comment_status;
	public float student2_comment_rating;
	public String student2_comment_content;

	//public int attention ;
	//public double comment_star;
	//public String comment_content;

	public String refuse_content;
}
