package com.lefu.letou.adserver.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lefu.letou.adserver.model.AdException;
import com.lefu.letou.adserver.util.LogUtil;
import com.lefu.letou.common.AdModel;
import com.lefu.letou.common.Params;

public class AppService extends AbstractAdService {
	private static Logger logger = LogUtil.posLogger;

	@Override
	public String findAd(Map<String, String> params, boolean isTest)
			throws AdException {
		String token = params.get(Params.TOKEN);

		String deviceId = params.get(Params.DEVICE_ID); // 设备id
		int productId = Integer.parseInt(params.get(Params.PRODUCT_ID));
		String businessId = params.get(Params.BUSINESS_ID); // 商户id
		String ip = params.get(Params.IP);
		// regionId 按照从地域从低到高的顺序
		List<Integer> regionIds = super.convertRegionId(ip);

		boolean found = false;
		Integer regionIdToAd = null;
		List<AdModel> adModels = null;
		for (Integer regionId : regionIds) {
			regionIdToAd = regionId;
			adModels = getAdByRegion(productId, regionId, isTest, logger);
			if (adModels.isEmpty()) {
				continue;
			} else {
				found = true;
				break;
			}
		}

		if (!found) {
			debug("无合适广告", isTest, logger);
			return null;
		}
		AdModel adModel = null;
		if (adModels.size() == 1) {
			adModel = adModels.get(0);
		}
		if (adModels.size() > 1) {
			adModel = randChooseAdModel(adModels);
			debug("找到" + adModels.size() + "个广告,随机选择:" + adModel.getAdId(),
					isTest, logger);
		}

		String url = adModel.getContent();

		String subUrl = String.format("%s=%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
				Params.APP, "1", Params.PRODUCT_ID, productId,
				Params.DEVICE_ID, deviceId, Params.BUSINESS_ID, businessId,
				Params.ADID, adModel.getAdId(), Params.REGIONID, regionIdToAd);


		String pvCode = AdsConfig.monitor_impression_url + subUrl;

		String ldp;
		try {
			ldp = AdsConfig.monitor_click_url + subUrl + "&o="
					+ URLEncoder.encode(adModel.getLdp(), "UTF-8");
			debug("app点击跳转地址:" + ldp, isTest, logger);
		} catch (UnsupportedEncodingException e) {
			throw new AdException("encode编码错误", adModel.getLdp());
		}

		JSONObject result = new JSONObject();
		result.put(Params.AD_URL, url);
		result.put(Params.WITH, adModel.getWidth());
		result.put(Params.HEIGHT, adModel.getHeight());
		result.put(Params.EXT, adModel.getExtName());
		JSONArray pvs = new JSONArray();
		pvs.add(pvCode);
		result.put(Params.PM, pvs);
		result.put(Params.CM, ldp);
		String str = result.toString();
		String md5 = md5Encode(str);

		if (md5.equals(token)) {
			// 广告内容相同
			JSONObject tokenResult = new JSONObject();
			tokenResult.put("same", "true");
			String returnStr = tokenResult.toString();
			logger.info("response:" + returnStr);
			return returnStr;
		} else {
			result.put(Params.TOKEN, md5);
			String returnStr = result.toString();
			logger.info("response:" + returnStr);
			return returnStr;
		}
	}

	public String md5Encode(String data) {
		return DigestUtils.md5Hex(data);
	}
}
