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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore.Audio;
import android.util.Log;

public class RobatteryService extends Service {
	private static final String LOGCAT = "RobatteryService";
	
	/**
	 * How long the service should wait between checks and/or notifications.
	 */
	private static final int PREPTIME = 1000 * 10; // ten seconds
	
	/**
	 * How long the service should wait between checks and/or notifications.
	 */
	private static final int IDLETIME = 1000 * 60 * 5; // five minutes
	
	private boolean started = false;
	private boolean registered = false;
	
	/**
	 * The latest battery status representation.
	 */
	private RobatteryStatus battery = null;
	
	/**
	 * Receiver for asynchronous battery change messages from the OS.
	 */
	private IntentFilter batteryIntentFilter = new IntentFilter( Intent.ACTION_BATTERY_CHANGED );
	private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive( Context c, Intent intent ) {
			Log.d(LOGCAT,"onReceive");
			
			battery = new RobatteryStatus(intent);
		}
	};
	
	/**
	 * Handler for restarting the service in case of a crash.
	 */
	private Handler starter = new Handler() {
		@Override
		public void handleMessage(Message m) {
			Log.d(LOGCAT, "starter");
			
			if ( !started ) {
				Log.i(LOGCAT, "Manually starting RobatteryService.");
				
				Intent robatteryServiceIntent = new Intent();
				robatteryServiceIntent.setComponent(new ComponentName(getApplicationContext(), "net.leetcode.robattery.RobatteryService"));
				
				startService(robatteryServiceIntent);
			}
		}
	};
	
	/**
	 * Timeout-based update method for sending notifications when the battery is low.
	 */
	private Handler notifier = new Handler() {
		@Override
		public void handleMessage(Message m) {
			// null means the status hasn't yet been propagated from the system event
			if ( battery == null ) {
				this.sendEmptyMessageDelayed(0, PREPTIME);
				
			} else {
				this.sendEmptyMessageDelayed(0, IDLETIME);
			
				if ( battery.level <= 25 && (
						battery.status == BatteryManager.BATTERY_STATUS_DISCHARGING ||
						battery.status == BatteryManager.BATTERY_STATUS_NOT_CHARGING ) ) {
					sendNotification();
				}
			}
		}
	};
	
	/**
	 * Handle service bindings from connecting tasks.
	 */
    @Override
	public IBinder onBind(Intent intent) {
    	Log.d(LOGCAT,"onBind");
    	
		register();
		
		return new RobatteryBinder(this);
	}

    
    /**
     * Service has been created.
     */
    @Override
    public void onCreate() {
    	super.onCreate();
    	Log.d(LOGCAT,"onCreate");
    	
    	starter.sendEmptyMessageDelayed(0, PREPTIME);
    }
    
    /**
     * Service is ready to begin.  Set the tick timeout and register event listeners.
     */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
    	Log.d(LOGCAT,"onStart");
		
    	started = true;
    	notifier.sendEmptyMessageDelayed(0, PREPTIME);
		register();
	}
	
	/**
	 * Service is to be stopped and/or cleaned up.  Unregister event listeners. 
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
    	Log.d(LOGCAT,"onDestroy");
		
		unregister();
    	notifier.removeMessages(0);
    	started = false;
	}
	
	public RobatteryStatus getStatus() {
		return battery;
	}
	
	public int getLevel() {
		return battery.level;
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
		String title = "Battery Status: " + String.valueOf(battery.level) + "%";
		
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.robot, title, System.currentTimeMillis());
		notification.setLatestEventInfo(context, title, "Robattery", pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		notification.ledARGB = 0xffff0000;
		notification.ledOnMS = 200;
		notification.ledOffMS = 800;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		
		long[] vibration = {200, 200};
		notification.vibrate = vibration;
		
		//notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");

		nm.cancelAll();
		nm.notify(1, notification);
	}
}
