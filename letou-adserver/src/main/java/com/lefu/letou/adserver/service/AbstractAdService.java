package com.lefu.letou.adserver.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.lefu.letou.adserver.model.AdException;
import com.lefu.letou.common.AdModel;
import com.lefu.letou.common.IPUtil;
import com.lefu.letou.common.Params;
import com.lefu.letou.common.RedisUtil;

public abstract class AbstractAdService {

	abstract public String findAd(Map<String, String> params, boolean isTest)
			throws AdException;

	protected int getIndustryId(String businessId, boolean isTest, Logger logger) {
		// 从redis 读商户所属的行业
		List<String> businessFields = RedisUtil.getBusinessFields(businessId,
				Params.REDIS_FIELDS_INDUSTRY);
		String s = businessFields.get(0);
		if (s == null) {
			debug("没有找到商户行业信息," + businessId, isTest, logger);
			return 0;
		}
		return Integer.parseInt(s);
	}

	/**
	 * 根据地域查找合适的广告
	 *
	 * @param regionIds
	 * @return
	 */
	protected List<AdModel> getAdByRegion(int productId, Integer regionId,
			boolean isTest, Logger logger) {
		if (regionId == null) {
			regionId = 0;
		}
		Collection<AdModel> values = AdsHolder.getAdsModel().values();

		List<AdModel> models = new ArrayList<AdModel>();
		// Date now = new Date();
		for (AdModel adModel : values) {
			List<Integer> productIds = adModel.getProductIds();
			if (!productIds.contains(productId)) {
				continue;
			}
			List<Integer> targetRegionId = adModel.getTargetRegionId();
			if (!targetRegionId.contains(regionId)) {
				continue;
			}
			// Date startDate = adModel.getStartDate();

			// Calendar c = Calendar.getInstance();
			// c.setTime(now);
			// c.add(Calendar.HOUR_OF_DAY, 3); // 可以提前3小时请求广告
			// now = c.getTime();

			// if (startDate.before(now) && adModel.getEndDate().after(now)) {
			// // 目前不支持活动期间隔天投放, 在活动时间内都会投放
			models.add(adModel);
			// debug("符合投放,adId:" + adModel.getAdId(), isTest, logger);
			// } else {
			// debug("不符合投放时间,id:" + adModel.getAdId(), isTest, logger);
			// }
		}
		return models;
	}

	/**
	 * 随机数选广告
	 *
	 */
	protected AdModel randChooseAdModel(List<AdModel> models)
			throws AdException {
		if (models.isEmpty()) {
			throw new AdException("随机选择广告的范围为空");
		}
		if (models.size() == 1) {
			return models.get(0);
		}
		Random random = new Random();
		int nextInt = random.nextInt(models.size());
		return models.get(nextInt);
	}

	protected void debug(String msg, boolean isDebug, Logger logger) {
		if (isDebug) {
			logger.info("[DEBUG]" + msg);
		}
	}

	protected URI toURI(String scheme, String host, int port, String path,
			String query) throws AdException {
		URI uri;
		try {
			uri = new URI(scheme, null, host, port, path, query, null);
			return uri;
		} catch (URISyntaxException e) {
			throw new AdException("URI错误:"
					+ String.format("%s %s %s %s %s", scheme, host, port, path,
							query), e);
		}
	}

	// protected String postForm(URI uri, List<NameValuePair> params)
	// throws AdException {
	// HttpPost post = new HttpPost(uri);
	// if (!headers.isEmpty()) {
	// for (Header header : headers) {
	// post.setHeader(header);
	// }
	// } else {
	// post.setHeader("Content-Type",
	// "application/x-www-form-urlencoded; charset=UTF-8");
	// }
	// UrlEncodedFormEntity urlEncodedFormEntity;
	// try {
	// urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
	// urlEncodedFormEntity.setContentEncoding("utf-8");
	// post.setEntity(urlEncodedFormEntity);
	// return executeMethd(post);
	// } catch (UnsupportedEncodingException e) {
	// throw new AdException("post参数不支持UTF-8编码");
	// }
	// }

	// protected String get(String url) throws AdException {
	// HttpGet get = new HttpGet(url);
	// return executeMethd(get);
	// }
	//
	// /**
	// * 连接池
	// */
	// private PoolingHttpClientConnectionManager cm = new
	// PoolingHttpClientConnectionManager();
	//
	// private HttpRequestRetryHandler retryHandler = new
	// HttpRequestRetryHandler() {
	// @Override
	// public boolean retryRequest(IOException exception, int executionCount,
	// HttpContext httpContext) {
	// if (executionCount > 3) {
	// LogUtil.getLogger().info(
	// "最大请求次数" + 3 + ", 当前第 " + executionCount + "次请求, 不再重试");
	// return false;
	// }
	// if (exception instanceof NoHttpResponseException) {
	// // 如果服务器丢掉了连接，那么就重试
	// LogUtil.getLogger().error("~~~~~~~~~  请求无响应, 重试 ~~~~~~~~~~~");
	// return true;
	// }
	// return false;
	// }
	// };
	//
	// private CloseableHttpClient client = HttpClients.custom()
	// .setConnectionManager(cm).setRetryHandler(retryHandler).build();
	//
	// private RequestConfig requestConfig = RequestConfig.custom()
	// .setSocketTimeout(1000).setConnectTimeout(1000).build();
	//
	// /**
	// * get 请求
	// */
	// protected String get(URI uri) throws AdException {
	// HttpGet get = new HttpGet(uri);
	// return executeMethd(get);
	// }
	//
	// private String executeMethd(HttpRequestBase method) throws AdException {
	// method.setConfig(requestConfig);
	// CloseableHttpResponse response = null;
	// try {
	// response = client.execute(method);
	// int statusCode = response.getStatusLine().getStatusCode();
	// if (statusCode == 200) {
	// HttpEntity entity = null;
	// try {
	// entity = response.getEntity();
	// return extractResult(entity);
	// } finally {
	// consume(entity);
	// }
	// } else {
	// // 其他错误
	// LogUtil.getLogger().error(extractResult(response.getEntity()));
	// throw new AdException("查询IP库出错:"
	// + response.getStatusLine().toString());
	// }
	//
	// } catch (IOException e) {
	// throw new AdException("请求IP库出错", e);
	// } finally {
	// // method.releaseConnection();
	// if (response != null) {
	// try {
	// response.close();
	// } catch (IOException e) {
	// LogUtil.getLogger().error(e.getMessage());
	// }
	// }
	// }
	// }
	//
	// private void consume(HttpEntity entity) throws AdException {
	// try {
	// EntityUtils.consume(entity);
	// } catch (IOException e) {
	// throw new AdException("consume entity 时出错 : " + e.getMessage());
	// }
	// }
	//
	// private String extractResult(HttpEntity entity) throws AdException {
	// try {
	// return EntityUtils.toString(entity, HTTP.UTF_8);
	// } catch (IOException e) {
	// throw new AdException("解析返回结果错误 : " + e.getMessage(), e);
	// }
	// }

	/**
	 * 转换ip后从redis找到regionId
	 *
	 * @param ip
	 * @return
	 */
	public List<Integer> convertRegionId(String ip) {
		List<Integer> result = new ArrayList<Integer>();
		long num = IPUtil.ip2Long(ip);
		Integer regionId = RedisUtil.getRegionIdByIP(num);
		if (regionId == null) {
			regionId = 0;
		}
		result.add(regionId);
		getRegionId(regionId, result);
		return result;
	}

	protected List<Integer> getRegionId(int regionId, List<Integer> result) {
		if (regionId == 0) {
			return result;
		}
		List<String> regionFields = RedisUtil.getRegionFields(regionId + "",
				Params.REDIS_FIELDS_PARENTID);
		String pId = regionFields.get(0);
		int parentId = Integer.parseInt(pId);

		if (pId != null && parentId != -1) {
			result.add(parentId);
			getRegionId(parentId, result);
		}
		return result;
	}
}
