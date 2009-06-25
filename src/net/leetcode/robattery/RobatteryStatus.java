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

/**
 * Class for transmitting the current status of the battery.
 * @author jreese
 */
public class RobatteryStatus {
	
	/**
	 * Build a status object from an Intent object passed from the
	 * ACTION_BATTERY_CHANGED event.
	 * @param intent Intent from ACTION_BATTERY_CHANGED event
	 */
	public RobatteryStatus(Intent intent) {
		present = intent.getBooleanExtra("present", present);
		status = intent.getIntExtra("status", status);
		plugtype = intent.getIntExtra("plugged", plugtype);
		level = intent.getIntExtra("level", level);
		scale = intent.getIntExtra("scale", scale);
		health = intent.getIntExtra("health", health);
		temperature = intent.getIntExtra("temperature", temperature);
		voltage = intent.getIntExtra("voltage", voltage);
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
