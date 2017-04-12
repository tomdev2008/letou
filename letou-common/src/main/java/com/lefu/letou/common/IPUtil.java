package com.lefu.letou.common;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class IPUtil {
	/**
	 * 将字符串表示的ip地址转换为long表示.
	 *
	 * @param ip
	 *            ip地址
	 * @return 以32位整数表示的ip地址
	 */
	public static final long ip2Long(final String ip) {
		// if (!RegexpUtils.isExactlyMatches("(\\d{1,3}\\.){3}\\d{1,3}", ip)) {
		// throw new IllegalArgumentException("[" + ip + "]不是有效的ip地址");
		// }
		final String[] ipNums = ip.split("\\.");
		return (Long.parseLong(ipNums[0]) << 24)
				+ (Long.parseLong(ipNums[1]) << 16)
				+ (Long.parseLong(ipNums[2]) << 8)
				+ (Long.parseLong(ipNums[3]));
	}

	public static long ip2num(String ip) {
		long ipNum = 0;
		try {
			if (ip != null) {
				String ips[] = ip.split("\\.");
				for (int i = 0; i < ips.length; i++) {
					int k = Integer.parseInt(ips[i]);
					ipNum = ipNum + k * (1L << ((3 - i) * 8));
				}
			}
		} catch (Exception e) {
		}
		return ipNum;
	}

	public static void main(String[] args) throws UnknownHostException {
		System.out.println(num2ip(3684548096l));
		long ip = IPUtil.ip2Long("192.168.0.254");
		System.out.println(ip);
		System.out.println(ip2num("192.168.0.254"));
		System.out.println(num2ip(ip2num("192.168.0.254")));
		System.out.println(Inet4Address
				.getByName(String.valueOf(ip2num("192.168.0.254")))
				.getHostAddress().toString());

		String s = "192.168.0.254";
		String[] ips = s.split("\\.");

		System.out.println((Long.parseLong(ips[0]) << 24)
				+ (Long.parseLong(ips[1]) << 16)
				+ (Long.parseLong(ips[2]) << 8) + Long.parseLong(ips[3]));
		System.out.println(Long.parseLong(ips[0]) * 256 * 256 * 256
				+ Long.parseLong(ips[1]) * 256 * 256 + Long.parseLong(ips[2])
				* 256 + Long.parseLong(ips[3]));

	}

	public static String num2ip(long longIp) {
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}
}
