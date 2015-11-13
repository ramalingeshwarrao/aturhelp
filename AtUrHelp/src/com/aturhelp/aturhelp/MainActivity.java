package com.aturhelp.aturhelp;


import com.aturhelp.common.AtUrHelpInfo;
import com.aturhelp.dbprovider.DBProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity{

	DBProvider mydb = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		 mydb = new DBProvider(this);
		 AtUrHelpInfo aturhelpinfo = mydb.getAtUrHelpInfo();
		 if ( aturhelpinfo != null && aturhelpinfo.getIsRegister().equals("yes")) {
			 Intent intent = new Intent(getApplicationContext(),
						LocationActivity.class);
				startActivity(intent);
		 } else {
			 Intent intent = new Intent(getApplicationContext(),
					 RegisterActivity.class);
				startActivity(intent);
		 }
	}
}
