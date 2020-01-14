package org.coody.framework.core.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.coody.framework.core.assember.BeanAssember;
import org.coody.framework.core.builder.ConfigBuilder;
import org.coody.framework.core.config.CoodyConfig;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.exception.BeanNotFoundException;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.match.MatchUtil;

public class CustomBeanLoader implements CoodyLoader {

	static final HashMap<String, HashMap<String, String>> PARAMENT_MAP = new HashMap<String, HashMap<String, String>>();

	static final HashMap<String, HashMap<String, String>> FIELD_MAP = new HashMap<String, HashMap<String, String>>();

	static final HashMap<String, HashMap<String, String>> CONFIG_MAP = new HashMap<String, HashMap<String, String>>();

	@Override
	public void doLoader() throws Exception {
		// 加载基础Bean配置
		for (String key : ConfigBuilder.propertyKeySet()) {
			String configValue = ConfigBuilder.getProperty(key);
			if (CommonUtil.isNullOrEmpty(configValue)) {
				continue;
			}
			if (MatchUtil.isParameterMatch(key, CoodyConfig.BEAN_CONFIG_MAPPER)) {
				Map<String, String> beanConfig = MatchUtil.exporeMatchedMap(key, CoodyConfig.BEAN_CONFIG_MAPPER);
				if (CommonUtil.isNullOrEmpty(beanConfig)) {
					continue;
				}
				String beanName = beanConfig.get(CoodyConfig.BEAN_NAME);
				if (!CONFIG_MAP.containsKey(beanName)) {
					CONFIG_MAP.put(beanName, new HashMap<String, String>());
				}
				String property = beanConfig.get(CoodyConfig.PROPERTY);
				CONFIG_MAP.get(beanName).put(property, configValue);
			}
		}
		// 加载构造函数属性名
		for (String key : ConfigBuilder.propertyKeySet()) {
			String configValue = ConfigBuilder.getProperty(key);
			if (CommonUtil.isNullOrEmpty(configValue)) {
				continue;
			}
			if (MatchUtil.isParameterMatch(key, CoodyConfig.BEAN_PARAMENT_MAPPER)) {
				Map<String, String> beanConfig = MatchUtil.exporeMatchedMap(key, CoodyConfig.BEAN_PARAMENT_MAPPER);
				if (CommonUtil.isNullOrEmpty(beanConfig)) {
					continue;
				}
				String beanName = beanConfig.get(CoodyConfig.BEAN_NAME);
				if (!PARAMENT_MAP.containsKey(beanName)) {
					PARAMENT_MAP.put(beanName, new HashMap<String, String>());
				}
				String property = beanConfig.get(CoodyConfig.PROPERTY);
				PARAMENT_MAP.get(beanName).put(property, configValue);
			}
		}
		// 加载Bean字段名
		for (String key : ConfigBuilder.propertyKeySet()) {
			String configValue = ConfigBuilder.getProperty(key);
			if (CommonUtil.isNullOrEmpty(configValue)) {
				continue;
			}
			if (MatchUtil.isParameterMatch(key, CoodyConfig.BEAN_FIELD_MAPPER)) {
				Map<String, String> beanConfig = MatchUtil.exporeMatchedMap(key, CoodyConfig.BEAN_FIELD_MAPPER);
				if (CommonUtil.isNullOrEmpty(beanConfig)) {
					continue;
				}
				String beanName = beanConfig.get(CoodyConfig.BEAN_NAME);
				if (!FIELD_MAP.containsKey(beanName)) {
					FIELD_MAP.put(beanName, new HashMap<String, String>());
				}
				String property = beanConfig.get(CoodyConfig.PROPERTY);
				FIELD_MAP.get(beanName).put(property, configValue);
			}
		}
		// 初始化所有Bean
		Collection<List<String>> originalBeans = doRelationOrdered(PARAMENT_MAP, FIELD_MAP, CONFIG_MAP);
		for (List<String> keys : originalBeans) {
			ThreadBlockPool pool = new ThreadBlockPool(keys.size(), 60);
			for (String key : keys) {
				pool.pushTask(new Runnable() {
					@Override
					public void run() {
						initBean(key);
					}
				});
			}
			pool.execute();
		}
	}

	private void initBean(String key) {
		try {
			Map<String, String> beanConfig = PARAMENT_MAP.get(key);
			String clazzName = CONFIG_MAP.get(key).get(CoodyConfig.CLASS_NAME);
			Class<?> clazz = Class.forName(clazzName);
			Map<String, Object> parameters = null;
			if (PARAMENT_MAP.containsKey(key)) {
				parameters = builderParamenterMap(clazz, beanConfig);
			}
			Object bean = BeanAssember.initBean(clazz, key, parameters);
			if (bean == null) {
				return;
			}
			// 初始化字段
			Map<String, String> fieldConfig = FIELD_MAP.get(key);
			if (CommonUtil.isNullOrEmpty(fieldConfig)) {
				return;
			}
			Map<String, Object> fielders = builderParamenterMap(bean.getClass(), fieldConfig);
			BeanAssember.initField(bean, fielders);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 根据依赖关系排序
	 * 
	 * @param clazz
	 * @param parameneterMap
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Collection<List<String>> doRelationOrdered(HashMap<String, HashMap<String, String>> paramentMap,
			HashMap<String, HashMap<String, String>> fieldMap, HashMap<String, HashMap<String, String>> configMap)
			throws ClassNotFoundException {
		// 加载基础Bean
		Map<String, Integer> beanMap = new HashMap<String, Integer>();

		List<String> group = new ArrayList<String>();
		configCheck: for (String key : configMap.keySet()) {
			Map<String, String> propertyMap = new HashMap<String, String>();
			if (fieldMap.containsKey(key)) {
				propertyMap.putAll(fieldMap.get(key));
			}
			if (paramentMap.containsKey(key)) {
				propertyMap.putAll(paramentMap.get(key));
			}
			if (CommonUtil.isNullOrEmpty(propertyMap)) {
				group.add(key);
			}
			for (String poperty : propertyMap.keySet()) {
				String fieldBeanName = MatchUtil.exporeMatchedFirstByRegular(propertyMap.get(poperty),
						CoodyConfig.INPUT_BEAN_MAPPER);
				if (!CommonUtil.isNullOrEmpty(fieldBeanName)) {
					if (!configMap.containsKey(fieldBeanName)) {
						if (!configMap.get(key).containsKey(CoodyConfig.CLASS_NAME)) {
							continue configCheck;
						}
						Class<?> clazz = Class.forName(configMap.get(key).get(CoodyConfig.CLASS_NAME));
						throw new BeanNotFoundException(fieldBeanName, clazz);
					}
					continue configCheck;
				}
			}
			group.add(key);
		}
		int index = beanMap.size();
		for (String line : group) {
			beanMap.put(line, index);
		}
		beanInstallWhile: while (beanMap.size() < configMap.size()) {
			group = new ArrayList<String>();
			try {
				for (String key : configMap.keySet()) {
					if (beanMap.containsKey(key)) {
						continue;
					}
					Map<String, String> propertyMap = new HashMap<String, String>();
					if (fieldMap.containsKey(key)) {
						propertyMap.putAll(fieldMap.get(key));
					}
					if (paramentMap.containsKey(key)) {
						propertyMap.putAll(paramentMap.get(key));
					}
					for (String poperty : propertyMap.keySet()) {
						String valueBeanName = MatchUtil.exporeMatchedFirstByRegular(propertyMap.get(poperty),
								CoodyConfig.INPUT_BEAN_MAPPER);
						if (!CommonUtil.isNullOrEmpty(valueBeanName)) {
							if (!beanMap.containsKey(valueBeanName)) {
								continue beanInstallWhile;
							}
						}
					}
					group.add(key);
				}
			} finally {
				index = beanMap.size();
				for (String line : group) {
					beanMap.put(line, index);
				}
			}
		}
		Map<Integer, List<String>> relationBeanMap = new TreeMap<Integer, List<String>>();
		for (String key : beanMap.keySet()) {
			if (!relationBeanMap.containsKey(beanMap.get(key))) {
				relationBeanMap.put(beanMap.get(key), new ArrayList<String>());
			}
			relationBeanMap.get(beanMap.get(key)).add(key);
		}
		return relationBeanMap.values();
	}

	private Map<String, Object> builderParamenterMap(Class<?> clazz, Map<String, String> parameneterMap) {
		Map<String, Object> paramenters = new HashMap<String, Object>();
		for (String parameneter : parameneterMap.keySet()) {
			String value = parameneterMap.get(parameneter);
			if (!MatchUtil.isParameterMatch(value, CoodyConfig.INPUT_BEAN_MAPPER)) {
				paramenters.put(parameneter, value);
				continue;
			}
			String valueBeanName = MatchUtil.exporeMatchedFirstByRegular(value, CoodyConfig.INPUT_BEAN_MAPPER);
			if (CommonUtil.isNullOrEmpty(valueBeanName)) {
				throw new BeanNotFoundException(valueBeanName, clazz);
			}
			Object inputBean = BeanContainer.getBean(valueBeanName);
			if (inputBean == null) {
				throw new BeanNotFoundException(valueBeanName, clazz);
			}
			paramenters.put(parameneter, inputBean);
		}
		return paramenters;
	}

}
