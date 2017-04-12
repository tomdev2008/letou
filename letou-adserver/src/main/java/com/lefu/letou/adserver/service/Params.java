package com.lefu.letou.adserver.service;

public class Params {

	// redis
	public static String ALLADS = "lt-ads"; // 所有的广告
	public static String POSADS = "posAds";
	public static String REDIS_PREFIX = "lt-";

	// common
	public static String APP = "app";
	public static String PRODUCT_ID = "p";

	public static String BUSINESS_ID = "bid";
	public static String DEVICE_ID = "did";
	public static String ADID = "adId";
	public static String ADPLANS = "adPlans";
	public static String STARTDATE = "startDate";
	public static String ENDDATE = "endDate";
	public static String IMPRESSION = "impression";
	public static String PLANID = "planId";
	public static String AD_URL = "url";
	public static String AD_LDP = "ldp";

	// app
	public static String USER_AGENT = "ua";
	public static String IP = "ip";
	public static String WITH = "w";
	public static String HEIGHT = "h";
	public static String OS = "os";
	public static String OS_VERSION = "osv";
	public static String TEST = "test";
	public static String TIME = "ts";

	// pos
	public static String REGION = "region";
	public static String REGIONID = "regionId";
	/** 商户信息在redis中hash的格式, 0:商户编号,1:地域id,2:地域名称 */
	public static String[] POSFIELDS = new String[] { BUSINESS_ID, REGIONID,
			REGION };
}
