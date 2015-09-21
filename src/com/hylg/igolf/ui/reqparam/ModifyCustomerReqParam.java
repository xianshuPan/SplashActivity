package com.hylg.igolf.ui.reqparam;

import java.io.Serializable;

public class ModifyCustomerReqParam implements Serializable {
	
	private static final long serialVersionUID = -3184496627734075998L;
	public ModifyCustomerReqParam() {
//		this.phone = phone;
		yearsExp = Integer.MAX_VALUE;
		sex = Integer.MAX_VALUE;
		age = Integer.MAX_VALUE;
		city = "";
		industry = "";
	}
	public String sn;
	public String phone;
	public String oldPwd;
	public String newPwd;
	public String nickname;
	public int yearsExp;
	public int sex;
	public String state;
	public String city;
	public String industry; 
	public int age;
}
