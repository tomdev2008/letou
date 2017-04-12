package com.lefu.letou.common;

import redis.clients.jedis.Jedis;

public interface RedisCallback<T> {
	T doinRedis(Jedis jedis);
}
