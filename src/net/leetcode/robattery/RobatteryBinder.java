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
		
		reply.writeInt(service.getLevel());
		
		return true;
	}

}
