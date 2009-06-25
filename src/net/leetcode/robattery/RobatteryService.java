/*
 *  Copyright (C) 2009    John Reese, LeetCode.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

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
	private final String LOGCAT = "RobatteryService";
	
	/**
	 * How long the service should wait between checks and/or notifications.
	 */
	private static final int IDLETIME = 1000 * 60 * 5; // five minutes
	
	private boolean registered = false;
	
	/**
	 * The latest battery status representation.
	 */
	private RobatteryStatus status = null;
	
	/**
	 * Receiver for asynchronous battery change messages from the OS.
	 */
	private IntentFilter batteryIntentFilter = new IntentFilter( Intent.ACTION_BATTERY_CHANGED );
	private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive( Context c, Intent intent ) {
			Log.d(LOGCAT,"onReceive");
			
			status = new RobatteryStatus(intent);
		}
	};
	
	/**
	 * Timeout-based update method for sending notifications when the battery is low.
	 */
	private Handler idler = new Handler() {
		@Override
		public void handleMessage(Message m) {
			this.sendEmptyMessageDelayed(0, RobatteryService.IDLETIME);
			
			if ( status.level <= 20 ) {
				sendNotification();
			}
		}
	};
	
	/**
	 * Handle service bindings from connecting tasks.
	 */
    @Override
	public IBinder onBind(Intent intent) {
    	Log.d(LOGCAT,"onBind");
    	
		this.register();
		
		return new RobatteryBinder(this);
	}

    
    /**
     * Service has been created.
     */
    @Override
    public void onCreate() {
    	super.onCreate();
    	Log.d(LOGCAT,"onCreate");
    }
    
    /**
     * Service is ready to begin.  Set the tick timeout and register event listeners.
     */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
    	Log.d(LOGCAT,"onStart");
		
    	this.idler.sendEmptyMessageDelayed(0, RobatteryService.IDLETIME);
		this.register();
	}
	
	/**
	 * Service is to be stopped and/or cleaned up.  Unregister event listeners. 
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
    	Log.d(LOGCAT,"onDestroy");
		
		this.unregister();
    	this.idler.removeMessages(0);
	}
	
	public RobatteryStatus getStatus() {
		return status;
	}
	
	public int getLevel() {
		return status.level;
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
		String title = "Battery Status: " + String.valueOf(status.level) + "%";
		
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
