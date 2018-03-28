package com.aicyber.util;

import com.aicyber.constant.Constants;

import android.util.Log;

public class Logger {
	public static boolean logEnable = true;

	public final static void debug(String message) {
		if (logEnable) {
			debug(null, message);
		}
	}

	public final static void info(String message) {
		if (logEnable) {
			info(null, message);
		}
	}

	public final static void warn(String message) {
		if (logEnable) {
			warn(null, message);
		}
	}

	public final static void error(String message) {
		if (logEnable) {
			error(null, message);
			LogFileUtil.error_log(message);
		}
	}

	public final static void debug(String tag, String message) {
		if (logEnable) {
			Log.d(tag == null ? Constants.TAG : tag, message);
		}
	}

	public final static void info(String tag, String message) {
		if (logEnable) {
			Log.i(tag == null ? Constants.TAG : tag, message);
		}
	}

	public final static void warn(String tag, String message) {
		if (logEnable) {
			Log.w(tag == null ? Constants.TAG : tag, message);
		}
	}

	public final static void error(String tag, String message) {
		if (logEnable) {
			Log.e(tag == null ? Constants.TAG : tag, message);
		}
	}
}
