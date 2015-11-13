package com.aturhelp.common;

public class Constants {

	public static final String BASE_URL="http://aturhelp.com/aturhelp";
	public static final String RESOURCE_NAME = "/hr";
	public static final String ADMIN_INSERTION = "/insert/admin";
	public static final String CUSTOMER_INSERTIOIN = "/insert/user";
	public static final String CUSTOMER_INFO_BY_DEVICE_ID = "/customerexist?deviceid=";
	public static final String ACTIVATE_ADMIN = "/ata?adminid=";
	public static final String UPDATE_CUSTOMER_INFO = "/update";
	public static final String COMPLAINT = "/help";
	public static final String UPDATE_TICKET = "/updateticketid";
	public static final String GET_LOG_DATA = "/logdata?deviceid=";
	public static final String GET_CUSTOMER_LOG_DATA = "/customerlogdata?deviceid=";
	
	//content types 
	public static final String APPLICATION_JSON = "application/json";
	public static final String ACCEPT = "Accept";
	public static final String CONTENT_TYPE = "Content-type";
	
	//rest keys 
	public static final String EMAIL = "email";
	public static final String MOBILE_NO = "mobileno";
	public static final String FULL_NAME = "name";
	public static final String DEVICE_ID = "deviceid";
	public static final String HELP_DEVICE_ID = "hid";
	public static final String REQUEST_ID = "requestid";
	public static final String COMPLAINT_TYPE = "htype";
	public static final String COMPLAINT_DES = "hdes";
	public static final String SERVICE_ID = "hserid";
	public static final String PASSWORD = "password";
	public static final String RETRYPASSWORD = "retrypassword";
	
	//GCM 
	public static final String PROJECT_NUMBER = "84376433817";
	
	//Error message
	public static final String INVALID_EMAIL="Invalid Email";
	public static final String HAVE_VALUE = " can not be empty";
	public static final String PASSWORD_MATCH_ERROR = "Password Does Not Match";
	public static final String INVALID_PASSWORD ="Invalid Password";
	
	//Alert Messages
	public static final String REG_SUCCESS = "Registration successful";
	public static final String ADMIN_ACT_SUCCESS = "Successfully Activated";
	public static final String REG_FAILURE = "Unsuccessful Registration, kindly check internet connection is available or not";
	public static final String ADMIN_ACT_FAILURE = "Unsuccessful admin activation, kindly check internet connection is available or not/Invalid Id";
	public static final String COMPLAINT_SUCCESS = "Successfully submitted";
	public static final String NO_COMPLAINTS_FOUND = "No Complaints";
	public static final String OFFLINE_DIALOG = "OFFLINE_DIALOG";
	public static final String BUTTON_TEXT_FOR_REG_ALERT = "Ok";
	public static final String BUTTON_NO = "No";
	public static final String MESSAGE_ALERT = "You are in offline, sent complaint through SMS";
	public static final String RESPOND_MSG_ALERT = "Respond through SMS";
	public static final String PLEASE_WAIT = "Please wait ...";
	public static final String REGISTERING_WAIT = "Registering ...";
	public static final String ADMIN_WAIT = "Activating ...";
	public static final String SENDING_COMPLAINT = "Submitting request ...";
	public static final String WORKING_ON_IT = "Working on it ...";
	public static final String FAIL_TO_GET_DATA = "Unable to get data";
	public static final String OPEN = "OPEN";
	public static final String CLOSED = "CLOSED";
	public static final String ADMIN_NOTIFICATION = "ADMIN_NOTIFICATION";
	public static final String CUSTOMER_NOTIFICATION = "CUSTOMER_NOTIFICATION";
	
	//OFFLine message
	public static final String APP_IS_IN_ONLINE = "You are working in offline";
	
	//DB 
	public static final String DATABASE_NAME = "aturhelp.db";
	public static final String ATURHELP_TABLE_NAME = "aturhelpinfo";
	public static final String ATURHELP_COL_IS_REGISTER = "is_register";
	public static final String ATURHELP_COL_IS_ADMIN = "is_admin";
	public static final String ATURHELP_COL_CUS_NAME = "cus_name";
	public static final String ATURHELP_COL_EMAIL = "email";
	public static final String ATURHELP_COL_MOBILE_NUMBER = "mobileno";
	public static final String ATURHELP_COL_LOC_IS_REGISTER = "is_register_loc";
	
	//Services
	public static final String SERVICES_ID = "SER_ID";
	public static final String SERVICES_MOBILE_NO = "mobileno";
	public static final String SERVICES_PGRL_PARIMALA = "SER_PGRL_PARIMALA";
	public static final String SERVICES_PGRL_PARIMALA_MOBILE_NO = "9959321853";
	public static final String SERVICES_MCL_PARIMALA = "SER_MCL_PARIMALA";
	public static final String SERVICES_MCL_PARIMALA_MOBILE_NO = "9959321853";
	
	
	
	
	
}
