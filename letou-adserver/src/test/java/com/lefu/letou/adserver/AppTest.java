package com.lefu.letou.adserver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import com.alibaba.fastjson.JSONArray;
import com.lefu.letou.common.AdModel;
import com.lefu.letou.common.Params;
import com.lefu.letou.common.PlanModel;
import com.lefu.letou.common.RedisUtil;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

	// 写测试数据
	public static void main(String[] args) throws ParseException {
//		writeAds();
		writeBusiness();
		// writeRegions();
	}

	private static void writeBusiness() {
		HashMap<String, String> b1 = new HashMap<String, String>();
		b1.put(Params.REDIS_FIELDS_REGIONID, 7 + ""); // 北京
		b1.put(Params.REDIS_FIELDS_NAME, "商户名称1");
		b1.put(Params.REDIS_FIELDS_INDUSTRY, 1 + "");
		RedisUtil.setBusinessFields(1 + "", b1);

		HashMap<String, String> b2 = new HashMap<String, String>();
		b2.put(Params.REDIS_FIELDS_REGIONID, 9 + ""); // 天津
		b2.put(Params.REDIS_FIELDS_NAME, "商户名称1");
		b2.put(Params.REDIS_FIELDS_INDUSTRY, 2 + "");
		RedisUtil.setBusinessFields(2 + "", b2);
		System.out.println(11);
	}

	private static void writeAds() throws ParseException {
		JSONArray arr = new JSONArray();
		Date endDate = sdf.parse("2016-06-31-08:00:00");
		for (int i = 1; i <= 4; i++) {
			AdModel ad = new AdModel();
			ad.setAdId(i);
			ad.setDailyQuantity(10000 + i);
			ad.setStartDate(new Date());
			ad.setEndDate(endDate);
			ad.setLimitQuantity(true);
			List<Integer> regions = new ArrayList<Integer>();
			ad.setTargetRegionId(regions);
			ad.setTotalQuantity(20000 + i * 10);
			// 1,2 POS机, 3,4 app
			List<Integer> product = new ArrayList<Integer>();
			product.add(i);
			ad.setProductIds(product);
			if (i == 1) {
				// ad.setContent("https://www.baidu.com/img/baidu_jgylogo3.gif");
				ad.setContent("全国广告内容");
				regions.add(0);
			}
			if (i == 2) {
				// ad.setContent("http://static.open-open.com/lib/uploadImg/20140109/20140109205646_795.jpg");
				ad.setContent("北京内容2");
				regions.add(11);
			}
			if (i == 3) {
				ad.setContent("https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=2183406100,4080587798&fm=58");
				ad.setWidth(200);
				ad.setHeight(200);
				ad.setExtName("png");
				regions.add(0);
			}
			if (i == 4) {
				ad.setContent("http://www.voiceads.cn/img/intro/logo.png");
			}
			ad.setLdp("https://www.baidu.com/search/error.html?id=" + i);
			for (int j = 0; j < 3; j++) {
				PlanModel p = new PlanModel();
				p.setAdId(i);
				p.setStartDate(new Date());
				p.setEndDate(endDate);
				p.setImpression(2000);
				p.setPlanId(j + 1);
				// ad.addPlan(p);
			}
			arr.add(ad);
		}
		String str = arr.toString();
		System.out.println(str);
		RedisUtil.set(Params.ALLADS, str);
	}
}
