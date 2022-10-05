package org.coody.framework.rcc.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import org.coody.framework.core.annotation.Order;
import org.coody.framework.core.builder.ConfigBuilder;
import org.coody.framework.core.container.BeanContainer;
import org.coody.framework.core.loader.iface.CoodyLoader;
import org.coody.framework.core.threadpool.ThreadBlockPool;
import org.coody.framework.core.util.CommonUtil;
import org.coody.framework.core.util.log.LogUtil;
import org.coody.framework.core.util.reflex.PropertUtil;
import org.coody.framework.rcc.annotation.RccClient;
import org.coody.framework.rcc.config.RccConfig;
import org.coody.framework.rcc.exception.RccException;
import org.coody.framework.rcc.handler.RccHandler;
import org.coody.framework.rcc.util.asm.ImplClassMaker;

/**
 * 
 * @author Coody
 *
 */
@Order(-1)
public class RccLoader implements CoodyLoader {

	@Override
	public void doLoader() throws Exception {

		ConfigBuilder.builder();
		ConfigBuilder.flush(new RccConfig(), RccConfig.PREFIX);
		if (CommonUtil.isNullOrEmpty(BeanContainer.getClazzContainer())) {
			return;
		}

		// 解析客户端
		ThreadBlockPool pool = new ThreadBlockPool(100, 60);
		for (Class<?> clazz : BeanContainer.getClazzContainer()) {
			if (clazz.isAnnotation()) {
				continue;
			}
			if (CommonUtil.isNullOrEmpty(clazz.getAnnotations())) {
				continue;
			}
			if (!clazz.isInterface()) {
				continue;
			}
			Annotation rccInterface = PropertUtil.getAnnotation(clazz, RccClient.class);
			if (rccInterface == null) {
				continue;
			}
			if (isImpled(clazz)) {
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

	private boolean isImpled(Class<?> clazz) {
		for (Class<?> line : BeanContainer.getClazzContainer()) {
			if (line.isInterface()) {
				continue;
			}
			if (clazz.isAssignableFrom(line)) {
				return true;
			}
		}
		return false;
	}

	private void loaderClazz(Class<?> clazz) {
		Class<?> implClazz = ImplClassMaker.createInterfaceImpl(clazz, RccHandler.class);
		LogUtil.log.info("创建实现类->" + implClazz);
		BeanContainer.getClazzContainer().add(implClazz);
		// 复制注解
		Class<?>[] interfaces = implClazz.getInterfaces();
		if (CommonUtil.isNullOrEmpty(interfaces)) {
			return;
		}
		for (Class<?> interfaced : interfaces) {
			Annotation[] annotations = interfaced.getAnnotations();
			if (CommonUtil.isNullOrEmpty(annotations)) {
				continue;
			}
			try {
				PropertUtil.addAnnotations(implClazz, annotations);
			} catch (Exception e) {
				LogUtil.log.error("注解Copy失败", e);
				continue;
			}
			Set<Method> methods = PropertUtil.getMethods(implClazz);
			for (Method line : methods) {
				try {
					PropertUtil.addAnnotations(line, annotations);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}
	}

}
