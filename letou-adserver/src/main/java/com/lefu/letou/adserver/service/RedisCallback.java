package com.lefu.letou.adserver.service;

import redis.clients.jedis.Jedis;

public interface RedisCallback<T> {
	T doinRedis(Jedis jedis);
}
