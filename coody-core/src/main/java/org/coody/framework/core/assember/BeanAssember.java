package org.coody.framework.core.assember;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coody.framework.Cson;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.exception.BeanInitException;
import org.coody.framework.core.exception.BeanNameCreateException;
import org.coody.framework.core.exception.BeanNotFoundException;
import org.coody.framework.core.proxy.CglibProxy;
import org.coody.framework.core.util.clazz.ClassUtil;
import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.core.util.reflex.ParameterNameUtil;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.coody.framework.core.util.CommonUtil;

public class BeanAssember {

	static CglibProxy proxy = new CglibProxy();

	public static <T> T initBean(Class<?> cla) {
		return initBean(cla, null);
	}

	public static <T> T initBean(Class<?> cla, String additionBeanName) {
		return initBean(cla, additionBeanName, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T initBean(Class<?> cla, String additionBeanName, Map<String, Object> parameterMap) {

		Set<String> names = BeanContainer.getOverallBeanName(cla);
		if (!CommonUtil.isNullOrEmpty(additionBeanName)) {
			names.add(additionBeanName);
		}
		if (CommonUtil.isNullOrEmpty(names)) {
			throw new BeanNameCreateException(cla);
		}
		Object bean = proxy.getProxy(cla, parameterMap);
		if (bean == null) {
			throw new BeanInitException(cla);
		}
		for (String beanName : names) {
			if (CommonUtil.isNullOrEmpty(beanName)) {
				continue;
			}
			LogUtil.log.debug("初始化Bean >>" + beanName + ":" + cla.getName());
			BeanContainer.setBean(beanName, bean);
		}
		if (CommonUtil.isNullOrEmpty(parameterMap)) {
			// 启动字节码加速
			ParameterNameUtil.doExecutable(cla);
		}
		return (T) bean;
	}

	public static void initField(Object bean, Map<String, Object> parameterMap)
			throws IllegalArgumentException, IllegalAccessException {
		List<Field> fields = loadFields(bean.getClass());
		if (CommonUtil.isNullOrEmpty(fields)) {
			return;
		}
		fieldSet: for (Field field : fields) {
			if (CommonUtil.isNullOrEmpty(field.getAnnotations())) {
				continue;
			}
			Annotation autoBuild = PropertUtil.getAnnotation(field, AutoBuild.class);
			if (CommonUtil.isNullOrEmpty(autoBuild)) {
				continue;
			}
			String[] beanNames = PropertUtil.getAnnotationValue(autoBuild, "value");
			beanSearch: for (String beanName : beanNames) {
				if (CommonUtil.isNullOrEmpty(beanName)) {
					beanName = field.getType().getName();
				}
				Object targetBean = BeanContainer.getBean(beanName);
				if (targetBean == null) {
					continue beanSearch;
				}
				field.setAccessible(true);

				LogUtil.log.debug("注入字段 >>" + field.getName() + ":" + bean.getClass().getName());
				field.set(bean, targetBean);
				continue fieldSet;
			}
			throw new BeanNotFoundException(Cson.toJson(beanNames), bean.getClass());
		}
		if (CommonUtil.isNullOrEmpty(parameterMap)) {
			return;
		}
		for (Field field : fields) {
			if (!parameterMap.containsKey(field.getName())) {
				continue;
			}
			PropertUtil.setFieldValue(bean, field, parameterMap.get(field.getName()));
		}
	}

	public static void initField(Object bean) throws IllegalArgumentException, IllegalAccessException {
		initField(bean, null);
	}

	private static List<Field> loadFields(Class<?> clazz) {
		clazz = ClassUtil.getSourceClass(clazz);
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
		if (CommonUtil.isNullOrEmpty(childFields)) {
			return fields;
		}
		fields.addAll(childFields);
		return fields;
	}
}
