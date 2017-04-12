package com.lefu.letou.monitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogAnalysis {

	// 127.0.0.1 - - [13/May/2016:09:48:50 +0800] "GET / HTTP/1.1" 200 612 "-"
	// "curl/7.41.0"

	static Pattern p = Pattern
			.compile("(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})\\s-\\s(.*)\\s\\[(.*)\\]\\s\"(GET|POST)\\s(\\/.*)\\s(.*)\"\\s(\\d{3})\\s(\\d+)\\s\"(.*)\"\\s\"(.*)\"(.*)");
	static Map <String, Integer> map = new HashMap<String, Integer>();
	
	public static void main(String[] args) throws IOException {
		// test();
//		writeFile();
		readFile();
		
	}

	private static void writeFile() throws IOException {
		File file = new File("D:\\software\\nginx-1.9.14\\logs/test.log");
		String s = "127.0.0.%s - - [12/Apr/2016:17:24:13 +0800] \"POST "
				+ "/hm.js?dd4738b5fb302cb062ef19107df5d2e4 HTTP/1.1\" 404 169 "
				+ "\"http://newtab.firefoxchina.cn/error-tab-rec.html\" "
				+ "\"Mozilla/%s.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0\"";
		FileWriter f = new FileWriter(file,true);
		BufferedWriter writer = new BufferedWriter(f);
		for (int i = 0; i < 50; i++) {
			writer.write(String.format(s, i, i));
			writer.newLine();
		}
		writer.flush();
		writer.close();

	}

	private static void test() {
		String s = "127.0.0.1 - - [12/Apr/2016:17:24:13 +0800] \"POST /hm.js?dd4738b5fb302cb062ef19107df5d2e4 HTTP/1.1\" 404 169 \"http://newtab.firefoxchina.cn/error-tab-rec.html\" \"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0\"";
		Matcher m = p.matcher(s);
		System.out.println(m.matches());
		int groupCount = m.groupCount();
		System.out.println("groupCount:" + groupCount);
		for (int i = 0; i < groupCount; i++) {
			System.out.println(m.group(i));
		}

	}

	private static void readFile() throws FileNotFoundException, IOException {
		// String s =
		// "127.0.0.1 - - [12/Apr/2016:17:24:13 +0800] \"POST /hm.js?dd4738b5fb302cb062ef19107df5d2e4 HTTP/1.1\" 404 169 \"http://newtab.firefoxchina.cn/error-tab-rec.html\" \"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0\"";
		File file = new File("D:\\software\\nginx-1.9.14\\logs/test.log");
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(isr);
		String ip = null;
		String fox = null;
		String tempString = null;
		
		while ((tempString = reader.readLine()) != null) {
			Matcher m = p.matcher(tempString);
			m.matches();
//			int groupCount = m.groupCount();
			
			ip = m.group(1);
			fox = m.group(10);
			
			Integer integer = map.get(ip);
			if(integer != null) {
				map.put(ip, integer +1);
			} else {
				map.put(ip, 1);
			}
//			System.out.println("groupCount:" + groupCount);
//			for (int i = 0; i < groupCount; i++) {
//				System.out.println(m.group(i));
//			}
		}
		System.out.println(map.keySet().size());
		System.out.println(map);
		reader.close();
	}
}
