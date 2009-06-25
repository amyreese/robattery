package net.leetcode.robattery;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class RobatteryBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
    	Intent robatteryServiceIntent = new Intent();
    	robatteryServiceIntent.setComponent(new ComponentName(context, "net.leetcode.robattery.RobatteryService"));
    	
		context.startService(robatteryServiceIntent);
	}

}
