package com.lefu.letou.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lefu.letou.common.JdbcUtil;
import com.lefu.letou.common.LoggerUtil;

public class CycStatistics implements Job {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		LoggerUtil.info("start report Job :" + new Date().toString());
		String dirs;
		String separater;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		try {
			Properties props = null;
			props = loadProps("/conf.properties");

			dirs = props.getProperty("logdir");
			String[] split = dirs.split(",");
			separater = props.get("separater") == null ? "-" : props
					.getProperty("separater");
			// System.out.println(separater);
			ExecutorService threadPool = Executors.newFixedThreadPool(2);

			CompletionService<Map<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>>> completionService = new ExecutorCompletionService<Map<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>>>(
					threadPool);
			String yesterday = getYesterday();
			for (String dir : split) {
				File logDir = new File(dir+File.separator+yesterday);
				if (!logDir.exists() || !logDir.isDirectory()) {
					LoggerUtil.info("日志存储路径 "+logDir.toString()+" 不存在");
					return;
//					throw new Exception("日志存储路径不存在");
				}
				Collection<File> files = FileUtils.listFiles(logDir,
						FileFilterUtils.suffixFileFilter(".log"), null);
				if (files.isEmpty()) {
					LoggerUtil.info(logDir.toString()
							+" 路径下没有格式为：click-机器序号-"+yesterday+".log 或 impression-机器序号-"+yesterday+".log的日志文件");
					return;
				}
				// 每个日志目录提交一次数据
				Map<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>> retAll = new HashMap<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>>();
				//记录计数线程个数
				int i=0;
				List<File> clickFiles=new ArrayList<File>();
				List<File> impressionFiles=new ArrayList<File>();
				for (File file : files) {
					String name = file.getName();
					String[] names = name.split(separater);
					// if (names.length == 1) {
					// throw new Exception("日志文件名不包含日期");
					// }

					if (name.startsWith("click")
							&& name.endsWith(yesterday + ".log")) {
						
						clickFiles.add(file);
						//completionService.submit(new ClickLogParser(file));
						//i++;
					}
					if (name.startsWith("impression")
							&& name.endsWith(yesterday + ".log")) {
						impressionFiles.add(file);
						//completionService.submit(new ImpressionParser(file));
						//i++;
					}
				}
				if(clickFiles.size()>0){
					i++;
					File[] cFiles=new File[clickFiles.size()];
					clickFiles.toArray(cFiles);
					completionService.submit(new ClickLogParser(cFiles));
				}
				if(impressionFiles.size()>0){
					i++;
					File[] iFiles=new File[impressionFiles.size()];
					impressionFiles.toArray(iFiles);
					completionService.submit(new ImpressionParser(iFiles));
				}
				for(int j=0;j<i;j++){
					retAll.putAll(completionService.take().get());
				}
				if(retAll==null||retAll.size()==0){
					LoggerUtil.info(logDir.toString()
							+" 路径下没有格式为：click-机器序号-"+yesterday+".log 或 impression-机器序号-"+yesterday+".log的日志文件，或日志文件内容为空");
					continue;
				}
				List<String> retAllKey= new ArrayList<String>();
				for(Map.Entry<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>> retAllEntry:retAll.entrySet()){					
					retAllKey.add(retAllEntry.getKey());
				}
				Map<Integer, Map<Integer, Map<Integer, Map<String, Integer>>>> retForamt=null;
				if(retAllKey.size()>1){
					retForamt=formatMap(retAll, retAllKey.get(0));
					mergeMap(formatMap(retAll, retAllKey.get(1)), retForamt);
				}else{
					retForamt=formatMap(retAll, retAllKey.get(0));
				}	
				if(retForamt.size()>0){
					// 提交jdbc
					Date date = sdf.parse(yesterday);
					saveReportDaily(retForamt, date);
				}else{
					LoggerUtil.info(logDir.toString()
							+" 路径下没有格式为：click-机器序号-"+yesterday+".log 或 impression-机器序号-"+yesterday+".log的日志文件，或日志文件内容为空");					
				}
				
			}
			// 所有目录循环后关闭线程池
			threadPool.shutdown();
		} catch (Exception e) {
			LoggerUtil.error(e.getMessage(), e);
			throw new JobExecutionException(e);
		}
	}

	public Properties loadProps(String file) throws IOException {
		InputStream stream = Report.class.getResourceAsStream(file);
		Properties p = null;
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(stream));
			p = new Properties();
			p.load(r);
		} finally {
			stream.close();
			r.close();
		}
		return p;
	}

	public static String getYesterday() {
		Date dNow = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dNow);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		dNow = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // 设置时间格式
		// String defaultStartDate = sdf.format(dNow); //格式化前一天
		return sdf.format(dNow);
	}

	/*
	 * Map<String,Map<Integer, Map<Integer, Map<Integer, Integer>>>>
	 * Map<类型,<广告id,<地域id,<商户id,count数量>>>>> 转化为 Map<Integer, Map<Integer,
	 * Map<Integer, Map<String,Integer>>>> Map<广告id,<地域id,<商户id,<类型,count数量>>>>
	 *
	 * 输入参数： Map<..> 为ClickLogParserRet/ImpressionParserRet 两个callable的返回数值
	 * String item=ClickLogParser/ImpressionParser
	 */
	public static Map<Integer, Map<Integer, Map<Integer, Map<String, Integer>>>> formatMap(
			Map<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>> map,
			String Item) {
		Map<Integer, Map<Integer, Map<Integer, Integer>>> result = map
				.get(Item);
		Map<Integer, Map<Integer, Map<Integer, Map<String, Integer>>>> ret = new HashMap<Integer, Map<Integer, Map<Integer, Map<String, Integer>>>>();
		for (Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>> adEntry : result
				.entrySet()) {
			Integer adId = adEntry.getKey(); // adId 广告id
			Map<Integer, Map<Integer, Integer>> regionMap = adEntry.getValue();
			Map<Integer, Map<Integer, Map<String, Integer>>> regionAll = new HashMap<Integer, Map<Integer, Map<String, Integer>>>();
			for (Map.Entry<Integer, Map<Integer, Integer>> regionEntry : regionMap
					.entrySet()) {
				Integer regionId = regionEntry.getKey(); // 地域id
				Map<Integer, Integer> bMap = regionEntry.getValue();

				Map<Integer, Map<String, Integer>> bidALL = new HashMap<Integer, Map<String, Integer>>();
				for (Map.Entry<Integer, Integer> busiMap : bMap.entrySet()) {
					Integer bid = busiMap.getKey(); // 商户id
					Integer count = busiMap.getValue();
					Map<String, Integer> countMap = new HashMap<String, Integer>();
					countMap.put(Item, count);
					bidALL.put(bid, countMap);
				}
				regionAll.put(regionId, bidALL);
			}
			ret.put(adId, regionAll);
		}
		return ret;
	}

	/*
	 * 把相同结构的MAP1添加到MAP2中 循环MAP1 看MAP2中是否存在，若不存在则put
	 */
	public static void mergeMap(
			Map<Integer, Map<Integer, Map<Integer, Map<String, Integer>>>> map1,
			Map<Integer, Map<Integer, Map<Integer, Map<String, Integer>>>> map2) {
		for (Map.Entry<Integer, Map<Integer, Map<Integer, Map<String, Integer>>>> adEntry : map1
				.entrySet()) {
			Integer adId = adEntry.getKey(); // adId 广告id
			Map<Integer, Map<Integer, Map<String, Integer>>> regionMap = adEntry
					.getValue();
			if (map2.containsKey(adId)) {
				for (Map.Entry<Integer, Map<Integer, Map<String, Integer>>> regionEntry : regionMap
						.entrySet()) {
					Integer regionId = regionEntry.getKey(); // 地域id
					Map<Integer, Map<String, Integer>> bMap = regionEntry
							.getValue();
					if (map2.get(adId).containsKey(regionId)) {
						for (Map.Entry<Integer, Map<String, Integer>> busiMap : bMap
								.entrySet()) {
							Integer bid = busiMap.getKey(); // 商户id
							Map<String, Integer> countMap = busiMap.getValue();
							if (map2.get(adId).get(regionId).containsKey(bid)) {
								for (Map.Entry<String, Integer> ciMap : countMap
										.entrySet()) {
									String cOri = ciMap.getKey();
									Integer count = ciMap.getValue();
									if (map2.get(adId).get(regionId).get(bid)
											.containsKey(cOri)) {
										continue;
									} else {
										map2.get(adId).get(regionId).get(bid)
												.put(cOri, count);
									}
								}
							} else {
								// bMap.put(bid,
								// map2.get(adId).get(regionId).get(bid));
								map2.get(adId).get(regionId).put(bid, countMap);
							}
						}

					} else {
						// regionMap.put(regionId,
						// map2.get(adId).get(regionId));
						map2.get(adId).put(regionId, bMap);
					}
				}

			} else {
				map2.put(adId, regionMap);
			}
		}
	}

	/**
	 * 保存广告记录 report_daily
	 * */
	public static void saveReportDaily(
			Map<Integer, Map<Integer, Map<Integer, Map<String, Integer>>>> map,
			Date date) throws SQLException {
		PreparedStatement pstmt = null;
		Connection connection = null;
		String preparedSql = "INSERT INTO report_daily (advertiseId,click,targetRegion,date,Impression,PublisherId)VALUES(?,?,?,?,?,?)";
		int[] r;		
		try {
			connection = Report.jdbc.getConnection();
			pstmt = connection.prepareStatement(preparedSql);
			connection.setAutoCommit(false);
			for (Map.Entry<Integer, Map<Integer, Map<Integer, Map<String, Integer>>>> adEntry : map
					.entrySet()) {
				Integer adId = adEntry.getKey(); // adId 广告id
				Map<Integer, Map<Integer, Map<String, Integer>>> regionMap = adEntry
						.getValue();
				for (Map.Entry<Integer, Map<Integer, Map<String, Integer>>> regionEntry : regionMap
						.entrySet()) {
					Integer regionId = regionEntry.getKey(); // 地域id
					Map<Integer, Map<String, Integer>> bMap = regionEntry
							.getValue();
					for (Map.Entry<Integer, Map<String, Integer>> busiMap : bMap
							.entrySet()) {
						Integer bid = busiMap.getKey(); // 商户id
						Map<String, Integer> countMap = busiMap.getValue();
						pstmt.setInt(1, adId);
						pstmt.setInt(2,
								countMap.get("ClickLogParser") == null ? 0
										: countMap.get("ClickLogParser"));
						pstmt.setInt(3, regionId);
						pstmt.setDate(4, new java.sql.Date(date.getTime()));
						pstmt.setInt(5,
								countMap.get("ImpressionParser") == null ? 0
										: countMap.get("ImpressionParser"));
						pstmt.setInt(6, bid);
						pstmt.addBatch();
						LoggerUtil.info(" bid="+bid +" adid="+adId+" regionId="+regionId
								+" click="+countMap.get("ClickLogParser")
								+" Impression="+countMap.get("ImpressionParser"));
					}
				}
			}
			r = pstmt.executeBatch();			
			connection.commit();
			LoggerUtil.info("JDBC执行成功！");
		} finally {
			connection.setAutoCommit(true);
			Report.jdbc.close(null, pstmt, connection);
		}
	}
}
