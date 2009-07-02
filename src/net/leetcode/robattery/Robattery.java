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

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Robattery extends Activity {
	private static final int IDLETIME = 1000 * 60; // one minute
	private final String LOGCAT = "Robattery";
	
	private boolean bound = false;
	private boolean connected = false;
	
	private Intent robatteryServiceIntent;
	private IBinder robatteryBinder;
	
	private TextView batteryTextView;
	
	private Handler tickHandler = new Handler() {
		private int count = 0;
		
		@Override
		public void handleMessage(Message m) {
			count++;
			
			if ( bound ) {
				if ( connected ) {
					if ( robatteryBinder != null ) {
						Parcel data = Parcel.obtain();
						Parcel reply = Parcel.obtain();
						
						RobatteryStatus battery = null;
						
						try {
							robatteryBinder.transact(0, data, reply, 0);
							battery = new RobatteryStatus(reply.readBundle());
							
						} catch (RemoteException e) {
						}
						
						if ( battery != null ) {
							batteryTextView.setText(
									"Battery Level: "+String.valueOf(battery.level)+"%\n"+
									"Status: "+String.valueOf(battery.status)+"\n"+
									"Temperature: "+String.valueOf(battery.temperature)+"\n"
									);
						}
					} else {
						batteryTextView.setText("Binder not available.");
					}
				} else {
					batteryTextView.setText("Service not connected.");
				}
			}
			
			this.sendEmptyMessageDelayed(0, IDLETIME);
		}
	};
	
	private ServiceConnection robatteryConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder binder) {
			Log.i(LOGCAT, "Service connected");
			
			connected = true;
			robatteryBinder = binder;
		}
		
		public void onServiceDisconnected(ComponentName name) {
			Log.i(LOGCAT, "Service disconnected");
			
			connected = false;
			robatteryBinder = null;
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	robatteryServiceIntent = new Intent();
    	robatteryServiceIntent.setComponent(new ComponentName(this, "net.leetcode.robattery.RobatteryService"));
    	
        this.startService(robatteryServiceIntent);
        
        batteryTextView = new TextView(this);
        batteryTextView.setText("Waiting for battery status...");
        setContentView(batteryTextView);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	tickHandler.sendEmptyMessageDelayed(0, 1000);
    	
    	bound = this.bindService(robatteryServiceIntent, robatteryConnection, 0);
    	if ( !bound ) {
    		batteryTextView.setText("Could not bind service!");
    	}
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	if (bound) {
    		this.unbindService(robatteryConnection);
    	}
    	
    	tickHandler.removeMessages(0);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.main_settings:
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(this, "net.leetcode.robattery.RobatterySettings"));
			startActivity(intent);
			break;

		default:
			break;
		}

		return true;
	}
}


