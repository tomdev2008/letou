package com.lefu.letou.datasender;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONArray;
import com.lefu.letou.common.AdModel;
import com.lefu.letou.common.LoggerUtil;
import com.lefu.letou.common.Params;
import com.lefu.letou.common.RedisUtil;

public class AdsReader implements Job {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			List<Map<String, Object>> executeResult = App.jdbc
					.executeResult("SELECT ad.Id, ad.startDate ,ad.EndDate , ad.TotalQuantity, "
							+ "ad.DailyQuantity,  ad.LimitQuantity , mr.content, mr.landingpage , "
							+ "group_CONCAT(DISTINCT ProductId) pid , GROUP_CONCAT(DISTINCT r.RegionId ) rid "
							+ "FROM ad_advertisement ad "
							+ "INNER JOIN  ad_advertisement_product_ref  ref ON ref.AdvertisementId = ad.Id "
							+ "INNER JOIN  ad_material mr ON mr.AdvertisementId = ad.Id "
							+ "inner join ad_advertisement_region_ref  r on ad.Id = r.AdvertisementId"
							+ " where ad.IsDeleted = 0 "
							+ "and  ad.StartDate < now()  and  now() < DATE_ADD(ad.EndDate, INTERVAL 1 DAY) "
							+ "and ad.Status = 1 "
							+ "group by id ");
			List<AdModel> ads = new ArrayList<AdModel>();
			for (Map<String, Object> map : executeResult) {
				Integer adId = (Integer) map.get("id");
				Date startDate = (Date) map.get("startdate");
				Date endDate = (Date) map.get("enddate");
				Integer limitQuantity = (Integer) map.get("limitquantity");
				if (limitQuantity == null) {
					limitQuantity = 0;
				}
				String content = map.get("content").toString();
				String ldp = map.get("landingpage").toString();

				String productId = map.get("pid") == null ? "" : map.get("pid")
						.toString();
				String targetRegion = map.get("rid") == null ? "" : map.get(
						"rid").toString();

				AdModel model = new AdModel();
				model.setAdId(adId);
				model.setStartDate(startDate);
				model.setEndDate(endDate);
				model.setLimitQuantity(limitQuantity == 1);
				if (limitQuantity == 1) {
					int totalQuantity = map.get("totalquantity") == null ? 0
							: (Integer) map.get("totalquantity");
					int dailyQuantity = map.get("dailyquantity") == null ? 0
							: (Integer) map.get("dailyquantity");
					model.setTotalQuantity(totalQuantity);
					model.setDailyQuantity(dailyQuantity);
				}
				model.setContent(content);
				model.setLdp(ldp);

				model.setProductIds(split(productId));
				model.setTargetRegionId(split(targetRegion));

				ads.add(model);
			}

			String json = JSONArray.toJSONString(ads);
			RedisUtil.set(Params.ALLADS, json);
			LoggerUtil.info(json);
		} catch (SQLException e) {
			LoggerUtil.error(e.getMessage(), e);
		}
	}

	private List<Integer> split(String s) {
		if (s == null || s.equals("")) {
			return Collections.emptyList();
		}
		List<Integer> result = new ArrayList<Integer>();
		String[] arr = s.split(",");
		for (String string : arr) {
			result.add(Integer.parseInt(string));
		}
		return result;
	}

}
