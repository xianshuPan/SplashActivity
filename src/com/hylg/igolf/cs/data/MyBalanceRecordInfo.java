package com.hylg.igolf.cs.data;

import java.io.Serializable;

public class MyBalanceRecordInfo implements Serializable {
	private static final long serialVersionUID = 8457574924520671856L;
	
	public int id;
	public int custid;
	public double amount;
	public int type;
	public long dealTime;
	public int status;

	public String from_who;

}
