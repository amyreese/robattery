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

import android.os.Binder;
import android.os.Parcel;
import android.util.Log;

public class RobatteryBinder extends Binder {
	private final String LOGCAT = "RobatteryBinder";
	private RobatteryService service = null;
	
	public RobatteryBinder( RobatteryService service ) {
		super();
		this.service = service;		
	}
	
	@Override
	public boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
		Log.d(LOGCAT,"onTransact");
		
		reply.writeBundle(service.getStatus().bundle());
		
		return true;
	}

}
