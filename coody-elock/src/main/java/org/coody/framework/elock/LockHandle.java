package org.coody.framework.elock;

import org.coody.framework.elock.exception.JedisNotInitedException;
import org.coody.framework.elock.lock.ThreadLocker;
import org.coody.framework.elock.redis.ELockCache;

/**
 * 分布式锁
 * 
 * @author Coody
 *
 */
public class LockHandle {

	/**
	 * 加锁
	 * 
	 * @param key
	 * @param expireSecond
	 * @throws InterruptedException
	 */
	public static void lock(String key, Integer expireSecond) throws InterruptedException {
	
		if (ELockCache.jedisPool == null) {
			throw new JedisNotInitedException("JedisPool未初始化");
		}
		// 锁入列
		ThreadLocker.fallIn(key, expireSecond);
	}

	/**
	 * 释放锁
	 * 
	 * @param key
	 */
	public static void unLock(String key) {
		ThreadLocker.fallOut(key);
	}

}
