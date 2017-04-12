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

public class IPReader implements Job {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			List<Map<String, Object>> maps = App.jdbc
					.executeResult("SELECT id,regionid, startip, endip FROM ad_region_ip  ORDER BY endip asc ");

			// Map<String, String> hash = new HashMap<String, String>();
			Map<String, Double> hash = new HashMap<String, Double>();
			for (Map<String, Object> map : maps) {
				Integer regionId = (Integer) map.get("regionid");
				String endIP = map.get("endip").toString();
				hash.put(regionId.toString(), new Double(endIP ));
			}
			RedisUtil.zAdd(Params.ALLIPS, hash);
			LoggerUtil.info("ip同步完成" + hash.size());
		} catch (SQLException e) {
			LoggerUtil.error(e.getMessage(), e);
		}

	}

}
