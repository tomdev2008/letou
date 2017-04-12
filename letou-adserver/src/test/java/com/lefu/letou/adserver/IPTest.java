package com.lefu.letou.adserver;

import java.util.List;

import com.lefu.letou.adserver.service.AppService;
import com.lefu.letou.common.IPUtil;
import com.lefu.letou.common.Params;
import com.lefu.letou.common.RedisUtil;

public class IPTest {
	public static void main(String[] args) {
		Integer regionIdByIP = RedisUtil.getRegionIdByIP(1034698751l);
		System.out.println(regionIdByIP);
		long l1 = IPUtil.ip2Long("61.172.63.255");
		System.out.println(l1);
		AppService service = new AppService();
		List<Integer> convertRegionId = service
				.convertRegionId("61.172.63.255");

		for (Integer integer : convertRegionId) {
			List<String> name = RedisUtil.getRegionFields(integer + "",
					Params.REDIS_FIELDS_NAME);
			System.out.println(name);
		}
		System.out.println(convertRegionId);

	}
}
