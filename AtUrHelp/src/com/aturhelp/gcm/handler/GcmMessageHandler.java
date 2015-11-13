package com.aturhelp.gcm.handler;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import com.aturhelp.aturhelp.AdminLogActivity;
import com.aturhelp.aturhelp.CustomerLogActivity;
import com.aturhelp.aturhelp.R;
import com.aturhelp.common.Constants;
import com.aturhelp.gcm.receiver.GcmBroadcastReceiver;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService {

	String mes;
	String messageType;

	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		mes = intent.getStringExtra("message");
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		messageType = gcm.getMessageType(intent);

		// mes = extras.getString("title");
//		if (!getApplicationContext().getPackageName().equalsIgnoreCase(((ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity.getPackageName()))
//		{
//		// App is not in the foreground
			showToast();
//		} else {
//			
//			Handler mHandler = new Handler(getMainLooper());
//		    mHandler.post(new Runnable() {
//		        @Override
//		        public void run() {
//		        	dialog();
//		        }
//		    });
//		}
		
		Log.i("GCM",
				"Received : (" + messageType + ")  "
						+ extras.getString("title"));

		GcmBroadcastReceiver.completeWakefulIntent(intent);

	}

	public void showToast() {

		Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("AtUrHelp Service");
		builder.setSound(uri);

		Intent notificationIntent = null;
		if (mes != null && mes.equals(Constants.ADMIN_NOTIFICATION)) {
			notificationIntent = new Intent(getApplicationContext(), AdminLogActivity.class);
			builder.setContentText("You got new complaints");
		} else if (mes != null && mes.equals(Constants.CUSTOMER_NOTIFICATION)) { 
			notificationIntent = new Intent(getApplicationContext(), CustomerLogActivity.class);
			builder.setContentText("You request was resolved");
		}
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(1, builder.build());

	}
	
	public void dialog() {

		AlertDialog alertDialogBuilder = new AlertDialog.Builder(getApplicationContext()).create();
		if (mes != null && mes.equals(Constants.ADMIN_NOTIFICATION)) {
			alertDialogBuilder.setMessage("You got new complaints");	
		} else {
			alertDialogBuilder.setMessage("You request was resolved");
		}
		
		alertDialogBuilder.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alertDialogBuilder.show();
	}
	
	
}