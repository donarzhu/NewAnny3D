package com.aicyber.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;

public class LogFileUtil {
	private static final Logger logger = LoggerFactory.getLogger();
	public final static String FORMAT_YYYYMMDDH24MM = "yyyy-MM-dd HH:mm:ss";

	public static void debug_log(String message) {
		logger.debug(getCurrentTime() + ":" + message);
	}

	public static void error_log(String message) {
		logger.error(getCurrentTime() + ":" + message);
	}

	public static String throwable2String(Throwable paramThrowable) {
		StringWriter localStringWriter = new StringWriter(1024);
		PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
		paramThrowable.printStackTrace(localPrintWriter);
		localPrintWriter.close();
		return localStringWriter.toString();
	}
	
	public static String getCurrentTime() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpledateformat = new SimpleDateFormat(
				FORMAT_YYYYMMDDH24MM);
		return simpledateformat.format(calendar.getTime());
	}
}
