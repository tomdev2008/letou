package com.lefu.letou.adserver.util;

import org.apache.log4j.Logger;

public class LogUtil {
	private static Logger logger = Logger.getLogger(LogUtil.class);

	public static Logger getLogger() {
		return logger;
	}

	public static final Logger posLogger = Logger.getLogger("poslog");
	public static final Logger posErrorLog = Logger.getLogger("posErrorLog");
	public static final Logger appLogger = Logger.getLogger("applog");
	public static final Logger appErrorLog = Logger.getLogger("appErrorLog");
}
