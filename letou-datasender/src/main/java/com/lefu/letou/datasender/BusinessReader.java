package com.lefu.letou.datasender;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lefu.letou.common.LoggerUtil;
import com.lefu.letou.common.Params;
import com.lefu.letou.common.RedisUtil;

public class BusinessReader implements Job {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			List<Map<String, Object>> maps = App.jdbc
					.executeResult("select b.customer_no  bid, b.short_name name ,  industry.id  industryid, r.id regionid  "
							+ "from ad_business_info   b "
							+ "inner join ad_industry industry  on b.mcc = industry.mcc "
							+ "inner join ad_region r  on b.organization_code = r.id");

			for (Map<String, Object> map : maps) {
				String bid = map.get("bid").toString();
				String bName = map.get("name") == null ? "" : map.get("name")
						.toString();
				int industryId = map.get("industryid") == null ? 0
						: (Integer) map.get("industryid");
				int regionId = map.get("regionid") == null ? 0 : (Integer) map
						.get("regionid");

				HashMap<String, String> bMap = new HashMap<String, String>();
				bMap.put(Params.REDIS_FIELDS_REGIONID, regionId + ""); // 北京
				bMap.put(Params.REDIS_FIELDS_NAME, bName);
				bMap.put(Params.REDIS_FIELDS_INDUSTRY, industryId + "");
				RedisUtil.setBusinessFields(bid + "", bMap);
			}

			LoggerUtil.info("商户同步完成" + maps.size());
		} catch (SQLException e) {
			LoggerUtil.error(e.getMessage(), e);
		}

	}

}
