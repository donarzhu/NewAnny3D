package com.aicyber.alter;

import android.app.Application;
import android.content.Context;

public class AicyberApplication extends Application {

	private static AicyberApplication mInstance;
	private Context mContext;

	public static AicyberApplication getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mInstance = this;
		mContext = mInstance.getApplicationContext();
	}
}
