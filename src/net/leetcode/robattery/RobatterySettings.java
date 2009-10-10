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

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Activity screen for setting Robattery preferences
 * @author jreese
 */
public class RobatterySettings extends PreferenceActivity {
	public static final String LOGCAT = "RobatterySettings";

	/**
	 * Listener class for passing preference updates to the settings activity.
	 * in order to update list item summaries.
	 */
	private class RobatterySettingsUpdateListener implements SharedPreferences.OnSharedPreferenceChangeListener {
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			Log.d(LOGCAT, "onSharedPreferencesChanged: " + key);
			
			updateSummaries(prefs);
		}
	}
	
	/**
	 * Update list preference summaries to match new pref values.
	 * @param prefs SharedPreferences instance to use for value lookups
	 */
	public void updateSummaries(SharedPreferences prefs) {
		// Battery level pref summary
		findPreference("notification_level").setSummary(prefs.getString("notification_level", "?") + "%");
		
		// Ringtone pref summary
		String path = prefs.getString("notification_ringtone", "");
		Uri uri = Uri.parse(path);
		Log.d(LOGCAT, "Uri: " + uri.toString());
		if (path == "") {
			findPreference("notification_ringtone").setSummary(R.string.silent);
		} else if (RingtoneManager.isDefault(uri)) {
			findPreference("notification_ringtone").setSummary(R.string.default_ringtone);
		} else {
			Ringtone tone = RingtoneManager.getRingtone(getBaseContext(), uri);
			findPreference("notification_ringtone").setSummary(tone.getTitle(getBaseContext()));
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
		
		// start the update listener and initialize summaries
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		prefs.registerOnSharedPreferenceChangeListener(new RobatterySettingsUpdateListener());
		updateSummaries(prefs);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		// kick the update listener and refresh summaries
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		prefs.registerOnSharedPreferenceChangeListener(new RobatterySettingsUpdateListener());
		updateSummaries(prefs);
	}
}
