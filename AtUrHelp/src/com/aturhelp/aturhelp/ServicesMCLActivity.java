package com.aturhelp.aturhelp;

import com.aturhelp.common.Constants;
import com.aturhelp.utils.AtUrHelpUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ServicesMCLActivity extends Activity{

	private TextView networkView = null;
	
	protected void onResume() {
		super.onResume();
		networkView = (TextView) findViewById(R.id.sermclofflinestatus);
		AtUrHelpUtils.isNetworkAvailable(getApplicationContext(), networkView);
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.services_mcl);
		
		//check network status.If no network is available, app will work in offline.
		networkView = (TextView) findViewById(R.id.sermclofflinestatus);
		AtUrHelpUtils.isNetworkAvailable(getApplicationContext(), networkView);
				
		final Bundle bundle = new Bundle();
		bundle.putString(Constants.SERVICES_ID, Constants.SERVICES_MCL_PARIMALA);
		bundle.putString(Constants.SERVICES_MOBILE_NO, Constants.SERVICES_MCL_PARIMALA_MOBILE_NO);
		final Button locBtn = (Button) findViewById(R.id.serMCLParimala);
		locBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ComplaintActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				
			}
		});
	}
}
