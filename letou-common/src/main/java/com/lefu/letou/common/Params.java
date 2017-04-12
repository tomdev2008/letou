package com.lefu.letou.common;

public class Params {

	// redis
	public static final String ALLADS = "lt-ads"; // 所有的广告
	public static final String ALLREGIONS = "lt-allregions";
	public static final String ALLIPS = "lt-allIps";
	public static final String REDIS_PREFIX = "lt-";
	public static final String REDIS_FIELDS_NAME = "name";
	public static final String REDIS_FIELDS_ENGLISH = "english";
	public static final String REDIS_FIELDS_PARENTID = "parentId";
	public static final String REDIS_FIELDS_REGIONID = "regionId";
	public static final String REDIS_FIELDS_INDUSTRY = "industry";
	public static final String REDIS_FIELDS_BUSINESS = "b";

	
	
	// common
	public static final String APP = "app";
	public static final String PRODUCT_ID = "p";

	public static final String BUSINESS_ID = "bid";
	public static final String DEVICE_ID = "did";
	public static final String ADID = "adId";
	public static final String ADPLANS = "adPlans";
	public static final String STARTDATE = "startDate";
	public static final String ENDDATE = "endDate";
	public static final String IMPRESSION = "impression";
	public static final String PLANID = "planId";
	public static final String AD_URL = "url";
	public static final String AD_LDP = "ldp";
	public static final String O = "o";
	public static final String INDUSTRY = "industry";

	/** 展示内容 */
	public static final String AD_CONTENT = "content";

	// app
	public static final String USER_AGENT = "ua";
	public static final String IP = "ip";
	public static final String WITH = "w";
	public static final String HEIGHT = "h";
	public static final String OS = "os";
	public static final String OS_VERSION = "osv";
	public static final String TEST = "test";
	public static final String TIME = "ts";
	public static final String EXT = "ext";
	public static final String PM = "pm";
	public static final String CM = "cm";
	public static final String TOKEN = "token";

	// pos
	public static final String SUCCESS = "success";
	public static final String REGION = "region";
	public static final String REGIONID = "regionId";
	/** 商户信息在redis中hash的格式, 0:地域id,1:地域名称 */
//	public static final String[] POSFIELDS = new String[] { REGIONID, REGION };
}
