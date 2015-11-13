package com.aturhelp.aturhelp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.aturhelp.common.AtUrHelpInfo;
import com.aturhelp.common.Constants;
import com.aturhelp.dbprovider.DBProvider;
import com.aturhelp.utils.AtUrHelpUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AdminActivation extends Activity{

	private DBProvider mydb = null;
	private TextView networkView = null;
	private EditText adminId = null;
	private Boolean isInternetAvailable = null;
	private HttpResponse httpResponse = null;
	private ProgressDialog ringProgressDialog;
	private String deviceId = null;
	
	protected void onResume() {
		super.onResume();
		networkView = (TextView) findViewById(R.id.adminactofflinestatus);
		AtUrHelpUtils.isNetworkAvailable(getApplicationContext(), networkView);
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_activation);
		adminId = (EditText) findViewById(R.id.admin_act_no);
		final Button activateButton = (Button) findViewById(R.id.btnAdminAct);
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();
		activateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new ServerPro().execute();
				// Disable register button
				activateButton.setClickable(false);
				// Show progress bar
				ringProgressDialog = ProgressDialog.show(AdminActivation.this, Constants.PLEASE_WAIT,	Constants.REGISTERING_WAIT, true);
				ringProgressDialog.setCancelable(false);
			}
		});
	}
	
	// Server processing
	private class ServerPro extends AsyncTask<Void, Void, String> {
		
		@Override
		protected String doInBackground(Void... params) {
			
			String output = null;
			HttpClient httpClient = new DefaultHttpClient();
			String adminActURL = AtUrHelpUtils.activateUser(adminId.getText().toString().trim(), deviceId);
			HttpGet httpGet = new HttpGet(adminActURL);
			isInternetAvailable = AtUrHelpUtils.isInternetAvailable(getApplicationContext());
			if (isInternetAvailable) {
				try {
					httpGet.setHeader(Constants.ACCEPT, Constants.APPLICATION_JSON);
					httpResponse = httpClient.execute(httpGet);
					output = AtUrHelpUtils.getASCIIContentFromEntity(httpResponse
							.getEntity());
				} catch (Exception e) {
					Log.v("Fail to get user info from the server", e+"");
					Log.e("Locatoin Type", e+"");
				}
				return output;
			} else {
				return "offline";
			}
		}
		
		protected void onPostExecute(String results) {
			ringProgressDialog.dismiss();
			if (results.equals("offline")) {
				dialog(false);
			} else {
				try {
					JSONObject object = new JSONObject(results);
					String status = (String)object.get("status");
					if (status != null && !status.equals("0") ) {
						//Invalid adminid
						dialog(false);
					} else {
						dialog(true);
					}
				} catch (Exception e) {
					Log.e("Parse Excepiton", ""+e);
				}
			}
		}
	}
	
	public void dialog(final boolean status) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		String statusMsg = null;
		if (status) {
			statusMsg = Constants.ADMIN_ACT_SUCCESS;
			mydb = new DBProvider(this);
			AtUrHelpInfo atUrHelpInfo = new AtUrHelpInfo();
			atUrHelpInfo.setIsAdmin("yes");
			boolean adminUpdateStatus = mydb.updateAtUrHelpInfoAdminStatus(atUrHelpInfo);
			if (adminUpdateStatus) {
				Log.v("Updation for admin", "admin successfully updated");
			}
			
		} else {
			statusMsg = Constants.ADMIN_ACT_FAILURE;
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
									AdminActivation.class);
						}

						startActivity(intent);
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
