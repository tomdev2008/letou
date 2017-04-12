package com.lefu.letou.report;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class TestParser {
	
	
	
	@Test
	public void testParser() {
		String uri = "requestURI:/i?app=0&p=1&did=posid123&bid=3&industry=350&adId=1&regionId=1";
		long long1 = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			Map<String, String> map = parseParam(uri);
			System.out.println(map);
		}
		long long2 = System.currentTimeMillis();
		System.out.println(long2 - long1);
	}

	private Map<String, String> parseParam(String line) {
		Map<String, String> params = new HashMap<String, String>();
		QueryStringDecoder decoder = new QueryStringDecoder(line);
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

}
