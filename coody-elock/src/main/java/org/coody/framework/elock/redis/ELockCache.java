package org.coody.framework.elock.redis;

import java.util.Map;

import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.elock.config.ClockConfigFactory;
import org.coody.framework.elock.pointer.ELockerPointer;
import org.coody.framework.elock.redis.entity.SubscriberEntity;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

public class ELockCache {
	
	
	private static final String REDIS_SUCCESS_FLAG="OK";

	public JedisPool jedisPool;

	public ELockCache() {
		ELockerPointer.setELockCache(this);
	}

	public ELockCache(JedisPool jedisPool) {
		setJedisPool(jedisPool);
		ELockerPointer.setELockCache(this);
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public ELockCache setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
		ELockerPointer.setELockCache(this);
		final Jedis jedis = jedisPool.getResource();
		final SubscriberEntity subscriberEntity = new SubscriberEntity();
		Thread thread = new Thread(new Runnable() {
			public void run() {
				jedis.subscribe(subscriberEntity, ClockConfigFactory.CHANNEL);
			}
		});
		thread.start();
		return this;
	}

	public synchronized static ELockCache initJedisPool(String host, Integer port, String secretKey, Integer timeOut) {
		return initJedisPool(host, port, secretKey, timeOut, new JedisPoolConfig());
	}

	public synchronized static ELockCache initJedisPool(JedisPool inJediPool) {
		return new ELockCache().setJedisPool(inJediPool);
	}

	public boolean isConnectioned() {
		if (jedisPool == null) {
			return false;
		}
		return true;
	}

	public synchronized static ELockCache initJedisPool(String host, Integer port, String secretKey, Integer timeOut,
			JedisPoolConfig jedisPoolConfig) {
		if (StringUtil.isNullOrEmpty(secretKey)) {
			secretKey = null;
		}
		JedisPool inJediPool = new JedisPool(jedisPoolConfig, host, port, 10000, secretKey);
		return initJedisPool(inJediPool);
	}

	public synchronized static void initJedisPool(String host, Integer port, String secretKey, Integer timeOut,
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

	public boolean setNx(String key, Integer expireSecond) {
		Jedis jedis = jedisPool.getResource();
		try {
			SetParams setParams=new SetParams();
			setParams.ex(expireSecond);
			setParams.nx();
			String result=jedis.set(key, String.valueOf(Thread.currentThread().getId()),setParams);
			if(StringUtil.isNullOrEmpty(result)){
				return false;
			}
			if(REDIS_SUCCESS_FLAG.equals(result)){
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	public String getNx(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.get(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	public void delCache(String key) {
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

	public void publish(String channel, String msg) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.publish(channel, msg);
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
