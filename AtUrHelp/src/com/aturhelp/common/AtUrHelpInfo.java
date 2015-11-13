package com.aturhelp.common;

import java.io.Serializable;

public class AtUrHelpInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4958101142768237941L;
	
	private String isRegister;
	private String isAdmin;
	private String cusName;
	private String mobileNo;
	private String email;
	// This field we are using in location page
	private String isRegisterLoc;
	private String servicesList;
	
	
	public String getIsRegister() {
		return isRegister;
	}
	public void setIsRegister(String isRegister) {
		this.isRegister = isRegister;
	}
	public String getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(String isAdmin) {
		this.isAdmin = isAdmin;
	}
	public String getCusName() {
		return cusName;
	}
	public void setCusName(String cusName) {
		this.cusName = cusName;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIsRegisterLoc() {
		return isRegisterLoc;
	}
	public void setIsRegisterLoc(String isRegisterLoc) {
		this.isRegisterLoc = isRegisterLoc;
	}
	public String getServicesList() {
		return servicesList;
	}
	public void setServicesList(String servicesList) {
		this.servicesList = servicesList;
	}

}
