package com.lefu.letou.monitor;

import java.io.IOException;
import java.util.Properties;

import com.lefu.letou.common.FileUtil;
import com.lefu.letou.monitor.util.LoggerUtil;

public class MonitorConfig {
	public static int port;

	static {
		try {
			loadConfig();
		} catch (IOException e) {
			LoggerUtil.getLogger().error(e.getMessage(), e);
		}
	}

	private static void loadConfig() throws IOException {
		Properties props = FileUtil.loadProps("/server.properties");
		port = Integer.parseInt(props.getProperty("port"));
	}
}
