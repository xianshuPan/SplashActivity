package com.hylg.igolf.ui.hall.data;

import java.io.Serializable;

public class PlanSubmitInfo implements Serializable {
	private static final long serialVersionUID = -5985031211778809092L;
	public PlanSubmitInfo() {
		this(-1);
	}
	public PlanSubmitInfo(int index) {
		this.index = index;
	}
	public void refreshPlan(PlanSubmitInfo plan) {
		index = plan.index;
		teeCourse = plan.teeCourse;
		courseStr = plan.courseStr;
		teeTime = plan.teeTime;
		timeStr = plan.timeStr;
		region = plan.region;
	}
	public int index;
	public long teeCourse;
	public String courseStr;
	public long teeTime;
	public String timeStr;

	public String region;

	public String log() {
		return "PlanSubmitInfo:: \n" + 
					"\nindex: " + index +
					"\nteeCourse: " + teeCourse + 
					"\ncourseStr: " + courseStr + 
					"\nteeTime: " + teeTime + 
					"\ntimeStr: " + timeStr;
	}
}
