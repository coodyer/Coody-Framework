package org.coody.framework.cache.aspecter;

import java.lang.reflect.Method;

import org.coody.framework.cache.annotation.CacheWipe;
import org.coody.framework.cache.annotation.CacheWipes;
import org.coody.framework.cache.annotation.CacheWrite;
import org.coody.framework.cache.instance.LocalCache;
import org.coody.framework.cache.instance.iface.CoodyCacheFace;
import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.model.AspectPoint;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.core.util.reflex.MethodSignUtil;

@AutoBuild
public class CacheAspect {

	@AutoBuild
	LocalCache localCache;

	/**
	 * 写缓存操作
	 * 
	 * @param aspect
	 * @return 方法返回内容
	 * @throws Throwable
	 */
	@Around(annotationClass = CacheWrite.class)
	public Object cCacheWrite(AspectPoint point) throws Throwable {
		Class<?> clazz = point.getAbler().getClazz();
		Method method = point.getAbler().getMethod();
		if (method == null) {
			return point.invoke();
		}
		// 获取注解
		CacheWrite handle = method.getAnnotation(CacheWrite.class);
		if (handle == null) {
			return point.invoke();
		}
		// 封装缓存KEY
		Object[] paras = point.getParams();
		String key = handle.key();
		try {
			if (CommonUtil.isNullOrEmpty(key)) {
				key = this.getClass().getSimpleName() + ":" + MethodSignUtil.getKeyByMethod(clazz, method);
			}
			if (CommonUtil.isNullOrEmpty(handle.fields())) {
				String paraKey = MethodSignUtil.getKeyByParameters(paras);
				if (!CommonUtil.isNullOrEmpty(paraKey)) {
					key += ":";
					key += paraKey;
				}
			}
			if (!CommonUtil.isNullOrEmpty(handle.fields())) {
				key = MethodSignUtil.getKeyByFields(clazz, method, paras, key, handle.fields());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Integer cacheTimer = ((handle.expire() == 0) ? 24 * 3600 : handle.expire());
		CoodyCacheFace cacheable = getCacheable(handle.engine());
		// 获取缓存
		try {
			Object result = cacheable.getCache(key);
			LogUtil.log.debug("获取缓存 >>" + key + ",结果:" + result);
			if (!CommonUtil.isNullOrEmpty(result)) {
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Object result = point.invoke();
		if (result != null) {
			try {
				cacheable.setCache(key, result, cacheTimer);
				LogUtil.log.debug("设置缓存 >>" + key + ",结果:" + result + ",缓存时间:" + cacheTimer);
			} catch (Exception e) {
			}
		}
		return result;
	}

	/**
	 * 缓存清理
	 * 
	 * @param aspect
	 * @return 方法返回内容
	 * @throws Throwable
	 */
	@Around(annotationClass = CacheWipes.class)
	@Around(annotationClass = CacheWipe.class)
	public Object zCacheWipe(AspectPoint able) throws Throwable {
		Class<?> clazz = able.getAbler().getClazz();
		Method method = able.getAbler().getMethod();
		if (method == null) {
			return able.invoke();
		}
		Object[] paras = able.getParams();
		Object result = able.invoke();
		CacheWipe[] handles = method.getAnnotationsByType(CacheWipe.class);
		if (CommonUtil.isNullOrEmpty(handles)) {
			return result;
		}

		for (CacheWipe handle : handles) {
			String key = handle.key();
			try {
				if (CommonUtil.isNullOrEmpty(key)) {
					key = this.getClass().getSimpleName() + ":" + MethodSignUtil.getKeyByMethod(clazz, method);
				}
				if (CommonUtil.isNullOrEmpty(handle.fields())) {
					String paraKey = MethodSignUtil.getKeyByParameters(paras);
					if (!CommonUtil.isNullOrEmpty(paraKey)) {
						key += ":";
						key += paraKey;
					}
				}
				if (!CommonUtil.isNullOrEmpty(handle.fields())) {
					key = MethodSignUtil.getKeyByFields(clazz, method, paras, key, handle.fields());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			LogUtil.log.debug("删除缓存 >>" + key);
			CoodyCacheFace cacheable = getCacheable(handle.engine());
			cacheable.delCache(key);
		}
		return result;
	}

	private CoodyCacheFace getCacheable(Class<?> clazz) {
		if (clazz == LocalCache.class) {
			return localCache;
		}
		return BeanContainer.getBean(clazz);
	}
}
