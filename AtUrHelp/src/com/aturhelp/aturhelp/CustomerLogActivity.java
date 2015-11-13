package com.aturhelp.aturhelp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aturhelp.adapter.AdminListAdapter;
import com.aturhelp.adapter.CustomerListAdapter;
import com.aturhelp.common.Constants;
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
import android.widget.ListView;

public class CustomerLogActivity extends Activity {

	private String deviceId = null;
	private HttpResponse httpResponse = null;
	private ListView lView = null;
	private JSONArray complaintArray = null;
	private JSONObject complaintObject = null;
	private ProgressDialog ringProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_complaint_main);
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();

		new ServerPro().execute();
		ringProgressDialog = ProgressDialog.show(CustomerLogActivity.this, Constants.PLEASE_WAIT,	Constants.WORKING_ON_IT, true);
		ringProgressDialog.setCancelable(false);

		lView = (ListView) findViewById(R.id.complaintListView);
	}

	public void refresh() {
		complaintArray = null;
		complaintObject = null;
		new ServerPro().execute();
	}

	// Server processing
	private class ServerPro extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String output = null;
			HttpClient httpClient = new DefaultHttpClient();
			String logInfoURL = AtUrHelpUtils.getCustomerLogInfo(deviceId);
			HttpGet httpGet = new HttpGet(logInfoURL);
			try {
				httpGet.setHeader(Constants.ACCEPT, Constants.APPLICATION_JSON);
				httpResponse = httpClient.execute(httpGet);
				output = AtUrHelpUtils.getASCIIContentFromEntity(httpResponse
						.getEntity());
			} catch (Exception e) {
				Log.v("Fail to get user info from the server", e + "");
				Log.e("Locatoin Type", e + "");
				ringProgressDialog.dismiss();
				return null;
			}
			return output;
		}

		protected void onPostExecute(String results) {
			if (results == null) {
				dialog(Constants.FAIL_TO_GET_DATA);
			} else {
				setList(results);	
			}
			
		}

	}

	public void setList(String result) {
		try {
			if (result != null && !result.equals("")) {
				JSONObject object = new JSONObject(result);
				try {
					complaintArray = (JSONArray) object.get("help");	
				} catch (Exception e) {
					complaintObject = (JSONObject)object.get("help");
					
				}
				lView.setAdapter(new CustomerListAdapter(this, complaintArray, complaintObject));
				ringProgressDialog.dismiss();
			} else {
				ringProgressDialog.dismiss();
				dialog(Constants.NO_COMPLAINTS_FOUND);
			}
			
		} catch (JSONException e) {
			ringProgressDialog.dismiss();
			e.printStackTrace();
		}
	}
	
	
	public void dialog(String msg) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage(msg);
		alertDialogBuilder.setPositiveButton(
				Constants.BUTTON_TEXT_FOR_REG_ALERT,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent(getApplicationContext(),
								LocationActivity.class);
						startActivity(intent);
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
