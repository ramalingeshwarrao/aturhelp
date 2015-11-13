package com.aturhelp.aturhelp;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.aturhelp.common.AtUrHelpInfo;
import com.aturhelp.common.Constants;
import com.aturhelp.dbprovider.DBProvider;
import com.aturhelp.utils.AtUrHelpUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LocationActivity extends Activity{


	private TextView networkView = null;
	private String deviceId = null;
	private Boolean isInternetAvailable = null;
	private HttpResponse httpResponse = null;
	private DBProvider mydb = null;
	
	
	protected void onResume() {
		super.onResume();
		networkView = (TextView) findViewById(R.id.locofflinestatus);
		AtUrHelpUtils.isNetworkAvailable(getApplicationContext(), networkView);
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locations);
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();
		mydb = new DBProvider(this);
		//need to validate whether customer exist or not
		new ServerPro().execute();
		
		final Button pgrlBtn = (Button) findViewById(R.id.locPiduguralla);
		final Button mclBtn = (Button) findViewById(R.id.locMacherla);
		
		//check network status.If no network is available, app will work in offline.
		networkView = (TextView) findViewById(R.id.locofflinestatus);
		AtUrHelpUtils.isNetworkAvailable(getApplicationContext(), networkView);
		
		pgrlBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ServicesPGRLActivity.class);
				startActivity(intent);
			}
		});
		
		mclBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ServicesMCLActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.adminactivation:
			Intent intent = new Intent(getApplicationContext(),
					AdminActivation.class);
			startActivity(intent);
			return true;
		case R.id.complaintList:
			Intent compIntent = new Intent(getApplicationContext(),
					AdminLogActivity.class);
			startActivity(compIntent);
			return true;
		case R.id.customerComplaintList:
			Intent custCompIntent = new Intent(getApplicationContext(),
					CustomerLogActivity.class);
			startActivity(custCompIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
		// Server processing
		private class ServerPro extends AsyncTask<Void, Void, String> {
			
			@Override
			protected String doInBackground(Void... params) {
				String output = null;
				AtUrHelpInfo atUrHelpInfo = null;
				HttpClient httpClient = new DefaultHttpClient();
				String userInfoURL = AtUrHelpUtils.getUserInfo(deviceId);
				HttpGet httpGet = new HttpGet(userInfoURL);
				//If internet is available we need to send request to server, else we need to send message to service provider regarding complaint
				isInternetAvailable = AtUrHelpUtils.isInternetAvailable(getApplicationContext());
				atUrHelpInfo = mydb.getAtUrHelpInfo();
				if (atUrHelpInfo != null && atUrHelpInfo.getIsRegisterLoc() != null && atUrHelpInfo.getIsRegisterLoc().equals("no")) {
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
				return "registered";
				
			}
			
			protected void onPostExecute(String results) {
				// need to validate whether record exist or not
				if (!results.equals("registered") && isInternetAvailable != null && isInternetAvailable) {
					try {
						JSONObject object = new JSONObject(results);
						String status = (String)object.get("status");
						if (status != null && !status.equals("0") ) {
							// Somewhat the data is not register for this user, we need to show register page for this user.
							Intent intent = new Intent(getApplicationContext(),
									RegisterActivity.class);
							startActivity(intent);
						} else {
							mydb.updateAtUrHelpInfoAtLoc();
						}
					} catch (JSONException e) {
						Log.e("Parsing result",e+"");
					}
					
					System.out.println("the output is "+results);
				}
				
			}
		}
}
