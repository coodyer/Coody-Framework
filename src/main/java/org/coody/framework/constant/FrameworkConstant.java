package org.coody.framework.constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.coody.framework.annotation.InitBean;
import org.coody.framework.annotation.PathBinding;
import org.coody.framework.aspect.entity.AspectEntity;

public class FrameworkConstant {

	/**
	 * 初始化Bean拦截的注解
	 */
	public static final Class<?>[] beanAnnotations = new Class[] { InitBean.class, PathBinding.class };
	
	/**
	 * 切面存储。key为切面ID，Value为切面实例
	 */
	public static final Map<String, List<AspectEntity>> aspectMap = new ConcurrentHashMap<String, List<AspectEntity>>();
	
	/**
	 * 表主键列表
	 */
	public static final String table_primary_keys="table_primary_keys";
	
	/**
	 * 自动化缓存KEY
	 */
	public static final String AUTO_CACHE_KEY="AUTO_CACHE_KEY";
	
	
	public static void writeToAspectMap(String methodKey,AspectEntity aspectEntity){
		if(FrameworkConstant.aspectMap.containsKey(methodKey)){
			FrameworkConstant.aspectMap.get(methodKey).add(aspectEntity);
			return;
		}
		List<AspectEntity> aspectEntitys=new ArrayList<AspectEntity>();
		aspectEntitys.add(aspectEntity);
		FrameworkConstant.aspectMap.put(methodKey, aspectEntitys);
	}
}
