package org.coody.framework.elock.redis;

import java.util.Map;

import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.elock.config.ClockConfigFactory;
import org.coody.framework.elock.redis.entity.SubscriberEntity;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class ELockCache {

	public static JedisPool jedisPool;

	public static synchronized void initJedisPool(String host, Integer port, String secretKey, Integer timeOut) {
		initJedisPool(host, port, secretKey, timeOut, new JedisPoolConfig());
	}

	public static synchronized void initJedisPool(JedisPool inJediPool) {
		jedisPool=inJediPool;
		final Jedis jedis = ELockCache.jedisPool.getResource();
		final SubscriberEntity subscriberEntity = new SubscriberEntity();
		Thread thread=new Thread(new Runnable() {
			public void run() {
				jedis.subscribe(subscriberEntity, ClockConfigFactory.CHANNEL);
			}
		});
		thread.start();
	}
	public static synchronized void initJedisPool(String host, Integer port, String secretKey, Integer timeOut,
			JedisPoolConfig jedisPoolConfig) {
		if (StringUtil.isNullOrEmpty(secretKey)){
			secretKey=null;
		}
		JedisPool inJediPool = new JedisPool(jedisPoolConfig, host, port, 10000, secretKey);
		initJedisPool(inJediPool);
	}

	public static synchronized void initJedisPool(String host, Integer port, String secretKey, Integer timeOut,
			Map<String, Object> jedisPoolConfig) {
		JedisPoolConfig config = new JedisPoolConfig();
		// 设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
		for (String key : jedisPoolConfig.keySet()) {
			try {
				PropertUtil.setFieldValue(config, key, jedisPoolConfig.get(key));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		initJedisPool(host, port, secretKey, timeOut, config);
	}

	public static Integer setNx(String key, Integer expire) {
		Jedis jedis = jedisPool.getResource();
		try {
			Long result = jedis.setnx(key, "1");
			jedis.expire(key, expire);
			return result.intValue();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static void delCache(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static void publish(String channel, String msg) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.publish(ClockConfigFactory.CHANNEL, msg);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

	}
}
