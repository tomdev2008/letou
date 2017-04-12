package com.lefu.letou.adserver.service;

import java.io.IOException;
import java.util.Properties;

import com.lefu.letou.common.FileUtil;

public class AdsConfig {
	public static String monitor_click_url;
	public static String monitor_impression_url;
	public static int port;
	public static int dateAdvance;

	static {
		try {
			loadConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public AdsConfig() {

	}

	private static void loadConfig() throws IOException {
		Properties props = FileUtil.loadProps("/server.properties");
		monitor_click_url = props.get("monitor.click").toString();
		monitor_impression_url = props.get("monitor.impression").toString();

		String p = props.getProperty("port");
		port = Integer.parseInt(p);
		String adv = props.getProperty("dateadvance");
		dateAdvance = Integer.parseInt(adv);

		System.out.println(p);
		System.out.println(monitor_impression_url);
		System.out.println(monitor_click_url);
		System.out.println(dateAdvance);

	}
}
