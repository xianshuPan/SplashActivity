package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class CourseInfo implements Serializable {
	private static final long serialVersionUID = 6268704012533912964L;
	
	public CourseInfo() {
		id = Long.MAX_VALUE;
	}
	
	// 清除信息
	public void clearCourseInfo() {
		id = Long.MAX_VALUE;
		abbr = "";
		name = "";
	}
	
	public long id;
	public String abbr; // 球场简称
	public String name; // 球场名称
}
