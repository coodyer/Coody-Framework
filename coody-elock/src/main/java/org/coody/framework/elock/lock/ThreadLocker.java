package org.coody.framework.elock.lock;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.coody.framework.elock.config.ClockConfigFactory;
import org.coody.framework.elock.exception.LockTimeOutException;
import org.coody.framework.elock.lock.wrapper.ThreadWrapper;
import org.coody.framework.elock.redis.ELockCache;

@SuppressWarnings("deprecation")
public class ThreadLocker {

	static Logger logger = Logger.getLogger(ThreadLocker.class);

	private static final Map<String, ConcurrentLinkedQueue<ThreadWrapper>> THREAD_CONTAINER = new ConcurrentHashMap<String, ConcurrentLinkedQueue<ThreadWrapper>>();

	static {
		// 启动守护线程
		Thread guardThread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						TimeUnit.MILLISECONDS.sleep(2);
						guard();
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		});
		guardThread.start();
	}

	/**
	 * 线程记录到容器
	 * 
	 * @param key
	 * @throws InterruptedException
	 * @throws LockTimeOutException
	 */
	public static void fallIn(String key, Integer expireSecond) throws InterruptedException {

		Long timeOut = expireSecond.longValue() * 1000;
		ThreadWrapper thread = new ThreadWrapper(Thread.currentThread(), timeOut);
		synchronized (THREAD_CONTAINER) {
			if (!THREAD_CONTAINER.containsKey(key)) {
				ConcurrentLinkedQueue<ThreadWrapper> queue = new ConcurrentLinkedQueue<ThreadWrapper>();
				THREAD_CONTAINER.put(key, queue);
			}
		}
		while (!tryLock(key, expireSecond)) {
			if (thread.isExpire()) {
				throw new LockTimeOutException("等待锁超时>>" + key+">>"+thread.getThread().getId());
			}
			THREAD_CONTAINER.get(key).add(thread);
			logger.debug("线程入列>>" + thread.getThread().getId());
			Thread.currentThread().suspend();
		}
		if (thread.isExpire()) {
				throw new LockTimeOutException("等待锁超时>>" + key+">>"+thread.getThread().getId());
		}
		logger.debug("线程执行>>" + thread.getThread().getId());
	}

	/**
	 * 解锁线程
	 * 
	 * @param key
	 */
	public static void next(String key) {
		ConcurrentLinkedQueue<ThreadWrapper> queue = THREAD_CONTAINER.get(key);
		if (queue == null) {
			return;
		}
		ThreadWrapper thread = queue.poll();
		logger.debug("线程出列>>" + thread);
		if (thread == null) {
			return;
		}
		thread.getThread().resume();
	}

	public static void fallOut(String key) {
		ELockCache.delCache(key);
		ELockCache.publish(ClockConfigFactory.CHANNEL, key);
	}

	private static boolean tryLock(String key, Integer expireSecond) {
		Integer result = ELockCache.setNx(key, expireSecond);
		return result == 1;
	}

	private static synchronized void guard() {
		try {
			for (String key : THREAD_CONTAINER.keySet()) {
				ConcurrentLinkedQueue<ThreadWrapper> queue = THREAD_CONTAINER.get(key);
				Iterator<ThreadWrapper> iterator = queue.iterator();
				while (iterator.hasNext()) {
					ThreadWrapper thread = iterator.next();
					if (thread.isExpire()) {
						queue.remove(thread);
						thread.getThread().resume();
					}
				}
			}
		} catch (Exception e) {
		}
	}
}
