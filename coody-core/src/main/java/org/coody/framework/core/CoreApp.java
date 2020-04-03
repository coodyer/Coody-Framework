package org.coody.framework.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.coody.framework.core.annotation.Order;
import org.coody.framework.core.config.CoodyConfig;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.exception.InitException;
import org.coody.framework.core.exception.base.CoodyException;
import org.coody.framework.core.loader.AnnotationLoader;
import org.coody.framework.core.loader.AspectLoader;
import org.coody.framework.core.loader.BeanLoader;
import org.coody.framework.core.loader.CustomBeanLoader;
import org.coody.framework.core.loader.FieldLoader;
import org.coody.framework.core.loader.InitRunLoader;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.clazz.ClassUtil;
import org.coody.framework.core.util.log.LogUtil;

public class CoreApp {

	@SuppressWarnings("serial")
	static Map<Integer, List<Class<?>>> loadersMap = new TreeMap<Integer, List<Class<?>>>() {
		{
			put(0, new ArrayList<Class<?>>(Arrays.asList(new Class<?>[] { AnnotationLoader.class })));
			put(1, new ArrayList<Class<?>>(Arrays.asList(new Class<?>[] { AspectLoader.class })));
			put(2, new ArrayList<Class<?>>(Arrays.asList(new Class<?>[] { CustomBeanLoader.class, BeanLoader.class })));
			put(4, new ArrayList<Class<?>>(Arrays.asList(new Class<?>[] { FieldLoader.class })));
			put(Integer.MAX_VALUE, new ArrayList<Class<?>>(Arrays.asList(new Class<?>[] { InitRunLoader.class })));
		}
	};

	public static void initLoader(String assember) throws ClassNotFoundException {
		String[] loaders = assember.split(",");
		for (String loader : loaders) {
			if (CommonUtil.isNullOrEmpty(loader)) {
				continue;
			}
			Class<?> loaderClazz = Class.forName(loader.trim());
			if (!CoodyLoader.class.isAssignableFrom(loaderClazz)) {
				throw new CoodyException(loaderClazz.getName() + "不是加载器");
			}
			Integer seq = Integer.MAX_VALUE - 1;
			Order order = loaderClazz.getAnnotation(Order.class);
			if (order != null) {
				seq = order.value();
			}
			if (!loadersMap.containsKey(seq)) {
				loadersMap.put(seq, new ArrayList<Class<?>>());
			}
			loadersMap.get(seq).add(loaderClazz);
		}
	}

	public static Set<Class<?>> initScanner(String packager) {
		long startTime = System.currentTimeMillis();
		// 加载扫描包列表
		List<String> packets = new ArrayList<String>();
		packets.add("org.coody.framework");
		packets.addAll(Arrays.asList(packager.split(",")));
		Set<Class<?>> clazzs = new HashSet<Class<?>>();
		for (String packet : packets) {
			Set<Class<?>> clazzsTemp = ClassUtil.getClasses(packet);
			clazzs.addAll(clazzsTemp);
		}
		if (CommonUtil.isNullOrEmpty(clazzs)) {
			throw new InitException("扫描类为空");
		}
		LogUtil.log.info("扫描类>>数量:" + clazzs.size() + ",耗时:" + (System.currentTimeMillis() - startTime));
		return clazzs;
	}

	public static void init(CoodyConfig config) throws Exception {
		// 初始化组建加载器
		initLoader(config.assember);
		// 初始化扫描类
		Set<Class<?>> clazzs = initScanner(config.packager);

		BeanContainer.setClazzContainer(clazzs);
		// 进行加载操作
		long tInit = System.currentTimeMillis();
		for (Integer seq : loadersMap.keySet()) {
			if (CommonUtil.isNullOrEmpty(loadersMap.get(seq))) {
				continue;
			}
			ThreadBlockPool pool = new ThreadBlockPool(loadersMap.get(seq).size(), 7200);
			for (Class<?> loaderClazz : loadersMap.get(seq)) {
				pool.pushTask(new Runnable() {
					@Override
					public void run() {
						try {
							LogUtil.log.debug(loaderClazz.getName() + " >>开始加载");
							long t0 = System.currentTimeMillis();
							CoodyLoader loader = (CoodyLoader) loaderClazz.newInstance();
							loader.doLoader();
							long t1 = System.currentTimeMillis();
							LogUtil.log.info(loaderClazz.getName() + " >>加载耗时:" + (t1 - t0) + "ms");
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
			}
			pool.execute();
		}
		long tEnd = System.currentTimeMillis();
		LogUtil.log.info("Coody Framework >>加载耗时:" + (tEnd - tInit) + "ms");
	}

}
