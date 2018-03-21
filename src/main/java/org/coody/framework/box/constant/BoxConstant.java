package org.coody.framework.box.constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.box.annotation.InitBean;
import org.coody.framework.box.annotation.PathBinding;
import org.coody.framework.box.aspect.entity.AspectEntity;

public class BoxConstant {

	/**
	 * 初始化Bean拦截的注解
	 */
	public static final Class<?>[] beanAnnotations = new Class[] { InitBean.class, PathBinding.class };
	
	/**
	 * 切面存储。key为切面ID，Value为切面实例
	 */
	public static final Map<String, AspectEntity> aspectMap = new ConcurrentHashMap<String, AspectEntity>();
	
	/**
	 * 表主键列表
	 */
	public static final String table_primary_keys="table_primary_keys";
	
	/**
	 * 自动化缓存KEY
	 */
	public static final String AUTO_CACHE_KEY="AUTO_CACHE_KEY";
	
	
	
}
