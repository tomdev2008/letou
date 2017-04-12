package com.lefu.letou.report;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.lefu.letou.common.LoggerUtil;

/*
 * Map<String,Map<Integer, Map<Integer, Map<Integer, Integer>>>>
 * Map<类型,<广告id,<地域id,<商户id,count数量>>>>>
 *
 * */
public class ClickLogParser extends AbstractParser
		implements
		Callable<Map<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>>> {

	private static final String prefix = "requestURI:/c?";

	public ClickLogParser(File[] logFile) {
		super();
		this.logFile = logFile;
	}
	

	@Override
	public Map<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>> call()
			throws Exception {
		Map<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>> ret = new HashMap<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>>();
		Map<Integer, Map<Integer, Map<Integer, Integer>>> result = doParse(prefix);
		ret.put("ClickLogParser", result);
		return ret;

	}

}
