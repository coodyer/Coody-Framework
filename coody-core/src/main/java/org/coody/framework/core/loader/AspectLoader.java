package org.coody.framework.core.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.Arounds;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.constant.FrameworkConstant;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.model.AspectEntity;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.LogUtil;
import org.coody.framework.core.util.MethodSignUtil;
import org.coody.framework.core.util.PropertUtil;
import org.coody.framework.core.util.StringUtil;

/**
 * 切面加载器
 * 
 * @author Coody
 *
 */
public class AspectLoader implements CoodyLoader {

	@Override
	public void doLoader() throws Exception {
		if (StringUtil.isNullOrEmpty(BeanContainer.getClazzContainer())) {
			return;
		}
		ThreadBlockPool pool = new ThreadBlockPool(100, 60);
		for (Class<?> clazz : BeanContainer.getClazzContainer()) {
			if (clazz.isAnnotation()) {
				continue;
			}
			if (StringUtil.isNullOrEmpty(clazz.getAnnotations())) {
				continue;
			}
			Annotation initBean = PropertUtil.getAnnotation(clazz, AutoBuild.class);
			if (initBean == null) {
				continue;
			}
			pool.pushTask(new Runnable() {
				@Override
				public void run() {
					loaderClazz(clazz);
				}
			});
		}
		pool.execute();
	}

	private void loaderClazz(Class<?> clazz) {
		Method[] methods = clazz.getDeclaredMethods();
		if (StringUtil.isNullOrEmpty(methods)) {
			return;
		}
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers()) || Modifier.isAbstract(method.getModifiers())) {
				continue;
			}
			if (StringUtil.isNullOrEmpty(method.getAnnotations())) {
				continue;
			}
			List<Annotation> arounds = PropertUtil.getAnnotations(method, Around.class);
			if (StringUtil.isNullOrEmpty(arounds)) {
				List<Annotation> aroundParents = PropertUtil.getAnnotations(method, Arounds.class);
				if (StringUtil.isNullOrEmpty(aroundParents)) {
					continue;
				}
				arounds = new ArrayList<Annotation>();
				for (Annotation aroundParent : aroundParents) {
					Annotation[] aroundTemps = PropertUtil.getAnnotationValue(aroundParent, "value");
					if (StringUtil.isNullOrEmpty(aroundTemps)) {
						continue;
					}
					arounds.addAll(Arrays.asList(aroundTemps));
				}
			}
			for (Annotation around : arounds) {
				try {
					Map<String, Object> annotationValueMap = PropertUtil.getAnnotationValueMap(around);
					Class<?>[] annotationClass = (Class<?>[]) annotationValueMap.get("annotationClass");
					String classMappath = (String) annotationValueMap.get("classMappath");
					String methodMappath = (String) annotationValueMap.get("methodMappath");
					if (StringUtil.isAllNull(annotationClass, classMappath, methodMappath)) {
						continue;
					}
					Boolean ownIntercept = (Boolean) annotationValueMap.get("ownIntercept");
					LogUtil.log.debug("初始化切面方法 >>" + MethodSignUtil.getKeyByMethod(clazz, method));
					AspectEntity aspectEntity = new AspectEntity();
					// 装载切面控制方法
					aspectEntity.setAnnotationClass(annotationClass);
					aspectEntity.setMethodMappath(methodMappath);
					aspectEntity.setClassMappath(classMappath);
					aspectEntity.setAspectInvokeMethod(method);
					aspectEntity.setOwnIntercept(ownIntercept);
					aspectEntity.setAspectClazz(clazz);
					String methodKey = MethodSignUtil.getMethodUnionKey(method);
					FrameworkConstant.writeToAspectMap(methodKey, aspectEntity);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

}
