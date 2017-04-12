package com.lefu.letou.report;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class ImpressionParser extends AbstractParser
		implements
		Callable<Map<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>>> {

	private static final String prefix = "requestURI:/i?";

	public ImpressionParser(File[] logFile) {
		super();
		this.logFile = logFile;
		// logModel = new ImpressionLogModel(date);
	}

	@Override
	public Map<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>> call()
			throws Exception {

		Map<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>> ret = new HashMap<String, Map<Integer, Map<Integer, Map<Integer, Integer>>>>();
		Map<Integer, Map<Integer, Map<Integer, Integer>>> result = doParse(prefix);
		// saveResult(result,"ImpressionParser");
		ret.put("ImpressionParser", result);
		return ret;
	}

}
