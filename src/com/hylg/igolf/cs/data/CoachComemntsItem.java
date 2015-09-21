package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class CoachComemntsItem implements Serializable {
	private static final long serialVersionUID = 1356316048962664806L;
	
	public long id; // 约球单ID
	public int appid; // 约球类型：开放式，一对一
	public float star; // 约球状态
	public String content; // 显示状态
	public long commentTime; // 显示内容
	
	public int coach_id;
	
	public int student_id;
	
	public String student_sn;
	
	public String student_avatar;
	
	public String student_nick_name;
}
