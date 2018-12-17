package org.coody.framework.core.assember;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.config.CoodyConfig;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.exception.BeanInitException;
import org.coody.framework.core.exception.BeanNameCreateException;
import org.coody.framework.core.exception.BeanNotFoundException;
import org.coody.framework.core.loader.BeanLoader;
import org.coody.framework.core.proxy.CglibProxy;
import org.coody.framework.core.util.MatchUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;
import org.nico.noson.Noson;

@AutoBuild
public class BeanAssember {

	private static final Logger logger = Logger.getLogger(BeanLoader.class);

	static CglibProxy proxy = new CglibProxy();

	public static <T> T initBean(Class<?> cla) {
		return initBean(cla, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T initBean(Class<?> cla,String additionBeanName) {
		
		Set<String> names = BeanContainer.getOverallBeanName(cla);
		if(!StringUtil.isNullOrEmpty(additionBeanName)){
			names.add(additionBeanName);
		}
		if (StringUtil.isNullOrEmpty(names)) {
			throw new BeanNameCreateException(cla);
		}
		Object bean = proxy.getProxy(cla);
		if (bean == null) {
			throw new BeanInitException(cla);
		}
		for (String beanName : names) {
			if (StringUtil.isNullOrEmpty(beanName)) {
				continue;
			}
			logger.debug("初始化Bean >>" + beanName + ":" + cla.getName());
			BeanContainer.setBean(beanName, bean);
		}
		return (T) bean;
	}
	public static void initField(Object bean, Map<String, String> params)
			throws IllegalArgumentException, IllegalAccessException {
		List<Field> fields = loadFields(bean.getClass());
		if (StringUtil.isNullOrEmpty(fields)) {
			return;
		}
		fieldSet:for (Field field : fields) {
			if (StringUtil.isNullOrEmpty(field.getAnnotations())) {
				continue;
			}
			Annotation autoBuild = PropertUtil.getAnnotation(field, AutoBuild.class);
			if (StringUtil.isNullOrEmpty(autoBuild)) {
				continue;
			}
			String[] beanNames = PropertUtil.getAnnotationValue(autoBuild, "value");
			beanSearch:for(String beanName:beanNames){
				if (StringUtil.isNullOrEmpty(beanName)) {
					beanName = field.getType().getName();
				}
				if (!BeanContainer.contains(beanName)) {
					continue beanSearch;
				}
				field.setAccessible(true);
				Object writeValue = BeanContainer.getBean(beanName);
				logger.debug("注入字段 >>" + field.getName() + ":" + bean.getClass().getName());
				field.set(bean, writeValue);
				continue fieldSet;
			}
			throw new BeanNotFoundException(Noson.reversal(beanNames), bean.getClass());
		}
		if (StringUtil.isNullOrEmpty(params)) {
			return;
		}
		for (Field field : fields) {
			if (!params.containsKey(field.getName())) {
				continue;
			}
			String value= params.get(field.getName());
			if(!MatchUtil.isParaMatch(value,  CoodyConfig.INPUT_BEAN_MAPPER)){
				PropertUtil.setFieldValue(bean, field,value);
				continue;
			}
			String beanName = MatchUtil.matchExportFirst(value, CoodyConfig.INPUT_BEAN_MAPPER);
			if(StringUtil.isNullOrEmpty(beanName)){
				throw new BeanNotFoundException(beanName, bean.getClass());
			}
			Object inputBean=BeanContainer.getBean(beanName);
			if(inputBean==null){
				throw new BeanNotFoundException(beanName, bean.getClass());
			}
			PropertUtil.setFieldValue(bean, field,inputBean);
		}
	}

	public static void initField(Object bean) throws IllegalArgumentException, IllegalAccessException {
		initField(bean, null);
	}

	private static List<Field> loadFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		Field[] fieldArgs = clazz.getDeclaredFields();
		for (Field f : fieldArgs) {
			fields.add(f);
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null) {
			return fields;
		}
		List<Field> childFields = loadFields(superClass);
		if (StringUtil.isNullOrEmpty(childFields)) {
			return fields;
		}
		fields.addAll(childFields);
		return fields;
	}
}
