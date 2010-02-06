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

import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;

/**
 * Class for transmitting the current status of the battery.
 * @author jreese
 */
public class Battery {
	
	/**
	 * Build a status object from an Intent object passed from the
	 * ACTION_BATTERY_CHANGED event.
	 * @param intent Intent from ACTION_BATTERY_CHANGED event
	 */
	public Battery(Intent intent) {
		present = intent.getBooleanExtra("present", present);
		status = intent.getIntExtra("status", status);
		plugtype = intent.getIntExtra("plugged", plugtype);
		level = intent.getIntExtra("level", level);
		scale = intent.getIntExtra("scale", scale);
		health = intent.getIntExtra("health", health);
		temperature = intent.getIntExtra("temperature", temperature);
		voltage = intent.getIntExtra("voltage", voltage);
	}
	
	/**
	 * Build a status object from a Bundle object.
	 * @param bundle Bundle containing status data
	 */
	public Battery(Bundle bundle) {
		present = bundle.getBoolean("present", present);
		status = bundle.getInt("status", status);
		plugtype = bundle.getInt("plugtype", plugtype);
		level = bundle.getInt("level", level);
		scale = bundle.getInt("scale", scale);
		health = bundle.getInt("health", health);
		temperature = bundle.getInt("temperature", temperature);
		voltage = bundle.getInt("voltage", voltage);
	}
	
	/**
	 * Create a Bundle object from the battery status.
	 * @return New Bundle object containing status data
	 */
	public Bundle bundle() {
		Bundle b = new Bundle();
		addToBundle(b);
		return b;
	}
	
	/**
	 * Inject object properties into an existing Bundle object
	 * @param b Existing Bundle object
	 */
	public void addToBundle(Bundle b) {
		b.putBoolean("present", present);
		b.putInt("status", status);
		b.putInt("plugtype", plugtype);
		b.putInt("level", level);
		b.putInt("scale", scale);
		b.putInt("health", health);
		b.putInt("temperature", temperature);
		b.putInt("voltage", voltage);
	}
	
	public boolean present = false;
	
	public int status = BatteryManager.BATTERY_STATUS_UNKNOWN;
	
	public int plugtype = 0;
	
	public int level = 0;
	
	public int scale = 100;

	public int health = BatteryManager.BATTERY_HEALTH_UNKNOWN;
	
	public int temperature = 0;
	
	public int voltage = 0;
	
}
