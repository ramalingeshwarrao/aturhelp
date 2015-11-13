package com.aturhelp.aturhelp;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.aturhelp.aturhelp.R;
import com.aturhelp.common.AtUrHelpInfo;
import com.aturhelp.common.Constants;
import com.aturhelp.dbprovider.DBProvider;
import com.aturhelp.srevices.NetworkStatus;
import com.aturhelp.utils.AtUrHelpUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity {

	private DBProvider mydb = null;
	private String deviceId = null;
	private String requestId = null;
	private GoogleCloudMessaging gcm;
	private boolean isValidationPass;
	private EditText email = null;
	private EditText mobileNo = null;
	private EditText fullName = null;
	private TextView networkView = null;
	private AtUrHelpInfo helpInfo = new AtUrHelpInfo();
	private ProgressDialog ringProgressDialog;
	private Map<String, EditText> values = new LinkedHashMap<String, EditText>(3);


	protected void onResume() {
		super.onResume();
		networkView = (TextView) findViewById(R.id.regofflinestatus);
		AtUrHelpUtils.isNetworkAvailable(getApplicationContext(), networkView);
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		// GCM Generation
		new RequestIdGen().execute();
		// Register in database
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();
		final Button regitButton = (Button) findViewById(R.id.btnRegister);
		email = (EditText) findViewById(R.id.reg_email);
		mobileNo = (EditText) findViewById(R.id.reg_mobileno);
		fullName = (EditText) findViewById(R.id.reg_fullname);
		
		//check network status.If no network is available, app will work in offline.
		networkView = (TextView) findViewById(R.id.regofflinestatus);
		AtUrHelpUtils.isNetworkAvailable(getApplicationContext(), networkView);
		
		regitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				values.put(Constants.FULL_NAME, fullName);
				values.put(Constants.EMAIL, email);
				values.put(Constants.MOBILE_NO, mobileNo);
				isValidationPass = AtUrHelpUtils.validations(getApplicationContext(), values);
				if (isValidationPass) {
					new ServerPro().execute();
					// Disable register button
					regitButton.setClickable(false);
					// Show progress bar
					ringProgressDialog = ProgressDialog.show(RegisterActivity.this, Constants.PLEASE_WAIT,	Constants.REGISTERING_WAIT, true);
					ringProgressDialog.setCancelable(false);
				}
			}
		});
	}

	private class RequestIdGen extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging
							.getInstance(getApplicationContext());
					requestId = gcm.register(Constants.PROJECT_NUMBER);
					Log.d("Register GCM ", requestId);
				}
			} catch (Exception e) {
				Log.e("Register", "Fail to create gcm request id ", e);
			}
			return requestId;
		}

		protected void onPostExecute(String reqId) {
			requestId = reqId;
		}

	}

	//Server processing
	private class ServerPro extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String insertionURL = AtUrHelpUtils.getCustomerRegistrationURL();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(insertionURL);
			StringEntity se = null;
			String output = null;
			JSONObject json = new JSONObject();
			try {
				json.put(Constants.EMAIL, email.getText());
				json.put(Constants.MOBILE_NO, mobileNo.getText());
				json.put(Constants.FULL_NAME, fullName.getText());
				json.put(Constants.DEVICE_ID, deviceId);
				json.put(Constants.REQUEST_ID, requestId);
				helpInfo.setCusName(fullName.getText().toString());
				helpInfo.setMobileNo(mobileNo.getText().toString());
				helpInfo.setEmail(email.getText().toString());
				se = new StringEntity(json.toString());
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						Constants.APPLICATION_JSON));
				httpPost.setEntity(se);
				httpPost.setHeader(Constants.ACCEPT, Constants.APPLICATION_JSON);
				httpPost.setHeader(HTTP.CONTENT_TYPE,
						Constants.APPLICATION_JSON);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				output = AtUrHelpUtils.getASCIIContentFromEntity(httpResponse
						.getEntity());
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Fail to post data",e+"");
				ringProgressDialog.dismiss();
				return null;
			}
			return output;
		}

		protected void onPostExecute(String results) {
			//Close progress bar
			ringProgressDialog.dismiss();
			//Button registerButton = (Button) findViewById(R.id.btnRegister);
			//registerButton.setClickable(true);
			// If there is no internet connection we need to throw error like internet is not available
			if (results == null) {
				dialog(false);	
			} else {
				dialog(true);
			}
			
		}
		
	}
	
	public void dialog(final boolean status) {
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		String statusMsg = null;
		if (status) {
			statusMsg = Constants.REG_SUCCESS;
			mydb = new DBProvider(this);
			helpInfo.setIsRegister("yes");
			boolean insertstatus = mydb.insertContact(helpInfo);
			if (insertstatus) {
				Log.d("Data Insertion", "Successfully inserted");
			}
		} else {
			statusMsg = Constants.REG_FAILURE;
		}
		alertDialogBuilder.setMessage(statusMsg);
		alertDialogBuilder.setPositiveButton(
				Constants.BUTTON_TEXT_FOR_REG_ALERT,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = null;
						if (status) {
							intent = new Intent(getApplicationContext(),
									LocationActivity.class);
						} else {
							intent = new Intent(getApplicationContext(),
									RegisterActivity.class);
						}

						startActivity(intent);
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	
}
