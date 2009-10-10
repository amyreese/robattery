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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Class for initiating and tracking notifications for the Robattery background service
 * @author jreese
 */
public class RobatteryNotification {
	private static final String LOGCAT = "RobatteryNotification";
	
	/**
	 * Color for the notification LED
	 */
	private static final int LED_ARGB = 0xffff0000;
	
	/**
	 * Length of notification LED blink
	 */
	private static final int LED_ON = 200;
	
	/**
	 * Pause between notification LED blinks
	 */
	private static final int LED_OFF = 200;
	
	/**
	 * Vibrate notification pattern
	 */
	private static final long[] VIBE_TIMING = {200, 200};
	
	/**
	 * Application context used for intents and notifications.
	 */
	private Context context;
	
	/**
	 * Preferences for determining when and how to send a notification.
	 */
	private SharedPreferences prefs;
	
	/**
	 * The current battery status.
	 */
	private RobatteryStatus battery;
	
	/**
	 * Initiate a notification based on the given battery status.
	 * @param status
	 */
	public RobatteryNotification(Context context, RobatteryStatus battery) {
		this.context = context;
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.battery = battery;
		
		trigger();
	}
	
	/**
	 * Determine if a notification needs to be sent
	 */
	private void trigger() {
		Log.d(LOGCAT, "triggered");
		
		// cancel any existing notifications
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancelAll();

		// get the preferred battery level threshold
		int minimum_level;
		try {
			minimum_level = Integer.parseInt(prefs.getString("notification_level", "15"));
		} catch(NumberFormatException e) {
			minimum_level = 15;
		}
		
		// send a new notification if below the threshold and unplugged
		if (battery.level <= minimum_level && battery.status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
			send(nm);
		}
	}
	
	/**
	 * Create and send a notification
	 * @param nm NotificationManager to notify
	 */
	private void send(NotificationManager nm) {
		Log.d(LOGCAT, "sending");
		
		// generate the base notification details
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, Robattery.class), 0);
		String title = "Battery Low: " + String.valueOf(battery.level) + "%";
		
		// create the notification object
		Notification notification = new Notification(R.drawable.robot, title, System.currentTimeMillis());
		notification.setLatestEventInfo(context, title, "Robattery", pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		// set the notification LED
		notification.ledARGB = LED_ARGB;
		notification.ledOnMS = LED_ON;
		notification.ledOffMS = LED_OFF;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		
		// set preferred vibration
		if (prefs.getBoolean("notification_vibrate", true)) {
			notification.vibrate = VIBE_TIMING;
		}
		
		// set preferred ringtone
		String ringtone = prefs.getString("notification_ringtone", "");
		if (!ringtone.equals("")) {
			notification.sound = Uri.parse(ringtone);
		}

		// send the notification
		nm.notify(1, notification);
		
		Log.d(LOGCAT, "sent");
	}
}