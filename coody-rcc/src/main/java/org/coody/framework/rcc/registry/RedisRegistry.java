package org.coody.framework.rcc.registry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.coody.framework.core.bean.InitBeanFace;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.core.util.random.RandomUtil;
import org.coody.framework.rcc.config.RccConfig;
import org.coody.framework.rcc.entity.RccInstance;
import org.coody.framework.rcc.exception.RccException;
import org.coody.framework.rcc.instance.RccKeepInstance;
import org.coody.framework.rcc.pool.RccThreadPool;
import org.coody.framework.rcc.registry.entity.RccRegistryEntity;
import org.coody.framework.rcc.registry.iface.RccRegistry;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Redis注册中心
 * 
 * @author Coody
 *
 */
public class RedisRegistry implements RccRegistry, InitBeanFace {


	RccRegistryEntity data;

	JedisPool jedisPool;


	public RedisRegistry(JedisPool jedisPool) throws InstantiationException, IllegalAccessException {
		super();
		this.jedisPool = jedisPool;
	}

	@Override
	public Set<RccInstance> getRccInstances(String methodKey) {

		return data.getData().get(methodKey);
	}

	@Override
	public RccInstance getRccInstance(String methodKey) {
		Set<RccInstance> set = getRccInstances(methodKey);
		if (CommonUtil.isNullOrEmpty(set)) {
			throw new RccException("未找到方法服务对象->" + methodKey);
		}
		List<RccInstance> list = new ArrayList<RccInstance>(set);
		Integer[] prs = new Integer[list.size()];
		for (int i = 0; i < prs.length; i++) {
			prs[i] = list.get(i).getPr();
		}
		Integer index = RandomUtil.randomByPr(prs);
		return list.get(index);
	}

	@Override
	public boolean register(String methodKey, String host, Integer port, Integer pr) {

		LogUtil.log.info("注册RCC服务->" + methodKey + "," + host + "," + port + "," + pr);
		RccInstance instance = new RccInstance();
		instance.setHost(host);
		instance.setPort(port);
		instance.setPr(pr);

		String key = String.format("%s:%s", RccConfig.registerKey, methodKey);
		Jedis jedis = jedisPool.getResource();
		// 写方法map
		jedis.hset(key.getBytes(), String.format("%s:%s", host, port.toString()).getBytes(),
				RccKeepInstance.serialer.serialize(instance));

		// 写总key
		jedis.hset(RccConfig.registerKey, key, key);
		return false;
	}

	@Override
	public void init() throws Exception {
		RccThreadPool.GUARD_POOL.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Jedis jedis = null;
					try {
						jedis = jedisPool.getResource();
						Map<String, String> map = jedis.hgetAll(RccConfig.registerKey);
						if (CommonUtil.isNullOrEmpty(map)) {
							continue;
						}
						RccRegistryEntity current = new RccRegistryEntity();
						for (String key : map.keySet()) {
							Map<byte[], byte[]> methodMap = jedis.hgetAll(map.get(key).getBytes());
							if (CommonUtil.isNullOrEmpty(methodMap)) {
								continue;
							}
							Set<RccInstance> list = new HashSet<RccInstance>();
							for (byte[] methodKey : methodMap.keySet()) {
								list.add(RccKeepInstance.serialer.unSerialize(methodMap.get(methodKey)));
							}
							current.getData().put(key, list);
						}
					} catch (Exception e) {
						LogUtil.log.error("同步注册中心数据失败", e);
					} finally {
						try {
							jedis.close();
						} catch (Exception e) {
							// TODO: handle exception
						}
						try {
							TimeUnit.MILLISECONDS.sleep(RccConfig.keepTime);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
	}

}
