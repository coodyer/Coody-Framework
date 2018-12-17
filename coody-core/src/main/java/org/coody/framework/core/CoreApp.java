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
import org.coody.framework.core.loader.AspectLoader;
import org.coody.framework.core.loader.BeanLoader;
import org.coody.framework.core.loader.CustomBeanLoader;
import org.coody.framework.core.loader.FieldLoader;
import org.coody.framework.core.loader.InitRunLoader;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.logger.BaseLogger;
import org.coody.framework.core.util.ClassUtil;
import org.coody.framework.core.util.StringUtil;

public class CoreApp {

	static BaseLogger logger = BaseLogger.getLogger(CoreApp.class);

	@SuppressWarnings("serial")
	static Map<Integer, List<Class<?>>> loadersMap = new TreeMap<Integer, List<Class<?>>>() {
		{
			put(1, Arrays.asList(new Class<?>[] { AspectLoader.class }));
			put(2, Arrays.asList(new Class<?>[] { CustomBeanLoader.class }));
			put(3, Arrays.asList(new Class<?>[] { BeanLoader.class }));
			put(4, Arrays.asList(new Class<?>[] { FieldLoader.class }));
			put(Integer.MAX_VALUE, Arrays.asList(new Class<?>[] { InitRunLoader.class }));
		}
	};

	public static Set<Class<?>> initLoader(String assember) throws ClassNotFoundException {
		String[] loaders = assember.split(",");
		for (String loader : loaders) {
			Class<?> loaderClazz = Class.forName(loader.trim());
			if (!CoodyLoader.class.isAssignableFrom(loaderClazz)) {
				throw new CoodyException(loaderClazz.getName() + "不是加载器");
			}
			Integer seq = Integer.MAX_VALUE - 1;
			Order order = loader.getClass().getAnnotation(Order.class);
			if (order != null) {
				seq = order.value();
			}
			if (loadersMap.containsKey(seq)) {
				loadersMap.get(seq).add(loaderClazz);
				continue;
			}
			List<Class<?>> loaderList = new ArrayList<Class<?>>();
			loaderList.add(loaderClazz);
			loadersMap.put(seq, loaderList);
		}
		Set<Class<?>> currentLoaders = new HashSet<Class<?>>();
		for (Integer key : loadersMap.keySet()) {
			for (Class<?> clazz : loadersMap.get(key)) {
				if (currentLoaders.contains(clazz)) {
					continue;
				}
				currentLoaders.add(clazz);
			}
		}
		if (StringUtil.isNullOrEmpty(currentLoaders)) {
			throw new InitException("加载器为空");
		}
		return currentLoaders;
	}

	public static Set<Class<?>> initScanner(String packager) {
		// 加载扫描包列表
		String[] packets = packager.split(",");
		Set<Class<?>> clazzs = new HashSet<Class<?>>();
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
		Set<Class<?>> loaders = initLoader(config.assember);
		// 初始化扫描类
		Set<Class<?>> clazzs = initScanner(config.packager);
		
		BeanContainer.setClazzContainer(clazzs);
		// 进行加载操作
		long tInit = System.currentTimeMillis();
		for (Class<?> loader : loaders) {
			logger.info(loader.getName() + " >>开始加载");
			long t0 = System.currentTimeMillis();
			CoodyLoader icopLoader = (CoodyLoader) loader.newInstance();
			icopLoader.doLoader();
			long t1 = System.currentTimeMillis();
			logger.info(loader.getName() + " >>加载耗时:" + (t1 - t0) + "ms");
		}
		long tEnd = System.currentTimeMillis();
		logger.info("Coody Framework >>加载耗时:" + (tEnd - tInit) + "ms");
	}

}
