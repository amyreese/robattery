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
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Class for initiating and tracking notifications for the Robattery background service
 * @author jreese
 */
public class RobatteryNotification {
	private static final String LOGCAT = "RobatteryNotification";
	
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
	private RobatteryStatus status;
	
	/**
	 * Initiate a notification based on the given battery status.
	 * @param status
	 */
	public RobatteryNotification(Context context, RobatteryStatus status) {
		this.context = context;
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.status = status;
		
		String level = prefs.getString("notification_level", "");
		Log.d(LOGCAT, "notification level " + level);
	}
	
	private void sendNotification() {
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, Robattery.class), 0);
		String title = "Battery Status: " + String.valueOf(status.level) + "%";
		
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
