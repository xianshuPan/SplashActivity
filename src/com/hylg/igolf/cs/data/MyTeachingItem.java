package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class MyTeachingItem implements Serializable {
	private static final long serialVersionUID = 8457574924520671856L;

	public String courseName;
	public String courseAddress;
	public String coachTime;
	public int times;

	public String student1Name;
	public String student1Sn;
	public long student1Id;
	public int student1appStatus;

	public String student2Name;
	public String student2Sn;
	public long student2Id;
	public int student2appStatus;

	public long coachId;
	public long coachAppId;


	public double price;

	public int alone;

}
