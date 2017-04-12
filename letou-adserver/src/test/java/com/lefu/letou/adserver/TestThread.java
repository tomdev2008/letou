package com.lefu.letou.adserver;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.lefu.letou.adserver.service.AdsConfig;
import com.lefu.letou.adserver.service.AdsHolder;

public class TestThread {

	public static void main(String[] args) {
		JSONObject o = new JSONObject();
		o.put("a", true);
		System.out.println(o);
	}

}