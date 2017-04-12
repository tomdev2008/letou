package com.lefu.letou.adserver.service;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lefu.letou.common.AdModel;
import com.lefu.letou.common.LoggerUtil;
import com.lefu.letou.common.Params;
import com.lefu.letou.common.RedisUtil;

public class AdsHolder implements Runnable {
	private static Map<Integer, AdModel> adsModel = new HashMap<Integer, AdModel>(); // key 为 adModel 的id
	private static Map<Integer, AdModel> tempMap = new HashMap<Integer, AdModel>(
			1000000);

	public static Map<Integer, AdModel> getAdsModel() {
		return adsModel;
	}

	public static void setAdsModel(Map<Integer, AdModel> adsModel) {
		AdsHolder.adsModel = adsModel;
	}

	@Override
	public void run() {
		readAllAds();
	}

	private void readAllAds() {
		String ads = RedisUtil.get(Params.ALLADS);
		if (ads == null) {
			LoggerUtil.info("lt-ads:No Ads JONS");
			return;
		}
		JSONArray arr = JSONArray.parseArray(ads);
		// List<AdModel> models = new ArrayList<AdModel>();
		for (Object object : arr) {
			JSONObject jsonObj = (JSONObject) object;
			AdModel adModel = JSONObject.toJavaObject(jsonObj, AdModel.class);
			// String plansStr = jsonObj.getString(Params.ADPLANS);
			// List<PlanModel> plans = JSONArray.parseArray(plansStr,
			// PlanModel.class);
			// adModel.setAdPlans(plans);
			tempMap.put(adModel.getAdId(), adModel);
		}
		adsModel = new HashMap<Integer, AdModel>(tempMap);
		tempMap.clear();
	}

}
