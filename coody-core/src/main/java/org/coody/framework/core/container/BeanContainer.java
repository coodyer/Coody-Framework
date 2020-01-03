package org.coody.framework.core.container;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.constant.InsideTypeConstant;
import org.coody.framework.core.exception.BeanConflictException;
import org.coody.framework.core.util.ClassUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

/**
 * 
 * @author Coody
 *
 *         2018年12月17日
 * 
 * @blog 54sb.org
 */
@SuppressWarnings({ "unchecked" })
public class BeanContainer {

	private static Set<Class<?>> clazzContainer = new HashSet<Class<?>>();

	private static Map<String, Map<String, Object>> beanContainer = new HashMap<String, Map<String, Object>>();

	private static Map<Class<?>, Set<String>> beanNameContainer = new HashMap<Class<?>, Set<String>>();

	public static <T> T getBean(Class<?> cla) {
		String beanName = getGeneralBeanName(cla);
		if (StringUtil.isNullOrEmpty(beanName)) {
			return null;
		}
		return getBean(beanName);
	}

	public static Set<Class<?>> getClazzContainer() {
		return clazzContainer;
	}

	public static void setClazzContainer(Set<Class<?>> clazzContainer) {
		BeanContainer.clazzContainer = clazzContainer;
	}

	public static <T> T getBean(String beanName) {
		if (StringUtil.isNullOrEmpty(beanName)) {
			return null;
		}
		Map<String, Object> map = beanContainer.get(beanName);
		if (StringUtil.isNullOrEmpty(map)) {
			return null;
		}
		if (map.size() > 1) {
			throw new BeanConflictException(beanName + "存在多个实例,未明确指定");
		}
		for (String key : map.keySet()) {
			return (T) map.get(key);
		}
		return null;
	}

	public static synchronized void setBean(String beanName, Object bean) {
		Class<?> clazz = ClassUtil.getSourceClass(bean.getClass());
		String realBeanName = clazz.getName();
		if (beanContainer.containsKey(beanName)) {
			Map<String, Object> map = beanContainer.get(beanName);
			map.put(realBeanName, bean);
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(realBeanName, bean);
		beanContainer.put(beanName, map);
	}

	public static boolean contains(String beanName) {
		return beanContainer.containsKey(beanName);
	}

	public static HashSet<?> getBeans() {
		HashSet<Object> beans = new HashSet<Object>();
		for (String key : beanContainer.keySet()) {
			Map<String, Object> map = beanContainer.get(key);
			if (StringUtil.isNullOrEmpty(map)) {
				continue;
			}
			for (String realKey : map.keySet()) {
				beans.add(map.get(realKey));
			}
		}
		return beans;
	}

	private static Set<String> getDeclaredBeanNames(Class<?> clazz) {
		Set<String> beanNames = beanNameContainer.get(clazz);
		if (!StringUtil.isNullOrEmpty(beanNames)) {
			return beanNames;
		}
		clazz = ClassUtil.getSourceClass(clazz);
		try {
			beanNames = new HashSet<String>();
			beanNames.add(clazz.getName());
			if (StringUtil.isNullOrEmpty(clazz.getAnnotations())) {
				return beanNames;
			}
			List<Annotation> initBeans = PropertUtil.getAnnotations(clazz, AutoBuild.class);
			if (StringUtil.isNullOrEmpty(initBeans)) {
				return beanNames;
			}
			for (Annotation annotation : initBeans) {
				if (StringUtil.isNullOrEmpty(annotation)) {
					continue;
				}
				String[] values = PropertUtil.getAnnotationValue(annotation, "value");
				if (StringUtil.isNullOrEmpty(values)) {
					continue;
				}
				beanNames.addAll(Arrays.asList(values));
			}
			return beanNames;
		} finally {
			beanNameContainer.put(clazz, beanNames);
		}
	}

	public static String getGeneralBeanName(Class<?> clazz) {
		if (InsideTypeConstant.isSystem(clazz)) {
			return null;
		}
		clazz = ClassUtil.getSourceClass(clazz);
		return clazz.getName();
	}

	public static Set<String> getOverallBeanName(Class<?> clazz) {
		if (InsideTypeConstant.isSystem(clazz)) {
			return new HashSet<String>();
		}
		clazz = ClassUtil.getSourceClass(clazz);
		Set<String> beanNames = new HashSet<String>(getDeclaredBeanNames(clazz));
		Class<?>[] interfaces = clazz.getInterfaces();
		if (!StringUtil.isNullOrEmpty(interfaces)) {
			for (Class<?> interfacer : interfaces) {
				Set<String> interfaceBeanNames = getOverallBeanName(interfacer);
				if (!StringUtil.isNullOrEmpty(interfaceBeanNames)) {
					beanNames.addAll(interfaceBeanNames);
				}
			}
		}
		Class<?> superer = clazz.getSuperclass();
		if (!StringUtil.isNullOrEmpty(superer)) {
			Set<String> superBeanNames = getOverallBeanName(superer);
			if (!StringUtil.isNullOrEmpty(superBeanNames)) {
				beanNames.addAll(superBeanNames);
			}
		}

		return beanNames;
	}
}
