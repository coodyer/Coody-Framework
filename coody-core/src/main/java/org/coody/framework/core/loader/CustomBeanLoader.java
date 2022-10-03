package org.coody.framework.core.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import org.coody.framework.core.assember.BeanAssember;
import org.coody.framework.core.builder.ConfigBuilder;
import org.coody.framework.core.config.CoodyConfig;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.exception.BeanNotFoundException;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.log.LogUtil;
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
		List<String> beans = doRelation(PARAMENT_MAP, FIELD_MAP, CONFIG_MAP);
		for (String key : beans) {
			initBean(key);
		}
	}

	private void initBean(String key) {
		try {
			Map<String, String> beanConfig = PARAMENT_MAP.get(key);
			String clazzName = CONFIG_MAP.get(key).get(CoodyConfig.CLASS_NAME);
			Class<?> clazz = Class.forName(clazzName);
			LogUtil.log.debug("创建Bean >>" + clazz.getName());
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
	public List<String> doRelation(HashMap<String, HashMap<String, String>> paramentMap,
			HashMap<String, HashMap<String, String>> fieldMap, HashMap<String, HashMap<String, String>> configMap)
			throws ClassNotFoundException {
		// 加载基础Bean
		List<Entry<String, HashMap<String, String>>> beanArray = new ArrayList<Map.Entry<String, HashMap<String, String>>>(
				configMap.entrySet());

		for (Entry<String, HashMap<String, String>> entry : configMap.entrySet()) {
			if (paramentMap.containsKey(entry.getKey())) {
				for (String field : paramentMap.get(entry.getKey()).keySet()) {
					String name = MatchUtil.exporeMatchedFirstByRegular(paramentMap.get(entry.getKey()).get(field),
							CoodyConfig.INPUT_BEAN_MAPPER);
					if (!CommonUtil.isNullOrEmpty(name)) {
						beanArray.remove(entry);
					}
				}
			}
			if (fieldMap.containsKey(entry.getKey())) {
				for (String field : fieldMap.get(entry.getKey()).keySet()) {
					String name = MatchUtil.exporeMatchedFirstByRegular(fieldMap.get(entry.getKey()).get(field),
							CoodyConfig.INPUT_BEAN_MAPPER);
					if (!CommonUtil.isNullOrEmpty(name)) {
						beanArray.remove(entry);
					}
				}
			}
		}
		LinkedBlockingQueue<Entry<String, HashMap<String, String>>> queue = new LinkedBlockingQueue<Map.Entry<String, HashMap<String, String>>>();
		for (Entry<String, HashMap<String, String>> entry : configMap.entrySet()) {
			queue.offer(entry);
		}
		while (!queue.isEmpty()) {
			Entry<String, HashMap<String, String>> line = queue.poll();
			if (beanArray.contains(line)) {
				continue;
			}
			if (paramentMap.containsKey(line.getKey())) {
				for (String field : paramentMap.get(line.getKey()).keySet()) {
					String name = MatchUtil.exporeMatchedFirstByRegular(paramentMap.get(line.getKey()).get(field),
							CoodyConfig.INPUT_BEAN_MAPPER);
					if (CommonUtil.isNullOrEmpty(name)) {
						continue;
					}
					for (Entry<String, HashMap<String, String>> entry : beanArray) {
						if (entry.getKey().equals(name)) {
							beanArray.add(line);
							break;
						}
					}
				}
			}
			if (fieldMap.containsKey(line.getKey())) {
				for (String field : fieldMap.get(line.getKey()).keySet()) {
					String name = MatchUtil.exporeMatchedFirstByRegular(fieldMap.get(line.getKey()).get(field),
							CoodyConfig.INPUT_BEAN_MAPPER);
					if (CommonUtil.isNullOrEmpty(name)) {
						continue;
					}
					for (Entry<String, HashMap<String, String>> entry : beanArray) {
						if (entry.getKey().equals(name)) {
							beanArray.add(line);
							break;
						}
					}
				}
			}
			
			if (beanArray.contains(line)) {
				continue;
			}
			queue.offer(line);
		}
		List<String> ordereds = new ArrayList<String>();
		for (Entry<String, HashMap<String, String>> line : beanArray) {
			ordereds.add(line.getKey());
		}
		return ordereds;
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
