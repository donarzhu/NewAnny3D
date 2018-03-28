package com.aicyber.db;

import java.util.UUID;

import com.aicyber.alter.AicyberApplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveValue {

	private static Context mContext = AicyberApplication.getInstance().getApplicationContext();

	public static SharedPreferences getSharedPreferences() {
		return mContext.getSharedPreferences("arcyber_robot_config", Context.MODE_PRIVATE);
	}

	public static String getUserId() {
		if (getString("userid") == null || getString("userid").length() <= 0) {
			UUID uuid = UUID.randomUUID();
			SaveValue.putString("userid", "robot_" + uuid);
		}
		return getString("userid");
	}

	public static String getString(String key) {
		return getSharedPreferences().getString(key, "");
	}

	public static void putString(String key, String value) {
		getSharedPreferences().edit().putString(key, value).commit();
	}
}
