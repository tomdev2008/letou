package com.lefu.letou.monitor;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Test1 {

	public static void main(String[] args) {
		String s = "[{1:543}, {2:234},{3:344}, {5:324}]";
		JSONArray arr = JSONArray.parseArray(s);
		for (Object object : arr) {
			JSONObject obj = (JSONObject) object;
			Set<Entry<String, Object>> entrySet = obj.entrySet();
			Iterator<Entry<String, Object>> iterator = entrySet.iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> next = iterator.next();
				System.out.println(next.getKey() +", "+ next.getValue());
			}
			System.out.println(entrySet);
		}
	}

}
