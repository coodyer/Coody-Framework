package org.coody.framework.core;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.coody.framework.core.logger.BaseLogger;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.ClassUtil;
import org.coody.framework.core.util.StringUtil;
import org.coody.framework.core.util.UnsafeUtil;

public class CoreApp {

	static BaseLogger logger = BaseLogger.getLogger(CoreApp.class);

	@SuppressWarnings("serial")
	static Map<Integer, List<Class<?>>> loadersMap = new TreeMap<Integer, List<Class<?>>>() {
		{
			put(0, Arrays.asList(new Class<?>[] { AnnotationLoader.class }));
			put(1, Arrays.asList(new Class<?>[] { AspectLoader.class }));
			put(2, Arrays.asList(new Class<?>[] { CustomBeanLoader.class, BeanLoader.class }));
			put(4, Arrays.asList(new Class<?>[] { FieldLoader.class }));
			put(Integer.MAX_VALUE, Arrays.asList(new Class<?>[] { InitRunLoader.class }));
		}
	};

	public static void initLoader(String assember) throws ClassNotFoundException {
		String[] loaders = assember.split(",");
		for (String loader : loaders) {
			if (StringUtil.isNullOrEmpty(loader)) {
				continue;
			}
			Class<?> loaderClazz = Class.forName(loader.trim());
			if (!CoodyLoader.class.isAssignableFrom(loaderClazz)) {
				throw new CoodyException(loaderClazz.getName() + "不是加载器");
			}
			Integer seq = Integer.MAX_VALUE - 1;
			Order order = loader.getClass().getAnnotation(Order.class);
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
		// 加载扫描包列表
		String[] packets = packager.split(",");
		Set<Class<?>> clazzs = ClassUtil.getClasses("org.coody.framework");
		for (String packet : packets) {
			Set<Class<?>> clazzsTemp = ClassUtil.getClasses(packet);
			clazzs.addAll(clazzsTemp);
		}
		if (StringUtil.isNullOrEmpty(clazzs)) {
			throw new InitException("扫描类为空");
		}
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
			if (StringUtil.isNullOrEmpty(loadersMap.get(seq))) {
				continue;
			}
			ThreadBlockPool pool = new ThreadBlockPool(loadersMap.get(seq).size(), 60);
			for (Class<?> loader : loadersMap.get(seq)) {
				pool.pushTask(new Runnable() {
					@Override
					public void run() {
						try {
							logger.debug(loader.getName() + " >>开始加载");
							long t0 = System.currentTimeMillis();
							CoodyLoader icopLoader = (CoodyLoader) UnsafeUtil.createInstance(loader);
							icopLoader.doLoader();
							long t1 = System.currentTimeMillis();
							logger.info(loader.getName() + " >>加载耗时:" + (t1 - t0) + "ms");
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
			}
			pool.execute();
		}
		long tEnd = System.currentTimeMillis();
		logger.info("Coody Framework >>加载耗时:" + (tEnd - tInit) + "ms");
	}

}
