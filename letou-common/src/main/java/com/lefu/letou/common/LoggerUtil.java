package com.lefu.letou.common;

import org.apache.log4j.Logger;

public class LoggerUtil {
	private static Logger logger = Logger.getLogger(LoggerUtil.class);

	public static void info(String s) {
		logger.info(s);
	}

	public static void error(String s) {
		logger.error(s);
	}

	public static void error(String s, Throwable e) {
		logger.error(s, e);
	}

	public static Logger getLogger() {
		return logger;
	}
}
