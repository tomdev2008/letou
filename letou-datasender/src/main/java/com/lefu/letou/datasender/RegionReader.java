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

public class RegionReader implements Job {
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			List<Map<String, Object>> regions = App.jdbc
					.executeResult("SELECT id,name,english, parentid FROM   ad_region   ORDER BY Id ");

			Map<String, String> hash = new HashMap<String, String>();
			for (Map<String, Object> map : regions) {
				String regionId = map.get("id").toString();
				String name = map.get("name") == null ? "" : (String) map
						.get("name");
				String english = map.get("english") == null ? "" : (String) map
						.get("english");
				Integer parentId = (Integer) map.get("parentid");

				hash.put(Params.REDIS_FIELDS_NAME, name);
				hash.put(Params.REDIS_FIELDS_ENGLISH, english);
				hash.put(Params.REDIS_FIELDS_PARENTID, parentId.toString());

				RedisUtil.setRegionFields(regionId, hash);
			}
			LoggerUtil.info("Region同步完成" +regions.size());
		} catch (SQLException e) {
			LoggerUtil.error(e.getMessage(), e);
		}
	}

}
