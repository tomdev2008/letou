package com.lefu.letou.datasender;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.lefu.letou.common.RedisCallback;
import com.lefu.letou.common.RedisUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class RedisClear {
	public static void main(String[] args) {
		RedisUtil.execute(new RedisCallback<Integer>() {

			@Override
			public Integer doinRedis(Jedis jedis) {
				String flushDB = jedis.flushDB();
				System.out.println(flushDB);
				return null;
			}
		});
	}

}
