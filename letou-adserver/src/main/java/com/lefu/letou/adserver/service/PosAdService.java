package com.lefu.letou.adserver.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.lefu.letou.adserver.model.AdException;
import com.lefu.letou.adserver.util.LogUtil;
import com.lefu.letou.common.AdModel;
import com.lefu.letou.common.Params;
import com.lefu.letou.common.RedisUtil;

public class PosAdService extends AbstractAdService {
	private Logger logger = LogUtil.posLogger;
	private String posAdRecordDate = "yyyyMMdd";

	@Override
	public String findAd(Map<String, String> params, boolean isTest)
			throws AdException {
		String deviceId = params.get(Params.DEVICE_ID); // 设备id
		String businessId = params.get(Params.BUSINESS_ID); // 商户id

		Integer productId = Integer.parseInt(params.get(Params.PRODUCT_ID));
		// hmset key为商户id ,fields: regionId 1, region "beijing"
		List<String> business = RedisUtil.getBusinessFields(businessId,
				Params.REGIONID);
		debug("商户id为" + businessId, isTest, logger);
		String localRegionId = "0";
		if (business.get(0) != null) {
			localRegionId = business.get(0);
		} else {
			debug("没有此商户信息:" + businessId + ",投全国广告", isTest, logger);
			LogUtil.posErrorLog.error("没有此商户信息:" + businessId + ",regionId为0");
		}
		debug("本地存储RegionId=" + localRegionId, isTest, logger);

		List<Integer> regionIds = new ArrayList<Integer>();
		int rId = Integer.parseInt(localRegionId);
		regionIds.add(rId);
		// 优先地区选择
		List<AdModel> adModels = getAdByRegion(productId, rId, isTest, logger);
		if (adModels.isEmpty()) {
			adModels = parentRegionSearch(isTest, productId, regionIds, rId);
		}

		if (adModels.isEmpty()) {
			debug("无合适广告", isTest, logger);
			return null;
		}
		AdModel adModel = chooseOne(isTest, localRegionId, adModels);
		int industryId = getIndustryId(businessId, isTest, logger);
		String ldp;
		try {
			ldp = String.format(AdsConfig.monitor_click_url
					+ "%s=%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
					Params.APP, "0", Params.PRODUCT_ID, productId,
					Params.DEVICE_ID, deviceId, Params.BUSINESS_ID, businessId,
					Params.INDUSTRY, industryId, Params.ADID,
					adModel.getAdId(), Params.REGIONID, localRegionId,
					Params.O, URLEncoder.encode(adModel.getLdp(), "UTF-8"));
			debug("POS机扫码url为:" + ldp, isTest, logger);
		} catch (UnsupportedEncodingException e) {
			throw new AdException("encode编码错误", adModel.getLdp());
		}
		JSONObject result = new JSONObject();
		result.put(Params.AD_URL, ldp);
		result.put(Params.ADID, adModel.getAdId());
		String content = adModel.getContent();
		if (content != null) { // 文字内容
			result.put(Params.AD_CONTENT, content);
			debug("广告" + adModel.getAdId() + "文字内容:" + content, isTest, logger);
		} else {
			debug("广告:" + adModel.getAdId() + ", 没有文字内容", isTest, logger);
		}
		logger.info("response:" + result);
		return result.toString();
	}

	private AdModel chooseOne(boolean isTest, String localRegionId,
			List<AdModel> adModels) throws AdException {
		AdModel adModel = null;
		if (adModels.size() == 1) {
			adModel = adModels.get(0);
		}
		if (adModels.size() > 1) {
			adModel = randChooseAdModel(adModels);
			List<String> regionFields = RedisUtil.getRegionFields(
					localRegionId, Params.REDIS_FIELDS_ENGLISH);
			debug(regionFields.get(0) + "找到" + adModels.size() + "个广告,随机选择:"
					+ adModel.getAdId(), isTest, logger);
		}
		return adModel;
	}

	private List<AdModel> parentRegionSearch(boolean isTest, Integer productId,
			List<Integer> regionIds, int rId) {
		getRegionId(rId, regionIds);

		List<AdModel> adModels = null;
		for (Integer regionId : regionIds) {
			adModels = getAdByRegion(productId, regionId, isTest, logger);
			if (adModels.isEmpty()) {
				continue;
			} else {
				break;
			}
		}
		return adModels;
	}

	/**
	 * 记录商户投放广告时间
	 *
	 * @throws AdException
	 */
	public void recordPosAd(Map<String, String> params) throws AdException {
		// 当前商户投哪个广告, 记录redis
		// key:bid, value:date,adId
		String suc = params.get(Params.SUCCESS);
		if (suc == null) {
			throw new AdException("success is null");
		}
		boolean succ = Boolean.parseBoolean(suc);
		String did = params.get(Params.DEVICE_ID);
		String bid = params.get(Params.BUSINESS_ID);
		String adid = params.get("adid");
		if (adid == null) {
			throw new AdException("adid is null");
		}
		if (did == null) {
			throw new AdException("did is null");
		}
		if (bid == null) {
			throw new AdException("bid is null");
		}

		if (!succ) {
			LogUtil.posLogger.info("SendFail,did=" + did + ",bid" + bid);
		} else {
			String date = new SimpleDateFormat(posAdRecordDate)
					.format(new Date());
			if (params.get("date") != null) {
				date = params.get("date");
			}
			Map<String, String> hash = new HashMap<String, String>();
			hash.put(date, adid);
			RedisUtil.hmset(bid, hash);
			logger.info("SendOK,adid:" + adid + ",bid:" + bid + ",date:" + date);
		}

	}
}
