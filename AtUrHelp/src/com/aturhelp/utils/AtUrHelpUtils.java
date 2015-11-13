package com.aturhelp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.provider.Settings.Secure;
import android.provider.SyncStateContract.Helpers;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aturhelp.common.Constants;
import com.aturhelp.srevices.NetworkStatus;

public class AtUrHelpUtils {
	
	private static final String SHA_256 = "SHA256";

	private static Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
					+ "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
					+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
					+ "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
					+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
					+ "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$");

	public static String getCustomerRegistrationURL() {
		return Constants.BASE_URL + Constants.RESOURCE_NAME
				+ Constants.CUSTOMER_INSERTIOIN;
	}
	
	public static String updateTicketId() {
		return Constants.BASE_URL + Constants.RESOURCE_NAME + Constants.UPDATE_TICKET;
	}
	
	public static String getComplaintURL() {
		return Constants.BASE_URL + Constants.RESOURCE_NAME + Constants.COMPLAINT; 
	}
	
	public static String getUserInfo(String deviceId) {
		return Constants.BASE_URL + Constants.RESOURCE_NAME + Constants.CUSTOMER_INFO_BY_DEVICE_ID+deviceId;
	}
	
	public static String activateUser(String adminId, String deviceId) {
		return Constants.BASE_URL + Constants.RESOURCE_NAME + Constants.ACTIVATE_ADMIN+adminId+"&deviceid="+deviceId;
	}
	
	public static String getLogInfo(String deviceId) {
		return Constants.BASE_URL + Constants.RESOURCE_NAME + Constants.GET_LOG_DATA+deviceId;
	}
	
	public static String getCustomerLogInfo(String deviceId) {
		return Constants.BASE_URL + Constants.RESOURCE_NAME + Constants.GET_CUSTOMER_LOG_DATA+deviceId;
	}

	public static String getASCIIContentFromEntity(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = null;
		StringBuffer out = null;
		if (entity != null) {
			
		in = entity.getContent();
		out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);
			if (n > 0)
				out.append(new String(b, 0, n));
		}
		return out.toString();
		}
		return "";
	}

	public static boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	public static boolean validations(Context context, Map<String, EditText> values) {
		Set<String> keys = values.keySet();
		for (String key : keys) {
			EditText editText = values.get(key);
			String value = editText.getText().toString();
			if (value == null || value.equals("") ) {
				Toast.makeText(context, key+Constants.HAVE_VALUE, Toast.LENGTH_SHORT).show();
				return false;
			}
			
			// Email validation
			if (key.equals(Constants.EMAIL)) {
				if (!AtUrHelpUtils.checkEmail(editText.getText().toString().trim())) {
					Toast.makeText(context, Constants.INVALID_EMAIL, Toast.LENGTH_SHORT)
							.show();
					return false;
				}		
			}
		}
		return true;
	}
	
	public static boolean isInternetAvailable(Context context) {
		return NetworkStatus.getInstance(context).isOnline();
	}
	
	// We will validate whether network is available or not.
	public static void isNetworkAvailable(Context context, TextView view) {
		
		if(isInternetAvailable(context)) {
			view.setText("");
		} else  {
			view.setText(Constants.APP_IS_IN_ONLINE);
			view.setTextColor(ColorStateList.valueOf(Color.RED));
		}
	}
	
}
