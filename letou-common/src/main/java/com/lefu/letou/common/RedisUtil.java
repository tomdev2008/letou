package com.lefu.letou.common;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

public class RedisUtil {
	private static Logger logger = Logger.getLogger(RedisUtil.class);

	private static JedisSentinelPool JedisSentinelPool;

	public static Integer zRange(final String key, final long ipNum) {
		Set<String> set = execute(new RedisCallback<Set<String>>() {
			@Override
			public Set<String> doinRedis(Jedis jedis) {
				Set<String> zrangeByScore = jedis.zrangeByScore(key, ipNum,
						Double.MAX_VALUE, 0, 1);
				return zrangeByScore;
			}
		});
		if (set == null || set.isEmpty()) {
			return null;
		}
		return Integer.parseInt(set.iterator().next());
	}

	public static Integer getRegionIdByIP(long ip) {
		return zRange(Params.ALLIPS, ip);
	}

	/**
	 * redis读取 key 为 regionId 的多个 fields
	 *
	 * @param regionId
	 *            地域id
	 * @param fields
	 * @return
	 */
	public static List<String> getRegionFields(String regionId,
			String... fields) {
		return hmget(Params.REDIS_PREFIX + Params.REGION + regionId, fields);
	}

	public static void setRegionFields(String regionId, Map<String, String> hash) {
		hmset(Params.REDIS_PREFIX + Params.REGION + regionId, hash);
	}

	public static Long zAdd(final String key, final Map<String, Double> scoreMembers) {
		return execute(new RedisCallback<Long>() {

			@Override
			public Long doinRedis(Jedis jedis) {
				return jedis.zadd(key, scoreMembers);
			}

		});
	}

	/**
	 * 获取商户信息
	 *
	 * @param businessId
	 *            商户id
	 * @param fields
	 * @return
	 */
	public static List<String> getBusinessFields(String businessId,
			String... fields) {
		return hmget(Params.REDIS_PREFIX + Params.REDIS_FIELDS_BUSINESS
				+ businessId, fields);
	}

	public static void setBusinessFields(String businessId,
			Map<String, String> hash) {
		hmset(Params.REDIS_PREFIX + Params.REDIS_FIELDS_BUSINESS + businessId,
				hash);
	}

	public static String set(final String key, final String value) {
		return execute(new RedisCallback<String>() {
			@Override
			public String doinRedis(Jedis jedis) {
				return jedis.set(key, value);
			}
		});
	}

	public static String get(final String key) {
		return execute(new RedisCallback<String>() {
			@Override
			public String doinRedis(Jedis jedis) {
				return jedis.get(key);
			}
		});

	}

	public static String hmset(final String key,final  Map<String, String> map) {
		return execute(new RedisCallback<String>() {

			@Override
			public String doinRedis(Jedis jedis) {
				return jedis.hmset(key, map);
			}

		});
	}

	/**
	 *
	 * @param key
	 * @param fields
	 * @return
	 */
	public static List<String> hmget(final String key, final String... fields) {
		return execute(new RedisCallback<List<String>>() {

			@Override
			public List<String> doinRedis(Jedis jedis) {
				return jedis.hmget(key, fields);
			}
		});
	}

	public static <T> T execute(RedisCallback<T> action) {
		Jedis jedis = getJedis();
		try {
			return action.doinRedis(jedis);
		} finally {
			jedis.close();
		}

	}

	static {
		try {
			loadProperties();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static Jedis getJedis() {
		return JedisSentinelPool.getResource();
	}

	private static void loadProperties() throws IOException {
		Properties props = FileUtil.loadProps("/redis.properties");
		String host = props.getProperty("redis.host");
		String password = props.getProperty("redis.password");
		String masterName = props.getProperty("redis.master.name");
		int dbindex = Integer.parseInt(props.getProperty("redis.dbIndex"));
		int timeout = Integer.parseInt(props
				.getProperty("redis.connection.timeout"));
		int maxTotal = Integer.parseInt(props
				.getProperty("redis.pool.maxTotal"));
		int minIdle = Integer.parseInt(props.getProperty("redis.pool.minIdle"));
		int maxIdle = Integer.parseInt(props.getProperty("redis.pool.maxIdle"));
		int maxWaitMillis = Integer.parseInt(props
				.getProperty("redis.pool.maxWaitMillis"));
		boolean blockWhenExhausted = Boolean.parseBoolean(props
				.getProperty("redis.pool.blockWhenExhausted"));
		boolean testOnBorrow = Boolean.parseBoolean(props
				.getProperty("redis.pool.testOnBorrow"));
		boolean testOnReturn = Boolean.parseBoolean(props
				.getProperty("redis.pool.testOnReturn"));
		boolean testWhileIdle = Boolean.parseBoolean(props
				.getProperty("redis.pool.testWhileIdle"));

		int minEvictableIdleTimeMillis = Integer.parseInt(props
				.getProperty("redis.pool.minEvictableIdleTimeMillis"));
		int timeBetweenEvictionRunsMillis = Integer.parseInt(props
				.getProperty("redis.pool.timeBetweenEvictionRunsMillis"));
		int numTestsPerEvictionRun = Integer.parseInt(props
				.getProperty("redis.pool.numTestsPerEvictionRun"));
		int softMinEvictableIdleTimeMillis = Integer.parseInt(props
				.getProperty("redis.pool.softMinEvictableIdleTimeMillis"));
		boolean lifo = Boolean.parseBoolean(props
				.getProperty("redis.pool.lifo"));

		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(maxTotal);
		jedisPoolConfig.setMinIdle(minIdle);
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
		jedisPoolConfig.setTestOnBorrow(testOnBorrow);
		jedisPoolConfig.setTestOnReturn(testOnReturn);
		jedisPoolConfig.setTestWhileIdle(testWhileIdle);
		jedisPoolConfig
				.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		jedisPoolConfig
				.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		jedisPoolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		jedisPoolConfig
				.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
		jedisPoolConfig.setLifo(lifo);

		Set<String> sentinels = new HashSet<String>();
		sentinels.addAll(Arrays.asList(host.split(";")));

		LoggerUtil.info("redis.host=" + host);

		JedisSentinelPool = new JedisSentinelPool(masterName, sentinels,
				jedisPoolConfig, timeout, password, dbindex);
	}

}
