package org.coody.framework.core.loader;

import java.util.HashMap;
import java.util.Map;

import org.coody.framework.core.assember.BeanAssember;
import org.coody.framework.core.build.ConfigBuilder;
import org.coody.framework.core.config.CoodyConfig;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.util.MatchUtil;
import org.coody.framework.core.util.StringUtil;

public class CustomBeanLoader implements CoodyLoader {

	Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

	@Override
	public void doLoader() throws Exception {
		for (String key : ConfigBuilder.propertyKeySet()) {
			String configValue = ConfigBuilder.getProperty(key);
			if (StringUtil.isNullOrEmpty(configValue)) {
				continue;
			}
			if(!MatchUtil.isParaMatch(key,  CoodyConfig.BEAN_MAPPER)){
				continue;
			}
			Map<String, String> beanConfig = MatchUtil.matchParamMap(key, CoodyConfig.BEAN_MAPPER);
			if (StringUtil.isNullOrEmpty(beanConfig)) {
				continue;
			}
			String beanName = beanConfig.get(CoodyConfig.BEAN_NAME);
			if (!map.containsKey(beanName)) {
				map.put(beanName, new HashMap<String, String>());
			}
			String property = beanConfig.get(CoodyConfig.PROPERTY);
			map.get(beanName).put(property, configValue);
		}

		// 初始化所有Bean
		Map<String,Object> beans=new HashMap<String, Object>();
		for (String key : map.keySet()) {
			Map<String, String> beanConfig = map.get(key);
			String clazzName = beanConfig.get(CoodyConfig.CLASS_NAME);
			Class<?> clazz = Class.forName(clazzName);
			Object bean=BeanAssember.initBean(clazz, key);
			if(bean==null){
				continue;
			}
			beans.put(key, bean);
		}
		
		//初始化所有字段
		for (String key : beans.keySet()) {
			Map<String, String> beanConfig = map.get(key);
			BeanAssember.initField(beans.get(key), beanConfig);
		}
	}

	public static void main(String[] args) {
		System.out.println(CoodyConfig.BEAN_MAPPER);
		System.out.println( MatchUtil.matchExport("${aaa}", CoodyConfig.INPUT_BEAN_MAPPER));
	}
}
