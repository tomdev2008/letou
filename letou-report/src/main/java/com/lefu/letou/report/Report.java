package com.lefu.letou.report;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.lefu.letou.common.FileUtil;
import com.lefu.letou.common.JdbcUtil;
import com.lefu.letou.common.LoggerUtil;

public class Report {
	private static String dirs;
	private static String separater;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public static JdbcUtil jdbc;
	public static String url;
	public static String username;
	public static String password;
	public static boolean jdbcDEV; // 获取数据源方式
	public static String datasourceName;
	private static SchedulerFactory sf = new StdSchedulerFactory();
	private static String cronReport;

	public static void main(String[] args) throws Exception {
		// 获取JDBC
		Properties jdbcprops = FileUtil.loadProps("/jdbc.properties");
		// Properties props = FileUtil.loadProps("/jdbc.properties");
		url = jdbcprops.getProperty("jdbc.url");
		username = jdbcprops.getProperty("jdbc.username");
		password = jdbcprops.getProperty("jdbc.password");
		jdbcDEV = "1".equals(jdbcprops.getProperty("jdbc.dev"));
		datasourceName = jdbcprops.getProperty("jdbc.datasourceName");

		LoggerUtil.info("datasourceName=" + datasourceName);
		if (jdbcDEV) {
			// Class.forName("com.mysql.jdbc.Driver");
			jdbc = JdbcUtil.getInstance(url, username, password);
			LoggerUtil.info("url=" + url);
			LoggerUtil.info("username=" + username);
			LoggerUtil.info("password=" + password);

		} else {
			jdbc = JdbcUtil.getInstance(datasourceName);
		}

		Properties props = FileUtil.loadProps("/conf.properties");
		cronReport = props.getProperty("cron.report");
		LoggerUtil.info("cron.report=" + cronReport);

		LoggerUtil.info("jdbcDev=" + jdbcDEV);
		LoggerUtil.info("datasourceName=" + datasourceName);

		// 定时执行任务
		schedule(CycStatistics.class, cronReport, "CronReport");
		LoggerUtil.info("letou-report 启动完成");
	}

	private static void schedule(Class<? extends Job> cls, String expression,
			String name) throws SchedulerException {
		Scheduler sched = sf.getScheduler();
		JobDetail job = newJob(cls).withIdentity(name).build();
		CronTrigger trigger = newTrigger().withIdentity(name)
				.withSchedule(cronSchedule(expression)).build();
		Date ft = sched.scheduleJob(job, trigger);
		sched.start();
	}

}
