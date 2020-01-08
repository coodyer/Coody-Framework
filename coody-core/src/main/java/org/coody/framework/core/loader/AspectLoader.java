package org.coody.framework.core.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.coody.framework.core.annotation.Around;
import org.coody.framework.core.annotation.Arounds;
import org.coody.framework.core.annotation.AutoBuild;
import org.coody.framework.core.constant.FrameworkConstant;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.model.AspectEntity;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.reflex.MethodSignUtil;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.coody.framework.core.util.CommonUtil;

/**
 * 
 * @author Coody
 *
 */
public class AspectLoader implements CoodyLoader {

	@Override
	public void doLoader() throws Exception {
		if (CommonUtil.isNullOrEmpty(BeanContainer.getClazzContainer())) {
			return;
		}
		ThreadBlockPool pool = new ThreadBlockPool(100, 60);
		for (Class<?> clazz : BeanContainer.getClazzContainer()) {
			if (clazz.isAnnotation()) {
				continue;
			}
			if (CommonUtil.isNullOrEmpty(clazz.getAnnotations())) {
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
		if (CommonUtil.isNullOrEmpty(methods)) {
			return;
		}
		for (Method method : methods) {
			if (Modifier.isStatic(method.getModifiers()) || Modifier.isAbstract(method.getModifiers())) {
				continue;
			}
			if (CommonUtil.isNullOrEmpty(method.getAnnotations())) {
				continue;
			}
			List<Around> around = PropertUtil.getAnnotations(method, Around.class);
			if (CommonUtil.isNullOrEmpty(around)) {
				List<Arounds> aroundParents = PropertUtil.getAnnotations(method, Arounds.class);
				if (CommonUtil.isNullOrEmpty(aroundParents)) {
					continue;
				}
				around = new ArrayList<Around>();
				for (Arounds arounds : aroundParents) {
					around.addAll(Arrays.asList(arounds.value()));
				}
			}
			for (Around line : around) {
				try {
					AspectEntity aspectEntity = new AspectEntity();
					aspectEntity.setAnnotationClass(line.annotationClass());
					aspectEntity.setMethodMappath(line.methodMappath());
					aspectEntity.setClassMappath(line.classMappath());
					aspectEntity.setAspectInvokeMethod(method);
					aspectEntity.setOwnIntercept(line.ownIntercept());
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
