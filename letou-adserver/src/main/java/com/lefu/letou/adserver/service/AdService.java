package com.lefu.letou.adserver.service;

import java.util.Map;

public interface AdService {
	/**
	 * 根据请求参数寻找广告
	 * 
	 * @param params
	 * @return
	 */
	String findAd(Map<String, String> params, boolean isTest);

}
