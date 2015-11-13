package com.aturhelp.adapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aturhelp.aturhelp.AdminLogActivity;
import com.aturhelp.aturhelp.R;
import com.aturhelp.aturhelp.RegisterActivity;
import com.aturhelp.common.AtUrHelpInfo;
import com.aturhelp.common.Constants;
import com.aturhelp.dbprovider.DBProvider;
import com.aturhelp.utils.AtUrHelpUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AdminListAdapter extends BaseAdapter{

	
	private HttpResponse httpResponse = null;
	AdminLogActivity logActivity = null;
	private JSONArray complaintList = null;
	private JSONObject complaintObj = null;
	private ProgressDialog ringProgressDialog;
	
	public AdminListAdapter(AdminLogActivity logActivity, JSONArray complaintList, JSONObject complaintObj) {
		this.logActivity=logActivity;
		this.complaintList = complaintList;
		this.complaintObj = complaintObj;
	}
	
	@Override
	public int getCount() {
		if (complaintList != null) {
			return complaintList.length(); 	
		} else {
			return 1;
		}
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = LayoutInflater.from(logActivity);
		View v=inflater.inflate(R.layout.admin_complaint_list, null);
		
		TextView logSubject = (TextView) v.findViewById(R.id.logsubject);
		TextView logDes = (TextView) v.findViewById(R.id.logdescription);
		TextView logticket = (TextView) v.findViewById(R.id.logticket);
		Button btnClose = (Button) v.findViewById(R.id.logclose);
		Button btnRespond = (Button) v.findViewById(R.id.logrespond);
		JSONObject object = null;
		
		String ticketId = "";
		String subject = "";
		String description = "";
		String mobileNo = "";
		String email = "";
		try {
			if (complaintList != null) {
				object = complaintList.getJSONObject(position);	
			} else {
				object = complaintObj;
			}
			subject = object.get("htype").toString();
			description = object.get("hdes").toString();
			mobileNo = object.get("mobileno").toString();
			email = object.get("email").toString();
			logSubject.setText(subject);
			logDes.setText(description);
			ticketId = object.get("ticketno").toString();
			logticket.setText(ticketId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		final String ticket = ticketId;
		final String fsub = subject;
		final String fdes = description;
		final String fMobileNo = mobileNo;
		btnClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("the ticket id is "+ticket);
				new ServerPro().execute(ticket, fsub, fdes);
				ringProgressDialog = ProgressDialog.show(logActivity, Constants.PLEASE_WAIT,	Constants.WORKING_ON_IT, true);
				ringProgressDialog.setCancelable(false);
			}
		});
		
		btnRespond.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				confirmDialog(ticket, fMobileNo);
			}
		});
		
		
		return v;
	}
	
	// Server processing
	private class ServerPro extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String id = params[0].toString();
			String sub = params[1].toString();
			String des = params[2].toString();
			System.out.println("the ticket id is123 "+id);
			String output = null;
			StringEntity se = null;
			HttpClient httpClient = new DefaultHttpClient();
			String updateTicketURL = AtUrHelpUtils.updateTicketId();
			HttpPost httpPost = new HttpPost(updateTicketURL);
			JSONObject json = new JSONObject();
			try {
				json.put("ticketno", id);
				json.put("htype", sub);
				json.put("hdes", des);
				se = new StringEntity(json.toString());
				httpPost.setEntity(se);
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
				Log.v("Fail to get user info from the server", e + "");
				Log.e("Locatoin Type", e + "");
				ringProgressDialog.dismiss();
				return null;
			}
			return output;
		}

		protected void onPostExecute(String results) {
			ringProgressDialog.dismiss();
			logActivity.refresh();
		}

	}
	
	public void confirmDialog(final String ticketid, final String mobileNo) {


		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(logActivity);
		alertDialogBuilder.setMessage(Constants.RESPOND_MSG_ALERT);
		alertDialogBuilder.setPositiveButton(
				Constants.BUTTON_TEXT_FOR_REG_ALERT,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						String bodyMsg = "We received your ticket "+ticketid +" will solve as early as possible"; 
						Intent sendIntent = new Intent(Intent.ACTION_VIEW);
						sendIntent.putExtra("sms_body", bodyMsg);
						sendIntent.putExtra("address", mobileNo);
						sendIntent.setType("vnd.android-dir/mms-sms");
						logActivity.startActivity(sendIntent);
					}
				});
		alertDialogBuilder.setNegativeButton(Constants.BUTTON_NO, null);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}
