package net.leetcode.robattery;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class RobatterySettings extends PreferenceActivity {
	public static final String LOGCAT = "Robattery";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
	}

}
