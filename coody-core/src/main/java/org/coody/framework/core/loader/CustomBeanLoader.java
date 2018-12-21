package org.coody.framework.core.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.coody.framework.core.assember.BeanAssember;
import org.coody.framework.core.build.ConfigBuilder;
import org.coody.framework.core.config.CoodyConfig;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.exception.BeanNotFoundException;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.util.MatchUtil;
import org.coody.framework.core.util.StringUtil;

public class CustomBeanLoader implements CoodyLoader {

	static final HashMap<String, HashMap<String, String>> PARAMENT_MAP = new HashMap<String, HashMap<String, String>>();

	static final HashMap<String, HashMap<String, String>> FIELD_MAP = new HashMap<String, HashMap<String, String>>();

	static final HashMap<String, HashMap<String, String>> CONFIG_MAP = new HashMap<String, HashMap<String, String>>();

	@Override
	public void doLoader() throws Exception {
		// 加载基础Bean配置
		for (String key : ConfigBuilder.propertyKeySet()) {
			String configValue = ConfigBuilder.getProperty(key);
			if (StringUtil.isNullOrEmpty(configValue)) {
				continue;
			}
			if (MatchUtil.isParaMatch(key, CoodyConfig.BEAN_CONFIG_MAPPER)) {
				Map<String, String> beanConfig = MatchUtil.matchParamMap(key, CoodyConfig.BEAN_CONFIG_MAPPER);
				if (StringUtil.isNullOrEmpty(beanConfig)) {
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
			if (StringUtil.isNullOrEmpty(configValue)) {
				continue;
			}
			if (MatchUtil.isParaMatch(key, CoodyConfig.BEAN_PARAMENT_MAPPER)) {
				Map<String, String> beanConfig = MatchUtil.matchParamMap(key, CoodyConfig.BEAN_PARAMENT_MAPPER);
				if (StringUtil.isNullOrEmpty(beanConfig)) {
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
			if (StringUtil.isNullOrEmpty(configValue)) {
				continue;
			}
			if (MatchUtil.isParaMatch(key, CoodyConfig.BEAN_FIELD_MAPPER)) {
				Map<String, String> beanConfig = MatchUtil.matchParamMap(key, CoodyConfig.BEAN_FIELD_MAPPER);
				if (StringUtil.isNullOrEmpty(beanConfig)) {
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
		List<String> originalBeans=doRelationOrder(PARAMENT_MAP, FIELD_MAP, CONFIG_MAP);
		for (String key : originalBeans) {
			Map<String, String> beanConfig = PARAMENT_MAP.get(key);
			String clazzName = CONFIG_MAP.get(key).get(CoodyConfig.CLASS_NAME);
			Class<?> clazz = Class.forName(clazzName);
			Map<String, Object> parameters=null;
			if(PARAMENT_MAP.containsKey(key)){
				parameters = builderParamenterMap(clazz, beanConfig);
			}
			Object bean = BeanAssember.initBean(clazz, key, parameters);
			if (bean == null) {
				continue;
			}
			//初始化字段
			Map<String, String> fieldConfig = FIELD_MAP.get(key);
			if(StringUtil.isNullOrEmpty(fieldConfig)){
				continue;
			}
			Map<String, Object> fielders = builderParamenterMap(bean.getClass(), fieldConfig);
			BeanAssember.initField(bean, fielders);
		}
	}
	
	/**
	 * 根据依赖关系排序
	 * @param clazz
	 * @param parameneterMap
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public List<String> doRelationOrder(HashMap<String, HashMap<String, String>> paramentMap,HashMap<String, HashMap<String, String>> fieldMap,HashMap<String, HashMap<String, String>> configMap) throws ClassNotFoundException{
		//加载基础Bean
		List<String> originalBeans=new ArrayList<String>();
		configCheck:for (String key : configMap.keySet()) {
			Map<String, String> propertyMap=new HashMap<String, String>();
			if(fieldMap.containsKey(key)){
				propertyMap.putAll(fieldMap.get(key));
			}
			if(paramentMap.containsKey(key)){
				propertyMap.putAll(paramentMap.get(key));
			}
			if(StringUtil.isNullOrEmpty(propertyMap)){
				originalBeans.add(key);
			}
			for(String poperty:propertyMap.keySet()){
				String valueBeanName = MatchUtil.matchExportFirst(propertyMap.get(poperty), CoodyConfig.INPUT_BEAN_MAPPER);
				if(!StringUtil.isNullOrEmpty(valueBeanName)){
					if(!configMap.containsKey(valueBeanName)){
						if(!configMap.get(key).containsKey(CoodyConfig.CLASS_NAME)){
							continue configCheck; 
						}
						Class<?> clazz = Class.forName(configMap.get(key).get(CoodyConfig.CLASS_NAME));
						throw new BeanNotFoundException(valueBeanName, clazz);
					}
					continue configCheck; 
				}
			}
			originalBeans.add(key);
		}
		while(originalBeans.size()<configMap.size()){
			configCheck:for (String key : configMap.keySet()) {
				if(originalBeans.contains(key)){
					continue;
				}
				Map<String, String> propertyMap=new HashMap<String, String>();
				if(fieldMap.containsKey(key)){
					propertyMap.putAll(fieldMap.get(key));
				}
				if(paramentMap.containsKey(key)){
					propertyMap.putAll(paramentMap.get(key));
				}
				for(String poperty:propertyMap.keySet()){
					String valueBeanName = MatchUtil.matchExportFirst(propertyMap.get(poperty), CoodyConfig.INPUT_BEAN_MAPPER);
					if(!StringUtil.isNullOrEmpty(valueBeanName)){
						if(!originalBeans.contains(valueBeanName)){
							continue configCheck; 
						}
					}
				}
				originalBeans.add(key);
			}
		}
		return originalBeans;
	}

	private Map<String, Object> builderParamenterMap(Class<?> clazz, Map<String, String> parameneterMap) {
		Map<String, Object> paramenters = new HashMap<String, Object>();
		for (String parameneter : parameneterMap.keySet()) {
			String value = parameneterMap.get(parameneter);
			if (!MatchUtil.isParaMatch(value, CoodyConfig.INPUT_BEAN_MAPPER)) {
				paramenters.put(parameneter, value);
				continue;
			}
			String valueBeanName = MatchUtil.matchExportFirst(value, CoodyConfig.INPUT_BEAN_MAPPER);
			if (StringUtil.isNullOrEmpty(valueBeanName)) {
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
