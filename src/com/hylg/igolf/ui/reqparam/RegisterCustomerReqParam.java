package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class RegisterCustomerReqParam implements Serializable {
	private static final long serialVersionUID = -4027431832281174639L;

	public RegisterCustomerReqParam(String phone) {
		this.phone = phone;
		yearsExp = Integer.MAX_VALUE;
		sex = Integer.MAX_VALUE;
		age = Integer.MAX_VALUE;
		city = "";
		industry = "";
	}
	
	public String phone;
	public String password;
	public String nickname;
	public int yearsExp;
	public int sex;
	public String city; // key
	public String industry; // key
	public int age;
}
