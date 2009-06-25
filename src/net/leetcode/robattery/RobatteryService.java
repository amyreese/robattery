package net.leetcode.robattery;

import android.net.Uri;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore.Audio;
import android.util.Log;

public class RobatteryService extends Service {
	private static final int IDLETIME = 1000 * 60 * 5; // five minutes
	private final String LOGCAT = "RobatteryService";
	
	private boolean registered = false;
	private int batteryLevel = -1;
	
	private IntentFilter batteryIntentFilter = new IntentFilter( Intent.ACTION_BATTERY_CHANGED );
	private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive( Context c, Intent intent ) {
			Log.d(LOGCAT,"onReceive");
			batteryLevel = intent.getIntExtra( "level", 0 );
		}
	};
	
	private Handler idler = new Handler() {
		@Override
		public void handleMessage(Message m) {
			this.sendEmptyMessageDelayed(0, RobatteryService.IDLETIME);
			
			if ( batteryLevel <= 20 ) {
				sendNotification();
			}
		}
	};
	
    @Override
	public IBinder onBind(Intent intent) {
    	Log.d(LOGCAT,"onBind");
    	
		this.register();
		
		return new RobatteryBinder(this);
	}

    
    @Override
    public void onCreate() {
    	super.onCreate();
    	Log.d(LOGCAT,"onCreate");
    }
    
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
    	Log.d(LOGCAT,"onStart");
		
    	this.idler.sendEmptyMessageDelayed(0, RobatteryService.IDLETIME);
		this.register();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
    	Log.d(LOGCAT,"onDestroy");
		
		this.unregister();
    	this.idler.removeMessages(0);
	}
	
	public int getLevel() {
		return batteryLevel;
	}
		
	private void register() {
		if ( !registered ) {
	    	registerReceiver(batteryReceiver, batteryIntentFilter);
	    	registered = true;
		}
	}
	
	private void unregister() {
		if ( registered ) {
			unregisterReceiver(batteryReceiver);
			registered = false;
		}
	}
	
	private void sendNotification() {
		Context context = getApplicationContext();
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, Robattery.class), 0);
		String title = "Battery Status: " + String.valueOf(batteryLevel) + "%";
		
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.robot, title, System.currentTimeMillis());
		notification.setLatestEventInfo(context, title, "Robattery", pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		notification.ledARGB = 0xff000000;
		notification.ledOnMS = 300;
		notification.ledOffMS = 200;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		
		long[] vibration = {200, 100};
		notification.vibrate = vibration;
		
		notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");

		nm.cancelAll();
		nm.notify(1, notification);
	}
}
