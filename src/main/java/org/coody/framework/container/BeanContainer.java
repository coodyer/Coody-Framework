package org.coody.framework.container;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.annotation.InitBean;
import org.coody.framework.annotation.PathBinding;
import org.coody.framework.util.PropertUtil;
import org.coody.framework.util.StringUtil;

@SuppressWarnings({"unchecked"})
public class BeanContainer {
	
	
	private static Map<String, Object> beanMap=new ConcurrentHashMap<String, Object>();
	
	public static <T> T getBean(Class<?> cla){
		String beanName=getBeanName(cla);
		if(StringUtil.isNullOrEmpty(beanName)){
			return null;
		}
		return (T) beanMap.get(beanName);
	}
	
	public static <T> T getBean(String beanName){
		if(StringUtil.isNullOrEmpty(beanName)){
			return null;
		}
		return (T) beanMap.get(beanName);
	}
	public static void writeBean(String beanName,Object bean){
		beanMap.put(beanName, bean);
	}
	public static boolean containsBean(String beanName){
		return beanMap.containsKey(beanName);
	}
	public static Collection<?> getBeans(){
		return beanMap.values();
	}
	public static String getBeanName(Class<?> clazz){
		List<Annotation> initBeans=PropertUtil.getAnnotations(clazz, InitBean.class);
		if(StringUtil.isNullOrEmpty(initBeans)){
			return null;
		}
		for (Annotation annotation : initBeans) {
			if (StringUtil.isNullOrEmpty(annotation)) {
				continue;
			}
			String beanName = clazz.getName();
			if(PathBinding.class.isAssignableFrom(annotation.annotationType())){
				return beanName;
			}
			beanName = (String) PropertUtil.getAnnotationValue(annotation, "value");
			if (!StringUtil.isNullOrEmpty(beanName)) {
				return beanName;
			}
			return clazz.getName();
		}
		return null;
	}
	
	public static List<String> getBeanNames(Class<?> clazz){
		Set<String> beanNames=new HashSet<String>();
		String beanName=getBeanName(clazz);
		if(StringUtil.isNullOrEmpty(beanName)){
			return null;
		}
		beanNames.add(beanName);
		Class<?>[] clazzs=clazz.getInterfaces();
		if(!StringUtil.isNullOrEmpty(clazzs)){
			for(Class<?> clazTemp:clazzs){
				beanName=getBeanName(clazTemp);
				if(StringUtil.isNullOrEmpty(beanName)){
					beanName=clazTemp.getName();
				}
				if(BeanContainer.containsBean(beanName)){
					continue;
				}
				beanNames.add(beanName);
			}
		}
		if(StringUtil.isNullOrEmpty(beanNames)){
			return null;
		}
		return new ArrayList<String>(beanNames);
	}
}
