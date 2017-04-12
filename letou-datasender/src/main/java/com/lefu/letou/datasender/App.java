package com.lefu.letou.datasender;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.lefu.letou.common.FileUtil;
import com.lefu.letou.common.JdbcUtil;
import com.lefu.letou.common.LoggerUtil;
import com.lefu.letou.common.Params;
import com.lefu.letou.common.RedisUtil;

/**
 * Hello world!
 *
 */
public class App {

	public static String url;
	public static String username;
	public static String password;
	public static boolean jdbcDEV; // 获取数据源方式
	public static String datasourceName;

	public static JdbcUtil jdbc;
	private static String cronAds;
	private static String cronRegion;
	private static String cronBusiness;
	private static SchedulerFactory sf = new StdSchedulerFactory();

	public static void main(String[] args) throws IOException,
			SchedulerException {
		readProperties();
		if (jdbcDEV) {
			jdbc = JdbcUtil.getInstance(url, username, password);
		} else {
			jdbc = JdbcUtil.getInstance(datasourceName);
		}

		List<String> regionIds = RedisUtil.getBusinessFields(1 + "",
				Params.REDIS_FIELDS_REGIONID);
		if (regionIds.get(0) == null) {
			// 初始化导入
			initData();
		}
		readAds();
		readIP();
		readRegion();
//		readBusiness();
	}

	// 初始化数据
	private static void initData() throws JobExecutionException {
		new IPReader().execute(null);
		new RegionReader().execute(null);
		new BusinessReader().execute(null);
	}

//	private static void readBusiness() throws SchedulerException {
//		schedule(BusinessReader.class, cronAds, "cron-business-read");
//	}

	private static void readRegion() throws SchedulerException {
		schedule(RegionReader.class, cronRegion, "cron-region-read");
	}

	private static void readIP() throws SchedulerException {
		schedule(IPReader.class, cronRegion, "cron-Ip-read");
	}

	private static void readAds() throws SchedulerException {
		schedule(AdsReader.class, cronAds, "cron-Ads-read");
	}

	private static void schedule(Class<? extends Job> cls, String expression,
			String name) throws SchedulerException {
		Scheduler sched = sf.getScheduler();
		JobDetail job = newJob(cls).withIdentity(name).build();
		CronTrigger trigger = newTrigger().withIdentity(name)
				.withSchedule(cronSchedule(expression)).build();
		Date ft = sched.scheduleJob(job, trigger);
		LoggerUtil.info(ft.toLocaleString());
		sched.start();
	}

	private static void readProperties() throws IOException {
		Properties props = FileUtil.loadProps("/jdbc.properties");
		url = props.getProperty("jdbc.url");
		username = props.getProperty("jdbc.username");
		password = props.getProperty("jdbc.password");
		jdbcDEV = "1".equals(props.getProperty("jdbc.dev"));
		datasourceName = props.getProperty("jdbc.datasourceName");

		props = FileUtil.loadProps("/conf.properties");
		cronAds = props.getProperty("cron.ads"); // 广告数据
		cronRegion = props.getProperty("cron.region"); // 地域ip数据
		cronBusiness = props.getProperty("cron.business"); // 商户数据

		LoggerUtil.info("cron.ads=" + cronAds);
		LoggerUtil.info("cron.region=" + cronRegion);
		LoggerUtil.info("cron.business=" + cronBusiness);
	}
}
