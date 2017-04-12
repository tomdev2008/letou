package com.lefu.letou.monitor.util;

import org.apache.log4j.Logger;

public class LoggerUtil {
	private static Logger logger = Logger.getLogger(LoggerUtil.class);

	public static final Logger clickLogger = Logger.getLogger("clickLog");
	public static final Logger impressionLogger = Logger
			.getLogger("impressionLog");

	public static Logger getLogger() {
		return logger;
	}
}
