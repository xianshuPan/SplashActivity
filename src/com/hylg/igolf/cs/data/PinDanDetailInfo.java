package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class PinDanDetailInfo implements Serializable {
	private static final long serialVersionUID = 8457574924520671856L;

	public String courseName;
	public String courseAddress;
	public String coachTime;
	public String studentName;
	public String studentSn;
	public String coachSn;
	public String coachName;

	public long studentId;
	public long coachId;
	public long coachAppId;

	public double distance;

	public double price;

	public int refer;
	public int emergemcy;

	public int times;

}
