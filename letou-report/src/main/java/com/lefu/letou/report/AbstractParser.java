package com.lefu.letou.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.lefu.letou.common.LoggerUtil;

public class AbstractParser {
	protected File[] logFile;
	// adId ,regionId, bid : count
	
	protected boolean checkParam(String line, String prefix, int lineNum) {
		String[] parts = line.split(" ");
		if (parts.length != 4) {
			LoggerUtil.getLogger().debug("wrong formate,line number:" + lineNum);
			return false;
		}
		String level = parts[0];
		if (!level.equals("INFO")) {
			LoggerUtil.getLogger().debug("wrong level,line number:" + lineNum);
			return false;
		}
		String content = parts[3];
		if (!content.startsWith(prefix)) {
			LoggerUtil.getLogger().debug("wrong URI, line number:" + lineNum);
			return false;
		}
		return true;
	}

	private Map<String, String> params;
	private QueryStringDecoder decoder;

	protected Map<String, String> parseLine(String line) {
		params = new HashMap<String, String>();
		decoder = new QueryStringDecoder(line);
		Map<String, List<String>> parameters = decoder.parameters();
		Iterator<Entry<String, List<String>>> iterator = parameters.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Entry<String, List<String>> next = iterator.next();
			List<String> value = next.getValue();
			if (value != null) {
				params.put(next.getKey(), next.getValue().get(0));
			}
		}
		return params;
	}

	protected void addUp(
			Map<Integer, Map<Integer, Map<Integer, Integer>>> result,
			Map<String, String> params) {
		String adIdStr = params.get("adId");
		if (!isNumeric(adIdStr))
			return;
		String regionIdStr = params.get("regionId");
		if (!isNumeric(regionIdStr))
			return;
		String bidStr = params.get("bid");
		if (!isNumeric(bidStr))
			return;
		int adId = Integer.parseInt(adIdStr);
		int regionId = Integer.parseInt(regionIdStr);
		int bid = Integer.parseInt(bidStr); // 商户id

		Map<Integer, Map<Integer, Integer>> adMap = result.get(adId);
		if (adMap == null) {
			adMap = new HashMap<Integer, Map<Integer, Integer>>();
			result.put(adId, adMap);
		}
		Map<Integer, Integer> regionMap = adMap.get(regionId);
		if (regionMap == null) {
			regionMap = new HashMap<Integer, Integer>();
			adMap.put(regionId, regionMap);
		}
		Integer impressionCount = regionMap.get(bid);
		if (impressionCount == null) {
			impressionCount = 0;
		}
		impressionCount++;
		regionMap.put(bid, impressionCount);
	}

	protected boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	protected void saveResult(
			Map<Integer, Map<Integer, Map<Integer, Integer>>> result) {
		if (result == null)
			return;
		for (Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>> adEntry : result
				.entrySet()) {
			Integer adId = adEntry.getKey(); // adId 广告id

			Map<Integer, Map<Integer, Integer>> regionMap = adEntry.getValue();

			for (Map.Entry<Integer, Map<Integer, Integer>> regionEntry : regionMap
					.entrySet()) {
				Integer regionId = regionEntry.getKey(); // 地域id
				Map<Integer, Integer> bMap = regionEntry.getValue();
				for (Map.Entry<Integer, Integer> busiMap : bMap.entrySet()) {
					Integer bid = busiMap.getKey(); // 商户id
					Integer count = busiMap.getValue();
					System.out.println("ad:" + adId + ", region:" + regionId
							+ ",bid:" + bid + ", total:" + count);
				}
			}
		}
	}

	protected  Map<Integer, Map<Integer, Map<Integer, Integer>>> doParse(String prefix) throws IOException {
		Reader fileReader = null;
		Map<Integer, Map<Integer, Map<Integer, Integer>>> result = new HashMap<Integer, Map<Integer, Map<Integer, Integer>>>();
		for(File file:logFile){
			try {			
				fileReader = new FileReader(file);
				final BufferedReader reader = IOUtils.toBufferedReader(fileReader);
				String line = reader.readLine();
				int lineNum = 0;
				while (line != null) {
					lineNum++;
					if (!checkParam(line, prefix, lineNum)) {
						line = reader.readLine();
						continue;
					}
					Map<String, String> params = parseLine(line);
					addUp(result, params);
					line = reader.readLine();
				}
				LoggerUtil.info("[" + file.getName() + "]done,共" + lineNum + "行");
				
			} finally {
				IOUtils.closeQuietly(fileReader);
			}
		}
		return result;
	}
}
