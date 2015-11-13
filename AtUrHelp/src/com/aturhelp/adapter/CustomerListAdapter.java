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
import com.aturhelp.aturhelp.CustomerLogActivity;
import com.aturhelp.aturhelp.R;
import com.aturhelp.aturhelp.RegisterActivity;
import com.aturhelp.common.Constants;
import com.aturhelp.utils.AtUrHelpUtils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CustomerListAdapter extends BaseAdapter{

	
	CustomerLogActivity logActivity = null;
	private JSONArray complaintList = null;
	private JSONObject complaintObj = null;
	
	public CustomerListAdapter(CustomerLogActivity logActivity, JSONArray complaintList, JSONObject complaintObj) {
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
		View v=inflater.inflate(R.layout.customer_complaint_list, null);
		
		TextView logSubject = (TextView) v.findViewById(R.id.cuslogsubject);
		TextView logDes = (TextView) v.findViewById(R.id.custlogdescription);
		TextView logStatus = (TextView) v.findViewById(R.id.custlogstatus);
		TextView logTicket = (TextView) v.findViewById(R.id.custlogticket);
		JSONObject object = null;
		
		try {
			if (complaintList != null) {
				object = complaintList.getJSONObject(position);	
			} else {
				object = complaintObj;
			}
			String status = object.get("status").toString();
			String statString = null;
			if (status != null && status.equals("1")) {
				statString = Constants.CLOSED;
			} else {
				statString = Constants.OPEN;
			}
			logSubject.setText(object.get("htype").toString());
			logDes.setText(object.get("hdes").toString());
			logStatus.setText(statString);
			logTicket.setText(object.get("ticketno").toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return v;
	}
}
