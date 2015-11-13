package com.aturhelp.aturhelp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
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
import android.widget.Spinner;
import android.widget.TextView;

public class ComplaintActivity extends Activity {
	
	private String deviceId = null;
	private String complaintSub =  null;
	private String complaintDes = null;
	private Button subBtn = null;
	private String serviceId = null;
	private String servMobileNo = null;
	private TextView networkView = null;
	private Boolean isInternetAvailable = null;
	private ProgressDialog ringProgressDialog;
	DBProvider mydb = null;
	
	protected void onResume() {
		super.onResume();
		networkView = (TextView) findViewById(R.id.comofflinestatus);
		AtUrHelpUtils.isNetworkAvailable(getApplicationContext(), networkView);
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complaint);
		Bundle bundle = this.getIntent().getExtras();
		serviceId = bundle.getString(Constants.SERVICES_ID);
		servMobileNo = bundle.getString(Constants.SERVICES_MOBILE_NO);
		
		//check network status.If no network is available, app will work in offline.
		networkView = (TextView) findViewById(R.id.comofflinestatus);
		AtUrHelpUtils.isNetworkAvailable(getApplicationContext(), networkView);
		
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();
		subBtn = (Button) findViewById(R.id.submit);
		subBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					new ServerPro().execute();
					subBtn.setClickable(false);
					//show circle
					ringProgressDialog = ProgressDialog.show(ComplaintActivity.this, Constants.PLEASE_WAIT,	Constants.SENDING_COMPLAINT, true);
					ringProgressDialog.setCancelable(false);
			}
		});
	}
	
	// Server processing
	private class ServerPro extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
			Spinner spinner = (Spinner) findViewById(R.id.spinner);
			complaintSub = spinner.getSelectedItem().toString();
			EditText textArea = (EditText) findViewById(R.id.textarea);
			complaintDes = textArea.getText().toString();
			String insertionURL = AtUrHelpUtils.getComplaintURL();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(insertionURL);
			StringEntity se = null;
			String output = null;
			HttpResponse httpResponse = null;
			JSONObject json = new JSONObject();
			//If internet is available we need to send request to server, else we need to send message to service provider regarding complaint
			isInternetAvailable = AtUrHelpUtils.isInternetAvailable(getApplicationContext());
			if (isInternetAvailable) {
				try {
					json.put(Constants.COMPLAINT_TYPE, complaintSub);
					json.put(Constants.COMPLAINT_DES, complaintDes);
					json.put(Constants.HELP_DEVICE_ID, deviceId);
					json.put(Constants.SERVICE_ID, serviceId);
					se = new StringEntity(json.toString());
					se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
							Constants.APPLICATION_JSON));
					httpPost.setEntity(se);
					httpPost.setHeader(Constants.ACCEPT, Constants.APPLICATION_JSON);
					httpPost.setHeader(HTTP.CONTENT_TYPE,
							Constants.APPLICATION_JSON);
					httpResponse = httpClient.execute(httpPost);
					output = AtUrHelpUtils.getASCIIContentFromEntity(httpResponse
							.getEntity());
				} catch (Exception e) {
					Log.v("Fail to send request to server", e+"");
					Log.e("Complaint Type", e+"");
				}
			} else {
				try {
					
					return Constants.OFFLINE_DIALOG;
					
					} catch (Exception msg) {
					Log.v("Fail to send message", msg+"");
					Log.e("Complaint Type", msg+"");
					ringProgressDialog.dismiss();
					output = null;
				}
			}
			
			return output;
		}

		protected void onPostExecute(String results) {
			ringProgressDialog.dismiss();
			subBtn.setClickable(true);
			if (results.equals(Constants.OFFLINE_DIALOG)) {
				confirmDialog();
			} else {
				dialog();	
			}
			
		}

	}

	public void dialog() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage(Constants.COMPLAINT_SUCCESS);
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
	
	public void confirmDialog() {
		
		mydb = new DBProvider(this);
		final AtUrHelpInfo helpInfo = mydb.getAtUrHelpInfo();
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage(Constants.MESSAGE_ALERT);
		alertDialogBuilder.setPositiveButton(
				Constants.BUTTON_TEXT_FOR_REG_ALERT,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						String newLine = "\n";
						String bodyMsg = "Des : "+complaintSub + newLine +"Sub : "+complaintDes + newLine + " Customer Name :" +helpInfo.getCusName();
						Intent sendIntent = new Intent(Intent.ACTION_VIEW);
						sendIntent.putExtra("sms_body", bodyMsg);
						sendIntent.putExtra("address", servMobileNo);
						sendIntent.setType("vnd.android-dir/mms-sms");
					    startActivity(sendIntent);
					}
				});
		alertDialogBuilder.setNegativeButton(Constants.BUTTON_NO, null);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
